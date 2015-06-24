package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.ralara.adapter.MyPageListAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.MyPageInfoDto;
import com.returndays.ralara.kakao.KakaoLink;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;

public class MypageActivity extends BaseActivity{

	ListView mMylist;
	MyPageListAdapter adap;
	Context ctx;
	int curr_item_pos = 0;
	HttpDocument mHttpUtil;
	private LinearLayout tab3_bottom_line;
	private LinearLayout tab1, tab2, tab3, tab4;
	
	private RecyclingImageView tab1_img, tab2_img, tab3_img, tab4_img;
	private OnClickListener onclickListener;
	com.returndays.customview.TextViewNanumGothic top_sub_title_text;
	ArrayList<MyPageInfoDto> mypagelist = null;

	public static int instanceCount = 0;

	public MypageActivity () {
		super();
		instanceCount++;
		LogUtil.D("test", "MypageActivity() instanceCount: " + instanceCount);
	}
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		instanceCount--;
		LogUtil.D("test", "finalize() instanceCount: " + instanceCount);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypage);
		ctx = getApplicationContext();
		mHttpUtil = new HttpDocument(getApplicationContext());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initUI();
		initData();
	}

	private void initData() {

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(ctx));

		mHttpUtil.getDocument(UrlDef.GET_COUNTS, params, null, new HttpDocument.HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("Result").text().equals("true")){
					String egg = document.select("EGG").text();
					String gold = document.select("GOLD").text();
					String scratch = document.select("SCRATCH").text();

					if(mypagelist.size() > 7){
						mypagelist.set(4, new MyPageInfoDto(getResources().getString(R.string.mypage_scratch_cnt), "", scratch, getResources().getString(R.string.mypage_div_2), 3));
						mypagelist.set(5, new MyPageInfoDto(getResources().getString(R.string.mypage_egg_cnt), "", egg, "", 4));
						mypagelist.set(6, new MyPageInfoDto(getResources().getString(R.string.mypage_gold_cnt), "", gold, "", 5));
						
						adap.setData(mypagelist);
						adap.notifyDataSetChanged();
					}
				}
			}
		}, false);
	}

	private void sendKakao() throws Exception{
		ArrayList<Map<String, String>> metaInfoArray = new ArrayList<Map<String, String>>();

		// If application is support Android platform.
		Map<String, String> metaInfoAndroid = new Hashtable<String, String>(1);
		metaInfoAndroid.put("os", "android");
		metaInfoAndroid.put("devicetype", "phone");
		metaInfoAndroid.put("installurl", "market://details?id=com.returndays.ralara");
		metaInfoAndroid.put("executeurl", "ralaralink://startmain");
		
		// If application is support ios platform.
//		Map<String, String> metaInfoIOS = new Hashtable<String, String>(1);
//		metaInfoIOS.put("os", "ios");
//		metaInfoIOS.put("devicetype", "phone");
//		metaInfoIOS.put("installurl", "your iOS app install url");
//		metaInfoIOS.put("executeurl", "kakaoLinkTest://starActivity");
		
		// add to array
		metaInfoArray.add(metaInfoAndroid);
//		metaInfoArray.add(metaInfoIOS);
		
		// Recommended: Use application context for parameter. 
		KakaoLink kakaoLink = KakaoLink.getLink(getApplicationContext());
		
		// check, intent is available.
		if(!kakaoLink.isAvailableIntent()) {
			DialogUtil.alert(MypageActivity.this, "카카오톡이 설치되어 있지 않습니다.");			
			return;
		}
		
		String encoding = "UTF-8";
		/**
		 * @param activity
		 * @param url
		 * @param message
		 * @param appId
		 * @param appVer
		 * @param appName
		 * @param encoding
		 * @param metaInfoArray
		 */
		kakaoLink.openKakaoAppLink(
				this, 
				"market://details?id=com.returndays.ralara", 
				getString(R.string.kakao_recomm_comment),  
				getPackageName(), 
				getPackageManager().getPackageInfo(getPackageName(), 0).versionName,
				"랄라라",
				encoding , 
				metaInfoArray);
	}
	
	private void initUI() {

		top_sub_title_text = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text);
		top_sub_title_text.setText("마이페이지");

		tab3_bottom_line = (LinearLayout)findViewById(R.id.tab3_bottom_line);
		tab3_bottom_line.setBackgroundResource(R.drawable.tab_item_on);

		tab1 = (LinearLayout)findViewById(R.id.tab1);
		tab2 = (LinearLayout)findViewById(R.id.tab2);
		tab3 = (LinearLayout)findViewById(R.id.tab3);
		tab4 = (LinearLayout)findViewById(R.id.tab4);

		tab1_img = (RecyclingImageView)findViewById(R.id.tab1_image);
		tab2_img = (RecyclingImageView)findViewById(R.id.tab2_image);
		tab3_img = (RecyclingImageView)findViewById(R.id.tab3_image);
		tab4_img = (RecyclingImageView)findViewById(R.id.tab4_image);

		tab3_img.setImageResource(R.drawable.tab3_on);

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

		mMylist = (ListView)findViewById(R.id.mypage_list);
		
		mypagelist = new ArrayList<MyPageInfoDto>();
		
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_profile), "", "", getResources().getString(R.string.mypage_div_1), 0));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_add_info), "", "", "", 10));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_repass), "", "", "", 1));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_withraw), "", "", "", 2 ));

		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_scratch_cnt), "", "로딩중..", getResources().getString(R.string.mypage_div_2), 3));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_egg_cnt), "", "로딩중..", "", 4));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_gold_cnt), "", "로딩중..", "", 5 ));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_myreward), "", "", "" , 6));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_recomm_friend), "", "", "" , 7));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_recomm_kakao), "", "", "" , 8));
		mypagelist.add(new MyPageInfoDto(getResources().getString(R.string.mypage_find_friend), "", "", "" , 9));

		adap = new MyPageListAdapter(MypageActivity.this, mypagelist); 

		mMylist.setAdapter(adap);

		mMylist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {

				curr_item_pos = position;

				int id = adap.getItem(position).ID;

				LogUtil.D("MyPage Idx = " + id);

				if(id == 0){
					Intent i = new Intent(ctx, MyProfileActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 1){
					Intent i = new Intent(ctx, MyPassChange.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 2){
					Intent i = new Intent(ctx, WithdrawActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 3){
					Intent i = new Intent(ctx, ScratchListActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 4){
					Intent i = new Intent(ctx, R2ScratchActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 5){
					Intent i = new Intent(ctx, MyGoldCountActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 6){
					Intent i = new Intent(ctx, MyRewardActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 7){
					Intent i = new Intent(ctx, FriendRecommActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 8){
					try {
						sendKakao();
					} catch (Exception e) {
					}
				}else if(id == 9){
					Intent i = new Intent(ctx, FindFriendActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}else if(id == 10){
					Intent i = new Intent(ctx, MyInfoAddActivity.class );
					i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(i);
				}
			}
		});

	}

//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		DialogUtil.confirm(this, R.string.program_end_msg, new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				DialogUtil.alert.dismiss();
//				finish();
//				endApplication();
//			}
//
//			//MypageActivity.super.onBackPressed();
//
//		});
//	}

	@Override
	protected void onDestroy() {
		mHttpUtil.threadStop();
		
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

		super.onDestroy();
	}
}
