package com.returndays.ralara;

import java.util.Hashtable;
import java.util.Random;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.winsontan520.WScratchView;
import com.winsontan520.WScratchView.OnScratchEndListner;

public class ScratchCardNonAdActivity extends BaseActivity {

	TextView txt_egg;
	TextView txt_gold;
	TextView txt_scratch;

	HttpDocument mHttpUtil, mHttpUtil2, mHttpUtil3;
	String ad_seq;

	ProgressBar mPbar, mSbar;
	private WScratchView scratchView;
	RecyclingImageView mBackgroundView; 

	private String mScrath_seq;
	
	private final static int  cardimage[] = { R.drawable.card1 , R.drawable.card2 , R.drawable.card3 , 
		R.drawable.card4 , R.drawable.card5 , R.drawable.card6 , R.drawable.card7 , R.drawable.card8 }; 
	private int rdcard;
	
	
	private final static int  winimage[] = {
		R.drawable.win01,R.drawable.win02, R.drawable.win03, R.drawable.win04, R.drawable.win05, R.drawable.win06, R.drawable.win07, 
		R.drawable.win08
	}; 
	private int rdwin;

	private final static int  goldimage[] = {
		R.drawable.gold_01,R.drawable.gold_02, R.drawable.gold_03, R.drawable.gold_04, R.drawable.gold_05, R.drawable.gold_06, R.drawable.gold_07, 
		R.drawable.gold_08
	}; 
	private int rdgold;

	
	private final static int  failimage[] = {
		R.drawable.fail_01 ,R.drawable.fail_02, R.drawable.fail_03, R.drawable.fail_04, R.drawable.fail_05, R.drawable.fail_06, R.drawable.fail_07, 
		R.drawable.fail_08
	};
	private int rdfail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch);

		Bundle b = getIntent().getExtras();
		//ad_seq = (String) b.get("ad_seq");
		mScrath_seq = (String) b.get("scratch_seq");

		mPbar = (ProgressBar) findViewById(R.id.prog_scratch);
		mSbar = (ProgressBar) findViewById(R.id.prog_bar_scratch);

		scratchView = (WScratchView) findViewById(R.id.scratch_view);
		mBackgroundView = (RecyclingImageView) findViewById(R.id.scratch_backimg);

		mHttpUtil = new HttpDocument(this);
		mHttpUtil2 = new HttpDocument(this);
		mHttpUtil3 = new HttpDocument(this);


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


		txt_egg = (TextView) findViewById(R.id.top_r_cnt2);
		txt_gold = (TextView) findViewById(R.id.top_m_cnt2);
		txt_scratch = (TextView) findViewById(R.id.top_s_cnt2);

		final String user_seq = com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext());
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", user_seq );
		mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				if(document.select("ResultTable").select("Result").text().equals("true")){
					Elements entry = document.select("ReturnTable");
					String Egg = MadUtil.priceFormat(entry.select("EGG").text())+" 개";
					String Gold = MadUtil.priceFormat(entry.select("GOLD").text())+" 골드";
					String Scratch = MadUtil.priceFormat(entry.select("SCRATCH").text())+" 장";

					txt_egg.setText(Egg);
					txt_gold.setText(Gold);
					txt_scratch.setText(Scratch);
				}

			}
		}, false);



		Random mRdcard = new Random();
		rdcard = mRdcard.nextInt(7);
		
		Random mWdcard = new Random();
		rdwin = mWdcard.nextInt(7);
		
		Random mFdcard = new Random();
		rdfail = mFdcard.nextInt(7);

		Random mGdcard = new Random();
		rdgold = mGdcard.nextInt(7);


		Hashtable<String, String> params2 = new Hashtable<String, String>();
		params2.put("user_seq", user_seq);
		params2.put("scratch_seq", mScrath_seq);

		mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW_NON3, params2, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				mPbar.setVisibility(View.GONE);

				String code = document.select("ReturnTable").select("Code").text();
				
				if(code.equals("1")){
					scratchView.setOverlayImage(cardimage[rdcard]);
					mBackgroundView.setBackgroundResource(winimage[rdwin]);

					scratchView.setScratchEndListner(new OnScratchEndListner() {

						@Override
						public void scratchEndListner(int scratchCnt) {
						
							mSbar.setProgress(scratchCnt);
							if(scratchCnt == 170){
								scratchView.setOverlayImage(winimage[rdwin]);

								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("scratch_seq", mScrath_seq);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NON4, params3, null, new HttpCallBack() {

									@Override
									public void onHttpCallBackListener(Document document) {
										String rtn = "";
										rtn = document.select("ReturnTable").select("Code").text();
										if(rtn.equals("0")){

											Intent intent = new Intent(ScratchCardNonAdActivity.this, ScratchResultActivity.class);
											intent.putExtra("scratch_seq", mScrath_seq);
											intent.putExtra("ad_seq", "");
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

											((Activity)ScratchCardNonAdActivity.this).startActivity(intent);
											((Activity)ScratchCardNonAdActivity.this).overridePendingTransition(R.anim.fade, R.anim.hold); 
											finish();
										}else{
											Toast.makeText(ScratchCardNonAdActivity.this, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
											finish();
										}

									}
								},false);

							}
						}
					});

				}else if(code.equals("7")){
					scratchView.setOverlayImage(cardimage[rdcard]);
					mBackgroundView.setBackgroundResource(failimage[rdfail]);

					scratchView.setScratchEndListner(new OnScratchEndListner() {

						@Override
						public void scratchEndListner(int scratchCnt) {
						
							mSbar.setProgress(scratchCnt);
							if(scratchCnt == 170){
								scratchView.setOverlayImage(failimage[rdfail]);

								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("scratch_seq", mScrath_seq);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NON4, params3, null, new HttpCallBack() {

									@Override
									public void onHttpCallBackListener(Document document) {
										String rtn = "";
										rtn = document.select("ReturnTable").select("Code").text();
										if(rtn.equals("0")){

											Intent intent = new Intent(ScratchCardNonAdActivity.this, ScratchResultFailActivity.class);
											intent.putExtra("scratch_seq", mScrath_seq);
											intent.putExtra("ad_seq", "");
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

											((Activity)ScratchCardNonAdActivity.this).startActivity(intent);
											((Activity)ScratchCardNonAdActivity.this).overridePendingTransition(R.anim.fade, R.anim.hold); 
											finish();
										}else{
											Toast.makeText(ScratchCardNonAdActivity.this, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
											finish();
										}

									}
								},false);

							}
						}
					});		

				}else if(code.equals("8")){
					scratchView.setOverlayImage(cardimage[rdcard]);
					mBackgroundView.setBackgroundResource(failimage[rdfail]);

					scratchView.setScratchEndListner(new OnScratchEndListner() {

						@Override
						public void scratchEndListner(int scratchCnt) {
						
							mSbar.setProgress(scratchCnt);
							if(scratchCnt == 170){
								scratchView.setOverlayImage(failimage[rdfail]);

								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("scratch_seq", mScrath_seq);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NON4, params3, null, new HttpCallBack() {

									@Override
									public void onHttpCallBackListener(Document document) {
										String rtn = "";
										rtn = document.select("ReturnTable").select("Code").text();
										if(rtn.equals("0")){

											Intent intent = new Intent(ScratchCardNonAdActivity.this, ScratchResultFailActivity.class);
											intent.putExtra("scratch_seq", mScrath_seq);
											intent.putExtra("ad_seq", "");
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

											((Activity)ScratchCardNonAdActivity.this).startActivity(intent);
											((Activity)ScratchCardNonAdActivity.this).overridePendingTransition(R.anim.fade, R.anim.hold); 
											finish();
										}else{
											Toast.makeText(ScratchCardNonAdActivity.this, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
											finish();
										}

									}
								},false);

							}
						}
					});		
				}else if(code.equals("9")){
					scratchView.setOverlayImage(cardimage[rdcard]);
					mBackgroundView.setBackgroundResource(goldimage[rdgold]);

					scratchView.setScratchEndListner(new OnScratchEndListner() {

						@Override
						public void scratchEndListner(int scratchCnt) {
						
							mSbar.setProgress(scratchCnt);
							if(scratchCnt == 170){
								scratchView.setOverlayImage(goldimage[rdgold]);

								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("scratch_seq", mScrath_seq);


								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NON4, params3, null, new HttpCallBack() {

									@Override
									public void onHttpCallBackListener(Document document) {
										String rtn = "";
										rtn = document.select("ReturnTable").select("Code").text();
										if(rtn.equals("0")){

											Intent intent = new Intent(ScratchCardNonAdActivity.this, ScratchResultAdActivity.class);
											intent.putExtra("scratch_seq", mScrath_seq);
											intent.putExtra("ad_seq", "");
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

											((Activity)ScratchCardNonAdActivity.this).startActivity(intent);
											((Activity)ScratchCardNonAdActivity.this).overridePendingTransition(R.anim.fade, R.anim.hold); 
											finish();
										}else{
											Toast.makeText(ScratchCardNonAdActivity.this, "네트워크 에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
											finish();
										}

									}
								},false);

							}
						}
					});	
				}
				else if(code.equals("-2003")){
					Toast.makeText(ScratchCardNonAdActivity.this, "이미 스크래치를 한 광고입니다.", Toast.LENGTH_LONG).show();
					finish();

				}else if(code.equals("-9999")){
					Toast.makeText(ScratchCardNonAdActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
					finish();
				}
				else{
					Toast.makeText(ScratchCardNonAdActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_LONG).show();
					finish();
				}
			}
		}, true);

	}


	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onStop(){
		super.onStop();
		//LogUtil.D(" onStop------------------------------");
		LogUtil.D(" STOP------------------------------");
		finish();
	}
	@Override
	public void onPause(){
		super.onPause();
		//LogUtil.D(" onStop------------------------------");
		LogUtil.D(" onPause------------------------------");
		finish();
	}
	@Override
	protected void onDestroy() {
		LogUtil.D(" onDESTROY------------------------------");
		mHttpUtil.threadStop();
		mHttpUtil2.threadStop();
		mHttpUtil3.threadStop();
		super.onDestroy();
		//sv.destroyDrawingCache();
		//sv = null;
		
		if(mBackgroundView != null){
			mBackgroundView.setImageBitmap(null);
		}
		
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

}
