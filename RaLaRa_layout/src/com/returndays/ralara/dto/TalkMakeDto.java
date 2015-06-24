package com.returndays.ralara.dto;

import java.io.Serializable;

public class TalkMakeDto extends BaseDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String TITLE;
	public String KEYWORD;
	public String CONTENT;
	public String ROOM_IMG_PATH;
	public String TITLE_IMG_PATH;
	public int EGG_CNT;
	public int LIKE_CNT;
	public int COMM_CNT;

}
