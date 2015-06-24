package com.returndays.ralara.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;

import android.app.Activity;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.google.android.gms.internal.ct;
import com.returndays.customview.PresentEggDialog;
import com.returndays.customview.ProfileDialog;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CommentDto;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.StringUtil;

public class CommentListAdapter extends BaseAdapter {
	private ArrayList<CommentDto> mDatas = new ArrayList<CommentDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private ProfileDialog dialog;
	AQuery aq;
	OnClickListener onclickListener;

	@SuppressWarnings("unchecked")
	public CommentListAdapter(Activity activity, ArrayList<CommentDto> datas, OnClickListener listener) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<CommentDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		aq = new AQuery(activity);
		onclickListener = listener;
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<CommentDto> datas) {
		this.mDatas = (ArrayList<CommentDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public CommentDto getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.comm_item, null);

			holder.comm_id = (TextView)convertView.findViewById(R.id.comm_id);
			holder.comm_time = (TextView)convertView.findViewById(R.id.comm_time);
			holder.comm_content = (TextView)convertView.findViewById(R.id.comm_content);
			holder.profile_img = (RecyclingImageView)convertView.findViewById(R.id.profile_img);
			holder.like_emoti = (RecyclingImageView)convertView.findViewById(R.id.like_emoti);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final CommentDto dto = getItem(position);
		holder.comm_id.setText(dto.USER_NICKNAME);
		holder.comm_id.setOnClickListener(onclickListener);
		
		if(dto.IS_STICKER.equals("N")){
			
			// 글자색넣기
			int strIdx = 0;
			int cnt = dto.CONTENTS.split("@").length - 1;
			if(cnt > 0){
				SpannableStringBuilder sps = new SpannableStringBuilder(dto.CONTENTS);
			
				for(int i = 0 ; i < cnt; i++){
					int a = sps.toString().indexOf("@", strIdx);
					int d =  sps.toString().indexOf("$", strIdx);
					
					if(a != -1 && d != -1 && d > a){
						LogUtil.D("start = " + a + "  end = " + d);
						sps.setSpan(new ForegroundColorSpan(Color.rgb(0x8c, 0x5e, 0xa1)), a, d, 0);
						strIdx = d +1;
					}
				}
				//문자열제거 ( spanablestring은 replace를 지원하지않음 )
				strIdx = 0;
				for(int i = 0 ; i < cnt; i++){
					int a = sps.toString().indexOf("@", strIdx);
					int d =  sps.toString().indexOf("$", strIdx);
					
					if(a != -1 && d != -1 && d > a){
						sps.delete(a, a+1);
						d--;
						if(d == sps.length()){
							sps.delete(d - 1, d);	
						}else{
							sps.delete(d, d+1);
						}
						
						strIdx = sps.toString().indexOf("@");
					}
				}
				
				holder.comm_content.setText(sps, BufferType.SPANNABLE);
			}else{
				holder.comm_content.setText(dto.CONTENTS);
			}
		}else{
			holder.comm_content.setText("");
		}
		
		// 남은시간 설정
		DateFormat expire_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
		long expired = 0;
		try {

			Date tempDate = expire_date.parse(dto.REG_DATE.replace("T", " "));
			
			long time = (Calendar.getInstance(Locale.KOREA).getTime().getTime()) - tempDate.getTime();
			
			expired = time;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String timeStr = StringUtil.getBeforeTimeString(expired);
		if(timeStr.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("M.dd a hh:mm", Locale.KOREA);
			
			try {
				Date reg_date = sdf.parse(dto.REG_DATE.replace("T", " "));
				timeStr =sdf.format(reg_date);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			timeStr.replace("AM", "오전");
			timeStr.replace("PM", "오후");
			
		}
		
		holder.comm_time.setText(timeStr);
		
		AQuery aquery = aq.recycle(convertView);
		
		ImageOptions opt = new ImageOptions();
		opt.round = 60;
		if(dto.USER_IMAGE.equals("")){
			if(dto.SEX.equals("M")){
				aquery.id(holder.profile_img).image(R.drawable.man_ico);
			}else if(dto.SEX.equals("F")){
				aquery.id(holder.profile_img).image(R.drawable.woman_ico);
			}
			
		}else{
			aquery.id(holder.profile_img).image(dto.USER_IMAGE, opt);
		}
		
		LogUtil.D( "position = " + position +" is sticker = " +  dto.IS_STICKER);
		
		holder.like_emoti.setImageDrawable(null);
		if(dto.IS_STICKER.equals("Y")){

			int STK_NO = Integer.parseInt(dto.STICKER_NO);
			switch (STK_NO) {
			case 1:
				aquery.id(holder.like_emoti).image(R.drawable.stk_icon_1);
				break;
			case 2:
				aquery.id(holder.like_emoti).image(R.drawable.stk_icon_2);
				break;
			case 3:
				aquery.id(holder.like_emoti).image(R.drawable.stk_icon_3);
				break;
			case 4:
				aquery.id(holder.like_emoti).image(R.drawable.stk_icon_4);
				break;
			case 5:
				aquery.id(holder.like_emoti).image(R.drawable.stk_icon_5);
				break;
			default:
				break;
			}
		}else{
			//holder.like_emoti.setVisibility(View.GONE);
		}
		
		
		
		holder.profile_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String user_seq = dto.USER_SEQ;
				
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", user_seq);
				final HttpDocument http = new HttpDocument(mActivity);
				
				http.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						// TODO Auto-generated method stub
						if(document.select("ResultTable").select("Result").text().equals("true")){
							if(document.select("ReturnTable").isEmpty()){
//								LogUtil.D( "return table = " +  document.select("ReturnTable").text());
								Toast.makeText(mActivity, "탈퇴한 유저 입니다.", Toast.LENGTH_LONG).show();
							}else{
								String PROFILE_OPEN_YN = document.select("PROFILE_OPEN_YN").text();
								final String SEX = document.select("SEX").text();
								String USER_IMG = document.select("USER_IMG").text();
								String USER_IMG_PATH = document.select("USER_IMG_PATH").text();
								final String USER_NICKNAME = document.select("USER_NICKNAME").text();
								String SIDO = document.select("SIDO").text();
								String GUGUN = document.select("GUGUN").text();
								
								dialog = new ProfileDialog(mActivity);
								final String user_image = USER_IMG_PATH + USER_IMG; 
								if(user_image.equals("")){
									if(SEX.equals("M")){
										dialog.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.man_ico));
									}else if(SEX.equals("F")){
										dialog.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.woman_ico));
									}
								}else{
									dialog.setImage(USER_IMG_PATH + USER_IMG);
								}
								
								if(!Setting.getUserSeq(mActivity).equals(user_seq)){
									dialog.setPresentOther(new OnClickListener() {
										@Override
										public void onClick(View v) {
											Hashtable<String, String> params = new Hashtable<String, String>();
											params.put("user_seq", Setting.getUserSeq(mActivity));
											final HttpDocument doc = new HttpDocument(mActivity);
											doc.getDocument(UrlDef.GET_COUNTS, params, null, new HttpCallBack() {
												@Override
												public void onHttpCallBackListener(Document document) {
													if(document.select("ResultTable").select("Result").text().equals("true")){
														String egg = document.select("EGG").text();
														if( Integer.parseInt(egg) > 0){
															dialog.dismiss();
															// 다이얼로그 띄움
															final PresentEggDialog presentDialog = new PresentEggDialog(mActivity);
															presentDialog.setTitle(USER_NICKNAME + "님께 알 선물하기");
															presentDialog.setMaxPresent( Integer.parseInt(egg) );
															presentDialog.setPresentSubmitEvent(new OnClickListener() {
																@Override
																public void onClick(View v) {
																	presentDialog.dismiss();
																	final String amt = presentDialog.getCurrentAmount();
																	Hashtable<String, String> params = new Hashtable<String, String>();
																	params.put("user_seq", Setting.getUserSeq(mActivity));
																	params.put("friend_user_seq", user_seq); 
																	params.put("amount", amt);
																	final HttpDocument http = new HttpDocument(mActivity);
																	http.getDocument(UrlDef.EGG_GIFT, params, null, new HttpCallBack() {
																		@Override
																		public void onHttpCallBackListener(Document document) {
																			if(document.select("ResultTable").select("Result").text().equals("true")){
																				String Code = document.select("Code").text();
																				if(Code.equals("0")){
																					DialogUtil.alert(mActivity, "[" + USER_NICKNAME + "] 님께 알" + amt + "개를 선물하였습니다.");
																				}else{
																					if(Code.equals("-2001")){
																						Toast.makeText(mActivity, "자기 자신에게 선물할 수 없습니다.", Toast.LENGTH_LONG).show();
																					}else if(Code.equals("-2002")){
																						Toast.makeText(mActivity, "선물할 알이 부족합니다.", Toast.LENGTH_LONG).show();
																					}
																				}
																			}else{
																				Toast.makeText(mActivity, "오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
																			}
																			http.threadStop();
																		}
																	}, true);
																}
															});
															
															presentDialog.show();
															
														}else{
															dialog.dismiss();
															Toast.makeText(mActivity, "알이 최소 1개 이상이어야 합니다.", Toast.LENGTH_LONG).show();
														}
													}else{
														dialog.dismiss();
														Toast.makeText(mActivity, "현재 보유하고 있는 알 개수를 가져오지 못하였습니다.", Toast.LENGTH_LONG).show();
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
		
		return convertView;
	}
	
	private class Holder {
		RecyclingImageView profile_img, like_emoti;
		TextView comm_id, comm_time, comm_content;
		
	}

}
