package com.returndays.ralara.dto;

import java.io.Serializable;

public class PushMsgDto extends BaseDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String user_seq;
	public String room_seq;
	public String reply;
	public String date;
	public String title;
	public String activity;
	public String flag;
	public String url;
	public String desc;
	
	
}
