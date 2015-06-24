package com.returndays.ralara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.example.android.bitmapfun.util.AsyncTask;
import com.returndays.customview.DefaultDialog;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.customview.TutorialDialog;
import com.returndays.customview.UserTypeDialog;
import com.returndays.ralara.adapter.SettingListAdapter;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.SettingInfoDto;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class SettingActivity extends BaseActivity{

	private SettingListAdapter adap;
	private ListView mMylist;
	boolean listLoaded = false, isFirst = true;
	private LinearLayout tab1, tab2, tab3, tab4;
	
	private RecyclingImageView tab1_img, tab2_img, tab3_img, tab4_img;
	private OnClickListener onclickListener;
	private LinearLayout tab4_bottom_line;
	TextViewNanumGothic top_sub_title_text ;
	ArrayList<SettingInfoDto> settingList = new ArrayList<SettingInfoDto>();
	boolean isUpdate = false;
	String verSionString = "";
	String currVerString = "";
	String packageName = "";
	
	AsyncTask<Void, Void, Void> task;
	
	public static int instanceCount = 0;
	
	public SettingActivity () {
        super();
        instanceCount++;
        LogUtil.D("test", "SettingActivity() instanceCount: " + instanceCount);
	}
	@Override
    protected void finalize() throws Throwable {
              super.finalize();
              instanceCount--;
              LogUtil.D("test", "finalize() instanceCount: " + instanceCount);
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initUI();
		initData();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
	}
	
	private void initData() {

		task = new ConfigDown().execute();
		
		settingList = new ArrayList<SettingInfoDto>();

		// 버전정보
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu1), "", "로딩중..", getResources().getString(R.string.setting_gubun1), 0, false, ""));
		// 공지및 도움말
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu2), "", "", "", 1, false, ""));
		// 로그아웃
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu9), "", "", "", 8, false, ""));
		// 튜토리얼
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu11), "", "","랄라라 사용법 보기" , 10, false, ""));
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu12), "", "","" , 11, false, ""));
		
		// 로고송 설정
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu3), "", "", getResources().getString(R.string.setting_gubun2), 2 , false, ""));
		
		// 알림설정
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu4), "", "", "" , 3, false, ""));
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu5), "", "", getResources().getString(R.string.setting_gubun3), 4, false, ""));

		// 광고 모드 설정
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu13), "", "", getResources().getString(R.string.setting_gubun4), 12, false, ""));
		// 수발신광고 설정
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu10), "", "","", 9, false, ""));

		// 문의하기
		settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu8), "", "", getResources().getString(R.string.setting_gubun5) , 7, false, ""));
		
		// 개발...
		//settingList.add(new SettingInfoDto(getResources().getString(R.string.setting_menu_dev1), "", "", getResources().getString(R.string.setting_gubun_dev) , 10, false, ""));

		adap = new SettingListAdapter(this, settingList); 

		mMylist.setAdapter(adap);

		mMylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {

				int id = adap.getItem(position).ID;

				LogUtil.D("Setting Idx = " + id);

				if(id == 0){
					// 버전정보
					Intent ii = new Intent(getApplicationContext(), VersionInfoActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivity(ii);
					
				}else if(id == 1){
					
					String noticeUrl = UrlDef.NOTICE_URL;
					
					Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					i.putExtra("url", noticeUrl);
					startActivity(i);
					
				}else if(id == 2){
					Intent i = new Intent(getApplicationContext(), SetAlertActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 3){
					Intent i = new Intent(getApplicationContext(), SetAlertSoundActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 4){
					Intent i = new Intent(getApplicationContext(), SetSoundActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
//				}
//				else if(id == 5){
//					//					Intent i = new Intent(ctx, R2ScratchActivity.class );
//					//					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					//					startActivity(i);
//					//LinearLayout item = (LinearLayout)mMylist.getItemAtPosition(position);
//
//					CheckBox ck = (CheckBox)view.findViewById(R.id.setting_chk);
//					if(ck.isChecked()){
//						com.returndays.ralara.preference.Setting.setCallAd(getApplicationContext(), "OFF");
//						ck.setChecked(false);
//					}else{
//						com.returndays.ralara.preference.Setting.setCallAd(getApplicationContext(), "ON");
//						ck.setChecked(true);
//					}
//
//				}
//				else if(id == 6){
//					//					Intent i = new Intent(ctx, MyGoldCountActivity.class );
//					//					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					//					startActivity(i);
//					CheckBox ck = (CheckBox)view.findViewById(R.id.setting_chk);
//					if(ck.isChecked()){
//						com.returndays.ralara.preference.Setting.setSlideAd(getApplicationContext(), "OFF");
//
//						ck.setChecked(false);
//					}else{
//						com.returndays.ralara.preference.Setting.setSlideAd(getApplicationContext(), "ON");
//						ck.setChecked(true);
//					}

				}else if(id == 7){
					Intent i = new Intent(getApplicationContext(), FaqActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 8){
					
					final DefaultDialog alert = new DefaultDialog(SettingActivity.this);
					alert.setTitle("로그아웃");
					alert.setMessage("로그아웃 하시겠습니까?");
					alert.setPositiveButton("확인", new OnClickListener() {
						
						
						@SuppressLint("InlinedApi")
						@Override
						public void onClick(View v) {
							MadUtil.clearAllPrefferencesLogout(getApplicationContext());
							MadUtil.DBDelete(getApplicationContext());
							Intent i = new Intent(getApplicationContext(), LoginActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							alert.dismiss();
							finish();
							startActivity(i);
							overridePendingTransition(0, 0);
							
						}
					});
					alert.setNegativeButton("취소", new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							alert.dismiss();
						}
					});
					
					alert.show();
				}else if(id == 9){
					// 수발신 설정
					Intent i = new Intent(getApplicationContext(), AdCallSettingActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 10){
					// 튜토리얼 보기
					TutorialDialog t_dialog = new TutorialDialog(SettingActivity.this);
					t_dialog.show();
				}else if(id == 11){
					// 메뉴얼 보기
					Intent i = new Intent(getApplicationContext(), TutorialActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					i.putExtra("act","setting");
					startActivity(i);
				}else if(id == 12){
					UserTypeDialog dialog = new UserTypeDialog(SettingActivity.this);
					dialog.show();
				}
			}
		});
	}
	
	
	private void initUI() {
		
		top_sub_title_text = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text);
		top_sub_title_text.setText("설정");
		
		tab4_bottom_line = (LinearLayout)findViewById(R.id.tab4_bottom_line);
		tab4_bottom_line.setBackgroundResource(R.drawable.tab_item_on);
		
		mMylist = (ListView)findViewById(R.id.setting_list);
		
		tab1 = (LinearLayout)findViewById(R.id.tab1);
		tab2 = (LinearLayout)findViewById(R.id.tab2);
		tab3 = (LinearLayout)findViewById(R.id.tab3);
		tab4 = (LinearLayout)findViewById(R.id.tab4);

		tab1_img = (RecyclingImageView)findViewById(R.id.tab1_image);
		tab2_img = (RecyclingImageView)findViewById(R.id.tab2_image);
		tab3_img = (RecyclingImageView)findViewById(R.id.tab3_image);
		tab4_img = (RecyclingImageView)findViewById(R.id.tab4_image);

		tab4_img.setImageResource(R.drawable.tab4_on);
		

		onclickListener = new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent ii = null;
				
				switch (v.getId()) {
				case R.id.tab1:
					ii = new Intent(getApplicationContext(), AdlistActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(ii);
					overridePendingTransition(0, 0);
					break;
				case R.id.tab2:
					ii = new Intent(getApplicationContext(), TalkActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(ii);
					overridePendingTransition(0, 0);
					break;
				case R.id.tab3:
					ii = new Intent(getApplicationContext(), MypageActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(ii);
					overridePendingTransition(0, 0);
					break;
				case R.id.tab4:
					ii = new Intent(getApplicationContext(), SettingActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(ii);
					overridePendingTransition(0, 0);
					break;
				}
			}
		};

		tab1.setOnClickListener(onclickListener);
		tab2.setOnClickListener(onclickListener);
		tab3.setOnClickListener(onclickListener);
		tab4.setOnClickListener(onclickListener);
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
				int lenghtOfFile = connection.getContentLength();
				LogUtil.I("lenghtOfFile===============================:"+lenghtOfFile);

				InputStream input = null;
				input = url.openStream();
				OutputStream out = null;
				out = new FileOutputStream(f);

				byte[] b = new byte[100];
				int read;
				int total = 0;

				while ((read = input.read(b)) != -1) {
					LogUtil.D("----------------------");
					total += read;
					LogUtil.D("total : " + total);
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
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(Void result) {
			XmlDom xml = MadUtil.getConfigFile();
			SettingInfoDto dto = settingList.get(0);
			
			int appVer = Integer.parseInt(xml.tag("AppVer").text());
			int curVersion = 1;
			com.returndays.ralara.preference.Setting.setSvrVersion(getApplicationContext(), String.valueOf(appVer));
			String packageNm = "", versionName = "";
			try {
				PackageInfo i = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0);
				curVersion = i.versionCode;
				packageNm = i.packageName;
				versionName = i.versionName;
			} catch(NameNotFoundException e) {
				e.printStackTrace();
			}
			
			dto.RIGHT_TITLE = versionName;
			
			if(appVer > curVersion){
				dto.UPDATE_ALERT = "Y";
				settingList.set(0, dto);
				isUpdate = true;
				verSionString = xml.tag("AppVerName").text();
				packageName = packageNm;
			}
			
			adap.setData(settingList);
			adap.notifyDataSetChanged();
		}
	}

//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		DialogUtil.confirm(this, R.string.program_end_msg, new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				DialogUtil.alert.dismiss();
//				finish();
//				endApplication();
//				
//				//SettingActivity.super.onBackPressed();
//			}
//		});
//	}
	
	@Override
	protected void onDestroy() {
		
		if(tab1_img != null){
			tab1_img.setImageBitmap(null);
		}
		if(tab2_img != null){
			tab2_img.setImageBitmap(null);
		}
		if(tab3_img != null){
			tab3_img.setImageBitmap(null);
		}
		if(tab4_img != null){
			tab4_img.setImageBitmap(null);
		}
		
//		if (task != null){
//			if(!task.isCancelled()){
//				boolean isKill = task.cancel(true);
//				LogUtil.D("async task kill result = " + isKill);
//			}
//		}
		
		super.onDestroy();
	}
}
