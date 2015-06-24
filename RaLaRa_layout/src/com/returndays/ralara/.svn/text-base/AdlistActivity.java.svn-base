package com.returndays.ralara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.customview.UserTypeDialog;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.AdSimpleListAdapter;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.AdSimpleDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.service.AdDownloadService;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.util.StringUtil;

public class AdlistActivity extends BaseActivity{

	LinearLayout tab1, tab2, tab3, tab4;
	OnClickListener onclickListener;
	LinearLayout tab_slide, mRightBtns;
	
	RecyclingImageView tab_slide_seek, top_r_cnt_img, top_m_cnt_img, top_s_cnt_img;
	com.returndays.customview.TextViewNanumGothic top_sub_title_text; 
	
	ListView mADList, mADViewList;
	LinearLayout btn_goto_r, btn_goto_m, btn_goto_s;
	TextView mRcount, mScount, mScratchCount, mMarqeeText;
	AdSimpleListAdapter mAdap;
	HttpDocument mHttpUtil, getCounts, getMarqueeText; 
	boolean isFirst = true;
	String marqUrl = null;
	String marqAct = null;
	
	RecyclingImageView go_reward_btn, go_my_scratch_btn, marq_text_icon;
	RecyclingImageView tab1_img, tab2_img, tab3_img, tab4_img;
	int curr_pos = 0;
	private LinearLayout tab1_bottom_line;
	TextView adlist_empty_text;

	public static int instanceCount = 0;
	
	public AdlistActivity () {
        super();
        instanceCount++;
        LogUtil.D("test", "AdlistActivity() instanceCount: " + instanceCount);
	}
	@Override
    protected void finalize() throws Throwable {
              super.finalize();
              instanceCount--;
              LogUtil.D("test", "finalize() instanceCount: " + instanceCount);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adlist);
		getCounts 		= new HttpDocument(this);
		getMarqueeText 	= new HttpDocument(this);
		mHttpUtil 		= new HttpDocument(this);

		if(getIntent().getExtras() != null){
			String isFistLogin = getIntent().getExtras().getString("firstlogin");
			if(isFistLogin != null){
				if(isFistLogin.equals("YES")){
					
					String nickname = getIntent().getExtras().getString("nickname");
					
					DialogUtil.confirm(AdlistActivity.this, "랄라라", String.format(getString(R.string.goto_friend_recomm) , nickname ) , new OnClickListener(){
						@Override
						public void onClick(View v) {
							DialogUtil.alert.dismiss();							
							overridePendingTransition(0, 0);
							startSingTopActivity(FriendRecommActivity.class);
						}
					});
				}
			}
		}
		
//		 튜토리얼
		if(Setting.getIsTutorialView(getApplicationContext()).equals("")){
			Intent ii = new Intent(getApplicationContext(), TutorialActivity.class);
			ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			ii.putExtra("act","adlist");
			startActivity(ii);
		}
		
		// 사용자 설정
		if(Setting.getUserType(getApplicationContext()).equals("")){
			UserTypeDialog dialog = new UserTypeDialog(AdlistActivity.this);
			dialog.show();
		}
	
