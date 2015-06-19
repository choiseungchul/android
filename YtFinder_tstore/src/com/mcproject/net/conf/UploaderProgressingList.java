package com.mcproject.net.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mcproject.net.util.LogUtil;

public class UploaderProgressingList {

	public static HashMap<String, String> uploaders = new HashMap<String, String>();
	
	public static boolean setUploaderOn(String uploader_id){
		if(!uploaders.containsKey(uploader_id)){
			uploaders.put(uploader_id, "Y");
			return true;
		}else{
			if(uploaders.get(uploader_id).equals("Y")){
				return false;
			}else if(uploaders.get(uploader_id).equals("N")){
				uploaders.put(uploader_id, "Y");
				return true;
			}else{
				LogUtil.E("?????!!!");
				return false;
			}
		}
	}
	
	// 현재 메모리에 있는 수집 업로더 아이디를 없앤다
	public static boolean removeUploader(String uploader_id){
		if(uploaders.containsKey(uploader_id)){
			uploaders.remove(uploader_id);
			return true;
		}else{
			return false;
		}
	}
	
	public static ArrayList<String> getUploaders(){
		ArrayList< String> uploader = new ArrayList<String>();
		
		for(Entry<String, String> key : uploaders.entrySet()){
			if(uploaders.get(key).equals("N")){
				uploader.add(key.getKey());
			}
		}
		return uploader;
	}
	
	public static void setUploaderOff(String uploader_id){
		uploaders.put(uploader_id, "N");
	}
	
}
