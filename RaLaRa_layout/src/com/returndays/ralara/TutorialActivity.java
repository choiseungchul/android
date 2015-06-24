package com.returndays.ralara;

import com.androidquery.AQuery;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.customview.TextViewNanumGothic;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class TutorialActivity extends BaseActivity{

	ViewPager tutorial_manual;
	RecyclingImageView menual_close;
	CheckBox view_later;
	TextViewNanumGothic view_later_text;
	AQuery aq;
	RecyclingImageView navi_1, navi_2, navi_3, navi_4, navi_5, navi_6, navi_7, navi_8, navi_9;
	String close_gubun = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_main);
		Setting.setIsTutorialView(getApplicationContext(), "Y");
		
		close_gubun = getIntent().getExtras().getString("act");
		
		aq = new AQuery(this);
		
		tutorial_manual = (ViewPager)findViewById(R.id.tutorial_manual);
		
		tutorial_manual.setAdapter(new PagerAdapterClass(this));
		tutorial_manual.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				setBtnNavi(position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		navi_1 = (RecyclingImageView)findViewById(R.id.navi_1);
		navi_2 = (RecyclingImageView)findViewById(R.id.navi_2);
		navi_3 = (RecyclingImageView)findViewById(R.id.navi_3);
		navi_4 = (RecyclingImageView)findViewById(R.id.navi_4);
		navi_5 = (RecyclingImageView)findViewById(R.id.navi_5);
		navi_6 = (RecyclingImageView)findViewById(R.id.navi_6);
		navi_7 = (RecyclingImageView)findViewById(R.id.navi_7);
		navi_8 = (RecyclingImageView)findViewById(R.id.navi_8);
		navi_9 = (RecyclingImageView)findViewById(R.id.navi_9);
	}
	
	private void setBtnNavi(int position){
		aq.id(navi_1).image(R.drawable.ra_m_navi_off);
		aq.id(navi_2).image(R.drawable.ra_m_navi_off);
		aq.id(navi_3).image(R.drawable.ra_m_navi_off);
		aq.id(navi_4).image(R.drawable.ra_m_navi_off);
		aq.id(navi_5).image(R.drawable.ra_m_navi_off);
		aq.id(navi_6).image(R.drawable.ra_m_navi_off);
		aq.id(navi_7).image(R.drawable.ra_m_navi_off);
		aq.id(navi_8).image(R.drawable.ra_m_navi_off);
		aq.id(navi_9).image(R.drawable.ra_m_navi_off);
		
		if(position == 0){
			aq.id(navi_1).image(R.drawable.ra_m_navi_on);
		}else if(position == 1){
			aq.id(navi_2).image(R.drawable.ra_m_navi_on);
		}else if(position == 2){
			aq.id(navi_3).image(R.drawable.ra_m_navi_on);
		}else if(position == 3){
			aq.id(navi_4).image(R.drawable.ra_m_navi_on);
		}else if(position == 4){
			aq.id(navi_5).image(R.drawable.ra_m_navi_on);
		}else if(position == 5){
			aq.id(navi_6).image(R.drawable.ra_m_navi_on);
		}else if(position == 6){
			aq.id(navi_7).image(R.drawable.ra_m_navi_on);
		}else if(position == 7){
			aq.id(navi_8).image(R.drawable.ra_m_navi_on);
		}else if(position == 8){
			aq.id(navi_9).image(R.drawable.ra_m_navi_on);
		}
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
            return 9;
        }
 
        @SuppressLint("NewApi")
		@Override
        public Object instantiateItem(View pager, int position) {
        	LogUtil.D("position = " + position);
            View v = null;
            RecyclingImageView img;
            v = mInflater.inflate(R.layout.tutorial_main_item, null);
        	img =(RecyclingImageView)v.findViewById(R.id.tutorial_img);
            
            if(position==0){
            	aq.id(img).image(R.drawable.ra_m_0);
            }else if(position==1){
            	aq.id(img).image(R.drawable.ra_m_4);
            }else if(position==2){
            	aq.id(img).image(R.drawable.ra_m_5);
            }else if(position==3){
            	aq.id(img).image(R.drawable.ra_m_1);
            }else if(position==4){
            	aq.id(img).image(R.drawable.ra_m_2);
            }else if(position==5){
            	aq.id(img).image(R.drawable.ra_m_3);
            }else if(position==6){
            	aq.id(img).image(R.drawable.ra_m_6);
            }else if(position==7){
            	aq.id(img).image(R.drawable.ra_m_7);
            }else if(position==8){
            	aq.id(img).image(R.drawable.ra_m_8);
            	img.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(close_gubun.equals("adlist")){
							Intent ii = new Intent(getApplicationContext(), AdlistActivity.class);
							ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(ii);
						}else if(close_gubun.equals("setting")){
							Intent ii = new Intent(getApplicationContext(), SettingActivity.class);
							ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(ii);
						}else{
							Intent ii = new Intent(getApplicationContext(), AdlistActivity.class);
							ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(ii);
						}
						
					}
				});
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
    
}
