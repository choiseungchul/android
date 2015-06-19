package com.mcproject.net.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class DownloadTask extends AsyncTask<Void, Integer, Long> {

	public final static int TIME_OUT = 1000 * 60 * 5;
	private final static int BUFFER_SIZE = 1024 * 128;

	private static final String TAG = "DownloadTask";
	private static final boolean DEBUG = true;
	private static final String TEMP_SUFFIX = ".download";

	private File file;
	private File tempFile;
	private String url;
	private RandomAccessFile outputStream;
	private DownloadTaskListener listener;
	private Context context;

	private long downloadSize;
	private long previousFileSize;
	private long totalSize;
	private long downloadPercent;
	private long networkSpeed;
	private long previousTime;
	private long totalTime;
	private Throwable error = null;
	private boolean interrupt = false;

	private final class ProgressReportingRandomAccessFile extends RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {

			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			super.write(buffer, offset, count);
			progress += count;
			publishProgress(progress);
		}
	}

	public DownloadTask(Context context, String url, String path) throws MalformedURLException {

		this(context, url, path, null);
	}

	public DownloadTask(Context context, String url, String path, DownloadTaskListener listener)
			throws MalformedURLException {
		super();
		this.url = url;
		this.listener = listener;
		this.file = new File(path);
		this.tempFile = new File(path + TEMP_SUFFIX);
		this.context = context;
	}

	public String getUrl() {

		return url;
	}

	public boolean isInterrupt() {

		return interrupt;
	}

	public long getDownloadPercent() {

		return downloadPercent;
	}

	public long getDownloadSize() {

		return downloadSize + previousFileSize;
	}

	public long getTotalSize() {

		return totalSize;
	}

	public long getDownloadSpeed() {

		return this.networkSpeed;
	}

	public long getTotalTime() {

		return this.totalTime;
	}

	public DownloadTaskListener getListener() {
		return this.listener;
	}

	@Override
	protected void onPreExecute() {
		previousTime = System.currentTimeMillis();
		if (listener != null)
			listener.preDownload(this);
	}

	@Override
	protected Long doInBackground(Void... params) {
		long result = -1;
		try {
			result = download();
		} catch(Exception e){
			LogUtil.E("DownloadTask 132 " + e.toString());
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {

		if (progress.length > 1) {
			totalSize = progress[1];
			if (totalSize == -1) {
				if (listener != null) {
					LogUtil.E("totalsize = " + totalSize);
					listener.errorDownload(this, error);
				}
			} else {

			}
		} else {
			totalTime = System.currentTimeMillis() - previousTime;
			downloadSize = progress[0];
			downloadPercent = (downloadSize + previousFileSize) * 100 / totalSize;
			networkSpeed = downloadSize / totalTime;
			if (listener != null) {
				listener.updateProcess(this);
			}
		}
	}

	@Override
	protected void onPostExecute(Long result) {

		if (result == -1 || interrupt || error != null) {
			if (DEBUG && error != null) {
				Log.v(TAG, "Download failed." + error.getMessage());
				Toast.makeText(context, R.string.stream_url_failed, Toast.LENGTH_LONG).show();
			}
			if (listener != null) {
				listener.errorDownload(this, error);
			}
			return;
		}
		// finish download
		tempFile.renameTo(file);
		if (listener != null)
			listener.finishDownload(this);
	}

	@Override
	public void onCancelled() {

		super.onCancelled();
		interrupt = true;
	}

	private AndroidHttpClient client;
	private HttpGet httpGet;
	private HttpResponse response;

	private long download() {
		if (DEBUG) {
			Log.v(TAG, "totalSize: " + totalSize);
		}

		/*
		 * check net work
		 */
		 if (!NetworkUtils.isNetworkAvailable(context)) {
			 LogUtil.E("네트워크 연결 안됨");
			 return -1;
		 }

		 /*
		  * check file length
		  */
		 client = AndroidHttpClient.newInstance("DownloadTask");
		 httpGet = new HttpGet(url);
		 try {
			 response = client.execute(httpGet);
		 } catch (IOException e) {
			 LogUtil.E("214 IOException : " + e.toString());
			 if(this.listener != null){
				 this.listener.errorDownload(this, e);
			 }
		 }
		 totalSize = response.getEntity().getContentLength();

//		 if (file.exists() && totalSize == file.length()) {
//			 if (DEBUG) {
//				 Log.v(null, "Output file already exists. Skipping download.");
//			 }
//			 return -1;
//
//		 } else
			 
		 if (tempFile.exists()) {
			 httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
			 previousFileSize = tempFile.length();

			 client.close();
			 client = AndroidHttpClient.newInstance("DownloadTask");
			 try {
				 response = client.execute(httpGet);
			 } catch (IOException e) {
				 LogUtil.E(e.toString());
			 }

			 if (DEBUG) {
				 Log.v(TAG, "File is not complete, download now.");
				 Log.v(TAG, "File length:" + tempFile.length() + " totalSize:" + totalSize);
			 }
		 } 

		 /*
		  * check memory
		  */
		 long storage = StorageUtils.getAvailableStorage();
		 if (DEBUG) {
			 Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
		 }

		 if (totalSize - tempFile.length() > storage) {
			 LogUtil.E("저장 공간 부족");
			 return -1;
		 }
		 
		 // 동영상이 0 바이트일경우
		 if( totalSize == 0 ){
			 return -1;
		 }
		 
		 /*
		  * start download
		  */
		 
		 try {
			 outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");
		 } catch (FileNotFoundException e) {
			 // TODO Auto-generated catch block
			 LogUtil.E(" 265 FileNotFoundException : " + e.getMessage());
		 }

		 publishProgress(0, (int) totalSize);

		 InputStream input;
		 int bytesCopied = 0;
		 
		 try {			
			 input = response.getEntity().getContent();
			 bytesCopied = copy(input, outputStream);
			 if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1 && !interrupt) {
				 throw new IOException("Download incomplete: " + bytesCopied + " != " + totalSize);
			 }

			 if (DEBUG) {
				 Log.v(TAG, "Download completed successfully.");
			 }
		 } catch (IllegalStateException e) {
			 LogUtil.E("284 IllegalStateException : " + e.toString());
			 tempFile.delete();
		 } catch (IOException e) {
			 // TODO Auto-generated catch block
			 LogUtil.E("287 IOException : " + e.toString());
			 tempFile.delete();
		 } catch (NetworkErrorException e) {
			 // TODO Auto-generated catch block
			 LogUtil.E("290 NetworkErrorException : " + e.toString());
			 tempFile.delete();
		 } catch (Exception e){
			 LogUtil.E("292 Exception : " + e.toString());
			 tempFile.delete();
		 }

		 return bytesCopied;

	}

	public int copy(InputStream input, RandomAccessFile out) throws IOException,
	NetworkErrorException {

		if (input == null || out == null) {
			return -1;
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		if (DEBUG) {
			Log.v(TAG, "length" + out.length());
		}

		int count = 0, n = 0;
		long errorBlockTimePreviousTime = -1, expireTime = 0;

		try {

			out.seek(out.length());

			while (!interrupt) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;

				/*
				 * check network
				 */
				 if (!NetworkUtils.isNetworkAvailable(context)) {
					 throw new NetworkErrorException("Network blocked.");
				 }

				 if (networkSpeed == 0) {
					 if (errorBlockTimePreviousTime > 0) {
						 expireTime = System.currentTimeMillis() - errorBlockTimePreviousTime;
						 LogUtil.I("expireTime = " + expireTime);
						 if (expireTime > TIME_OUT) {
							 LogUtil.E("345 connect time out!!!!!");
							 this.listener.errorDownload(this, null);
						 }
					 } else {
						 LogUtil.I("errorBlockTimePreviousTime = " + errorBlockTimePreviousTime);
						 errorBlockTimePreviousTime = System.currentTimeMillis();
					 }
				 } else {
//					 LogUtil.I("networkSpeed is 0");
					 expireTime = 0;
					 errorBlockTimePreviousTime = -1;
				 }
			}
		} finally {
			client.close(); // must close client first
			client = null;
			out.close();
			in.close();
			input.close();
		}
		return count;

	}

}
