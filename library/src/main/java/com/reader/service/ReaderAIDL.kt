package com.reader.service

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.RemoteException
import com.alibaba.fastjson.JSON
import com.reader.library.BuildConfig
import com.reader.service.utils.*
import org.joda.time.DateTime
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

@SuppressLint("StaticFieldLeak")
object ReaderAIDL {
    private const val TAG = "ReaderAIDL"
    private var connectionTime = 0L //连接时间
    private var mContext: Context? = null
    private var readerService: IReaderService? = null//这个对象
    private var mReaderListener: IReaderSdkListener? = null//SDK回调结果
    private var hashtableOutPut:Hashtable<String,String>? = null  //输出结果
    private var nfc_state_open = false //打开状态 控制按钮
    private val mInnerReaderListener: IReaderListener = object : IReaderListener.Stub() {
        @SuppressLint("CheckResult")
        @Throws(RemoteException::class)
        override fun onRecvData(data: String) {
            if (mReaderListener != null) {

                Timber.d("$TAG [收数据]:$data")
                val resp = JSON.parseObject(data, BaseResponse::class.java)
                val outPutData = resp.outPutData(mContext!!)

                when(resp.moduleCode){
                    //TEST
                    8->{
                        //测试升级
                        if(resp.functionCode == 1){
                            if (curTestUpdateIndex >= lstTestUpdateData.size) {
                                outPutData.put(ReaderConstant.KEY_RESULT_MSG,"update success please reboot")
                                //返回界面结果
                                mReaderListener?.onRecvData(outPutData)
                                //结束升级
                                return
                            }
                            //继续下一个包
                            val upDateCommand = BaseCommand(8,1)
                            upDateCommand.payload = lstTestUpdateData[curTestUpdateIndex]
                            baseSendData(upDateCommand)
                            curTestUpdateIndex++

                            outPutData.put(ReaderConstant.KEY_RESULT_MSG,"${if(lstTestUpdateData.isNotEmpty()) curTestUpdateIndex*100 / lstTestUpdateData.size else 0}%")
                            //返回界面结果
                            mReaderListener?.onRecvData(outPutData)
                        }
                    }
                    //Update
                    ReaderConstant.MODULE_UPDATE->{
                        //升级芯片响应
                        if (resp.functionCode == ReaderConstant.FUNCTION_UPDATE_IC) {
                            if (resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS) {
                                if (curIcUpdateIndex >= lstIcUpdateData.size) {
                                    outPutData.put(ReaderConstant.KEY_RESULT_MSG,"update success please reboot")
                                    //返回界面结果
                                    mReaderListener?.onRecvData(outPutData)
                                    //结束升级
                                    return
                                }
                                //继续下一个包
                                val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_IC)
                                upDateCommand.nextPacket(
                                    curIcUpdateIndex,
                                    lstIcUpdateData[curIcUpdateIndex]
                                )
                                baseSendData(upDateCommand)
                                curIcUpdateIndex++

                                outPutData.put(ReaderConstant.KEY_RESULT_MSG,"${if(lstIcUpdateData.isNotEmpty()) curIcUpdateIndex*100 / lstIcUpdateData.size else 0}%")
                                //返回界面结果
                                mReaderListener?.onRecvData(outPutData)
                            }else{

                                //报错信息
                                mReaderListener?.onRecvData(outPutData)
                            }
                        }
                        //升级系统响应
                        if (resp.functionCode == ReaderConstant.FUNCTION_UPDATE_SYSTEM) {
                            if (resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS) {
                                    upDateSystemTime = System.currentTimeMillis() //正常响应
                                if (curSystemUpdateIndex >= lstSystemUpdateData.size) {
                                    outPutData.put(ReaderConstant.KEY_RESULT_MSG,"update success please reboot")
                                    //返回界面结果
                                    mReaderListener?.onRecvData(outPutData)
                                    //结束升级
                                    return
                                }
//                                //继续下一个包
//                                val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_SYSTEM)
//                                upDateCommand.nextPacket(
//                                    curSystemUpdateIndex,
//                                    lstSystemUpdateData[curSystemUpdateIndex]
//                                )
//                                baseSendData(upDateCommand)
//                                Timber.d("$TAG [系统升级] 下一帧 index:${curSystemUpdateIndex} size:${lstSystemUpdateData[curSystemUpdateIndex].size}")
//                                curSystemUpdateIndex++
                                outPutData.put(ReaderConstant.KEY_RESULT_MSG,"${if(lstSystemUpdateData.isNotEmpty()) curSystemUpdateIndex*100 / lstSystemUpdateData.size else 0}%")
                                //返回界面结果
                                mReaderListener?.onRecvData(outPutData)
                            }else{
//                                dipossUpDateSystem?.dispose()
                                //报错信息
                                mReaderListener?.onRecvData(outPutData)
                            }
                        }
                    }
                    //INFO
                    ReaderConstant.MODULE_INFO->{
                        if(resp.functionCode == ReaderConstant.FUNCTION_LOG_DOWN){
                            //下载日志
                            if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
                                downLoadLogTime = System.currentTimeMillis() //更新时间
                                //第一包
                                if(downLoadLogNum == 0 && resp.payload.size == 12){
                                    downLoadLogNum = HexUtils.hexToIntLE(resp.payload,0,4)
                                    val downLoadLogSize = HexUtils.hexToIntLE(resp.payload,4,4)
                                    //crc校验包
                                    Timber.d("$TAG [下载日志] 响应第一帧 num:$downLoadLogNum size:$downLoadLogSize")
                                }else{
                                    //下一包
                                    downLoadLogData += resp.payload.copyOfRange(4,resp.payload.size)
                                    //Timber.d("$TAG [下载日志] 响应下一包 index:$downLoadLogIndex size:${resp.payload.size}")
                                }
                                outPutData.put(ReaderConstant.KEY_RESULT_MSG,"${if(downLoadLogNum > 0) downLoadLogIndex*100 / downLoadLogNum else 0}%")
                            }else{
                                //Timber.d("$TAG [下载日志] 响应下一包 num:$downLoadLogNum index:$downLoadLogIndex size:${resp.payload.size}")
                                //第一包 错误 结束
                                if(downLoadLogNum == 0 && resp.payload.isEmpty()){
                                    if (downLondLogThread != null) {
                                        Timber.d( "$TAG downLondLogThread" + downLondLogThread?.getId() + " end by resp");
                                        downLondLogThread?.interrupt();
                                        downLondLogThread = null;
                                    }
                                }else {
                                    //错误
                                    downLoadLogTime = System.currentTimeMillis() //更新时间
                                    //下一包
                                    downLoadLogData += resp.payload.copyOfRange(
                                        4,
                                        resp.payload.size
                                    )
                                }

                            }
                            mReaderListener?.onRecvData(outPutData)
                        }else {
                            //其他系统信息
                            val key = when (resp.functionCode) {
                                ReaderConstant.FUNCTION_INFO_SYSTEM -> ReaderConstant.KEY_INFO_SYSTEM_VERSION
                                ReaderConstant.FUNCTION_INFO_IC -> ReaderConstant.KEY_INFO_IC_VERSION
                                ReaderConstant.FUNCTION_INFO_SN -> ReaderConstant.KEY_INFO_SN
                                else -> ""
                            }
                            if (key.isNotEmpty() && resp.payload.isNotEmpty()) {
                                outPutData.put(
                                    key,
                                    String(resp.payload)
                                )
                                hashtableOutPut = outPutData
                            }else {
                                hashtableOutPut = outPutData
                                mReaderListener?.onRecvData(outPutData)
                            }
                        }
                    }
                    //NFC
                    ReaderConstant.MODULE_NFC->{
                        when(resp.functionCode){
                            //打开关闭
                            ReaderConstant.FUNCTION_NFC_OPEN->{
                                nfc_state_open = (resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS)
                                hashtableOutPut = outPutData
                            }
                            ReaderConstant.FUNCTION_NFC_CLOSE->{
                                if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
                                    nfc_state_open = false
                                }
                                hashtableOutPut = outPutData
                            }
                            //探测有卡(TODO)
                            ReaderConstant.FUNCTION_NFC_DETECT->{
                                mReaderListener?.onRecvData(outPutData)
                            }
                            //APDU
                            ReaderConstant.FUNCTION_NFC_APDU->{
                                if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
                                    outPutData.put(ReaderConstant.KEY_RESULT_MSG,
                                    HexUtils.bytesToHexString(resp.payload))
                                    System.out.println("Timber =======outPutData:$outPutData=======")
                                }

                                //APDU结果
                                hashtableOutPut = outPutData

                            }
                            //返回卡CARD_INFO信息
                            ReaderConstant.FUNCTION_NFC_CARD_INFO->{
//                                Timber.d("$TAG [收数据]: NFC 2")
                                if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS) {
//                                    Timber.d("$TAG [收数据]: NFC 3")
//                                    uint8_t type; 1
//                                    uint8_t nfccardType; 1
//                                    /* For type A card */
//                                    uint8_t ATQA[2];
//                                    uint8_t SAK[1];
//                                    uint8_t UID_Length;1
//                                    uint8_t UID[10];
//                                    /* For type B card */
//                                    uint8_t ATQB_Length; 1
//                                    uint8_t ATQB[13];
//                                    uint8_t PUPI[4];
                                    outPutData.put(ReaderConstant.KEY_NFC_TYPE,
                                        HexUtils.hexToIntLE(resp.payload,0,1).toString()
                                    )
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_TYPE,
                                        HexUtils.hexToIntLE(resp.payload,1,1).toString()
                                    )
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_ATQA,
                                        HexUtils.hexToIntLE(resp.payload,2,2).toString()
                                    )
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_SAK,
                                        HexUtils.hexToIntLE(resp.payload,4,1).toString()
                                    )
                                    val uidLen = HexUtils.hexToIntLE(resp.payload,5,1)
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_UID_LENGTH,
                                        uidLen.toString()
                                    )
                                    val strUid =  HexUtils.bytesToHexString(resp.payload.copyOfRange(6,6+uidLen).reversedArray())
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_UID,
                                        strUid
                                    )
                                    //TYPE B
                                    val atqbLen = HexUtils.hexToIntLE(resp.payload,16,1)
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_ATQB_LENGTH,
                                        atqbLen.toString()
                                    )
                                    val atqb = if(atqbLen>0) HexUtils.bytesToHexString(resp.payload.copyOfRange(17,17+atqbLen)).toString() else ""
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_ATQB,
                                        atqb
                                    )
                                    outPutData.put(ReaderConstant.KEY_NFC_CARD_PUPI,
                                        HexUtils.hexToLongLE(resp.payload,30,4).toString()
                                    )
                                }

                                //CARDINFO 结果
                                hashtableOutPut = outPutData

                            }
                            //读值
                            ReaderConstant.FUNCTION_NFC_READ_VALUE->{
                                if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS) {
                                    outPutData.put(ReaderConstant.KEY_NFC_READ_VALUE,
                                        HexUtils.bytesToHexString(resp.payload)
                                        //HexUtils.hexToLongLE(resp.payload,0,resp.payload.size).toString()
                                    )
                                }
                                //结果
                                hashtableOutPut = outPutData
                            }
                            //读块
                            ReaderConstant.FUNCTION_NFC_READ_BLOCK->{
                                if(resp.resultCode == ReaderConstant.RESULT_CODE_SUCCESS) {
                                    outPutData.put(ReaderConstant.KEY_NFC_READ_BLOCK,
                                        HexUtils.bytesToHexString(resp.payload)
                                    )
                                }
                                //结果
                                hashtableOutPut = outPutData
                            }
                            //NFC其他FUNCTION结果
                            else->{
                                hashtableOutPut = outPutData
                            }
                        }
                    }
                    //EMV
                    ReaderConstant.MODULE_EMV->{
                        hashtableOutPut = outPutData
                    }
                    //其他MODULE
                    else-> mReaderListener?.onRecvData(outPutData)
                }


            }
        }
    } //内部回调结果


    /***
     * 回调结果并初始化绑定服务
     */
    fun register(context: Context, readerListener: IReaderSdkListener?) {
        if (Math.abs(System.currentTimeMillis() - connectionTime) < 3000) {
            return
        }
        if (mContext == null) {
            mContext = context
            Timber.plant(
                FileLogTree()
                    .storeAt(SDCardUtils.getExternalCacheDir("log", context.applicationContext))
            )
            Timber.d("$TAG 版本:" + BuildConfig.VERSION_NAME)
        }
        connectionTime = System.currentTimeMillis()
        mReaderListener = readerListener
        Timber.d("$TAG [注册回调register] mReaderListener:${mReaderListener != null} readerService:${(readerService != null)}")
        if (readerService != null) {
            readerService?.registerListener(mInnerReaderListener)
            respSdkMsg(0, ReaderConstant.RESULT_CODE_SUCCESS, "connected")
            return
        }



        connect()
        waitForResult()
    }

    /***
     * 取消绑定和结果回调
     */
    @Throws(RemoteException::class)
    fun unRegister() {
        readerService?.unregisterListener(mInnerReaderListener)
    }

    /***
     * 连接对象
     */
    var mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Timber.d("$TAG onServiceConnected readerService:${service.hashCode()}")
            hashtableOutPut = Hashtable()
            respSdkMsg(0, ReaderConstant.RESULT_CODE_SUCCESS, "ServiceConnected")
