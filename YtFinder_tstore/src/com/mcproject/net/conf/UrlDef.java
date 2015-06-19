package com.mcproject.net.conf;

public class UrlDef {

	// search api
	public static final String SEARCH_URL = "https://www.googleapis.com/youtube/v3/search";
	// video api
	public static final String GET_DETAIL = "https://www.googleapis.com/youtube/v3/videos";
	
	public static final String GET_CATEGORY = "https://www.googleapis.com/youtube/v3/videoCategories";
	
	// 연관 검색어 가져오기
	public static final String GET_AUTO_SEARCH = "http://suggestqueries.google.com/complete/search?q=%s&client=firefox&ds=yt";
	
	// 댓글 가져오기
	public static final String GET_COMMENTS = "https://gdata.youtube.com/feeds/api/videos/%s/comments?orderby=published";
	
	// device code 요청 POST
	public static final String AUTH_GET_DEVICE_CODE_URL = "https://accounts.google.com/o/oauth2/device/code";
	// 토큰 요청 
	public static final String AUTH_GET_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
	
	// 서버 목록
	public static final String SERVER = "http://14.63.171.14/ytfinder_web/";
//	public static final String SERVER = "http://192.168.219.104:8080/ytfinder_web/";
	
	public static final String GET_MP3_FROM_SERVER = SERVER + "convert.jsp";
	// 버전 체크
	public static final String GET_VERSION = SERVER + "version.jsp";
	
	// 사용자 등록
	public static final String ADD_USER = SERVER + "usercmd/add.jsp";
	// 음원 추출 제한
	public static final String DOWNLOAD_LIMIT = SERVER + "usercmd/dnlimit.jsp";
	// 광고 뷰 통계
	public static final String ADD_ADVIEW_LOG = SERVER + "usercmd/ad_view_log.jsp";
	// 영상 스트리밍 URL 
	public static final String GET_STREAM_URL = SERVER + "get_stream_url.jsp";
	
	
	// 문의하기 
	public static final String ADD_CONTACT = SERVER + "usercmd/contact_add.jsp";
}
