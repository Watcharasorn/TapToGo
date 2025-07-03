package com.reader.client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.reader.service.BaseResponse
import com.reader.service.IReaderSdkListener
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import kotlinx.android.synthetic.main.activity_emv.*
import kotlinx.android.synthetic.main.activity_emv.et_value
import kotlinx.android.synthetic.main.activity_emv.tv_msg
import org.joda.time.DateTime

class EmvActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来自aar
    private val mShowMsg = StringBuilder()//显示内容
    private var mSucessNum = 0 //统计成功次数
    private var mFailNum = 0 //统计失败次数
    private var mCheckCardTime = 0L //统计耗时
    private val mHandler = Handler() //循环处理
    private var runnableDetect:Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emv)

        //返回结果
        pos.register(applicationContext, IReaderSdkListener { data ->
            runOnUiThread {
                //服务主动返回消息
                if(data.containsKey(ReaderConstant.KEY_RESULT_MSG)){
                    tv_msg.text = " ${data[ReaderConstant.KEY_RESULT_MSG]}"
                }else{
                    tv_msg.text = "$data"
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }

    //初始化
    fun emvInit(view: View) {
        val resultCode = pos.emv_init()
        mShowMsg.insert(0,"${now()}-[${baseContext.getString(R.string.emv_init)}] ${showResult(resultCode)}  ")
        showMsg()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            bt_emv_detect.isEnabled = true
            bt_emv_loop.isEnabled = true
        }
    }

    //寻卡
    fun emvDetect(view: View) {
        val resultCode = pos.emv_detect(500)
        mShowMsg.insert(0,"${now()}-[${baseContext.getString(R.string.emv_detect)}] ${showResult(resultCode)} ")
        showMsg()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            bt_emv_transaction.isEnabled = true
            bt_emv_close.isEnabled = true
        }
    }

    //交易
    fun emvTransaction(view: View){
        val amount = try {
            et_value.text.toString().toLong()
        }catch (e:Exception){
            e.printStackTrace()
            1L
        }
        val resultCode = pos.emv_startTransaction(amount)
        mShowMsg.insert(0,"${now()}-[${baseContext.getString(R.string.emv_transaction)}] ${showResult(resultCode)}  ")
        showMsg()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            bt_emv_transaction.isEnabled = false
        }
    }

    //下电
    fun emvClose(view: View) {
        val resultCode = pos.emv_powoff()
        mShowMsg.insert(0,"${now()}-[${baseContext.getString(R.string.emv_close)}] ${showResult(resultCode)} ")
        showMsg()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            bt_emv_transaction.isEnabled = false
            bt_emv_close.isEnabled = false
        }
    }

    //循环交易
    fun emvLoop(view: View) {
        //初始化
        if (runnableDetect == null) {
            runnableDetect = Runnable {
                if (bt_emv_detect.isEnabled) {
                    mCheckCardTime = System.currentTimeMillis()
                    val resultCode = pos.emv_detect(500)
                    if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
                        val result = pos.emv_startTransaction(1)
                        if(result == ReaderConstant.RESULT_CODE_SUCCESS){
                            mSucessNum++
                        }else{
                            mFailNum++
                        }
                    }else{
                        mFailNum++
                    }
                    //打印统计
                    mShowMsg.clear()
                    mShowMsg.insert(0,"[pass]:$mSucessNum [fail]:$mFailNum time:${System.currentTimeMillis()-mCheckCardTime} ms")
                    showMsg()
                    mHandler.postDelayed(runnableDetect, 300)
                }
            }
            //开始
            mFailNum = 0
            mSucessNum = 0
            mCheckCardTime = System.currentTimeMillis()
            mHandler.post(runnableDetect)
        }
        //改变字体
        if(bt_emv_loop.tag == null){
            bt_emv_loop.tag = 1
            bt_emv_loop.text = baseContext.getString(R.string.nfc_detect_no_stop_stop)
        }else{
            //停止
            mHandler.removeCallbacks(runnableDetect)
            runnableDetect = null
            bt_emv_loop.tag = null
            bt_emv_loop.text = baseContext.getString(R.string.emv_loop)
            //打印统计
            mShowMsg.clear()
            mShowMsg.insert(0,"[pass]:$mSucessNum [fail]:$mFailNum time:${System.currentTimeMillis()-mCheckCardTime} ms")
            showMsg()
            //下电
            pos.emv_powoff()
        }
    }

    fun back(view: View){
        finish()
    }

    //时间
    fun now():String{
        return DateTime().toLocalTime().toString()
    }

    //显示日志，包括历史
    private fun showMsg() {
        mShowMsg.insert(0,"\n")
        val str = mShowMsg.toString()
        //超过4000，删4000内容
        if(str.length > 4000){
            mShowMsg.clear()
            mShowMsg.append(str.substring(0,4000))
        }
        tv_msg.text = mShowMsg.toString()
    }

    //显示结果
    private fun showResult(resultCode: Int): String {
        return if(resultCode == 0) "[pass]" else "[fail]:$resultCode ${BaseResponse().changeMsg(resultCode,this)}"
    }
}