package com.returndays.ralara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.returndays.ralara.service.AdSlideService;
import com.returndays.ralara.util.LogUtil;

public class BaseActivity extends Activity {
	//Activity mActivity;
	public static float DENSITY = -1;
	
	public static int instanceCount = 0;
	
	public BaseActivity () {
        super();
        instanceCount++;
        LogUtil.D("test", "BaseActivity() instanceCount: " + instanceCount);
	}	
	
	@Override
    protected void finalize() throws Throwable {
              super.finalize();
              instanceCount--;
              LogUtil.D("test", "BaseActivity finalize() instanceCount: " + instanceCount);
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        if(DENSITY == -1) {
	        DisplayMetrics outMetrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
	        DENSITY = outMetrics.densityDpi;
        }
        if(AdSlideService.isLive == 0){
        	startService(new Intent(this, AdSlideService.class));
        }
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void endApplication() {
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.addCategory(Intent.CATEGORY_HOME);
	    startActivity(intent);
		finish();
//		System.exit(0);
	}
	
	
	public void startSingTopActivity(Class<?> cls) {
		Intent intent = new Intent( getApplicationContext(), cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	
}
