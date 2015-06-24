package com.momsfree.net.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.momsfree.util.LogUtil;

public class InstallReciever extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {

		LogUtil.D("action = " + intent.getAction());
		
		Log.i("momsfree install tag", "============================app install changed==========================================================================================================");
		
		
		
	}

}
