package com.mcproject.net.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mcproject.net.conf.Define;
import com.mcproject.net.service.AdLimitService;
import com.mcproject.net.service.CategorySyncService;
import com.mcproject.net.service.ClientNotificaionService;
import com.mcproject.net.service.UploaderVideoCollectService;
import com.mcproject.net.util.McUtil;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		McUtil.startAlarmService(context, CategorySyncService.class, Define.CATEGORY_INIT_SERVICE_ID, Define.CATEGORY_INIT_SERVICE_INTERVAL_TIME, 10);
		McUtil.startAlarmService(context, UploaderVideoCollectService.class, Define.UPLOADER_VIDEO_INIT_SERVICE_ID, Define.UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME, 10);
		McUtil.startAlarmService(context, ClientNotificaionService.class, Define.UPLOADER_VIDEO_INIT_SERVICE_ID, Define.UPLOADER_VIDEO_INIT_SERVICE_INTERVAL_TIME, 120);
		McUtil.startAlarmService(context, AdLimitService.class, Define.AD_WALL_VIEW_LIMIT_SERVICE_ID, Define.AD_WALL_VIEW_LIMIT_SERVICE_INTERVAL_TIME, 100);	
	}

}
