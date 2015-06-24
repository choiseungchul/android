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
import com.returndays.ralara.dto.MyRewardDto;
import com.returndays.ralara.util.StringUtil;

public class MyRewradAdapter extends BaseAdapter {
	private ArrayList<MyRewardDto> mDatas = new ArrayList<MyRewardDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private AQuery aquery;

	@SuppressWarnings("unchecked")
	public MyRewradAdapter(Activity activity, ArrayList<MyRewardDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<MyRewardDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.aquery = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<MyRewardDto> datas) {
		this.mDatas = (ArrayList<MyRewardDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public MyRewardDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.myreward_item, null);
			holder.adItem_container = (LinearLayout) convertView.findViewById(R.id.myreward_item_container);

			holder.title = (TextView) convertView.findViewById(R.id.ad_title);
			holder.thumbNail = (RecyclingImageView) convertView.findViewById(R.id.ad_thumb);
			holder.rightBtn = (RecyclingImageView)convertView.findViewById(R.id.goto_adbtn);
			holder.end_time = (TextView)convertView.findViewById(R.id.end_time);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		MyRewardDto dto = getItem(position);

		holder.title.setText("["+dto.CHC_COMP_NAME +"] " +  dto.PROD_NAME);
		if(dto.PIN_EXPIRE.equals("")){
			holder.end_time.setText("");
		}else{
			holder.end_time.setText("유효기간 : "+StringUtil.getDateYYYYMMDD(dto.PIN_EXPIRE));
		}
		AQuery aq = aquery.recycle(convertView);
		ImageOptions opt = new ImageOptions();
		opt.round = 15;
		aq.id(holder.thumbNail).image(dto.IMG_URL_150 , opt);

		return convertView;
	}


	private class Holder {
		RecyclingImageView thumbNail, rightBtn;
		TextView title, end_time;
		LinearLayout adItem_container;
	}
}
