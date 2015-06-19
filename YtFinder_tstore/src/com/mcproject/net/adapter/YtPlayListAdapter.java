package com.mcproject.net.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class YtPlayListAdapter extends BaseAdapter {

	private ArrayList<YTListDto> mDatas = new ArrayList<YTListDto>();
	private Context context;
	private LayoutInflater mInflater;
	private AQuery aq;
	private OnClickListener mvThumbClick;
	
	
	public YtPlayListAdapter(Context context, ArrayList<YTListDto> objects) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mDatas = objects;  
		aq = new AQuery(context);
	}
	
	public void insert(YTListDto item, int to){
		mDatas.add(to, item);
	}

	public void setMovieThumbClickListener(OnClickListener thumbClick){
		mvThumbClick = thumbClick;
	}
	
	public void remove(int position){
		mDatas.remove(position);
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
			convertView = mInflater.inflate(R.layout.playlist_item, null);
			
			holder.mv_thumb = (RecyclingImageView)convertView.findViewById(R.id.mv_thumb);
			holder.mv_title = (TextView)convertView.findViewById(R.id.mv_title);
//			holder.mv_desc = (TextView)convertView.findViewById(R.id.mv_desc);
			holder.mv_date = (TextView)convertView.findViewById(R.id.mv_date);
			holder.mv_channel_title = (TextView)convertView.findViewById(R.id.mv_channel_title);
			holder.mv_duration = (TextView)convertView.findViewById(R.id.mv_duration);
			holder.menu_icon = (RecyclingImageView)convertView.findViewById(R.id.menu_icon);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		YTListDto dto = getItem(position);

		holder.mv_title.setText(dto.title);
//		holder.mv_desc.setText(dto.description);
		
		AQuery aquery = aq.recycle(convertView);
		
		aquery.id(holder.mv_thumb).image(dto.thumbnail);
		HashMap<String, Object> dataObj = new HashMap<String, Object>();
		dataObj.put("position", position);
		dataObj.put("data", dto);
		holder.mv_thumb.setTag(dataObj);
		holder.mv_thumb.setOnClickListener(mvThumbClick);
		
		String date_text =  McUtil.parseDateString(context, dto.publish_date_origin);
		holder.mv_date.setText(date_text);
		
		holder.mv_channel_title.setText( context.getString(R.string.list_item_channel_title)  + dto.channel_title);
		holder.mv_duration.setText(context.getString(R.string.list_item_mv_duration) + dto.duration);
	
		return convertView;
	}

	private class Holder {
		RecyclingImageView mv_thumb, menu_icon;
		TextView mv_title, mv_date, mv_channel_title, mv_duration;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public YTListDto getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

}
