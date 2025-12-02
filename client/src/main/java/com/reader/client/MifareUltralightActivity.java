package com.reader.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.reader.service.PsamResponse;
import com.reader.service.ReaderAIDL;
import com.reader.service.ReaderConstant;

import timber.log.Timber;


public class PsamCardActivity extends AppCompatActivity {
	private static final String TAG = "PSAM_SDK";
	private ReaderAIDL reader = ReaderAIDL.INSTANCE;
	private PsamResponse mPsamResp = new PsamResponse();
	private int OK = ReaderConstant.RESULT_CODE_SUCCESS;
	private Button readButton, protocolbtn, buttonAPDU;
	private Button getFDButton;
	private Button poweronButton;
	private Button poweroffButton;
	private Button openButton;
	private Button closeButton;
	private EditText mEditTextApdu;
	private TextView textReader;
	private Spinner mPasmNumSpr;

	// PasmNum
	private ArrayAdapter<String> mPasmNumAdapter;
	private int mPasmNum = 1 ;//SmartCardReader.SLOT_PSAM1;
	
	String internalModel;
	int tps537psamSelect = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_ic_psam);

//		internalModel = SystemUtil.getInternalModel();
		mPasmNumSpr = (Spinner) findViewById(R.id.mPasmNumSpr);
		mPasmNumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		mPasmNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mPasmNumAdapter.add("ICC");
		mPasmNumAdapter.add("PSAM1");
		mPasmNumAdapter.add("PSAM2");
