package com.mcproject.ytfinder_dev;

import com.mcproject.net.conf.Define;
import com.skplanet.tad.AdInterstitial;
import com.skplanet.tad.AdInterstitialListener;
import com.skplanet.tad.AdView.Slot;

import android.app.Activity;
import android.os.Bundle;

public class AdActivity extends Activity{
	// T�ֵ�
	AdInterstitial adInterstitial;
	AdInterstitialListener mAdInterstitialListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Context�� parameter�� AdView�� ��ü�� �����մϴ�.
		initAd();		
	}
	
	private void initAd(){
		// Context�� parameter�� AdInterstitial��ü�� �����մϴ�.
		if(adInterstitial == null){
			adInterstitial = new AdInterstitial(this);
			// �غ� ������ �߱޹��� ClientId�� ���� �Է��մϴ�.
//			adInterstitial.setClientId(Define.TAD_CLIENT);
			adInterstitial.setClientId("AXT003001");
			
			// ���ϴ� ũ���� Slot�� �����մϴ�.
			adInterstitial.setSlotNo(Slot.INTERSTITIAL);
			// TestMode�� ���մϴ�. true�� ��� test ���� ���ŵ˴ϴ�.
			adInterstitial.setTestMode(true);
			// ���� ����� �� 5�� ���� ������� ������ ���� ��� ���� â�� �ڵ����� ���� �������� �����մϴ�.
			adInterstitial.setAutoCloseWhenNoInteraction(false);
			// ���� ����� �� ���� �׼ǿ� ���� �ٸ� App���� ��ȯ �� ���� â�� �ڵ����� ���� �������� �����մϴ�.
			adInterstitial.setAutoCloseAfterLeaveApplication(false);
			
			mAdInterstitialListener = new AdInterstitialListener() {
				// ���� ��û�ϱ� ���� ȣ���� �˴ϴ�.
				public void onAdWillReceive() { }
				//���� ������ ������ ��� ȣ���� �˴ϴ�.
				public void onAdReceived() { }
				// ������ ���� �ε� �� �� ȣ���� �˴ϴ�.
				public void onAdWillLoad() { }
				// ������ �ε��� �Ϸ�Ǿ��� �� ȣ���� �˴ϴ�.
				public void onAdLoaded() { 
					adInterstitial.showAd();
				}
				// ���� ����, �ε� �������� ���� �� ȣ��ǰ� �ڼ��� ������ errorCode�� �����ϼ���
				public void onAdFailed(ErrorCode errorcode) { }
				// ���������� showAd()�� �� �� ȣ��˴ϴ�
				public void onAdPresentScreen () { }
				// ������ ���� ���� �� ȣ��˴ϴ�.
				public void onAdDismissScreen () { }
				// ������ Ŭ������ ����, �ٸ� ���ø����̼��� ����� ���� ȣ��˴ϴ�.
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
