package com.mcproject.net.dto;

import java.io.Serializable;

public class YTListDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 11232312L;
	
	public int position;
	public String etag;
	public String kind;
	public String videoid;
	public String publish_date;
	public String channel_id;
	public String title;
	public String description;
	public String thumbnail;
	public String channel_title;
	public String liveBroadcastContent;
	public String duration;
	public int hh;
	public int mm;
	public int ss;
	public String seq;
	public boolean isFavoriteVideo;
	public boolean isFavoriteUploader;
	public String publish_date_origin;
}
