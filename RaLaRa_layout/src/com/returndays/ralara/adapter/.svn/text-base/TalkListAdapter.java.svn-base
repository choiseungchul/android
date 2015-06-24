package com.returndays.ralara.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.returndays.customview.SquareImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.TalkListDto;
import com.returndays.ralara.util.StringUtil;

public class TalkListAdapter extends BaseAdapter {
	private ArrayList<TalkListDto> mDatas = new ArrayList<TalkListDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	private AQuery listAq;

	@SuppressWarnings("unchecked")
	public TalkListAdapter(Activity activity, ArrayList<TalkListDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<TalkListDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		this.listAq = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<TalkListDto> datas) {
		this.mDatas = (ArrayList<TalkListDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public TalkListDto getItem(int position) {
		// TODO Auto-generated method stub
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if(convertView == null) {
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.talk_room, null);

			holder.title = (TextView) convertView.findViewById(R.id.talk_room_title);
			holder.thum = (SquareImageView)convertView.findViewById(R.id.talk_room_img);
			holder.talk_room_frame = (FrameLayout)convertView.findViewById(R.id.talk_room_frame);
			holder.talk_room_info_text = (TextView)convertView.findViewById(R.id.talk_room_info_text);
			holder.talk_room_stopped = (SquareImageView)convertView.findViewById(R.id.talk_room_stopped);

			convertView.setTag(holder);
		} else { 
			holder = (Holder) convertView.getTag();
		}

		TalkListDto dto = getItem(position);

		holder.talk_room_stopped.setVisibility(View.GONE);
		
		if(dto.TYPE.equals("N")){
			AQuery aq = listAq.recycle(convertView);

			aq.id(holder.thum).image(dto.ThumbNail, true, true, 150, 0, null, 0, 1.0f/1.0f);

			// 남은시간 설정
			DateFormat expire_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);

			try {

				Date tempDate = expire_date.parse(dto.EXPIRE_DATE.replace("T", " "));
				
				long time = tempDate.getTime() - (Calendar.getInstance(Locale.KOREA).getTime().getTime());

				long expired = time;
				
				holder.talk_room_info_text.setText("남은시간 : " + StringUtil.getTimeString(expired)  + "(" + dto.ROOM_USERS_COUNT + " 명 참가중) ");
				holder.talk_room_info_text.setSelected(true);
				
			}catch(Exception e){
			}
			
			holder.title.setText(dto.TITLE);
			holder.talk_room_info_text.setSelected(true);
			
		}else if(dto.TYPE.equals("F")){
			holder.talk_room_info_text.setText("");
			holder.title.setText("");
			int imageId = Integer.parseInt(dto.ThumbNail);

			holder.thum.setImageBitmap(null);
			holder.thum.setBackgroundResource(imageId);
//			holder.thum.setImageDrawable(null);
//			holder.thum.setImageDrawable(mActivity.getResources().getDrawable(imageId));			
		}else if(dto.TYPE.equals("R")){
			holder.talk_room_info_text.setText("");
			holder.title.setText("");
			AQuery aq = listAq.recycle(convertView);
			aq.id(holder.thum).image(dto.ThumbNail, true, true, 150, 0, null, 0, 1.0f/1.0f);
			
		}else if(dto.TYPE.equals("B")){
			AQuery aq = listAq.recycle(convertView);
			aq.id(holder.thum).image(dto.ThumbNail, true, true, 150, 0, null, 0, 1.0f/1.0f);
			DateFormat expire_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
			try {
				Date tempDate = expire_date.parse(dto.EXPIRE_DATE.replace("T", " "));
				long time = tempDate.getTime() - (Calendar.getInstance(Locale.KOREA).getTime().getTime());
				long expired = time;
				
				holder.talk_room_info_text.setText("남은시간 : " + StringUtil.getTimeString(expired)  + "(" + dto.ROOM_USERS_COUNT + " 명 참가중) ");
				holder.talk_room_info_text.setSelected(true);
				
			}catch(Exception e){
			}
			
			holder.title.setText(dto.TITLE);
			holder.talk_room_info_text.setSelected(true);
			holder.talk_room_stopped.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}


	private class Holder {
		TextView title, talk_room_info_text;
		SquareImageView thum, talk_room_stopped;
		FrameLayout talk_room_frame;
	}

}
