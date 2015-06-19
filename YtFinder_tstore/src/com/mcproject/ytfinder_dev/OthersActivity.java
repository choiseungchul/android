package com.mcproject.ytfinder_dev;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.adapter.OthersListAdapter;
import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.dialog.ScrollTextDialog;
import com.mcproject.net.dto.OthersListDto;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;
import com.skplanet.tad.AdInterstitial;
import com.skplanet.tad.AdInterstitialListener;
import com.skplanet.tad.AdInterstitialListener.ErrorCode;
import com.skplanet.tad.AdView.Slot;

public class OthersActivity extends Activity{

	private LinearLayout menu_btn_1;
	private LinearLayout menu_btn_2;
	private LinearLayout menu_btn_3;
	private ListView list_layout;
	private Builder NationSelectDialog;
	ArrayList<OthersListDto> datas;
	OthersListAdapter adap;
	private LinearLayout menu_btn_4;
	private boolean IS_PRESS_BACK = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.others_layout);

		initUI();
		initData();
	}

	private void initData() {
		loadData();

		adap = new OthersListAdapter(getApplicationContext(), datas);

		list_layout.setAdapter(adap);
		list_layout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapv, View arg1, int pos,
					long arg3) {

				if(pos == 0){			
					final ProgressDialog dialog = ProgressDialog.show(OthersActivity.this, null, getString(R.string.load), true, true);
					
					RequestQueue reqQ = Volley.newRequestQueue(getApplicationContext());
					
					// 버전 체크
					JsonObjectRequest version = new JsonObjectRequest(UrlDef.GET_VERSION, null, new Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {	
							dialog.dismiss();
							
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
									int latestVersionCode = Integer.parseInt(LatestVersion);
									
									if(vCode < requireVersionCode){
										// 업데이트 필요
										AlertDialog.Builder alert = new Builder(OthersActivity.this);										
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
									}else if(vCode < latestVersionCode){
										// 최신업데이트 할거냐
										AlertDialog.Builder alert = new Builder(OthersActivity.this);												
										alert.setTitle(R.string.update_recommend_title);
										String msg = String.format(getString(R.string.update_recommend_msg), AppVer);
										alert.setMessage(msg);										
										alert.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {								
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
										alert.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										});
										alert.show();
									}else{
										// 최신버전임
										AlertDialog.Builder alert = new Builder(OthersActivity.this);										
										alert.setTitle(R.string.update_not_require_title);								
										alert.setMessage(R.string.update_not_require);								
										alert.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {								
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										});										
										alert.show();
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
				}else if(pos == 1){
					// 국가설정
					NationSelectDialog = new Builder(OthersActivity.this);
					NationSelectDialog.setTitle(getString(R.string.user_select_nation_title));
					final String[] srch_nation_names = getResources().getStringArray(R.array.user_select_nation_names);
					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, srch_nation_names);
					NationSelectDialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							McUtil.setNation(getApplicationContext(), which);
							dataReload();
						}
					});
					NationSelectDialog.show();
//				}else if(pos == 2){
					// 세이프서치 설정
//					AlertDialog.Builder setSafeSearch = new Builder(OthersActivity.this);
//					setSafeSearch.setTitle(getString(R.string.others_act_list_title3));
//
//					final String[] safe_s_level = new String[3];
//					safe_s_level[0] = getString(R.string.safe_search_text0);
//					safe_s_level[1] = getString(R.string.safe_search_text1);
//					safe_s_level[2] = getString(R.string.safe_search_text2);
//
//					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, safe_s_level);
//					setSafeSearch.setAdapter(adapter, new AlertDialog.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							if(which == 0){
//								AppUserSettings.setSafeSearch(getApplicationContext(), "none");
//							}else if(which == 1){
//								AppUserSettings.setSafeSearch(getApplicationContext(), "moderate");
//							}else if(which == 2){
//								AppUserSettings.setSafeSearch(getApplicationContext(), "strict");
//							}
//
//							dataReload();
//						}
//					});
//					setSafeSearch.show();

				}else if(pos == 2){
					// 푸시알람 설정
					AlertDialog.Builder dialog = new Builder(OthersActivity.this);
					dialog.setTitle(getString(R.string.others_act_list_title4));

					final String[] noti_setting = new String[2];
					noti_setting[0] = getString(R.string.noti_alert_yes);
					noti_setting[1] = getString(R.string.noti_alert_no);

					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, noti_setting);
					dialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								AppUserSettings.setNotiAlert(getApplicationContext(), "Y");
							}else if(which == 1){
								AppUserSettings.setNotiAlert(getApplicationContext(), "N");
							}
							dataReload();
						}
					});
					dialog.show();
				}else if(pos == 3){
					// 오디오 다운로드 설정
					AlertDialog.Builder dialog = new Builder(OthersActivity.this);
					dialog.setTitle(getString(R.string.others_act_list_title5));

					// 배열에 공백이 있으면 안됨
					final String[] noti_setting = new String[2];
					noti_setting[0] = getString(R.string.save_mp3_128);
					noti_setting[1] = getString(R.string.save_mp3_192);
//					noti_setting[2] = getString(R.string.save_mp3_256);
//					noti_setting[3] = getString(R.string.save_mp3_320);

					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, noti_setting);
					dialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
								AppUserSettings.setAudioBitrate(getApplicationContext(), "128");
							}else if(which == 1){
								AppUserSettings.setAudioBitrate(getApplicationContext(), "192");
							}
