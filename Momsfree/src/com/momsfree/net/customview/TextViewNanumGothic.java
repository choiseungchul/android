package com.momsfree.net.customview;

import com.momsfree.net.conf.Define;
import com.momsfree.util.LogUtil;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewNanumGothic extends TextView{

	String asset = Define.FONT_NANUMGOTHIC;

	public TextViewNanumGothic(Context context, AttributeSet set, int Rid) {
		super(context, set, Rid);
		setCustomFont(context, asset);
	}

	public TextViewNanumGothic(Context context, AttributeSet set) {
		super(context, set);
		setCustomFont(context, asset);
	}

	public TextViewNanumGothic(Context context) {
		super(context);
		setCustomFont(context, asset);
	}

	public boolean setCustomFont(Context ctx, String asset) {
		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), asset);  
		} catch (Exception e) {
			LogUtil.D(e.getMessage());
			return false;
		}
		setTypeface(tf);  
		return true;
	}

}

