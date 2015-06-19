package com.mcproject.net.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mcproject.net.dto.OthersListDto;
import com.mcproject.ytfavorite_t.R;

public class OthersListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private ArrayList<OthersListDto> mDatas;
	
	public OthersListAdapter(Context context, ArrayList<OthersListDto> datas) {
		this.mInflater = LayoutInflater.from(context);
		this.context = context;
		this.mDatas = datas;
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
			convertView = mInflater.inflate(R.layout.others_list_item, null);

			holder.others_title = (TextView)convertView.findViewById(R.id.others_title);
			holder.others_setting = (TextView)convertView.findViewById(R.id.others_setting);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		OthersListDto dto = getItem(position);
		
		holder.others_title.setText(dto.others_title);
		holder.others_setting.setText(dto.others_set_text);
		
		return convertView;
	}

	private class Holder {
		TextView others_title, others_setting;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public OthersListDto getItem(int position) {
		return mDatas.get(position);
	}
	
	public void setData(ArrayList<OthersListDto> datas){
		mDatas = datas;
	}
}
