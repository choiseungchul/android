package com.returndays.ralara;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.image.BitmapHelper;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.dto.TalkMakeDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;

public class TalkMake2Activity extends BaseActivity {

	RecyclingImageView talker_image, talk_room_img;
	TextView talker_id, talk_starttime;
	EditText talk_content;
	HttpUtil mHttpUtil;
	private Uri mTempImageUri;

	int aspectX = 200, aspectY = 200, outputX = 1024, outputY = 1024;
	int GET_ALBUM_CODE = 1111;
	int GET_CAMERA_CODE = 2222;

	private float LIMIT_SIZE = 1920;
	private String tempImage = "TEMP_TALK_CONTENT_IMAGE.png";
	private String mTalkImagePath = null;
	Bitmap bm;
	private Uri tempCameraCropped;
	private Button save_btn;
	TalkMakeDto data;

	boolean isImageDialog = false;
	boolean isImageUpload = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.talk_make2);

		Bundle d =getIntent().getExtras();

		data =  (TalkMakeDto)d.get("data");

		mHttpUtil = new HttpUtil(getApplicationContext());

		initUI();
		initData();

	}

	private ProgressDialog loagindDialog; // Loading Dialog

	void saveAndViewDialog() {
		/* ProgressDialog */
		if(checkInput()){
			loagindDialog = ProgressDialog.show(this, "로딩중..",
					"Talk방을 생성중입니다...", true, false);

			Thread thread = new Thread(new Runnable() {
				public void run() {
					// 시간걸리는 처리
					saveContent();
				}
			});
			thread.start();
		}

	}

	private void saveContent() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("keywords", data.KEYWORD);
		params.put("room_img", data.ROOM_IMG_PATH);
		params.put("egg_cnt", data.EGG_CNT);
		params.put("title_image", mTalkImagePath == null ? "" : mTalkImagePath);
		params.put("title", "없음");
		params.put("contents", talk_content.getText().toString());

		mHttpUtil.httpExecute(UrlDef.TALK_MAKE_ROOM, params, new HttpListener() {
			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {

				if(result.isSuccess){
					if(xml.tag("ResultTable").tag("Result").text().equals("true")){

						String Code = xml.tag("Code").text();
						if(Code.equals("0")){
							try{
								handler.sendEmptyMessage(0);
							}catch(Exception e){
								e.printStackTrace();
							}

							String room_seq = xml.tag("ROOM_SEQ").text();

							Intent ii = new Intent(TalkMake2Activity.this, TalkInActivity.class);

							ii.putExtra("room_seq",room_seq );

							ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(ii);

							finish();

						}else{
							if(Code.equals("-2001")){
								Toast.makeText(getApplicationContext(), "이미 방이 존재합니다.", Toast.LENGTH_LONG).show();
								if(loagindDialog != null){
									loagindDialog.dismiss();
								}
							}else if(Code.equals("-2002")){
								Toast.makeText(getApplicationContext(), "알이 부족합니다.", Toast.LENGTH_LONG).show();
								if(loagindDialog != null){
									loagindDialog.dismiss();
								}
							}else if(Code.equals("-2003")){
								if(loagindDialog != null){
									loagindDialog.dismiss();
								}
							}
						}

					}
				}else{
					try{
						handler.sendEmptyMessage(0);
					}catch(Exception e){
						e.printStackTrace();
					}
					Toast.makeText(getApplicationContext(), "방생성에 실패하였습니다.", Toast.LENGTH_LONG).show();
				}

			}
		}, true);

	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(loagindDialog != null)
			loagindDialog.dismiss(); // 다이얼로그 삭제
		}
	};

	private void initData() {
		// TODO Auto-generated method stub
		HttpUtil getUser = new HttpUtil(getApplicationContext());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		getUser.httpExecute(UrlDef.USERINFO, params, new HttpListener() {

			@SuppressLint("SimpleDateFormat")
			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {
				// TODO Auto-generated method stub
				if(result.isSuccess){
					if(xml.tag("ResultTable").tag("Result").text().equals("true")){
						String ProfileImage = xml.tag("USER_IMG_PATH").text() + xml.tag("USER_IMG").text() ;
						String USER_NICKNAME = xml.tag("USER_NICKNAME").text();

						ImageOptions opt = new ImageOptions();
						opt.round = 10;
						AQuery aq = new AQuery(getApplicationContext());
						aq.id(talker_image).image(ProfileImage, opt);

						talker_id.setText(USER_NICKNAME);

						SimpleDateFormat sdf = new SimpleDateFormat("M월 dd일");

						talk_starttime.setText(sdf.format(new Date(System.currentTimeMillis())));

					}else{

					}
				}
			}
		}, false);

	}

	private void initUI() {
		// TODO Auto-generated method stub

		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveAndViewDialog();
			}
		});

		talker_image = (RecyclingImageView)findViewById(R.id.talker_image);
		talker_id = (TextView)findViewById(R.id.talker_id);
		talk_starttime = (TextView)findViewById(R.id.talk_starttime);
		talk_content = (EditText)findViewById(R.id.talk_content);
		talk_room_img = (RecyclingImageView)findViewById(R.id.talk_room_img);

		talk_room_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(isImageDialog == false){
					isImageDialog = true;
					
					InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);   

					//키보드를 숨긴다
					imm.hideSoftInputFromWindow(talk_content.getWindowToken(), 0);   

					final String[] gselect = {"갤러리","카메라"};  

					new AlertDialog.Builder(TalkMake2Activity.this)
					.setItems(gselect, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){

								mTempImageUri = Uri.fromFile(getTempFile());

								Intent intent = new Intent(Intent.ACTION_PICK); 
								intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
								//							intent.putExtra("crop", "true");
								//							intent.putExtra( "aspectX", aspectX );
								//							intent.putExtra( "aspectY", aspectY );
								//							intent.putExtra( "outputX", outputX );
								//							intent.putExtra( "outputY", outputY);
								//							intent.putExtra("scale", "true");
								intent.putExtra( MediaStore.EXTRA_OUTPUT, mTempImageUri );
								intent.putExtra( "outputFormat", Bitmap.CompressFormat.PNG.toString() );
								startActivityForResult(intent, GET_ALBUM_CODE);

							}else if(which == 1){
								//mTempImageUri = Uri.fromFile(getTempFile());

								Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 

								startActivityForResult(intent, GET_CAMERA_CODE);
							}

						}

					}).create().show();
				}

			}
		});
	}

	private boolean checkInput(){

		if(talk_content.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_LONG).show();
			return false;
		}else{
			if(bm == null){
				return true;
			}else{
				if(isImageUpload){
					return true;
				}else{
					return false;
				}
			}
		}
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
				try
				{	
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Uri selectedImage = data.getData();

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();


					bm = BitmapHelper.bitmapDecoder(picturePath, LIMIT_SIZE);
					talk_room_img.setImageBitmap( bm);

					saveImagefile();

					Map<String, Object> params = new HashMap<String, Object>();

					File tempfile = getTempFile();

					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("image1", tempfile);

					AQuery aq = new AQuery(getApplicationContext());

					aq.ajax(UrlDef.ROOM_MAKE2_IMAGEUP, params, String.class, new AjaxCallback<String>(){

						public void callback(String url, String str, AjaxStatus status){
							mTalkImagePath = str;
							isImageUpload = true;
						}
					});
					
					isImageUpload = false;

				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(requestCode == GET_CAMERA_CODE){

				String[] projection = { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Thumbnails.DATA };
				String path = "";
				Cursor cursorImages = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
				if (cursorImages != null && cursorImages.moveToLast())
				{
					path = cursorImages.getString(0);
				}
				Bitmap capturedImg = null;
				File tempFile = null;
				try
				{
					capturedImg = BitmapHelper.bitmapDecoder(path, LIMIT_SIZE);
					tempFile = BitmapHelper.mediaSave(this, capturedImg, "/ralara");
					//bm = BitmapHelper.bitmapDecoder(tempFile.getAbsolutePath(), 440);
					// 이미지 저장 
					//file = BitmapHelper.mediaSave(getApplicationContext(), image, "/TodayCody");
					talk_room_img.setImageBitmap( capturedImg);

					//					int cimgWidth = capturedImg.getWidth();
					//					int cimgHeight = capturedImg.getHeight();
					//					
					//					int containerWidth = talk_room_img.getWidth();
					//					talk_room_img.setMinimumHeight(cimgHeight * (cimgWidth / containerWidth));

					Map<String, Object> params = new HashMap<String, Object>();

					LogUtil.D("image file = " + tempFile.getAbsolutePath());

					params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params.put("image1", tempFile);

					AQuery aq = new AQuery(getApplicationContext());

					aq.ajax(UrlDef.ROOM_MAKE_IMAGEUP, params, String.class, new AjaxCallback<String>(){

						public void callback(String url, String str, AjaxStatus status){
							mTalkImagePath = str;
							isImageUpload = true;
						}
					});
					
					isImageDialog = false;
					
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private File getTempFile() {
		File file = new File( Define.AD_FOLDER + "/" + tempImage  );
		try{
			file.createNewFile();
		}
		catch( Exception e ){
			Log.e("kingpig", "fileCreation fail" );
		}
		return file;
	}

	private void saveImagefile(){
		OutputStream out = null;

		try {

			out = new FileOutputStream(getTempFile());
			bm.compress(CompressFormat.PNG, 100, out);

		} catch (Exception e) {         
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(talker_image !=null){
			talker_image.setImageBitmap(null);
		}
		if(talk_room_img !=null){
			talk_room_img.setImageBitmap(null);
		} 
		super.onDestroy();
	}

}
