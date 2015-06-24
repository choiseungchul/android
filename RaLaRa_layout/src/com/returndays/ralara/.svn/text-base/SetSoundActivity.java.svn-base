package com.returndays.ralara;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.returndays.ralara.adapter.SettingSoundAdapter;
import com.returndays.ralara.dto.SettingSoundDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;

public class SetSoundActivity extends BaseActivity{

	ListView mSoundList;
	MediaPlayer mPlayer;
	SettingSoundAdapter mAdap;
	LinearLayout back_btn;
	Button save_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_soundlist);
		
		initUI();
		initData();
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		Field[] fs = R.raw.class.getFields();
		String[] titles = getResources().getStringArray(R.array.logo_sound);
		
		mPlayer = new MediaPlayer();

		ArrayList<SettingSoundDto> datas = new ArrayList<SettingSoundDto>();
		
//		LogUtil.D("mp3List = " + mp3List.toString());
		
		int count = 0;
		
		for(int i = 0; i < fs.length ; i++){
			
			LogUtil.D("mp3List = " + fs[i].getName());
			
			if(fs[i].getName().contains("logosong")){
				boolean isSelected = false;
				if(fs[i].getName().equals(Setting.getLogoSound(getApplicationContext()))){
					isSelected = true;
				}
				datas.add(new SettingSoundDto(titles[count], fs[i].getName(), isSelected ));
				count++;
			}
		}
		
		boolean isNoSound = false;
		if(Setting.getLogoSound(getApplicationContext()).equals("NO")){
			isNoSound = true;
		}
		datas.add(new SettingSoundDto("사용안함", "NO", isNoSound));
		
		mAdap = new SettingSoundAdapter(SetSoundActivity.this, datas);
		
		mSoundList.setAdapter(mAdap);
		LogUtil.D("clickable = " + mSoundList.isClickable());
		
		mSoundList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View vv, int position,
					long arg3) { 

				SettingSoundDto data = mAdap.getItem(position);
				
				try {
					
					int listCount = mSoundList.getChildCount();
					for(int i = 0 ; i < listCount; i++){
						View item = mSoundList.getChildAt(i);
						CheckBox _ck = (CheckBox)item.findViewById(R.id.setting_alert_chk);
						
						_ck.setChecked(false);
					}
					
					CheckBox ck = (CheckBox)vv.findViewById(R.id.setting_alert_chk);
					ck.setChecked(true);
					
					LogUtil.D("alarm set = " + Setting.getLogoSound(getApplicationContext()));
					
					stopPlaying();
					
					if(data.mp3Path.equals("NO")){
						Setting.setLogoSound(getApplicationContext(), "NO");
					}else{
						Setting.setLogoSound(getApplicationContext(), data.mp3Path);
						
						Uri audio = Uri.parse("android.resource://" + getPackageName()+ "/raw/" + data.mp3Path);
						
						mPlayer = new MediaPlayer();
						mPlayer.setLooping(false);
						
						mPlayer.setDataSource(getApplicationContext(), audio);
						mPlayer.prepare();
						mPlayer.start();
					}
					
					LogUtil.D("Logosound : " + data.mp3Path);
					
					
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					LogUtil.D("IllegalArgumentException : " + e.getMessage());
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					LogUtil.D("IllegalStateException : " + e.getMessage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LogUtil.D("IOException : " + e.getMessage());
				}
				
			}
		});
	}

	private void initUI() {
		// TODO Auto-generated method stub
		mSoundList = (ListView)findViewById(R.id.sound_list);
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);

		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	private void stopPlaying() {
        if (mPlayer != null) {
        	mPlayer.stop();
        	mPlayer.release();
        	mPlayer = null;
       }
    }
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		stopPlaying();
		super.onBackPressed();
	}
	
}
