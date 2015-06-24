package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.SettingInfoDto;

@SuppressLint("NewApi")
public class SettingListAdapter extends BaseAdapter {
	private ArrayList<SettingInfoDto> mDatas = new ArrayList<SettingInfoDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public SettingListAdapter(Activity activity, ArrayList<SettingInfoDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<SettingInfoDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<SettingInfoDto> datas) {
		this.mDatas = (ArrayList<SettingInfoDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public SettingInfoDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.setting_item, null);

			holder.title = (TextView) convertView.findViewById(R.id.setting_list_tit);
			holder.gubun_title = (TextView)convertView.findViewById(R.id.setting_gubun_title);
			holder.check = (CheckBox)convertView.findViewById(R.id.setting_chk);
			holder.my_gubun_cont = (LinearLayout)convertView.findViewById(R.id.setting_gubun);
			holder.right_title = (TextView)convertView.findViewById(R.id.setting_list_right_title);
			holder.setting_update_info = (RecyclingImageView)convertView.findViewById(R.id.setting_update_info);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		SettingInfoDto dto = getItem(position);

		if(dto.UPDATE_ALERT.equals("Y")){
			holder.setting_update_info.setVisibility(View.VISIBLE);
		}else{
			holder.setting_update_info.setVisibility(View.GONE);
		}
		
		if(dto.RIGHT_TITLE.equals("")){
			holder.right_title.setVisibility(View.GONE);
		}else{
			holder.right_title.setVisibility(View.VISIBLE);
			holder.right_title.setText(dto.RIGHT_TITLE);
		}
		if(dto.CHECKBOX.equals("")){
			holder.check.setVisibility(View.GONE);
		}else{
			
			holder.check.setVisibility(View.VISIBLE);
			//holder.check.setText(dto.CHECKBOX);
			
			holder.check.setChecked(dto.isChecked);
			
		}
		if(dto.GUBUNSTR.equals("")){
			holder.my_gubun_cont.setVisibility(View.GONE);
		}else{
			holder.my_gubun_cont.setVisibility(View.VISIBLE);
			holder.gubun_title.setText(dto.GUBUNSTR);
		}

		holder.title.setText(dto.TITLE);
		
		return convertView;
	}


	private class Holder {
		TextView title, right_title, gubun_title;
		RecyclingImageView setting_update_info;
		CheckBox check;
		LinearLayout my_gubun_cont;
	}

}
