package com.returndays.ralara;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.androidquery.AQuery;
import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class ScratchResultAdActivity extends BaseActivity {

	TextView txt_egg;
	TextView txt_gold;
	TextView txt_scratch;
	HttpDocument mHttpUtil, mHttpUtil2;
	LinearLayout mScratchResult;
	ImageView mGoBack ;
	ImageView mLogo;
	//Button mbtn_plus_gold;
	Boolean isGold = false;
	String mAd_seq ="", mScratch_seq ="";
	RecyclingImageView mRealImg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratchadresult);

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
		mScratchResult = (LinearLayout) findViewById(R.id.img_scrach_ad_result);
		mGoBack = (ImageView) findViewById(R.id.top_back_btn);
		mLogo = (ImageView) findViewById(R.id.top_logo_btn_result);
		//mbtn_plus_gold = (Button) findViewById(R.id.btn_plus_gold);
		mRealImg = (RecyclingImageView) findViewById(R.id.img_scratch_ad_real_img);



		//mScratchResult.setBackgroundResource( R.drawable.img_sorry);
		mGoBack.setVisibility(View.VISIBLE);

		mGoBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
		
		
		mRealImg.setOnClickListener(new OnClickListener() {
			
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

					if(document.select("ResultTable").select("Result").text().equals("true")){
						Elements entry = document.select("ReturnTable");

						String code = entry.select("Code").text();

						if(code.equals("9")){
							Elements entry2 = document.select("DataTable");
							String ad_img = document.select("AD_IMG").text();
							final String end_action = document.select("END_ACTION").text();

							AQuery aq = new AQuery(getApplicationContext());

							aq.id(mRealImg).image(ad_img).width(270).height(339);				
							

						}else{
							Toast.makeText(ScratchResultAdActivity.this, "잘못된 접근입니다!! 지속적으로 에러 발생시 1:1 문의사항을 남겨주세요!", Toast.LENGTH_LONG).show();
							finish();
						}


					}

					//LogUtil.D(document.text());

				}
			}, true);
		}else{
			Hashtable<String, String> params2 = new Hashtable<String, String>();
			params2.put("user_seq", user_seq);
			params2.put("scratch_seq", mScratch_seq);

			mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW_NON5, params2, null, new HttpCallBack() {
				@Override
				public void onHttpCallBackListener(Document document) {

					if(document.select("ResultTable").select("Result").text().equals("true")){
						Elements entry = document.select("ReturnTable");

						String code = entry.select("Code").text();

						if(code.equals("9")){
							Elements entry2 = document.select("DataTable");
							String ad_img = document.select("AD_IMG").text();
							final String end_action = document.select("END_ACTION").text();

							AQuery aq = new AQuery(getApplicationContext());

							aq.id(mRealImg).image(ad_img).width(270).height(339);
							


						}else{
							Toast.makeText(ScratchResultAdActivity.this, "잘못된 접근입니다!! 지속적으로 에러 발생시 1:1 문의사항을 남겨주세요!", Toast.LENGTH_LONG).show();
							finish();
						}
					}

				}
			}, true);
		}



	}

	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onStop(){
		super.onStop();
		finish();
	}
	@Override
	public void onPause(){
		super.onPause();
		finish();
	}
	@Override
	protected void onDestroy() {
		mHttpUtil.threadStop();
		mHttpUtil2.threadStop();
		
		super.onDestroy();
		
		if(mRealImg != null){
			mRealImg.setImageBitmap(null);
		}
		
	}


	@Override
	public void onBackPressed() {
		finish();
	}

}
