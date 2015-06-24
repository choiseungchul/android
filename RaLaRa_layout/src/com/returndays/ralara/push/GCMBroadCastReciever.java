package com.returndays.ralara.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.returndays.ralara.util.LogUtil;

public class GCMBroadCastReciever extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		LogUtil.D("GCM ONRECIEVE..............");
		
		
	}

//    public static final int NOTIFICATION_ID = 1;
//    //private NotificationManager mNotificationManager;
//    NotificationCompat.Builder builder;
//    Context ctx;
//    
//    @Override
//    public void onReceive(Context context, Intent intent) {
//    	// Explicitly specify that GcmIntentService will handle the intent.
//    	
//    	LogUtil.D("GCM ONRECIEVE FIRST..................");
//    	
//        ComponentName comp = new ComponentName(context.getPackageName(),
//                GCMIntentService.class.getName());
//        // Start the service, keeping the device awake while it is launching.
//        startWakefulService(context, (intent.setComponent(comp)));
//        setResultCode(Activity.RESULT_OK);
//    	
//        LogUtil.D("GCM ONRECIEVE. END ................");
//        
//    }
    
    


}
