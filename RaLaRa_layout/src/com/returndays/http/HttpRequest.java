package com.returndays.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;
 
/**
 * HttpRequest
 * This class handles POST and GET requests and
 * enables you to upload files via post.
 * Cookies are stored in the HttpClient.
 * @author Sander Borgman
 * @url http://www.sanderborgman.nl
 * @version 1
 */
public class HttpRequest {
 
	/**
	 * GET request
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static HttpData getRequest(HttpClient client, String url) throws Exception {
 
		/**
		 * Setup
		 */
		HttpData httpData = new HttpData();
		HttpGet httpGet = new HttpGet(url);
 
		/**
		 * Run request
		 */
		httpGet.setHeader(HttpConst.HEADDER_KEY, HttpConst.HEADDER_VALUE);
		HttpResponse response = client.execute(httpGet);
		httpData.data = GetText(response);
		httpData.response = response;
		/**
		 * Return data
		 */
		return httpData;
 
	}
 
	/**
	 * POST request
	 * @param client
	 * @param url
	 * @param postData
	 * @return
	 */
	public static HttpData postRequest(HttpClient client, String url, Map postData) throws Exception {
 
		/**
		 * Setup
		 */
		HttpData httpData = new HttpData();
		HttpPost httpPost = new HttpPost(url);
 
		MultipartEntity multipartEntity = new MultipartEntity();
 
		/**
		 * Handle post data
		 */
		Iterator postDataIterator = postData.entrySet().iterator();
		while(postDataIterator.hasNext()) {
			Map.Entry entry = (Map.Entry)postDataIterator.next();
 
			StringBody stringBody = new StringBody(entry.getValue().toString());
			multipartEntity.addPart(entry.getKey().toString(), stringBody);
		}
 
		httpPost.setEntity(multipartEntity);
 
		/**
		 * Run request
		 */
		HttpResponse response = client.execute(httpPost);
		httpData.data = GetText(response);
		httpData.response = response;
		/**
		 * Return data
		 */
		return httpData;
 
	}
 
	/**
	 * POST request with file
	 * @param client
	 * @param url
	 * @param postData
	 * @param postDataFiles
	 * @return
	 */
	public static HttpData postRequest(HttpClient client, String url, Map postData, Map postDataFiles) throws Exception {
		/**
		 * Setup
		 */
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000); // 5000 5�� --> 20��  
		
		HttpData httpData = new HttpData();
		HttpPost httpPost = new HttpPost(url);
 
		MultipartEntity multipartEntity = new MultipartEntity();
		
		/**
		 * Handle post data
		 */
		if(postData!=null) {
			Iterator postDataIterator = postData.entrySet().iterator();
			while(postDataIterator.hasNext()) {
				Map.Entry entry = (Map.Entry)postDataIterator.next();
	 
				StringBody stringBody = new StringBody(entry.getValue().toString());
				multipartEntity.addPart(entry.getKey().toString(), stringBody);
			}
		}
 
		/**
		 * Handle file data
		 */
		if(postDataFiles!=null) {
			Iterator postDataFilesIterator = postDataFiles.entrySet().iterator();
			while(postDataFilesIterator.hasNext()) {
				Map.Entry entry = (Map.Entry)postDataFilesIterator.next();
	 
				FileBody fileBody = new FileBody((File)entry.getValue());
				multipartEntity.addPart(entry.getKey().toString(), fileBody);
			}
		}
		httpPost.setHeader(HttpConst.HEADDER_KEY, HttpConst.HEADDER_VALUE);
		httpPost.addHeader("User-Agent", "Android");
		httpPost.setEntity(multipartEntity);
		/**
		 * Run request
		 */
		Log.d("1" , "****************fileup start****************");
		HttpResponse response = client.execute(httpPost);
		Log.d("1" , "****************fileup end****************");
		httpData.data = GetText(response);
		httpData.response = response;
		/**
		 * Return data
		 */
		return httpData;
 
	}
	
	/**
	 * POST request with file
	 * @param client
	 * @param url
	 * @param postData
	 * @param postDataFiles
	 * @return
	 */
	public static HttpData postRequestWithContentType(HttpClient client, String url, Map postData, Map postDataFiles, JSONObject json) throws Exception {
		/**
		 * Setup
		 */
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000); // 5000 5�� --> 20��
		
		HttpData httpData = new HttpData();
		HttpPost httpPost = new HttpPost(url);
 
		MultipartEntity multipartEntity = new MultipartEntity();
		
		/**
		 * Handle post data
		 */
		if(postData!=null) {
			Iterator postDataIterator = postData.entrySet().iterator();
			while(postDataIterator.hasNext()) {
				Map.Entry entry = (Map.Entry)postDataIterator.next();
	 
				StringBody stringBody = new StringBody(entry.getValue().toString());
				multipartEntity.addPart(entry.getKey().toString(), stringBody);
			}
		}
 
		/**
		 * Handle file data
		 */
		if(postDataFiles!=null) {
			Iterator postDataFilesIterator = postDataFiles.entrySet().iterator();
			while(postDataFilesIterator.hasNext()) {
				Map.Entry entry = (Map.Entry)postDataFilesIterator.next();
	 
				FileBody fileBody = new FileBody((File)entry.getValue());
				multipartEntity.addPart(entry.getKey().toString(), fileBody);
			}
		}
//		httpPost.setHeader(HttpConst.HEADDER_KEY, HttpConst.HEADDER_VALUE);
//		httpPost.addHeader("User-Agent", "Android");
		
		httpPost.setHeader("json", json.toString());
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
	    StringEntity se = new StringEntity(json.toString());
	
	    httpPost.setEntity(se);
	    
//		httpPost.setEntity(multipartEntity);
		/**
		 * Run request
		 */
		Log.d("1" , "****************fileup start****************");
		HttpResponse response = client.execute(httpPost);
		Log.d("1" , "****************fileup end****************");
		httpData.data = GetText(response);
		httpData.response = response;
		/**
		 * Return data
		 */
		return httpData;
 
	}
 
	/**
	 * Get string from stream
	 * @param InputStream
	 * @return
	 */
	private static String GetText(InputStream in)
	{
		String text = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
			line = reader.readLine();
			while(line != null)
			{
				sb.append(line + "\n");
				line = reader.readLine();
			}
			text = sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return text;
	}
 
	/**
	 * Get string from stream
	 * @param HttpResponse
	 * @return
	 */
	private static String GetText(HttpResponse response)
	{
		String text = "";
		try {
			text = GetText(response.getEntity().getContent());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return text;
	}
}