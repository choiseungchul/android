package com.mcproject.net.conf;

import com.mcproject.net.util.McUtil;
import android.content.Context;


public class AppUserSettings {

	/**
	 *사용자 국가 설정
	 */
	public static void setNationCode(Context ctx, String Code) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_NATION, Code);
	}
	public static String getNationCode(Context ctx) {
		String NationCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_NATION);
		return NationCode;
	}
	
	/**
	 *사용자 언어 설정
	 */
	public static void setHLCode(Context ctx, String Lang) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_HLCODE, Lang);
	}
	public static String getHLCode(Context ctx) {
		String HLCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_HLCODE);
		return HLCode;
	}
	
	/**
	 * 세이프 서치 설정
	 */
	public static void setSafeSearch(Context ctx, String safemode) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SAFE_SEARCH, safemode);
	}
	public static String getSafeSearch(Context ctx) {
		String HLCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SAFE_SEARCH);
		return HLCode;
	}
	
	/**
	 * 업로더 영상 알림 설정
	 */
	public static void setNotiAlert(Context ctx, String isNoti) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SET_NOTIFICATION, isNoti);
	}
	public static String getNotiAlert(Context ctx) {
		String isNoti = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SET_NOTIFICATION);
		return isNoti;
	}
	
	/**
	 * 동영상 다운로드 해상도 구분
	 */
	public static void setMovieDownLoadResolution(Context ctx, String resLevel) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_MOVIE_DOWNLOAD_RESOLUTION, resLevel);
	}
	public static String getMovieDownLoadResolution(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_MOVIE_DOWNLOAD_RESOLUTION);
		return resLevel;
	}
	
	/**
	 * 오늘 광고를 몇번 봤는지 여부
	 */
	public static void setIsAdToday(Context ctx, String isToday) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_TODAY, isToday);
	}
	public static String getIsAdToday(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_TODAY);
		return resLevel;
	}
	
	/**
	 * 음원 추출 방식
	 */
	public static void setAudioBitrate(Context ctx, String a_format) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUDIO_OUTPUT_BITRATE, a_format);
	}
	public static String getAudioBitrate(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUDIO_OUTPUT_BITRATE);
		return resLevel;
	}
	
	/**
	 * 사용자정보를 서버에 넘겼는지여부
	 */
	public static void setIsAddUser(Context ctx, String is_added) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_ADDUSER, is_added);
	}
	public static String getIsAddUser(Context ctx) {
		String is_added = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_ADDUSER);
		return is_added;
	}
	
	/**
	 * 전면광고가 캐시되었는지 여부
	 */
	public static void setAppwallCached(Context ctx, String is_added) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_AD_CACHED, is_added);
	}
	public static String getAppwallCached(Context ctx) {
		String is_added = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_AD_CACHED);
		return is_added;
	}
}