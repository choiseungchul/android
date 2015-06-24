package com.momsfree.util;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;

public class LocationUtil {

	private static final long TWO_MINUTES = 1000 * 60 * 2;

	public static void getLocationInfo(Context context, String address, HttpCallBack callback) {

		HttpDocument http = new HttpDocument(context);
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("address", address);
		params.put("sensor","false");
		http.setMethod("GET");
		
		http.getDocument(UrlDef.GETGEO_LATLNG_URL, params, null, callback, false);
	}
	
	public static double[] getGeoPoint(JSONObject jsonObject) {
		double[] loc = new double[2];
		try {
			loc[1] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lng");

			loc[0] = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lat");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loc;
	}

	
	public static String getLocationStringFomat(String address){
		
		int index = -1;
		
	    String patternStr = "[동면읍리]";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(address);
	    
	    if(matcher.find()){
	    	index = matcher.end();//this will give you index
	    	LogUtil.D("matched Index = " + index);
	    }
		
		if(index != -1){
			LogUtil.D("동 면 읍 리 lastIndex = " + index);
			return address.substring(0, index);
		}else{
			return address;
		}
	}
	
	public static double[] getLocationByAddress(Context ctx, String addr){
		double[] loc = new double[2];
		
		Geocoder coder = new Geocoder(ctx);
		List<Address> address;

		try {
		    address = coder.getFromLocationName(addr ,1);
		    if (address == null) {
		        return null;
		    }
		    Address location = address.get(0);
		    loc[0] = location.getLatitude();
		    loc[1] = location.getLongitude();

		     return loc;
		     
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getGeoLocation(Context ctx, Location myLocation) {
		StringBuffer mAddress = new StringBuffer();
		Geocoder geoCoder = new Geocoder(ctx, Locale.KOREA);
		if(myLocation != null) {
			double latPoint = myLocation.getLatitude();
			double lngPoint = myLocation.getLongitude();
			try {
				// 위도,경도를 이용하여 현재 위치의 주소를 가져온다. 
				List<Address> addresses;
				addresses = geoCoder.getFromLocation(latPoint, lngPoint, 1);
				
				LogUtil.D("get addr by lib = " + myLocation);
				
				for(Address addr: addresses){
					int index = addr.getMaxAddressLineIndex();
					for(int i=0;i<=index;i++){
						mAddress.append(addr.getAddressLine(i));
						mAddress.append(" ");
						
						
//						LogUtil.D("addr line " + i + " = " + addr.getAddressLine(i));
						
					}
					mAddress.append("\n");
				}
				
				return getLocationStringFomat(mAddress.toString());

			} catch (IOException e) {
				//e.printStackTrace();
				LogUtil.D("주소를 가져올 수 없음");
				return null;
			}			
		}
		return null;
	}
	
	public static void getGeoLocation(Context ctx, Location loc, HttpCallBack callback){
		if(loc == null){
			return;
		}
		
		String latlng = loc.getLatitude() + "," + loc.getLongitude();
		
		LogUtil.D("get addr by google = " + loc);
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("latlng", latlng);
		params.put("sensor", "false");
		params.put("language", Locale.KOREA.getLanguage());
		HttpDocument getAddr = new HttpDocument(ctx);
		getAddr.setMethod("GET");
		
		getAddr.getDocument(UrlDef.GETGEOCODE_URL, params,null, callback, false);
	}
	
	// GPS 사용여부 체크
	public static boolean checkGPS(LocationManager locManager) {
		if(locManager == null) return false;
		
		boolean isGPS = locManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
		if(isGPS) {
			return true;
		}
		else {
			return false;
		}
	}

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	public static boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

}
