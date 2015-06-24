package com.momsfree.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.LogUtil;

public class JoinCompleteActivity extends BaseActivity{

	private static final String PARAM1="user_email";
	private static final String PARAM2="token";
	
	TextView empty_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		
		empty_tv = (TextView)findViewById(R.id.empty_tv);
		
		Uri data = getIntent().getData();				
		if(data != null) {									
			final String user_email	= data.getQueryParameter(PARAM1);
			String token = data.getQueryParameter(PARAM2);
			
			if(user_email == null || token == null){
				Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0, 0);
			}else{
				try {
					token = URLDecoder.decode(token, "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				LogUtil.D("user_email = " + user_email);
			
				final HttpDocument login = new HttpDocument(getApplicationContext());
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_id", user_email);
				params.put("token", token);
				login.getDocument(UrlDef.LOGIN, params, null, new HttpCallBack() {
					
					@Override
					public void onHttpCallBackListener(Document document, Header[] header) {
						String returnData = document.select("body").text();

						JSONObject obj = null;
						try {
							obj = new JSONObject(returnData);

							LogUtil.D("all = " + obj.toString());

							String result = obj.getString("result");
							if(result.equals("0000")){
								// 로그인 성공
								String token = obj.getString("token");							
								String user_type = obj.getString("member_type");
								try {
									token = URLDecoder.decode(token, "UTF-8");
									
									Setting.setToken(getApplicationContext(), token);
									Setting.setCookieString(getApplicationContext(), token);
									Setting.setEmail(getApplicationContext(), user_email);
									Setting.setUserType(getApplicationContext(), user_type);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								String url = "";
								String title = "";
								Intent ii = new Intent(getApplicationContext(), WebActivity.class);
								if(Setting.getUserType(getApplicationContext()).equals("M")){
									url = UrlDef.FIND_SITTER;
									title = "베이비시터 정보";
								}else if(Setting.getUserType(getApplicationContext()).equals("B")){
									url = UrlDef.FIND_JOB;
									title = "일자리 정보";
								}else{
									url = UrlDef.FIND_SITTER;
									title = "베이비시터 정보";
								}
								
								ii = new Intent(getApplicationContext(), WebActivity.class);
								ii.putExtra("url", url);
								ii.putExtra("title", title);
								ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(ii);
								finish();
								overridePendingTransition(0,0);
							}else{
								String msg = obj.getString("msg");
								String msg_detail = obj.getString("msg_detail");

								try {
									Toast.makeText(getApplicationContext(), URLDecoder.decode(msg, "UTF-8"),  Toast.LENGTH_LONG).show();
									Toast.makeText(getApplicationContext(), URLDecoder.decode(msg_detail, "UTF-8"),  Toast.LENGTH_LONG).show();
									
									Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									finish();			
									startActivity(intent);
									overridePendingTransition(0,0);
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						login.threadStop();
					}
					
				}, false);
			}	
		}	
	}
}