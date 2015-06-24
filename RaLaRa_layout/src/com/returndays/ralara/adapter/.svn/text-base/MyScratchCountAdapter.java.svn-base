package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.MyScratchDto;

public class MyScratchCountAdapter extends BaseAdapter {
	private ArrayList<MyScratchDto> mDatas = new ArrayList<MyScratchDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private AQuery aquery;

	@SuppressWarnings("unchecked")
	public MyScratchCountAdapter(Activity activity, ArrayList<MyScratchDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<MyScratchDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.aquery = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<MyScratchDto> datas) {
		this.mDatas = (ArrayList<MyScratchDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public MyScratchDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.scratch_count_item, null);
			holder.adItem_container = (LinearLayout) convertView.findViewById(R.id.item_container);

			holder.title = (TextView) convertView.findViewById(R.id.ad_title);
			holder.thumbNail = (RecyclingImageView) convertView.findViewById(R.id.ad_thumb);
			holder.rightBtn = (RecyclingImageView)convertView.findViewById(R.id.goto_adbtn);
			holder.regdate = (TextView) convertView.findViewById(R.id.scratch_reg_date);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		MyScratchDto dto = getItem(position);

		holder.title.setText(dto.AD_TITLE);
		if(!dto.REG_DATE.equals("")){
			holder.regdate.setText(dto.REG_DATE.substring(0, 10));
		}

		AQuery aq = aquery.recycle(convertView);
		
		if(dto.AD_IMG.equals("")){
			aq.id(holder.thumbNail).image(R.drawable.ralara_default_listview_img);
			holder.title.setText("알변환 스크래치");
		}else{
			ImageOptions opt = new ImageOptions();
			opt.round = 15;
			aq.id(holder.thumbNail).image(dto.AD_IMG , opt);			
		}

		return convertView;
	}


	private class Holder {
		RecyclingImageView thumbNail, rightBtn;
		TextView title;
		LinearLayout adItem_container;
		TextView regdate;
	}
}
