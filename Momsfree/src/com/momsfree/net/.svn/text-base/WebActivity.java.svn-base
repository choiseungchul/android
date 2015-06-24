package com.momsfree.net;

import java.util.Hashtable;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.AddrSrchDialog;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.customview.FindAddrDialog;
import com.momsfree.net.customview.MWebView;
import com.momsfree.net.customview.TextViewNanumGothic;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.GPSTracker;
import com.momsfree.util.LocationUtil;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;

public class WebActivity extends BaseActivity{
	boolean isFirstLoaded = false;
	MWebView mWeb;
	String mUrl, mTitle;
	Button save_btn; 
	ImageButton home_btn, loc_btn;
//	ProgressBar web_loader;
	TextViewNanumGothic top_title, webpage_network_error;
	LinearLayout menu1_btn, menu2_btn, menu3_btn, menu4_btn;
	ImageView switch_mom_sitter;

	int menu1, menu2, menu3, menu4;
	int id_homebtn, id_toptitle, id_locbtn;
	int mom_sitter_id;

	String UserType = "";
	int mWebId = -1;
	int mWebLoaderId = -1;
	private Handler handle = new Handler();
	Runnable run;
	long waittime = 10000;
	GPSTracker track;
	Location myLocation;

	AddrSrchDialog addrDialog;
	FindAddrDialog findDialog;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		UserType = Setting.getUserType(getApplicationContext());
		Setting.setListType(getApplicationContext(), UserType);

		LogUtil.D("user_type = " + UserType);

		if(UserType.equals("") || UserType.equals("M")){
			setContentView(R.layout.webview_blue);
			mWebId  = R.id.set_webpage_blue;
//			mWebLoaderId = R.id.set_webpage_loader_blue;
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
			menu1 = R.id.menu1_btn_blue;
			menu2 = R.id.menu2_btn_blue;
			menu3 = R.id.menu3_btn_blue;
			menu4 = R.id.menu4_btn_blue;
			mom_sitter_id = R.id.switch_mom_sitter_blue;
		}else{
			setContentView(R.layout.webview_pink);
			mWebId  = R.id.set_webpage_pink;
//			mWebLoaderId = R.id.set_webpage_loader_pink;
			id_homebtn = R.id.home_btn_pink;
			id_toptitle = R.id.top_title_pink;
			id_locbtn = R.id.loc_btn_pink;
			menu1 = R.id.menu1_btn_pink;
			menu2 = R.id.menu2_btn_pink;
			menu3 = R.id.menu3_btn_pink;
			menu4 = R.id.menu4_btn_pink;
			mom_sitter_id = R.id.switch_mom_sitter_pink;
		}

		initUI();
		initData();

		mUrl = getIntent().getExtras().getString("url");
		mTitle = getIntent().getExtras().getString("title");
		if(mTitle != null){
			top_title.setText(mTitle);
		}

		// 위치정보 보냄
		if(!Setting.getLatString(getApplicationContext()).equals("") && !Setting.getLngString(getApplicationContext()).equals("")
				&& ( mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=sitter") || mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=mom") )){
			
			mUrl = mUrl + "&lat=" + Setting.getLatString(getApplicationContext()) + "&lng=" + Setting.getLngString(getApplicationContext());
			
			LogUtil.D("latlng url = " + mUrl);
		}

		mWeb  = (MWebView) findViewById(mWebId);
//		web_loader = (ProgressBar)findViewById(mWebLoaderId);
		webpage_network_error = (TextViewNanumGothic)findViewById(R.id.webpage_network_error);

		mWeb.setWebViewClient(new MyWebClient());

		mWeb.getSettings().setRenderPriority(RenderPriority.HIGH);
		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);
		set.setDomStorageEnabled(true);
		set.setGeolocationEnabled(true);
		set.setPluginState(PluginState.ON);
		set.setJavaScriptCanOpenWindowsAutomatically(true);
		
		set.setAppCacheEnabled(true);
		set.setCacheMode(WebSettings.LOAD_DEFAULT);
		set.setRenderPriority(WebSettings.RenderPriority.HIGH);
		set.setSupportZoom(false);
		set.setAllowFileAccess(true);
		set.setSavePassword(false);
		set.setSupportMultipleWindows(true);
		set.setAppCachePath("");
		set.setAppCacheMaxSize(5*1024*1024);
		set.setAllowContentAccess(true);
		set.setAllowFileAccess(true);
		set.setAllowFileAccessFromFileURLs(true);
		set.setAllowUniversalAccessFromFileURLs(true);

