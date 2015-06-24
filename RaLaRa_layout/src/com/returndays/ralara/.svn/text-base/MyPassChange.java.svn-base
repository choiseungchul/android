package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.http.HttpDocument;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.crypto.SimpleCrypto;
import com.returndays.ralara.preference.Setting;

public class MyPassChange extends BaseActivity{

	LinearLayout back_btn;
	EditText curr_pass, new_pass, new_pass_re;
	TextView exchange_pass_hint;
	String mCurrpass, mNewpass, mNewpass_RE;
	Button save_btn;
	HttpDocument mhttp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypass_change);

		mhttp = new HttpDocument(this);
		
		initUI();
		initData();
	}

	private void initData() {

	}

	private void initUI() {
		
		exchange_pass_hint = (TextView)findViewById(R.id.exchange_pass_hint);
		
		save_btn = (Button)findViewById(R.id.save_btn);
		
		curr_pass = (EditText)findViewById(R.id.curr_pass);
		curr_pass.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				mCurrpass = s.toString();
			}
		});
		new_pass = (EditText)findViewById(R.id.new_pass);
		new_pass.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				mNewpass = s.toString();
			}
		});
		new_pass_re = (EditText)findViewById(R.id.new_pass_re);
		new_pass_re.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				mNewpass_RE = s.toString();
			}
		});
		
		save_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				exchange_pass_hint.setText("");
				
				if(mNewpass.equals(mNewpass_RE) && ( mNewpass.length() > 5 && mNewpass.length() < 13 ) ){
					
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("token", Setting.getToken(getApplicationContext()));
					params.put("user_password", mCurrpass);
					params.put("new_password", mNewpass_RE);
					
					mhttp.getDocument(UrlDef.USER_PASS_CHANGE, params, null, new HttpDocument.HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							// TODO Auto-generated method stub
							if(document.select("ResultTable").select("Result").text().equals("true")){
								
//								LogUtil.W(xml.toString());
								
								String Code = document.select("code").text();
								
								if(Code.equals("0")){
									try {
										Setting.setPassword(getApplicationContext(), SimpleCrypto.encrypt(Define.CRYPTO_KEY, mNewpass));
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show();
									onBackPressed();
								}else{
									
									if(Code.equals("-2001")){
										Toast.makeText(getApplicationContext(), "비밀번호가 틀립니다.", Toast.LENGTH_LONG).show();
										curr_pass.requestFocus();
									}else{
										Toast.makeText(getApplicationContext(), "비밀번호가 변경되지 않았습니다.", Toast.LENGTH_LONG).show();
									}
								}
							}
						}
					}, true);
					
				}else{
					
					if(!mNewpass.equals(mNewpass_RE)){
						exchange_pass_hint.setText("비밀번호를 확인해주세요");
						new_pass.requestFocus();
					}
					if(mNewpass.length() < 6 || mNewpass.length() > 12){
						exchange_pass_hint.setText("비밀번호는 6~12자리로 입력해주세요.");
						new_pass.requestFocus();
					}
					
				}
				
				
				
			}
		});
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);

		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
	// TODO Auto-generated method stub	
		mhttp.threadStop();
		super.onDestroy();
	}

}
