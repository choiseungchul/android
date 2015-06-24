package com.returndays.ralara;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import com.androidquery.AQuery;
import com.returndays.ralara.util.LogUtil;

public class ImageZoom extends BaseActivity{
	
	WebView mWeb;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.image_zoom);
		
		Bundle b = getIntent().getExtras();
		
		String url = b.getString("url");
		
		LogUtil.D("image zoom url = " + url);
		
		mWeb = (WebView)findViewById(R.id.imagezoom);
		
		AQuery aq = new AQuery(getApplicationContext());
		
		aq.id(mWeb).webImage(url);
		
	}
}
