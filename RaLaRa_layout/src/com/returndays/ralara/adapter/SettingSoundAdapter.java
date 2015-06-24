package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.returndays.ralara.R;
import com.returndays.ralara.dto.SettingSoundDto;

public class SettingSoundAdapter extends BaseAdapter {
	private ArrayList<SettingSoundDto> mDatas = new ArrayList<SettingSoundDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public SettingSoundAdapter(Activity activity, ArrayList<SettingSoundDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<SettingSoundDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<SettingSoundDto> datas) {
		this.mDatas = (ArrayList<SettingSoundDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public SettingSoundDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.setting_sound_item, null);

			holder.title = (TextView) convertView.findViewById(R.id.setting_alert_title);
			holder.check = (CheckBox)convertView.findViewById(R.id.setting_alert_chk);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		SettingSoundDto dto = getItem(position);

		holder.title.setText(dto.title);
		if(dto.selected){
			holder.check.setChecked(true);
		}else{
			holder.check.setChecked(false);
		}
		
		return convertView;
	}


	private class Holder {
		TextView title;
		CheckBox check;
	}

}
