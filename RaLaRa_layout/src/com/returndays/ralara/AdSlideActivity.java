package com.returndays.ralara;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.MadUtil;

public class AdSlideActivity  extends BaseActivity  {

	RelativeLayout mCount, mSeek;
	static AdSlideActivity _this;
	HttpDocument mHttpUtil;
	boolean isTouch = false;

	String strIndex;
	CallAdDto mDto;
	ArrayList<CallAdDto> ads ;

	TextView txt_egg , txt_gold, txt_scratch;
	Bitmap bm1;
	
	RecyclingImageView mSlideImg;

	HashMap<String, String> mDatamap;

	public static AdSlideActivity getInstance(){
		return _this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_this = this;

		setContentView(R.layout.ad_slide);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		TextView day_time 	= (TextView) findViewById(R.id.day_time);
		SeekBar mUnlockBar 	= (SeekBar) findViewById(R.id.seekBar2);

		txt_egg 			= (TextView) findViewById(R.id.txt_egg);
		txt_gold = (TextView) findViewById(R.id.txt_gold);
		txt_scratch = (TextView) findViewById(R.id.txt_scratch);

		RelativeLayout mMain_frame = (RelativeLayout) findViewById(R.id.main_frame);
		LinearLayout mTxt_err_msg = (LinearLayout) findViewById(R.id.txt_err_msg);
		ImageView mIndicator = (ImageView) findViewById(R.id.img_indi01);
		mSlideImg = (RecyclingImageView) findViewById(R.id.img_slide_back); 

		Calendar c = Calendar.getInstance();
		int Month = c.get(Calendar.MONTH)+1; 
		int Day = c.get(Calendar.DAY_OF_MONTH);
		int DayOfWeek = c.get(Calendar.DAY_OF_WEEK); // 일요일(1), 월요일(2), ..., 토요일(7)

		String stringDayOfWeek[] = { "", "일", "월", "화", "수", "목", "금", "토" }; 
		String stringDayAndTimeFormat = String.format("%d월 %d일 " + stringDayOfWeek[DayOfWeek] + "요일 " , Month, Day);
		day_time.setText(stringDayAndTimeFormat);

		//세팅값에서 광고 인덱스 가져오기
		strIndex = com.returndays.ralara.preference.Setting.getIndexLockAd(getBaseContext());
		if(strIndex.equals("")) strIndex = "-1";

		//sqlite 에서 광고 가져오기
		ads = InOutCallAdQuery.selectLockAdList(getBaseContext());
		if(ads.size() < 1) {

			mTxt_err_msg.setVisibility(View.VISIBLE);

			mUnlockBar.setThumbOffset(50);
			mUnlockBar.setMax(100);
			mUnlockBar.setProgress(50);

			mCount = (RelativeLayout)findViewById(R.id.dim_frame);
			mSeek = (RelativeLayout)findViewById(R.id.seekbar_frame);

			RotateAnimation anim;
			anim = new RotateAnimation(0,359,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(1500);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatCount(-1);

			mIndicator.startAnimation(anim);
			mUnlockBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {

					//mUnlockBar.setThumb(getResources().getDrawable(R.drawable.btn_circle_on));

					if (seekBar.getProgress() >= 90  ) {

						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
						vibrator.vibrate(20);
						seekBar.setProgress(90);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);
						AdSlideActivity.super.finish();

					}else if ( seekBar.getProgress() <= 10 ){

						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
						vibrator.vibrate(20);
						seekBar.setProgress(10);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);
						AdSlideActivity.super.finish();
					}

				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					isTouch = true;
					mCount.setVisibility(View.VISIBLE);
					mSeek.setVisibility(View.VISIBLE);

					mHttpUtil = new HttpDocument(getApplicationContext());
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));
					mHttpUtil.getDocument(UrlDef.MY_POINT, params,null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							// TODO Auto-generated method stub
							String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
							String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
							String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

							txt_egg.setText(Egg);
							txt_gold.setText(Gold);
							txt_scratch.setText(Scratch);
						}

					}, false);


				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

					isTouch = false;
					mCount.setVisibility(View.INVISIBLE);
					mSeek.setVisibility(View.VISIBLE);

					if (seekBar.getProgress() >= 95  ) {
						seekBar.setProgress(100);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);

						//Intent intent = new Intent(getApplicationContext(), ScratchViewActivity.class);
						finish();
						return;

					}else if ( seekBar.getProgress() <= 25 ){
						seekBar.setProgress(0);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);
						//Toast.makeText(MainActivity.this, "Unlock On", Toast.LENGTH_SHORT).show();
						finish();
						return;
					}
					else {
						seekBar.setProgress(50);

					}
				}
			});

		}else{
			/**
			 * 서버에 광고가 있을 경우!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * 
			 */
			int index = Integer.parseInt(strIndex)+1;
			if(index > ads.size()-1) index = 0;
			com.returndays.ralara.preference.Setting.setIndexLockAd(getBaseContext(), String.valueOf(index));
			mDto = ads.get(index);

			mDatamap = new HashMap<String, String>();

			mDatamap.put("CAMP_IDX", mDto.CAMP_IDX);
			mDatamap.put("AD_SEQ", mDto.AD_SEQ);
			mDatamap.put("AD_KIND", mDto.AD_KIND);
			mDatamap.put("START_TIME", mDto.START_TIME);
			mDatamap.put("END_TIME", mDto.END_TIME);
			mDatamap.put("IMG1", mDto.IMG1);
			mDatamap.put("END_ACTION", mDto.END_ACTION);
			mDatamap.put("AD_TITLE", mDto.AD_TITLE);
			mDatamap.put("AD_DESC", mDto.AD_DESC);
			mDatamap.put("AD_MOVIE", mDto.AD_MOVIE);


			try{
				mHttpUtil = new HttpDocument(getApplicationContext());
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));
				params.put("ad_seq", mDatamap.get("AD_SEQ"));
				mHttpUtil.getDocument(UrlDef.AD_VIEW_START, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						// TODO Auto-generated method stub
						//LogUtil.W(document.toString());

						Elements entry = document.select("ReturnTable");
						String results = MadUtil.priceFormat(entry.select("Result").text());

					}
				}, false);
			}catch(Exception e){
				e.printStackTrace();
			}


			try{

				AQuery aq = new AQuery(getApplicationContext());
				aq.id(mSlideImg).image(mDto.IMG1);
				
				/*
				String ImagePath = Define.AD_CALL_FOLDER+"/"+ mDto.IMG1.substring(mDto.IMG1.lastIndexOf("/")+1);
				
				File folder = new File(Define.AD_CALL_FOLDER);
								
				
				File f = new File(folder, "/"+ mDto.IMG1.substring(mDto.IMG1.lastIndexOf("/")+1));
				if(!f.exists()) {
					AQuery aq = new AQuery(getApplicationContext());
					aq.id(mMain_frame).image(mDto.IMG1);	
					
				}
				
				
				bm1 = loadBackgroundBitmap(AdSlideActivity.this, ImagePath);
				//BitmapFactory.decodeFile(ImagePath,options);
				@SuppressWarnings("deprecation")
				Drawable d = new BitmapDrawable(bm1);

				int sdk = android.os.Build.VERSION.SDK_INT;
				if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
					mMain_frame.setBackgroundDrawable(d);
				} else {
					mMain_frame.setBackground(d);
				}
				*/
				
			}catch(Exception e){
				e.printStackTrace();
				finish();
			}

			mUnlockBar.setThumbOffset(50);
			mUnlockBar.setMax(100);
			mUnlockBar.setProgress(50);

			mCount = (RelativeLayout)findViewById(R.id.dim_frame);
			mSeek = (RelativeLayout)findViewById(R.id.seekbar_frame);

			mUnlockBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {

					//mUnlockBar.setThumb(getResources().getDrawable(R.drawable.btn_circle_on));
					if (seekBar.getProgress() >= 90  ) {
						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
						vibrator.vibrate(20);
						seekBar.setProgress(90);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);


						Intent intent = new Intent(AdSlideActivity.this, AdAlertActivity.class);
						intent.putExtra("data", mDatamap);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

						finish();


					}else if ( seekBar.getProgress() <= 10 ){

						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
						vibrator.vibrate(20);


						seekBar.setProgress(10);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);

						AdSlideActivity.super.finish();
					}


				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					isTouch = true;
					mCount.setVisibility(View.VISIBLE);
					mSeek.setVisibility(View.VISIBLE);

					mHttpUtil = new HttpDocument(getApplicationContext());
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));
					mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {

						@Override
						public void onHttpCallBackListener(Document document) {
							// TODO Auto-generated method stub

							Elements entry = document.select("ReturnTable");
							String Egg = MadUtil.priceFormat(entry.select("EGG").text())+" 개";
							String Gold = MadUtil.priceFormat(entry.select("GOLD").text())+" 골드";
							String Scratch = MadUtil.priceFormat(entry.select("SCRATCH").text())+" 장";

							txt_egg.setText(Egg);
							txt_gold.setText(Gold);
							txt_scratch.setText(Scratch);

						}
					}, false);


				}


				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {

					isTouch = false;
					mCount.setVisibility(View.INVISIBLE);
					mSeek.setVisibility(View.VISIBLE);

					if (seekBar.getProgress() >= 95  ) {
						seekBar.setProgress(100);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);

					}else if ( seekBar.getProgress() <= 25 ){
						seekBar.setProgress(0);
						seekBar.setEnabled(false);
						seekBar.setFocusable(false);
					}
					else {
						seekBar.setProgress(50);
					}
				}
			});
		}
	}


	@Override
	public void onStop(){
		super.onStop();
	}

	@Override
	public void onPause(){
		super.onPause();
		
	}

	@Override
	protected void onDestroy() {
		mHttpUtil.threadStop();
		super.onDestroy();
		if(bm1 != null){
			bm1.recycle();	
		}
		
	}

	private static void recycleBitmap(ImageView iv) {
		Drawable d = iv.getDrawable();
		if (d instanceof BitmapDrawable) {
			Bitmap b = ((BitmapDrawable)d).getBitmap();
			b.recycle();
		} 

		d.setCallback(null);
	}

	public static Bitmap loadBackgroundBitmap(Context context,String imgFilePath) throws Exception, OutOfMemoryError { 

		// 폰의 화면 사이즈를 구한다.
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		int displayWidth = display.getWidth();
		int displayHeight = display.getHeight();


		// 읽어들일 이미지의 사이즈를 구한다.
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.RGB_565;
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgFilePath, options);


		float widthScale = options.outWidth / displayWidth;
		float heightScale = options.outHeight / displayHeight;
		float scale = widthScale > heightScale ? widthScale : heightScale;

		//LogUtil.D("scaleeeeeeeeeeeeeeeeeeeeeee : "+scale);

		if(scale >= 8) {
			options.inSampleSize = 8;

		} else if(scale >= 6) {
			options.inSampleSize = 6;
		} else if(scale >= 4) {
			options.inSampleSize = 4;
		} else if(scale >= 2) {
			options.inSampleSize = 2;
		} else {
			options.inSampleSize = 2;
		}

		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(imgFilePath, options);

	}


	public void onBackPressed() {

		finish();
		AdSlideActivity.super.onBackPressed();
	}


}
