package com.mcproject.net.util;

import android.content.Context;
import android.content.Intent;


public class ShareUtil {

	public static void share_click(Context ctx, String mTitle, String mMessage){	 		
	    Intent sendIntent = new Intent();
	    sendIntent.setAction(Intent.ACTION_SEND);
	    sendIntent.putExtra(Intent.EXTRA_TEXT, mMessage);
	    sendIntent.setType("text/plain");
	    
	    ctx.startActivity(Intent.createChooser(sendIntent, mTitle));
	}	
}
