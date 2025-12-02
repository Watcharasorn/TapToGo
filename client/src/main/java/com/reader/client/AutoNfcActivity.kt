package com.reader.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.common.apiutil.ResultCode
import com.common.apiutil.decode.DecodeReader
import com.common.apiutil.powercontrol.PowerControl
import com.common.apiutil.util.StringUtil
import com.common.callback.IDecodeReaderListener
import com.reader.client.R
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import com.reader.service.utils.HexUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Date

class AutoNfcActivity : AppCompatActivity() {

    private val pos = ReaderAIDL
    private lateinit var textview_status: TextView
    private lateinit var textview_result: TextView
    private lateinit var textview_qr: TextView

    private lateinit var powerControl: PowerControl
    private lateinit var decodeReader: DecodeReader
    private lateinit var colorLedController: ColorLedController
    private val scanHandler = Handler(Looper.getMainLooper())
    private var qrSessionActive = false
    private val qrCharset: Charset = Charset.forName("GB2312")

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private var ledResetJob: Job? = null

    private val scanRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!qrSessionActive) return
            try {
                decodeReader.cmdSend(StringUtil.hexStringToBytes(SCAN_COMMAND))
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to send scan command", ex)
                updateQrStatus("QR scan command failed: ${ex.message}")
            } finally {
                if (qrSessionActive) {
                    scanHandler.postDelayed(this, QR_SCAN_INTERVAL_MS)
                }
            }
        }
    }

    private val decodeListener: IDecodeReaderListener = object : IDecodeReaderListener {
        override fun onRecvData(data: ByteArray?) {
            if (data == null) return
            val result = try {
                String(data, qrCharset)
            } catch (ex: UnsupportedEncodingException) {
                Log.e(TAG, "QR charset decode error", ex)
                updateQrStatus("QR charset error: ${ex.message}")
                showFailureFeedback()
                scheduleLedReset(LED_ERROR_HOLD_MS)
                return
            }

            runOnUiThread {
                updateQrStatus("QR Result: $result")
                pos.peripheral_buzzer(120)
            }
            showSuccessFeedback()
            scheduleLedReset(LED_SUCCESS_HOLD_MS)

            scanHandler.removeCallbacks(scanRunnable)
            if (qrSessionActive) {
                scanHandler.postDelayed(scanRunnable, QR_SCAN_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_nfc)

        textview_status = findViewById(R.id.textview_status)
        textview_result = findViewById(R.id.textview_result)
        textview_qr = findViewById(R.id.textview_qr)

        powerControl = PowerControl(applicationContext)
        decodeReader = DecodeReader(applicationContext)
        colorLedController = ColorLedController(applicationContext)
        resetLedState()

        pos.register(applicationContext, com.reader.service.IReaderSdkListener { data ->
            // Listener for async messages from the service.
            // We can update the UI here if needed.
        })

        scope.launch {
            // Add a small delay to allow the service to initialize after registration.
            delay(500)
            runNfcFlow()
        }
    }

    override fun onStart() {
        super.onStart()
        resetLedState()
        startQrScan()
    }

    override fun onStop() {
        super.onStop()
        stopQrScan()
        resetLedState()
    }

    override fun onDestroy() {
        super.onDestroy()
        ledResetJob?.cancel()
        ledResetJob = null
        job.cancel()
        scope.launch(Dispatchers.IO) {
            pos.nfc_close()
        }
        stopQrScan()
        resetLedState()
        pos.unRegister()
    }

    @SuppressLint("CheckResult")
    private suspend fun runNfcFlow() {
        withContext(Dispatchers.IO) {
            // 1. Open NFC
            updateStatus("Opening NFC reader...")
            val openResult = pos.nfc_open()
            if (openResult != ReaderConstant.RESULT_CODE_SUCCESS) {
                updateStatus("Failed to open NFC reader. Code: $openResult")
                showFailureFeedback()
                scheduleLedReset(LED_ERROR_HOLD_MS)
                return@withContext
            }

            while (job.isActive) {
                resetLedState()
                updateStatus("NFC Reader Opened. Waiting for card...")
                runOnUiThread { textview_result.text = "" }

                // 2. Detect Card
                var cardDetected = false
                while (!cardDetected && job.isActive) {
                    val pollResult = pos.nfc_pollOnMifareCard(1000)
                    if (pollResult.isNotEmpty()) {
                        try {
                            val resp = JSON.parseObject(pollResult)
                            if (resp.containsKey(ReaderConstant.KEY_RESULT_CODE) &&
                                resp.getIntValue(ReaderConstant.KEY_RESULT_CODE) == ReaderConstant.RESULT_CODE_SUCCESS
                            ) {
                                cardDetected = true
                                updateStatus("Card detected. Reading card data...")
                            }
                        } catch (e: Exception) {
                            // Ignore JSON parsing errors, just retry
                            updateStatus("Error detecting card, retrying...")
                        }
                    }
                    delay(500) // Wait before retrying
                }

                if (!job.isActive) return@withContext

                // 3. Send APDU and get data
                val cardDataTriple = sendApduAndGetData()
                if (cardDataTriple != null) {
                    updateStatus("Card reading complete.")
                    showResult(cardDataTriple.first, cardDataTriple.second, cardDataTriple.third)
                    showSuccessFeedback()
                    delay(LED_SUCCESS_HOLD_MS)
                } else {
                    updateStatus("Failed to read card data.")
                    showFailureFeedback()
                    delay(LED_ERROR_HOLD_MS)
                }

                // Wait for card to be removed and then prompt for a new one
                resetLedState()
                updateStatus("Please remove the card.")
                delay(2000) // Wait 2 seconds for card removal

                runOnUiThread {
                    textview_status.text = "Present a new card."
                }
                delay(1000)
            }
        }
    }

    private fun sendApduAndGetData(): Triple<String?, String?, String?>? {
        // 1) SELECT PPSE
        updateStatus("Sending PPSE command...")
        val ppseResp = executeNfcApduCommandGetResponse("PPSE", "00A404000E325041592E5359532E444446303100")
        if (ppseResp == null) {
            updateStatus("Failed on PPSE command.")
            return null
        }
        updateStatus("PPSE OK. Parsing AID...")

        val parsed = com.reader.client.emv.TlvParser.parse(ppseResp.dropLast(4))
        val aid = parsed.mapNotNull { it.getAid() }.firstOrNull()
        val label = parsed.mapNotNull { it.getLabel() }.firstOrNull()

        var pdol: String? = null
        updateStatus("Sending SELECT AID command...")
        val selectResp = executeNfcApduCommandGetResponse("SELECT AID", "00A4040007$aid")
        if (selectResp == null) {
            updateStatus("Failed on SELECT AID command.")
            return null
        }
        updateStatus("SELECT AID OK. Parsing PDOL...")
        val vparsed = com.reader.client.emv.TlvParser.parse(selectResp.dropLast(4))
        pdol = vparsed.mapNotNull { it.getPdol() }.firstOrNull()

        // --- GPO Command ---
        updateStatus("Building and sending GPO command...")
        val amountInCents = 100L // 1.00 currency unit, example amount
        val pdolBytes = if (pdol != null) HexUtils.hexStringToBytes(pdol) else ByteArray(0)
        val pdolData = if (pdolBytes.isNotEmpty()) buildPdolData(pdolBytes, amountInCents) else ByteArray(0)

        val gpoApduHex = buildGpoApduHex(pdolData)
        val gpoResp = executeNfcApduCommandGetResponse("GPO", gpoApduHex)
        if (gpoResp == null) {
            updateStatus("Failed on GPO command.")
            return null
        }
        updateStatus("GPO OK. Parsing card data...")

        // --- Parse GPO Response and Read Records ---
        var cardInfo: Pair<String, String>? = EmvUtils.extractPanAndExpiry(gpoResp)

        if (cardInfo == null) {
            updateStatus("Tag 57 not found, trying to read records from AFL...")
            val recordInfo = parseAflAndReadRecords(gpoResp)
            val panFromRec = recordInfo["PAN"]
            if (panFromRec != null) {
                cardInfo = Pair(panFromRec, recordInfo["EXP"] ?: "")
            } else {
                updateStatus("Could not find PAN in records.")
            }
        }

        return if (cardInfo != null) Triple(cardInfo.first, cardInfo.second, label) else null
    }

    private fun updateStatus(message: String) {
        runOnUiThread {
            textview_status.text = message
        }
    }

    private fun showResult(pan: String?, expiry: String?, label: String?) {
        runOnUiThread {
            if (pan != null && expiry != null) {
                val labelText = if (label != null) "Label: $label" else ""
                textview_result.text = "PAN: $pan Expiry (YYMM): $expiry\n$labelText"
                pos.peripheral_buzzer(200) // Add buzzer call here
            } else {
                textview_result.text = "Could not retrieve PAN and Expiry."
            }
        }
    }

    // ------------------------- Helper functions from NfcActivity -------------------------

    private fun executeNfcApduCommandGetResponse(functionName: String, commandHex: String): String? {
        val ba = HexUtils.hexStringToBytes(commandHex)
        val resp = pos.nfc_sendApdu(ba)
        return if (resp.isNotEmpty()) resp else null
    }

    private fun startQrScan() {
        if (qrSessionActive) return
        updateQrStatus("Preparing QR scanner...")
        val powerResult = runCatching { powerControl.decodePower(1) }.getOrElse {
            Log.e(TAG, "Failed to power on QR scanner", it)
            updateQrStatus("QR power on error: ${it.message}")
            resetLedState()
            qrSessionActive = false
            scheduleQrRetry()
            return
        }
        if (powerResult != ResultCode.SUCCESS) {
            Log.e(TAG, "QR power on returned code $powerResult")
            updateQrStatus("QR power on failed (code $powerResult). Retrying...")
            resetLedState()
            qrSessionActive = false
            scheduleQrRetry()
            return
        }

        val openResult = runCatching { decodeReader.open(QR_BAUD_RATE) }.getOrElse {
            Log.e(TAG, "Failed to open QR reader", it)
            updateQrStatus("QR reader open error: ${it.message}")
            resetLedState()
            qrSessionActive = false
            scheduleQrRetry()
            return
        }

        if (openResult == ResultCode.SUCCESS) {
            qrSessionActive = true
            decodeReader.setDecodeReaderListener(decodeListener)
            updateQrStatus("QR scanner ready. Waiting for code...")
            scanHandler.post(scanRunnable)
        } else {
            updateQrStatus("Failed to open QR reader (code $openResult). Retrying...")
            resetLedState()
            qrSessionActive = false
            scheduleQrRetry()
        }
    }

    private fun scheduleQrRetry() {
        scanHandler.removeCallbacks(scanRunnable)
        scanHandler.postDelayed({ startQrScan() }, QR_RETRY_DELAY_MS)
    }

    private fun stopQrScan() {
        qrSessionActive = false
        scanHandler.removeCallbacksAndMessages(null)

        val closeResult = runCatching { decodeReader.close() }.getOrElse {
            Log.e(TAG, "Error closing QR reader", it)
            -1
        }
        if (closeResult != ResultCode.SUCCESS) {
            Log.w(TAG, "QR reader close returned code $closeResult")
        }

        val powerOffResult = runCatching { powerControl.decodePower(0) }.getOrElse {
            Log.e(TAG, "Error powering off QR reader", it)
            -1
        }
        if (powerOffResult != ResultCode.SUCCESS) {
            Log.w(TAG, "QR power off returned code $powerOffResult")
        }
        resetLedState()
    }

    private fun updateQrStatus(message: String) {
        runOnUiThread {
            textview_qr.text = message
        }
    }

    private fun showSuccessFeedback() {
        pos.peripheral_ledGreen()
        colorLedController.showGreen(LED_SUCCESS_HOLD_MS)
    }

    private fun showFailureFeedback() {
        pos.peripheral_ledRed()
        colorLedController.showRed(LED_ERROR_HOLD_MS)
    }

    private fun showWaitFeedback() {
        pos.peripheral_ledYellow()
        colorLedController.showYellow(LED_WAIT_HOLD_MS)
    }


    private fun scheduleLedReset(delayMs: Long) {
        ledResetJob?.cancel()
        if (delayMs <= 0) {
            resetLedState()
            ledResetJob = null
            return
        }
        ledResetJob = scope.launch {
            delay(delayMs)
            resetLedState(cancelPending = false)
            ledResetJob = null
        }
    }

    private fun resetLedState(cancelPending: Boolean = true) {
        if (cancelPending) {
            ledResetJob?.cancel()
            ledResetJob = null
        }
        pos.peripheral_ledClose()
        colorLedController.shutdown()
    }

    companion object {
        private const val TAG = "AutoNfcActivity"
        private const val QR_BAUD_RATE = 115200
        private const val QR_SCAN_INTERVAL_MS = 3000L
        private const val QR_RETRY_DELAY_MS = 3000L
        private const val SCAN_COMMAND = "7E01303030304053434E545247313B03"
        private const val LED_SUCCESS_HOLD_MS = 2500L
        private const val LED_ERROR_HOLD_MS = 2500L
        private const val LED_WAIT_HOLD_MS = 2500L
    }


    private fun stripSWBytes(hexResp: String): ByteArray {
        val b = HexUtils.hexStringToBytes(hexResp)
        if (b.size >= 2) {
            val sw1 = b[b.size - 2].toInt() and 0xFF
            val sw2 = b[b.size - 1].toInt() and 0xFF
            if (sw1 == 0x90 && sw2 == 0x00) {
                return b.copyOfRange(0, b.size - 2)
            }
        }
        return b
    }

    private fun buildPdolData(pdolBytes: ByteArray, amountInCents: Long): ByteArray {
        val out = ArrayList<Byte>()
        var i = 0
        while (i < pdolBytes.size) {
            var tag = pdolBytes[i].toInt() and 0xFF
            i++
            if ((tag and 0x1F) == 0x1F) {
                var b: Int
                do {
                    b = pdolBytes[i].toInt() and 0xFF
                    tag = (tag shl 8) or b
                    i++
                } while ((b and 0x80) == 0x80)
            }

            val len = pdolBytes[i].toInt() and 0xFF
            i++

            val value: ByteArray = when (tag) {
                0x9F66 -> fitToLength(byteArrayOf(0x32.toByte(), 0x00.toByte(), 0x40.toByte(), 0x00.toByte()), len)
                0x9F02 -> amountToBcd(amountInCents, len)
                0x9F03 -> ByteArray(len) { 0x00 }
                0x9F1A -> fitToLength(byteArrayOf(0x02.toByte(), 0xF4.toByte()), len)
                0x95 -> fitToLength(byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00), len)
                0x5F2A -> fitToLength(byteArrayOf(0x02.toByte(), 0xF4.toByte()), len)
                0x9A -> fitToLength(dateYYMMDD(), len)
                0x9C -> fitToLength(byteArrayOf(0x00), len)
                0x9F37 -> randomBytes(len)
                else -> ByteArray(len) { 0x00 }
            }
            out.addAll(value.toList())
        }
        return out.toByteArray()
    }

    private fun fitToLength(src: ByteArray, len: Int): ByteArray {
        return if (src.size == len) src
        else if (src.size < len) {
            ByteArray(len - src.size) { 0x00 } + src
        } else {
            src.copyOfRange(src.size - len, src.size)
        }
    }

    private fun amountToBcd(amountInCents: Long, numBytes: Int): ByteArray {
        val digits = numBytes * 2
        val s = amountInCents.toString().padStart(digits, '0')
        return decimalStringToBcd(s)
    }

    private fun decimalStringToBcd(s: String): ByteArray {
        val out = ByteArray((s.length + 1) / 2)
        var j = 0
        var i = 0
        if (s.length % 2 != 0) {
            val ns = "0$s"
            while (j < ns.length) {
                val hi = ns[j].digitToInt()
                val lo = ns[j + 1].digitToInt()
                out[i] = ((hi shl 4) or lo).toByte()
                j += 2
                i++
            }
        } else {
            while (j < s.length) {
                val hi = s[j].digitToInt()
                val lo = s[j + 1].digitToInt()
                out[i] = ((hi shl 4) or lo).toByte()
                j += 2
                i++
            }
        }
        return out
    }

    private fun dateYYMMDD(): ByteArray {
        val fmt = java.text.SimpleDateFormat("yyMMdd", java.util.Locale.US)
        val ds = fmt.format(Date())
        return decimalStringToBcd(ds)
    }

    private fun randomBytes(len: Int): ByteArray {
        val r = java.security.SecureRandom()
        val b = ByteArray(len)
        r.nextBytes(b)
        return b
    }

    private fun buildGpoApduHex(pdolData: ByteArray): String {
        return if (pdolData.isEmpty()) {
            "80A8000002830000"
        } else {
            val pdolTl = byteArrayOf(0x83.toByte(), pdolData.size.toByte()) + pdolData
            val lc = pdolTl.size.toByte()
            val apdu = byteArrayOf(0x80.toByte(), 0xA8.toByte(), 0x00, 0x00, lc) + pdolTl + byteArrayOf(0x00)
            HexUtils.bytesToHexString(apdu)
        }
    }

    private fun parseAflAndReadRecords(gpoRespHex: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val gpoBytes = stripSWBytes(gpoRespHex)

        val gpoTlvs = com.reader.client.emv.TlvParser.parse(HexUtils.bytesToHexString(gpoBytes))
        val aflTlv = gpoTlvs.mapNotNull { it.findTag("94") }.firstOrNull()

        val afl = if (aflTlv != null) {
            HexUtils.hexStringToBytes(aflTlv.value)
        } else {
            if (gpoBytes.isNotEmpty() && (gpoBytes[0].toInt() and 0xFF) != 0x77) {
                gpoBytes
            } else {
                ByteArray(0)
            }
        }

        if (afl.isEmpty()) return result

        var i = 0
        while (i + 3 < afl.size) {
            val b1 = afl[i].toInt() and 0xFF
            val firstRec = afl[i + 1].toInt() and 0xFF
            val lastRec = afl[i + 2].toInt() and 0xFF
            val sfi = (b1 and 0xF8) shr 3
            for (rec in firstRec..lastRec) {
                val p2 = ((sfi shl 3) or 0x04) and 0xFF
                val readCmd = String.format("00B2%02X%02X00", rec, p2)
                val readResp = executeNfcApduCommandGetResponse("READ REC SFI${sfi} R$rec", readCmd)
                if (readResp != null) {
                    val recTlvs = com.reader.client.emv.TlvParser.parse(readResp.dropLast(4))

                    val panTlv = recTlvs.mapNotNull { it.findTag("5A") }.firstOrNull()
                    if (panTlv != null) {
                        result["PAN"] = bcdToString(HexUtils.hexStringToBytes(panTlv.value))
                    }

                    val expTlv = recTlvs.mapNotNull { it.findTag("5F24") }.firstOrNull()
                    if (expTlv != null) {
                        result["EXP"] = bcdToString(HexUtils.hexStringToBytes(expTlv.value))
                    }

                    if (result.containsKey("PAN")) return result
                }
            }
            i += 4
        }
        return result
    }

    private fun bcdToString(bcd: ByteArray): String {
        val sb = StringBuilder()
        for (b in bcd) {
            val hi = ((b.toInt() and 0xF0) ushr 4)
            val lo = (b.toInt() and 0x0F)
            if (hi <= 9) sb.append(hi) else break
            if (lo <= 9) sb.append(lo) else break
        }
        return sb.toString()
    }

    object EmvUtils {
        fun extractPanAndExpiry(gpoHex: String): Pair<String, String>? {
            val data = gpoHex.uppercase()
            val idx = data.indexOf("57")
            if (idx == -1) return null
            val lenHex = data.substring(idx + 2, idx + 4)
            val len = lenHex.toInt(16)
            val track2 = data.substring(idx + 4, idx + 4 + len * 2)
            val sepIdx = track2.indexOfAny(charArrayOf('D', 'F'))
            if (sepIdx == -1) return null
            val pan = track2.substring(0, sepIdx)
            val expiry = track2.substring(sepIdx + 1, sepIdx + 5)
            return Pair(pan, expiry)
        }
    }
}
