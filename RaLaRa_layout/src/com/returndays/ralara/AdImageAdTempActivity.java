package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.returndays.customview.SlideImageView;
import com.returndays.customview.SlideImageView.OnItemChangeListner;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.image.ImageLoader;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.MadUtil;

public class AdImageAdTempActivity extends BaseActivity {
	Button mImgBtn;
	SlideImageView sv;
	HttpDocument  mHttpUtil2, mHttpUtil3 ; 
	String ad_seq;
	ArrayList<String> imgUrlList = new ArrayList<String>();
	ImageLoader imageLoader;
	int screenWidth;
	int screenHeight;
	String img01 = "";
	String img02 = "";
	String img03 = "";
	TextViewNanumGothic mloading ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adimagetemp);

		mloading = (TextViewNanumGothic) findViewById(R.id.txt_adimageloading);


		mHttpUtil2 = new HttpDocument(this);
		mHttpUtil3 = new HttpDocument(this);

		Bundle b = getIntent().getExtras();
		ad_seq = (String) b.get("ad_seq");

		/*
		if(!DbUtil.checkDataBase(getApplicationContext())) {
			DbUtil.initDbFile(getApplicationContext());
		}
		 */
		init(); 



		mImgBtn = (Button) findViewById(R.id.go_scratch_btn);
		mImgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ScratchCardActivity.class);
				intent.putExtra("ad_seq", ad_seq);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);

			}
		});


	}


	private void init() {
		// TODO Auto-generated method stub
		sv = (SlideImageView) findViewById(R.id.cv_img1);
		imageLoader = new ImageLoader(this);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		screenWidth = (int)	MadUtil.PixelToDp(metrics.densityDpi, screenWidth);
		screenHeight = (int)MadUtil.PixelToDp(metrics.densityDpi, screenHeight);





	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getData();
	}


	private void getData() {


		Hashtable<String, String> params1 = new Hashtable<String, String>();
		params1.put("ad_seq", ad_seq);

		mHttpUtil3.getDocument(UrlDef.AD_VIEW_IMAGE, params1, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				img01 = document.select("AD_IMG1").text();
				img02 = document.select("AD_IMG2").text();
				img03 = document.select("AD_IMG3").text();
				
				if(!img01.equals("")){
					Hashtable<String, String> params2 = new Hashtable<String, String>();
					params2.put("user_seq", Setting.getUserSeq(getApplicationContext()));
					params2.put("ad_seq", ad_seq);

					mHttpUtil2.getDocument(UrlDef.AD_VIEW_VIEW1, params2, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document) {
							String code = document.select("ReturnTable").select("Code").text();
							//Toast.makeText(AdImageAdTempActivity.this, code, Toast.LENGTH_LONG).show();
							if(code.equals("0")){
								if(!document.select("DataTable").text().equals("")){
									String seq = document.select("SEQ").text();

									if(!seq.equals("")){
										mloading.setVisibility(View.GONE);

										imgUrlList.clear();

										if(!img01.equals(""))
											imgUrlList.add(img01);

										if(!img02.equals(""))
											imgUrlList.add(img02);

										if(!img03.equals(""))
											imgUrlList.add(img03);

										sv.init(AdImageAdTempActivity.this, imageLoader, screenWidth, screenHeight, imgUrlList);
										//sv.init(AdImageAdTempActivity.this, imageLoader, 300, 300, imgUrlList);
										final int childcnt = imgUrlList.size();


										sv.setOnItemChangeListner(new OnItemChangeListner() {

											@Override
											public void itemChangeListner(int position) {
												if (position== childcnt-1)
													mImgBtn.setVisibility(View.VISIBLE);
											}
										});


									}else{
										Toast.makeText(AdImageAdTempActivity.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();
										mloading.setVisibility(View.VISIBLE);
										mloading.setText("네트워크 에러가 발생하였습니다. 다시 시도바랍니다..");
									}

								}
							}else if(code.equals("-9998")){
								Toast.makeText(AdImageAdTempActivity.this, R.string.viewedad_msg , Toast.LENGTH_LONG).show();
								mloading.setVisibility(View.VISIBLE);
								mloading.setText(R.string.viewedad_msg);

								finish();

								Intent intents = new Intent(AdImageAdTempActivity.this, AdImageOnlyActivity.class);
								intents.putExtra("ad_seq", ad_seq);
								intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								overridePendingTransition(0, 0);
								startActivity(intents);



							}			
							else{


								Toast.makeText(AdImageAdTempActivity.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();
								mloading.setVisibility(View.VISIBLE);
								mloading.setText("네트워크 에러가 발생하였습니다. 다시 시도바랍니다.");
							}

						}
					}, true);
				}
				
			}
		}, true);

	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil2.threadStop();
		mHttpUtil3.threadStop();
		imgUrlList.clear();
		super.onDestroy();
	}

}
