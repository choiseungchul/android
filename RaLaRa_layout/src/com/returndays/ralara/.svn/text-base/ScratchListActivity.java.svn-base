package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.returndays.http.HttpDocument;
import com.returndays.ralara.adapter.MyScratchCountAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.MyScratchDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class ScratchListActivity extends BaseActivity{

	Button save_btn;
	HttpDocument mHttpUtil;
	MyScratchCountAdapter mAdap;
	ListView mAdList;
	LinearLayout back_btn;
	TextView mTxtEmpty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch_count);
		mHttpUtil = new HttpDocument(getApplicationContext()); 
		//initUI();
		//initData();
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
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("user_token", Setting.getToken(getApplicationContext()));

		mHttpUtil.getDocument(UrlDef.MY_SCRATCH_LIST, params, null, new HttpDocument.HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				Elements entries = document.select("ReturnTable");
				String code = document.select("DataTable").select("Code").text();

				//LogUtil.I("return table " + entries.size());
				//LogUtil.I("data table" + entries2.size());
				LogUtil.D("size:::::::::::::::::::::"+ code);
				if(code.equals("0")){
					
					
					ArrayList<MyScratchDto> datas = new ArrayList<MyScratchDto>();

					for(Element entry : entries) {
						MyScratchDto dto = new MyScratchDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
					}

					mAdap = new MyScratchCountAdapter(ScratchListActivity.this, datas);

					mAdList.setAdapter(mAdap);

					mAdList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {

							String isEggTrans = ((MyScratchDto) arg0.getItemAtPosition(arg2)).AD_SEQ;
							if(isEggTrans.equals("")){

								Intent i = new Intent(getApplicationContext(),  ScratchCardNonAdActivity.class);
								i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
								i.putExtra("scratch_seq", ((MyScratchDto) arg0.getItemAtPosition(arg2)).SEQ);
								startActivity(i);
							}else{
								String ad_kind = ((MyScratchDto) arg0.getItemAtPosition(arg2)).AD_KIND;
								if(ad_kind.equals("1")){
									Intent i = new Intent(getApplicationContext(),  ScratchCardActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									i.putExtra("ad_seq", ((MyScratchDto) arg0.getItemAtPosition(arg2)).AD_SEQ);
									startActivity(i);	
								}else if(ad_kind.equals("2")){
									Intent i = new Intent(getApplicationContext(),  ScratchCardActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									i.putExtra("ad_seq", ((MyScratchDto) arg0.getItemAtPosition(arg2)).AD_SEQ);
									startActivity(i);
								}else if(ad_kind.equals("3")){
									Intent i = new Intent(getApplicationContext(),  ScratchCardActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
									i.putExtra("ad_seq", ((MyScratchDto) arg0.getItemAtPosition(arg2)).AD_SEQ);
									startActivity(i);
								}

							}

						}
					});
				}else{
					
					ArrayList<MyScratchDto> datas = new ArrayList<MyScratchDto>();
					mAdap = new MyScratchCountAdapter(ScratchListActivity.this, datas);
					mAdList.setAdapter(mAdap);
										
					mAdList.setEmptyView(mTxtEmpty);
					mTxtEmpty.setVisibility(View.VISIBLE);
				}
				
				
			}
		}, false);
	}

	private void initUI() {
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);

		mAdList = (ListView)findViewById(R.id.scratch_list);
		mTxtEmpty = (TextView) findViewById(R.id.txt_emptys);

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		super.onDestroy();
	}
}
