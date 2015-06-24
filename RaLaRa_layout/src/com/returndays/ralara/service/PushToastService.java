package com.returndays.ralara.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.ralara.R;
import com.returndays.ralara.util.LogUtil;

// 다이어로그 띄움
public class PushToastService extends Service{
	
	public PushToastService(){
		super();
		LogUtil.D("PushDialogService create");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogUtil.D("PushDialogService onStartCommand");
		Bundle b = intent.getExtras();
		String title = b.getString("title");
		String content = b.getString("content");
		ToastAll(getApplicationContext(), title, content );
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	WakeLock screenWakeLock;

	private void acquireWakeLock(Context context) {
		PowerManager pm = (PowerManager)context.getSystemService
				(Context.POWER_SERVICE);
		screenWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				, context.getClass().getName());

		if (screenWakeLock != null) {
			screenWakeLock.acquire();
		}
	}

	private void releaseWakeLock() {
		if (screenWakeLock != null) {
			screenWakeLock.release();
			screenWakeLock = null;
		}
	}
	
	boolean flag = false;
	public void ToastAll(Context context, String title, String content) {

		// 화면 켜기
		// 이 부분이 바로 화면을 깨우는 부분 되시겠다.
        // 화면이 잠겨있을 때 보여주기
		
		acquireWakeLock(context);
		
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//        // 키잠금 해제하기
//        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//        // 화면 켜기
//        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_toast, null);
		ImageView image = (ImageView) layout.findViewById(R.id.push_image);
		TextView titleView = (TextView) layout.findViewById(R.id.push_title);
		TextView contentView = (TextView)layout.findViewById(R.id.push_content);
		titleView.setText(title);
		contentView.setText(content);
		
		Toast mToast = new Toast(context.getApplicationContext());
		
		mToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		mToast.setDuration(Toast.LENGTH_LONG);
		mToast.setView(layout);         
		if(flag == false){
			flag = true;
			mToast.show();
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					flag = false;
					onDestroy();
				}
			}, 2000);//토스트 켜져있는 시간동안 핸들러 지연 대충 숏이 2초 조금 넘는거 같다.
		} else{
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}

	
	@Override
	public void onDestroy() {
		LogUtil.D("PushDialogService Destroy");
		super.onDestroy();
	}

}
