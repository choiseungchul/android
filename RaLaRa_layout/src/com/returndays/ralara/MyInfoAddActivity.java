package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.os.Bundle;
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

public class MyInfoAddActivity extends BaseActivity{

	EditText transfer_addr, hp_no;
	Button save_btn;
	HttpDocument mHttpUtil_myprofile;
	LinearLayout sub_back_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfoadd);
		
		mHttpUtil_myprofile = new HttpDocument(getApplicationContext());
		
		initUI();
		initData();
		
	}

	private void initData() {
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		
		mHttpUtil_myprofile.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
			
			@Override
			public void onHttpCallBackListener(Document document) {
				
				if(document.select("ResultTable").select("Result").text().equals("true")){
					if(!document.select("ReturnTable").select("USER_SEQ").text().equals("")){
						
						String ADDRESS_HEAD = document.select("ADDRESS_HEAD").text();
						String USER_HP_NO = document.select("USER_HP_NO").text();
						
						transfer_addr.setText(ADDRESS_HEAD);
						hp_no.setText(USER_HP_NO);
					}
				}
			}
		}, false);
	}

	private void initUI() {
		transfer_addr = (EditText)findViewById(R.id.transfer_addr);
		hp_no = (EditText)findViewById(R.id.hp_no);
		save_btn = (Button)findViewById(R.id.save_btn);
		sub_back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		sub_back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInput()){
					final HttpDocument send = new HttpDocument(MyInfoAddActivity.this);
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("hp_no", hp_no.getText().toString());
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("token", Setting.getToken(getApplicationContext()));
					params.put("address", transfer_addr.getText().toString());
					
					send.getDocument(UrlDef.USER_EDIT_ADD, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							if(document.select("ResultTable").select("Result").text().equals("true")){
								String USER_HP_NO = document.select("USER_HP_NO").text();
								String ADDRESS_HEAD = document.select("ADDRESS_HEAD").text();
								
								hp_no.setText(USER_HP_NO);
								transfer_addr.setText(ADDRESS_HEAD);
								
							}else{
								Toast.makeText(getApplicationContext(), "정보가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
							}
							
							send.threadStop();
						}
					}, true);
				}else{
					Toast.makeText(getApplicationContext(), "배송지 또는 휴대폰번호가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	private boolean checkInput(){
		if(transfer_addr.getText().toString().equals("")){
			return false;
		}else{
			if(hp_no.getText().toString().equals("")){
				return false;
			}else{
				return true;
			}
		}
	}
	
}
