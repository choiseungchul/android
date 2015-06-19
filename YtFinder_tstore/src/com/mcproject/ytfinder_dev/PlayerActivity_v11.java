package com.mcproject.ytfinder_dev;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.mcproject.net.adapter.UploaderDetailAdapter;
import com.mcproject.net.adapter.YtFavoriteVideoAdapter;
import com.mcproject.net.adapter.YtListAdapter;
import com.mcproject.net.adapter.YtPlayListAdapter;
import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.customview.ActionBarPaddedFrameLayout;
import com.mcproject.net.customview.PlayerMovieListDialog;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.dto.UserDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.DeviceUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Mp3Downloader;
import com.mcproject.net.util.Params;
import com.mcproject.net.util.YouTubeMovieDownloader;
import com.mcproject.ytfavorite_t.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PlayerActivity_v11 extends YouTubeFailureRecoveryActivity{

	ActionBarPaddedFrameLayout player_container;
	YouTubePlayerView youtube_view;	
	String movieId;
	List<String> movieIds;
	String curr_mvId;
	String curr_title;
	String list_type;
	int yt_player_h;
	boolean isFullScreen = false;
	ActionBar action_bar;
	LinearLayout layout_portrait;

	YTListDto curr_movie_dto;
	ArrayList<YTListDto> dto_list = null;

	private YouTubePlayer mPlayer = null;
	private LayoutInflater inflate;
	ListView mv_list;
	AQuery aq;
	int player_type = 0; // single => 0 , multi => 1
	boolean single_repeat = false;
	// 여러개 영상 플레이타입
	int multi_type = 0; // 일반 => 0 , 하나만 반복 => 1, 순서섞기 => 2

	// 업로더 영상
	private boolean _bLastItem;
	private boolean _bLoading;
	LinearLayout progress;
	private int itemCount = 0;
	private int perPage = 10;
	private int totalCount = 0;

	// 검색에서 들어왔을떄
	private List<YTListDto> search_datas = new ArrayList<YTListDto>();
	private String search_nextPageToken;
	private ErrorListener errorListener;
	private RequestQueue reqQ;

	// 중간 버튼 4개
	private LinearLayout get_search_list, download_btn, play_type; // set_favorite,
	private TextView play_type_text3;
	private RecyclingImageView play_type_button; //set_favorite_image
	private boolean movieIsLoaded = false;
	private ArrayList<CollectedDto> uploader_datas;

	// 동영상 목록 어뎁터
	private YtListAdapter search_adap;
	private YtPlayListAdapter pl_adap;
	private YtFavoriteVideoAdapter fav_adap;
	private UploaderDetailAdapter uploader_adap;

	private String duration_type = "0";
	
	// 검색 또는 업로더쪽에서 온경우 스크롤 이벤트 넣음
	private OnScrollListener onSearchAdapScroll;
	private OnScrollListener onUploadAdapScroll;

	// 액션바 상단 
	private TextView movie_title;
	private RecyclingImageView landscape_get_favorite, landscape_get_search_list, landscape_download_btn, landscape_play_type;
	// 검색 리스트 다이어로그
	PlayerMovieListDialog dialog = null;
	private boolean isDialogOpen;
	private ListView dialog_lv;
	// 액션바 UI 숨김 타이머
	String TimerName = "hideTimer";
	Timer hideTimer = new Timer(TimerName);
	TimerTask hideUITask;
	private int hideDelay = 3 * 1000; // 3초
	Handler handle = new Handler();

	// 결과내 검색
	private EditText inner_search;
//	private TextView inner_sbtn;
	private LinearLayout inner_srch_layer;
	private boolean IS_INNER_SEARCH = false;

	private int inner_totalCount = 0;
	private int inner_itemCount = 0;
	private ArrayList<CollectedDto> uploader_insearch_data;
	private ArrayList<YTListDto> search_inner_datas;
	private String search_inner_nextPageToken;
	private String INNER_REQUEST_TAG = "inner_search";
	//////////////////////////////////////////////////////////////////////////////////////

	// 플레이어 액티비티로 넘어온 커서 위치
	int cursor = -1;
	// 플레이어 내부 커서
	int visible_item_position = 0;
	private LinearLayout share_btn;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		aq = new AQuery(getApplicationContext());

		inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		setContentView(R.layout.player);

		Bundle ex = getIntent().getExtras();
		if(ex != null){
			if(ex.getString("id") != null){
				movieId = ex.getString("id");
				curr_mvId = movieId;
				curr_title = ex.getString("mvName");
				curr_movie_dto = (YTListDto)ex.getSerializable("dto");
//				checkFavMovie(curr_mvId);

				curr_title = curr_movie_dto.title;				
				player_type = 0;

			}else if(ex.getBundle("ID_ARRAY") != null){
				String[] ids = ex.getBundle("ID_ARRAY").getStringArray("ids");
				movieIds = new ArrayList<String>();
				for(int i = 0 ; i < ids.length ; i++){
					movieIds.add(ids[i]);
				}
				dto_list = (ArrayList<YTListDto>) ex.getSerializable("dto");

				player_type = 1;

				if(movieIds.size() > 0) {
					curr_title = dto_list.get(0).title;
					curr_mvId = movieIds.get(0);
//					checkFavMovie(curr_mvId);
				}
			}

			// 플레이리스트 전체 재생인지 판단
			if(ex.getString("list_type") != null){
				list_type = ex.getString("list_type");
			}

			// 커서 위치
			cursor = ex.getInt("cursor");
		}
		initUI();
		initData();	
	}

	private void initData() {
		// 플레이어 아래 관련 영상 목록 출력
		getSearchList();
	}

	// 액션바 숨김 핸들러 호출
	private void hideActionBar(){
		handle.post(new Runnable() {
			@Override
			public void run() {
				if(action_bar.isShowing()){
					action_bar.hide();
				}
			}
		});
	}

	// 영상 다운로드 시작
	private void mv_download() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String title =  sdf.format(new Date(System.currentTimeMillis()));		
		if(curr_title != null){
			title = curr_title;
		}		
		LogUtil.I("title = " + title);
		
		YouTubeMovieDownloader getMovie = new YouTubeMovieDownloader(getApplicationContext(), title, curr_mvId);
		getMovie.execute("http://www.youtube.com/watch?v="+curr_mvId+"&feature=youtube_gdata_player&spf=prefetch");
