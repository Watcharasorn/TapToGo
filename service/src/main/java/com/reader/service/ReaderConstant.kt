package com.reader.service

object ReaderConstant {
    //
    const val MODULE_NFC = 0x01 //NFC
    const val MODULE_UPDATE = 0x02 //软件升级
    const val MODULE_PERIPHERAL = 0x03 //外设控制(灯、蜂鸣器灯)
    const val MODULE_INFO = 0x04 // 设备信息：SN、软件版本号等
    const val MODULE_TAMPER = 0x05 // 防拆
    const val MODULE_PSAM = 0x06 // PSAM
    const val MODULE_LOG = 0x07 //日志

    //
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

    //
    const val FUNCTION_NFC_OPEN = 0x01 //    0x01 nfc open
    const val FUNCTION_NFC_CLOSE = 0x02 //    0x02 nfc close
    const val FUNCTION_NFC_DETECT = 0x03 //    0x03 nfc detect
    const val FUNCTION_NFC_APDU = 0x04 //    0x04 nfc apdu

    //
    const val NFC_DETECT_CARD_TYPE_UNKNOWN = 0x00 //UNKNOWN
    const val NFC_DETECT_CARD_TYPE_ISO14443A = 0x01 // TYPEA
    const val NFC_DETECT_CARD_TYPE_ISO14443B = 0x02 // TYPEB
    const val NFC_DETECT_CARD_TYPE_FELICA = 0x04 // FELICA

    //UPDATE
    const val FUNCTION_UPDATE_IC = 0x01 //    0x01单片机升级
    const val FUNCTION_UPDATE_SYSTEM = 0x02 //    0x02系统升级
    const val FUNCTION_UPDATE_REBOOT = 0x03 //    0x03系统重启

    //PERIPHERAL
    const val FUNCTION_PERIPHERAL_LED = 0x01 // LED灯
    const val FUNCTION_PERIPHERAL_BUZZER = 0x02 // 蜂鸣器

    //INFO
    const val FUNCTION_INFO_SYSTEM = 0x01 //    0x01系统版本获取
    const val FUNCTION_INFO_IC = 0x02 //    0x02单片机APP版本获取
    const val FUNCTION_INFO_SN = 0x03 //    0x03系统sn获取
}