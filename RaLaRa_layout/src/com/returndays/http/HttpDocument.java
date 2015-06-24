package com.returndays.http;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.returndays.ralara.util.LogUtil;


public class HttpDocument {
	Context mContext;
	int jobId = 0;
	ProgressDialog dialog;
	long wait = 30000;
	Queue<Job> jobQueue = new LinkedList<Job>();
	int threadStopCount = 0;
	boolean isRepeat = false;  
	JSONObject jsonObj = null;
	
	HttpThead httpThead = new HttpThead();
	String MethodType = "GET"; 
	
	public HttpDocument(Context context) {
		mContext = context;
	}
	
	public void setEntity(JSONObject json){
		jsonObj = json;
	}
	
	public void setMethod(String type){
		MethodType = type;
	}
	
	public void setRequestRepeat(boolean flag){
		isRepeat = flag;
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
		
		if(httpThead.getState()==Thread.State.NEW){
			httpThead.start();
		}else{
			LogUtil.D("httpThread state = " + httpThead.getState());
		}
			
	}
	
	class Job {
		int jobId;
		boolean isDialog;
		HttpCallBack mCallBackListner;
		Hashtable<String, String> mParams;
		Hashtable<String, File> mFiles;
		String mUrl;
		Document document;
	}
	
	static public interface HttpCallBack {
		public void onHttpCallBackListener(Document document);
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
					Toast.makeText(mContext, mContext.getString(com.returndays.ralara.R.string.http_null_error), Toast.LENGTH_SHORT).show();
//					Log.e("1", job.mUrl + " : document null!");
					job.document = new Document("<?xml version=\"1.0\" encoding=\"utf-8\"?><error>NetworkError</error>");
					job.mCallBackListner.onHttpCallBackListener(job.document);
					return;
				}
				
				job.mCallBackListner.onHttpCallBackListener(job.document);
			} else {
				if(msg.arg1 == 0) {
					dialog = ProgressDialog.show(mContext, null, mContext.getString(com.returndays.ralara.R.string.load), true, true);
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
							jobQueue.wait(wait);
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
						if(job.mFiles == null) {
							
							if( MethodType.equals("GET")){
								try {
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
//							    	Log.d("1", sb.toString());
							    	 // 10�� 
						    		document = Jsoup.parse(new URL(sb.toString()), 30000);//document = Jsoup.parse(new URL(sb.toString()), 8000);
								} catch(Exception e) {
									e.printStackTrace();
								}
							}else if(MethodType.equals("POST")){
								HttpData httpData = null;
								HttpClient client = new DefaultHttpClient();
								try {
									if( jsonObj == null){
										httpData = HttpRequest.postRequest(client, 
												job.mUrl, job.mParams, null);
//										Log.d("1", "@@:"+httpData.data);
									}else{
										httpData = HttpRequest.postRequestWithContentType(client, 
												job.mUrl, job.mParams, null, jsonObj);
//										Log.d("1", "@@:"+httpData.data);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								document = Jsoup.parseBodyFragment(httpData == null ? "" : httpData.data);
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
							document = Jsoup.parseBodyFragment(httpData == null ? "" : httpData.data);
						}
						job.document = document;
						
						Message msg2 = Message.obtain();
						msg2.what = 0;
						msg2.arg1 = job.isDialog ? 0 : 1;
						msg2.obj = job;
						mHandler.sendMessage(msg2);
						
					}
					
					if(Thread.interrupted()){
						LogUtil.D("thread INTERRUPTED--------------------");
						break;
					}else{
//						threadStopCount++;
//						LogUtil.D("thread NOT INTERRUPTED!!");
//						if(!isRepeat){
//							if(threadStopCount > 2){
//								threadStop();
//							}
//						}
					}
				}
			} catch(InterruptedException e) {
				LogUtil.D("thread interrupted -----------------------------");
			}
			
		}
	}
}
