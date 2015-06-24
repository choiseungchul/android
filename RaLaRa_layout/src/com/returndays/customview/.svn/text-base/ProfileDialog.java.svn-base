package com.returndays.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.returndays.ralara.R;

public class ProfileDialog extends Dialog{

	SquareImageView other_profile_image;
	TextView other_profile_desc, present_to_others;
	AQuery aq;
	
	public ProfileDialog(Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
		
		setContentView(R.layout.profile_dialog);
		
		other_profile_image = (SquareImageView)findViewById(R.id.other_profile_image);
		other_profile_desc  = (TextView)findViewById(R.id.other_profile_desc);
		present_to_others = (TextView)findViewById(R.id.present_to_others);
		
	}
	
	public void setPresentOther(View.OnClickListener listener){
		present_to_others.setVisibility(View.VISIBLE);
		present_to_others.setOnClickListener(listener);
		present_to_others.setEnabled(true);
		present_to_others.setClickable(true);
	}
	
	public void setImage(String url){
		if(url != null){
			aq = new AQuery(getContext());
			aq.id(other_profile_image).image(url, true, true, 0, 0, null, 0, 1.0f);
		}else{
			aq = new AQuery(getContext());
			aq.id(other_profile_image).image(R.drawable.man_ico);
		}
	}
	
	public void setImageDrawable(Drawable drawable){
		aq = new AQuery(getContext());
		aq.id(other_profile_image).image(drawable);
	
	}
	
	public void setDesc(String desc){
		other_profile_desc.setText(desc);
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
