package com.returndays.ralara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Random;

import org.apache.http.HttpVersion;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.util.XmlDom;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpData;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.crypto.SimpleCrypto;
import com.returndays.ralara.http.HTTPrequest;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class SplashActivity extends BaseActivity {

	ProgressBar mSeek = null;
	TextViewNanumGothic mComp, mCopy;
	LinearLayout sp_back;
	HttpDocument mHttpUtil, gcmUpdate;
	boolean isDestroy = false;
	long viewtime = 0L;
	AudioManager aManager;
	boolean needUpdate = false;
	boolean serverCheck = false;
	AsyncTask<Void, Integer, String> Task;
	String user_hp = null;
	TelephonyManager telManager;

	private int logosong[] = {
			R.raw.logosong1, R.raw.logosong2, R.raw.logosong3, R.raw.logosong4, R.raw.logosong5
	};
	private MediaPlayer mediaPlayer;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
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
		
		
		sp_back = (LinearLayout)findViewById(R.id.sp_back);
		//sp_back_image = new BitmapDrawable();
		sp_back.setBackgroundResource(R.drawable.splash_bg);
		
		viewtime = System.currentTimeMillis();

		mSeek = (ProgressBar)findViewById(R.id.progressbar);	
		mComp = (TextViewNanumGothic)findViewById(R.id.company);
		mCopy = (TextViewNanumGothic)findViewById(R.id.copyright);

		mHttpUtil = new HttpDocument(getApplicationContext());
		gcmUpdate = new HttpDocument(getApplicationContext());

		if(!DbUtil.checkDataBase(getApplicationContext())) {
			DbUtil.initDbFile(getApplicationContext());
		}
		
		if(!MadUtil.isNetworkConnected(getApplicationContext())) {
			DialogUtil.confirm(SplashActivity.this, R.string.network_disconnect_msg, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});

		} else {
			
			new ConfigDown().execute();
		}
	}
	
	GoogleCloudMessaging gcm;

	// Gcm 체크
	private void registerInBackground() {

		LogUtil.D("registerInBackground called");
		
		Task = new AsyncTask<Void, Integer, String>() {
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
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
				params.put("google_cd", result);
				gcmUpdate.getDocument(UrlDef.GOOGLE_CODE_EXCHANGE, params, null, new HttpCallBack() {

					@SuppressLint("HandlerLeak")
					@Override
					public void onHttpCallBackListener(Document document) {
						if(document.select("ReturnTable").select("Result").text().equals("true")){
							LogUtil.D("RegId is UPDATED");
						}
						gcmUpdate.threadStop();
					}
				}, false);
			};
			
		}.execute(null, null, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	void gotoLoginPage(){
		
		long curr_time = System.currentTimeMillis();
		if((curr_time - viewtime) < 2000){

		}

		finish();
		overridePendingTransition(0, 0);
		startSingTopActivity(LoginActivity.class);		
	}
	
	Handler mUIHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: 
				//mProgressMsg.setText("0%");
				break;
			}
		}
	};

	private class ConfigDown extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			File folder = new File(Define.AD_APP_FOLDER);
			if(!folder.exists()) folder.mkdirs();
			File f = new File(folder, Define.CONFIG_FILE_NM);
			try {
				
				URL url = null;
				url = new URL(UrlDef.VERSION_CHECK);
				URLConnection connection = null;
				connection = url.openConnection();
				connection.setConnectTimeout(1000*5);
				connection.connect();
				
				//mUIHandler.sendEmptyMessage(0);

				InputStream input = null;
				input = url.openStream();
				
				int lenghtOfFile = connection.getContentLength();
				if(lenghtOfFile == -1){
					HTTPrequest httpRequest = new HTTPrequest(HttpVersion.HTTP_1_0);
//					httpRequest.setHTTPrequestListener(new HTTPrequestListener() {
//						@Override
//						public void downloadProgress(int iPercent) {
//							LogUtil.D("percent = " + iPercent);
//						}
//					});
					HttpData data =  httpRequest.getRequest(UrlDef.VERSION_CHECK);
					LogUtil.D("http 1.0 content length = " + data.response.getEntity().getContentLength());
					lenghtOfFile = (int)data.response.getEntity().getContentLength();
				}
				
//				LogUtil.I("lenghtOfFile===============================:"+lenghtOfFile);
				
				OutputStream out = null;
				out = new FileOutputStream(f);

				byte[] b = new byte[100];
				int read;
				int total = 0;

				while ((read = input.read(b)) != -1) {
					LogUtil.D("----------------------");
					total += read;
					LogUtil.D("total : " + total);
					int percent = (int) (((double)total/(double)lenghtOfFile)*100);
					publishProgress(percent);
					out.write(b, 0, read);
					Thread.sleep(100);
				}
				
				input.close();
				out.close();
			} catch (Exception e) {
				
				Toast.makeText(getApplicationContext(), "네트워크 상태가 좋지 않습니다.", Toast.LENGTH_LONG).show();
				
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			XmlDom xml = MadUtil.getConfigFile();
			LogUtil.I("xml null?"+(xml==null));

			int needUpdate = Integer.parseInt(xml.tag("NeedUpdate").text());
			String serverCheck = xml.tag("serverAbleable").text();
			String serverCheckTime = xml.tag("serverCheckTime").text();
			int appVer = Integer.parseInt(xml.tag("AppVer").text());
			Setting.setSvrVersion(getApplicationContext(), String.valueOf(appVer));
			int curVersion = 1;
			String packageNm = "";
			try {
				PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
				curVersion = i.versionCode;
				packageNm = i.packageName;
			} catch(NameNotFoundException e) {}

			final String _packageNm = packageNm;
			
			if(serverCheck.equals("N")){
				SplashActivity.this.serverCheck = true;
				Intent ii = new Intent(getApplicationContext(), CheckServerActivity.class);
				ii.putExtra("checkTime", serverCheckTime);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
			}
			
			if(curVersion < needUpdate) { //강제 업데이트
				
				SplashActivity.this.needUpdate = true;
				
				DialogUtil.alert(SplashActivity.this, 
						"최신버전으로 업데이트를 하셔야" +
							"\n정상적으로 이용하실 수 있습니다." +
							"\n확인 버튼을 누르시면 마켓으로 이동합니다.", 
							new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(Define.IS_TSTORE){
							MadUtil.TstoreLink(SplashActivity.this, "OA00648870");
						}else{
							DialogUtil.alert.dismiss();
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("market://details?id="+_packageNm));
							startActivity(intent);
							finish();
						}
					}
				});
			}
			
			if(!SplashActivity.this.serverCheck && !SplashActivity.this.needUpdate){
				if(!isDestroy){

					if(Setting.getToken(getApplicationContext()).equals("")) {
						if(!Setting.getEmail(getApplicationContext()).equals("")){

							/// 추후 주석 삭제할것!!!

//							if(telManager.getLine1Number() == null){
//								DialogUtil.alert(SplashActivity.this, "개통되지 않은 폰입니다. 서비스를 이용하실 수 없습니다.", new View.OnClickListener(){
//									@Override
//									public void onClick(View v) {
//										// TODO Auto-generated method stub
//										finish();
//										overridePendingTransition(0, 0);
//									}
//								});
//	
//							}else{
//								if(telManager.getLine1Number().equals("")){
//									DialogUtil.alert(SplashActivity.this, "개통되지 않은 폰입니다. 서비스를 이용하실 수 없습니다.", new View.OnClickListener(){
//										@Override
//										public void onClick(View v) {
//											// TODO Auto-generated method stub
//											finish();
//											overridePendingTransition(0, 0);
//										}
//									});
//								}
//							}

							
							Hashtable<String, String> params = new Hashtable<String, String>();
							params.put("phone_cd", telManager.getDeviceId());
							params.put("hp_no", user_hp);
							params.put("u_email", Setting.getEmail(getApplicationContext()));

							mHttpUtil.getDocument(UrlDef.IS_JOIN, params, null, new HttpCallBack() {

								@Override
								public void onHttpCallBackListener(Document document) {
									// TODO Auto-generated method stub
									if(document.select("resulttable").select("result").text().equals("true")){
										String RESULT = document.select("RESULT").text();
										String PHONE_CD_RST = document.select("PHONE_CD_RST").text();
										String HP_NO_RST = document.select("HP_NO_RST").text();

										if(RESULT.equals("Y") && PHONE_CD_RST.equals("Y") && HP_NO_RST.equals("Y")){
											gotoLoginPage();

										}else if(RESULT.equals("U") && PHONE_CD_RST.equals("N") && HP_NO_RST.equals("N")){
											finish();
											overridePendingTransition(0, 0);
											startSingTopActivity(Join1Activity.class);
										}else{
											gotoLoginPage();
										}
									}
								}
							}, false);
						}else{
							gotoLoginPage();
						}

						/*
						 * 로그인 정보가 있을때
						 */
					} else {

						try{	
							
							if(!Setting.getEmail(getApplicationContext()).equals("") && !Setting.getPassword(getApplicationContext()).equals("") ){
												
								if( Setting.getAutoLogin(getApplicationContext()).equals("Y")){
									TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
									Hashtable<String, String> params = new Hashtable<String, String>();
									params.put("user_email", Setting.getEmail(getApplicationContext()));
									params.put("user_pwd", SimpleCrypto.decrypt(Define.CRYPTO_KEY, Setting.getPassword(getApplicationContext())));
									params.put("phone_cd", telManager.getDeviceId());
									params.put("hp_no", user_hp);
									
									int version = android.os.Build.VERSION.SDK_INT;
									params.put("os_version" , MadUtil.getOSVersionString(version));

									mHttpUtil.getDocument(UrlDef.LOGIN_NOTK3, params, null, new HttpCallBack() {

										@Override
										public void onHttpCallBackListener(Document document) {
											// TODO Auto-generated method stub
											if(document.select("ResultTable").select("Result").text().equals("true")){
												Setting.setToken(getApplicationContext(), document.select("TOKEN_KEY").text());
												
												Toast.makeText(getApplicationContext(), "    " + document.select("USER_NICKNAME").text() + "" +  getString(R.string.login_complete_toast) + "    ", Toast.LENGTH_SHORT).show();

												// GCM 점검
												try{
													registerInBackground();
												}catch(Exception e){
													e.printStackTrace();
												}

												aManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

												switch(aManager.getRingerMode())
												{
												case AudioManager.RINGER_MODE_SILENT: 
													finish();
													startSingTopActivity(AdlistActivity.class);
													overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
													break; 
												case AudioManager.RINGER_MODE_VIBRATE:  
													finish();
													startSingTopActivity(AdlistActivity.class);
													overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
													break;  
												case AudioManager.RINGER_MODE_NORMAL: 
													Random rnd1 = new Random();
													int rdmlogosong = 	rnd1.nextInt(5);
													boolean isLogosong  = false;

													String logosongno = Setting.getLogoSound(getApplicationContext());
													if (logosongno.equals("") ){
														isLogosong = false;
													}else if (logosongno.equals("logosong1")){
														rdmlogosong = 0;
														isLogosong = true;
													}else if (logosongno.equals("logosong2")){
														rdmlogosong = 1;
														isLogosong = true;
													}else if (logosongno.equals("logosong3")){
														rdmlogosong = 2;
														isLogosong = true;
													}else if (logosongno.equals("logosong4")){
														rdmlogosong = 3;
														isLogosong = true;
													}else if (logosongno.equals("logosong5")){
														rdmlogosong = 4;
														isLogosong = true;
													}else if (logosongno.equals("NO")){
														isLogosong = false;
													}else{
														isLogosong = false;													
													}

													if(isLogosong){
														mediaPlayer = MediaPlayer.create(getApplicationContext(), logosong[rdmlogosong]);
														mediaPlayer.start();

														mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

															@Override
															public void onCompletion(MediaPlayer mp) {
																// TODO Auto-generated method stub
																mediaPlayer.release();
																
																finish();
																startSingTopActivity(AdlistActivity.class);
																overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
																	
															}
														});
														break;   
													}else{
												
														//finish();
														Intent ii = new Intent(getApplicationContext(), AdlistActivity.class);
														ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
														startActivity(ii);
//														overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
														
													}
												}

											}else{
												// 로그인 실패
												LogUtil.I("자동 로그인 실패");
												gotoLoginPage();

											}
										}
									}, false);
								}else{
									// 자동 로그인 설정이 아님
									LogUtil.I("자동 로그인 설정 아님");
									gotoLoginPage();

								}
							}else{
								// 로그인 정보가 하나라도 없을 경우
								LogUtil.I("로그인 정보 부족");
								LogUtil.I(Setting.getEmail(getApplicationContext()));
								LogUtil.I(Setting.getPassword(getApplicationContext()));
								gotoLoginPage();
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}// isDestroy == false
			}
			
		}//onPost

		@Override
		protected void onProgressUpdate(Integer... values) {
			LogUtil.D("splash progress = " + values[0] );
			
			mSeek.setProgress(values[0]);
		}
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isDestroy = true;
		mHttpUtil.threadStop();
//		try{
//			if(Task != null){
//				if(!Task.isCancelled()){
//					Task.cancel(true);
//				}
//			}		
//		}catch(Exception e){
//		}
//		
		super.onDestroy();
	}

}
