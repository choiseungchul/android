package com.mcproject.net.util;

import java.util.Hashtable;

import android.content.Context;

import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;

public class Params extends Hashtable<String, String>{

	/**
	 * 
	 */
	Context ctx;
	
	private static final long serialVersionUID = 1L;

	public Params(Context ctx){
		this.ctx = ctx;
		put("key", Define.YT_API_KEY);
		put("maxResults", "10");
		
		if(!AppUserSettings.getSafeSearch(ctx).equals("")){
			put("safeSearch", AppUserSettings.getSafeSearch(ctx));
		}
	}
	
	/**
	 * 파라미터 출력
	 * */
	public void trace() {
		for ( java.util.Map.Entry<String, String> p :  this.entrySet()){
			LogUtil.I(p.getKey() + " : " + p.getValue());
		}
	}
	
}
