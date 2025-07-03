package com.reader.service;
/**
 * Created by ljj on 2018/12/12
 * 串口通信(输入接口）
 */
public interface SerialData {
    void send(BaseCommand data);//发送数据
    void setListener(SerialDataListener listener);//设置回调
    void start();//运行线程
    void stop();//停止线程
    boolean can();//是否可以发射数据
}
