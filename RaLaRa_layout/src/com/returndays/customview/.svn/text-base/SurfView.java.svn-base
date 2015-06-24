package com.returndays.customview;

import com.returndays.ralara.R;
import com.returndays.ralara.util.LogUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SurfView extends  SurfaceView implements Callback {


	public Bitmap mBack;
	SurfaceHolder mHolder;
	DrawThread mThread;

	public SurfView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SurfView(Context context, AttributeSet attrs) {
		super(context, attrs);


		// TODO Auto-generated constructor stub
	}

	public SurfView(Context context , int mwidth, int mheight) {
		super(context);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false;

		//mBack = BitmapFactory.decodeResource(context.getResources(), R.drawable.ad_background_1, options );
		mBack = Bitmap.createScaledBitmap(mBack, mwidth, mheight, true);


		mHolder = getHolder();
		mHolder.addCallback(this);
	}





	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mThread != null) {
			mThread.SizeChange(width, height);
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread = new DrawThread(mHolder);
		mThread.start();



	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mThread.bExit = true;
		for (;;) {
			try { 
				mThread.join();
				break;
			} 
			catch (Exception e) {;}
		}

	}

	class DrawThread extends Thread {
		boolean bExit;
		int mWidth, mHeight;
		SurfaceHolder mHolder;

		DrawThread(SurfaceHolder Holder) {
			mHolder = Holder;
			bExit = false;
		}

		public void SizeChange(int Width, int Height) {
			mWidth = Width;
			mHeight = Height;
		}



		// 스레드에서 그리기를 수행한다.
		public void run() {
			Canvas canvas;

			synchronized(mHolder) {

				canvas = mHolder.lockCanvas();
				LogUtil.D("시작했어요~~~~~~~");
				canvas.drawColor(Color.BLACK);
				canvas.drawBitmap(mBack, 0, 0, null );
				LogUtil.D("대단해요~~~~~~~");	
				mHolder.unlockCanvasAndPost(canvas);
			}


		}
	}


}
