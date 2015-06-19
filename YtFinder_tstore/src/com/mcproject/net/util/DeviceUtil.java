package com.mcproject.net.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.mcproject.net.dto.UserDto;

public class DeviceUtil {
	
	// TelephonyManager
//	getDeviceId() : IMEI - 디바이스 아이디
//	getSubscriberId() : IMSI - 통신사 가입 아이디
//	getLine1Number() : MSISDN - 전화 번호
//	getSimCountryIso() : SIM 국가 코드
//	getSimOperator() : 고객 센터 + MNC (모바일 국가 코드 + 모바일 네트워크 코드)
//	getSimOperatorName() : 서비스 이름
//	getSimSerialNumber() : SIM 일련 번호
//	getSimState() : SIM 상태 (통신 가능 한가, PIN 잠겨 있는지 등)
//	getVoiceMailNumber() : 음성 메일 번호

	public static UserDto getUserInfo(Context ctx){		
		UserDto dto = new UserDto();
		
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);		
		dto.setDevice_id(tm.getDeviceId());
		dto.setLocale(tm.getSimCountryIso());
		
		AccountManager am = (AccountManager) ctx.getSystemService(Context.ACCOUNT_SERVICE);
		Account[] acc =  am.getAccounts();
		for(Account ac : acc){
			if(ac.type.equals("com.google")){
				LogUtil.D("google account name = " + ac.name);
				dto.setGoogle_account(ac.name);
			}
		}
		
		return dto;
	}
	
}
