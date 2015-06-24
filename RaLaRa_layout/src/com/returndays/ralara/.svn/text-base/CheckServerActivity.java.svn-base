package com.returndays.ralara;

import com.returndays.customview.TextViewNanumGothic;

import android.os.Bundle;

public class CheckServerActivity extends BaseActivity{

	TextViewNanumGothic server_check_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_server);
		
		String checkTime = getIntent().getExtras().getString("checkTime");
		
		server_check_info = (TextViewNanumGothic)findViewById(R.id.server_check_info);
		server_check_info.setText(String.format(getString(R.string.server_check_text), checkTime));
		
	}
	
}
