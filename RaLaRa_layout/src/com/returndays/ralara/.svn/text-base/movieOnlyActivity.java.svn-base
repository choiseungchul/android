package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import com.returndays.customview.SlideImageView.OnItemChangeListner;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class movieOnlyActivity extends Activity {
	String ad_seq, ad_movie;
	HttpDocument mHttp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_activity);

		Bundle b = getIntent().getExtras();
		ad_seq = (String) b.get("ad_seq");
		ad_movie = (String) b.get("ad_movie");
		
		LogUtil.D("=====================================");
		LogUtil.D("ad_movie : "+ ad_movie);
		LogUtil.D("ad_seq : "+ ad_seq);
		LogUtil.D("=====================================");
		
		
		
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|);

		// 생성한 비디오뷰를 선언
		final VideoView videoView = (VideoView) findViewById(R.id.videoView1);

		// 비디오뷰를 커스텀하기 위해서 미디어컨트롤러 객체 생성
		final MediaController mediaController = new MediaController(this);

		// 비디오뷰에 연결
		mediaController.setAnchorView(videoView);
		
		//Uri video = Uri.parse("android.resource://" + getPackageName()+ "/raw/movie1");
		Uri video = Uri.parse(ad_movie);
		videoView.setVideoURI(video);
		videoView.setMediaController(mediaController);
		//비디오뷰를 포커스하도록 지정
		videoView.requestFocus();

		mediaController.setVisibility(View.GONE);
		//videoView.setMediaController(controller)
		//동영상 재생
		//videoView.set
		videoView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//mediaController.setVisibility(View.GONE);
				return false;
			}
		});


		videoView.start();
		videoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				finish();
			}

		});
		

		mHttp = new HttpDocument(this);
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		params.put("ad_seq", ad_seq);

		mHttp.getDocument(UrlDef.AD_VIEW_ADD_GOLD_AD, params, null, new HttpCallBack() {
			@Override
			public void onHttpCallBackListener(Document document) {
				//LogUtil.D(document.text());
				if(document.select("Code").text().equals("1")){
					Toast.makeText(getApplicationContext(), "본 광고에서 최대 300골드를 받으셨습니다. 더 이상 골드가 적립되지 않습니다.", Toast.LENGTH_SHORT).show();
				}
			}
		}, false);
		

	}
	@Override
	public void onStop(){
		super.onStop();
		finish();
	}

	@Override
	public void onPause(){
		super.onPause();
		finish();
	}



	@Override
	protected void onDestroy() {
		LogUtil.D(" onDESTROY------------------------------");
		mHttp.threadStop();
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}


}