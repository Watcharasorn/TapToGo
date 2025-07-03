package com.reader.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import android_serialport_api.SerialPort
import com.swallowsonny.convertextlibrary.readStringBE
import timber.log.Timber
import java.io.*


/**
 * Created by ljj on 2018/12/11
 * 读取485的数据
 */
class SerialRecvThread(var context: Context) : SerialData {
    private var mInputStream: InputStream? = null
    private var mOutputStream: OutputStream? = null
    private var mListener: SerialDataListener? = null
    private val baudrate = 3000000//9600 //115200;
    private val flags = 0
    private var mReadThread: ReadThread? = null
    private var mDataArray = ByteArray(0) //串口数据缓存
    private val parser = BaseResponse() //解析器
    private var mErrCount = 0  //串口失败次数

    private inner class ReadThread : Thread() {
        @UseExperimental(ExperimentalStdlibApi::class)
        override fun run() {
            super.run()
            while (!isInterrupted) {
                var size: Int
                if (!canRunning) {
                    sleep()
                    continue
                }
                try {
                    val buffer = ByteArray(4096+14)
                    if (mInputStream == null) {
                        Timber.e("$TAG  mInputStream == null")
                        //超过5次 休眠10秒
                        if(mErrCount > 5){
                            try {
                                sleep(10000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                        initSerialPort()
                        try {
                            sleep(2000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                        if(mErrCount < Int.MAX_VALUE) {
                            mErrCount++
                        }
                        continue
                    }
                    mErrCount = 0
                    //获取串口数据
                    size = mInputStream!!.read(buffer) //非阻塞

                    if (size > 0) {
                        //Timber.d("[收] len:$size")
                        mDataArray+=buffer.copyOf(size)

                        var canRun = true
                        while (canRun){
                            val ret = parser.ini(mDataArray,size)
                            if(ret && mListener != null){
                                //TODO::回调
                               mListener?.onReadSerialData(parser)
                            }

                            //粘包处理
                            if(!ret){
                                Timber.i("[包不合格抛弃]")
                                mDataArray = ByteArray(0)
                                canRun = false
                            }
                            else if(mDataArray.size >= parser.total+6){
                                if(mDataArray.size == parser.total+6){
                                    canRun = false
                                }
                                if(mDataArray.size > parser.total+6) {
                                    Timber.i("[粘包处理] size:${mDataArray.size} total:${parser.total + 6}")
                                }
                                mDataArray = mDataArray.copyOfRange(parser.total+6,mDataArray.size)
                            }else{
                                canRun = false
                                Timber.i("[包长度小于一个完整包] len:${parser.total} size:${mDataArray.size}")
                            }
                        }
                    }
                } catch (e: IOException) {
                    Timber.e("$TAG SerialQrReaderFactory  err:" + e.message)
                    mInputStream = null
                    e.printStackTrace()
                }
                sleep()
            }
        }

        private fun sleep() {
            try {
                sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

//        fun send(commandData:ByteArray){
//            //组织包头 包尾 中间数据
//            try {
//                mOutputStream?.write(commandData)
//                mOutputStream?.flush()
//                Timber.d("[发] end")
//            }catch (e:Exception){
//                e.printStackTrace()
//                Timber.d("SerialRecvThread send err:${e.message}")
//
//                if(mOutputStream != null){
//                    close(mOutputStream)
//                }
//                if (mInputStream != null) {
//                    close(mInputStream)
//                }
//                if (mSerialPort != null) {
//                    mSerialPort!!.close()
//                }
//                mInputStream = null
//                mOutputStream = null
//            }
//        }

    }

    @Volatile
    private var canRunning = false

    /**
     * 初始化串口
     */
    private fun initSerialPort() {
        if (waitForBarcodeReaderReady()) {
            try {
                if (mSerialPort != null) {
                    mSerialPort!!.close()
//                    closeSerialPort()
                }
                val tmp = getSerialPath("ttyACM")
                if (tmp.isNullOrEmpty()) {
                    Timber.d("unable to find ttyACM*")
                    return
                } else {
                    mSerialPort = SerialPort(tmp, baudrate, flags)
                    mInputStream = mSerialPort!!.inputStream
                    mOutputStream = mSerialPort!!.outputStream
                    Timber.d("$TAG initSerialPort $tmp ok baudrate: $baudrate flags: $flags")
                }
//                    var initOk = false
//                    if (!initOk) {
//                        try {
//                            val pathname = "/dev/ttyACM0"
//                            mSerialPort = SerialPort(pathname, baudrate, flags)
//                            mInputStream = mSerialPort!!.inputStream
//                            mOutputStream = mSerialPort!!.outputStream
//                            Timber.d("$TAG initSerialPort ttyACM0 ok baudrate: $baudrate flags: $flags")
//                            initOk = true
//                        } catch (e: Exception) {
//                            Timber.e("$TAG ttyACM0 ERROR " + e.message)
//                            closeSerialPort()
//                        }
//                    }
//                    if (!initOk) {
//                        try {
//                            val pathname = "/dev/ttyACM1"
//                            mSerialPort = SerialPort(pathname, baudrate, flags)
//                            mInputStream = mSerialPort!!.inputStream
//                            mOutputStream = mSerialPort!!.outputStream
//                            Timber.d("$TAG initSerialPort ttyACM1 ok baudrate: $baudrate flags: $flags")
//                            initOk = true
//                        } catch (e: Exception) {
//                            Timber.e("$TAG ttyACM1 ERROR " + e.message)
//                            closeSerialPort()
//                        }
//                    }
//                    if (!initOk) {
//                        try {
//                            val pathname = "/dev/ttyACM2"
//                            mSerialPort = SerialPort(pathname, baudrate, flags)
//                            mInputStream = mSerialPort!!.inputStream
//                            mOutputStream = mSerialPort!!.outputStream
//                            Timber.d("$TAG initSerialPort ttyACM2 ok baudrate: $baudrate flags: $flags")
//                            initOk = true
//                        } catch (e: Exception) {
//                            Timber.e("$TAG ttyACM2 ERROR " + e.message)
//                            closeSerialPort()
//                        }
//                    }
            } catch (e: Exception) {
                Timber.e("$TAG ttyACMX ERROR ")
                closeSerialPort()
            }
        }
    }

    //节点
    private fun getSerialPath(prefix: String): String? {
        var result: String? = null
        for (i in 0..255) {
            val file = File("/dev/$prefix$i")
            if (file.exists()) {
                result = file.absolutePath
            }
        }
        return result
    }
    /***
     * 释放串口对象
     */
    private fun closeSerialPort() {
        if (mInputStream != null) {
            close(mInputStream)
        }
        if (mOutputStream != null) {
            close(mOutputStream)
        }
        if (mSerialPort != null) {
            mSerialPort!!.close()
        }
        mInputStream = null
        mOutputStream = null

    }

    /**
     * 停止线程
     */
    override fun stop() {
        Timber.e("$TAG serial stop")
        canRunning = false
//        if (mReadThread != null) //mReadThread.interrupt();
        if (mInputStream != null) {
            close(mInputStream)
        }
        if (mSerialPort != null) {
            mSerialPort!!.close()
        }
        mInputStream = null
        mSerialPort = null
        //mListener = null;
        if(isUSBBroadcastRegister) {
            context.unregisterReceiver(mUsbBroadcast);
            isUSBBroadcastRegister = false;
        }
    }

    /***
     * 是否能发送数据
     */
    override fun can(): Boolean {
        return mOutputStream != null && mInputStream != null
    }


    /***
     * 发送数据
     */
    override fun send(data: BaseCommand) {
        val commandData = data.CommondData()
        if(commandData.size < 100) {
            Timber.d("[发] len:${commandData.size} data:${commandData.readStringBE(0, commandData.size)}")
        }else{
            Timber.d("[发] len:${commandData.size} data:${commandData.readStringBE(0, 10)} ... ${commandData.readStringBE(commandData.size-4,commandData.size)} ")
        }

        //组织包头 包尾 中间数据
        try {
            mOutputStream?.write(commandData)
            mOutputStream?.flush()
        }catch (e:Exception){
            e.printStackTrace()
            Timber.d("SerialRecvThread send err:${e.message}")

            if(mOutputStream != null){
                close(mOutputStream)
            }
            if (mInputStream != null) {
                close(mInputStream)
            }
            if (mSerialPort != null) {
                mSerialPort!!.close()
            }
            mInputStream = null
            mOutputStream = null
        }
    }

//    /***
//     * 模拟发射数据
//     */
//    fun testSend(){
//        val ba = ByteArray(3)
//        ba.writeInt8(1,0)
//        ba.writeInt8(2,1)
//        ba.writeInt8(3,2)
//        Timber.d("mOutputStream is null:${mOutputStream == null} size:${ba.size}")
//        mOutputStream?.write(ba)
//    }

    /**
     * 设置返回数据
     */
    override fun setListener(listener: SerialDataListener) {
        mListener = listener
    }

    /**
     * 关闭流
     */
    private fun close(os: Closeable?) {
        if (os != null) {
            try {
                os.close()
            } catch (e: IOException) {
                Log.e(TAG, e.message)
            }
        }
    }

    /**
     * 打开线程 默认参数
     */
    override fun start() {
        Timber.d("$TAG serial start")

        canRunning = true

        if (mReadThread == null) {
            mReadThread = ReadThread()
            Timber.d("$TAG new mReadThread")
            mReadThread!!.start()
            if (!isUSBBroadcastRegister) {
                val filter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
                context.registerReceiver(mUsbBroadcast, filter)
                isUSBBroadcastRegister = true
            }
        }
    }


    /**
     * 上电后调用此方法等待二维码阅读器连接
     * @return true，连接上  false 连接失败
     */
    private fun waitForBarcodeReaderReady(): Boolean {
        for (count in 0..49) {
            if (isBarcodeReaderConnected) {
                return true
            }
            try {
                Thread.sleep(200)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return false

    }

    //设备确认扫码设备是否正常
    private val isBarcodeReaderConnected: Boolean
        private get() {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val usbList = usbManager.deviceList
            for (key in usbList.keys) {
                val usbDevice = usbList[key]
//                System.out.println("=====usb:${JSON.toJSONString(usbDevice)}=====")
//                Timber.d("usb :${JSON.toJSONString(usbDevice)}")
//                if (usbDevice != null && usbDevice.productId == 0x0005 && usbDevice.vendorId == 0x0bb4) {
//                    return true
//                }
                if (usbDevice != null && usbDevice.productId == 9123 && usbDevice.vendorId == 1659) {
                    return true
                }
            }
//            return false
            return true //有些固件不返回pid和VID 只能直接通过，但会引起连上二维码阅读器
        }

    /**
     * Create a broadcast receiver.
     */
    private var isUSBBroadcastRegister = false
    private val mUsbBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(paramAnonymousContext: Context, paramAnonymousIntent: Intent) {
            val action = paramAnonymousIntent.action
            Timber.d("mUsbBroadcast action:$action")
            if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                closeSerialPort()
            }
        }
    }

    companion object {
        private const val TAG = "SerialRecvThread"
        private var mSerialPort: SerialPort? = null
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: SerialRecvThread? = null
        fun getInstance(context: Context): SerialRecvThread? {
            if (INSTANCE == null) {
                Timber.d("create $TAG")
                INSTANCE = SerialRecvThread(context)
            }
            return INSTANCE
        }
    }
}