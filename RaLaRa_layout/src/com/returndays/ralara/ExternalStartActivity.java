package com.returndays.ralara;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ExternalStartActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = new Intent(getApplicationContext(), SplashActivity.class);
		startActivity(i);
		finish();
		overridePendingTransition(0, 0);
		
	}
	
}
