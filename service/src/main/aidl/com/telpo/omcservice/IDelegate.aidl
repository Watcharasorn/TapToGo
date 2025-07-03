// IDelegate.aidl
package com.telpo.omcservice;

// Declare any non-default types here with import statements

import android.content.ComponentName;

interface IDelegate {
    //
    void enableAdb(boolean enabled);
    //
    String getCpuTemperature();
    //
    String getDeviceModel();
    //
    String getDeviceSerial();
    //
    String getFirmwareVersion();
    //
    boolean isNavBarEnabled();
    //
    boolean isStatusBarEnabled();
    //
    void installApk(String path);
    //
    void installFirmware(String path, String version);
    //
    void reboot();
    //
    void shutdown();
    //
    void resetPassword();
    //
    void setApkWhitelist(in List<String> whitelist);
    //
    void setCurrentTime(String time);
    //
    void setBootAlarm(long timestamp);
    //
    void setServerAddress(String address);
}