//		if(movieIsLoaded){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//			String title =  sdf.format(new Date(System.currentTimeMillis()));		
//			if(curr_title != null){
//				title = curr_title;
//			}		
//			LogUtil.I("title = " + title);
//			
//			YouTubeMovieDownloader getMovie = new YouTubeMovieDownloader(getApplicationContext(), title, curr_mvId);
//			getMovie.execute("http://www.youtube.com/watch?v="+curr_mvId+"&feature=youtube_gdata_player&spf=prefetch");
//		}else{
//			Toast.makeText(getApplicationContext(), R.string.mv_download_is_not_loaded, Toast.LENGTH_SHORT).show();
//		}
	}

	private void initUI(){
		progress = (LinearLayout) inflate.inflate(R.layout.bottom_loader, null);

		play_type_text3 = (TextView)findViewById(R.id.play_type_text3);
		if(list_type != null){
			if(list_type.equals("uploader")){
				play_type_text3.setText(R.string.player_btn_text3_2);
			}else if(list_type.equals("favorite_video")){
				play_type_text3.setText(R.string.player_btn_text3_2);
			}else{
				play_type_text3.setText(R.string.player_btn_text3);
			}
		}
		
		inner_srch_layer = (LinearLayout)findViewById(R.id.inner_srch_layer);
		inner_search = (EditText)findViewById(R.id.inner_search);
		inner_search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionCode, KeyEvent event) {
				if(actionCode == EditorInfo.IME_ACTION_SEARCH){
					LogUtil.D("on editor action called");
					inner_search_btn_click();
				}
				return false;
			}
		});
		// 결과내 검색 버튼
//		inner_sbtn = (TextView)findViewById(R.id.inner_sbtn);
//		inner_sbtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LogUtil.D("inner_sbtn click called");
//				inner_search_btn_click();
//			}
//		});

		// 공통
		mv_list = (ListView) findViewById(R.id.mv_list);
//		set_favorite = (LinearLayout)findViewById(R.id.set_favorite);
//		//set_favorite.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				setFavorite();
//			}
//		});
//		set_favorite_image = (RecyclingImageView)findViewById(R.id.set_favorite_image);
		
		share_btn = (LinearLayout)findViewById(R.id.share_btn);
		share_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
			    sendIntent.setAction(Intent.ACTION_SEND);
			    sendIntent.putExtra(Intent.EXTRA_TEXT, curr_title + "\nhttp://www.youtube.com/watch?v=" + curr_mvId + "\n" + getString(R.string.share_footer));
			    sendIntent.setType("text/plain");			
			    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text)));
			}
		});

		// 결과내 검색 버튼 눌렀을때
		get_search_list = (LinearLayout)findViewById(R.id.get_search_list);
		get_search_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!IS_INNER_SEARCH) {
					if(list_type.equals("search") || list_type.equals("favorite_video") || list_type.equals("uploader")){
						inner_srch_layer.setVisibility(View.VISIBLE);
						aq.id(get_search_list).image(R.drawable.srch_detail_on);
						IS_INNER_SEARCH = true;
					}else{
						Toast.makeText(getApplicationContext(), R.string.srch_playlist_not_support, Toast.LENGTH_LONG).show();
					}
				}else{					
					IS_INNER_SEARCH = false;
					inner_srch_layer.setVisibility(View.GONE);
					aq.id(get_search_list).image(R.drawable.srch_detail_off);
				}
			}
		});
		download_btn = (LinearLayout)findViewById(R.id.download_btn);
		download_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				download_btnClick();
			}
		});

		OnClickListener playTypeListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(player_type == 0){
					if(single_repeat) single_repeat = false;
					else single_repeat = true;
					checkRepeatBtn();
				}else if(player_type == 1){
					if(multi_type == 0){
						multi_type = 1;
						////set_favorite.setClickable(false);			
						mPlayer.loadVideo(curr_mvId);						
					}else if(multi_type == 1){
						multi_type = 0;
						////set_favorite.setClickable(false);
						mPlayer.loadVideos(movieIds);
					}
					checkMultiPlayBtn();
				}
			}
		};

		play_type = (LinearLayout)findViewById(R.id.play_type);
		play_type.setOnClickListener(playTypeListener);

		play_type_button = (RecyclingImageView)findViewById(R.id.play_type_button);
		if(player_type == 1){
			aq.id(play_type_button).image(R.drawable.normal_on);
		}

		// API 11 이상
		player_container = (ActionBarPaddedFrameLayout)findViewById(R.id.player_container);
		action_bar = getActionBar();
		action_bar.setNavigationMode(ActionBar.DISPLAY_SHOW_CUSTOM);
		action_bar.setDisplayShowCustomEnabled(true);
		action_bar.setDisplayShowTitleEnabled(false);
		action_bar.setDisplayShowHomeEnabled(false);
		View ac_view = inflate.inflate(R.layout.action_bar_player_opt, null);
		movie_title = (TextView) ac_view.findViewById(R.id.movie_title);
		if(curr_movie_dto != null)
			movie_title.setText(curr_movie_dto.title);
		ac_view.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));

		// 공유기능 버튼으로 변경됨
		landscape_get_favorite = (RecyclingImageView) ac_view.findViewById(R.id.landscape_get_favorite);
		landscape_get_favorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent();
			    sendIntent.setAction(Intent.ACTION_SEND);
			    sendIntent.putExtra(Intent.EXTRA_TEXT, curr_title + "\nhttp://www.youtube.com/watch?v=" + curr_mvId + "\n" + getString(R.string.share_footer));
			    sendIntent.setType("text/plain");			
			    startActivity(Intent.createChooser(sendIntent, getString(R.string.share_text)));
			}
		});
		landscape_get_search_list = (RecyclingImageView) ac_view.findViewById(R.id.landscape_get_search_list);
		landscape_get_search_list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFullScreen){
					getSearchListDialog();
				}else{
					getSearchList();
				}
			}
		});
		landscape_download_btn = (RecyclingImageView) ac_view.findViewById(R.id.landscape_download_btn);
		landscape_download_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				download_btnClick();
			}
		});
		landscape_play_type = (RecyclingImageView)ac_view.findViewById(R.id.landscape_play_type);
		landscape_play_type.setOnClickListener(playTypeListener);
		if(player_type == 1){
			aq.id(landscape_play_type).image(R.drawable.normal_on);
		}

		action_bar.setCustomView(ac_view);

		player_container.setActionBar(action_bar);
		//Action bar background is transparent by default.
		action_bar.setBackgroundDrawable(new ColorDrawable(0xAA000000));

		youtube_view = (YouTubePlayerView)findViewById(R.id.youtube_view);
		layout_portrait = (LinearLayout)findViewById(R.id.layout_portrait);

		youtube_view.initialize(Define.YT_API_KEY, this);
		action_bar.hide();
	}

	private void checkMultiPlayBtn(){
		if(multi_type == 0){
			aq.id(play_type_button).image(R.drawable.normal_on);
			aq.id(landscape_play_type).image(R.drawable.normal_on);
		}else if(multi_type == 1){
			aq.id(play_type_button).image(R.drawable.repeat_on);
			aq.id(landscape_play_type).image(R.drawable.repeat_on);
		}else if(multi_type == 2){
			aq.id(play_type_button).image(R.drawable.play_shuffle_on);
			aq.id(landscape_play_type).image(R.drawable.play_shuffle_on);
		}
	}

	private void checkRepeatBtn(){
		if(single_repeat){
			aq.id(play_type_button).image(R.drawable.repeat_on);
			aq.id(landscape_play_type).image(R.drawable.repeat_on);
		}else{
			aq.id(play_type_button).image(R.drawable.repeat_off);
			aq.id(landscape_play_type).image(R.drawable.repeat_off);
		}
	}

