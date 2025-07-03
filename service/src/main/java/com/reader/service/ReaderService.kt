package com.reader.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import android_serialport_api.SerialPort
import com.alibaba.fastjson.JSON
import com.reader.service.SerialRecvThread.Companion.getInstance
import com.reader.service.utils.FileLogTree
import com.reader.service.utils.SDCardUtils
import com.swallowsonny.convertextlibrary.*
import timber.log.Timber
import java.util.*

class ReaderService : Service() {
    private val TAG = "ReaderService"

    private var mIsServiceRunning = false
    //观察者模式，多个客户端订阅
    private val mCallbackList = RemoteCallbackList<IReaderListener>()
    private var mLock = Any()
    //串口方法
    var serialRecvThread: SerialData? = null

    //AIDL方法
    private val binder: IBinder = object : IReaderService.Stub() {
        override fun init(): Int {
            Timber.d("$TAG init ")
            return 1
        }

        override fun registerListener(callback: IReaderListener) {
            synchronized(mLock) {
//                val length = mCallbackList.beginBroadcast()
//                var isExist = false
//                for (i in 0 until length) {
//                    val callback1 = mCallbackList.getBroadcastItem(i)
//                    if (callback1.hashCode() == callback.hashCode()) {
//                        isExist = true
//                        continue
//                    } else {
//                        mCallbackList.unregister(callback1)
//                    }
//                }
//                if (!isExist) {
//                    Timber.d("$TAG register callback:${callback.hashCode()}")
//                    mCallbackList.register(callback)
//                }
//                mCallbackList.finishBroadcast()

                Timber.d("$TAG register callback:${callback.hashCode()}")
                mCallbackList.register(callback)

            }
        }

        override fun unregisterListener(callback: IReaderListener) {
            Timber.d("$TAG unregister callback:${callback.hashCode()}")
            mCallbackList.unregister(callback)
        }

        override fun sendData(data: String?) {
            try {
                //解析数据对象
                val reqCommand = JSON.parseObject(data, BaseCommand::class.java)

                //判断串口是否连接
                if (serialRecvThread == null || !serialRecvThread!!.can()) {
                    val resp = BaseResponse()
                    resp.moduleCode = reqCommand.moduleCode
                    resp.functionCode = reqCommand.functionCode
                    resp.resultCode = ReaderConstant.RESULT_CODE_DEVICE_NOT_FOUND
                    dispatchResult(resp)
                    Timber.d("$TAG serialRecvThread:${serialRecvThread != null} can:${serialRecvThread?.can()}")
                    return
                }

                //TODO::暂时不支持并发，转串口通信
                serialRecvThread?.send(reqCommand)
            }catch (e:Exception){
                e.printStackTrace()
                Timber.d("$TAG e:${e.message} snedData :${data}")
            }

        }

        override fun version(): String {
            return BuildConfig.VERSION_NAME
        }


    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(mIsServiceRunning){
            return START_STICKY
        }
        mIsServiceRunning = true
        Timber.d("onStartCommand service 版本:" + BuildConfig.VERSION_NAME)




        return super.onStartCommand(intent, flags, startId)
    }

    /***
     * 启动串口
     */
    private fun startSerial() {
        //创建串口对象
        serialRecvThread = getInstance(applicationContext)
        //回调结果
        serialRecvThread?.setListener { data: BaseResponse -> dispatchResult(data) }
        //启动
        if(serialRecvThread?.can() != true) {
            serialRecvThread?.start()
        }
    }

    //客户端绑定服务
    override fun onBind(intent: Intent): IBinder {
        Timber.d("onBind service 版本:" + BuildConfig.VERSION_NAME)
//        startSerial()
        return binder
    }

    override fun onCreate() {
        startSerial()
    }

    //主动上报卡号
    private fun dispatchResult(response: BaseResponse) {
        synchronized(mLock) {
            val length = mCallbackList.beginBroadcast()
            for (i in 0 until length) {
                val callback = mCallbackList.getBroadcastItem(i)
                try {
                    //Timber.d("ReaderService dispatchResult:${response.moduleCode}")
                    callback.onRecvData(JSON.toJSONString(response))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                break
            }
            mCallbackList.finishBroadcast()
        }
    }

}