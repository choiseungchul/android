package com.returndays.ralara;

import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.nodes.Document;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.MWebView;
import com.returndays.customview.MWebView.DIRECTION;
import com.returndays.customview.MWebView.OnEdgeTouchListener;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class CallEndActivity  extends BaseActivity implements SensorEventListener  {

	MWebView mWeb;
	String mUrl;
	String mAdSeq, mAmount, mIsGold, mEggGrade , mInx ;
	EditText mEditUri;
	ProgressBar progressBar;
	RecyclingImageView mBtn, web_srch_btn, set_web_type;

	TextView txt_egg;
	TextView txt_gold;
	TextView txt_scratch;

	LinearLayout btn_goto_r, btn_goto_m, btn_goto_s;
	
	HttpDocument mHttpUtil;
	OnClickListener onclickListener;
	boolean isView = false;


	private long lastTime;
	private float speed;
	private float lastX;
	private float lastY;
	private float x, y;

	private static final int SHAKE_THRESHOLD = 2200;
	private static final int DATA_X = SensorManager.AXIS_X;
	private static final int DATA_Y = SensorManager.AXIS_Y;

	private SensorManager sensorManager;
	private Sensor accelerormeterSensor;


	@Override
	public void onStart() {
		super.onStart();
		if (accelerormeterSensor != null)
			sensorManager.registerListener(this, accelerormeterSensor,
					SensorManager.SENSOR_DELAY_GAME);
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().requestFeature(Window.FEATURE_PROGRESS); // 프로그레스
		super.onCreate(savedInstanceState);

		mHttpUtil = new HttpDocument(getApplicationContext());
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


		setContentView(R.layout.callend);
		mUrl = getIntent().getExtras().getString("url");
		mAdSeq = getIntent().getExtras().getString("ad_seq");
		mInx  = getIntent().getExtras().getString("index"); 
		mAmount = "1";//
		mIsGold = "N";//getIntent().getExtras().getString("is_gold");
		mEggGrade = "1";//getIntent().getExtras().getString("egg_grade");

		LogUtil.E("ET", "onCreate mInx : " + mInx  + "  /  " + mAdSeq);

		//Toast.makeText(CallEndActivity.this , "mInx : "+ mInx , Toast.LENGTH_SHORT).show();

		web_srch_btn = (RecyclingImageView)findViewById(R.id.web_srch_btn);


		web_srch_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mUrl = mEditUri.getText().toString();
				if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
				{
					mWeb.loadUrl(mUrl);
				}else{
					mWeb.loadUrl("http://"+mUrl);
				}
			}
		});
		set_web_type = (RecyclingImageView)findViewById(R.id.set_web_type);
		set_web_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent( CallEndActivity.this , AdCallSettingActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(ii);
			}
		});

		btn_goto_r = (LinearLayout)findViewById(R.id.btn_goto_r);
		btn_goto_m = (LinearLayout)findViewById(R.id.btn_goto_m);
		btn_goto_s = (LinearLayout)findViewById(R.id.btn_goto_s);

		txt_egg = (TextView) findViewById(R.id.top_r_cnt);
		txt_gold = (TextView) findViewById(R.id.top_m_cnt);
		txt_scratch = (TextView) findViewById(R.id.top_s_cnt);

		onclickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = null;

				if(v.getId() == R.id.btn_goto_r){
					ii = new Intent(getApplicationContext(), R2ScratchActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);

				}else if(v.getId() == R.id.btn_goto_m){
					ii = new Intent(getApplicationContext(), MyGoldCountActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);

				}else if(v.getId() == R.id.btn_goto_s){
					ii = new Intent(getApplicationContext(), ScratchListActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);

				}
			}
		};

		btn_goto_r.setOnClickListener(onclickListener);
		btn_goto_m.setOnClickListener(onclickListener);
		btn_goto_s.setOnClickListener(onclickListener);

		mEditUri = (EditText) findViewById(R.id.txt_urledit);

		mEditUri.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId == EditorInfo.IME_ACTION_NEXT){ // IME_ACTION_SEARCH , IME_ACTION_GO
					mUrl = mEditUri.getText().toString();
					if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
					{
						mWeb.loadUrl(mUrl);
					}else{
						mWeb.loadUrl("http://"+mUrl);
					}
				}
				if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO
					mUrl = mEditUri.getText().toString();
					if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
					{
						mWeb.loadUrl(mUrl);
					}else{
						mWeb.loadUrl("http://"+mUrl);
					};
				}
				return false;
			}
		});

		progressBar = (ProgressBar) findViewById(R.id.top_simple_pro);

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
				String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
				String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

				txt_egg.setText(Egg);
				txt_gold.setText(Gold);
				txt_scratch.setText(Scratch);
			}
		}, false);


		mWeb  = (MWebView) findViewById(R.id.webviewControl);


		mWeb.setWebViewClient(new MyWebClient());
		//mWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//mWeb.getSettings().setRenderPriority(RenderPriority.HIGH);

		WebSettings set = mWeb.getSettings();
		set.setJavaScriptEnabled(true);
		set.setBuiltInZoomControls(false);
		set.setDatabaseEnabled(true);
		set.setDomStorageEnabled(true);
		set.setAppCacheEnabled(true);
		set.setCacheMode(WebSettings.LOAD_NO_CACHE);
		set.setRenderPriority(WebSettings.RenderPriority.HIGH);
		set.setSupportZoom(false);
		set.setAllowFileAccess(true);
		set.setSavePassword(false);
		set.setSupportMultipleWindows(false);
		set.setAppCachePath("");
		set.setAppCacheMaxSize(5*1024*1024);
		set.setPluginsEnabled(true);


		if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
		{
			mWeb.loadUrl(mUrl);
		}else{
			mWeb.loadUrl("http://"+mUrl);
		}


		if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
		{
			mEditUri.setText(mUrl);
		}else{
			mEditUri.setText("http://"+mUrl);
		}

		mWeb.setWebViewClient(new MyWebClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				try {
					
				} catch (Exception e) {
				}
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				try {
					mEditUri.setText(url);
					
					// 페이지 저장
					HttpDocument sendUrlInfo = new HttpDocument(getApplicationContext());
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("ad_seq", mAdSeq);
					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("loaded_url", url);
					sendUrlInfo.getDocument(UrlDef.CALLEND_URL_COLLECT, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							if(document.select("ResultTable").select("Result").text().equals("true")){
								LogUtil.D("send loadurl success");
							}
						}
					}, false);
					
				} catch (Exception e) {
				}
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				if(url.startsWith("http://")) {
					view.loadUrl(url);
					return true;		
				} else {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID, getPackageName());
					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				}		

			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);

			};

		});

		mWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				progressBar.setProgress(newProgress);
			}
		});

		mWeb.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				//LogUtil.D("test touch X : " + mWeb.getScrollX() + " --- touch Y : " + mWeb.getScrollY() );
				return false;
			}
		});



		mWeb.setOnEdgeTouchListener(new OnEdgeTouchListener() {
			@Override
			public void onEdgeTouch(DIRECTION direction) {
				if(!isView){
					if(mWeb.getBottom() <  mWeb.getHeight() + mWeb.getScrollY() ){
						Animation anim  = null;

						anim = new AlphaAnimation(0, 1);
						anim.setDuration(1000);
						mBtn.startAnimation(anim);
						mBtn.setVisibility(View.VISIBLE);
						isView = true;
					}
				}
				//LogUtil.D("test touch scale : " + mWeb.getScaleY() + " --- touch Y : " + mWeb.getScrollY() + ", Height : " + mWeb.getHeight()  + " getBottom : " +  mWeb.getBottom() );

			}
		});






		/*
		mWeb.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				 case MotionEvent.ACTION_DOWN : 
					 LogUtil.D("downddddddddddddddddddddddddddd");
				 break;
				 case MotionEvent.ACTION_UP :
					 LogUtil.D("uppppppppppppppppppppppp");
				 break;
				default :
					break;	
				}	
				return false;
			}
		});
		 */ 

		mBtn = (RecyclingImageView)findViewById(R.id.btn_callend_egg);
		mBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
				params.put("ad_seq", mAdSeq);
				params.put("is_gold", mIsGold);
				params.put("amount", mAmount);
				params.put("egg_grade", mEggGrade);

				mHttpUtil.getDocument( UrlDef.MY_POINT_ADD, params,null, new HttpCallBack() {

					@Override
					public void onHttpCallBackListener(Document document) {
						
						if(document.select("CODE").text().equals("0")){
							Toast.makeText(CallEndActivity.this, "알 1개가 적립되었습니다!! 알 3개 이상이면 스크래치권으로 변환하실 수 있습니다.", Toast.LENGTH_SHORT).show();
							
						}else if (document.select("CODE").text().equals("1")){
							Toast.makeText(CallEndActivity.this, "하루에 최대 30개의 알을 받으 실 수 있습니다.더이상 알이 적립되지 않습니다.", Toast.LENGTH_SHORT).show();
							
						}else{
							Toast.makeText(CallEndActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
						}
						
						String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
						String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
						String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

						txt_egg.setText(Egg);
						txt_gold.setText(Gold);
						txt_scratch.setText(Scratch);	
					}
				}, false);

				//Toast.makeText(CallEndActivity.this, "알이 적립되었습니다!! 알이 3개 이상이면 스크래치권으로 변환하실 수 있습니다.", Toast.LENGTH_SHORT).show();
				mBtn.setVisibility(View.INVISIBLE);

			}
		});
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long currentTime = System.currentTimeMillis();
			long gabOfTime = (currentTime - lastTime);
			if (gabOfTime > 100) {
				lastTime = currentTime;
				x = event.values[SensorManager.AXIS_X];
				y = event.values[SensorManager.AXIS_Y];

				speed = Math.abs(x + y  - lastX - lastY ) / gabOfTime * 10000;

				if (speed > SHAKE_THRESHOLD) {
					// 이벤트발생!!

					Toast.makeText(getApplicationContext(), "핸드폰을 흔들면 창이 닫힙니다.", Toast.LENGTH_SHORT).show();
					finish();

				}

				lastX = event.values[DATA_X];
				lastY = event.values[DATA_Y];
				;
			}

		}

	}


	@Override
	public void onRestart() {
		super.onRestart();
		setUI();
	}
	@Override
	public void onPause() {
		super.onPause();
		//finish();
		
		try {
	        Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(mWeb, (Object[]) null);

	    } catch(ClassNotFoundException cnfe) {
	        cnfe.printStackTrace();
	    } catch(NoSuchMethodException nsme) {
	        nsme.printStackTrace();
	    } catch(InvocationTargetException ite) {
	        ite.printStackTrace();
	    } catch (IllegalAccessException iae) {
	        iae.printStackTrace();
	    }

	}

	@Override
	public void onStop() {
		super.onStop();
		if (sensorManager != null)
			sensorManager.unregisterListener(this);


	}

	public void setUI(){

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttpUtil.getDocument(UrlDef.MY_POINT, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				String Egg = MadUtil.priceFormat(document.select("EGG").text())+" 개";
				String Gold = MadUtil.priceFormat(document.select("GOLD").text())+" 골드";
				String Scratch = MadUtil.priceFormat(document.select("SCRATCH").text())+" 장";

				txt_egg.setText(Egg);
				txt_gold.setText(Gold);
				txt_scratch.setText(Scratch);
			}
		}, false);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		isView = false;
		mBtn.setVisibility(View.INVISIBLE);
		super.onNewIntent(intent);
		mUrl = Setting.getCallEndAdSettingURL(getApplicationContext());
		mAdSeq = Setting.getCallEndAdSettingADSEQ(getApplicationContext());
		mInx  =  Setting.getCallEndAdSettingIDX(getApplicationContext());

		if(mUrl.startsWith("http://") || mUrl.startsWith("https://"))
		{
			mWeb.loadUrl(mUrl);
		}else{
			mWeb.loadUrl("http://"+mUrl);
		}
		//Toast.makeText(CallEndActivity.this , "RE___mInx new intent : "+ mInx , Toast.LENGTH_SHORT).show();

		//Toast.makeText(CallEndActivity.this, "NewIntent.............................................", Toast.LENGTH_SHORT).show();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			switch(keyCode)
			{
			case KeyEvent.KEYCODE_BACK:
				if(mWeb.canGoBack() == true){
					mWeb.goBack();
				}else{
					LogUtil.D("Back pressed");
					if(!isBackPressed){
						LogUtil.D("backpressed first");
						Toast.makeText(getApplicationContext(), "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
						isBackPressed = true;
						tt = new Timer();
						tt.schedule(new TimerTask() {
							@Override
							public void run() {
								LogUtil.D("backpressed set false");
								isBackPressed = false;
							}
						}, 5000);
					}else{
						LogUtil.D("callendactivity finish");
						if(tt != null){
							tt.cancel();
							tt = null;
						}
						finish();
					}
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}



	class MyWebClient extends WebViewClient {
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}

	}

	boolean isBackPressed = false;

	Timer tt = null;

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil.threadStop();
		
		super.onDestroy();
	}
	
}
