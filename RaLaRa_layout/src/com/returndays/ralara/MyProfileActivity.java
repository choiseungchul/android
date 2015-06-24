package com.returndays.ralara;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.returndays.customview.SquareImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.image.BitmapHelper;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.LogUtil;

public class MyProfileActivity extends BaseActivity{

	SquareImageView prf_img;
	TextView prf_desc, open_date;
	LinearLayout back_btn;
	Button mSex, mSido, mGugun;
	Button save_profile;
	String mSidoStr, mGugunStr, mSexStr, mProfileImagePath;
	HttpDocument mHttpUtil, mHttpUtil_myprofile, mHttpUtilGugun, updateUser;
	String[] gugunList;
	File folder;
	private Uri mTempImageUri, tempCameraCropped;
	int aspectX = 200, aspectY = 200, outputX = 1024, outputY = 1024;
	int GET_ALBUM_CODE = 1111;
	int GET_CAMERA_CODE = 2222;
	int CROP_FROM_CAMERA = 3333;
	private String tempImage = "MYPROFILE_IMAGE.jpg";
	private Bitmap bm;
	private float LIMIT_SIZE = 1920;
	int yy, mm, dd;
	String date;
	boolean profile_image_bonus = false;
	boolean isExistProfileImage = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myprofile);
		
		mHttpUtil = new HttpDocument(getApplicationContext());
		mHttpUtil_myprofile = new HttpDocument(getApplicationContext());
		mHttpUtilGugun = new HttpDocument(getApplicationContext());
		updateUser = new HttpDocument(this);
		
		if (getIntent().getExtras() != null){
			String egg_bonus = getIntent().getExtras().getString("egg_bonus");
			if(egg_bonus != null){
				if(egg_bonus.equals("Y"))
				profile_image_bonus  = true;
			}
		}
		
		initUI();
		initData();
	}

	private void reloadProfile(){
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		
		mHttpUtil_myprofile.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
			
			@Override
			public void onHttpCallBackListener(Document document) {
				
				if(document.select("ResultTable").select("Result").text().equals("true")){
					if(!document.select("ReturnTable").select("USER_SEQ").text().equals("")){
						String USER_EMAIL = document.select("USER_EMAIL").text();
						String USER_NICKNAME = document.select("USER_NICKNAME").text();
						String THUMBNAIL = document.select("USER_IMG_PATH").text() + document.select("USER_IMG").text() ;
						String HP_NO = document.select("HP_NO").text();
						String SIDO = document.select("SIDO").text();
						String GUGUN = document.select("GUGUN").text();
						String SEX = document.select("SEX").text();
						String ADDRESS_HEAD = document.select("ADDRESS_HEAD").text();
						String BIRTHDAY = document.select("BIRTHDAY").text();
						
						prf_desc.setText(USER_NICKNAME + "  /  " + USER_EMAIL );
						
						if(!THUMBNAIL.equals("")){
							AQuery aq = new AQuery(getApplicationContext());
							aq.id(prf_img).image(THUMBNAIL);
							isExistProfileImage = true;
						}else{
							if(SEX.equals("M")){
								prf_img.setImageDrawable(getResources().getDrawable(R.drawable.man_ico));
							}else if(SEX.equals("F")){
								prf_img.setImageDrawable(getResources().getDrawable(R.drawable.woman_ico));
							}
							isExistProfileImage = false;
						}
						mSido.setText(SIDO);
						mGugun.setText(GUGUN);
						if(SEX.equals("M")){
							mSex.setText("남자");
							mSexStr = "남자";
						}else if(SEX.equals("F")){
							mSex.setText("여자");
							mSexStr = "여자";
						}
						
						mSidoStr = SIDO;
						mGugunStr = GUGUN;
						
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("area_depth", "2");
						params.put("sido", SIDO);
						
						mHttpUtilGugun.getDocument(UrlDef.GET_AREA, params, null, new HttpCallBack() {
							
							@Override
							public void onHttpCallBackListener(Document document) {
								
								LogUtil.W(document.select("ResultTable").select("Result").text());
								if(!document.select("ReturnTable").isEmpty()){
									Elements list = document.select("ReturnTable");
									gugunList =  new String[list.size()];
									for(int i = 0 ; i < list.size(); i++){
										Element item = list.get(i);
										gugunList[i] = item.select("GUGUN").text();
									}

									mGugun.setClickable(true);
								}
							}
						}, false);
						
						final String[] birth = BIRTHDAY.split("-");
						
						date = birth[0] + "-" + birth[1] + "-" + birth[2];
						yy = Integer.parseInt(birth[0]);
						mm = Integer.parseInt(birth[1]);
						dd = Integer.parseInt(birth[2]);
						open_date.setText(birth[0] + "년 " + birth[1] + "월 " + birth[2] + "일");
					}
				}
			}
		}, false);
	}
	
	private void initData() {
		
		folder = new File(Define.AD_APP_FOLDER);
		if(!folder.exists()) folder.mkdirs();
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		
		mHttpUtil_myprofile.getDocument(UrlDef.USERINFO, params, null, new HttpCallBack() {
			
			@Override
			public void onHttpCallBackListener(Document document) {
				
				if(document.select("ResultTable").select("Result").text().equals("true")){
					if(!document.select("ReturnTable").select("USER_SEQ").text().equals("")){
						String USER_EMAIL = document.select("USER_EMAIL").text();
						String USER_NICKNAME = document.select("USER_NICKNAME").text();
						String THUMBNAIL = document.select("USER_IMG_PATH").text() + document.select("USER_IMG").text() ;
						String SIDO = document.select("SIDO").text();
						String GUGUN = document.select("GUGUN").text();
						String SEX = document.select("SEX").text();
						String ADDRESS_HEAD = document.select("ADDRESS_HEAD").text();
						String BIRTHDAY = document.select("BIRTHDAY").text();
						String HP_NO = document.select("HP_NO").text();
						
						prf_desc.setText(USER_NICKNAME + "  /  " + USER_EMAIL );
						
						if(!THUMBNAIL.equals("")){
							AQuery aq = new AQuery(getApplicationContext());
							aq.id(prf_img).image(THUMBNAIL);
							isExistProfileImage = true;
						}else{
							if(SEX.equals("M")){
								prf_img.setImageDrawable(getResources().getDrawable(R.drawable.man_ico));
							}else if(SEX.equals("F")){
								prf_img.setImageDrawable(getResources().getDrawable(R.drawable.woman_ico));
							}
						}
						mSido.setText(SIDO);
						mGugun.setText(GUGUN);
						if(SEX.equals("M")){
							mSex.setText("남자");
							mSexStr = "남자";
						}else if(SEX.equals("F")){
							mSex.setText("여자");
							mSexStr = "여자";
						}
						
						mSidoStr = SIDO;
						mGugunStr = GUGUN;
						
						mSex.setBackgroundResource(R.drawable.button_dark);
						mSido.setBackgroundResource(R.drawable.button_dark);
						mGugun.setBackgroundResource(R.drawable.button_dark);
						
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("area_depth", "2");
						params.put("sido", SIDO);
						
						mHttpUtilGugun.getDocument(UrlDef.GET_AREA, params, null, new HttpCallBack() {
							
							@Override
							public void onHttpCallBackListener(Document document) {
								
								LogUtil.W(document.select("ResultTable").select("Result").text());
								if(!document.select("ReturnTable").isEmpty()){
									Elements list = document.select("ReturnTable");
									gugunList =  new String[list.size()];
									for(int i = 0 ; i < list.size(); i++){
										Element item = list.get(i);
										gugunList[i] = item.select("GUGUN").text();
									}

									mGugun.setClickable(true);
									//mGugun.setBackgroundResource(R.drawable.button_gray);
								}
							}
						}, false);
						
						final String[] birth = BIRTHDAY.split("-");
						
						date = birth[0] + "-" + birth[1] + "-" + birth[2];
						yy = Integer.parseInt(birth[0]);
						mm = Integer.parseInt(birth[1]);
						dd = Integer.parseInt(birth[2]);
						
						open_date.setOnClickListener(new OnClickListener() {
							@Override 
							public void onClick(View v) {
								
								DatePickerDialog dp = new DatePickerDialog(MyProfileActivity.this, new OnDateSetListener() {
									
									@Override
									public void onDateSet(DatePicker view, int year, int monthOfYear,
											int dayOfMonth) {
										
										String month = "";
										String day = "";
										if(monthOfYear < 9){
											month = "0" + (monthOfYear + 1);
										}else{
											month = String.valueOf( monthOfYear + 1 );
										}
										if(dayOfMonth < 10){
											day = "0" + dayOfMonth;
										}else{
											day = String.valueOf(dayOfMonth);
										}
										open_date.setText(year + "년 " + month + "월 " + day + "일");
										date = year + "-" + month + "-" + day;
										yy = year;
										mm = monthOfYear + 1;
										dd = dayOfMonth;
									}
								} 
								,yy
								, mm - 1
								, dd);
								
								dp.setTitle("생년월일을 설정하세요.");
								dp.show();
							} 
						});
						
						open_date.setText(birth[0] + "년 " + birth[1] + "월 " + birth[2] + "일");
						
						
						// 프로필 저장
						save_profile.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								
								String sex = null;
								
								Hashtable<String,String> params = new Hashtable<String, String>();
								if(mSexStr.equals("남자")){
									sex = "M";
								}else if(mSexStr.equals("여자")) {
									sex = "F";
								}
								params.put("sex", sex);
								params.put("user_brithday", date);
								params.put("sido", mSidoStr);
								params.put("gugun", mGugunStr);
								params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
								params.put("token", Setting.getToken(getApplicationContext()));
								
								
								LogUtil.D(params.toString());
								
								updateUser.getDocument(UrlDef.USER_EDIT2, params, null,  new HttpCallBack() {
									
									@Override
									public void onHttpCallBackListener(Document document) {
										
										String Code = document.select("ReturnTable").select("Code").text();
										String result = document.select("ReturnTable").select("Result").text();
										
										if(Code.equals("0")){
											if(document.select("ResultTable").select("Result").text().equals("true")){
												String TOKEN = document.select("TOKEN_KEY").text();
												Setting.setToken(getApplicationContext(), TOKEN);
												Toast.makeText(getApplicationContext(), "회원정보가 저장되었습니다.", Toast.LENGTH_LONG).show();
												reloadProfile();
											}
										}else{
											if(Code.equals("-2001")){
												Toast.makeText(getApplicationContext(), "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show();
											}else if(Code.equals("-2002")){
												Toast.makeText(getApplicationContext(), "시/도 정보가 없습니다.", Toast.LENGTH_SHORT).show();
											}else if(Code.equals("-2003")){
												Toast.makeText(getApplicationContext(), "구/군 정보가 없습니다.", Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(getApplicationContext(), Code + ":" +result , Toast.LENGTH_SHORT).show();
											}
										}
									}
								}, true);
							}
						});
						
					}
				}else{
					Toast.makeText(getApplicationContext(), R.string.userinfo_failed, Toast.LENGTH_LONG).show();
				}
			}
		}, false);
	}

	private void initUI() {
		open_date = (TextView)findViewById(R.id.open_date);
		prf_img = (SquareImageView)findViewById(R.id.prf_img);
		prf_desc = (TextView)findViewById(R.id.prf_desc);
		mSex = (Button)findViewById(R.id.input_sex);
		mSido = (Button)findViewById(R.id.sido);
		mGugun = (Button)findViewById(R.id.gugun);
		
		save_profile = (Button)findViewById(R.id.save_btn);
		prf_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				final String[] gselect = {"갤러리","카메라", "프로필 이미지 제거" };  

				new AlertDialog.Builder(MyProfileActivity.this)
				.setItems(gselect, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){
							mTempImageUri = Uri.fromFile(getTempFile());
							
							Intent intent = new Intent(Intent.ACTION_PICK); 
						    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
						    intent.putExtra("crop", "true");
						    intent.putExtra( "aspectX", aspectX );
							intent.putExtra( "aspectY", aspectY );
							intent.putExtra( "outputX", outputX );
							intent.putExtra( "outputY", outputY);
						    intent.putExtra("scale", "true");
							intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
							intent.putExtra( "outputFormat", Bitmap.CompressFormat.JPEG.toString() );
							startActivityForResult(intent, GET_ALBUM_CODE);
							
						}else if(which == 1){
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
							intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
							startActivityForResult(intent, GET_CAMERA_CODE);
						}else if(which == 2){
							if(isExistProfileImage){
								DialogUtil.confirm(MyProfileActivity.this, "프로필 이미지를 삭제 하시겠습니까?", new View.OnClickListener(){
									@Override
									public void onClick(View v) {
										final HttpDocument httpdoc = new HttpDocument(getApplicationContext());
										Hashtable<String, String> params = new Hashtable<String, String>();
										params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
										httpdoc.getDocument(UrlDef.MY_PROFILE_IMAGE_DELETE, params, null, new HttpCallBack() {
											@Override
											public void onHttpCallBackListener(Document document) {
												if(document.select("ResultTable").select("Result").text().equals("true")){
													Toast.makeText(getApplicationContext(), "프로필 이미지가 삭제되었습니다.", Toast.LENGTH_LONG).show();
													reloadProfile();
													DialogUtil.alert.dismiss();
												}
												httpdoc.threadStop();
											}
										}, false);
									}
								});
								DialogUtil.alert.show();
							}else{
								Toast.makeText(getApplicationContext(), "프로필 이미지가 없습니다.", Toast.LENGTH_LONG).show();
							}
						}
					}

				}).create().show();
			}
		});
		
		mGugun.setClickable(false);
		
		mSido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final String[] sidoList = getResources().getStringArray(R.array.sido_type);

				new AlertDialog.Builder(MyProfileActivity.this)
				.setItems(sidoList, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSido.setText(sidoList[which]);
						mSidoStr = sidoList[which];

						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("area_depth", "2");
						params.put("sido", mSidoStr);

						mGugun.setClickable(false);

						mHttpUtil.getDocument(UrlDef.GET_AREA, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {
								
								LogUtil.W(document.select("ResultTable").select("Result").text());

								if(!document.select("ReturnTable").isEmpty()){
									Elements  list = document.select("ReturnTable");
									gugunList =  new String[list.size()];

									for(int i = 0 ; i < list.size(); i++){
										Element item = list.get(i);
										gugunList[i] = item.select("GUGUN").text();
									}
									
									mGugunStr = gugunList[0];
									mGugun.setText(mGugunStr);

									mGugun.setClickable(true);
								}
							}
						}, false);

					}

				}).create().show();
			}
		});

		mGugun = (Button)findViewById(R.id.gugun);
		mGugun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String[] gugun = gugunList;

				new AlertDialog.Builder(MyProfileActivity.this)
				.setItems(gugun, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGugun.setText(gugun[which]);
						mGugunStr = gugun[which];

						mGugun.setBackgroundResource(R.drawable.button_dark);
					}

				}).create().show();
			}
		});
		
		mSex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				final String[] sexList = getResources().getStringArray(R.array.sex);

				new AlertDialog.Builder(MyProfileActivity.this)
				.setItems(sexList, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSex.setText(sexList[which]);
						mSexStr = sexList[which];

						mSex.setBackgroundResource(R.drawable.button_dark);

					}

				}).create().show();
			}
		});
		
		save_profile.setClickable(false);
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		
		back_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	
	// 갤러리 이미지 설정및 서버로 전송
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("1", "onActivityResult " + requestCode);
		if (resultCode == Activity.RESULT_OK)
		{
			// 앨범에서 선택한 사진 처리하기 
			if (requestCode == GET_ALBUM_CODE)
			{ 
				File tempfile = getTempFile();

				try
				{	// 이미지 리사이징
					bm = BitmapHelper.bitmapDecoder(tempfile.getAbsolutePath(), LIMIT_SIZE);
					
					// 이미지 저장 
					//file = BitmapHelper.mediaSave(getApplicationContext(), image, "/TodayCody");
					prf_img.setImageBitmap( bm);
					
					sendImage();
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(requestCode == GET_CAMERA_CODE){

				tempCameraCropped = Uri.fromFile(getTempFile());
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(Uri.fromFile(getTempFile()), "image/*");
				intent.putExtra("scale", true);
				intent.putExtra("outputX", outputX);
				intent.putExtra("outputY", outputY);
				intent.putExtra("aspectX", aspectX);
				intent.putExtra("aspectY", aspectY);
				intent.putExtra("return-data", true);
				intent.putExtra(Media.MIME_TYPE, "image/png");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraCropped);
				startActivityForResult(intent, CROP_FROM_CAMERA);

			}else if(requestCode == CROP_FROM_CAMERA){
				File tempfile = getTempFile();
				
				try {
					bm = BitmapHelper.bitmapDecoder(tempfile.getAbsolutePath(), LIMIT_SIZE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				prf_img.setImageBitmap( bm);
				
				sendImage();
			}
		}
	}
	
	private void sendImage(){
				
		
		File tempfile = getTempFile();
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		Hashtable<String, File> files = new Hashtable<String, File>();
		
		LogUtil.D("image file = " + tempfile.getAbsolutePath());
		
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		
		files.put("image1", tempfile);
		
		final HttpDocument http = new HttpDocument(MyProfileActivity.this);
		http.getDocument(UrlDef.USER_PROFILE_IMAGEUP, params, files,  new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(!document.select("IMAGE_PATH").isEmpty()){
					mProfileImagePath = document.select("IMAGE_PATH").text() + document.select("IMAGE").text();
					LogUtil.D("profile url = " + mProfileImagePath);
					Toast.makeText(getApplicationContext(), "프로필 이미지가 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
					isExistProfileImage = true;
					//Toast.makeText(getApplicationContext(), mProfileImagePath, Toast.LENGTH_LONG).show();
					if(profile_image_bonus){
						// 보너스 알 적립
						final HttpDocument getBonus = new HttpDocument(getApplicationContext());
						Hashtable<String, String> params = new Hashtable<String, String>();
						params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
						getBonus.getDocument(UrlDef.MY_PROFILE_IMAGE_BONUS, params, null, new HttpCallBack() {
							@Override
							public void onHttpCallBackListener(Document document) {
								
								LogUtil.D(document.toString());
								
								if(document.select("ReturnTable").select("Code").text().equals("0")){
									if(document.select("Table2").select("Code").text().equals("1")){
										Toast.makeText(getApplicationContext(), "알6개를 적립받으셨습니다.", Toast.LENGTH_SHORT).show();
									}
								}else{
									if(document.select("ReturnTable").select("Code").text().equals("-2001")){
										Toast.makeText(getApplicationContext(), "이미 알6개를 적립받으셨습니다.", Toast.LENGTH_SHORT).show();
									}
								}
								getBonus.threadStop();
							}
						}, false);
					}
				}
				http.threadStop();
			}
		}, true);
	}
	
	private File getTempFile() {
		File file = new File( Define.AD_APP_FOLDER + tempImage  );
		try{
			file.createNewFile();
		}
		catch( Exception e ){
			Log.e("kingpig", "fileCreation fail" );
		}
		return file;
	}
	
	
	@Override
	protected void onDestroy() {
		
		mHttpUtil.threadStop();
		mHttpUtil_myprofile.threadStop();
		mHttpUtilGugun.threadStop();
		updateUser.threadStop();
		
		if(prf_img != null){
			prf_img.setImageBitmap(null);
		}
		
		super.onDestroy();
	}
}
