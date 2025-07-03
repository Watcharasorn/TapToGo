package com.reader.service


import com.reader.service.utils.CRC16
import com.swallowsonny.convertextlibrary.*
import timber.log.Timber

/***
 * 接收部分协议
 */
open class BaseResponse{
//    帧头0xE5F5 2 0
//    数据长度除帧头和帧尾之外的数据长度 2 2
//    模块码module_code 1 4
//    功能码function_code 1 5
//    执行结果result_code 2 6
//    响应数据不定长，个别功能的执行是有要上报响应的数据 N 8
//    校验码CRC16校验码校验范围 = 数据长度 + 模块码+功能码 + 执行结果+响应数据 2 8+N
//    帧尾0xE6F6 2 8+N+2
    var total = 0 //除帧头和帧尾之外的数据长度

    @JvmField
    var moduleCode = 0 // 模块码


    @JvmField
    var functionCode = 0 //功能码 收

    @JvmField
    var resultCode = 0 //执行结果 收

    @JvmField
    var payload = ByteArray(0) // 数据载荷

    //解析包头 byteArray
    @UseExperimental(ExperimentalStdlibApi::class)
    open fun ini(org: ByteArray, size: Int):Boolean{
        Timber.d("[收] size:$size len:${org.size} data:${org.readStringBE(0,org.size)}")
        //过滤1 长度不足
        if(org.size < 10){
            Timber.d("过滤1 response < 10")
            return false
        }

        //过滤2 包头
        if(org.readUInt8(0) != 0xE5 && org.readUInt8(1) != 0xF5){
            Timber.d("过滤2 fail 帧头E5F5 0:${org.readUInt8(0)} 1:${org.readUInt8(1)}")
            return false
        }

        //过滤3 可变包长度对应不上
        total = org.readInt16LE(2)
        if(total+4 > org.size){
            Timber.d("过滤3 数据长度:${total+4}超过包长:${org.size}")
            return false
        }

        //过滤4 尾帧对应不上
        if(org.readUInt8(4+total) != 0xE6 && org.readUInt8(4+total+1) != 0xF6){
            Timber.d("过滤4 fail 帧尾0xE6F6 0:${org.readUInt8(4+total)} 1:${org.readUInt8(5+total)}")
            return false
        }

        //过滤5 CRC不过
        val crc16 = CRC16.calculate(org.copyOfRange(2,total+2))
        val checksum = org.readUInt16LE(total+2)
        if(crc16 != checksum ){
            Timber.d("过滤5 fail CRC不通过 crc16:$crc16 checksum:$checksum")
            return false
        }
//    模块码 module_code 1 4
//    功能码function_code 1 5
//    执行结果result_code 2 6
//    响应数据不定长，个别功能的执行是有要上报响应的数据 N 8
//    校验码CRC16校验码校验范围 = 数据长度 + 模块码+功能码 + 执行结果+响应数据 2 8+N
        try {
            moduleCode = org.readInt8(4) //模块码
            functionCode = org.readInt8(5) //功能码
            resultCode = org.readInt16LE(6) //结果码

            //转成byte
            payload = org.copyOfRange(8, total+2)

            Timber.i("[解析包] 总长度:$total 模块:${moduleCode} 功能:$functionCode 结果:$resultCode payload:${if(payload.isNotEmpty()) payload.readStringBE(0,payload.size) else ""}")
            return true
        }catch (e:Exception){
            e.printStackTrace()
            Timber.i(" [解析包] e:${e.message}")
            return false
        }
    }
}

//帧头0xE3F3 2 0
//数据长度除帧头和帧尾之外的数据长度 2 2
//模块码 module_code 1 4
//命令码 command_code 1 5
//命令数据不定长 N 6
//校验码CRC16校验码校验范围 = 数据长度 + 模块码+命令码 +命令数据 2 6+N
//帧尾0xE4F4 2 6+N+2
class BaseCommand {

    @JvmField
    var moduleCode = 0// 模块码

    @JvmField
    var functionCode = 0 //功能码 收


    @JvmField
    var payload = ByteArray(0) // 数据载荷


    /***
     * 组织命令数据
     */
    fun CommondData():ByteArray{

        val startFlag = byteArrayOf(0xE3.toByte(), 0xF3.toByte())
        val len = ByteArray(2)
        len.writeInt16LE(payload.size+4)//长度
        val module = ByteArray(1)
        module.writeInt8(moduleCode)
        val command = ByteArray(1)
        command.writeInt8(functionCode)
        val crcData = len + module + command + payload

        val crc16 = CRC16.calculate(crcData)
//        Timber.d("crc:$crc16 crcData:${crcData.readStringBE(0,crcData.size)}")
        val checksum = ByteArray(2)
        checksum.writeInt16LE(crc16)
        val endFlag = byteArrayOf(0xE4.toByte(), 0xF4.toByte())

        return startFlag+ crcData + checksum + endFlag
    }
}