//							else if(which == 2){
//								AppUserSettings.setAudioBitrate(getApplicationContext(), "256");
//							}else if(which == 3){
//								AppUserSettings.setAudioBitrate(getApplicationContext(), "320");
//							}
							dataReload();
						}
					});
					dialog.show();
				}else if(pos == 4){
					// 영상 다운로드 해상도
					// 오디오 다운로드 설정
					AlertDialog.Builder dialog = new Builder(OthersActivity.this);
					dialog.setTitle(getString(R.string.others_act_list_title6));

					String[] noti_setting = null;					
					noti_setting = new String[3];
					noti_setting[0] = getString(R.string.mv_resolution_high);
					noti_setting[1] = getString(R.string.mv_resolution_medium);
					noti_setting[2] = getString(R.string.mv_resolution_low);
					
					ArrayAdapter< String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_dialog_item, noti_setting);
					dialog.setAdapter(adapter, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							if(which == 0){
								AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "1");
							}else if(which == 1){
								AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "2");
							}else if(which == 2){
								AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "3");
								// 저화질 영상이 없을 경우가 있다는걸 명시
								Toast.makeText(getApplicationContext(), R.string.mv_down_low_resolution_alert, Toast.LENGTH_LONG).show();
							}
							
//							if(Define.IS_PROVERSION){
//								if(which == 0){
//									AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "1");
//								}else if(which == 1){
//									AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "2");
//								}else if(which == 2){
//									AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "3");
//									// 저화질 영상이 없을 경우가 있다는걸 명시
//									Toast.makeText(getApplicationContext(), R.string.mv_down_low_resolution_alert, Toast.LENGTH_LONG).show();
//								}
//							}else{
//								if(which == 0){
//									AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "2");
//								}else if(which == 1){
//									AppUserSettings.setMovieDownLoadResolution(getApplicationContext(), "3");
//								}
//							}
							dataReload();
						}
					});

					dialog.show();
				} // else if
				else if(pos == 5){
					// 저장폴더
					AlertDialog.Builder dialog = new Builder(OthersActivity.this);
					dialog.setTitle(getString(R.string.others_act_list_title7));
					dialog.setMessage(Define.APP_FOLDER);
					dialog.show();
					
				}else if(pos == 6){
					// 문의하기
					Intent ii = new Intent(getApplicationContext(), ContactActivity.class);
					startActivity(ii);
				}else if(pos == 7){
					// About
					ScrollTextDialog dialog = new ScrollTextDialog(OthersActivity.this);
					dialog.setTitle(R.string.others_act_list_title8);
					dialog.setContent(R.string.about_text);
					dialog.show();
				}
			} // override on itemclick
		});
	}

	private void dataReload(){
		loadData();
		if(adap!=null){
			adap.setData(datas);
			adap.notifyDataSetChanged();
		}
	}

	private void loadData() {
		datas = new ArrayList<OthersListDto>();

		// 버전정보
		String version = "unknown";
		try {
		PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
		version = i.versionName;
		} catch(NameNotFoundException e) { }
		datas.add(new OthersListDto(getString(R.string.others_act_list_title1), version));

		// 국가코드 
		datas.add(new OthersListDto(getString(R.string.others_act_list_title2), AppUserSettings.getNationCode(getApplicationContext())));

		// 세이프서치 설정
//		String safe_search = AppUserSettings.getSafeSearch(getApplicationContext());
//		if(safe_search.equals("") || safe_search.equals("moderate")) safe_search = getString(R.string.safe_search_text1);
//		else if(safe_search.equals("none")) safe_search = getString(R.string.safe_search_text0);
//		else if(safe_search.equals("strict")) safe_search = getString(R.string.safe_search_text2);
//		datas.add(new OthersListDto(getString(R.string.others_act_list_title3), safe_search));

		// 업로더 영상 알림 설정
		String noti = AppUserSettings.getNotiAlert(getApplicationContext());
		if(noti.equals("") || noti.equals("Y")) noti = getString(R.string.noti_alert_yes);
		else if(noti.equals("N")) noti = getString(R.string.noti_alert_no);
		datas.add(new OthersListDto(getString(R.string.others_act_list_title4), noti));

		// 음원 추출 비트레이트 
		String bitrate = AppUserSettings.getAudioBitrate(getApplicationContext());
		if(bitrate.equals("")) bitrate = "128";
		datas.add(new OthersListDto(getString(R.string.others_act_list_title5), bitrate+"k"));

		// 동영상 다운로드 해상도 설정 
		String resolution = AppUserSettings.getMovieDownLoadResolution(getApplicationContext());
		String resText = "";
		if(resolution.equals("1")){			
			resText = getString(R.string.mv_resolution_high);
		}else if(resolution.equals("2")){
			resText = getString(R.string.mv_resolution_medium);
		}else if(resolution.equals("3")){
			resText = getString(R.string.mv_resolution_low);
		}else{
			resText = getString(R.string.mv_resolution_high);
		}
		
		datas.add(new OthersListDto(getString(R.string.others_act_list_title6), resText));
		
		// 저장경로
		datas.add(new OthersListDto(getString(R.string.others_act_list_title7), Define.APP_FOLDER));
		
		// 문의하기
		datas.add(new OthersListDto(getString(R.string.others_act_list_title9), ""));
		
		// About
		datas.add(new OthersListDto(getString(R.string.others_act_list_title8), ""));
	}

	private void initUI() {
		initAd();
		
		list_layout = (ListView)findViewById(R.id.list_layout);

		// 하단 메뉴 초기화작업
		menu_btn_1 = (LinearLayout)findViewById(R.id.menu_btn_1);
		menu_btn_2 = (LinearLayout)findViewById(R.id.menu_btn_2);
		menu_btn_3 = (LinearLayout)findViewById(R.id.menu_btn_3);
		menu_btn_4 = (LinearLayout)findViewById(R.id.menu_btn_4);
		menu_btn_4.setBackgroundResource(R.drawable.button_black_on);

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
					Intent ii = new Intent(getApplicationContext(), FavoriteActivity_v9.class);
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
