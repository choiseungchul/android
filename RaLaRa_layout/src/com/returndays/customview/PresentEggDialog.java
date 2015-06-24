package com.returndays.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;

public class PresentEggDialog extends Dialog{

	AQuery aq;
	RecyclingImageView present_egg_plus, present_egg_minus;
	TextView present_title, present_egg_count, present_egg_submit, present_egg_cancel, present_other_desc;
	int MAX_COUNT = 0;
	
	public PresentEggDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.present_egg_dialog);
		
		initUI();
	}
	
	private void initUI() {
		// TODO Auto-generated method stub
		present_title = (TextView)findViewById(R.id.present_title);
		present_egg_count = (TextView)findViewById(R.id.present_egg_count);
		present_egg_count.setText("1");
		present_egg_plus = (RecyclingImageView)findViewById(R.id.present_egg_plus);
		present_egg_plus.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				int curr_cnt = Integer.parseInt(present_egg_count.getText().toString());
				if(curr_cnt + 1 > MAX_COUNT){
					Toast.makeText(getContext(), "더이상 선물할 수 없습니다.", Toast.LENGTH_LONG).show();
				}else{
					present_egg_count.setText(String.valueOf(curr_cnt+1));
				}
			}
		});
		present_egg_minus = (RecyclingImageView)findViewById(R.id.present_egg_minus);
		present_egg_minus.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				int curr_cnt = Integer.parseInt(present_egg_count.getText().toString());
				if(curr_cnt > 0){
					present_egg_count.setText(String.valueOf(curr_cnt - 1));
				}
			}
		});
		
		
		present_egg_submit = (TextView)findViewById(R.id.present_egg_submit);
		present_egg_cancel = (TextView)findViewById(R.id.present_egg_cancel);
		present_egg_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void setTitle(String title){
		present_title.setText(title);
	}
	
	public String getCurrentAmount(){
		return present_egg_count.getText().toString();
	}
		
	
	public void setMaxPresent(int max){
		MAX_COUNT = max;
	}
	
	public void setPresentSubmitEvent(View.OnClickListener listener){
		present_egg_submit.setOnClickListener(listener);
	}
	
//	public void setImage(String url, String sex){
//		if(url != null){
//			aq = new AQuery(getContext());
//			aq.id(present_others_profile_img).image(url, true, true, 0, 0, null, 0, 1.0f);
//		}else{
//			aq = new AQuery(getContext());
//			if(sex.equals("M")){
//				aq.id(present_others_profile_img).image(R.drawable.man_ico);
//			}else if(sex.equals("F")){
//				aq.id(present_others_profile_img).image(R.drawable.woman_ico);
//			}
//		}
//	}

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
