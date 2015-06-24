package com.returndays.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.returndays.ralara.R;

public class CheckDialog extends Dialog{

	TextView mTitle, mMessage, dialog_check_text;
	Button mBtn1, mBtn2;
	CheckBox dialog_check;
	boolean isSecondBtn = false;
	
	public CheckDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.check_dialog);
		
		mTitle = (TextView)findViewById(R.id.dialog_title_chk);
		mMessage = (TextView)findViewById(R.id.dialog_msg_chk);
		mBtn1 = (Button)findViewById(R.id.dialog_leftbtn_chk);
		mBtn2 = (Button)findViewById(R.id.dialog_rightbtn_chk);
		dialog_check = (CheckBox)findViewById(R.id.dialog_check);
		dialog_check_text = (TextView)findViewById(R.id.dialog_check_text);
		
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	
	public void setMessage(String message){
		mMessage.setText(message);
	}
	
	public void setCheckBoxText(String text){
		dialog_check_text.setText(text);
	}
	
	public boolean getChecked(){
		return dialog_check.isChecked();
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
