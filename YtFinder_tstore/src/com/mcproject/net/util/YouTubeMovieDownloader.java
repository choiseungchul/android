package com.mcproject.net.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.os.AsyncTask;

import com.mcproject.net.conf.AppUserSettings;


// 유튜브 스트리밍 url 파서 클래스
public class YouTubeMovieDownloader extends AsyncTask<String, ArrayList<String>, ArrayList<String>> {

	private String mvName = null;
	private Context ctx;
	private String mvId;

	public YouTubeMovieDownloader(Context context, String mvName, String mvId ){
		this.mvName = mvName;
		this.ctx = context;
		this.mvId = mvId;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		String url = params[0];
		ArrayList<String> urlList = new ArrayList<String>();
		try {
			ArrayList<Video> videos = getStreamingUrisFromYouTubePage(url);
			if(videos != null)
				LogUtil.I("finded stream urls length = " + videos.size());
			else LogUtil.W("finded stream urls length 0 ");

			if (videos != null) {
				if(!videos.isEmpty()){					
					// 다운로드 해상도 설정값
					int resLevel = 1;
					
					if(AppUserSettings.getMovieDownLoadResolution(ctx).equals("")){
						resLevel = 1;
					}else{
						resLevel = Integer.parseInt(AppUserSettings.getMovieDownLoadResolution(ctx));
					}
					
					// 고해상도 다운로드 설정일때만 고해상도 url로 설정				
					if(resLevel <= 1){
						for (Video video : videos) {
							if (video.ext.toLowerCase().contains("mp4")
									&& video.type.toLowerCase().contains("high")) {						
								urlList.add(video.url);
							}
						}
					}
					
					// 중화질이나 고화질일 경우 이부분의 로직을 탄다
					if(resLevel <= 2){
						for (Video video : videos) {
							if (video.ext.toLowerCase().contains("mp4")
									&& video.type.toLowerCase().contains("medium")) {
								urlList.add(video.url);							
							}
						}
					
					
						for (Video video : videos) {
							if (video.ext.toLowerCase().contains("3gp")
									&& video.type.toLowerCase().contains("medium")) {
								urlList.add(video.url);								
							}
						}
					}
					
					// 저화질일 경우 이 로직만 탄다, 나머지는 모두 이 로직을 탐
					if(resLevel <= 3){						
						for (Video video : videos) {
							if (video.ext.toLowerCase().contains("mp4")
									&& video.type.toLowerCase().contains("low")) {
								urlList.add(video.url);								
							}
						}
					
						for (Video video : videos) {
							if (video.ext.toLowerCase().contains("3gp")
									&& video.type.toLowerCase().contains("low")) {
								urlList.add(video.url);								
							}
						}						
					}

					return urlList;
				}
			}
		} catch (Exception e) {
			LogUtil.E(e.toString());
			this.cancel(true);
		}
		LogUtil.W("Couldn't find any stream url");
		
		return urlList;
	}

	@Override
	protected void onPostExecute(ArrayList<String> streamingUrls) {		
//		Intent ii = new Intent(ctx, MovieDownloadService.class);
//		ii.putExtra("streamUrl", streamingUrls);
//		LogUtil.I("util mvName = " + mvName);
//		ii.putExtra("mvId", mvId);
//		ii.putExtra("mvName", mvName);
//		
//		ctx.startService(ii);
		
		MovieDownloader dn = new MovieDownloader(ctx, streamingUrls, mvId, mvName);
		dn.start();
		
		super.onPostExecute(streamingUrls);
	}

	public class Meta {
		public String num;
		public String type;
		public String ext;

		public Meta(String num, String ext, String type) {
			this.num = num;
			this.ext = ext;
			this.type = type;
		}
	}

	public class Video {
		public String ext = "";
		public String type = "";
		public String url = "";

		public Video(String ext, String type, String url) {
			this.ext = ext;
			this.type = type;
			this.url = url;
		}
	}

	// 실제로 웹페이지를 로드하여 url 분석하는 곳
	public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl)
			throws IOException {
		if (ytUrl == null) {
			return null;
		}
		
		// signature 저장
		String sig = null, sig2 = null, sig3 = null;

		// Remove any query params in query string after the watch?v=<vid> in
		// e.g.
		// http://www.youtube.com/watch?v=0RUPACpf8Vs&feature=youtube_gdata_player
		int andIdx = ytUrl.indexOf('&');
		if (andIdx >= 0) {
			ytUrl = ytUrl.substring(0, andIdx);
		}

		// Get the HTML response
		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				userAgent);
		// 추가한거
		client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

		HttpGet request = new HttpGet(ytUrl);

		HttpResponse response = client.execute(request);
		String html = "";
		InputStream in = response.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder str = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			str.append(line.replace("\\u0026", "&"));
		}
		in.close();
		html = str.toString();

		// Parse the HTML response and extract the streaming URIs
