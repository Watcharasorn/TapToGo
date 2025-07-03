package com.reader.service


import android.content.Context
//import com.swallowsonny.convertextlibrary.*
import java.util.*
import com.reader.library.R
import com.reader.service.utils.HexUtils
import org.joda.time.DateTime
import java.nio.charset.Charset

/***
 * 接收部分协议
 */
open class BaseResponse{
    var total = 0 //除帧头和帧尾之外的数据长度

    @JvmField
    var moduleCode = 0 // 模块码

    @JvmField
    var functionCode = 0 //功能码 收

    @JvmField
    var resultCode = 0 //执行结果 收

    @JvmField
    var payload = ByteArray(0) // 数据载荷

    //API返回结构
    fun outPutData(mContext: Context):Hashtable<String,String>{
        val hashtable = Hashtable<String,String>()
        hashtable.put(ReaderConstant.KEY_MODULE,moduleCode.toString())
        hashtable.put(ReaderConstant.KEY_COMMAND,functionCode.toString())
        hashtable.put(ReaderConstant.KEY_RESULT_CODE,resultCode.toString())
        hashtable.put(ReaderConstant.KEY_RESULT_MSG,changeMsg(resultCode,mContext))
        return hashtable
    }

    //API 翻译错误码内容
    fun changeMsg(resultCode: Int, mContext: Context):String{
        return when(resultCode){
            ReaderConstant.RESULT_CODE_SUCCESS-> mContext.resources.getString(R.string.RESULT_CODE_SUCCESS) //"操作成功" //mContext.resources.getString(R.string.) //
            ReaderConstant.RESULT_CODE_NOT_INITIALIZED-> mContext.resources.getString(R.string.RESULT_CODE_NOT_INITIALIZED) //"未初始化"
            ReaderConstant.RESULT_CODE_INITIALIZED->mContext.resources.getString(R.string.RESULT_CODE_NOT_INITIALIZED) //"已初始化"
            ReaderConstant.RESULT_CODE_TIMEOUT->mContext.resources.getString(R.string.RESULT_CODE_TIMEOUT) //"超时"
            ReaderConstant.RESULT_CODE_DEVICE_NOT_FOUND->mContext.resources.getString(R.string.RESULT_CODE_DEVICE_NOT_FOUND) //"设备未找到"
            ReaderConstant.RESULT_CODE_INVALID_PARAMETER->mContext.resources.getString(R.string.RESULT_CODE_INVALID_PARAMETER) //"非法参数"
            ReaderConstant.RESULT_CODE_NOT_ENOUGH_CACHE->mContext.resources.getString(R.string.RESULT_CODE_NOT_ENOUGH_CACHE) //"缓存不足"
            ReaderConstant.RESULT_CODE_NOT_SUPPORTED->mContext.resources.getString(R.string.RESULT_CODE_NOT_SUPPORTED) //"暂时未支持"
            ReaderConstant.RESULT_CODE_UNKNOWN->mContext.resources.getString(R.string.RESULT_CODE_UNKNOWN) //"未知错误"
            ReaderConstant.RESULT_CODE_PERMISSION_DENIED->mContext.resources.getString(R.string.RESULT_CODE_PERMISSION_DENIED) //"权限不足"
            ReaderConstant.RESULT_CODE_TRANSPORT_FAILED->mContext.resources.getString(R.string.RESULT_CODE_TRANSPORT_FAILED) //"传输失败"
            else -> mContext.resources.getString(R.string.RESULT_CODE_NOT_DEFINED) //"未定义错误"
        }
    }
}

/***
 * 发送部分协议
 */
open class BaseCommand(module:Int,function:Int) {

    @JvmField
    var moduleCode = module// 模块码


    @JvmField
    var functionCode = function //功能码 收


    @JvmField
    var payload = ByteArray(0) // 数据载荷

}


/***
 * NFC 对象
 */
class NfcCommand(function:Int) :BaseCommand(ReaderConstant.MODULE_NFC,function){
    var detectTime = 0L //检测时间
        set(value) {
            field = value
            iniPayLoad()
        }
//        认证块编号 01 1 10
//        密码类型 01 1 11
//        密码FF FF FF FF FF FF N 12
    var block = 0 //认证块编号
    var pswType = 0 //密码类型
    var psw = "" //密码内容
    var data = "" //写内容
    var value = 0L //写value
    var startBlock = 0 //开始块
    var endBlock = 0 //结束块

