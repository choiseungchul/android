package com.returndays.image;

import java.io.File;

import android.view.View; 
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.androidquery.AQuery; 
import com.returndays.ralara.R;
 

public class AQueryUtil
{
	/**
	 * 이미지 디스플레이( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */
	public static void displayListImgaeFileCache(AQuery aq,View imageView,String imageUrl)
	{ 
		aq.id(imageView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f);		
	}
	
	/**
	 * 이미지 디스플레이( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */
	public static void displayListImgaeFileCache(AQuery aq,View imageView,String imageUrl,int imageSize)
	{ 
		aq.id(imageView).image(imageUrl, true, true, imageSize, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f);		
	}
	
	/**
	 * 이미지 디스플레이( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */
	public static void displayImgaeByScalType(AQuery aq,View imageView,String imageUrl)
	{ 
		aq.id(imageView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK,AQuery.RATIO_PRESERVE);
	}
	
	/**
	 * 이미지 디스플레이 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgaeFileCache(AQuery aq,View imageView,View progressView,String imageUrl)
	{ 
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f);
	}
	
	/**
	 * 이미지 디스플레이 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgaeFileCache(AQuery aq,View imageView,View progressView,String imageUrl,int imageSize)
	{ 
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, imageSize, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f);
	}
	
	/**
	 * 이미지 디스플레이 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgaeFileCache100(AQuery aq,View imageView,View progressView,String imageUrl)
	{ 
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, 150, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f); 
	}
	
	/**
	 * 이미지 디스플레이 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgae(AQuery aq,View imageView,View progressView,String imageUrl)
	{ 
		
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f);
	}
	
	/**
	 * 이미지 디스플레이+ 클릭 리스너 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */
	public static void displayListImgaeFileCache(AQuery aq,View imageView,String imageUrl, OnClickListener onClick)
	{ 
		//aq.id(imageView).progress(R.id.progress).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f).clicked(onClick);
		aq.id(imageView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f).clicked(onClick);
	} 
	
	/**
	 * 이미지 디스플레이+ 클릭 리스너 포함 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgaeFileCache(AQuery aq,View imageView,View progressView,String imageUrl, OnClickListener onClick)
	{ 
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f).clicked(onClick);
	}
	
	/**
	 * 이미지 디스플레이+ 클릭 리스너 포함 + 추가 프로그래스 포함 ( boolean memCache : false, boolean fileCache : true, int targetWidth : 0, int fallbackId : 0, Bitmap preset : null, int animId : AQuery.FADE_IN_NETWORK )
	 * @param aq
	 * @param imageView
	 * @param imageUrl
	 */                   
	public static void displayListImgaeFileCache(AQuery aq,View imageView,View progressView,String imageUrl, OnClickListener onClick,int imageSize)
	{ 
		aq.id(imageView).progress(progressView).image(imageUrl, true, true, imageSize, 0, null, AQuery.FADE_IN_NETWORK, 1.0f/1.0f).clicked(onClick);
	}
 
	/**
	 * 텍스트 뷰 텍스트 세팅 
	 * @param aq
	 * @param textView
	 * @param textVal
	 */
	public static void setText(AQuery aq,TextView textView,CharSequence textVal)
	{ 
		aq.id(textView).text(textVal);
	} 
	
	/**
	 * 텍스트 뷰 세팅 및 백그라운드 컬러 변경   
	 * @param aq
	 * @param textView
	 * @param textVal
	 * @param color
	 */
	public static void setTextBackColor(AQuery aq,TextView textView,String textVal,int color)
	{ 
		aq.id(textView).text(textVal).backgroundColor(color);
	} 
	
	/**
	 * 뷰 활성화 
	 * @param aq
	 * @param textView
	 * @param textVal
	 */
	public static void setVisibleView(AQuery aq,View view)
	{ 
		aq.id(view).visible();
	} 
	
	/**
	 * 뷰 비활성화 
	 * @param aq
	 * @param textView
	 * @param textVal
	 */
	public static void setInisibleView(AQuery aq, View view)
	{ 
		aq.id(view).invisible();
	} 
	
	/**
	 * 뷰 삭제 
	 * @param aq
	 * @param textView
	 * @param textVal
	 */
	public static void setGoneView(AQuery aq, View view)
	{ 
		aq.id(view).gone();
	} 
	
	/**
	 * Click 리스너 바인딩 
	 * @param aq
	 * @param textView
	 * @param textVal
	 */
	public static void setOnClick(AQuery aq, View view,OnClickListener onClick)
	{ 
		aq.id(view).clicked(onClick);
	} 
}
