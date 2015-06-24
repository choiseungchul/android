package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.TalkPushListAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.TalkPushListDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TalkPushListActivity extends BaseActivity{

	ListView push_alarm_list;
	TalkPushListAdapter mAdap;
	String room_seq;
	HttpDocument mHttpDocument;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_push_list);
		
		Bundle b = getIntent().getExtras();
		room_seq = b.getString("room_seq");
		
		mHttpDocument = new HttpDocument(getApplicationContext());
		
		initUI();
		initData();
	}

	private void initData() {
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("last_room_seq", "0");
		
		mHttpDocument.getDocument(UrlDef.TALK_LIST_MY, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("Result").text().equals("true")){
					int size = document.select("ROOM_SEQ").size();
					
					ArrayList<String> roomSeqList = new ArrayList<String>();
					
					SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
					Cursor c = db.rawQuery("select ROOM_SEQ  from talk_push_list order by SEQ desc", null);
					
					if(c.moveToFirst()){
						do{
							String ROOM_SEQ = c.getString(0);
							roomSeqList.add(ROOM_SEQ);
						}while(c.moveToNext());
					}
					
					ArrayList<TalkPushListDto> datas = new ArrayList<TalkPushListDto>();
					
					for(int k = 0 ; k < size; k++){
						String ROOM_SEQ = document.select("ROOM_SEQ").get(k).text();
						LogUtil.D("ROOM_SEQ = " + ROOM_SEQ);
						
						if(roomSeqList.contains(ROOM_SEQ) && !room_seq.equals(ROOM_SEQ)){
							String KEYWORDS = document.select("KEYWORDS").get(k).text();
							String ROOM_IMG = document.select("ROOM_IMG").get(k).text();
							
							TalkPushListDto dto = new TalkPushListDto();
							dto.Room_seq = ROOM_SEQ;
							dto.TalkDesc = KEYWORDS;
							dto.ThumbNail = ROOM_IMG;
							
							Cursor cc = db.rawQuery("select DATE  from talk_push_list WHERE ROOM_SEQ = " + ROOM_SEQ, null);
							if(cc.moveToFirst()){
								String DATE = cc.getString(0);
								dto.TIME = DATE;
							}
							datas.add(dto);
						}
					}
					mAdap = new TalkPushListAdapter(TalkPushListActivity.this, datas);
					push_alarm_list.setAdapter(mAdap);
				}
			}
		}, false);
	}

	private void initUI() {
		// TODO Auto-generated method stub
		push_alarm_list = (ListView)findViewById(R.id.push_alarm_list);
		push_alarm_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				TalkPushListDto dto = (TalkPushListDto)arg0.getItemAtPosition(arg2);
				String Room_seq = dto.Room_seq;
				LogUtil.D("talk push list room_seq = " + Room_seq);
				Intent ii = new Intent(getApplicationContext(), TalkInActivity.class);
				ii.putExtra("room_seq", Room_seq);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpDocument.threadStop();
		super.onDestroy();
	}
	
}
