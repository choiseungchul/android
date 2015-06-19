package com.mcproject.net.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.mcproject.net.dto.UserDto;

public class DeviceUtil {
	
	// TelephonyManager
//	getDeviceId() : IMEI - ����̽� ���̵�
//	getSubscriberId() : IMSI - ��Ż� ���� ���̵�
//	getLine1Number() : MSISDN - ��ȭ ��ȣ
//	getSimCountryIso() : SIM ���� �ڵ�
//	getSimOperator() : �� ���� + MNC (����� ���� �ڵ� + ����� ��Ʈ��ũ �ڵ�)
//	getSimOperatorName() : ���� �̸�
//	getSimSerialNumber() : SIM �Ϸ� ��ȣ
//	getSimState() : SIM ���� (��� ���� �Ѱ�, PIN ��� �ִ��� ��)
//	getVoiceMailNumber() : ���� ���� ��ȣ

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
