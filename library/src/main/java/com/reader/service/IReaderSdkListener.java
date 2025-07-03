package com.reader.service;

import java.util.Hashtable;

/**
 * Created by ljj on 2025/5/23
 * 对外输出输出对象
 */
public interface IReaderSdkListener {
    void onRecvData(Hashtable<String,String> data);
}

