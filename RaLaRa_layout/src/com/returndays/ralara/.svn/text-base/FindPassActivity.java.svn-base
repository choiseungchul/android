package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class FindPassActivity extends BaseActivity{

	Button save_btn, find_pass_submit;
	TextViewNanumGothic top_sub_title_text_sub;
	EditText find_pass_input_id, find_pass_input_nick, find_pass_input_hpno;
	LinearLayout back_btn;
	String imsi_password;
	TextView go_faq_btn;
	String user_hp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_password);

		TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
		try{
			user_hp = MadUtil.getPhoneNumber(getApplicationContext());
			if(user_hp == null){
				user_hp =  telManager.getSubscriberId();
			}
			if(user_hp == null){
				user_hp = "";
			}
		}catch(Exception e){
			user_hp = "";
		}
		
		initUI();
	}

	private void insertPass(){
		imsi_password = MadUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789", 8);
		
		LogUtil.D("임시 비밀번호 = " + imsi_password);
		
		final HttpDocument http = new HttpDocument(getApplicationContext());
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_email", find_pass_input_id.getText().toString());
		params.put("user_nick", find_pass_input_nick.getText().toString());
		params.put("hp_no", find_pass_input_hpno.getText().toString());
		params.put("imsi_password", imsi_password);
		
		http.getDocument(UrlDef.FIND_PASSWORD, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				String Code = document.select("Code").text();
				
				if(Code.equals("0")){
					sendMail();
				}else{
					DialogUtil.alert(FindPassActivity.this, "일치하는 정보가 없습니다");
				}				
				http.threadStop();
			}
		}, false);
	}
	
	private boolean checkInput(){
		if(find_pass_input_id.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
			return false;
		}else{
			if(find_pass_input_nick.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_LONG).show();
				return false;
			}else{
				if(find_pass_input_hpno.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), "휴대폰 번호를 입력해주세요.", Toast.LENGTH_LONG).show();
					return false;
				}else{
					return true;
				}
			}
		}
	}
	
	private void sendMail(){
		final HttpDocument http = new HttpDocument(getApplicationContext());
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_email", find_pass_input_id.getText().toString());
		params.put("mail_title", "[랄라라] 임시 비밀번호가 발급되었습니다.");
		params.put("mail_content", "회원님의 비밀번호가 [ " + imsi_password + " ] 로 변경되었습니다.");
		
		http.getDocument(UrlDef.SEND_EMAIL, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {				
				DialogUtil.alert(FindPassActivity.this, find_pass_input_id.getText().toString() + "으로 임시 비밀번호가 전송되었습니다.", new OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogUtil.alert.dismiss();
						Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
						ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(ii);
						overridePendingTransition(0, 0);
					}
				});
				
				http.threadStop();
				
				
			}
		}, false);
		
	}

	private void initUI() {
		find_pass_submit = (Button)findViewById(R.id.find_pass_submit);
		find_pass_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInput()){
					insertPass();
				}
			}  
		});

		find_pass_input_id = (EditText)findViewById(R.id.find_pass_input_id);
		find_pass_input_nick = (EditText)findViewById(R.id.find_pass_input_nick);
		
		find_pass_input_hpno = (EditText)findViewById(R.id.find_pass_input_hpno);
		find_pass_input_hpno.setText(user_hp);
		find_pass_input_hpno.setInputType(0);

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);
		top_sub_title_text_sub = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text_sub);
		top_sub_title_text_sub.setText("비밀번호 찾기");
		
		go_faq_btn = (TextView)findViewById(R.id.go_faq_btn);
		go_faq_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), FindUserFaqActivity.class );
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		});
	}

}
