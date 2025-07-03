package com.telpo.omcservice

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.reader.service.R
import android.content.Intent
import android.view.View
import com.reader.service.ReaderService
import com.reader.service.SerialRecvThread
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    val runnable = Runnable { finish() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        startService(Intent(this, ReaderService::class.java))
        //第一步：启动omc守护app
        startService(Intent(this, DelegateService::class.java))
        bt_start.postDelayed(runnable,5000)
//        Flowable.just(1).delay(5,TimeUnit.SECONDS)
//            .subscribe {
//                finish()
//            }

    }

//    override fun onResume() {
//        super.onResume()
//    }
//
//    fun start(view: View?) {
//        startService(Intent(this, ReaderService::class.java))
//        //第一步：启动omc守护app
//        startService(Intent(this, DelegateService::class.java))
//
//        tv_msg.text = "启动保护成功\n"
//    }
//
//    fun stop(view: View?) {
//        //第二步：app正常退出，不用omc守护 执行下方取消方法
//        try {
//            val packageName = "com.telpo.omcservice";
//            startService(
//                Intent().apply {
//                    component = ComponentName(
//                        packageName, "$packageName.OmcService"
//                    )
//                }.putExtra(
//                    "delegate",
//                    ComponentName("", "")
//                )
//            )
//            tv_msg.text = "退出保护成功\n"
////            tv_msg.setTextColor(Color.RED)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun send(view: View?){
//
//        SerialRecvThread.getInstance(baseContext)?.testSend()
//    }

    override fun onPause() {
        bt_start.removeCallbacks(runnable)
        super.onPause()
    }
}