//	private void checkFavMovie(String uploader_id){
//		boolean isFav = DbQueryUtil.isFavoriteMovie(getApplicationContext(), uploader_id);
//		if(isFav){
//			aq.id(set_favorite_image).image(R.drawable.fav_on);
//			aq.id(landscape_get_favorite).image(R.drawable.fav_on);
//		}else{
//			aq.id(set_favorite_image).image(R.drawable.fav_off);
//			aq.id(landscape_get_favorite).image(R.drawable.fav_off);
//		}
//	}

//	private void setFavorite(){
//		boolean isFav = DbQueryUtil.isFavoriteMovie(getApplicationContext(), curr_mvId);
//		if(movieIsLoaded){
//			if(isFav){
//				long rs = DbQueryUtil.removeFavoriteVideo(getApplicationContext(), curr_mvId);
//				if(rs != -1) {
//					aq.id(set_favorite_image).image(R.drawable.fav_off);
//					aq.id(landscape_get_favorite).image(R.drawable.fav_off);
//				}
//			}else{
//				long rs = DbQueryUtil.addFavoriteVideo(getApplicationContext(), curr_movie_dto);
//				if(rs != -1){
//					aq.id(set_favorite_image).image(R.drawable.fav_on);
//					aq.id(landscape_get_favorite).image(R.drawable.fav_on);
//				}
//			}
//		}	
//	}

	// 동영상 목록
	private void getSearchList(){
		if(list_type != null){
			// 검색쪽에서 왔을떄
			if(list_type.equals("search")){
				if(reqQ == null)
					reqQ = Volley.newRequestQueue(getApplicationContext());
				if(errorListener==null){
					errorListener = new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							LogUtil.E(error.toString());
						}
					};
				}

				search_datas = new ArrayList<YTListDto>();
				search_inner_datas = new ArrayList<YTListDto>();
				search_nextPageToken = null;
				search_inner_nextPageToken = null;

				if(!IS_INNER_SEARCH)
					search_adap = new YtListAdapter(getApplicationContext(), search_datas);
				else 
					search_adap = new YtListAdapter(getApplicationContext(), search_inner_datas);

				search_adap.setMovieThumbClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
						loadOtherVideo(data, "1");
					}
				});

				// 스크롤 이벤트
				onSearchAdapScroll = new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {}
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

						visible_item_position = firstVisibleItem;

						if ( _bLastItem == false && _bLoading == false && (totalItemCount - visibleItemCount) <= firstVisibleItem ) {
							LogUtil.D("getData ... Total length = " + totalItemCount);

							String srch_text = inner_search.getText().toString();
							
							if(MainActivity.saved_search_type == 0){
								if(!IS_INNER_SEARCH){
									getDataFirst();
								}else{
									if(!srch_text.equals("")){
										getData(srch_text);
									}
								}
							}else if(MainActivity.saved_search_type == 1){
								if(!IS_INNER_SEARCH){
									getData(MainActivity.saved_query);
								}else{
									if(!srch_text.equals("")){
										getData(srch_text);
									}
								}
							}else if(MainActivity.saved_search_type == 2){
								if(!IS_INNER_SEARCH){
									Bundle detail_query = MainActivity.saved_detail_search;
									getDetailSearchData(detail_query);									
								}else{
									if(!srch_text.equals("")){
										Bundle detail_query = MainActivity.saved_detail_search;
										detail_query.putString("q", srch_text);
										getDetailSearchData(detail_query);
									}
								}
							}
						}
					}
				};

				mv_list.setOnScrollListener(onSearchAdapScroll);

				_bLoading = true;

				// 처음 데이터 나올떄만 호출
				if(MainActivity.saved_search_type == 0){
					getDataFirst();
				}else if(MainActivity.saved_search_type == 1){
					String query = MainActivity.saved_query;
					getData(query);
				}else if(MainActivity.saved_search_type == 2){
					Bundle detail_query = MainActivity.saved_detail_search;
					getDetailSearchData(detail_query);
				}

			}else if(list_type.equals("playlist")){
				ArrayList<YTListDto> datas = DbQueryUtil.getPlayList(getApplicationContext());

				pl_adap = new YtPlayListAdapter(getApplicationContext(), datas);
				pl_adap.setMovieThumbClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data = (HashMap<String, Object>)v.getTag();
						loadOtherVideo(data, "1");
					}
				});
				mv_list.setAdapter(pl_adap);
				if(cursor != -1){
					mv_list.setSelection(cursor);
				}

			}else if(list_type.equals("playlist_multi")){
				ArrayList<YTListDto> datas = DbQueryUtil.getPlayList(getApplicationContext());

				pl_adap = new YtPlayListAdapter(getApplicationContext(), datas);
				pl_adap.setMovieThumbClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data = (HashMap<String, Object>)v.getTag();
						loadOtherVideo(data, "1");
					}
				});
				mv_list.setAdapter(pl_adap);
				if(cursor != -1){
					mv_list.setSelection(cursor);
				}

			}else if(list_type.equals("favorite_video")){
				// 즐겨찾기 영상에서 넘어옴
				ArrayList<YTListDto> datas = DbQueryUtil.getFavoriteVideo(getApplicationContext());
				fav_adap = new YtFavoriteVideoAdapter(getApplicationContext(), datas);
				fav_adap.setMovieThumbClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
						loadOtherVideo(data, "1");
					}
				});
				mv_list.setAdapter(fav_adap);
				if(cursor != -1){
					mv_list.setSelection(cursor);
				}

			}else if(list_type.equals("uploader")){		
				// 업로더 영상 목록에서 넘어옴
				final String uploader = getIntent().getExtras().getString("uploader");

				if(uploader != null){
					LogUtil.D("uploader = " + uploader);

					mv_list.addFooterView(progress);

					totalCount = DbQueryUtil.getCollectFavoriteVideoCount(getApplicationContext(), uploader);

					if(cursor == -1){
						if(uploader_datas == null) {
							uploader_datas = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader, 0, perPage);
						}
					}else{
						uploader_datas = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader, 0, cursor + perPage);
					}

					uploader_adap = new UploaderDetailAdapter(getApplicationContext(), uploader_datas);
					uploader_adap.setMovieClick(new OnClickListener() {
						@Override
						public void onClick(View v) {
							HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
							loadOtherVideo(data, "2");							
						}
					});
					uploader_adap.setAddPlayListClick(new OnClickListener() {
						@Override
						public void onClick(View v) {
							HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
							int position = (Integer) data.get("position");
							CollectedDto dto = (CollectedDto) data.get("data");
							if(!dto.isPlaylist){
								long rs = DbQueryUtil.addPlayList(getApplicationContext(), dto);
								if(rs != -1){
									dto.isPlaylist = true;
									uploader_datas.set(position, dto);
									uploader_adap.setData(uploader_datas);
									uploader_adap.notifyDataSetChanged();
								}
							}else{
								long rs = DbQueryUtil.removePlayList(getApplicationContext(), dto);
								if(rs != -1){
									dto.isPlaylist = false;
									uploader_datas.set(position, dto);
									uploader_adap.setData(uploader_datas);
									uploader_adap.notifyDataSetChanged();
								}
							}
						}
					});

					mv_list.setAdapter(uploader_adap);
					// 커서가 있다면 그쪽이로 이동
					if(cursor != -1){
						mv_list.setSelection(cursor);
					}

					onUploadAdapScroll = new OnScrollListener() {
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {}
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {

							visible_item_position = firstVisibleItem;

							if ( _bLastItem == false && _bLoading == false && (totalItemCount - visibleItemCount) <= firstVisibleItem ) {
								uploaderMovieListloadMore(uploader);
							}
						}
					};

					mv_list.setOnScrollListener(onUploadAdapScroll);

					if(cursor == -1){
						itemCount += perPage;
					}else{
						itemCount += (cursor+perPage);
					}

					_bLoading = false;

					removeFooter();
				}

			}
		}
	}

	// 업로더 목록 갱신 - 업로더 쪽에서 왔을때만 쓴다
	private void uploaderMovieListloadMore(String uploader_id){
		LogUtil.I("uploaderMovieListloadMore called");
		
		if(!IS_INNER_SEARCH){
			if(uploader_datas != null){
				if(totalCount > uploader_datas.size()){
					mv_list.addFooterView(progress);

					_bLoading = true;

					ArrayList<CollectedDto> moreData = null;
					moreData = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, uploader_datas.size(), perPage);

					uploader_datas.addAll(moreData);
					uploader_adap.setData(uploader_datas);
					uploader_adap.notifyDataSetChanged();

					_bLoading = false;

					removeFooter();
				}
			}
		}else{
			LogUtil.D("uploader_insearch_data...");
			if(uploader_insearch_data != null){
				LogUtil.I("inner_totalCount = " + inner_totalCount);
				LogUtil.I("uploader_insearch_data length = " + uploader_insearch_data.size());
				
				if(inner_totalCount > uploader_insearch_data.size()){
					String srch_text = inner_search.getText().toString();
					if(!srch_text.equals("")){
						mv_list.addFooterView(progress);

						_bLoading = true;

						ArrayList<CollectedDto> moreData = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, srch_text, uploader_insearch_data.size(), perPage);

						LogUtil.D("moreData = " + moreData.toString());

						uploader_insearch_data.addAll(moreData);
						uploader_adap.setData(uploader_insearch_data);
						uploader_adap.notifyDataSetChanged();

						_bLoading = false;

						removeFooter();
					}				
				}
			}
		}
	}

	private void download_btnClick(){
		AlertDialog.Builder alert = new Builder(PlayerActivity_v11.this);
		
		String[] items = new String[2];
		items[0] = getString(R.string.mv_dn_dialog_title);
		items[1] = getString(R.string.mp3_dn_dialog_title);
		
		alert.setItems(items, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					mv_download();
				}else if(which == 1){
					mp3_download();
				}
			}
		});	
		alert.show();
	}
	
	private void mp3_download() {
		try{			
//			String ranStr = McUtil.getRandomString("abcdefghijklmnopqrstuvwxyz1234567890", 15); 
			
			UserDto user = DeviceUtil.getUserInfo(getApplicationContext());
			
			Hashtable<String, String> params = new Hashtable<String, String>();
			if(user.getGoogle_account() != null){
				params.put("g_a", user.getGoogle_account());
			}else{
				params.put("g_a", "unknown google account");
			}
			if(user.getDevice_id() != null){
				params.put("d_i", user.getDevice_id());
			}else{
				params.put("d_i", "unknown device id");
			}
			
			// 다운로드 횟수 제한
			String param_query = McUtil.getParams(params);
			
			StringRequest req = new StringRequest(UrlDef.DOWNLOAD_LIMIT + param_query, new Listener<String>() {
				@Override
				public void onResponse(String response) {
					if(response.trim().equals("true")){
						// 다운로드가능
						Mp3Downloader dn = new Mp3Downloader(getApplicationContext(), curr_mvId, curr_title);
						dn.start();
					}else{
						Toast.makeText(getApplicationContext(), R.string.mp3_download_limited, Toast.LENGTH_LONG).show();
					}
				}
			}, new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					LogUtil.E("error = " + error);
				}
			});
			
			reqQ.add(req);
			
			
		}catch(Exception e){
			LogUtil.E("get mp3 down limit error : " + e.toString());
		}		
	}
	
	// 리스트에서 동영상 로드
	private void loadOtherVideo(HashMap<String, Object> data, String dataType) {
		// TODO Auto-generated method stub
		if(dataType.equals("1")){
			YTListDto dto = (YTListDto) data.get("data");
			int position = (Integer) data.get("position");
			////set_favorite.setClickable(false);
			if(dto.title != null) curr_title = dto.title;
			else LogUtil.D("847 loadOtherVideo 1 : " + dto.title);
			curr_mvId = dto.videoid;
			mPlayer.loadVideo(dto.videoid);
		}else if(dataType.equals("2")){
			CollectedDto dto = (CollectedDto) data.get("data");
			int position = (Integer) data.get("position");
			////set_favorite.setClickable(false);
			if(dto.title != null) curr_title = dto.title;
			else LogUtil.D("855 loadOtherVideo 2 : " + dto.title);
			curr_mvId = dto.videoid;
			mPlayer.loadVideo(dto.videoid);			
		}		
		if(isDialogOpen){
			try{
				dialog.dismiss();
			}catch(Exception e){
				LogUtil.E("812 : " +e.toString());
			}
		}	
		
		LogUtil.D("loadOtherVideo curr_title = " + curr_title);
	}

	// 플레이어 초기화 동작
	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		mPlayer = player;
		mPlayer.setShowFullscreenButton(true);
		//		player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
		mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

		if(!wasRestored) {
			if(movieId != null){
				////set_favorite.setClickable(false);
				mPlayer.loadVideo(movieId);
			}else {
				//set_favorite.setClickable(false);
				mPlayer.loadVideos(movieIds);
			}
		}

		// orientation 바뀔때
		mPlayer.setOnFullscreenListener(new OnFullscreenListener() {
			@Override
			public void onFullscreen(boolean fullscreen) {
				isFullScreen = fullscreen;

				LogUtil.I("IS FULLSCREEN = " + fullscreen);
				player_container.setEnablePadding(!fullscreen);

				if(movieIsLoaded){
					ViewGroup.LayoutParams playerParams = youtube_view.getLayoutParams();
					if (fullscreen) {
						layout_portrait.setVisibility(View.GONE);
						playerParams.width = MATCH_PARENT;
						playerParams.height = MATCH_PARENT;
						// orientation 변경
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						if(!action_bar.isShowing()) action_bar.show();

						hideUITask = new TimerTask() {
							@Override
							public void run() {
								if(action_bar != null){
									hideActionBar();
								}
							}
						};
						hideTimer.schedule(hideUITask, hideDelay);
						
//						if(!mPlayer.isPlaying()) mPlayer.play();

					} else {
						layout_portrait.setVisibility(View.VISIBLE);
						playerParams.width = MATCH_PARENT;
						playerParams.height = WRAP_CONTENT;

						// orientation 변경
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						if(action_bar.isShowing()) action_bar.hide();
						
//						if(!mPlayer.isPlaying()) mPlayer.play();
					}
				}
			}
		});

		mPlayer.setPlayerStateChangeListener(new PlayerStateChangeListener() {
			@Override
			public void onVideoStarted() {
			}
			@Override
			public void onVideoEnded() {
				LogUtil.I("onVideoEnded....");

				if(player_type == 0){
					if(single_repeat){
						mPlayer.loadVideo(curr_mvId);
					}
				}else if(player_type == 1){					
					// 반복재생일 경우
					if(multi_type == 1){
						mPlayer.loadVideo(curr_mvId);
					}
				}
			}
			@Override
			public void onLoading() {
				movieIsLoaded = false;
			}
			@Override
			public void onLoaded(String load_movieId) {
				LogUtil.I("onloaded = " + load_movieId);
				curr_mvId = load_movieId;

				// 플레이 리스트 쪽만
				if(dto_list != null){
					for(int i = 0 ; i < dto_list.size(); i++){
						YTListDto d = dto_list.get(i);
						if(d.videoid.equals(load_movieId)){
							curr_movie_dto = d;
							break;
						}
					}
				}
				
				LogUtil.I("onLoaded curr_title = " + curr_title);
				if(curr_title != null){
					movie_title.setText(curr_title);
				}
//				checkFavMovie(curr_mvId);
				movieIsLoaded = true;
			}
			@Override
			public void onError(ErrorReason arg0) {
				LogUtil.E(arg0.toString());
			}
			@Override
			public void onAdStarted() {				
			}
		});		

		// 클릭시 액션바 나오도록
		// 3초 이후 액션바 자동 숨김
		youtube_view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isFullScreen){
					if(!action_bar.isShowing()){
						action_bar.show();
						hideTimer.purge();
						hideTimer = new Timer(TimerName);
						hideUITask = new TimerTask() {
							@Override
							public void run() {
								hideActionBar();
							}
						};
						hideTimer.schedule(hideUITask, hideDelay);
					}
				}
			}
		});
	}

	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
		      ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		      : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
	
