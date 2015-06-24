package com.returndays.customview;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.R;
import com.returndays.ralara.preference.Setting;

public class TutorialDialog extends Dialog{

	ViewPager tutorial_container;
	RecyclingImageView tutorial_close;
	CheckBox view_later;
	RecyclingImageView go_1,go_2,go_3,go_4,go_5;
	TextViewNanumGothic view_later_text;
	AQuery aq;
	
	public TutorialDialog(final Context context) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		aq = new AQuery(context);
		
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA010101));
		
		setContentView(R.layout.tutorial_dialog);
		
		tutorial_container = (ViewPager)findViewById(R.id.tutorial_container);
		
		tutorial_container.setAdapter(new PagerAdapterClass(context));
		tutorial_container.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setImageReset(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		View.OnClickListener close_listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Setting.setIsTutorialView(context, "N");
				dismiss();
			}
		};
		
//		view_later_text = (TextViewNanumGothic)findViewById(R.id.view_later_text);
//		view_later_text.setOnClickListener(close_listener);
//		view_later = (CheckBox)findViewById(R.id.view_later);
//		view_later.setOnClickListener(close_listener);
		
		tutorial_close = (RecyclingImageView)findViewById(R.id.tutorial_close);
		tutorial_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		go_1 = (RecyclingImageView)findViewById(R.id.go_1);
		go_2 = (RecyclingImageView)findViewById(R.id.go_2);
		go_3 = (RecyclingImageView)findViewById(R.id.go_3);
		go_4 = (RecyclingImageView)findViewById(R.id.go_4);
		go_5 = (RecyclingImageView)findViewById(R.id.go_5);
		
	}
	
	 /**
     * PagerAdapter 
     */
    private class PagerAdapterClass extends PagerAdapter{
         
        private LayoutInflater mInflater;
 
        public PagerAdapterClass(Context c){
            super();
            mInflater = LayoutInflater.from(c);
        }
        
        @Override
        public int getCount() {
            return 5;
        }
 
        @SuppressLint("NewApi")
		@Override
        public Object instantiateItem(View pager, int position) {
            View v = null;
            RecyclingImageView img;
            v = mInflater.inflate(R.layout.tutorial_image_item, null);
        	img =(RecyclingImageView)v.findViewById(R.id.tuto_image);
            
            if(position==0){
            	img.setBackgroundResource(R.drawable.tuto1);
            }else if(position==1){
            	img.setBackgroundResource(R.drawable.tuto2);
            }else if(position==2){
            	img.setBackgroundResource(R.drawable.tuto3);
            }else if(position==3){
            	img.setBackgroundResource(R.drawable.tuto4);
            }else if(position==4){
            	img.setBackgroundResource(R.drawable.tuto5);
            }
             
            ((ViewPager)pager).addView(v, 0);
             
            return v; 
        }
 
        @Override
        public void destroyItem(View pager, int position, Object view) {    
            ((ViewPager)pager).removeView((View)view);
        }
 
        @Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
        @Override public Parcelable saveState() { return null; }
        @Override public void startUpdate(View arg0) {}
        @Override public void finishUpdate(View arg0) {}

		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			// TODO Auto-generated method stub
			 return pager == obj; 
		}
    }
    
    private void setImageReset(int position){
    	aq.id(go_1).background(R.drawable.page_dot);
    	aq.id(go_2).background(R.drawable.page_dot);
    	aq.id(go_3).background(R.drawable.page_dot);
    	aq.id(go_4).background(R.drawable.page_dot);
    	aq.id(go_5).background(R.drawable.page_dot);
    	if(position == 0){
    		aq.id(go_1).background(R.drawable.page_dot_over);
    	}else if(position == 1){
    		aq.id(go_2).background(R.drawable.page_dot_over);
    	}else if(position == 2){
    		aq.id(go_3).background(R.drawable.page_dot_over);
    	}else if(position == 3){
    		aq.id(go_4).background(R.drawable.page_dot_over);
    	}else if(position == 4){
    		aq.id(go_5).background(R.drawable.page_dot_over);
    	}
    }
    
    @Override
    public void onDetachedFromWindow() {
    	
    	super.onDetachedFromWindow();
    }
    
}
