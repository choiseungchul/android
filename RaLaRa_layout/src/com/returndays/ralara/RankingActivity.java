package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.DefaultDialog;
import com.returndays.customview.PresentEggDialog;
import com.returndays.customview.ProfileDialog;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.RankingAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.RankingDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.MadUtil;

public class RankingActivity extends BaseActivity{

	ListView user_ranking_list;
	RankingAdapter adap;
	Button save_btn;
	TextViewNanumGothic ranking_title, rank_list_empty;
	LinearLayout sub_back_btn;
	RecyclingImageView btn_rank_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranking);
		
		initUI();
		initData();
		
	}

	private void initData() {
//		String tempUserImage = "http://img1.ralara.net/image/user/20131004/bf6f797d-1612-40cc-88b8-fd2dafb44278/M1_20131004.jpg";
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("null1", "0");
		final HttpDocument http = new HttpDocument(getApplicationContext());
		http.getDocument(UrlDef.RANKING_LIST, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				
				rank_list_empty.setVisibility(View.GONE);
				
				if(document.select("ResultTable ").select("Result").text().equals("true")){
					Elements entries = document.select("ReturnTable");
					
					ArrayList<RankingDto> datas = new ArrayList<RankingDto>();
					
					for(Element entry : entries) {
						RankingDto dto = new RankingDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
					}
					
					if(datas.size() > 0){
						ranking_title.setText("주간랭킹 ( " + datas.get(0).WEEK + " )");
					}else{
						user_ranking_list.setEmptyView(rank_list_empty);
						rank_list_empty.setVisibility(View.VISIBLE);
					}
					
					adap = new RankingAdapter(RankingActivity.this, datas);
					user_ranking_list.setAdapter(adap);
					
					user_ranking_list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							
							RankingDto dto =  (RankingDto)arg0.getItemAtPosition(arg2);
							final String user_seq = dto.USER_SEQ;
							
							Hashtable<String, String> params = new Hashtable<String, String>();
							params.put("user_seq", user_seq);
							final HttpDocument http = new HttpDocument(getApplicationContext());
							
							http.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
								@Override
								public void onHttpCallBackListener(Document document) {
									// TODO Auto-generated method stub
									if(document.select("ResultTable").select("Result").text().equals("true")){
										
										if(document.select("ReturnTable").isEmpty()){
											Toast.makeText(getApplicationContext(), "탈퇴한 유저 입니다.", Toast.LENGTH_LONG).show();
										}else{
											String PROFILE_OPEN_YN = document.select("PROFILE_OPEN_YN").text();
											final String SEX = document.select("SEX").text();
											String USER_IMG = document.select("USER_IMG").text();
											String USER_IMG_PATH = document.select("USER_IMG_PATH").text();
											final String USER_NICKNAME = document.select("USER_NICKNAME").text();
											String SIDO = document.select("SIDO").text();
											String GUGUN = document.select("GUGUN").text();
											
											final ProfileDialog dialog = new ProfileDialog(RankingActivity.this);
											final String user_image = USER_IMG_PATH + USER_IMG; 
											if(user_image.equals("")){
												if(SEX.equals("M")){
													dialog.setImageDrawable(getResources().getDrawable(R.drawable.man_ico));
												}else if(SEX.equals("F")){
													dialog.setImageDrawable(getResources().getDrawable(R.drawable.woman_ico));
												}
											}else{
												dialog.setImage(USER_IMG_PATH + USER_IMG);
											}
											
											if(!Setting.getUserSeq(getApplicationContext()).equals(user_seq)){
												dialog.setPresentOther(new OnClickListener() {
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
																	if( Integer.parseInt(egg) > 0){
																		dialog.dismiss();
																		// 다이얼로그 띄움
																		final PresentEggDialog presentDialog = new PresentEggDialog(RankingActivity.this);
																		presentDialog.setTitle(USER_NICKNAME + "님께 알 선물하기");
																		presentDialog.setMaxPresent( Integer.parseInt(egg) );
																		presentDialog.setPresentSubmitEvent(new OnClickListener() {
																			@Override
																			public void onClick(View v) {
																				presentDialog.dismiss();
																				final String amt = presentDialog.getCurrentAmount();
																				Hashtable<String, String> params = new Hashtable<String, String>();
																				params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
																				params.put("friend_user_seq", user_seq); 
																				params.put("amount", amt);
																				final HttpDocument http = new HttpDocument(RankingActivity.this);
																				http.getDocument(UrlDef.EGG_GIFT, params, null, new HttpCallBack() {
																					@Override
																					public void onHttpCallBackListener(Document document) {
																						if(document.select("ResultTable").select("Result").text().equals("true")){
																							String Code = document.select("Code").text();
																							if(Code.equals("0")){
																								DialogUtil.alert(RankingActivity.this, "[" + USER_NICKNAME + "] 님께 알" + amt + "개를 선물하였습니다.");
																							}else{
																								if(Code.equals("-2001")){
																									Toast.makeText(getApplicationContext(), "자기 자신에게 선물할 수 없습니다.", Toast.LENGTH_LONG).show();
																								}else if(Code.equals("-2002")){
																									Toast.makeText(getApplicationContext(), "선물할 알이 부족합니다.", Toast.LENGTH_LONG).show();
																								}
																							}
																						}else{
																							Toast.makeText(getApplicationContext(), "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
																						}
																						
																						http.threadStop();
																					}
																				}, true);
																			}
																		});
																		
																		presentDialog.show();
																		
																	}else{
																		dialog.dismiss();
																		Toast.makeText(getApplicationContext(), "알이 최소 1개 이상이어야 합니다.", Toast.LENGTH_LONG).show();
																	}
																}else{
																	dialog.dismiss();
																	Toast.makeText(getApplicationContext(), "현재 보유하고 있는 알 개수를 가져오지 못하였습니다.", Toast.LENGTH_LONG).show();
																}
																doc.threadStop();
															}
														}, false);
													}
												});
											}
											dialog.setDesc("[" + USER_NICKNAME + "]  " +  SIDO + " " + GUGUN );
											dialog.show();
										}
									}
									http.threadStop();
								}
							}, false);
							
						}
					});
				}
				
				http.threadStop();
			}
		}, false);
		
	}

	private void initUI() {
		
		btn_rank_info = (RecyclingImageView)findViewById(R.id.btn_rank_info);
		btn_rank_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final DefaultDialog dialog = new DefaultDialog(RankingActivity.this);
				dialog.setTitle(getString(R.string.rank_info_title));
				dialog.setMessage(getString(R.string.rank_info_msg));
				dialog.show();
				dialog.setPositiveButton("확인",new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		});
		
		sub_back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		sub_back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		ranking_title = (TextViewNanumGothic)findViewById(R.id.ranking_title);
		
		user_ranking_list = (ListView)findViewById(R.id.user_ranking_list);
		
		rank_list_empty = (TextViewNanumGothic)findViewById(R.id.rank_list_empty);
	}
	
	@Override
	protected void onDestroy() {
		if(btn_rank_info != null){
			btn_rank_info.setImageBitmap(null);
		}
		
		super.onDestroy();
	}
}
