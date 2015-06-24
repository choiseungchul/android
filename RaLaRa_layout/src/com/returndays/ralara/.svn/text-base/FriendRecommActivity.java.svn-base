package com.returndays.ralara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.FriendListAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.RecommFriendDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class FriendRecommActivity extends BaseActivity{

	ListView recomm_list;
	LinearLayout sub_back_btn;
	RecyclingImageView friend_recomm_benifit1, friend_recomm_benifit2,friend_recomm_benifit3; 
	TextView recomm_count, recomm_friend_count_top;
	Button save_btn;
	HttpDocument mHttp, sendSmsByServer, send_recomm, mHttpUtil ,http;
	SeekBar recomm_seekbar;
	FriendListAdapter mAdap;
	ArrayList<RecommFriendDto> datas;
	String user_hp;
	// 맨처음만 설정되는 값
	int recom_count = 0;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recomm);

		TelephonyManager telManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		
		mHttp = new HttpDocument(getApplicationContext());
		sendSmsByServer = new HttpDocument(getApplicationContext());
		send_recomm = new HttpDocument(getApplicationContext());
		mHttpUtil = new HttpDocument(getApplicationContext());
		http = new HttpDocument(getApplicationContext());

		try{
			user_hp = MadUtil.getPhoneNumber(getApplicationContext());
			if(user_hp == null){
				user_hp =  telManager.getSubscriberId();
			}
			if(user_hp == null){
				String ran = MadUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789",13);
				user_hp = ran;
			}
		}catch(Exception e){
			String ran = MadUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789",13);
			user_hp = ran;
		}

		initUI();
		initData();
	}

	private void initData() {
		sendAllContact();
	}

	private void sendSmsFromServer(final RecommFriendDto dto, final Button btn){

		Hashtable<String, String> params = new Hashtable<String, String>(); 
		params.put("user_hp", dto.FRIEND_HP);
		params.put("contents", getString(R.string.recomm_comment));
		params.put("sender_no", user_hp);
		sendSmsByServer.getDocument(UrlDef.SMS_SEND, params, null, new HttpCallBack(){
			@Override
			public void onHttpCallBackListener(Document document) {
				LogUtil.D("sms result = " + document.toString());

				if(document.select("body").select("result").text().equals("ok")){
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("MY_HP", user_hp);
					params.put("FRIEND_HP", dto.FRIEND_HP);
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					send_recomm.getDocument(UrlDef.RECOMM_FRIEND2, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							LogUtil.D("recomm result = " + document.toString());

							if(document.select("returntable").select("code").text().equals("0")){
								String curr_recomm = document.select("table3").select("curr_recomm").text().trim();
								String add_amount = document.select("egg_add_cnt").text();
								DialogUtil.alert(FriendRecommActivity.this, "친구를 초대하여 알 " + add_amount + " 개를 적립받았습니다.");
								recomm_count.setText(curr_recomm + " 명");
								recomm_seekbar.setProgress(Integer.parseInt(curr_recomm));
							}else if(document.select("returntable").select("code").text().equals("-2001")){
								DialogUtil.alert(FriendRecommActivity.this, "이미 추천한 친구입니다.");
							}else if(document.select("returntable").select("code").text().equals("-2002")){
								DialogUtil.alert(FriendRecommActivity.this, "추천갯수를 30개 넘었습니다.");
							}else{
								DialogUtil.alert(FriendRecommActivity.this, "추천 오류");
							}
						}
					}, false);
				}else{
					DialogUtil.alert(FriendRecommActivity.this, "문자 전송이 실패하였습니다.");
					btnChange(btn, 0, dto);
				}
			}
		}, false);
	}

