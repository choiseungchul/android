package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.returndays.ralara.R;
import com.returndays.ralara.dto.MyPageInfoDto;

public class MyPageListAdapter extends BaseAdapter {
	private ArrayList<MyPageInfoDto> mDatas = new ArrayList<MyPageInfoDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public MyPageListAdapter(Activity activity, ArrayList<MyPageInfoDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<MyPageInfoDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<MyPageInfoDto> datas) {
		this.mDatas = (ArrayList<MyPageInfoDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public MyPageInfoDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.mypage_item, null);

			holder.title = (TextView) convertView.findViewById(R.id.mypage_list_tit);
			holder.gubun_title = (TextView)convertView.findViewById(R.id.mypage_gubun_title);
			holder.check = (CheckBox)convertView.findViewById(R.id.mypage_list_check);
			holder.my_gubun_cont = (LinearLayout)convertView.findViewById(R.id.mypage_gubun);
			holder.count = (TextView)convertView.findViewById(R.id.mypage_list_count);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		MyPageInfoDto dto = getItem(position);

		if(dto.COUNT.equals("")){
			holder.count.setVisibility(View.GONE);
		}else{
			holder.count.setVisibility(View.VISIBLE);
			holder.count.setText(dto.COUNT);
		}
		if(dto.CHECKBOX.equals("")){
			holder.check.setVisibility(View.GONE);
		}else{
			holder.check.setVisibility(View.VISIBLE);
			holder.check.setText(dto.CHECKBOX);
		}
		if(dto.GUBUNSTR.equals("")){
			holder.my_gubun_cont.setVisibility(View.GONE);
		}else{
			holder.my_gubun_cont.setVisibility(View.VISIBLE);
			holder.gubun_title.setText(dto.GUBUNSTR);
		}

		if(dto.ID == 6){
			// 텍스트 더 크게
			holder.title.setTextColor(mActivity.getResources().getColor(R.color.purple_text));
			holder.title.setTypeface(null, Typeface.BOLD);
		}
		if(dto.ID == 8){
			// 텍스트 더 크게
			holder.title.setTextColor(mActivity.getResources().getColor(R.color.brown_kakao));
			holder.title.setTypeface(null, Typeface.BOLD);
		}
		
		
		holder.title.setText(dto.TITLE);
	
		
		return convertView;
	}


	private class Holder {
		TextView title, count, gubun_title;
		CheckBox check;
		LinearLayout my_gubun_cont;
	}

}
