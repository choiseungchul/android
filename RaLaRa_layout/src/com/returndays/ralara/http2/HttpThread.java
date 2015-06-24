package com.returndays.ralara.http2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.google.api.client.http.HttpResponse;
import com.returndays.ralara.util.LogUtil;

public class HttpThread extends Thread{

	private String url = null;
	private HashMap<String, Object> param = null;
	private HttpCallBack callback;
	
	public HttpThread(String url, HashMap<String, Object> param, HttpCallBack callback) {
		this.url = url;
		this.param = param;
		this.callback = callback;
	}
	
	@SuppressLint("HandlerLeak")
	Handler h = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				String html = (String) msg.obj;
				Document docu = Jsoup.parse(html, "", Parser.xmlParser());
				callback.onHttpCallBackListener(docu);
				interrupt();
			}
		}
	};
	
	@Override
    public void run() {
		HttpResponse response = HttpMethod.getInstance().HttpGet(url, param);
		
		Message msg = Message.obtain();
		
		if (response.getStatusCode() == 200){
			msg.what = 0;
			StringBuffer sb = new StringBuffer();
			
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getContent()));
				String line = null;
				while( (line = br.readLine()) != null){
					sb.append(line);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			msg.obj = sb.toString();
			
			h.sendMessage(msg);
			
		}		
	}
	
	@Override
	protected void finalize() throws Throwable {
		LogUtil.D("http thread finalized............");
		super.finalize();
	}
	
}
