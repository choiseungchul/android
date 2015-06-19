package com.mcproject.net.service;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfinder_dev.FavoriteActivity_v9;
import com.mcproject.ytfavorite_t.R;

public class ClientNotificaionService extends Service{

	NotificationManager mNotificationManager;
	private int NOTIFICATION_ID = 65536;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(AppUserSettings.getNotiAlert(getApplicationContext()).equals("Y") || AppUserSettings.getNotiAlert(getApplicationContext()).equals("")){
			mNotificationManager = (NotificationManager) getBaseContext().getSystemService(NOTIFICATION_SERVICE);

			LogUtil.I("ClientNotificaionService start...");

			ArrayList<String> list = DbQueryUtil.getNewUploadContent(getBaseContext());

			if(list.size() > 0){
				String notiTitle = getString(R.string.new_upload_content_added);
				String desc = getString(R.string.new_upload_content_add_desc);
				String uploaderNames = "";

				StringBuilder sb = new StringBuilder();
				for(int i = 0 ; i < list.size() ; i++){
					sb.append(list.get(i));
					sb.append(", ");
				}

				if(sb.length() > 2){
					uploaderNames = sb.substring(0, sb.length()-2);
				}

				desc = String.format(desc, uploaderNames);

				LogUtil.I("uploaderNames = " + uploaderNames);

				Intent notificationIntent = new Intent(getBaseContext(), FavoriteActivity_v9.class);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
		                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
		                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				notificationIntent.putExtra("tab_on", "2");

				PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
						notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.noti_icon)
				.setContentTitle(notiTitle)
				.setTicker(notiTitle)
				.setAutoCancel(false)			
				.setStyle(new NotificationCompat.BigTextStyle()
				.bigText(desc))
				.setContentText(desc);
				//				.setVibrate(new long[]{500, 500, 500});

				mBuilder.setContentIntent(contentIntent);
				mBuilder.setOngoing(false); //ÁøÇàÁß¿¡ ¶ß´Â°Ô¾Æ´Ô
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

			}
			LogUtil.I("ClientNotificaionService end...");
		}

		return super.onStartCommand(intent, flags, startId);
	}
}
