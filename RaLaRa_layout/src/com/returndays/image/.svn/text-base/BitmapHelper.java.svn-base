package com.returndays.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import android.R.string;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log; 

public class BitmapHelper {

	public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap , int roundLevel) {
	
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	
	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = convertDipsToPixels(context, roundLevel);
	
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	
	    return output;
	}
	
	/**
	 * �ʼ����� 
	 * @param context
	 * @param dips
	 * @return
	 */
	public static int convertDipsToPixels(Context context, int dips) {
	
	    final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (dips * scale + 0.5f);
	}
	
	/**
	 * �̹��� ���� 
	 * @param ctx
	 * @param bm
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public static File mediaSave(Context ctx, Bitmap bm, String folder) throws IOException {
		
		File file = null; 
		String dirString = Environment.getExternalStorageDirectory().toString() + folder;
		Date now = new Date();
		String fileName = now.toGMTString().replace("|", "").replace("/", "").replace(" ", "").replace(":", "")+".jpg";

        boolean mExternalStorageWriteable = hasStorage(true,folder);
		if(mExternalStorageWriteable)
		{
			File dir = new File(dirString);
			if(!dir.isDirectory()) dir.mkdirs();
			
			file = new File(dir, fileName);
			OutputStream fOut = new FileOutputStream(file); 
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close(); 
		}else{ 
//		    //FileOutputStream ��ȯ 
//			OutputStream fOut = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
//
//			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//			fOut.flush();
//			fOut.close(); 
//			
//			file = ctx.getFileStreamPath(fileName);
			 
			//file = File.createTempFile("mcb", ".mp3", context.getCacheDir());
			
			file = new File(ctx.getCacheDir(),fileName);
			OutputStream fOut = new FileOutputStream(file); 
			bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close(); 
		}
		
		//Log.d("1", "file save:" + fileName);
		return file;
	    
/**		// ���� �޸� ���� ���� ���� �Ǵ� - ���� 
//		boolean mExternalStorageAvailable = false;
//		boolean mExternalStorageWriteable = false;
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state)) {    
//			// We can read and write the media    
//			mExternalStorageAvailable = true;
//			mExternalStorageWriteable = true;
//		} 
//		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
//		{    // We can only read the media    
//			mExternalStorageAvailable = true;    
//			mExternalStorageWriteable = false;
//		} 
//		else 
//		{    // Something else is wrong. It may be one of many other states, but all we need    
//			//  to know is we can neither read nor write    
//			mExternalStorageAvailable = false;
//			mExternalStorageWriteable = false;
//		}
//		// ���� �޸� ���� ���� ���� �Ǵ� - ����  
 
//		ContentValues values = new ContentValues(9); 
//		values.put(Images.Media.TITLE, file.getName()); 
//		values.put(Images.Media.DISPLAY_NAME, file.getName()); 
//		values.put(Images.Media.DATE_TAKEN, new Date().getTime()); 
//		values.put(Images.Media.MIME_TYPE, "image/jpeg"); 
//		values.put(Images.Media.ORIENTATION, 0);
//		values.put(Images.Media.DATA, fileName);
//		values.put(Images.Media.SIZE, file.length());
//		
//		ContentResolver contentResolver = ctx.getContentResolver(); 
//		contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
**/		
	}

	/**
	 * ���� �޸� ���� ���� Ȯ�� 
	 * @param requireWriteAccess
	 * @param folder
	 * @return boolean
	 */
	public static boolean hasStorage(boolean requireWriteAccess, String folder)
	{
		boolean ret = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			if (requireWriteAccess)
			{
				boolean writable = checkFsWritable(folder);
				ret = writable;
			}
			else
			{
				ret = true;
			}
		}
		else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			ret = true;
		}
		return ret;
	}
	
	/**
	 * ���� ���� ���� ���� üũ
	 * @param folder
	 * @return boolean
	 */
	private static boolean checkFsWritable(String folder)
	{ 
		try
		{ 
			// Create a temporary file to see whether a volume is really writeable.
			// It's important not to put it in the root directory which may have a
			// limit on the number of files.
			String directoryName = Environment.getExternalStorageDirectory().toString() + folder;
			File directory = new File(directoryName);
			if (!directory.isDirectory()) // ȭ�Ͻý��� ���丮�� �ƴϸ� �� 
			{
				if (!directory.mkdirs())
				{
					return false;
				}
			}
			File f = new File(directoryName, ".zen");
			
			// Remove stale file if any
			if (f.exists())
			{
				f.delete();
			}
			if (!f.createNewFile())
			{
				return false;
			}
			f.delete();
			return true;
		}
		catch (IOException ex)
		{
			return false;
		} 
	}
 
	/*public static Bitmap bitmapDecoder(String path, float reqW, float reqH) {
		int i;
		Bitmap bm;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, option);
		option.inJustDecodeBounds = false;
		i = 2;
		
		if(option.outWidth > option.outHeight) {
			while(option.outWidth/i >= reqW) 
				i++;
		} else {
			while(option.outHeight/i >= reqH) 
				i++;
		}
		i--;
		option.inSampleSize = i;
		bm = BitmapFactory.decodeFile(path, option);
		bm = Bitmap.createScaledBitmap(bm, (int)reqW, (int)reqH, true);
		return bm;
	}*/
	
	/**
	 * Ư��������(440) ���� �̹��� ������¡ ó�� �Լ� 
	 * @param path
	 * @param req
	 * @return
	 * @throws IOException 
	 */
	public static Bitmap bitmapDecoder(String path, float req) throws IOException {
		
		int LIMIT_SIZE = 440; 
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, option);
	 	option.inJustDecodeBounds = false; 
	 	
		float w = option.outWidth; 
		float h = option.outHeight;		
	 	
		// ���ø� ���� ���� ��� ���� 
		float ww = option.outWidth; 
		float hh = option.outHeight; 
		
		// ���μ��� ������ �°� ���� 
		float xRate = 0, yRate = 0;
		if(w > req) {
			float tmp = w;
			w = req;
			xRate = w/tmp;
			h = h*(xRate);
		}
		if(h > req) {
			float tmp = h;
			h = req;
			yRate = h/tmp;
			w = w*(yRate);
		}
		
		// ������ ������¡ ���ø� ������ �и�  
		float i = 1;
		
		// ���صǴ� ������� ���� ��� ���� < MOD BY PCK 201.04.12 > 
		if(ww > hh)
		{
			if(LIMIT_SIZE < ww)
			{
				i = LIMIT_SIZE / ww ; 
			}
		} 	
		else if(ww < hh)
		{
			if(LIMIT_SIZE < hh)
			{
				i = LIMIT_SIZE / hh ; 
			}
		} 	  
		else if (ww == hh)
		{
			i = LIMIT_SIZE / hh ; 
		}   

		// ���� ������¡�� �޸� ����� ���� ���� ������� �̸� ������¡ �Ѵ�.
		option.inSampleSize = (int)(1/i); 
  
	    int exifDegree = getExifInfo(path);
		// ������¡ 1 
	    Bitmap bm = BitmapFactory.decodeFile(path, option); 
		// ������¡ 2 
		bm = Bitmap.createScaledBitmap(bm, (int)w, (int)h, true); 
		// �̹��� ȸ����� 
	    bm = rotate(bm, exifDegree);
	    
	    /*
		Bitmap blank = Bitmap.createBitmap((int)req, (int)req, Config.ARGB_8888);
		Canvas canvas = new Canvas(blank);
		Paint paint = new Paint();
		paint.setAlpha(255);
		canvas.drawPaint(paint);
		
		int left = (int)(req-bm.getWidth())/2;
		int top = (int)(req-bm.getHeight())/2;
		int right = left+bm.getWidth();
		int bottom = top+bm.getHeight();
		canvas.drawRGB(255, 255, 255);
		canvas.drawBitmap(bm, null, new Rect(left, top, right, bottom), null);
		return blank;
		*/
	    
		return bm;
	}
	
	/**
	 * �̹��� ȸ������ ��ȸ 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static int getExifInfo(String path) throws IOException {
		
	    // �̹��� ȸ������ ���� 
		ExifInterface exif = new ExifInterface(path); 
	    int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
	    int exifDegree = exifOrientationToDegrees(exifOrientation);
		return exifDegree;
	}
	
	/** 
	 * EXIF������ ȸ��� ��ȯ�ϴ� �޼��� 
	 *  
	 * @param exifOrientation EXIF ȸ�� 
	 * @return ���� ���� 
	 */ 
	public static int exifOrientationToDegrees(int exifOrientation) 
	{ 
	  if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) 
	  { 
	    return 90; 
	  } 
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) 
	  { 
	    return 180; 
	  } 
	  else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) 
	  { 
	    return 270; 
	  } 
	  return 0; 
	} 
	
	/** 
	 * �̹����� ȸ���ŵ�ϴ�. 
	 *  
	 * @param bitmap ��Ʈ�� �̹��� 
	 * @param degrees ȸ�� ���� 
	 * @return ȸ��� �̹��� 
	 */ 
	public static Bitmap rotate(Bitmap bitmap, int degrees) 
	{ 
	  if(degrees != 0 && bitmap != null) 
	  { 
	    Matrix m = new Matrix(); 
	    m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2); 
	     
	    try 
	    { 
	      Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), m, true); 
	      if(bitmap != converted) 
	      { 
	        bitmap.recycle(); 
	        bitmap = converted; 
	      } 
	    } 
	    catch(OutOfMemoryError ex) 
	    { 
	    	return bitmap; 
	      // �޸𸮰� �����Ͽ� ȸ���� ��Ű�� ���� ��� �׳� ���� ��ȯ�մϴ�. 
	    } 
	  } 
	  return bitmap; 
	} 

	
	/*
	public static Bitmap bitmapDecoder(Bitmap bitmap, float reqW) {
		int i;
		Bitmap bm;
		i = 2;
		float reqH = reqW/ bitmap.getWidth() * bitmap.getHeight();
		while(bitmap.getWidth()/i >= reqW && bitmap.getHeight()/i >= reqH) {
			i++;
		}
		i--;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = i;
		
		bm = Bitmap.createScaledBitmap(bitmap, (int)reqW, (int)reqH, true);
		return bm;
	}
	
	public static Bitmap bitmapDecoder(Bitmap bitmap, float reqW, float reqH) {
		int i;
		Bitmap bm;
		i = 2;
		while(bitmap.getWidth()/i >= reqW && bitmap.getHeight()/i >= reqH) {
			i++;
		}
		i--;
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = i;
		
		bm = Bitmap.createScaledBitmap(bitmap, (int)reqW, (int)reqH, true);
		return bm;
	}*/

}