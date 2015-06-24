package com.returndays.customview;

import java.util.ArrayList;

import com.returndays.image.ImageLoader;
import com.returndays.ralara.R;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;



public class SlideImageView extends LinearLayout {
	private int curIndex = 0;
	
	ArrayList<String> list = new ArrayList<String>();
	Context mCtx;
	Activity mActivity;
	
	GestureDetector gestureDetector;
	GalleryForOneFling gallery;
	LinearLayout statusContainer;
	ImageLoader imageLoader;
	OnImageClickListner mImageClickListner;
	OnItemChangeListner mItemChangeListner;
	
	public SlideImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOrientation(VERTICAL);
		mCtx = context;
	}
	
	
	
	public void init(Activity activity, ImageLoader imageLoader, int dpWidth, int dpHeight, ArrayList<String> listBitmap) {
		this.removeAllViews();
		mActivity = activity;
		this.imageLoader = imageLoader;
		list = listBitmap;
		gallery = new GalleryForOneFling(mCtx);
		gallery.setLayoutParams(new LayoutParams(
				(int)MadUtil.getDpToPixel(mCtx, dpWidth), 
				(int)MadUtil.getDpToPixel(mCtx, dpHeight)
		));
		gestureDetector = new GestureDetector(onGestureListener);
		gallery.setClickable(true);
		
		gallery.setAdapter(new GalleryAdapter());
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				status(arg2);
				if(mItemChangeListner!=null) {
					mItemChangeListner.itemChangeListner(arg2);
				}
				
				
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		gallery.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return gestureDetector.onTouchEvent(arg1);
			}
		});
		
		
		statusContainer = new LinearLayout(mCtx);
		statusContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		statusContainer.setGravity(Gravity.CENTER);
		statusContainer.setPadding(0, 0, 0, 0);
		//statusContainer.setTop(top)
		
		for(int i=0;i<listBitmap.size();i++) {
			ImageView img = new ImageView(mCtx);
        	img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	img.setPadding(5, 5, 5, 5);
        	statusContainer.addView(img);
		}
		
		this.addView(gallery);
		this.addView(statusContainer);

	}
	
	
	private void status(int index) {
		setCurIndex(index);
		int[] resource = {R.drawable.page_n, R.drawable.page_f};
		for(int i=0;i<statusContainer.getChildCount();i++) {
			ImageView tmp = (ImageView) statusContainer.getChildAt(i);
			tmp.setImageResource(resource[(i==index?1:0)]);
		}
	}
	
	OnGestureListener onGestureListener = new OnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(mImageClickListner!=null) {
				mImageClickListner.imageClickListner(getCurIndex());
				return true;
			}
			return false;
		}
		
		public void onShowPress(MotionEvent e) {}
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {return false;}
		public void onLongPress(MotionEvent e) {}
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {return false;}
		public boolean onDown(MotionEvent e) {return false;}
	};
	
	
	class GalleryAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		public GalleryAdapter() {
			/*TypedArray a = mCtx.obtainStyledAttributes(R.styleable.GalleryTheme);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.GalleryTheme_android_galleryItemBackground, 0);
			a.recycle();*/
		}
		
		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView iv;
			if(arg1 == null) {
				iv = new ImageView(mCtx);
				iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
				iv.setLayoutParams(new Gallery.LayoutParams(
						android.widget.Gallery.LayoutParams.MATCH_PARENT, android.widget.Gallery.LayoutParams.MATCH_PARENT		
				));
				//iv.setBackgroundResource(mGalleryItemBackground);
			} else {
				iv = (ImageView) arg1;
			}
			iv.setTag(list.get(arg0));
			imageLoader.DisplayImage(list.get(arg0), mActivity, iv, ImageView.ScaleType.FIT_CENTER );
			return iv;
		}
	}
	
	public void setOnImageClickListner(OnImageClickListner listner) {
		mImageClickListner = listner;
	}
	
	public void setOnItemChangeListner(OnItemChangeListner listner) {
		mItemChangeListner = listner;
	}
	
	
	public int getCurIndex() {
		return curIndex;
	}



	public void setCurIndex(int curIndex) {
		this.curIndex = curIndex;
	}


	static public interface OnImageClickListner {
		public void imageClickListner(int position);
	}
	
	static public interface OnItemChangeListner {
		public void itemChangeListner(int position);
	}
	
}

class GalleryForOneFling extends Gallery {
	
	public GalleryForOneFling(Context context) {
		super(context);
	}
	public GalleryForOneFling(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		int keyCode;
		if(e2.getX() > e1.getX()) {
			keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
		} else {
			keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(keyCode, null);
		return true;
	}
}

