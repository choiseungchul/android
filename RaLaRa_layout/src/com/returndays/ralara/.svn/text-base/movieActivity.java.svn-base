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

public class movieActivity extends Activity {
	String ad_seq, ad_movie;
	HttpDocument mHttpUtil2;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_activity);

		Bundle b = getIntent().getExtras();
		ad_seq = (String) b.get("ad_seq");
		ad_movie = (String) b.get("ad_movie");
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|);

		// 생성한 비디오뷰를 선언
		final VideoView videoView = (VideoView) findViewById(R.id.videoView1);

		// 비디오뷰를 커스텀하기 위해서 미디어컨트롤러 객체 생성
		final MediaController mediaController = new MediaController(this);

		// 비디오뷰에 연결
		mediaController.setAnchorView(videoView);
		// 안드로이드 res폴더에 raw폴더를 생성 후 재생할 동영상파일을 넣습니다.
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
				mediaController.hide();
				return false;
			}
		});


		mHttpUtil2 = new HttpDocument(this);

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

							videoView.start();
							videoView.setOnCompletionListener(new OnCompletionListener() {
								@Override
								public void onCompletion(MediaPlayer arg0) {

									Intent intent = new Intent(getApplicationContext(), ScratchCardActivity.class);
									intent.putExtra("ad_seq", ad_seq);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									finish();
									overridePendingTransition(0, 0);
									startActivity(intent);
								}

							});
						}else{
							Toast.makeText(movieActivity.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();

						}
					}
				}else if(code.equals("-9998")){
					Toast.makeText(movieActivity.this, R.string.viewedad_msg , Toast.LENGTH_LONG).show();
					
					
					
					Intent intents = new Intent(movieActivity.this, movieOnlyActivity.class);
					intents.putExtra("ad_seq", ad_seq);
					intents.putExtra("ad_movie", ad_movie);
					intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					overridePendingTransition(0, 0);
					startActivity(intents);	
					
					finish();
				}
				else{
					Toast.makeText(movieActivity.this, R.string.networkfail_msg2 , Toast.LENGTH_LONG).show();

				}

			}
		}, true);


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
		mHttpUtil2.threadStop();
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}


}