//		Handler h = new Handler();
//		h.postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				mWeb.stopLoading();
//				mWeb.loadUrl(mUrl);
//			}
//		}, 1000);

		final Map<String, String> header = new Hashtable<String, String>();
		header.put("user_id", Setting.getEmail(getApplicationContext()));
		header.put("session_token", Setting.getToken(getApplicationContext()));
		
		LogUtil.D("header data = " + header.get("user_id") + " / " + header.get("session_token"));

		mWeb.loadUrl(mUrl, header);
		
//		mWeb.loadUrl(mUrl);
		
		if(mUrl.startsWith(UrlDef.FIND_JOB)){
			switch_mom_sitter.setImageResource(R.drawable.menu1_icon);
			Setting.setListType(getApplicationContext(), "M");
		}else if(mUrl.startsWith(UrlDef.FIND_SITTER)){
			switch_mom_sitter.setImageResource(R.drawable.menu5_icon);
			Setting.setListType(getApplicationContext(), "B");
		}
	}

	private void initData() {

	}

	private void initUI() {

		home_btn = (ImageButton)findViewById(id_homebtn);
		home_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), UserSelectActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0,0);
			}
		});

		loc_btn = (ImageButton)findViewById(id_locbtn);
		loc_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(MomsUtil.CheckInternet(getApplicationContext())){
					track = new GPSTracker(getApplicationContext(), new Handler(), new Runnable() {
						@Override
						public void run() {
							try{
								myLocation = track.getCurrLocation();
							}catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
							
							String addr = LocationUtil.getGeoLocation(getApplicationContext(), myLocation);
							if(addr ==null){
								LocationUtil.getGeoLocation(getApplicationContext(), myLocation, new HttpCallBack(){
									@Override
									public void onHttpCallBackListener(
											Document document, Header[] header) {
										JSONObject data = null;

										try {
											data = new JSONObject(document.select("body").text());
											JSONArray rs = data.getJSONArray("results");
											JSONObject json = (JSONObject) rs.get(0);
											String user_addr = json.getString("formatted_address");
											showDialog(LocationUtil.getLocationStringFomat(user_addr), myLocation.getLatitude(), myLocation.getLongitude());
											
										} catch (JSONException e) {

											e.printStackTrace();
										}
									}
								});
							}else{
								
								showDialog(LocationUtil.getLocationStringFomat(addr), myLocation.getLatitude(), myLocation.getLongitude());
							}
							
						} // runnable
					});
					
				}else{
					final DefaultDialog dialog = new DefaultDialog(WebActivity.this);
					dialog.setTitle("알림");
					dialog.setMessage("네트워크 연결이 필요합니다.");
					dialog.isNegativeButton(false);
					dialog.show();
					dialog.setPositiveButton("확인", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
		});

		top_title = (TextViewNanumGothic)findViewById(id_toptitle);

		menu1_btn = (LinearLayout)findViewById(menu1);
		menu2_btn = (LinearLayout)findViewById(menu2);
		menu3_btn = (LinearLayout)findViewById(menu3);
		menu4_btn = (LinearLayout)findViewById(menu4);

		switch_mom_sitter = (ImageView)findViewById(mom_sitter_id);
		

		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();

				if(id == menu1){
					if(Setting.getListType(getApplicationContext()).equals("M")){
						top_title.setText("베이비시터 정보");
						mUrl = UrlDef.FIND_SITTER;
						
						if(!Setting.getLatString(getApplicationContext()).equals("") && !Setting.getLngString(getApplicationContext()).equals("")){
							mUrl = mUrl + "&lat=" + Setting.getLatString(getApplicationContext()) + "&lng=" + Setting.getLngString(getApplicationContext());
						}
						Setting.setListType(getApplicationContext(), "B");
						switch_mom_sitter.setImageResource(R.drawable.menu5_icon);
						
					}else if(Setting.getListType(getApplicationContext()).equals("B")){
						top_title.setText("일자리 정보");
						mUrl = UrlDef.FIND_JOB;
						
						if(!Setting.getLatString(getApplicationContext()).equals("") && !Setting.getLngString(getApplicationContext()).equals("")){
							mUrl = mUrl + "&lat=" + Setting.getLatString(getApplicationContext()) + "&lng=" + Setting.getLngString(getApplicationContext());
						}
						Setting.setListType(getApplicationContext(), "M");
						switch_mom_sitter.setImageResource(R.drawable.menu1_icon);
					}

					mWeb.stopLoading();
//					web_loader.setVisibility(View.VISIBLE);
					mWeb.loadUrl(mUrl);
					
				}else if(id == menu2){
					
					top_title.setText("찜목록");
					mWeb.stopLoading();
//					web_loader.setVisibility(View.VISIBLE);
					mWeb.loadUrl(UrlDef.FAVORITE_URL);
					
				}else if(id == menu3){
					
					top_title.setText("마이페이지");
					mWeb.stopLoading();
//					web_loader.setVisibility(View.VISIBLE);
					mWeb.loadUrl(UrlDef.MYPAGE_URL);
					
				}else if(id == menu4){
					Intent ii = null;
					ii = new Intent(getApplicationContext(), SettingActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);
				}
			}
		};

		menu1_btn.setOnClickListener(listener);
		menu2_btn.setOnClickListener(listener);
		menu3_btn.setOnClickListener(listener);
		menu4_btn.setOnClickListener(listener);

	}

	//	private void getLocation(){
	//	
	//		locListener = new LocationListener() {
	//
	//			@Override
	//			public void onStatusChanged(String provider, int status, Bundle extras) {
	//			}
	//
	//			@Override
	//			public void onProviderEnabled(String provider) {
	//				if(LocationUtil.checkGPS(locManager)){
	//					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListener);
	//				}else{
	//					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, locListener);
	//				}
	//			}
	//
	//			@Override
	//			public void onProviderDisabled(String provider) {
	//			}
	//
	//			@Override
	//			public void onLocationChanged(Location location) {
	//				//myLocation = location;
	//				LogUtil.D(location.toString());
	////				myLocation = location;
	//				if(LocationUtil.isBetterLocation(location, myLocation)){
	//					myLocation = location;
	//				}
	//				String locationAddr = LocationUtil.getGeoLocation(getApplicationContext(), myLocation);
	//				showDialog(locationAddr);
	//			}
	//		};
	//		
	//		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locListener);
	//		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, locListener);
	//		
	//		run = new Runnable() {
	//			@Override
	//			public void run() {
	//				// TODO Auto-generated method stub
	//				showInputDialog();
	//			}
	//		};
	//		handle.postDelayed(run, waittime);
	//	}

	// 최종 주소지가 나왔을때..
	private void showDialog(final String LocationString, final double lat, final double lng){
		findDialog = new FindAddrDialog(WebActivity.this);
		findDialog.setTitle(LocationString);
		findDialog.setSrchbtn(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findDialog.dismiss();
				addrDialog = new AddrSrchDialog(WebActivity.this);
				addrDialog.setTitle(LocationString);
				addrDialog.setItemOnclick(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						String address = (String)arg0.getItemAtPosition(arg2);
						
						double[] loc = LocationUtil.getLocationByAddress(getApplicationContext(), address);
						
						
						if(loc != null){
							Setting.setLatString(getApplicationContext(), String.valueOf(loc[0]));
							Setting.setLngString(getApplicationContext(), String.valueOf(loc[1]));
							
							Toast.makeText(getApplicationContext(), "위치가 저장되었습니다.", Toast.LENGTH_LONG).show();
							
							mWeb.stopLoading();
							
							if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=sitter")){
//								web_loader.setVisibility(View.VISIBLE);
								mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=sitter&lat=" + loc[0]+"&lng=" + loc[1]);
							}else if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=mom")){
//								web_loader.setVisibility(View.VISIBLE);
								mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=mom&lat=" + loc[0]+"&lng=" + loc[1]);
							}
							
							addrDialog.dismiss();
						}else{
//							LogUtil.D("좌표를 받을수 없음....");
							LocationUtil.getLocationInfo(getApplicationContext(), address, new HttpCallBack() {
								@Override
								public void onHttpCallBackListener(Document document, Header[] header) {
									String data = document.select("body").text();
									
									LogUtil.D("getLocationInfo = " + data);
									
									JSONObject json;
									try {
										json = new JSONObject(data);
										
										double[] locationPoint = LocationUtil.getGeoPoint(json);
										
										Setting.setLatString(getApplicationContext(), String.valueOf(locationPoint[0]));
										Setting.setLngString(getApplicationContext(), String.valueOf(locationPoint[1]));
										
										Toast.makeText(getApplicationContext(), "위치가 저장되었습니다.", Toast.LENGTH_LONG).show();
										
										mWeb.stopLoading();
										
										if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=sitter")){
//											web_loader.setVisibility(View.VISIBLE);
											mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=sitter&lat=" + locationPoint[0]+"&lng=" + locationPoint[1]);
										}else if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=mom")){
//											web_loader.setVisibility(View.VISIBLE);
											mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=mom&lat=" + locationPoint[0]+"&lng=" + locationPoint[1]);
										}
										
										addrDialog.dismiss();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										addrDialog.dismiss();
									}
								}
							});
						}
					}
				});
				addrDialog.setBackBtn(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addrDialog.dismiss();
						findDialog.show();
						
					}
				});
				addrDialog.setCloseBtn(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addrDialog.dismiss();
					}
				});

				addrDialog.show();
				
			}
		});

		findDialog.setLocationBtnListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setLatString(getApplicationContext(), String.valueOf(lat));
				Setting.setLngString(getApplicationContext(), String.valueOf(lng));
				findDialog.dismiss();

				if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=sitter")){
