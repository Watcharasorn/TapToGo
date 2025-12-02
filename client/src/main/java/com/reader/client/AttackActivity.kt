package com.reader.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.reader.service.BaseResponse
import com.reader.service.IReaderSdkListener
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import kotlinx.android.synthetic.main.activity_led_test.*
import timber.log.Timber

class LedActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来自aar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led_test)

        //返回结果
        pos.register(applicationContext, IReaderSdkListener { data ->
            runOnUiThread {
//                //服务主动返回消息
//                if(data.containsKey(ReaderConstant.KEY_RESULT_MSG)){
//                    tv_msg.text = "result:${data[ReaderConstant.KEY_RESULT_MSG]}"
//                }else {
//                    tv_msg.text = "$data"
//                }
                Timber.d("[led]:$data")
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }

    fun ledOpen(view: View) {
        val resultCode = when(spinner_led.selectedItemPosition){
            1->{pos.peripheral_ledGreen()}
            2->{pos.peripheral_ledYellow()}
            else->pos.peripheral_ledRed()
        }
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            Toast.makeText(this, getString(R.string.common_success), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, showResult(resultCode), Toast.LENGTH_SHORT).show()
        }
    }
    fun ledClose(view: View) {
        val resultCode = pos.peripheral_ledClose()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            Toast.makeText(this, getString(R.string.common_success), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, showResult(resultCode), Toast.LENGTH_SHORT).show()
        }
    }


    fun buzzer(view: View) {
        val resultCode = pos.peripheral_buzzer(2000)
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            Toast.makeText(this, getString(R.string.common_success), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, showResult(resultCode), Toast.LENGTH_SHORT).show()
        }
    }

    //显示结果
    private fun showResult(resultCode: Int): String {
        return if(resultCode == 0) "[pass]" else "[fail]:$resultCode ${BaseResponse().changeMsg(resultCode,this)}"
    }

    fun back(view: View) {
        finish()
    }
}