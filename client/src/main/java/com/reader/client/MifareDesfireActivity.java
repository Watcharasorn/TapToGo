package com.telpo.tps550.api.demo.nfc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.common.demo.R;
import com.telpo.tps550.api.demo.bean.BaseActivity;

import static java.lang.Thread.sleep;


public class MifareDesfireActivity extends BaseActivity {

    public static final int CHECK_NFC = 1;
    public static final int GET_ATS = 2;
    public static final int ACTIVATE_NEW_CARD = 3;
    public static final int FREE_MEM = 4;
    public static final int FORMAT = 5;
    public static final int GET_VERSION = 6;
    public static final int GET_APP_IDS = 7;
    public static final int CREATE_APP = 8;
    public static final int DELETE_APP = 9;
    public static final int GET_FILE_IDS = 10;
    public static final int GET_FILE_SETTINGS = 11;
    public static final int CREATE_STD_DATA_FILE = 12;
    public static final int DELETE_FILE = 13;
    public static final int WRITE_DATA = 14;
    public static final int READ_DATA = 15;
    public static final int GET_KEY_SETTINGS = 16;
    public static final int GET_KEY_VERSION = 17;
    public static final int CREATE_BACKUP_DATA_FILE = 18;
    public static final int CREATE_VALUE_FILE = 19;
    public static final int CREDIT = 20;
    public static final int LIMITED_CREDIT = 21;
    public static final int DEBIT = 22;
    public static final int GET_VALUE = 23;
    public static final int CREATE_LINEAR_RECORD_FILE = 24;
    public static final int CREATE_CYCLIC_RECORD_FILE = 25;
    public static final int WRITE_RECORD = 26;
    public static final int UPDATE_RECORD = 27;
    public static final int READ_RECORDS = 28;
    public static final int CLEAR_RECORD_FILE = 29;
    public static final int COMMIT_TRANSACTION = 30;
    public static final int COMMIT_TRANSACTION_Option = 31;
    public static final int ABORT_TRANSATION = 32;
    //public static final int COMMIT_READER_ID = 33;
    public static final int READ_SIG = 34;
    public static final int SET_CONFIGURATION = 35;
    public static final int GET_CARD_UID = 36;
    public static final int AUTH_FIRST = 37;
    public static final int AUTH_NO_FIRST = 38;
    public static final int CHANGE_KEY = 39;
    public static final int CHANGE_KEY_EV2 = 40;
    public static final int CHANGE_KEY_SETTINGS = 41;
    public static final int CHANGE_FILE_SETTINGS = 42;
    public static final int SELECT_APP = 43;
    public static final int INITIALIZE_KEYSET = 44;
    public static final int FINALIZE_KEYSET = 45;
    public static final int ROLL_KEYSET = 46;
    public static final int CREATE_TRANSACTION_MAC_FILE = 47;
    public static final int COMMIT_READER_ID = 48;
    public static final int CREATE_DELEGATED_APP = 49;
    public static final int GET_DELEGATED_INFO = 50;
    public static final int GET_FILE_COUNTERS = 51;
    public static final int FORMAT_TO_OVER = 52;

    public static final int FELICA_POLLING = 60;
    public static final int FELICA_READ = 61;
    public static final int FELICA_WRITE = 62;

    public static final int OPEN_NFC_EV2 = 96;
    public static final int OPEN_NFC_FELICA = 97;
    public static final int CLOSE_NFC_EV2 = 98;
    public static final int CLOSE_NFC_FELICA = 99;

    @SuppressLint("StaticFieldLeak")
    private static final int OK = 0;
    private static final int FAIL = -1;
    private static Button btn_OpenNFC = null;
    private static Button btn_CheckNFC = null;
    private static Button btn_GetATS = null;
    private static Button btn_ActivateNewCard = null;
    private static Button btn_FreeMem = null;
    private static Button btn_Format = null;
    private static Button btn_GetVersion = null;
    private static Button btn_GetApplicationIDs = null;
    private static Button btn_CreateApplication = null;
    private static Button btn_DeleteApplication = null;
    private static Button btn_GetFileIDs = null;
    private static Button btn_GetFileSettings = null;
    private static Button btn_CreateStdDataFile = null;
    private static Button btn_DeleteFile = null;
    private static Button btn_WriteData = null;
    private static Button btn_ReadData = null;
    private static Button btn_GetKeySettings = null;
    private static Button btn_GetKeyVersion = null;
    private static Button btn_CloseNFC = null;
    private static Button btn_CreateBackupDataFile = null;
    private static Button btn_CreateValueFile = null;
    private static Button btn_Credit = null;
    private static Button btn_LimitedCredit = null;
    private static Button btn_Debit = null;
    private static Button btn_GetValue = null;
    private static Button btn_CreateLinearRecordFile = null;
    private static Button btn_CreateCyclicRecordFile = null;
    private static Button btn_WriteRecord = null;
    private static Button btn_UpdateRecord = null;
    private static Button btn_ReadRecords = null;
    private static Button btn_ClearRecordFile = null;
    private static Button btn_CommitTransaction = null;
    private static Button btn_AbortTransaction = null;
    //private static Button btn_CommitReaderID = null;
    private static Button btn_Read_Sig = null;
    private static Button btn_SetConfiguration = null;
    private static Button btn_GetCardUID = null;
    private static Button btn_AuthenticateEV2First = null;
    private static Button btn_AuthenticateEV2NonFirst = null;
    private static Button btn_ChangeKey = null;
    private static Button btn_ChangeKeyEV2 = null;
    private static Button btn_ChangeKeySettings = null;
    private static Button btn_ChangeFileSettings = null;
    private static Button btn_SelectApplication = null;
    private static Button btn_InitializeKeySet = null;
    private static Button btn_FinalizeKeySet = null;
    private static Button btn_RollKeySet = null;
    private static Button btn_CreateTransactionMACFile = null;
    private static Button btn_CommitReaderID = null;
    private static Button btn_CreateDelegatedApplication = null;
    private static Button btn_GetDelegatedInfo = null;
    private static Button btn_GetFileCounters = null;
    private static Button btn_Format2Over = null;

    private static Button btn_Felica_OpenNFC = null;
    private static Button btn_Felica_Polling = null;
    private static Button btn_Felica_Read = null;
    private static Button btn_Felica_Write = null;
    private static Button btn_Felica_CloseNFC = null;
    private static Button btn_Switch_JumpEv2Test = null;
    private static Button btn_Switch_JumpEv3Test = null;
    private static Button btn_Switch_JumpFelicaTest = null;

