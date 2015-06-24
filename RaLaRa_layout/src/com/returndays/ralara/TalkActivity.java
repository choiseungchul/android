package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.CheckDialog;
import com.returndays.customview.DefaultDialog;
import com.returndays.customview.EndlessScrollListener;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.TalkListAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.dto.TalkListDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class TalkActivity extends BaseActivity{

	private HttpDocument mHttpUtil;
	private HttpDocument getRooms;
	private HttpDocument joinRoom;
	private HttpDocument checkMakeRoom;
	TalkListAdapter mAdap;
	GridView mRoomList;
	TextView mRcount, mScount, mScratchCount, viewtab_all, viewtab_my;
	EndlessScrollListener scrollListener;
	TextViewNanumGothic talk_list_empty;
	int curr_seq = 0;
	int pageNum = 0;
	boolean isLoad = false;
	int ScrollState;
	int totalItem;
	int totalRoomCount = 0;
	int allCount;
	View vv;
	boolean isFirst = false;
	
	int pdleft = 0;
	int pdright = 0;
	int list_flag = 0;
	private OnClickListener onclickListener;
	private LinearLayout tab1;
	private LinearLayout tab2;
	private LinearLayout tab3;
	private LinearLayout tab4;
	private RecyclingImageView tab1_img, tab2_img, tab3_img, tab4_img;
	
	private LinearLayout tab2_bottom_line;
	private Button save_btn;
	com.returndays.customview.TextViewNanumGothic top_talk_sub_title_text;

	public static int instanceCount = 0;
	
	DefaultDialog is_set_profile;
	CheckDialog dialog;
	
	public TalkActivity () {
        super();
        instanceCount++;
        LogUtil.D("test", "TalkActivity() instanceCount: " + instanceCount);
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
		
		mHttpUtil = new HttpDocument(getApplicationContext());
		getRooms = new HttpDocument(this);
		joinRoom = new HttpDocument(getApplicationContext());
		checkMakeRoom = new HttpDocument(getApplicationContext());
		
		initUI();
		initData();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk);
		
		if(!DbUtil.checkDataBase(getApplicationContext())) {
			DbUtil.initDbFile(getApplicationContext());
		}
	}

	private void initData() {
		isLoad = true;

		curr_seq = 0;		

		String url = "";
		if(list_flag == 0){
			url = UrlDef.TALK_LIST;
		}else if(list_flag == 1){

		}else if(list_flag == 2){
			url = UrlDef.TALK_LIST_MY;
		}

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("last_room_seq", String.valueOf(curr_seq));

		getRooms = new HttpDocument(this);
		getRooms.getDocument(url, params, null, new HttpDocument.HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				talk_list_empty.setVisibility(View.GONE);
				
				final ArrayList<TalkListDto> datas = new ArrayList<TalkListDto>();
				// 기본 방3개 입력
				if(list_flag != 2){
					datas.add( new TalkListDto( "", "", String.valueOf(R.drawable.talk_top_icon1), "0", "", "","", "", "F" ));
					datas.add( new TalkListDto( "", "", String.valueOf(R.drawable.talk_top_icon2), "0", "", "","", "", "F" ));
					datas.add( new TalkListDto( "", "", String.valueOf(R.drawable.talk_top_icon3), "0", "", "","", "", "F" ));
					datas.add( new TalkListDto( "", "", String.valueOf(R.drawable.talk_top_icon4), "0", "", "","", "", "F" ));
				}
				
				if(document.select("ResultTable").select("Result").text().equals("true")){

					int size = document.select("ROOM_SEQ").size();
					String total = document.select("ROWCNT").text();
					if(total != null){
						totalRoomCount = Integer.parseInt(total);
					}

					if(totalRoomCount > 0){
						
						for(int k = 0 ; k < size; k++){
							String USER_SEQ = document.select("ROOM_MAKER").get(k).text();
							String KEYWORDS = document.select("KEYWORDS").get(k).text();
							String ROOM_IMG = document.select("ROOM_IMG").get(k).text();
							String ROOM_SEQ = document.select("ROOM_SEQ").get(k).text();
							String EXPIRE_DATE = document.select("EXPIRE_DATE").get(k).text();
							String DEL_FLAG = document.select("DEL_FLAG").get(k).text();
							String USER_CNT = document.select("USER_CNT").get(k).text();
							String D_CNT = document.select("D_CNT").get(k).text();
							
							String room_flag = "N";

							if(!D_CNT.equals("")){
								int d_cnt = Integer.parseInt(D_CNT);
								if(d_cnt >= 5){
									room_flag = "B";
								}
							}
							
							datas.add( new TalkListDto( USER_SEQ, KEYWORDS, ROOM_IMG, "0", EXPIRE_DATE, ROOM_SEQ,DEL_FLAG, USER_CNT, room_flag ));

							if(k == size-1){
								curr_seq = Integer.parseInt(ROOM_SEQ);
								LogUtil.D("curr_seq = " + curr_seq);
							}

							isFirst = true;
							LogUtil.D("all item count = " + allCount);

							isLoad = false;
							LogUtil.D("totalRoomCount = " + totalRoomCount);
						}
					}
					
					if(list_flag == 0){
						LogUtil.D("viewtab_all===");
						viewtab_my.setBackgroundColor(getResources().getColor(R.color.yellow_count_bg));
						viewtab_all.setBackgroundColor(getResources().getColor(R.color.yellow));
					}else if(list_flag == 2){
						LogUtil.D("viewtab_my===");
						viewtab_all.setBackgroundColor(getResources().getColor(R.color.yellow_count_bg));
						viewtab_my.setBackgroundColor(getResources().getColor(R.color.yellow));
					}
					
					// 알모으기 
					if(list_flag == 0){
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
						final HttpDocument httpUtil = new HttpDocument(getApplicationContext());
						httpUtil.getDocument(UrlDef.COMPAIGN, params,null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {
								if(document != null && document.select("ReturnTable") != null) {
									
									final Elements entries = document.select("ReturnTable");
									
									LogUtil.D("entry size = " + entries.size());
									
									// 나이대를 가져옴
									
//									final HttpDocument getUserInfo = new HttpDocument(getApplicationContext());
//									
//									Hashtable<String, String> params = new Hashtable<String, String>();
//									params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
//
//									getUserInfo.getDocument(UrlDef.USERINFO, params, null,  new HttpCallBack() {
//										@Override
//										public void onHttpCallBackListener(Document document) {
//											if(document.select("ResultTable").select("Result").text().equals("true")){
//												if(!document.select("ReturnTable").select("USER_SEQ").text().equals("")){
//													// 나이대 구분
//													String birthday = document.select("BIRTHDAY").text();
//													
//													String user_ages = "";
//													
//													DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
//													try {
//														long user_birth =  sdFormat.parse(birthday.split("T")[0]).getTime();
//														
//														if(user_birth < sdFormat.parse("2005-01-01").getTime() && user_birth >=  sdFormat.parse("1995-01-01").getTime() ){
//															user_ages = "1";
//														}else if(user_birth < sdFormat.parse("1995-01-01").getTime() && user_birth >=  sdFormat.parse("1985-01-01").getTime() ){
//															user_ages = "2";
//														}else if(user_birth < sdFormat.parse("1985-01-01").getTime() && user_birth >=  sdFormat.parse("1975-01-01").getTime() ){
//															user_ages = "3";
//														}else if(user_birth < sdFormat.parse("1975-01-01").getTime() && user_birth >=  sdFormat.parse("1965-01-01").getTime() ){
//															user_ages = "4";
//														}else if(user_birth < sdFormat.parse("1965-01-01").getTime() && user_birth >=  sdFormat.parse("1955-01-01").getTime() ){
//															user_ages = "5";
//														}else if(user_birth < sdFormat.parse("1955-01-01").getTime() && user_birth >=  sdFormat.parse("1945-01-01").getTime() ){
//															user_ages = "6";
//														}else if(user_birth < sdFormat.parse("1945-01-01").getTime() && user_birth >=  sdFormat.parse("1935-01-01").getTime() ){
//															user_ages = "7";
//														}
//													} catch (ParseException e) {
//														e.printStackTrace();
//													}
//													
//													LogUtil.D("user_ages = " + user_ages);
//													
//													List<String> AD_SEQ_LIST = MadUtil.getCategoryList(user_ages);
//													
//													for(int i = 0 ; i < AD_SEQ_LIST.size() ; i++){
//														LogUtil.D("ad_seq = " + AD_SEQ_LIST.get(i));
//													}
//													
//													for(Element entry : entries) {
//														CallAdDto dto = new CallAdDto();
//														MadUtil.autoMappingXmlToObject(entry, dto);
//														if(AD_SEQ_LIST.contains(dto.AD_KIND)){
//															datas.add( new TalkListDto( dto.AD_SEQ, "", dto.TALK_ICON, "0", "", dto.END_ACTION,"", "", "R" ));
//														}
//													}
//													
//													mAdap = new TalkListAdapter(TalkActivity.this, datas);
//													mRoomList.setAdapter(mAdap);
//													
//													mRoomList.setSelection(allCount - 30 < 0 ? 0 : allCount - 30 );
//												}
//											}
//											getUserInfo.threadStop();
//										}
//									}, false);
									
									for(Element entry : entries) {
										CallAdDto dto = new CallAdDto();
										MadUtil.autoMappingXmlToObject(entry, dto);
										if(Integer.parseInt(dto.AD_KIND) > 99 && Integer.parseInt(dto.AD_KIND) < 200)
										datas.add( new TalkListDto( dto.AD_SEQ, "", dto.TALK_ICON, "0", "", dto.END_ACTION,"", "", "R" ));
									}
									
									mAdap = new TalkListAdapter(TalkActivity.this, datas);
									mRoomList.setAdapter(mAdap);
									
									mRoomList.setSelection(allCount - 30 < 0 ? 0 : allCount - 30 );
									
								}
								httpUtil.threadStop();
							}
						},false);
					}else{
						mAdap = new TalkListAdapter(TalkActivity.this, datas);
						mRoomList.setAdapter(mAdap);
						
						mRoomList.setSelection(allCount - 30 < 0 ? 0 : allCount - 30 );
					}
					
				}else{
					Toast.makeText(getApplicationContext(), "Talk목록을 불러오는데 실패하였습니다.", Toast.LENGTH_LONG).show();
					isLoad = false;
					mRoomList.setEmptyView(talk_list_empty);
					talk_list_empty.setVisibility(View.VISIBLE);
				}
				
			}
		}, false);
	}



	private void loadMore(){

		String url = "";

		if(list_flag == 0){
			url = UrlDef.TALK_LIST;
		}else if(list_flag == 1){

		}else if(list_flag == 2){
			url = UrlDef.TALK_LIST_MY;
		}

		if(!isLoad){

			isLoad = true;

			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
			params.put("last_room_seq", String.valueOf(curr_seq));

			getRooms.getDocument(url, params, null, new HttpDocument.HttpCallBack() {

				@Override
				public void onHttpCallBackListener(Document document) {

					talk_list_empty.setVisibility(View.GONE);
					
					if(document.select("Result").text().equals("true")){

						//datas = new ArrayList<TalkListDto>();
						int size = document.select("ROOM_SEQ").size();
						String total = document.select("ROWCNT").text();
						if(total != null){
							totalRoomCount = Integer.parseInt(total);
						}
						if(totalRoomCount > 0){
							
							ArrayList<TalkListDto> datas = new ArrayList<TalkListDto>();
							
							for(int k = 0 ; k < size; k++){
								String USER_SEQ = document.select("ROOM_MAKER").get(k).text();
								String KEYWORDS = document.select("KEYWORDS").get(k).text();
								String ROOM_IMG = document.select("ROOM_IMG").get(k).text();
								String ROOM_SEQ = document.select("ROOM_SEQ").get(k).text();
								String EXPIRE_DATE = document.select("EXPIRE_DATE").get(k).text();
								String DEL_FLAG = document.select("DEL_FLAG").get(k).text();
								String USER_CNT = document.select("USER_CNT").get(k).text();
								String D_CNT = document.select("D_CNT").get(k).text();
								
								String room_flag = "N";

								if(!D_CNT.equals("")){
									int d_cnt = Integer.parseInt(D_CNT);
									if(d_cnt >= 5){
										room_flag = "B";
									}
								}
								
								datas.add( new TalkListDto( USER_SEQ, KEYWORDS, ROOM_IMG, "0", EXPIRE_DATE, ROOM_SEQ,DEL_FLAG, USER_CNT, room_flag ));

								if(k == size-1){
									curr_seq = Integer.parseInt(ROOM_SEQ);
									LogUtil.D("curr_seq = " + curr_seq);
								}

								//mAdap = new TalkListAdapter(TalkActivity.this, datas);
								mAdap.setData(datas);
								//mRoomList.setDa(mAdap);
								mAdap.notifyDataSetChanged();
								isFirst = true;
								LogUtil.D("all item count = " + allCount);
								mRoomList.setSelection(allCount - 30 < 0 ? 0 : allCount - 30 );
								isLoad = false;
								LogUtil.D("totalRoomCount = " + totalRoomCount);
							}
						}
					}else{
						Toast.makeText(getApplicationContext(), "Talk목록을 불러오는데 실패하였습니다.", Toast.LENGTH_LONG).show();
						isLoad = false;
					}
				}
			}, false);
		}
	}
	
	// 
	private void joinRoom(final String seq){
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", seq);
		joinRoom.getDocument(UrlDef.TALK_ROOM_JOIN2, params, null, new HttpDocument.HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				LogUtil.W(document.toString());
				
				if(document.select("ResultTable").select("Result").text().equals("true")){
					String Code = document.select("code").text();
					if(Code.equals("0")){
						// SQLite에 입력되지않았을경우 넣는다.
						if(!checkIsJoin(seq)){
							insertJoinFlag(seq);
						}
						Intent ii = new Intent(getApplicationContext(), TalkInActivity.class);
						ii.putExtra("room_seq", seq);
						ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(ii);
					}else{
//						if(Code.equals("-2002")){
//							Toast.makeText(getApplicationContext(), "알이 부족합니다.", Toast.LENGTH_LONG).show();
//						}else 
						if(Code.equals("-2003")){
							Toast.makeText(getApplicationContext(), "Talk방이 종료되었습니다.", Toast.LENGTH_LONG).show();
						}else{
							// -9999, -8888 에러
							Toast.makeText(getApplicationContext(),  " : 방에 들어갈 수 없습니다.", Toast.LENGTH_LONG).show();
						}
					}
				}else{
					Toast.makeText(getApplicationContext(), "방에 들어갈 수 없습니다..", Toast.LENGTH_LONG).show();
				}
			}

		}, false);
	}

	private void initUI() {
		
		top_talk_sub_title_text = (TextViewNanumGothic)findViewById(R.id.top_talk_sub_title_text);
		top_talk_sub_title_text.setText("알 모으기");

		talk_list_empty = (TextViewNanumGothic)findViewById(R.id.talk_list_empty);
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("방 만들기");
		save_btn.setVisibility(View.VISIBLE);
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", com.returndays.ralara.preference.Setting.getUserSeq(getApplicationContext()));

				checkMakeRoom.getDocument(UrlDef.TALK_MAKE_CHECK, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						
						String Code = document.select("ReturnTable").select("Code").text();
						
						if(Code.equals("0")){
							Intent ii = new Intent(getApplicationContext(), TalkRoomMakeActivity.class);
							ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(ii);
						}else{
							if(Code.equals("-2001")){
								Toast.makeText(getApplicationContext(), "이미 생성한 방이 있습니다.", Toast.LENGTH_LONG).show();
							}else if(Code.equals("-2002")){
								Toast.makeText(getApplicationContext(), "알이 부족합니다. 최소 3개 이상의 알이 필요합니다.", Toast.LENGTH_LONG).show();
							}
						}
					}
				}, false);
			}
		});

		tab2_bottom_line = (LinearLayout)findViewById(R.id.tab2_bottom_line);
		tab2_bottom_line.setBackgroundResource(R.drawable.tab_item_on);

		tab1 = (LinearLayout)findViewById(R.id.tab1);
		tab2 = (LinearLayout)findViewById(R.id.tab2);
		tab3 = (LinearLayout)findViewById(R.id.tab3);
		tab4 = (LinearLayout)findViewById(R.id.tab4);

		tab1_img = (RecyclingImageView)findViewById(R.id.tab1_image);
		tab2_img = (RecyclingImageView)findViewById(R.id.tab2_image);
		tab3_img = (RecyclingImageView)findViewById(R.id.tab3_image);
		tab4_img = (RecyclingImageView)findViewById(R.id.tab4_image);

		tab2_img.setImageResource(R.drawable.tab2_on);

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

		viewtab_all = (TextView)findViewById(R.id.viewtab_all);
		viewtab_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.D("viewtab_all");
				list_flag = 0;
				initData();
			}
		});
		viewtab_my = (TextView)findViewById(R.id.viewtab_my);
		viewtab_my.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.D("viewtab_my");
				list_flag = 2;
				initData();
			}
		});

		mRoomList = (GridView)findViewById(R.id.talk_list);
		mRcount = (TextView)findViewById(R.id.top_r_cnt);
		mScount = (TextView)findViewById(R.id.top_m_cnt);
		mScratchCount = (TextView)findViewById(R.id.top_s_cnt);

		pdleft = mRoomList.getPaddingLeft();
		pdright = mRoomList.getPaddingRight();

		mRoomList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				final TalkListDto data = (TalkListDto)arg0.getItemAtPosition(arg2);

				if(data.TYPE.equals("N")){
					final String seq = data.ROOM_SEQ;
					
					final HttpDocument getMyInfo  = new HttpDocument(getApplicationContext());
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					getMyInfo.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							
							if(document.select("ResultTable").select("Result").text().equals("true")){
								String profile_img = document.select("USER_IMG_PATH").text() + document.select("USER_IMG").text();
								final String is_profile_bonus = document.select("IS_PROFILE_BONUS").text();
								
								if(profile_img.equals("") ){
									is_set_profile = new DefaultDialog(TalkActivity.this);
									is_set_profile.setTitle("프로필 이미지 등록");
									if(is_profile_bonus.equals(""))
										is_set_profile.setMessage("프로필 이미지가 없습니다.\n방입장 전에 프로필 이미지를 등록하시면 알 6개를 드립니다.");
									else 
										is_set_profile.setMessage("프로필 이미지가 없습니다.\n방입장 전에 프로필 이미지를 등록하시겠습니까?");
									is_set_profile.setPositiveButton("이미지 등록", new OnClickListener() {
										@Override
										public void onClick(View v) {
											is_set_profile.dismiss();
											Intent ii = new Intent(getApplicationContext(), MyProfileActivity.class);
											if(is_profile_bonus.equals(""))
												ii.putExtra("egg_bonus", "Y");
											else
												ii.putExtra("egg_bonus", "N");
											ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
											startActivity(ii);
										}
									});
									is_set_profile.setNegativeButton("닫기", new OnClickListener() {
										@Override
										public void onClick(View v) {
											is_set_profile.dismiss();
											
											joinRoom(seq);
											
//											if(data.USER_SEQ.equals(Setting.getUserSeq(getApplicationContext()))){
//												joinRoom(seq);
//											}else{
//												
//												if(Setting.getTalkJoinAlert(getApplicationContext()).equals("") || Setting.getTalkJoinAlert(getApplicationContext()).equals("N")){
//													
//													if(!checkIsJoin(seq)){
//														dialog = new CheckDialog(TalkActivity.this);
//														dialog.setTitle("Talk방 입장");
//														dialog.setMessage("Talk Play방에 입장하기 위해 알2개가 소진됩니다.\n(본인이 만든방 제외)");
//														dialog.setCheckBoxText("이 메세지를 다시 표시하지 않음");
//														dialog.setPositiveButton("방 입장", new OnClickListener() {
//															@Override
//															public void onClick(View v) {
//																if(dialog.getChecked()){
//																	Setting.setTalkJoinAlert(getApplicationContext(), "Y");
//																	dialog.dismiss();
//																	joinRoom(seq);
//																}else{
//																	dialog.dismiss();
//																	joinRoom(seq);					
//																}
//															}
//														});
//														dialog.setNegativeButton("닫기", new OnClickListener() {
//															@Override
//															public void onClick(View v) {
//																dialog.dismiss();
//															}
//														});
//														dialog.show();
//													}else{
//														joinRoom(seq);
//													}
//												}else{
//													joinRoom(seq);
//												}
//											}
										}
									});
									is_set_profile.show();
								}else{
									
									joinRoom(seq);
									
//									if(data.USER_SEQ.equals(Setting.getUserSeq(getApplicationContext()))){
//										joinRoom(seq);
//									}else{
//										
//										if(Setting.getTalkJoinAlert(getApplicationContext()).equals("") || Setting.getTalkJoinAlert(getApplicationContext()).equals("N")){
//											
//											if(!checkIsJoin(seq)){
//												dialog = new CheckDialog(TalkActivity.this);
//												dialog.setTitle("Talk방 입장");
//												dialog.setMessage("Talk Play방에 입장하기 위해 알2개가 소진됩니다.\n(본인이 만든방 제외)");
//												dialog.setCheckBoxText("이 메세지를 다시 표시하지 않음");
//												dialog.setPositiveButton("방 입장", new OnClickListener() {
//													@Override
//													public void onClick(View v) {
//														if(dialog.getChecked()){
//															Setting.setTalkJoinAlert(getApplicationContext(), "Y");
//															dialog.dismiss();
//															joinRoom(seq);
//														}else{
//															dialog.dismiss();
//															joinRoom(seq);					
//														}
//													}
//												});
//												dialog.setNegativeButton("닫기", new OnClickListener() {
//													@Override
//													public void onClick(View v) {
//														dialog.dismiss();
//													}
//												});
//												dialog.show();
//											}else{
//												joinRoom(seq);
//											}
//										}else{
//											joinRoom(seq);
//										}
//									}
								}
							}
							
							getMyInfo.threadStop();
						}
					}, false);
					
					
				}else if(data.TYPE.equals("F")){
					if(arg2 == 0){
						// 랭킹
						Intent i = new Intent(getApplicationContext(),  RankingActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(i);
					}else if(arg2 == 1){
						//이벤트
						Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						i.putExtra("url", UrlDef.EVENT_URL);
						i.putExtra("title", "이벤트");
						startActivity(i);
					}else if(arg2 == 2){
						// 당첨후기
						Uri uri = Uri.parse(UrlDef.FB_DANG_URL);
						Intent it  = new Intent(Intent.ACTION_VIEW,uri);
						startActivity(it);
					}else if(arg2 == 3){
						// 도움말
						Intent i = new Intent(getApplicationContext(),  SettingWebActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						i.putExtra("url", UrlDef.FAQ_URL);
						i.putExtra("title", "Help");
						startActivity(i);
						
					}
				}else if(data.TYPE.equals("R")){
					
					Intent i = new Intent(getApplicationContext(),  CallEndActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					i.putExtra("url", data.ROOM_SEQ);
					i.putExtra("ad_seq", data.USER_SEQ);
					i.putExtra("index", "0");
					startActivity(i);
				}else if(data.TYPE.equals("B")){
					if(data.USER_SEQ.equals(Setting.getUserSeq(getApplicationContext()))){
						joinRoom(data.ROOM_SEQ);
					}else{
						Toast.makeText(getApplicationContext(), "신고횟수를 초과하여 입장이 제한된 방입니다.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});

		mRoomList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				ScrollState = scrollState;
			}

			@SuppressLint("NewApi")
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

//				if(ScrollState == SCROLL_STATE_TOUCH_SCROLL){
//					mRoomList.setPadding(pdleft, 0, pdright, 0);
//				}
				
				
//				if(firstVisibleItem == 0){
//					mRoomList.setPadding(pdleft, pdright,pdright, 0);
//				}else{
//					
//				}

				//&& totalItemCount <= totalRoomCount
				//&& firstVisibleItem > 11
				if( totalItemCount == firstVisibleItem + visibleItemCount  && totalRoomCount != 0){


					//					LogUtil.D("======================grid bottom ================");
					//					LogUtil.D("======================ISLOAD : " +isLoad+" ================");

					if(!isLoad && ScrollState != SCROLL_STATE_IDLE){
						LogUtil.D("loadMOre................");						
						loadMore();

						allCount  = totalItemCount;
					}
				}
			}
		});
	}

	private boolean checkIsJoin(String seq){
		int count=0;
		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		Cursor c = db.rawQuery("select count(*) AS CNT from talk_room where ROOM_SEQ = " + seq, null);
		
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
		value.put("ROOM_SEQ", seq);
		value.put("REG_DATE", "datetime()");
		
		long id = db.insert("talk_room", "ROOM_SEQ, REG_DATE", value);
		if(id > 0){
			Cursor c = db.rawQuery("select count(*) AS CNT from talk_room where ROOM_SEQ = " + seq, null);
			if(c.moveToFirst()){
				int count = c.getInt(0);
				LogUtil.D("count = " + count);
			}
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
//				//TalkActivity.super.onBackPressed();
//			}
//		});
//	}
	
	@Override
	protected void onDestroy() {
		getRooms.threadStop();
		mHttpUtil.threadStop();
		joinRoom.threadStop();
		checkMakeRoom.threadStop();
		
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
