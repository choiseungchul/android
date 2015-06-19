package com.mcproject.net.util;

import java.util.ArrayList;

import com.mcproject.net.util.YouTubeMovieDownloader.Video;

public class ResolutionUtil {

	public static String getStreamUrl(ArrayList<Video> vList, int idx){
		try{			
			return vList.get(idx).url;			
		}catch(ArrayIndexOutOfBoundsException e){
			if(idx != 0)
			return vList.get(idx - 1).url;
			else return null;
		}		
	}
}
