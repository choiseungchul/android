package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.PresentEggDialog;
import com.returndays.customview.ProfileDialog;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.FindFriendAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.FindFriendDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.MadUtil;

public class FindFriendActivity extends BaseActivity{

	ListView find_friend_list;
	HttpDocument findSend;
	TextViewNanumGothic top_sub_title_text_sub, srch_empty;
	EditText srch_text;
	Button srch_btn;
	FindFriendAdapter adap;
	RecyclingImageView btn_rank_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_friend);

		findSend = new HttpDocument(this);

		initUI();
	}

	private void srch_friend(){

		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);   
		//키보드를 없앤다.   
		imm.hideSoftInputFromWindow(srch_text.getWindowToken(),0);

		if(srch_text.getText().toString().equals("")){
			DialogUtil.alert(FindFriendActivity.this, "친구의 닉네임을 입력해주세요");
			return;
		}

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_nick", srch_text.getText().toString());
		findSend.getDocument(UrlDef.SEARCH_FRIEND, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(!document.select("ReturnTable").isEmpty()){
					ArrayList<FindFriendDto> datas = new ArrayList<FindFriendDto>();

					Elements entries = document.select("ReturnTable");

					for(Element entry : entries) {
						FindFriendDto dto = new FindFriendDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
					}

					adap = new FindFriendAdapter(FindFriendActivity.this, datas);
					find_friend_list.setAdapter(adap);

					find_friend_list.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							FindFriendDto dto =  (FindFriendDto)arg0.getItemAtPosition(arg2);
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

											final ProfileDialog dialog = new ProfileDialog(FindFriendActivity.this);
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
																		final PresentEggDialog presentDialog = new PresentEggDialog(FindFriendActivity.this);
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
																				final HttpDocument http = new HttpDocument(FindFriendActivity.this);
																				http.getDocument(UrlDef.EGG_GIFT, params, null, new HttpCallBack() {
																					@Override
																					public void onHttpCallBackListener(Document document) {
																						if(document.select("ResultTable").select("Result").text().equals("true")){
																							String Code = document.select("Code").text();
																							if(Code.equals("0")){
																								DialogUtil.alert(FindFriendActivity.this, "[" + USER_NICKNAME + "] 님께 알" + amt + "개를 선물하였습니다.");
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

					if(datas.size() == 0){
						Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
						find_friend_list.setEmptyView(srch_empty);
						adap.notifyDataSetChanged();
						srch_empty.setVisibility(View.VISIBLE);
						srch_empty.setText(getString(R.string.friend_srch_empty));
					}
					
				}else{
					find_friend_list.setEmptyView(srch_empty);
					srch_empty.setVisibility(View.VISIBLE);
					if(adap != null)
					adap.notifyDataSetChanged();
					srch_empty.setText(getString(R.string.friend_srch_empty));
					Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
				}
			}
		}, true);
	}

	private void initUI() {
		btn_rank_info = (RecyclingImageView)findViewById(R.id.btn_rank_info);
		btn_rank_info.setVisibility(View.GONE);
		find_friend_list = (ListView)findViewById(R.id.find_friend_list);
		top_sub_title_text_sub = (TextViewNanumGothic)findViewById(R.id.top_sub_title_text_sub);
		top_sub_title_text_sub.setText("친구 찾기");
		top_sub_title_text_sub.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		srch_empty = (TextViewNanumGothic)findViewById(R.id.srch_empty);
		srch_text = (EditText)findViewById(R.id.srch_text);
		srch_btn = (Button)findViewById(R.id.srch_btn);
		srch_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				srch_friend();
			}
		});
	}

	@Override
	protected void onDestroy() {
		findSend.threadStop();

		super.onDestroy();
	}
}
