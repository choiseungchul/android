package com.momsfree.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.AddrSrchDialog;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.customview.FindAddrDialog;
import com.momsfree.net.customview.InputDialog;
import com.momsfree.net.customview.MWebView;
import com.momsfree.net.customview.TextViewNanumGothic;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;

public class WebActivityNonMenu extends BaseActivity{
	MWebView mWeb;
	String mUrl, mTitle;
	Button save_btn; 
	ImageButton home_btn;
//	ProgressBar web_loader;
	TextViewNanumGothic top_title, webpage_network_error;
	LinearLayout menu1_btn, menu2_btn, menu3_btn, menu4_btn;
	ImageButton loc_btn;
	String UserType = "";
	int mWebId = -1;
	int mWebLoaderId = -1;
	private DefaultDialog dialog;
	private InputDialog input_dialog;
	private Handler handle = new Handler();
	Runnable run;
	long waittime = 10000;
	
	int id_homebtn, id_toptitle, id_locbtn;
	
	AddrSrchDialog addrDialog;
	FindAddrDialog findDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		UserType = Setting.getUserType(getApplicationContext());
		
		LogUtil.D("user_type = " + UserType);
		
	
		if(UserType.equals("") || UserType.equals("M")){
			setContentView(R.layout.webview_non_menu_blue);
			mWebId = R.id.set_webpage_non_menu_blue;
//			mWebLoaderId = R.id.wn_loader_blue;
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
		}else{
			setContentView(R.layout.webview_non_menu_pink);
			mWebId = R.id.set_webpage_non_menu_pink;
//			mWebLoaderId = R.id.wn_loader_pink;
			id_homebtn = R.id.home_btn_pink;
			id_toptitle = R.id.top_title_pink;
			id_locbtn = R.id.loc_btn_pink;
		}

		run = new Runnable() {
			@Override
			public void run() {
				showInputDialog();
			}
		};
		
		initUI();
		initData();

		mUrl = getIntent().getExtras().getString("url");
		mTitle = getIntent().getExtras().getString("title");
		if(mTitle != null){
			top_title.setText(mTitle);
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
		
		set.setJavaScriptCanOpenWindowsAutomatically(true);
		set.setGeolocationEnabled(true);
		set.setGeolocationDatabasePath("/data/data/com.momsfree.net/");

		mWeb.loadUrl(mUrl);

		home_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}


	private void initData() {

	}


	private void initUI() {
		home_btn = (ImageButton)findViewById(id_homebtn);
		home_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				overridePendingTransition(0,0);
				finish();
			}
		});
		top_title = (TextViewNanumGothic)findViewById(id_toptitle);
		loc_btn = (ImageButton)findViewById(id_locbtn);
		loc_btn.setVisibility(View.INVISIBLE);
//		loc_btn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//				if(MomsUtil.CheckInternet(getApplicationContext())){
//					GPSTracker track = new GPSTracker(getApplicationContext());
//					if(track.canGetLocation()){
//						Location loc = track.getLocation();
//						String addr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
//						if(addr ==null){
//							LocationUtil.getGeoLocation(getApplicationContext(), loc, new HttpCallBack(){
//								@Override
//								public void onHttpCallBackListener(
//										Document document, Header[] header) {
//									JSONObject data = null;
//
//									try {
//										data = new JSONObject(document.select("body").text());
//										JSONArray rs = data.getJSONArray("results");
//										JSONObject json = (JSONObject) rs.get(0);
//										String user_addr = json.getString("formatted_address");
//										showDialog(user_addr);
//									} catch (JSONException e) {
//
//										e.printStackTrace();
//									}
//									
//								}
//							});
//							
//						}else{
//							showDialog(addr);
//						}
//					}else{
//						handle.postDelayed(run, waittime);
//						
//						LogUtil.D("GPSTracker can not track");
//					}
//				}else{
//					final DefaultDialog dialog = new DefaultDialog(WebActivityNonMenu.this);
//					dialog.setTitle("알림");
//					dialog.setMessage("네트워크 연결이 필요합니다.");
//					dialog.isNegativeButton(false);
//					dialog.show();
//					dialog.setPositiveButton("확인", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							dialog.dismiss();
//						}
//					});
//				}
//			}
//		});
		
	}

	private void showInputDialog(){

		if(dialog == null){

			//둘다안될경우는 입력창을 띄운다.
			input_dialog = new InputDialog(WebActivityNonMenu.this);
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
						dialog = new DefaultDialog(WebActivityNonMenu.this);
						dialog.setTitle("주소 확인");
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
	
	// 최종 주소지가 나왔을때..
		private void showDialog(final String LocationString){
			findDialog = new FindAddrDialog(WebActivityNonMenu.this);
			findDialog.setTitle(LocationString);
			findDialog.setSrchbtn(new OnClickListener() {
				@Override
				public void onClick(View v) {
					findDialog.dismiss();
					addrDialog = new AddrSrchDialog(WebActivityNonMenu.this);
					addrDialog.setTitle(LocationString);
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
			
			findDialog.show();
		}

	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			
			if (url.startsWith("momsfree://joincomplete/")) {
				Intent i = new Intent(getApplicationContext(), JoinCompleteActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.setData(Uri.parse(url));
				startActivity(i);
				overridePendingTransition(0,0);

			}else if(url.startsWith("momsfree://logout/")){
				MomsUtil.clearAllPrefferences(getApplicationContext());
				Intent i = new Intent(getApplicationContext(), LoginActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				overridePendingTransition(0,0);
			}else if(url.startsWith("tel:")){
				try {
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse(url));
					startActivity(callIntent);
					overridePendingTransition(0,0);
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
			super.onPageFinished(view, url);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		if(!mWeb.canGoBack()){
			super.onBackPressed();
			// TODO Auto-generated method stub
//			final DefaultDialog dialog = new DefaultDialog(this);
//			dialog.setTitle("알림");
//			dialog.setMessage("프로그램을 종료하시겠습니까?");
//			dialog.setPositiveButton("예", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//					finish();
//				}
//			});
//			dialog.setNagativeButton("아니오", new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			});
//			dialog.show();
		}else{
			mWeb.goBack();
		}
	}
}
