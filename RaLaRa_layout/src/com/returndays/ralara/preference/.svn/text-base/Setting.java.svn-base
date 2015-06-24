package com.returndays.ralara.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.returndays.ralara.conf.Define;
import com.returndays.ralara.util.MadUtil;

public class Setting {
	/**
	 * 백그라운드 서비스를 실행했는지 여부
	 * @param ctx
	 * @return
	 */
	public static boolean getBackServiceIsStart(Context ctx) {
		String flag = MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_ALARMSET_YN);
		return flag.equals("Y");
	}
	/**
	 * 백그라운드 서비스를 실행했는지 여부
	 * @param ctx
	 * @return
	 */
	public static void setBackServiceIsStart(Context ctx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_ALARMSET_YN, "Y");
	}
	
	/**
	 * 로그인 토큰
	 * @param ctx
	 * @return
	 */
	public static String getToken(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TOKEN);
	}
	/**
	 * 로그인 토큰
	 * @param ctx
	 * @return
	 */
	public static void setToken(Context ctx, String token) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TOKEN, token);
	}
	
	
	/**
	 * 로그인 유저 시퀀스
	 * @param ctx
	 * @return
	 */
	public static String getUserSeq(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_SEQ);
	}
	
	/**
	 * 로그인 유저 시퀀스
	 * @param ctx
	 * @return
	 */
	public static void setUserSeq(Context ctx, String userSeq) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_SEQ, userSeq);
	}
	
	
	/**
	 * Google Cloud Message 코드
	 * @param ctx
	 * @return
	 */
	public static String getGcmCode(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_GCM_CODE);
	}
	/**
	 * Google Cloud Message 코드
	 * @param ctx
	 * @return
	 */
	public static void setGcmCode(Context ctx, String c2dm) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_GCM_CODE, c2dm);
	}
	
	/**
	 * 수발신 광고의 노출됐던 포지션값
	 * @param ctx
	 * @return
	 */
	public static String getInOutAdIndex(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_INOUT_AD_SHOWED_INDEX);
	}
	
	/**
	 * 수발신 광고의 노출됐던 포지션값
	 * @param ctx
	 * @return
	 */
	public static void setInOutAdIndex(Context ctx, String index) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_INOUT_AD_SHOWED_INDEX, index);
	}
	
	/**
	 * 수발신 광고 화면 위치값(위 아래)
	 * @param ctx
	 * @return
	 */
	public static String getAdPosition(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_POSITION);
	}
	/**
	 * 수발신 광고 화면 위치값(위 아래)
	 * @param ctx
	 * @return
	 */
	public static void setAdPosition(Context ctx, String position) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_POSITION, position);
	}
	
	
	/**
	 * 유저 이메일
	 * @param ctx
	 * @return
	 */
	public static String getEmail(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID2, Context.MODE_PRIVATE, Define.PREFER_KEY_EMAIL);
	}
	/**
	 * 유저 이메일
	 * @param ctx
	 * @return
	 */
	public static boolean setEmail(Context ctx, String email) {
		return MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID2, Context.MODE_PRIVATE, Define.PREFER_KEY_EMAIL, email);
	}
	
	/**
	 * 유저 패스워드
	 * @param ctx
	 * @return
	 */
	public static String getPassword(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PASSWORD);
	}
	
	/**
	 * 유저 패스워드
	 * @param ctx
	 * @return
	 */
	public static void setPassword(Context ctx, String password) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PASSWORD, password);
	}
	
	
	/**
	 * 자동 로그인 설정
	 * */
	public static String getAutoLogin(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUTOLOGIN);
	}
	public static void setAutoLogin(Context ctx, String isAuto) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AUTOLOGIN, isAuto);
	}
	
	public static String getIsFirstContactSend(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_FIRST);
	}
	public static void setIsFirstContactSend(Context ctx, String isFirst) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_FIRST, isFirst);
	}
	
	/**
	 * 튜토리얼 실행 여부
	 * @param ctx
	 * @return
	 */
	public static String getIsTutorialView(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TUTORIAL_VIEW);
	}
	public static void setIsTutorialView(Context ctx, String isFirst) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TUTORIAL_VIEW, isFirst);
	}
	
	// 사용자 타입 설정
	public static String getUserType(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_TYPE);
	}
	public static void setUserType(Context ctx, String isFirst) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_TYPE, isFirst);
	}
	
	/**
	 * 슬라이드 광고 나오는 시간 ex) 8-22
	 * @param ctx
	 * @return
	 */
	public static String  getSlideAdPeriod(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_SLIDE_PERIOD_TIME);
	}
	public static void setSlideAdPeriod(Context ctx, String isFirst) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_SLIDE_PERIOD_TIME, isFirst);
	}
	
	/**
	 * 수발신 광고 나오는 시간 ex) 8-22
	 * @param ctx
	 * @return
	 */
	public static String  getCallAdPeriod(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_CALLOUT_AD_PERIOD_TIME);
	}
	public static void setCallAdPeriod(Context ctx, String isFirst) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_CALLOUT_AD_PERIOD_TIME, isFirst);
	}
	
	
	public static String getPolicyApp(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_APP);
	}
	public static void setPolicyApp(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_APP, version);
	}
	
	public static String getPolicyPrivercy(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_PRIVERCY);
	}
	public static void setPolicyPrivercy(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_PRIVERCY, version);
	}
	
	
	public static String getPolicyLocation(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_LOCATION);
	}
	public static void setPolicyLocation(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_POLICY_LOCATION, version);
	}
	
	public static String getSvrVersion(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_SVR_VERSION);
	}
	
	public static void setSvrVersion(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_SVR_VERSION, version);
	}
	
	// 나이대별 통화종료후 구분 설정 했는지여부
	public static String getIsFirstSetCallendCategory(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_SET_CALLEND_CATEGORY);
	}
	public static void setIsFirstSetCallendCategory(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_IS_SET_CALLEND_CATEGORY, version);
	}
	
	// 나이대 저장
	public static String getUserAges(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_AGES);
	}
	public static void setUserAges(Context ctx, String version) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_USER_AGES, version);
	}
	
	
	public static String getIndexCallAd(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_CALL_AD_IDX);
	}
	
	public static void setIndexCallAd(Context ctx, String call_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_CALL_AD_IDX, call_idx);
	}
	

	public static String getIndexLockAd(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_LOCK_AD_IDX);
	}
	
	public static void setIndexLockAd(Context ctx, String camp_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_LOCK_AD_IDX, camp_idx);
	}
	
	/*
	 * 알람 설정 
	 * */
	public static void setAlarmSound(Context ctx, String alarm_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_ALARM_SOUND, alarm_idx);
	}
	public static String getAlarmSound(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_ALARM_SOUND);
	}
	
	/* 로고송 설정 */
	
	public static void setLogoSound(Context ctx, String logo_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_LOGO_SOUND, logo_idx);
	}
	public static String getLogoSound(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_APP_LOGO_SOUND);
	}
	
	/*
	 * 수발신 광고 설정
	 * */
	
	public static void setCallAd(Context ctx, String alarm_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_CALL_AD, alarm_idx);
	}
	public static String getCallAd(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_CALL_AD);
	}
	
	/*
	 * 잠금화면 광고 설정
	 * */
	
	public static void setSlideAd(Context ctx, String alarm_idx) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SLIDE_AD, alarm_idx);
	}
	public static String getSlideAd(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_SLIDE_AD);
	}
	
	/**
	 * 
	 * Talk방 입장시 경고 메시지 출력여부
	 * */
	
	public static void setTalkJoinAlert(Context ctx, String alert) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TALK_ROOM_JOIN_ALERT, alert);
	}
	public static String getTalkJoinAlert(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_TALK_ROOM_JOIN_ALERT);
	}
	
	/*
	 * 푸시 알람 노티 설정
	 * */
	public static void setPushAlarm(Context ctx, String alert) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_NOTI, alert);
	}
	public static String getPushAlarm(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_NOTI);
	}
	
	/*
	 * Talk 푸시 알람 댓글 메시지알림 설정
	 * */
	public static void setPushMessageAlarm(Context ctx, String alert) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_SHOW_REPLY, alert);
	}
	public static String getPushMessageAlarm(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_SHOW_REPLY);
	}
	
	/*
	 * 화면꺼짐 상태에서 알림 다이어로그 설정
	 * */
	public static void setPushScreenOff(Context ctx, String alert) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_SHOW_DIALOG, alert);
	}
	public static String getPushScreenOff(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_PUSH_SHOW_DIALOG);
	}
	
	/*
	 * 통화 종료 후 나오는 웹 사이트 분류 저장
	 * */
	public static void setAdCallCategory(Context ctx, String category) {
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_CATEGORY, category);
	}
	public static String getAdCallCategory(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_CATEGORY);
	}
	
	
	/*
	 * 통화종료 후 연결될 URL , 광고 AD_SEQ , IDX 
	 * 
	 */
	
	public static void setCallEndAdSettingURL(Context ctx , String url){
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_URL, url);
	}
	public static String getCallEndAdSettingURL(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_URL);
	}
	
	
	public static void setCallEndAdSettingADSEQ(Context ctx , String ad_seq){
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_AD_SEQ, ad_seq);
	}
	public static String getCallEndAdSettingADSEQ(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_AD_SEQ);
	}
	
	public static void setCallEndAdSettingIDX(Context ctx , String idx){
		MadUtil.setSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_IDX, idx);
	}
	public static String getCallEndAdSettingIDX(Context ctx) {
		return MadUtil.getSharedPreferencesValue(ctx, Define.PREFER_ID, Context.MODE_PRIVATE, Define.PREFER_KEY_AD_CALL_END_IDX);
	}
	
	
	
	
}
