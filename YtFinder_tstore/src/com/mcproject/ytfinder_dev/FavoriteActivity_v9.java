package com.mcproject.ytfinder_dev;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.mcproject.net.adapter.KeywordListAdapter;
import com.mcproject.net.adapter.UploaderListAdapter;
import com.mcproject.net.adapter.YtFavoriteVideoAdapter;
import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.customview.TouchListView;
import com.mcproject.net.customview.TouchListView.DropListener;
import com.mcproject.net.dto.FavUploaderDto;
import com.mcproject.net.dto.MySearchDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfavorite_t.R;

public class FavoriteActivity_v9 extends BaseActivity{

	private TouchListView list_layout_uploader;
	private ListView list_layout;
	private String currQuery;
	private TextView tab_1;
	private TextView tab_2;
	private TextView tab_3;
	private int curr_tab = 0;
	static int saved_curr_tab = 0;
	String tabNum = "1";

	private SwipeDismissAdapter us_adapter;
	private ArrayList<FavUploaderDto> uploader_list;
	private UploaderListAdapter ul_adap;

	@Override
	protected void onResume() {
//		if(list_layout != null){
//			if(saved_curr_tab == 0){
//				getVideo();
//			}else if(saved_curr_tab == 1){
//				getUploader();
//			}else if(saved_curr_tab == 2){
//				getKeyword();
//			}
//		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_layout_v9);

		if(getIntent().getExtras() != null){
			if(getIntent().getExtras().getString("tab_on") != null){
				tabNum = getIntent().getExtras().getString("tab_on");				
			}
		}
		
		LogUtil.D("tabNum = " + tabNum);

		initUI();
		initData();
	}

	private void initData() {
		if(tabNum.equals("1")){
			curr_tab = 0;
			getVideo();
		}else if(tabNum.equals("2")){
			curr_tab = 1;
			getUploader();
		}else if(tabNum.equals("3")){
			curr_tab = 2;
			getKeyword();
		}else{
			curr_tab = 0;
			getVideo();
		}

	}

