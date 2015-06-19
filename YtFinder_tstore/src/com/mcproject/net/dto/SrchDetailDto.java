package com.mcproject.net.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class SrchDetailDto  implements Parcelable{

	public String publishedAfter;
	public String publishedBefore;
	public String channelTitle;
	public String videoDuration;
	public String query;
	public String order;
	public String type;

	public SrchDetailDto(){
	}
	
	public SrchDetailDto(Parcel in){
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(publishedAfter);
		dest.writeString(publishedBefore);
		dest.writeString(channelTitle);
		dest.writeString(videoDuration);
		dest.writeString(query);
		dest.writeString(order);
		dest.writeString("type");
	}

	private void readFromParcel(Parcel in){
		publishedAfter = in.readString();
		publishedBefore = in.readString();
		channelTitle = in.readString();
		videoDuration = in.readString();
		query = in.readString();
		order = in.readString();
		type = in.readString();
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public SrchDetailDto createFromParcel(Parcel in) {
			return new SrchDetailDto(in);
		}

		public SrchDetailDto[] newArray(int size) {
			return new SrchDetailDto[size];
		}
	};

}
