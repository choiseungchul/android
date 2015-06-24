package com.returndays.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.returndays.ralara.R;


public class ImageLoader
{

	// the simplest in-memory cache implementation. This should be replaced with
	// something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	public static HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
	private Context mCtx;
	public File cacheDir;

	/**
	 * 생성자 : File Pointer 결정
	 * @param context
	 */
	public ImageLoader(Context context)
	{
		// Make the background thead low priority. This way it will not affect
		// the UI performance
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		mCtx = context;
		// Find the dir to save cached images 		// 이미지 쓰기 주석처리 2011.06.13 update by pck
//		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "LazyList");
//		else
//			cacheDir = context.getCacheDir();
//		
//		if (!cacheDir.exists())
//			cacheDir.mkdirs();
	}

	public void DisplayImage(String url, Activity activity, ImageView imageView, ImageView.ScaleType scaleType, int defaultResource)
	{

		imageView.setBackgroundResource(defaultResource);

		if (cache.containsKey(url))
		{
			imageView.setScaleType(scaleType);
			imageView.setImageBitmap(cache.get(url));
		}
		else
		{
			imageView.post(new LoadAnimation(imageView));
			queuePhoto(url, imageView, scaleType);
		}
	}

	public void DisplayImage(String url, Activity activity, ImageView imageView, ImageView.ScaleType scaleType)
	{

		imageView.setBackgroundResource(R.drawable.noimage01_3);

		if (cache.containsKey(url))
		{
			imageView.setScaleType(scaleType);
			imageView.setImageBitmap(cache.get(url));
		}
		else
		{
			imageView.post(new LoadAnimation(imageView));
			queuePhoto(url, imageView, scaleType);
		}
	}
	
	public void DisplayImageByAquery(String url, Activity activity, ImageView imageView, ImageView.ScaleType scaleType)
	{ 
		imageView.setBackgroundResource(R.drawable.noimage01_3);
		imageView.setScaleType(scaleType);
		AQuery aq = new AQuery(activity);
		AQueryUtil.displayImgaeByScalType(aq, imageView, url);
	}
	
	
	public void DisplayImage(int drawableImage, String url, Activity activity, ImageView imageView, ImageView.ScaleType scaleType)
	{

		imageView.setBackgroundResource(drawableImage);

		if (cache.containsKey(url))
		{
			imageView.setScaleType(scaleType);
			imageView.setImageBitmap(cache.get(url));
		}
		else
		{
			imageView.post(new LoadAnimation(imageView));
			queuePhoto(url, imageView, scaleType);
		}
	}

	private void queuePhoto(String url, ImageView imageView, ImageView.ScaleType scaleType)
	{
		// This ImageView may be used for other images before. So there may be
		// some old tasks in the queue. We need to discard them.
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView, scaleType);
		synchronized (photosQueue.photosToLoad)
		{
			photosQueue.photosToLoad.offer(p);
			photosQueue.photosToLoad.notifyAll();
		}

		// start thread if it's not started yet
		if (photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}

