package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;


public class ScratchResultFailActivity extends BaseActivity {

	TextView txt_egg;
	TextView txt_gold;
	TextView txt_scratch;
	HttpDocument mHttpUtil, mHttpUtil2;
	LinearLayout mScratchResult;
	ImageView mGoBack ;
	ImageView mLogo;
	String mAd_seq ="", mScratch_seq ="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch_fail);

		Bundle b = getIntent().getExtras();
		mAd_seq = (String) b.get("ad_seq");
		mScratch_seq = (String) b.get("scratch_seq");


		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		mHttpUtil = new HttpDocument(this);
		mHttpUtil2 = new HttpDocument(this);


		txt_egg = (TextView) findViewById(R.id.top_r_cnt2);
		txt_gold = (TextView) findViewById(R.id.top_m_cnt2);
		txt_scratch = (TextView) findViewById(R.id.top_s_cnt2);
		mScratchResult = (LinearLayout) findViewById(R.id.scrach_view_result);
		mGoBack = (ImageView) findViewById(R.id.top_back_btn);
		mLogo = (ImageView) findViewById(R.id.top_logo_btn_result);



		mScratchResult.setBackgroundResource( R.drawable.img_sorry);
		mGoBack.setVisibility(View.VISIBLE);

		mScratchResult.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		mGoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		String user_seq = com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext());


		Hashtable<String, String> params =  new Hashtable<String, String>();
		params.put("user_seq",user_seq );


		mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				String code = document.select("ReturnTable").text();
				if(!code.equals("")) {

					String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
					String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
					String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

					txt_egg.setText(Egg);
					txt_gold.setText(Gold);
					txt_scratch.setText(Scratch);

				}
			}

		}, false);


		if(!mAd_seq.equals("")){
			Hashtable<String, String> params2 = new Hashtable<String, String>();
			params2.put("user_seq", user_seq);
			params2.put("ad_seq", mAd_seq);

			mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW5, params2, null, new HttpCallBack() {

				@Override
				public void onHttpCallBackListener(Document document) {
					//LogUtil.D(document.text());

					if(document.select("ResultTable").select("Result").text().equals("true")){
						Elements entry = document.select("ReturnTable");

						String code = entry.select("Code").text();
						
						//Toast.makeText(ScratchResultFailActivity.this, code, Toast.LENGTH_LONG).show();
					}

				}
			}, true);
		}else{
			Hashtable<String, String> params2 = new Hashtable<String, String>();
			params2.put("user_seq", user_seq);
			params2.put("scratch_seq", mScratch_seq);
			
			LogUtil.D("user_seq ::::::::: " + user_seq);
			LogUtil.D("scratch_seq ::::::::: " + mScratch_seq);
			
			mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW_NON5, params2, null, new HttpCallBack() {

				@Override
				public void onHttpCallBackListener(Document document) {
					LogUtil.D(document.text());

					if(document.select("ResultTable").select("Result").text().equals("true")){
						Elements entry = document.select("ReturnTable");

						String code = entry.select("Code").text();
						
						//Toast.makeText(ScratchResultFailActivity.this, code, Toast.LENGTH_LONG).show();
					}

				}
			}, true);

		}

	}

	@Override
	public void onResume(){
		super.onResume();
		/*
		 * 기존에 열었던 페이지 인지..확인
    	Intent intent = new Intent(getApplicationContext(), AdlistActivity.class);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		overridePendingTransition(0, 0);
		startActivity(intent);
		 */
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
		//DestroyUtil.unbindReferences(myActivity, R.id.seekBar2);
		//DestroyUtil.unbindReferences(myActivity, R.id.main_frame);

		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		finish();
		// TODO Auto-generated method stub
	}
}
