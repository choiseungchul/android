package com.returndays.ralara.service;


import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.returndays.customview.SlideImageView.OnItemChangeListner;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.AdImageAdTempActivity;
import com.returndays.ralara.AdImageOnlyActivity;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.LogUtil;

public class AppInstallFinderService extends Service {

	public boolean isLive = false;

	String mPackage , mUserSeq, mAdSeq ;
	HttpDocument mHttpUtil; 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);

		try{

			mPackage = intent.getExtras().getString("package");
			mUserSeq = intent.getExtras().getString("userseq");
			mAdSeq = intent.getExtras().getString("ad_seq");

			mHttpUtil = new HttpDocument(this);


			LogUtil.D("install finder service start : ");

			if(!isLive &&!mPackage.equals("")){
				FinderThread threads = new FinderThread(this, mHandler);
				threads.start();
				isLive = true;
			}
		}catch(Exception e){
			e.printStackTrace();	
		}

		return Service.START_STICKY ;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mHttpUtil.threadStop();
		LogUtil.D("install finder service end" );
		isLive = false;
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


	class FinderThread extends Thread{
		AppInstallFinderService mParent;
		Handler mHandler;

		public FinderThread(AppInstallFinderService parents, Handler handler){
			mParent = parents;
			mHandler = handler;
		}

		public void run(){
			boolean isInstall = false;

			LogUtil.D(" 쓰레드 실행!~!! " );

			for(int i=0;i<200;i++){

				isInstall = isPackageInstalled(getBaseContext(), mPackage);

				LogUtil.D(" i : " + i + " / is install :  " + isInstall );

				if(isInstall){
					Message msg = new Message();
					msg.what = 0;	
					mHandler.sendMessage(msg);
					return;

				}
				try{Thread.sleep(3000);}
				catch(Exception e){
					e.printStackTrace();
				}

			}
		}
	}
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				//스크래치권...지급
				Hashtable<String, String> params2 = new Hashtable<String, String>();
				params2.put("user_seq", Setting.getUserSeq(getApplicationContext()));
				params2.put("ad_seq", mAdSeq);

				mHttpUtil.getDocument(UrlDef.AD_VIEW_VIEW1, params2, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						String code = document.select("ReturnTable").select("Code").text();

						if(code.equals("0")){
							if(!document.select("DataTable").text().equals("")){
								String seq = document.select("SEQ").text();

								if(!seq.equals("")){
									Toast.makeText(AppInstallFinderService.this, "감사합니다! 앱 설치를 하셨네요~ 랄라라 스크래치권 1장을 획득하셨습니다.", Toast.LENGTH_LONG);
									onDestroy();
									return;
									
								}else{
									Toast.makeText(AppInstallFinderService.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();
									onDestroy();
									return;
								}

							}
						}else if(code.equals("-9998")){
							onDestroy();
							return;
						}			
						else{
							Toast.makeText(AppInstallFinderService.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();
							onDestroy();
							return;
						}
					}
				}, false);



				onDestroy();
			}
		}
	};


}