    private static TextView tv_CardInfo = null;
    private static TextView tv_CardATS = null;
    private static TextView tv_ActivateNewCard = null;
    private static TextView tv_FreeMem = null;
    private static TextView tv_Format = null;
    private static TextView tv_GetVersion = null;
    private static TextView tv_GetApplicationIDs = null;
    private static TextView tv_CreateApplication = null;
    private static TextView tv_DeleteApplication = null;
    private static TextView tv_GetFileIDs = null;
    private static TextView tv_GetFileSettings = null;
    private static TextView tv_CreateStdDataFile = null;
    private static TextView tv_DeleteFile = null;
    private static TextView tv_WriteData = null;
    private static TextView tv_ReadData = null;
    private static TextView tv_GetKeySettings = null;
    private static TextView tv_GetKeyVersion = null;
    private static TextView tv_CreateBackupDataFile = null;
    private static TextView tv_CreateValueFile = null;
    private static TextView tv_Credit = null;
    private static TextView tv_LimitedCredit = null;
    private static TextView tv_Debit = null;
    private static TextView tv_GetValue = null;
    private static TextView tv_CreateLinearRecordFile = null;
    private static TextView tv_CreateCyclicRecordFile = null;
    private static TextView tv_WriteRecord = null;
    private static TextView tv_UpdateRecord = null;
    private static TextView tv_ReadRecords = null;
    private static TextView tv_ClearRecordFile = null;
    private static TextView tv_CommitTransaction = null;
    private static TextView tv_AbortTransaction = null;
    //private static TextView tv_CommitReaderID = null;
    private static TextView tv_Read_Sig = null;
    private static TextView tv_SetConfiguration = null;
    private static TextView tv_GetCardUID = null;
    private static TextView tv_AuthenticateEV2First = null;
    private static TextView tv_AuthenticateEV2NonFirst = null;
    private static TextView tv_ChangeKey = null;
    private static TextView tv_ChangeKeyEV2 = null;
    private static TextView tv_ChangeKeySettings = null;
    private static TextView tv_ChangeFileSettings = null;
    private static TextView tv_SelectApplication = null;
    private static TextView tv_InitializeKeySet = null;
    private static TextView tv_FinalizeKeySet = null;
    private static TextView tv_RollKeySet = null;
    private static TextView tv_CreateTransactionMACFile = null;
    private static TextView tv_CommitReaderID = null;
    private static TextView tv_CreateDelegatedApplication = null;
    private static TextView tv_GetDelegatedInfo = null;
    private static TextView tv_GetFileCounters = null;
    private static TextView tv_Format2Over = null;

    private static TextView tv_Felica_Polling = null;
    private static TextView tv_Felica_Read = null;
    private static TextView tv_Felica_Write = null;

    private static EditText et_CreateApplication = null;
    private static EditText et_DeleteApplication = null;
    private static EditText et_CreateStdDataFile = null;
    private static EditText et_DeleteFile = null;
    private static EditText et_WriteData = null;
    private static EditText et_ReadData = null;
    private static EditText et_GetFileIDs = null;
    private static EditText et_GetFileSettings = null;
    private static EditText et_GetKeySettings = null;
    private static EditText et_GetKeyVersion = null;
    private static EditText et_CreateBackupDataFile = null;
    private static EditText et_CreateValueFile = null;
    private static EditText et_Credit = null;
    private static EditText et_LimitedCredit = null;
    private static EditText et_Debit = null;
    private static EditText et_GetValue = null;
    private static EditText et_CreateLinearRecordFile = null;
    private static EditText et_CreateCyclicRecordFile = null;
    private static EditText et_WriteRecord = null;
    private static EditText et_UpdateRecord = null;
    private static EditText et_ReadRecords = null;
    private static EditText et_ClearRecordFile = null;
    private static EditText et_CommitTransaction = null;
    //private static EditText et_CommitReaderID = null;
    private static EditText et_SetConfiguration = null;
    private static EditText et_AuthenticateEV2First = null;
    private static EditText et_AuthenticateEV2NonFirst = null;
    private static EditText et_ChangeKey = null;
    private static EditText et_ChangeKeyEV2 = null;
    private static EditText et_ChangeKeySettings = null;
    private static EditText et_ChangeFileSettings = null;
    private static EditText et_SelectApplication = null;
    private static EditText et_InitializeKeySet = null;
    private static EditText et_FinalizeKeySet = null;
    private static EditText et_RollKeySet = null;
    private static EditText et_CreateTransactionMACFile = null;
    private static EditText et_CommitReaderID = null;
    private static EditText et_CreateDelegatedApplication = null;
    private static EditText et_GetDelegatedInfo = null;
    private static EditText et_GetFileCounters = null;

    private static EditText et_Felica_Polling = null;
    private static EditText et_Felica_Read = null;
    private static EditText et_Felica_Write = null;

    private static TableLayout layout = null;
    private Demo demo = new Demo(this);

    private View layoutSwitch = null;
//    private View layoutEv2 = null;
//    private View layoutFelica = null;

    private static long exitTime = 0;// 退出时间
    private static boolean isSwitchUI = true;

    private static boolean isChecking = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if(isSwitchUI) {
                // 判断间隔时间 大于2秒就退出应用
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    // 计算两次返回键按下的时间差
                    exitTime = System.currentTimeMillis();
                } else {
                    // 关闭应用程序
                    finish();
                }
            }else {
                isSwitchUI = true;
                setContentView(layoutSwitch);
                this.setTitle("IC Card Type Select");
                demo.OP_NFC_Device_Close();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mifare_desfire);

        LayoutInit();
        JumpEv2TestUI();
        btn_Switch_JumpEv2Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo.OP_NFC_Device_Close();
                Message message = new Message();
                message.what = CLOSE_NFC_EV2;
                handler.sendMessage(message);
                JumpEv2TestUI();
            }
        });

        btn_Switch_JumpEv3Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo.OP_NFC_Device_Close();
                Message message = new Message();
                message.what = CLOSE_NFC_EV2;
                handler.sendMessage(message);
                JumpEv3TestUI();
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        String intToStr = "";
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_NFC_EV2:
                    if(msg.arg1 == OK) {
                        btn_CheckNFC.setEnabled(true);
                        btn_OpenNFC.setEnabled(false);
                        btn_CloseNFC.setEnabled(true);
                    }
                    break;

                case OPEN_NFC_FELICA:
                    if(msg.arg1 == OK) {
                        btn_Felica_OpenNFC.setEnabled(false);
                        btn_Felica_Polling.setEnabled(true);
                        btn_Felica_CloseNFC.setEnabled(true);
                    }
                    break;

                case CHECK_NFC:
                    String Nfcstr = "";
                    tv_CardInfo.setText("");
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        if(buf[0] == 'A') {
                            Nfcstr += "Card Tpye: Type A CPU\n";
//                            tv_CardInfo.append("Card Tpye: Type A CPU\n");
                        }else if(buf[0] == 'B') {
                            Nfcstr += "Card Tpye: Type B CPU\n";
//                            tv_CardInfo.append("Card Tpye: Type B CPU\n");
                        }else if(buf[0] == 'F') {
                            Nfcstr += "Card Tpye: Type F CPU\n";
//                            tv_CardInfo.append("Card Tpye: Type F CPU\n");
                        }
                        byte[] atqa = {buf[2],buf[3]};
                        int[] strLen = {2};
                        String aqta_str = bytesToHexString(atqa, strLen);
                        Nfcstr +=  "ATQA: "+aqta_str+"\n";
//                        tv_CardInfo.append("ATQA: "+aqta_str+"\n");

                        byte[] ask = {buf[4]};
                        strLen[0] = 1;
                        String ask_str = bytesToHexString(ask, strLen);
                        Nfcstr +=  "ASK: "+ask_str+"\n";
