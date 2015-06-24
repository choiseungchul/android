package com.momsfree.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.customview.InputDialog;
import com.momsfree.net.customview.MWebView;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.LocationUtil;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;

public class UserSelectActivity extends BaseActivity{

	LinearLayout btn_select_baby_sitter, btn_select_moms;
	private LocationManager locManager;
	private Location myLocation = null;
	LocationListener locListener;
	DefaultDialog dialog;
	InputDialog input_dialog;
	int SETTING_ACTIVITY_RESULT = 1;
	String currAddr = "";
	boolean useGps = false;
	Criteria crit;
	long minTime = 1000;
	float minDistance = 0;

	Handler handle = new Handler();
	Runnable run;
	long waittime = 30000;
	boolean isNetwork = true;
	
	MWebView banner_view;
	
	Handler gotoMainHandle = new Handler();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_select);

		if(!MomsUtil.CheckInternet(getApplicationContext())){

			dialog = new DefaultDialog(UserSelectActivity.this);
			dialog.setBackgroundWhite();
			dialog.setTitle("알림");
			dialog.setMessage("네트워크에 연결되어 있지 않습니다.\n주소를 가져올수 없습니다.");
			dialog.setPositiveButton("확인", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.isNegativeButton(false);
			dialog.show();

			isNetwork = false;

		}

		// LocationListener 핸들
		locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		initUI();

		crit = new Criteria();
		crit.setPowerRequirement(Criteria.NO_REQUIREMENT);
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		//		crit.setAltitudeRequired(true);
		//		crit.setBearingRequired(true);
		//		crit.setSpeedRequired(true);
		//		crit.setCostAllowed(true);

		locListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				LogUtil.D("provider = " + provider);
				LogUtil.D("extras = " + extras.toString());
			}

			@Override
			public void onProviderEnabled(String provider) {
				LogUtil.D("provide enabled : " +provider  );
				if(LocationUtil.checkGPS(locManager)){
					locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListener);
				}else{
					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locListener);
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				LogUtil.D("provide disabled : " +provider  );
			}

			@Override
			public void onLocationChanged(Location location) {
				//myLocation = location;
				LogUtil.D(location.toString());
				//				myLocation = location;
				if(LocationUtil.isBetterLocation(location, myLocation)){
					myLocation = location;
					
					String lat = String.valueOf(myLocation.getLatitude());
					String lng = String.valueOf(myLocation.getLongitude());
					
					Setting.setLatString(getApplicationContext(), lat);
					Setting.setLngString(getApplicationContext(), lng);
					
					currAddr = LocationUtil.getGeoLocation(getApplicationContext(), myLocation);
					if(currAddr == null){
						LocationUtil.getGeoLocation(getApplicationContext(), myLocation, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document,
									Header[] header) {
								JSONObject data = null;

								try {
									data = new JSONObject(document.select("body").text());
									JSONArray rs = data.getJSONArray("results");
									JSONObject json = (JSONObject) rs.get(0);
									currAddr = json.getString("formatted_address");
									
									showDialog(LocationUtil.getLocationStringFomat(currAddr), myLocation.getLatitude(), myLocation.getLongitude());
									
//									showDialog(currAddr);
								} catch (JSONException e) {

									e.printStackTrace();
								}
							}
						});
					}else{
						showDialog(currAddr, myLocation.getLatitude(), myLocation.getLongitude());
					}
				}

				
			}
		};

		run = new Runnable() {
			@Override
			public void run() {
				showInputDialog();
			}
		};

