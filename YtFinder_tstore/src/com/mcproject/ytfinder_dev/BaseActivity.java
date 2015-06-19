package com.mcproject.ytfinder_dev;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.customview.CustomEditText;
import com.mcproject.net.customview.RecyclingImageView;
import com.mcproject.net.dialog.DatePickerDialog;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.Params;
import com.mcproject.ytfavorite_t.R;
import com.skplanet.tad.AdInterstitial;
import com.skplanet.tad.AdInterstitialListener;
import com.skplanet.tad.AdView.Slot;

public class BaseActivity extends Activity{

	private static final boolean IS_AD_CACHE = false;
	private boolean IS_PRESS_BACK = false;
	
	private LinearLayout srch_detail_layer;
	private RecyclingImageView srch_detail_btn;
	private CustomEditText srch_query;

	private boolean isDetailSearch;
	private LinearLayout sort_btn_layer;
	private TextView srch_select_nation;
	private TextView srch_select_start_date, srch_select_end_date;
	private TextView srch_select_duration;
	private EditText srch_select_query;
	private RadioButton order_type_1, order_type_2, order_type_3, order_type_4, order_type_5, order_type_6;
	private TextView srch_detail_start;
	AlertDialog.Builder nationListDialog, categoryDialog, durationDialog;
	Bundle detail_params = new Bundle();
	int duration_type = 0;
	OnClickListener radioListener;
	boolean isCategoryLoad = false;

	// 날짜 구분 다이어로그
	DatePickerDialog before_dialog;
	String before_year = "";
	String before_month = "";
	String before_day = "";

