package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.returndays.customview.MWebView;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.util.LogUtil;

public class RewardWebActivity extends BaseActivity{

	MWebView mWeb;
	String mUrl, mTitle;
	LinearLayout back_btn;
	ProgressBar web_loader;
	Button share_icon;
	TextViewNanumGothic back_btn_txt;
	ValueCallback<Uri> mUploadMessage;
	String s_url = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_web_reward);

		back_btn_txt = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text_sub);

		mUrl = getIntent().getExtras().getString("url");
		mTitle = getIntent().getExtras().getString("title");
		if(mTitle != null){
			back_btn_txt.setText(mTitle);
		}

		setShortUrl();
		
		mWeb  = (MWebView) findViewById(R.id.set_webpage);
		web_loader = (ProgressBar)findViewById(R.id.set_webpage_loader);

		mWeb.setWebViewClient(new MyWebClient(){
//			@SuppressWarnings("unused")
//			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//				mUploadMessage = uploadMsg;
//				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//				i.addCategory(Intent.CATEGORY_OPENABLE);
//				i.setType("*/*");
//				SettingWebActivity.this.startActivityForResult(Intent.createChooser(i, "사진을 선택하세요"),FILECHOOSER_RESULTCODE);
//			}
		});
		mWeb.getSettings().setRenderPriority(RenderPriority.HIGH);
		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);
		set.setDatabaseEnabled(true);
		set.setDomStorageEnabled(true);
		set.setAppCacheEnabled(true);
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		set.setRenderPriority(WebSettings.RenderPriority.HIGH);
		set.setSupportZoom(false);
		set.setAllowFileAccess(true);
		set.setSavePassword(false);
		set.setSupportMultipleWindows(false);
		set.setAppCachePath("");
		set.setAppCacheMaxSize(5*1024*1024);
		set.setPluginsEnabled(true);
		//		set.setPluginState(PluginState.ON);

		mWeb.loadUrl(mUrl);

		share_icon = (Button) findViewById(R.id.share_icon);
		share_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				share_click();
			}
		});
		share_icon.setClickable(false);
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void share_click(){
	    Intent emailIntent = new Intent();
	    emailIntent.setAction(Intent.ACTION_SEND);
	    emailIntent.putExtra(Intent.EXTRA_TEXT, mUrl);
	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
	    emailIntent.setType("message/rfc822");

	    PackageManager pm = getPackageManager();
	    Intent sendIntent = new Intent(Intent.ACTION_SEND);     
	    sendIntent.setType("text/plain");
//
	    Intent openInChooser = Intent.createChooser(emailIntent, "당첨 공유 하기");

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();      
	    
	    for (int i = 0; i < resInfo.size(); i++) {
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        if(packageName.contains("twitter") || packageName.contains("com.facebook") 
	        		|| packageName.contains("com.nhn.android.band") ||  packageName.contains("com.kakao") ) {

	            Intent intent = new Intent();
	            intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	            intent.setAction(Intent.ACTION_SEND);
	            intent.setType("text/plain");
	            
	            intent.putExtra(Intent.EXTRA_TEXT, s_url);
	            intent.putExtra(Intent.EXTRA_SUBJECT, "[랄라라] 당첨!! ");
	            
	            
	            if(packageName.contains("twitter")) {
	            	intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	            } else if(packageName.contains("com.facebook")) {
	            	intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	            } else if(packageName.contains("com.kakao")){
	            	intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	            } else if(packageName.contains("com.nhn.android.band")){
	            	intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
	            } 
	            
	        }
	    }

	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);
	    
	    LogUtil.D( "intents size = " + intentList.size());
	    
	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
	    startActivity(openInChooser); 
	}

	private void setShortUrl(){
		JSONObject json = new JSONObject();
	    
	    final HttpDocument getAccessToken = new HttpDocument(getApplicationContext());
        Hashtable<String, String> params = new Hashtable<String, String>();
               
        try {
			json.put("key",Define.GOOGLE_KEY);
			json.put("longUrl", mUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        getAccessToken.setMethod("POST");
        getAccessToken.setEntity(json);
        
        getAccessToken.getDocument("https://www.googleapis.com/urlshortener/v1/url", params, null, new com.returndays.http.HttpDocument.HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				String json = document.select("body").text();
				try {
					JSONObject data = new JSONObject(json);
					s_url =  data.getString("id");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				share_icon.setClickable(true);
				getAccessToken.threadStop();
			}
		}, false);
	}
	
	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			web_loader.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}
	}
}