    //加载检测时间
    fun iniPayLoad() {
        //有两个指令需要 超时时间
        if(functionCode == ReaderConstant.FUNCTION_NFC_DETECT || functionCode == ReaderConstant.FUNCTION_NFC_CARD_INFO) {
            payload = HexUtils.intToLE4ByteArray(detectTime.toInt())
        }
        //加载密钥
        else if(functionCode == ReaderConstant.FUNCTION_NFC_AUTHENTICATION){
            val bPsw = HexUtils.hexStringToBytes(this.psw)
            val bPswLen = HexUtils.intToLE4ByteArray(bPsw.size+2)
            val baBlock = HexUtils.intToLEBytes(block)
            val baPswType = HexUtils.intToLEBytes(pswType)
            payload = bPswLen+baBlock+baPswType+bPsw
        }
        //读块 读值 restore transfer
        else if(functionCode == ReaderConstant.FUNCTION_NFC_READ_BLOCK
            || functionCode == ReaderConstant.FUNCTION_NFC_READ_VALUE
            || functionCode == ReaderConstant.FUNCTION_NFC_RESTORE
            || functionCode == ReaderConstant.FUNCTION_NFC_TRANSFER
        ){
            payload = HexUtils.intToLEBytes(block)
        }
        //写块
        else if(functionCode == ReaderConstant.FUNCTION_NFC_WRITE_BLOCK){
            val bData = HexUtils.hexStringToBytes(this.data)
            val bDataLen = HexUtils.intToLE4ByteArray(bData.size+1)
            val ba = HexUtils.intToLEBytes(block)
            payload = bDataLen+ba+bData
        }
        //写value
        else if(functionCode == ReaderConstant.FUNCTION_NFC_WRITE_VALUE){
            val bBlock = HexUtils.intToLEBytes(block)
            val bValue = HexUtils.intToLE4ByteArray(value.toInt())
            payload = bBlock+bValue
        }
        //increment OR DECREMENT value
        else if(functionCode == ReaderConstant.FUNCTION_NFC_INCREMENT_VALUE || functionCode == ReaderConstant.FUNCTION_NFC_DECREMENT_VALUE){
            val bStartBlock = HexUtils.intToLEBytes(startBlock)
            val bEndBlock = HexUtils.intToLEBytes(endBlock)
            val bValue = HexUtils.intToLE4ByteArray(value.toInt()).reversedArray()
            payload = bStartBlock+bEndBlock+bValue
        }

    }
}

///***
// * NFC 返回对象
// */
//class NfcResponse:BaseResponse(){
////    nfc 类型  00 未知类型 01 CARDTYPE_ISO14443A 02 CARDTYPE_ISO14443B  04 CARDTYPE_FELICA
//    var nfcType = 0
//
//    init {
//        loadPayLoad()
//    }
//    //加载数据为对象
//    private fun loadPayLoad(){
//        if(functionCode == ReaderConstant.FUNCTION_NFC_DETECT) {
//            nfcType = payload.readInt32LE()
//        }
//    }
//
//
//}

/***
 * 升级 对象
 */
class UpDateCommand(function:Int) :BaseCommand(ReaderConstant.MODULE_UPDATE,function){
//    总的包数升级包分发总包数 4 6
//    总的字节长度升级包总的字节数 4 10
//    CRC检验码升级包总的校验码 4 14
    var totalNum = 0L
    var totalLen = 0L
    var checksum = 0L

    //第一帧
    fun firstPacket(totalNum:Long,totalLen:Long,checksum: Long){
        this.totalLen = totalLen
        this.totalNum = totalNum
        this.checksum = checksum
//        payload = ByteArray(12)
//        payload.writeInt32LE(totalNum,0)
//        payload.writeInt32LE(totalLen,4)
//        payload.writeInt32LE(checksum,8)
        val bTotalNum = HexUtils.intToLE4ByteArray(totalNum.toInt())
        val bTotalLen = HexUtils.intToLE4ByteArray(totalLen.toInt())
        val bCheckSum = HexUtils.intToLE4ByteArray(checksum.toInt())
        payload = bTotalNum+bTotalLen+bCheckSum
    }

    private var index = 0L
    //后续帧
    fun nextPacket(index:Int,data:ByteArray){
        this.index = index.toLong()+1
//        val ba = ByteArray(4)
//        ba.writeInt32LE(this.index)
        val ba = HexUtils.intToLE4ByteArray(this.index.toInt())
        payload = ba+data

    }
}

/***
 * 外设
 */
class PeripheralCommand(function: Int):BaseCommand(ReaderConstant.MODULE_PERIPHERAL,function){
//    01 01 01 01
//    灯的顺序为 蓝、黄、绿、红
//    01表示亮，00表示灭
    var lightBlue = false
    var lightYellow = false
    var lightGreen = false
    var lightRed = false

    fun iniPayLoad() {
//        val lightData = ByteArray(4)
//        lightData.writeInt8(if(lightBlue) 0x01 else 0x00,0)
//        lightData.writeInt8(if(lightYellow) 0x01 else 0x00,1)
//        lightData.writeInt8(if(lightGreen) 0x01 else 0x00,2)
//        lightData.writeInt8(if(lightRed) 0x01 else 0x00,3)
//        payload = lightData
        val bBlue = HexUtils.intToLEBytes(if(lightBlue) 0x01 else 0x00)
        val bYellow = HexUtils.intToLEBytes(if(lightYellow) 0x01 else 0x00)
        val bGreen = HexUtils.intToLEBytes(if(lightGreen) 0x01 else 0x00)
        val bRed = HexUtils.intToLEBytes(if(lightRed) 0x01 else 0x00)
        payload = bBlue+bYellow+bGreen+bRed
    }

