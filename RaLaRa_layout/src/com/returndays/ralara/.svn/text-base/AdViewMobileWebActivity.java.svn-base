package com.returndays.ralara;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.bool;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.util.XmlDom;
import com.returndays.customview.MWebView;
import com.returndays.customview.MWebView.DIRECTION;
import com.returndays.customview.MWebView.OnEdgeTouchListener;
import com.returndays.ralara.CallEndActivity.MyWebClient;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.service.AdDownloadService;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;


public class AdViewMobileWebActivity extends BaseActivity {
	
	MWebView mWeb;
	String mUrl;
	String mAdSeq, mUserseq ;
	HttpUtil mHttpUtil, mHttpUtil2;
	private static final String PARAMURL="url", PARAMADSEQ ="adseq", PARAMUSERSEQ ="userseq" ; 
	TextView mTxtUrl, mTxtAdSeq , mTxtUserseq;
		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
				
		getWindow().requestFeature(Window.FEATURE_PROGRESS); // 프로그레스
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adviewmobileweb);
		
		mTxtUrl = (TextView) findViewById(R.id.ralara_txt_id);
		mTxtAdSeq = (TextView) findViewById(R.id.ralara_txt_id2);
		mTxtUserseq = (TextView) findViewById(R.id.ralara_txt_id3);
		
		
		Uri data = getIntent().getData();				
		if(data != null) {									
			String paramurl	= data.getQueryParameter(PARAMURL);	
			//mTxtUrl.setText(paramurl);
			
			String paramadseq = data.getQueryParameter(PARAMADSEQ);	
			//mTxtAdSeq.setText(paramadseq);
			
			String paramuserseq = data.getQueryParameter(PARAMUSERSEQ);	
			//mTxtUserseq.setText(paramuserseq);
			
			
			/*	
			Intent intent = new Intent(getApplicationContext(), ScratchViewActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			overridePendingTransition(0, 0);
			startActivity(intent);
			*/
			
			
		}
		
		
		/*
		mUrl = getIntent().getExtras().getString("url");
		mAdSeq = getIntent().getExtras().getString("ad_seq");
		mUserseq  = getIntent().getExtras().getString("user_seq");
		
		//mUrl = "http://m.naver.com";

		progressBar = (ProgressBar) findViewById(R.id.adtop_progressbar);

		
		mWeb  = (MWebView) findViewById(R.id.webviewControl);
		mWeb.setWebViewClient(new MyWebClient());
		mWeb.getSettings().setRenderPriority(RenderPriority.HIGH);
		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);
		set.setDomStorageEnabled(true);
		mWeb.loadUrl(mUrl);
		mEditUri.setText(mUrl);

		mWeb.setWebViewClient(new MyWebClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				try {

				} catch (Exception e) {
				}
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				try {
				} catch (Exception e) {
				}
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);

			};

		});

		mWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setProgress(newProgress);
			}
		});

		mWeb.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				
				return false;
			}
		});
		*/


	}
	@Override
	public void onRestart() {

		super.onRestart();
		mUrl = getIntent().getExtras().getString("url");
		mAdSeq = getIntent().getExtras().getString("ad_seq");
		mWeb.loadUrl(mUrl);
		

		
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	
	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

	}
}
