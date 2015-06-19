package com.mcproject.net.conf;

import android.os.Environment;

public class Define {
	
	public static final String TAG = "YTFINDER";
	public static final boolean IS_LOG = true;
	// 0 = 티스토어
	// 1 = U+ 스토어
	// 2 = 올레 스토어
	public static final int MARKET_FLAG = 0;
	// T 애드 키 - T스토어
	public static final String TAD_CLIENT = "AX00046EA";
	// T 애드 키 - U+스토어
//	public static final String TAD_CLIENT = "AX000473E";
	// 앱스토어 URL
	public static final String TSTORE_URL = "http://www.tstore.co.kr/userpoc/game/viewProduct.omp?t_top=DP000503&dpCatNo=DP03018&insDpCatNo=DP03018&insProdId=0000660933&prodGrdCd=PD004401&stPrePageNm=DP25002&stActionPositionNm=06&stDisplayOrder=5&stKeywordCallId=W140510011428015b491";
	public static final String UPLUS_URL = "http://www.tstore.co.kr/userpoc/game/viewProduct.omp?t_top=DP000503&dpCatNo=DP03018&insDpCatNo=DP03018&insProdId=0000660933&prodGrdCd=PD004401&stPrePageNm=DP25002&stActionPositionNm=06&stDisplayOrder=5&stKeywordCallId=W140510011428015b491";
	public static final String OLLEH_URL = "http://www.tstore.co.kr/userpoc/game/viewProduct.omp?t_top=DP000503&dpCatNo=DP03018&insDpCatNo=DP03018&insProdId=0000660933&prodGrdCd=PD004401&stPrePageNm=DP25002&stActionPositionNm=06&stDisplayOrder=5&stKeywordCallId=W140510011428015b491";
	
	
	public static final String YT_API_KEY = "AIzaSyAUv1yEofaD0xaFE97Q2BYmdfGUdhajoRo";
	
	public static final String CLIENT_ID = "15064863549-857sqktnse9nfeabvfm0q8ai4q5qsion.apps.googleusercontent.com";
//	public static final String CLIENT_ID = "15064863549-um1pu47l145rg2vp2ab1a1es7f29jrej.apps.googleusercontent.com";
	
	public static final String AUTH_SCOPE = "https://www.googleapis.com/auth/youtube";
	public static final String GRANT_TYPE = "http://oauth.net/grant_type/device/1.0";
	public static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob"; //http://localhost
	
	public static final String APP_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytfinder"; 
	public static final String APP_MOVIE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytfinder/movie/"; 
	public static final String APP_SOUND_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ytfinder/sound/"; 
//	public static final String APP_MOVIE_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/ytfinder/";
//	public static final String APP_SOUND_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/ytfinder/";
	
	public static final String CONFIG_FILE_NM = "Config.xml";
	
	public static final String GCM_PROJECT_ID = "15064863549";
	
	/*
	 * preference 키값
	 */
	public static final String PREFER_ID = "setting";
	public static final String PREFER_KEY_USER_NATION = "user_nation";
	public static final String PREFER_KEY_USER_HLCODE = "user_hlCode";
	public static final String PREFER_KEY_SAFE_SEARCH = "safe_search";
	public static final String PREFER_KEY_SET_NOTIFICATION = "set_notification";
	public static final String PREFER_KEY_MOVIE_DOWNLOAD_RESOLUTION = "movie_download_resolution";
	public static final String PREFER_KEY_AD_TODAY = "ad_today";
	public static final String PREFER_KEY_AUDIO_OUTPUT_BITRATE = "audio_output_bitrate";
	public static final String PREFER_KEY_IS_ADDUSER = "is_adduser";
	public static final String PREFER_KEY_IS_AD_CACHED = "is_ad_cached";
	
	//db 테이블명
	public static final String TB_SEARCH_LIST_NORMAL = "search_list_normal";
	public static final String TB_USER_PLAYLIST = "user_playlist";
	public static final String TB_USER_FAVORITE = "user_favorite";
	public static final String TB_UPLOADER_VIDEO_UPLOAD_LOG = "uploader_video_upload_log";
	public static final String TB_VIDEO_CATEGORY = "video_category";
	
	// 서비스 주기 - 카테고리 목록 동기화
	public static final int CATEGORY_INIT_SERVICE_ID = 1111;
//	public static final long CATEGORY_INIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 60 * 3;
	public static final long CATEGORY_INIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 60 * 24;
	
	// 서비스 주기 - 비디오 업로더 영상 수집
	public static final int UPLOADER_VIDEO_INIT_SERVICE_ID = 2222;
//	public static final long UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 60 * 3;
	public static final long UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 10;
	
	// 서비스 주기 - 노티바 서비스
	public static final int CLIENT_NOTI_INIT_SERVICE_ID = 3333;
	public static final long CLIENT_NOTI_INIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 10;

	
	// 광고 노출 제한 서비스 
	public static final int AD_WALL_VIEW_LIMIT_SERVICE_ID = 4444;
	public static final long AD_WALL_VIEW_LIMIT_SERVICE_INTERVAL_TIME = 1000 * 60 * 60 * 24 ; // 테스트용으로 1분마다 리셋
	// 최대 광고 노출 횟수
	
	
	
}
