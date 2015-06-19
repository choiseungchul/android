package com.mcproject.net.customview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.widget.Toast;

import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.service.VideoCollectByIdService;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class FavDialog extends AlertDialog.Builder
{	
	Activity ctx;
	YTListDto dto_type1 = null;
	CollectedDto dto_type2 = null;
	
	boolean isFavVideo = false;
	boolean isFavUploader = false;
	boolean isPlaylist = false;
	
	Runnable runn = null;
	
	public FavDialog(Activity context, int objFlag, Object dto, final boolean[] itemCheck) {
		super(context);		
		ctx = context;
		
		if(objFlag == 0){
			dto_type1 = (YTListDto) dto;
		}else if(objFlag == 1){
			dto_type2 = (CollectedDto)dto;
		}

		setTitle(R.string.fav_dialog_choice_title);
		
		this.isFavVideo = itemCheck[0];
		this.isFavUploader = itemCheck[1];
		this.isPlaylist = itemCheck[2];
		
		String[] items = context.getResources().getStringArray(R.array.fav_dialog_choice_text);
		
		setMultiChoiceItems(items, itemCheck, new OnMultiChoiceClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if(which == 0){
					isFavVideo = isChecked;
				}else if(which == 1){
					isFavUploader = isChecked;
				}else if(which == 2){
					isPlaylist = isChecked;
				}
			}
		});
		
		setPositiveButton(R.string.dialog_yes, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String uploader_id = null;
				String video_id = null;
				String uploader_title = null;
				if(dto_type2 == null){
					video_id = dto_type1.videoid;
					uploader_id = dto_type1.channel_id;
					uploader_title = dto_type1.channel_title;
				}else{
					video_id = dto_type2.videoid;
					uploader_id = dto_type2.channel_id;
					uploader_title = dto_type2.channel_title;
				}
				
				// 영상 즐겨찾기
				if(isFavVideo){
					if(dto_type2 == null){
						DbQueryUtil.addFavoriteVideo(ctx, dto_type1);
					}else{
						DbQueryUtil.addFavoriteVideo(ctx, dto_type2);
					}					
				}else{
					DbQueryUtil.removeFavoriteVideo(ctx, video_id);
				}
				
				// 플레이 리스트 추가
				if(isPlaylist){
					if(dto_type2 == null)
					DbQueryUtil.addPlayList(ctx, dto_type1);
					else{
						DbQueryUtil.addPlayList(ctx, dto_type2);
					}
				}else{
					if(dto_type1 == null)
					DbQueryUtil.removePlayList(ctx, dto_type2);
					else{
						DbQueryUtil.removePlayList(ctx, dto_type1);
					}
				}
				
				// 업로더 즐겨찾기
				if(isFavUploader){
					long rs = DbQueryUtil.addFavoriteUploader(ctx, uploader_id, uploader_title);
					if(rs == -1){
						// 이미 업로더가 즐겨찾기에 등록되 있음
//						Toast.makeText(ctx, R.string.favlist_uploader_id_alreay_added, Toast.LENGTH_LONG).show();
					}else{
						// 업로더의 영상을 수집
						int serviceID = (int)Math.random() * 10000;
						Bundle b = new Bundle();
						b.putString("uploader_id", uploader_id);
						String publishAt = DbQueryUtil.getFavoriteVideoRecentTime(ctx, uploader_id);
						if(publishAt != null) b.putString("publish_date",publishAt);
						McUtil.startServiceWithExtra(ctx, VideoCollectByIdService.class, serviceID, b);				
					}
				}else{
					boolean isRemoved = UploaderProgressingList.removeUploader(uploader_id);
					long rs = DbQueryUtil.removeFavoriteUploaderLogAndFavor(ctx, uploader_id);
					if(rs != -1){	
						Toast.makeText(ctx, R.string.uploader_delete_success, Toast.LENGTH_SHORT).show();
					}else{
						//Toast.makeText(ctx, R.string.uploader_delete_failed, Toast.LENGTH_SHORT).show();
					}
				}
				
				// 실행
				if(runn != null){
					runn.run();
				}				
			}
		});
		
		setNegativeButton(R.string.dialog_no, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});	
	}
	
	public boolean getIsFavVideo(){
		return this.isFavVideo;
	}
	public boolean getIsFavUploader(){
		return this.isFavUploader;
	}
	public boolean getIsPlayList(){
		return this.isPlaylist;
	}
	
	public void setComplete(Runnable run){
		this.runn = run;
	}
}
