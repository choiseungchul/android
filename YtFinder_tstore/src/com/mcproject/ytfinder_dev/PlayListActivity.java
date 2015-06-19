package com.mcproject.ytfinder_dev;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.mcproject.net.adapter.YtPlayListAdapter;
import com.mcproject.net.customview.TouchListView;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfavorite_t.R;

public class PlayListActivity extends BaseActivity{

	private LayoutInflater inflate;
	private LinearLayout progress;
	private TouchListView list_layout;
	private YtPlayListAdapter adap;

	private ArrayList<YTListDto> datas;
	private String currQuery;

	private OnScrollListener onScroll;
	private boolean _bLastItem;
	private boolean _bLoading;
	private TextView play_all, delete_all, back_play;
	
	// 실행취소
	TextView undoLayout;
	OnClickListener undoClick;
	Handler undoLayoutHide = new Handler();
	private long delayMillis = 5*1000;
	SwipeDismissAdapter adapter;
	Runnable uiHideCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist_layout);

		inflate = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		
		initUI();
		initData();
	}

	private void initData() {
		getPlayListData();
	}
	
	private void getPlayListData(){
		_bLoading = true;
		datas = DbQueryUtil.getPlayList(getApplicationContext());
		setUIData();
	}

	private void initUI() {
		super.BaseUIInit(this);
		progress = (LinearLayout) inflate.inflate(R.layout.bottom_loader, null);
		undoLayout = (TextView)findViewById(R.id.playlist_del_undo);

		//처음 데이터 0 개로 셋팅
		list_layout = (TouchListView)findViewById(R.id.list_layout);
		list_layout.setDropListener(onDrop);

		getSrchQuery().setImeAction(new Runnable() {
			@Override
			public void run() {
				datas = new ArrayList<YTListDto>();
				currQuery = getSrchQuery().getText().toString();
				DbQueryUtil.saveSearchNormalString(getApplicationContext(), currQuery);
				getData();
				hideSoftKeyboard(getSrchQuery());
			}

		});
		getSrchQuery().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				datas = new ArrayList<YTListDto>();
				currQuery = getSrchQuery().getText().toString();
				DbQueryUtil.saveSearchNormalString(getApplicationContext(), currQuery);
				getData();
				hideSoftKeyboard(getSrchQuery());
				getSrchQuery().srch_query_slide_left();
			}
		});
		
		// 상세검색
		getSrchStartBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				datas = new ArrayList<YTListDto>();
				setLayer();

				getDetailSearchDate();
			}
		});
		
		getMenuBtn(2).setOnClickListener(null);
		getMenuBtn(2).setBackgroundResource(R.drawable.button_black_on);
		
		// 
		play_all = (TextView)findViewById(R.id.play_all);
		play_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle d = new Bundle();
				ArrayList<YTListDto> list = new ArrayList<YTListDto>();
				String[] ids = new String[adap.getCount()];
				
				for(int i = 0 ; i < adap.getCount(); i++){
					YTListDto dto = adap.getItem(i);
					ids[i] = dto.videoid;
					list.add(dto);
				}
				if(list.size() != 0){
					d.putStringArray("ids", ids);
					gotoListPlayer(d, list, -1);
				}else{
					Toast.makeText(getApplicationContext(), R.string.playlist_empty, Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		delete_all = (TextView)findViewById(R.id.delete_all);
		delete_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder del_dialog = new Builder(PlayListActivity.this);
				del_dialog.setTitle(R.string.playlist_dialog_remove_all_title);
				del_dialog.setMessage(R.string.playlist_dialog_remove_all_msg);
				del_dialog.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DbQueryUtil.deletePlayList(getApplicationContext());
						getPlayListData();
					}
				});
				del_dialog.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				del_dialog.show();
			}
		});
		
		// 백그라운드 플레이는 추후 업데이트
//		back_play = (TextView)findViewById(R.id.back_play);
//		back_play.setOnClickListener(new OnClickListener() {			
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
	}

	private void getData() {
		Intent ii = new Intent(getApplicationContext(), MainActivity.class);
		ii.putExtra("search", getSrchQuery().getText().toString());
		ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(ii);
		finish();
		overridePendingTransition(0, 0);
	}
	
	private void getDetailSearchDate() {
		Intent ii = new Intent(getApplicationContext(), MainActivity.class);
		Bundle params =  getDetailParams();
		ii.putExtra("search_detail", params);
		ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(ii);
		finish();
		overridePendingTransition(0, 0);
	}
	
	@Override
	protected void onStop() {
		// 현재 정렬된 플레이 리스트 정렬
		if(adap != null && _bLoading == false){
			ArrayList<YTListDto> data = new ArrayList<YTListDto>();
			for(int i = 0 ; i < adap.getCount() ; i++){
				YTListDto dto = adap.getItem(i);
				data.add(dto);
			}
			
			DbQueryUtil.sortPlayList(getApplicationContext(), data);
		}
		
		super.onStop();
	}
	
	private void setUIData(){
		// 모든 데이터를 다 가져왔으니 리스트에 뿌림		
//		adap.setData(datas);
		adap = new YtPlayListAdapter(getApplicationContext(), datas);
		
		adap.setMovieThumbClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> dataObj = (HashMap<String, Object>) v.getTag();
				YTListDto dto = (YTListDto) dataObj.get("data");
				gotoSinglePlayer(dto.videoid, dto, "playlist", -1);
			}
		});
		
		undoClick = new OnClickListener() {			
			@Override
			public void onClick(View v) {
				
			}
		};
//		undoLayout.setOnClickListener(undoClick);
		
		adapter = new SwipeDismissAdapter(adap, new OnDismissCallback() {			

			@Override
			public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
				for (final int position : reverseSortedPositions) {
					final YTListDto dto = adap.getItem(position);
					long rs = DbQueryUtil.removePlayList(getApplicationContext(), dto);
					if(rs != -1){
						
						undoLayoutHide.removeCallbacks(uiHideCallback);
						
						undoLayout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								long rs2 = DbQueryUtil.alivePlayList(getApplicationContext(), dto);
								LogUtil.I("alive list item result = " + rs2);
								adap.insert(dto, position);
								adap.notifyDataSetChanged();
								adapter.notifyDataSetChanged();
								undoLayout.setVisibility(View.GONE);
							}
						});
						
						undoLayout.setVisibility(View.VISIBLE);
						
						uiHideCallback = new Runnable() {
							@Override
							public void run() {
								undoLayout.setVisibility(View.GONE);
							}
						};
						
						undoLayoutHide.postDelayed(uiHideCallback, delayMillis);
						
						adap.remove(position);
						adap.notifyDataSetChanged();
						adapter.notifyDataSetChanged();
					}else{
						Toast.makeText(getApplicationContext(), R.string.default_error, 1200).show();
					}
				}
			}
		});
		
		adapter.setAbsListView(list_layout);
		
		list_layout.setAdapter(adapter);
		list_layout.setOnScrollListener(onScroll);

		list_layout.removeFooterView(progress);
		_bLoading = false;
	}
	
	private TouchListView.DropListener onDrop=new TouchListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			LogUtil.D("from = " + from);
			LogUtil.D("to = " + to);
			
			YTListDto item=adap.getItem(from);
			adap.remove(from);
			adap.insert(item, to);
			adap.notifyDataSetChanged();
		}
	};
	
}
