package com.reader.service

object ReaderConstant {
    //module
    const val MODULE_NFC = 0x01 //NFC
    const val MODULE_UPDATE = 0x02 //软件升级
    const val MODULE_PERIPHERAL = 0x03 //外设控制(灯、蜂鸣器灯)
    const val MODULE_INFO = 0x04 // 设备信息：SN、软件版本号 日志等
    const val MODULE_ATTACK = 0x05 // 防拆
    const val MODULE_PSAM = 0x06 // PSAM
    const val MODULE_EMV = 0x07 //EMV
    const val MODULE_SDK = 0x08 //SDK日志

    //code
    const val RESULT_CODE_SUCCESS = 0 //0操作成功
    const val RESULT_CODE_TIMEOUT = -1 //-1操作超时
    const val RESULT_CODE_INVALID_PARAMETER = -2 //-2参数非法
    const val RESULT_CODE_DEVICE_NOT_FOUND = -3 //-3设备未找到
    const val RESULT_CODE_NOT_INITIALIZED = -4 //-4设备或资源未初始化
    const val RESULT_CODE_INITIALIZED = -5 //-5设备或资源已经初始化
    const val RESULT_CODE_NOT_ENOUGH_CACHE = -6 //-6缓存不足
    const val RESULT_CODE_NOT_SUPPORTED = -7 //-7暂时未支持
    const val RESULT_CODE_UNKNOWN = -8 //-8未知错误
    const val RESULT_CODE_PERMISSION_DENIED = -9 //-9权限不足
    const val RESULT_CODE_TRANSPORT_FAILED = -10 //-10传输失败

    //command nfc 14
    const val FUNCTION_NFC_OPEN = 0x01 //    0x01 nfc open
    const val FUNCTION_NFC_CLOSE = 0x02 //    0x02 nfc close
    const val FUNCTION_NFC_DETECT = 0x03 //    0x03 nfc detect
    const val FUNCTION_NFC_APDU = 0x04 //    0x04 nfc apdu
    const val FUNCTION_NFC_AUTHENTICATION = 0x05 //    0x05 nfc authentication
    const val FUNCTION_NFC_READ_BLOCK = 0x06 //    0x06 nfc read block
    const val FUNCTION_NFC_WRITE_BLOCK = 0x07 //    0x07 nfc write block
    const val FUNCTION_NFC_READ_VALUE = 0x08 //    0x08 nfc read value
    const val FUNCTION_NFC_WRITE_VALUE = 0x09 //    0x09 nfc write value
    const val FUNCTION_NFC_INCREMENT_VALUE = 0x0A //    0x0a nfc increment value
    const val FUNCTION_NFC_DECREMENT_VALUE = 0x0B //    0x0b nfc Decrement value
    const val FUNCTION_NFC_CARD_INFO = 0x2D //    0x2d nfc get card info
    const val FUNCTION_NFC_RESTORE = 0x2E //    0x2e nfc restore
    const val FUNCTION_NFC_TRANSFER = 0x2F //    0x2F nfc transfer
//    //
//    const val NFC_DETECT_CARD_TYPE_UNKNOWN = 0x00 //UNKNOWN
//    const val NFC_DETECT_CARD_TYPE_ISO14443A = 0x01 // TYPEA
//    const val NFC_DETECT_CARD_TYPE_ISO14443B = 0x02 // TYPEB
//    const val NFC_DETECT_CARD_TYPE_FELICA = 0x04 // FELICA

    //UPDATE 3
    const val FUNCTION_UPDATE_IC = 0x01 //    0x01单片机升级
    const val FUNCTION_UPDATE_SYSTEM = 0x02 //    0x02系统升级
    const val FUNCTION_UPDATE_REBOOT = 0x03 //    0x03系统重启

    //PERIPHERAL 2
    const val FUNCTION_PERIPHERAL_LED = 0x01 // LED灯
    const val FUNCTION_PERIPHERAL_BUZZER = 0x02 // 蜂鸣器

    //INFO 4
    const val FUNCTION_INFO_SYSTEM = 0x01 //    0x01系统版本获取
    const val FUNCTION_INFO_IC = 0x02 //    0x02单片机APP版本获取
    const val FUNCTION_INFO_SN = 0x03 //    0x03系统sn获取
    const val FUNCTION_LOG_DOWN = 0x04  // 0x01 下载日志

    //ATTACK 3
    const val FUNCTION_ATTACK_TYPE = 0x01   //    0x01攻击类型获取
    const val FUNCTION_ATTACK_SOURCE = 0x02 //    0x02攻击源获取
    const val FUNCTION_ATTACK_RESET = 0x03  //    0x03防拆信息复位

    //PSAM 6
    const val FUNCTION_PSAM_OPEN = 0x01   //    0x01 Open
    const val FUNCTION_PSAM_CLOSE = 0x02   //    0x02 Close
    const val FUNCTION_PSAM_POWERON = 0x03   //    0x03 Power_on
    const val FUNCTION_PSAM_POWEROFF = 0x04   //    0x04 Power_off
    const val FUNCTION_PSAM_DETECT = 0x05   //    0x05 detect
    const val FUNCTION_PSAM_APDU = 0x06   //    0x06 APDU指令发送
    const val FUNCTION_PSAM_ATR = 0x07  //    0x07 获取ATR
    const val FUNCTION_PSAM_PROTECT = 0x08   //    0x08 获取 protect
    const val FUNCTION_PSAM_FD = 0x09   //    0x09 获取FD

    //EMV 5
    const val FUNCTION_EMV_INIT = 0x01   //    0x01emv init
    const val FUNCTION_EMV_DETECT_CARD = 0x02   // 0x02emv detect card
    const val FUNCTION_EMV_CLOSE = 0x03   //0x03emv close
    const val FUNCTION_EMV_TRANSACTION = 0x04   //0x04emv transaction

    //RESPON NAME
    const val KEY_MODULE = "module"     //模块名(公共）
    const val KEY_COMMAND = "command"   //命令名(公共）
    const val KEY_RESULT_CODE = "code"     //结果代码(公共）
    const val KEY_RESULT_MSG = "msg"       //结果消息(公共）
    const val KEY_INFO_SYSTEM_VERSION = "systemVersion"       //系统版本
    const val KEY_INFO_IC_VERSION = "icVersion"              //IC版本
    const val KEY_INFO_SN  = "sn"                            //sn
    const val KEY_NFC_TYPE = "nfc_type"
    const val KEY_NFC_CARD_TYPE = "nfc_card_type"
    const val KEY_NFC_CARD_ATQA = "nfc_card_atqa"
    const val KEY_NFC_CARD_SAK = "nfc_card_sak"
    const val KEY_NFC_CARD_UID_LENGTH = "nfc_card_uid_len"
    const val KEY_NFC_CARD_UID = "nfc_card_uid"
    const val KEY_NFC_CARD_ATQB_LENGTH = "nfc_card_atqb_len"
    const val KEY_NFC_CARD_ATQB = "nfc_card_atqb"
    const val KEY_NFC_CARD_PUPI = "nfc_card_pupi"
    const val KEY_NFC_READ_VALUE = "nfc_read_value"
    const val KEY_NFC_READ_BLOCK = "nfc_read_block"
}