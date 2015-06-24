package com.momsfree.net.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.momsfree.net.R;

public class DefaultDialog extends Dialog{

	TextViewNanumGothic mTitle, mMessage, mBtn1, mBtn2, dialog_addr_area;
	
	public DefaultDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		setContentView(R.layout.dialog);
		
		mTitle = (TextViewNanumGothic)findViewById(R.id.dialog_title);
		mMessage = (TextViewNanumGothic)findViewById(R.id.dialog_content);
		mBtn1 = (TextViewNanumGothic)findViewById(R.id.dialog_ok);
		mBtn2 = (TextViewNanumGothic)findViewById(R.id.dialog_cancel);
		dialog_addr_area = (TextViewNanumGothic)findViewById(R.id.dialog_addr_area);
		setBackgroundGrayOver();
	}
	
	public void setAddrMsg(String addr){
		dialog_addr_area.setVisibility(View.VISIBLE);
		dialog_addr_area.setText(addr);
	}
	
	public void setBackgroundWhite(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
	}
	
	public void setMessage(String message){
		//message = message.replaceAll("nn", "\n");
		
		mMessage.setText(message);
	}
	
	public void isNegativeButton(boolean flag){
		if(!flag){
			mBtn2.setVisibility(View.GONE);
		}else{
			mBtn2.setVisibility(View.VISIBLE);
		}
	}
	
	public void setPositiveButton(String title, View.OnClickListener listener){
		mBtn1.setText(title);
		mBtn1.setOnClickListener( listener);
	}
	
	public void setNagativeButton(String title, View.OnClickListener listener){
		mBtn2.setText(title);
		mBtn2.setOnClickListener( listener);
	}
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	


	
}
