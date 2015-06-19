package com.mcproject.net.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mcproject.net.conf.Define;

public class DbUtil {
	public static final String DB_FILE_NM = "ytfinder_db21.sqlite";
	public static SQLiteDatabase db;
	public static String DB_PATH = ""; 

	static public SQLiteDatabase getDb(Context ctx) {
		//DB_PATH = ctx.getCacheDir().getParentFile().getAbsolutePath();
		DB_PATH = Define.APP_FOLDER;
		try{
			if(db == null) {
				db = SQLiteDatabase.openDatabase( DB_PATH+"/"+DB_FILE_NM, 
					null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
			}
		}catch(Exception e){
			//Toast.makeText(ctx, text, duration)
		}
		return db;
	}
	
	static public boolean checkDataBase(Context ctx) {
		//DB_PATH = ctx.getCacheDir().getParentFile().getAbsolutePath();
		DB_PATH = Define.APP_FOLDER;
		
    	SQLiteDatabase checkDB = null;
    	try {
    		checkDB = SQLiteDatabase.openDatabase(DB_PATH + "/" + DB_FILE_NM, 
    				null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    	} catch(Exception e) {
    		return false;
    	}
    	if(checkDB!=null) checkDB.close();
    	LogUtil.D( "DB Path (checkDataBase): "+ DB_PATH + "/" + DB_FILE_NM);
    	return checkDB!=null;
    }
	
	static public void initDbFile(Context ctx) {
		//DB_PATH = ctx.getCacheDir().getParentFile().getAbsolutePath();
		DB_PATH = Define.APP_FOLDER;
		File outfile = new File(DB_PATH + "/" + DB_FILE_NM);
				
		if (outfile.length() <= 0) {
			AssetManager assetManager = ctx.getAssets();
			try {
				
				InputStream is = assetManager.open(DB_FILE_NM, AssetManager.ACCESS_BUFFER);
				long filesize = is.available();
				byte [] tempdata = new byte[(int)filesize];
				is.read(tempdata); 
				is.close();
				outfile.createNewFile();
				FileOutputStream fo = new FileOutputStream(outfile);
				fo.write(tempdata);
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getColumnData(Cursor c, String columnName) {
		if(c.getColumnIndex(columnName) != -1)
		return c.getString(c.getColumnIndex(columnName));
		else return "";
	}
	
	
}
