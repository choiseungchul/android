package com.momsfree.net.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;

import com.momsfree.net.R;

public class FindAddrDialog extends Dialog{

	TextViewNanumGothic mTitle, mBtn1, mBtn2;
	EditText mMessage;
	
	public FindAddrDialog(final Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		setContentView(R.layout.find_addr_dialog);
		
		mTitle = (TextViewNanumGothic)findViewById(R.id.find_addr_title);
		mBtn1 = (TextViewNanumGothic)findViewById(R.id.find_addr_set);
//		mBtn1.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Setting.setUserLocation(context, mTitle.getText().toString());
//				dismiss();
//			}
//		});
		mBtn2 = (TextViewNanumGothic)findViewById(R.id.find_addr_srch);
		
		setBackgroundGrayOver();
	}
	
	public void setLocationBtnListener(View.OnClickListener listener){
		mBtn1.setOnClickListener(listener);
	}
	
	public void setBackgroundTransparent(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}
	
	public void setSrchbtn(View.OnClickListener listener){
		mBtn2.setOnClickListener(listener);
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
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
