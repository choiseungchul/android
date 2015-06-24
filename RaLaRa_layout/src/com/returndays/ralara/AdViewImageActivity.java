package com.returndays.ralara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.androidquery.util.XmlDom;

import android.support.v4.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.returndays.ralara.BaseActivity;
import com.returndays.ralara.AdViewImageActivity.AdViewPagerAdapter;
import com.returndays.ralara.AdViewImageActivity.FragType;
import com.returndays.fragment.AdImageViewFragment;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.util.DataDownload;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;

import com.returndays.ralara.R;
import com.returndays.ralara.util.*;


public class AdViewImageActivity extends FragmentActivity{

	ViewPager mPager;
	LinearLayout mBottomContainer;
	Context ctx;
	int mCurPage = 0;
	public String mAd_Seq;
	AtomicInteger mImgCounter;
	String mAd_kind	;
	ArrayList<Button> mBtns;
	public XmlDom mXmldata;
	AdViewPagerAdapter mAdapter;
	enum FragType{IMAGE};
	ArrayList<FragType> mPagerData;

	ProgressDialog mProgressDialog;
	HttpUtil httpUtil;
	String[] mImageUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adimageview);
		Bundle bundle = getIntent().getExtras();
		mAd_Seq = bundle.getString("ad_seq");

		httpUtil = new HttpUtil(this);
		
		ctx = this;
		mImgCounter = new AtomicInteger(0);
		mBtns = new ArrayList<Button>();

		initUI();
		setData();

	}
	
	void initUI() {
		mPager = (ViewPager) findViewById(R.id.ad_image_pager);
		mBottomContainer = (LinearLayout) findViewById(R.id.status_container);
		mProgressDialog = new ProgressDialog(this);
	}

	void setData() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ad_seq", mAd_Seq);
		params.put("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(ctx));
		httpUtil.httpExecute(UrlDef.AD_VIEW_IMAGE, params, new HttpListener() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(final XmlDom xml, HttpResultDto result) {
				//LogUtil.I(xml.toString());
				if(result.isSuccess) {

					mAd_kind = xml.tag("AD_KIND").text();
					mXmldata = xml.tag("ReturnTable");

					String img_01 = mXmldata.tag("AD_IMG1").text();
					String img_02 = mXmldata.tag("AD_IMG2").text();
					String img_03 = mXmldata.tag("AD_IMG3").text();

					LogUtil.W("mAd_img1:"+img_01);
					LogUtil.W("mAd_img2:"+img_02);
					LogUtil.W("mAd_img3:"+img_03);

					mPagerData = new ArrayList<AdViewImageActivity.FragType>();
					ArrayList<String> imgArr = new ArrayList<String>();

					if(!img_01.isEmpty()){
						imgArr.add(img_01);
						mPagerData.add(FragType.IMAGE);
						//LogUtil.W("111111111111111111111111111111aaa");		
					}
					if(!img_02.isEmpty()){
						imgArr.add(img_02);
						mPagerData.add(FragType.IMAGE);
						//LogUtil.W("2222222222222222222222222222222aaa");
					}
					if(!img_03.isEmpty()){
						imgArr.add(img_03);
						mPagerData.add(FragType.IMAGE);
						//LogUtil.W("3333333333333333333333333333333aaa");
					}

					mImageUrl =	imgArr.toArray(new String[]{});

					LogUtil.W("mImageUrl:"+imgArr.size());					

					if(imgArr.size() > 0) {
						progressInit(String.format("[ %s/%s ]광고 컨텐츠 다운로드", mImgCounter.get()+1, mImageUrl.length));
						mProgressDialog.show();
						if(mImageUrl.length > 0) new DataDownload(mProgressDialog, mHandler).execute(mImageUrl[mImgCounter.getAndIncrement()]);
					} 

				}
			}
		}, true);
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				mProgressDialog.setMax((Integer) msg.obj);
			} else if(msg.what == 1) {
				if(mImgCounter.get() < mImageUrl.length) {
					progressInit(String.format("[ %s/%s ]광고 컨텐츠 다운로드", mImgCounter.get()+1, mImageUrl.length));
					new DataDownload(mProgressDialog, mHandler).execute(mImageUrl[mImgCounter.getAndIncrement()]);
				} else {
					mProgressDialog.dismiss();
					setPager();
				}
			}
		}

	};

	void progressInit(String title) {
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage("잠시만 기다려주세요...");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgress(0);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCanceledOnTouchOutside(false);
	}

	class AdViewPagerAdapter extends FragmentPagerAdapter {

		public AdViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			//return AdImageViewFragment.newInstence(arg0);

			if(FragType.IMAGE == mPagerData.get(arg0)) {
				return AdImageViewFragment.newInstence(arg0);
			}else{
				return null;
			}

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mPagerData.size();
		}
	}

	@Override
	protected void onDestroy() {
		mBtns.clear();
		mPagerData.clear();
		
		super.onDestroy();
	}


	void setPager() {
		mAdapter = new AdViewPagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				mCurPage = arg0;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});


		mBottomContainer.removeAllViews();

		for(int i=0;i<mPagerData.size();i++) {
			Button btn = new Button(ctx);
			int wh = (int) MadUtil.PixelToDp(BaseActivity.DENSITY, 24);
			LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(wh, wh);
			btn.setBackgroundResource(R.drawable.btn_page);
			btn.setEnabled(i == mCurPage);
			mBottomContainer.addView(btn);
			btn.setLayoutParams(layout);
			layout.setMargins(i==0?0:10, 0, 0, 0);
			mBtns.add(btn);
		}
	}



}