//	private void add_egg(final String count){
//
//		Hashtable<String, String> params = new Hashtable<String, String>();
//		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
//		params.put("ad_seq", "88888888-8888-8888-8888-888888888888");
//		params.put("is_gold", "N");
//		params.put("amount", count);
//		params.put("egg_grade", "1");
//
//		mHttpUtil.getDocument( UrlDef.MY_POINT_ADD, params,null, new HttpCallBack() {
//
//			@Override
//			public void onHttpCallBackListener(Document document) {
//
//				LogUtil.D("add_egg = " + document.toString());
//
//				if(document.select("ResultTable").select("Result").text().equals("true")){
//					DialogUtil.alert(FriendRecommActivity.this, String.format("알 %s개가 적립되었습니다!!", count));
//				}else{
//					Toast.makeText(getApplicationContext(), String.format("알 적립 실패.", count), Toast.LENGTH_SHORT).show();
//				}
//			}
//		}, false);
//	}

	private void syncRecomm(){
		final HttpDocument getRecomm = new HttpDocument(getApplicationContext());
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("MY_HP", user_hp);
		getRecomm.getDocument(UrlDef.GET_RECOMM, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("ResultTable").select("Result").text().equals("true")){
					Elements els = document.select("ReturnTable");
					if(els.size() > 0){

						String[] recomm_hp = new String[els.size()];
						int idx = 0;
						for(Element el : els){
							recomm_hp[idx] = el.select("FRIEND_HP").text();
							idx++;
						}
						MadUtil.syncRecomm(getApplicationContext(), recomm_hp);

						recom_count = els.size();
						
						if(recom_count >= 30){
							recom_count = 30;
						}
						
						recomm_seekbar.setProgress(recom_count);
						recomm_count.setText( recom_count + " 명");
					}
					// 데이터 출력
					setData();
					dialog.dismiss();
				}
				getRecomm.threadStop();
			}

		}, false);

	}

	// 0 => 초대, 1 => 확인
	private void btnChange(Button btn, int type, RecommFriendDto dto){
		if(type == 0){
			btn.setBackgroundResource(R.drawable.button_purple);
			btn.setText("초대");
			dto.RESULT = "0";
			datas.set(dto.index, dto);
			mAdap.setData(datas);
			mAdap.notifyDataSetChanged();
		}else if(type == 1){
			btn.setBackgroundResource(R.drawable.button_gray);
			btn.setText("확인");
			dto.RESULT = "2";
			datas.set(dto.index, dto);
			mAdap.setData(datas);
			mAdap.notifyDataSetChanged();
		}
	}

	private void setData() {
		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		Cursor c = db.rawQuery("select * from recomm_friend_list order by display_name asc", null);

		datas = new ArrayList<RecommFriendDto>();

		if(c.getCount() > 0){
			if(c.moveToFirst()){
				int cnt = 0;

				do{
					RecommFriendDto dto = new RecommFriendDto();

					dto.FRIEND_HP = c.getString(0);
					dto.FRIEND_NM = c.getString(1);
					dto.RESULT = String.valueOf( c.getInt(2) );
					dto.index = cnt;
					datas.add(dto);
					cnt++;

				}while(c.moveToNext());
			}
		}

		if(mAdap == null){
			mAdap = new FriendListAdapter(FriendRecommActivity.this, datas, new OnClickListener() {
				@Override
				public void onClick(View v) {
					Button btn = (Button)v;
					RecommFriendDto dto = (RecommFriendDto)v.getTag();
					if(dto.RESULT.equals("0")){
						sendSmsFromServer(dto, btn);
						btnChange(btn, 1, dto);
					}
				}
			});
			recomm_list.setAdapter(mAdap);
		}else{
			mAdap.setData(datas);
			mAdap.notifyDataSetChanged();
		}

	}

	// 연락처를 db, 서버에 둘다 입력
	private void sendAllContact(){
		// 서버에 있는 모든 연락처 제거 후 다시 입력
		dialog = ProgressDialog.show(FriendRecommActivity.this, null, getString(com.returndays.ralara.R.string.load), true, true);

		HashMap<String, RecommFriendDto>c_list = MadUtil.getContactList(getApplicationContext());

		if(c_list.size() == 0){
			dialog.dismiss();
			recomm_friend_count_top.setText(c_list.size() + " 명");
			Toast.makeText(getApplicationContext(), "연락처가 없습니다.", Toast.LENGTH_LONG).show();
			return;
		}
		
		recomm_friend_count_top.setText(c_list.size() + " 명");

		StringBuffer hp = new StringBuffer();
		StringBuffer name = new StringBuffer();

		// sqlite db에 넣기
		MadUtil.syncContact(getApplicationContext(), c_list);

		for(Entry<String, RecommFriendDto> dto : c_list.entrySet()){
			hp.append(dto.getKey());
			hp.append("|");
			name.append(dto.getValue().FRIEND_NM);
			name.append("|");
		}
		if(hp.length() > 0){
			hp.substring(0, hp.length() - 1);
			name.substring(0,name.length() - 1);
		}

		final HttpDocument sendContact = new HttpDocument(getApplicationContext());
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("USER_SEQ", Setting.getUserSeq(getApplicationContext()));
		params.put("MY_HP", user_hp);
		params.put("FRIEND_HP", hp.toString() );
		params.put("FRIEND_NM", name.toString() );
		sendContact.setMethod("POST");
		sendContact.getDocument(UrlDef.ADD_FRIEND2, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("ResultTable").select("Result").text().equals("true")){
					LogUtil.D("send friend success ");

					syncRecomm();
				}
				sendContact.threadStop();
			}
		}, false);
	}

	private void initUI() {

		sub_back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		sub_back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("동기화");
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendAllContact();
			}
		});

		recomm_seekbar = (SeekBar)findViewById(R.id.recomm_seekbar);
		recomm_seekbar.setEnabled(false);

		recomm_list = (ListView)findViewById(R.id.recomm_list);

		recomm_friend_count_top = (TextView)findViewById(R.id.recomm_friend_count_top);
		recomm_count = (TextView)findViewById(R.id.recomm_count);

		friend_recomm_benifit1 = (RecyclingImageView)findViewById(R.id.friend_recomm_benifit1);
		friend_recomm_benifit2 = (RecyclingImageView)findViewById(R.id.friend_recomm_benifit2);
		friend_recomm_benifit3 = (RecyclingImageView)findViewById(R.id.friend_recomm_benifit3);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttp.threadStop();
		http.threadStop();
		sendSmsByServer.threadStop();
		send_recomm.threadStop();
		http.threadStop();
		if(dialog !=null){
			dialog.dismiss();
		}
		
		if(friend_recomm_benifit1 != null){
			friend_recomm_benifit1.setImageBitmap(null);
		}
		if(friend_recomm_benifit2 != null){
			friend_recomm_benifit2.setImageBitmap(null);
		}
		if(friend_recomm_benifit3 != null){
			friend_recomm_benifit3.setImageBitmap(null);
		}

		super.onDestroy();
	}

}
