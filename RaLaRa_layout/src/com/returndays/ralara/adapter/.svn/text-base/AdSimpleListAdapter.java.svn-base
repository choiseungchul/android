package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.AQUtility;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.AdSimpleDto;

public class AdSimpleListAdapter extends BaseAdapter {
	private ArrayList<AdSimpleDto> mDatas = new ArrayList<AdSimpleDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	
	private AQuery aquery;
	@SuppressWarnings("unchecked")
	public AdSimpleListAdapter(Activity activity, ArrayList<AdSimpleDto> datas, OnClickListener saveClick) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<AdSimpleDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.aquery = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<AdSimpleDto> datas) {
		this.mDatas = (ArrayList<AdSimpleDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public AdSimpleDto getItem(int position) {
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

			holder.ad_gubun_cont = (LinearLayout)convertView.findViewById(R.id.ad_gubun_cont);

			holder.title = (TextView) convertView.findViewById(R.id.ad_title);
			holder.gubun_title = (TextView)convertView.findViewById(R.id.ad_gubun);
			holder.thumbNail = (RecyclingImageView) convertView.findViewById(R.id.ad_thumb);
			holder.ad_win_cnt = (TextView)convertView.findViewById(R.id.ad_win_cnt);
			holder.ad_win_total = (TextView)convertView.findViewById(R.id.ad_win_total);
			holder.rightBtn = (RecyclingImageView)convertView.findViewById(R.id.goto_adbtn);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		AdSimpleDto dto = getItem(position);

		// 당첨가능인원수 계산
		int next_win_count = Integer.parseInt(dto.WIN_CNT) - Integer.parseInt(dto.WIN_CNT_CUR);
		
		holder.title.setText(dto.AD_TITLE);
		holder.ad_win_cnt.setText(String.valueOf(next_win_count));
		holder.ad_win_total.setText(dto.WIN_CNT);

		AQUtility.cleanCacheAsync(mActivity);
		
		AQuery aq = aquery.recycle(convertView);
		
		ImageOptions opt = new ImageOptions();
		opt.round = 15;
		holder.thumbNail.setImageDrawable(null);
		aq.id(holder.thumbNail).image(dto.THUM_IMG_HOST + dto.THUM_IMG, opt);

		if(dto.IS_GROUP_TYPE.equals("1")){
			holder.ad_gubun_cont.setVisibility(View.VISIBLE);
			holder.gubun_title.setText(R.string.ad_earn);
		}else if(dto.IS_GROUP_TYPE.equals("2")){
			holder.ad_gubun_cont.setVisibility(View.VISIBLE);
			holder.gubun_title.setText(R.string.ad_earned);
		}else if(dto.IS_GROUP_TYPE.equals("3")){
			holder.ad_gubun_cont.setVisibility(View.VISIBLE);
			holder.gubun_title.setText(R.string.ad_end_msg);
		}else{
			holder.ad_gubun_cont.setVisibility(View.GONE);
		}

		return convertView;
	}


	private class Holder {
		RecyclingImageView thumbNail, rightBtn;
		TextView title, ad_win_total, ad_win_cnt, gubun_title;
		LinearLayout ad_gubun_cont;
	}
}
