package com.mcproject.net.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.youtube.player.internal.c;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.dto.FavUploaderDto;
import com.mcproject.net.dto.MySearchDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.ytfavorite_t.R;

public class DbQueryUtil {

	public enum SEARCH_LIST_NORMAL {seq, search_text, search_datetime, search_count};
	public enum USER_PLAYLIST {seq, is_del, etag, kind, videoid, publish_date, channel_id, title, publish_date_origin, description, thumbnail, channel_title, duration, liveBroadcastContent, hh, mm, ss, sort};
	public enum USER_FAVORITE {seq, sort, videoid, fav_type, keyword, uploader_id, etag, kind,  publish_date, channel_id, title, description, thumbnail, channel_title, duration, liveBroadcastContent, hh, mm, ss, reg_date};
	public enum UPLOADER_VIDEO_UPLOAD_LOG {seq, is_user_view, uploader_id, latest_update_date, videoid, video_datetime, etag, kind,  publish_date, title, description, thumbnail, channel_title, duration, liveBroadcastContent, hh, mm, ss};
	public enum VIDEO_CATEGORY {seq, category_id, category_name};

	// 비디오 카테고리 저장
	public static void initVideoCategory(Context ctx, String[] category_id, String[] category_names){
		LogUtil.D("initVideoCategory.......");

		if(category_id != null && category_names != null){
			if(category_id.length > 1){			
				SQLiteDatabase db = DbUtil.getDb(ctx);
				
				db.beginTransaction();
				long rs = db.delete(Define.TB_VIDEO_CATEGORY, "", null);
				if( rs == 0 ){
					return;
				}
				
				for(int i = 1 ; i < category_id.length ; i++){
					ContentValues values = new ContentValues();
					values.put(VIDEO_CATEGORY.category_id.toString(), category_id[i]);
					values.put(VIDEO_CATEGORY.category_name.toString(), category_names[i]);
					db.insert(Define.TB_VIDEO_CATEGORY, null, values);
				}
				
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		}
		
		LogUtil.D("initVideoCategory complete !!");
	}

	// 카테고리 아이디로 카테고리명 조회
	public static String getVideoCategoryName(Context ctx, String category_id){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select  category_name  from " + Define.TB_VIDEO_CATEGORY + " where category_id = ? ";
		Cursor c = db.rawQuery(sql, new String[]{category_id});
		if(c.getCount() > 0){
			String category_name = DbUtil.getColumnData(c, VIDEO_CATEGORY.category_name.toString());
			if(c != null) c.close();
			return category_name;
		}else{
			if(c != null) c.close();
			return "";
		}
	}

	// 비디오 카테고리 조회
	public static String[][] getVideoCategoryList(Context ctx){
		String[][] list = new String[2][];
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_VIDEO_CATEGORY; 
		Cursor c = db.rawQuery(sql , null);

		String[] ids = new String[c.getCount()+1];
		String[] titles = new String[c.getCount()+1];
		ids[0] = "-1";
		titles[0] = ctx.getString(R.string.srch_select_cate_btn);
		int cnt = 1;

		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				ids[cnt] = DbUtil.getColumnData(c, VIDEO_CATEGORY.category_id.toString());
				titles[cnt] = DbUtil.getColumnData(c, VIDEO_CATEGORY.category_name.toString());
				cnt++;
			}
		}

		if(c!=null) c.close();

		list[0] = ids;
		list[1] = titles;

