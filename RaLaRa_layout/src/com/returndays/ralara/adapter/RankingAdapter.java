package com.returndays.ralara.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.RankingDto;

public class RankingAdapter extends BaseAdapter {
	private ArrayList<RankingDto> mDatas = new ArrayList<RankingDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private AQuery aquery;
	@SuppressWarnings("unchecked")
	public RankingAdapter(Activity activity, ArrayList<RankingDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<RankingDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		aquery = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<RankingDto> datas) {
		this.mDatas = (ArrayList<RankingDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public RankingDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.ranking_item, null);

			holder.rank_icon = (RecyclingImageView)convertView.findViewById(R.id.rank_icon);
			holder.rank_img = (RecyclingImageView)convertView.findViewById(R.id.rank_img);
			holder.rank_num = (TextView)convertView.findViewById(R.id.rank_num);
			holder.rank_usernick = (TextView)convertView.findViewById(R.id.rank_usernick);
			holder.rank_point = (TextView)convertView.findViewById(R.id.rank_point);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		RankingDto dto = getItem(position);
		int rankNumber = Integer.parseInt(dto.RANK);
		if(rankNumber < 4){
			holder.rank_icon.setVisibility(View.VISIBLE);
			holder.rank_num.setVisibility(View.GONE);
			if(rankNumber == 1){
				holder.rank_icon.setImageResource(R.drawable.rank1_icon);
			}else if(rankNumber == 2){
				holder.rank_icon.setImageResource(R.drawable.rank2_icon);
			}else if(rankNumber == 3){
				holder.rank_icon.setImageResource(R.drawable.rank3_icon);
			}
		}else{
			holder.rank_icon.setVisibility(View.GONE);
			holder.rank_num.setVisibility(View.VISIBLE);
			holder.rank_num.setText(dto.RANK);
		}

		AQuery aq = aquery.recycle(convertView);
		if(dto.USER_IMG.equals("")){
			if(dto.SEX.equals("M")){
				aq.id(holder.rank_img).image(R.drawable.man_ico);
			}else if(dto.SEX.equals("F")){
				aq.id(holder.rank_img).image(R.drawable.woman_ico);
			}
		}else{
			ImageOptions opt = new ImageOptions();
			opt.round = 15;
			aq.id(holder.rank_img).image(dto.USER_IMG, opt);
		}
		
		holder.rank_usernick.setText(dto.USER_NICKNAME);
		holder.rank_point.setText(dto.SCOER + " 점");

		return convertView;
	}

	private class Holder {
		RecyclingImageView rank_icon, rank_img;
		TextView rank_num, rank_usernick, rank_point;

	}
}
