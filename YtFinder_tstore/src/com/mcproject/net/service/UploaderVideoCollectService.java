package com.mcproject.net.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Params;

public class UploaderVideoCollectService extends Service {

	private ErrorListener errorListener;
	private ArrayList<Boolean> _isLoad = new ArrayList<Boolean>();
	private ArrayList<Boolean> _isFinal = new ArrayList<Boolean>();
	private ArrayList<Integer> totalResults = new ArrayList<Integer>();
	private ArrayList<String> nextPageToken = new ArrayList<String>();
	//private ArrayList<RequestQueue> rq = new ArrayList<RequestQueue>();
	
	RequestQueue reqQ = null;

	Timer collectTimer = new Timer();
	TimerTask collectTask = null;
	private long delay = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.D("collect service start..");
		
		errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.E(error.toString());
			}
		};
		
		if(collectTask == null){
			collectTask = new TimerTask() {
				@Override
				public void run() {
					
					LogUtil.D("collectTask start..");
					
					_isLoad = new ArrayList<Boolean>();
					_isFinal = new ArrayList<Boolean>();
					totalResults = new ArrayList<Integer>();
					nextPageToken = new ArrayList<String>();					
					reqQ = Volley.newRequestQueue(getApplicationContext());
					
					ArrayList<String > uploaders = new ArrayList<String>();
					
					try{
						uploaders = DbQueryUtil.getFavoriteUploaderId(getBaseContext());
						
						LogUtil.I("uploader size = " + uploaders.size());
						
						for(int i = 0 ; i < uploaders.size() ; i++){							
							_isLoad.add(false);
							_isFinal.add(false);
							totalResults.add(0);
							nextPageToken.add("--");
							
							LogUtil.D("start process uploaderid = " + uploaders.get(i));
							LogUtil.D("UploaderProgressingList uploader stat = " + UploaderProgressingList.uploaders.get(uploaders.get(i)));
							if(UploaderProgressingList.setUploaderOn(uploaders.get(i))){							
								startCollectProcess(uploaders.get(i), i);
							}else{
								LogUtil.D(uploaders.get(i) + " already collecting..");
							}
						}
					}catch(NullPointerException e){		
						LogUtil.E(e.toString());
					}catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			};
			collectTimer.schedule(collectTask, delay, Define.UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME);
		}else{
			LogUtil.D("collectTask already scheduled");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void startCollectProcess(String uploader_id, final int idx){
		LogUtil.D("startCollectProcess : " + uploader_id + " idx : " + idx);
		
		String recent = DbQueryUtil.getFavoriteVideoRecentTime(getBaseContext(), uploader_id);
		
		LogUtil.W("publishAfter = " + recent);
		
		collectData(uploader_id, idx, recent);
	}

	private void collectData(final String uploader_id, final int idx, final String publishAfter){

		LogUtil.D("collectData uploader_id = " + uploader_id + "  start!!!");
		LogUtil.D("collectData publishAfter = " + publishAfter);
		
//		LogUtil.I( "_isLoad : " + _isLoad.get(idx));
//		LogUtil.I( "_isFinal : " + _isFinal.get(idx));
//		LogUtil.I( "totalResults : " + totalResults.get(idx));
//		LogUtil.I( "nextPageToken : " + nextPageToken.get(idx));
//		LogUtil.I( "rq : " + rq.get(idx));
//		LogUtil.I( "publishAfter : " + publishAfter);
		
		if(UploaderProgressingList.uploaders.containsKey(uploader_id)){
			if(UploaderProgressingList.uploaders.get(uploader_id).equals("Y")){
				Listener<JSONObject> listener = new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						final ArrayList<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);

						if(_data.size() == 1){
							if(_data.get(0).publish_date_origin.equals(publishAfter)){
								_isFinal.set(idx, true);
								totalResults.set(idx, 0);
								LogUtil.W("UploaderVideoCollectService  same data and return!!!");
								UploaderProgressingList.setUploaderOff(uploader_id);
								LogUtil.D("collectData uploader_id = " + uploader_id + "  complete!!!");								
								return;
							}
						}
						
						StringBuilder sb = new StringBuilder();
						
						if(_data.size() == 0) {
							_isFinal.set(idx, true);
						}else{

							try {
								JSONObject pageInfo = response.getJSONObject("pageInfo");
								if(totalResults.get(idx) == 0)
									totalResults.set(idx, pageInfo.getInt("totalResults"));

								if(response.isNull("nextPageToken")){
									nextPageToken.set(idx,"empty");
								}else{
									nextPageToken.set(idx, response.getString("nextPageToken"));
								}
							} catch (JSONException e) {
								LogUtil.E(e.toString());
							}

							for(int i = 0 ; i < _data.size(); i++){
								sb.append(_data.get(i).videoid + ",");
							}

							int valid_content_length = _data.size();

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
										int curr_total = totalResults.get(idx);
										
										LogUtil.D("현재 "+ uploader_id + " 의 남은 갯수 = " + curr_total);
										
										totalResults.set(idx,  curr_total - _inputed.size());

										// list내용을 sqlite에 저장
										// DB에 넣기전에 한번더 체크
										if(UploaderProgressingList.uploaders.containsKey(uploader_id)){
											if(UploaderProgressingList.uploaders.get(uploader_id).equals("Y")){
												DbQueryUtil.addUploaderLogTable(getBaseContext(), _inputed);
											}

											if(_isFinal.get(idx)){
												UploaderProgressingList.setUploaderOff(uploader_id);
												LogUtil.D("collectData uploader_id = " + uploader_id + "  complete!!!");
											}

											if(totalResults.get(idx) > 0){
												collectData(uploader_id, idx, publishAfter);
											}
										}

										LogUtil.D("처리후 "+ uploader_id + " 의 남은 갯수 = " + (curr_total - _inputed.size()));
										
										_isLoad.set(idx, false);
									}
								}, errorListener);

								reqQ.add(get_mv_detail);
							}
						}
					}
				};

				Params params = new Params(getApplicationContext());
				params.put("part", "snippet");
				params.put("order", "date");
				if(publishAfter != null)
				params.put("publishedAfter", publishAfter);
				params.put("maxResults", "50");
				params.put("channelId", uploader_id);
				try{
					if(!nextPageToken.get(idx).equals("--")){
						if(!nextPageToken.get(idx).equals("empty")){
							params.put("pageToken", nextPageToken.get(idx));
						}else{
							_isFinal.set(idx, true);
							LogUtil.D("collectData uploader_id = " + uploader_id + " complete.........");
						}
					}
				}catch(Exception e) {
					LogUtil.E(e.toString());
				}
				
				JsonObjectRequest req = new JsonObjectRequest(UrlDef.SEARCH_URL + McUtil.getParams(params) , null, listener, errorListener);

				reqQ.add(req);
			}
		}
		
	}

	@Override
	public void onDestroy() {
		LogUtil.I("UploaderVideoCollectService destroyed");
		super.onDestroy();
	}
	
}
