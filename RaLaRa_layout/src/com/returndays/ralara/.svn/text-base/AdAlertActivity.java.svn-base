package com.returndays.ralara;

import java.util.HashMap;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.service.AppInstallFinderService;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;

public class AdAlertActivity  extends BaseActivity{

	TextView mTitle, mMsg;
	Button mOk, mNo, mNotview;
	HttpDocument mHttp;

	HashMap<String, String> mDatamap;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ad_alert);
		
		mHttp = new HttpDocument(this);

		if(!DbUtil.checkDataBase(getApplicationContext())) {
			DbUtil.initDbFile(getApplicationContext());
		}
		
		mTitle = (TextView) findViewById(R.id.alert_title);
		mMsg = (TextView) findViewById(R.id.alert_msg);
		mOk = (Button) findViewById(R.id.alert_leftbtn);
		mNo = (Button) findViewById(R.id.alert_rightbtn);
		//mNotview = (Button) findViewById(R.id.alert_notviewbtn);

		mDatamap = (HashMap<String, String>) getIntent().getExtras().getSerializable("data");

		/*
		try{
			if(checkIsJoin(mDatamap.get("AD_SEQ"))){
				goAdView();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		mTitle.setText("알림");
		mMsg.setText(mDatamap.get("AD_DESC").replaceAll("nn", "\n"));

		mOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				goAdView();
			}
		});


		mNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		/*
		mNotview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				try{
					insertJoinFlag(mDatamap.get("AD_SEQ"));
				}catch(Exception e){
					e.printStackTrace();
				}
				// TODO Auto-generated method stub
				finish();
			}
		});
		*/

	}

	public void goAdView(){
		try{

			//잠금화면 이미지 광고
			if(mDatamap.get("AD_KIND").equals("1")){
				Intent intent = new Intent(AdAlertActivity.this, AdImageAdTempActivity.class);
				intent.putExtra("ad_seq", mDatamap.get("AD_SEQ"));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				//동영상 광고
			}else if(mDatamap.get("AD_KIND").equals("2")){
				Intent intent = new Intent(AdAlertActivity.this, movieActivity.class);
				intent.putExtra("ad_seq", mDatamap.get("AD_SEQ"));
				intent.putExtra("ad_movie", mDatamap.get("AD_MOVIE"));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				//앱다운로드 광고	
			}else if(mDatamap.get("AD_KIND").equals("3")){

				if (!isPackageInstalled(AdAlertActivity.this, mDatamap.get("END_ACTION"))){
					if( Define.IS_TSTORE  ){
						finish();
					} else {

						Intent intentservice  = new Intent( AdAlertActivity.this, AppInstallFinderService.class);
						intentservice.putExtra("package", mDatamap.get("END_ACTION"));
						intentservice.putExtra("userseq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));
						intentservice.putExtra("ad_seq", mDatamap.get("AD_SEQ"));
						startService(intentservice);

						String installUrl = mDatamap.get("END_ACTION");
						installUrl = "market://details?id="+installUrl;
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setData(Uri.parse(installUrl));

						getApplicationContext().startActivity(intent);
					}
					// 이미 인스톨 되어 있을 경우		
				}else{

					Toast.makeText(getApplicationContext(), "이미 설치되어 있는 앱 입니다.", Toast.LENGTH_LONG).show();
					
					//골드 적립
					
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("ad_seq", mDatamap.get("AD_SEQ"));
					
					mHttp.getDocument(UrlDef.AD_VIEW_ADD_GOLD_AD, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							//LogUtil.D(document.text());
							if(document.select("Code").text().equals("1")){
								Toast.makeText(getApplicationContext(), "본 광고에서 최대 300골드를 받으셨습니다. 더 이상 골드가 적립되지 않습니다.", Toast.LENGTH_SHORT).show();
							}
						}
					}, false);
					
					//골드적립 끝

					String installUrl = mDatamap.get("END_ACTION");
					installUrl = "market://details?id="+installUrl;
					LogUtil.W("installUrl:"+installUrl);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setData(Uri.parse(installUrl));

					getApplicationContext().startActivity(intent);						

				}
				//모바일 웹사이트 광고	
			}else if(mDatamap.get("AD_KIND").equals("4")){
				Intent i = new Intent(Intent.ACTION_VIEW); 
				i.putExtra("ad_seq", mDatamap.get("AD_SEQ"));
				Uri u = Uri.parse( mDatamap.get("END_ACTION")); 
				i.setData(u); 
				startActivity(i);
				//페이스북 광고	
			}else if(mDatamap.get("AD_KIND").equals("5")){
				Intent intent = new Intent(getApplicationContext(), AdViewWebClientActivity.class);
				intent.putExtra("ad_seq",  mDatamap.get("AD_SEQ"));
				intent.putExtra("url",   mDatamap.get("END_ACTION"));
				intent.putExtra("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				startActivity(intent);
			}

			finish();

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static boolean isPackageInstalled(Context ctx, String pkgName) {

		try {
			ctx.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			//e.printStackTrace();
			return false;
		}
		return true;

	}

	private boolean checkIsJoin(String seq){
		int count=0;
		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		Cursor c = db.rawQuery("select count(*) AS CNT from ad_alert where ad_seq = '" + seq + "'", null);
		if(c.moveToFirst()){
			count = c.getInt(0);
			LogUtil.D("is Join Count = " + count);
		}
		
		if(count > 0){
			return true;
		}else{
			return false;
		}
	}

	private void insertJoinFlag(String seq){
		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		ContentValues value = new ContentValues();
		value.put("ad_seq", seq);

		long id = db.insert("ad_alert", "ad_seq", value);
		if(id > 0){
			Cursor c = db.rawQuery("select count(*) AS CNT from ad_alert where ad_seq = '" + seq + "'", null);
			if(c.moveToFirst()){
				int count = c.getInt(0);
				LogUtil.D("count = " + count);
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttp.threadStop();
		super.onDestroy();
	}

}
