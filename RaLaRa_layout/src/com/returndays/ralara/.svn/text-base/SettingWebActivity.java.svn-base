package com.returndays.ralara;

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

public class SettingWebActivity extends BaseActivity{

	MWebView mWeb;
	String mUrl, mTitle;
	Button save_btn; 
	LinearLayout back_btn;
	ProgressBar web_loader;
	TextViewNanumGothic back_btn_txt;
	ValueCallback<Uri> mUploadMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_web);

		back_btn_txt = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text_sub);

		mUrl = getIntent().getExtras().getString("url");
		mTitle = getIntent().getExtras().getString("title");
		if(mTitle != null){
			back_btn_txt.setText(mTitle);
		}

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

		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
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
