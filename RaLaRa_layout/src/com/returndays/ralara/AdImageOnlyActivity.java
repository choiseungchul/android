package com.returndays.ralara;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;
import com.returndays.ralara.util.StringUtil;

public class AdImageOnlyActivity extends BaseActivity{
	Button mImgBtn;
	SlideImageView sv2;
	String ad_seq;
	ArrayList<String> imgUrlList = new ArrayList<String>();
	ImageLoader imageLoader;
	int screenWidth;
	int screenHeight;
	String img01 = "";
	String img02 = "";
	String img03 = "";
	TextViewNanumGothic mloading ;

	HttpDocument  mHttpUtil2, mHttpUtil3 ; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adimagetemp);

		mloading = (TextViewNanumGothic) findViewById(R.id.txt_adimageloading);
		//mloading.setText("dddddddddddddddddddddddddddddd");

		Bundle b = getIntent().getExtras();
		ad_seq = (String) b.get("ad_seq");


		init(); 

		mImgBtn = (Button) findViewById(R.id.go_scratch_btn);
		mImgBtn.setText("닫 기");
		mImgBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mHttpUtil2 = new HttpDocument(this);
		mHttpUtil3 = new HttpDocument(this);

		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("ad_seq", ad_seq);

		mHttpUtil2.getDocument(UrlDef.AD_VIEW_ADD_GOLD_AD, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				//LogUtil.D(document.text());
				if(document.select("Code").text().equals("1")){
					Toast.makeText(getApplicationContext(), "본 광고에서 최대 300골드를 받으셨습니다. 더 이상 골드가 적립되지 않습니다.", Toast.LENGTH_SHORT).show();
				}

			}
		}, false);

		getData();
	}


	private void init() {
		// TODO Auto-generated method stub
		sv2 = (SlideImageView) findViewById(R.id.cv_img1);
		imageLoader = new ImageLoader(this);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;

		screenWidth = (int)	MadUtil.PixelToDp(metrics.densityDpi, screenWidth);
		screenHeight = (int)MadUtil.PixelToDp(metrics.densityDpi, screenHeight);

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
					mloading.setVisibility(View.GONE);
					imgUrlList.clear();

					if(!img01.equals(""))
						imgUrlList.add(img01);

					if(!img02.equals(""))
						imgUrlList.add(img02);

					if(!img03.equals(""))
						imgUrlList.add(img03);
					sv2.init(AdImageOnlyActivity.this, imageLoader, screenWidth, screenHeight, imgUrlList);

					final int childcnt = imgUrlList.size();
					sv2.setOnItemChangeListner(new OnItemChangeListner() {
						@Override
						public void itemChangeListner(int position) {
							if (position== childcnt-1)
								mImgBtn.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		}, true);

	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttpUtil2.threadStop();
		mHttpUtil3.threadStop();
		super.onDestroy();
	}
}
