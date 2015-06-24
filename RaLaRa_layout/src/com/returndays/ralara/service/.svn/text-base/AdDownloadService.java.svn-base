package com.returndays.ralara.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.CallAdDto;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.query.InOutCallAdQuery;
import com.returndays.ralara.util.DbUtil;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class AdDownloadService extends Service {
	File folder;
	private File folder2;
	private static ExecutorService fetchExe;
	public static boolean isDownload;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		//Toast.makeText(getApplicationContext(), "[랄라라]새로운 광고가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		final Context ctx = getBaseContext();
		folder = new File(Define.AD_CALL_FOLDER);
		if(!folder.exists()) folder.mkdirs();
//		folder2 = new File(Define.AD_ADLIST_FOLDER);
//		if(!folder2.exists()) folder2.mkdirs();
		
		if(fetchExe == null) {
			fetchExe = Executors.newFixedThreadPool(4, new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable r) {
					Thread t = new Thread(r);
					t.setPriority(Thread.MIN_PRIORITY);
					return t;
				}
			});
		}
		
		
		if(!DbUtil.checkDataBase(ctx)) {
			DbUtil.initDbFile(ctx);
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startDt = format.format(new Date(System.currentTimeMillis()));
		LogUtil.W("$$$$$$$$$$$ AdDownloadService ("+startDt+") $$$$$$$$$$$");
		
		//Toast.makeText(getApplicationContext(), "[랄라라]새로운 광고를 다운로드 합니다.", Toast.LENGTH_SHORT).show();
		
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
		//LogUtil.W("back service:" + UrlDef.COMPAIGN + Setting.getUserSeq(getApplicationContext()) );
		final HttpDocument httpUtil = new HttpDocument(getApplicationContext());
		httpUtil.getDocument(UrlDef.COMPAIGN, params,null, new HttpCallBack() {
			
			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				if(document != null && document.select("ReturnTable") != null) {
					InOutCallAdQuery.deleteInOutAD(ctx);
					Elements entries = document.select("ReturnTable");
					ArrayList<CallAdDto> datas = new ArrayList<CallAdDto>();
					for(Element entry : entries) {
						CallAdDto dto = new CallAdDto();
						MadUtil.autoMappingXmlToObject(entry, dto);
						datas.add(dto);
						if( Integer.parseInt(dto.AD_KIND) > 100 )
							fetchExe.execute(new Download(dto.IMG1));
						}
					//fetchExe.shutdown();
					int r = entries.size();
					
					LogUtil.I( " R size ============== :  " + r);
					
					// 나이대별 
					if(Setting.getIsFirstSetCallendCategory(getBaseContext()).equals("Y")){
						for(int i=0;i<r;i++) {
							CallAdDto dto = datas.get(i);
							long ret = InOutCallAdQuery.insertInOutAD(getBaseContext(), dto);
							if(Integer.parseInt(dto.AD_KIND) > 100 && Integer.parseInt(dto.AD_KIND) < 200){
								if(!InOutCallAdQuery.isExistAd(getBaseContext(), datas.get(i).AD_SEQ)){
									InOutCallAdQuery.insertAdFlag(getBaseContext(), dto.AD_SEQ, dto.AD_TITLE, dto.AD_TITLE, dto.END_ACTION, "Y");
								}
							}
						}
					}else{
						if(!Setting.getUserAges(ctx).equals("")){
							List<String> AD_SEQ_LIST = MadUtil.getCategoryList(Setting.getUserAges(ctx));
							for(int i=0;i<r;i++) {
								CallAdDto dto = datas.get(i);
								long ret = InOutCallAdQuery.insertInOutAD(getBaseContext(), dto);
								if(Integer.parseInt(dto.AD_KIND) > 100 && Integer.parseInt(dto.AD_KIND) < 200){
									if(!InOutCallAdQuery.isExistAd(getBaseContext(), datas.get(i).AD_SEQ)){
										if(AD_SEQ_LIST.contains(dto.AD_KIND)){
											InOutCallAdQuery.insertAdFlag(getBaseContext(), dto.AD_SEQ, dto.AD_TITLE, dto.AD_TITLE, dto.END_ACTION, "Y");
										}else{
											InOutCallAdQuery.insertAdFlag(getBaseContext(), dto.AD_SEQ, dto.AD_TITLE, dto.AD_TITLE, dto.END_ACTION, "N");
										}
									}
								}
							}
							Setting.setIsFirstSetCallendCategory(ctx, "Y");
						}else{
							for(int i=0;i<r;i++) {
								CallAdDto dto = datas.get(i);
								long ret = InOutCallAdQuery.insertInOutAD(getBaseContext(), dto);
								if(Integer.parseInt(dto.AD_KIND) > 100 && Integer.parseInt(dto.AD_KIND) < 200){
									if(!InOutCallAdQuery.isExistAd(getBaseContext(), datas.get(i).AD_SEQ)){
										InOutCallAdQuery.insertAdFlag(getBaseContext(), dto.AD_SEQ, dto.AD_TITLE, dto.AD_TITLE, dto.END_ACTION, "Y");
									}
								}
							}
						}
					}
					
					onDestroy();
				}
				httpUtil.threadStop();
			}
		}, false);
	}
	
	

	private Integer[] random(int size) {
		ArrayList<Integer> Test = new ArrayList<Integer>();
		ArrayList<Integer> Test2 = new ArrayList<Integer>();
		int MaxSize = size;
		for(int i=0;i<MaxSize;i++){
			Test.add(new Integer(i));
		}
		for(int i=0;i<MaxSize;i++){
			int random = (int)(Math.random()*Test.size());
			Test2.add(Test.get(random));
			Test.remove(random);
		}
		return Test2.toArray(new Integer[]{});
	}

	class Download implements Runnable {
		private String mUrl;
		public Download(String url) {
			mUrl = url;
		}

		@Override
		public void run() {
			File folder = new File(Define.AD_CALL_FOLDER);
			if(!folder.exists()) folder.mkdirs();
			
			File f = new File(folder, mUrl.substring(mUrl.lastIndexOf("/")+1));
			
			if(f.exists()){
				Log.d("skip", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				return ;
			}

			try{
				InputStream input = null;
				OutputStream out = null;
				HttpURLConnection connection = null;
			
			
				try {
					URL url = new URL(mUrl);
					connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(1000*120);
					connection.connect();
	
					input = url.openStream();
					out = new FileOutputStream(f);
					byte[] b = new byte[4 * 1024];
					int read;
					while ((read = input.read(b)) != -1) {
						out.write(b, 0, read);
					}
					
					
					input.close();
					out.close();
	
					
	
				} catch(Exception e) {
					e.printStackTrace();
				}finally{
					try {
	                    if (out != null)
	                    	out.close();
	                    if (input != null)
	                        input.close();
	                } 
	                catch (IOException ignored) { }

	                if (connection != null)
	                    connection.disconnect();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

	}


}
