package com.momsfree.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Browser;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.momsfree.inappbilling.util.IabHelper;
import com.momsfree.inappbilling.util.IabResult;
import com.momsfree.inappbilling.util.Inventory;
import com.momsfree.inappbilling.util.Purchase;
import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.AddrSrchDialog;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.customview.FindAddrDialog;
import com.momsfree.net.customview.TextViewNanumGothic;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.GPSTracker;
import com.momsfree.util.LocationUtil;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;


public class SettingActivity extends BaseActivity{

	static final String TAG = "MomsfreeIAB";

	// IN APP Billing 
	boolean mIsPremium = false;
	boolean mSubscribedToInfiniteGas = false;
	static final String SKU_PRODUCT = "ticket_01";
	static final int RC_REQUEST = 10001;
	IabHelper mHelper;





	TextViewNanumGothic top_title, setting_btn_notice, setting_btn_shop, setting_btn_faq, setting_btn_location, setting_btn_agreement;
	CheckBox setting_btn_pushalarm;
	ImageButton loc_btn;
	private LinearLayout menu1_btn;
	private LinearLayout menu2_btn;
	private LinearLayout menu3_btn;
	private LinearLayout menu4_btn;

	int menu1, menu2, menu3, menu4;
	int id_homebtn, id_toptitle, id_locbtn;

	FindAddrDialog findDialog;
	AddrSrchDialog addrDialog;

	ImageView switch_mom_sitter;
	int mom_sitter_id;

	private Handler handle = new Handler();
	Runnable run;
	long waittime = 10000;
	private ImageButton home_btn;