//		if (html.contains("verify-age-thumb")) {
//			LogUtil.W("YouTube is asking for age verification. We can't handle that sorry.");
//			return null;
//		}

		if (html.contains("das_captcha")) {
			LogUtil.W("Captcha found, please try with different IP address.");
			return null;
		}

		Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
		// Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
		Matcher m = p.matcher(html);
		List<String> matches = new ArrayList<String>();
		while (m.find()) {
			matches.add(m.group());
		}

		if (matches.size() != 1) {
			LogUtil.W("Found zero or too many stream maps.");
			return null;
		}

		String urls[] = matches.get(0).split(",");
		
		HashMap<String, String> foundArray = new HashMap<String, String>();
		for (String ppUrl : urls) {
			String url = URLDecoder.decode(ppUrl, "UTF-8");
			
//			LogUtil.D("url = " + url);
			
			Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
			Matcher m1 = p1.matcher(url);
			String itag = null;
			if (m1.find()) {
				itag = m1.group(1);
			}
			
			url = url.replace("&codecs=", "&codec=");	
			url = url.replace("codecs=", "codec=");
			
			url = url.replace("&sparams=", "&spa=");
			if(url.startsWith("sparams="))
			url = url.replace("sparams=", "spa=");
			url = url.replace("&au=", "&aauu=");
			if(url.startsWith("au="))
			url = url.replace("au=", "aauu=");
			url = url.replace("&ipbits=", "&ipb=");
			if(url.startsWith("ipbits="))
			url = url.replace("ipbits=", "ipb=");
			url = url.replace("&ratebypass=", "&ratebypasseed=");
			if(url.startsWith("ratebypass="))
			url = url.replace("ratebypass=", "ratebypasseed=");
			if(url.startsWith("ms="))
				url = url.replace("ms=", "msdd=");
			url = url.replace("&ms=", "&msdd=");
			if(url.startsWith("mws="))
				url = url.replace("mws=", "msdwd=");
			url = url.replace("&mws=", "&msdwd=");

			String regex = "(.*?)[&]";
//			String regex = "[0-9A-Z]{40}.[0-9A-Z]{40}";
			
			Pattern p2 = Pattern.compile("sig=" + regex);
			Matcher m2 = p2.matcher(url);
			
			if (m2.find()) {
				sig = m2.group(1);
			}
			
			if(sig == null){
				Pattern p2_2 = Pattern.compile("signature=" + regex);
				Matcher m2_2 = p2_2.matcher(url);
				
				if (m2_2.find()) {
					sig3 = m2_2.group(1);
				}
			}
			
			if(sig3 == null){
				// 새로 추가한거
				Pattern p2_1 = Pattern.compile("s=" + regex);
				Matcher m2_1 = p2_1.matcher(url);
				
				if (m2_1.find()) {
					sig2 = m2_1.group(1);					
				}
			}
			
			Pattern p3 = Pattern.compile("url=(.*?)[&]");
			Matcher m3 = p3.matcher(ppUrl);
			String um = null;
			if (m3.find()) {
				um = m3.group(1);
			}

			if (itag != null && um != null) {
				if(sig != null) {
					LogUtil.I(sig);					
					foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&" + "sig=" + sig);
				}
				if(sig2 != null){
					LogUtil.I(sig2);					
					foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&" + "signature=" + sig2);
				}
				if(sig3 != null){
					LogUtil.I(sig3);					
					foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&" + "s=" + sig3);
				}
			}
		} // for

		if (foundArray.size() == 0) {
			LogUtil.W("Couldn't find any URLs and corresponding signatures");
			// signature log
			LogUtil.I("sig1 = " + sig);
			LogUtil.I("sig2 = " + sig2);
			LogUtil.I("sig3 = " + sig3);
			
			for (String ppUrl : urls) {
				String url = URLDecoder.decode(ppUrl, "UTF-8");
//				LogUtil.W(url);
			}
			return new ArrayList<YouTubeMovieDownloader.Video>();
		}

		HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
		typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
		typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
		typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
		typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
		typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
		typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
		typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
		typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
		typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
		typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
		typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
		typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
		typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
		typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

		ArrayList<Video> videos = new ArrayList<Video>();

		for (String format : typeMap.keySet()) {
			Meta meta = typeMap.get(format);

			if (foundArray.containsKey(format)) {
				Video newVideo = new Video(meta.ext, meta.type,
						foundArray.get(format));
				videos.add(newVideo);
				
//				LogUtil.D("YouTube Video streaming details: ext:" + newVideo.ext
//						+ ", type:" + newVideo.type + ", url:" + newVideo.url);
			}
		}

		return videos;
	}

}
