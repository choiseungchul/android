package com.mcproject.ytfinder_dev;

import com.mcproject.net.conf.Define;
import com.skplanet.tad.AdInterstitial;
import com.skplanet.tad.AdInterstitialListener;
import com.skplanet.tad.AdView.Slot;

import android.app.Activity;
import android.os.Bundle;

public class AdActivity extends Activity{
	// T애드
	AdInterstitial adInterstitial;
	AdInterstitialListener mAdInterstitialListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Context를 parameter로 AdView의 객체를 생성합니다.
		initAd();		
	}
	
	private void initAd(){
		// Context를 parameter로 AdInterstitial객체를 생성합니다.
		if(adInterstitial == null){
			adInterstitial = new AdInterstitial(this);
			// 준비 과정에 발급받은 ClientId를 직접 입력합니다.
//			adInterstitial.setClientId(Define.TAD_CLIENT);
			adInterstitial.setClientId("AXT003001");
			
			// 원하는 크기의 Slot을 설정합니다.
			adInterstitial.setSlotNo(Slot.INTERSTITIAL);
			// TestMode를 정합니다. true인 경우 test 광고가 수신됩니다.
			adInterstitial.setTestMode(true);
			// 광고가 노출된 후 5초 동안 사용자의 반응이 없을 경우 광고 창을 자동으로 닫을 것인지를 설정합니다.
			adInterstitial.setAutoCloseWhenNoInteraction(false);
			// 광고가 노출된 후 랜딩 액션에 인해 다른 App으로 전환 시 광고 창을 자동으로 닫을 것인지를 설정합니다.
			adInterstitial.setAutoCloseAfterLeaveApplication(false);
			
			mAdInterstitialListener = new AdInterstitialListener() {
				// 광고를 요청하기 전에 호출이 됩니다.
				public void onAdWillReceive() { }
				//광고 전문을 수신한 경우 호출이 됩니다.
				public void onAdReceived() { }
				// 수신한 광고를 로딩 할 때 호출이 됩니다.
				public void onAdWillLoad() { }
				// 광고의 로딩이 완료되었을 때 호출이 됩니다.
				public void onAdLoaded() { 
					adInterstitial.showAd();
				}
				// 광고 수신, 로드 과정에서 실패 시 호출되고 자세한 내용은 errorCode를 참조하세요
				public void onAdFailed(ErrorCode errorcode) { }
				// 삽입형광고가 showAd()가 될 때 호출됩니다
				public void onAdPresentScreen () { }
				// 삽입형 광고가 닫힐 때 호출됩니다.
				public void onAdDismissScreen () { }
				// 광고의 클릭으로 인해, 다른 애플리케이션이 실행될 때에 호출됩니다.
				public void onAdLeaveApplication () { }
			};
			
			adInterstitial.setListener(mAdInterstitialListener);
			adInterstitial.loadAd();
		}		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(adInterstitial != null){
			adInterstitial.destroyAd();
		}
		super.onDestroy();
		
	}
}
