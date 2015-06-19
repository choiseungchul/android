package com.mcproject.net.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.haarman.listviewanimations.view.DynamicListView.Swappable;
import com.mcproject.net.util.LogUtil;

public class SwapableListView extends ListView implements Swappable{

	public SwapableListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public SwapableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public SwapableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void swapItems(int arg0, int arg1) {
		
		LogUtil.D("pos 1 = " + arg0);
		LogUtil.D("pos 2 = " + arg1);
		
	}
}
