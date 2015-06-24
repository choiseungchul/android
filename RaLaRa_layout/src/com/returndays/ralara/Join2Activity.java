package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.UserDto;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class Join2Activity extends BaseActivity {

	UserDto dto;
	//TextView agree1, agree2, agree3;
	CheckBox agr1_btn, agr2_btn;
	Button agreement_contents1, agreement_contents2;
	Button nextbtn;
	HttpDocument mHttpUtil;
	String agree_clause_ver, agree_pri_ver, agree_loc_ver;
	LinearLayout agr_loader;
	int loadCount = 0;
	boolean agr1_checked = false, agr2_checked = false; //agr3_checked = false;
	String user_hp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.join2);

		TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		dto = new UserDto();
		
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
		
		dto.hp_no = user_hp;
		dto.phone_cd = telManager.getDeviceId();
		dto.hp_auth_code = "55555";
		dto.is_fbuser = "0";
		
		// OS버전 입력
		int version = android.os.Build.VERSION.SDK_INT;

		dto.phone_os_gb = MadUtil.getOSVersionString(version);

		mHttpUtil = new HttpDocument(this);

		initUI();
		initData();
	}

	private void initData() {


		agree_clause_ver = "1";
		agree_pri_ver = "1";
		agree_loc_ver = "1";

		//		agree1.setText(R.string.contents1);
		//		agree2.setText(R.string.contents2);
		//		agree3.setText(R.string.contents3);
		//		
		agr_loader.setVisibility(View.INVISIBLE);


		//		if(!MadUtil.isNetworkConnected(getApplicationContext())) {
		/*
			DialogUtil.confirm(Join2Activity.this, R.string.network_disconnect_msg, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		 */
		//		} else {

		// 버전
		Hashtable<String, String> params_agr1 = new Hashtable<String, String>();
		params_agr1.put("policy_gb", "1");
		mHttpUtil.getDocument(UrlDef.GET_POLICY_VER, params_agr1, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				if(document ==null){
					DialogUtil.confirm(Join2Activity.this, R.string.network_disconnect_msg, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
				}else{
					if(document.select("ResultTable").select("Result").text().equals("true")){
						agree_clause_ver = document.select("POLICY_1").text();
						agree_pri_ver  = document.select("POLICY_2").text();
//						agree_loc_ver = document.select("POLICY_3").text();

						LogUtil.D(agree_clause_ver);
						LogUtil.D(agree_pri_ver);
						LogUtil.D(agree_loc_ver);


						agr1_btn.setClickable(true);
						agr2_btn.setClickable(true);
//						agr3_btn.setClickable(true);


						agr_loader.setVisibility(View.INVISIBLE);
					}
				}
			}
		}, true);
		//		}
		/*
		//개인정보
		Map<String, Object> params_agr2 = new HashMap<String, Object>();
		params_agr2.put("policy_gb", "2");
		mHttpUtil2.httpExecute(UrlDef.GET_POLICY, params_agr2, new HttpListener() {

			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {
				// TODO Auto-generated method stub
				if(xml.tag("ResultTable").tag("Result").text().equals("true")){
					agree_pri_ver = xml.tag("VERSION").text();
					agree2.setText(xml.tag("POLICY_CONTENTS").text());
					agr2_btn.setClickable(true);

					// 스크롤 만들기
					int maxLine = xml.tag("POLICY_CONTENTS").text().length() / 20;
					agree2.setMaxLines(maxLine);
					agree2.setVerticalScrollBarEnabled(true);
					agree2.setMovementMethod(new ScrollingMovementMethod());
					loadCount++;
					if(loadCount > 2){
						agr_loader.setVisibility(View.INVISIBLE);
					}
				}
			}
		}, false);

		// 위치정보
		Map<String, Object> params_agr3 = new HashMap<String, Object>();
		params_agr3.put("policy_gb", "3");
		mHttpUtil3.httpExecute(UrlDef.GET_POLICY, params_agr3, new HttpListener() {

			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {
				// TODO Auto-generated method stub
				if(xml.tag("ResultTable").tag("Result").text().equals("true")){
					agree_loc_ver = xml.tag("VERSION").text();
					agree3.setText(xml.tag("POLICY_CONTENTS").text());
					agr3_btn.setClickable(true);

					// 스크롤 만들기
					int maxLine = xml.tag("POLICY_CONTENTS").text().length() / 20;
					agree3.setMaxLines(maxLine);
					agree3.setVerticalScrollBarEnabled(true);
					agree3.setMovementMethod(new ScrollingMovementMethod());

					loadCount++;
					if(loadCount > 2){
						agr_loader.setVisibility(View.INVISIBLE);
					}
				}
			}
		}, false);
		 */
	}

	private void initUI() {

		agreement_contents1 = (Button)findViewById(R.id.agreement_contents1);
		agreement_contents1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String noticeUrl = "http://m.ralara.net/settings/options/agree_ralara.html";
				Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.putExtra("url", noticeUrl);
				startActivity(i);
				//overridePendingTransition(R.anim.leftin, R.anim.leftout);
			}
		});
		agreement_contents2 = (Button)findViewById(R.id.agreement_contents2);
		agreement_contents2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String noticeUrl = "http://m.ralara.net/settings/options/agree_privacy.html";
				Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				i.putExtra("url", noticeUrl);
				startActivity(i);
			}
		});
