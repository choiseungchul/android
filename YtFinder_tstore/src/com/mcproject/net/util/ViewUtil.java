package com.mcproject.net.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class ViewUtil {

	static LayoutInflater inflate;
	
	public static View getInflatedView(Context ctx, int resId){
		if(inflate == null){
			inflate = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);			
		}		
		return inflate.inflate(resId, null);
	}
	
}
