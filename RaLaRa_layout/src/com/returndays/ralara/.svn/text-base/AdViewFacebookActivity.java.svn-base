package com.returndays.ralara;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import com.androidquery.util.XmlDom;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class AdViewFacebookActivity  extends Dialog  {
		
		ProgressDialog mDialog;
		WebView mWebView;
		Activity mActivity;
		String mCampaignIdx;
		ImageButton mCloseBtn;
		public AdViewFacebookActivity(Activity activity, String url, String campaingIdx) {
			super(activity, R.style.Dialog);
			setContentView(R.layout.webview);
			mActivity = activity;
			mCampaignIdx = campaingIdx;
			mCloseBtn = (ImageButton) findViewById(R.id.close_btn);
			mWebView = (WebView) findViewById(R.id.web_view);
			mDialog = new ProgressDialog(mActivity);
			mDialog.setMessage("로딩중 입니다.\n잠시만 기다려주세요.");
			
			WebSettings setting = mWebView.getSettings();
			setting.setJavaScriptEnabled(true);
			setting.setSavePassword(true);
			mWebView.setWebChromeClient(new WebChromeClient() {
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
			});
			
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public void onLoadResource(WebView view, String url) {
					LogUtil.W("onLoadResource:"+url);
					if(url.contains("m.facebook.com/a/profile.php?fan")) {
						new AlertDialog.Builder(view.getContext())
						.setMessage("좋아요를 하셨습니다.\n참여해 주셔서 감사합니다")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								/*
								HttpUtil httpUtil = new HttpUtil(mActivity);
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("campaignidx", mCampaignIdx);
								httpUtil.httpExecute(UrlDef.SAVEMONEY, params, new HttpListener() {
									@Override
									public void onSuccess(XmlDom xml, HttpResultDto result) {
										if(result.isSuccess) {
											String retCode= xml.tag("ReturnTable").tag("Result").text();
											MadUtil.PointResultToast(mActivity, retCode);
											AdViewFacebookActivity.this.dismiss();
											mActivity.finish();
										}
									}
								}, true);
								*/
								AdViewFacebookActivity.this.dismiss();
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
					super.onPageFinished(view, url);
				}
				
				

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					try {
						
					} catch (Exception e) {}
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
				
				

			});
			
			mCloseBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/*
					DialogUtil.confirm(mActivity, "알림", "좋아요 버튼을 누르셔야 적립이 됩니다.\n창을 닫으시겠습니까?", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							AdViewFacebookActivity.this.dismiss();
						}
					});
					*/
				}
			});
			
			mWebView.loadUrl(url);
		}
		
		@Override
		public void show() {
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.copyFrom(this.getWindow().getAttributes());
			lp.gravity = Gravity.CENTER;
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = LayoutParams.MATCH_PARENT;
			super.show();
			this.getWindow().setAttributes(lp);

		}

		@Override
		public void onBackPressed() {
			if(mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				super.onBackPressed();
			}
		}
		
	
		
		
		
	}
