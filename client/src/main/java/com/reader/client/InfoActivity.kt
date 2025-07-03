package com.reader.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.reader.service.IReaderSdkListener
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import kotlinx.android.synthetic.main.activity_info.tv_msg
import timber.log.Timber

class InfoActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来自aar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        //返回结果
        pos.register(applicationContext, IReaderSdkListener { data ->
            runOnUiThread {
                //服务主动返回消息
                if(data.containsKey(ReaderConstant.KEY_RESULT_MSG)){
                    tv_msg.text = "result:${data[ReaderConstant.KEY_RESULT_MSG]}"
                }else {
                    tv_msg.text = "$data"
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }

    fun versionSystem(view: View) {
        val str = pos.info_versionSystem()
        if(str.isNotEmpty()) {
            tv_msg.text = "sysVer: ${str}"
        }
    }

    fun versionIc(view: View) {
        val str = pos.info_versionIc()
        if(str.isNotEmpty()) {
            tv_msg.text = "mcuVer: ${str}"
        }
    }

    fun versionSdkService(view: View){
        val str = pos.sdkServiceVersion()
        if(str.isNotEmpty()) {
            tv_msg.text = "readerServiceVer:${pos.sdkServiceVersion()}"
        }
    }

    fun systemSn(view: View) {
        val str = pos.info_systemSn()
        if(str.isNotEmpty()) {
            tv_msg.text = "sn: ${str}"
        }
    }

    fun back(view: View){
        finish()
    }
}