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

import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.MadUtil;

public class FindEmailActivity extends BaseActivity{

	EditText find_id_input_nick, find_id_input_hpno;
	Button find_email_submit, save_btn;
	TextViewNanumGothic top_sub_title_text_sub;
	LinearLayout back_btn;
	String user_hp;
	TextView go_faq_btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_email);

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

	private void alertEmail(String email){

		try {
			StringBuffer sb = new StringBuffer();

			String e1 = email.split("@")[0];
			String e2 = email.split("@")[1];
			int cut_ln = e1.length() / 2 ;

			sb.append(e1.substring(0, cut_ln));

			for(int i = 0 ; i < cut_ln ; i++){
				sb.append("*");
			}
			
			sb.append("@");
			sb.append(e2);

			DialogUtil.alert(FindEmailActivity.this, "회원님의 아이디는\n" + sb.toString() + " 입니다.");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void initUI() {
		go_faq_btn = (TextView)findViewById(R.id.go_faq_btn);
		
		find_id_input_nick = (EditText)findViewById(R.id.find_id_input_nick);
		
		find_id_input_hpno = (EditText)findViewById(R.id.find_id_input_hpno);
		find_id_input_hpno.setText(user_hp);
		find_id_input_hpno.setInputType(0);
		
		find_email_submit = (Button)findViewById(R.id.find_email_submit);
		find_email_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final HttpDocument http = new HttpDocument(getApplicationContext());
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_nick", find_id_input_nick.getText().toString());
				params.put("hp_no", find_id_input_hpno.getText().toString());	
				http.getDocument(UrlDef.FIND_EMAIL, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						if(document.select("Code").text().equals("-2001")){
							DialogUtil.alert(FindEmailActivity.this, "일치하는 정보가 없습니다.");
						}else{
							alertEmail(document.select("USER_EMAIL").text());
						}
						http.threadStop();
					}
				}, false);
			}
		});
		
		go_faq_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), FindUserFaqActivity.class );
				i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(i);
			}
		});

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
		top_sub_title_text_sub.setText("아이디 찾기");
	}
}