//	@Override
//	  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//	    int controlFlags = mPlayer.getFullscreenControlFlags();
//	    if (isChecked) {
//	      // If you use the FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE, your activity's normal UI
//	      // should never be laid out in landscape mode (since the video will be fullscreen whenever the
//	      // activity is in landscape orientation). Therefore you should set the activity's requested
//	      // orientation to portrait. Typically you would do this in your AndroidManifest.xml, we do it
//	      // programmatically here since this activity demos fullscreen behavior both with and without
//	      // this flag).
//	      setRequestedOrientation(PORTRAIT_ORIENTATION);
//	      controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
//	    } else {
//	      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
//	      controlFlags &= ~YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
//	    }
//	    mPlayer.setFullscreenControlFlags(controlFlags);
//	  }
	
	@Override
	protected Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){			
		}else if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
		}
		super.onConfigurationChanged(newConfig);
	}

	// 검색 쪽에서 접근하였을때 -> loadMore 기능도 추가된것
	private void getData(String query){
		LogUtil.I("getData");
		_bLoading = true;
		
		if(!isDialogOpen)
		mv_list.addFooterView(progress);
		else dialog_lv.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(!IS_INNER_SEARCH){
						if(response.isNull("nextPageToken"))
							search_nextPageToken = "empty";
						else search_nextPageToken = response.getString("nextPageToken");
					}else{
						if(response.isNull("nextPageToken"))
							search_inner_nextPageToken = "empty";
						else search_inner_nextPageToken = response.getString("nextPageToken");
					}

					// 아이디값 추가할거
					StringBuilder sb = new StringBuilder();
					int valid_content_length = 0;

					final List<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);

					if(_data.size() == 0){
						Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_LONG).show();
						removeFooter();
						return;
					}

					for(int i = 0 ; i < _data.size(); i++){
						sb.append(_data.get(i).videoid + ",");
					}

					valid_content_length = _data.size();

					String id_param = "";

					if(sb.length() > 1){
						id_param = sb.substring(0, sb.length() - 1);
					}

					// 영상 상세정보를 가져온다, 아이디 값이 있을경우만
					if(id_param.length() > 5){
						Hashtable<String, String> get_detail = new Hashtable<String, String>();
						get_detail.put("key", Define.YT_API_KEY);
						get_detail.put("part", "snippet,contentDetails");		// 필수파라미터
						get_detail.put("maxResults", String.valueOf(valid_content_length));			
						get_detail.put("id", id_param);

						String get_detail_param_str = McUtil.getParams(get_detail);
						JsonObjectRequest get_mv_detail = new JsonObjectRequest(UrlDef.GET_DETAIL + get_detail_param_str, null , new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {

								List<YTListDto> _inputed = McUtil.parseVideoDetailList(getApplicationContext(), response, _data);

								if(!IS_INNER_SEARCH)
									search_datas.addAll(_inputed);
								else search_inner_datas.addAll(_inputed);

								// 모든 데이터를 다 가져왔으니 리스트에 뿌림
								setUIData();
							}
						}, errorListener);

						// 결과내 검색일경우 태그 삽입
						if(IS_INNER_SEARCH)
						get_mv_detail.setTag(INNER_REQUEST_TAG);
						reqQ.add(get_mv_detail);
					}

				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} 
			}
		};

		Params params = new Params(getApplicationContext());

		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet");				// 조회 방식
		params.put("maxResults", "50");		// 조회할 갯수
		// 정령방식 가져오기
		String orderBy = "viewCount";
		if(MainActivity.saved_sort == 1){
			orderBy = "viewCount";
		}else if(MainActivity.saved_sort == 2){
			orderBy = "date";
		}else if(MainActivity.saved_sort == 3){
			orderBy = "rating";
		}else{
			orderBy = "viewCount";
		}
		params.put("order", orderBy); 						// 정렬방식

		if(!IS_INNER_SEARCH){
			if(search_nextPageToken != null){
				if(search_nextPageToken.equals("empty")){
					removeFooter();
					
					return;
				}
				params.put("pageToken",search_nextPageToken);	// 페이지 토큰
			}
		}else{
			if(search_inner_nextPageToken != null){
				if(search_inner_nextPageToken.equals("empty")){
					removeFooter();
					
					return;
				}
				params.put("pageToken",search_inner_nextPageToken);	// 페이지 토큰
			}
		}
		
		if(query != null){
			params.put("q", query);			// 검색어
		}

		String getParam = McUtil.getParams(params);
		JsonObjectRequest request = new JsonObjectRequest(UrlDef.SEARCH_URL + getParam,  null,  listener, errorListener);

		// 결과내 검색일경우 태그 삽입
		if(IS_INNER_SEARCH)
		request.setTag(INNER_REQUEST_TAG);
		reqQ.add(request);

		params.trace();
	}
	// 처음에 뿌려줄때  -> loadMore 기능도 추가된것
	private void getDataFirst(){
		_bLoading = true;
		if(!isDialogOpen)
			mv_list.addFooterView(progress);
		else dialog_lv.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.isNull("nextPageToken"))
						search_nextPageToken = "empty";
					else search_nextPageToken = response.getString("nextPageToken");
					
					List<YTListDto> _data = McUtil.parsePopularDataList(getApplicationContext(), response);
					
					if(_data.size() == 0){
						Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_LONG).show();
						return;
					}
					
					search_datas.addAll(_data);
					setUIData();
					
				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} catch( Exception e ){
					LogUtil.E(e.toString());
				}
			}
		};

		Params params = new Params(getApplicationContext());

		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet,contentDetails");				// 조회 방식
		params.put("maxResults", "50");		// 조회할 갯수
		params.put("chart", "mostPopular");
		params.put("regionCode", AppUserSettings.getNationCode(getApplicationContext()));
		if(!IS_INNER_SEARCH){
			if(search_nextPageToken != null){
				if(search_nextPageToken.equals("empty")){
					removeFooter();									
					return;
				}
				params.put("pageToken",search_nextPageToken);	// 페이지 토큰
			}
		}

		String getParam = McUtil.getParams(params);
		JsonObjectRequest request = new JsonObjectRequest(UrlDef.GET_DETAIL + getParam,  null,  listener, errorListener);

		// 결과내 검색일경우 태그 삽입
		if(IS_INNER_SEARCH)
		request.setTag(INNER_REQUEST_TAG);
		reqQ.add(request);

		params.trace();

	}

	// 상세검색  -> loadMore 기능도 추가된것
	private void getDetailSearchData(Bundle detail){
		LogUtil.I("getDetailSearchDate");
		_bLoading = true;

		if(!isDialogOpen)
		mv_list.addFooterView(progress);
		else dialog_lv.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(!IS_INNER_SEARCH){
						if(response.isNull("nextPageToken"))
							search_nextPageToken = "empty";
						else search_nextPageToken = response.getString("nextPageToken");
					}else{
						if(response.isNull("nextPageToken"))
							search_inner_nextPageToken = "empty";
						else search_inner_nextPageToken = response.getString("nextPageToken");
					}

					// 아이디값 추가할거
					StringBuilder sb = new StringBuilder();
					int valid_content_length = 0;

					final List<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);

					if(_data.size() == 0){
						Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_LONG).show();
						return;
					}

					//					datas.addAll(_data);

					for(int i = 0 ; i < _data.size(); i++){
						sb.append(_data.get(i).videoid + ",");
					}
					valid_content_length = _data.size();

					String id_param = "";

					if(sb.length() > 1){
						id_param = sb.substring(0, sb.length() - 1);
					}

					// 영상 상세정보를 가져온다, 아이디 값이 있을경우만
					if(id_param.length() > 5){
						Hashtable<String, String> get_detail = new Hashtable<String, String>();
						get_detail.put("key", Define.YT_API_KEY);
						get_detail.put("part", "snippet,contentDetails");		// 필수파라미터
						get_detail.put("maxResults", String.valueOf(valid_content_length));			
						get_detail.put("id", id_param);

						String get_detail_param_str = McUtil.getParams(get_detail);
						JsonObjectRequest get_mv_detail = new JsonObjectRequest(UrlDef.GET_DETAIL + get_detail_param_str, null , new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {

								List<YTListDto> _inputed = McUtil.parseVideoDetailList(getApplicationContext(), response, _data);

								// 비디오 러닝타임 전체 선택이 아닐경우
								if(!duration_type.equals("0")){
									// duration 계산
									int duration_select = Integer.parseInt(duration_type);

									// 1분미만
									for(int i = 0; i < _inputed.size(); i++){
										YTListDto dto = _inputed.get(i);
										if(duration_select == 1){
											if(dto.hh == 0 && dto.mm == 0){
												if(!IS_INNER_SEARCH)
													search_datas.add(dto);
												else search_inner_datas.add(dto);
											}
										}else if(duration_select == 2){
											if(dto.hh == 0 && dto.mm <= 5 && dto.mm > 0){
												if(dto.mm == 5){
													if(dto.ss == 0) {
														if(!IS_INNER_SEARCH)
															search_datas.add(dto);
														else search_inner_datas.add(dto);
													}
												}else{
													if(!IS_INNER_SEARCH)
														search_datas.add(dto);
													else search_inner_datas.add(dto);
												}
											}
										}else if(duration_select == 3){
											if(dto.hh == 0 && dto.mm < 11 && dto.mm >= 5){
												if(dto.mm == 10){
													if(dto.ss == 0) {
														if(!IS_INNER_SEARCH)
															search_datas.add(dto);
														else search_inner_datas.add(dto);
													}
												}
												if(!IS_INNER_SEARCH)
													search_datas.add(dto);
												else search_inner_datas.add(dto);
											}
										}else if(duration_select == 4){
											if(dto.mm >= 10){
												if(!IS_INNER_SEARCH)
													search_datas.add(dto);
												else search_inner_datas.add(dto);
											}
										}
									} // for
								}else{
									// 비디오 러닝타임 전체
									if(!IS_INNER_SEARCH)
										search_datas.addAll(_inputed);
									else search_inner_datas.addAll(_inputed);
								}
								// ui에 넣기
								setUIData();
							}
						}, errorListener);

						// 결과내 검색일경우 태그 삽입
						if(IS_INNER_SEARCH)
						get_mv_detail.setTag(INNER_REQUEST_TAG);
						reqQ.add(get_mv_detail);
					}
				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} 
			}
		};

		Params params = new Params(getApplicationContext());

		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet");				// 필수 파라미터
		params.put("maxResults", "50");
		if(!IS_INNER_SEARCH){
			if(search_nextPageToken != null){
				if(search_nextPageToken.equals("empty")){
					removeFooter();
					return;
				}
				params.put("pageToken",search_nextPageToken);	// 페이지 토큰
			}
		}else{
			if(search_inner_nextPageToken != null){
				if(search_inner_nextPageToken.equals("empty")){
					removeFooter();
					return;
				}
				params.put("pageToken",search_inner_nextPageToken);	// 페이지 토큰
			}
		}

		String getParam = McUtil.getParams(params);

		Bundle d_param = detail;
		if(!d_param.get("duration_type").equals("0")){			
			duration_type = d_param.getString("duration_type");
		}

		JsonObjectRequest request = new JsonObjectRequest(UrlDef.SEARCH_URL + getParam + McUtil.getDetailParams(d_param),  null,  listener, errorListener);
		// 결과내 검색일경우 태그 삽입
		if(IS_INNER_SEARCH)
		request.setTag(INNER_REQUEST_TAG);
		
		reqQ.add(request);

		params.trace();
	}

	// 검색결과에서 왔을때만 쓴다
	private void setUIData(){
		// 모든 데이터를 다 가져왔으니 리스트에 뿌림
		if(!isDialogOpen){
			if(!IS_INNER_SEARCH)
				search_adap.setData(search_datas);
			else search_adap.setData(search_inner_datas);

			if(mv_list.getAdapter() == null)
				mv_list.setAdapter(search_adap);
			else search_adap.notifyDataSetChanged();
			removeFooter();

			// 결과내 재검색이 아닐경우만 커서 이동
			if(!IS_INNER_SEARCH){				
				if(cursor != -1 ){
					LogUtil.D("cursor = " + cursor);
					if(mv_list.getCount() > cursor){
						if(visible_item_position > cursor){
							mv_list.setSelection(visible_item_position);
						}else{
							mv_list.setSelection(cursor);
						}
					}else{
						// 커서위치가 검색목록보다 더 큰경우 검색목록 추가
						LogUtil.I("좀더 검색 pageToken = " + search_nextPageToken);
						getData(MainActivity.saved_query);
					}
				}
			}

		}else{
			if(!IS_INNER_SEARCH)
				search_adap.setData(search_datas);
			else search_adap.setData(search_inner_datas);
			if(dialog_lv.getAdapter() == null)
				dialog_lv.setAdapter(search_adap);
			else search_adap.notifyDataSetChanged();

			dialog_lv.removeFooterView(progress);

			// 결과내 재검색이 아닐경우만 커서 이동
			if(!IS_INNER_SEARCH){
				LogUtil.D("visible_item_position = " + visible_item_position);
				if(dialog_lv.getCount() > visible_item_position){
					dialog_lv.setSelection(visible_item_position);
				}
			}
		}

		_bLoading = false;
	}

	// 액션바에 있는 동영상 목록
	private void getSearchListDialog() {		
		isDialogOpen = true;
		// 영상멈춤
		mPlayer.pause();
		
		if(dialog == null) {
			dialog = new PlayerMovieListDialog(PlayerActivity_v11.this);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					// 다이어로그 없어지면 다시 재생
					mPlayer.play();
				}
			});
		}

		dialog_lv = dialog.getListView();

		dialog.setTitle(R.string.player_list_dialog_title);

		LogUtil.I("cursor = " + cursor);
		LogUtil.I("visible_item_position = " + visible_item_position);

		if(list_type.equals("search")){
			if(search_adap != null) {
				dialog.setSearchAdapter(search_adap);
				dialog_lv.setOnScrollListener(onSearchAdapScroll);
				if(cursor != -1){
					if(visible_item_position > cursor){
						dialog_lv.setSelection(visible_item_position);
					}else{
						dialog_lv.setSelection(cursor);
					}
				}
				dialog.show();
			}else Toast.makeText(getApplicationContext(), R.string.player_list_dialog_list_data_no, Toast.LENGTH_LONG).show();
		}else if(list_type.equals("playlist")){
			if(pl_adap != null) {
				dialog.setPlayListAdapter(pl_adap);
				if(cursor != -1){
					if(visible_item_position > cursor){
						dialog_lv.setSelection(visible_item_position);
					}else{
						dialog_lv.setSelection(cursor);
					}
				}
				dialog.show();
			}else {
				Toast.makeText(getApplicationContext(), R.string.player_list_dialog_list_data_no, Toast.LENGTH_LONG).show();
			}
		}else if(list_type.equals("playlist_multi")){
			if(pl_adap != null) {
				dialog.setPlayListAdapter(pl_adap);
				if(cursor != -1){
					if(visible_item_position > cursor){
						dialog_lv.setSelection(visible_item_position);
					}else{
						dialog_lv.setSelection(cursor);
					}
				}
				dialog.show();
			}else Toast.makeText(getApplicationContext(), R.string.player_list_dialog_list_data_no, Toast.LENGTH_LONG).show();
		}else if(list_type.equals("favorite_video")){
			if(fav_adap != null) {
				dialog.setFavoriteAdapter(fav_adap);
				if(cursor != -1){
					if(visible_item_position > cursor){
						dialog_lv.setSelection(visible_item_position);
					}else{
						dialog_lv.setSelection(cursor);
					}
				}
				dialog.show();
			}else Toast.makeText(getApplicationContext(), R.string.player_list_dialog_list_data_no, Toast.LENGTH_LONG).show();
		}else if(list_type.equals("uploader")){
			if(uploader_adap != null) {
				dialog.setUploaderAdapter(uploader_adap);
				dialog_lv.setOnScrollListener(onUploadAdapScroll);
				if(cursor != -1){
					if(visible_item_position > cursor){
						LogUtil.I("visible_item_position = " + visible_item_position);
						dialog_lv.setSelection(visible_item_position);
					}else{
						dialog_lv.setSelection(cursor);
					}
				}
				dialog.show();
			}else Toast.makeText(getApplicationContext(), R.string.player_list_dialog_list_data_no, Toast.LENGTH_LONG).show();
		}
	}
	
	// 결과내 검색 버튼 눌렀을시 
	private void inner_search_btn_click(){
		String srch_text = inner_search.getText().toString();
		if(srch_text.equals("")){
			Toast.makeText(getApplicationContext(), R.string.srch_alert, Toast.LENGTH_SHORT).show();
			return;
		}
		
		IS_INNER_SEARCH = true;
		// 키보드 숨김
		hideSoftKeyboard(inner_search);
		
		if(list_type != null){
			if(list_type.equals("search")){
				// 모든 결과내 검색 멈춤
				reqQ.cancelAll(INNER_REQUEST_TAG);
				// 데이터 리셋
				search_inner_datas = new ArrayList<YTListDto>();
				search_adap = new YtListAdapter(getApplicationContext(), search_inner_datas);
				search_adap.setMovieThumbClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
						loadOtherVideo(data, "1");
					}
				});
				mv_list.setAdapter(search_adap);
				search_inner_nextPageToken = null;
				
				if(MainActivity.saved_search_type == 0){
					getData(srch_text);
				}else if(MainActivity.saved_search_type == 1){
					getData(srch_text);
				}else if(MainActivity.saved_search_type == 2){
					Bundle detail = MainActivity.saved_detail_search;
					detail.putString("q", srch_text);
					getDetailSearchData(detail);
				}
			}else if(list_type.equals("favorite_video")){				
				ArrayList<YTListDto> list = DbQueryUtil.getFavoriteVideo(getApplicationContext(), srch_text);
				YtListAdapter adap = new YtListAdapter(getApplicationContext(), list);
				adap.setMovieThumbClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
						loadOtherVideo(data, "1");
					}
				});
				mv_list.setAdapter(adap);
			}else if(list_type.equals("uploader")){
				final String uploader = getIntent().getExtras().getString("uploader");
				
				if(uploader != null){
					mv_list.addFooterView(progress);

					inner_totalCount = DbQueryUtil.getCollectFavoriteVideoCount(getApplicationContext(), uploader, srch_text);
					if(inner_totalCount == 0){
						removeFooter();
						Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_LONG).show();
						return;
					}
					
					uploader_insearch_data = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader, srch_text, 0, 10);

					uploader_adap = new UploaderDetailAdapter(getApplicationContext(), uploader_insearch_data);
					uploader_adap.setMovieClick(new OnClickListener() {
						@Override
						public void onClick(View v) {
							HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
							loadOtherVideo(data, "2");
						}
					});
					uploader_adap.setAddPlayListClick(new OnClickListener() {
						@Override
						public void onClick(View v) {
							HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
							int position = (Integer) data.get("position");
							CollectedDto dto = (CollectedDto) data.get("data");
							if(!dto.isPlaylist){
								long rs = DbQueryUtil.addPlayList(getApplicationContext(), dto);
								if(rs != -1){
									dto.isPlaylist = true;
									uploader_insearch_data.set(position, dto);
									uploader_adap.setData(uploader_insearch_data);
									uploader_adap.notifyDataSetChanged();
								}
							}else{
								long rs = DbQueryUtil.removePlayList(getApplicationContext(), dto);
								if(rs != -1){
									dto.isPlaylist = false;
									uploader_insearch_data.set(position, dto);
									uploader_adap.setData(uploader_insearch_data);
									uploader_adap.notifyDataSetChanged();
								}
							}
						}
					});

					mv_list.setAdapter(uploader_adap);

					onUploadAdapScroll = new OnScrollListener() {
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {}
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem,
								int visibleItemCount, int totalItemCount) {

							visible_item_position = firstVisibleItem;

							if ( _bLastItem == false && _bLoading == false && (totalItemCount - visibleItemCount) <= firstVisibleItem ) {
								uploaderMovieListloadMore(uploader);
							}
						}
					};
					mv_list.setOnScrollListener(onUploadAdapScroll);

					inner_itemCount += perPage;
					_bLoading = false;

					removeFooter();
				}
			}// list_type uploader
		}
	}
	
	private void removeFooter(){
		try{
			if(!isDialogOpen){
				if(mv_list != null){
					if(mv_list.getFooterViewsCount() > 0)
					mv_list.removeFooterView(progress);
				}
			}else{
				if(dialog_lv != null){
					if(dialog_lv.getFooterViewsCount() > 0)
						dialog_lv.removeFooterView(progress);
				}
			}
		}catch(Exception e){
			LogUtil.E("removeFooter error : " + e.toString());
		}
	}
	
	protected void hideSoftKeyboard(View view) {
		InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@Override
	public void onBackPressed() {
		LogUtil.D("onBackPressed fullscreen = " + isFullScreen);
		int ori = McUtil.getScreenOrientation(this);
		if(ori == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			super.onBackPressed();
		}else{
			super.onBackPressed();
		}
	}
}