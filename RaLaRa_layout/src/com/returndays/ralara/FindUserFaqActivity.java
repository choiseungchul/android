package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.MadUtil;

public class FindUserFaqActivity extends BaseActivity{

	EditText mFaqTitle, mFaqContent, faq_email;
	Button mSendbtn;
	LinearLayout back_btn;
	Button save_btn;
	HttpDocument mHttpUtil;
	String user_hp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_user_faq_write);
		
		mHttpUtil = new HttpDocument(getApplicationContext());
		
		TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
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
		
		initUI();
	}
	
	private void saveContent() {
		if(checkInput()){
			Hashtable<String, String> params = new Hashtable<String, String>();
			String user_seq = Setting.getUserSeq(getApplicationContext());
			if(user_seq.equals("")){
				user_seq = "88888888-8888-8888-8888-888888888888";
			}
			params.put("user_seq", user_seq);
			params.put("hp_no", user_hp);
			params.put("user_email", faq_email.getText().toString());
			params.put("faq_gubun", "1");
			params.put("faq_title", mFaqTitle.getText().toString());
			params.put("faq_contents", mFaqContent.getText().toString());
			
			mHttpUtil.getDocument(UrlDef.FAQ_WRITE, params, null,  new HttpCallBack() {
				@Override
				public void onHttpCallBackListener(Document document) {
					// TODO Auto-generated method stub
					if(document.select("ResultTable").select("Result").text().equals("true")){
						Toast.makeText(getApplicationContext(), "1:1 문의하기가 완료되었습니다.", Toast.LENGTH_LONG).show();
						onBackPressed();
					}
				}
			}, false);
		}
	}

	private void initUI() {

		faq_email = (EditText)findViewById(R.id.faq_email);
		
		mFaqTitle = (EditText)findViewById(R.id.faq_title);
		mFaqContent = (EditText)findViewById(R.id.faq_content);
//		mSendbtn = (Button)findViewById(R.id.faq_btn);
//		mSendbtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				saveContent();
//			}
//		});
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("문의하기");
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveContent();
			}
		});
	}
	
	private boolean checkInput() {
		if(mFaqTitle.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_LONG).show();
			return false;
		}else{
			if(mFaqContent.getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_LONG).show();
				return false;
			}else{
				if(faq_email.getText().toString().equals("")){
					Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
					return false;
				}
				return true;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		super.onDestroy();
		
	}
	
}
