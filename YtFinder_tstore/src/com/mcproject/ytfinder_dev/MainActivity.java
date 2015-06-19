package com.mcproject.ytfinder_dev;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.mcproject.net.adapter.YtListAdapter;
import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.customview.FavDialog;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.UserDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.service.AdLimitService;
import com.mcproject.net.service.CategorySyncService;
import com.mcproject.net.service.ClientNotificaionService;
import com.mcproject.net.service.UploaderVideoCollectService;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.DbUtil;
import com.mcproject.net.util.DeviceUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Params;
import com.mcproject.ytfavorite_t.R;

import com.crashlytics.android.Crashlytics;


public class MainActivity extends BaseActivity {

	ListView list_layout;
	YtListAdapter adap;
	SwipeDismissAdapter adapter;
	OnScrollListener onScroll;
	List<YTListDto> datas = new ArrayList<YTListDto>();

	RequestQueue reqQ;
	String QUEUE_TAG = "mvlist";
	String nextPageToken;
	String currQuery;
	public static String saved_query;
	public static int saved_sort = 0;
	public static Bundle saved_detail_search;
	public static int saved_search_type = 0;

	// UI
	private boolean                        _bLoading = false;
	private boolean                        _bLastItem = false;
	OnDismissCallback onDismiss;
	LinearLayout progress;
	LayoutInflater inflate;
	ProgressDialog progDialog;

	private boolean isDetailSearch;
	private int search_type = 0;
	ErrorListener errorListener;
	
	private LinearLayout sort_viewcount;
	private LinearLayout sort_datetime;
	private LinearLayout sort_recomm;
	OnClickListener sortClickListener;
	int sortType = 0;																// ???? ???
	RecyclingImageView sort_viewcount_image, sort_datetime_image, sort_recomm_image;			// ???? ??????
	AlertDialog.Builder NationSelectDialog;
	
	String duration_type = "0";
	private LinearLayout sort_btn_layer;

