package com.momsfree.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.momsfree.net.R;
import com.momsfree.net.conf.Define;

public class MomsUtil {

	// 숏컷 아이콘 넣기
	public static void addShortCut(Context context){
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClassName(context.getPackageName(), context.getPackageName() + ".SplashActivity");
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		Intent.ShortcutIconResource.fromContext(context, R.drawable.icon_launcher));
		//intent.putExtra("duplicate", false);
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT"); 
		context.sendBroadcast(addIntent);
//		Toast.makeText(context, "바탕화면에 바로가기가 설치되었습니다.", Toast.LENGTH_SHORT).show();
	}
	
	// 데이터 사용여부 체크
	public static boolean CheckInternet(Context ctx) {
		ConnectivityManager connec = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// Check if wifi or mobile network is available or not. If any of them is
		// available or connected then it will return true, otherwise false;
		return wifi.isConnected() || mobile.isConnected();
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager tMgr =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		String phone = "";
		try{
			if(tMgr.getLine1Number() != null){
				phone = tMgr.getLine1Number();
			}
			phone.replaceAll("-", "");
			phone = phone.substring(phone.length()-10, phone.length());
			phone="0" + phone;
		}catch(Exception e){
			e.printStackTrace();
		}
		return phone;
	}

	public static void sendSMS(Context ctx, String phoneNumber, String message) {
		try {
			PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_SENT"), 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0, new Intent("SMS_DELIVERED"), 0);

			SmsManager sms = SmsManager.getDefault(); 
			sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		} catch (Exception e) {
			LogUtil.E("MadUtil sendSMS:"+e.toString());
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
		default:
			break;
		}

		return versionString;
	}


	static public void setSharedPreferencesValue(Context ctx, String nm, int mode, String key, String value) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	static public String getSharedPreferencesValue(Context ctx, String nm, int mode, String key) {
		SharedPreferences pref = ctx.getSharedPreferences(nm, mode);
		return pref.getString(key, "");
	}

	public static void clearAllPrefferences(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Define.PREFER_ID, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
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

	public static boolean TstoreCheck(Activity act) {
		PackageManager pm = act.getPackageManager();
		List< ApplicationInfo > appList = pm.getInstalledApplications( 0 );
		ApplicationInfo app = null;
		int nSize = appList.size();
		for( int i = 0; i < nSize; i++ ) {
			app = appList.get( i );
			if(app.packageName.indexOf("com.skt.skaf.A000Z00040") != -1) {
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
		intent.setClassName("com.skt.skaf.A000Z00040","com.skt.skaf.A000Z00040.A000Z00040");
		intent.setAction("COLLAB_ACTION");	//action
		intent.putExtra("com.skt.skaf.COL.URI",("PRODUCT_VIEW/"+aid+"/0").getBytes());	//user data
		intent.putExtra("com.skt.skaf.COL.REQUESTER","A000Z00040");	//my App ID
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

		return output;
	}

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

		 return output;
	 }

	 public static float getDpToPixel(Context ctx, int dp)
	 {
		 float scale = ctx.getResources().getDisplayMetrics().density;
		 return dp * scale;
	 }
}
