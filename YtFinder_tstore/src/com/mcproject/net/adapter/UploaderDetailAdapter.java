package com.mcproject.net.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class UploaderDetailAdapter extends BaseAdapter{

	private LayoutInflater inflater;
	private Context ctx;
	private ArrayList<CollectedDto> datas;
	private AQuery aq;
	private OnClickListener movieClick;
	private OnClickListener movieAddClick;

	public UploaderDetailAdapter(Context ctx, ArrayList<CollectedDto> datas){
		this.inflater = LayoutInflater.from(ctx);
		this.ctx = ctx;
		this.datas = datas;
		this.aq = new AQuery(ctx);
	}
	
	public void setMovieClick(OnClickListener listener){
		movieClick = listener;
	}
	public void setAddPlayListClick(OnClickListener listener){
		movieAddClick = listener;
	}

	public void setData(ArrayList<CollectedDto> addData){
		this.datas = addData;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public CollectedDto getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.uploader_detail_item, null);

			holder.mv_thumb = (RecyclingImageView)convertView.findViewById(R.id.mv_thumb);
			holder.mv_title = (TextView)convertView.findViewById(R.id.mv_title);
			holder.mv_date = (TextView)convertView.findViewById(R.id.mv_date);
			holder.mv_duration = (TextView)convertView.findViewById(R.id.mv_duration);
			holder.add_playlist = (RecyclingImageView)convertView.findViewById(R.id.add_playlist);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		CollectedDto dto = getItem(position);

		if(dto != null){
			holder.mv_title.setText(dto.title);

			AQuery aquery = aq.recycle(convertView);
			aquery.id(holder.mv_thumb).image(dto.thumbnail);
			
			// 데이터 입력
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("position", position);
			data.put("data", dto);
			
			holder.mv_thumb.setTag(data);
			holder.mv_thumb.setOnClickListener(movieClick);

			if(dto.isPlaylist){
				holder.add_playlist.setImageResource(R.drawable.fav_playlist_added);
			}else{
				holder.add_playlist.setImageResource(R.drawable.fav_playlist_not_added);
			}

			holder.add_playlist.setTag(data);
			holder.add_playlist.setOnClickListener(movieAddClick);

			String upload_time = McUtil.parseDateString(ctx, dto.publish_date_origin);

			holder.mv_date.setText(upload_time);

			holder.mv_duration.setText(ctx.getString(R.string.list_item_mv_duration) + dto.duration);
		}


		return convertView;
	}

	private class Holder{
		RecyclingImageView mv_thumb, add_playlist;
		TextView mv_title, mv_date, mv_duration;
	}

}
