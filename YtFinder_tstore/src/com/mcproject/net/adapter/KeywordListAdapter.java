package com.mcproject.net.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mcproject.net.dto.MySearchDto;
import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfavorite_t.R;

public class KeywordListAdapter extends BaseAdapter{

	private Context context;
	private LayoutInflater mInflater;
	private AQuery aq;
	private ArrayList<MySearchDto> mDatas;
	
	public KeywordListAdapter(Context context, ArrayList<MySearchDto> datas) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mDatas = datas;  
		aq = new AQuery(context);
	}

	public void remove(int index){
		mDatas.remove(index);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public MySearchDto getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDatas.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.keyword_item, null);
			
			holder.keyword_string = (TextView) convertView.findViewById(R.id.keyword_string);
			holder.keyword_datetime = (TextView) convertView.findViewById(R.id.keyword_datetime);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		MySearchDto dto = getItem(position);
		holder.keyword_string.setText(dto.search_text);
		String datetime = dto.search_datetime;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		Date tempdate;
		try {
			tempdate = sdf.parse(datetime);
			datetime = DateUtils.getRelativeDateTimeString (context, tempdate.getTime(), 0L, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
		} catch (ParseException e) {
			LogUtil.E(e.toString());
		}
		holder.keyword_datetime.setText(datetime);
		
		return convertView;
	}
	
	private class Holder{
		TextView keyword_string, keyword_datetime;
	}
	
}
