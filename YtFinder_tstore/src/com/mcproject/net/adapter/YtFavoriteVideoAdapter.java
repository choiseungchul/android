package com.mcproject.net.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.ytfavorite_t.R;

public class YtFavoriteVideoAdapter extends BaseAdapter {

	private List<YTListDto> mDatas = new ArrayList<YTListDto>();
	private Context context;
	private LayoutInflater mInflater;
	private AQuery aq;
	private OnClickListener mvThumbClick;
	private OnClickListener mvFaviconClick;
	private OnClickListener mvUploaderClick;
	
//	@SuppressWarnings("unchecked")
//	public YtListAdapter(Activity activity, ArrayList<YTListDto> datas) {
//		this.mActivity = activity;
//		this.mDatas = (ArrayList<YTListDto>) datas.clone();
//		this.mInflater = mActivity.getLayoutInflater();
//		aq = new AQuery(activity);
//	}
	
	public YtFavoriteVideoAdapter(final Context context, List<YTListDto> datas) {
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.mDatas = datas;  
		aq = new AQuery(context);
	}
	
	public void setMovieThumbClickListener(OnClickListener thumbClick){
		mvThumbClick = thumbClick;
	}
	
	public void setFaviconClickListener(OnClickListener favClick){
		mvFaviconClick = favClick;
	}
	
	public void setUploaderClickListener(OnClickListener uploaderClick){
		mvUploaderClick = uploaderClick;
	}

	public void remove(int location){
		mDatas.remove(location);
		notifyDataSetChanged();
	}
	
	public void setData(List<YTListDto> datas) {
		this.mDatas = datas;
		//(List<YTListDto>) datas.clone();
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
			convertView = mInflater.inflate(R.layout.favorite_video_item, null);
			
			holder.mv_thumb = (RecyclingImageView)convertView.findViewById(R.id.mv_thumb);
			holder.mv_title = (TextView)convertView.findViewById(R.id.mv_title);
//			holder.mv_desc = (TextView)convertView.findViewById(R.id.mv_desc);
			holder.mv_date = (TextView)convertView.findViewById(R.id.mv_date);
			holder.mv_channel_title = (TextView)convertView.findViewById(R.id.mv_channel_title);
			holder.mv_duration = (TextView)convertView.findViewById(R.id.mv_duration);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		YTListDto dto = getItem(position);

		holder.mv_title.setText(dto.title);
//		holder.mv_desc.setText(dto.description);
		
		AQuery aquery = aq.recycle(convertView);
		
		HashMap<String, Object> dataObj = new HashMap<String, Object>();
		dataObj.put("position", position);
		dataObj.put("data", dto);
		
		aquery.id(holder.mv_thumb).image(dto.thumbnail);
		holder.mv_thumb.setTag(dataObj);
		holder.mv_thumb.setOnClickListener(mvThumbClick);
		
		holder.mv_date.setText(dto.publish_date);
		
		if(dto.isFavoriteUploader){
			holder.mv_channel_title.setBackgroundResource(R.drawable.title_bar_bg);
			holder.mv_channel_title.setText( context.getString(R.string.list_item_uploader_added) + dto.channel_title);
		}else{
			holder.mv_channel_title.setBackgroundResource(R.drawable.title_bar_bg_off);
			holder.mv_channel_title.setText(context.getString(R.string.list_item_channel_title) + dto.channel_title);
		}
		holder.mv_channel_title.setTag(dataObj);
		holder.mv_channel_title.setOnClickListener(mvUploaderClick);
		
		holder.mv_duration.setText( context.getString(R.string.list_item_mv_duration) + dto.duration);
	
		return convertView;
	}

	private class Holder {
		RecyclingImageView mv_thumb;
		TextView mv_title, mv_date, mv_channel_title, mv_duration;
		//mv_desc
	}
}
