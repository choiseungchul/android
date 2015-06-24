package com.returndays.ralara;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.android.bitmapfun.util.AsyncTask;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.crypto.SimpleCrypto;
import com.returndays.ralara.dto.UserDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.StringUtil;

public class Join3Activity extends BaseActivity{

	Button mSex, mSido, mGugun, mEmailList;
	TextViewNanumGothic mJoinSubmit;
	EditText mEmailInput1, mEmailInput2,
	mInputNick, mInputPass;
	TextView mNickCheck, mBirthInput, email_check_text;
	int sexSelectedIndex = -1, is_fbuser = -1;
	String mSexStr, mSidoStr, mGugunStr;
	boolean passCheck;
	UserDto dto;
	HttpDocument mHttpUtil, mHttpUtil2, mHttpUtil3, mHttpUtil4;
	String[] gugunList;
	GoogleCloudMessaging gcm;
	String gcmRegId;
	int yy = 1980, mm = 1, dd = 1;
	String BirthStr = "";
	boolean isSidoClicked = false;
	AsyncTask<String, String, Object> Task;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join3);

		dto = (UserDto) getIntent().getSerializableExtra("data");
		mHttpUtil = new HttpDocument(this);
		mHttpUtil2 = new HttpDocument(getApplicationContext());
		mHttpUtil3 = new HttpDocument(getApplicationContext());
		mHttpUtil4 = new HttpDocument(this);

		initUI();

		LogUtil.D(dto.toString());

		if(Setting.getGcmCode(getApplicationContext()).equals("")){
			// GCM 코드 등록
			gcm = GoogleCloudMessaging.getInstance(this);

			registerBackground();
		}
		
		try{
			AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
			Account[] list = manager.getAccounts();
			if(list.length > 0){
				LogUtil.D("account = " + list[0].name);
				String input1 = list[0].name.split("@")[0];
				String input2 = list[0].name.split("@")[1];
				
				mEmailInput1.setText(input1);
				mEmailInput2.setText(input2);
			}
		}catch(Exception e){
			LogUtil.D(e.toString());
		}
	}

	void initUI(){

		email_check_text = (TextView)findViewById(R.id.email_check_text);
		
		mBirthInput = (TextView)findViewById(R.id.input_birthday);
		mBirthInput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog dp = new DatePickerDialog(Join3Activity.this, new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						// TODO Auto-generated method stub
						String month = "";
						String day = "";
						if(monthOfYear < 9){
							month = "0" + (monthOfYear + 1);
						}else{
							month = String.valueOf( monthOfYear + 1 );
						}
						if(dayOfMonth < 10){
							day = "0" + dayOfMonth;
						}else{
							day = String.valueOf(dayOfMonth);
						}
						mBirthInput.setText(year + "년 " + month + "월 " + day + "일");
						mBirthInput.setBackgroundResource(R.drawable.button_dark);
						BirthStr = year + "-" + month + "-" + day;
						yy = year;
						mm = monthOfYear + 1;
						dd = dayOfMonth;
					}
				} 
				,yy
				, mm - 1
				, dd);

				dp.setTitle("생년월일을 설정하세요.");
				dp.show();
			}
		});

		//성별
		mSex = (Button)findViewById(R.id.input_sex);

		mEmailInput1 = (EditText)findViewById(R.id.e_mail_input1);
		mEmailInput1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(!mEmailInput2.getText().toString().equals("") && !mEmailInput1.getText().toString().equals("")){
					if(mEmailInput1.getText().toString().length() > 2){
						sendCheckEmail(mEmailInput1.getText().toString() + "@" + mEmailInput2.getText().toString() );
					}else{
						email_check_text.setText("이메일 앞자리는 3글자 이상 넣어주세요.");
						email_check_text.setTextColor(getResources().getColor(R.color.red));
					}
				}
			}
		});
		mEmailInput2 = (EditText)findViewById(R.id.e_mail_input2);
		mEmailInput2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(!mEmailInput2.getText().toString().equals("") && !mEmailInput1.getText().toString().equals("")){					
					if(mEmailInput1.getText().toString().length() > 2){
						sendCheckEmail(mEmailInput1.getText().toString() + "@" + mEmailInput2.getText().toString() );
					}else{
						email_check_text.setText("이메일 앞자리는 3글자 이상 넣어주세요.");
						email_check_text.setTextColor(getResources().getColor(R.color.red));
					}
				}
			}
		});

		mInputPass = (EditText)findViewById(R.id.input_pass);
		mInputPass.setFilters(new InputFilter[] {filterAlpha, new InputFilter.LengthFilter(12)});
		mInputPass.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int count = s.toString().length();
				if(count >5 && count < 13){
					LogUtil.D(count + "");
					passCheck = true;
				}else{
					passCheck = false;
				}
			}
		});

		mNickCheck = (TextView)findViewById(R.id.nick_check);

		mEmailList = (Button)findViewById(R.id.email_list);

		mJoinSubmit = (TextViewNanumGothic)findViewById(R.id.join_submit);
		mJoinSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String emailAddr1 = mEmailInput1.getText().toString();
				String emailAddr2 = mEmailInput2.getText().toString();
				String passWord = mInputPass.getText().toString();
				String nickName = mInputNick.getText().toString();

				if(InputValidate()){
					dto.email = emailAddr1 + "@" + emailAddr2;
					dto.passwd = passWord;
					dto.nickname = nickName;
					dto.birthday = BirthStr;
					dto.sido = mSidoStr;
					dto.gugun = mGugunStr;
					dto.is_fbuser = "N";
					if(mSexStr.equals("남자")){
						dto.sex = "M";
					}else if(mSexStr.equals("여자")){
						dto.sex = "F";
					}
					if(dto.google_code == null){
						dto.google_code = Setting.getGcmCode(getApplicationContext());

					}else{
						if(dto.google_code.equals("")){
							dto.google_code = Setting.getGcmCode(getApplicationContext());
						}
					}

					if(dto.google_code.equals("")){
						dto.google_code = "NOT REGSTERED";
					}

					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_email", dto.email);
					params.put("user_pwd", dto.passwd);
					params.put("user_nickname", dto.nickname);
					params.put("sex", dto.sex);
					params.put("user_brithday", dto.birthday);
					params.put("hp_no", dto.hp_no);
					params.put("use_clause_ver", dto.use_clause_ver);
					params.put("user_info_handle_ver", dto.user_info_handle_ver);
					params.put("user_location_handle", "0");
//					params.put("user_location_handle", dto.user_location_handle_ver);
					params.put("phone_os_ver", dto.phone_os_gb);
					params.put("phone_cd", dto.phone_cd);
					params.put("google_cd", dto.google_code);
					params.put("sido", dto.sido);
					params.put("gugun", dto.gugun);
					params.put("is_fbusr", dto.is_fbuser);
					
					LogUtil.D("phone cd = " + dto.phone_cd);

					mHttpUtil.getDocument(UrlDef.JOIN, params, null,  new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							// TODO Auto-generated method stub
							LogUtil.W(document.toString());
							if(document.select("ReturnTable").select("Code").text().equals("0")){
								try {
									Setting.setPassword(getApplicationContext(), SimpleCrypto.encrypt(Define.CRYPTO_KEY, dto.passwd));
								} catch (Exception e) {
									e.printStackTrace();
								}
								Setting.setEmail(getApplicationContext(), dto.email);
								Hashtable<String, String> params = new Hashtable<String, String>();
								params.put("user_emial", dto.email);

								mHttpUtil.getDocument(UrlDef.USERINFO_EMAIL, params, null,  new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										if(document.select("ResultTable").select("Result").text().equals("true")){
											if(!document.select("ReturnTable").select("USER_SEQ").text().equals("")){
												Setting.setUserSeq(getApplicationContext(), document.select("USER_SEQ").text());
												Setting.setToken(getApplicationContext(), document.select("TOKEN_KEY").text());
												Setting.setAutoLogin(getApplicationContext(), "Y");
												Setting.setCallAd(getApplicationContext(), "ON");
												Setting.setSlideAd(getApplicationContext(), "ON");
												Setting.setPushAlarm(getApplicationContext(), "Y");
												Setting.setPushMessageAlarm(getApplicationContext(), "Y");
												Setting.setPushScreenOff(getApplicationContext(), "Y");
												Setting.setAlarmSound(getApplicationContext(), "alarm_idx_03");
												Setting.setLogoSound(getApplicationContext(), "logosong1");
												Toast.makeText(getApplicationContext(), R.string.userinfo_success, Toast.LENGTH_LONG).show();
												
												// 나이대 저장
												String birthday = document.select("BIRTHDAY").text();
												
												DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
												
												try {
													long user_birth =  sdFormat.parse(birthday.split("T")[0]).getTime();
													
													if(user_birth < sdFormat.parse("2005-01-01").getTime() && user_birth >=  sdFormat.parse("1995-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "1");
													}else if(user_birth < sdFormat.parse("1995-01-01").getTime() && user_birth >=  sdFormat.parse("1985-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "2");
													}else if(user_birth < sdFormat.parse("1985-01-01").getTime() && user_birth >=  sdFormat.parse("1975-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "3");
													}else if(user_birth < sdFormat.parse("1975-01-01").getTime() && user_birth >=  sdFormat.parse("1965-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "4");
													}else if(user_birth < sdFormat.parse("1965-01-01").getTime() && user_birth >=  sdFormat.parse("1955-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "5");
													}else if(user_birth < sdFormat.parse("1955-01-01").getTime() && user_birth >=  sdFormat.parse("1945-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "6");
													}else if(user_birth < sdFormat.parse("1945-01-01").getTime() && user_birth >=  sdFormat.parse("1935-01-01").getTime() ){
														Setting.setUserAges(getApplicationContext(), "7");
													}
													
												} catch (ParseException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												
												Intent ii = new Intent(getApplicationContext(), TutorialActivity.class);
												ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												ii.putExtra("act","adlist");
												startActivity(ii);
												
												finish();
												overridePendingTransition(0, 0);
											}
										}else{
											Toast.makeText(getApplicationContext(), R.string.userinfo_failed, Toast.LENGTH_LONG).show();
										}
									}
								}, false);

							}else if(document.select("ReturnTable").select("Code").text().equals("-2001")){
								// 실패
								Toast.makeText(getApplicationContext(), R.string.already_Join, Toast.LENGTH_LONG).show();

							}else if(document.select("ReturnTable").select("Code").text().equals("-2005")){
								// 실패
								Toast.makeText(getApplicationContext(), R.string.already_Join_abuse, Toast.LENGTH_LONG).show();

							}else if(document.select("ReturnTable").select("Code").text().equals("-2006")){
								// 실패
								Toast.makeText(getApplicationContext(), R.string.already_Join_abuse2, Toast.LENGTH_LONG).show();
							}
							else{
								Toast.makeText(getApplicationContext(), R.string.join_failed, Toast.LENGTH_LONG).show();
							}
						}

					}, true);

				}
			}
		});

		mEmailList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String[] emails = getResources().getStringArray(R.array.email_types);

				new AlertDialog.Builder(Join3Activity.this)
				.setItems( emails, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mEmailInput2.setText(emails[which]);
					}
				}).create().show();
			}
		});

		mSido = (Button)findViewById(R.id.sido);
		mSido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String[] sidoList = getResources().getStringArray(R.array.sido_type);

				new AlertDialog.Builder(Join3Activity.this)
				.setItems(sidoList, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSido.setText(sidoList[which]);
						mSidoStr = sidoList[which];
						
						isSidoClicked = true;

						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("area_depth", "2");
						params.put("sido", mSidoStr);

						mGugun.setEnabled(false);

						mHttpUtil2.getDocument(UrlDef.GET_AREA, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {

								if(!document.select("ReturnTable").isEmpty()){
									Elements list = document.select("ReturnTable");
									gugunList =  new String[list.size()];

									for(int i = 0 ; i < list.size(); i++){
										Element item = list.get(i);
										gugunList[i] = item.select("GUGUN").text();
									}

									mGugun.setText(gugunList[0]);
									mGugunStr = gugunList[0];

									mSido.setBackgroundResource(R.drawable.button_dark);
									mGugun.setEnabled(true);
									mGugun.setBackgroundResource(R.drawable.button_gray);
								}
							}
						}, false);

					}

				}).create().show();
			}
		});

		mGugun = (Button)findViewById(R.id.gugun);
		mGugun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String[] gugun = gugunList;
				
				if(isSidoClicked == false){
					Toast.makeText(getApplicationContext(), "시/도 를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(gugun.length == 0){
					final HttpDocument get_gugun = new HttpDocument(getApplicationContext());
					
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("area_depth", "2");
					params.put("sido", mGugun.getText().toString());
					
					get_gugun.getDocument(UrlDef.GET_AREA, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {

							if(!document.select("ReturnTable").isEmpty()){
								Elements list = document.select("ReturnTable");
								gugunList =  new String[list.size()];

								for(int i = 0 ; i < list.size(); i++){
									Element item = list.get(i);
									gugunList[i] = item.select("GUGUN").text();
								}

								mGugun.setText(gugunList[0]);
								mGugunStr = gugunList[0];

								mGugun.setEnabled(true);
								mGugun.setBackgroundResource(R.drawable.button_gray);
								
								new AlertDialog.Builder(Join3Activity.this)
								.setItems(gugun, new DialogInterface.OnClickListener(){
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mGugun.setText(gugun[which]);
										mGugunStr = gugun[which];

										mGugun.setBackgroundResource(R.drawable.button_dark);
									}

								}).create().show();
							}
							get_gugun.threadStop();
						}
					}, false);
				}else{
					new AlertDialog.Builder(Join3Activity.this)
					.setItems(gugun, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mGugun.setText(gugun[which]);
							mGugunStr = gugun[which];

							mGugun.setBackgroundResource(R.drawable.button_dark);
						}

					}).create().show();
				}
			}
		});

		mInputNick = (EditText)findViewById(R.id.input_nick);
		mInputNick.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!hasFocus){
					sendNickCheck();
				}else{

				}
			}
		});

		mInputNick.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((actionId == EditorInfo.IME_ACTION_DONE) ||
						(event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					sendNickCheck();
				}  
				return false;
			}
		});
		
		mInputNick.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.toString().length() < 2 ||s.toString().length() > 9){
					mNickCheck.setVisibility(View.VISIBLE);
					mNickCheck.setText(R.string.join3_nick_notcorrect);
					mNickCheck.setTextColor(getResources().getColor(R.color.red));
				}else{
//					mNickCheck.setVisibility(View.INVISIBLE);
					sendNickCheck();
				}
			}
		});

		mSex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String[] sexList = getResources().getStringArray(R.array.sex);

				new AlertDialog.Builder(Join3Activity.this)
				.setItems(sexList, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSex.setText(sexList[which]);
						mSexStr = sexList[which];

						mSex.setBackgroundResource(R.drawable.button_dark);

					}

				}).create().show();
			}
		});
	}

	private void sendNickCheck(){
		// 서버로 전송
		if(!mInputNick.getText().toString().equals("")){
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("nickname", mInputNick.getText().toString());
			mHttpUtil3.setRequestRepeat(true);
			mHttpUtil3.getDocument(UrlDef.NICK_CHECK, params, null, new HttpCallBack() {
				@Override
				public void onHttpCallBackListener(Document document) {
					LogUtil.W(document.toString());

					if(document.select("ResultTable").select("Result").text().equals("true")){
						String rs = document.select("ReturnTable").select("Result").text();
						if(rs.equals("Y")){
							mNickCheck.setVisibility(View.VISIBLE);
							mNickCheck.setText(R.string.join3_nick_correct);
							mNickCheck.setTextColor(getResources().getColor(R.color.purple));
						}else if(rs.equals("N")){

							mNickCheck.setVisibility(View.VISIBLE);
							mNickCheck.setText(R.string.join3_nick_exist);
							mNickCheck.setTextColor(getResources().getColor(R.color.red));
						}
					}
				}
			}, false);
		}
	}

	private boolean InputValidate() {

		String emailAddr1 = mEmailInput1.getText().toString();
		String emailAddr2 = mEmailInput2.getText().toString();
		String nickName = mInputNick.getText().toString();
		String sex = mSexStr == null ? "" : mSexStr;
		String sido = mSidoStr == null ? "" : mSidoStr;
		String gugun = mGugunStr == null ? "" : mGugunStr;

		if(emailAddr1.equals("") || emailAddr2.equals("")){

			Toast.makeText(getApplicationContext(), R.string.join3_email_isempty, Toast.LENGTH_LONG).show();
			mEmailInput1.requestFocus();
			return false;
		}else{
			boolean isCorrectEmail = StringUtil.checkEmail(emailAddr1 + "@" + emailAddr2);
			if(isCorrectEmail){
				if(passCheck){
					if(nickName.equals("")){
						Toast.makeText(getApplicationContext(), R.string.join3_nick_notcorrect, Toast.LENGTH_LONG).show();
						mInputNick.requestFocus();
						return false;
					}else{

						if(BirthStr.equals("")){
							Toast.makeText(getApplicationContext(), R.string.join3_birthday_notcorrect, Toast.LENGTH_LONG).show();
							return false;
						}else{
							if(sex.equals("")){
								Toast.makeText(getApplicationContext(), R.string.join3_sex_notcorrect, Toast.LENGTH_LONG).show();
								return false;
							}else{

								if(sido.equals("")){
									Toast.makeText(getApplicationContext(), R.string.join3_sido_notcorrect, Toast.LENGTH_LONG).show();
									return false;	
								}else{
									if(gugun.equals("")){
										Toast.makeText(getApplicationContext(), R.string.join3_gugun_notcorrect, Toast.LENGTH_LONG).show();
										return false;
									}else{

										return true;
									}
								}
							}
						}

					}
				}else{
					Toast.makeText(getApplicationContext(), R.string.join3_pass_notcorrect, Toast.LENGTH_LONG).show();
					return false;
				}
			}else{
				Toast.makeText(getApplicationContext(), R.string.join3_email_notcorrect, Toast.LENGTH_LONG).show();
				return false;
			}

		}


	}

	private void registerBackground() {
		Task = new AsyncTask<String, String, Object>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... params) {
				String msg = "";
				LogUtil.D("GCM REGSTER...");

				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
				}

				try{
					gcmRegId = gcm.register(Define.GCM_PROJECT_ID);
				}catch(Exception e){
					e.printStackTrace();
				}

				LogUtil.D("AFTER GCM REGSTER...");

				msg = "Device registered, registration id=" + gcmRegId;

				if(gcmRegId != null){
					if(!gcmRegId.equals("")){
						Setting.setGcmCode(getApplicationContext(), gcmRegId);
						dto.google_code = gcmRegId;
					}
				}
				return msg;
			}

			@Override
			protected void onPostExecute(Object msg) {
				super.onPostExecute(msg);
				if(msg != null){
					Log.d("ASYNC", "msg = " + msg.toString());
				}
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
			}

		}.execute(null, null, null);
	}

	private void sendCheckEmail(String email){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("u_email", email);
		LogUtil.D("u_email = " + email);
		
		email_check_text.setText("이메일 중복확인중...");
		email_check_text.setTextColor(getResources().getColor(R.color.purple));
		mHttpUtil4.setRequestRepeat(true);
		mHttpUtil4.getDocument(UrlDef.JOIN_CHK_EMAIL, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				
				String CNT = document.select("ReturnTable").select("CNT").text();
				LogUtil.D("Email cnt = " + CNT);
				
				if(document.select("ResultTable").select("Result").text().equals("true")){
					if(CNT.equals("0")){
						email_check_text.setText("* 사용가능한 이메일입니다.");
						email_check_text.setTextColor(getResources().getColor(R.color.purple));
					}else{
						email_check_text.setText("* 중복된 이메일입니다.");
						email_check_text.setTextColor(getResources().getColor(R.color.red));
					}
				}
			}
		}, false);
		
	}
	
	// 영문 숫자 허용
	protected InputFilter filterAlpha = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if(!ps.matcher(source).matches()){
				return "";
			}
			return null;
		}
	};
	

	// 한글만 허용
	protected InputFilter filterHangle = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			Pattern ps = Pattern.compile("^[ㄱ-가-힣]+$");
			if(!ps.matcher(source).matches()){
				return "";
			}
			return null;
		}
	};

	@Override
	public void onBackPressed() {
		DialogUtil.confirm(Join3Activity.this, R.string.program_end_msg, new View.OnClickListener() {
			@Override
			public void onClick(View dialog) {
				try{
				DialogUtil.alert.dismiss();
				}catch(Exception e){
					
				}
				Join3Activity.super.onBackPressed();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		mHttpUtil2.threadStop();
		mHttpUtil3.threadStop();
		mHttpUtil4.threadStop();
		
//		if(Task != null){
//			if(!Task.isCancelled()){
//				Task.cancel(true);
//			}
//		}
		
		super.onDestroy();
	}
}