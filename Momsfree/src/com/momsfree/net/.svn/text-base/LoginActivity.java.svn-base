package com.momsfree.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.customview.AddrSrchDialog;
import com.momsfree.net.customview.DefaultDialog;
import com.momsfree.net.customview.FindAddrDialog;
import com.momsfree.net.customview.InputDialog;
import com.momsfree.net.customview.TextViewNanumGothic;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;
import com.momsfree.net.preference.Setting;
import com.momsfree.util.GPSTracker;
import com.momsfree.util.LocationUtil;
import com.momsfree.util.LogUtil;
import com.momsfree.util.MomsUtil;

public class LoginActivity extends BaseActivity{

	TextViewNanumGothic join_btn, find_email_btn, find_pass_btn, login_btn, top_title;
	EditText login_input_email, login_input_pass;
	CheckBox auto_login_chk;
	OnClickListener listener;
	HttpDocument mHttp;
	private ImageButton loc_btn;
	DefaultDialog dialog;
	ImageButton home_btn;
	int id_homebtn, id_toptitle, id_locbtn;

	private InputDialog input_dialog;
	private Handler handle = new Handler();
	Runnable run;
	long waittime = 10000;

	Handler gotoMainHandle = new Handler();
	
	AddrSrchDialog addrDialog;
	FindAddrDialog findDialog;
	GPSTracker track;
	Location myLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(Setting.getUserType(getApplicationContext()).equals("M")){
			setContentView(R.layout.login_blue);
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
		}else if(Setting.getUserType(getApplicationContext()).equals("B")){
			setContentView(R.layout.login_blue);
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
			//			setContentView(R.layout.login_pink);
			//			id_homebtn = R.id.home_btn_pink;
			//			id_toptitle = R.id.top_title_pink;
			//			id_locbtn = R.id.loc_btn_pink;
		}else{
			setContentView(R.layout.login_blue);
			id_homebtn = R.id.home_btn_blue;
			id_toptitle = R.id.top_title_blue;
			id_locbtn = R.id.loc_btn_blue;
		}


		mHttp = new HttpDocument(this);

		run = new Runnable() {

			@Override
			public void run() {
				showInputDialog();
			}
		};

