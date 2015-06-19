package com.mcproject.net.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.ytfavorite_t.R;


public class McUtil {
	
	// 랜덤 문자열
	public static String getRandomString(String chars, int length) {
		Random rand = new Random();
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<length; i++) {
			buf.append(chars.charAt(rand.nextInt(chars.length())));
		}
		return buf.toString();
	}

	// Orientation 구하기
	public static int getScreenOrientation(Activity act) {
	    int rotation = act.getWindowManager().getDefaultDisplay().getRotation();
	    DisplayMetrics dm = new DisplayMetrics();
	    act.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;
	    int height = dm.heightPixels;
	    int orientation;
	    // if the device's natural orientation is portrait:
	    if ((rotation == Surface.ROTATION_0
	            || rotation == Surface.ROTATION_180) && height > width ||
	        (rotation == Surface.ROTATION_90
	            || rotation == Surface.ROTATION_270) && width > height) {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            default:
	                LogUtil.E("Unknown screen orientation. Defaulting to " + "portrait.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;              
	        }
	    }
	    // if the device's natural orientation is landscape or if the device
	    // is square:
	    else {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            default:
	            	LogUtil.E("Unknown screen orientation. Defaulting to " + "landscape.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;              
	        }
	    }

	    return orientation;
	}
	
	// 미디어 스캔
	public static void mediaScan(Context ctx, String path){
		File file = new File(path);
	    Uri uri = Uri.fromFile(file);
	    Intent scanFileIntent = new Intent(
	            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
	    ctx.sendBroadcast(scanFileIntent);
	}
	
	// 숏컷 아이콘 넣기
	public static void addShortCut(Context context){
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClassName(context.getPackageName(), context.getPackageName() + ".MainActivity");
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context, R.drawable.lc_icon));
		//intent.putExtra("duplicate", false);
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT"); 
		context.sendBroadcast(addIntent);
		//			Toast.makeText(context, "바탕화면에 바로가기가 설치되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public static void traceArray(String[] data){
		for(int i = 0 ; i < data.length ; i++){
			LogUtil.I( i + " : " + data[i]);
		}
	}

	public static void traceList(ArrayList<String> list){
		for ( int i = 0 ; i < list.size() ; i++){
			LogUtil.I(list.get(i));
		}
	}

	public static void traceParams(Bundle params){
		for ( String p :  params.keySet()){
			LogUtil.I(p + " : " + params.get(p).toString());
		}
	}

	public static void setNation(Context ctx, int type){
		String nationCode = ctx.getResources().getStringArray(R.array.user_select_nation_regionCode)[type];
		AppUserSettings.setNationCode(ctx, nationCode);
		String hlCode = ctx.getResources().getStringArray(R.array.user_select_nation_hlcode)[type];
		AppUserSettings.setHLCode(ctx, hlCode);
	}

	public static List<YTListDto> parsePopularDataList(Context ctx, JSONObject response){
		List<YTListDto> datas = new ArrayList<YTListDto>();
		
		try {
			JSONArray items = response.getJSONArray("items");
			
			for(int i = 0 ; i < items.length() ; i++){
				
				YTListDto dto = new YTListDto();
				
				JSONObject item_data = items.getJSONObject(i);
				String id  = item_data.getString("id");

				JSONObject snippet = item_data.getJSONObject("snippet");
				JSONObject contentDetails = item_data.getJSONObject("contentDetails");

				String publishedAt = snippet.getString("publishedAt");
				String title = snippet.getString("title");
				String description = snippet.getString("description");
				String channelId = snippet.getString("channelId");
				String channelTitle = snippet.getString("channelTitle");

				String duration = contentDetails.getString("duration");
				
				// 시간계산
				int[] hms = getHms(duration); 
				dto.hh = hms[0];
				dto.mm = hms[1];
				dto.ss = hms[2];

				JSONObject thumnails = snippet.getJSONObject("thumbnails").getJSONObject("default");
				String thumnails_medium = thumnails.getString("url");

				dto.description = description;
				
				dto.publish_date_origin = publishedAt;
				dto.title = title;

				publishedAt = parseDateString(ctx, publishedAt);

				dto.publish_date = publishedAt;
				
				dto.thumbnail = thumnails_medium;
				dto.videoid = id;
				dto.channel_id = channelId;
				dto.channel_title = channelTitle;
				dto.kind = item_data.getString("kind");
				dto.etag = item_data.getString("etag");
				
				duration = parseDurationString(ctx, duration);
				dto.duration = duration;
				
				ArrayList<String> fav_uploader = DbQueryUtil.getFavoriteUploaderId(ctx);
				ArrayList<YTListDto> fav_video = DbQueryUtil.getFavoriteVideo(ctx);
				
				// 즐겨찾기된 영상인지 조회
				for(int s = 0 ; s < fav_video.size(); s++){
					if(fav_video.get(s).videoid.equals(dto.videoid)){
						dto.isFavoriteVideo = true;
						break;
					}
				}

				// 즐겨찾기된 업로더인지 조회
				for(int s = 0 ; s < fav_uploader.size(); s++){
					if(fav_uploader.get(s).equals(dto.channel_id)){
						dto.isFavoriteUploader = true;
						break;
					}
				}
				datas.add(dto);
				
			}// for
		} catch (JSONException e) {
			LogUtil.E(e.toString());
		}

		return datas;
	}
	

	// 가져온 영상 목록에 영상재생시간, 업로더 명 추가
	public static List<YTListDto> parseVideoDetailList(Context ctx, JSONObject response, List<YTListDto> datas){
		try {
			JSONArray detail_item = response.getJSONArray("items");
			ArrayList<String> time_str = new ArrayList<String>();
			ArrayList<int[]> time_detail = new ArrayList<int[]>();
			ArrayList<String> uploaderList = new ArrayList<String>();

			if(detail_item.length() == 0){
				return new ArrayList<YTListDto>();
			}

			for(int k = 0 ; k < detail_item.length(); k++){
				JSONObject item_json = detail_item.getJSONObject(k);
				JSONObject contentDetails = item_json.getJSONObject("contentDetails");
				JSONObject snippet = item_json.getJSONObject("snippet");

				String duration = contentDetails.getString("duration");

				int[] hms = getHms(duration);
				time_detail.add(hms);

				duration = parseDurationString(ctx, duration);
				time_str.add(duration);

				uploaderList.add(snippet.getString("channelTitle"));

			}

			ArrayList<String> fav_uploader = DbQueryUtil.getFavoriteUploaderId(ctx);
			ArrayList<YTListDto> fav_video = DbQueryUtil.getFavoriteVideo(ctx);

			for(int a = 0 ; a < time_str.size() ; a++){
				YTListDto dto = datas.get(a);

				dto.duration = time_str.get(a);

				dto.hh = time_detail.get(a)[0];
				dto.mm = time_detail.get(a)[1];
				dto.ss = time_detail.get(a)[2];

				dto.channel_title = uploaderList.get(a);

				// 즐겨찾기된 영상인지 조회
				for(int s = 0 ; s < fav_video.size(); s++){
					if(fav_video.get(s).videoid.equals(dto.videoid)){
						dto.isFavoriteVideo = true;
						break;
					}
				}

				// 즐겨찾기된 업로더인지 조회
				for(int s = 0 ; s < fav_uploader.size(); s++){
					if(fav_uploader.get(s).equals(dto.channel_id)){
						dto.isFavoriteUploader = true;
						break;
					}
				}

				datas.set(a, dto);
			}

		} catch (JSONException e) {
			LogUtil.E(e.toString());
		}
		return datas;
	}

	public static int[] getHms(String duration){
//		LogUtil.I("getHms duration = " + duration);
		
		if(duration.indexOf("PT") != -1){
			duration = duration.replace("PT", "T");
		}
		
		int[] hms = new int[3];
		String h="0",m="0",s="0";
		try {
			// 시, 분, 초 계산

			if(duration.indexOf("H") != -1){
				h = duration.substring(duration.indexOf("T") + 1, duration.indexOf("H"));
				if(duration.indexOf("M") != -1){
					m = duration.substring(duration.indexOf("H") + 1, duration.indexOf("M"));
					if(duration.indexOf("S") != -1){
						s = duration.substring(duration.indexOf("M") + 1, duration.indexOf("S"));
					}
				}
				else if(duration.indexOf("S") != -1){
					s = duration.substring(duration.indexOf("H") + 1, duration.indexOf("S"));
				}
				
			}else{
				if(duration.indexOf("M") != -1){
					m = duration.substring(duration.indexOf("T") + 1, duration.indexOf("M"));
					if(duration.indexOf("S") != -1){
						s = duration.substring(duration.indexOf("M") + 1, duration.indexOf("S"));
					}
				}else{
					if(duration.indexOf("S") != -1){
						s = duration.substring(duration.indexOf("T") + 1, duration.indexOf("S"));
					}
				}
			}
		}catch(Exception e){
			LogUtil.E(duration + " => getHms() this string error!");
		}

		try{
			hms[0] = Integer.parseInt(h);
		}catch(NumberFormatException e){
			LogUtil.I("duration = " + duration);
			LogUtil.E(e.toString());
		}
		try{
			hms[1] = Integer.parseInt(m);
		}catch(NumberFormatException e){
			LogUtil.I("duration = " + duration);
			LogUtil.E(e.toString());
		}
		try{
			
			hms[2] = Integer.parseInt(s);
		}catch(NumberFormatException e){
			LogUtil.I("duration = " + duration);
			LogUtil.E(e.toString());
		}
		
		
		return hms;
	}

	/**
	 * 검색 목록 파싱
	 * */
	public static ArrayList<YTListDto> parseSearchListToDataObject(Context ctx, JSONObject response){

		ArrayList<YTListDto> datas = new ArrayList<YTListDto>();

		JSONArray items;
		try {
			items = response.getJSONArray("items");
			for(int i = 0 ; i < items.length() ; i++){
				JSONObject item_data = items.getJSONObject(i);
				JSONObject id = item_data.getJSONObject("id");
				String videoId = "";
				// 비디오 아이디값이 있는것만 넣는다.
				if(!id.isNull("videoId")){
					videoId = id.getString("videoId");

					JSONObject snippet = item_data.getJSONObject("snippet");

					String publishedAt = snippet.getString("publishedAt");
					String title = snippet.getString("title");
					String description = snippet.getString("description");
					String channelId = snippet.getString("channelId");
					//					String channelTitle = snippet.getString("channelTitle");

					JSONObject thumnails = snippet.getJSONObject("thumbnails").getJSONObject("default");
					String thumnails_medium = thumnails.getString("url");

					YTListDto dto = new YTListDto();
					dto.description = description;
					dto.title = title;

					dto.publish_date_origin = publishedAt;
					
					publishedAt = parseDateString(ctx, publishedAt);

					dto.publish_date = publishedAt;
					dto.thumbnail = thumnails_medium;
					dto.videoid = videoId;
					dto.channel_id = channelId;
					//					dto.channel_title = channelTitle;
					dto.kind = item_data.getString("kind");
					dto.etag = item_data.getString("etag");

					datas.add(dto);
				}
			}// for
		} catch (JSONException e) {
			LogUtil.E(e.toString());
		}

		return datas;
	}

	public static String parseDurationString(Context ctx, String duration){
		duration = duration.replace("PT", "");
		duration = duration.replace("H", ctx.getString(R.string.duration_hour));
		duration = duration.replace("M", ctx.getString(R.string.duration_minute));
		duration = duration.replace("S", ctx.getString(R.string.duration_second));
		return duration;
	}
	/*
	 * 유튜브 시간을 몇초몇분몇시 전 으로 변경
	 * */
	public static String parseDateString(Context ctx, String publishedAt){
		publishedAt = publishedAt.replace("T", " ");
		publishedAt = publishedAt.replace("Z", "");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date tempdate;
		try {
			tempdate = sdf.parse(publishedAt);
			publishedAt = DateUtils.getRelativeDateTimeString ( ctx, tempdate.getTime(), 0L, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
		} catch (ParseException e) {
			LogUtil.E(e.toString());
		}
		return publishedAt;
	}

	public static float getDpToPixel(Context ctx, int dp)
	{
		float scale = ctx.getResources().getDisplayMetrics().density;
		return dp * scale;
	}

	/*
	 * hashtable 값 GET파라미터로 변경
	 * */
	public static String getParams(Hashtable<String, String> params){
		StringBuilder sb = new StringBuilder();
		int i = 0;

		Enumeration<String> keys = params.keys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			try {
				sb.append(i==0?"?":"&")
				.append(key).append("=")
				.append(URLEncoder.encode(params.get(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LogUtil.E(e.toString());
			}
			i++;
		}
		return sb.toString();
	}

	/*
	 * hashtable 값 GET파라미터로 변경
	 * */
	public static String getDetailParams(Bundle params){
		StringBuilder sb = new StringBuilder();

		for( String key : params.keySet() ){
			try {
				sb.append("&")
				.append(key).append("=")
				.append(URLEncoder.encode(params.getString(key), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static boolean CheckInternet(Context ctx) {
		ConnectivityManager connec = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// Check if wifi or mobile network is available or not. If any of them is
		// available or connected then it will return true, otherwise false;
		return wifi.isConnected() || mobile.isConnected();
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String phone = "";
		try{
			if(tMgr.getLine1Number() != null){
				phone = tMgr.getLine1Number();
			}
			phone.replaceAll("-", "");
			phone = phone.substring(phone.length()-10, phone.length());
			phone="0" + phone;
		}catch(Exception e){
			e.printStackTrace();
		}
		return phone;
	}

	public static void sendSMS(Context ctx, String phoneNumber, String message) {
		try {
			PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_SENT"), 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_DELIVERED"), 0);

			SmsManager sms = SmsManager.getDefault(); 
			sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		} catch (Exception e) {
			LogUtil.E("MadUtil sendSMS:"+e.toString());
		}
	}

	public static View disableEnableControls(boolean enable, ViewGroup vg){
		for (int i = 0; i < vg.getChildCount(); i++){
			View child = vg.getChildAt(i);
			child.setEnabled(enable);
			if (child instanceof ViewGroup){ 
				disableEnableControls(enable, (ViewGroup)child);
			}
		}
		return vg;
	}

	public static String priceFormat(String txt) {
		try {
			DecimalFormat df = new DecimalFormat("###,###");
			return df.format(Double.parseDouble(txt));
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isNetworkConnected(Context ctx) {
		ConnectivityManager connMgr = (ConnectivityManager) 
				ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}


	public static String getMyPhoneNumber(Context ctx){
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyMgr.getLine1Number();
	}

	// 업로더에 대한 동영상 정보를 읽어오기 위해서
	public static void startServiceWithExtra(Context ctx, Class<?> serviceCls, int serviceID, Bundle extra){
		Intent service = new Intent(ctx, serviceCls);
		service.putExtra("extra", extra);
		ctx.startService(service);
	}
	
	public static void startAlarmService(Context ctx, Class<?> serviceCls, int serviceID, long interval, int delaySecond) {
		Intent i = new Intent(ctx, serviceCls);
		PendingIntent sender = PendingIntent.getService(ctx, serviceID, i, 0);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, delaySecond);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
				calendar.getTimeInMillis(), 
				interval, 
				sender);
	}

	public static String getOSVersionString(int version){
		String versionString = null;

		switch (version) {
		case 2:
			versionString = "? 1.1";
			break;
		case 3:
			versionString = "1.5";
			break;
		case 4:
			versionString = "1.6";
			break;
		case 5:
			versionString = "2.0";
			break;
		case 6:
			versionString = "2.0.1";
			break;
		case 7:
			versionString = "2.1";
			break;
		case 8:
			versionString = "2.2";
			break;
		case 9:
			versionString = "2.3";
			break;
		case 10:
			versionString = "2.3.3-2.3.4";
			break;
		case 11:
			versionString = "3.0";
			break;
		case 12:
			versionString = "3.1";
			break;
		case 13:
			versionString = "3.2";
			break;
		case 14:
			versionString = "4.0";
			break;
		case 15:
			versionString = "4.0.3";
			break;
		case 16:
			versionString = "4.1";
			break;
		case 17:
			versionString = "4.2";
			break;
		case 18:
			versionString = "4.3";
			break;
		case 19:
			versionString  = "4.4";
		default:
			versionString = "4.4 over";
			break;
		}

		return versionString;
	}


	static public void setSharedPreferencesValue(Context ctx, String nm, int mode, String key, String value) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	static public String getSharedPreferencesValue(Context ctx, String nm, int mode, String key) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		return pref.getString(key, "");
	}

	public static void clearAllPrefferences(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Define.PREFER_ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	static public byte[] readFile(File file) throws IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength) throw new IOException("File size >= 2 GB");
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		}
		finally {
			f.close();
		}
	}

	public static boolean TstoreCheck(Activity act) {
		PackageManager pm = act.getPackageManager();
		List< ApplicationInfo > appList = pm.getInstalledApplications( 0 );
		ApplicationInfo app = null;
		int nSize = appList.size();
		for( int i = 0; i < nSize; i++ ) {
			app = appList.get( i );
			if(app.packageName.indexOf("com.skt.skaf.A000Z00040") != -1) {
				return true;
			}
		}
		return false;
	}

	public static void TstoreLink(Activity activity, String aid) {
		aid = aid.replace("OA", "00");
		LogUtil.W("TstoreLink aid:"+aid);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //mandatory flag
		intent.setClassName("com.skt.skaf.A000Z00040","com.skt.skaf.A000Z00040.A000Z00040");
		intent.setAction("COLLAB_ACTION");	//action
		intent.putExtra("com.skt.skaf.COL.URI",("PRODUCT_VIEW/"+aid+"/0").getBytes());	//user data
		intent.putExtra("com.skt.skaf.COL.REQUESTER","A000Z00040");	//my App ID
		activity.startActivity(intent);
	}
	
	// 서비스 실행중인지 조회
	public static boolean isServiceRunningCheck(Context ctx, String ServiceName) {
		String serviceName = ctx.getPackageName() + ".service." + ServiceName;
		LogUtil.D("isServiceRunningCheck serviceName = " + serviceName);
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ( serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
	}
	
	public static YTListDto convertCollectDtoToYTLDto(CollectedDto dto ){
		
		YTListDto returnDto = new YTListDto();
		returnDto.videoid = dto.videoid;
		returnDto.channel_id = dto.channel_id;
		returnDto.channel_title = dto.channel_title;
		returnDto.description = dto.description;
		returnDto.duration = dto.duration;
		returnDto.etag = dto.etag;
		returnDto.hh = dto.hh;
		returnDto.isFavoriteUploader = dto.isFavoriteUploader;
		returnDto.isFavoriteVideo = dto.isFavoriteVideo;
		returnDto.kind = dto.kind;
		returnDto.liveBroadcastContent = dto.liveBroadcastContent;
		returnDto.mm = dto.mm;
		returnDto.publish_date = dto.publish_date;
		returnDto.publish_date_origin = dto.publish_date_origin;
		returnDto.seq = dto.seq;
		returnDto.ss = dto.ss;
		returnDto.thumbnail = dto.thumbnail;
		returnDto.title = dto.title;
		
		return returnDto;
	}
	
	/*
	 * 현재 액티비티가 최상단에 있는지 판별
	 * */
	public static boolean isTopActivity(Context ctx, String activity_name){
		String pkgName = ctx.getPackageName();
		ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> info;
		info = activityManager.getRunningTasks(7);
		for (Iterator iterator = info.iterator(); iterator.hasNext();)  {
			RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
			String name = pkgName + "." + activity_name;
			if(runningTaskInfo.topActivity.getClassName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
//	public static void autoMappingXmlToObject(Element xml, Object obj) {
//		Field[] fields =  obj.getClass().getFields();
//		for(Field field : fields) {
//			if(xml.select(field.getName()) != null) {
//				try {
//					field.set(obj, xml.select(field.getName()).text());
//				} catch (Exception e) {}
//			}
//		}
//	}
	
}
