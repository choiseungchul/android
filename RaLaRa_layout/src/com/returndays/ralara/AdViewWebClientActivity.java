package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.returndays.customview.MWebView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class AdViewWebClientActivity extends BaseActivity{

	MWebView mWeb;
	String mUrl, mUrlPageName;
	String mAdSeq, mUserseq ;
	EditText mEditUri;
	ProgressBar progressBar;
	ProgressDialog mDialog;
	Boolean isPage;

	ImageView mBtn;
	HttpDocument mHttpUtil;
	boolean isView = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_PROGRESS); // 프로그레스
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callend);

		mHttpUtil = new HttpDocument(getApplicationContext());
		
		mUrl = getIntent().getExtras().getString("url");
		mUrlPageName = mUrl;
		mUrl = "http://m.ralara.net/facebook/"+ mUrl+".asp";
		mAdSeq = getIntent().getExtras().getString("ad_seq");
		mUserseq = getIntent().getExtras().getString("user_seq");

		final TextView txt_egg = (TextView) findViewById(R.id.top_r_cnt);
		final TextView txt_gold = (TextView) findViewById(R.id.top_m_cnt);
		final TextView txt_scratch = (TextView) findViewById(R.id.top_s_cnt);

		mEditUri = (EditText) findViewById(R.id.txt_urledit);
		mWeb  = (MWebView) findViewById(R.id.webviewControl);
		progressBar = (ProgressBar) findViewById(R.id.top_simple_pro);

		//mEditUri.setVisibility(View.GONE );

		mDialog = new ProgressDialog(getApplicationContext());
		mDialog.setMessage("로딩중 입니다.\n잠시만 기다려주세요.");


		mWeb.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWeb.getSettings().setJavaScriptEnabled(true);
		mWeb.setVerticalScrollbarOverlay(true);
		mWeb.getSettings().setSavePassword(true);
		mWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWeb.getSettings().setAppCacheEnabled(true);
		mWeb.getSettings().setPluginsEnabled(true);
		//mWeb.getSettings().setSupportMultipleWindows(true);
		mWeb.getSettings().setBuiltInZoomControls(false);
		mWeb.getSettings().setDomStorageEnabled(true);


		mWeb.loadUrl(mUrl);
		isPage = true;
		mEditUri.setText(mUrl);

		mWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				final JsResult finalRes = result;
				new AlertDialog.Builder(view.getContext())
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finalRes.confirm();  
					}
				})
				.setCancelable(false)
				.create().show();
				return true;
			}
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setProgress(newProgress);
			}
		});

		mWeb.setWebViewClient(new WebViewClient() {
			@Override
			public void onLoadResource(WebView view, String url) {
				LogUtil.W("onLoadResource:"+url);
				if(url.contains("facebook.com/plugins/likebox/connect") && isPage ) {
					new AlertDialog.Builder(view.getContext())
					.setMessage("좋아요를 하셨습니다.\n참여해 주셔서 감사합니다")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							/*	
							Intent intent = new Intent(getApplicationContext(), ScratchViewActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							finish();
							overridePendingTransition(0, 0);
							startActivity(intent);
							*/	
							/*
							HttpUtil httpUtil = new HttpUtil(getApplicationContext());
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("campaignidx", mCampaignIdx);
							httpUtil.httpExecute(UrlDef.SAVEMONEY, params, new HttpListener() {
								@Override
								public void onSuccess(XmlDom xml, HttpResultDto result) {
									if(result.isSuccess) {
										String retCode= xml.tag("ReturnTable").tag("Result").text();
										MadUtil.PointResultToast(getApplicationContext(), retCode);
										AdViewFacebookActivity.this.dismiss();
										getApplicationContext().finish();
									}
								}
							}, true);
							 */
							AdViewWebClientActivity.this.finish();
						}
					})
					.setCancelable(false)
					.create().show();
				}
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				LogUtil.W("onPageFinished");

				try {
					mDialog.cancel();
				} catch (Exception e) {}

				if(url.contains("facebook.com/plugins/close_popup")){
					view.loadUrl(mUrl);

				}



				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
			}



			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				try {
					if(url.contains(mUrlPageName) ){
						isPage = true;						
					}else{
						isPage = false;						
					}
					mEditUri.setText(url);				
					LogUtil.W("URL", url);

				} catch (Exception e) {}
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {

				String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
				String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
				String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

				txt_egg.setText(Egg);
				txt_gold.setText(Gold);
				txt_scratch.setText(Scratch);
			}

		}, false);


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
		finish();

	}

	@Override
	public void onStop() {
		super.onStop();
		//finish();

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode)
			{
			case KeyEvent.KEYCODE_BACK:
				if(mWeb.canGoBack() == true){
					mWeb.goBack();
				}else{
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}



	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

	}
}
