package com.returndays.ralara.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.dto.TalkPushListDto;
import com.returndays.ralara.util.StringUtil;

public class TalkPushListAdapter extends BaseAdapter {
	private ArrayList<TalkPushListDto> mDatas = new ArrayList<TalkPushListDto>();
	private Activity mActivity;
	private LayoutInflater mInflater;
	AQuery aq;

	@SuppressWarnings("unchecked")
	public TalkPushListAdapter(Activity activity, ArrayList<TalkPushListDto> datas) {
		this.mActivity = activity;
		this.mDatas = (ArrayList<TalkPushListDto>) datas.clone();
		this.mInflater = mActivity.getLayoutInflater();
		aq = new AQuery(activity);
	}

	@SuppressWarnings("unchecked")
	public void setData(ArrayList<TalkPushListDto> datas) {
		this.mDatas = (ArrayList<TalkPushListDto>) datas.clone();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDatas.size();
	}

	@Override
	public TalkPushListDto getItem(int position) {
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
			convertView = mInflater.inflate(R.layout.talk_push_list_item, null);

			holder.talk_push_list_image = (RecyclingImageView) convertView.findViewById(R.id.talk_push_list_image);
			holder.talk_push_list_desc = (TextView)convertView.findViewById(R.id.talk_push_list_desc);
			holder.talk_push_list_time = (TextView)convertView.findViewById(R.id.talk_push_list_time);

			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		TalkPushListDto dto = getItem(position);

		AQuery aquery = aq.recycle(convertView);
		holder.talk_push_list_image.setImageDrawable(null);
		aquery.id(holder.talk_push_list_image).image(dto.ThumbNail);
		holder.talk_push_list_desc.setText("#" + dto.TalkDesc + "\n방에 댓글이 달렸습니다.");

		// 남은시간 설정
		DateFormat expire_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
		long expired = 0;
		try {

			Date tempDate = expire_date.parse(dto.TIME);

			long time = (Calendar.getInstance(Locale.KOREA).getTime().getTime()) - tempDate.getTime();

			expired = time;

		}catch(Exception e){
			e.printStackTrace();
		}

		String timeStr = StringUtil.getBeforeTimeString(expired);
		if(timeStr.equals("")){
			SimpleDateFormat sdf = new SimpleDateFormat("M.dd a hh:mm", Locale.KOREA);

			try {
				Date reg_date = sdf.parse(dto.TIME);
				timeStr =sdf.format(reg_date);

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			timeStr.replace("AM", "오전");
			timeStr.replace("PM", "오후");

		}
		
		
		holder.talk_push_list_time.setText(timeStr);
		
		return convertView;
	}


	private class Holder {
		RecyclingImageView talk_push_list_image;
		TextView talk_push_list_desc,talk_push_list_time ;

	}

}
