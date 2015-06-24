package com.momsfree.util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import com.momsfree.net.http.HttpDocument.HttpCallBack;

public class GPSTracker extends Service implements LocationListener 
{

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location Mylocation; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;
	protected LocationListener locListener;
	long minTime = 1000;
	float minDistance = 0;
	String currAddr;
	Criteria crit;

	public GPSTracker(Context context, final Handler handle, final Runnable run) 
	{
		this.mContext = context;
		locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);
		
		locListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				LogUtil.D("provider = " + provider);
				LogUtil.D("extras = " + extras.toString());
			}

			@Override
			public void onProviderEnabled(String provider) {
				LogUtil.D("provide enabled : " +provider  );
				if(LocationUtil.checkGPS(locationManager)){
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locListener);
				}else{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locListener);
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				LogUtil.D("provide disabled : " +provider  );
			}

			@Override
			public void onLocationChanged(Location location) {
				//myLocation = location;
				LogUtil.D("onLocationChanged : " +  location.toString());
				LogUtil.D("myLocation : " + Mylocation);
				
				if(LocationUtil.isBetterLocation(location, Mylocation)){
					Mylocation = location;
					LogUtil.D("11111");
					try {
						currAddr = LocationUtil.getGeoLocation(mContext, Mylocation);
						LogUtil.D("2222");
						if(currAddr == null){
							
							LogUtil.D("33333");
							
							LocationUtil.getGeoLocation(mContext, Mylocation, new HttpCallBack() {
								@Override
								public void onHttpCallBackListener(Document document,
										Header[] header) {
									JSONObject data = null;

									try {
										data = new JSONObject(document.select("body").text());
										JSONArray rs = data.getJSONArray("results");
										JSONObject json = (JSONObject) rs.get(0);
										currAddr = json.getString("formatted_address");
										
										LogUtil.D("currAddr = " + currAddr);
										
										locationManager.removeUpdates(locListener);
										
										handle.post(run);
										
									} catch (JSONException e) {

										e.printStackTrace();
									}
								}
							});
						}else{
							LogUtil.D("currAddr = " + currAddr);
							
							locationManager.removeUpdates(locListener);
							
							handle.post(run);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					
				}

				
			}
		};
		
		crit = new Criteria();
		crit.setPowerRequirement(Criteria.POWER_HIGH);
		crit.setAccuracy(Criteria.ACCURACY_FINE);
		
		String provider = locationManager.getBestProvider(crit, true);
		LogUtil.D("best provider = " + provider);
		
		if(provider == null)
			provider = LocationManager.NETWORK_PROVIDER;

		locationManager.requestLocationUpdates(provider, minTime, minDistance, locListener);
		if(provider.equals(LocationManager.PASSIVE_PROVIDER)){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locListener);
		}

		LogUtil.D("listener = " + locListener);

//		final Location loc = locationManager.getLastKnownLocation(provider);
//		
//		if(loc != null){
//			currAddr = LocationUtil.getGeoLocation(mContext, loc);
//			if(currAddr ==  null){
//				LocationUtil.getGeoLocation(mContext, loc, new HttpCallBack(){
//					@Override
//					public void onHttpCallBackListener(Document document, Header[] header) {
//						JSONObject data = null;
//						LogUtil.D(document.select("body").text());
//						try {
//							data = new JSONObject(document.select("body").text());
//							JSONArray rs = data.getJSONArray("results");
//							JSONObject json = (JSONObject) rs.get(0);
//							currAddr = json.getString("formatted_address");
//							
//							handle.post(run);
//							
//						} catch (JSONException e) {
//
//							e.printStackTrace();
//						}
//					}
//				});
//			}
//		}

	}

	public Location getCurrLocation(){
		return Mylocation;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopTracking()
	{
		if(locationManager != null)
		{
			locationManager.removeUpdates(GPSTracker.this);
		}		
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude()
	{
		if(Mylocation != null)
		{
			latitude = Mylocation.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude()
	{
		if(Mylocation != null)
		{
			longitude = Mylocation.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() 
	{
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is not enabled");

		// Setting Dialog Message
		alertDialog.setMessage("Enable location services to determine your location.");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) 
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) 
	{

	}

	@Override
	public void onProviderDisabled(String provider) 
	{
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
	}

	@Override
	public IBinder onBind(Intent arg0) 
	{
		return null;
	}

}