package com.returndays.ralara;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.nodes.Document;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.UserDto;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.util.StringUtil;

public class Join1Activity extends BaseActivity{

	EditText mPhoneNum, mAuthCode;
	String mPhoneNumber, mAuthNumber;
	SmsReceiver mReceiver;
	Button mAuthBtn, mReAuthBtn, mJoin, mJoinFB;
	TextView mAuthTimer;
	TimerTask authTimerTask ;
	Timer authTimer;
	Handler timerHandler = new Handler();
	int second = 40;	
	boolean isHpAuth = false;
	Context mContext;
	String CRYPTO_SEED = "ralaraHPAuth";
	int smsSendCount = 0;
	boolean isSMSSuccess = false;
	HttpDocument sendSmsByServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join1);

		sendSmsByServer = new HttpDocument(getApplicationContext());
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		if(telManager.getLine1Number() == null){
			DialogUtil.alert(getApplicationContext(), "개통되지 않은 폰입니다. 서비스를 이용하실 수 없습니다.", new View.OnClickListener(){
				@Override
				public void onClick(View dialog) {
					// TODO Auto-generated method stub
					finish();
					overridePendingTransition(0, 0);
				}
			});

		}else{
			if(telManager.getLine1Number().equals("")){
				DialogUtil.alert(getApplicationContext(), "개통되지 않은 폰입니다. 서비스를 이용하실 수 없습니다.", new View.OnClickListener(){
					@Override
					public void onClick(View dialog) {
						// TODO Auto-generated method stub
						finish();
						overridePendingTransition(0, 0);
					}
				});
			}
		}
		

		InitUI();
		initData();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(99999999);
		mReceiver = new SmsReceiver();
		registerReceiver(mReceiver, intentFilter);

		setTimerTask();
	}
	
	private void setTimerTask(){
		authTimerTask = new TimerTask() {
			@Override
			public final void run() {
				// TODO Auto-generated method stub
				UpdateTimerView();
			}
		};
	}

	private void UpdateTimerView(){
		timerHandler.post(timerRun);
	}

	boolean isFirstSendSms = false;
	
	final Runnable timerRun = new Runnable() {
		@Override
		public void run() {
			if(second == 0){
				if(isHpAuth == false){
					mAuthTimer.setText(getResources().getString(R.string.hp_auth_timer) +" "+ String.valueOf(second) + "초");
					Toast.makeText(getApplicationContext(), "휴대폰 인증을 다시 확인해 주시기 바랍니다.", Toast.LENGTH_LONG).show();
					mAuthBtn.setEnabled(true);
					mAuthTimer.setVisibility(View.INVISIBLE);
					authTimer.cancel();
					authTimer = null;
					second = 41;
					isFirstSendSms = true;
				}
			}else{
				mAuthTimer.setText(getResources().getString(R.string.hp_auth_timer) +" "+ String.valueOf(second) + "초");
			}
			second--;
		}
	};

	private void sendSms() {

		mPhoneNumber = mPhoneNum.getText().toString();
		if(mPhoneNumber.trim().equals("")) {
			DialogUtil.alert(getApplicationContext(), getString(R.string.join1_validate_msg4));
			return;
		}
		mAuthTimer.setVisibility(View.VISIBLE);
		mAuthNumber = getAuthNumber();
		mAuthBtn.setEnabled(false);
		authTimer = new Timer();
		setTimerTask();
		authTimer = new Timer();
		authTimer.schedule(authTimerTask, 0, 1000);
		
		isSMSSuccess = MadUtil.sendSMS(getApplicationContext(), mPhoneNumber, Define.RETURNDAYS_PHONE, String.format(getString(R.string.auth_sms_msg), mAuthNumber));
	}

	private void sendSmsFromServer(){
		
		if(mPhoneNum.getText().toString().equals("")) {
			DialogUtil.alert(getApplicationContext(), getString(R.string.join1_validate_msg4));
			return;
		}
		
		mAuthTimer.setVisibility(View.VISIBLE);
		mAuthNumber = getAuthNumber();
		mAuthBtn.setEnabled(false);
		authTimer = new Timer();
		setTimerTask();
		authTimer.schedule(authTimerTask, 0, 1000);

		LogUtil.D("hp_no = " + MadUtil.getPhoneNumber(getApplicationContext()));
		
		String hpNo = mPhoneNum.getText().toString();
		String content = String.format(getString(R.string.auth_sms_msg), mAuthNumber);
		Hashtable<String, String> params = new Hashtable<String, String>(); 
		params.put("user_hp", hpNo);
		params.put("contents", content);	
		sendSmsByServer.getDocument(UrlDef.SMS_SEND, params, null, new HttpCallBack(){
			@Override
			public void onHttpCallBackListener(Document document) {
			}
		}, false);
	}

	class SmsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				Bundle bundle = intent.getExtras();
				Object messages[] = (Object[])bundle.get("pdus");
				SmsMessage smsMessage[] = new SmsMessage[messages.length];

				int smsPieces = messages.length;
				for(int n=0;n<smsPieces; n++) {
					smsMessage[n] = SmsMessage.createFromPdu((byte[])messages[n]);
				}

				String message = smsMessage[0].getMessageBody().toString();

				LogUtil.D(message);

				if(message.contains(mAuthNumber)) {
					mAuthCode.setText(mAuthNumber);
					authTimer.cancel();
					authTimer = null;
					mAuthTimer.setText(R.string.hp_auth_complete);
					isHpAuth = true;
					unregisterReceiver(mReceiver);
					
					mAuthBtn.setBackgroundResource(R.drawable.button_dark);
					mJoin.setBackgroundResource(R.drawable.button_purple);
					
//					// 다음 액티비티로 넘김
//					Handler h = new Handler();
//					Runnable rr = new Runnable() {
//						@Override
//						public void run() {
//							pressJoin2();
//						}
//					};
//					h.postDelayed(rr, 1500);
				}else{

				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private String getAuthNumber() {
		int number = 1 + (int)(Math.random() * 99999);
		return StringUtil.getLPad(String.valueOf(number), 5, "0");
	}

	void initData() {
		mPhoneNum.setText(MadUtil.getPhoneNumber(getApplicationContext()));
	}

	private void InitUI() {

		mAuthCode = (EditText)findViewById(R.id.hp_authcode);
		mPhoneNum = (EditText)findViewById(R.id.hp_num);
		mAuthBtn = (Button)findViewById(R.id.hp_auth);
		mAuthTimer = (TextView)findViewById(R.id.hp_auth_time);
		mAuthTimer.setVisibility(View.INVISIBLE);
		mJoin = (Button)findViewById(R.id.nm_join_btn);
		mJoinFB = (Button)findViewById(R.id.fb_join_btn);
		mJoinFB.setVisibility(View.INVISIBLE);

		mJoin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pressJoin2();
			}
		});

		mJoinFB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(isHpAuth){
					TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
					
					UserDto ud = new UserDto();
					ud.hp_no = mPhoneNum.getText().toString();
					ud.hp_auth_code = mAuthCode.getText().toString();
					ud.phone_cd = telManager.getDeviceId();
					ud.is_fbuser = "1";

					Intent i = new Intent(getApplicationContext(), Join2Activity.class);
					i.putExtra("data", ud);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

					finish();
					overridePendingTransition(0, 0);
					startActivity(i);
				}
			}
		});

		mAuthBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isFirstSendSms){
//					sendSms();
					sendSmsFromServer();
				}else{
					sendSms();
				}
			}
		});
	}
	
	private void pressJoin2(){
		if(isHpAuth){
			if(mAuthNumber.equals(mAuthCode.getText().toString())){
				TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

				UserDto ud = new UserDto();
				ud.hp_no = mPhoneNum.getText().toString();
				ud.phone_cd = telManager.getDeviceId();
				ud.hp_auth_code = mAuthCode.getText().toString();
				ud.is_fbuser = "0";

				Intent i = new Intent(getApplicationContext(), Join2Activity.class);
				i.putExtra("data", ud);
				i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

				finish();
				overridePendingTransition(0, 0);
				startActivity(i);
			}else{
				Toast.makeText(getApplicationContext(), "인증번호가 틀립니다.", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), "핸드폰 인증 후 가입바랍니다.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {

		DialogUtil.confirm(Join1Activity.this, R.string.alert ,R.string.program_end_msg, new View.OnClickListener() {
			@Override
			public void onClick(View dialog) {
				try{
					DialogUtil.alert.dismiss();
				}catch(Exception e){
				}
				//unregisterReceiver(mReceiver);
				Join1Activity.super.onBackPressed();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		sendSmsByServer.threadStop();
		super.onDestroy();
	}
}