//		if(isNetwork){
//
//			isUseGPS();
//
//			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListener);
//			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locListener);
//		}

		startGetLocation();
		
	}

	private void showInputDialog(){

		if(dialog == null){
			locManager.removeUpdates(locListener);
			handle.removeCallbacks(run);

			//둘다안될경우는 입력창을 띄운다.
			input_dialog = new InputDialog(UserSelectActivity.this);
			input_dialog.setBackgroundTransparent();
			input_dialog.setTitle("알림");
			input_dialog.setContent("현재 위치정보를 받을수 없습니다.\n수동으로 위치를 입력해주세요.");
			input_dialog.setHint("주소검색 (ex.동, 읍, 면)");
			input_dialog.setSearchEvent(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(input_dialog.getMessage().equals("")){
						Toast.makeText(getApplicationContext(), "검색할 주소를 입력해주세요.", Toast.LENGTH_LONG).show();
					}else{
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("address", input_dialog.getMessage());

						final HttpDocument getaddr = new HttpDocument(getApplicationContext());
						getaddr.getDocument(UrlDef.GET_ADDR, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document, Header[] header) {
								String data = document.select("body").text();

								try {
									JSONObject json = new JSONObject(data);
									String addr = json.getString("addree");

									addr = URLDecoder.decode(addr, "UTF-8");

									input_dialog.setMessage(addr);

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								getaddr.threadStop();
							}
						}, false);
					}
				}
			});
			input_dialog.setPositiveButton("입력", new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(input_dialog.getMessage().equals("")){
						Toast.makeText(getApplicationContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
					}else{
						dialog = new DefaultDialog(UserSelectActivity.this);
						dialog.setTitle("주소 확인");
						dialog.setBackgroundWhite();
						dialog.setMessage("입력된 주소\n" + input_dialog.getMessage() + "\n 가 맞습니까?");
						dialog.setPositiveButton("네", new OnClickListener() {
							@Override
							public void onClick(View v) {
								input_dialog.dismiss();
								Setting.setUserLocation(getApplicationContext(), input_dialog.getMessage());
								dialog.dismiss();
								Toast.makeText(getApplicationContext(), "위치정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
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
			});

			input_dialog.setNagativeButton("입력안함", new OnClickListener() {
				@Override
				public void onClick(View v) {
					input_dialog.dismiss();
				}
			});

			input_dialog.show();
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
				gotoLoginPage();
			}
		}else{
			gotoLoginPage();
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

	private void gotoLoginPage(){
		Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
		ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(ii);
		overridePendingTransition(0,0);
		finish();
	}
	
	// GPS 사용여부 물어보는 다이어로그
	private void isUseGPS(){
		if(!LocationUtil.checkGPS(locManager)){
			dialog = new DefaultDialog(this);
			dialog.setBackgroundWhite();
			dialog.setTitle("GPS 수신 알림");
			dialog.setMessage("정확한 위치를 확인하기 위해 GPS를 설정하시겠습니까?");
			dialog.setPositiveButton("확인", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTING_ACTIVITY_RESULT);
				}
			});
			dialog.setNagativeButton("취소", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					useGps = false;
					//Toast.makeText(getApplicationContext(), "네트워크에서 위치정보를 받아옵니다.", Toast.LENGTH_LONG).show();
					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locListener);

					LogUtil.D("listener = " + locListener);

					final Location loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					if(loc != null){
						currAddr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
						if(currAddr ==  null){
							LocationUtil.getGeoLocation(getApplicationContext(), loc, new HttpCallBack() {
								
								@Override
								public void onHttpCallBackListener(Document document, Header[] header) {
									JSONObject data = null;
									LogUtil.D(document.select("body").text());
									try {
										data = new JSONObject(document.select("body").text());
										JSONArray rs = data.getJSONArray("results");
										JSONObject json = (JSONObject) rs.get(0);
										currAddr = json.getString("formatted_address");
										showDialog(currAddr, loc.getLatitude(), loc.getLongitude());
									} catch (JSONException e) {

										e.printStackTrace();
									}
								}
							});
						}						
					}
				}
			});

			dialog.show();
			handle.postDelayed(run, waittime);
		}else{
			useGps = true;
			//Toast.makeText(getApplicationContext(), "GPS에서 위치정보를 받아옵니다.", Toast.LENGTH_LONG).show();

			String provider = locManager.getBestProvider(crit, true);
			LogUtil.D("best provider = " + provider);

			if(provider == null)
				provider = LocationManager.NETWORK_PROVIDER;
			
			locManager.requestLocationUpdates(provider, minTime, minDistance, locListener);

			LogUtil.D("listener = " + locListener);

			final Location loc = locManager.getLastKnownLocation(provider);
			if(loc != null){
				currAddr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
				if(currAddr ==  null){
					LocationUtil.getGeoLocation(getApplicationContext(), loc, new HttpCallBack() {
						
						@Override
						public void onHttpCallBackListener(Document document, Header[] header) {
							JSONObject data = null;
							LogUtil.D(document.select("body").text());
							try {
								data = new JSONObject(document.select("body").text());
								JSONArray rs = data.getJSONArray("results");
								JSONObject json = (JSONObject) rs.get(0);
								currAddr = json.getString("formatted_address");
								showDialog(currAddr, loc.getLatitude(), loc.getLongitude());
							} catch (JSONException e) {

								e.printStackTrace();
							}
						}
					});
				}
			}

			handle.postDelayed(run, waittime);
		}
	}

	private void startGetLocation(){
		final DefaultDialog dialog = new DefaultDialog(this);
		dialog.setTitle("위치정보 서비스 안내");
		dialog.setMessage("현재 위치정보 사용에 동의합니다.");
		dialog.setPositiveButton("확인", new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
				String provider = locManager.getBestProvider(crit, true);
				LogUtil.D("best provider = " + provider);
				if(provider == null)
					provider = LocationManager.NETWORK_PROVIDER;

				locManager.requestLocationUpdates(provider, minTime, minDistance, locListener);

				LogUtil.D("listener = " + locListener);

				final Location loc = locManager.getLastKnownLocation(provider);
				if(loc != null){
					currAddr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
					if(currAddr ==  null){
						LocationUtil.getGeoLocation(getApplicationContext(), loc, new HttpCallBack(){
							@Override
							public void onHttpCallBackListener(Document document, Header[] header) {
								JSONObject data = null;
								LogUtil.D(document.select("body").text());
								try {
									data = new JSONObject(document.select("body").text());
									JSONArray rs = data.getJSONArray("results");
									JSONObject json = (JSONObject) rs.get(0);
									currAddr = json.getString("formatted_address");
									showDialog(currAddr, loc.getLatitude(), loc.getLongitude());
								} catch (JSONException e) {

									e.printStackTrace();
								}
							}
						});
					}
				}

				handle.postDelayed(run, waittime);
			}
		});
		dialog.setNagativeButton("취소", new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.setTitle("위치정보 서비스 안내");
				dialog.setMessage("현재 위치정보 사용을 거절하셨습니다.\n위치재설정 또는 가입 정보 입력 시\n위치를 직접 선택해 주세요.");
				dialog.setPositiveButton("확인", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.setNagativeButton("취소", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		dialog.show();	
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == SETTING_ACTIVITY_RESULT){
//			// GPS로 부터 위치 정보를 업데이트 요청
//			if(LocationUtil.checkGPS(locManager)){
//				useGps = true;
//				Toast.makeText(getApplicationContext(), "GPS를 이용하여 위치정보를 받아옵니다.", Toast.LENGTH_LONG).show();
//			}else{
//				useGps = false;
//				Toast.makeText(getApplicationContext(), "네트워크에서 위치정보를 받아옵니다.", Toast.LENGTH_LONG).show();
//			}
//
//			String provider = locManager.getBestProvider(crit, true);
//
//			LogUtil.D("best provider = " + provider);
//
//			locManager.requestLocationUpdates(provider, minTime, minDistance, locListener);
//
//			Location loc = locManager.getLastKnownLocation(provider);
//			if(loc != null){
//				currAddr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
//				if(currAddr ==  null){
//					LocationUtil.getGeoLocation(getApplicationContext(), loc, callback);
//				}
//			}
//		}
//	}

	// 최종 주소지가 나왔을때..
	private void showDialog(String LocationString, final double lat, final double lng){
		
		locManager.removeUpdates(locListener);
		
		if(dialog != null){
			if(dialog.isShowing()){
				dialog.dismiss();
				dialog = null;
			}
		}
		dialog = new DefaultDialog(this);
		dialog.setBackgroundWhite();
		dialog.setTitle("위치정보 서비스 안내");
		dialog.setMessage(getResources().getString(R.string.location_info_dialog));
		dialog.setAddrMsg(LocationString);
		dialog.setPositiveButton("저장", new OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setUserLocation(getApplicationContext(), currAddr);
				Setting.setLatString(getApplicationContext(), String.valueOf(lat));
				Setting.setLngString(getApplicationContext(), String.valueOf(lng));
				handle.removeCallbacks(run);
				locManager.removeUpdates(locListener);
				locManager = null;
				Toast.makeText(getApplicationContext(), "위치가 저장되었습니다.", Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
		dialog.setNagativeButton("취소", new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				handle.removeCallbacks(run);
				handle.postDelayed(run, waittime);

				String provider = locManager.getBestProvider(crit, true);

				LogUtil.D("best provider = " + provider);
				if(provider == null)
					provider = LocationManager.NETWORK_PROVIDER;

				locManager.requestLocationUpdates(provider, minTime, minDistance, locListener);

			}
		});
		dialog.show();
	}
	
	private void initUI() {
		
		banner_view = (MWebView)findViewById(R.id.banner_view);
		banner_view.setWebViewClient(new MyWebClient());
		WebSettings set = banner_view.getSettings();
		set.setJavaScriptEnabled(false);
		set.setBuiltInZoomControls(false);
		set.setDomStorageEnabled(true);
		banner_view.loadUrl(UrlDef.BANNER_URL);
		
		
		btn_select_moms = (LinearLayout)findViewById(R.id.btn_select_moms_big);
		btn_select_moms.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeAllCallback();
				Setting.setListType(getApplicationContext(), "M");
				
				LoginProc();
			}
		});

		btn_select_baby_sitter = (LinearLayout)findViewById(R.id.btn_select_baby_sitter_big);
		btn_select_baby_sitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeAllCallback();
				Setting.setListType(getApplicationContext(), "B");
				LoginProc();
			}			
		});
	}

	private void removeAllCallback(){
		try{
			locManager.removeUpdates(locListener);
			if(run != null){
				handle.removeCallbacks(run);
			}
			if(input_dialog != null){
				input_dialog.dismiss();
				input_dialog = null;
			}
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		try{
			if(locManager != null){
				locManager.removeUpdates(locListener);
			}
			handle.removeCallbacks(run);
			if(input_dialog != null){
				input_dialog.dismiss();
				input_dialog = null;
			}
			if(dialog != null){
				dialog.dismiss();
				dialog = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	class MyWebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.startsWith("http://")){
				Intent ii = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
				return true;
			}
			
			return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
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
	}
}