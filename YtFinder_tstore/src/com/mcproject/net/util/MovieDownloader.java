package com.mcproject.net.util;

import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.conf.Define;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.download.DownloadTask;
import com.mcproject.net.download.DownloadTaskListener;
import com.mcproject.net.download.StorageUtils;
import com.mcproject.net.dto.ProgressDto;
import com.mcproject.ytfavorite_t.R;

public class MovieDownloader {

	private ArrayList<String> url;
	private String mvId, mvName;
	Context ctx;
	
	private NotificationManager mNotifyManager;
	private Builder mBuilder;
	
	long delay = 1000;
	long period = 500;
	ArrayList<String> streamurls = new ArrayList<String>();
	
	// web에서 url 얻어오는 시간
	int timeout = 1000 * 60;
	// web에서 url 얻어올때 재시도 횟수
	int ret_count = 5;
	
	ProgressDto dto = new ProgressDto();
	
	public MovieDownloader(Context ctx, ArrayList<String> url, String mvId, String mvName){
		this.ctx = ctx;
		this.url = url;
		this.mvId = mvId;
		this.mvName = mvName;
		
		mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(ctx);	
		
		dto.percent = 0;
		dto.speed = 0;
	}
	
	public void start(){
		// start FileDownload
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
		
		if(streamurls.size() > 0)
			download(streamurls.get(0));
		else{
			finalDownload();
		}
		
	}

	private void download(String url){
		
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
		
		File mp3file = new File(Define.APP_MOVIE_FOLDER + mvName + ".mp4");
		// mime type 구분하기
		String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mp3file).toString());
	    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		LogUtil.I("movie mimetype = " + mimetype);
		notificationIntent.setDataAndType(Uri.fromFile(mp3file), mimetype);

	    PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
	    
		final int ID = (int) (Math.random() * 100000000);
		
		mBuilder.setContentTitle("Movie Download")
		    .setContentText("Download in progress.........")
		    .setSmallIcon(R.drawable.noti_icon);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setOngoing(true);
		
		mNotifyManager.notify(ID, mBuilder.build());
		
		final TimerTask downloadProgTask = new TimerTask() {
			@Override
			public void run() {
				mBuilder.setContentText( "download progress ... " + dto.percent + "% / speed = " + dto.speed + "KB");
				mNotifyManager.notify(ID, mBuilder.build());
			}
		};
		
		final Timer downloadProg = new Timer("mv_download_"+ID);
		
		downloadProg.schedule(downloadProgTask, delay, period);
		
		try {
			DownloadTask dt = new DownloadTask(ctx, url, Define.APP_MOVIE_FOLDER + mvName + ".mp4", new DownloadTaskListener() {
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
					mBuilder.setOngoing(false);
					mBuilder.setAutoCancel(true);
					mNotifyManager.notify(ID, mBuilder.build());
					LogUtil.I("download finish");
					
					McUtil.mediaScan(ctx, Define.APP_MOVIE_FOLDER + mvName + ".mp4");
				}

				@Override
				public void errorDownload(DownloadTask task, Throwable error) {
					downloadProgTask.cancel();
					downloadProg.cancel();					
					dto = new ProgressDto();
					
					task.cancel(true);
					mNotifyManager.cancel(ID);
					
					if(streamurls.size() > 1){						
						streamurls.remove(0);
						download(streamurls.get(0));			
					}else{
						finalDownload();
					}					
				}
			});
			dt.execute();
		} catch (MalformedURLException e) {
			LogUtil.E(e.toString());
		} catch (Exception e) {
			LogUtil.E(e.toString());
		}
	}
	
	private void finalDownload() {
		Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
		
		final File mp3file = new File(Define.APP_MOVIE_FOLDER + mvName + ".mp4");
		// mime type 구분하기
		String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(mp3file).toString());
	    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
		LogUtil.I("movie mimetype = " + mimetype);
		notificationIntent.setDataAndType(Uri.fromFile(mp3file), mimetype);

	    PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
	            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		final int ID = (int) (Math.random() * 100000000);
		
		mBuilder = new NotificationCompat.Builder(ctx);
		mBuilder.setContentTitle("Video Download")
		    .setContentText("Download in progress.........")
		    .setSmallIcon(R.drawable.noti_icon);
		mBuilder.setContentIntent(pendingIntent);
		mBuilder.setOngoing(true);
		
		mNotifyManager.notify(ID, mBuilder.build());
		
		RequestQueue q = Volley.newRequestQueue(ctx);
		
		StringRequest req = new StringRequest(UrlDef.GET_STREAM_URL + "?mvId=" + mvId, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				// 오류
				if(response.contains("ERROR")){					
					mBuilder.setOngoing(false);
					mBuilder.setContentText("Not Found Download URL");
					mNotifyManager.notify(ID, mBuilder.build());
				}else{
					// 같은 이름의 파일이있다면 날짜를 붙임
					String downloadFileName = Define.APP_MOVIE_FOLDER + mvName + ".mp4";
					if(mp3file.exists()){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date d = new Date();
						String todayTime = sdf.format(d);
						downloadFileName = Define.APP_MOVIE_FOLDER + mvName + "_" + todayTime + ".mp4";
					}
					
					Request dnReq = new DownloadManager.Request(Uri.parse(response));
					
					dnReq.setTitle("다운로드 중...");
					dnReq.setDescription(mvName);
					dnReq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mvName + ".mp4");
					
					DownloadManager dnMgr = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
					
					dnMgr.enqueue(dnReq);
									
					mBuilder.setOngoing(false);
					//mBuilder.setContentText("Download Error.. Please try again");
					mNotifyManager.cancelAll();
					
					// 다운로드 시작
//					downloadProg.schedule(downloadProgTask, delay, period);	
//					
//					LogUtil.D("movie dn url = " + response);
//					
//					try {
//						DownloadTask dt = new DownloadTask(ctx, response, downloadFileName, new DownloadTaskListener() {
//							@Override
//							public void updateProcess(DownloadTask task) {
//								dto.percent = (int) task.getDownloadPercent();
//								dto.speed = task.getDownloadSpeed();
//							}
//							@Override
//							public void preDownload(DownloadTask task) {
//							}
//							@Override
//							public void finishDownload(DownloadTask task) {
//								downloadProgTask.cancel();
//								downloadProg.cancel();
//								
//								mBuilder.setContentText("Download complete");
//								mBuilder.setAutoCancel(true);
//								mBuilder.setOngoing(false);
//								mNotifyManager.notify(ID, mBuilder.build());
//								LogUtil.I("download finish");
//								
//								McUtil.mediaScan(ctx, Define.APP_MOVIE_FOLDER + mvName + ".mp4");
//							}
//
//							@Override
//							public void errorDownload(DownloadTask task, Throwable error) {
//								downloadProgTask.cancel();
//								downloadProg.cancel();	
//								
//								task.cancel(true);					
//								mBuilder.setOngoing(false);
//								mBuilder.setContentText("Download Error.. Please try again");
//								mNotifyManager.notify(ID, mBuilder.build());
//							}
//						});
//						dt.execute();
//					} catch (MalformedURLException e) {
//						LogUtil.E(e.toString());
//					} catch (Exception e) {
//						LogUtil.E(e.toString());
//					}
				}			
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		});
		
		// url 얻어오는 시간이 좀 길 수 있으니 타임아웃 시간을 늘린다.		
		req.setRetryPolicy(new DefaultRetryPolicy(
				timeout, ret_count, 
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		
		q.add(req);	
	}
}
