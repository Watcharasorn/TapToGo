package com.reader.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.reader.service.BaseResponse
import com.reader.service.IReaderSdkListener
import com.reader.service.ReaderAIDL
import com.reader.service.ReaderConstant
import com.reader.service.utils.HexUtils
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.activity_nfc.*
import org.joda.time.DateTime

class NfcActivity : AppCompatActivity() {
    private val pos = ReaderAIDL //这个对象将来自aar
    private var state_open = false //打开状态 控制按钮
    private var state_detect = false //读卡状态 控制 APDU和authentication
    private var state_auth = false   //加载密钥状态 控制读写块
    private val mShowMsg = StringBuilder()//显示内容
    private val mStartBlock = 1  //操作块号
    private val mEndBlock = 2    //操作结束块号
    private var mSucessNum = 0L //统计成功次数
    private var mFailNum = 0L //统计失败次数
    private var mCheckCardTime = 0L //统计耗时
    private val mHandler = Handler() //循环处理
    private var runnableDetect:Runnable? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        //返回结果
        pos.register(applicationContext, IReaderSdkListener { data ->
            runOnUiThread{
                //服务主动返回消息
                if(data.containsKey(ReaderConstant.KEY_RESULT_MSG)){
                    mShowMsg.append("${now()}-result :${data[ReaderConstant.KEY_RESULT_MSG]} \n")
                }else {
                    tv_msg.text = "$data"
                }
                showMsg()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        pos.unRegister()
    }

    //打开设备
    @SuppressLint("CheckResult")
    fun nfcopen(view: View?) {
        val resultCode = pos.nfc_open()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            state_open = true
            nfc_check_btn.isEnabled = true
            nfc_close_btn.isEnabled = true
            buttonNfcAPDU.isEnabled = false
            authenticateBtn.isEnabled = false
        }
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_open)}]  ${showResult(resultCode)} \n")
        showMsg()
    }



    //关闭设备
    @SuppressLint("CheckResult")
    fun nfcclose(view: View?) {
        val resultCode = pos.nfc_close()
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            state_open = false
            nfc_close_btn.isEnabled = false
            nfc_check_btn.isEnabled = false
            buttonNfcAPDU.isEnabled = false
            authenticateBtn.isEnabled = false
            decBtn.isEnabled = false
            incBtn.isEnabled = false
            readValueBtn.isEnabled = false
            writeValueBtn.isEnabled = false
            readBlockBtn.isEnabled = false
            writeBlockBtn.isEnabled = false
            restoreBtn.isEnabled = false
            transferBtn.isEnabled = false
        }
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_close)}] ${showResult(resultCode)} \n")
        showMsg()

        //循环线程停止
        mHandler.removeCallbacks(runnableDetect)
        runnableDetect = null
        bt_nfc_detect_no_stop.tag = null
        bt_nfc_detect_no_stop.text = baseContext.getString(R.string.nfc_detect_no_stop)
    }

    //寻卡 读卡
    @SuppressLint("CheckResult")
    fun detect(view: View?) {
        val lastTime = System.currentTimeMillis()
        val resultCode = pos.nfc_pollOnMifareCard(500)
        if(resultCode.isNotEmpty()){
            try {
                val resp = JSON.parseObject(resultCode)
                //循环操作显示
                if(bt_nfc_detect_no_stop.tag != null){
                    if (resp.containsKey(ReaderConstant.KEY_RESULT_CODE) && resp.getIntValue(ReaderConstant.KEY_RESULT_CODE) == ReaderConstant.RESULT_CODE_SUCCESS) {
                        if(mSucessNum >= Long.MAX_VALUE) mSucessNum = 0
                        mSucessNum++
                    }else{
                        if(mFailNum >= Long.MAX_VALUE) mFailNum = 0
                        mFailNum++
                    }
                    mShowMsg.clear()
                    mShowMsg.append("[pass]:$mSucessNum [fail]:$mFailNum  time:${System.currentTimeMillis()-lastTime} ms")
                    showMsg()
                    return
                }
                //单次寻卡显示
                if (resp.containsKey(ReaderConstant.KEY_RESULT_CODE) &&
                    resp.getIntValue(ReaderConstant.KEY_RESULT_CODE) == ReaderConstant.RESULT_CODE_SUCCESS) {
                    //成功
                    mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_detect)}] [pass]:${resultCode}\n")
                    state_detect = true
                    buttonNfcAPDU.isEnabled = true
                    authenticateBtn.isEnabled = true
                    showMsg()
                    return
                }else if (resp.containsKey(ReaderConstant.KEY_RESULT_CODE) &&
                    resp.getIntValue(ReaderConstant.KEY_RESULT_CODE) != ReaderConstant.RESULT_CODE_SUCCESS){
                    //失败
                    val code = resp.getIntValue(ReaderConstant.KEY_RESULT_CODE)
                    val msg = resp.getString(ReaderConstant.KEY_RESULT_MSG)
                    mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_detect)}] [fail]:${code} $msg\n")
                }else{
                    //其他
                    mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_detect)}] [fail]:${resultCode}\n")
                }


            }catch (e:Exception){
                mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_detect)}] err:${resultCode}\n")
                e.printStackTrace()
            }
        }

        //默认
        state_detect = false
        buttonNfcAPDU.isEnabled = false
        authenticateBtn.isEnabled = false
        showMsg()

    }



    //连续寻卡
    fun detectNotStop(view: View?){
        //初始化
        if(runnableDetect == null) {
            runnableDetect = Runnable {
                if (!state_open) {
                    nfcopen(nfc_open_btn)
                } else {
                    detect(nfc_check_btn)
                }
                mHandler.postDelayed(runnableDetect, 300)
            }
            //开始
            mFailNum = 0
            mSucessNum = 0
            mCheckCardTime = System.currentTimeMillis()
            mHandler.post(runnableDetect)

        }

        if(bt_nfc_detect_no_stop.tag == null){
            bt_nfc_detect_no_stop.tag = 1
            bt_nfc_detect_no_stop.text = baseContext.getString(R.string.nfc_detect_no_stop_stop)
        }else{
            //停止
            mHandler.removeCallbacks(runnableDetect)
            runnableDetect = null
            bt_nfc_detect_no_stop.tag = null
            bt_nfc_detect_no_stop.text = baseContext.getString(R.string.nfc_detect_no_stop)
            //打印统计
            mShowMsg.clear()
            mShowMsg.append("  [pass]:$mSucessNum  [fail]:$mFailNum  time:${System.currentTimeMillis()-mCheckCardTime} ms \n")
            showMsg()
            nfcclose(nfc_close_btn)
        }
    }


    //发送apdu
    @SuppressLint("CheckResult")
    fun sendApdu(view: View?) {
        val ba = HexUtils.hexStringToBytes(editTextNfcAPDU.text.toString())
        val resultCode = pos.nfc_sendPdu(ba)
        if(resultCode.isNotEmpty()){
            mShowMsg.append("${now()}-[apdu]:[pass] ${resultCode}\n")
        }else{
            mShowMsg.append("${now()}-[apdu]:[fail]\n")
        }

        //认证读写块方式不生效
        authenticateBtn.isEnabled = false
        decBtn.isEnabled = false
        incBtn.isEnabled = false
        readValueBtn.isEnabled = false
        writeValueBtn.isEnabled = false
        readBlockBtn.isEnabled = false
        writeBlockBtn.isEnabled = false
        restoreBtn.isEnabled = false
        transferBtn.isEnabled = false
        showMsg()
    }

    //认证
    @SuppressLint("CheckResult")
    fun authentication(view: View) {
        val psw = "FFFFFFFFFFFF"//et_psw.text.toString()
        val resultCode = pos.nfc_authentication(mStartBlock,10,psw)
        if(resultCode == ReaderConstant.RESULT_CODE_SUCCESS){
            state_auth = true
            buttonNfcAPDU.isEnabled = false
            decBtn.isEnabled = true
            incBtn.isEnabled = true
            readValueBtn.isEnabled = true
            writeValueBtn.isEnabled = true
            readBlockBtn.isEnabled = true
            writeBlockBtn.isEnabled = true
            restoreBtn.isEnabled = true
            transferBtn.isEnabled = true
        }
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_authentication)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //读块
    fun readBlock(view: View) {
        val resultCode = pos.nfc_readBlock(mStartBlock)
        if(resultCode.isNotEmpty()){
            mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_readblock)}]:[pass] ${resultCode}\n")
        }else{
            mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_readblock)}]:[fail]\n")
        }
        showMsg()
    }

    //写块
    fun writeBlock(view: View) {
        val data = editTexWriteBlock.text.toString()
        val resultCode = pos.nfc_writeBlock(mStartBlock,data)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_writeblock)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //读值
    fun readValue(view: View) {
        val resultCode = pos.nfc_readValue(mEndBlock)
        if(resultCode.isNotEmpty()){
            mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_readvalue)}]:[pass]:${resultCode}\n")
        }else{
            mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_readvalue)}]:[fail]\n")
        }
        showMsg()
    }

    //写值
    fun writeValue(view: View) {
        var value = 1
        try {
            val ba = HexUtils.hexStringToBytes(editTexWriteValue.text.toString())
            value = HexUtils.hexToIntLE(ba,0,ba.size)
        }catch (e:Exception){
            e.printStackTrace()
        }

        val resultCode = pos.nfc_writeValue(mEndBlock,value)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_writevalue)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //自增
    fun incrementValue(view: View) {
        val value = try {
            1
        }catch (e:Exception){
            1
        }
        val resultCode = pos.nfc_incrementValue(mEndBlock,mEndBlock,value)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_incrementvalue)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //自减
    fun decrementValue(view: View) {
        val value = try {
            1
        }catch (e:Exception){
            1
        }
        val resultCode = pos.nfc_decrementValue(mEndBlock,mEndBlock,value)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_decrementvalue)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //恢复
    fun restore(view: View) {
        val resultCode = pos.nfc_restore(mStartBlock)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_cache_to_block)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //交易
    fun transfer(view: View) {
        val resultCode = pos.nfc_transfer(mStartBlock)
        mShowMsg.append("${now()}-[${baseContext.getString(R.string.nfc_block_to_cache)}]${showResult(resultCode)}\n")
        showMsg()
    }

    //返回界面
    fun back(view: View){
        finish()
    }

    //时间
    fun now():String{
        return DateTime().toLocalTime().toString()
    }

    //显示日志，包括历史
    private fun showMsg() {
        val str = mShowMsg.toString()
        //超过4000，删4000内容
        if(str.length > 4000){
            mShowMsg.clear()
            mShowMsg.append(str.substring(4000))
        }
        textview_uid.text = mShowMsg.toString()
    }

    //显示结果
    private fun showResult(resultCode: Int): String {
        return if(resultCode == 0) "[pass]" else "[fail]:$resultCode ${BaseResponse().changeMsg(resultCode,this)}"
    }

}