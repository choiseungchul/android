package com.returndays.ralara.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.returndays.http.HttpConst;
import com.returndays.http.HttpData;

public class HTTPrequest
{
	//member variables
	private SchemeRegistry mSchemeRegistry;
	private HttpParams mHttpParams;
	private SingleClientConnManager mSCCmgr;
	private HttpClient mHttpClient;
	private HTTPrequestListener mHTTPrequestListener = null;

	//constants
	private final int TIMEOUT_CONNECTION = 20000;//20sec
	private final int TIMEOUT_SOCKET = 30000;//30sec

	//interface for callbacks
	public interface HTTPrequestListener
	{
		public void downloadProgress(int iPercent);
	}

	/**
	 * Creates an HttpClient that uses plain text only.
	 * note: Default constructor uses HTTP 1.1
	 */
	public HTTPrequest()
	{
		this(HttpVersion.HTTP_1_1);
	}

	/**
	 * Creates an HttpClient that uses plain text only.
	 * @param httpVersion HTTP Version (0.9, 1.0, 1.1)
	 */
	public HTTPrequest(HttpVersion httpVersion)
	{
		//define permitted schemes
		mSchemeRegistry = new SchemeRegistry();
		// https로 설정
		mSchemeRegistry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 80));

		//define http parameters
		mHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mHttpParams, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(mHttpParams, TIMEOUT_SOCKET);
		HttpProtocolParams.setVersion(mHttpParams, httpVersion);
		HttpProtocolParams.setContentCharset(mHttpParams, HTTP.UTF_8);

		//tie together the schemes and parameters
		mSCCmgr = new SingleClientConnManager(mHttpParams, mSchemeRegistry);

		//generate a new HttpClient using connection manager and parameters
		mHttpClient = new DefaultHttpClient(mSCCmgr, mHttpParams);
	}

	public void setHTTPrequestListener(HTTPrequestListener httpRequestListener)
	{
		mHTTPrequestListener = httpRequestListener;
	}

	//other methods for POST and GET
	
	public HttpData getRequest(String url){
		HttpData httpData = new HttpData();
		HttpGet httpGet = new HttpGet(url);
 
		/**
		 * Run request
		 */
		httpGet.setHeader(HttpConst.HEADDER_KEY, HttpConst.HEADDER_VALUE);
		HttpResponse response = null;
		try {
			response = mHttpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpData.data = GetText(response);
		httpData.response = response;
		/**
		 * Return data
		 */
		return httpData;
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
}
