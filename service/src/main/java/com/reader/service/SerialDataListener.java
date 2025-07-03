package com.reader.service;
/**
 * Created by ljj on 2018/12/12
 * 串口通信(输出接口）
 */
public interface SerialDataListener {
    void onReadSerialData(BaseResponse data);
}

