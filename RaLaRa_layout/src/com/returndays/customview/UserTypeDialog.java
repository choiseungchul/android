package com.returndays.customview;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.customview.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.returndays.ralara.R;
import com.returndays.ralara.adapter.UserTypeAdapter;
import com.returndays.ralara.dto.SettingAlertDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;


public class UserTypeDialog extends Dialog{

	TextViewNanumGothic mTitle, mBtn1, mBtn2;
	ListView user_type_list;
	UserTypeAdapter mAdap;
	Activity ctx;
	ArrayList<SettingAlertDto> mList;
	LayoutInflater inflate;
	LinearLayout lock_ad_time, callend_ad_time;
	int type = -1;
	int lock_ad_start = 0, lock_ad_end = 24;
	int callend_ad_start = 0, callend_ad_end = 24;
	View range_seek;
	TextView lock_ad_min, lock_ad_max, callend_ad_min, callend_ad_max;


	public UserTypeDialog(final Activity context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);

		ctx = context;
		inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setContentView(R.layout.user_type_dialog);

		mTitle = (TextViewNanumGothic)findViewById(R.id.dialog_title);
		user_type_list = (ListView)findViewById(R.id.user_type_list);
		mBtn1 = (TextViewNanumGothic)findViewById(R.id.dialog_ok);
		mBtn2 = (TextViewNanumGothic)findViewById(R.id.dialog_cancel);
		
		setBackgroundGrayOver();

		mBtn2.setOnClickListener(new View.OnClickListener (){
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		mBtn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type != -1){
					// 타입별 설정
					if(type == 0){
						// 전체보기 모드
						Setting.setCallAd(context, "ON");
						Setting.setSlideAd(context, "ON");	
					}else if(type == 1){
						// 심플모드
						Setting.setSlideAd(context, "OFF");
						Setting.setCallAd(context, "OFF");
					}else if(type == 2){
						// 잠금화면 광고 모드
						Setting.setSlideAd(context, "ON");
						Setting.setCallAd(context, "OFF");

					}else if(type == 3){
						// 수발신 광고 모드
						Setting.setCallAd(context, "ON");
						Setting.setSlideAd(context, "OFF");
						
					}else if(type == 4){
						// 시간설정 모드
						Setting.setCallAd(context, "ON");
						Setting.setSlideAd(context, "ON");

						// 시간저장
						Setting.setSlideAdPeriod(context, lock_ad_start + ":" + lock_ad_end);
						Setting.setCallAdPeriod(context, callend_ad_start + ":" + callend_ad_end);

					}

					Setting.setUserType(context, String.valueOf(type));

					dismiss();

					Toast.makeText(context, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(context, "모드를 지정해주세요.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		mList = new ArrayList<SettingAlertDto>();
		
		mList.add(new SettingAlertDto("전체보기 모드", "잠금화면 O, 수발신 광고 O \n당첨확률이 가장 높습니다.", false));
		mList.add(new SettingAlertDto("심플 모드", "수발신 광고 X , 잠금화면 광고 X \n당첨확률이 가장 낮습니다", false));
		mList.add(new SettingAlertDto("잠금화면광고 모드", "수발신 광고 X, 잠금화면 광고 O\n당첨확률이 보통입니다", false));
		mList.add(new SettingAlertDto("수발신광고 모드", "수발신 광고 O, 잠금화면 광고 X\n알을 모으기 쉽습니다.", false));
		
//		mList.add(new SettingAlertDto("시간설정 모드", "광고가 노출되는 시간을 설정할 수 있습니다. ", false));

		String mode = Setting.getUserType(context);
		//Toast.makeText(context, "mode."+ mode, Toast.LENGTH_SHORT).show();
		
		
		if(!mode.equals("")){
			int pos = Integer.parseInt(mode);
			SettingAlertDto dto = mList.get(pos);
			dto.isSet = true;
			mList.set(pos, dto);
			type = pos;
		}

		// 푸터부분
		range_seek = inflate.inflate(R.layout.user_type_seekbar, null);
		LinearLayout lock_ad_time = (LinearLayout)range_seek.findViewById(R.id.lock_ad_time);
		LinearLayout callend_ad_time = (LinearLayout)range_seek.findViewById(R.id.callend_ad_time);
		lock_ad_min = (TextView)range_seek.findViewById(R.id.lock_ad_min);
		lock_ad_max = (TextView)range_seek.findViewById(R.id.lock_ad_max);
		callend_ad_min = (TextView)range_seek.findViewById(R.id.callend_ad_min);
		callend_ad_max = (TextView)range_seek.findViewById(R.id.callend_ad_max);

		LogUtil.D("Setting.getSlideAdPeriod => " + Setting.getSlideAdPeriod(context));
		LogUtil.D("Setting.getCallAdPeriod => " + Setting.getCallAdPeriod(context));

		RangeSeekBar<Integer> lock_ad = new RangeSeekBar<Integer>(0, 24, context);
		if(Setting.getSlideAdPeriod(context).equals("")){
			lock_ad.setSelectedMinValue(0);
			lock_ad.setSelectedMaxValue(24);
		}else{
			int st = Integer.parseInt(Setting.getSlideAdPeriod(context).split(":")[0]);
			int ed= Integer.parseInt(Setting.getSlideAdPeriod(context).split(":")[1]);
			LogUtil.D("st = " + st + " / ed = " + ed);
			lock_ad.setSelectedMinValue(st);
			lock_ad.setSelectedMaxValue(ed);
			lock_ad_min.setText(st + "시");
			lock_ad_max.setText(ed + "시");
			lock_ad_start = st;
			lock_ad_end = ed;
		}
		lock_ad.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				lock_ad_start = minValue;
				lock_ad_end = maxValue;
				lock_ad_min.setText(minValue + "시");
				lock_ad_max.setText(maxValue + "시");
			}
		});
		RangeSeekBar<Integer> callend_ad = new RangeSeekBar<Integer>(0, 24, context);
		if(Setting.getCallAdPeriod(context).equals("")){
			callend_ad.setSelectedMinValue(0);
			callend_ad.setSelectedMaxValue(24);
		}else{
			int st = Integer.parseInt(Setting.getCallAdPeriod(context).split(":")[0]);
			int ed= Integer.parseInt(Setting.getCallAdPeriod(context).split(":")[1]);
			LogUtil.D("st = " + st + " / ed = " + ed);
			callend_ad.setSelectedMinValue(st);
			callend_ad.setSelectedMaxValue(ed);
			callend_ad_min.setText(st + "시");
			callend_ad_max.setText(ed + "시");
			callend_ad_start = st;
			callend_ad_end = ed;
		}
		callend_ad.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
					Integer minValue, Integer maxValue) {
				callend_ad_start = minValue;
				callend_ad_end = maxValue;
				callend_ad_min.setText(minValue + "시");
				callend_ad_max.setText(maxValue + "시");
			}
		});
		lock_ad_time.addView(lock_ad);
		callend_ad_time.addView(callend_ad);

		mAdap = new UserTypeAdapter(context, mList);
