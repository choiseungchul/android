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

	// ���� ī�װ� ����
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

	// ī�װ� ���̵�� ī�װ��� ��ȸ
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

	// ���� ī�װ� ��ȸ
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

	// �Ϲ� �˻��� ����
	public static long saveSearchNormalString(Context ctx, String queryString){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		// �μ�Ʈ
		ContentValues values = new ContentValues();
		values.put(SEARCH_LIST_NORMAL.search_text.toString(), queryString);
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", ctx.getResources().getConfiguration().locale);
		String datetime = sdf.format(dt);
		values.put(SEARCH_LIST_NORMAL.search_datetime.toString(), datetime);
		values.put(SEARCH_LIST_NORMAL.search_count.toString(), "1");

		return db.insert(Define.TB_SEARCH_LIST_NORMAL, "", values);
	}

	// �Ϲ� �˻��� ����
	public static long removeSearchNormalString(Context ctx, String queryString){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		return db.delete(Define.TB_SEARCH_LIST_NORMAL, " search_text = ? ", new String[]{queryString});
	}

	// �˻��� ��� ����
	public static long removeSearchAll(Context ctx){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		return db.delete(Define.TB_SEARCH_LIST_NORMAL, null, null);
	}
	
	// �Ϲ� �˻��� ��� 5�� ��ȸ
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
	
	// ����� �˻��� ��� �ҷ�����
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

	// ���ã�� �߰� - ����
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
	
	// ���ã�� �߰� - ����
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

	// ���ã�� �߰� - ���δ�
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

	// ���ã�� ���� - ����
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

	// ���ã�� ��ȸ - ����
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

	// ���ã�� ��ȸ - ���� ( �˻� )
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

	// ������ ���ã�� �Ǿ��ִ��� üũ
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

	// ���ã�� ��ȸ - ���δ� ���̵�
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

	// ���ã�� ��ȸ - ���δ� �̸�
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

	/////////////////////////// ���δ� ���� �Է� ////////////////////////
	// ������ ������ �ֱٳ�¥ �˻� 
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

	// ���δ� ��� �ҷ����� -> ��Ƽ��Ƽ�� ����Ʈ���� ���� �����
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

		// ���ĵ� �����Ͱ� ���°�
		ArrayList<FavUploaderDto> return_uploaderList = new ArrayList<FavUploaderDto>();
		
		// �����ϱ�
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

	// ���ο� ���ε� ������ �ִ� ���δ��̸� ��ȯ
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

	// ������ ���� db�� �ֱ�
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
			// ��Ʃ�꿡�� ��ȯ���� ���� �ð�
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.video_datetime.toString(), dto.publish_date_origin);
			values.put(UPLOADER_VIDEO_UPLOAD_LOG.videoid.toString(), dto.videoid);

			// videoId�� ��ȸ
			Cursor exist = db.rawQuery("select videoid from " + Define.TB_UPLOADER_VIDEO_UPLOAD_LOG + " where videoid = ? ", new String[]{ dto.videoid });
			if(exist.getCount() == 0){
				exist.close();
				db.insert(Define.TB_UPLOADER_VIDEO_UPLOAD_LOG, null, values);
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();
	}


	// ������ ���� ��ȸ - ��ü
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

	// ������ ���� ��ȸ - ����
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
	
	// ������ ���� ��ȸ - ���� (�˻�)
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

	// ������ ���� ��ȸ - �κ�
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
			LogUtil.I("���̻� ����");
		}
		return data;
	}

	// ������ ���� ��ȸ - �κ� ( api 11 ���� �������� ����ϴ� �� ) 
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
			LogUtil.I("���̻� ����");
		}
		return data;
	}
	
	// ������ ���� ��ȸ - �κ�  (�˻� �߰�)
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
			LogUtil.I("���̻� ����");
		}
		return data;
	}

	// ������ �ش���δ��� ���� ��� �� ���ã�⸦ ����
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

	// ������ �׸��� ���ο� ���ε� ��� ���� 
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

	// ����ڰ� ���ο� ���ε� ����� �� 
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

	// �÷��̸���Ʈ �߰�
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

	// �÷��̸���Ʈ �߰�
	public static long addPlayList(Context ctx, CollectedDto dto){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String sql = "select videoid from " + Define.TB_USER_PLAYLIST + " where videoid = ? and is_del = 1";
		Cursor c = db.rawQuery(sql , new String[]{dto.videoid});
		if(c.getCount() > 0){
			// ������Ʈ �Ѵ�
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

	// �÷��� ����Ʈ ��ȸ
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

	// �÷��̸���Ʈ ������
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
	
	// ���δ� ����Ʈ ������
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

	// �÷��̸���Ʈ ����
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

	// �÷��̸���Ʈ ���� �ǵ�����
	public static long alivePlayList(Context ctx, YTListDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "0");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

	// �÷��̸���Ʈ���� ����
	public static long removePlayList(Context ctx, YTListDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "1");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

	// �÷��̸���Ʈ���� ����
	public static long removePlayList(Context ctx, CollectedDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put(USER_PLAYLIST.is_del.toString(), "1");
		return db.update(Define.TB_USER_PLAYLIST, values, " videoid = ? ", new String[]{ dto.videoid });
	}

}