//		mPasmNumAdapter.add("PSAM4");
//		mPasmNumAdapter.add("PSAM5");
//		mPasmNumAdapter.add("PSAM6");
//		mPasmNumAdapter.add("PSAM7");
//		mPasmNumAdapter.add("PSAM8");
		mPasmNumSpr.setAdapter(mPasmNumAdapter);
		mPasmNumSpr.setOnItemSelectedListener(new OnItemSelectedListener() {

			
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mPasmNum = position;
				Log.d("tagg", "using slot:"+mPasmNum);
				closeButton.setEnabled(false);
				openButton.setEnabled(true);
				poweroffButton.setEnabled(false);
				poweronButton.setEnabled(false);
				readButton.setEnabled(false);
				protocolbtn.setEnabled(false);
				getFDButton.setEnabled(false);
				buttonAPDU.setEnabled(false);
				textReader.setText("");
			}

			
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		
		mEditTextApdu = (EditText) findViewById(R.id.editTextAPDU);
		textReader = (TextView) findViewById(R.id.textReader);
		readButton = (Button)findViewById(R.id.read_btn);
		readButton.setOnClickListener(listener);
		readButton.setEnabled(false);
		poweronButton = (Button)findViewById(R.id.poweron_btn);
		poweronButton.setOnClickListener(listener);
		poweronButton.setEnabled(false);
		poweroffButton = (Button)findViewById(R.id.poweroff_btn);
		poweroffButton.setOnClickListener(listener);
		poweroffButton.setEnabled(false);
		openButton = (Button)findViewById(R.id.open_btn);
		openButton.setOnClickListener(listener);
		closeButton = (Button)findViewById(R.id.close_btn);
		closeButton.setOnClickListener(listener);
		closeButton.setEnabled(false);
		protocolbtn = (Button) findViewById(R.id.protocol_btn);
		protocolbtn.setOnClickListener(listener);
		protocolbtn.setEnabled(false);
		getFDButton = (Button) findViewById(R.id.get_fd__btn);
		getFDButton.setOnClickListener(listener);
		getFDButton.setEnabled(false);
		buttonAPDU = (Button) findViewById(R.id.buttonAPDU);
		buttonAPDU.setEnabled(false);
		
//		reader = new SmartCardReader(PsamCardActivity.this, SmartCardReader.SLOT_PSAM1);
		reader.register(this,data -> {
			Timber.d("[psam]:"+ JSON.toJSONString(data));
		});
	}

	/**
	 * rk3399pro 客户psam住建部卡片需要先设置频率才能对卡片上电
	 * @param view
	 */
	public void switchHighSpeedMode(View view){
//		if(reader != null){
//			reader.switchHighSpeedMode();
//		}
	}

	/**
	 * rk3399pro psam读卡器复位(设置过住建部卡片频率,其他卡片不能使用,需要对读卡器芯片复位才行)
	 * @return
	 */
	public void resetReader(View view){
//		if("TPS537".equals(internalModel)){
//			PowerUtil.TPS537psamReset(tps537psamSelect);
//		}else{
//			PowerUtil.rk3399proResetReader();
//		}
		
	}
	
	OnClickListener listener = new OnClickListener() {

		
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.open_btn:
//				if (reader.close()) {
//					reader = new SmartCardReader(PsamCardActivity.this, mPasmNum);
//				}
				reader.psam_close(mPasmNum);
				new openTask().execute();
				break;

			case R.id.close_btn:
//				reader.close();
				reader.psam_close(mPasmNum);
				closeButton.setEnabled(false);
				openButton.setEnabled(true);
				poweroffButton.setEnabled(false);
				poweronButton.setEnabled(false);
				readButton.setEnabled(false);
				protocolbtn.setEnabled(false);
				getFDButton.setEnabled(false);
				buttonAPDU.setEnabled(false);
				textReader.setText("");
				break;

			case R.id.read_btn:
//				String atrString = reader.getATRString();
//				textReader.setText("ATR:" + (TextUtils.isEmpty(atrString.trim()) ? "null" : atrString));
				int rc = reader.psam_atr(mPasmNum,mPsamResp);
				textReader.setText("ATR:" + (rc == OK ? mPsamResp.getArt(): "null" ));
				Toast.makeText(PsamCardActivity.this, getString(R.string.get_data_success), Toast.LENGTH_SHORT).show();
				break;

			case R.id.poweron_btn:
//				if (reader.powerOn()) {
				if(reader.psam_powerOn(mPasmNum) == OK){
					poweronButton.setEnabled(false);
					poweroffButton.setEnabled(true);
					readButton.setEnabled(true);
					protocolbtn.setEnabled(true);
					getFDButton.setEnabled(true);
					buttonAPDU.setEnabled(true);
				} else {
					Toast.makeText(PsamCardActivity.this, "ICC power on failed", Toast.LENGTH_SHORT).show();
				}
				
				break;

			case R.id.poweroff_btn:
//				reader.powerOff();
				reader.psam_powerOff(mPasmNum);
				poweroffButton.setEnabled(false);
				poweronButton.setEnabled(true);
				readButton.setEnabled(false);
				protocolbtn.setEnabled(false);
				getFDButton.setEnabled(false);
				buttonAPDU.setEnabled(false);
				break;
			case R.id.protocol_btn:
//				int proto = reader.getProtocol();
//				if (proto == SmartCardReader.PROTOCOL_T0) {
//					textReader.setText("protocol: T0");
//				} else if (proto == SmartCardReader.PROTOCOL_T1) {
//					textReader.setText("protocol: T1");
//				} else {
//					textReader.setText("protocol: NA");
//				}
				if(reader.psam_protect(mPasmNum,mPsamResp) == OK){
					String proto = mPsamResp.getPro();
					if(proto.equals("00")){
						textReader.setText("protocol: T0");
					}else if(proto.equals("01")){
						textReader.setText("protocol: T1");
					}else{
						textReader.setText("protocol: NA");
					}
				}
				Toast.makeText(PsamCardActivity.this, getString(R.string.get_protocol_success), Toast.LENGTH_SHORT).show();
				break;
			case R.id.get_fd__btn:
//				int fd = reader.getFD();
//				textReader.setText(getString(R.string.psam_read_rate) + StringUtil.toHexString(new byte[]{(byte)fd}));
				if(reader.psam_FD(mPasmNum,mPsamResp) == OK){
					textReader.setText(getString(R.string.psam_read_rate) +mPsamResp.getFd());
				}
				break;
			default:
				break;
			}
		}
	};

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	protected void onDestroy()
	{
		super.onDestroy();
	}

	ProgressDialog dialog;
	private class openTask extends AsyncTask<Void, Integer, Boolean>
	{

		
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
//			if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
//				dialog = new ProgressDialog(PsamCardActivity.this);
//				dialog.setMessage("please wait ...");
//				dialog.setCancelable(false);
//				dialog.show();
//			}
		}
		
		
		protected Boolean doInBackground(Void... params)
		{
			return reader.psam_open(mPasmNum) == OK;
//			return reader.open();
		}
		
		
		protected void onPostExecute(Boolean result)
		{
//			if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS360U.ordinal()) {
//				dialog.dismiss();
//			}
			
			if (result)
			{
				openButton.setEnabled(false);
				closeButton.setEnabled(true);
				poweronButton.setEnabled(true);
			}
			else 
			{
				Toast.makeText(PsamCardActivity.this, "Open reader failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	public void sendAPDUkOnClick(View view) {
		byte[] pSendAPDU;
		byte[] result;
		int[] pRevAPDULen = new int[1];
		String apduStr;
		pRevAPDULen[0] = 300;
		apduStr = mEditTextApdu.getText().toString();
		pSendAPDU = toByteArray(apduStr);

		int rc = reader.psam_sendApdu(pSendAPDU,mPasmNum,mPsamResp);
		textReader.setText(rc != OK ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + mPsamResp.getApdu());
		if(rc == OK){
			Toast.makeText(PsamCardActivity.this, getString(R.string.send_comm_success), Toast.LENGTH_SHORT).show();
		}
//		result = reader.transmit(pSendAPDU);
//		textReader.setText(TextUtils.isEmpty(StringUtil.toHexString(result)) ? getString(R.string.send_APDU_fail) : getString(R.string.send_APDU_success) + StringUtil.toHexString(result));
//		if (!TextUtils.isEmpty(StringUtil.toHexString(result))) {
//			Toast.makeText(PsamCardActivity.this, getString(R.string.send_comm_success), Toast.LENGTH_SHORT).show();
//		}
		
	}
	
	private String BCD2Str(byte[] data)
	{
		String string;
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < data.length; i++)
		{
			string = Integer.toHexString(data[i] & 0xFF);
			if (string.length() == 1)
			{
				stringBuilder.append("0");
			}
			
			stringBuilder.append(string.toUpperCase());
			stringBuilder.append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	private byte[] str2BCD(String string)
	{
		int len;
		String str;
		String hexStr = "0123456789ABCDEF";
		
		String s = string.toUpperCase();
		
		len = s.length();
		if ((len % 2) == 1)
		{
			str = s + "0";
			len = (len + 1) >> 1;
		}
		else 
		{
			str = s;
			len >>= 1;
		}
		
		byte[] bytes = new byte[len];
		byte high;
		byte low;
		
		for (int i = 0, j = 0; i < len; i++, j += 2)
		{
			high = (byte)(hexStr.indexOf(str.charAt(j)) << 4);
			low = (byte)hexStr.indexOf(str.charAt(j + 1));
			bytes[i] = (byte)(high | low);
		}
		
		return bytes;
	}

	public static byte[] toByteArray(String hexString) {

		int hexStringLength = hexString.length();
		byte[] byteArray = null;
		int count = 0;
		char c;
		int i;

		// Count number of hex characters
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
				count++;
			}
		}

		byteArray = new byte[(count + 1) / 2];
		boolean first = true;
		int len = 0;
		int value;
		for (i = 0; i < hexStringLength; i++) {

			c = hexString.charAt(i);
			if (c >= '0' && c <= '9') {
				value = c - '0';
			} else if (c >= 'A' && c <= 'F') {
				value = c - 'A' + 10;
			} else if (c >= 'a' && c <= 'f') {
				value = c - 'a' + 10;
			} else {
				value = -1;
			}

			if (value >= 0) {

				if (first) {

					byteArray[len] = (byte) (value << 4);

				} else {

					byteArray[len] |= value;
					len++;
				}

				first = !first;
			}
		}

		return byteArray;
	}
	
}
