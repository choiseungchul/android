package com.returndays.ralara.dto;

import java.lang.reflect.Field;

public class BaseDto {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Class<?> cls = this.getClass();
		Field[] fs = cls.getFields();
		boolean firstCheck = true;
		for(Field f : fs) {
			try {
				sb.append(firstCheck ? "" : ", ").append(f.getName()).append("=").append(f.get(this));
			} catch (Exception e) {;}
			if(firstCheck) firstCheck = false;
		}
		return sb.toString();
	}
	
}
