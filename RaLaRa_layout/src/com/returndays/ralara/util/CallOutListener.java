package com.returndays.ralara.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Handler;
import android.os.Message;



public class CallOutListener implements Runnable {
	private boolean isConnected = false;
	private boolean stop = false;
	private BufferedReader bufferedreader;
	private enum Type {DIALING, ALERTING, ACTIVE};
	private ConnectListener mListener;


	public CallOutListener() {}

	public CallOutListener(ConnectListener listener) {
		mListener = listener;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(mListener != null) 
					mListener.onConnected();
				break;
			}
		}

	};

	static public interface ConnectListener {
		public void onConnected();
	}

	public void Stop() {
		stop = true;
		try {
			bufferedreader.close();
		} catch (Exception e) {}
	}

	public boolean IsConnected() {
		return isConnected;
	}

	@Override
	public void run() {
		InputStream inputstream;
		try {

			Process process = Runtime.getRuntime().exec("logcat -v time -b radio");
			inputstream = process.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(
					inputstream);
			bufferedreader = new BufferedReader(inputstreamreader);
			String str = "";
			long tempTime = 0;
			Type preType = null;
			
			
			long unsolCdmaTime = 0;
			int cnt = 0;
			while (!stop && (str = bufferedreader.readLine()) != null) {
				LogUtil.I("BBB", str);
				
				
				if(str.contains("UNSOL_CDMA_INFO_REC") && preType != null && preType == Type.ACTIVE) {
					
					if(unsolCdmaTime == 0) {
						unsolCdmaTime = System.currentTimeMillis();
					}
					/**
					 * 갤럭시 s3 자동응답 까지 테스트 완료..
					 * GET_CURRENT_CALLS로그가 통화 전까지만 찍히고 전화 받을댄 안찍힘..
					 * 그래서 그 후 찍히는 로그중 UNSOL_CDMA_INFO_REC과 조합하여 판별함.
					 * 다른단말은 될지 모르겠음.
					 */
					LogUtil.W("CCC", (System.currentTimeMillis() - unsolCdmaTime) + ", " + (System.currentTimeMillis()-tempTime));
					
					if(cnt > 0) {
						//발신 이후에 상대방이 받으면 로그가 2개 찍히므로 이건 받은거라고 볼 수 있다.
						if(cnt >= 2) {
							isConnected = true;
							mHandler.sendEmptyMessage(1);
							Stop();
						}
						cnt++;
					}
					//발신이 됐을때 이 로직을 탄다.. 
					if(System.currentTimeMillis() - unsolCdmaTime > 100 && System.currentTimeMillis()-tempTime > 100 && cnt == 0) {
						cnt++;
					} 
					
					
					tempTime = System.currentTimeMillis();
					unsolCdmaTime = System.currentTimeMillis();
				}
				
				
				if(str.contains("GET_CURRENT_CALLS")) {
					if(str.contains(Type.DIALING.toString())) {

						if(tempTime ==0) {
							tempTime = System.currentTimeMillis();
						}
						LogUtil.I("AAA", str+"("+(System.currentTimeMillis()-tempTime)+")");
						tempTime = System.currentTimeMillis();
						preType = Type.DIALING; 

					} else if(str.contains(Type.ALERTING.toString())) {

						if(tempTime ==0) {
							tempTime = System.currentTimeMillis();
						}
						LogUtil.D("AAA", str+"("+(System.currentTimeMillis()-tempTime)+")");
						preType = Type.ALERTING; 

						tempTime = System.currentTimeMillis();
					} else if(str.contains(Type.ACTIVE.toString())) {

						if(tempTime ==0) {
							tempTime = System.currentTimeMillis();
						}

						LogUtil.W("AAA", str+"("+(System.currentTimeMillis()-tempTime)+")");
						
						
						/**********************************************************
						 * 로그 찍히는 순서 DIALING->ALERTING->ACTIVE 가 일반적이지만.. 
						 * 시작순서는 지맘대로고 몇번씩 찍힐지는 알 수 없음..
						 * 
						 * 이전 상태가 ALERTING 이면서
						 * 이전 상태값과 현재 상태값의 로그 시간 차이가
						 * 1초이상 나면 상대방이 응답해서 ACTIVE 로그가 찍혔다고 
						 * 볼수 있다. (테스트 결과 ARS역시 1초 이내에 로그 안찍힘.. 약간 간당간당.. 시간을 더 줄여볼까..-_-)
						 * 
						 * 테스트된 단말기- 모토로이(모토모라), 갤럭시S1(삼성), HTC레이더 4g(HTC)
						 * 
						 * 죽어도 안되는 단말기 - 갤럭시탭(삼성.. 이새끼는 병신임.. 상대방이 응답해도 어떠한 로그도 찍히지 않음.)
						 **********************************************************/
						if(System.currentTimeMillis()-tempTime > 1000 && preType != null && preType == Type.ALERTING) {
							LogUtil.W("*******************Connected**************************");
							isConnected = true;
							mHandler.sendEmptyMessage(1);
							Stop();
						}
						tempTime = System.currentTimeMillis();
						preType = Type.ACTIVE; 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
