package com.mcproject.net.service;

import java.util.Timer;
import java.util.TimerTask;

import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.util.LogUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AdLimitService extends Service{

	Timer adSetTimer = new Timer();
	TimerTask adSetTask = null;
	private long delay = 0;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(adSetTask == null){
			adSetTask = new TimerTask() {
				@Override
				public void run() {
//					LogUtil.D("ad reset....");
					AppUserSettings.setIsAdToday(getBaseContext(), "0");	
				}
			};
			adSetTimer.schedule(adSetTask, delay, Define.AD_WALL_VIEW_LIMIT_SERVICE_INTERVAL_TIME);
		}else{
//			LogUtil.D("adSetTask already scheduled");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}	
}
