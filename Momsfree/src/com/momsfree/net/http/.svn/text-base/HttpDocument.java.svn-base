package com.momsfree.net.http;

import java.io.File;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.momsfree.net.R;
import com.momsfree.net.conf.Define;
import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.LogUtil;


public class HttpDocument {
	Context mContext;
	int jobId = 0;
	ProgressDialog dialog;
	Queue<Job> jobQueue = new LinkedList<Job>();
	String MethodType = "POST";

	HttpThead httpThead = new HttpThead();

	public HttpDocument(Context context) {
		mContext = context;
	}

	public void setMethod(String type){
		MethodType = type;
	}

	public void getDocument(String url, Hashtable<String, String> params,
			Hashtable<String, File> files, HttpCallBack callBack, boolean isDialog) {

		synchronized (jobQueue) {
			Job job = new Job();
			job.jobId = jobId;
			job.isDialog = isDialog;
			job.mCallBackListner = callBack;
			job.mUrl = url;
			job.mParams = params;
			job.mFiles = files;

			jobQueue.offer(job);
			jobQueue.notify();
			jobId++;
		}

		if(httpThead.getState()==Thread.State.NEW)
			httpThead.start();
	}

	class Job {
		int jobId;
		boolean isDialog;
		HttpCallBack mCallBackListner;
		Hashtable<String, String> mParams;
		Hashtable<String, File> mFiles;
		String mUrl;
		Document document;
		Header[] header;
	}

	static public interface HttpCallBack {
		public void onHttpCallBackListener(Document document, Header[] header);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				if(msg.arg1 == 0) {
					try {
						dialog.dismiss();
					} catch(Exception e) {e.printStackTrace();}
				}
				Job job = (Job) msg.obj;
				if(job.document == null) {
					Toast.makeText(mContext, mContext.getString(R.string.http_null_error), Toast.LENGTH_SHORT).show();
					Log.e("1", job.mUrl + " : document null!");
					return;
				}
				job.mCallBackListner.onHttpCallBackListener(job.document, job.header);
			} else {
				if(msg.arg1 == 0) {
					dialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.load), true, true);
				}
			}
		}
	};

	public void threadStop() {
		httpThead.interrupt();
	}

	class HttpThead extends Thread {

		@Override
		public void run() {
			try {

				while(true) {

					if(jobQueue.size() == 0) {
						synchronized (jobQueue) {
							jobQueue.wait();
						}
					} 
					if(jobQueue.size() != 0) {
						Job job;
						synchronized (jobQueue) {
							job = jobQueue.poll();
						}
						//						Log.d("1", "jobj:" + job.jobId + ", murl:" + job.mUrl + ", jobQueue size:"+jobQueue.size());
						Message msg = Message.obtain();
						msg.what = 1;
						msg.arg1 = job.isDialog ? 0 : 1;
						mHandler.sendMessage(msg);


						Document document = null;
						Header[] headerData = null;
						if(job.mFiles == null) {
							try {

								if(MethodType.equals("POST")){

									HttpData httpData = null;
									HttpClient client = new DefaultHttpClient();

									try {
										httpData = HttpRequest.postRequest(client, 
												job.mUrl, job.mParams, job.mFiles);
										//										Log.d("1", "@@:"+httpData.data);
									} catch (Exception e) {
										e.printStackTrace();
									}

									headerData = httpData.response.getAllHeaders();

//									if(job.mUrl.equals( UrlDef.LOGIN )){
//										setCookies((DefaultHttpClient)client);
//									}

									document = Jsoup.parseBodyFragment(httpData == null ? "" : httpData.data);
								}
								else if(MethodType.equals("GET")){

									int i = 0;
									StringBuilder sb = new StringBuilder();
									sb.append(job.mUrl);

									if(job.mParams!=null) {
										Enumeration<String> keys = job.mParams.keys();
										while(keys.hasMoreElements()) {
											String key = keys.nextElement();
											sb.append(i==0?"?":"&")
											.append(key).append("=")
											.append(URLEncoder.encode(job.mParams.get(key), "UTF-8"));
											i++;
										}
									}

									HttpData httpData = null;
									HttpClient client = new DefaultHttpClient();

									try {
										httpData = HttpRequest.getRequest(client, sb.toString());
										//										Log.d("1", "@@:"+httpData.data);
									} catch (Exception e) {
										e.printStackTrace();
									}

									headerData = httpData.response.getAllHeaders();

									if(job.mUrl.equals( UrlDef.LOGIN )){
										setCookies((DefaultHttpClient)client);
									}

									document = Jsoup.parseBodyFragment(httpData == null ? "" : httpData.data);	
								}
								//						    	Log.d("1", sb.toString());
								// 10�� 

							} catch(Exception e) {
								e.printStackTrace();
							}

						} else {
							HttpData httpData = null;
							HttpClient client = new DefaultHttpClient();
							try {
								httpData = HttpRequest.postRequest(client, 
										job.mUrl, job.mParams, job.mFiles);
								//								Log.d("1", "@@:"+httpData.data);
							} catch (Exception e) {
								e.printStackTrace();
							}

							headerData = httpData.response.getAllHeaders();

							if(job.mUrl.equals( UrlDef.LOGIN )){
								setCookies((DefaultHttpClient)client);
							}

							document = Jsoup.parseBodyFragment(httpData == null ? "" : httpData.data);
						}
						job.document = document;
						job.header = headerData;

						Message msg2 = Message.obtain();
						msg2.what = 0;
						msg2.arg1 = job.isDialog ? 0 : 1;
						msg2.obj = job;
						mHandler.sendMessage(msg2);
					}


					if(Thread.interrupted())
						break;
				}
			} catch(InterruptedException e) {

			}

		}
	}

	private void setCookies(DefaultHttpClient client){
		
		List<Cookie> cookies = ((DefaultHttpClient)client).getCookieStore().getCookies();

		if (!cookies.isEmpty()) {

			LogUtil.D("set cookies...");

			for (int i = 0; i < cookies.size(); i++) {

				CookieSyncManager.createInstance(mContext);
				CookieManager cookieManager = CookieManager.getInstance();

				cookieManager.removeAllCookie();

				Cookie sessionInfo = null;
				StringBuffer sb = new StringBuffer();
				
				for (Cookie cookie : cookies ) {
					sessionInfo = cookie;
					String cookieString = sessionInfo.getName() + "="
							+ sessionInfo.getValue() + "; Path="
							+ sessionInfo.getPath();
					cookieManager.setCookie(sessionInfo.getDomain(),cookieString);
					
					LogUtil.D("cookieString = " + cookieString);
					
					sb.append(sessionInfo.getName() + "=" + sessionInfo.getValue());
					
					CookieSyncManager.getInstance().sync();
					
				}
				
				Setting.setCookieString(mContext, sb.toString());
			}
		}

	}
}
