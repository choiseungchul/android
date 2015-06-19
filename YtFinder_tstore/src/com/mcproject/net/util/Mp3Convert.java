package com.mcproject.net.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mcproject.net.conf.Define;
import com.mcproject.net.ffmpeg.Utils;
import com.mcproject.ytfavorite_t.R;

@Deprecated
public class Mp3Convert {

	Context ctx;
	private String mFfmpegInstallPath;

	public Mp3Convert(Context ctx){
		this.ctx = ctx;

		installFfmpeg();
	}

	public void saveMp3(final String in, final String out){
		//		final FfmpegJob job = new FfmpegJob(mFfmpegInstallPath);
//		final String cmd = mFfmpegInstallPath + " -vn -acodec copy -i "+in+" " + out;

		final String cmd = mFfmpegInstallPath + " -i "+in+" -vn -acodec libmp3lame -ar 44.1k -ac 2 -ab 128k " + out;

		LogUtil.I("convert cmd = " + cmd);

		File ffmpegFile = new File(ctx.getCacheDir(), "ffmpeg");
		if(ffmpegFile.exists()){
			LogUtil.I("ffmpeg exist!!");
		}
		
		Runtime runtime = Runtime.getRuntime();
		try{
			Process p = runtime.exec(cmd);
			
			LogUtil.I("process run");
			
			int rs = p.waitFor();
			
			LogUtil.I("process result = " + rs);
			
//			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//			String line = null;
//			while ((line = br.readLine())!=null){
//				Log.d("Test",line);
//			}
//			br.close();

		}catch(Exception e){
			LogUtil.E(e.toString());
		}

//		new AsyncTask<Void, Void, Void>() {
//
//			@Override
//			protected Void doInBackground(Void... arg0) {
//
//				LogUtil.I("start process");
//				
//				Runtime runtime = Runtime.getRuntime();
//				try{
//					Process p = runtime.exec(cmd);
//					
//					LogUtil.I("process run");
//					
//					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
//					String line = null;
//					while ((line = br.readLine())!=null){
//						Log.d("Test",line);
//					}
//					br.close();
//
//				}catch(Exception e){
//					Log.e("Test",e.toString());
//				}
//				
//				LogUtil.I("process close");
//				
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Void result) {
//				Toast.makeText(ctx, "Ffmpeg job complete.", Toast.LENGTH_SHORT).show();
//			}
//
//		}.execute();
	}

	private void installFfmpeg() {
		File ffmpegFile = new File(ctx.getCacheDir(), "ffmpeg");
		mFfmpegInstallPath = ffmpegFile.toString();
		LogUtil.D("ffmpeg install path: " + mFfmpegInstallPath);

		if (!ffmpegFile.exists()) {
			try {
				ffmpegFile.createNewFile();
			} catch (IOException e) {
				LogUtil.E("Failed to create new file!" + e.toString());
			}
			Utils.installBinaryFromRaw(ctx, R.raw.ffmpeg, ffmpegFile);
		}
		
		ffmpegFile.setReadable(true);
		ffmpegFile.setWritable(true);
		ffmpegFile.setExecutable(true);
	}

}
