package com.returndays.ralara;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.returndays.ralara.util.LogUtil;

public class TalkPopupActivity extends BaseActivity{

	TextView popup_title, popup_msg;
	Button popup_go, popup_dismiss;
	String room_seq, title;
	static TalkPopupActivity _this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtil.D("TalkPopupActivity onCreate");
		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> Info = am.getRunningTasks(1);
		ComponentName topActivity = Info.get(0).topActivity;
		
		if((topActivity.getPackageName() + ".TalkPopupActivity").equals(topActivity.getClassName())){
			// adslide가 최상단에 있을경우
			LogUtil.D("adslide is topActivity");
			if(TalkPopupActivity._this != null){
				TalkPopupActivity._this.finish();
			}else{
				LogUtil.D("AdSlideActivity is NULL");
			}
		}
		super.onCreate(savedInstanceState);

		//acquireWakeLock(getApplicationContext());
		// 이 부분이 바로 화면을 깨우는 부분 되시겠다.
        // 화면이 잠겨있을 때 보여주기
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        // 키잠금 해제하기
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        // 화면 켜기
        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    

		setContentView(R.layout.talk_popup);
		
		initUI();

		room_seq = getIntent().getExtras().get("room_seq").toString();
		title = getIntent().getExtras().get("title").toString();

		popup_title.setText("랄라라 알림");
		popup_msg.setText(title);

		_this = this;
	}

	private void initUI() {

		popup_title = (TextView)findViewById(R.id.popup_title);
		popup_msg = (TextView)findViewById(R.id.popup_msg);
		popup_go = (Button)findViewById(R.id.popup_go);
		popup_dismiss = (Button)findViewById(R.id.popup_dismiss);
		
		popup_go.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// 노티바 제거
				removeNoti();
				Intent ii = new Intent(getApplicationContext(), TalkInActivity.class);
				ii.putExtra("room_seq", room_seq);
				startActivity(ii);
				finish();
			}
		});
		popup_dismiss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//releaseWakeLock();
				removeNoti();
				finish();
			}
		});
	}

	WakeLock screenWakeLock;

	private void removeNoti(){
		NotificationManager nMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nMgr.cancel(GCMIntentService.NOTIFICATION_ID);
	}
	
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

	@Override
	public void onBackPressed() {
		
		releaseWakeLock();
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		LogUtil.D("TalkPopupActivity onStart");
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		LogUtil.D("TalkPopupActivity onPause");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LogUtil.D("TalkPopupActivity onStop");
		super.onStop();
		//finish();
	}

}
