package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.returndays.customview.TextViewNanumGothic;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.RecommFriendDto;
import com.returndays.ralara.util.StringUtil;

public class FriendListAdapter extends BaseAdapter {
	private ArrayList<RecommFriendDto> mDatas = new ArrayList<RecommFriendDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private OnClickListener mSaveClick;
	@SuppressWarnings("unchecked")
	public FriendListAdapter(Activity activity, ArrayList<RecommFriendDto> datas, OnClickListener saveClick) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<RecommFriendDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.mSaveClick = saveClick;
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<RecommFriendDto> datas) {
		this.mDatas = (ArrayList<RecommFriendDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public RecommFriendDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.recomm_item, null);

			holder.friend_name = (TextViewNanumGothic)convertView.findViewById(R.id.friend_name);
			holder.friend_recomm_btn = (Button)convertView.findViewById(R.id.friend_recomm_btn);
			holder.friend_hp = (TextViewNanumGothic)convertView.findViewById(R.id.friend_hp);

			convertView.setTag(holder);
			
		} else {
			holder = (Holder) convertView.getTag();
		}

		RecommFriendDto dto = getItem(position);
		dto.index = position;
		
//		LogUtil.D("position = " + position);

		holder.friend_name.setText(dto.FRIEND_NM);
		
		String fr_hp = dto.FRIEND_HP;
		fr_hp = StringUtil.makePhoneNumber(fr_hp);
		
		holder.friend_hp.setText(fr_hp);
		holder.friend_recomm_btn.setTag(dto);
		holder.friend_recomm_btn.setOnClickListener(mSaveClick);
		if(dto.RESULT.equals("0")){
			holder.friend_recomm_btn.setText("추천");
			holder.friend_recomm_btn.setBackgroundResource(R.drawable.button_purple);
		}else if(dto.RESULT.equals("2")){
			holder.friend_recomm_btn.setText("확인");
			holder.friend_recomm_btn.setBackgroundResource(R.drawable.button_gray);			
		}

		return convertView;
	}


	private class Holder {
		TextViewNanumGothic friend_name, friend_hp;
		Button friend_recomm_btn;
	}
}
