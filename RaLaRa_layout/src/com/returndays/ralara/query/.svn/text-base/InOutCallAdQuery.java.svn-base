package com.returndays.ralara.query;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.returndays.ralara.dto.CallAdCategoryDto;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;

public class InOutCallAdQuery {
	public static final String TB_NAME = "lock_ad_master";
	enum Columns{CAMP_IDX, AD_SEQ, IMG1,IMG2,IMG3,IMG4,AD_MOVIE, AD_KIND, START_TIME, END_TIME, END_ACTION, AD_TITLE,FILTER_SEX, FILTER_AGE_START, FILTER_AGE_END, AD_DESC, AD_STAT};

	public static void deleteInOutAD(Context ctx) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		db.delete(TB_NAME, null, null);
	}

	public static void deleteCamp(Context ctx, String idx) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		db.delete(TB_NAME, Columns.CAMP_IDX.toString() + " = ? ", new String[]{idx});
	}

	public static long insertInOutAD(Context ctx, CallAdDto dto) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new  ContentValues();
		values.put(Columns.AD_SEQ.toString(), dto.AD_SEQ);
		values.put(Columns.CAMP_IDX.toString(), dto.CAMP_IDX);
		values.put(Columns.IMG1.toString(), dto.IMG1);
		values.put(Columns.IMG2.toString(), dto.IMG2);
		values.put(Columns.IMG3.toString(), dto.IMG3);
		values.put(Columns.IMG4.toString(), dto.IMG4);
		values.put(Columns.AD_MOVIE.toString(), dto.AD_MOVIE);
		values.put(Columns.AD_KIND.toString(), dto.AD_KIND);
		values.put(Columns.START_TIME.toString(), dto.START_TIME);
		values.put(Columns.END_TIME.toString(), dto.END_TIME);
		values.put(Columns.END_ACTION.toString(), dto.END_ACTION);
		values.put(Columns.AD_TITLE.toString(), dto.AD_TITLE);
		values.put(Columns.FILTER_SEX.toString(), dto.FILTER_SEX);
		values.put(Columns.FILTER_AGE_START.toString(), dto.FILTER_AGE_START);
		values.put(Columns.FILTER_AGE_END.toString(), dto.FILTER_AGE_END);
		values.put(Columns.AD_DESC.toString(), dto.AD_DESC);
		values.put(Columns.AD_STAT.toString(), dto.AD_STAT);

		return db.insert(TB_NAME, "", values);
	}

	public static boolean isExistAd(Context ctx, String AD_SEQ){
		boolean rtn = false;
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String tbname = "call_out_ad_flag";
		Cursor c = db.query(tbname, new String[]{"ad_seq"}, "ad_seq = '" + AD_SEQ + "'", null, null, null, null);
		if(c.moveToFirst()){
			rtn = true;
		}else{
			rtn =  false;
		}
		if(c != null && !c.isClosed()) c.close();
		return rtn;
	}

	public static long insertAdFlag(Context ctx,  String ad_seq, String ad_title, String ad_kind, String end_action, String is_view){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String tbname = "call_out_ad_flag";
		ContentValues values = new ContentValues();
		values.put("ad_seq", ad_seq);
		values.put("ad_title", ad_title);
		values.put("ad_kind", ad_kind);
		values.put("end_action", end_action);
		values.put("is_view", is_view);

		return db.insert(tbname, "", values);
	}

	public static long updateAdFlag(Context ctx, String ad_seq, String is_view){

		LogUtil.D("updateAdFlag AD_SEQ = " + ad_seq);

		SQLiteDatabase db = DbUtil.getDb(ctx);
		String tbname = "call_out_ad_flag";
		ContentValues values = new ContentValues();
		values.put("is_view", is_view);

		return db.update(tbname, values, "ad_seq = '" + ad_seq + "'", null);
	}

	//	public static InOutCallAdDto selectInOutAd(Context ctx) {
	//		InOutCallAdDto item = new InOutCallAdDto();
	//		SQLiteDatabase db = DbUtil.getDb(ctx);
	//		Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMPAIGN_IDX.toString() + " ASC");
	//		if(c != null && c.getCount() > 0) {
	//			c.moveToNext();
	//			for(String columnStr : c.getColumnNames()) {
	//				try {
	//					Field field = item.getClass().getField(columnStr);
	//					field.set(item, DbUtil.getColumnData(c, columnStr));
	//				} catch (Exception e) {}
	//			}
	//		}
	//		if(c != null) c.close();
	//		return item;
	//	}

	public static ArrayList<CallAdDto> selectInOutAdList(Context ctx) {
		ArrayList<CallAdDto> datas = new ArrayList<CallAdDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		//Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMP_IDX.toString() + " DESC");
		String Query = "select * from "+TB_NAME+" where  ( cast(AD_KIND as numeric) > '99' and cast(AD_KIND as numeric) < '200' ) and ( datetime(START_TIME) < datetime('now') and datetime(END_TIME) > datetime('now')) order by camp_idx "; 
		Cursor c = db.rawQuery( Query , null);

		LogUtil.W("selectInOutAdList:"+((c==null)?"null":c.getCount()+" / " + Query));
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				CallAdDto item = new CallAdDto();
				for(String columnStr : c.getColumnNames()) {
					try {
						Field field = item.getClass().getField(columnStr);
						field.set(item, DbUtil.getColumnData(c, columnStr));
					} catch (Exception e) {}
				}
				datas.add(item);
			}
		}
		if(c != null && !c.isClosed()) c.close();
		return datas;
	}

	public static ArrayList<CallAdCategoryDto> selectInOutAdFlagList(Context ctx, int type) {
		ArrayList<CallAdCategoryDto> datas = new ArrayList<CallAdCategoryDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		String tbname = "call_out_ad_flag";
		//Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMP_IDX.toString() + " DESC");
		String Query = null;
		if(type == 0)
			Query = "select * from "+tbname;
		else if(type == 1){
			Query = "select * from "+tbname + " WHERE is_view = 'Y'";
		}
		Cursor c = db.rawQuery( Query , null);
		LogUtil.W("selectInOutAdFlagList:"+((c==null)?"null":c.getCount()+" / " + Query));
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				CallAdCategoryDto item = new CallAdCategoryDto();
				for(String columnStr : c.getColumnNames()) {
					try {
						Field field = item.getClass().getField(columnStr);
						field.set(item, DbUtil.getColumnData(c, columnStr));
					} catch (Exception e) {}
				}
				datas.add(item);
			}
		}
		if(c != null && !c.isClosed()) c.close();
		return datas;
	}

	public static CallAdDto selectInOutAdViewOne(Context ctx, String ad_seq) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		//Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMP_IDX.toString() + " DESC");
		String Query = "select * from "+TB_NAME+" where  ad_seq = ? "; 
		Cursor c = db.rawQuery(Query, new String[]{ ad_seq });
		CallAdDto item = new CallAdDto();
		//LogUtil.W("selectInOutAdList:"+((c==null)?"null":c.getCount()+" / " + Query));
		if(c != null && c.getCount() > 0) {
			if(c.moveToFirst()){
				for(String columnStr : c.getColumnNames()) {
					try {
						Field field = item.getClass().getField(columnStr);
						field.set(item, DbUtil.getColumnData(c, columnStr));
					} catch (Exception e) {}
				}
			}
		}
		if(c != null && !c.isClosed()) c.close();
		return item;
	}

	public static ArrayList<CallAdDto> selectInOutAdView(Context ctx, String call_idx) {
		ArrayList<CallAdDto> datas = new ArrayList<CallAdDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		//Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMP_IDX.toString() + " DESC");
		String Query = "select * from "+TB_NAME+" where  CAMP_IDX = ? "; 
		Cursor c = db.rawQuery( Query , new String[]{call_idx});

		//LogUtil.W("selectInOutAdList:"+((c==null)?"null":c.getCount()+" / " + Query));
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				CallAdDto item = new CallAdDto();
				for(String columnStr : c.getColumnNames()) {
					try {
						Field field = item.getClass().getField(columnStr);
						field.set(item, DbUtil.getColumnData(c, columnStr));
					} catch (Exception e) {}
				}
				datas.add(item);
			}
		}
		if(c != null && !c.isClosed()) c.close();
		return datas;
	}



	public static ArrayList<CallAdDto> selectLockAdList(Context ctx) {
		ArrayList<CallAdDto> datas = new ArrayList<CallAdDto>();
		SQLiteDatabase db = DbUtil.getDb(ctx);
		//Cursor c = db.query(TB_NAME, new String[]{"*"}, null, null, null, null, Columns.CAMP_IDX.toString() + " DESC");
		Cursor c = db.rawQuery("select * from "+TB_NAME+" where AD_STAT = '1' and (cast(AD_KIND as numeric) > '0' and cast(AD_KIND as numeric) < '100') and ( datetime(START_TIME) < datetime('now') and datetime(END_TIME) > datetime('now')) order by camp_idx ", null);

		LogUtil.W("selectInOutAdList:"+((c==null)?"null":c.getCount()+""));
		if(c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				CallAdDto item = new CallAdDto();
				for(String columnStr : c.getColumnNames()) {
					try {
						Field field = item.getClass().getField(columnStr);
						field.set(item, DbUtil.getColumnData(c, columnStr));
					} catch (Exception e) {}
				}
				datas.add(item);
			}
		}
		if(c != null && !c.isClosed()) c.close();
		return datas;
	}

	public static int selectAdCnt(Context ctx) {
		int cnt = 0;
		SQLiteDatabase db = DbUtil.getDb(ctx);

		Cursor c = db.rawQuery("select count(*) as cnt from "+TB_NAME+" where AD_STAT = '1' and (cast(AD_KIND as numeric) > '0' and cast(AD_KIND as numeric) < '100') and ( datetime(START_TIME) < datetime('now') and datetime(END_TIME) > datetime('now')) order by camp_idx ", null);


		if(c != null && c.getCount() > 0) {
			if(c.moveToFirst()){
				String cnt_str = c.getString(0);
				if(!cnt_str.equals("")){
					cnt = Integer.parseInt(cnt_str);
				}
			}
		}
		if(c != null && !c.isClosed()) c.close();

		return cnt;
	}


}
