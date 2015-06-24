package com.returndays.ralara.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.service.CallService;
import com.returndays.ralara.service.callOutService;

public class PhoneStateReceiver extends BroadcastReceiver {
	public static boolean mIsStarted = false;
	Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		/**
		 * 로그인 정보가 없으면 작동하지 않는다..
		 */
		mContext = context;
		if(Setting.getUserSeq(mContext).equals("")) return;
		
		String action = intent.getAction();
		String state = intent.getStringExtra("state");
		Intent service = new Intent(context, CallService.class);
		Intent servicecallout  = new Intent (context, callOutService.class);
		
		if (Setting.getCallAd(mContext).equals("OFF")) return;
		if (Setting.getCallAd(mContext).equals("")) return;
		
		//전화 상태가 바뀌었을 때
		if(action.equalsIgnoreCase(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
			LogUtil.D("aaaaa","11111111111111111111111111111111111111111111111");
			//전화가 왔을 때
			if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				
				LogUtil.D("aaaaa","333333333333333333333333333333333333333333333333333");
				
				String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				mIsStarted = true;
				
				service.putExtra("isOn", true);
				service.putExtra("inout", "I");
				service.putExtra("number", number);
				service.putExtra("name", getName(number));
				service.putExtra("type", TelephonyManager.EXTRA_STATE_RINGING);
				context.startService(service);
			// 통화중
			} else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				LogUtil.D("aaaaa","44444444444444444444444444444444444444444");
				if(mIsStarted) {
					LogUtil.D(TelephonyManager.EXTRA_STATE_OFFHOOK);
					service.putExtra("isOn", false);
					service.putExtra("type", TelephonyManager.EXTRA_STATE_OFFHOOK);
					context.startService(service);
					LogUtil.D("aaaaa","5555555555555555555555555555555555555555555555");
				} else {
					LogUtil.D("aaaaa","6666666666666666666666666666666666666666666666");
				}
			} else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				LogUtil.D("aaaaa","777777777777777777777777777777777777777777777777");	
				servicecallout.putExtra("isOn", false);
				servicecallout.putExtra("type", TelephonyManager.EXTRA_STATE_IDLE);
				context.startService(servicecallout);

			}
		//전화 했을 때
		} else if(action.equalsIgnoreCase(Intent.ACTION_NEW_OUTGOING_CALL)) {
			LogUtil.D("aaaaa","2222222222222222222222222222222222222222222222222222");
			String number = getResultData();
			getName(number);
			mIsStarted = false;
			servicecallout.putExtra("isOn", true);
			servicecallout.putExtra("inout", "O");
			servicecallout.putExtra("number", number);
			servicecallout.putExtra("name", getName(number));
			context.startService(servicecallout);
			
		}



	}

	String getName(String phoneNumber) {

		//phoneNumber = phoneNumber.replaceAll("-", "");
		String phoneNum = MadUtil.getPhoneNumber(mContext);
		
		LogUtil.W("dd", "phoneNumber:"+phoneNum);

		String name = "";
		Cursor c = null;
		try {
			c = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
					new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, 
					ContactsContract.CommonDataKinds.Phone.NUMBER +" = ? or " 
					+  ContactsContract.CommonDataKinds.Phone.NUMBER + "= ? " 
					, new String[]{  phoneNumber ,   phoneNum }, null);
			if(c != null && c.getCount() > 0) {
				c.moveToFirst();
				name = c.getString(0);
				LogUtil.W("999", "name:"+name);
			} else {
				LogUtil.W("999", "name:fail");
			}
		} catch (Exception e) {
		} finally {
			if(c != null) c.close();
		}

		return name;
	}

	String getNameReal(String ph){
		String username = "";

		Uri myPerson=ContactsContract.Contacts.CONTENT_URI;
		Cursor cur = mContext.getContentResolver().query(myPerson, null, null, null, null );
		try {
			if (cur.moveToFirst()) {

				String contactId;
				int nameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME); 
				int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);
				do {
					String name = cur.getString(nameColumn);
					contactId = cur.getString(idColumn);
					Cursor phones = mContext.getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId,
							null,
							null);
					while (phones.moveToNext()) {
						String	phoneNumber = phones
								.getString(phones .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneNumber = phoneNumber.replaceAll("-", "");
						if(ph.equals(phoneNumber)){
							username = name;
						}
						//LogUtil.W("999", "name:"+name + " phoneNumber="+ phoneNumber );

					}
					phones.close();
				} while (cur.moveToNext());
			}
		} catch (Exception e) {
		} finally {
			if(cur != null) cur.close();
		}


		return username;
	}



}
