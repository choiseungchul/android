package com.mcproject.net.service;

import org.json.JSONArray;
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
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Params;
import com.mcproject.ytfavorite_t.R;

public class CategorySyncService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// 카테고리 저장 서비스
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		LogUtil.I("Category Sync Start........");
		
		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
		Listener<JSONObject> listener = new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					JSONArray items = response.getJSONArray("items");
					final String[] ids = new String[items.length()+1];
					final String[] titles = new String[items.length()+1];
					ids[0] = "-1";
					titles[0] = getString(R.string.srch_select_cate_btn);
					for(int i = 0 ; i < items.length(); i++){
						JSONObject item = items.getJSONObject(i);
						ids[i+1] = item.getString("id");
						JSONObject snippet = item.getJSONObject("snippet");
						titles[i+1] = snippet.getString("title");
					}
					// DB에 저장한다.
					DbQueryUtil.initVideoCategory(getApplicationContext(), ids, titles);
					
					LogUtil.D("Category Sync Success !!");
					
				} catch (JSONException e) {
					LogUtil.D("Category Sync Error");
					LogUtil.D(e.toString());
				}
			}
		};
		
		Params get_cate_param = new Params(getApplicationContext());
		get_cate_param.put("key", Define.YT_API_KEY);
		get_cate_param.put("part", "snippet");
		get_cate_param.put("hl", AppUserSettings.getHLCode(getApplicationContext()));
		get_cate_param.put("regionCode", AppUserSettings.getNationCode(getApplicationContext()));
		get_cate_param.put("fields", "items");
		String param = McUtil.getParams(get_cate_param);
		
//		get_cate_param.trace();
		
		rq.add(new JsonObjectRequest(UrlDef.GET_CATEGORY + param, null, listener, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.D(error.toString());
			}
		}));
		
		return super.onStartCommand(intent, flags, startId);
	}
	
}
