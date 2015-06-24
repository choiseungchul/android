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
import android.widget.CheckBox;
import android.widget.ListView;

import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.adapter.AdCallCategoryAdapter;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CallAdCategoryDto;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.MadUtil;

public class AdCallSettingActivity extends BaseActivity{

	ListView mListview;
	AdCallCategoryAdapter mAdap;
	Button save_btn;
	TextViewNanumGothic txt_btn;
	ArrayList<CallAdCategoryDto> mAdsFlag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adcall_setting);
		
		initUI();
		initData();
	}

	private void initData() {
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		final HttpDocument httpUtil = new HttpDocument(getApplicationContext());
		httpUtil.getDocument(UrlDef.COMPAIGN, params,null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				if(document != null && document.select("ReturnTable") != null) {
					//InOutCallAdQuery.deleteInOutAD(getApplicationContext());
					Elements entries = document.select("ReturnTable");
					ArrayList<CallAdDto> datas = new ArrayList<CallAdDto>();
					for(Element entry : entries) {
						CallAdDto dto = new CallAdDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
					}
					int r = entries.size();
					for(int i=0;i<r;i++) {
						CallAdDto dto = datas.get(i);
						if(Integer.parseInt(dto.AD_KIND) > 100 && Integer.parseInt(dto.AD_KIND) < 200){
							if(!InOutCallAdQuery.isExistAd(getApplicationContext(), datas.get(i).AD_SEQ)){
								InOutCallAdQuery.insertAdFlag(getApplicationContext(), dto.AD_SEQ, dto.AD_TITLE, dto.AD_TITLE, dto.END_ACTION, "Y");
							}
						}
					}
					
					httpUtil.threadStop();
				}
			}
		}, false);
		
		mAdsFlag = InOutCallAdQuery.selectInOutAdFlagList(getApplicationContext(), 0);
				
		mAdap = new AdCallCategoryAdapter(this, mAdsFlag);
		mListview.setAdapter(mAdap);
		
	}
	
	private void refreshContent(int position, CallAdCategoryDto dto){
		
		mAdsFlag.set(position, dto);
		
		mAdap.setData(mAdsFlag);
		mAdap.notifyDataSetChanged();
	}

	private void initUI() { 
		mListview = (ListView)findViewById(R.id.adcall_setting_list);
		
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> lv, View item, int position,
					long arg3) {
				CheckBox ck = (CheckBox)item.findViewById(R.id.adcall_set_chk);
				CallAdCategoryDto dto = (CallAdCategoryDto)lv.getItemAtPosition(position);
				if(ck.isChecked()){
					ck.setChecked(false);
					InOutCallAdQuery.updateAdFlag(getApplicationContext(), dto.ad_seq, "N");
					dto.is_view = "N";
				}else{
					ck.setChecked(true);
					InOutCallAdQuery.updateAdFlag(getApplicationContext(), dto.ad_seq, "Y");
					dto.is_view = "Y";
				}
				
				refreshContent(position, dto);
			}
		});
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);
		
		txt_btn = (TextViewNanumGothic) findViewById(R.id.top_sub_title_text_sub);
		txt_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAdsFlag.clear();
		super.onDestroy();
	}
}
