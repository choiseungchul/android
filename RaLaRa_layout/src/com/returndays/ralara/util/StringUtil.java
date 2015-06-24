package com.returndays.ralara.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 
 * @(#) Id: StringUtil.java, v 1.0 2012. 5. 21. 오후 12:39:24 espata Exp 
 * Revision: 1.0 
 * Date: 2012. 5. 21. 오후 12:39:24 
 * 
 * ===================================
 * LG전자 한국영업 신물류 프로젝트
 * 
 * Copyright (c) 2003-2012 by LG CNS, Inc.
 * All rights reserved.
 *
 * DLS Mobile Android 
 * 문자열 관련 유틸리티
 * 
 * @author 곽상동 (kwaksd2011@gmail.com)
 * @version Revision: 1.0 Date: 2012. 5. 21. 오후 12:39:24
 */
public class StringUtil {	

	/**
	 * 문자열내 공백 제거
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 6. 7.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */
	public static String removeSpace(String data) {
		String result = "";
		int nSize = data.length();

		for (int i = 0; i < nSize; i++) {
			char c = data.charAt(i);

			if (c != ' ') {
				result += c;
			}
		}

		return result;
	}	

	/**
	 * 문자열 ,(comma) 제거
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 24.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */

	public static String removeComma(String data)
	{
		if (data == null || data.equals("")) {
			return "";
		}

		return (data.replace(",", "")).replace(" ", "").trim();
	}

	/**
	 * 문자열 ,(comma) 제거
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 24.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */

	public static String removeComma(String data, String ifNullReplaceWithString)
	{
		if (data == null || data.equals("")) {
			return ifNullReplaceWithString;
		}

		return (data.replace(",", "")).replace(" ", "").trim();
	}

	/**
	 * 문자열 ,(comma) 제거
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 24.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */

	public static String removeComma(CharSequence data)
	{
		if (data == null || data.toString().equals("")) {
			return "";
		}
		String value = data.toString();

		return (value.replace(",", "")).replace(" ", "").trim();
	}

	/**
	 * 문자열 ,(comma) 제거
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 24.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */

	public static String removeComma(CharSequence data, String ifNullReplaceWithString)
	{
		if (data == null || data.toString().equals("")) {
			return ifNullReplaceWithString;
		}
		String value = data.toString();

		return (value.replace(",", "")).replace(" ", "").trim();
	}

	/**
	 * String left pad
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 12.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */
	public static String getLPad(String input, int pad_len, String pad_char) {
		int len = input.length();
		String return_value = "";

		// pad length 보다 작을 때
		if (len < pad_len) {
			for (int i = 0; i < pad_len - len; i++) {
				return_value = pad_char + return_value;
			}
			return (return_value + input);
		} else {
			//pad length 보다 같거나 클때
			return input.substring(len - pad_len, len);
		}
	}

	/**
	 * String right pad
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 8. 12.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */
	public static String getRPad(String input, int pad_len, String pad_char) {
		int len = input.length();
		String return_value = "";

		// pad length 보다 작을 때
		if (len < pad_len) {
			for (int i = 0; i < pad_len - len; i++) {
				return_value =  return_value + pad_char;
			}
			return (input + return_value);
		} else {
			//pad length 보다 같거나 클때
			return input.substring(0, pad_len);
		}
	}

	/**
	 * 변수가 null 이면
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 9. 16.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */
	public static String ifNullValue(String input)
	{
		if (input == null) {
			return "";
		}

		return input;
	}

	/**
	 * 변수가 null 이면
	 * <p>
	 * 
	 * @author kwaksd2011@gmail.com
	 * @date 2011. 9. 16.
	 * @version 1.0.0
	 * @since 1.0.0
	 * @param str
	 * @return <p>
	 * 
	 *         <pre>
	 * - 사용 예
	 * 
	 * </pre>
	 */
	public static String ifNullValue(String input, String default_value)
	{
		if (input == null) {
			return default_value;
		}

		return input;
	}

	/**
	 * 문자열이 모두 숫자인가
	 * @param input
	 * @return
	 */
	public static boolean isDigits(String str)
	{
		if(str.length() == 0)
			return false;

		char[] chars = str.trim().toCharArray();
		int len = chars.length;		

		for (int i=0; i<len; i++)
		{
			char c = chars[i];

			//	숫자외에
			//	. , - , % 기호까지 포함한다.
			if (
					((c >= '0') && (c <= '9'))	||
					(c == '.')	||
					(c == '-')	||
					(c == '%')	||
					(c == ',')
					)
				continue;
			else
				return false;
		}

		return true;
	}

	/**
	 * 전화번호 포맷
	 * @param input
	 * @return
	 */
	public static String makePhoneNumber(String value)
	{
		try
		{
			if (value.length() == 10)
			{
				return value.substring(0, 3) + "-" + value.substring(3, 6) + "-" + value.substring(6, 10);
			}
			if (value.length() == 11)
			{
				return value.substring(0, 3) + "-" + value.substring(3, 7) + "-" + value.substring(7, 11);
			}
		}
		catch (Exception localException)
		{
		}

		return value;
	}

	/**
	 * 암호문자 표현
	 * @param input
	 * @return
	 */
	public static String makePassword(int length)
	{
		String result = "";
		for (int i = 0; i < length; ++i)
		{
			result = result + "*";
		}
		return result;
	}

	/**
	 * String이 null이거나 ""이면  0 리턴
	 * @param input
	 * @return
	 */
	public static String nullToZero (String input) {
		if (input == null) {
			return "0";
		} 

		input = input.trim();
		if (input.equals("")) {
			input = "0";
		} 

		return input;
	}

	/**
	 * String이 null이거나 ""이면  지정한 값으로 리턴
	 * @param input
	 * @return
	 */
	public static String nullToValue (String input, String replace) {
		if (input == null) {
			return replace;
		} 
		//trim
		input = input.trim();
		if (input.equals("")) {
			return replace;
		} 

		return input;
	}

	/**
	 * 서버로 송신할 때 멀티 칼럼 | 문자 붙여서 조합하기
	 * @param input
	 * @param field_name
	 * @param cursor
	 * @return
	 */
	public static String getInterfaceMultiColumn(String input, String field_name, Cursor cursor) {
		String field_value = "";
		if (input == null) {
			input = "";
		}
		input = input.trim();

		if (field_name == null || field_name.equals("")) {
			field_value = "";
		} else {
			field_value = cursor.getString(cursor.getColumnIndex(field_name));
		}
		//if null
		if (field_value == null) {
			field_value = "";
		}

		input = input + field_value + "|";

		return input;
	}

	/**
	 * 서버로 송신할 때 멀티 칼럼 | 문자 붙여서 조합하기
	 * @param input
	 * @param append_value
	 * @return
	 */
	public static String getInterfaceMultiColumn(String input, String append_value) {
		if (input == null) {
			input = "";
		}
		input = input.trim();

		if (append_value == null) {
			append_value = "";
		}

		input = input + append_value + "|";

		return input;
	}	

	public static String getPhoneNumber(String ph_no){
		if(ph_no.contains("+82")){
			return "0" + ph_no.substring(3, ph_no.length()).replace("-", "");
		}else if(ph_no.substring(0, 2).equals("010")){
			return ph_no.replace("-", "");
		}else{
			return ph_no.replace("-", "");
		}
	}

	// 000 단위마다 콤마찍기
	public static String getFormatNumber(String number){
		if(isDigits(number)){
			double num = Double.parseDouble(number);  // "이천구백육십만" 이라는 숫자를 대입
			DecimalFormat df = new DecimalFormat("#,###,###");
			return df.format(num);
		}else{
			return "";
		}

	}

	// 이메일 형식 검사
	public static boolean checkEmail(String email){
		String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(email);
		boolean isNormal = m.matches();
		return isNormal;
	}

	// 날짜 
	public static String getDateYYMMDD(String time){
		if(time != null){
			if(!time.equals("")){
				return time.split("T")[0];
			}
		}
		return "";
	}

	public static String getDateYYYYMMDD(String time){
		if(time != null){
			try{
				if(!time.equals("") && time.length() == 8 ){

					return time.substring(0,4)+"-"+time.substring(4,6)+"-"+time.substring(6,8);
				}else{
					return "";	

				}
			}catch(Exception e){
				e.printStackTrace();

			}

		}
		return "";
	}


	// 댓글 시간확인
	public static String getBeforeTimeString(long milliseconds){

		int h = (int)milliseconds / ( 60 * 60 * 1000);
		int m =(int)( (milliseconds - (h * 60 * 60 * 1000)) / (60 * 1000));
		int s = (int)( (milliseconds - ((h * 60 * 60 * 1000) + (m * 60 * 1000))) /  1000);

		if(h >= 24){
			return ""; // 하루가 지나감
		}else if(h < 24 && h > 0 ){
			return h + " 시간전";
		}else{
			if(m > 0){
				return m + " 분전";
			}else{
				return "지금 막";
			}
		}
	}

	// 밀리세컨드를 시간 분 초 : 구분자로 생성
	public static String getTimeString(long milliseconds){

		int h = (int)milliseconds / ( 60 * 60 * 1000);
		int m =(int)( (milliseconds - (h * 60 * 60 * 1000)) / (60 * 1000));
		int s = (int)( (milliseconds - ((h * 60 * 60 * 1000) + (m * 60 * 1000))) /  1000);

		StringBuilder sb = new StringBuilder();
		if(h < 10){
			sb.append("0" + h);
		}else{
			sb.append(h);
		}
		sb.append(":");
		if(m < 10){
			sb.append("0" + m);
		}else{
			sb.append(m);
		}
		sb.append(":");
		if(s < 10){
			sb.append("0" + s);
		}else{
			sb.append(s);
		}

		return sb.toString();
	}

	//	public static void setTextViewColorPartial_comment(TextView view, String fulltext, String NickName, int color) {
	//		view.setText(fulltext, TextView.BufferType.SPANNABLE);
	//		Spannable str = (Spannable) view.getText();
	//		int cnt = fulltext.split("\\@").length - 1;
	//		for(int i = 0; i < cnt ; i++){
	//			str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	//		}
	//	}

	public static void setTextViewColorPartial(TextView view, String fulltext, String subtext, int color) {
		view.setText(fulltext, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) view.getText();
		int i = fulltext.indexOf(subtext);
		str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	}
}