package com.returndays.ralara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.example.android.bitmapfun.util.AsyncTask;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.util.MadUtil;

public class VersionInfoActivity extends BaseActivity{

	TextViewNanumGothic current_version_text, latest_version_text, go_update;
	LinearLayout sub_back_btn, vc_back;
	Button save_btn;
	RecyclingImageView imageView1;
	String NewVString, CurrVString, PackageName;
	AsyncTask<Void, Void, Void> Task;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version_check);
		
		initUI();
		initData();
	}

	private void initData() {
		Task = new ConfigDown().execute();
	}

	@SuppressLint("NewApi")
	private void initUI() {
		vc_back = (LinearLayout)findViewById(R.id.vc_back);
		imageView1 = (RecyclingImageView)findViewById(R.id.imageView1);
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.INVISIBLE);
		
		current_version_text = (TextViewNanumGothic)findViewById(R.id.current_version_text);
		latest_version_text = (TextViewNanumGothic)findViewById(R.id.latest_version_text);
		go_update = (TextViewNanumGothic)findViewById(R.id.go_update);
		
		sub_back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		sub_back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		go_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Define.IS_TSTORE){
					// 티스토어로
					MadUtil.TstoreLink(VersionInfoActivity.this, "OA00648870");
				}else{
					goGooglePlay();
				}
			}
		});
		go_update.setEnabled(false);
		
	}
	
	private class ConfigDown extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {

			File folder = new File(Define.AD_APP_FOLDER);
			if(!folder.exists()) folder.mkdirs();
			File f = new File(folder, Define.CONFIG_FILE_NM);
			try {
				URL url = null;
				url = new URL(UrlDef.VERSION_CHECK);
				HttpURLConnection connection = null;
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(1000*5);
				connection.connect();

				InputStream input = null;
				input = url.openStream();
				OutputStream out = null;
				out = new FileOutputStream(f);

				byte[] b = new byte[256];
				int read;

				while ((read = input.read(b)) != -1) {
					out.write(b, 0, read);
				}
				input.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			XmlDom xml = MadUtil.getConfigFile();
			int AppVer = Integer.parseInt(xml.tag("AppVer").text());
			int currVer = 1;
			NewVString = xml.tag("AppVerName").text();
			PackageInfo i;
			try {
				i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
				CurrVString = i.versionName;
				PackageName = i.packageName;
				currVer = i.versionCode;
				
				current_version_text.setText(CurrVString);
				latest_version_text.setText(NewVString);
				
				if(currVer == AppVer){
					go_update.setBackgroundResource(R.drawable.button_gray);
					go_update.setText("현재 최신버전 입니다.");
				}else{
					go_update.setBackgroundResource(R.drawable.button_purple);
					go_update.setEnabled(true);
				}
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void goGooglePlay(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="+ PackageName));
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		if(Task != null){
//			if(!Task.isCancelled()){
//				Task.cancel(true);
//			}
//		}
		
		super.onDestroy();
	}
}
