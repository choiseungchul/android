package com.returndays.ralara.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.returndays.ralara.conf.Define;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DataDownload extends AsyncTask<String, Integer, Void>{
	ProgressDialog mProgressDialog;
	String defaultPath;
	Handler mHandler;
	public DataDownload(ProgressDialog progress, Handler handler) {
		mProgressDialog = progress;
		mHandler = handler;
	}

	@Override
	protected Void doInBackground(String... params) {
		File folder = new File(Define.AD_APP_FOLDER);
		if(!folder.exists()) folder.mkdirs();

		File f = new File(folder, params[0].substring(params[0].lastIndexOf("/")+1));
		if(f.exists()) return null;

		try{
			InputStream input = null;
			OutputStream out = null;
			HttpURLConnection connection = null;
			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(1000*120);
				connection.connect();

				int lenghtOfFile = connection.getContentLength();
				Message msg = Message.obtain();
				msg.what = 0;
				msg.obj = new Integer(lenghtOfFile);
				mHandler.sendMessage(msg);

				input = url.openStream();
				out = new FileOutputStream(f);
				byte[] b = new byte[4 * 1024];
				int read;
				int total = 0;
				while ((read = input.read(b)) != -1) {
					total += read;
					publishProgress(total);
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
		return null;
	}


	@Override
	protected void onPostExecute(Void result) {
		mHandler.sendEmptyMessage(1);
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setProgress(values[0]);
		

	}
}
