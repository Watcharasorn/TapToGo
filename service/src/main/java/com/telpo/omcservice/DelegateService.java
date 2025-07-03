package com.telpo.omcservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import com.reader.service.ReaderService;

import java.util.List;

/***
 * 此服务类用于 接收omc的指令并进行处理，同时守护app
 * 需引入 IDelegate.aidl,文件夹位置不要变
 */
public class DelegateService extends Service {

    private final IBinder binder = new IDelegate.Stub() {
        @Override
        public void enableAdb(boolean enabled) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCpuTemperature() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getFirmwareVersion() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDeviceModel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getDeviceSerial() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isNavBarEnabled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isStatusBarEnabled() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void installApk(String path) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void installFirmware(String path, String version) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void reboot() {
            //((PowerManager) getSystemService(Context.POWER_SERVICE)).reboot(null);
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void resetPassword() {
            // TODO: 重置密码
        }

        @Override
        public void setApkWhitelist(List<String> whitelist) {
            // TODO: 设置APK安装白名单
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCurrentTime(String time) {
            // TODO: 设置当前系统时间
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBootAlarm(long timestamp) {
            // TODO: 设置定时开机
            throw new UnsupportedOperationException();
        }

        @Override
        public void setServerAddress(String address) {
            // TODO: 设置服务器地址
            throw new UnsupportedOperationException();
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        // 注册系统UI广播，用于处理状态栏、导航栏显示/隐藏
        // 如果系统已处理该广播，这里则不用注册
        //registerReceiver(receiver, new IntentFilter(ACTION_SYSTEM_UI));
	    //这里omc守护app
        System.out.println("omcService restart readerService");
        //if (AppManager.getInstance().currentActivity() == null) {
            //startActivity(getPackageManager().getLaunchIntentForPackage(getPackageName()));
        //}
        startService(new Intent(this, ReaderService.class));
        // omc守护开始 携带当前服务的ComponentName和服务器地址启动OmcService
        try {
            startService(getOmcServiceIntent().putExtra("delegate",
                    new ComponentName(getPackageName(), getClass().getName()))
             );
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static Intent getOmcServiceIntent() {
        final String packageName = "com.telpo.omcservice";
        return new Intent().setComponent(new ComponentName(
                packageName, packageName + ".OmcService"));
    }

}
