package com.returndays.ralara;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.androidquery.AQuery;
import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ScratchResultActivity extends BaseActivity{

	TextView txt_egg;
	TextView txt_gold;
	TextView txt_scratch;
	HttpDocument mHttpUtil,mHttpUtil2, mHttpUtil3;
	LinearLayout mScratchResult;
	ImageView mGoBack;
	ImageView mLogo;
	Button mBtnGoWinPage, mBtnGoMyPage;
	RecyclingImageView mImgResult;
	String mAd_seq ="", mScratch_seq ="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch_result);

		Bundle b = getIntent().getExtras();
		mAd_seq = (String) b.get("ad_seq");
		mScratch_seq = (String) b.get("scratch_seq");


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		mHttpUtil = new HttpDocument(this);
		mHttpUtil2 = new HttpDocument(this);
		mHttpUtil3 = new HttpDocument(this);

		txt_egg = (TextView) findViewById(R.id.top_r_cnt2);
		txt_gold = (TextView) findViewById(R.id.top_m_cnt2);
		txt_scratch = (TextView) findViewById(R.id.top_s_cnt2);
		mScratchResult = (LinearLayout) findViewById(R.id.scrach_view_result);
		mGoBack = (ImageView) findViewById(R.id.top_back_btn);
		mLogo = (ImageView) findViewById(R.id.top_logo_btn_result);
		mBtnGoMyPage = (Button) findViewById(R.id.btn_mypage);
		mBtnGoWinPage = (Button) findViewById(R.id.btn_mywinlist);
		mImgResult = (RecyclingImageView) findViewById(R.id.img_scratch_win_real_img);


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

		mBtnGoMyPage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MyProfileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);


			}
		});

		mBtnGoWinPage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), MyRewardActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);


			}
		});


		final String user_seq = com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext());
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

						if(code.equals("1")){

							String ad_img = document.select("AD_IMG").text();
							final String need_address = document.select("NEED_ADDRESS").text();
							final String winner_seq = document.select("WINNER_SEQ").text();
							final String prod_id = document.select("PROD_ID").text();

							AQuery aq = new AQuery(getApplicationContext());

							aq.id(mImgResult).image(ad_img).width(270).height(339);					
							if(need_address.equals("Y")){
								mBtnGoMyPage.setVisibility(View.VISIBLE);
							}
							if(need_address.equals("N")){	
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("ad_seq", mAd_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("prod_id", prod_id);

								mHttpUtil3.getDocument(UrlDef.AD_STORE_VIEW6, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										//LogUtil.D("aaaaaaaaaaaaaaaaaaaa : " + document.text());
									}
								}, true);
							}else if(need_address.equals("Y")){
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("ad_seq", mAd_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("prod_id", prod_id);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NEEDADDRESS_6, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										//LogUtil.D("aaaaaaaaaaaaaaaaaaaa : " + document.text());
									}
								}, true);

							}else if(need_address.equals("I")){
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("ad_seq", mAd_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("prod_id", prod_id);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_GAME_6, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										//LogUtil.D("aaaaaaaaaaaaaaaaaaaa : " + document.text());
									}
								}, true);

							}

						}else{
							Toast.makeText(ScratchResultActivity.this, "잘못된 접근입니다!! 지속적으로 에러 발생시 1:1 문의사항을 남겨주세요!", Toast.LENGTH_LONG).show();
							finish();
						}
					}
				}
			}, true);
		}
		else{
			Hashtable<String, String> params2 = new Hashtable<String, String>();
			params2.put("user_seq", user_seq);
			params2.put("scratch_seq", mScratch_seq);

			mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW_NON5, params2, null, new HttpCallBack() {
				@Override
				public void onHttpCallBackListener(Document document) {

					if(document.select("ResultTable").select("Result").text().equals("true")){
						Elements entry = document.select("ReturnTable");

						String code = entry.select("Code").text();
							
						if(code.equals("1")){
								
							String ad_img = document.select("AD_IMG").text();
							final String need_address = document.select("NEED_ADDRESS").text();
							final String winner_seq = document.select("WINNER_SEQ").text();
							final String prod_id = document.select("PROD_ID").text();
							final String rand_ad_seq = document.select("AD_SEQ").text();

							AQuery aq = new AQuery(getApplicationContext());
							
							LogUtil.D("bbbbbbbbbbbbbbb",entry.text());	
							
							aq.id(mImgResult).image(ad_img).width(270).height(339);					
							if(need_address.equals("Y")){
								mBtnGoMyPage.setVisibility(View.VISIBLE);
							}
							if(need_address.equals("N")){	
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("scratch_seq", mScratch_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("prod_id", prod_id);

								mHttpUtil3.getDocument(UrlDef.AD_STORE_VIEW6NonAdseq, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {

									}
								}, true);
							}else if(need_address.equals("Y")){
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("ad_seq", rand_ad_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("scratch_seq", mScratch_seq);
								params3.put("prod_id", prod_id);

								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_NEEDADDRESS_6, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {

									}
								}, true);

							}else if(need_address.equals("I")){
								Hashtable<String, String> params3 = new Hashtable<String, String>();
								params3.put("user_seq", user_seq);
								params3.put("ad_seq", rand_ad_seq);
								params3.put("winner_seq", winner_seq);
								params3.put("prod_id", prod_id);
								params3.put("scratch_seq", mScratch_seq);
								
								
								
								mHttpUtil3.getDocument(UrlDef.AD_VIEW_VIEW_GAME_6_NO_AD, params3, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										
									}
								}, true);

							}

						}else{
							Toast.makeText(ScratchResultActivity.this, "잘못된 접근입니다!! 지속적으로 에러 발생시 1:1 문의사항을 남겨주세요!", Toast.LENGTH_LONG).show();
							finish();
						}
					}
				}
			}, true);
		}


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
		mHttpUtil3.threadStop();
		super.onDestroy();
		
		if(mImgResult != null){
			mImgResult.setImageBitmap(null);
		}
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}
}