//					web_loader.setVisibility(View.VISIBLE);
					mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=sitter&lat=" + lat+"&lng=" + lng);
				}else if(mUrl.startsWith("http://momsfree.co.kr/app/a_v/view.php?act=mom")){
//					web_loader.setVisibility(View.VISIBLE);
					mWeb.loadUrl("http://momsfree.co.kr/app/a_v/view.php?act=mom&lat=" + lat+"&lng=" + lng);
				}
			}
		});

		findDialog.show();
	}


	class MyWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			LogUtil.D("onPageStarted");
			if(!isFirstLoaded){
				final Map<String, String> header = new Hashtable<String, String>();
				header.put("user_id", Setting.getEmail(getApplicationContext()));
				header.put("session_token", Setting.getToken(getApplicationContext()));
				
				LogUtil.D("header data = " + header.get("user_id") + " / " + header.get("session_token"));

				view.loadUrl(mUrl, header);
				
				isFirstLoaded = true;
			}
			
//			super.onPageStarted(view, url, favicon);
		}
		
		
		
		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			LogUtil.D("doUpdateVisitedHistory");
			super.doUpdateVisitedHistory(view, url, isReload);
		}
		
		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			LogUtil.D("onReceivedHttpAuthRequest");
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}
		
		public boolean shouldOverrideUrlLoading(WebView view, String url){

			LogUtil.D("shouldOverrideUrlLoading");
			
			if (url.startsWith("momsfree://joincomplete/")) {
				Intent i = new Intent(getApplicationContext(), JoinCompleteActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setData(Uri.parse(url));
				startActivity(i);
				finish();

			}else if(url.startsWith("momsfree://logout/")){
				MomsUtil.clearAllPrefferences(getApplicationContext());
				Setting.setIsFirstUse(getApplicationContext(), "N");
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}else if(url.startsWith("tel:")){
				try {
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse(url));
					startActivity(callIntent);
				} catch (ActivityNotFoundException e) {
					LogUtil.D("전화걸기", "전화걸기에 실패했습니다");
				}
			}else{
				view.loadUrl(url);
			}			

			return true;
		}

		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			mWeb.setVisibility(View.GONE);
			webpage_network_error.setVisibility(View.VISIBLE);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
//			web_loader.setVisibility(View.GONE);
//			CookieSyncManager.getInstance().sync();
//			String cookies = CookieManager.getInstance().getCookie(url);
//			LogUtil.D("All the cookies in a string = " + cookies);

			super.onPageFinished(view, url);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(addrDialog != null){
			addrDialog.dismiss();
			addrDialog = null;
		}

		if(findDialog != null){
			findDialog.dismiss();
			findDialog = null;
		}

		super.onDestroy();
	}

	@Override
	public void onBackPressed() {

		if(!mWeb.canGoBack()){
			
			final DefaultDialog dialog = new DefaultDialog(this);
			dialog.setTitle("알림");
			dialog.setBackgroundGrayOver();
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
		}else{
			mWeb.goBack();
		}
	}
}
