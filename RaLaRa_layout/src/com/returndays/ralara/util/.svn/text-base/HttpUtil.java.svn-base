package com.returndays.ralara.util;

import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.preference.Setting;

public class HttpUtil {

	private ProgressBar mProgressBar;
	private Context mContext;
	private FrameLayout mLayout;
	private final int PROGRESS_ID = 98314736;
	private HttpListener mHttpListener;

	/**
	 * 액티비티에서 호출
	 * @param activity
	 */
	public HttpUtil(Activity activity) {
		mContext = activity;
		mLayout = (FrameLayout) ((Activity) mContext).findViewById(android.R.id.content);
		progressAdd();
	}

	/**
	 * 액티비티가 없는 곳에서 호출
	 * @param context
	 */
	public HttpUtil(Context context) {
		mContext = context;
	}

	public void progressHide() {
		try {
			if(mProgressBar != null) {
				//MadUtil.disableEnableControls(true, mLayout);
				mProgressBar.setVisibility(View.GONE);
			}
		} catch (Exception e) {}
	}

	public void progressShow() {
		try {
			if(mProgressBar != null) {
				//MadUtil.disableEnableControls(false, mLayout);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {}
	}



	private void progressAdd() {
		try {
			if(mLayout.findViewById(PROGRESS_ID) == null) {
				int wrap = LayoutParams.WRAP_CONTENT;

				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(wrap, wrap);
				lp.gravity = Gravity.CENTER;

				mProgressBar = new ProgressBar(mContext, null, android.R.attr.progressBarStyleLarge);
				mProgressBar.setLayoutParams(lp);
				mProgressBar.setVisibility(View.GONE);
				mProgressBar.setId(PROGRESS_ID);


				mLayout.addView(mProgressBar);
			} else {
				mProgressBar = (ProgressBar) mLayout.findViewById(PROGRESS_ID);
			}
		} catch (Exception e) {}
	}
	


	public void httpExecute(String url, final Map<String, Object> params, final HttpListener httpListener, final boolean isProgress) {
		mHttpListener = httpListener;
		if(params != null && !url.equals(UrlDef.LOGIN_NOTK)) {
			params.put("token", Setting.getToken(mContext));
			params.put("userseq", Setting.getUserSeq(mContext));
		}

		if(params != null) {
			Set<String> keys = params.keySet();
			for(String key : keys) {
				LogUtil.I("http params ===>  " + key + " : " + params.get(key));
			}
		}
		
		LogUtil.D("is progress ::::::::::::::::::::::::::::::::::"+isProgress);

		if(isProgress) progressShow();
		AQuery aq = new AQuery(mContext);
		
		aq.ajax(url, params, XmlDom.class, new AjaxCallback<XmlDom>() {
			@Override
			public void callback(final String url, XmlDom xmlDom, AjaxStatus status) {
				LogUtil.W("status:"+ status.getCode() +", " + status.getError());
				
				if(isProgress) progressHide();
				
				if(xmlDom == null) {
					HttpResultDto result = new HttpResultDto();
					result.isSuccess = false;
					if(mLayout != null) {
						String msg = "";
						int code = status.getCode();
						switch (code) {
						case AjaxStatus.NETWORK_ERROR:
							msg = "NETWORK_ERROR";
							break;
						case AjaxStatus.AUTH_ERROR:
							msg = "AUTH_ERROR";
							break;
						case AjaxStatus.TRANSFORM_ERROR:
							msg = "TRANSFORM_ERROR";
							break;
						default: 
							msg = "UNKNOWN";
							break;
						}
						try {
							DialogUtil.confirm(mContext, "["+msg+"]\n"+mContext.getString(R.string.networkfail_msg), new View.OnClickListener() {
								@Override
								public void onClick(View dialog) {
									httpExecute(url, params, mHttpListener, isProgress);
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							mHttpListener.onSuccess(null, result);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else {
					HttpResultDto result = new HttpResultDto();
					if(xmlDom.tag("ResultTable") != null) {
						LogUtil.D("http result : " + xmlDom.tag("ResultTable").tag("Result").text());
						result.isSuccess = xmlDom.tag("ResultTable").tag("Result").text().equals("true");
					} else {
						result.isSuccess = true;
					}
					try {
						mHttpListener.onSuccess(xmlDom, result);
					} catch (Exception e) {
						LogUtil.E(e.toString());
					}
				}
				
			}
		});
	}
	
	public void httpExecute(String url, final Map<String, Object> params, final HttpListener httpListener, final boolean isProgress, final boolean isAlert) {
		mHttpListener = httpListener;
		if(params != null && !url.equals(UrlDef.LOGIN)) {
			params.put("token", Setting.getToken(mContext));
			params.put("userseq", Setting.getUserSeq(mContext));
		}

		if(params != null) {
			Set<String> keys = params.keySet();
			for(String key : keys) {
				LogUtil.I("http params ===>  " + key + " : " + params.get(key));
			}
		}


		if(isProgress) progressShow();
		AQuery aq = new AQuery(mContext);
		aq.ajax(url, params, XmlDom.class, new AjaxCallback<XmlDom>() {
			@Override
			public void callback(final String url, XmlDom xmlDom, AjaxStatus status) {
				LogUtil.W("status:"+ status.getCode() +", " + status.getError());
				if(isProgress) progressHide();
				
				if(xmlDom == null) {
					HttpResultDto result = new HttpResultDto();
					result.isSuccess = false;
					if(mLayout != null) {
						String msg = "";
						int code = status.getCode();
						switch (code) {
						case AjaxStatus.NETWORK_ERROR:
							msg = "NETWORK_ERROR";
							break;
						case AjaxStatus.AUTH_ERROR:
							msg = "AUTH_ERROR";
							break;
						case AjaxStatus.TRANSFORM_ERROR:
							msg = "TRANSFORM_ERROR";
							break;
						}
						try {
							if(isAlert)
							DialogUtil.confirm(mContext, msg+"\n"+mContext.getString(R.string.networkfail_msg), new View.OnClickListener() {
								@Override
								public void onClick(View dialog) {
									httpExecute(url, params, mHttpListener, isProgress);
								}
							});
						} catch (Exception e) {
							
						}
					} else {
						try {
							mHttpListener.onSuccess(null, result);
						} catch (Exception e) {}
					}

				} else {
					HttpResultDto result = new HttpResultDto();
					if(xmlDom.tag("ResultTable") != null) {
						LogUtil.D("http result : " + xmlDom.tag("ResultTable").tag("Result").text());
						result.isSuccess = xmlDom.tag("ResultTable").tag("Result").text().equals("true");
					} else {
						result.isSuccess = true;
					}
					try {
						mHttpListener.onSuccess(xmlDom, result);
					} catch (Exception e) {
						LogUtil.E(e.toString());
						
					}
				}
				
			}
		});
	}
	
}
