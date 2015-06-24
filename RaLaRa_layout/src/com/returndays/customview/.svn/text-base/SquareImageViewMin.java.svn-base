package com.returndays.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageViewMin extends ImageView{

	public SquareImageViewMin(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public SquareImageViewMin(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public SquareImageViewMin(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDetachedFromWindow() {
		setImageDrawable(null);
		super.onDetachedFromWindow();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		
		//Get canvas width and height
	    int w = MeasureSpec.getSize(widthMeasureSpec);
	    int h = MeasureSpec.getSize(heightMeasureSpec);
 
	    w = Math.min(w, h);
	    h = w;
		
		setMeasuredDimension(w, h);
	}

}
