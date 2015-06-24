package com.returndays.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.returndays.ralara.preference.Setting;

public class MWebView extends WebView {
	Context mContext;
	ProgressDialog mDialog;
	Activity mActivity;

	protected OnEdgeTouchListener onEdgeTouchListener;
	public static enum DIRECTION { LEFT, RIGHT, NONE };

	public MWebView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public MWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public MWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mDialog = new ProgressDialog(mContext);
		mDialog.setTitle("로딩 중 입니다.");
		mDialog.setMessage("잠시만 기다려주세요.....");

		WebSettings setting = this.getSettings();
		setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
		setting.setJavaScriptEnabled(true);
		setting.setLoadWithOverviewMode(true);
		setting.setUseWideViewPort(true);
		setting.setDomStorageEnabled(true);

		this.setWebViewClient(new Mclient());
		this.setWebChromeClient(new WebChromeClient() {
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
			public boolean onJsConfirm(WebView view, String url,
					String message, JsResult result) {
				final JsResult finalRes = result;
				new AlertDialog.Builder(view.getContext())
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finalRes.confirm();  
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.setCancelable(false)
				.create().show();
				return true;
			}

		});

		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalScrollbarOverlay(true);
		this.setVerticalScrollbarOverlay(true);
		this.addJavascriptInterface(new Hybrid(), "Hybrid");

	}

	public void setActivity(Activity activity) {
		mActivity = activity;
	}

	class Hybrid {

		public String getToken() {
			return Setting.getToken(mContext);
		}

		public String getUserSeq() {
			return Setting.getUserSeq(mContext);
		}

		public void back() {
			if(mActivity != null) {
				mActivity.finish();
			}
		}

	}


	class Mclient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			try {
				mDialog.cancel();
			} catch (Exception e) {
			}
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			try {
				mDialog.show();
			} catch (Exception e) {
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if(onEdgeTouchListener != null){
			if(getScrollX()==0){
				onEdgeTouchListener.onEdgeTouch(DIRECTION.LEFT);
			} else if((getScrollX() + getWidth())==computeHorizontalScrollRange()){
				onEdgeTouchListener.onEdgeTouch(DIRECTION.RIGHT);
			} else {
				onEdgeTouchListener.onEdgeTouch(DIRECTION.NONE);
			}

		}

	}

	
	public void setOnEdgeTouchListener(OnEdgeTouchListener l){

		this.onEdgeTouchListener = l;

	}

	public interface OnEdgeTouchListener {

		void onEdgeTouch(DIRECTION direction);

	}

}
