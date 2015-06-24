package com.returndays.ralara.http;

import com.androidquery.util.XmlDom;
import com.returndays.ralara.dto.HttpResultDto;

public interface HttpListener {
	public void onSuccess(XmlDom xml, HttpResultDto result);
}