	@Override
	protected void onStart() {
		// ?????????
//		McUtil.addShortCut(getApplicationContext());
		Crashlytics.start(this);
		VolleyLog.DEBUG = false;
		
		// ?????
		File folder = new File(Define.APP_FOLDER);
		if(!folder.exists()) folder.mkdirs();
		
		// db ??
		if(!DbUtil.checkDataBase(getApplicationContext())){
			DbUtil.initDbFile(getApplicationContext());
		}
		
		// ???? ????
		if(!McUtil.isServiceRunningCheck(getApplicationContext(), "CategorySyncService")){
			McUtil.startAlarmService(getApplicationContext(), CategorySyncService.class, Define.CATEGORY_INIT_SERVICE_ID, Define.CATEGORY_INIT_SERVICE_INTERVAL_TIME, 10);	
		}
		if(!McUtil.isServiceRunningCheck(getApplicationContext(), "UploaderVideoCollectService")){
			McUtil.startAlarmService(getApplicationContext(), UploaderVideoCollectService.class, Define.UPLOADER_VIDEO_INIT_SERVICE_ID, Define.UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME, 10);	
		}
		if(!McUtil.isServiceRunningCheck(getApplicationContext(), "ClientNotificaionService")){
			McUtil.startAlarmService(getApplicationContext(), ClientNotificaionService.class, Define.CLIENT_NOTI_INIT_SERVICE_ID, Define.CLIENT_NOTI_INIT_SERVICE_INTERVAL_TIME, 120);	
		}
		if(!McUtil.isServiceRunningCheck(getApplicationContext(), "AdLimitService")){
			McUtil.startAlarmService(getApplicationContext(), AdLimitService.class, Define.AD_WALL_VIEW_LIMIT_SERVICE_ID, Define.AD_WALL_VIEW_LIMIT_SERVICE_INTERVAL_TIME, 15);	
		}
		
		// ?????? ??
		
		
		super.onStart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		setContentView(R.layout.main_layout);
		
		reqQ = Volley.newRequestQueue(getApplicationContext());
		inflate = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

		errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.E(error.toString());
				
				if(progDialog != null)
				progDialog.dismiss();
				try{
					if(list_layout.getFooterViewsCount() > 0)
						list_layout.removeFooterView(progress);
					
					reqQ.cancelAll(new RequestQueue.RequestFilter() {
						@Override
						public boolean apply(Request<?> request) {
							// TODO Auto-generated method stub
							return true;
						}
					});
					
					if(progDialog != null)
					progDialog.dismiss();
					
				}catch(Exception e){
				}
			}
		};
		
		initUI();
		
		Bundle ex = getIntent().getExtras();
		if(ex != null){
			if(ex.getString("search") != null){
				String q = ex.getString("search");
				currQuery = q;
				getSrchQuery().setText(q);
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				getData();
			}else if(ex.getBundle("search_detail") != null){
				Bundle search_detail  = ex.getBundle("search_detail");
				setDetailParams(search_detail);
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				getDetailSearchDate();
			}
		}else{
			// ????? ???? ????
			if(AppUserSettings.getNationCode(getApplicationContext()).equals("")){
				NationSelectDialog = new Builder(MainActivity.this);
				NationSelectDialog.setTitle(getString(R.string.user_select_nation_title));
				final String[] srch_nation_names = getResources().getStringArray(R.array.user_select_nation_names);
				ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, srch_nation_names);
				NationSelectDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						McUtil.setNation(getApplicationContext(), which);
						initData();
					}
				});
				NationSelectDialog.show();
			}else{
				if(saved_search_type == 0){
					initData();
				}else if(saved_search_type == 1){
					currQuery = saved_query;
					getSrchQuery().setText(currQuery);
					progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
					getData();
				}else if(saved_search_type == 2){
					Bundle search_detail  = saved_detail_search;
					setDetailParams(search_detail);
					progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
					getDetailSearchDate();
				}
			}
		}
		
		// ?????? ??????? ???
		if(AppUserSettings.getIsAddUser(getApplicationContext()).equals("")){
			UserDto ud = DeviceUtil.getUserInfo(getApplicationContext());
			LogUtil.I("device id = " + ud.getDevice_id());
			LogUtil.I("google account = " + ud.getGoogle_account());
			LogUtil.I("locale = " + ud.getLocale());
			
			Listener<String> listener = new Listener<String>() {
				@Override
				public void onResponse(String response) {
					String rs = response.trim();
					if(rs.equals("1")){
						LogUtil.D("added User");
						AppUserSettings.setIsAddUser(getApplicationContext(), "Y");
					}else if(rs.equals("2")){
						LogUtil.D("already added");
						AppUserSettings.setIsAddUser(getApplicationContext(), "Y");
					}else if(rs.equals("-1")){
						LogUtil.D("device id length not 15");
					}else if(rs.equals("0")){
						LogUtil.D("insert table error");
					}else{
						LogUtil.D("web result = " + rs);
					}
				}
			};
			
			try{
				Hashtable<String, String> params = new Hashtable<String, String>();
				if(ud.getGoogle_account() != null){
					params.put("g_a", ud.getGoogle_account());
				}else{
					params.put("g_a", "unknown google account");
				}
				if(ud.getDevice_id() != null){
					params.put("d_i", ud.getDevice_id());
				}else{
					params.put("d_i", "unknown device id");
				}
				if(ud.getLocale() != null){
					params.put("locale", ud.getLocale());
				}else{
					params.put("locale", "unknown locale");
				}
				
				String p =McUtil.getParams(params);
				
				StringRequest sreq = new StringRequest(UrlDef.ADD_USER + p, listener, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "error : " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
					}
				});
				
				reqQ.add(sreq);
			}catch(NullPointerException e){
				LogUtil.E("send user info NULL : " + e.toString());
			}catch(Exception e){
				LogUtil.E(e.toString());
			}
		}
		
	}

	// ?????
	private void getData(){
		LogUtil.I("getData");
		_bLoading = true;
		search_type = 1;
		isDetailSearch = false;

		list_layout.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.isNull("nextPageToken"))
						nextPageToken = "empty";
					else nextPageToken = response.getString("nextPageToken");

					// ????? ??????
					StringBuilder sb = new StringBuilder();
					int valid_content_length = 0;

					final List<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);

					if(_data.size() == 0){
						set_search_empty();
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

					// ???? ???????? ????????, ????? ???? ???????I
					if(id_param.length() > 5){
						Hashtable<String, String> get_detail = new Hashtable<String, String>();
						get_detail.put("key", Define.YT_API_KEY);
						get_detail.put("part", "snippet,contentDetails");		// ?????????
						get_detail.put("maxResults", String.valueOf(valid_content_length));			
						get_detail.put("id", id_param);

						String get_detail_param_str = McUtil.getParams(get_detail);
						JsonObjectRequest get_mv_detail = new JsonObjectRequest(UrlDef.GET_DETAIL + get_detail_param_str, null , new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {

								List<YTListDto> _inputed = McUtil.parseVideoDetailList(getApplicationContext(), response, _data);
								
								datas.addAll(_inputed);
								
								if(progDialog != null){
									if(progDialog.isShowing()){
										progDialog.dismiss();
									}
								}

								// ??? ??????? ?? ?????????? ??????? ???
								setUIData();
								sort_btn_layer.setVisibility(View.VISIBLE);

							}
						}, errorListener);

						reqQ.add(get_mv_detail);
					}

				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} 
			}
		};

		Params params = new Params(getApplicationContext());

		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet");				// ??? ???
		params.put("maxResults", "10");		// ????? ????
		params.put("order", "viewCount"); 						// ??????
		if(nextPageToken != null){
			if(nextPageToken.equals("empty")){
				set_search_empty();
				return;
			}
			params.put("pageToken",nextPageToken);	// ?????? ???
		}
		if(sortType == 1){
			params.put("order", "viewCount");
		}else if(sortType == 2){
			params.put("order", "date");
		}else if(sortType == 3){
			params.put("order", "rating");
		}
		if(currQuery != null){
			params.put("q", currQuery);			// ?????
		}

		String getParam = McUtil.getParams(params);
		JsonObjectRequest request = new JsonObjectRequest(UrlDef.SEARCH_URL + getParam,  null,  listener, errorListener);
		request.setTag(QUEUE_TAG);
		reqQ.add(request);
		
		params.trace();
	}

	// ????? ??????
	private void getDataFirst(){
		LogUtil.I("getDataFirst");
		
		search_type = 0;
		
		_bLoading = true;
		list_layout.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.isNull("nextPageToken"))
						nextPageToken = "empty";
					else nextPageToken = response.getString("nextPageToken");

					JSONArray items = response.getJSONArray("items");

					if(items.length() == 0){
						set_search_empty();
						return;
					}
					
					final List<YTListDto> _data = McUtil.parsePopularDataList(getApplicationContext(), response);

					datas.addAll(_data);
					setUIData();
					
					sort_btn_layer.setVisibility(View.GONE);
					
				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} catch (Exception e){ 
					LogUtil.E(e.toString());
				}finally{
					if(progDialog != null){
						if(progDialog.isShowing()){
							progDialog.dismiss();
						}
					}
				}
			}
		};

		Params params = new Params(getApplicationContext());
		
		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet,contentDetails");				// ??? ???
		params.put("maxResults", "10");		// ????? ????
		params.put("chart", "mostPopular");
		params.put("regionCode", AppUserSettings.getNationCode(getApplicationContext())); 	
		if(nextPageToken != null){
			if(nextPageToken.equals("empty")){
				set_search_empty();
				return;
			}
			params.put("pageToken",nextPageToken);	// ?????? ???
		}
		
		if(sortType == 1){
			params.put("order", "viewCount");
		}else if(sortType == 2){
			params.put("order", "date");
		}else if(sortType == 3){
			params.put("order", "rating");
		}

		String getParam = McUtil.getParams(params);
		JsonObjectRequest request = new JsonObjectRequest(UrlDef.GET_DETAIL + getParam,  null,  listener, errorListener);
		request.setTag(QUEUE_TAG);
		reqQ.add(request);
		
		params.trace();

	}

	// ????
	private void getDetailSearchDate(){
		LogUtil.I("getDetailSearchDate");
		
		search_type = 2;
		
		_bLoading = true;

		list_layout.addFooterView(progress);

		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if(response.isNull("nextPageToken"))
						nextPageToken = "empty";
					else nextPageToken = response.getString("nextPageToken");

					// ????? ??????
					StringBuilder sb = new StringBuilder();
					int valid_content_length = 0;

					final List<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);
					
					if(_data.size() == 0){
						set_search_empty();
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

					// ???? ???????? ????????, ????? ???? ???????I
					if(id_param.length() > 5){
						Hashtable<String, String> get_detail = new Hashtable<String, String>();
						get_detail.put("key", Define.YT_API_KEY);
						get_detail.put("part", "snippet,contentDetails");		// ?????????
						get_detail.put("maxResults", String.valueOf(valid_content_length));			
						get_detail.put("id", id_param);

						String get_detail_param_str = McUtil.getParams(get_detail);
						JsonObjectRequest get_mv_detail = new JsonObjectRequest(UrlDef.GET_DETAIL + get_detail_param_str, null , new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								
								List<YTListDto> _inputed = McUtil.parseVideoDetailList(getApplicationContext(), response, _data);
								
								if(!duration_type.equals("0")){
									// duration ???
									int duration_select = Integer.parseInt(duration_type);
									
									// 1?????
									for(int i = 0; i < _inputed.size(); i++){
										YTListDto dto = _inputed.get(i);
										if(duration_select == 1){
											if(dto.hh == 0 && dto.mm == 0){
												datas.add(dto);
											}
										}else if(duration_select == 2){
											if(dto.hh == 0 && dto.mm <= 5 && dto.mm > 0){
												if(dto.mm == 5){
													if(dto.ss == 0) datas.add(dto);
												}else{
													datas.add(dto);
												}
											}
										}else if(duration_select == 3){
											if(dto.hh == 0 && dto.mm < 11 && dto.mm >= 5){
												if(dto.mm == 10){
													if(dto.ss == 0) datas.add(dto);
												}
												datas.add(dto);
											}
										}else if(duration_select == 4){
											if(dto.mm >= 10){
												datas.add(dto);
											}
										}
											
									} // for
								}else{
									datas.addAll(_inputed);
								}
								
								if(progDialog != null){
									if(progDialog.isShowing()){
										progDialog.dismiss();
									}
								}
								// ui?? ???
								setUIData();
								sort_btn_layer.setVisibility(View.VISIBLE);
							}
						}, errorListener);
						reqQ.add(get_mv_detail);
					}

				} catch (JSONException e) {
					LogUtil.E(e.toString());
				} 
			}
		};

		Params params = new Params(getApplicationContext());
		
		params.put("key",Define.YT_API_KEY);
		params.put("part","snippet");				// ??? ??????
		params.put("maxResults", "10");
		if(nextPageToken != null){
			if(nextPageToken.equals("empty")){
				set_search_empty();
				return;
			}
			params.put("pageToken",nextPageToken);	// ?????? ???
		}
		if(sortType == 1){
			params.put("order", "viewCount");
		}else if(sortType == 2){
			params.put("order", "date");
		}else if(sortType == 3){
			params.put("order", "rating");
		}

		String getParam = McUtil.getParams(params);
		
		Bundle d_param = getDetailParams();
		if(!d_param.get("duration_type").equals("0")){
			params.put("maxResults", "50");
			duration_type = d_param.getString("duration_type");
		}else{
			params.put("maxResults", "10");
			duration_type = d_param.getString("duration_type");
		}
		
		JsonObjectRequest request = new JsonObjectRequest(UrlDef.SEARCH_URL + getParam + McUtil.getDetailParams(d_param),  null,  listener, errorListener);
		request.setTag(QUEUE_TAG);
		reqQ.add(request);
		
		params.trace();
	}

	private void setUIData(){
		// ??? ??????? ?? ?????????? ??????? ???
		adap.setData(datas);
		
		if(list_layout.getAdapter() == null)
			list_layout.setAdapter(adap);
		else adap.notifyDataSetChanged();
		list_layout.setOnScrollListener(onScroll);

		list_layout.removeFooterView(progress);
		_bLoading = false;
	}
	
	private void initData() {
		progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
		if(search_type == 1){
			getData();
		}else if(search_type == 0) getDataFirst();
	}

	private void initUI() {
		// ??? ????
		super.BaseUIInit(this);
		
		sort_btn_layer = (LinearLayout)findViewById(R.id.sort_btn_layer);
		
		progress = (LinearLayout) inflate.inflate(R.layout.bottom_loader, null);

		//??? ?????? 0 ???? ????
		list_layout = (ListView)findViewById(R.id.list_layout);

		adap = new YtListAdapter(getApplicationContext(), datas);
		adap.setMovieThumbClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
				YTListDto dto = (YTListDto) data.get("data");
				int position = (Integer) data.get("position");
				saveSearchHistory();
				gotoSinglePlayer(dto.videoid, dto, "search", position );
			}
		});
		
		// ?????? ??? 3???? ????
		adap.setFaviconClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				@SuppressWarnings("unchecked")
				final HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
				final YTListDto dto = (YTListDto) data.get("data");
				final int position = (Integer) data.get("position");
				
				boolean[] checkItems = new boolean[3];
				checkItems[0] = dto.isFavoriteVideo;
				checkItems[1] = dto.isFavoriteUploader;
				checkItems[2] = DbQueryUtil.isPlaylist(getApplicationContext(), dto.videoid);
				
				final FavDialog f_dialog = new FavDialog(MainActivity.this, 0, dto, checkItems);
				
				Runnable run = new Runnable() {	
					@Override
					public void run() {
						boolean isFavVideo = f_dialog.getIsFavVideo();
						boolean isFavUploader = f_dialog.getIsFavUploader();
						// ???????? ??????? ???I 
						if(!dto.isFavoriteUploader && isFavUploader){
							// ?????????? ??? ?????
							for(int i = 0 ; i < datas.size(); i++){
								YTListDto datasItem = datas.get(i);
								if(datasItem.channel_id.equals(dto.channel_id)){
									datasItem.isFavoriteUploader = true;
									datas.set(i, datasItem);
								}
							}
						}else if(dto.isFavoriteUploader && !isFavUploader){
							// ???????? ???????? ???
							// ????????? ??? ???
							for(int i = 0 ; i < datas.size(); i++){
								YTListDto datasItem = datas.get(i);
								if(datasItem.channel_id.equals(dto.channel_id)){
									datasItem.isFavoriteUploader = false;
									datas.set(i, datasItem);
								}
							}
						}
						
						dto.isFavoriteVideo = isFavVideo;			
						datas.set(position, dto);
						
						adap.setData(datas);
						adap.notifyDataSetChanged();
					}
				};
				
				f_dialog.setComplete(run);
				f_dialog.show();
			}
		});
				
