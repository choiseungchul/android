package com.mcproject.ytfinder_dev;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.dto.UserDto;
import com.mcproject.net.util.DeviceUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends Activity{

	EditText c_title, c_content;
	TextView c_submit;
	RequestQueue reqQ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_layout);
	
		reqQ = Volley.newRequestQueue(getApplicationContext());
		
		initUI();
		initData();		
	}

	private void initData() {
		
	}

	private void initUI() {
		c_title = (EditText)findViewById(R.id.c_title);
		c_content = (EditText)findViewById(R.id.c_content);
		
		c_submit = (TextView)findViewById(R.id.c_submit);
		
		c_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				c_submit.setEnabled(false);
				
				if(c_title.getText().toString().equals("")){
					c_submit.setEnabled(true);
					Toast.makeText(getApplicationContext(), R.string.contact_title, Toast.LENGTH_SHORT).show();
					return;
				}
				if(c_content.getText().toString().equals("")){
					c_submit.setEnabled(true);
					Toast.makeText(getApplicationContext(), R.string.contact_msg, Toast.LENGTH_SHORT).show();
					return;
				}
				
				UserDto ud = DeviceUtil.getUserInfo(getApplicationContext());
				Hashtable<String, String> params = new Hashtable<String, String>();
				try {
					params.put("title", URLEncoder.encode( c_title.getText().toString(), "UTF-8") + " - Tstore" );
					params.put("msg", URLEncoder.encode( c_content.getText().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					
				}
				
				params.put("g_a", ud.getGoogle_account());
				String g_param = McUtil.getParams(params);
				
				StringRequest req = new StringRequest(UrlDef.ADD_CONTACT + g_param, new Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.I("add Result = " + response);
						if(response.trim().equals("-1")){
							// ¿À·ù
							c_submit.setEnabled(true);
							Toast.makeText(getApplicationContext(), R.string.contact_error, Toast.LENGTH_SHORT).show();
						}else{
							//Á¢¼öµÊ
							Toast.makeText(getApplicationContext(), R.string.contact_success, Toast.LENGTH_SHORT).show();
							finish();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						LogUtil.E(error.toString());
						Toast.makeText(getApplicationContext(), R.string.contact_error, Toast.LENGTH_SHORT).show();
					}
				});
				
				reqQ.add(req);				
			}
		});
		
	}
	
}
