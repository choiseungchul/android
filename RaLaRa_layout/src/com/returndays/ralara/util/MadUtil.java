package com.returndays.ralara.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.SAXException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.util.XmlDom;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.R;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.dto.DevideInfoDto;
import com.returndays.ralara.dto.RecommFriendDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;

public class MadUtil {

	// DB 데이터 삭제
	public static void DBDelete(Context ctx) {
		SQLiteDatabase db = DbUtil.getDb(ctx);
		db.delete("call_in_out_feedback_fail" , "", null);
		db.delete("call_in_out_ad" , "", null);
		db.delete("lock_ad_master" , "", null);
		db.delete("talk_room" , "", null);
		db.delete("talk_push_list" , "", null);
		db.delete("ad_alert" , "", null);
		db.delete("call_out_ad_flag" , "", null);
		db.delete("recomm_friend_list" , "", null);
	}

	//Sqlite에 서버에서 가져온 추천목록 입력
	public static void syncRecomm(Context ctx, String[] recomm_hp){
		SQLiteDatabase db = DbUtil.getDb(ctx);
		ContentValues values = new ContentValues();
		values.put("is_recomm", "2");
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0 ; i < recomm_hp.length; i++){
			sb.append("'");
			sb.append(recomm_hp[i]);
			sb.append("',");
		}
		
		String where = sb.toString().substring(0, sb.length() - 1);
		
