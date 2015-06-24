package com.returndays.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.returndays.ralara.R;
import com.returndays.ralara.AdViewImageActivity;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.util.LogUtil;

public class AdImageViewFragment extends Fragment {
	int mPosition;
	ImageView img;
	Bitmap mBm;
	public static AdImageViewFragment newInstence(int position) {
		AdImageViewFragment fragment = new AdImageViewFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt("position");
		LogUtil.W( "positions : " +  mPosition );
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v =  inflater.inflate(R.layout.fragment_image, null);
		img = (ImageView) v.findViewById(R.id.ad_fragment_img);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AdViewImageActivity act = (AdViewImageActivity) getActivity();
		LogUtil.W(act.mXmldata.text());
		
		String url = act.mXmldata.tag("AD_IMG"+(mPosition+1)).text();
		
		LogUtil.W( "imggggggggggggg1 : " +  url );
		
		mBm = BitmapFactory.decodeFile(Define.AD_APP_FOLDER+url.substring(url.lastIndexOf("/")));
		img.setImageBitmap(mBm);
	}

	@Override
	public void onDestroy() {
		if(mBm != null) mBm.recycle();
		super.onDestroy();
	}
	
	

}
