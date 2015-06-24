package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.MyRewradAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.MyRewardDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.MadUtil;

public class MyRewardActivity extends BaseActivity{

	LinearLayout back_btn;
	HttpDocument mHttpUtil;
	
	ListView mAdList;
	Button save_btn;
	Activity mActivity;
	MyRewradAdapter mAdap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myreward);
		mActivity = this;

		mHttpUtil = new HttpDocument(this);

		initUI();
		initData();
	}

	private void initData() {

		Hashtable<String, String> params = new Hashtable<String, String>();
		final String user_seq = Setting.getUserSeq(mActivity);
		//params.put("user_seq", Setting.getUserSeq(ctx));
		params.put("user_seq", user_seq);

		mHttpUtil.getDocument(UrlDef.MY_PINNO, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {

				String code = document.select("ResultTable").select("Result").text();
				//Toast.makeText(MyRewardActivity.this, code, Toast.LENGTH_LONG).show();
				if(code.equals("true")){
					Elements entries = document.select("ReturnTable");
					ArrayList<MyRewardDto> datas = new ArrayList<MyRewardDto>();
					for(Element entry : entries) {
						MyRewardDto dto = new MyRewardDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
					}
					
					mAdap = new MyRewradAdapter(mActivity, datas);
					mAdList.setAdapter(mAdap);
					mAdList.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							
							String end_action =  UrlDef.MY_PINNO_LIST_ITEM + "/?user_seq=" + user_seq + "&win_seq=" 
									+ ((MyRewardDto) arg0.getItemAtPosition(arg2)).WINNER_SEQ + "&pin_seq=" +
									((MyRewardDto) arg0.getItemAtPosition(arg2)).SEQ; 
							Intent i = new Intent(getApplicationContext(),  RewardWebActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
							i.putExtra("url", end_action);
							i.putExtra("reward_content", ((MyRewardDto) arg0.getItemAtPosition(arg2)).PROD_NAME);
							i.putExtra("title", "당첨권");
							startActivity(i);
							//finish();

						}
					});
				}
			}
		}, true);



		/*
		mHttpUtil.httpExecute(UrlDef.GET_AD, params, new HttpListener() {

			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {
				// TODO Auto-generated method stub


				LogUtil.I("return table " + entries.size());
				LogUtil.I("data table" + entries2.size());

				ArrayList<MyRewardDto> datas = new ArrayList<MyRewardDto>();

				for(XmlDom entry : entries) {

					LogUtil.I(entry.toString());

					MyRewardDto dto = new MyRewardDto();
					MadUtil.autoMappingXmlToObject(entry, dto);
					datas.add(dto);
				}

				for(XmlDom entry : entries2) {
					LogUtil.I(entry.toString());

					MyRewardDto dto = new MyRewardDto();
					MadUtil.autoMappingXmlToObject(entry, dto);

					datas.add(dto);
				}

				LogUtil.D(datas.toString());

				mAdap = new MyRewradAdapter(mActivity, datas);

				mAdList.setAdapter(mAdap);

				mAdList.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub

					}
				});

			}
		}, false);

		 */
	}

	private void initUI() {

		mAdList = (ListView)findViewById(R.id.my_reward_list);
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