	GPSTracker track;
	Location myLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1w4W32qpkwyt99TBXQQtbm8t9B7+fpfnS0zSpA05TorrR9owX/aBr8jaK8aPgBjZA1t/7fvLeCCfXHTUV1g9cB7hObMDtnE3FFYo5l7StCzUaHwhFgkoa1a+w1i2ha+N5TNwgdjT33rzDeP01Sfm9o8DWM92FhlDoHqq1OqTtnbDncdyioFsh/AO8r2KGAttP53ekKDYE2n5GlxozPMAI80nZrh25kk0ZwFr3PrvCsj1bANXDVJ/JMubYzDDLhHAYoXNQwXRnezpKHeJdsEwwQ8vP+ITGcG5FX1LLCH8PfgusLmUUrVD2Y5gkyLZuJhikmU2+pADHIB7D9969o9KxwIDAQAB";

		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("구글결제 오류 발생 : " + result);
					return;
				}

				// Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});


		if(Setting.getUserType(getApplicationContext()).equals("M")){
			setContentView(R.layout.setting_blue);
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
			menu1 = R.id.menu1_btn_blue;
			menu2 = R.id.menu2_btn_blue;
			menu3 = R.id.menu3_btn_blue;
			menu4 = R.id.menu4_btn_blue;
			mom_sitter_id = R.id.switch_mom_sitter_blue;
		}else if(Setting.getUserType(getApplicationContext()).equals("B")){
			setContentView(R.layout.setting_pink);
			id_homebtn = R.id.home_btn_pink;
			id_toptitle = R.id.top_title_pink;
			id_locbtn = R.id.loc_btn_pink;
			menu1 = R.id.menu1_btn_pink;
			menu2 = R.id.menu2_btn_pink;
			menu3 = R.id.menu3_btn_pink;
			menu4 = R.id.menu4_btn_pink;
			mom_sitter_id = R.id.switch_mom_sitter_pink;
		}else{
			setContentView(R.layout.setting_blue);
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
			menu1 = R.id.menu1_btn_blue;
			menu2 = R.id.menu2_btn_blue;
			menu3 = R.id.menu3_btn_blue;
			menu4 = R.id.menu4_btn_blue;
			mom_sitter_id = R.id.switch_mom_sitter_blue;
		}

		initUI();
		initData();

	}

	private void initData() {

	}

	void complain(String message) {
		Log.e(TAG, "**** IAB Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	private void initUI() {

		setting_btn_agreement = (TextViewNanumGothic)findViewById(R.id.setting_btn_agreement);
		setting_btn_agreement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), WebActivity.class);
				ii.putExtra("url" , UrlDef.AGREEMENT);
				ii.putExtra("title", "더보기");
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0,0);
			}
		});

		switch_mom_sitter = (ImageView)findViewById(mom_sitter_id);

		home_btn = (ImageButton)findViewById(id_homebtn);
		home_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), UserSelectActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0,0);
			}
		});


		top_title = (TextViewNanumGothic)findViewById(id_toptitle);
		top_title.setText("더보기");

		loc_btn = (ImageButton)findViewById(id_locbtn);
		loc_btn.setVisibility(View.INVISIBLE);

		setting_btn_notice = (TextViewNanumGothic)findViewById(R.id.setting_btn_notice);
		setting_btn_notice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "http://momsfree.co.kr/app/a_v/board.php?act=notice";
				Intent ii = new Intent(getApplicationContext(), WebActivity.class);
				ii.putExtra("url" , url);
				ii.putExtra("title", "더보기");
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0,0);
			}
		});
		setting_btn_shop = (TextViewNanumGothic)findViewById(R.id.setting_btn_shop);
		setting_btn_shop.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final HttpDocument httpdd = new HttpDocument(SettingActivity.this);
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_id", Setting.getEmail(getApplicationContext()));
				//params.put("push_google_id", regId);
				httpdd.getDocument(UrlDef.IS_PURCHASE_PROD, params, null, new HttpCallBack() {

					@Override
					public void onHttpCallBackListener(Document document,Header[] header) {
						String data = document.select("body").text();
						
						try {
							
							Log.d(TAG, " data  : "+ data);
							
							JSONObject json = new JSONObject(data);
							String result = json.getString("result");
							String user_id_result = json.getString("user_id");
							
							Log.d(TAG, " result  : "+ result + ", user_id_result :  " + user_id_result + ". shared : "+ Setting.getEmail(getApplicationContext()));
							
							if(result.equals("0") && user_id_result.equals(Setting.getEmail(getApplicationContext()))  ){
								   String payload = user_id_result; 
							        mHelper.launchPurchaseFlow(SettingActivity.this, SKU_PRODUCT , RC_REQUEST, mPurchaseFinishedListener, payload);
								
							}else{
								alert("이미 구매하신 내역이 있거나 구매하실 수 없습니다.");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
						httpdd.threadStop();
						
					}
				}, false);
				
		     
			}
		});
		setting_btn_faq = (TextViewNanumGothic)findViewById(R.id.setting_btn_faq);
		setting_btn_faq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = "http://momsfree.co.kr/app/a_v/board.php?act=faq";
				Intent ii = new Intent(getApplicationContext(), WebActivity.class);
				ii.putExtra("url" , url);
				ii.putExtra("title", "더보기");
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(ii);
				finish();
				overridePendingTransition(0,0);
			}
		});

		setting_btn_location = (TextViewNanumGothic)findViewById(R.id.setting_btn_location);
		setting_btn_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(MomsUtil.CheckInternet(getApplicationContext())){

					track = new GPSTracker(getApplicationContext(), new Handler(), new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub

	
//							
							final Location loc = track.getCurrLocation();
//
							showDialog("", loc.getLatitude(), loc.getLongitude());
							
//							String addr = LocationUtil.getGeoLocation(getApplicationContext(), loc);
//							
//							if(addr ==null){
//								LocationUtil.getGeoLocation(getApplicationContext(), loc, new HttpCallBack(){
//									@Override
//									public void onHttpCallBackListener(
//											Document document, Header[] header) {
//										JSONObject data = null;
//
//										try {
//											data = new JSONObject(document.select("body").text());
//											JSONArray rs = data.getJSONArray("results");
//											JSONObject json = (JSONObject) rs.get(0);
//											String user_addr = json.getString("formatted_address");
//											showDialog(LocationUtil.getLocationStringFomat(user_addr), loc.getLatitude(), loc.getLongitude());
//										} catch (JSONException e) {
//
//											e.printStackTrace();
//										}
//									}
//								});

//							}else{
//								showDialog(LocationUtil.getLocationStringFomat(addr), loc.getLatitude(), loc.getLongitude());
//							}

						}
					});

				}else{
					final DefaultDialog dialog = new DefaultDialog(SettingActivity.this);
					dialog.setTitle("알림");
					dialog.setMessage("네트워크 연결이 필요합니다.");
					dialog.isNegativeButton(false);
					dialog.show();
					dialog.setPositiveButton("확인", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
		});
		setting_btn_pushalarm = (CheckBox)findViewById(R.id.setting_btn_pushalarm);

		setting_btn_pushalarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if(MomsUtil.CheckInternet(getApplicationContext())){
					track = new GPSTracker(getApplicationContext(), new Handler(), new Runnable() {
						@Override
						public void run() {
							try{
								myLocation = track.getCurrLocation();
							}catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}

							String addr = LocationUtil.getGeoLocation(getApplicationContext(), myLocation);
							if(addr ==null){
								LocationUtil.getGeoLocation(getApplicationContext(), myLocation, new HttpCallBack(){
									@Override
									public void onHttpCallBackListener(
											Document document, Header[] header) {
										JSONObject data = null;

										try {
											data = new JSONObject(document.select("body").text());
											JSONArray rs = data.getJSONArray("results");
											JSONObject json = (JSONObject) rs.get(0);
											String user_addr = json.getString("formatted_address");
											showDialog(LocationUtil.getLocationStringFomat(user_addr), myLocation.getLatitude(), myLocation.getLongitude());

										} catch (JSONException e) {

											e.printStackTrace();
										}
									}
								});
							}else{

								showDialog(LocationUtil.getLocationStringFomat(addr), myLocation.getLatitude(), myLocation.getLongitude());
							}

						} // runnable
					});

				}else{
					final DefaultDialog dialog = new DefaultDialog(SettingActivity.this);
					dialog.setTitle("알림");
					dialog.setMessage("네트워크 연결이 필요합니다.");
					dialog.isNegativeButton(false);
					dialog.show();
					dialog.setPositiveButton("확인", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				}
			}
		});

		menu1_btn = (LinearLayout)findViewById(menu1);
		menu2_btn = (LinearLayout)findViewById(menu2);
		menu3_btn = (LinearLayout)findViewById(menu3);
		menu4_btn = (LinearLayout)findViewById(menu4);

		View.OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();

				String mUrl = "";
				String title = "";

				if(id == menu1){

					if(Setting.getListType(getApplicationContext()).equals("M")){

						mUrl = UrlDef.FIND_SITTER;
						title = "베이비 시터 정보";

						if(!Setting.getLatString(getApplicationContext()).equals("") && !Setting.getLngString(getApplicationContext()).equals("")){
							mUrl = mUrl + "&lat=" + Setting.getLatString(getApplicationContext()) + "&lng=" + Setting.getLngString(getApplicationContext());
						}
						Setting.setListType(getApplicationContext(), "B");
						switch_mom_sitter.setImageResource(R.drawable.menu5_icon);

					}else if(Setting.getListType(getApplicationContext()).equals("B")){

						mUrl = UrlDef.FIND_JOB;
						title = "일자리 정보";

						if(!Setting.getLatString(getApplicationContext()).equals("") && !Setting.getLngString(getApplicationContext()).equals("")){
							mUrl = mUrl + "&lat=" + Setting.getLatString(getApplicationContext()) + "&lng=" + Setting.getLngString(getApplicationContext());
						}
						Setting.setListType(getApplicationContext(), "M");
						switch_mom_sitter.setImageResource(R.drawable.menu1_icon);
					}

					Intent ii = new Intent(getApplicationContext(), WebActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ii.putExtra("url", mUrl);
					ii.putExtra("title", title);

					startActivity(ii);

				}else if(id == menu2){

					mUrl = UrlDef.FAVORITE_URL;
					title = "찜하기";

					Intent ii = new Intent(getApplicationContext(), WebActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ii.putExtra("url", mUrl);
					ii.putExtra("title", title);

					startActivity(ii);

				}else if(id == menu3){

					mUrl = UrlDef.MYPAGE_URL;
					title = "마이페이지";

					Intent ii = new Intent(getApplicationContext(), WebActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					ii.putExtra("url", mUrl);
					ii.putExtra("title", title);
					startActivity(ii);

				}else if(id == menu4){
				}
			}
		};

		menu1_btn.setOnClickListener(listener);
		menu2_btn.setOnClickListener(listener);
		menu3_btn.setOnClickListener(listener);
		menu4_btn.setOnClickListener(listener);
	}

	
	 IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
	        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
	            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
	            if (result.isFailure()) {
	                complain("구매가 이루어지지 않았습니다.");
	                //setWaitScreen(false);
	                return;
	            }
	            if (!verifyDeveloperPayload(purchase)) {
	                complain("구매가 이루어지지 않았습니다. (-99)");
	              //  setWaitScreen(false);
	                return;
	            }

	            Log.d(TAG, "Purchase successful.");

	            if (purchase.getSku().equals(SKU_PRODUCT)) {
	                // bought 1/4 tank of gas. So consume it.
	                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
	                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
	            }
	           
	        }
	    };
	

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */
			/*
	            // Do we have the premium upgrade?
	            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
	            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
	            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

	            // Do we have the infinite gas plan?
	            Purchase infiniteGasPurchase = inventory.getPurchase(SKU_INFINITE_GAS);
	            mSubscribedToInfiniteGas = (infiniteGasPurchase != null && 
	                    verifyDeveloperPayload(infiniteGasPurchase));
	            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE") 
	                        + " infinite gas subscription.");
	            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
			 */
			// Check for gas delivery -- if we own gas, we should fill up the tank immediately
			Purchase productPurchase = inventory.getPurchase(SKU_PRODUCT);
			if (productPurchase != null && verifyDeveloperPayload(productPurchase)) {
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_PRODUCT), mConsumeFinishedListener);
				return;
			}

			// updateUi();
			// setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// We know this is the "gas" sku because it's the only one we consume,
			// so we don't check which sku was consumed. If you have more than one
			// sku, you probably should check...
			if (result.isSuccess()) {
				
				
				final HttpDocument httpd = new HttpDocument(SettingActivity.this);
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_id", Setting.getEmail(getApplicationContext()));
				params.put("vpresult", "00");
				//params.put("push_google_id", regId);
				httpd.getDocument(UrlDef.PURCHASE_PROD, params, null, new HttpCallBack() {

					@Override
					public void onHttpCallBackListener(Document document,Header[] header) {
						String data = document.select("body").text();
						
						try {
							JSONObject json = new JSONObject(data);
							String result = json.getString("result");
							if(result.equals("0000")){
								alert("구매완료 하였습니다.");
							}else{
								alert("구매가 정상적으로 이루어지지 않았습니다.");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						
						httpd.threadStop();
						
					}
				}, false);
				
				
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				// Log.d(TAG, "Consumption successful. Provisioning.");
				// mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
				// saveData();
				// alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
			}
			else {
				complain("아이템 구매가 실패하였습니다. 관리자에게 문의바랍니다.");
			}
			//updateUi();
			//setWaitScreen(false);
			Log.d(TAG, "End consumption flow.");
		}
	};

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase and 
		 * verifying it here might seem like a good approach, but this will fail in the 
		 * case where the user purchases an item on one device and then uses your app on 
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on 
		 *    one device work on other devices owned by the user).
		 * 
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
		else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	// 최종 주소지가 나왔을때..
	private void showDialog(final String LocationString, final double lat, final double lng){
		findDialog = new FindAddrDialog(SettingActivity.this);
		findDialog.setTitle(LocationString);
		findDialog.setSrchbtn(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findDialog.dismiss();
				addrDialog = new AddrSrchDialog(SettingActivity.this);
				addrDialog.setTitle(LocationString);
				addrDialog.setItemOnclick(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						String address = (String)arg0.getItemAtPosition(arg2);

						double[] loc = LocationUtil.getLocationByAddress(getApplicationContext(), address);

						if(loc != null){
							Setting.setLatString(getApplicationContext(), String.valueOf(loc[0]));
							Setting.setLngString(getApplicationContext(), String.valueOf(loc[1]));

							Toast.makeText(getApplicationContext(), "위치가 저장되었습니다.", Toast.LENGTH_LONG).show();

							addrDialog.dismiss();
						}else{
							LocationUtil.getLocationInfo(getApplicationContext(), address, new HttpCallBack() {
								@Override
								public void onHttpCallBackListener(Document document, Header[] header) {
									String data = document.select("body").text();

									LogUtil.D("getLocationInfo = " + data);

									JSONObject json;
									try {
										json = new JSONObject(data);

										double[] locationPoint = LocationUtil.getGeoPoint(json);

										Setting.setLatString(getApplicationContext(), String.valueOf(locationPoint[0]));
										Setting.setLngString(getApplicationContext(), String.valueOf(locationPoint[1]));

										Toast.makeText(getApplicationContext(), "위치가 저장되었습니다.", Toast.LENGTH_LONG).show();

										addrDialog.dismiss();
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										addrDialog.dismiss();
									}
								}
							});
						}

					}
				});
				addrDialog.setBackBtn(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addrDialog.dismiss();
						findDialog.show();

					}
				});
				addrDialog.setCloseBtn(new OnClickListener() {
					@Override
					public void onClick(View v) {
						addrDialog.dismiss();
					}
				});
				addrDialog.show();
			}
		});

		findDialog.setLocationBtnListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setLatString(getApplicationContext(), String.valueOf(lat));
				Setting.setLngString(getApplicationContext(), String.valueOf(lng));
				findDialog.dismiss();
			}
		});
		findDialog.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	 @Override
	    public void onDestroy() {
	        super.onDestroy();
	        
	        // very important:
	        Log.d(TAG, "Destroying helper.");
	        if (mHelper != null) mHelper.dispose();
	        mHelper = null;
	    }

}
