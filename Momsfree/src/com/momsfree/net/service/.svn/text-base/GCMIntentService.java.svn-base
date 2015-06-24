package com.momsfree.net.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.momsfree.net.R;
import com.momsfree.net.SplashActivity;
import com.momsfree.net.conf.Define;
import com.momsfree.net.reciever.GcmBroadcastReceiver;
import com.momsfree.util.LogUtil;

public class GCMIntentService extends IntentService{

	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private int resultCode = 0;
	String TAG = "MOMSFREE";

	public GCMIntentService (){
		super(Define.GCM_PROJECT_ID);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.D("gcm intent service binded");
		return super.onBind(intent);
	}

	@Override
    protected void onHandleIntent(Intent intent) {
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
                Toast.makeText(getApplicationContext(), "푸시 전송 오류 : " + extras.toString(), Toast.LENGTH_SHORT).show();
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
            	Toast.makeText(getApplicationContext(), "푸시 전송 오류 : " + extras.toString(), Toast.LENGTH_SHORT).show();
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                
                Log.i(TAG, "Recieved : " + extras.toString());
                // Post notification of received message.
                String title = extras.getString("title");
                String msg = extras.getString("message");
                String msg_info = extras.getString("info");
                
                sendNotification(title, msg, msg_info);
                
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

	// Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("GCM Notification")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
	
	private void sendNotification(String title, String msg, String msg_info) {
		resultCode ++;

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), resultCode,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setTicker(msg)
//		.setLargeIcon()
		.setSmallIcon(R.drawable.push_icon)
		.setContentTitle(title)
		.setAutoCancel(true)
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(msg))
		.setContentText(msg_info)
		.setVibrate(new long[]{500, 500, 500});

		mBuilder.setContentIntent(contentIntent);
		mBuilder.setOngoing(false); //진행중에 뜨는게아님
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		
	}


}
