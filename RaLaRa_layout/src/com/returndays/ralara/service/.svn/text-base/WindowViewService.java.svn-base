package com.returndays.ralara.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.returndays.ralara.util.LogUtil;

public class WindowViewService extends Service{

	WindowManager mWindowManager;
	TextView mPopupView;
	WindowManager.LayoutParams mParams;
	private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
	private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
	private int MAX_X = -1, MAX_Y = -1;					//뷰의 위치 최대 값
	
	public WindowViewService(){
		super();
		LogUtil.D("WindowViewService create");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		LogUtil.D("WindowViewService On");
		showWindowView();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {		
		return null;
	}
	
	public void showWindowView(){
		LogUtil.D("showWindowview in Service");
		
		try{
			mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
			mPopupView = new TextView(this);	
			mParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,					//항상 최 상위에 있게. status bar 밑에 있음. 터치 이벤트 받을 수 있음.
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,		//이 속성을 안주면 터치 & 키 이벤트도 먹게 된다. 
					//포커스를 안줘서 자기 영역 밖터치는 인식 안하고 키이벤트를 사용하지 않게 설정
					PixelFormat.TRANSLUCENT);										//투명
			mParams.gravity = Gravity.LEFT | Gravity.TOP;					

			mPopupView.setText("이 뷰는 항상 위에 있다.\n갤럭시 & 옵티머스 팝업 뷰와 같음");	//텍스트 설정
			mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);								//텍스트 크기 18sp
			mPopupView.setTextColor(Color.BLUE);															//글자 색상
			mPopupView.setBackgroundColor(Color.argb(127, 0, 255, 255));								//텍스트뷰 배경 색

			// 리스너 등록
			mPopupView.setOnTouchListener( viewOnTouchListener );
			mPopupView.setOnLongClickListener(viewOnLongClick);

			mWindowManager.addView(mPopupView, mParams);		//최상위 윈도우에 뷰 넣기. *중요 : 여기에 permission을 미리 설정해 두어야 한다. 매니페스트에
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private OnLongClickListener viewOnLongClick = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			
			return false;
		}
	};
	
	private OnTouchListener viewOnTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
					if(MAX_X == -1){
						DisplayMetrics matrix = new DisplayMetrics();
						mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서
	
						MAX_X = matrix.widthPixels - mPopupView.getWidth();			//x 최대값 설정
						MAX_Y = matrix.heightPixels - mPopupView.getHeight();			//y 최대값 설정
					}
					START_X = event.getRawX();                    //터치 시작 점
					START_Y = event.getRawY();                    //터치 시작 점
					PREV_X = mParams.x;                            //뷰의 시작 점
					PREV_Y = mParams.y;                            //뷰의 시작 점
					break;
				case MotionEvent.ACTION_MOVE:
					int x = (int)(event.getRawX() - START_X);    //이동한 거리
					int y = (int)(event.getRawY() - START_Y);    //이동한 거리
	
					//터치해서 이동한 만큼 이동 시킨다
					mParams.x = PREV_X + x;
					mParams.y = PREV_Y + y;
	
					//뷰의 위치 최적화
					if(mParams.x > MAX_X + 100 || mParams.y > MAX_Y + 100) {
						if(mWindowManager != null){
							mWindowManager.removeView(mPopupView);
						}
					}else{
						mWindowManager.updateViewLayout(mPopupView, mParams);    //뷰 업데이트
					}
	
					break;
			}

			return true;
		}
	};
	
	@Override
	public void onDestroy() {
		LogUtil.D("WindowViewService Destroy");
		super.onDestroy();
	}

}
