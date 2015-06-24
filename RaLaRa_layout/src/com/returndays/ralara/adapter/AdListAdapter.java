package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.returndays.ralara.R;
import com.returndays.ralara.dto.AdDto;

public class AdListAdapter extends BaseAdapter {
	private ArrayList<AdDto> mDatas = new ArrayList<AdDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private OnClickListener mSaveClick;
	@SuppressWarnings("unchecked")
	public AdListAdapter(Activity activity, ArrayList<AdDto> datas, OnClickListener saveClick) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<AdDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.mSaveClick = saveClick;

	}
	
	@SuppressWarnings("unchecked")
	public void setData(ArrayList<AdDto> datas) {
		this.mDatas = (ArrayList<AdDto>) datas.clone();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public AdDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.adlist_item, null);
			holder.adItem_container = (LinearLayout) convertView.findViewById(R.id.item_container);
			
			holder.title = (TextView) convertView.findViewById(R.id.ad_title);
			
			holder.gubun_tit = (TextView)convertView.findViewById(R.id.ad_gubun);
			
			holder.thumbNail = (ImageView) convertView.findViewById(R.id.ad_thumb);
			holder.ad_win_cnt = (TextView)convertView.findViewById(R.id.ad_win_cnt);
			holder.ad_win_total = (TextView)convertView.findViewById(R.id.ad_win_total);
			holder.rightBtn = (ImageView)convertView.findViewById(R.id.goto_adbtn);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		AdDto dto = getItem(position);
		
		
		return convertView;
	}
	
	
	private class Holder {
		ImageView thumbNail;
		ImageView rightBtn;
		TextView title, ad_win_total, ad_win_cnt, gubun_tit;
		LinearLayout adItem_container;
	}
}
