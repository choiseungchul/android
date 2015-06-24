package com.returndays.ralara;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.service.AdSlideService;
import com.returndays.ralara.service.PushToastService;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;

public class GCMIntentService extends IntentService{

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	public static boolean isScreenOn = false;
	int isLive = 0;
	private AudioManager aManager;
	private MediaPlayer mediaPlayer;

	public GCMIntentService (){
		super(Define.GCM_PROJECT_ID);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {

		LogUtil.D("Service Binded===================");

		return null;
	}



	int resultCode = 0;

	private void sendNotification(HashMap<String, String> data) {

		resultCode++;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.get("url")));

		// Talk 댓글
		if(data.get("flag").equals("1")){
//			playPushSound();
			notificationIntent = new Intent(getApplicationContext(), TalkInActivity.class);
			LogUtil.D("gcm service room_seq = " + data.get("room_seq").toString());
			notificationIntent.putExtra("room_seq", data.get("room_seq").toString());

			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_icon)
			.setContentTitle(data.get("title"))			
			.setAutoCancel(true)
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(data.get("desc")))
			.setContentText(data.get("desc"))
			.setVibrate(new long[]{500, 500, 500});

			mBuilder.setContentIntent(contentIntent);
			mBuilder.setOngoing(false); //진행중에 뜨는게아님
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}else if(data.get("flag").equals("2")){
			// 광고 (스크래치)
			// 상단 노티바에 내용 보여주기
//			playPushSound();
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_icon)
			.setContentTitle(data.get("title"))
			.setTicker(data.get("title"))
			.setAutoCancel(true)			
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(data.get("desc")))
			.setContentText(data.get("desc"))
			.setVibrate(new long[]{500, 500, 500});

			mBuilder.setContentIntent(contentIntent);
			mBuilder.setOngoing(false); //진행중에 뜨는게아님
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}else if(data.get("flag").equals("3")){
			// 이벤트
//			playPushSound();
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_icon)
			.setContentTitle(data.get("title"))
			.setTicker(data.get("title"))
			.setAutoCancel(true)			
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(data.get("desc")))
			.setContentText(data.get("desc"))
			.setVibrate(new long[]{500, 500, 500});

			mBuilder.setContentIntent(contentIntent);
			mBuilder.setOngoing(false); //진행중에 뜨는게아님
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			// 상단 노티바에 내용 보여주기
		}
		else if(data.get("flag").equals("4")){
			// 그냥알림
			// 5초뒤 제거
//			playPushSound();
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_icon)
			.setContentTitle(data.get("title"))
			.setTicker(data.get("title"))
			.setAutoCancel(true)			
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(data.get("desc")))
			.setContentText(data.get("desc"))
			.setVibrate(new long[]{500, 500, 500});

			mBuilder.setContentIntent(contentIntent);
			mBuilder.setOngoing(false); //진행중에 뜨는게아님
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
		else if(data.get("flag").equals("5")){
			// 다른거..
			// 노티바 대신 다이어로그 뜸
			// 서비스 시작
//			playPushSound();
			Intent ii = new Intent(getApplicationContext(), PushToastService.class);
			ii.putExtra("title", data.get("title"));
			ii.putExtra("content", data.get("desc"));
			startService(ii);
			
		}else if(data.get("flag").equals("6")){
			// 알 선물받음
			notificationIntent = new Intent(getApplicationContext(), AdlistActivity.class);
//			playPushSound();
			PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
					notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.noti_icon)
			.setContentTitle(data.get("title"))
			.setTicker(data.get("title"))
			.setAutoCancel(true)			
			.setStyle(new NotificationCompat.BigTextStyle()
			.bigText(data.get("desc")))
			.setContentText(data.get("desc"))
			.setVibrate(new long[]{500, 500, 500});

			mBuilder.setContentIntent(contentIntent);
			mBuilder.setOngoing(false); //진행중에 뜨는게아님
			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		}
	}

	private void playPushSound(){
		aManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		switch(aManager.getRingerMode())
		{
		case AudioManager.RINGER_MODE_SILENT: 
			break; 
		case AudioManager.RINGER_MODE_VIBRATE:  
			break;  
		case AudioManager.RINGER_MODE_NORMAL: 
			String soundName = Setting.getAlarmSound(getApplicationContext());

			if(soundName.equals("")){
				playSound("alarm_idx_03");
			}else{
				playSound(soundName);
			}
			break;
		};
	}

	private void playSound(String sound){
		LogUtil.D("sound name = " + sound);
		if(mediaPlayer != null){
			if(mediaPlayer.isPlaying()){
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
		try{
			Uri audio = Uri.parse("android.resource://" + getPackageName()+ "/raw/" + sound);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setLooping(false);
			mediaPlayer.setDataSource(getApplicationContext(), audio);
			mediaPlayer.prepare();
			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.release();
					mediaPlayer = null;
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void insertPushAlarm(String title, String flag, String desc, String room_seq, String reply, String date, String url){
		
		LogUtil.D("insert push alarm---");
		
		SQLiteDatabase db = DbUtil.getDb(getApplicationContext());
		String tbname = "talk_push_list";
		Cursor c = db.rawQuery("select count(*) AS CNT from talk_push_list where ROOM_SEQ = " + room_seq, null);
		if(c.moveToFirst()){
			int count = c.getInt(0);
			LogUtil.D("count = " + count);

			ContentValues value = new ContentValues();
			value.put("TITLE", title);
			value.put("FLAG", flag);
			value.put("DESC", desc);
			value.put("ROOM_SEQ", room_seq);
			value.put("REPLY", reply);
			value.put("URL", url);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
			Date datetime = new Date();
			value.put("DATE", dateFormat.format(datetime));

			long id = 0;
			if(count > 0){
				// 업데이트
				id = db.update(tbname, value, "ROOM_SEQ = " + room_seq , null);
			}else{
				// 인서트
				id = db.insert(tbname, "", value);
			}
			LogUtil.D("exeUpdate Result = " + id);
		}else{
			LogUtil.D("cursor empty....");
			
			ContentValues value = new ContentValues();
			value.put("TITLE", title);
			value.put("FLAG", flag);
			value.put("DESC", desc);
			value.put("ROOM_SEQ", room_seq);
			value.put("REPLY", reply);
			value.put("URL", url);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
			Date datetime = new Date();
			value.put("DATE", dateFormat.format(datetime));
			
			long id = db.insert(tbname, "", value);
			
			LogUtil.D("exeUpdate Result = " + id);
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LogUtil.D("gcm on message action = " + intent.getAction());
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.
					MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				//sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_DELETED.equals(messageType)) {
				//sendNotification("Deleted messages on server: " +
				LogUtil.D(extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.
					MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				LogUtil.D( "Received: " + extras.toString());

				// Post notification of received message.
				//
				String user_seq = null, room_seq = null , reply = null, url = null,
						date = null, title = null, activity = null, flag = null, desc = null;
				try {
					room_seq = URLDecoder.decode(extras.get("room_seq").toString(), "UTF-8" );
					reply = URLDecoder.decode(extras.get("reply").toString(), "UTF-8" );
					date = URLDecoder.decode(extras.get("date").toString(), "UTF-8" );
					title = URLDecoder.decode(extras.get("title").toString(), "UTF-8" );
					activity = URLDecoder.decode(extras.get("activity").toString(), "UTF-8" );
					flag = URLDecoder.decode(extras.get("flag").toString(), "UTF-8" );
					url = URLDecoder.decode(extras.get("url").toString(), "UTF-8" );
					desc = URLDecoder.decode(extras.get("desc").toString(), "UTF-8" );
					user_seq = URLDecoder.decode(extras.get("user_seq").toString(), "UTF-8" );

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}

				HashMap<String, String> dto = new HashMap<String, String>();
				dto.put("room_seq", room_seq);
				dto.put("reply", reply);
				dto.put("date", date);
				dto.put("title", title);
				dto.put("activity", activity);
				dto.put("flag", flag);
				dto.put("url", url);
				dto.put("desc", desc);
				dto.put("user_seq", user_seq);

				ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> Info = am.getRunningTasks(1);

				ComponentName topActivity = Info.get(0).topActivity;

				LogUtil.D("top_activity = " + topActivity.getClassName());

				if(topActivity.getClassName().equals("com.returndays.ralara.TalkInActivity")){
					// 댓글일경우
					if(flag.equals("1")){
						insertPushAlarm(title, flag, desc, room_seq, reply, date, url);
						//TalkInActivity.getInstance().otherRoomPushReload();
						if(TalkInActivity.getInstance() != null){
							if(Setting.getUserSeq(getApplicationContext()).equals(user_seq)){
								TalkInActivity.getInstance().reloadCommnet(false);
							}else{
								TalkInActivity.getInstance().reloadCommnet(true);
							}
							TalkInActivity.getInstance().otherPushReload();
						}
					}else{
						sendNotification(dto);
					}
				}else{
					if(Setting.getPushAlarm(getApplicationContext()).equals("Y")){
						sendNotification(dto);						
						if(AdSlideService.screenOn == false){
							// 화면 꺼짐
							if(Setting.getPushScreenOff(getApplicationContext()).equals("Y")){
								if(topActivity.getPackageName().equals("com.returndays.ralara") ){
									//댓글일 경우만
									if(flag.equals("1")){
										if((topActivity.getPackageName() + ".AdSlideActivity").equals(topActivity.getClassName())){
											// adslide가 최상단에 있을경우
											LogUtil.D("adslide is topActivity");
											if(AdSlideActivity.getInstance() != null){
												AdSlideActivity.getInstance().finish();
											}else{
												LogUtil.D("AdSlideActivity is NULL");
											}
										}
										Intent ii = new Intent(getApplicationContext(), TalkPopupActivity.class);
										ii.putExtra("room_seq", dto.get("room_seq"));
										ii.putExtra("title", dto.get("title"));
										ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(ii);
									}
								}
							}
						}else{
							// 화면 켜져있는상태
							if(Setting.getPushMessageAlarm(getApplicationContext()).equals("Y")){
								//								LogUtil.D("WindowViewService Service Start");
								//								Intent ii = new Intent(getApplicationContext(), WindowViewService.class);
								//								startService(ii);							
							}

							if(topActivity.getPackageName().equals("com.returndays.ralara")){
							}else{

							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		LogUtil.D("GCMIntentService Destroyed");
		super.onDestroy();
	}

}