	private Bitmap getBitmap(String url, ImageView iv)
	{
 
		try
		{ 
			// I identify images by hashcode. Not a perfect solution, good for the
			// demo.
//			String filename = String.valueOf(url.hashCode()) + ".jpg";
//			File f = new File(cacheDir, filename);
//
//			// from SD cache
//			// Bitmap b = decodeFile(f);
//	 
//			BitmapFactory.Options options1 = new BitmapFactory.Options();
//			options1.inTempStorage = new byte[16*1024];
//			
//			// 이미지 화일이 존재하면 화일로 부터 읽어 들임 
//			// version 1.0 - 1.1.5 decodeStream Error 발생 
//			Bitmap b = BitmapFactory.decodeFile(f.getPath(),options1);
//			if (b != null)
//				return b; 
			
			// from web
			// 화일이 존재하지 앟으면 웹으로부터 읽어 들임 
			
//			Bitmap bitmap = null;
//			InputStream is = new URL(url).openStream();
//			OutputStream os = new FileOutputStream(f);
//			Utils.CopyStream(is, os);
//			
//			is.close();
//			os.close();
//			// bitmap = decodeFile(f);
//			// bitmap = BitmapHelper.bitmapDecoder(f.getPath(), iv.getWidth(),
//			// iv.getHeight());
//			BitmapFactory.Options options2 = new BitmapFactory.Options();
//			options2.inTempStorage = new byte[16*1024]; 
//			
//			// version 1.0 - 1.1.5 decodeStream Error 발생 
//			bitmap = BitmapFactory.decodeFile(f.getPath(),options2);
			
			Bitmap bitmap = null;
			bitmap = getBitmapFromUri(url);
			return bitmap;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 웹으로부터 이미지 가져오기
	 * 
	 * @param uri
	 * @return
	 */
	public static Bitmap getBitmapFromUri(String uri)
	{
		Bitmap bm = null;
		URL url = null;
		URLConnection URLConn = null;
		HttpGet httpRequest = null;
		InputStream instream = null;
		HttpClient httpclient = null;
		try
		{
			url     = new URL(uri) ;
			
			URLConn = url.openConnection(); 
			URLConn.setConnectTimeout(30000);
			URLConn.setReadTimeout(30000);

			httpRequest 		  = new HttpGet(url.toURI());
			httpclient  		  = new DefaultHttpClient();
			
			HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
			HttpEntity   entity   = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			
			// InputStream 가져오기  
			instream 	 = bufHttpEntity.getContent();
			byte[] bytes = new byte[instream.available()];
			BufferedInputStream bin = new BufferedInputStream(instream);
			bin.read(bytes);
			bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // 기존 202 라인 (bitmap size exceeds VM budget)
			 
			/*
			byte[] buffer = new byte[16*1024]; 
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Config.RGB_565;  // RGB4444 // RGB8888
			options.inTempStorage = buffer;
			options.inSampleSize = 4;
			bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			//Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			*/
		}
		catch (Exception e)
		{
			Log.e("Error:", e.getMessage());
		}
		finally
		{
			httpclient.getConnectionManager().shutdown();
			if (instream != null)
			{
				try
				{
					instream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		} 
		return bm; 
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f)
	{
		try
		{
			/*
			 * //decode image size BitmapFactory.Options o = new
			 * BitmapFactory.Options(); o.inJustDecodeBounds = true;
			 * BitmapFactory.decodeStream(new FileInputStream(f),null,o);
			 * 
			 * //Find the correct scale value. It should be the power of 2.
			 * final int REQUIRED_SIZE=70; int width_tmp=o.outWidth,
			 * height_tmp=o.outHeight; int scale=1; while(true){
			 * if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
			 * break; width_tmp/=2; height_tmp/=2; scale*=2; }
			 * 
			 * //decode with inSampleSize BitmapFactory.Options o2 = new
			 * BitmapFactory.Options(); o2.inSampleSize=scale; return
			 * BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			 */

			return BitmapFactory.decodeStream(new FileInputStream(f));
		}
		catch (FileNotFoundException e)
		{
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad
	{
		public String url;
		public ImageView imageView;
		public ImageView.ScaleType scaleType;

		public PhotoToLoad(String u, ImageView i, ImageView.ScaleType s)
		{
			url = u;
			imageView = i;
			scaleType = s;
		}
	}

	PhotosQueue photosQueue = new PhotosQueue();

	public void stopThread()
	{
		photoLoaderThread.interrupt();
	}

	// stores list of photos to download
	class PhotosQueue
	{
		// private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
		private Queue<PhotoToLoad> photosToLoad = new LinkedList<PhotoToLoad>();

		// removes all instances of this ImageView
		public void Clean(ImageView image)
		{
			synchronized (photosToLoad)
			{

				Iterator<PhotoToLoad> it = photosToLoad.iterator();
				while (it.hasNext())
				{
					if (it.next().imageView == image)
					{
						it.remove();
					}
				}
			}

		}
	}

	class PhotosLoader extends Thread
	{
		public void run()
		{
			try
			{
				while (true)
				{
					// thread waits until there are any images to load in the
					// queue
					if (photosQueue.photosToLoad.size() == 0)
						synchronized (photosQueue.photosToLoad)
						{
							photosQueue.photosToLoad.wait();
						}
					if (photosQueue.photosToLoad.size() != 0)
					{ 
						PhotoToLoad photoToLoad;
						Bitmap bmp = null;
						synchronized (photosQueue.photosToLoad)
						{
							photoToLoad = photosQueue.photosToLoad.poll();
						}
						
						try
						{
							bmp = getBitmap(photoToLoad.url, photoToLoad.imageView);
							if (bmp == null) {
								bmp = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.noimage01_3);
							}
	 
							cache.put(photoToLoad.url, bmp);
							//bmp.recycle();
						}
						catch (OutOfMemoryError e)
						{
							clearCache(); 
							//Log.e("1", "가용 메모리가 부족합니다. 앱을 종료 후 다시시작하세요.");
							//bmp.recycle(); 
							e.printStackTrace(); 
						}

						Object tag = photoToLoad.imageView.getTag();
						if (tag != null && ((String) tag).equals(photoToLoad.url))
						{
							BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad.imageView, photoToLoad.scaleType);
							photoToLoad.imageView.post(bd);
//							Log.d("1", "loaded:" + photoToLoad.url);
							// a.runOnUiThread(bd);
						} 						
					}
					if (Thread.interrupted())
						break;
				}
			}
			catch (InterruptedException e)
			{
				// allow thread to exit
			}
		}
	}

	PhotosLoader photoLoaderThread = new PhotosLoader();

	class LoadAnimation implements Runnable
	{
		ImageView imageView;

		public LoadAnimation(ImageView i)
		{
			imageView = i;
		}

		public void run()
		{
			synchronized (imageView)
			{
				if (!imageView.getTag().equals("loaded"))
				{
					imageView.setScaleType(ImageView.ScaleType.CENTER);
					imageView.setImageResource(R.drawable.loading);
					AnimationDrawable loadAnimation = (AnimationDrawable) imageView.getDrawable();
					loadAnimation.start();
				}
			}
		}
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		ImageView imageView;
		ImageView.ScaleType scaleType;

		public BitmapDisplayer(Bitmap b, ImageView i, ImageView.ScaleType s)
		{
			bitmap = b;
			imageView = i;
			scaleType = s;
		}

		public void run()
		{
			synchronized (imageView)
			{ 
				imageView.setTag("loaded");
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(scaleType);
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	public void clearCache()
	{
		// clear memory cache
		cache.clear(); 
		// clear SD cache
		/*
		 * 프로그램종료할때 삭제함. File[] files=cacheDir.listFiles(); for(File f:files)
		 * f.delete();
		 */
	}

}