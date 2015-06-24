package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.FindFriendDto;

public class FindFriendAdapter extends BaseAdapter {
	private ArrayList<FindFriendDto> mDatas = new ArrayList<FindFriendDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private AQuery aquery;
	@SuppressWarnings("unchecked")
	public FindFriendAdapter(Activity activity, ArrayList<FindFriendDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<FindFriendDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		aquery = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<FindFriendDto> datas) {
		this.mDatas = (ArrayList<FindFriendDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public FindFriendDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.find_friend_item, null);

			holder.find_user_img = (RecyclingImageView)convertView.findViewById(R.id.find_user_img);
			holder.find_user_nick = (TextView)convertView.findViewById(R.id.find_user_nick);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		FindFriendDto dto = getItem(position);
		AQuery aq = aquery.recycle(convertView);
		if(dto.USER_IMG.equals("")){
			if(dto.SEX.equals("M")){
				aq.id(holder.find_user_img).image(R.drawable.man_ico);
			}else if(dto.SEX.equals("F")){
				aq.id(holder.find_user_img).image(R.drawable.woman_ico);
			}
		}else{
			aq.id(holder.find_user_img).image(dto.USER_IMG);
		}
		
		holder.find_user_nick.setText(dto.USER_NICKNAME);
		
		
		return convertView;
	}

	private class Holder {
		RecyclingImageView find_user_img;
		TextView find_user_nick;
	}
}
