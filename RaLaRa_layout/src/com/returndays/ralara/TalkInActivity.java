package com.returndays.ralara;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.AppealDialog;
import com.returndays.customview.DefaultDialog;
import com.returndays.customview.FixWidthImageView;
import com.returndays.customview.PresentEggDialog;
import com.returndays.customview.ProfileDialog;
import com.returndays.customview.SingoDialog;
import com.returndays.customview.SquareImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.CommentListAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CommentDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.util.StringUtil;

public class TalkInActivity extends BaseActivity{

	LinearLayout back_btn ,comm_list_wrap, talk_sticker_cont;
	Button comm_write;
	TextView  like_btn, reply_btn;
	TextView talker_id, talk_regdate, talk_title, talk_content, talk_keyword, talk_timer;
	FixWidthImageView talk_room_img;
	SquareImageView talker_image;

	RecyclingImageView talk_push_on, talk_push_off, other_room_push,
	talk_stk_1, talk_stk_2, talk_stk_3, talk_stk_4, talk_stk_5, 
	room_appeal, room_singo, room_delete;
	HttpDocument getRoomInfo, sendComment, getCommList, setPushAlarm, sendLike, getRoomMakerInfo, getPushStat;
	String room_seq;
	EditText comm_text;
	View listViewHeader;
	ListView comm_list;
	LayoutInflater inflater;
	CommentListAdapter mAdap;
	long expired;
	Timer exp_timer;
	TimerTask exp_timer_task;
	SimpleDateFormat timer_format = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);
	InputMethodManager imm;
	OnClickListener stk_icon_click;
	boolean isLiked;
	private static TalkInActivity _instance;
	DefaultDialog dialog;
	boolean isOneMinuteAlert = false;
	String room_maker = null;
	ProfileDialog prf_dialog;
	List<String> focusId = new ArrayList<String>(); 

	private AudioManager aManager;
	private MediaPlayer mediaPlayer;

	OnClickListener presentListener;

	public static TalkInActivity getInstance(){
		return _instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_in);

		_instance = this;

		Bundle b = getIntent().getExtras();

		room_seq = (String) b.get("room_seq");

		LogUtil.D("room_seq = " + room_seq);

		inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

		//CMgr = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

		getRoomInfo = new HttpDocument(getApplicationContext());
		getCommList = new HttpDocument(getApplicationContext());
		sendComment = new HttpDocument(getApplicationContext());
		setPushAlarm = new HttpDocument(getApplicationContext());
		sendLike = new HttpDocument(getApplicationContext());
		getRoomMakerInfo = new HttpDocument(getApplicationContext());
		getPushStat = new HttpDocument(getApplicationContext());

		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		initUI();
		initData();

	}

	public void reloadCommnet(boolean flag){
		if(flag){
			moreComm(1);
		}else{
			moreComm(0);
		}
	}

	Handler timerHandler = new Handler();

	private void UpdateTimerView(){
		timerHandler.post(timerRun);
	}

	final Runnable timerRun = new Runnable() {
		@Override
		public void run() {
			if(expired < 0){
				talk_timer.setText("timeout");
				exp_timer.cancel();
				exp_timer = null;
				dialog = new DefaultDialog(TalkInActivity.this);
				dialog.setTitle("Talk방 알림");
				dialog.setMessage("Talk가 종료되었습니다.");
				dialog.setPositiveButton("확인", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
						onBackPressed();
					}
				});
				dialog.show();
			}else if(expired < (62 * 1000)){
				try{
					expired -= 1000;
					Calendar c = Calendar.getInstance(Locale.KOREA);
					c.setTimeInMillis(expired);
					talk_timer.setText(StringUtil.getTimeString(expired));
					if(isOneMinuteAlert == false){
						isOneMinuteAlert = true;
						talk_timer.setBackgroundResource(R.drawable.talk_time_back_alert);
						dialog = new DefaultDialog(TalkInActivity.this);
						dialog.setTitle("Talk방 알림");
						dialog.setMessage("Talk방이 1분 이내로 종료됩니다.");
						dialog.setPositiveButton("확인", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				}catch(Exception e){					
				}
			}else{
				expired -= 1000;
				Calendar c = Calendar.getInstance(Locale.KOREA);
				c.setTimeInMillis(expired);
				talk_timer.setText(StringUtil.getTimeString(expired));
			}
		}
	};

	@SuppressLint("SimpleDateFormat")
	private void initData() {

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", room_seq);

		LogUtil.D(Setting.getUserSeq(getApplicationContext()) + "=> userseq ");
		LogUtil.D(room_seq + "=> room_seq ");

		getRoomInfo.getDocument(UrlDef.TALK_ROOM_INFO, params, null, new HttpDocument.HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				if(document.select("ResultTable").select("Result").text().equals("true")){
					String KEYWORDS = document.select("KEYWORDS").text();
					String EXPIRE_DATE = document.select("EXPIRE_DATE").text();
					String REG_DATE = document.select("REG_DATE").text();
					String USER_IMAGE = document.select("USER_IMAGE").text();
					final String TITLE_IMAGE = document.select("TITLE_IMAGE").text();
					String USER_NICKNAME = document.select("USER_NICKNAME").text();
					String REPLY_CNT = document.select("REPLY_CNT").text();
					String LIKE_CNT = document.select("LIKE_CNT").text();
					String CONTENTS = document.select("CONTENTS").text();
					String DEL_FLAG = document.select("DEL_FLAG").text();
					String TITLE = document.select("TITLE").text();
					String SEX = document.select("SEX").text();
					String D_CNT = document.select("D_CNT").text();
					room_maker = document.select("ROOM_MAKER").text();


					if(DEL_FLAG.equals("N")){

						AQuery aq = new AQuery(getApplicationContext());
						ImageOptions opt = new ImageOptions();
						opt.round = 60;
						if(!USER_IMAGE.equals("")){
							aq.id(talker_image).image(USER_IMAGE, opt);
						}else{
							if(SEX.equals("M")){
								talker_image.setImageResource(R.drawable.man_ico);
							}else if(SEX.equals("F")){
								talker_image.setImageResource(R.drawable.woman_ico);
							}
						}
						aq.id(talk_room_img).progress(R.id.talk_room_img_loader).image(TITLE_IMAGE);
						
						CONTENTS = CONTENTS.replace("::", "\n");
						
						talk_content.setText(CONTENTS);
						talker_id.setText(USER_NICKNAME);
						talk_title.setText(TITLE);
						talk_keyword.setText("#" + KEYWORDS);
						like_btn.setText(LIKE_CNT);
						reply_btn.setText(REPLY_CNT);
						
						if(room_maker.equals(Setting.getUserSeq(getApplicationContext()))){
							room_delete.setVisibility(View.VISIBLE);
							room_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									final DefaultDialog dialog = new DefaultDialog(TalkInActivity.this);
									dialog.setTitle("Talk 방 삭제");
									dialog.setMessage("확인 버튼을 누르시면 Talk 방의 모든 정보가 사라집니다.\n계속 하시겠습니까?");
									dialog.setNegativeButton("취소",new OnClickListener() {
										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});
									dialog.setPositiveButton("확인", new OnClickListener() {
										@Override
										public void onClick(View v) {
											final HttpDocument deleteRoom = new HttpDocument(getApplicationContext());
											Hashtable<String, String> params = new Hashtable<String, String>();
											params.put("room_seq",room_seq);
											deleteRoom.getDocument(UrlDef.ROOM_DELETE, params, null, new HttpCallBack() {
												@Override
												public void onHttpCallBackListener(Document document) {
													if(document.select("ResultTable").select("Result").text().equals("true")){
														dialog.dismiss();
														finish();
														Toast.makeText(getApplicationContext(), "Talk 방이 삭제되었습니다", Toast.LENGTH_LONG).show();
													}else{
														dialog.dismiss();
														Toast.makeText(getApplicationContext(), "Talk 방이 삭제되지 않았습니다", Toast.LENGTH_LONG).show();
													}
													deleteRoom.threadStop();
												}
											}, false);
										}
									});
									dialog.show();
								}
							});
							
						}else{
							
							// 운영자방은 뺸다
							if(!room_seq.equals("516")){
								
								room_singo.setVisibility(View.VISIBLE);
								room_singo.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 신고
										final SingoDialog singo = new SingoDialog(TalkInActivity.this);
										singo.setPositiveButton("신고", new OnClickListener() {
											@Override
											public void onClick(View v) {
												final HttpDocument singoRoom = new HttpDocument(getApplicationContext());
												Hashtable<String, String> params = new Hashtable<String, String>();
												params.put("room_seq",room_seq);
												params.put("content",singo.getEditText());
												params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
												
												singoRoom.getDocument(UrlDef.ROOM_SINGO, params, null, new HttpCallBack() {
													@Override
													public void onHttpCallBackListener(Document document) {
														
														if(document.select("ResultTable").select("Result").text().equals("true")){
															String Code = document.select("Code").text();
															if(Code.equals("0")){
																Toast.makeText(getApplicationContext(), "신고가 접수되었습니다.", Toast.LENGTH_LONG).show();
															}else if(Code.equals("-2001")){
																Toast.makeText(getApplicationContext(), "이미 신고하였습니다.", Toast.LENGTH_LONG).show();
															}
														}else{
															Toast.makeText(getApplicationContext(), "일시적인 오류입니다.", Toast.LENGTH_LONG).show();
														}
														
														singo.dismiss();
														
														singoRoom.threadStop();
													}
												}, false);
											}
										});
										singo.show();
									}
								});
							}
						}
						
						if(!D_CNT.equals("")){
							int d_cnt = Integer.parseInt(D_CNT);
							if(d_cnt > 0){
								room_appeal.setVisibility(View.VISIBLE);
								room_appeal.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										final AppealDialog appeal = new AppealDialog(TalkInActivity.this);
										
										final HttpDocument getappealRoom = new HttpDocument(getApplicationContext());
										Hashtable<String, String> params = new Hashtable<String, String>();
										params.put("room_seq",room_seq);
										params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
										getappealRoom.getDocument(UrlDef.ROOM_GET_APPEAL, params, null, new HttpCallBack() {
											@Override
											public void onHttpCallBackListener(Document document) {
												
												if(document.select("ResultTable").select("Result").text().equals("true")){
													appeal.setEditText(document.select("USER_CONTENT").text());
													appeal.show();
												}
												getappealRoom.threadStop();
												
												appeal.setPositiveButton("이의제기", new OnClickListener() {
													@Override
													public void onClick(View v) {
														final HttpDocument getappealRoom = new HttpDocument(getApplicationContext());
														Hashtable<String, String> params = new Hashtable<String, String>();
														params.put("room_seq",room_seq);
														params.put("content", appeal.getEditText());
														params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
														getappealRoom.getDocument(UrlDef.ROOM_APPEAL, params, null, new HttpCallBack() {
															@Override
															public void onHttpCallBackListener(Document document) {
																if(document.select("ResultTable").select("Result").text().equals("true")){
																	Toast.makeText(getApplicationContext(), "이의제기 내용이 전달되었습니다.", Toast.LENGTH_LONG).show();
																}
																appeal.dismiss();
																getappealRoom.threadStop();
															}
														}, false);
													}
												});
											}
										}, false);
									}
								});
							}
							
							if(d_cnt >= 5){
								// 댓글 좋아요 제한
								OnClickListener listener = new OnClickListener() {
									@Override
									public void onClick(View v) {
										Toast.makeText(getApplicationContext(), "신고접수된 방입니다.", Toast.LENGTH_SHORT).show();
									}
								};
								
								like_btn.setOnClickListener(listener);
								reply_btn.setOnClickListener(listener);
								comm_text.setOnClickListener(listener);
								comm_write.setOnClickListener(listener);
							}
						}

						LogUtil.D("Set UI DATA complete");

						talk_room_img.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {

								Intent ii = new Intent(getApplicationContext(), ImageZoom.class);
								ii.putExtra("url", TITLE_IMAGE);
								ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								startActivity(ii);
							}
						});

						LogUtil.D("date format..");

						// 방생성 날짜 설정
						DateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

						try {

							Date tempDate = sdFormat.parse(REG_DATE.split("T")[0]);
							SimpleDateFormat sdf = new SimpleDateFormat("M월 dd일");
							talk_regdate.setText(sdf.format(tempDate));

						} catch (ParseException e) {

							e.printStackTrace();
						}

						// 남은시간 설정
						DateFormat expire_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);

						try {

							Date tempDate = expire_date.parse(EXPIRE_DATE.replace("T", " "));
							//							LogUtil.D("시간 : " +  tempDate.getHours());
							//							LogUtil.D("분 : " +  tempDate.getMinutes());
							//							LogUtil.D("초 : " +  tempDate.getSeconds());
							//
							//							LogUtil.D("시간 : " +  Calendar.getInstance(Locale.KOREA).getTime().getHours());
							//							LogUtil.D("분 : " +  Calendar.getInstance(Locale.KOREA).getTime().getMinutes());
							//							LogUtil.D("초 : " +  Calendar.getInstance(Locale.KOREA).getTime().getSeconds());

							long time = tempDate.getTime() - (Calendar.getInstance(Locale.KOREA).getTime().getTime());

							expired = time;

							LogUtil.D("expire time = " + expired);

							if(expired < 0){
								Toast.makeText(getApplicationContext(), "이미 종료된 방입니다.", Toast.LENGTH_LONG).show();
								finish();
								return;

							}

							exp_timer_task = new TimerTask() {

								@Override
								public void run() {

									UpdateTimerView();
								}
							};

							exp_timer = new Timer();

							talk_timer.setText(StringUtil.getTimeString(expired));
							exp_timer.schedule(exp_timer_task, 1000, 1000);

						} catch (ParseException e) {

							e.printStackTrace();
						}

						LogUtil.D("before addheader..");

						try{
							comm_list.addHeaderView(listViewHeader);
							//comm_list.addFooterView(listViewFooter);
						}catch(Exception e){

						}

						LogUtil.D("after addheader..");

						listViewHeader.setOnClickListener(null);

						getCommnets();

						Hashtable<String, String> pushstatparam = new Hashtable<String, String>();
						pushstatparam.put("user_seq", Setting.getUserSeq(getApplicationContext()));
						pushstatparam.put("room_seq", room_seq);
						getPushStat.getDocument(UrlDef.TALK_ROOM_GET_PUSH_STAT, pushstatparam, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {
								if(document.select("ResultTable").select("Result").text().equals("true")){
									String IS_PUSH = document.select("IS_PUSH").text();
									if(IS_PUSH.equals("Y")){
										talk_push_off.setVisibility(View.GONE);
										talk_push_on.setVisibility(View.VISIBLE);
									}else if(IS_PUSH.equals("N")){
										talk_push_on.setVisibility(View.GONE);
										talk_push_off.setVisibility(View.VISIBLE);
									}
								}
							}
						}, false);

					}else{

					}

				}

			}
		}, false);

	}


	private void getCommnets(){

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", room_seq);

		getCommList.getDocument(UrlDef.TALK_REPLY_LIST, params,null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				if(document.select("ResultTable").select("Result").text().equals("true")){

					Elements entries = document.select("ReturnTable");

					ArrayList<CommentDto> datas = new ArrayList<CommentDto>();

					for(Element entry : entries) {
						CommentDto dto = new CommentDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
						if(entry.select("USER_SEQ").text().equals(Setting.getUserSeq(getApplicationContext()))){
							if(entry.select("IS_STICKER").text().equals("Y")){
								isLiked = true;
							}
						}
					}

					// 댓글
					mAdap = new CommentListAdapter(TalkInActivity.this, datas, new OnClickListener() {
						@Override
						public void onClick(View v) {
							TextView tv = (TextView)v;
							String Nick = tv.getText().toString();
							String curr_txt = comm_text.getText().toString();
							if(curr_txt.length() > 0){
								comm_text.setText(curr_txt + " @" + Nick + " ");
							}else{
								comm_text.setText(curr_txt + "@" + Nick + " ");
							}
							comm_list.setSelectionFromTop(comm_list.getAdapter().getCount() - 1, comm_list.getScrollY());
							comm_text.setInputType(1);
							imm.showSoftInput(comm_text, InputMethodManager.SHOW_IMPLICIT);
							comm_text.requestFocus();
							comm_text.setSelection(comm_text.length());

							for(int i = 0 ; i < focusId.size() ; i++){
								if(focusId.get(i).equals("@" + Nick)){
									focusId.remove(i);
									break;
								}
							}
							focusId.add("@" + Nick);
						}
					});
					comm_list.setAdapter(mAdap);
					mAdap.notifyDataSetChanged();
					getRoomCount();

				}
			}
		}, false);
	}

	private void getRoomCount(){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", room_seq);

		getRoomInfo.getDocument(UrlDef.TALK_ROOM_INFO, params,null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				if(document.select("ResultTable").select("Result").text().equals("true")){
					String REPLY_CNT = document.select("REPLY_CNT").text();
					String LIKE_CNT = document.select("LIKE_CNT").text();

					like_btn.setText(LIKE_CNT);
					reply_btn.setText(REPLY_CNT);
				}
			}
		}, false);
	}

	private void moreComm(final int type){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", room_seq);

		getCommList.getDocument(UrlDef.TALK_REPLY_LIST, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				if(document.select("ResultTable").select("Result").text().equals("true")){
					Elements entries = document.select("ReturnTable");
					ArrayList<CommentDto> datas = new ArrayList<CommentDto>();
					for(Element entry : entries) {
						CommentDto dto = new CommentDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);

						if(entry.select("USER_SEQ").text().equals(Setting.getUserSeq(getApplicationContext()))){
							if(entry.select("IS_STICKER").text().equals("Y")){
								isLiked = true;
							}
						}
					}

					// 댓글
					mAdap.setData(datas);
					mAdap.notifyDataSetChanged();
					comm_list.setSelectionFromTop(comm_list.getAdapter().getCount() - 1, comm_list.getScrollY());
					getRoomCount();

					// 푸시로 온경우
					if(type == 1){
						aManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

						switch(aManager.getRingerMode())
						{
						case AudioManager.RINGER_MODE_SILENT: 
							break; 
						case AudioManager.RINGER_MODE_VIBRATE:  
							break;  
						case AudioManager.RINGER_MODE_NORMAL: 
							// 소리나는부분
							if(mediaPlayer == null){
								mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.chuk);
								mediaPlayer.start();
								mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {

										mediaPlayer.release();
										mediaPlayer = null;
									}
								});
							}else{
								mediaPlayer.stop();
								mediaPlayer.release();
								mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.chuk);
								mediaPlayer.start();
								mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {

										mediaPlayer.release();
										mediaPlayer = null;
									}
								});
							}

							break;
						};
					}

				}
			}
		}, false);
	}

	private Handler handle = new Handler();
	Runnable r = new Runnable() {

		@Override
		public void run() {

			otherRoomPushReload();
		}
	};

	private void initUI() {

		other_room_push = (RecyclingImageView)findViewById(R.id.other_room_push);

		room_appeal = (RecyclingImageView)findViewById(R.id.room_appeal);
		room_singo = (RecyclingImageView)findViewById(R.id.room_singo);
		room_delete = (RecyclingImageView)findViewById(R.id.room_delete);
		

		// 상단 붙일 LISTVIEW
		comm_list = (ListView)findViewById(R.id.comm_list);

		comm_list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,	int position, long arg3) {
				LogUtil.D("position = " + position);
				if( position != 0 && position != comm_list.getCount() ){
					final CommentDto data = (CommentDto)arg0.getItemAtPosition(position);
					if(data.USER_SEQ.equals(Setting.getUserSeq(getApplicationContext()))){
						final String[] gselect = {"복사하기","삭제하기"}; 
						new AlertDialog.Builder(TalkInActivity.this)
						.setItems(gselect, new DialogInterface.OnClickListener(){
							@SuppressWarnings("deprecation")
							@SuppressLint("NewApi")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which == 0){
									try{
										int currentapiVersion = android.os.Build.VERSION.SDK_INT;
										if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
											android.content.ClipboardManager CMgr = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
											CMgr.setText(data.CONTENTS);
										} else {
											android.text.ClipboardManager CMgr = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
											CMgr.setText(data.CONTENTS);
										}
									}catch (Exception e) {
										e.printStackTrace();
									}
								}else if(which == 1){
									deleteComment(data.REPLY_SEQ);
								}
							}
						}).create().show();

					}else{
						final String[] gselect = {"복사하기","신고하기"};  
						new AlertDialog.Builder(TalkInActivity.this)
						.setItems(gselect, new DialogInterface.OnClickListener(){
							@SuppressWarnings("deprecation")
							@SuppressLint("NewApi")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which == 0){
									try{
										int currentapiVersion = android.os.Build.VERSION.SDK_INT;
										if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
											android.content.ClipboardManager CMgr = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
											CMgr.setText(data.CONTENTS);
										} else {
											android.text.ClipboardManager CMgr = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
											CMgr.setText(data.CONTENTS);
										}
									}catch (Exception e) {
										e.printStackTrace();
									}
								}else if(which == 1){
									declareComment(data.REPLY_SEQ);
								}
							}
						}).create().show();
					}
				}
				return false;
			}
		});

		listViewHeader = inflater.inflate(R.layout.talk_in_header, null);
		//listViewFooter = inflater.inflate(R.layout.talk_in_footer, null);

		talk_room_img = (FixWidthImageView)listViewHeader.findViewById(R.id.talk_room_img);
		talker_image = (SquareImageView)listViewHeader.findViewById(R.id.talker_image);
		talker_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				//				LogUtil.D("room_maker => " + room_maker);

				if(room_maker != null){

					//					LogUtil.D("getRoomMakerInfo => " + getRoomMakerInfo.toString());

					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_seq", room_maker);
					getRoomMakerInfo.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							LogUtil.D(document.select("ResultTable").select("Result").text());
							LogUtil.D("rt table is empty = " + document.select("ReturnTable").isEmpty());
							LogUtil.D("rt table size = " + document.select("ReturnTable").size());

							if(document.select("ReturnTable") != null){
								if(document.select("ReturnTable").isEmpty() ||  document.select("ReturnTable").size() == 0){
									LogUtil.D( "" +  document.select("ReturnTable").text());
									Toast.makeText(getApplicationContext(), "탈퇴한 유저 입니다.", Toast.LENGTH_LONG).show();
								}else{
									String USER_IMG = document.select("USER_IMG").text();
									String USER_IMG_PATH = document.select("USER_IMG_PATH").text();
									final String USER_NICKNAME = document.select("USER_NICKNAME").text();
									final String SEX = document.select("SEX").text();
									final String user_image = USER_IMG_PATH + USER_IMG;

									prf_dialog = new ProfileDialog(TalkInActivity.this);
									if(!user_image.equals("")){
										prf_dialog.setImage(USER_IMG_PATH + USER_IMG);
									}else{
										if(SEX.equals("M")){
											prf_dialog.setImageDrawable(getResources().getDrawable(R.drawable.man_ico));
										}else if(SEX.equals("F")){
											prf_dialog.setImageDrawable(getResources().getDrawable(R.drawable.woman_ico));
										}

									}
									prf_dialog.setDesc("[" + USER_NICKNAME + "]");
									if(Setting.getUserSeq(getApplicationContext()).equals(room_maker) == false){
										prf_dialog.setPresentOther(new OnClickListener(){
											@Override
											public void onClick(View v) {
												Hashtable<String, String> params = new Hashtable<String, String>();
												params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
												final HttpDocument doc = new HttpDocument(getApplicationContext());
												doc.getDocument(UrlDef.GET_COUNTS, params, null, new HttpCallBack() {
													@Override
													public void onHttpCallBackListener(Document document) {
														if(document.select("ResultTable").select("Result").text().equals("true")){
															String egg = document.select("EGG").text();
															LogUtil.D("egg = " + egg);

															if( Integer.parseInt(egg) > 0){
																prf_dialog.dismiss();

																final PresentEggDialog presentDialog = new PresentEggDialog(_instance);
																presentDialog.setMaxPresent( Integer.parseInt(egg) );
																presentDialog.setTitle(USER_NICKNAME + "님께 알 선물하기");
																presentDialog.setPresentSubmitEvent(new OnClickListener() {
																	@Override
																	public void onClick(View v) {
																		presentDialog.dismiss();
																		final String amt = presentDialog.getCurrentAmount();
																		Hashtable<String, String> params = new Hashtable<String, String>();
																		params.put("user_seq", Setting.getUserSeq(_instance));
																		params.put("friend_user_seq", room_maker); 
																		params.put("amount", amt);
																		final HttpDocument http = new HttpDocument(_instance);
																		http.getDocument(UrlDef.EGG_GIFT, params, null, new HttpCallBack() {
																			@Override
																			public void onHttpCallBackListener(Document document) {
																				if(document.select("ResultTable").select("Result").text().equals("true")){
																					String Code = document.select("Code").text();
																					if(Code.equals("0")){
																						DialogUtil.alert(_instance, "[" + USER_NICKNAME + "] 님께 알" + amt + "개를 선물하였습니다.");
																					}else{
																						if(Code.equals("-2001")){
																							Toast.makeText(_instance, "자기 자신에게 선물할 수 없습니다.", Toast.LENGTH_LONG).show();
																						}else if(Code.equals("-2002")){
																							Toast.makeText(_instance, "선물할 알이 부족합니다.", Toast.LENGTH_LONG).show();
																						}
																					}
																				}else{
																					Toast.makeText(_instance, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
																				}

																				http.threadStop();
																			}

																		}, true);
																	}
																});

																presentDialog.show();

															}else{
																prf_dialog.dismiss();
																Toast.makeText(getApplicationContext(), "알이 최소 1개 이상이어야 합니다.", Toast.LENGTH_LONG).show();
															}
														}else{
															prf_dialog.dismiss();
															Toast.makeText(getApplicationContext(), "현재 보유하고 있는 알 개수를 가져오지 못하였습니다.", Toast.LENGTH_LONG).show();
														}
														doc.threadStop();
													}
												}, false);
											}
										});
									}else{
										LogUtil.D("roommaker is ME");
									}
									prf_dialog.show();
								}
							}else{
								Toast.makeText(getApplicationContext(), "탈퇴한 유저입니다.", Toast.LENGTH_SHORT).show();
							}

						}

					}, false);

				}
			}
		});

		talker_id = (TextView)listViewHeader.findViewById(R.id.talker_id);

		talk_regdate = (TextView)listViewHeader.findViewById(R.id.talk_regdate);
		talk_title = (TextView)listViewHeader.findViewById(R.id.talk_title);
		talk_content = (TextView)listViewHeader.findViewById(R.id.talk_content);

		// 좋아요 이모티콘
		talk_sticker_cont = (LinearLayout)listViewHeader.findViewById(R.id.talk_sticker_cont);
		talk_stk_1 = (RecyclingImageView)listViewHeader.findViewById(R.id.talk_stk_1);
		talk_stk_2 = (RecyclingImageView)listViewHeader.findViewById(R.id.talk_stk_2);
		talk_stk_3 = (RecyclingImageView)listViewHeader.findViewById(R.id.talk_stk_3);
		talk_stk_4 = (RecyclingImageView)listViewHeader.findViewById(R.id.talk_stk_4);
		talk_stk_5 = (RecyclingImageView)listViewHeader.findViewById(R.id.talk_stk_5);

		stk_icon_click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				final View vv = v;

				if(isLiked){
					Toast.makeText(getApplicationContext(), "이미 좋아요 하셨습니다.", Toast.LENGTH_LONG).show();
					talk_sticker_cont.setVisibility(View.GONE);
				}else{
					String stk_number = "";
					if(v.getId() == R.id.talk_stk_1){
						stk_number = "1";
					}else if(v.getId() == R.id.talk_stk_2){
						stk_number = "2";
					}else if(v.getId() == R.id.talk_stk_3){
						stk_number = "3";
					}else if(v.getId() == R.id.talk_stk_4){
						stk_number = "4";
					}else if(v.getId() == R.id.talk_stk_5){
						stk_number = "5";
					}

					if(!stk_number.equals("")){
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
						params.put("room_seq", room_seq);
						params.put("contents", "좋아요");
						params.put("is_sticker", "Y");
						params.put("sticker_no", stk_number);

						sendLike.getDocument(UrlDef.TALK_REPLY_WRITE, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {

								if(document.select("ResultTable").select("Result").text().equals("true")){
									talk_sticker_cont.setVisibility(View.GONE);
									moreComm(0);
									vv.setEnabled(true);
								}else{
									talk_sticker_cont.setVisibility(View.GONE);
									Toast.makeText(getApplicationContext(), "좋아요에 실패 하였습니다.", Toast.LENGTH_LONG).show();
									vv.setEnabled(true);
								}
							}
						}, false);
					}
				}
			}
		};

		talk_stk_1.setOnClickListener(stk_icon_click);
		talk_stk_2.setOnClickListener(stk_icon_click);
		talk_stk_3.setOnClickListener(stk_icon_click);
		talk_stk_4.setOnClickListener(stk_icon_click);
		talk_stk_5.setOnClickListener(stk_icon_click);

		like_btn = (TextView)listViewHeader.findViewById(R.id.like_btn);
		like_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(talk_sticker_cont.getVisibility() == View.GONE){
					talk_sticker_cont.setVisibility(View.VISIBLE);
				}else{
					talk_sticker_cont.setVisibility(View.GONE);
				}
			}
		});


		reply_btn = (TextView)listViewHeader.findViewById(R.id.reply_btn);
		reply_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				comm_text.setInputType(1);
				imm.showSoftInput(comm_text, InputMethodManager.SHOW_IMPLICIT);
				comm_list.setSelectionFromTop(comm_list.getAdapter().getCount() - 1, comm_list.getScrollY());
			}
		});


		comm_write = (Button)findViewById(R.id.comm_write);
		comm_text = (EditText)findViewById(R.id.comm_text);
		comm_text.setInputType(0);
		comm_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				comm_text.setInputType(1);
				imm.showSoftInput(comm_text, InputMethodManager.SHOW_IMPLICIT);
				comm_list.setSelectionFromTop(comm_list.getAdapter().getCount() - 1, comm_list.getScrollY());
			}
		});

		comm_text.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO
					sendComment();
				}
				return false;
			}
		});

		comm_write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {

				sendComment();
			}
		});

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBackPressed();
			}
		});

		talk_keyword = (TextView)findViewById(R.id.talk_keyword);
		talk_timer =(TextView)findViewById(R.id.talk_timer);

		talk_push_on = (RecyclingImageView)findViewById(R.id.talk_push_on);
		talk_push_off = (RecyclingImageView)findViewById(R.id.talk_push_off);

		talk_push_on.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				talk_push_on.setVisibility(View.GONE);
				talk_push_off.setVisibility(View.VISIBLE);

				setPushAlarm("N");

			}
		});
		talk_push_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				talk_push_off.setVisibility(View.GONE);
				talk_push_on.setVisibility(View.VISIBLE);

				setPushAlarm("Y");
			}
		});
	}

	private void setPushAlarm(String flag){

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("room_seq", room_seq);
		params.put("push_yn", flag);

		setPushAlarm.getDocument(UrlDef.TALK_PUSH_ONOFF, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {

				LogUtil.D(document.toString());

				if(document.select("ResultTable").select("Result").text().equals("true")){
					String IS_PUSH = document.select("IS_PUSH").text();
					if(IS_PUSH.equals("Y")){
						Toast.makeText(getApplicationContext(), "푸시알람이 켜졌습니다.", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(getApplicationContext(), "푸시알람이 꺼졌습니다.", Toast.LENGTH_LONG).show();
					}
				}
			}
		}, false);

	}

	public void otherPushReload(){
		LogUtil.D("otherPushReload.....");

		handle.post(r);
	}

	// 다른방에 댓글이 달렸을 경우 
	private void otherRoomPushReload(){

		LogUtil.D("otherRoomPushReload.....");

		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		Cursor c = db.rawQuery("select ROOM_SEQ  from talk_push_list order by SEQ desc limit 0, 1", null);
		if(c.moveToFirst()){
			String ROOM_SEQ = c.getString(0);
			if(!ROOM_SEQ.equals(room_seq)){
				other_room_push.setVisibility(View.VISIBLE);
				other_room_push.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//other_room_push.setVisibility(View.INVISIBLE);

						Intent ii = new Intent(getApplicationContext(), TalkPushListActivity.class);
						ii.putExtra("room_seq", room_seq);
						ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(ii);

					}
				});
			}
		}
	}

	// 댓글 전송
	private void sendComment(){
		String comment_txt = comm_text.getText().toString();
		comm_write.setEnabled(false);
		if(comm_text.getText().toString().trim().equals("")){
			Toast.makeText(getApplicationContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
			comm_write.setEnabled(true);
		}else{
			for(int i = 0 ; i < focusId.size() ; i++){
				if(comment_txt.contains( focusId.get(i))){
					LogUtil.D("focuse id replaced");
					comment_txt = comment_txt.replace( focusId.get(i), focusId.get(i) + "$");
				}
			}

			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
			params.put("room_seq", room_seq);
			params.put("contents", comment_txt);
			params.put("is_sticker", "N");

			LogUtil.D("Comment Content = " + comment_txt);

			sendComment.getDocument(UrlDef.TALK_REPLY_WRITE, params, null, new HttpCallBack() {
				@Override
				public void onHttpCallBackListener(Document document) {

					if(document.select("ResultTable").select("Result").text().equals("true")){
						comm_text.setText("");
						//키보드를 숨긴다
						imm.hideSoftInputFromWindow(comm_text.getWindowToken(), 0);   
						moreComm(0);
						comm_write.setEnabled(true);
					}
				}
			}, false);
		}
	}

	// 댓글 신고하기
	private void declareComment(final String reply_seq) {
		final String[] gubun = getResources().getStringArray(R.array.declare_type);
		new AlertDialog.Builder(TalkInActivity.this)
		.setItems(gubun, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("reply_seq", reply_seq);
				params.put("report_code" , String.valueOf(which));
				params.put("report_contents" , "-");
				params.put("report_user_seq", Setting.getUserSeq(getApplicationContext()));

				final HttpDocument declareComm = new HttpDocument(getApplicationContext());
				declareComm.getDocument(UrlDef.TALK_REPLY_REPORT, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						String Code = document.select("ReturnTable").select("Code").text();
						if(Code.equals("0")){
							Toast.makeText(getApplicationContext(), "신고가 완료되었습니다.", Toast.LENGTH_LONG).show();
						}else if(Code.equals("-2001")){
							Toast.makeText(getApplicationContext(), "이미 신고하였습니다.", Toast.LENGTH_LONG).show();
						}else{
							Toast.makeText(getApplicationContext(), "신고 도중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
						}
						declareComm.threadStop();
					}
				}, false);
			}
		}).create().show();
	}

	private void deleteComment(String comm_seq){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("reply_seq", comm_seq);

		final HttpDocument delComm = new HttpDocument(getApplicationContext());
		delComm.getDocument(UrlDef.TALK_REPLY_DELETE, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				String Code = document.select("ReturnTable").select("Code").text();
				if(Code.equals("0")){
					Toast.makeText(getApplicationContext(), "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
					moreComm(0);
				}else if(Code.equals("-2001")){
					Toast.makeText(getApplicationContext(), "내 댓글이 아닙니다.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "댓글 삭제 오류.", Toast.LENGTH_SHORT).show();
				}
				delComm.threadStop();
			}
		}, false);
	}

	@Override
	public void onBackPressed() {

		if(talk_sticker_cont.getVisibility() == View.VISIBLE){
			talk_sticker_cont.setVisibility(View.GONE);			
			return;
		}

		if(exp_timer != null){
			exp_timer.cancel();
			exp_timer = null;
		}
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {

		getRoomInfo.threadStop(); 
		sendComment.threadStop();
		getCommList.threadStop();
		setPushAlarm.threadStop();
		sendLike.threadStop();
		getRoomMakerInfo.threadStop();

		focusId.clear();

		if(talk_push_on != null){
			talk_push_on.setImageBitmap(null);
		}
		if(talk_push_off != null){
			talk_push_off.setImageBitmap(null);
		}
		if(other_room_push != null){
			other_room_push.setImageBitmap(null);
		}
		if(talk_stk_1 != null){
			talk_stk_1.setImageBitmap(null);
		}
		if(talk_stk_2 != null){
			talk_stk_2.setImageBitmap(null);
		}
		if(talk_stk_3 != null){
			talk_stk_3.setImageBitmap(null);
		}
		if(talk_stk_4 != null){
			talk_stk_4.setImageBitmap(null);
		}
		if(talk_stk_5 != null){
			talk_stk_5.setImageBitmap(null);
		}


		super.onDestroy();
	}

}
