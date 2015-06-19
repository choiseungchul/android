package com.mcproject.ytfinder_dev;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mcproject.net.adapter.UploaderDetailAdapter;
import com.mcproject.net.customview.CustomEditText;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.ytfavorite_t.R;

public class FavoriteUploaderActivity extends Activity{
	
	private TextView uploader_name, uploader_search;
	private ListView list_layout;
	private String uploader_id;
	private String channel_title;
	private ArrayList<CollectedDto> datas;
	private UploaderDetailAdapter adap;
	
	private int itemCount = 0;
	private int perPage = 10;
	private int totalCount = 0;
	private boolean _bLastItem;
	private boolean _bLoading;
	private LinearLayout progress;
	private LayoutInflater inflate;
	private OnClickListener onFavUploaderAddPlayListClick;
	private OnClickListener onFavUploaderMovieClick;
	private LinearLayout uploader_search_bar;
	private EditText search_inner;
	
	String srch_text = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_uploader_list);
		
		inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		
		uploader_id = getIntent().getExtras().getString("uploader_id");
		channel_title = getIntent().getExtras().getString("uploader_name");
		
		LogUtil.I("uploader_id = " + uploader_id);
		
		if(uploader_id != null && channel_title != null){
			initUI();
			initData();
		}
	}

	private void initData() {
		_bLoading = true;
		
		list_layout.addFooterView(progress);
		
		totalCount = DbQueryUtil.getCollectFavoriteVideoCount(getApplicationContext(), uploader_id);
		LogUtil.I("totalCount = " + totalCount);
		
		uploader_name.setText(channel_title);
		datas = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, itemCount, perPage);
		
		onFavUploaderAddPlayListClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
				int position = (Integer) data.get("position");
				CollectedDto dto = (CollectedDto) data.get("data");
				
				LogUtil.D("is playlist = " + dto.isPlaylist);
				
				if(!dto.isPlaylist){
					long rs = DbQueryUtil.addPlayList(getApplicationContext(), dto);
					if(rs != -1){
						dto.isPlaylist = true;
						datas.set(position, dto);
						adap.setData(datas);
						adap.notifyDataSetChanged();
					}else{
						LogUtil.D("add playlist rs = " + rs);
					}
				}else{
					long rs = DbQueryUtil.removePlayList(getApplicationContext(), dto);
					if(rs != -1){
						dto.isPlaylist = false;
						datas.set(position, dto);
						adap.setData(datas);
						adap.notifyDataSetChanged();
					}
				}
			}
		};
		
		onFavUploaderMovieClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
				int position = (Integer) data.get("position");
				CollectedDto dto = (CollectedDto) data.get("data");
				
				Intent ii = null;
				if(Build.VERSION.SDK_INT >= 11){
					ii = new Intent(getApplicationContext(), PlayerActivity_v11.class);
				}else{
					ii = new Intent(getApplicationContext(), PlayerActivity.class);
				}
				
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				ii.putExtra("id", dto.videoid);
				ii.putExtra("dto", McUtil.convertCollectDtoToYTLDto(dto));
				ii.putExtra("list_type", "uploader");
				ii.putExtra("uploader", uploader_id);
				ii.putExtra("cursor", position);
				startActivity(ii);
			}
		};
		
		adap = new UploaderDetailAdapter(getApplicationContext(), datas);
		adap.setAddPlayListClick(onFavUploaderAddPlayListClick);
		adap.setMovieClick(onFavUploaderMovieClick);
		
		list_layout.setAdapter(adap);
		list_layout.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if ( _bLastItem == false && _bLoading == false && (totalItemCount - visibleItemCount) <= firstVisibleItem ) {
					loadMore();
				}
			}
		});
		
		list_layout.removeFooterView(progress);
		
		itemCount += perPage;
		_bLoading = false;
		
	}
	
	private void loadMore(){
		_bLoading = true;
		if(totalCount > datas.size()){
			list_layout.addFooterView(progress);
			
			ArrayList<CollectedDto> moreData = null;
			if(srch_text == null){
				moreData = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, itemCount, perPage);
			}else{
				moreData = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, srch_text, itemCount, perPage);
			}
			
			datas.addAll(moreData);
			adap.setData(datas);
			adap.notifyDataSetChanged();
			
			itemCount += perPage;
			_bLoading = false;
			
			list_layout.removeFooterView(progress);
		}
	}

	private void initUI() {
		
		uploader_name = (TextView)findViewById(R.id.uploader_name);
		uploader_search = (TextView)findViewById(R.id.uploader_search);
		uploader_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploader_search_bar.setVisibility(View.VISIBLE);
			}
		});
		
		uploader_search_bar = (LinearLayout)findViewById(R.id.uploader_search_bar);
		
		// 업로더 내 검색
		search_inner = (EditText)findViewById(R.id.search_inner);
		search_inner.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					String query = search_inner.getText().toString();
					
					if(!query.equals("")){
						_bLoading = true;
						
						ArrayList<CollectedDto> list =  DbQueryUtil.getCollectFavoriteVideos(getApplicationContext(), uploader_id, query, 0, perPage);
						if(list.size() > 0){
							totalCount = DbQueryUtil.getCollectFavoriteVideoCount(getApplicationContext(), uploader_id, query);
							itemCount = list.size();
							srch_text = query;
							datas = list;
							adap.setData(datas);
							adap.notifyDataSetChanged();
							
						}else{
							Toast.makeText(getApplicationContext(), R.string.srch_select_alert_empty, Toast.LENGTH_SHORT).show();
							search_inner.setText("");
						}
						
						hideSoftKeyboard(search_inner);
						
						_bLoading = false;
						
					}else{
						Toast.makeText(getApplicationContext(), R.string.srch_alert, Toast.LENGTH_SHORT).show();
						srch_text = null;
					}
				}
				
				return false;
			}
		});
		
		list_layout = (ListView)findViewById(R.id.list_layout);
		
		progress = (LinearLayout) inflate.inflate(R.layout.bottom_loader, null);
	}
	
	@Override
	protected void onStop() {
		// 새로운영상 본걸로 처리
		if(uploader_id != null){
			ArrayList<String> uploaders = new ArrayList<String>();
			uploaders.add(uploader_id);
			DbQueryUtil.setIsViewUser(getApplicationContext(), uploaders);
		}
		super.onStop();
	}
	
	// 키보드 숨김
	protected void hideSoftKeyboard(View view) {
		InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
