package com.telpo.omcservice

import android.app.Application
import com.reader.service.BuildConfig
import com.reader.service.utils.FileLogTree
import com.reader.service.utils.SDCardUtils
import timber.log.Timber

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //这里连接串口，有可能启动太早硬件没有准备好
        Timber.plant(
            FileLogTree()
                .storeAt(SDCardUtils.getExternalCacheDir("log", applicationContext))
        )
        Timber.d("onCreate service 版本:" + BuildConfig.VERSION_NAME)

    }
}