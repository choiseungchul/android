package com.mcproject.net.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.FavUploaderDto;
import com.mcproject.ytfavorite_t.R;

public class UploaderListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<FavUploaderDto> mDatas;
	private OnClickListener onItemClick;
	private OnClickListener onTitleClick;
	
	public UploaderListAdapter(Context context, ArrayList<FavUploaderDto> datas) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mDatas = datas;
	}
	
	public void setOnItemClick(OnClickListener click){
		this.onItemClick = click;
	}
	
	public void setOnTitleClick(OnClickListener titleClick){
		this.onTitleClick = titleClick;
	}
	
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position).hashCode();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.favorite_uploader_list_item, null);

			holder.mv_count = (TextView)convertView.findViewById(R.id.mv_count);
			holder.uploader_title = (TextView)convertView.findViewById(R.id.uploader_title);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		FavUploaderDto dto = getItem(position);
		
		if(dto.count != null){
			if(dto.count.equals("0")){
				holder.mv_count.setVisibility(View.GONE);
			}else{
				holder.mv_count.setVisibility(View.VISIBLE);
				holder.mv_count.setText(dto.count);
			}
		}
		else holder.mv_count.setVisibility(View.GONE);
		
		holder.uploader_title.setText(dto.title);
		holder.uploader_title.setTag(dto);
		holder.uploader_title.setOnClickListener(onItemClick);
		
		holder.uploader_title.setTag(dto);
		holder.uploader_title.setOnClickListener(onTitleClick);
		
		return convertView;
	}

	private class Holder {
		TextView mv_count, uploader_title;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public FavUploaderDto getItem(int position) {
		return mDatas.get(position);
	}
	
	public void remove(int position){
		mDatas.remove(position);
		notifyDataSetChanged();
	}
	
	public void insert(FavUploaderDto item, int to){
		mDatas.add(to, item);
	}
	
}