		initUI();
	}

	private void showInputDialog(){

		if(dialog == null){
			//둘다안될경우는 입력창을 띄운다.
			input_dialog = new InputDialog(LoginActivity.this);
			input_dialog.setTitle("알림");
			input_dialog.setContent("현재 위치정보를 받을수 없습니다.\n수동으로 위치를 입력해주세요.");
			input_dialog.setHint("주소검색 (ex.동, 읍, 면)");
			input_dialog.setSearchEvent(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(input_dialog.getMessage().equals("")){
						Toast.makeText(getApplicationContext(), "검색할 주소를 입력해주세요.", Toast.LENGTH_LONG).show();
					}else{
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("address", input_dialog.getMessage());

						final HttpDocument getaddr = new HttpDocument(getApplicationContext());
						getaddr.getDocument(UrlDef.GET_ADDR, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(
									Document document, Header[] header) {
								String data = document.select("body").text();

								try {
									JSONObject json = new JSONObject(data);
									String addr = json.getString("addree");

									addr = URLDecoder.decode(addr, "UTF-8");

									input_dialog.setMessage(addr);

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								getaddr.threadStop();
							}
							
						}, false);
					}
				}
			});
			input_dialog.setPositiveButton("입력", new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(input_dialog.getMessage().equals("")){
						Toast.makeText(getApplicationContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show();
					}else{
						dialog = new DefaultDialog(LoginActivity.this);
						dialog.setTitle("주소 확인");
						dialog.setMessage("입력된 주소\n" + input_dialog.getMessage() + "\n 가 맞습니까?");
						dialog.setPositiveButton("네", new OnClickListener() {
							@Override
							public void onClick(View v) {
								input_dialog.dismiss();
								Setting.setUserLocation(getApplicationContext(), input_dialog.getMessage());
								dialog.dismiss();
								Toast.makeText(getApplicationContext(), "위치정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
							}
						});
						dialog.setNagativeButton("아니오", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

						dialog.show();
					}
				}
			});

			input_dialog.setNagativeButton("입력안함", new OnClickListener() {
				@Override
				public void onClick(View v) {
					input_dialog.dismiss();
				}
			});

			input_dialog.show();
		}
	}

	private void initUI() {		

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

		auto_login_chk = (CheckBox)findViewById(R.id.auto_login_chk);

		top_title = (TextViewNanumGothic)findViewById(id_toptitle);
		top_title.setText("로그인");

		loc_btn = (ImageButton)findViewById(id_locbtn);
		loc_btn.setOnClickListener(new OnClickListener() {
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
					final DefaultDialog dialog = new DefaultDialog(LoginActivity.this);
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

		join_btn = (TextViewNanumGothic)findViewById(R.id.join_btn);
		find_email_btn = (TextViewNanumGothic)findViewById(R.id.find_email_btn);
		find_pass_btn = (TextViewNanumGothic)findViewById(R.id.find_pass_btn);

		login_input_email = (EditText)findViewById(R.id.login_input_email);
		login_input_pass  =(EditText)findViewById(R.id.login_input_pass);
		login_btn = (TextViewNanumGothic)findViewById(R.id.login_btn);
		login_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkLoginInput()){
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("user_id", login_input_email.getText().toString());
					params.put("user_passwd", login_input_pass.getText().toString());
					params.put("auto_login", "auto_login");

					mHttp.setMethod("POST");

					mHttp.getDocument(UrlDef.LOGIN, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document, Header[] header) {
							String returnData = document.select("body").text();

							JSONObject obj = null;
							try {
								obj = new JSONObject(returnData);

								LogUtil.D("all = " + obj.toString() );

								String result = obj.getString("result");
								if(result.equals("0000")){
									// 로그인 성공
									Setting.setEmail(getApplicationContext(), login_input_email.getText().toString());

									//									StringBuffer sb = new StringBuffer();
									//									
									//									for(int i = 0 ; i < header.length; i++){
									//										LogUtil.D(header[i].getName() + ":" + header[i].getValue());
									//										if(header[i].getName().contains("Set-Cookie")){
									//											String v = header[i].getValue().split(";")[0] ;
									//											LogUtil.D("v = " + v);
									//											sb.append( v );
									//											sb.append(";");
									//										}
									//									}

									String token = obj.getString("token");
									
									String m_type = obj.getString("member_type");
									try {
										token = URLDecoder.decode(token, "UTF-8");

										Setting.setUserType(getApplicationContext(), m_type);

										Setting.setToken(getApplicationContext(), token);
										Setting.setCookieString(getApplicationContext(), token);
										
//										Define.token = token;
//										Define.user_id =  login_input_email.getText().toString();

										LogUtil.D("LOGIN TOKEN = " + token);
										Toast.makeText(getApplicationContext(), "로그인이 완료되었습니다.", Toast.LENGTH_LONG).show();
										
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} 

									if(auto_login_chk.isChecked()){
										Setting.setAutoLogin(getApplicationContext(), "Y");
									}

									String u_type = Setting.getUserType(getApplicationContext());
									String url = "";
									String title = "";
									if(u_type.equals("M")){
										url = UrlDef.FIND_SITTER;
										title = "일자리 정보";
									}else if(u_type.equals("B")){
										url = UrlDef.FIND_JOB;
										title = "베이비시터 정보";
									}
									
									final Intent ii = new Intent(getApplicationContext(), WebActivity.class);
									ii.putExtra("url", url);
									ii.putExtra("title", title);
									ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(ii);
									finish();
									overridePendingTransition(0,0);
									
								}else{
									String msg = obj.getString("msg");
									String msg_detail = obj.getString("msg_detail");

									try {
										Toast.makeText(getApplicationContext(), URLDecoder.decode(msg, "UTF-8"),  Toast.LENGTH_LONG).show();
										Toast.makeText(getApplicationContext(), URLDecoder.decode(msg_detail, "UTF-8"),  Toast.LENGTH_LONG).show();
									} catch (UnsupportedEncodingException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					}, false);
				}
			}
		});

		listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int id = v.getId();
				Intent ii = new Intent(getApplicationContext(), WebActivityNonMenu.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				switch (id) {
				case R.id.join_btn:
					ii.putExtra("url", "http://momsfree.co.kr/app/a_v/signup.php");
					ii.putExtra("title", getString(R.string.join));
					startActivity(ii);
					overridePendingTransition(0,0);
					break;
				case R.id.find_email_btn:
					ii.putExtra("url", "http://momsfree.co.kr/app/a_v/find.php");
					ii.putExtra("title", getString(R.string.find_email));
					startActivity(ii);
					overridePendingTransition(0,0);
					break;
				case R.id.find_pass_btn:
					ii.putExtra("url", "http://momsfree.co.kr/app/a_v/find.php?act=pass");
					ii.putExtra("title", getString(R.string.find_password));
					startActivity(ii);
					overridePendingTransition(0,0);
					break;
				default:
					break;
				}
			}
		};

		join_btn.setOnClickListener(listener);
		find_email_btn.setOnClickListener(listener);
		find_pass_btn.setOnClickListener(listener);
	}

	// 최종 주소지가 나왔을때..
	private void showDialog(final String LocationString, final double lat, final double lng){
		findDialog = new FindAddrDialog(LoginActivity.this);
		findDialog.setTitle(LocationString);
		findDialog.setSrchbtn(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findDialog.dismiss();
				addrDialog = new AddrSrchDialog(LoginActivity.this);
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
							addrDialog.dismiss();
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

	private boolean checkLoginInput(){	
		String Email = login_input_email.getText().toString();
		String passwd = login_input_pass.getText().toString();

		if(Email.equals("") || passwd.equals("")){
			Toast.makeText(getApplicationContext(), "이메일 또는 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void onBackPressed() {

		// TODO Auto-generated method stub
		final DefaultDialog dialog = new DefaultDialog(this);
		dialog.setTitle("알림");
		dialog.setMessage("프로그램을 종료하시겠습니까?");
		dialog.setPositiveButton("예", new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
			}
		});
		dialog.setNagativeButton("아니오", new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();

	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttp.threadStop();
		super.onDestroy();
	}

}
