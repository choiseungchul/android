package com.momsfree.util;

import android.util.Log;

import com.momsfree.net.conf.Define;

public class LogUtil {
	
	public static void D(String msg) {
		D(null, msg);
	}
	
	public static void D(String tag, String msg) {
		if(Define.IS_LOG) {
			if(tag == null) {
				Log.d(Define.TAG, msg);
			} else {
				Log.d(tag, msg);
			}
		}
	}
	
	public static void I(String msg) {
		I(null, msg);
	}
	
	public static void I(String tag, String msg) {
		if(Define.IS_LOG) {
			if(tag == null) {
				Log.i(Define.TAG, msg);
			} else {
				Log.i(tag, msg);
			}
		}
	}
	
	public static void W(String msg) {
		W(null, msg);
	}
	
	public static void W(String tag, String msg) {
		if(Define.IS_LOG) {
			if(tag == null) {
				Log.w(Define.TAG, msg);
			} else {
				Log.w(tag, msg);
			}
		}
	}
	
	
	public static void E(String msg) {
		E(null, msg);
	}
	
	public static void E(String tag, String msg) {
		if(Define.IS_LOG) {
			if(tag == null) {
				Log.e(Define.TAG, msg);
			} else {
				Log.e(tag, msg);
			}
		}
	}
	
	
	
}