		return list;
	}

	// 일반 검색어 저장
	public static long saveSearchNormalString(Context ctx, String queryString){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		// 인서트
		ContentValues values = new ContentValues();
		values.put(SEARCH_LIST_NORMAL.search_text.toString(), queryString);
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", ctx.getResources().getConfiguration().locale);
		String datetime = sdf.format(dt);
		values.put(SEARCH_LIST_NORMAL.search_datetime.toString(), datetime);
		values.put(SEARCH_LIST_NORMAL.search_count.toString(), "1");

		return db.insert(Define.TB_SEARCH_LIST_NORMAL, "", values);
	}

	// 일반 검색어 삭제
	public static long removeSearchNormalString(Context ctx, String queryString){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		return db.delete(Define.TB_SEARCH_LIST_NORMAL, " search_text = ? ", new String[]{queryString});
	}

	// 검색어 모두 제거
	public static long removeSearchAll(Context ctx){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		return db.delete(Define.TB_SEARCH_LIST_NORMAL, null, null);
	}
	
	// 일반 검색어 상단 5개 조회
	public static ArrayList<MySearchDto> getSearchListNormal(Context ctx, String text){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ArrayList<MySearchDto> list = new ArrayList<MySearchDto>();
		String query = "";
		if(text.equals(""))
			query = "select seq, search_text, search_datetime, search_count from " + Define.TB_SEARCH_LIST_NORMAL + " group by search_text order by search_datetime desc  limit 0, 5";
		else
			query = "select seq, search_text, search_datetime, search_count from " + Define.TB_SEARCH_LIST_NORMAL + " where search_text like '%" +text+ "%' " +
					"group by search_text order by search_datetime desc  limit 0, 10";
		Cursor c = db.rawQuery(query, null);

		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				MySearchDto dto = new MySearchDto();

				String search_text = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.search_text.toString());
				String seq = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.seq.toString());
				String search_datetime = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.search_datetime.toString());
				dto.search_text = search_text;
				dto.seq = seq;
				dto.search_datetime = search_datetime;

				list.add(dto);
			}

			c.close();
		}

		return list;
	}
	
	// 저장된 검색어 모두 불러오기
	public static ArrayList<MySearchDto> getSearchListAll(Context ctx){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ArrayList<MySearchDto> list = new ArrayList<MySearchDto>();
		String query = "select seq, search_text, search_datetime, search_count from " + Define.TB_SEARCH_LIST_NORMAL + " group by search_text order by search_datetime desc";
		
		Cursor c = db.rawQuery(query, null);

		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				MySearchDto dto = new MySearchDto();

				String search_text = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.search_text.toString());
				String seq = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.seq.toString());
				String search_datetime = DbUtil.getColumnData(c, SEARCH_LIST_NORMAL.search_datetime.toString());
				dto.search_text = search_text;
				dto.seq = seq;
				dto.search_datetime = search_datetime;

				list.add(dto);
			}

			c.close();
		}

		return list;
	}

	// 즐겨찾기 추가 - 영상
	public static long addFavoriteVideo(Context ctx, YTListDto dto){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' and videoid = '" + dto.videoid + "'"; 
		Cursor c = db.rawQuery(sql , null);
		if(c.getCount() > 0){
			c.close();
			return -1;
		}else{
			ContentValues values = new ContentValues();
			values.put(USER_FAVORITE.fav_type.toString(), "V");
			values.put(USER_PLAYLIST.channel_id.toString(), dto.channel_id);
			values.put(USER_PLAYLIST.channel_title.toString(), dto.channel_title);
			values.put(USER_PLAYLIST.description.toString(), dto.description);
			values.put(USER_PLAYLIST.duration.toString(), dto.duration);
			values.put(USER_PLAYLIST.etag.toString(), dto.etag);
			values.put(USER_PLAYLIST.hh.toString(), dto.hh);
			values.put(USER_PLAYLIST.kind.toString(), dto.kind);
			values.put(USER_PLAYLIST.liveBroadcastContent.toString(), dto.liveBroadcastContent);
			values.put(USER_PLAYLIST.mm.toString(), dto.mm);
			values.put(USER_PLAYLIST.publish_date.toString(), dto.publish_date);
			values.put(USER_PLAYLIST.ss.toString(), dto.ss);
			values.put(USER_PLAYLIST.thumbnail.toString(), dto.thumbnail);
			values.put(USER_PLAYLIST.title.toString(), dto.title);
			values.put(USER_PLAYLIST.videoid.toString(), dto.videoid);
			values.put(USER_FAVORITE.reg_date.toString(), "now()");

			c.close();

			return db.insert(Define.TB_USER_FAVORITE, null, values);
		}		
	}
	
	// 즐겨찾기 추가 - 영상
	public static long addFavoriteVideo(Context ctx, CollectedDto dto){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' and videoid = '" + dto.videoid + "'"; 
		Cursor c = db.rawQuery(sql , null);
		if(c.getCount() > 0){
			c.close();
			return -1;
		}else{
			ContentValues values = new ContentValues();
			values.put(USER_FAVORITE.fav_type.toString(), "V");
			values.put(USER_PLAYLIST.channel_id.toString(), dto.channel_id);
			values.put(USER_PLAYLIST.channel_title.toString(), dto.channel_title);
			values.put(USER_PLAYLIST.description.toString(), dto.description);
			values.put(USER_PLAYLIST.duration.toString(), dto.duration);
			values.put(USER_PLAYLIST.etag.toString(), dto.etag);
			values.put(USER_PLAYLIST.hh.toString(), dto.hh);
			values.put(USER_PLAYLIST.kind.toString(), dto.kind);
			values.put(USER_PLAYLIST.liveBroadcastContent.toString(), dto.liveBroadcastContent);
			values.put(USER_PLAYLIST.mm.toString(), dto.mm);
			values.put(USER_PLAYLIST.publish_date.toString(), dto.publish_date);
			values.put(USER_PLAYLIST.ss.toString(), dto.ss);
			values.put(USER_PLAYLIST.thumbnail.toString(), dto.thumbnail);
			values.put(USER_PLAYLIST.title.toString(), dto.title);
			values.put(USER_PLAYLIST.videoid.toString(), dto.videoid);
			values.put(USER_FAVORITE.reg_date.toString(), "now()");

			c.close();

			return db.insert(Define.TB_USER_FAVORITE, null, values);
		}		
	}

	// 즐겨찾기 추가 - 업로더
	public static long addFavoriteUploader(Context ctx, String uploader_id, String uploader_name){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select uploader_id from " + Define.TB_USER_FAVORITE + " where fav_type = 'U' and uploader_id = '" + uploader_id + "'"; 
		Cursor c = db.rawQuery(sql , null);
		if(c.getCount() > 0){
			c.close();
			return -1;
		}else{
			ContentValues values = new ContentValues();
			values.put(USER_FAVORITE.fav_type.toString(), "U");
			values.put(USER_FAVORITE.uploader_id.toString(), uploader_id);
			values.put(USER_FAVORITE.channel_title.toString(), uploader_name);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date date = new Date();
			values.put(USER_FAVORITE.reg_date.toString(), dateFormat.format(date));
			values.put(USER_FAVORITE.sort.toString(), c.getCount());

			c.close();

			return db.insert(Define.TB_USER_FAVORITE, null, values);
		}		
	}

	// 즐겨찾기 삭제 - 영상
	public static long removeFavoriteVideo(Context ctx, String videoid){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' and videoid = '" + videoid + "'"; 
		Cursor c = db.rawQuery(sql , null);
		if(c.getCount() > 0){
			c.close();
			return db.delete(Define.TB_USER_FAVORITE, " fav_type = ? and videoid = ? " , new String[]{"V",videoid});
		}else{
			c.close();
			return -1;
		}		
	}

	// 즐겨찾기 조회 - 영상
	public static ArrayList<YTListDto> getFavoriteVideo(Context ctx){
		ArrayList<YTListDto> list = new ArrayList<YTListDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' order by seq asc";
		Cursor c = db.rawQuery(sql , null);
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				YTListDto dto = new YTListDto();
				dto.seq = DbUtil.getColumnData(c, USER_FAVORITE.seq.toString());
				dto.channel_id = DbUtil.getColumnData(c, USER_FAVORITE.channel_id.toString());
				dto.channel_title = DbUtil.getColumnData(c, USER_FAVORITE.channel_title.toString());
				dto.description = DbUtil.getColumnData(c, USER_FAVORITE.description.toString());
				dto.duration = DbUtil.getColumnData(c, USER_FAVORITE.duration.toString());
				dto.etag = DbUtil.getColumnData(c, USER_FAVORITE.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.hh.toString()));
				dto.kind = DbUtil.getColumnData(c, USER_FAVORITE.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(c, USER_FAVORITE.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(c, USER_FAVORITE.publish_date.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(c, USER_FAVORITE.thumbnail.toString());
				dto.title = DbUtil.getColumnData(c, USER_FAVORITE.title.toString());
				dto.videoid = DbUtil.getColumnData(c, USER_FAVORITE.videoid.toString());
				dto.isFavoriteVideo = true;
				list.add(dto);
			}
			c.close();
		}
		return list;
	}

	// 즐겨찾기 조회 - 영상 ( 검색 )
	public static ArrayList<YTListDto> getFavoriteVideo(Context ctx, String srch_text){
		ArrayList<YTListDto> list = new ArrayList<YTListDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' and title like '%"+srch_text+"%' order by seq asc";
		Cursor c = db.rawQuery(sql , null);
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				YTListDto dto = new YTListDto();
				dto.seq = DbUtil.getColumnData(c, USER_FAVORITE.seq.toString());
				dto.channel_id = DbUtil.getColumnData(c, USER_FAVORITE.channel_id.toString());
				dto.channel_title = DbUtil.getColumnData(c, USER_FAVORITE.channel_title.toString());
				dto.description = DbUtil.getColumnData(c, USER_FAVORITE.description.toString());
				dto.duration = DbUtil.getColumnData(c, USER_FAVORITE.duration.toString());
				dto.etag = DbUtil.getColumnData(c, USER_FAVORITE.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.hh.toString()));
				dto.kind = DbUtil.getColumnData(c, USER_FAVORITE.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(c, USER_FAVORITE.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(c, USER_FAVORITE.publish_date.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(c, USER_FAVORITE.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(c, USER_FAVORITE.thumbnail.toString());
				dto.title = DbUtil.getColumnData(c, USER_FAVORITE.title.toString());
				dto.videoid = DbUtil.getColumnData(c, USER_FAVORITE.videoid.toString());
				dto.isFavoriteVideo = true;
				list.add(dto);
			}
			c.close();
		}
		return list;
	}

	// 영상이 즐겨찾기 되어있는지 체크
	public static boolean isFavoriteMovie(Context ctx, String movieId){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_USER_FAVORITE + " where fav_type = 'V' and videoid = ?";
		Cursor c = db.rawQuery(sql , new String[]{movieId});
		if(c.getCount() > 0){
			c.close();
			return true;
		}else{ 
			c.close();
			return false; 
		}
	}

	// 즐겨찾기 조회 - 업로더 아이디
	public static ArrayList<String> getFavoriteUploaderId(Context ctx){
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select uploader_id from " + Define.TB_USER_FAVORITE + " where fav_type = 'U' "; 
		Cursor c = db.rawQuery(sql , null);
		if(c != null){
			if(c.getCount() > 0){
				while (c.moveToNext()) {
					list.add(DbUtil.getColumnData(c, USER_FAVORITE.uploader_id.toString()));
				}				
				c.close();
			}
		}
		return list;
	}

	// 즐겨찾기 조회 - 업로더 이름
	public static ArrayList<String> getFavoriteUploaderName(Context ctx){
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select channel_title from " + Define.TB_USER_FAVORITE + " where fav_type = 'U' "; 
		Cursor c = db.rawQuery(sql , null);
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				list.add(DbUtil.getColumnData(c, USER_FAVORITE.channel_title.toString()));
			}

			c.close();
		}
		return list;
	}

	/////////////////////////// 업로더 정보 입력 ////////////////////////
	// 수집된 비디오의 최근날짜 검색 
	public static String getFavoriteVideoRecentTime(Context ctx, String uploader_id){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select video_datetime  from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? order by video_datetime desc limit 0, 1 ";
		Cursor c = db.rawQuery(sql , new String[]{ uploader_id });
		if(c.getCount() > 0){
			c.moveToNext();
			String video_datetime = DbUtil.getColumnData(c, "video_datetime");
			if(c!=null)c.close();
			return video_datetime;
		}else{
			if(c!=null) c.close();
			return null;
		}
	}

	// 업로더 목록 불러오기 -> 액티비티의 리스트에서 실제 사용함
	public static ArrayList<FavUploaderDto> getFavoriteUploaderList(Context ctx){
		ArrayList<FavUploaderDto> uploaderList = new ArrayList<FavUploaderDto>();

		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select channel_title, uploader_id from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " group by uploader_id ";
		Cursor c = db.rawQuery(sql , null);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				FavUploaderDto dto = new FavUploaderDto();

				String uploader_id = DbUtil.getColumnData(c, "uploader_id");
				String channel_title = DbUtil.getColumnData(c, "channel_title");
				String count =  getCountIsViewUser(ctx, uploader_id);

				dto.count = count;
				dto.uploader_id = uploader_id;
				dto.title = channel_title;

				uploaderList.add(dto);
			}
		}

		// 정렬된 데이터가 들어가는곳
		ArrayList<FavUploaderDto> return_uploaderList = new ArrayList<FavUploaderDto>();
		
		// 정렬하기
		String getFavList = "select uploader_id, sort from " + Define.TB_USER_FAVORITE + " where fav_type = ? order by sort asc ";
		Cursor cc = db.rawQuery(getFavList, new String[]{ "U" });
		if(cc.getCount() > 0){
			while(cc.moveToNext()){
				String uploader_id = DbUtil.getColumnData(cc, "uploader_id");
				
				for(int i = 0 ; i < uploaderList.size() ; i++){
					if(uploader_id.equals(uploaderList.get(i).uploader_id)){
						return_uploaderList.add(uploaderList.get(i));
					}
				}
			}
			
//			if(return_uploaderList.size() != 0){
//				Collections.reverse(return_uploaderList);
//			}
			
		}else{
			LogUtil.D("cc count = 0 ");
		}
		