		db.update("recomm_friend_list", values, "phone_number IN ( " + where + " )", null);
	}
	
	// SQLIte에 연락처 동기화
	public static void syncContact(Context context, HashMap<String, RecommFriendDto> c_list){	
		c_list = MadUtil.getContactList(context);
		
		SQLiteDatabase insertDb = DbUtil.getDb(context);

		insertDb.beginTransaction();

		insertDb.execSQL("delete from recomm_friend_list");
		
		for(Entry<String, RecommFriendDto> dto : c_list.entrySet()){
			ContentValues values = new ContentValues();
			values.put("phone_number", dto.getValue().FRIEND_HP);
			values.put("display_name", dto.getValue().FRIEND_NM);
			values.put("is_recomm", "0");
			insertDb.insert("recomm_friend_list", null, values);
			
		}
		insertDb.setTransactionSuccessful();
		insertDb.endTransaction();
	}
	
	public static List<String> getCategoryList(String gubun){
		List<String> list = new ArrayList<String>();
		
		// 10대
		if(gubun.equals("1")){
			list.add("106");
			list.add("108");
			list.add("102");
			list.add("115");
		}else if(gubun.equals("2")){
			list.add("106");
			list.add("101");
			list.add("103");
			list.add("108");
			list.add("115");
			list.add("102");
			list.add("113");
		}else if(gubun.equals("3")){
			list.add("101");
			list.add("106");
			list.add("116");
			list.add("105");
			list.add("110");
			list.add("112");
			list.add("102");
			list.add("117");
		}else if(gubun.equals("4")){
			list.add("101");
			list.add("109");
			list.add("112");
			list.add("117");
			list.add("105");
		}else if(gubun.equals("5")){
			list.add("101");
			list.add("104");
			list.add("117");
			list.add("110");
			list.add("112");
		}else if(gubun.equals("6")){
			list.add("101");
			list.add("110");
			list.add("109");
		}else if(gubun.equals("7")){
			
		}
		
		list.add("150");
		list.add("151");
		list.add("152");
		list.add("153");
		list.add("154");
		
		return list;
	}
	
	// 연락처 목록 서버로 전송
	public static void sendContactList(final Context ctx, final Runnable run){
		// 중복검사
		final HttpDocument getFriends = new HttpDocument(ctx);
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(ctx));
		getFriends.getDocument(UrlDef.FRIEND_RECOMM_LIST, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("ResultTable").select("Result").text().equals("true")){
					final HashMap<String, RecommFriendDto> contacts = getContactList(ctx);

					LogUtil.D("recomm List = " + document.select("ReturnTable").toString());

					boolean isFirst = false;

					Elements els = document.select("ReturnTable");
					if(els.size() == 0){
						isFirst = true;
					}

					// 연락처 문자열넣기
					StringBuffer fr_hp = new StringBuffer();
					StringBuffer fr_nm = new StringBuffer();

					String my_hp = getPhoneNumber(ctx);

					for( Entry<String, RecommFriendDto> datas : contacts.entrySet() ){
						if(!isFirst){
							// 중복 확인
							boolean isDuplicate = false;
							for(Element el : els){
								String pn = el.select("FRIEND_HP").text();
								if(pn.equals(datas.getKey())){
									isDuplicate = true;
								}
							}
							if(!isDuplicate){
								if(!datas.getKey().equals(my_hp)){
									fr_hp.append(datas.getKey());
									fr_hp.append("|");
									fr_nm.append(datas.getValue().FRIEND_NM);
									fr_nm.append("|");
								}
							}
						}else{
							// 처음넣은경우
							if(!datas.getKey().equals(my_hp)){
								fr_hp.append(datas.getKey());
								fr_hp.append("|");
								fr_nm.append(datas.getValue().FRIEND_NM);
								fr_nm.append("|");
							}
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					if(fr_hp.length() > 0){

						fr_hp.substring(0, fr_hp.length() - 1);
						fr_nm.substring(0, fr_nm.length() - 1);

						final HttpDocument http = new HttpDocument(ctx);
						http.setMethod("POST");

						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("USER_SEQ", Setting.getUserSeq(ctx));

						params.put("FRIEND_HP", fr_hp.toString() );
						params.put("FRIEND_NM", fr_nm.toString() );

						http.getDocument(UrlDef.FRIEND_ADD, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {
								if(document.select("ResultTable").select("Result").text().equals("true")){
									LogUtil.D("send add friend success ");
									//									Toast.makeText(ctx, "친구목록 동기화 완료.", Toast.LENGTH_LONG).show();

									if(run != null){
										Handler handle = new Handler();
										handle.post(run);
									}
								}
								http.threadStop();
							}
						}, false);
					}else{
						//						Toast.makeText(ctx, "동기화할 연락처가 없습니다.", Toast.LENGTH_LONG).show();
						if(run != null){
							Handler handle = new Handler();
							handle.post(run);
						}
					}
				}

				getFriends.threadStop();
			}
		}, false);		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static HashMap<String, RecommFriendDto> getContactList(Context mContext) {

		HashMap<String, RecommFriendDto> list = new HashMap<String, RecommFriendDto>();

		String sortOrder = ContactsContract.Data.CONTACT_ID + " ASC";
		// 중복제거
		String selection = "((" + 
				ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
				ContactsContract.Data.CONTACT_ID + " != '' ))" ;

		Cursor mCursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, selection, null, sortOrder);

		String name;
		String number;
		long id;

		String my_hp = getPhoneNumber(mContext);
		
		LogUtil.D("all contact count = " + mCursor.getCount());
		

		if (mCursor.moveToFirst() && mCursor.getCount() > 0) {
			do {
				name = mCursor.getString(mCursor.getColumnIndex(Phone.DISPLAY_NAME));
				number = mCursor.getString(mCursor.getColumnIndex(Phone.NUMBER)).replaceAll("-", "").replace(" ", "");
				id = mCursor.getLong(mCursor.getColumnIndex(Phone._ID));

				if(number.startsWith("+82")){
					number = number.replace("+82", "");
					number = "0" + number;
					LogUtil.D("+82 number = " + number);
				}
				
				if( ( number.startsWith("010") || number.startsWith("011")  || number.startsWith("016") || number.startsWith("017") ||number.startsWith("018") ||number.startsWith("019")  )
						&& !name.trim().equals("")){
					if(number.length() == 11 || number.length() == 10){
						if(!list.containsKey(number)){
							if(!number.equals(my_hp)){
								RecommFriendDto dto = new RecommFriendDto();

								dto.FRIEND_NM = name;
								dto.FRIEND_HP = number;
								dto.RESULT = "0";
								list.put(number, dto);
							}
						}
					}
				}

			} while(mCursor.moveToNext());
		}

		mCursor.close();

		return list;

	}



	public static void addShortCut(Context context){
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClassName(context.getPackageName(), context.getPackageName() + ".SplashActivity");
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name_kr));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher ));
		addIntent.putExtra("duplicate", false);	// 중복안되게
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT"); 
		context.sendBroadcast(addIntent);
		//		Toast.makeText(context, "바탕화면에 바로가기가 설치되었습니다.", Toast.LENGTH_SHORT).show();
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String phone = null;
		try{
			if(tMgr.getLine1Number() != null){
				phone = tMgr.getLine1Number();
			}
			if(phone != null){
				phone = phone.replaceAll("-", "").trim();
				if(phone.length() == 11 || phone.length() == 13){
					phone = phone.substring(phone.length()-10, phone.length());
				}else if(phone.length() == 10 || phone.length() == 12){
					phone = phone.substring(phone.length()-9, phone.length());
				}else{
					phone = phone.substring(phone.length()-10, phone.length());
				}
				phone="0" + phone;
			}
		}catch(Exception e){
			LogUtil.D(e.getMessage());
		}
		return phone;
	}

	public static String randomString(String chars, int length) {
		Random rand = new Random();
		StringBuilder buf = new StringBuilder();
		for (int i=0; i<length; i++) {
			buf.append(chars.charAt(rand.nextInt(chars.length())));
		}
		return buf.toString();
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						@SuppressWarnings("deprecation")
						String ip = Formatter.formatIpAddress(inetAddress.hashCode());
						LogUtil.D("MY IP ADDRESS = " + ip);
						return ip;
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static boolean sendSMS(Context ctx, String phoneNumber, String sender, String message) {
		try {
			PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_SENT"), 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_DELIVERED"), 0);

			SmsManager sms = SmsManager.getDefault(); 
			sms.sendTextMessage(phoneNumber, sender, message, sentPI, deliveredPI);

			return true;
		} catch (Exception e) {
			LogUtil.E("MadUtil sendSMS:"+e.toString());
			return false;
		}
	}

	public static View disableEnableControls(boolean enable, ViewGroup vg){
		for (int i = 0; i < vg.getChildCount(); i++){
			View child = vg.getChildAt(i);
			child.setEnabled(enable);
			if (child instanceof ViewGroup){ 
				disableEnableControls(enable, (ViewGroup)child);
			}
		}
		return vg;
	}
	/**
	 * Result = ok:성공, already_insert:이미수령함, insert_error:DB오류
	 * @param ctx
	 * @param retCode
	 * @return
	 */
	public static boolean PointResultToast(Context ctx, String retCode) {
		if(retCode.equals("ok")) {

			Toast.makeText(ctx, "포인트가 적립되었습니다.", Toast.LENGTH_LONG).show();
			return true;
		} else if(retCode.equals("already_save")){

			Toast.makeText(ctx, "포인트가 이미 적립되었습다.", Toast.LENGTH_LONG).show();
			return false;
		} else {

			Toast.makeText(ctx, "처리중 오류가 발생하였습니다."+retCode, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public static float DptoPixel(float density, float dp) {
		return dp*(density/160f);
	}

	public static float PixelToDp(float density, float px) {
		return px/(density/160f);
	}

	/*
	public static void TabInit(final Activity act) {
		int index = -1;
		if(act instanceof AdListActivity) {
			index = 0;
		} else if(act instanceof StoreActivity) {
			index = 1;
		} else if(act instanceof MyPageActivity) {
			index = 2;
		} else if(act instanceof MoreViewActivity) {
			index = 3;
		}

		ArrayList<TabInfo> tabInfos = new ArrayList<TabInfo>();
		tabInfos.add(new TabInfo((LinearLayout) act.findViewById(R.id.tab1), AdListActivity.class));
		tabInfos.add(new TabInfo((LinearLayout) act.findViewById(R.id.tab2), StoreActivity.class));
		tabInfos.add(new TabInfo((LinearLayout) act.findViewById(R.id.tab3), MyPageActivity.class));
		tabInfos.add(new TabInfo((LinearLayout) act.findViewById(R.id.tab4), MoreViewActivity.class));
		for(int i=0;i<tabInfos.size();i++) {
			final TabInfo info = tabInfos.get(i);
			info.tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(act, info.cls);
					intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					act.finish();
					act.overridePendingTransition(0, 0);
					act.startActivity(intent);
				}
			});

			if(i != index) {
				disableEnableControls(false, info.tab);
			}
		}
	}
	 */
	public static void autoMappingXmlToObject(XmlDom xml, Object obj) {
		Field[] fields =  obj.getClass().getFields();
		for(Field field : fields) {
			if(xml.tag(field.getName()) != null) {
				try {
					field.set(obj, xml.tag(field.getName()).text());
				} catch (Exception e) {}
			}
		}
	}

	public static void autoMappingXmlToObject(Element xml, Object obj) {
		Field[] fields =  obj.getClass().getFields();
		for(Field field : fields) {
			if(xml.select(field.getName()) != null) {
				try {
					field.set(obj, xml.select(field.getName()).text());
				} catch (Exception e) {}
			}
		}
	}

	public static String priceFormat(String txt) {
		try {
			DecimalFormat df = new DecimalFormat("###,###");
			return df.format(Double.parseDouble(txt));
		} catch (Exception e) {
			return "";
		}
	}


	private static class TabInfo {
		LinearLayout tab;
		Class<?> cls;
		public TabInfo(LinearLayout tab, Class<?> cls) {
			this.tab = tab;
			this.cls = cls;
		}
	}


	public static boolean isNetworkConnected(Context ctx) {
		ConnectivityManager connMgr = (ConnectivityManager) 
				ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}


	public static String getMyPhoneNumber(Context ctx){
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyMgr.getLine1Number();
	}

	public static void startAlarmService(Context ctx, Class<?> serviceCls, int serviceID, long interval, int delaySecond) {
		Intent i = new Intent(ctx, serviceCls);
		PendingIntent sender = PendingIntent.getService(ctx, serviceID, i, 0);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, delaySecond);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
				calendar.getTimeInMillis(), 
				interval, 
				sender);
	}

	public static String getOSVersionString(int version){
		String versionString = null;

		switch (version) {
		case 2:
			versionString = "? 1.1";
			break;
		case 3:
			versionString = "1.5";
			break;
		case 4:
			versionString = "1.6";
			break;
		case 5:
			versionString = "2.0";
			break;
		case 6:
			versionString = "2.0.1";
			break;
		case 7:
			versionString = "2.1";
			break;
		case 8:
			versionString = "2.2";
			break;
		case 9:
			versionString = "2.3";
			break;
		case 10:
			versionString = "2.3.3-2.3.4";
			break;
		case 11:
			versionString = "3.0";
			break;
		case 12:
			versionString = "3.1";
			break;
		case 13:
			versionString = "3.2";
			break;
		case 14:
			versionString = "4.0";
			break;
		case 15:
			versionString = "4.0.3";
			break;
		case 16:
			versionString = "4.1";
			break;
		case 17:
			versionString = "4.2";
			break;
		case 18:
			versionString = "4.3";
			break;
		case 19:
			versionString = "4.4";
			break;
		default:
			versionString = "over";
			break;
		}

		return versionString;
	}


	static public boolean setSharedPreferencesValue(Context ctx, String nm, int mode, String key, String value) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	static public String getSharedPreferencesValue(Context ctx, String nm, int mode, String key) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		return pref.getString(key, "");
	}

	public static void clearAllPrefferencesLogout(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Define.PREFER_ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	// 회원탈퇴시
	public static void clearAllPrefferencesWithraw(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Define.PREFER_ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
		
		SharedPreferences pref2 = ctx.getSharedPreferences(Define.PREFER_ID2, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor2 = pref2.edit();
		editor2.clear();
		editor2.commit();
	}
	
	@SuppressLint("NewApi")
	public static void clearAllPrefferencesImmediate(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Define.PREFER_ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.apply();
	}

	static public byte[] readFile(File file) throws IOException {
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength) throw new IOException("File size >= 2 GB");
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		}
		finally {
			f.close();
		}
	}

	static public XmlDom getConfigFile() {
		File f = new File(Define.AD_APP_FOLDER + "/" + Define.CONFIG_FILE_NM);

		LogUtil.W(f.getAbsolutePath());
		LogUtil.W("f.exists():" + f.exists());

		XmlDom xml = null;
		try {
			xml = new XmlDom(MadUtil.readFile(f));
		} catch (SAXException e) {
			LogUtil.E("getConfigFile1:"+e.toString());
		} catch (IOException e) {
			LogUtil.E("getConfigFile2:"+e.toString());
		}
		return xml;
	}

	public static boolean TstoreCheck(Activity act) {
		PackageManager pm = act.getPackageManager();
		List< ApplicationInfo > appList = pm.getInstalledApplications( 0 );
		ApplicationInfo app = null;
		int nSize = appList.size();
		for( int i = 0; i < nSize; i++ ) {
			app = appList.get( i );
			if(app.packageName.indexOf("com.skt.skaf.OA00648870") != -1) {
				return true;
			}
		}
		return false;
	}

	public static void TstoreLink(Activity activity, String aid) {
		aid = aid.replace("OA", "00");
		LogUtil.W("TstoreLink aid:"+aid);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //mandatory flag
		intent.setClassName("com.skt.skaf.OA00648870","com.skt.skaf.OA00648870.OA00648870");
		intent.setAction("COLLAB_ACTION");	//action
		intent.putExtra("com.skt.skaf.COL.URI",("PRODUCT_VIEW/"+aid+"/0").getBytes());	//user data
		intent.putExtra("com.skt.skaf.COL.REQUESTER","OA00648870");	//my App ID
		activity.startActivity(intent);
	}

	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			LogUtil.D("LoadImageFromWebOperations ERROR = " + e.getMessage());
			return null;
		}
	}

	// 라운딩 처리만
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);


		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		bitmap.recycle();

		return output;
	}

	// 라운딩 + 컬러값
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, int myColor) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);


		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		ColorFilter filter = new PorterDuffColorFilter(myColor, PorterDuff.Mode.SRC_OVER);
		paint.setColorFilter(filter);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		bitmap.recycle();

		return output;
	}

	/*
	 * 
	 * 
	 * */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels, int myColor, int width, int height) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);


		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		ColorFilter filter = new PorterDuffColorFilter(myColor, PorterDuff.Mode.SRC_OVER);
		paint.setColorFilter(filter);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		bitmap.recycle();

		return output;
	}

	public static DevideInfoDto getDevideInfo(){
		DevideInfoDto dto = new DevideInfoDto();
		dto.DEVICE = Build.DEVICE;
		dto.BRAND = Build.BRAND;
		dto.MODEL = Build.MODEL;
		dto.PRODUCT = Build.PRODUCT;
		
		return dto;
	}

	public static float getDpToPixel(Context ctx, int dp)
	{
		float scale = ctx.getResources().getDisplayMetrics().density;
		return dp * scale;
	}

	public static void setDefaultCategory(Context ctx){
		ArrayList<CallAdDto> ads = InOutCallAdQuery.selectInOutAdList(ctx);
		StringBuffer sb = new StringBuffer();

		for(int i = 0 ; i < ads.size(); i++){
			String KIND = ads.get(i).AD_KIND;
			sb.append(KIND);
			sb.append(":");
		}
		sb.substring(0, sb.length() - 1);

		LogUtil.D("setDefaultCategory = " + sb.toString());

		Setting.setAdCallCategory(ctx, sb.toString());
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

}
