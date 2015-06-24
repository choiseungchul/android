package com.returndays.ralara.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.returndays.ralara.conf.Define;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.service.AdDownloadService;
import com.returndays.ralara.service.AdSlideService;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		MadUtil.startAlarmService(context, AdDownloadService.class, Define.AD_DOWN_SERVICE_ID, Define.AD_DOWN_INTERVAL_TIME, 10);
		//MadUtil.startAlarmService(context, AdFeedBackFailService.class, Define.AD_FAIL_SERVICE_ID, Define.AD_FAIL_INTERVAL_TIME, 15);
		//if(AdSlideService.isLive == 0 ){
		MadUtil.startAlarmService(context, AdSlideService.class, Define.AD_SLIDE_SERVICE_ID, Define.AD_SLIDE_INTERVAL_TIME, 10);
		
	}

}
