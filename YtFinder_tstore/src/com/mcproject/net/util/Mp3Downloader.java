package com.mcproject.net.util;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.mcproject.net.conf.AppUserSettings;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.download.DownloadTask;
import com.mcproject.net.download.DownloadTaskListener;
import com.mcproject.net.download.StorageUtils;
import com.mcproject.net.dto.ProgressDto;
import com.mcproject.ytfavorite_t.R;

public class Mp3Downloader {

	private String mvId, mvName;
	Context ctx;
	
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	
	long delay = 1000;
	long period = 500;
	
	ProgressDto dto = new ProgressDto();

	public Mp3Downloader(Context ctx, String mvId, String mvName){
		this.ctx = ctx;
		this.mvId = mvId;
		this.mvName = mvName;
		
		mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(ctx);	
		
		dto.percent = 0;
		dto.speed = 0;
	}
	
	public void start(){
		File folder_movie = new File(Define.APP_MOVIE_FOLDER);
		if(!folder_movie.exists()) folder_movie.mkdirs();
		
		// 스토리지 체크
		if(!StorageUtils.checkAvailableStorage()){
			Toast.makeText(ctx, ctx.getString(R.string.mv_download_storage_error1), Toast.LENGTH_LONG).show();
			return;
		}
		if(!StorageUtils.isSdCardWrittenable()){
			Toast.makeText(ctx, ctx.getString(R.string.mv_download_storage_error2), Toast.LENGTH_LONG).show();
			return;
		}
		
		download();
	}

	private void download() {
		
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
		
		final File mp3file = new File(Define.APP_MOVIE_FOLDER + mvName + ".mp3");
		// mime type 구분하기
		String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mp3file).toString());
	    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		LogUtil.I("mp3 mimetype = " + mimetype);
		notificationIntent.setDataAndType(Uri.fromFile(mp3file), mimetype);

	    PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
	            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
	    final int ID = (int) (Math.random() * 100000000);
		
		mBuilder = new NotificationCompat.Builder(ctx);
		mBuilder.setContentTitle("Audio Download")
		    .setContentText("Download in progress.........")
		    .setSmallIcon(R.drawable.noti_icon);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setOngoing(true);
		
		mNotifyManager.notify(ID, mBuilder.build());
		
		final TimerTask downloadProgTask = new TimerTask() {
			@Override
			public void run() {
				mBuilder.setContentText( "download progress ... " + dto.percent + "% / speed = " +dto.speed + "KB");
				mNotifyManager.notify(ID, mBuilder.build());
			}
		};
		
		final Timer downloadProg = new Timer("mp3_download_"+ID);
		
		// 같은 이름의 파일이있다면 날짜를 붙임
		String downloadFileName = Define.APP_MOVIE_FOLDER + mvName + ".mp3";
		if(mp3file.exists()){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = new Date();
			String todayTime = sdf.format(d);
			downloadFileName = Define.APP_MOVIE_FOLDER + mvName + "_" + todayTime + ".mp3";
		}
		
		// 다운로드 시작
		downloadProg.schedule(downloadProgTask, delay, period);	
		
		String bitrate = AppUserSettings.getAudioBitrate(ctx);
		if(bitrate.equals("")){
			bitrate = "128";
		}
		
		String url = UrlDef.GET_MP3_FROM_SERVER + "?videoid="+ mvId + "&bitrate=" + bitrate;
		
		try {
			DownloadTask dt = new DownloadTask(ctx, url, downloadFileName, new DownloadTaskListener() {
				@Override
				public void updateProcess(DownloadTask task) {
					dto.percent = (int) task.getDownloadPercent();
					dto.speed = task.getDownloadSpeed();
				}
				@Override
				public void preDownload(DownloadTask task) {
				}
				@Override
				public void finishDownload(DownloadTask task) {
					downloadProgTask.cancel();
					downloadProg.cancel();
					
					mBuilder.setContentText("Download complete");
					mBuilder.setAutoCancel(true);
					mBuilder.setOngoing(false);
					mNotifyManager.notify(ID, mBuilder.build());
					LogUtil.I("download finish");
					
					McUtil.mediaScan(ctx, Define.APP_MOVIE_FOLDER + mvName + ".mp3");
				}

				@Override
				public void errorDownload(DownloadTask task, Throwable error) {
					downloadProgTask.cancel();
					downloadProg.cancel();	
					
					task.cancel(true);					
					mBuilder.setOngoing(false);
					mBuilder.setContentText("Download Error.. Please try again");
					mNotifyManager.notify(ID, mBuilder.build());
				}
			});
			dt.execute();
		} catch (MalformedURLException e) {
			LogUtil.E(e.toString());
		} catch (Exception e) {
			LogUtil.E(e.toString());
		}
	}
}