	DatePickerDialog after_dialog;
	String after_year = "";
	String after_month = "";
	String after_day = "";
	private TextView srch_select_cate;
	private LinearLayout menu_btn_1, menu_btn_2, menu_btn_3, menu_btn_4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void BaseUIInit(Activity mActivity){
		RequestQueue reqQ = Volley.newRequestQueue(getApplicationContext());
		
		// 버전 체크
		JsonObjectRequest version = new JsonObjectRequest(UrlDef.GET_VERSION, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					// 최소 요구 버전
					String RequireVersion = response.getString("RequireVersion");
					// 현재 최신 버전
					String LatestVersion = response.getString("LatestVersion");
					// 버전 이름
					String AppVer = response.getString("AppVer");
					
					String version = "";
					int vCode = 0;
					try {
						PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
						version = i.versionName;
						vCode = i.versionCode;
						final String packageName = i.packageName;
						
						int requireVersionCode = Integer.parseInt(RequireVersion);
						
						if(vCode < requireVersionCode){
							// 업데이트 필요
							AlertDialog.Builder alert = new Builder(BaseActivity.this);
							alert.setCancelable(false);
							alert.setTitle(R.string.update_need_title);
							alert.setMessage(R.string.update_need_msg);
							alert.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// 업데이트화면으로 이동
									Intent intent = new Intent(Intent.ACTION_VIEW);
									if(Define.MARKET_FLAG == 0){										
										intent.setData(Uri.parse(Define.TSTORE_URL));									
									}else if(Define.MARKET_FLAG == 1){
										intent.setData(Uri.parse(Define.UPLUS_URL));										
									}else if(Define.MARKET_FLAG == 2){										
										intent.setData(Uri.parse(Define.OLLEH_URL));										
									}									
									startActivity(intent);
									finish();
								}
							});
							alert.show();
						}else{
							
						}
					
					} catch(NameNotFoundException e) { 
					}
					
				} catch (JSONException e) {
					LogUtil.E(e.toString());
				}
				
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.E(error.toString());
			}
		});
		
		reqQ.add(version);
		
		initAd();
		
		// 왼쪽 슬라이드 메뉴
		//		SlidingMenu menu = new SlidingMenu(mActivity);
		//        menu.setMode(SlidingMenu.LEFT);
		//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		//        menu.setShadowWidthRes(R.dimen.shadow_width);
		//        menu.setShadowDrawable(R.drawable.shadow);
		//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		//        menu.setFadeDegree(0.35f);
		//        menu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);
		//        menu.setMenu(R.layout.left_menu);

		// 검색 조건들 뷰
		// 국가 선택
		srch_select_nation = (TextView)mActivity.findViewById(R.id.srch_select_nation);
		srch_select_nation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(nationListDialog == null){
					nationListDialog = new Builder(BaseActivity.this);
					nationListDialog.setTitle(getString(R.string.srch_dialog_nation_title));
					
					final String[] srch_nation_names = getResources().getStringArray(R.array.srch_nation_names);
					
					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, srch_nation_names);
					
					nationListDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String[] srch_nation_codes = getResources().getStringArray(R.array.srch_nation_codes);
							// 전체 국가일 경우 뺀다
							if(!srch_nation_codes[which].equals("ALL"))
							detail_params.putString("regionCode", srch_nation_codes[which] +"");
							else{
								detail_params.remove("regionCode");
							}
							
							LogUtil.D("selected Nation = " + detail_params.get("regionCode"));
							srch_select_nation.setText(srch_nation_names[which]);
						}
					});
					nationListDialog.show();
				}else{
					nationListDialog.show();
				}
			}
		});

		srch_select_cate = (TextView)findViewById(R.id.srch_select_cate);
		srch_select_cate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//categoryDialog
				if(categoryDialog == null){
					categoryDialog = new Builder(BaseActivity.this);
					categoryDialog.setTitle(getString(R.string.srch_select_cate_dialog_title));

					// cate[0] => 아이디값 배열
					// cate[1] => 이름 배열
					final String[][] cate = DbQueryUtil.getVideoCategoryList(getApplicationContext());

					McUtil.traceArray(cate[0]);
					McUtil.traceArray(cate[1]);

					if(cate[0].length > 1){
						isCategoryLoad = true;
						ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, cate[1]);

						categoryDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if(which != 0){
									detail_params.putString("videoCategoryId", cate[0][which]);
								}else{
									detail_params.remove("videoCategoryId");
								}
								srch_select_cate.setText(cate[1][which]);
							}
						});
						categoryDialog.show();

					}else{
						// 카테고리 불러오기
						RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
						Listener<JSONObject> listener = new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {

								try {
									JSONArray items = response.getJSONArray("items");
									final String[] ids = new String[items.length()+1];
									final String[] titles = new String[items.length()+1];
									ids[0] = "-1";
									titles[0] = getString(R.string.srch_select_cate_btn);
									for(int i = 0 ; i < items.length(); i++){
										JSONObject item = items.getJSONObject(i);
										ids[i+1] = item.getString("id");
										JSONObject snippet = item.getJSONObject("snippet");
										titles[i+1] = snippet.getString("title");
									}

									// DB에 저장한다.
									DbQueryUtil.initVideoCategory(getApplicationContext(), ids, titles);

									ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, titles);

									categoryDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											if(which != 0){
												detail_params.putString("videoCategoryId", ids[which]);
											}else{
												detail_params.remove("videoCategoryId");
											}
											srch_select_cate.setText(titles[which]);
										}
									});
									isCategoryLoad = true;
									categoryDialog.show();
									srch_select_cate.setText(getString(R.string.srch_select_cate_dialog_title));
								} catch (JSONException e) {
									LogUtil.D(e.toString());
								}
							}
						};

						Params get_cate_param = new Params(getApplicationContext());
						get_cate_param.put("key", Define.YT_API_KEY);
						get_cate_param.put("part", "snippet");
						get_cate_param.put("hl", AppUserSettings.getHLCode(getApplicationContext()));
						get_cate_param.put("regionCode", AppUserSettings.getNationCode(getApplicationContext()));
						get_cate_param.put("fields", "items");
						String param = McUtil.getParams(get_cate_param);

						get_cate_param.trace();

						rq.add(new JsonObjectRequest(UrlDef.GET_CATEGORY + param, null, listener, new ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								LogUtil.D(error.toString());
							}
						}));

						srch_select_cate.setText(getString(R.string.srch_select_cate_loading));
					}

				}else{
					if(isCategoryLoad)
						categoryDialog.show();
				}
			}
		});

		// 초기 날짜 설정
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		int curDay = calendar.get(Calendar.DAY_OF_MONTH);

		srch_select_start_date = (TextView)mActivity.findViewById(R.id.srch_select_start_date);
		srch_select_start_date.setText(curYear-20 + ".01.01");
		String start_date_p = curYear-20 + "-01-01T00:00:00Z";
		detail_params.putString("publishedAfter", start_date_p);
		srch_select_start_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(before_dialog == null){
					before_dialog = new DatePickerDialog(BaseActivity.this, false);
					before_dialog.setUpdateDate(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							before_year =  before_dialog.getYear();
							before_month = before_dialog.getMonth();
							before_day = before_dialog.getDay();
							if(before_day.length() == 1){
								before_day = "0" + before_day;
							}

							srch_select_start_date.setText(before_year + "." + before_month + "." + before_day); 

						}
					});
					before_dialog.setDateInputListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String param = before_year + "-" + before_month + "-" + before_day + "T00:00:00Z";
							detail_params.putString("publishedAfter", param);
							before_dialog.dismiss();
						}
					});
					before_dialog.show();
				}else{
					before_dialog.show();
				}
			}
		});
		srch_select_end_date = (TextView)mActivity.findViewById(R.id.srch_select_end_date);
		String curM = "";
		String curD = "";
		if(curMonth < 10){
			curM = "0"+curMonth;
		}else{
			curM = String.valueOf(curMonth);
		}
		if(curDay < 10){
			curD = "0"+curDay;
		}else{
			curD = String.valueOf(curDay);
		}
		srch_select_end_date.setText(curYear + "." + curM + "." + curD);
		String after_date_p = curYear + "-" + curM + "-" + curD + "T23:59:59Z";
		detail_params.putString("publishedBefore", after_date_p);
		srch_select_end_date.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(after_dialog == null){
					after_dialog = new DatePickerDialog(BaseActivity.this, true);
					after_dialog.setUpdateDate(new Runnable() {
						@Override
						public void run() {
							after_year =  after_dialog.getYear();
							after_month = after_dialog.getMonth();
							after_day = after_dialog.getDay();
							if(after_day.length() == 1){
								after_day = "0" + after_day;
							}
							srch_select_end_date.setText(after_year + "." + after_month + "." + after_day); 
						}
					});
					after_dialog.setDateInputListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String param = after_year + "-" + after_month + "-" + after_day + "T23:59:59Z";
							detail_params.putString("publishedBefore", param);
							after_dialog.dismiss();
						}
					});
					after_dialog.show();
				}else{
					after_dialog.show();
				}

			}
		});
		
		srch_select_duration = (TextView)mActivity.findViewById(R.id.srch_select_duration);
		srch_select_duration.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(durationDialog == null){
					durationDialog = new Builder(BaseActivity.this);
					durationDialog.setTitle(getString(R.string.srch_select_duration_title));

					final String[] srch_select_duration_item = getResources().getStringArray(R.array.srch_select_duration_item);

					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, srch_select_duration_item);

					durationDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 시간구분은 따로 저장
							duration_type = which;
							srch_select_duration.setText(srch_select_duration_item[which]);
						}
					});
					durationDialog.show();
				}else{
					durationDialog.show();
				}
			}
		});
		srch_select_query = (EditText)mActivity.findViewById(R.id.srch_select_query);

		order_type_1 = (RadioButton)mActivity.findViewById(R.id.order_type_1);
		order_type_2 = (RadioButton)mActivity.findViewById(R.id.order_type_2);
		order_type_3 = (RadioButton)mActivity.findViewById(R.id.order_type_3);
		order_type_4 = (RadioButton)mActivity.findViewById(R.id.order_type_4);
		order_type_5 = (RadioButton)mActivity.findViewById(R.id.order_type_5);
		order_type_6 = (RadioButton)mActivity.findViewById(R.id.order_type_6);

		radioListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				removeCheckRadioButton();

				if(v.getId() == R.id.order_type_1){
					order_type_1.setChecked(true);
					detail_params.putString("order", "date");
				}
				else if(v.getId() == R.id.order_type_2){
					order_type_2.setChecked(true);
					detail_params.putString("order", "rating");
				}
				else if(v.getId() == R.id.order_type_3){
					order_type_3.setChecked(true);
					detail_params.putString("order", "title");
				}
				else if(v.getId() == R.id.order_type_4){
					order_type_4.setChecked(true);
					detail_params.putString("order", "videoCount");
				}
				else if(v.getId() == R.id.order_type_5){
					order_type_5.setChecked(true);
					detail_params.putString("order", "viewCount");
				}
				else if(v.getId() == R.id.order_type_6){
					order_type_6.setChecked(true);
					detail_params.putString("order", "relevance");
				}
			}
		};
		order_type_1.setOnClickListener(radioListener);
		order_type_2.setOnClickListener(radioListener);
		order_type_3.setOnClickListener(radioListener);
		order_type_4.setOnClickListener(radioListener);
		order_type_5.setOnClickListener(radioListener);
		order_type_6.setOnClickListener(radioListener);

		srch_detail_start = (TextView)mActivity.findViewById(R.id.srch_detail_start);

		// 상세검색 레이어
		sort_btn_layer = (LinearLayout)mActivity.findViewById(R.id.sort_btn_layer);
		srch_detail_layer = (LinearLayout) mActivity.findViewById(R.id.srch_detail_layer);
		srch_detail_btn = (RecyclingImageView)mActivity.findViewById(R.id.srch_detail_btn);
		srch_detail_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setLayer();
			}
		});

		srch_query = (CustomEditText)mActivity.findViewById(R.id.srch_query);

		// 하단 메뉴 초기화작업
		menu_btn_1 = (LinearLayout)mActivity.findViewById(R.id.menu_btn_1);
		menu_btn_2 = (LinearLayout)mActivity.findViewById(R.id.menu_btn_2);
		menu_btn_3 = (LinearLayout)mActivity.findViewById(R.id.menu_btn_3);
		menu_btn_4 = (LinearLayout)mActivity.findViewById(R.id.menu_btn_4);

		OnClickListener menu_click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.menu_btn_1){
					Intent ii = new Intent(getApplicationContext(), MainActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);
				}else if(v.getId() == R.id.menu_btn_2){
					Intent ii = new Intent(getApplicationContext(), PlayListActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);
				}else if(v.getId() == R.id.menu_btn_3){
					FavoriteActivity_v9.saved_curr_tab = 0;
					Intent ii = new Intent(getApplicationContext(), FavoriteActivity_v9.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);
					//					if(Build.VERSION.SDK_INT  > 11){
					//						Intent ii = new Intent(getApplicationContext(), FavoriteActivity.class);
					//						ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					//						startActivity(ii);
					//					}else{
					//						Intent ii = new Intent(getApplicationContext(), FavoriteActivity_v9.class);
					//						ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					//						startActivity(ii);
					//					}
				}else if(v.getId() == R.id.menu_btn_4){
					Intent ii = new Intent(getApplicationContext(), OthersActivity.class);
					ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(ii);
				}
				finish();
				overridePendingTransition(0, 0);
			}
		};

		menu_btn_1.setOnClickListener(menu_click);
		menu_btn_2.setOnClickListener(menu_click);
		menu_btn_3.setOnClickListener(menu_click);
		menu_btn_4.setOnClickListener(menu_click);
	}

	public LinearLayout getMenuBtn(int num){
		if(num == 1){
			return menu_btn_1;
		}else if(num == 2){
			return menu_btn_2;
		}else if(num == 3){
			return menu_btn_3;
		}else if(num ==4){
			return menu_btn_4;
		}else{
			return null;
		}
	}

	public void setLayer(){
		if(isDetailSearch){
			//			srch_detail_btn.setImageResource(R.drawable.srch_detail_off);
			//			ScaleAnimation sc = new  ScaleAnimation(1f, 0f, 1f, 0f); //, Animation.ABSOLUTE, 1f, Animation.ABSOLUTE, 1f
			//			sc.setDuration(300);
			//			sc.setFillAfter(true);
			//			sc.setInterpolator(new AccelerateInterpolator());
			//			srch_detail_layer.startAnimation(sc);

			FrameLayout.LayoutParams params = (LayoutParams) srch_detail_layer.getLayoutParams();
			srch_detail_btn.setImageResource(R.drawable.srch_detail_off);
			params.width = 0;
			params.height = 0;
			srch_detail_layer.setLayoutParams(params);
			if(sort_btn_layer != null){
				sort_btn_layer.setVisibility(View.VISIBLE);
			}
				
			isDetailSearch = false;
		}else{
			//			srch_detail_btn.setImageResource(R.drawable.srch_detail_on);
			//			
			//			ScaleAnimation sc = new  ScaleAnimation(0f, 1f, 0f, 1f); //, Animation.ABSOLUTE, 1f, Animation.ABSOLUTE, 1f
			//			sc.setDuration(300);
			//			sc.setFillAfter(true);
			//			sc.setInterpolator(new AccelerateInterpolator());
			//			srch_detail_layer.startAnimation(sc);

			FrameLayout.LayoutParams params = (LayoutParams) srch_detail_layer.getLayoutParams();
			srch_detail_btn.setImageResource(R.drawable.srch_detail_on);
			params.width = LayoutParams.MATCH_PARENT;
			params.height = LayoutParams.MATCH_PARENT;
			srch_detail_layer.setLayoutParams(params);
			if(sort_btn_layer != null)
				sort_btn_layer.setVisibility(View.GONE);
			isDetailSearch = true;
		}
	}

	// 최소한의 검색 조건이 있는지 확인
	private boolean inputValidate(){
		//		// 업로드 기간 비교
		//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", getResources().getConfiguration().locale);
		//		 try {
		//			Date b_date =  sdf.parse(before_year + "-" + before_month + "-" + before_day);
		//			Date a_date =  sdf.parse(after_year + "-" + after_month + "-" + after_day);
		//			if(!b_date.after(a_date)){
		//				Toast.makeText(getApplicationContext(), R.string.srch_select_alert_date, Toast.LENGTH_SHORT).show();
		//				return false;
		//			}
		//		} catch (ParseException e) {
		//			LogUtil.D(e.toString());
		//		}

		return true;
	}

	public LinearLayout getSrchDetailLayer(){
		return srch_detail_layer;
	}

	public CustomEditText getSrchQuery(){
		return srch_query;
	}

	public TextView getSrchStartBtn(){
		return srch_detail_start;
	}

	public void setDetailParams(Bundle b){
		detail_params = b;

		String category_id = b.getString("videoCategoryId");
		if(category_id != null){
			String cate_name = DbQueryUtil.getVideoCategoryName(getApplicationContext(), b.getString("videoCategoryId"));
			srch_select_cate.setText(cate_name);
		}

		String publish_before = b.getString("publishedBefore");
		publish_before = publish_before.split("T")[0];
		publish_before = publish_before.replace("-", ".");
		String publish_after = b.getString("publishedAfter");
		publish_after = publish_after.split("T")[0];
		publish_after = publish_after.replace("-", ".");
		srch_select_start_date.setText(publish_after);
		srch_select_end_date.setText(publish_before);

		String q = b.getString("q");
		if(q != null)
			srch_select_query.setText(q);

		String duration = b.getString("duration_type");
		int duration_type = Integer.parseInt(duration);
		String[] srch_select_duration_item = getResources().getStringArray(R.array.srch_select_duration_item); 
		srch_select_duration.setText(srch_select_duration_item[duration_type]);

		String order = b.getString("order");
		if(order != null){
			removeCheckRadioButton();
			if(order.equals("date")){
				order_type_1.setChecked(true);
			}else if(order.equals("rating")){
				order_type_2.setChecked(true);
			}else if(order.equals("title")){
				order_type_3.setChecked(true);
			}else if(order.equals("videoCount")){
				order_type_4.setChecked(true);
			}else if(order.equals("viewCount")){
				order_type_5.setChecked(true);
			}else if(order.equals("relevance")){
				order_type_6.setChecked(true);
			}
		}


	}

	// activity로 파라미터 전달
	public Bundle getDetailParams(){
		if(inputValidate()){
			if(detail_params.containsKey("videoCategoryId")){
				detail_params.putString("type", "video");
			}
			if(!srch_select_query.getText().toString().equals("")){
				detail_params.putString("type", "video");
				detail_params.putString("q", srch_select_query.getText().toString());
			}

			detail_params.putString("duration_type", String.valueOf(duration_type));

			McUtil.traceParams(detail_params);

			return detail_params;
		}
		return null;
	}

	private void removeCheckRadioButton(){
		order_type_1.setChecked(false);
		order_type_2.setChecked(false);
		order_type_3.setChecked(false);
		order_type_4.setChecked(false);
		order_type_5.setChecked(false);
		order_type_6.setChecked(false);
	}

	protected void showSoftKeyboard() {
		InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);
	}

	protected void hideSoftKeyboard(View view) {
		InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	// 플레이어 액티비티로 이동 ( 한개 동영상 )
	public void gotoSinglePlayer(String videoid, YTListDto dto, String list_type, int cursor){
		Intent ii = null;
		if(Build.VERSION.SDK_INT >= 11){
			ii = new Intent(getApplicationContext(), PlayerActivity_v11.class);
		}else{
			ii = new Intent(getApplicationContext(), PlayerActivity.class);
		}
		
//		ii = new Intent(getApplicationContext(), PlayerActivity.class);
		ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ii.putExtra("id", videoid);
		ii.putExtra("mvName", dto.title);
		ii.putExtra("dto", dto);
		ii.putExtra("list_type", list_type);
		if(cursor != -1)
		ii.putExtra("cursor", cursor);

		startActivity(ii);
	}

	// 플레이어 액티비티로 이동 ( 여러개 동영상 )
	public void gotoListPlayer(Bundle videoids, ArrayList<YTListDto> dto,  int cursor){
		Intent ii = null;
		if(Build.VERSION.SDK_INT >= 11){
			ii = new Intent(getApplicationContext(), PlayerActivity_v11.class);
		}else{
			ii = new Intent(getApplicationContext(), PlayerActivity.class);
		}
		ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		ii.putExtra("ID_ARRAY", videoids);
		ii.putExtra("dto", dto);
		ii.putExtra("list_type", "playlist_multi");
		if(cursor != -1)
		ii.putExtra("cursor", cursor);

		startActivity(ii);
	}

	
	boolean isAdLoading = false;
	private AdInterstitial adInterstitial;
	private AdInterstitialListener mAdInterstitialListener;
	
	private void initAd(){
		// Context를 parameter로 AdInterstitial객체를 생성합니다.
		if(adInterstitial == null){
			adInterstitial = new AdInterstitial(this);
			// 준비 과정에 발급받은 ClientId를 직접 입력합니다.
//			adInterstitial.setClientId(Define.TAD_CLIENT);
			
			adInterstitial.setClientId(Define.TAD_CLIENT);
			
			// 원하는 크기의 Slot을 설정합니다.
			adInterstitial.setSlotNo(Slot.INTERSTITIAL);
			// TestMode를 정합니다. true인 경우 test 광고가 수신됩니다.
			adInterstitial.setTestMode(false);
			// 광고가 노출된 후 5초 동안 사용자의 반응이 없을 경우 광고 창을 자동으로 닫을 것인지를 설정합니다.
			adInterstitial.setAutoCloseWhenNoInteraction(false);
			// 광고가 노출된 후 랜딩 액션에 인해 다른 App으로 전환 시 광고 창을 자동으로 닫을 것인지를 설정합니다.
			adInterstitial.setAutoCloseAfterLeaveApplication(false);
			
			mAdInterstitialListener = new AdInterstitialListener() {
				// 광고를 요청하기 전에 호출이 됩니다.
				public void onAdWillReceive() { }
				//광고 전문을 수신한 경우 호출이 됩니다.
				public void onAdReceived() { }
				// 수신한 광고를 로딩 할 때 호출이 됩니다.
				public void onAdWillLoad() { }
				// 광고의 로딩이 완료되었을 때 호출이 됩니다.
				public void onAdLoaded() { 					
				}
				// 광고 수신, 로드 과정에서 실패 시 호출되고 자세한 내용은 errorCode를 참조하세요
				public void onAdFailed(ErrorCode errorcode) { }
				// 삽입형광고가 showAd()가 될 때 호출됩니다
				public void onAdPresentScreen () { }
				// 삽입형 광고가 닫힐 때 호출됩니다.
				public void onAdDismissScreen () { }
				// 광고의 클릭으로 인해, 다른 애플리케이션이 실행될 때에 호출됩니다.
				public void onAdLeaveApplication () { }
			};
			
			adInterstitial.setListener(mAdInterstitialListener);
			adInterstitial.loadAd();
		}		
	}
	
	// 어플 종료전에 광고보기
	@Override
	public void onBackPressed() {
		if(isDetailSearch)
			setLayer();
		else {
			if(IS_PRESS_BACK){
				super.onBackPressed();
			}else{
				Toast.makeText(getApplicationContext(), R.string.on_finish, Toast.LENGTH_SHORT).show();
				IS_PRESS_BACK = true;
				// 시간내에 광고가 출력되었는지 체크
				if(AppUserSettings.getIsAdToday(getApplicationContext()).equals("0") || 
					AppUserSettings.getIsAdToday(getApplicationContext()).equals("")){
					
					if(adInterstitial.isReady()){
						adInterstitial.showAd();
						AppUserSettings.getIsAdToday(getApplicationContext()).equals("1");
					}					
				}
			} //IS_PRESS_BACK
		}
	}
	
	// 페이지 뷰 카운트 넘기기
	private void sendPageViewLog(){
		RequestQueue reqQ = Volley.newRequestQueue(getApplicationContext());
		
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				String rs = response.trim();
				
			}
		};
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.E(error.getLocalizedMessage());
			}
		};
		
		StringRequest req = new StringRequest(UrlDef.ADD_ADVIEW_LOG, listener, errorListener);
		
		reqQ.add(req);
	}
}