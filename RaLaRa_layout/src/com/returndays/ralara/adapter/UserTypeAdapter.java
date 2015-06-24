package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.returndays.ralara.R;
import com.returndays.ralara.dto.SettingAlertDto;

public class UserTypeAdapter extends BaseAdapter {

	private ArrayList<SettingAlertDto> mDatas = new ArrayList<SettingAlertDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public UserTypeAdapter(Activity activity, ArrayList<SettingAlertDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<SettingAlertDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<SettingAlertDto> datas) {
		this.mDatas = (ArrayList<SettingAlertDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public SettingAlertDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.user_type_item, null);

			holder.title = (com.returndays.customview.TextViewNanumGothic) convertView.findViewById(R.id.setting_alert_title);
			holder.title_desc = (com.returndays.customview.TextViewNanumGothic)convertView.findViewById(R.id.setting_alert_desc);
			holder.check = (RadioButton)convertView.findViewById(R.id.setting_alert_chk);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		SettingAlertDto dto = getItem(position);

		holder.title.setText(dto.title);
		holder.title_desc.setText(dto.title_desc);
		if(dto.isSet){
			holder.check.setChecked(true);
		}else{
			holder.check.setChecked(false);
		}

		return convertView;
	}

	private class Holder {
		com.returndays.customview.TextViewNanumGothic title, title_desc;
		RadioButton check;
	}

}