		MadUtil.startAlarmService(getApplicationContext(), AdDownloadService.class, Define.AD_DOWN_SERVICE_ID, Define.AD_DOWN_INTERVAL_TIME, 10);
		//initUI();
		//initData();
	}


	@Override
	protected void onResume()
	{
		LogUtil.D("on resume..");
		super.onResume();
		initUI();
		initData();
	}


	private void initData() {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		getCounts.getDocument(UrlDef.GET_COUNTS, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("Result").text().equals("true")){
					String egg = document.select("EGG").text();
					String gold = document.select("GOLD").text();
					String scratch = document.select("SCRATCH").text();

					mRcount.setText(egg + " 개");
					mScount.setText(StringUtil.getFormatNumber(gold) + " 골드");
					mScratchCount.setText(scratch + " 장");

				}else{
					mRcount.setText("0 개");
					mScount.setText("0 골드");
					mScratchCount.setText("0 장");
				}

			}
		}, false);

		Hashtable<String, String> params2 = new Hashtable<String, String>();
		params2.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttpUtil.getDocument(UrlDef.GET_AD02, params2, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				Elements entries = document.select("ReturnTable");
				Elements entries2 = document.select("DataTable");
				Elements entries3 = document.select("Table2");

				//LogUtil.I("return table " + entries.size());
				//LogUtil.I("data table : " + entries2.size());
				//LogUtil.I("data table : " + entries3.size());

				ArrayList<AdSimpleDto> view_stat0_datas = new ArrayList<AdSimpleDto>();
				ArrayList<AdSimpleDto> view_stat1_datas = new ArrayList<AdSimpleDto>();
				ArrayList<AdSimpleDto> view_stat2_datas = new ArrayList<AdSimpleDto>();
				ArrayList<AdSimpleDto> datas = new ArrayList<AdSimpleDto>();

				for(Element entry : entries) {
					AdSimpleDto dto = new AdSimpleDto();
					MadUtil.autoMappingXmlToObject(entry, dto);
					view_stat0_datas.add(dto);

				}
				for(Element entry : entries2) {
					AdSimpleDto dto = new AdSimpleDto();
					MadUtil.autoMappingXmlToObject(entry, dto);
					view_stat1_datas.add(dto);

				}
				for(Element entry : entries3) {
					AdSimpleDto dto = new AdSimpleDto();
					MadUtil.autoMappingXmlToObject(entry, dto);
					view_stat2_datas.add(dto);

				}

				if(view_stat0_datas.size() > 0){
					view_stat0_datas.get(0).IS_GROUP_TYPE = "1";
				}
				if(view_stat1_datas.size() > 0){
					view_stat1_datas.get(0).IS_GROUP_TYPE = "2";
				}
				if(view_stat2_datas.size() > 0){
					view_stat2_datas.get(0).IS_GROUP_TYPE = "3";
				}

				datas.addAll(view_stat0_datas);
				datas.addAll(view_stat1_datas);
				datas.addAll(view_stat2_datas);

				mAdap = new AdSimpleListAdapter(AdlistActivity.this, datas, new OnClickListener() {
					@Override
					public void onClick(View v) {}
				});

				mADList.setAdapter(mAdap);
				mADList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						HashMap<String, String> mDatamap;
						mDatamap = new HashMap<String, String>();

						mDatamap.put("AD_SEQ", ((AdSimpleDto) arg0.getItemAtPosition(arg2)).AD_SEQ);
						mDatamap.put("AD_KIND", ((AdSimpleDto) arg0.getItemAtPosition(arg2)).AD_KIND);
						mDatamap.put("END_ACTION", ((AdSimpleDto) arg0.getItemAtPosition(arg2)).END_ACTION);
						mDatamap.put("AD_DESC", ((AdSimpleDto) arg0.getItemAtPosition(arg2)).AD_DESC);
						mDatamap.put("AD_MOVIE", ((AdSimpleDto) arg0.getItemAtPosition(arg2)).AD_MOVIE);

						Intent i = new Intent(getApplicationContext(),  AdAlertActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						i.putExtra("data", mDatamap);

						startActivity(i);
						//finish();

					}
				});
				
				if(datas.size() == 0){
					mADList.setEmptyView(adlist_empty_text);
					adlist_empty_text.setVisibility(View.VISIBLE);
				}
			}
		}, true);

		Hashtable<String, String> getMarq = new Hashtable<String, String>();
		getMarq.put("token", Setting.getToken(getApplicationContext()));
		getMarqueeText.getDocument(UrlDef.ADLIST_MARQUEE_TEXT, getMarq, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("ResultTable").select("Result").text().equals("true")){
					if(document.select("FLAG").text().equals("1")){
						mMarqeeText.setText(document.select("TITLE").text());
						marqUrl = document.select("LOAD_URL").text();
						mMarqeeText.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 하단광고
								Intent ii = new Intent(Intent.ACTION_VIEW, Uri.parse(marqUrl));
								ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								startActivity(ii);
							}
						});
					}else if(document.select("FLAG").text().equals("2")){
						mMarqeeText.setText(document.select("TITLE").text());
						marqAct = document.select("ACTIVITY").text();
						mMarqeeText.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 하단광고
								Class<?> cls;
								try {
									cls = Class.forName("com.returndays.ralara." + marqAct);
									Intent i = new Intent(getApplicationContext(),  cls);
									i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(i);
								} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						});
					}else if(document.select("FLAG").text().equals("3")){
						mMarqeeText.setText(document.select("TITLE").text());
					}else if(document.select("FLAG").text().equals("4")){
						// 웹뷰 다이어로그 띄움
						mMarqeeText.setText(document.select("TITLE").text());
						
						
					}
					mMarqeeText.setEnabled(true);
					mMarqeeText.setSelected(true);
				}else{
					mMarqeeText.setText("네트워크 오류로 광고를 불러올 수 없습니다.");
					mMarqeeText.setSelected(true);
				}
			}
		}, false);
	}

	private void initUI() {

		btn_goto_r = (LinearLayout)findViewById(R.id.btn_goto_r);
		btn_goto_r.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), R2ScratchActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		btn_goto_m = (LinearLayout)findViewById(R.id.btn_goto_m);
		btn_goto_m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), MyGoldCountActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		btn_goto_s = (LinearLayout)findViewById(R.id.btn_goto_s);
		btn_goto_s.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), ScratchListActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		marq_text_icon = (RecyclingImageView)findViewById(R.id.marq_text_icon);
		
		go_reward_btn = (RecyclingImageView)findViewById(R.id.go_reward_btn);
		go_reward_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), MyRewardActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		go_my_scratch_btn = (RecyclingImageView)findViewById(R.id.go_my_scratch_btn);
		go_my_scratch_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), ScratchListActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		
		adlist_empty_text = (com.returndays.customview.TextViewNanumGothic)findViewById(R.id.adlist_empty_text);

		top_sub_title_text = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text);
		top_sub_title_text.setText("광고");

		top_r_cnt_img = (RecyclingImageView)findViewById(R.id.top_r_cnt_img);
		top_m_cnt_img = (RecyclingImageView)findViewById(R.id.top_m_cnt_img);
		top_s_cnt_img = (RecyclingImageView)findViewById(R.id.top_s_cnt_img);

		tab1_bottom_line = (LinearLayout)findViewById(R.id.tab1_bottom_line);
		tab1_bottom_line.setBackgroundResource(R.drawable.tab_item_on);

		mADList = (ListView)findViewById(R.id.adlist_notview);
		mRcount = (TextView)findViewById(R.id.top_r_cnt);
		mScount = (TextView)findViewById(R.id.top_m_cnt);
		mScratchCount = (TextView)findViewById(R.id.top_s_cnt);
		mMarqeeText = (TextView)findViewById(R.id.marq_text);
		mMarqeeText.setEnabled(false);

		mRightBtns = (LinearLayout)findViewById(R.id.right_btns);

		tab1 = (LinearLayout)findViewById(R.id.tab1);
		tab2 = (LinearLayout)findViewById(R.id.tab2);
		tab3 = (LinearLayout)findViewById(R.id.tab3);
		tab4 = (LinearLayout)findViewById(R.id.tab4);

		tab1_img = (RecyclingImageView)findViewById(R.id.tab1_image);
		tab2_img = (RecyclingImageView)findViewById(R.id.tab2_image);
		tab3_img = (RecyclingImageView)findViewById(R.id.tab3_image);
		tab4_img = (RecyclingImageView)findViewById(R.id.tab4_image);

		tab1_img.setImageResource(R.drawable.tab1_on);

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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		DialogUtil.confirm(this, R.string.program_end_msg, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogUtil.alert.dismiss();
				finish();
				endApplication();
				//AdlistActivity.super.onBackPressed();
			}
		});
	}

	@Override
	protected void onDestroy() {
		getCounts.threadStop();
		getMarqueeText.threadStop();
		mHttpUtil.threadStop();
		
		// 리사이클
		if(tab_slide_seek != null){
			tab_slide_seek.setImageBitmap(null);
		}
		if(top_r_cnt_img != null){
			top_r_cnt_img.setImageBitmap(null);
		}
		if(top_m_cnt_img != null){
			top_m_cnt_img.setImageBitmap(null);
		}
		if(top_s_cnt_img != null){
			top_s_cnt_img.setImageBitmap(null);
		}
		if(go_reward_btn != null){
			go_reward_btn.setImageBitmap(null);
		}
		if(go_my_scratch_btn != null){
			go_my_scratch_btn.setImageBitmap(null);
		}
		if(marq_text_icon != null){
			marq_text_icon.setImageBitmap(null);
		}
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