//		onDismiss = new OnDismissCallback() {
//			@Override
//			public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
//				for (int position : reverseSortedPositions) {
//					adap.remove(position);
//				}
//			}
//		};

		getSrchQuery().setImeAction(new Runnable() {
			@Override
			public void run() {
				datas = new ArrayList<YTListDto>();
				currQuery = getSrchQuery().getText().toString();
				DbQueryUtil.saveSearchNormalString(getApplicationContext(), currQuery);
				nextPageToken = null;
				getData();
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				hideSoftKeyboard(getSrchQuery());
			}
		});
		getSrchQuery().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				datas = new ArrayList<YTListDto>();
				currQuery = getSrchQuery().getText().toString();
				DbQueryUtil.saveSearchNormalString(getApplicationContext(), currQuery);
				nextPageToken = null;
				getData();
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				hideSoftKeyboard(getSrchQuery());
				getSrchQuery().srch_query_slide_left();
			}
		});

		adapter = new SwipeDismissAdapter(adap, onDismiss);
		onScroll = new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if ( _bLastItem == false && _bLoading == false && (totalItemCount - visibleItemCount) <= firstVisibleItem ) {
					LogUtil.D("getData ... Total length = " + totalItemCount);
					if(!isDetailSearch){
						if(search_type == 1)
						getData();
						else getDataFirst();
					}else{
						getDetailSearchDate();
					}
				}
			}
		};
		getSrchStartBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				datas = new ArrayList<YTListDto>();
				nextPageToken = null;
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				setLayer();

				getDetailSearchDate();
				isDetailSearch = true;
			}
		});
		
		sort_viewcount_image = (RecyclingImageView)findViewById(R.id.sort_viewcount_image); 
		sort_datetime_image = (RecyclingImageView)findViewById(R.id.sort_datetime_image);
		sort_recomm_image = (RecyclingImageView)findViewById(R.id.sort_recomm_image);
		
		sortClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(search_type == 0) {
					Toast.makeText(getApplicationContext(), R.string.srch_sort_alert, Toast.LENGTH_LONG).show();
					return;
				}
				
				datas = new ArrayList<YTListDto>();
				nextPageToken = null;
				progDialog = ProgressDialog.show(MainActivity.this, null, getString(R.string.srch_loading), true, true);
				
				if(v.getId() == R.id.sort_viewcount){
					sort_viewcount_image.setImageResource(R.drawable.arrow_up_on);
					sort_datetime_image.setImageResource(R.drawable.arrow_up_off);
					sort_recomm_image.setImageResource(R.drawable.arrow_up_off);
					sortType = 1;
					datas = new ArrayList<YTListDto>();
				}else if(v.getId() == R.id.sort_datetime){
					sort_datetime_image.setImageResource(R.drawable.arrow_up_on);
					sort_viewcount_image.setImageResource(R.drawable.arrow_up_off);
					sort_recomm_image.setImageResource(R.drawable.arrow_up_off);
					sortType = 2;
					datas = new ArrayList<YTListDto>();
				}else if(v.getId() == R.id.sort_recomm){
					sort_recomm_image.setImageResource(R.drawable.arrow_up_on);
					sort_datetime_image.setImageResource(R.drawable.arrow_up_off);
					sort_viewcount_image.setImageResource(R.drawable.arrow_up_off);
					sortType = 3;
					datas = new ArrayList<YTListDto>();
				}
				
				loadCancel();
				
				if(search_type == 1){
					getData();
				}else if(search_type == 2){
					getDetailSearchDate();
				}
			}
		};
		
		sort_viewcount = (LinearLayout)findViewById(R.id.sort_viewcount);
		sort_datetime = (LinearLayout)findViewById(R.id.sort_datetime);
		sort_recomm = (LinearLayout)findViewById(R.id.sort_recomm);
		sort_viewcount.setOnClickListener(sortClickListener);
		sort_datetime.setOnClickListener(sortClickListener);
		sort_recomm.setOnClickListener(sortClickListener);
		
		getMenuBtn(1).setOnClickListener(null);
		getMenuBtn(1).setBackgroundResource(R.drawable.button_black_on);
	}
	
	// ???? ??????? ???
	private void loadCancel(){
		_bLoading = true;
		reqQ.cancelAll(QUEUE_TAG);
		if(progDialog != null){
			progDialog.dismiss();
		}
		if(progress != null){
			try{
				if(list_layout.getFooterViewsCount() > 0){
					list_layout.removeFooterView(progress);
				}
			}catch(Exception e){
				LogUtil.E(e.toString());
			}
		}
		
		_bLoading = false;
	}
	
//	private void uploaderReload(String uploader_id, boolean isFav){
//		_bLoading = true;
//		
//		for(int i = 0 ; i < datas.size(); i++){
//			YTListDto dto = datas.get(i);
//			if(dto.channel_id.equals(uploader_id)){
//				dto.isFavoriteUploader = isFav;
//				datas.set(i, dto);
//			}
//		}
//		adap.setData(datas);
//		adap.notifyDataSetChanged();
//		
//		_bLoading = false;
//	}
	
	// ??? ???????
	private void set_search_empty(){
		Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_LONG).show();
		if(progDialog!=null) progDialog.dismiss();
		if(progress!=null){
			try{
				if(list_layout.getFooterViewsCount() > 0){
					list_layout.removeFooterView(progress);
				}
			}catch (Exception e) {
				LogUtil.E(e.toString());
			}
		}
	}
	
	// ???????? ????? ??????? ????
	private void saveSearchHistory(){
		saved_search_type = search_type;
		saved_sort = sortType;
		saved_query = currQuery;
		saved_detail_search = getDetailParams();
	}
	
	@Override
	protected void onStop() {
		saveSearchHistory();
		super.onStop();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}	
	
}