package com.returndays.ralara.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class ContactSendService extends Service{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogUtil.D("on start command...");
		MadUtil.sendContactList(getApplicationContext(), new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				stopSelf();
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
