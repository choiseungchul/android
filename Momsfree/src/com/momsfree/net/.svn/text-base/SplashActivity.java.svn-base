package com.momsfree.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.momsfree.net.conf.Define;
import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;

public class SplashActivity extends BaseActivity{

	ImageView splash_image;
	Handler handle;
	Runnable run;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		if(Setting.getIsFirstUse(getApplicationContext()).equals("")){
			MomsUtil.addShortCut(getApplicationContext());
			Setting.setIsFirstUse(getApplicationContext(), "N");
		}
		
		handle = new Handler();
		run = new Runnable() {
			@Override
			public void run() {
//				Intent ii = new Intent(getApplicationContext(), UserSelectActivity.class);
//				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(ii);
//				finish();
//				overridePendingTransition(0, 0);
				LoginProc();
			}
		};
		
		handle.postDelayed(run, 2000);
		
		if(!Setting.getEmail(getApplicationContext()).equals("")){
			GcmCheck();
		}
	
	}

	private void GcmCheck(){
		// GCM 점검
		try{
				registerInBackground();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	void LoginProc(){
		if(Setting.getAutoLogin(getApplicationContext()).equals("Y")){
			if(!Setting.getEmail(getApplicationContext()).equals("") && !Setting.getToken(getApplicationContext()).equals("")){

				final HttpDocument http = new HttpDocument(getApplicationContext());
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_id", Setting.getEmail(getApplicationContext()));
				params.put("token", Setting.getToken(getApplicationContext()));
				http.getDocument(UrlDef.LOGIN, params, null, new HttpCallBack() {

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
								final String member_type = obj.getString("member_type");
								
//								StringBuffer sb = new StringBuffer();
//								
//								for(int i = 0 ; i < header.length; i++){
//									LogUtil.D(header[i].getName() + ":" + header[i].getValue());
//									if(header[i].getName().contains("Set-Cookie")){
//										String v = header[i].getValue().split(";")[0] ;
//										LogUtil.D("v = " + v);
//										sb.append( v );
//										sb.append(";");
//									}
//								}
																
								try {
									
									token = URLDecoder.decode(token, "UTF-8");
									Setting.setToken(getApplicationContext(), token);
									Setting.setCookieString(getApplicationContext(), token);
									
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								gotoMainPage(member_type);
							}else{
								String msg = obj.getString("msg");
								String msg_detail = obj.getString("msg_detail");

								try {
									Toast.makeText(getApplicationContext(), URLDecoder.decode(msg, "UTF-8"),  Toast.LENGTH_LONG).show();
									Toast.makeText(getApplicationContext(), URLDecoder.decode(msg_detail, "UTF-8"),  Toast.LENGTH_LONG).show();
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								gotoLoginPage();

							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						http.threadStop();
					}
				}, false);
			}else{
				gotoUserSelectPage();
			}
		}else{
			gotoUserSelectPage();
		}
	}

	private void gotoMainPage(String user_type){
		String url = "";
		String title ="";

		if("M".equals(user_type)){
			url = UrlDef.FIND_SITTER;
			title = "베이비시터 정보";
		}else if("B".equals(user_type)){
			url = UrlDef.FIND_JOB;
			title = "일자리 정보";
		}

		Intent ii = new Intent(getApplicationContext(), WebActivity.class);
		ii.putExtra("url", url);
		ii.putExtra("title", title);
		ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(ii);
		overridePendingTransition(0,0);
		finish();
	}

	private void gotoUserSelectPage(){
		Intent ii = new Intent(getApplicationContext(), UserSelectActivity.class);
		ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(ii);
		overridePendingTransition(0,0);
		finish();
	}
	
	private void gotoLoginPage(){
		Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
		ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(ii);
		overridePendingTransition(0,0);
		finish();
	}

	GoogleCloudMessaging gcm;

	private void registerInBackground() {

		LogUtil.D("registerInBackground called");

		new AsyncTask<Void, Integer, String>() {
			protected String doInBackground(Void...  arg0) {
				// TODO Auto-generated method stub
				LogUtil.D("doInBackground called");

				String msg = "";
				String regid = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
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
				sendGcmCode(result);
			};
		}.execute(null, null, null);
		
	}

	private void sendGcmCode(String regId){
		TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
		HttpDocument sendGcmCode = new HttpDocument(SplashActivity.this);
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("token", Setting.getToken(getApplicationContext()));
		params.put("user_id", Setting.getEmail(getApplicationContext()));
		params.put("push_phone_id", telManager.getDeviceId());
		params.put("push_google_id", regId);
		sendGcmCode.getDocument(UrlDef.SEND_GOOGLE_CODE, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document,
					Header[] header) {
				String data = document.select("body").text();
				LogUtil.D("send gcm data = " + data);
				
				try {
					JSONObject json = new JSONObject(data);
					
					String rs = json.getString("result");
					if(rs.equals("0000")){
						
						LogUtil.D("send gcm success");
						
					}else{
						LogUtil.D("send gcm failed");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, false);
	}

	@Override
	public void onBackPressed() {

		if(handle != null){
			handle.removeCallbacks(run);
			handle = null;
			run = null;
		}
		
		// TODO Auto-generated method stub
		final DefaultDialog dialog = new DefaultDialog(this);
		dialog.setTitle("알림");
		dialog.setMessage("프로그램을 종료하시겠습니까?");
		dialog.setPositiveButton("예", new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setNagativeButton("아니오", new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();

	}

}
