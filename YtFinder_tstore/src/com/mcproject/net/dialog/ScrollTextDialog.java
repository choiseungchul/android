package com.mcproject.net.dialog;

import com.mcproject.ytfavorite_t.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

public class ScrollTextDialog extends Dialog{

	Context ctx;
	TextView title;
	TextView content;
	TextView close_btn;
	
	public ScrollTextDialog(Context context) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA000000));
		
		ctx = context;
		
		setContentView(R.layout.scroll_text_dialog);
		
		initUI();	
	}

	private void initUI() {
		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.content);
		close_btn = (TextView)findViewById(R.id.close_btn);
		
		close_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScrollTextDialog.this.dismiss();
			}
		});
	}
	
	public void setTitle(int resId){
		title.setText(resId);
	}
	
	public void setTitle(String text){
		content.setText(text);
	}
	
	public void setContent(int resId){
		content.setText(resId);
	}
	
	public void setContent(String text){
		content.setText(text);
	}

}
