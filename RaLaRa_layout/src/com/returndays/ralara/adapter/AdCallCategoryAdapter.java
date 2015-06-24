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
import com.returndays.ralara.dto.CallAdCategoryDto;

public class AdCallCategoryAdapter extends BaseAdapter {
	private ArrayList<CallAdCategoryDto> mDatas = new ArrayList<CallAdCategoryDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public AdCallCategoryAdapter(Activity activity, ArrayList<CallAdCategoryDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<CallAdCategoryDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<CallAdCategoryDto> datas) {
		this.mDatas = (ArrayList<CallAdCategoryDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public CallAdCategoryDto getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.adcall_setting_item, null);

			holder.title = (TextView) convertView.findViewById(R.id.adcall_set_title);
			holder.check = (CheckBox)convertView.findViewById(R.id.adcall_set_chk);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final CallAdCategoryDto dto = getItem(position);

		holder.title.setText(dto.ad_title);
		if(dto.is_view.equals("Y")){
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
