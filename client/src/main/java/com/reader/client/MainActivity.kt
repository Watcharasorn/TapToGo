package com.reader.client

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.reader.service.ReaderAIDL
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来转aar
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        // app locale 默认简体中文
//        val configuration = baseContext.resources.configuration
//        configuration.setLocale(Locale.ENGLISH)
//        resources.updateConfiguration(configuration,resources.displayMetrics)

        //获取库版本号
        tv_version.text = "sdkVer:${pos.sdkVersion()}"

        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }

    private fun initView() {
        bt_tab_nfc.setOnClickListener {
            startActivity(Intent(this, NfcActivity::class.java))
        }

        bt_tab_update.setOnClickListener {
            startActivity(Intent(this,UpdateActivity::class.java))
        }

        bt_tab_info.setOnClickListener {
            startActivity(Intent(this,InfoActivity::class.java))
        }
        bt_tab_emv.setOnClickListener {
            startActivity(Intent(this,EmvActivity::class.java))
        }

        bt_close.setOnClickListener {
            finish()
        }
    }

}

