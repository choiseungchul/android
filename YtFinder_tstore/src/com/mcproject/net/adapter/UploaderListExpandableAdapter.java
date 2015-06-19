package com.mcproject.net.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class UploaderListExpandableAdapter extends BaseExpandableListAdapter{
     
	private Context ctx = null;
    private ArrayList<String> groupId = null;		// 업로더 아이디
    private ArrayList<String> groupList = null;	// 업로더 이름
    private ArrayList<ArrayList<CollectedDto>> childList = null; // 영상 목록
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;
    private AQuery aq;
    private OnClickListener uploaderDeleteClick;
    private OnClickListener movieAddClick;
    private ArrayList<String> expendListId = new ArrayList<String>();
    private OnClickListener onLoadMore;
    private OnClickListener movieClick;
     
    public UploaderListExpandableAdapter(Context c, ArrayList<String> groupList, ArrayList<String> groupId,
            ArrayList<ArrayList<CollectedDto>> childList){
        super();
        this.ctx = c;
        this.inflater = LayoutInflater.from(c);
        this.groupId = groupId;
        this.groupList = groupList;
        this.childList = childList;
        this.aq = new AQuery(c);
    }
     
    public void setUploaderDeleteClick(OnClickListener listener){
    	this.uploaderDeleteClick = listener;
    }
    
    public void setMovieAddClick(OnClickListener listener){
    	this.movieAddClick = listener;
    }
    
    public void setLoadMoreClick(OnClickListener listener){
    	this.onLoadMore = listener;
    }
    
    public void setMovieClick(OnClickListener listener){
    	this.movieClick = listener;
    }
    
    // 그룹 포지션을 반환한다.
    @Override
    public String getGroup(int groupPosition) {
    	if(groupList.size() > 0)
    		return groupList.get(groupPosition);
    	else
        return null;
    }
 
    // 그룹 사이즈를 반환한다.
    @Override
    public int getGroupCount() {
        return groupList.size();
    }
 
    // 그룹 ID를 반환한다.
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    // 그룹뷰 각각의 ROW 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v = convertView;
         
        if(v == null){
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.expendablelist_title, parent, false);
            viewHolder.tv_groupName = (TextView) v.findViewById(R.id.expend_title);
            viewHolder.uploader_delete = (RecyclingImageView)v.findViewById(R.id.uploader_delete);
            viewHolder.mv_count = (TextView)v.findViewById(R.id.mv_count);
            
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)v.getTag();
        }
        
        // 펼쳤을때
        if(isExpanded){
        	// 펼친 영상을 is_view = 1 로 변경
        	if(!expendListId.contains(groupId.get(groupPosition))){
        		expendListId.add(groupId.get(groupPosition));
        	}
        }
        
        if(getGroup(groupPosition) != null){
        	viewHolder.tv_groupName.setText(getGroup(groupPosition));
        	viewHolder.uploader_delete.setTag(groupId.get(groupPosition));
        	viewHolder.uploader_delete.setOnClickListener(uploaderDeleteClick);
        	
        	String count = DbQueryUtil.getCountIsViewUser(ctx, groupId.get(groupPosition));
        	if(count.equals("0")){
        		viewHolder.mv_count.setVisibility(View.GONE);
        	}else{
        		viewHolder.mv_count.setVisibility(View.VISIBLE);
        		viewHolder.mv_count.setText(count);
        	}
        }
        
        return v;
    }
     
    public ArrayList<String> getExpendIdList(){
    	return expendListId;
    }
    
    // 차일드뷰를 반환한다.
    @Override
    public CollectedDto getChild(int groupPosition, int childPosition) {
    	if(childList.size() > 0){
    		return childList.get(groupPosition).get(childPosition);
    	} else{
    			return null;
    	}
    }
    
    // 목록 수정
    public void addChlidren(int groupPosition, ArrayList<CollectedDto> list){
    	childList.get(groupPosition).addAll(list);
    }
    
    // 목록안의 내용 수정
    public void setChild(int groupPosition, int childPosition, CollectedDto dto) {
    	if(childList.size() > 0){
    		childList.get(groupPosition).set(childPosition, dto);
    	}
    }
    
    // 목록안의 목록 제거
    public void removeChild(int groupPosition, int childPosition) {
    	if(childList.size() > 0){
    		childList.get(groupPosition).remove(childPosition);
    	}
    }
    
    // 차일드뷰 사이즈를 반환한다.
    @Override
    public int getChildrenCount(int groupPosition) {
    	if(groupList.size() > 0){
    		return childList.get(groupPosition).size();
    	}else{
    		return 0;
    	}
    }
 
    // 차일드뷰 ID를 반환한다.
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    // 차일드뷰 각각의 ROW
    @Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
         
    	ChildViewHolder holder;
    	
		if(convertView == null) {
			holder = new ChildViewHolder();
			convertView = inflater.inflate(R.layout.expendablelist_content, null);
			
			holder.mv_thumb = (RecyclingImageView)convertView.findViewById(R.id.mv_thumb);
			holder.mv_title = (TextView)convertView.findViewById(R.id.mv_title);
//			holder.mv_desc = (TextView)convertView.findViewById(R.id.mv_desc);
			holder.mv_date = (TextView)convertView.findViewById(R.id.mv_date);
			holder.mv_channel_title = (TextView)convertView.findViewById(R.id.mv_channel_title);
			holder.mv_duration = (TextView)convertView.findViewById(R.id.mv_duration);
			holder.add_playlist = (RecyclingImageView)convertView.findViewById(R.id.add_playlist);
			holder.normal_layout = (LinearLayout)convertView.findViewById(R.id.normal_layout);
			holder.loadmore_layout = (TextView)convertView.findViewById(R.id.loadmore_layout);
			
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		CollectedDto dto = getChild(groupPosition, childPosition);
		
		if(dto.list_type.equals("N")){
			holder.loadmore_layout.setVisibility(View.GONE);
			holder.normal_layout.setVisibility(View.VISIBLE);
			
			if(dto != null){
				holder.mv_title.setText(dto.title);
//				holder.mv_desc.setText(dto.description);
				
				AQuery aquery = aq.recycle(convertView);
				
				aquery.id(holder.mv_thumb).image(dto.thumbnail);
				holder.mv_thumb.setTag(dto);
				holder.mv_thumb.setOnClickListener(movieClick);
				
				if(dto.isFavoriteVideo){
					holder.add_playlist.setImageResource(R.drawable.fav_playlist_added);
				}else{
					holder.add_playlist.setImageResource(R.drawable.fav_playlist_not_added);
				}
				
				HashMap<String, Object> dataObj = new HashMap<String, Object>();
				dataObj.put("g_pos", groupPosition);
				dataObj.put("c_pos", childPosition);
				dataObj.put("data", dto);
				
				holder.add_playlist.setTag(dataObj);
				holder.add_playlist.setOnClickListener(movieAddClick);
				
				String upload_time = McUtil.parseDateString(ctx, dto.publish_date_origin);
				
				holder.mv_date.setText(upload_time);
				holder.mv_channel_title.setBackgroundResource(R.drawable.title_bar_bg);
				holder.mv_channel_title.setText(ctx.getString(R.string.list_item_channel_title) + dto.channel_title);
				
				holder.mv_channel_title.setTag(dataObj);
//				holder.mv_channel_title.setOnClickListener(mvUploaderClick);
				
				holder.mv_duration.setText( ctx.getString(R.string.list_item_mv_duration) + dto.duration);
			}
		}else if(dto.list_type.equals("B")){
			holder.normal_layout.setVisibility(View.GONE);
			holder.loadmore_layout.setVisibility(View.VISIBLE);
			
			HashMap<String, Object> dataObj = new HashMap<String, Object>();
			dataObj.put("g_pos", groupPosition);
			dataObj.put("c_pos", childPosition);
			dataObj.put("data", dto);
			
			holder.loadmore_layout.setTag(dataObj);
			holder.loadmore_layout.setText(ctx.getString(R.string.uploader_expendablelist_loadmore));
			holder.loadmore_layout.setOnClickListener(onLoadMore);
		}
		
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() { return true; }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
     
    class ViewHolder {
        TextView tv_groupName;
        TextView tv_childName;
        RecyclingImageView uploader_delete;
        TextView mv_count;
        String uploader_id;
    }
    class ChildViewHolder{
    	RecyclingImageView mv_thumb, add_playlist;
		TextView mv_title, mv_date, mv_channel_title, mv_duration;
		LinearLayout normal_layout; 
		TextView loadmore_layout;
    }
    
    public TextView getGenericView() {
        // Layout parameters for the ExpandableListView
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 64);

        TextView textView = new TextView(ctx);
        textView.setLayoutParams(lp);
        // Center the text vertically
        textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        // Set the text starting position
        textView.setPadding(36, 0, 0, 0);
        return textView;
    }
}