//                        tv_CardInfo.append("ASK: "+ask_str+"\n");

                        byte[] uid = new byte[7];
                        System.arraycopy(buf, 6, uid, 0, uid.length);
                        strLen[0] = 7;
                        String uid_str = bytesToHexString(uid, strLen);
                        Nfcstr +=  "UID: "+uid_str+"\n";
//                        tv_CardInfo.append("UID: "+uid_str+"\n");
                        ButtonsSetState(false);
                    }else{
                        Nfcstr = "未检索到IC卡";
//                        tv_CardInfo.setText("未检索到IC卡");
                    }
                    tv_CardInfo.setText(Nfcstr);
                    Log.d("tagg","CheckNFC >> \n" + Nfcstr);
                    break;

                case GET_ATS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_CardATS.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CardATS.setText("error: "+intToStr);
                    }

                    break;

                case ACTIVATE_NEW_CARD:
                    if(msg.arg1 == OK) {
                        tv_ActivateNewCard.setText("success");
                    }else{
                        tv_ActivateNewCard.setText("error");
                    }
                    break;

                case FREE_MEM:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_FreeMem.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_FreeMem.setText("error: "+intToStr);
                    }
                    break;

                case FORMAT:
                    if(msg.arg1 == OK) {
                        tv_Format.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Format.setText("error: "+intToStr);
                    }
                    break;

                case FORMAT_TO_OVER:
                    if(msg.arg1 == OK) {
                        tv_Format2Over.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Format2Over.setText("error: "+intToStr);
                    }
                    break;

                case GET_VERSION:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetVersion.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetVersion.setText("error: "+intToStr);
                    }
                    break;

                case GET_APP_IDS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetApplicationIDs.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetApplicationIDs.setText("error: "+intToStr);
                    }
                    break;

                case GET_DELEGATED_INFO:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetDelegatedInfo.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetDelegatedInfo.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_APP:
                    if(msg.arg1 == OK) {
                        tv_CreateApplication.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateApplication.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_DELEGATED_APP:
                    if(msg.arg1 == OK) {
                        tv_CreateDelegatedApplication.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateDelegatedApplication.setText("error: "+intToStr);
                    }
                    break;

                case DELETE_APP:
                    if(msg.arg1 == OK) {
                        tv_DeleteApplication.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_DeleteApplication.setText("error: "+intToStr);
                    }
                    break;

                case SELECT_APP:
                    if(msg.arg1 == OK) {
                        tv_SelectApplication.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_SelectApplication.setText("error: "+intToStr);
                    }
                    break;

                case GET_FILE_IDS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetFileIDs.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetFileIDs.setText("error: "+intToStr);
                    }
                    break;

                case GET_FILE_SETTINGS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetFileSettings.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetFileSettings.setText("error: "+intToStr);
                    }
                    break;

                case GET_FILE_COUNTERS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetFileCounters.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetFileCounters.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_STD_DATA_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateStdDataFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateStdDataFile.setText("error: "+intToStr);
                    }
                    break;

                case DELETE_FILE:
                    if(msg.arg1 == OK) {
                        tv_DeleteFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_DeleteFile.setText("error: "+intToStr);
                    }
                    break;

                case WRITE_DATA:
                    if(msg.arg1 == OK) {
                        tv_WriteData.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_WriteData.setText("error: "+intToStr);
                    }
                    break;

                case READ_DATA:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_ReadData.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ReadData.setText("error: "+intToStr);
                    }
                    break;

                case GET_KEY_SETTINGS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetKeySettings.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetKeySettings.setText("error: "+intToStr);
                    }
                    break;

                case GET_KEY_VERSION:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetKeyVersion.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetKeyVersion.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_BACKUP_DATA_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateBackupDataFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateBackupDataFile.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_VALUE_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateValueFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateValueFile.setText("error: "+intToStr);
                    }
                    break;

                case CREDIT:
                    if(msg.arg1 == OK) {
                        tv_Credit.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Credit.setText("error: "+intToStr);
                    }
                    break;

                case LIMITED_CREDIT:
                    if(msg.arg1 == OK) {
                        tv_LimitedCredit.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_LimitedCredit.setText("error: "+intToStr);
                    }
                    break;

                case DEBIT:
                    if(msg.arg1 == OK) {
                        tv_Debit.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Debit.setText("error: "+intToStr);
                    }
                    break;

                case GET_VALUE:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetValue.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetValue.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_LINEAR_RECORD_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateLinearRecordFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateLinearRecordFile.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_CYCLIC_RECORD_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateCyclicRecordFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateCyclicRecordFile.setText("error: "+intToStr);
                    }
                    break;

                case CREATE_TRANSACTION_MAC_FILE:
                    if(msg.arg1 == OK) {
                        tv_CreateTransactionMACFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CreateTransactionMACFile.setText("error: "+intToStr);
                    }
                    break;

                case WRITE_RECORD:
                    if(msg.arg1 == OK) {
                        tv_WriteRecord.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_WriteRecord.setText("error: "+intToStr);
                    }
                    break;

                case UPDATE_RECORD:
                    if(msg.arg1 == OK) {
                        tv_UpdateRecord.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_UpdateRecord.setText("error: "+intToStr);
                    }
                    break;

                case READ_RECORDS:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_ReadRecords.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ReadRecords.setText("error: "+intToStr);
                    }
                    break;

                case CLEAR_RECORD_FILE:
                    if(msg.arg1 == OK) {
                        tv_ClearRecordFile.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ClearRecordFile.setText("error: "+intToStr);
                    }
                    break;

                case COMMIT_TRANSACTION:
                    if(msg.arg1 == OK) {
                        tv_CommitTransaction.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CommitTransaction.setText("error: "+intToStr);
                    }
                    break;

                case COMMIT_TRANSACTION_Option:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_CommitTransaction.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CommitTransaction.setText("error: "+intToStr);
                    }
                    break;

                case ABORT_TRANSATION:
                    if(msg.arg1 == OK) {
                        tv_AbortTransaction.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_AbortTransaction.setText("error: "+intToStr);
                    }
                    break;

                case COMMIT_READER_ID:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_CommitReaderID.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_CommitReaderID.setText("error: "+intToStr);
                    }
                    break;

                case READ_SIG:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_Read_Sig.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Read_Sig.setText("error: "+intToStr);
                    }
                    break;

                case SET_CONFIGURATION:
                    if(msg.arg1 == OK) {
                        tv_SetConfiguration.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_SetConfiguration.setText("error: "+intToStr);
                    }
                    break;

                case GET_CARD_UID:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_GetCardUID.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_GetCardUID.setText("error: "+intToStr);
                    }
                    break;

                case AUTH_FIRST:
                    if(msg.arg1 == OK) {
                        tv_AuthenticateEV2First.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_AuthenticateEV2First.setText("error: "+intToStr);
                    }
                    break;

                case AUTH_NO_FIRST:
                    if(msg.arg1 == OK) {
                        tv_AuthenticateEV2NonFirst.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_AuthenticateEV2NonFirst.setText("error: "+intToStr);
                    }
                    break;

                case CHANGE_KEY:
                    if(msg.arg1 == OK) {
                        tv_ChangeKey.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ChangeKey.setText("error: "+intToStr);
                    }
                    break;

                case CHANGE_KEY_EV2:
                    if(msg.arg1 == OK) {
                        tv_ChangeKeyEV2.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ChangeKeyEV2.setText("error: "+intToStr);
                    }
                    break;

                case INITIALIZE_KEYSET:
                    if(msg.arg1 == OK) {
                        tv_InitializeKeySet.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_InitializeKeySet.setText("error: "+intToStr);
                    }
                    break;

                case FINALIZE_KEYSET:
                    if(msg.arg1 == OK) {
                        tv_FinalizeKeySet.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_FinalizeKeySet.setText("error: "+intToStr);
                    }
                    break;

                case ROLL_KEYSET:
                    if(msg.arg1 == OK) {
                        tv_RollKeySet.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_RollKeySet.setText("error: "+intToStr);
                    }
                    break;

                case CHANGE_KEY_SETTINGS:
                    if(msg.arg1 == OK) {
                        tv_ChangeKeySettings.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ChangeKeySettings.setText("error: "+intToStr);
                    }
                    break;

                case CHANGE_FILE_SETTINGS:
                    if(msg.arg1 == OK) {
                        tv_ChangeFileSettings.setText("success");
                    }else{
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_ChangeFileSettings.setText("error: "+intToStr);
                    }
                    break;

                case FELICA_POLLING:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Polling.setText(str);
                        btn_Felica_Read.setEnabled(true);
                        btn_Felica_Write.setEnabled(true);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Polling.setText("error: "+intToStr);
                    }
                    break;

                case FELICA_READ:
                    if(msg.arg1 == OK) {
                        byte[] buf = (byte[])msg.obj;
                        int[] bufLen = {buf.length};
                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Read.setText(str);
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Read.setText("error: "+intToStr);
                    }
                    break;

                case FELICA_WRITE:
                    if(msg.arg1 == OK) {
//                        byte[] buf = (byte[])msg.obj;
//                        int[] bufLen = {buf.length};
//                        String str = bytesToHexString(buf, bufLen);
                        tv_Felica_Write.setText("success");
                    }else {
                        if(msg.arg2 < 0) {
                            intToStr = String.valueOf(msg.arg2);
                        }else{
                            intToStr = Integer.toHexString(msg.arg2);
                            intToStr = "0x"+intToStr;
                        }
                        tv_Felica_Write.setText("error: "+intToStr);
                    }
                    break;

                case CLOSE_NFC_EV2:
                    if(msg.arg1 == OK) {
                        if(btn_GetATS.isEnabled()) {
                            ButtonsSetState(true);
                        }else{
                            btn_CloseNFC.setEnabled(false);
                            btn_CheckNFC.setEnabled(false);
                            btn_OpenNFC.setEnabled(true);
                        }
                    }
                    break;

                case CLOSE_NFC_FELICA:
                    if(msg.arg1 == OK) {
                        btn_Felica_OpenNFC.setEnabled(true);
                        btn_Felica_Polling.setEnabled(false);
                        btn_Felica_Read.setEnabled(false);
                        btn_Felica_Write.setEnabled(false);
                        btn_Felica_CloseNFC.setEnabled(false);
                    }
                    break;
                default:
                    break;
            }
            layout.setFocusable(true);
            layout.setFocusableInTouchMode(true);
            layout.requestFocus();
        }
    };

    public static void ButtonsSetState(boolean IsCloseNFC) {
        for(int i=0;i<layout.getChildCount();i++)
        {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if(!((Button) v).getText().equals("Open NFC")) {
                    v.setEnabled(!IsCloseNFC);
                }else{
                    v.setEnabled(IsCloseNFC);
                }
            }else if (v instanceof TableRow) {
                for(int j=0;j<((TableRow) v).getChildCount();j++) {
                    View b = ((TableRow) v).getChildAt(j);
                    if (b instanceof Button) {
                        b.setEnabled(!IsCloseNFC);
                    }
                }
            }
        }
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String[] hex = hexString.split(" ");

        if(hex.length > 0) {
            String hexStr = hex[0];
            for (int i=1; i< hex.length; i++) {
                hexStr = hexStr.concat(hex[i]);
            }
            hexStr = hexStr.toUpperCase();
            int length = hexStr.length() / 2;
            char[] hexChars = hexStr.toCharArray();
            byte[] d = new byte[length];
            for (int i = 0; i < length; i++) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }
            return d;
        }
        return null;
    }

    public static String bytesToHexString(byte[] src, int[] srcLen){
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || srcLen == null) {
            return null;
        }
        for (int i = 0; i < srcLen[0]; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            hv = hv.toUpperCase();
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            stringBuilder.append(" ");
        }
        stringBuilder.append("(hex)");
        return stringBuilder.toString();
    }

    public void JumpEv2TestUI() {
        this.setTitle("Mifare Desfire Ev2 Test");
        btn_Switch_JumpEv2Test.setEnabled(false);
        btn_Switch_JumpEv3Test.setEnabled(true);
//        LayoutInit();
        View line_GetFileCounters = (View)findViewById(R.id.line_GetFileCounters);
        line_GetFileCounters.setVisibility(View.GONE);
        et_ChangeFileSettings.setText("00 00 01 01 01 00 00");
        et_GetFileCounters.setVisibility(View.GONE);
        btn_GetFileCounters.setVisibility(View.GONE);
        tv_GetFileCounters.setVisibility(View.GONE);

        et_CreateStdDataFile.setText("00 00 01 01 01 00 00 20 00 00");
        BtnClickFunction();
    }

    public void JumpEv3TestUI() {
        this.setTitle("Mifare Desfire Ev3 Test");
        btn_Switch_JumpEv2Test.setEnabled(true);
        btn_Switch_JumpEv3Test.setEnabled(false);
//        LayoutInit();
        View line_GetFileCounters = (View)findViewById(R.id.line_GetFileCounters);
        line_GetFileCounters.setVisibility(View.VISIBLE);
        et_ChangeFileSettings.setText("00 00 01 07 43 00 00 41 FE FF");
        et_GetFileCounters.setVisibility(View.VISIBLE);
        btn_GetFileCounters.setVisibility(View.VISIBLE);
        tv_GetFileCounters.setVisibility(View.VISIBLE);

        et_CreateStdDataFile.setText("00 00 01 01 01 00 00 20 00 00 07 41 00 00 20 00 00");
        BtnClickFunction();
    }

    public void LayoutInit() {

        btn_Switch_JumpEv2Test = findViewById(R.id.switch_ev2_btn);
        btn_Switch_JumpEv3Test = findViewById(R.id.switch_ev3_btn);
        btn_OpenNFC = findViewById(R.id.Button_OpenNFC);
        btn_CheckNFC = findViewById(R.id.Button_CheckNFC);
        btn_GetATS = findViewById(R.id.Button_GetATS);
        btn_ActivateNewCard = findViewById(R.id.Button_ActivateNewCard);
        btn_FreeMem = findViewById(R.id.Button_FreeMem);
        btn_Format = findViewById(R.id.Button_Format);
        btn_GetVersion = findViewById(R.id.Button_GetVersion);
        btn_GetApplicationIDs = findViewById(R.id.Button_GetApplicationIDs);
        btn_CreateApplication = findViewById(R.id.Button_CreateApplication);
        btn_DeleteApplication = findViewById(R.id.Button_DeleteApplication);
        btn_GetFileIDs= findViewById(R.id.Button_GetFileIDs);
        btn_GetFileSettings= findViewById(R.id.Button_GetFileSettings);
        btn_CreateStdDataFile= findViewById(R.id.Button_CreateStdDataFile);
        btn_DeleteFile= findViewById(R.id.Button_DeleteFile);
        btn_WriteData= findViewById(R.id.Button_WriteData);
        btn_ReadData= findViewById(R.id.Button_ReadData);
        btn_GetKeySettings= findViewById(R.id.Button_GetKeySettings);
        btn_GetKeyVersion= findViewById(R.id.Button_GetKeyVersion);
        btn_CreateBackupDataFile = findViewById(R.id.Button_CreateBackupDataFile);
        btn_CreateValueFile = findViewById(R.id.Button_CreateValueFile);
        btn_Credit = findViewById(R.id.Button_Credit);
        btn_LimitedCredit = findViewById(R.id.Button_LimitedCredit);
        btn_Debit = findViewById(R.id.Button_Debit);
        btn_GetValue = findViewById(R.id.Button_GetValue);
        btn_CreateLinearRecordFile = findViewById(R.id.Button_CreateLinearRecordFile);
        btn_CreateCyclicRecordFile = findViewById(R.id.Button_CreateCyclicRecordFile);
        btn_WriteRecord = findViewById(R.id.Button_WriteRecord);
        btn_UpdateRecord = findViewById(R.id.Button_UpdateRecord);
        btn_ReadRecords = findViewById(R.id.Button_ReadRecords);
        btn_ClearRecordFile = findViewById(R.id.Button_ClearRecordFile);
        btn_CommitTransaction = findViewById(R.id.Button_CommitTransaction);
        btn_AbortTransaction = findViewById(R.id.Button_AbortTransaction);
        //btn_CommitReaderID = findViewById(R.id.Button_CommitReaderID);
        btn_Read_Sig = findViewById(R.id.Button_Read_Sig);
        btn_SetConfiguration = findViewById(R.id.Button_SetConfiguration);
        btn_GetCardUID = findViewById(R.id.Button_GetCardUID);
        btn_CloseNFC = findViewById(R.id.Button_CloseNFC);
        btn_AuthenticateEV2First = findViewById(R.id.Button_AuthenticateEV2First);
        btn_AuthenticateEV2NonFirst = findViewById(R.id.Button_AuthenticateEV2NonFirst);
        btn_ChangeKey = findViewById(R.id.Button_ChangeKey);
        btn_ChangeKeyEV2 = findViewById(R.id.Button_ChangeKeyEV2);
        btn_ChangeKeySettings = findViewById(R.id.Button_ChangeKeySettings);
        btn_ChangeFileSettings = findViewById(R.id.Button_ChangeFileSettings);
        btn_SelectApplication = findViewById(R.id.Button_SelectApplication);
        btn_InitializeKeySet = findViewById(R.id.Button_InitializeKeySet);
        btn_FinalizeKeySet = findViewById(R.id.Button_FinalizeKeySet);
        btn_RollKeySet = findViewById(R.id.Button_RollKeySet);
        btn_CreateTransactionMACFile = findViewById(R.id.Button_CreateTransactionMACFile);
        btn_CommitReaderID = findViewById(R.id.Button_CommitReaderID);
        btn_CreateDelegatedApplication = findViewById(R.id.Button_CreateDelegatedApplication);
        btn_GetDelegatedInfo = findViewById(R.id.Button_GetDelegatedInfo);
        btn_GetFileCounters = findViewById(R.id.Button_GetFileCounters);
        btn_Format2Over = findViewById(R.id.Button_Format2Over);

        tv_CardInfo = findViewById(R.id.TextView_CardInfo);
        tv_CardATS = findViewById(R.id.TextView_CardATS);
        tv_ActivateNewCard = findViewById(R.id.TextView_ActivateNewCard);
        tv_FreeMem = findViewById(R.id.TextView_FreeMem);
        tv_Format = findViewById(R.id.TextView_Format);
        tv_GetVersion = findViewById(R.id.TextView_GetVersion);
        tv_GetApplicationIDs = findViewById(R.id.TextView_GetApplicationIDs);
        tv_CreateApplication = findViewById(R.id.TextView_CreateApplication);
        tv_DeleteApplication = findViewById(R.id.TextView_DeleteApplication);
        tv_GetFileIDs = findViewById(R.id.TextView_GetFileIDs);
        tv_GetFileSettings = findViewById(R.id.TextView_GetFileSettings);
        tv_CreateStdDataFile = findViewById(R.id.TextView_CreateStdDataFile);
        tv_DeleteFile = findViewById(R.id.TextView_DeleteFile);
        tv_WriteData = findViewById(R.id.TextView_WriteData);
        tv_ReadData = findViewById(R.id.TextView_ReadData);
        tv_GetKeySettings = findViewById(R.id.TextView_GetKeySettings);
        tv_GetKeyVersion = findViewById(R.id.TextView_GetKeyVersion);
        tv_CreateBackupDataFile = findViewById(R.id.TextView_CreateBackupDataFile);
        tv_CreateValueFile = findViewById(R.id.TextView_CreateValueFile);
        tv_Credit = findViewById(R.id.TextView_Credit);
        tv_LimitedCredit = findViewById(R.id.TextView_LimitedCredit);
        tv_Debit = findViewById(R.id.TextView_Debit);
        tv_GetValue = findViewById(R.id.TextView_GetValue);
        tv_CreateLinearRecordFile = findViewById(R.id.TextView_CreateLinearRecordFile);
        tv_CreateCyclicRecordFile = findViewById(R.id.TextView_CreateCyclicRecordFile);
        tv_WriteRecord = findViewById(R.id.TextView_WriteRecord);
        tv_UpdateRecord = findViewById(R.id.TextView_UpdateRecord);
        tv_ReadRecords = findViewById(R.id.TextView_ReadRecords);
        tv_ClearRecordFile = findViewById(R.id.TextView_ClearRecordFile);
        tv_CommitTransaction = findViewById(R.id.TextView_CommitTransaction);
        tv_AbortTransaction = findViewById(R.id.TextView_AbortTransaction);
        //tv_CommitReaderID = findViewById(R.id.TextView_CommitReaderID);
        tv_Read_Sig = findViewById(R.id.TextView_Read_Sig);
        tv_SetConfiguration = findViewById(R.id.TextView_SetConfiguration);
        tv_GetCardUID = findViewById(R.id.TextView_GetCardUID);
        tv_AuthenticateEV2First = findViewById(R.id.TextView_AuthenticateEV2First);
        tv_AuthenticateEV2NonFirst = findViewById(R.id.TextView_AuthenticateEV2NonFirst);
        tv_ChangeKey = findViewById(R.id.TextView_ChangeKey);
        tv_ChangeKeyEV2 = findViewById(R.id.TextView_ChangeKeyEV2);
        tv_ChangeKeySettings = findViewById(R.id.TextView_ChangeKeySettings);
        tv_ChangeFileSettings = findViewById(R.id.TextView_ChangeFileSettings);
        tv_SelectApplication = findViewById(R.id.TextView_SelectApplication);
        tv_InitializeKeySet = findViewById(R.id.TextView_InitializeKeySet);
        tv_FinalizeKeySet = findViewById(R.id.TextView_FinalizeKeySet);
        tv_RollKeySet = findViewById(R.id.TextView_RollKeySet);
        tv_CreateTransactionMACFile = findViewById(R.id.TextView_CreateTransactionMACFile);
        tv_CommitReaderID = findViewById(R.id.TextView_CommitReaderID);
        tv_CreateDelegatedApplication = findViewById(R.id.TextView_CreateDelegatedApplication);
        tv_GetDelegatedInfo = findViewById(R.id.TextView_GetDelegatedInfo);
        tv_GetFileCounters = findViewById(R.id.TextView_GetFileCounters);
        tv_Format2Over = findViewById(R.id.TextView_Format2Over);

        et_CreateApplication = findViewById(R.id.EditText_CreateApplication);
        et_DeleteApplication = findViewById(R.id.EditText_DeleteApplication);
        et_CreateStdDataFile = findViewById(R.id.EditText_CreateStdDataFile);
        et_DeleteFile = findViewById(R.id.EditText_DeleteFile);
        et_WriteData = findViewById(R.id.EditText_WriteData);
        et_ReadData = findViewById(R.id.EditText_ReadData);
        et_GetFileIDs = findViewById(R.id.EditText_GetFileIDs);
        et_GetFileSettings = findViewById(R.id.EditText_GetFileSettings);
        et_GetKeySettings = findViewById(R.id.EditText_GetKeySettings);
        et_GetKeyVersion = findViewById(R.id.EditText_GetKeyVersion);
        et_CreateBackupDataFile = findViewById(R.id.EditText_CreateBackupDataFile);
        et_CreateValueFile = findViewById(R.id.EditText_CreateValueFile);
        et_Credit = findViewById(R.id.EditText_Credit);
        et_LimitedCredit = findViewById(R.id.EditText_LimitedCredit);
        et_Debit = findViewById(R.id.EditText_Debit);
        et_GetValue = findViewById(R.id.EditText_GetValue);
        et_CreateLinearRecordFile = findViewById(R.id.EditText_CreateLinearRecordFile);
        et_CreateCyclicRecordFile = findViewById(R.id.EditText_CreateCyclicRecordFile);
        et_WriteRecord = findViewById(R.id.EditText_WriteRecord);
        et_UpdateRecord = findViewById(R.id.EditText_UpdateRecord);
        et_ReadRecords = findViewById(R.id.EditText_ReadRecords);
        et_ClearRecordFile = findViewById(R.id.EditText_ClearRecordFile);
        et_CommitTransaction = findViewById(R.id.EditText_CommitTransaction);
        //et_CommitReaderID = findViewById(R.id.EditText_CommitReaderID);
        et_SetConfiguration = findViewById(R.id.EditText_SetConfiguration);
        et_AuthenticateEV2First = findViewById(R.id.EditText_AuthenticateEV2First);
        et_AuthenticateEV2NonFirst = findViewById(R.id.EditText_AuthenticateEV2NonFirst);
        et_ChangeKey = findViewById(R.id.EditText_ChangeKey);
        et_ChangeKeyEV2 = findViewById(R.id.EditText_ChangeKeyEV2);
        et_ChangeKeySettings = findViewById(R.id.EditText_ChangeKeySettings);
        et_ChangeFileSettings = findViewById(R.id.EditText_ChangeFileSettings);
        et_SelectApplication = findViewById(R.id.EditText_SelectApplication);
        et_InitializeKeySet = findViewById(R.id.EditText_InitializeKeySet);
        et_FinalizeKeySet = findViewById(R.id.EditText_FinalizeKeySet);
        et_RollKeySet = findViewById(R.id.EditText_RollKeySet);
        et_CreateTransactionMACFile = findViewById(R.id.EditText_CreateTransactionMACFile);
        et_CommitReaderID = findViewById(R.id.EditText_CommitReaderID);
        et_CreateDelegatedApplication = findViewById(R.id.EditText_CreateDelegatedApplication);
        et_GetDelegatedInfo = findViewById(R.id.EditText_GetDelegatedInfo);
        et_GetFileCounters = findViewById(R.id.EditText_GetFileCounters);

        layout = findViewById(R.id.TableLayout);
        for(int i=0;i<layout.getChildCount();i++)
        {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if(!((Button) v).getText().equals("Open NFC")) {
                    v.setEnabled(false);
                }
            }else if (v instanceof TableRow) {
                for(int j=0;j<((TableRow) v).getChildCount();j++) {
                    View b = ((TableRow) v).getChildAt(j);
                    if (b instanceof Button) {
                        b.setEnabled(false);
                    }
                }
            }
        }
    }

    public void BtnClickFunction() {

        btn_OpenNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = OPEN_NFC_EV2;
                        if(demo.OP_NFC_Device_Open() == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
        btn_CheckNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                tv_CardInfo.setText("");
                if(!isChecking){
                    Log.d("tagg", "check click");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            isChecking = true;
                            while (isChecking){
                                byte[] timeoutMs = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00};
                                byte[] outData = new byte[256];
                                int[] outLen = new int[1];
                                int rc = demo.OP_NFC_Device_Activate(timeoutMs, outData, outLen);

                                Message message = new Message();
                                message.what = CHECK_NFC;
                                if (rc == OK) {
                                    byte[] buf = new byte[outLen[0]];
                                    System.arraycopy(outData, 0, buf, 0, buf.length);
                                    message.obj = buf;
                                    message.arg1 = OK;
                                    handler.sendMessage(message);
                                    isChecking = false;
                                    break;
                                } else {
                                    message.arg1 = FAIL;
                                    handler.sendMessage(message);
                                }

                                try {
                                    sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                            }

                        }
                    }).start();
                }

            }
        });




        btn_GetATS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CardATS.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_CPU_GET_ATS(outData,outLen);
                        Message message = new Message();
                        message.what = GET_ATS;
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
        btn_ActivateNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ActivateNewCard.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = ACTIVATE_NEW_CARD;
                        int rc = demo.OP_activateEv2ForNewCard();
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
        btn_FreeMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_FreeMem.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = FREE_MEM;

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_getFreeMem(outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
        btn_Format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Format.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = FORMAT;
                        int rc = demo.OP_Format();
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_Format2Over.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Format2Over.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = FORMAT_TO_OVER;
                        int rc = demo.OP_Format();
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetVersion.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_VERSION;

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetVersion(outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetCardUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetCardUID.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_CARD_UID;

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetCardUID(outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetApplicationIDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetApplicationIDs.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_APP_IDS;

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetApplicationIDs(outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetDelegatedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetDelegatedInfo.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_DELEGATED_INFO;

                        String editText = et_GetDelegatedInfo.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetDelegatedInfo(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateApplication.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_APP;

                        String editText = et_CreateApplication.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateApplication(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateDelegatedApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateDelegatedApplication.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_DELEGATED_APP;

                        String editText = et_CreateDelegatedApplication.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateDelegatedApplication(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_DeleteApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_DeleteApplication.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = DELETE_APP;

                        String editText = et_DeleteApplication.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_DeleteApplication(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_SelectApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_SelectApplication.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = SELECT_APP;

                        String editText = et_SelectApplication.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_SelectApplication(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetFileIDs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetFileIDs.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_FILE_IDS;

                        String editText = et_GetFileIDs.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetFileIDs(hex, outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetFileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetFileSettings.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_FILE_SETTINGS;

                        String editText = et_GetFileSettings.getText().toString();
                        byte[] hex = hexStringToBytes(editText);

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetFileSettings(hex, outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetFileCounters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetFileCounters.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_FILE_COUNTERS;

                        String editText = et_GetFileCounters.getText().toString();
                        byte[] hex = hexStringToBytes(editText);

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetFileCounters(hex, outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateStdDataFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateStdDataFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_STD_DATA_FILE;

                        String editText = et_CreateStdDataFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateStdDataFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateTransactionMACFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateTransactionMACFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_TRANSACTION_MAC_FILE;

                        String editText = et_CreateTransactionMACFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateTransactionMACFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_DeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_DeleteFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = DELETE_FILE;

                        String editText = et_DeleteFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_DeleteFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_WriteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_WriteData.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = WRITE_DATA;

                        String editText = et_WriteData.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_WriteData(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ReadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ReadData.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = READ_DATA;

                        String editText = et_ReadData.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_ReadData(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetKeySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetKeySettings.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_KEY_SETTINGS;

                        String editText = et_GetKeySettings.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetKeySettings(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetKeyVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetKeyVersion.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_KEY_VERSION;

                        String editText = et_GetKeyVersion.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetKeyVersion(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateBackupDataFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateBackupDataFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_BACKUP_DATA_FILE;

                        String editText = et_CreateBackupDataFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateBackupDataFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateValueFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateValueFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_VALUE_FILE;

                        String editText = et_CreateValueFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateValueFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_Credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Credit.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREDIT;

                        String editText = et_Credit.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_Credit(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_LimitedCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_LimitedCredit.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = LIMITED_CREDIT;

                        String editText = et_LimitedCredit.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_LimitedCredit(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_Debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Debit.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = DEBIT;

                        String editText = et_Debit.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_Debit(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_GetValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_GetValue.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = GET_VALUE;

                        String editText = et_GetValue.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_GetValue(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateLinearRecordFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateLinearRecordFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_LINEAR_RECORD_FILE;

                        String editText = et_CreateLinearRecordFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateLinearRecordFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CreateCyclicRecordFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CreateCyclicRecordFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CREATE_CYCLIC_RECORD_FILE;

                        String editText = et_CreateCyclicRecordFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_CreateCyclicRecordFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_WriteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_WriteRecord.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = WRITE_RECORD;

                        String editText = et_WriteRecord.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_WriteRecord(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_UpdateRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_UpdateRecord.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = UPDATE_RECORD;

                        String editText = et_UpdateRecord.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_UpdateRecord(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ReadRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ReadRecords.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = READ_RECORDS;

                        String editText = et_ReadRecords.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_ReadRecords(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ClearRecordFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ClearRecordFile.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CLEAR_RECORD_FILE;

                        String editText = et_ClearRecordFile.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_ClearRecordFile(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CommitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CommitTransaction.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = COMMIT_TRANSACTION;

                        String editText = et_CommitTransaction.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        if(hex != null) {
                            byte[] outData = new byte[256];
                            int[] outLen = new int[1];
                            int rc = demo.OP_CommitTransaction(hex[0], outData, outLen);
                            if(rc == OK) {
                                byte[] buf = new byte[outLen[0]];
                                System.arraycopy(outData, 0, buf, 0, buf.length);
                                message.obj = buf;
                                message.what = COMMIT_TRANSACTION_Option;
                                message.arg1 = OK;
                            }else{
                                message.arg1 = FAIL;
                                message.arg2 = rc;
                            }
                        }else {
                            int rc = demo.OP_CommitTransaction();
                            if(rc == OK) {
                                message.arg1 = OK;
                            }else{
                                message.arg1 = FAIL;
                                message.arg2 = rc;
                            }
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_AbortTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_AbortTransaction.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = ABORT_TRANSATION;

                        int rc = demo.OP_AbortTransaction();
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CommitReaderID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_CommitReaderID.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = COMMIT_READER_ID;

                        String editText = et_CommitReaderID.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_CommitReaderID(hex, outData, outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_Read_Sig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Read_Sig.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = READ_SIG;

                        byte[] outData = new byte[256];
                        int[] outLen = new int[1];
                        int rc = demo.OP_Read_Sig(outData,outLen);
                        if(rc == OK) {
                            byte[] buf = new byte[outLen[0]];
                            System.arraycopy(outData, 0, buf, 0, buf.length);
                            message.obj = buf;
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_SetConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_SetConfiguration.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = SET_CONFIGURATION;

                        String editText = et_SetConfiguration.getText().toString();

                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_SetConfiguration(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_AuthenticateEV2First.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_AuthenticateEV2First.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = AUTH_FIRST;

                        String editText = et_AuthenticateEV2First.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_AuthenticateEV2First(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_AuthenticateEV2NonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_AuthenticateEV2NonFirst.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = AUTH_NO_FIRST;

                        String editText = et_AuthenticateEV2NonFirst.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_AuthenticateEV2NonFirst(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ChangeKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ChangeKey.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CHANGE_KEY;

                        String editText = et_ChangeKey.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_ChangeKey(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ChangeKeyEV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ChangeKeyEV2.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CHANGE_KEY_EV2;

                        String editText = et_ChangeKeyEV2.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_ChangeKeyEV2(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_InitializeKeySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_InitializeKeySet.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = INITIALIZE_KEYSET;

                        String editText = et_InitializeKeySet.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_InitializeKeySet(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_FinalizeKeySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_FinalizeKeySet.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = FINALIZE_KEYSET;

                        String editText = et_FinalizeKeySet.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_FinalizeKeySet(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_RollKeySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_RollKeySet.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = ROLL_KEYSET;

                        String editText = et_RollKeySet.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_RollKeySet(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ChangeKeySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ChangeKeySettings.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CHANGE_KEY_SETTINGS;

                        String editText = et_ChangeKeySettings.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_ChangeKeySettings(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_ChangeFileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_ChangeFileSettings.setText("");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message message = new Message();
                        message.what = CHANGE_FILE_SETTINGS;

                        String editText = et_ChangeFileSettings.getText().toString();
                        byte[] hex = hexStringToBytes(editText);
                        int rc = demo.OP_ChangeFileSettings(hex);
                        if(rc == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                            message.arg2 = rc;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });

        btn_CloseNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        isChecking = false;
                        Message message = new Message();
                        message.what = CLOSE_NFC_EV2;
                        if(demo.OP_NFC_Device_Close() == OK) {
                            message.arg1 = OK;
                        }else{
                            message.arg1 = FAIL;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

//    public void JumpFelicaTestUI() {
//        setContentView(R.layout.activity_felica);
//
//        this.setTitle("Felica Test");
//
//        layout = findViewById(R.id.TableLayout);
//        for(int i=0;i<layout.getChildCount();i++)
//        {
//            View v = layout.getChildAt(i);
//            if (v instanceof Button) {
//                if(!((Button) v).getText().equals("Open NFC")) {
//                    v.setEnabled(false);
//                }
//            }else if (v instanceof TableRow) {
//                for(int j=0;j<((TableRow) v).getChildCount();j++) {
//                    View b = ((TableRow) v).getChildAt(j);
//                    if (b instanceof Button) {
//                        b.setEnabled(false);
//                    }
//                }
//            }
//        }
//        btn_Felica_OpenNFC = (Button)findViewById(R.id.Button_Felica_OpenNFC);
//        btn_Felica_Polling = (Button)findViewById(R.id.Button_Felica_Polling);
//        btn_Felica_Read = (Button)findViewById(R.id.Button_Felica_Read);
//        btn_Felica_Write = (Button)findViewById(R.id.Button_Felica_Write);
//        btn_Felica_CloseNFC = (Button)findViewById(R.id.Button_Felica_CloseNFC);
//
//        et_Felica_Polling = (EditText)findViewById(R.id.EditText_Felica_Polling);
//        et_Felica_Read = (EditText)findViewById(R.id.EditText_Felica_Read);
//        et_Felica_Write = (EditText)findViewById(R.id.EditText_Felica_Write);
//
//        tv_Felica_Polling = (TextView)findViewById(R.id.TextView_Felica_Polling);
//        tv_Felica_Read = (TextView)findViewById(R.id.TextView_Felica_Read);
//        tv_Felica_Write = (TextView)findViewById(R.id.TextView_Felica_Write);
//
//        btn_Felica_OpenNFC.setOnClickListener(new ButtonListen());
//        btn_Felica_Polling.setOnClickListener(new ButtonListen());
//        btn_Felica_Read.setOnClickListener(new ButtonListen());
//        btn_Felica_Write.setOnClickListener(new ButtonListen());
//        btn_Felica_CloseNFC.setOnClickListener(new ButtonListen());
//    }

//    class ButtonListen implements View.OnClickListener {
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.Button_Felica_OpenNFC:
//                    new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Message message = new Message();
//                            message.what = OPEN_NFC_FELICA;
//                            if(demo.OP_NFC_Device_Open() == OK) {
//                                message.arg1 = OK;
//                            }else{
//                                message.arg1 = FAIL;
//                            }
//                            handler.sendMessage(message);
//                        }
//                    }).start();
//                    break;
//                case R.id.Button_Felica_Polling:
//                    tv_Felica_Polling.setText("");
//                    new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            byte[] outData = new byte[256];
//                            int[] outLen = new int[1];
//                            Message message = new Message();
//                            message.what = FELICA_POLLING;
//
//                            String editText = et_Felica_Polling.getText().toString();
//                            byte[] hex = hexStringToBytes(editText);
//                            int rc = demo.OP_Felica_polling(hex, outData, outLen);
//                            if(rc == OK) {
//                                byte[] buf = new byte[outLen[0]];
//                                System.arraycopy(outData, 0, buf, 0, buf.length);
//                                message.obj = buf;
//                                message.arg1 = OK;
//                            }else {
//                                message.arg1 = FAIL;
//                                message.arg2 = rc;
//                            }
//                            handler.sendMessage(message);
//                        }
//                    }).start();
//                    break;
//                case R.id.Button_Felica_Read:
//                    tv_Felica_Read.setText("");
//                    new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            byte[] outData = new byte[256];
//                            int[] outLen = new int[1];
//                            Message message = new Message();
//                            message.what = FELICA_READ;
//
//                            String editText = et_Felica_Read.getText().toString();
//                            byte[] hex = hexStringToBytes(editText);
//                            int rc = demo.OP_Felica_read(hex, outData, outLen);
//                            if(rc == OK) {
//                                byte[] buf = new byte[outLen[0]];
//                                System.arraycopy(outData, 0, buf, 0, buf.length);
//                                message.obj = buf;
//                                message.arg1 = OK;
//                            }else {
//                                message.arg1 = FAIL;
//                                message.arg2 = rc;
//                            }
//                            handler.sendMessage(message);
//                        }
//                    }).start();
//                    break;
//                case R.id.Button_Felica_Write:
//                    tv_Felica_Write.setText("");
//                    new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            byte[] outData = new byte[256];
//                            int[] outLen = new int[1];
//                            Message message = new Message();
//                            message.what = FELICA_WRITE;
//
//                            String editText = et_Felica_Write.getText().toString();
//                            byte[] hex = hexStringToBytes(editText);
//                            int rc = demo.OP_Felica_write(hex, outData, outLen);
//                            if(rc == OK) {
//                                byte[] buf = new byte[outLen[0]];
//                                System.arraycopy(outData, 0, buf, 0, buf.length);
//                                message.obj = buf;
//                                message.arg1 = OK;
//                            }else {
//                                message.arg1 = FAIL;
//                                message.arg2 = rc;
//                            }
//                            handler.sendMessage(message);
//                        }
//                    }).start();
//                    break;
//                case R.id.Button_Felica_CloseNFC:
////                    LinearLayout linearLayout = (LinearLayout)findViewById(R.id.LinearLayout_test);
////                    if(linearLayout.getVisibility() == View.GONE) {
////                        linearLayout.setVisibility(View.VISIBLE);
////                    }else {
////                        linearLayout.setVisibility(View.GONE);
////                    }
//                    new Thread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            Message message = new Message();
//                            message.what = CLOSE_NFC_FELICA;
//                            if(demo.OP_NFC_Device_Close() == OK) {
//                                message.arg1 = OK;
//                            }else{
//                                message.arg1 = FAIL;
//                            }
//                            handler.sendMessage(message);
//                        }
//                    }).start();
//                    break;
//                default:
//                    break;
//            }
//            layout.setFocusable(true);
//            layout.setFocusableInTouchMode(true);
//            layout.requestFocus();
//        }
//    }

    public void checkNfc(){
        byte[] timeoutMs = {(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] outData = new byte[256];
        int[] outLen = new int[1];
        int rc = demo.OP_NFC_Device_Activate(timeoutMs, outData, outLen);
        Message message = new Message();
        message.what = CHECK_NFC;
        if (rc == OK) {
            byte[] buf = new byte[outLen[0]];
            System.arraycopy(outData, 0, buf, 0, buf.length);
            message.obj = buf;
            message.arg1 = OK;
        } else {
            message.arg1 = FAIL;
        }
        handler.sendMessage(message);
        checkNfc();
    }
}
