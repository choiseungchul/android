package com.returndays.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.returndays.ralara.R;


public class SingoDialog extends Dialog{

	TextViewNanumGothic mTitle, mBtn1, mBtn2,input_desc;
	EditText edit_content;
	Activity ctx;
	LayoutInflater inflate;

	public SingoDialog(final Activity context) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);

		ctx = context;
		inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setContentView(R.layout.input_dialog);

		mTitle = (TextViewNanumGothic)findViewById(R.id.dialog_title);
		mBtn1 = (TextViewNanumGothic)findViewById(R.id.dialog_ok);
		mBtn2 = (TextViewNanumGothic)findViewById(R.id.dialog_cancel);
		edit_content = (EditText)findViewById(R.id.edit_content);
		input_desc = (TextViewNanumGothic)findViewById(R.id.input_desc);
		
		input_desc.setText(R.string.room_singo_desc);
		edit_content.setHint(R.string.room_singo_hint);
		mTitle.setText(R.string.room_singo_title);
		
		setBackgroundGrayOver();

		mBtn2.setOnClickListener(new View.OnClickListener (){
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	public void setBackgroundWhite(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}
	
	public String getEditText(){
		return edit_content.getText().toString();
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
}