    var playTime = 0L //蜂鸣时间 单位毫秒
        set(value) {
            field = value
            iniBeePayLoad()
        }

    private fun iniBeePayLoad(){
        if(functionCode == ReaderConstant.FUNCTION_PERIPHERAL_BUZZER) {
//            payload = ByteArray(4)
//            payload.writeInt32BE(playTime)
            payload = HexUtils.intToLE4ByteArray(playTime.toInt())
        }
    }
}

///***
// * 系统信息返回
// */
//class InfoResponse:BaseResponse(){
//    var version_system = ""
//    var version_ic = ""
//    var sn = ""
//
//    //加载数据为对象
//    fun loadPayLoad(){
//        if(moduleCode == ReaderConstant.MODULE_INFO) {
//            when(functionCode){
//                ReaderConstant.FUNCTION_INFO_SYSTEM->{
////                    Timber.d("[system] ${payload.readStringBE(0,payload.size)}")
//                    version_system = payload.readStringBE(0,payload.size, "ascii")
//                }
//                ReaderConstant.FUNCTION_INFO_SYSTEM->{
////                    Timber.d("[ic] ${payload.readStringBE(0,payload.size)}")
//                    version_system = payload.readStringBE(0,payload.size, "ascii")
//                }
//                ReaderConstant.FUNCTION_INFO_SN->{
////                    Timber.d("[sn] ${payload.readStringBE(0,payload.size)}")
//                    sn = payload.readStringBE(0,payload.size, "ascii")
//                }
//            }
//        }
//    }
//}

/***
 * 金融卡
 */
class PSAMCommand(function: Int,slot:Int = 0):BaseCommand(ReaderConstant.MODULE_PSAM,function){
    var slot = 0 //卡槽
    var detectTime = 0L //检测时间
    var apdu = ByteArray(0) //发送命令
    init {
        this.slot = slot
    }
    //加载检测时间
    fun loadingPayLoad() {
        if(functionCode == ReaderConstant.FUNCTION_PSAM_DETECT) {
//            payload = ByteArray(5)
//            payload.writeInt8(slot,0)
//            payload.writeInt32LE(detectTime,1)
            val bSlot = HexUtils.intToLEBytes(slot)
            val bDetectTime = HexUtils.intToLE4ByteArray(detectTime.toInt())
            payload = bSlot+bDetectTime
        }else if(functionCode == ReaderConstant.FUNCTION_PSAM_APDU) {
//            val ba = ByteArray(1)
//            ba.writeInt8(slot,0)
            val ba = HexUtils.intToLEBytes(slot)
            payload = ba+ apdu
        }
        else{
//            payload = ByteArray(1)
//            payload.writeInt8(slot,0)
            payload = HexUtils.intToLEBytes(slot)
        }
    }
}

/***
 * sdk的结果
 */
class SdkResponse(func: Int,result:Int,msg:String):BaseResponse(){
    init {
        this.moduleCode = ReaderConstant.MODULE_SDK
        this.resultCode = result
        this.functionCode = func
        this.payload = msg.toByteArray()
    }
}

/***
 * EMV卡命令
 */
class EMVCommand(function: Int):BaseCommand(ReaderConstant.MODULE_EMV,function){
//    var path = "" //路径
//    var isDebugOn = 0 //是否调试
    var detectTime = 0L //检测时间
    var nowTime = DateTime().toString("yyyy-MM-dd HH:mm:ss") //当前时间 格式：YYYY-MM-dd HH:mm:ss
    var transactionType = 1 // 交易类型 0：接触式 1：非接触式
    var amount = 0L //金额
    //加载检测时间
    fun iniPayLoad() {

        //01
        if(functionCode == ReaderConstant.FUNCTION_EMV_INIT) {
//            payload = HexUtils.hexStringToBytes(this.path)
        }
        //02
        else if(functionCode == ReaderConstant.FUNCTION_EMV_DETECT_CARD){
            payload = HexUtils.intToLE4ByteArray(detectTime.toInt())
        }
        //03
        else if(functionCode == ReaderConstant.FUNCTION_EMV_CLOSE){

        }
        //04
        else if(functionCode == ReaderConstant.FUNCTION_EMV_TRANSACTION){
//            交易类型 0：接触式 1：非接触式 4 6
//            交易金额 单位为分 8 10
//            当前时间 20 18
            val baType = HexUtils.intToLE4ByteArray(transactionType)
            val baAmount = HexUtils.intToLE8ByteArray(amount)
//            Timber.d("====== nowTime:${nowTime} amount:$amount")
            val baNow = nowTime.toByteArray(Charset.forName("ASCII"))
            val ba = if(baNow.size < 20)ByteArray(20-baNow.size) else ByteArray(0)
            payload = baType+baAmount+baNow+ba
        }
    }
}