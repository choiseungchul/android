package com.returndays.ralara;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.returndays.ralara.adapter.SettingAlertAdapter;
import com.returndays.ralara.dto.SettingAlertDto;
import com.returndays.ralara.preference.Setting;

public class SetAlertActivity extends BaseActivity{

	LinearLayout back_btn;
	SettingAlertAdapter mAdap;
	ListView mSetAlertList;
	Activity mActivity;
	Button save_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_alert);
		mActivity = this;	
		initUI();
		initData();

	}

	private void initData() {
		// TODO Auto-generated method stub
		
		ArrayList<SettingAlertDto> datas = new ArrayList<SettingAlertDto>();
		
		if(Setting.getPushAlarm(getApplicationContext()).equals("Y")){
			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_1), getResources().getString(R.string.setting_menu1_1_desc), true));
		}else{
			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_1), getResources().getString(R.string.setting_menu1_1_desc), false));
		}
//		if(Setting.getPushMessageAlarm(getApplicationContext()).equals("Y")){
//			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_2), getResources().getString(R.string.setting_menu1_2_desc), true));
//		}else{
//			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_2), getResources().getString(R.string.setting_menu1_2_desc), false));
//		}
		if(Setting.getPushScreenOff(getApplicationContext()).equals("Y")){
			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_3), getResources().getString(R.string.setting_menu1_3_desc), true));
		}else{
			datas.add(new SettingAlertDto(getResources().getString(R.string.setting_menu1_3), getResources().getString(R.string.setting_menu1_3_desc), false));
		}
		
		mAdap = new SettingAlertAdapter(mActivity, datas);
		
		mSetAlertList.setAdapter(mAdap);
		
		mSetAlertList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View vv, int position,
					long arg3) {
				
				CheckBox ck = (CheckBox)vv.findViewById(R.id.setting_alert_chk);
				if(ck.isChecked()){
					ck.setChecked(false);
					setAlarm(position, false);
				}else{
					ck.setChecked(true);
					setAlarm(position, true);
				}
				
			}
		});
	}

	private void initUI() {
		
		mSetAlertList = (ListView)findViewById(R.id.setting_alert_list);

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

	private void setAlarm(int pos, boolean set){
		
		if(pos == 0){
			if(set){
				Setting.setPushAlarm(getApplicationContext(), "Y");
			}else{
				Setting.setPushAlarm(getApplicationContext(), "");
			}
		}else if(pos == 1){
			if(set){
				Setting.setPushMessageAlarm(getApplicationContext(), "Y");
			}else{
				Setting.setPushMessageAlarm(getApplicationContext(), "");
			}
		}else if(pos == 2){
			if(set){
				Setting.setPushScreenOff(getApplicationContext(), "Y");
			}else{
				Setting.setPushScreenOff(getApplicationContext(), "");
			}
		}
	}

}