//		user_type_list.addFooterView(range_seek);
		user_type_list.setAdapter(mAdap);
		user_type_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		user_type_list.setFooterDividersEnabled(false);
		user_type_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View vv, int position, long arg3) {

				for(int i = 0 ; i < mList.size(); i++){
					SettingAlertDto dto = mList.get(i);
					dto.isSet = false;
					mList.set(i, dto);
				}

				SettingAlertDto dto = (SettingAlertDto)arg0.getItemAtPosition(position);

				RadioButton ck = (RadioButton)vv.findViewById(R.id.setting_alert_chk);
				ck.setChecked(true);
				dto.isSet =true;
				
				/*if(ck.isChecked()){
					
				}else{
					ck.setChecked(true);
					dto.isSet = true;
				}
				*/

				mList.set(position, dto);
				mAdap.notifyDataSetChanged();

				if(position == 4){
					if(ck.isChecked()){
						range_seek.setVisibility(View.VISIBLE);
						user_type_list.setSelectionFromTop(user_type_list.getCount()-1, user_type_list.getScrollY());
					}else{
						range_seek.setVisibility(View.GONE);
					}
				}else{
					range_seek.setVisibility(View.GONE);
				}

				type = position;
			}
		});

		if(Setting.getUserType(context).equals("4")){
			range_seek.setVisibility(View.VISIBLE);
			user_type_list.setSelectionFromTop(user_type_list.getCount()-1, user_type_list.getScrollY());
		}else{
			range_seek.setVisibility(View.GONE);
			user_type_list.setSelectionFromTop(type, user_type_list.getScrollY());
		}
	}
	
	

	public void setBackgroundWhite(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}

	public void setTitle(String title){
		mTitle.setText(title);
	}

	public void isNegativeButton(boolean flag){
		if(!flag){
			mBtn2.setVisibility(View.GONE);
		}else{
			mBtn2.setVisibility(View.VISIBLE);
		}
	}

	public void setPositiveButton(String title, View.OnClickListener listener){
		mBtn1.setText(title);
		mBtn1.setOnClickListener( listener);
	}

	public void setNagativeButton(String title, View.OnClickListener listener){
		mBtn2.setText(title);
		mBtn2.setOnClickListener( listener);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
}