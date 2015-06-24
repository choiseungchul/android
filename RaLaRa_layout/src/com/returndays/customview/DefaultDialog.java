package com.returndays.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.returndays.ralara.R;
import com.returndays.ralara.util.LogUtil;

public class DefaultDialog extends Dialog{

	TextView mTitle, mMessage;
	Button mBtn1, mBtn2;
	boolean isSecondBtn = false;
	
	public DefaultDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.dialog);
		
		mTitle = (TextView)findViewById(R.id.dialog_title);
		mMessage = (TextView)findViewById(R.id.dialog_msg);
		mBtn1 = (Button)findViewById(R.id.dialog_leftbtn);
		mBtn2 = (Button)findViewById(R.id.dialog_rightbtn);
		
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	
	public void setMessage(String message){
		message = message.replaceAll("nn", "\n");
		LogUtil.D("messageeeeeeeeeeeeeeeeeee :"+message);
		mMessage.setText(message);
	}
	
	public void setPositiveButton(String title, View.OnClickListener listener){
		mBtn1.setText(title);
		mBtn1.setOnClickListener( listener);
	}
	
	public void setNegativeButton(String title, View.OnClickListener listener){
		mBtn2.setText(title);
		mBtn2.setOnClickListener( listener);
		isSecondBtn = true;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		if(!isSecondBtn){
			mBtn2.setVisibility(View.GONE);
		}
		super.show();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	


	
}