//		if(return_uploaderList.size() == 0){
//			return_uploaderList = uploaderList;
//		}
		
		if(c != null) c.close();
		if(cc != null) cc.close();

		return return_uploaderList;
	}

	// 새로운 업로드 영상이 있는 업로더이름 반환
	public static ArrayList<String> getNewUploadContent(Context ctx){		
		ArrayList<String> list = new ArrayList<String>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select channel_title from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + "  where is_user_view = 'N' group by uploader_id ";
		try {
			Cursor c = db.rawQuery(sql , null);
			if(c != null){
				if(c.getCount() > 0){
					while(c.moveToNext()){
						list.add(DbUtil.getColumnData(c, "channel_title"));
					}
				}
				c.close();
			}
		}catch(Exception e){			
		}

		return list;
	}

	// 수집된 비디오 db에 넣기
	public static void addUploaderLogTable(Context ctx, List<YTListDto> list){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		db.beginTransaction();

		for(int i = 0 ; i < list.size() ; i++){
			YTListDto dto = list.get(i);
			ContentValues values = new ContentValues();
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString(), dto.publish_date);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.channel_title.toString(), dto.channel_title);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.description.toString(), dto.description);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.duration.toString(), dto.duration);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.etag.toString(), dto.etag);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.hh.toString(), dto.hh);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.kind.toString(), dto.kind);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.is_user_view.toString(), "N");

			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String latest_update_date = sdf.format(dt);

			values.put(UPLOADER_VIDEO_UPLOAD_LOG.latest_update_date.toString(), latest_update_date);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.liveBroadcastContent.toString(), dto.liveBroadcastContent);

			values.put(UPLOADER_VIDEO_UPLOAD_LOG.mm.toString(), dto.mm);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString(), dto.publish_date);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.ss.toString(), dto.ss);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.thumbnail.toString(), dto.thumbnail);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.title.toString(), dto.title);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.uploader_id.toString(), dto.channel_id);
			// 유튜브에서 변환하지 않은 시간
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString(), dto.publish_date_origin);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString(), dto.videoid);

			// videoId로 조회
			Cursor exist = db.rawQuery("select videoid from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where videoid = ? ", new String[]{ dto.videoid });
			if(exist.getCount() == 0){
				exist.close();
				db.insert(Define.TB_UPLOADER_VIDEO_UPLOAD_LOG, null, values);
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}


	// 수집된 비디오 조회 - 전체
	public static ArrayList<ArrayList<CollectedDto>> getCollectFavoriteVideos(Context ctx){
		ArrayList<ArrayList<CollectedDto>> list = new ArrayList<ArrayList<CollectedDto>>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select uploader_id from " + Define.TB_USER_FAVORITE + " where fav_type = 'U' order by reg_date desc ";
		Cursor c = db.rawQuery(sql, null);
		if(c.getCount() > 0){
			while(c.moveToNext()){
				String uploader_id = DbUtil.getColumnData(c, "uploader_id");

				String getListSql = "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? group by videoid order by video_datetime desc limit 0, 20";
				Cursor listCur = db.rawQuery(getListSql, new String[]{ uploader_id });
				if(c.getCount() > 0){
					ArrayList<CollectedDto> data = new ArrayList<CollectedDto>();
					while(listCur.moveToNext()){
						CollectedDto dto = new CollectedDto();
						dto.channel_id = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.uploader_id.toString());
						dto.channel_title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.channel_title.toString());
						dto.description = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.description.toString());
						dto.duration = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.duration.toString());
						dto.etag = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.etag.toString());
						dto.hh = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.hh.toString()));
						dto.kind = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.kind.toString());
						dto.liveBroadcastContent = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.liveBroadcastContent.toString());
						dto.mm = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.mm.toString()));
						dto.publish_date = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString());
						dto.publish_date_origin = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString());
						dto.ss = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.ss.toString()));
						dto.thumbnail = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.thumbnail.toString());
						dto.title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.title.toString());
						dto.videoid = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString());
						dto.list_type = "N";

						if(dto.videoid != null)
							data.add(dto);

					}
					CollectedDto moreIist = new CollectedDto();
					moreIist.list_type = "B";
					moreIist.channel_id = uploader_id;
					data.add(moreIist);

					if(listCur!=null)
						listCur.close();

					list.add(data);
				}
			}

			if(c!=null)c.close();
		}

		return list;
	}

	// 수집된 비디오 조회 - 갯수
	public static int getCollectFavoriteVideoCount(Context ctx, String uploader_id){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select count(*) as cnt from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? ";
		Cursor c = db.rawQuery(sql, new String[]{uploader_id});
		int totalCount = 0;
		if(c.getCount() > 0){
			if(c.moveToNext()){
				totalCount = Integer.parseInt(DbUtil.getColumnData(c, "cnt"));
			}
		}

		if(c != null) c.close();

		return totalCount;
	}
	
	// 수집된 비디오 조회 - 갯수 (검색)
	public static int getCollectFavoriteVideoCount(Context ctx, String uploader_id, String srch_text){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select count(*) as cnt from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? and title like '%"+srch_text+"%'";
		Cursor c = db.rawQuery(sql, new String[]{uploader_id});
		int totalCount = 0;
		if(c.getCount() > 0){
			if(c.moveToNext()){
				totalCount = Integer.parseInt(DbUtil.getColumnData(c, "cnt"));
			}
		}

		if(c != null) c.close();

		return totalCount;
	}

	// 수집된 비디오 조회 - 부분
	public static ArrayList<CollectedDto> getCollectFavoriteVideosByIdBottomBtn(Context ctx,String uploader_id, int limit_start, int limit_end){
		ArrayList<CollectedDto> data = new ArrayList<CollectedDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String getListSql = "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? group by videoid order by video_datetime desc limit ?, ? ";
		//		LogUtil.I("getCollectFavoriteVideosById query = " + "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = "+uploader_id+" limit "+limit_start+", "+limit_end+" ");
		Cursor listCur = db.rawQuery(getListSql, new String[]{ uploader_id, String.valueOf(limit_start), String.valueOf(limit_end) });

		if(listCur.getCount() > 0){
			while(listCur.moveToNext()){
				CollectedDto dto = new CollectedDto();
				dto.channel_id = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.uploader_id.toString());
				dto.channel_title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.channel_title.toString());
				dto.description = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.description.toString());
				dto.duration = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.duration.toString());
				dto.etag = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.hh.toString()));
				dto.kind = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString());
				dto.publish_date_origin = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.thumbnail.toString());
				dto.title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.title.toString());
				dto.videoid = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString());
				dto.list_type = "N";

				if(dto.videoid != null)
					data.add(dto);
			}

			CollectedDto moreIist = new CollectedDto();
			moreIist.list_type = "B";
			moreIist.channel_id = uploader_id;
			data.add(moreIist);

			listCur.close();
		}else{
			if(listCur !=null)
				listCur.close();
			LogUtil.I("더이상 없음");
		}
		return data;
	}

	// 수집된 비디오 조회 - 부분 ( api 11 이하 버전에서 사용하는 것 ) 
	public static ArrayList<CollectedDto> getCollectFavoriteVideos(Context ctx,String uploader_id, int limit_start, int limit_end){
		ArrayList<CollectedDto> data = new ArrayList<CollectedDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String getListSql = "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? group by videoid order by video_datetime desc limit ?, ? ";
		//			LogUtil.I("getCollectFavoriteVideosById query = " + "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = "+uploader_id+" limit "+limit_start+", "+limit_end+" ");
		Cursor listCur = db.rawQuery(getListSql, new String[]{ uploader_id, String.valueOf(limit_start), String.valueOf(limit_end) });

		if(listCur.getCount() > 0){
			while(listCur.moveToNext()){
				CollectedDto dto = new CollectedDto();
				dto.channel_id = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.uploader_id.toString());
				dto.channel_title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.channel_title.toString());
				dto.description = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.description.toString());
				dto.duration = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.duration.toString());
				dto.etag = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.hh.toString()));
				dto.kind = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString());
				dto.publish_date_origin = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.thumbnail.toString());
				dto.title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.title.toString());
				dto.videoid = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString());
				dto.list_type = "N";

				if(dto.videoid != null){
					dto.isPlaylist = isPlaylist(ctx, dto.videoid);
					data.add(dto);
				}

			}

			listCur.close();
		}else{
			if(listCur !=null)
				listCur.close();
			LogUtil.I("더이상 없음");
		}
		return data;
	}
	
	// 수집된 비디오 조회 - 부분  (검색 추가)
	public static ArrayList<CollectedDto> getCollectFavoriteVideos(Context ctx,String uploader_id, String srch_text,  int limit_start, int limit_end){
		ArrayList<CollectedDto> data = new ArrayList<CollectedDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String getListSql = "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? and title like '%"+srch_text+"%' group by videoid order by video_datetime desc limit ?, ? ";
		//			LogUtil.I("getCollectFavoriteVideosById query = " + "select * from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = "+uploader_id+" limit "+limit_start+", "+limit_end+" ");
		Cursor listCur = db.rawQuery(getListSql, new String[]{ uploader_id, String.valueOf(limit_start), String.valueOf(limit_end) });

		if(listCur.getCount() > 0){
			while(listCur.moveToNext()){
				CollectedDto dto = new CollectedDto();
				dto.channel_id = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.uploader_id.toString());
				dto.channel_title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.channel_title.toString());
				dto.description = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.description.toString());
				dto.duration = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.duration.toString());
				dto.etag = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.hh.toString()));
				dto.kind = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.publish_date.toString());
				dto.publish_date_origin = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.thumbnail.toString());
				dto.title = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.title.toString());
				dto.videoid = DbUtil.getColumnData(listCur, UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString());
				dto.list_type = "N";

				if(dto.videoid != null){
					dto.isPlaylist = isPlaylist(ctx, dto.videoid);
					data.add(dto);
				}

			}

			listCur.close();
		}else{
			if(listCur !=null)
				listCur.close();
			LogUtil.I("더이상 없음");
		}
		return data;
	}

	// 수집된 해당업로더의 영상 목록 및 즐겨찾기를 제거
	public static long removeFavoriteUploaderLogAndFavor(Context ctx, String uploader_id){		
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select uploader_id from " + Define.TB_USER_FAVORITE + " where fav_type = 'U' and uploader_id = ? "; 
		Cursor c = db.rawQuery(sql , new String[]{uploader_id});
		if(c.getCount() > 0){
			db.beginTransaction();

			int del_flag1 = db.delete(Define.TB_UPLOADER_VIDEO_UPLOAD_LOG , " uploader_id = ?  ", new String[]{uploader_id});
			int del_flag2 = db.delete(Define.TB_USER_FAVORITE, " fav_type = ? and uploader_id = ? " , new String[]{"U",uploader_id});
			LogUtil.I(" TB_UPLOADER_VIDEO_UPLOAD_LOG flag = " + del_flag1);
			LogUtil.I(" TB_USER_FAVORITE flag = " + del_flag2);

			db.setTransactionSuccessful();
			db.endTransaction();

			c.close();

			return 0;
		}else{
			if(c!=null)c.close();
			return -1;
		}
	}

	// 수집된 항목중 새로운 업로드 목록 개수 
	public static String getCountIsViewUser(Context ctx, String uploader_id){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select count(*) as cnt from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where uploader_id = ? and is_user_view = ? "; 
		Cursor c = db.rawQuery(sql , new String[]{uploader_id, "N"});
		if(c.getCount() > 0){
			c.moveToNext();
			String cnt = DbUtil.getColumnData(c, "cnt");
			c.close();
			return cnt;
		}else{
			if(c!=null)c.close();
			return "0";
		}
	}

	// 사용자가 새로운 업로드 목록을 봄 
	public static void setIsViewUser(Context ctx, ArrayList<String> uploaders){
		SQLiteDatabase db = DbUtil.getDb(ctx);

		ContentValues values = new ContentValues();
		values.put(UPLOADER_VIDEO_UPLOAD_LOG.is_user_view.toString(), "Y");

		db.beginTransaction();

		for(int i = 0 ; i < uploaders.size() ; i++){
			db.update(Define.TB_UPLOADER_VIDEO_UPLOAD_LOG, values, " uploader_id =  ?  ", new String[]{ uploaders.get(i) });
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}


	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 플레이리스트 추가
	public static long addPlayList(Context ctx, YTListDto dto){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_PLAYLIST + " where videoid = ? ";
		Cursor c = db.rawQuery(sql , new String[]{dto.videoid});
		if(c.getCount() > 0){
			c.close();
			return -1;
		}else{
			if(c!=null)
				c.close();

			sql = "select count(*) as cnt from " + Define.TB_USER_PLAYLIST;
			Cursor count = db.rawQuery(sql, null);
			String rowCnt = "0";
			if(count.getCount() > 0){
				if(count.moveToFirst()){
					rowCnt = DbUtil.getColumnData(count, "cnt");
				}
			}
			if(count!=null)count.close();

			ContentValues values = new ContentValues();
			values.put(USER_PLAYLIST.channel_id.toString(), dto.channel_id);
			values.put(USER_PLAYLIST.channel_title.toString(), dto.channel_title);
			values.put(USER_PLAYLIST.description.toString(), dto.description);
			values.put(USER_PLAYLIST.duration.toString(), dto.duration);
			values.put(USER_PLAYLIST.etag.toString(), dto.etag);
			values.put(USER_PLAYLIST.hh.toString(), dto.hh);
			values.put(USER_PLAYLIST.kind.toString(), dto.kind);
			values.put(USER_PLAYLIST.liveBroadcastContent.toString(), dto.liveBroadcastContent);
			values.put(USER_PLAYLIST.mm.toString(), dto.mm);
			values.put(USER_PLAYLIST.publish_date.toString(), dto.publish_date);
			values.put(USER_PLAYLIST.publish_date_origin.toString(), dto.publish_date_origin);
			values.put(USER_PLAYLIST.ss.toString(), dto.ss);
			values.put(USER_PLAYLIST.thumbnail.toString(), dto.thumbnail);
			values.put(USER_PLAYLIST.title.toString(), dto.title);
			values.put(USER_PLAYLIST.videoid.toString(), dto.videoid);
			values.put(USER_PLAYLIST.sort.toString(), rowCnt);
			values.put(USER_PLAYLIST.is_del.toString(), "0");

			return db.insert(Define.TB_USER_PLAYLIST, null, values);
		}		
	}

	// 플레이리스트 추가
	public static long addPlayList(Context ctx, CollectedDto dto){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_PLAYLIST + " where videoid = ? and is_del = 1";
		Cursor c = db.rawQuery(sql , new String[]{dto.videoid});
		if(c.getCount() > 0){
			// 업데이트 한다
			ContentValues values = new ContentValues();
			values.put(USER_PLAYLIST.is_del.toString(), "0");
			long rs = db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
			c.close();
			return rs;
		}else{

			if(c!=null)c.close();

			sql = "select count(*) as cnt from " + Define.TB_USER_PLAYLIST;
			Cursor count = db.rawQuery(sql, null);
			String rowCnt = "0";
			if(count.getCount() > 0){
				if(count.moveToFirst()){
					rowCnt = DbUtil.getColumnData(count, "cnt");
				}
			}
			if(count!=null)count.close();

			ContentValues values = new ContentValues();
			values.put(USER_PLAYLIST.channel_id.toString(), dto.channel_id);
			values.put(USER_PLAYLIST.channel_title.toString(), dto.channel_title);
			values.put(USER_PLAYLIST.description.toString(), dto.description);
			values.put(USER_PLAYLIST.duration.toString(), dto.duration);
			values.put(USER_PLAYLIST.etag.toString(), dto.etag);
			values.put(USER_PLAYLIST.hh.toString(), dto.hh);
			values.put(USER_PLAYLIST.kind.toString(), dto.kind);
			values.put(USER_PLAYLIST.liveBroadcastContent.toString(), dto.liveBroadcastContent);
			values.put(USER_PLAYLIST.mm.toString(), dto.mm);
			values.put(USER_PLAYLIST.publish_date.toString(), dto.publish_date);
			values.put(USER_PLAYLIST.publish_date_origin.toString(), dto.publish_date_origin);
			values.put(USER_PLAYLIST.ss.toString(), dto.ss);
			values.put(USER_PLAYLIST.thumbnail.toString(), dto.thumbnail);
			values.put(USER_PLAYLIST.title.toString(), dto.title);
			values.put(USER_PLAYLIST.videoid.toString(), dto.videoid);
			values.put(USER_PLAYLIST.sort.toString(), rowCnt);
			values.put(USER_PLAYLIST.is_del.toString(), "0");

			return db.insert(Define.TB_USER_PLAYLIST, null, values);
		}		
	}

	// 플레이 리스트 조회
	public static ArrayList<YTListDto> getPlayList(Context ctx){
		ArrayList<YTListDto> list = new ArrayList<YTListDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_USER_PLAYLIST + " where is_del = 0 order by sort desc";
		Cursor c = db.rawQuery(sql, null);

		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				YTListDto dto = new YTListDto();
				dto.seq = DbUtil.getColumnData(c, USER_PLAYLIST.seq.toString());
				dto.channel_id = DbUtil.getColumnData(c, USER_PLAYLIST.channel_id.toString());
				dto.channel_title = DbUtil.getColumnData(c, USER_PLAYLIST.channel_title.toString());
				dto.description = DbUtil.getColumnData(c, USER_PLAYLIST.description.toString());
				dto.duration = DbUtil.getColumnData(c, USER_PLAYLIST.duration.toString());
				dto.etag = DbUtil.getColumnData(c, USER_PLAYLIST.etag.toString());
				dto.hh = Integer.parseInt(DbUtil.getColumnData(c, USER_PLAYLIST.hh.toString()));
				dto.kind = DbUtil.getColumnData(c, USER_PLAYLIST.kind.toString());
				dto.liveBroadcastContent = DbUtil.getColumnData(c, USER_PLAYLIST.liveBroadcastContent.toString());
				dto.mm = Integer.parseInt(DbUtil.getColumnData(c, USER_PLAYLIST.mm.toString()));
				dto.publish_date = DbUtil.getColumnData(c, USER_PLAYLIST.publish_date.toString());
				dto.publish_date_origin = DbUtil.getColumnData(c, USER_PLAYLIST.publish_date_origin.toString());
				dto.ss = Integer.parseInt(DbUtil.getColumnData(c, USER_PLAYLIST.ss.toString()));
				dto.thumbnail = DbUtil.getColumnData(c, USER_PLAYLIST.thumbnail.toString());
				dto.title = DbUtil.getColumnData(c, USER_PLAYLIST.title.toString());
				dto.videoid = DbUtil.getColumnData(c, USER_PLAYLIST.videoid.toString());

				//				LogUtil.D("sort num = " + DbUtil.getColumnData(c, "sort"));

				list.add(dto);
			}

			c.close();
		}

		return list;
	}

	// 플레이리스트 재정렬
	public static void sortPlayList(Context ctx, ArrayList<YTListDto> data){
		SQLiteDatabase db = DbUtil.getDb(ctx);

		db.beginTransaction();
		db.delete(Define.TB_USER_PLAYLIST, "", null);

		int cnt = data.size();
		LogUtil.D("sortPlayList list count = " + cnt);

		for(int i = 0 ; i < data.size() ; i++){
			YTListDto dto = data.get(i);

			ContentValues values = new ContentValues();
			values.put(USER_PLAYLIST.channel_id.toString(), dto.channel_id);
			values.put(USER_PLAYLIST.channel_title.toString(), dto.channel_title);
			values.put(USER_PLAYLIST.description.toString(), dto.description);
			values.put(USER_PLAYLIST.duration.toString(), dto.duration);
			values.put(USER_PLAYLIST.etag.toString(), dto.etag);
			values.put(USER_PLAYLIST.hh.toString(), dto.hh);
			values.put(USER_PLAYLIST.kind.toString(), dto.kind);
			values.put(USER_PLAYLIST.liveBroadcastContent.toString(), dto.liveBroadcastContent);
			values.put(USER_PLAYLIST.mm.toString(), dto.mm);
			values.put(USER_PLAYLIST.publish_date.toString(), dto.publish_date);
			values.put(USER_PLAYLIST.publish_date_origin.toString(), dto.publish_date_origin);
			values.put(USER_PLAYLIST.ss.toString(), dto.ss);
			values.put(USER_PLAYLIST.thumbnail.toString(), dto.thumbnail);
			values.put(USER_PLAYLIST.title.toString(), dto.title);
			values.put(USER_PLAYLIST.videoid.toString(), dto.videoid);
			values.put(USER_PLAYLIST.sort.toString(), String.valueOf(cnt));
			values.put(USER_PLAYLIST.is_del.toString(), "0");

			db.insert(Define.TB_USER_PLAYLIST, null, values);

			cnt--;
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	// 업로더 리스트 재정렬
	public static void sortUploaderList(Context ctx, ArrayList<FavUploaderDto> data){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		db.beginTransaction();

		int cnt = data.size();
		LogUtil.D("sortUploader list count = " + cnt);

		for(int i = 0 ; i < data.size() ; i++){
			FavUploaderDto dto = data.get(i);

			ContentValues values = new ContentValues();
			values.put(USER_FAVORITE.sort.toString(), String.valueOf(i));

			db.update(Define.TB_USER_FAVORITE, values, " uploader_id = ? ", new String[]{ dto.uploader_id });
//			LogUtil.D("uploader id = " + dto.uploader_id + " / sort = " + cnt);
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}

	// 플레이리스트 제거
	public static long deletePlayList(Context ctx){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		return db.delete(Define.TB_USER_PLAYLIST, "", null);
	}

	public static boolean isPlaylist(Context ctx, String videoid){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select * from " + Define.TB_USER_PLAYLIST + " where videoid = ?";
		Cursor c = db.rawQuery(sql, new String[]{videoid});
		if(c.getCount() > 0) {
			c.close();
			return true;
		}else{
			c.close();
			return false;
		}
	}

	// 플레이리스트 삭제 되돌리기
	public static long alivePlayList(Context ctx, YTListDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "0");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

	// 플레이리스트에서 제거
	public static long removePlayList(Context ctx, YTListDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "1");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

	// 플레이리스트에서 제거
	public static long removePlayList(Context ctx, CollectedDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "1");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

}
