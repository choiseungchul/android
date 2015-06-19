package com.mcproject.net.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.ListView;

import com.mcproject.net.adapter.UploaderDetailAdapter;
import com.mcproject.net.adapter.YtFavoriteVideoAdapter;
import com.mcproject.net.adapter.YtListAdapter;
import com.mcproject.net.adapter.YtPlayListAdapter;
import com.mcproject.ytfavorite_t.R;

public class PlayerMovieListDialog extends Dialog{

	ListView list;
	
	public PlayerMovieListDialog(Context context) {
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

		setContentView(R.layout.player_movie_list_dialog);
		
		list = (ListView)findViewById(R.id.list);
	}

	
	public void setSearchAdapter(YtListAdapter adapter){
		list.setAdapter(adapter);
	}
	public void setPlayListAdapter(YtPlayListAdapter adapter){
		list.setAdapter(adapter);
	}
	public void setFavoriteAdapter(YtFavoriteVideoAdapter adapter){
		list.setAdapter(adapter);
	}
	public void setUploaderAdapter(UploaderDetailAdapter adapter){
		list.setAdapter(adapter);
	}
	
	
	public ListView getListView(){
		return list;
	}
	
}
