package com.returndays.ralara.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.returndays.ralara.CallEndActivity;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.dto.CallAdCategoryDto;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.CallOutListener;
import com.returndays.ralara.util.CallOutListener.ConnectListener;
import com.returndays.ralara.util.LogUtil;

public class callOutService extends Service {
	WindowManager mWm;
	ImageView mImageView1;
	TextView mInfoTextView;
	Timer mRemoveCheckTimer;

	int mIncrease = 0;
	boolean mViewPointToggle = false;
	Display mDisplay;
	WindowManager.LayoutParams wmParams;
	View mViewContainer;
	boolean mIncomming;
	CallAdDto mDto;
	String name, number;
	int adIndex = 0;

	String mCAMP_IDX;
	String mAD_SEQ;
	String mAD_KIND;
	String mSTART_TIME;
	String mEND_TIME;
	String mIMG1;
	String mEND_ACTION;
	String mAD_TITLE;
	boolean isClickAd = true;

	Bundle isView = new Bundle() ;


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 만약 뷰가 생성된지 30초 후에도 
	 * 핸드폰의 컨디션이 나빠(구린폰들) 뷰를 제거하지 못했을경우 
	 * 강제로 제거한다.
	 */
	private void removeViewTimerStart() {
		mRemoveCheckTimer = new Timer();
		mRemoveCheckTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mRemoveViewHandler.sendEmptyMessage(0);
			}
		}, 1000 * 2);
	}

	private void timerStop() {

		if(mRemoveCheckTimer != null) {
			try {
				mRemoveCheckTimer.cancel();
				mRemoveCheckTimer = null;
			} catch (Exception e) {
			}
		}
	}


	Handler mRemoveViewHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			removeView();
			timerStop();
		}
	};
	private TextView mPopupView;
	private android.view.WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;
	private String ad_seq_flag_y;

	@Override
	public void onCreate() {
		super.onCreate();

	}


	private boolean initUI() {

		ArrayList<CallAdDto> ads = InOutCallAdQuery.selectInOutAdList(getBaseContext());
		if(ads.size() < 1) {
			return false;
		}
		
		ArrayList<CallAdCategoryDto> list = InOutCallAdQuery.selectInOutAdFlagList(getBaseContext(), 1);

		Random r = new Random();
		if (list.size() == 0){
			return false;
		}
		int idx = r.nextInt(list.size());
		CallAdCategoryDto dto = list.get(idx);

		ad_seq_flag_y = dto.ad_seq;

		mDto = InOutCallAdQuery.selectInOutAdViewOne(getBaseContext(), dto.ad_seq);


		mCAMP_IDX = mDto.CAMP_IDX;
		mAD_SEQ = mDto.AD_SEQ;
		mAD_KIND = mDto.AD_KIND;
		mSTART_TIME = mDto.START_TIME;
		mEND_TIME = mDto.END_TIME;
		mIMG1 = mDto.IMG1;
		mEND_ACTION = mDto.END_ACTION;
		mAD_TITLE = mDto.AD_TITLE;

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mViewContainer = inflater.inflate(R.layout.image_layout, null);
		mImageView1 = (ImageView) mViewContainer.findViewById(R.id.image_view);

		mInfoTextView = (TextView) mViewContainer.findViewById(R.id.user_info);

		if(!name.equals(""))
			mInfoTextView.setText(name +  "  " + PhoneNumberUtils.formatNumber(number)  );
		else 
			mInfoTextView.setText(PhoneNumberUtils.formatNumber(number));

		mImageView1.setVisibility(View.VISIBLE);
		mViewContainer.setVisibility(View.VISIBLE);
		LogUtil.I(mDto.IMG1);

		Bitmap bm1 = BitmapFactory.decodeFile(Define.AD_CALL_FOLDER+"/"+ mDto.IMG1.substring(mDto.IMG1.lastIndexOf("/")+1));

		mImageView1.setImageBitmap(bm1);



		removeViewTimerStart();
		return true;
	}

	private void addDeviceWindow() {

		isView.putBoolean("isview", true);

		String position = Setting.getAdPosition(getBaseContext());
		mWm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		//removeView();

		mDisplay = mWm.getDefaultDisplay();

		wmParams = new WindowManager.LayoutParams();
		wmParams.alpha = 1.0f;
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY |WindowManager.LayoutParams.TYPE_SYSTEM_ALERT ; 
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
		wmParams.width =  LayoutParams.MATCH_PARENT;  
		wmParams.format = PixelFormat.TRANSLUCENT;
		final Display display = mWm.getDefaultDisplay();

		float height;
		if(display.getHeight() > display.getWidth())
			height = display.getHeight()/3.0f;
		else 
			height = display.getWidth()/3.0f;

		wmParams.height = (int) height;
		wmParams.gravity = position.equals("BOTTOM") ? Gravity.BOTTOM : Gravity.TOP;
		mWm.addView(mViewContainer, wmParams);

		mViewContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				removeView();
				isClickAd = false;

			}
		});
	}



	private void removeView() {
		try {
			mWm.removeView(mViewContainer);

			isView.putBoolean("isview", false);
			LogUtil.D("33. 뷰삭제정상 ");

		} catch (Exception e) {
			LogUtil.D("33. 뷰삭제 에러 :"+e.toString());
		}
	}

	Thread mCallOutThread;
	CallOutListener mRunnable;

	void outCallThreadStop() {
		try {
			if(mRunnable != null) {
				//mRunnable.Stop();
				mRunnable = null;
				LogUtil.W("44. 콜 스래드 (Runnable) 실행중  ");
			}
			if(mCallOutThread != null && mCallOutThread.isAlive()) {
				mCallOutThread.interrupt();
				mCallOutThread = null;
				LogUtil.W("44. 콜 스래드 (outThread 살아있음) ");
			}
		} catch (Exception e) {
			LogUtil.D("44. 콜스래드 멈춤 에러 :"+e.toString());
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {

		if(intent == null) return;
		mViewPointToggle = false;
		
		Bundle bundle = intent.getExtras();
		boolean isOn = bundle.getBoolean("isOn");
		if(isOn) {

			LogUtil.W("55. isOn 서비스 시작함...");

			String inout = bundle.getString("inout");
			number = bundle.getString("number");
			name =  bundle.getString("name");
			mIncomming = inout.equals("I");
			if(!initUI()) {
				return;
			}
			if(!isView.getBoolean("isview"))
				addDeviceWindow();

			if(!mIncomming) {
				mRunnable = new CallOutListener(new ConnectListener() {
					@Override
					public void onConnected() {
						
						removeView();
					}
				});
				mCallOutThread = new Thread(mRunnable);
				mCallOutThread.start();
			}


		} else {
			timerStop();
			removeView();
			if(mIncomming && bundle.getString("type").equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				//feedBack(getBaseContext(), mDto.CAMP_IDX);
				//Toast.makeText(this, "테스트 중", Toast.LENGTH_SHORT).show();


				/*Intent i = new Intent(this,  WebViewActivity.class);
				i.putExtra("url", UrlDef.AD_NAVER);
				PendingIntent p = PendingIntent.getActivity(this, 0, i, 0);
				try {
					p.send();

				} catch (CanceledException e) {
					e.printStackTrace();
				}*/
				LogUtil.W("55. 통화중 임.. ");	
			}

			if( bundle.getString("type").equals(TelephonyManager.EXTRA_STATE_IDLE)) {

				ArrayList<CallAdDto> ads = InOutCallAdQuery.selectInOutAdList(getBaseContext());
				if(ads.size() < 1) {

				}else{					
					//					int idx = 0;
					//					
					//					ArrayList<CallAdCategoryDto> list = InOutCallAdQuery.selectInOutAdFlagList(getBaseContext(), 1);
					//					
					//					Random r = new Random();
					//					idx = r.nextInt(list.size());

					LogUtil.W("55. 전화 끊음..   : call_idx  : " + ad_seq_flag_y);
					if(ad_seq_flag_y != null){
						mDto = InOutCallAdQuery.selectInOutAdViewOne(getBaseContext(), ad_seq_flag_y);
						if(isClickAd){	
							callEndProcess(mDto.END_ACTION, mDto.AD_SEQ, mDto.CAMP_IDX);
						}else{
							isClickAd = true;
						}
					
					}

				}


			}
			outCallThreadStop();
		}
		super.onStart(intent, startId);
	}
	
	// 광고 보기 설정 구분
	private boolean checkCallendProcess(){
		String mode = Setting.getUserType(getBaseContext());
		LogUtil.D("광고 모드 = " + mode);
		if(mode.equals("2") || mode.equals("3")){
			return true;
		}else if(mode.equals("4")){
			String callend_ad_time = Setting.getCallAdPeriod(getBaseContext());
			int st = Integer.parseInt(callend_ad_time.split(":")[0]);
			int ed = Integer.parseInt(callend_ad_time.split(":")[1]);
			Date dt = new Date();
			int h = dt.getHours();
			LogUtil.D("curr h = " + h);
			if(st <= h && ed >= h){
				return true;
			}else{
				return false;
			}
		}else{
			// 설정이 안되어있을 경우
			return true;
		}
	}
	
	private void callEndProcess(String urls , String ad_seq , String idx){
	
		if(checkCallendProcess()){
			Intent i = new Intent(this,  CallEndActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("url", urls);
			i.putExtra("ad_seq", ad_seq);
			i.putExtra("index", idx);
			//Toast.makeText(callOutService.this, "index : "+ idx, Toast.LENGTH_SHORT).show();
			
			Setting.setCallEndAdSettingURL( callOutService.this, urls);
			Setting.setCallEndAdSettingADSEQ( callOutService.this, ad_seq);
			Setting.setCallEndAdSettingIDX( callOutService.this, idx);
					
			PendingIntent p = PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
			try {
				p.send();
			} catch (CanceledException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try{
			outCallThreadStop();		
		}catch(Exception e){

		}

		super.onDestroy();
	}
}