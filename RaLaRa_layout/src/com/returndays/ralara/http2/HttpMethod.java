package com.returndays.ralara.http2;

import java.io.IOException;
import java.util.HashMap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

public class HttpMethod {

	private static HttpMethod _instance;
	HttpTransport http;
	
	public HttpMethod(){
		 http = AndroidHttp.newCompatibleTransport();
	}
	
	public static HttpMethod getInstance(){
		if(_instance == null) return new HttpMethod();
		else return _instance;
	}
	
	public HttpResponse HttpGet(String url, HashMap<String, Object> param){
		GenericUrl g_url = new GenericUrl(url);
		if(param != null)
		g_url.putAll(param);
		HttpResponse res = null;
		try {
			HttpRequest req = http.createRequestFactory().buildGetRequest(g_url);
			res =  req.execute();
		} catch (IOException e) {
		}
		
		return res;
	}
	
	public HttpResponse HttpPost(String url, HashMap<String, Object> param){
		GenericUrl g_url = new GenericUrl(url);
		if(param != null)
		g_url.putAll(param);
		HttpResponse res = null;
		try {
			HttpRequest req = http.createRequestFactory().buildPostRequest(g_url, null);
			res =  req.execute();
		} catch (IOException e) {
		}
		
		return res;
	}
	
}