	private void getVideo(){				
		list_layout.setVisibility(View.VISIBLE);
		list_layout_uploader.setVisibility(View.GONE);
		
		LogUtil.I("getVideo...");
		
		ArrayList<YTListDto> datas = DbQueryUtil.getFavoriteVideo(getApplicationContext());

		final YtFavoriteVideoAdapter adap = new YtFavoriteVideoAdapter(getApplicationContext(), datas);

		SwipeDismissAdapter adapter = new SwipeDismissAdapter(adap, new OnDismissCallback() {			
			@Override
			public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					YTListDto dto = adap.getItem(position);
					long rs = DbQueryUtil.removeFavoriteVideo(getApplicationContext(), dto.videoid);
					if(rs != -1){
						adap.remove(position);
						Toast.makeText(getApplicationContext(), R.string.favlist_video_removed, 1200).show();
					}else{
						Toast.makeText(getApplicationContext(), R.string.default_error, 1200).show();
					}
				}
			}
		});
		adapter.setAbsListView(list_layout);

		adap.setMovieThumbClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
				YTListDto dto = (YTListDto) data.get("data");
				int position = (Integer) data.get("position");
				gotoSinglePlayer(dto.videoid, dto, "favorite_video", position);
			}
		});
		list_layout.setAdapter(adapter);
		list_layout.setOnItemClickListener(null);
	}

	private void getUploader(){		
		list_layout.setVisibility(View.GONE);
		list_layout_uploader.setVisibility(View.VISIBLE);
		
		LogUtil.I("getUploader...");
		
		uploader_list = DbQueryUtil.getFavoriteUploaderList(getApplicationContext());

		ul_adap = new UploaderListAdapter(getApplicationContext(), uploader_list);
		
		us_adapter = new SwipeDismissAdapter(ul_adap, new OnDismissCallback() {
			@Override
			public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					uploader_remove(position);
				}
			}
		});
		us_adapter.setAbsListView(list_layout_uploader);

		list_layout_uploader.setAdapter(us_adapter);
		list_layout_uploader.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				FavUploaderDto dto = (FavUploaderDto) arg0.getItemAtPosition(arg2);
				Intent ii = new Intent(getApplicationContext(), FavoriteUploaderActivity.class);
				ii.putExtra("uploader_id", dto.uploader_id);
				ii.putExtra("uploader_name", dto.title);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		
		ul_adap.setOnTitleClick(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				FavUploaderDto dto = (FavUploaderDto) v.getTag();
				Intent ii = new Intent(getApplicationContext(), FavoriteUploaderActivity.class);
				ii.putExtra("uploader_id", dto.uploader_id);
				ii.putExtra("uploader_name", dto.title);
				ii.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(ii);
			}
		});
		
		// 순서변경
		list_layout_uploader.setDropListener(new DropListener() {
			@Override
			public void drop(int from, int to) {
				LogUtil.D("from = " + from);
				LogUtil.D("to = " + to);
				
				FavUploaderDto item=ul_adap.getItem(from);
				ul_adap.remove(from);
				ul_adap.insert(item, to);
				ul_adap.notifyDataSetChanged();
			}
		});
	}


	private void getKeyword(){
		list_layout.setVisibility(View.VISIBLE);
		list_layout_uploader.setVisibility(View.GONE);
		
		LogUtil.I("getKeyword...");

		ArrayList<MySearchDto> search_list = DbQueryUtil.getSearchListAll(getApplicationContext());

		final KeywordListAdapter adap = new KeywordListAdapter(getApplicationContext(), search_list);
		SwipeDismissAdapter adapter = new SwipeDismissAdapter(adap, new OnDismissCallback() {			
			@Override
			public void onDismiss(AbsListView arg0, int[] reverseSortedPositions) {
				for (int position : reverseSortedPositions) {
					String search_text = adap.getItem(position).search_text;
					long rs = DbQueryUtil.removeSearchNormalString(getApplicationContext(), search_text);
					if ( rs != -1) adap.remove(position);
					else Toast.makeText(getApplicationContext(), R.string.keyword_delete_error, Toast.LENGTH_SHORT).show();
				}
			}
		});
		adapter.setAbsListView(list_layout);
		list_layout.setAdapter(adapter);
		list_layout.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				MySearchDto dto = (MySearchDto) arg0.getItemAtPosition(arg2);
				getSrchQuery().setText(dto.search_text);
				getData();
			}
		});
	}

	private void initUI() {
		super.BaseUIInit(this);

		list_layout = (ListView)findViewById(R.id.list_layout);
		list_layout_uploader = (TouchListView)findViewById(R.id.list_layout_uploader);

		getSrchQuery().setImeAction(new Runnable() {
			@Override
			public void run() {
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
				getData();
				hideSoftKeyboard(getSrchQuery());
				getSrchQuery().srch_query_slide_left();
			}
		});

		// 상세검색
		getSrchStartBtn().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setLayer();
				getDetailSearchDate();
			}
		});

		getMenuBtn(3).setOnClickListener(null);
		getMenuBtn(3).setBackgroundResource(R.drawable.button_black_on);

		tab_1 = (TextView)findViewById(R.id.tab_1);
		tab_2 = (TextView)findViewById(R.id.tab_2);
		tab_3 = (TextView)findViewById(R.id.tab_3);
		OnClickListener tab_click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(curr_tab == 1){
					sortUploaderList();
				}
				if(v.getId() == R.id.tab_1){
					curr_tab = 0;
					getVideo();
				}else if(v.getId() == R.id.tab_2){
					curr_tab = 1;
					getUploader();
				}else if(v.getId() == R.id.tab_3){
					curr_tab = 2;
					getKeyword();
				}
			}
		};
		tab_1.setOnClickListener(tab_click);
		tab_2.setOnClickListener(tab_click);
		tab_3.setOnClickListener(tab_click);
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

	private void uploader_remove(final int position){
		AlertDialog.Builder alert = new Builder(FavoriteActivity_v9.this);
		alert.setTitle(R.string.fav_dialog_uploader_remove_title);
		alert.setMessage(R.string.fav_dialog_uploader_remove_msg);
		alert.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				FavUploaderDto dto = ul_adap.getItem(position);
				// 메모리 상에 올려져있는 
				boolean isRemoved = UploaderProgressingList.removeUploader(dto.uploader_id);
				long rs = DbQueryUtil.removeFavoriteUploaderLogAndFavor(getApplicationContext(), dto.uploader_id);
				
				if(rs != -1){
					ul_adap.remove(position);
					Toast.makeText(getApplicationContext(), R.string.uploader_delete_success, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.uploader_delete_failed, Toast.LENGTH_SHORT).show();
				}
			}
		});
		alert.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});				
		alert.show();
	}
	
	private void sortUploaderList(){
		if(ul_adap != null ){
			ArrayList<FavUploaderDto> data = new ArrayList<FavUploaderDto>();
			for(int i = 0 ; i < ul_adap.getCount() ; i++){
				FavUploaderDto dto = ul_adap.getItem(i);
				data.add(dto);
			}
			
			DbQueryUtil.sortUploaderList(getApplicationContext(), data);
		}
	}
	
	// 업로더 정렬 순서 입력
	@Override
	protected void onStop() {
		if(curr_tab == 1)
		sortUploaderList();
		
		super.onStop();
	}
	
}
