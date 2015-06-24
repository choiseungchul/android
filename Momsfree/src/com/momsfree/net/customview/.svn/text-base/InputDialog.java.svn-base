package com.momsfree.net.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.EditText;

import com.momsfree.net.R;

public class InputDialog extends Dialog{

	TextViewNanumGothic mTitle,  mBtn1, mBtn2, input_dialog_content, input_dialog_addr_srch;
	EditText mMessage;
	
	public InputDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		setContentView(R.layout.input_dialog);
		
		mTitle = (TextViewNanumGothic)findViewById(R.id.input_dialog_title);
		mMessage = (EditText)findViewById(R.id.input_dialog_edit);
		mBtn1 = (TextViewNanumGothic)findViewById(R.id.input_dialog_ok);
		mBtn2 = (TextViewNanumGothic)findViewById(R.id.input_dialog_cancel);
		input_dialog_content = (TextViewNanumGothic)findViewById(R.id.input_dialog_content);
		input_dialog_addr_srch = (TextViewNanumGothic)findViewById(R.id.input_dialog_addr_srch);
		
		setBackgroundGrayOver();
	}
	
	public void setBackgroundTransparent(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}
	
	public String getMessage(){
		if(!mMessage.getText().toString().equals("")){
			return mMessage.getText().toString();
		}else{
			return "";
		}
	}
	
	public void setMessage(String msg){
		mMessage.setText(msg);
	}
	
	public void setSearchEvent(View.OnClickListener listener){
		input_dialog_addr_srch.setOnClickListener(listener);
	}
	
	public void setContent(String content){
		input_dialog_content.setText(content);
	}
	
	public void setHint(String hint){
		mMessage.setHint(hint);
	}
	
	public void setTitle(String title){
		mTitle.setText(title);
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
