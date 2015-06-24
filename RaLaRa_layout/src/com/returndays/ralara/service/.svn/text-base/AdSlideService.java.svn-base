package com.returndays.ralara.service;

import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.returndays.ralara.AdSlideActivity;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.LogUtil;


public class AdSlideService extends Service {

	private KeyguardManager km = null; 
	@SuppressWarnings("deprecation")
	private KeyguardManager.KeyguardLock keylock = null;
	public static int isLive = 0;
	public static boolean screenOn = true;
	
	static AdSlideActivity _this;

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){
		@SuppressLint("InlinedApi")
		@Override
		public void onReceive(Context context, Intent intent) {
			//Toast.makeText(context, "서비스 실행중!", Toast.LENGTH_LONG).show();
			String action = intent.getAction();
			LogUtil.D("ad Slide Receiver Start :  / "+action);
			if(action.equals(Intent.ACTION_SCREEN_OFF)){
				screenOn = false;
			}else{
				screenOn = true;
			}

			TelephonyManager tpMng = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

			if(Setting.getUserSeq(getApplicationContext()).equals("")) return;

			int iCallStatus = tpMng.getCallState();
			//String sCallStatus = null;

			if (Setting.getSlideAd(getApplicationContext()).equals("OFF")) return;
			if (Setting.getSlideAd(getApplicationContext()).equals("")) return;

			// 시간대 별로 지정되있는지 확인
			if(Setting.getUserType(getApplicationContext()).equals("4")){
				String callend_ad_time = Setting.getCallAdPeriod(getBaseContext());
				int st = Integer.parseInt(callend_ad_time.split(":")[0]);
				int ed = Integer.parseInt(callend_ad_time.split(":")[1]);
				Date dt = new Date();
				int h = dt.getHours();
				LogUtil.D("curr h = " + h);
				if(st > h || ed < h){
					return;
				}
			}
			
			int cnt = InOutCallAdQuery.selectAdCnt(getBaseContext());

			LogUtil.D("ddddddddddddddddddddddddddddddddddddd : "+ cnt);
			//proccessKill();
			switch(iCallStatus)
			{
			case TelephonyManager.CALL_STATE_IDLE:
				if(action.equals("android.intent.action.SCREEN_OFF")){
					try{
					
						ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
						List<RunningTaskInfo> Info = am.getRunningTasks(1);
						ComponentName topActivity = Info.get(0).topActivity;
						
						if((topActivity.getPackageName() + ".AdSlideActivity").equals(topActivity.getClassName())){
							// adslide가 최상단에 있을경우
							LogUtil.D("adslide is topActivity");
							if(AdSlideService._this != null){
								AdSlideService._this.finish();
							}else{
								LogUtil.D("AdSlideActivity is NULL");
							}
						}
						if(cnt > 0){
							Intent i = new Intent(context, AdSlideActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
							//Thread.sleep(300);
							context.startActivity(i);
						}

					}catch(Exception e){
						e.printStackTrace();
					}
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//sCallStatus = "통화 중";
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//sCallStatus = "전화벨이 울리고 있음.";
				break;
			default:
				//sCallStatus = "아무 상태 아님.";
				break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//
		//		km=(KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
		//		if(km!=null){
		//			keylock = km.newKeyguardLock("test");
		//			keylock.disableKeyguard();
		//		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){

		if(isLive == 0){
			//Toast.makeText(this, "서비스가 시작되었습니다.", Toast.LENGTH_SHORT).show();

			IntentFilter filter = new IntentFilter("com.returndays.ralara.action.isAlive");
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			registerReceiver(mReceiver, filter);

			isLive = 1;

			return Service.START_REDELIVER_INTENT ;
		}else{
			//Toast.makeText(this, "이미 서비스가 시작되었습니다.", Toast.LENGTH_SHORT).show();
			return 0;
		}
	}

	@Override
	public void onDestroy(){
		if(keylock!=null){
			keylock.reenableKeyguard();
		}

		if(mReceiver != null)
			unregisterReceiver(mReceiver);
		//Toast.makeText(this, "서비스 종료", Toast.LENGTH_LONG).show();
		isLive = 0;
	}
	
//	private void proccessKill() {
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				String nameOfProcess = "com.cashslide";
//				ActivityManager  manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//				List<ActivityManager.RunningAppProcessInfo> listOfProcesses = manager.getRunningAppProcesses();
//				for (ActivityManager.RunningAppProcessInfo process : listOfProcesses) {
//				    if (process.processName.contains(nameOfProcess)) {
//				        LogUtil.D("aaaaaaaaaaaaaaaaaaaaaakilllllllllllllllllllllll : start");
//				    	//manager.restartPackage(process.processName);
//				        android.os.Process.killProcess(process.pid);
//				        manager.killBackgroundProcesses(nameOfProcess);
//				        manager.killBackgroundProcesses(String.valueOf(process.processName));
//				        LogUtil.D("aaaaaaaaaaaaaaaaaaaaaakilllllllllllllllllllllll : end");
//				        break;
//				    }
//				}
//			}
//		}, 500);
//		
//	}

}
