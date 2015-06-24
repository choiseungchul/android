package com.returndays.ralara.util;

import android.R;
import android.content.Context;
import android.view.View;

import com.returndays.customview.DefaultDialog;

public class DialogUtil {

	public static DefaultDialog alert ;
	
	static public void alert(Context ctx, String msg) {
		alert = new DefaultDialog(ctx);
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		alert.show();
	}

	static public void alert(Context ctx, int msg) {
		alert = new DefaultDialog(ctx);
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(ctx.getString(msg));
		alert.setPositiveButton(ctx.getString(R.string.ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		
		alert.show();
	}

	static public void alert(Context ctx, int msg, View.OnClickListener click) {
		 alert = new DefaultDialog(ctx);
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(ctx.getString(msg));
		alert.setPositiveButton(ctx.getString(R.string.ok), click);
		
		alert.show();
	}

	static public void alert(Context ctx, String msg, View.OnClickListener click) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.ok), click);
		alert.show();
	}


	static public void alert(Context ctx, String title, String msg) {
		alert = new DefaultDialog(ctx);
		
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.ok), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		
		alert.show();
	}

	static public void confirm(Context ctx, String msg, View.OnClickListener yesClick) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		alert.setNegativeButton(ctx.getString(R.string.no), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		
		//alert.show();
	}

	static public void confirm(Context ctx, int msg, View.OnClickListener yesClick) {
		alert = new DefaultDialog(ctx);
		
		alert.setTitle(com.returndays.ralara.R.string.alert);
		alert.setMessage(ctx.getString(msg));
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		
		alert.setNegativeButton(ctx.getString(R.string.no), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(alert != null && alert.isShowing()) {
					alert.dismiss();
				  }
				
			}
		});
		
		alert.show();
	}

	static public void confirm(Context ctx, String title, String msg, View.OnClickListener yesClick) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		alert.setNegativeButton(ctx.getString(R.string.no), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});

		alert.show();
	}

	static public void confirm(Context ctx, String title, String msg,
			View.OnClickListener yesClick, View.OnClickListener noClick) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(title);
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		alert.setNegativeButton(ctx.getString(R.string.no), noClick);
		
		alert.show();
	}

	static public void confirm(Context ctx, int title, int msg, View.OnClickListener yesClick) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(title);
		alert.setMessage(ctx.getString(msg));
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		alert.setNegativeButton(ctx.getString(R.string.no), new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		
		alert.show();
	}
	
	static public void confirm(Context ctx, int title, String msg, View.OnClickListener yesClick, View.OnClickListener noClick) {
		 alert = new DefaultDialog(ctx);
		
		alert.setTitle(ctx.getString(title));
		alert.setMessage(msg);
		alert.setPositiveButton(ctx.getString(R.string.yes), yesClick);
		alert.setNegativeButton(ctx.getString(R.string.no), noClick);
		alert.show();
	}
	
	
}
