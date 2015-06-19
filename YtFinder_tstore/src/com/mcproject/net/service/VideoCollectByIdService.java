package com.mcproject.net.service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Params;

public class VideoCollectByIdService extends Service{

	private String publishAfter = "1970-01-01T00:00:00Z";
	private boolean _isLoad = false;
	private boolean _isFinal = false;
	RequestQueue rq;
	ErrorListener errorListener;
	private int totalResults = 0;
	String nextTokens;
	Handler handle = new Handler();
	// 수집 딜레이 주기
	int delayTime = 60 * 1000;
	boolean isFirst = true;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_isFinal = false;
		_isLoad = false;
		totalResults = 0;
		publishAfter = "1970-01-01T00:00:00Z";
		nextTokens = null;
		
		LogUtil.D("VideoCollectByIdService on start ....");
		Bundle b = intent.getExtras().getBundle("extra");
		
		String uploader_id = b.getString("uploader_id");
		
		if(b.containsKey("publish_date")){
			publishAfter = b.getString("publish_date");
		}

		if(UploaderProgressingList.setUploaderOn(uploader_id)){
			rq = Volley.newRequestQueue(getApplicationContext());
			errorListener = new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					LogUtil.E(error.getLocalizedMessage());
				}
			};
			startCollectProcess(uploader_id);
		}else{
			LogUtil.W("other process collect video list");
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startCollectProcess(String uploader_id){
		collectProcess(uploader_id);
	}
	
	// 
	private void collectProcess(final String uploader_id){
		if(isFirst){
			collectData(uploader_id);
		}else{
			handle.postDelayed(new Runnable() {
				@Override
				public void run() {
					collectData(uploader_id);
				}
			}, delayTime);
		}
	}
	
	private void collectData(final String uploader_id){
		
		if(UploaderProgressingList.uploaders.containsKey(uploader_id)){
			if(UploaderProgressingList.uploaders.get(uploader_id).equals("Y")){
				
				_isLoad = true;
				
				LogUtil.D("남은video 갯수 = " + totalResults);

				Listener<JSONObject> listener = new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						final ArrayList<YTListDto> _data = McUtil.parseSearchListToDataObject(getApplicationContext(), response);
						
						StringBuilder sb = new StringBuilder();
						
						if(_data.size() == 0) {
							_isFinal = true;
						}else{
							
							try {
								JSONObject pageInfo = response.getJSONObject("pageInfo");
								if(totalResults == 0)
								totalResults = pageInfo.getInt("totalResults");
								
								if(response.isNull("nextPageToken")){
									nextTokens = "empty";
									LogUtil.W(response.toString());
								}else{
									nextTokens = response.getString("nextPageToken");
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

										totalResults -= _inputed.size();
										
										// list내용을 sqlite에 저장
										// DB에 넣을떄 한번 더 체크
										if(UploaderProgressingList.uploaders.containsKey(uploader_id)){
											if(UploaderProgressingList.uploaders.get(uploader_id).equals("Y")){
												DbQueryUtil.addUploaderLogTable(getBaseContext(), _inputed);
											}	
											
											if(_isFinal){
												UploaderProgressingList.setUploaderOff(uploader_id);
												LogUtil.D("video collect by id complete!!");
												onDestroy();									
											}
											
											if(totalResults > 0){
												collectProcess(uploader_id);
											}
										}
										
										_isLoad = false;
									}
								}, errorListener);

								rq.add(get_mv_detail);
							}
						}
					}
				};

				Params params = new Params(getApplicationContext());
				params.put("part", "snippet");
				params.put("order", "date");
				params.put("publishedAfter", publishAfter);
				params.put("maxResults", "50");
				params.put("channelId", uploader_id);				
				
				if(nextTokens != null){
					if(!nextTokens.equals("empty")){
						params.put("pageToken", nextTokens);
					}else{
						_isFinal  = true;
					}
					LogUtil.I("video collect by id nextpagetoken = " + nextTokens);
				}
				
				JsonObjectRequest req = new JsonObjectRequest(UrlDef.SEARCH_URL + McUtil.getParams(params) + "&rs=" + totalResults , null, listener, errorListener);

				rq.add(req);
			}else{
				LogUtil.W("video collect by id stopped!!!");
			}
		}
		
		
	}
	
	@Override
	public void onDestroy() {
		LogUtil.I("video collect by id destroyed");
		super.onDestroy();
	}

}
