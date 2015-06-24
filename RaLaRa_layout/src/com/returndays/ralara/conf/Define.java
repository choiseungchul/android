package com.returndays.ralara.conf;

import java.util.Arrays;
import java.util.List;

import android.os.Environment;

public class Define {
	
	public static final String TAG = "RETURNDAYS";
	public static final boolean IS_LOG = true;
	public static final boolean IS_TSTORE = false;
	
	public static final String FONT_OPENSANS = "RETURNDAYS";
	public static final String FONT_OPENSANS_R = "fonts/OpenSans-Regular.ttf";
	public static final String FONT_OPENSANS_L = "fonts/OpenSans-Light.ttf";
	public static final String FONT_NANUMGOTHIC = "fonts/NanumGothic.ttf";
	
	public static final String AD_FOLDER = Environment.getExternalStorageDirectory().toString() + "/ralara";
	public static final String AD_CALL_FOLDER = AD_FOLDER + "/call/.nomedia";
	public static final String AD_APP_FOLDER = AD_FOLDER + "/app/.nomedia";
	public static final String AD_TALK_FOLDER = AD_FOLDER + "/talk/.nomedia";
	
	public static final String GOOGLE_KEY = "AIzaSyDSKtat3n8lIPDQVPsksjelxQvWX6KDMeA";
	public static final String CONFIG_FILE_NM = "Config.xml";
	
	public static final String GCM_PROJECT_ID = "15064863549";
	
	public static final String CRYPTO_KEY = "ralara!@#$";
	
	public static final int EXCHANGE_SCRATCH_COUNT = 3;
	public static final int EXCHANGE_EGG_COUNT = 300;
	
	public static final String RETURNDAYS_PHONE = "0269591230";
	
	/*
	 * preference 키값을 정의 한다.
	 */
	public static final String PREFER_ID = "setting";
	public static final String PREFER_ID2 = "setting2";
	public static final String PREFER_KEY_ALARMSET_YN = "alarm_set";
	public static final String PREFER_KEY_TOKEN = "token";
	public static final String PREFER_KEY_USER_SEQ = "user_seq";
	public static final String PREFER_KEY_EMAIL = "user_email";
	public static final String PREFER_KEY_PASSWORD = "user_password";
	public static final String PREFER_KEY_AUTOLOGIN = "auto_login";
	public static final String PREFER_KEY_IS_FIRST = "is_first";
	public static final String PREFER_KEY_GCM_CODE = "gcm_code";
	public static final String PREFER_KEY_INOUT_AD_SHOWED_INDEX = "inout_ad_showed_index";
	public static final String PREFER_KEY_AD_POSITION = "ad_position";
	public static final String PREFER_KEY_APP_SVR_VERSION = "app_version";
	
	public static final String PREFER_KEY_APP_CALL_AD_IDX = "calladidx";
	public static final String PREFER_KEY_APP_LOCK_AD_IDX = "lockadidx";
	
	public static final String PREFER_KEY_APP_ALARM_SOUND = "alarm_sound";
	public static final String PREFER_KEY_APP_LOGO_SOUND = "logo_sound";
	
	public static final String PREFER_KEY_CALL_AD= "call_ad"; 
	public static final String PREFER_KEY_SLIDE_AD = "slide_ad";
	
	public static final String PREFER_KEY_POLICY_APP = "policy_app";
	public static final String PREFER_KEY_POLICY_PRIVERCY = "policy_privercy";
	public static final String PREFER_KEY_POLICY_LOCATION = "policy_location";
	
	public static final String PREFER_KEY_TALK_ROOM_JOIN_ALERT = "talk_room_join_alert";
	
	public static final String PREFER_KEY_PUSH_NOTI = "push_notification";
	public static final String PREFER_KEY_PUSH_SHOW_REPLY = "push_reply_content";
	public static final String PREFER_KEY_PUSH_SHOW_DIALOG = "push_show_dialog";
	
	public static final String PREFER_KEY_AD_CALL_CATEGORY = "ad_call_category";
	
	public static final String PREFER_KEY_AD_CALL_END_URL = "all_call_end_url";
	public static final String PREFER_KEY_AD_CALL_END_AD_SEQ = "all_call_end_ad_seq";
	public static final String PREFER_KEY_AD_CALL_END_IDX = "all_call_end_idx";
	
	
	public static final String PREFER_KEY_TUTORIAL_VIEW = "tutorial_view";
	public static final String PREFER_KEY_USER_TYPE = "user_type";
	public static final String PREFER_KEY_AD_SLIDE_PERIOD_TIME = "ad_slide_period";
	public static final String PREFER_KEY_CALLOUT_AD_PERIOD_TIME = "call_out_ad_time";
	
	public static final String PREFER_KEY_IS_SET_CALLEND_CATEGORY = "set_callend_category";
	public static final String PREFER_KEY_USER_AGES = "user_ages";
	
	/*
	 * Facebook API Permissions
	 * */
	public static String[] _fb_perm_read = {
		"basic_info",
		"user_birthday",
		"user_location",
		"user_about_me",
		"read_friendlists",
		"friends_about_me",
		"user_likes",
		"user_status",
		"email"
	};
	public static String[] _fb_perm_publish = {
		"publish_stream"
	};
	public static List<String> FB_PERMS_READ = Arrays.asList(_fb_perm_read);
	public static List<String> FB_PERMS_PUBLISH = Arrays.asList(_fb_perm_publish);
	
	
	
	/*
	 *    수발신 광고
	 * 
	 */
	public static final int AD_DOWN_SERVICE_ID = 43543;
	public static final long AD_DOWN_INTERVAL_TIME = 1000 * 60 * 60 * 3;//테스트용으로 6시간 설정.. (광고 받기 서비스)
	//public static final long AD_DOWN_INTERVAL_TIME = 1000 * 60 ;//테스트용으로 6시간 설정.. (광고 받기 서비스)
	
	
	public static final int AD_FAIL_SERVICE_ID = 69843;
	public static final int AD_SLIDE_SERVICE_ID = 99943;
	
	public static final long AD_SLIDE_INTERVAL_TIME = 1000 * 10;
	
	
	
}
