package com.mcproject.net.reciever;

import com.mcproject.net.util.LogUtil;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class DownloadManagerReciever extends BroadcastReceiver{

	private DownloadManager mManager;

	@Override
	public void onReceive(Context arg0, Intent intent) {
		
		mManager = (DownloadManager) arg0.getSystemService(arg0.DOWNLOAD_SERVICE);
		
		String action = intent.getAction();
		int id = intent.getExtras().getInt("dn_id");
		
		if (!DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			return;
		}
		//                context.getApplicationContext().unregisterReceiver(receiver);
		Query query = new Query();
		query.setFilterById(id);
		Cursor c = mManager.query(query);
		if (c.moveToFirst()) {
			int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
			if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
				String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				LogUtil.I("downloaded file " + uriString);         
				
				
				
			} else {
				LogUtil.I("download failed " + c.getInt(columnIndex));
				LogUtil.I("download failed reason = " + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));
			}
		}
		
	}
	
}