//            if (readerService != null) {
//                readerService?.registerListener(mInnerReaderListener)
//                Timber.d("$TAG readerService exist done")
//                return
//            }
            readerService = IReaderService.Stub.asInterface(service)
            try {

                //本来计划初始化时带参数
                readerService?.init()

                //注册回调
                readerService?.registerListener(mInnerReaderListener)

            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Timber.d("$TAG onServiceDisconnected")
            respSdkMsg(0, ReaderConstant.RESULT_CODE_NOT_INITIALIZED, "ServiceDisconnected")
            try {
                readerService?.unregisterListener(mInnerReaderListener)
            }catch (e:Exception){
                e.printStackTrace()
            }
            connect()
            readerService = null
            mContext = null
        }
    }

    /***
     * 断开连接
     */
    private fun connect() {
        if (mContext == null) {
            respSdkMsg(0, ReaderConstant.RESULT_CODE_NOT_INITIALIZED, "context not init")
            Timber.d("$TAG connect not exist ")
            return
        }

        try {
             mContext?.packageManager?.getPackageInfo("com.reader.service", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            respSdkMsg(0, ReaderConstant.RESULT_CODE_NOT_INITIALIZED, "service not install")
            return
        }


        if (readerService != null) {
            mContext?.unbindService(mConnection)
            Timber.d("$TAG connect unbindService ")
        }

        val intent = Intent()
        intent.action = "com.reader.service.action"
        intent.setPackage("com.reader.service")

        try {
            Timber.d("$TAG connect bindService ")
            mContext?.bindService(
                intent,
                mConnection,
                Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
            )
            respSdkMsg(0, ReaderConstant.RESULT_CODE_NOT_INITIALIZED, "unbindService")
        } catch (e: Throwable) {
            Timber.d("$TAG connect err ")
            e.printStackTrace()
            Timber.e(e)
        }
    }

    /***
     * 发送SDK消息
     */
    private fun respSdkMsg(func: Int, result: Int, msg: String) {
        val resp = SdkResponse(func, result, msg)
        val output = mContext?.let { resp.outPutData(it) }
        if(msg.isNotEmpty()){
            output?.put(ReaderConstant.KEY_RESULT_MSG,msg)
        }
        Timber.d("$TAG [sdk响应] func:$func result:$result $output ")
        if(output == null){
            //异常处理
            readerService?.registerListener(mInnerReaderListener)
            //通知client重新初始化
            val outputErr = Hashtable<String,String>()
            outputErr.put(ReaderConstant.KEY_MODULE,ReaderConstant.MODULE_SDK.toString())
            outputErr.put(ReaderConstant.KEY_RESULT_CODE,ReaderConstant.RESULT_CODE_NOT_INITIALIZED.toString())
            mReaderListener?.onRecvData(outputErr)
            return
        }
        mReaderListener?.onRecvData(output)
    }


    /***
     * nfc open
     */
    fun nfc_open():Int {
        try {
            val baseCommand =
                BaseCommand(ReaderConstant.MODULE_NFC, ReaderConstant.FUNCTION_NFC_OPEN)
            baseSendData(baseCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }

        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1

    }

    /***
     * nfc close
     */
    fun nfc_close():Int {
        try {
            val baseCommand =
                BaseCommand(ReaderConstant.MODULE_NFC, ReaderConstant.FUNCTION_NFC_CLOSE)
            baseSendData(baseCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    /***
     * nfc detect
     * @param timeout
     */
    fun nfc_detect(timeout: Int):Int {
        try {
            val baseCommand = NfcCommand(ReaderConstant.FUNCTION_NFC_DETECT)
            baseCommand.detectTime = timeout.toLong()
            Timber.d("$TAG 发[detect]:" + JSON.toJSONString(baseCommand))
            baseSendData(baseCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    /***
     * 获取MifareCard信息
     */
    @SuppressLint("CheckResult")
    fun nfc_pollOnMifareCard(timeout: Int):String{

            try {
                val baseCommand = NfcCommand(ReaderConstant.FUNCTION_NFC_CARD_INFO)
                baseCommand.detectTime = timeout.toLong()
                Timber.d("$TAG 发[get_card_info]:" + JSON.toJSONString(baseCommand))
                baseSendData(baseCommand)
            } catch (e: RemoteException) {
                connect()
                e.printStackTrace()
            }

//            Timber.d("$TAG [get_card_info] 等结果 1")
            //等结果
            if(waitForResult()){
                //判断下电，重新上电
                if(!nfc_state_open){
                    nfc_open()
                    return ""
                }
                return JSON.toJSONString(hashtableOutPut)
            }
            return ""
    }

    /***
     * nfc sendPdu
     */
    fun nfc_sendPdu(data: ByteArray):String {
        try {
            val baseCommand = NfcCommand(ReaderConstant.FUNCTION_NFC_APDU)
            baseCommand.payload = data
            baseSendData(baseCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_MSG) ?: ""
            }
        }
        return ""
    }

    //认证
    fun nfc_authentication(block:Int, pswType:Int, psw:String):Int {
//        密码长度 N+2 4 6
//        认证块编号 01 1 10
//        密码类型 01 1 11
//        密码FF FF FF FF FF FF N 12
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_AUTHENTICATION)
            command.psw = psw.ifEmpty { "FF FF FF FF FF FF" }
            command.block = block
            command.pswType = pswType
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //读块
    fun nfc_readBlock(block: Int):String {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_READ_BLOCK)
            command.block = block
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_NFC_READ_BLOCK) ?: ""
            }
        }
        return ""
    }

    //写块
    fun nfc_writeBlock(block: Int, data: String):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_WRITE_BLOCK)
            command.block = block
            command.data = data
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //读值
    fun nfc_readValue(block: Int):String {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_READ_VALUE)
            command.block = block
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_NFC_READ_VALUE) ?: ""
            }
        }
        return ""
    }

    //写值
    fun nfc_writeValue(block: Int, value: Int):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_WRITE_VALUE)
            command.block = block
            command.value = value.toLong()
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //自增
    fun nfc_incrementValue(startBlock: Int, endBlock: Int, value: Int):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_INCREMENT_VALUE)
            command.startBlock = startBlock
            command.endBlock = endBlock
            command.value = value.toLong()
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //自减
    fun nfc_decrementValue(startBlock: Int, endBlock: Int, value: Int):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_DECREMENT_VALUE)
            command.startBlock = startBlock
            command.endBlock = endBlock
            command.value = value.toLong()
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //存储
    fun nfc_restore(block: Int):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_RESTORE)
            command.block = block
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    //移动
    fun nfc_transfer(block: Int):Int {
        try {
            val command = NfcCommand(ReaderConstant.FUNCTION_NFC_TRANSFER)
            command.block = block
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }
    /***
     * 升级单片机
     */
    private var lstIcUpdateData = ArrayList<ByteArray>()
    private var curIcUpdateIndex = 0 //当前升级包号
    fun device_upDateIc(file: File) {
        if (!file.exists()) {
            return
        }
        lstIcUpdateData.clear()
        val updateSize = 4096
        var totalLen = 0L
        var checkSum = 0
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(file)
            val totalBytes = file.readBytes()
            checkSum = CRC16.cal_crc16(totalBytes)
            totalLen = totalBytes.size.toLong()
            Timber.d("$TAG [芯片升级] 第一帧 checkSum:${checkSum} size:${totalBytes.size}")

            //拆分多个包
            var n = 0
            while (n < totalBytes.size){
                //Timber.d("[拆分包] n:$n updateSize:$updateSize totalBytes:${totalBytes.size}")
                if((n + updateSize) < totalBytes.size) {
                    lstIcUpdateData.add(totalBytes.copyOfRange(n, n+updateSize))
                    n += updateSize
                }else{
                    lstIcUpdateData.add(totalBytes.copyOfRange(n,totalBytes.size))
                    n = totalBytes.size
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //最后一定要关闭文件流
            try {
                fis!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_IC)
        val finalTotalNum = lstIcUpdateData.size.toLong()
        val finalTotalLen = totalLen
        val finalCheckSum = checkSum.toLong()

        //第一帧
        curIcUpdateIndex = 0;
        upDateCommand.firstPacket(finalTotalNum, finalTotalLen, finalCheckSum)
        Timber.d("$TAG [系统升级]:${JSON.toJSONString(upDateCommand)}")
        try {
            baseSendData(upDateCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }

        //TODO::建议增加流程监控，超时结束
    }

    /***
     * 升级系统
     */
    private var lstSystemUpdateData = ArrayList<ByteArray>()
    private var curSystemUpdateIndex = 0 //当前系统升级包号
    private var upDateSystemTime = 0L //系统更新时间
    private var updateSystemThread:UpdateSystemThread? = null //线程
    fun device_upDateSystem(file: File) {
        val updateSize = 5*10240//8192 //1024*10//4096 //
        var totalLen = 0L
        var checkSum = 0
        var fis: FileInputStream? = null

        if (!file.exists()) {
            respSdkMsg(ReaderConstant.FUNCTION_UPDATE_SYSTEM,ReaderConstant.RESULT_CODE_UNKNOWN,"file not exist")
            return
        }
        lstSystemUpdateData.clear()

        try {
            fis = FileInputStream(file)
            val totalBytes = file.readBytes()
            checkSum = CRC16.cal_crc16(totalBytes)
            totalLen = totalBytes.size.toLong()

            //拆分多个包
            var n = 0
            while (n < totalBytes.size){
                //Timber.d("[拆分包] n:$n updateSize:$updateSize totalBytes:${totalBytes.size}")
                if((n + updateSize) < totalBytes.size) {
                    lstSystemUpdateData.add(totalBytes.copyOfRange(n, n+updateSize))
                    n += updateSize
                }else{
                    lstSystemUpdateData.add(totalBytes.copyOfRange(n,totalBytes.size))
                    n = totalBytes.size
                }
            }
            Timber.d("$TAG [系统升级] 第一帧 checkSum:${checkSum} size:${totalBytes.size} num:${lstSystemUpdateData.size}")

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //最后一定要关闭文件流
            try {
                fis!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_SYSTEM)
        val finalTotalNum = lstSystemUpdateData.size.toLong()
        val finalTotalLen = totalLen
        val finalCheckSum = checkSum.toLong()

        //第一帧
        curSystemUpdateIndex = 0;
        upDateCommand.firstPacket(finalTotalNum, finalTotalLen, finalCheckSum)
        //Timber.d("$TAG [系统升级] 第一帧:${JSON.toJSONString(upDateCommand)}")
        try {
            baseSendData(upDateCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }

        if (updateSystemThread != null) {
            Timber.d( "$TAG upgradeThread01_" + updateSystemThread?.getId() + " end.");
            updateSystemThread?.interrupt();
            updateSystemThread = null;
        }

        //线程流控
        updateSystemThread = UpdateSystemThread()
        updateSystemThread?.start()

    }


    /***
     * 测试升级
     */
    private var lstTestUpdateData = ArrayList<ByteArray>()
    private var curTestUpdateIndex = 0 //当前升级包号
//    fun device_upDateTest(file: File) {
//        if (!file.exists()) {
//            return
//        }
//        lstTestUpdateData.clear()
//        val updateSize = 5*10240
//        var totalLen = 0L
//        var checkSum = 0
//        var fis: FileInputStream? = null
//        try {
//            fis = FileInputStream(file)
//            val totalBytes = file.readBytes()
//            checkSum = CRC16.cal_crc16(totalBytes)
//            totalLen = totalBytes.size.toLong()
//
//
//            //拆分多个包
//            var n = 0
//            while (n < totalBytes.size){
//                //Timber.d("[拆分包] n:$n updateSize:$updateSize totalBytes:${totalBytes.size}")
//                if((n + updateSize) < totalBytes.size) {
//                    lstTestUpdateData.add(totalBytes.copyOfRange(n, n+updateSize))
//                    n += updateSize
//                }else{
//                    lstTestUpdateData.add(totalBytes.copyOfRange(n,totalBytes.size))
//                    n = totalBytes.size
//                }
//            }
//
//            Timber.d("$TAG [测试升级] 第一帧 checkSum:${checkSum} size:${totalBytes.size} num:${lstTestUpdateData.size}")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            //最后一定要关闭文件流
//            try {
//                fis!!.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        val upDateCommand = BaseCommand(8,1)
//        upDateCommand.payload = lstTestUpdateData[curTestUpdateIndex]
//        try {
//            baseSendData(upDateCommand)
//        } catch (e: RemoteException) {
//            connect()
//            e.printStackTrace()
//        }
//
//    }
    /***
     * 系统重启
     */
    fun device_reboot() {
        try {
            val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_REBOOT)
            baseSendData(upDateCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun ledBlue(enable: Boolean) {
        try {
            val ledCommand = PeripheralCommand(ReaderConstant.FUNCTION_PERIPHERAL_LED)
            ledCommand.lightBlue = enable
            ledCommand.lightYellow = false
            ledCommand.lightGreen = false
            ledCommand.lightRed = false
            ledCommand.iniPayLoad()
            baseSendData(ledCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun ledYellow(enable: Boolean) {
        try {
            val ledCommand = PeripheralCommand(ReaderConstant.FUNCTION_PERIPHERAL_LED)
            ledCommand.lightBlue = false
            ledCommand.lightYellow = enable
            ledCommand.lightGreen = false
            ledCommand.lightRed = false
            ledCommand.iniPayLoad()
            baseSendData(ledCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun ledGreen(enable: Boolean) {
        try {
            val ledCommand = PeripheralCommand(ReaderConstant.FUNCTION_PERIPHERAL_LED)
            ledCommand.lightBlue = false
            ledCommand.lightYellow = false
            ledCommand.lightGreen = enable
            ledCommand.lightRed = false
            ledCommand.iniPayLoad()
            baseSendData(ledCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun ledRed(enable: Boolean) {
        try {
            val ledCommand = PeripheralCommand(ReaderConstant.FUNCTION_PERIPHERAL_LED)
            ledCommand.lightBlue = false
            ledCommand.lightYellow = false
            ledCommand.lightGreen = false
            ledCommand.lightRed = enable
            ledCommand.iniPayLoad()
            baseSendData(ledCommand)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun buzzer(playTime: Long) {
        try {
            val command = PeripheralCommand(ReaderConstant.FUNCTION_PERIPHERAL_BUZZER)
            command.playTime = playTime
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun info_versionSystem():String {
        try {
            val command =
                BaseCommand(ReaderConstant.MODULE_INFO, ReaderConstant.FUNCTION_INFO_SYSTEM)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_INFO_SYSTEM_VERSION) ?: ""
            }
            if(hashtableOutPut?.containsKey(ReaderConstant.KEY_RESULT_MSG) == true){
                return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_MSG) ?: ""
            }
        }
        return ""
    }

    fun info_versionIc():String {
        try {
            val command = BaseCommand(ReaderConstant.MODULE_INFO, ReaderConstant.FUNCTION_INFO_IC)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_INFO_IC_VERSION) ?: ""
            }
            if(hashtableOutPut?.containsKey(ReaderConstant.KEY_RESULT_MSG) == true){
                return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_MSG) ?: ""
            }
        }
        return ""
    }

    fun info_systemSn():String {
        try {
            val command = BaseCommand(ReaderConstant.MODULE_INFO, ReaderConstant.FUNCTION_INFO_SN)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            if(hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt() == ReaderConstant.RESULT_CODE_SUCCESS) {
                return hashtableOutPut?.get(ReaderConstant.KEY_INFO_SN) ?: ""
            }
            if(hashtableOutPut?.containsKey(ReaderConstant.KEY_RESULT_MSG) == true){
                return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_MSG) ?: ""
            }
        }
        return ""
    }

    fun attackType() {
        try {
            val command =
                BaseCommand(ReaderConstant.MODULE_ATTACK, ReaderConstant.FUNCTION_ATTACK_TYPE)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun attackSource() {
        try {
            val command =
                BaseCommand(ReaderConstant.MODULE_ATTACK, ReaderConstant.FUNCTION_ATTACK_SOURCE)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun attackReset() {
        try {
            val command =
                BaseCommand(ReaderConstant.MODULE_ATTACK, ReaderConstant.FUNCTION_ATTACK_RESET)
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamOpen(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_OPEN, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamClose(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_CLOSE, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamPowerOn(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_POWERON, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamPowerOff(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_POWEROFF, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamDetect(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_DETECT, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamSendApdu(data: ByteArray, slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_APDU, slot)
            command.apdu = data
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamAtr(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_ATR, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamProtect(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_PROTECT, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    fun psamFD(slot: Int = 0) {
        try {
            val command = PSAMCommand(ReaderConstant.FUNCTION_PSAM_FD, slot)
            command.loadingPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
    }

    private fun baseSendData(baseCommand: BaseCommand) {
        if(readerService == null){
            respSdkMsg(baseCommand.functionCode, ReaderConstant.RESULT_CODE_INITIALIZED, "readerService not found")
        }
        readerService?.sendData(JSON.toJSONString(baseCommand))
    }

    fun sdkVersion():String{
        return BuildConfig.VERSION_NAME
    }

    fun sdkServiceVersion():String{
        return readerService?.version()?:""
    }

    /***
    * 下载日志
    */
    private var downLoadLogData = ByteArray(0) //日志包
    private var downLoadLogIndex = 0 //当前下载日志包号
    private var downLoadLogNum = 0   //下载日志包总数
    private var downLoadLogTime = 0L //更新时间
//    private var dipossDownLoadLog:Disposable? = null //流控
    private var downLondLogThread:DownLondLogThread? = null
    fun downloadLog(allLog:Boolean = false) {
        //流程如下，第一包获取下载日志大小 ，后续包一帧一帧获取
        downLoadLogIndex = 0 //当前下载日志包号
        downLoadLogNum = 0   //下载日志包总数
        downLoadLogTime = System.currentTimeMillis() //更新时间

        //第一包
        val command = BaseCommand(ReaderConstant.MODULE_INFO,ReaderConstant.FUNCTION_LOG_DOWN)
//        0: 设备当前开机周期日志
//        1: 所有日志
        val flag = if(allLog) 1 else 0
        command.payload = HexUtils.intToLE4ByteArray(flag)
//        command.payload = ByteArray(4)
//        command.payload.writeInt32LE(flag.toLong())
        baseSendData(command)
        Timber.d("$TAG [下载日志] 请求第一帧 ")

        //结束线程
        if (downLondLogThread != null) {
            Timber.d( "$TAG downLondLogThread" + downLondLogThread?.getId() + " end.");
            downLondLogThread?.interrupt();
            downLondLogThread = null;
        }

        //线程流控
        downLondLogThread = DownLondLogThread()
        downLondLogThread?.start()

//        var sendTime = downLoadLogTime //发送时间
//        dipossDownLoadLog?.dispose()
//        dipossDownLoadLog = Flowable.interval(30,TimeUnit.MILLISECONDS)
//            .map {
//                //超时5秒退出
//                if(downLoadLogIndex > 0 && Math.abs(System.currentTimeMillis() - downLoadLogTime) > 5000){
//                    Timber.d("$TAG [下载日志] 超时5秒退出:$downLoadLogIndex")
//                    dipossDownLoadLog?.dispose()
//                }
//
//                if(sendTime != downLoadLogTime) {
//                    sendTime = downLoadLogTime
//                    //正常结束
//                    if (downLoadLogIndex > 0 && downLoadLogNum > 0 && (downLoadLogIndex+1) > downLoadLogNum) {
//                        Timber.d("$TAG [下载日志] 结束 index:$downLoadLogIndex num:$downLoadLogNum size:${downLoadLogData.size} 保存文件 /sdcard/posLog.zip")
//                        FileUtils.saveBytesToSD(downLoadLogData,"/sdcard/posLog.zip")
//                        //结束下载
//                        dipossDownLoadLog?.dispose()
//                        return@map
//                    }
//
//                    //发送下一包
//                    val nextCommand = BaseCommand(ReaderConstant.MODULE_INFO, ReaderConstant.FUNCTION_LOG_DOWN)
//                    nextCommand.payload = HexUtils.intToLE4ByteArray(downLoadLogIndex+1)
////                    nextCommand.payload = ByteArray(4)
////                    nextCommand.payload.writeInt32LE((downLoadLogIndex+1).toLong())
//                    baseSendData(nextCommand)
//                    downLoadLogIndex++
//                    Timber.d("$TAG [下载日志] 下一帧:$downLoadLogIndex num:$downLoadLogNum" )
//
//                }
//
//            }
//            .subscribe()
    }


    /**
     * 等待结果
     * @return true，有结果  false 没结果
     */
    private fun waitForResult(num:Int=5): Boolean {
        hashtableOutPut = null
        for (count in 0..num) {
            if (hashtableOutPut != null) {
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

    /***
     * 升级系统线程
     */
    private class UpdateSystemThread : Thread() {
        override fun run() {
            super.run()
            //增加流程监控，超时结束
            var sendTime = upDateSystemTime //发送时间
            while (!((curSystemUpdateIndex > 0 && Math.abs(System.currentTimeMillis() - upDateSystemTime) > 5000)
                || (curSystemUpdateIndex >= lstSystemUpdateData.size))){

                    if(sendTime != upDateSystemTime) {
                        sendTime = upDateSystemTime
                        //继续下一个包
                        val upDateCommand = UpDateCommand(ReaderConstant.FUNCTION_UPDATE_SYSTEM)
                        upDateCommand.nextPacket(
                            curSystemUpdateIndex,
                            lstSystemUpdateData[curSystemUpdateIndex]
                        )

                        baseSendData(upDateCommand)

                        Timber.d("$TAG [系统升级] 下一帧 index:${curSystemUpdateIndex} size:${lstSystemUpdateData[curSystemUpdateIndex].size}")
                        curSystemUpdateIndex++
                    }
                }

        }
    }

    /***
     * 下载日志线程
     */
    private class DownLondLogThread : Thread(){
        override fun run() {
            super.run()
            var sendTime = downLoadLogTime //发送时间
            //超时5秒退出
            while (true){
                if(downLoadLogIndex > 0 && Math.abs(System.currentTimeMillis() - downLoadLogTime) > 5000){
                    break
                }

                if(sendTime != downLoadLogTime) {
                    sendTime = downLoadLogTime
                    //正常结束
                    if (downLoadLogIndex > 0 && downLoadLogNum > 0 && (downLoadLogIndex+1) > downLoadLogNum) {
                        Timber.d("$TAG [下载日志] 结束 index:$downLoadLogIndex num:$downLoadLogNum size:${downLoadLogData.size} 保存文件 /sdcard/posLog.zip")
                        FileUtils.saveBytesToSD(downLoadLogData,"/sdcard/posLog.zip")
                        //结束下载
                        break
                    }

                    //发送下一包
                    val nextCommand = BaseCommand(ReaderConstant.MODULE_INFO, ReaderConstant.FUNCTION_LOG_DOWN)
                    nextCommand.payload = HexUtils.intToLE4ByteArray(downLoadLogIndex+1)
                    baseSendData(nextCommand)
                    downLoadLogIndex++
                    Timber.d("$TAG [下载日志] 下一帧:$downLoadLogIndex num:$downLoadLogNum" )

                }
            }
        }
    }


    /***
     * emv初始化方法
     */
    fun emv_init():Int{
        try {
            val command = EMVCommand(ReaderConstant.FUNCTION_EMV_INIT)
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    /***
     * emv 寻卡
     */
    fun emv_detect(timeout: Int):Int{
        try {
            val command = EMVCommand(ReaderConstant.FUNCTION_EMV_DETECT_CARD)
            command.detectTime = timeout.toLong()
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    /***
     * emv 开始交易
     */
    fun emv_startTransaction(amount: Long):Int {
        try {
            val command = EMVCommand(ReaderConstant.FUNCTION_EMV_TRANSACTION)
            command.nowTime = DateTime().toString("yyyy-MM-dd HH:mm:ss")
            command.amount = amount
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult(10)){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }

    /***
     * emv 结束寻卡
     */
    fun emv_powoff():Int {
        try {
            val command = EMVCommand(ReaderConstant.FUNCTION_EMV_CLOSE)
            command.iniPayLoad()
            baseSendData(command)
        } catch (e: RemoteException) {
            connect()
            e.printStackTrace()
        }
        //等结果
        if(waitForResult()){
            return hashtableOutPut?.get(ReaderConstant.KEY_RESULT_CODE)?.toInt()?:-1
        }
        return -1
    }


}

