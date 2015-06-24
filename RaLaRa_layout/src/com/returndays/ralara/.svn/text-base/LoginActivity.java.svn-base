package com.returndays.ralara;

import java.io.IOException;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.example.android.bitmapfun.util.AsyncTask;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.crypto.SimpleCrypto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.util.StringUtil;

public class LoginActivity extends BaseActivity{
	
	EditText mEmailInput, mPassInput;
	Button mLoginBtn, mJoinBtn, find_id_btn, find_pass_btn;
	HttpDocument mHttpUtil;
	CheckBox mAutoLogin;
	boolean isAutoLogin = true;
	AsyncTask<Void, Integer, String> Task;
	String user_hp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		LogUtil.D("hp_no = " + telManager.getLine1Number());
		try{
			user_hp = MadUtil.getPhoneNumber(getApplicationContext());
			if(user_hp == null){
				user_hp =  telManager.getSubscriberId();
			}
			if(user_hp == null){
				String ran = MadUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789",13);
				user_hp = ran;
			}
		}catch(Exception e){
			String ran = MadUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789",13);
			user_hp = ran;
		}
		
		InitUI();
		
		mHttpUtil = new HttpDocument(this);
		gcmUpdate = new HttpDocument(getApplicationContext());
		
		LogUtil.D("email = " + Setting.getEmail(getApplicationContext()));
		
		if(!Setting.getEmail(getApplicationContext()).equals("")){
			mEmailInput.setText(Setting.getEmail(getApplicationContext()));
		}
	}

	private void InitUI() {
		
		find_id_btn = (Button)findViewById(R.id.find_id_btn);
		find_id_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSingTopActivity(FindEmailActivity.class);
				overridePendingTransition(0, 0);
			}
		});
		
		find_pass_btn = (Button)findViewById(R.id.find_pass_btn);
		find_pass_btn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startSingTopActivity(FindPassActivity.class);
				overridePendingTransition(0, 0);
			}
		});

		mEmailInput = (EditText)findViewById(R.id.login_idinput);	
		mPassInput = (EditText)findViewById(R.id.login_passinput);
		mLoginBtn = (Button)findViewById(R.id.login_btn);
		
		mAutoLogin = (CheckBox)findViewById(R.id.login_setAuto);
		
		
		mJoinBtn = (Button)findViewById(R.id.join_btn);
		
		mAutoLogin.setChecked(true);
		mAutoLogin.setTextColor(getResources().getColor(R.color.purple));
		mAutoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					mAutoLogin.setTextColor(getResources().getColor(R.color.purple));
					isAutoLogin = true;
				}else{
					mAutoLogin.setTextColor(getResources().getColor(R.color.gray_3));
					isAutoLogin = false;
				}
			}
		});
		
		mLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				
				if(mEmailInput.getText().toString().equals("") ){
					DialogUtil.alert(LoginActivity.this, "이메일을 입력해주세요.");
					return;
				}
				if(mPassInput.getText().toString().equals("")){
					DialogUtil.alert(LoginActivity.this, "비밀번호를 입력해주세요.");
					return;
				}
				if(!StringUtil.checkEmail( mEmailInput.getText().toString())){
					DialogUtil.alert(LoginActivity.this, "아이디가 올바르지 않습니다.");
					return;
				}
				
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_email", mEmailInput.getText().toString());
				params.put("user_pwd", mPassInput.getText().toString());
				params.put("hp_no", user_hp);
				params.put("phone_cd", telManager.getDeviceId());
				int version = android.os.Build.VERSION.SDK_INT;
				params.put("os_version" , MadUtil.getOSVersionString(version));
				
//				LogUtil.D(mEmailInput.getText().toString());
//				LogUtil.D(mPassInput.getText().toString());
//				LogUtil.D(user_hp);
//				LogUtil.D(telManager.getDeviceId());
//				LogUtil.D("os_version" , MadUtil.getOSVersionString(version));

				mHttpUtil.getDocument(UrlDef.LOGIN_NOTK3, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {

						LogUtil.D(document.toString());
						
						if(document.select("ReturnTable").select("Result").text().equals("true")){
							
							LogUtil.D(document.select("USER_SEQ").text());
							LogUtil.D(document.select("TOKEN_KEY").text());
							LogUtil.D(document.select("USER_EMAIL").text());
							
							Setting.setUserSeq(getApplicationContext(), document.select("USER_SEQ").text());
							Setting.setToken(getApplicationContext(), document.select("TOKEN_KEY").text());
							Setting.setEmail(getApplicationContext(), document.select("USER_EMAIL").text());
							Setting.setCallAd(getApplicationContext(), "ON");
							Setting.setSlideAd(getApplicationContext(), "ON");
							try {
								Setting.setPassword(getApplicationContext(), SimpleCrypto.encrypt(Define.CRYPTO_KEY, mPassInput.getText().toString()) );
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Setting.setPushAlarm(getApplicationContext(), "Y");
							Setting.setPushMessageAlarm(getApplicationContext(), "Y");
							Setting.setPushScreenOff(getApplicationContext(), "Y");
							Setting.setAlarmSound(getApplicationContext(), "alarm_idx_03");
							Setting.setLogoSound(getApplicationContext(), "logosong1");
							
							if(mAutoLogin.isChecked()){
								Setting.setAutoLogin(getApplicationContext(), "Y");
							}else{
								Setting.setAutoLogin(getApplicationContext(), "N");
							}
							
							registerInBackground();
							
							
						}else{
							DialogUtil.alert(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.");
							mEmailInput.requestFocus();
						}
					}
				}, true);
			}
		});
		
		mJoinBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(0, 0);
				startSingTopActivity(Join2Activity.class);
			}
		});
		
		mEmailInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		/////////////////////////////////////////////
		mPassInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 12 ){
					
				}
			}
		});		
	}
	
	GoogleCloudMessaging gcm;
	HttpDocument gcmUpdate;

	private void registerInBackground() {

		LogUtil.D("registerInBackground called");

		Task =new AsyncTask<Void, Integer, String>() {
			protected String doInBackground(Void...  arg0) {
				// TODO Auto-generated method stub
				LogUtil.D("doInBackground called");

				String msg = "";
				String regid = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
					}
					regid = gcm.register(Define.GCM_PROJECT_ID);
					msg = "Device registered, registration ID=" + regid;

					Setting.setGcmCode(getApplicationContext(), regid);

					LogUtil.D("msg => " + msg);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return regid;
			}
			
			protected void onPostExecute(String result) {
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
				params.put("google_cd", result);
				gcmUpdate.getDocument(UrlDef.GOOGLE_CODE_EXCHANGE, params, null, new HttpCallBack() {

					@Override
					public void onHttpCallBackListener(Document document) {
						// TODO Auto-generated method stub
						if(document.select("ReturnTable").select("Result").text().equals("true")){
							LogUtil.D("RegId is UPDATED");
							
						}
						gcmUpdate.threadStop();
						
						finish();
						overridePendingTransition(0, 0);
						startSingTopActivity(AdlistActivity.class);
					}
				}, false);
			};
		}.execute(null, null, null);
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		DialogUtil.confirm(LoginActivity.this, R.string.program_end_msg, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogUtil.alert.dismiss();
				LoginActivity.super.onBackPressed();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
//		try{
//			if(Task != null){
//				if(!Task.isCancelled()){
//					Task.cancel(true);
//				}
//			}	
//		}catch(Exception e){
//		}
		
		super.onDestroy();
	}
}
