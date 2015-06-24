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
import com.returndays.ralara.dto.TalkRoomTimeDto;

public class TalkRoomTimeAdapter extends BaseAdapter{
	
	Activity mActivity;
	ArrayList<TalkRoomTimeDto> mDatas;
	LayoutInflater mInflater;
	
	@SuppressWarnings("unchecked")
	public TalkRoomTimeAdapter( Activity activity, ArrayList<TalkRoomTimeDto> datas){
		this.mActivity = activity;
		this.mDatas = (ArrayList<TalkRoomTimeDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public TalkRoomTimeDto getItem(int position) {
		// TODO Auto-generated method stub
		return this.mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.talk_time_item, null);

			holder.title = (TextView) convertView.findViewById(R.id.setting_title);
			holder.check = (CheckBox)convertView.findViewById(R.id.setting_chk);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		TalkRoomTimeDto dto = getItem(position);

		holder.title.setText("알 " + dto.rcount + "   " + dto.minute + "분");
		
		return convertView;
		
	}

	private class Holder {
		TextView title;
		CheckBox check;
	}
}
