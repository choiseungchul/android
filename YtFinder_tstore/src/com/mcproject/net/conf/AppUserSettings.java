package com.mcproject.net.conf;

import com.mcproject.net.util.McUtil;
import android.content.Context;


public class AppUserSettings {

	/**
	 *����� ���� ����
	 */
	public static void setNationCode(Context ctx, String Code) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_NATION, Code);
	}
	public static String getNationCode(Context ctx) {
		String NationCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_NATION);
		return NationCode;
	}
	
	/**
	 *����� ��� ����
	 */
	public static void setHLCode(Context ctx, String Lang) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_HLCODE, Lang);
	}
	public static String getHLCode(Context ctx) {
		String HLCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_HLCODE);
		return HLCode;
	}
	
	/**
	 * ������ ��ġ ����
	 */
	public static void setSafeSearch(Context ctx, String safemode) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SAFE_SEARCH, safemode);
	}
	public static String getSafeSearch(Context ctx) {
		String HLCode = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SAFE_SEARCH);
		return HLCode;
	}
	
	/**
	 * ���δ� ���� �˸� ����
	 */
	public static void setNotiAlert(Context ctx, String isNoti) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SET_NOTIFICATION, isNoti);
	}
	public static String getNotiAlert(Context ctx) {
		String isNoti = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SET_NOTIFICATION);
		return isNoti;
	}
	
	/**
	 * ������ �ٿ�ε� �ػ� ����
	 */
	public static void setMovieDownLoadResolution(Context ctx, String resLevel) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_MOVIE_DOWNLOAD_RESOLUTION, resLevel);
	}
	public static String getMovieDownLoadResolution(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_MOVIE_DOWNLOAD_RESOLUTION);
		return resLevel;
	}
	
	/**
	 * ���� ���� ��� �ô��� ����
	 */
	public static void setIsAdToday(Context ctx, String isToday) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_TODAY, isToday);
	}
	public static String getIsAdToday(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_TODAY);
		return resLevel;
	}
	
	/**
	 * ���� ���� ���
	 */
	public static void setAudioBitrate(Context ctx, String a_format) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUDIO_OUTPUT_BITRATE, a_format);
	}
	public static String getAudioBitrate(Context ctx) {
		String resLevel = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUDIO_OUTPUT_BITRATE);
		return resLevel;
	}
	
	/**
	 * ����������� ������ �Ѱ��������
	 */
	public static void setIsAddUser(Context ctx, String is_added) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_ADDUSER, is_added);
	}
	public static String getIsAddUser(Context ctx) {
		String is_added = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_ADDUSER);
		return is_added;
	}
	
	/**
	 * ���鱤�� ĳ�õǾ����� ����
	 */
	public static void setAppwallCached(Context ctx, String is_added) {
		McUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_AD_CACHED, is_added);
	}
	public static String getAppwallCached(Context ctx) {
		String is_added = McUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_AD_CACHED);
		return is_added;
	}
}