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

public class SetAlertSoundActivity extends BaseActivity{

	LinearLayout back_btn;
	SettingSoundAdapter mAdap;
	ListView mSetSoundList;
	MediaPlayer mPlayer;
	Button save_btn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_sound);
		
		initUI();
		try {
			initData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	void displayFiles (AssetManager mgr, String path) {
//	    try {
//	        String list[] = mgr.list(path);
//	        if (list != null)
//	            for (int i=0; i<list.length; ++i)
//                {
//                    LogUtil.D("Assets:", path +"/"+ list[i]);
//                    displayFiles(mgr, path + list[i]);
//                }
//	    } catch (IOException e) {
//	    	LogUtil.D("List error:", "can't list" + path);
//	    }
//
//	}
	
	private void initData() throws IOException {
		// TODO Auto-generated method stub

		Field[] fs = R.raw.class.getFields();
		String[] titles = getResources().getStringArray(R.array.alarm_sound);
		
		mPlayer = new MediaPlayer();

		ArrayList<SettingSoundDto> datas = new ArrayList<SettingSoundDto>();
		
//		LogUtil.D("mp3List = " + mp3List.toString());
		
		int count = 0;
		
		for(int i = 0; i < fs.length ; i++){
			
			LogUtil.D("mp3List = " + fs[i].getName());
			
			if(fs[i].getName().contains("alarm_")){
				boolean isSelected = false;
				if(fs[i].getName().equals(Setting.getAlarmSound(getApplicationContext()))){
					isSelected = true;
				}
				datas.add(new SettingSoundDto(titles[count], fs[i].getName(), isSelected ));
				count++;
			}
			
		}
		
		mAdap = new SettingSoundAdapter(SetAlertSoundActivity.this, datas);
		
		mSetSoundList.setAdapter(mAdap);
		LogUtil.D("clickable = " + mSetSoundList.isClickable());
		
		mSetSoundList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View vv, int position,
					long arg3) { 

				SettingSoundDto data = mAdap.getItem(position);
				
				try {
					
					int listCount = mSetSoundList.getChildCount();
					for(int i = 0 ; i < listCount; i++){
						View item = mSetSoundList.getChildAt(i);
						CheckBox _ck = (CheckBox)item.findViewById(R.id.setting_alert_chk);
						
						_ck.setChecked(false);
					}
					
					CheckBox ck = (CheckBox)vv.findViewById(R.id.setting_alert_chk);
					ck.setChecked(true);
					
					Setting.setAlarmSound(getApplicationContext(), data.mp3Path);
					
					LogUtil.D("alarm set = " + Setting.getAlarmSound(getApplicationContext()));
					
					stopPlaying();
					
					Uri audio = Uri.parse("android.resource://" + getPackageName()+ "/raw/" + data.mp3Path);
					
					mPlayer = new MediaPlayer();
					mPlayer.setLooping(false);
					
					mPlayer.setDataSource(getApplicationContext(), audio);
					mPlayer.prepare();
					mPlayer.start();
					
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
	
	private void stopPlaying() {
        if (mPlayer != null) {
        	mPlayer.stop();
        	mPlayer.release();
        	mPlayer = null;
       }
    }

	private void initUI() {
		
		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setVisibility(View.GONE);
		
		mSetSoundList = (ListView)findViewById(R.id.setting_sound_list);
		
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);

		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		stopPlaying();
		super.onBackPressed();
	}
	
}