//		agreement_contents3 = (Button)findViewById(R.id.agreement_contents3);
//		agreement_contents3.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String noticeUrl = "http://m.ralara.net/settings/options/agree_location.html";
//				Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
//				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//				i.putExtra("url", noticeUrl);
//				startActivity(i);
//			}
//		});

		agr_loader = (LinearLayout)findViewById(R.id.agr_loader);

		nextbtn = (Button) findViewById(R.id.agreement_ok_btn);


		agr1_btn = (CheckBox) findViewById(R.id.agree_btn1);
		agr2_btn = (CheckBox) findViewById(R.id.agree_btn2);
//		agr3_btn = (CheckBox) findViewById(R.id.agree_btn3);
		agr1_btn.setClickable(false);
		agr2_btn.setClickable(false);
//		agr3_btn.setClickable(false);


		agr1_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					buttonView.setTextColor(getResources().getColor(R.color.purple));
					agreement_contents1.setBackgroundResource(R.drawable.button_dark);
					agr1_checked = true;
				}else{
					buttonView.setTextColor(getResources().getColor(R.color.gray_3));
					agreement_contents1.setBackgroundResource(R.drawable.button_gray);
					agr1_checked = false;
				}
				checkAgree();
			}
		});

		agr2_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					agreement_contents2.setBackgroundResource(R.drawable.button_dark);
					buttonView.setTextColor(getResources().getColor(R.color.purple));
					agr2_checked = true;
				}else{
					buttonView.setTextColor(getResources().getColor(R.color.gray_3));
					agreement_contents2.setBackgroundResource(R.drawable.button_gray);
					agr2_checked = false;
				}
				checkAgree();
			}
		});

//		agr3_btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				// TODO Auto-generated method stub
//				if (isChecked) {
//					buttonView.setTextColor(getResources().getColor(R.color.purple));
//					agreement_contents3.setBackgroundResource(R.drawable.button_dark);
//					agr3_checked = true;
//				}else{
//					agreement_contents3.setBackgroundResource(R.drawable.button_gray);
//					buttonView.setTextColor(getResources().getColor(R.color.gray_3));
//					agr3_checked = false;
//				}
//				checkAgree();
//			}
//		});

		nextbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dto.use_clause_ver = agree_clause_ver;
				dto.user_info_handle_ver = agree_pri_ver;
//				dto.user_location_handle_ver = agree_loc_ver;

				if(agr1_checked && agr2_checked){

					if(dto.is_fbuser.equals("0")){
						Intent  i = new Intent(getApplicationContext(), Join3Activity.class);
						i.putExtra("data", dto);
						i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						finish();
						overridePendingTransition(0, 0);
						startActivity(i);
					}else if(dto.is_fbuser.equals("1")){
						/*
						Intent  i = new Intent(getApplicationContext(), JoinFBActivity.class);
						i.putExtra("data", dto);
						i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

						finish();
						overridePendingTransition(0, 0);
						startActivity(i);
						*/
					}
				}else{
					Toast.makeText(getApplicationContext(), R.string.agree_required, Toast.LENGTH_LONG).show();
				}

			}

		});
	}

	private void checkAgree(){

		if(agr1_checked && agr2_checked){
			nextbtn.setBackgroundResource(R.drawable.button_dark);
			nextbtn.setClickable(true);
		}else{
			nextbtn.setBackgroundResource(R.drawable.button_gray);
			nextbtn.setClickable(false);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		DialogUtil.confirm(Join2Activity.this, R.string.program_end_msg, new View.OnClickListener() {
			@Override
			public void onClick(View dialog) {
				try{
					DialogUtil.alert.dismiss();
				}catch(Exception e){

				}
				Join2Activity.super.onBackPressed();
			}
		});
	}
}
