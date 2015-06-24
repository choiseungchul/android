package com.returndays.ralara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.SquareImageViewMin;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.image.BitmapHelper;
import com.returndays.ralara.adapter.TalkRoomTimeAdapter;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.DevideInfoDto;
import com.returndays.ralara.dto.TalkMakeDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class TalkRoomMakeActivity extends BaseActivity{

	SquareImageViewMin talk_room_exview;
	TextView talk_room_extext;
	EditText talk_room_name, talk_content;
	Button talk_time_btn; // make_talk_btn
	FrameLayout talk_image_add_camera, talk_image_add_gallery;
	HttpDocument getRoomRcount, sendTalkRoomData, topImageSave, contentImageSave;
	Button save_btn;
	RecyclingImageView talk_image_add_gallery_close, talk_image_add_camera_close;

	// 상단이미지
	int TALKIMG_GET_ALBUM_CODE = 1111;
	int TALKIMG_GET_CAMERA_CODE = 2222;
	int TALKIMG_CROP_FROM_CAMERA = 3333;
	private Uri mTempImageUri;
	private float TALKIMG_LIMIT_SIZE = 440;
	String tempImage = "TALKROOMCOVER.png";
	boolean isTopImageUpload = false;
	Bitmap bm_top;
	Uri tempCameraCropped;
	String mTalkRoomImagePath = null;

	// 하단 이미지
	int CONTENTIMG_GET_ALBUM_CODE = 4444;
	int CONTENTIMG_GET_CAMERA_CODE = 5555;
	Bitmap bm_center;
	private float CONTENTIMG_LIMIT_SIZE = 800;
	String tempImage_content = "TALKIMAGE_CONTENT.png";
	private String mTalkContentImagePath = null;
	TalkMakeDto data;
	Uri tempContentUri_camera;
	Uri tempContentUri_album;
	boolean isContentImageUpload = true;


	// 공통
	int aspectX = 180;
	int aspectY = 180;
	int outputX = 1024;
	int outputY = 1024;
	TalkRoomTimeAdapter mAdap;
	int[] egg_cnt;
	int[] room_time;
	private String[] timeList;
	int consumeR = 0;
	int timeR = 0;
	private OnClickListener saveClickListener;
	private View back_btn;
	private File folder;
	InputMethodManager imm;
	
	DevideInfoDto device;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_make_v2);

		getRoomRcount = new HttpDocument(getApplicationContext());
		sendTalkRoomData = new HttpDocument(this);
		topImageSave = new HttpDocument(this);
		contentImageSave = new HttpDocument(this);
		
		folder = new File(Define.AD_TALK_FOLDER);
		if(!folder.exists()) folder.mkdirs();
		
		device = MadUtil.getDevideInfo();
		
		// 남은 파일이 있으면 지운다.
		File top_img = new File(Define.AD_TALK_FOLDER + "/" + tempImage);
		if(top_img.exists()) top_img.delete();
		File center_img = new File(Define.AD_TALK_FOLDER + "/" + tempImage_content);
		if(center_img.exists()) center_img.delete();
		
		
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		initUI();
		initData();
	}

	private void initData() {

	}

	private void initUI() {

		talk_image_add_camera_close = (RecyclingImageView)findViewById(R.id.talk_image_add_camera_close);
		talk_image_add_camera_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isContentImageUpload = true;
				bm_center = null;
				talk_image_add_camera.setBackgroundResource(R.drawable.button_gray);
				talk_image_add_camera.setPadding(0, 0, 0, 0);
				talk_image_add_camera_close.setVisibility(View.INVISIBLE);
			}
		});
		talk_image_add_gallery_close = (RecyclingImageView)findViewById(R.id.talk_image_add_gallery_close);
		talk_image_add_gallery_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isContentImageUpload = true;
				bm_center = null;
				talk_image_add_gallery.setBackgroundResource(R.drawable.button_gray);
				talk_image_add_gallery.setPadding(0, 0, 0, 0);
				talk_image_add_gallery_close.setVisibility(View.INVISIBLE);
			}
		});
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("방 생성하기");
		

		saveClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( top_checkInput() && content_checkInput() ){
					saveContent();
				}
			}
		};
		
		save_btn.setOnClickListener(saveClickListener);

		talk_room_exview = (SquareImageViewMin)findViewById(R.id.talk_room_exview);
		talk_room_exview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isTopImageUpload = false;
				
				final String[] gselect = {"갤러리","카메라"};  

				new AlertDialog.Builder(TalkRoomMakeActivity.this)
				.setItems(gselect, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == 0){
							mTempImageUri = Uri.fromFile(getTempFile(tempImage));

							if(device.BRAND.equals("VEGA")){
								Intent intent = new Intent(Intent.ACTION_PICK); 
								intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
								intent.putExtra("crop", "true");
								intent.putExtra( "outputX", 440 );
								intent.putExtra( "outputY", 440);
								intent.putExtra("scale", "false");
								intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
								intent.putExtra( "outputFormat", Bitmap.CompressFormat.PNG.toString() );
								startActivityForResult(intent, TALKIMG_GET_ALBUM_CODE);
							}else{
								Intent intent = new Intent(Intent.ACTION_PICK); 
								intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
								intent.putExtra("crop", "true");
								intent.putExtra( "aspectX", aspectX );
								intent.putExtra( "aspectY", aspectY );
								intent.putExtra( "outputX", outputX );
								intent.putExtra( "outputY", outputY);
								intent.putExtra("scale", "true");
								intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
								intent.putExtra( "outputFormat", Bitmap.CompressFormat.PNG.toString() );
								startActivityForResult(intent, TALKIMG_GET_ALBUM_CODE);
							}
							
							
						}else if(which == 1){
							mTempImageUri = Uri.fromFile(getTempFile(tempImage));
							Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
							intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
							startActivityForResult(intent, TALKIMG_GET_CAMERA_CODE);
						}
					}
				}).create().show();
			}
		});

		talk_room_extext = (TextView)findViewById(R.id.talk_room_extext);
		talk_room_name = (EditText)findViewById(R.id.talk_room_name);
		Drawable image = getResources().getDrawable( R.drawable.talk_m_input );
		image.setBounds( 0, 0, 30,30 );
		talk_room_name.setCompoundDrawables(image, null, null, null);
		talk_content = (EditText)findViewById(R.id.talk_content);
		talk_time_btn = (Button)findViewById(R.id.talk_time_btn);
		talk_time_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("nullparam", "0");
				getRoomRcount.getDocument(UrlDef.TALK_CONSUME_EGG, params, null, new HttpCallBack() {
					@Override
					public void onHttpCallBackListener(Document document) {
						// TODO Auto-generated method stub
						if(document.select("ResultTable").select("Result").text().equals("true")){
							int size = document.select("SEQ").size();

							timeList = new String[size];
							egg_cnt = new int[size];
							room_time = new int[size];

							for(int i = 0 ; i < size ; i++){
								timeList[i] = "알" + document.select("EGG_CNT").get(i).text() + "개 " + document.select("ROOM_TIME").get(i).text() + "분";
								egg_cnt[i] = Integer.parseInt(document.select("EGG_CNT").get(i).text());
								room_time[i] = Integer.parseInt(document.select("ROOM_TIME").get(i).text());
							}

							new AlertDialog.Builder(TalkRoomMakeActivity.this)
							.setItems(timeList, new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									talk_time_btn.setText(timeList[which]);
									consumeR = egg_cnt[which];
									timeR = room_time[which];
								}

							}).create().show();
						}
					}
				}, false);	
			}
		});
		
		talk_image_add_camera = (FrameLayout)findViewById(R.id.talk_image_add_camera);
		talk_image_add_camera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//changeUploadedImage(2);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
				imm.hideSoftInputFromWindow(talk_content.getWindowToken(), 0);   
				tempContentUri_camera = Uri.fromFile(getTempFile(tempImage_content));
				intent.putExtra( MediaStore.EXTRA_OUTPUT, tempContentUri_camera );
				startActivityForResult(intent, CONTENTIMG_GET_CAMERA_CODE);
			}
		});

		talk_image_add_gallery = (FrameLayout)findViewById(R.id.talk_image_add_gallery);
		talk_image_add_gallery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//changeUploadedImage(2);
				imm.hideSoftInputFromWindow(talk_content.getWindowToken(), 0);   
				tempContentUri_album = Uri.fromFile(getTempFile(tempImage_content));
				Intent intent = new Intent(Intent.ACTION_PICK); 
				intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
				intent.putExtra( MediaStore.EXTRA_OUTPUT, tempContentUri_album );
				intent.putExtra( "outputFormat", Bitmap.CompressFormat.PNG.toString() );
				startActivityForResult(intent, CONTENTIMG_GET_ALBUM_CODE);							
			}
		});
		
		talk_room_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				talk_room_name.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
		});
		talk_room_name.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Drawable image = getResources().getDrawable( R.drawable.talk_m_input );
				image.setBounds( 0, 0, 30,30 );
				talk_room_name.setCompoundDrawables(image, null, null, null);
			}
		});

		talk_room_name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
				talk_room_extext.setText(s);
			}
		});

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);
		back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.D("resultCode = " + resultCode);
		Log.i("1", "onActivityResult " + requestCode);
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == TALKIMG_GET_ALBUM_CODE)
			{ 
				try
				{	// 이미지 리사이징
					File tempfile = getTempFile(tempImage);

					bm_top = BitmapHelper.bitmapDecoder(tempfile.getAbsolutePath(), TALKIMG_LIMIT_SIZE);

					int w =bm_top.getWidth();
					int h = bm_top.getHeight();
					int round = ((w / 4) + (h / 4)) / 2;
					
					LogUtil.D("w = " + w + " // h = " + h);
					
					LogUtil.D("round = " + round);
					

					bm_top = MadUtil.getRoundedCornerBitmap(bm_top, round);
					
					LogUtil.D("w = " + bm_top.getWidth() + " // h = " + bm_top.getHeight());

					talk_room_exview.setImageBitmap(bm_top);

					saveImagefile(tempImage, 0);
					sendTopImage();
					
				}catch(Exception e){
					LogUtil.D(e.toString());
				}
			}else if(requestCode == TALKIMG_GET_CAMERA_CODE){
				
				tempCameraCropped = Uri.fromFile(getTempFile(tempImage));
				
				LogUtil.D("tempCameraCropped => " + tempCameraCropped.getPath());
				Intent intent = new Intent("com.android.camera.action.CROP");
				
				intent.setDataAndType(tempCameraCropped, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra("outputX", outputX);
				intent.putExtra("outputY", outputY);
				intent.putExtra("aspectX", aspectX);
				intent.putExtra("aspectY", aspectY);
				intent.putExtra("return-data", true);
				intent.putExtra(Media.MIME_TYPE, "image/png");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraCropped);
				startActivityForResult(intent, TALKIMG_CROP_FROM_CAMERA);

			}else if(requestCode == TALKIMG_CROP_FROM_CAMERA){
				
				LogUtil.D("TALKIMG_CROP_FROM_CAMERA");
				
				try {
					bm_top = BitmapHelper.bitmapDecoder(getTempFile(tempImage).getAbsolutePath(), TALKIMG_LIMIT_SIZE);
				} catch (IOException e) {
					
					LogUtil.D(e.toString());
				}
				int w =bm_top.getWidth();
				int h = bm_top.getHeight();
				int round = ((w / 4) + (h / 4)) / 2;

				bm_top = MadUtil.getRoundedCornerBitmap(bm_top, round);

				talk_room_exview.setImageBitmap(bm_top);
				saveImagefile(tempImage, 0);
				sendTopImage();
			}else if(requestCode == CONTENTIMG_GET_ALBUM_CODE){
				try{	
					talk_image_add_gallery_close.setVisibility(View.VISIBLE);
					isContentImageUpload = false;
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Uri selectedImage = data.getData();
					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
				
					LogUtil.D("imgpath gallery = " + picturePath);
					if(picturePath == null){
						Toast.makeText(getApplicationContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
					}
					
					cursor.close();
					bm_center = BitmapHelper.bitmapDecoder(picturePath, CONTENTIMG_LIMIT_SIZE);
					saveImagefile(tempImage_content, 1);
					sendContentImage(1);
				}catch(Exception e){
					LogUtil.D(e.toString());
				}
			}else if(requestCode == CONTENTIMG_GET_CAMERA_CODE){
				
				talk_image_add_camera_close.setVisibility(View.VISIBLE);
				isContentImageUpload = false;
				try {
					bm_center = BitmapHelper.bitmapDecoder(getTempFile(tempImage_content).getAbsolutePath(), TALKIMG_LIMIT_SIZE);
					saveImagefile(tempImage_content, 1);
					sendContentImage(0);
				} catch (IOException e) {
					LogUtil.D(e.toString());
				}
			}
		}else{
			LogUtil.D("resultCode = " + resultCode);
			LogUtil.D("requestCode = " + requestCode);
			if(requestCode == CONTENTIMG_GET_ALBUM_CODE  || requestCode == CONTENTIMG_GET_CAMERA_CODE){
				isContentImageUpload = true;
			}
		}
		// lg 폰 카메라 관련
		if(resultCode == 0){
			// 앨범에서 선택한 사진 처리하기 
			if (requestCode == TALKIMG_GET_ALBUM_CODE)
			{ 
				try
				{	// 이미지 리사이징
					File tempfile = getTempFile(tempImage);

					bm_top = BitmapHelper.bitmapDecoder(tempfile.getAbsolutePath(), TALKIMG_LIMIT_SIZE);

					int w =bm_top.getWidth();
					int h = bm_top.getHeight();
					int round = ((w / 4) + (h / 4)) / 2;

					bm_top = MadUtil.getRoundedCornerBitmap(bm_top, round);

					talk_room_exview.setImageBitmap(bm_top);

					saveImagefile(tempImage, 0);
					sendTopImage();
					
				}catch(Exception e){
					LogUtil.D(e.toString());
				}
			}else if(requestCode == TALKIMG_GET_CAMERA_CODE){
				
				tempCameraCropped = Uri.fromFile(getTempFile(tempImage));
				LogUtil.D("tempCameraCropped => " + tempCameraCropped.getPath());
				Intent intent = new Intent("com.android.camera.action.CROP");
				
				if(device.BRAND.equals("VEGA")){
					intent.setComponent(new ComponentName(getApplicationContext(), TalkRoomMakeActivity.class));
				}
				
				intent.setDataAndType(tempCameraCropped, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra("outputX", outputX);
				intent.putExtra("outputY", outputY);
				intent.putExtra("aspectX", aspectX);
				intent.putExtra("aspectY", aspectY);
				intent.putExtra("return-data", true);
				intent.putExtra(Media.MIME_TYPE, "image/png");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraCropped);
				startActivityForResult(intent, TALKIMG_CROP_FROM_CAMERA);

			}else if(requestCode == TALKIMG_CROP_FROM_CAMERA){
				
				LogUtil.D("TALKIMG_CROP_FROM_CAMERA");
				
				try {
					bm_top = BitmapHelper.bitmapDecoder(getTempFile(tempImage).getAbsolutePath(), TALKIMG_LIMIT_SIZE);
				} catch (IOException e) {
					LogUtil.D(e.toString());
				}
				int w =bm_top.getWidth();
				int h = bm_top.getHeight();
				int round = ((w / 4) + (h / 4)) / 2;

				bm_top = MadUtil.getRoundedCornerBitmap(bm_top, round);

				talk_room_exview.setImageBitmap(bm_top);
				saveImagefile(tempImage, 0);
				sendTopImage();
			}else if(requestCode == CONTENTIMG_GET_ALBUM_CODE){
				try{	
					talk_image_add_gallery_close.setVisibility(View.VISIBLE);
					isContentImageUpload = false;
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Uri selectedImage = data.getData();
					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
				
					LogUtil.D("imgpath gallery = " + picturePath);
					if(picturePath == null){
						Toast.makeText(getApplicationContext(), "이미지가 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
					}
					
					cursor.close();
					bm_center = BitmapHelper.bitmapDecoder(picturePath, CONTENTIMG_LIMIT_SIZE);
					saveImagefile(tempImage_content, 1);
					sendContentImage(1);
				}catch(Exception e){
					LogUtil.D(e.toString());
				}
			}else if(requestCode == CONTENTIMG_GET_CAMERA_CODE){
				
				talk_image_add_camera_close.setVisibility(View.VISIBLE);
				isContentImageUpload = false;
				try {
					bm_center = BitmapHelper.bitmapDecoder(getTempFile(tempImage_content).getAbsolutePath(), TALKIMG_LIMIT_SIZE);
					saveImagefile(tempImage_content, 1);
					sendContentImage(0);
				} catch (IOException e) {
					LogUtil.D(e.toString());
				}
			}
		}
	}

	private void saveContent() {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("keywords", talk_room_name.getText().toString());
		params.put("room_img", mTalkRoomImagePath);
		params.put("egg_cnt", String.valueOf(consumeR));
		params.put("title_image", mTalkContentImagePath == null ? "" : mTalkContentImagePath);
		params.put("title", "없음");
		
		String comment_txt =  talk_content.getText().toString();
		comment_txt = comment_txt.replace("::", "");
		comment_txt  =  comment_txt.replace("\n", "::");
		
		params.put("contents",comment_txt);
		
		sendTalkRoomData.getDocument(UrlDef.TALK_MAKE_ROOM, params, null,  new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				if(document.select("ResultTable").select("Result").text().equals("true")){

					String Code = document.select("Code").text();
					if(Code.equals("0")){
						String room_seq = document.select("ROOM_SEQ").text();
						Intent ii = new Intent(TalkRoomMakeActivity.this, TalkInActivity.class);
						ii.putExtra("room_seq",room_seq );
						ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						
						startActivity(ii);
						
						finish();
						
						try {
							if(bm_top != null){
								bm_top.recycle();
							}	
							
							if(bm_center != null){
								bm_center.recycle();
							}	
						} catch (Exception e) {
							LogUtil.D(e.toString());
						}
						
					}else{
						if(Code.equals("-2001")){
							Toast.makeText(getApplicationContext(), "이미 방이 존재합니다.", Toast.LENGTH_LONG).show();
						}else if(Code.equals("-2002")){
							Toast.makeText(getApplicationContext(), "알이 부족합니다.", Toast.LENGTH_LONG).show();
						}else if(Code.equals("-2003")){
							Toast.makeText(getApplicationContext(), "방 유지 시간이 설정되지 않았습니다.", Toast.LENGTH_LONG).show();
						}
					}
				}else{
					Toast.makeText(getApplicationContext(), "방생성에 실패하였습니다.", Toast.LENGTH_LONG).show();
				}
			}
		}, true);

	}
	
	private void changeUploadedImage(int type){
		if(type == 0){
			talk_image_add_camera.setBackgroundResource(R.drawable.button_dark);
			talk_image_add_camera.setPadding(0, 0, 0, 0);
		}else if(type == 1){
			talk_image_add_gallery.setBackgroundResource(R.drawable.button_dark);
			talk_image_add_gallery.setPadding(0, 0, 0, 0);
		}
		
	}

	// 하단 인풋값 부분 체크
	private boolean content_checkInput(){
		if(talk_content.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_LONG).show();
			return false;
		}else{
			if(bm_center == null){
				return true;
			}else{
				if(isContentImageUpload){
					return true;
				}else{
					Toast.makeText(getApplicationContext(), "이미지를 업로드 중입니다.", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
	}
	
	//상단 인풋값 체크
	private boolean top_checkInput() {
		if(talk_room_name.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "키워드를 입력해주세요.", Toast.LENGTH_LONG).show();
			return false;
		}else{
			if(bm_top == null ) {
				Toast.makeText(getApplicationContext(), "이미지를 설정해 주세요.", Toast.LENGTH_LONG).show();
				return false;
			}
			else {
				if(consumeR != 0 && timeR != 0){
					if(isTopImageUpload){
						return true;
					}else{
						Toast.makeText(getApplicationContext(), "Talk 방 이미지를 업로드중입니다.", Toast.LENGTH_LONG).show();
						return false;
					}
				}else{
					Toast.makeText(getApplicationContext(), "Talk 유지 시간을 설정해주세요.", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
	}

	// 상단 이미지 전송
	private void sendTopImage(){
		Hashtable<String, File> files = new Hashtable<String, File>();
		files.put("image1", getTempFile(tempImage));
		topImageSave.getDocument(UrlDef.ROOM_MAKE_IMAGEUP, null, files, new HttpCallBack() {
			
			@Override
			public void onHttpCallBackListener(Document document) {
				String imgPath = document.select("body").text().trim();
				mTalkRoomImagePath = imgPath;
				LogUtil.D("mTalkRoomImagePath : " + mTalkRoomImagePath);
				isTopImageUpload = true;
			}
		}, true);		
	}
	
	// 하단 이미지 전송
	private void sendContentImage(final int type){
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
 		Hashtable<String, File> files = new Hashtable<String, File>();
		files.put("image1", getTempFile(tempImage_content));
		contentImageSave.getDocument(UrlDef.ROOM_MAKE2_IMAGEUP, params, files, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				String imgPath = document.select("body").text().trim();
				mTalkContentImagePath = imgPath;
				LogUtil.D("mTalkContentImagePath : " + mTalkContentImagePath);
				isContentImageUpload = true;
				
				changeUploadedImage(type);
			}
		}, true);
	}
	
	//비트맵 파일로 저장
	private void saveImagefile(String path, int gubun){
		OutputStream out = null;
		try {
			out = new FileOutputStream(getTempFile(path));
			if(gubun == 0){
				bm_top.compress(CompressFormat.PNG, 100, out);
			}else if(gubun == 1){
				bm_center.compress(CompressFormat.PNG, 100, out);
			}
		} catch (Exception e) {         
			LogUtil.D(e.toString());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LogUtil.D(e.toString());
			}
		}
	}

	private File getTempFile(String path){
		File file = new File( Define.AD_TALK_FOLDER + "/" + path );
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
		getRoomRcount.threadStop(); 
		sendTalkRoomData.threadStop();
		topImageSave.threadStop();
		contentImageSave.threadStop();
		
		if(bm_top != null)
			bm_top.recycle();
		if(bm_center != null)
			bm_center.recycle();
		
		if ( talk_image_add_gallery_close != null ){
			talk_image_add_gallery_close.setImageBitmap(null);
		}
		if ( talk_image_add_camera_close != null ){
			talk_image_add_camera_close.setImageBitmap(null);
		}
		
		super.onDestroy();
	}
}