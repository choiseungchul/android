package com.mcproject.ytfinder_dev;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.mcproject.net.adapter.KeywordListAdapter;
import com.mcproject.net.adapter.UploaderListExpandableAdapter;
import com.mcproject.net.adapter.YtListAdapter;
import com.mcproject.net.conf.UploaderProgressingList;
import com.mcproject.net.customview.FloatingGroupExpandableListView;
import com.mcproject.net.customview.WrapperExpandableListAdapter;
import com.mcproject.net.dto.CollectedDto;
import com.mcproject.net.dto.MySearchDto;
import com.mcproject.net.dto.YTListDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfavorite_t.R;

@Deprecated
public class FavoriteActivity extends BaseActivity{

	private LayoutInflater inflate;
	private LinearLayout progress;
	private ListView list_layout;
	private FloatingGroupExpandableListView expendablelist_layout;
	private WrapperExpandableListAdapter wrapperAdapter;
	private String currQuery;
	private ProgressDialog progDialog;
	private TextView tab_1;
	private TextView tab_2;
	private TextView tab_3;
	private OnClickListener onFavUploaderRemoveClick;
	private OnClickListener onFavUploaderAddPlayListClick;
	private UploaderListExpandableAdapter u_adap;
	private OnClickListener onFavUploaderLoadMoreClick;
	private OnClickListener onFavUploaderMovieClick;
	
	private int m_groupPositon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite_layout);
		inflate = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

		initUI();
		initData();
	}

	private void initData() {
		getVideo();		
	}

	private void getVideo(){
		expendablelist_layout.setVisibility(View.GONE);
		list_layout.setVisibility(View.VISIBLE);
		
		ArrayList<YTListDto> datas = DbQueryUtil.getFavoriteVideo(getApplicationContext());

		final YtListAdapter adap = new YtListAdapter(getApplicationContext(), datas);
		adap.setMovieThumbClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
				YTListDto dto = (YTListDto) data.get("data");
				int position = (Integer) data.get("position");
				gotoSinglePlayer(dto.videoid, dto, "favorite_video", position);
			}
		});
		list_layout.setAdapter(adap);
	}

	private void getUploader(){
		
		list_layout.setVisibility(View.GONE);
		expendablelist_layout.setVisibility(View.VISIBLE);
		
		progDialog = ProgressDialog.show(FavoriteActivity.this, null, getString(R.string.srch_loading), true, true);

		onFavUploaderMovieClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data =  (HashMap<String, Object>) v.getTag();
				YTListDto dto = (YTListDto) data.get("data");
				int position = (Integer) data.get("position");
				gotoSinglePlayer(dto.videoid, dto, "uploader", position);
			}
		};
		
		onFavUploaderRemoveClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String uploder_id = (String) v.getTag();
				
				AlertDialog.Builder uploaderDelete = new Builder(FavoriteActivity.this);
				uploaderDelete.setTitle(R.string.uploader_delete_dialog_title);
				uploaderDelete.setMessage(R.string.uploader_delete_dialog_msg);
				uploaderDelete.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UploaderProgressingList.setUploaderOff(uploder_id);
						long rs = DbQueryUtil.removeFavoriteUploaderLogAndFavor(getApplicationContext(), uploder_id);
						if(rs == 0){
							Toast.makeText(getApplicationContext(), R.string.uploader_delete_success, Toast.LENGTH_SHORT).show();
							reloadUploaderVideoList();
							
							expendablelist_layout.setAdapter(wrapperAdapter);
						}else{
							Toast.makeText(getApplicationContext(), R.string.uploader_delete_failed, Toast.LENGTH_SHORT).show();
						}
					}
				});
				uploaderDelete.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				uploaderDelete.show();
			}
		};
		
		onFavUploaderAddPlayListClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
				int g_pos = (Integer) data.get("g_pos");
				int c_pos = (Integer) data.get("c_pos");
				CollectedDto dto = (CollectedDto) data.get("data");
				if(!dto.isFavoriteVideo){
					long rs = DbQueryUtil.addPlayList(getApplicationContext(), dto);
					if(rs != -1){
						dto.isFavoriteVideo = true;
						u_adap.setChild(g_pos, c_pos, dto);
						u_adap.notifyDataSetChanged();
					}
				}else{
					long rs = DbQueryUtil.removePlayList(getApplicationContext(), dto);
					if(rs != -1){
						dto.isFavoriteVideo = false;
						u_adap.setChild(g_pos, c_pos, dto);
						u_adap.notifyDataSetChanged();
					}
				}
			}
		};
		
		onFavUploaderLoadMoreClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				HashMap<String, Object> data = (HashMap<String, Object>) v.getTag();
				int g_pos = (Integer) data.get("g_pos");
				int c_pos = (Integer) data.get("c_pos");
				CollectedDto dto = (CollectedDto) data.get("data");
				
				ArrayList<CollectedDto> list = DbQueryUtil.getCollectFavoriteVideosByIdBottomBtn(getApplicationContext(), dto.channel_id, c_pos, 20);
				u_adap.removeChild(g_pos, c_pos);
				u_adap.addChlidren(g_pos, list);
				
				u_adap.notifyDataSetChanged();
			}
		};
		
		reloadUploaderVideoList();
		
		expendablelist_layout.setAdapter(wrapperAdapter);
		expendablelist_layout.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				if(m_groupPositon != -1 && m_groupPositon != groupPosition) {
					expendablelist_layout.collapseGroup(m_groupPositon);
				}
//				expendablelist_layout.addHeaderView(exlist_top_title);
				m_groupPositon = groupPosition;
			}
		});
		
		
		progDialog.dismiss();
		
	}
	
	private void reloadUploaderVideoList(){
		ArrayList<String> uploaders_id = new ArrayList<String>();
		ArrayList<String> uploaders_name = new ArrayList<String>();
		ArrayList<YTListDto> playList = DbQueryUtil.getPlayList(getApplicationContext());
		ArrayList<ArrayList<CollectedDto>> all_data = DbQueryUtil.getCollectFavoriteVideos(getApplicationContext());
		for(int i = 0 ; i < all_data.size(); i++){
			String uploaderName = "empty";
			String uploaderId = "empty";
			if(all_data.get(i).size() > 0){
				uploaderName = all_data.get(i).get(0).channel_title;
				uploaderId = all_data.get(i).get(0).channel_id;
			}
			uploaders_name.add(uploaderName);
			uploaders_id.add(uploaderId);
			
			ArrayList<CollectedDto> childData = all_data.get(i);
			
			for(int s = 0 ; s < childData.size(); s++){
				CollectedDto dto = childData.get(s);
				for(int k = 0 ; k < playList.size() ; k++){
					YTListDto plDto = playList.get(k);
					if(dto.videoid == null){
						LogUtil.I("getCollectFavoriteVideos() videoid is null!!  uploader_name = " + uploaders_name.get(i));
					}
//					if(dto.videoid.equals(plDto.videoid)){
//						dto.isFavoriteVideo = true;
//						break;
//					}
				}
				childData.set(s, dto);
			}
			
			all_data.set(i, childData);
		}

		u_adap = new UploaderListExpandableAdapter(getApplicationContext(), uploaders_name, uploaders_id, all_data );
		u_adap.setUploaderDeleteClick(onFavUploaderRemoveClick);
		u_adap.setMovieAddClick(onFavUploaderAddPlayListClick);
		u_adap.setLoadMoreClick(onFavUploaderLoadMoreClick);	
		u_adap.setMovieClick(onFavUploaderMovieClick);
		
		
		wrapperAdapter = new WrapperExpandableListAdapter(u_adap);
	}

	private void getKeyword(){
		expendablelist_layout.setVisibility(View.GONE);
		list_layout.setVisibility(View.VISIBLE);
		ArrayList<MySearchDto> search_list = DbQueryUtil.getSearchListNormal(getApplicationContext(), "");
		
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
		list_layout.setAdapter(adap);
		
	}

	private void initUI() {
		super.BaseUIInit(this);
		
		progress = (LinearLayout) inflate.inflate(R.layout.bottom_loader, null);

		//처음 데이터 0 개로 셋팅
		list_layout = (ListView)findViewById(R.id.list_layout);
		expendablelist_layout = (FloatingGroupExpandableListView)findViewById(R.id.expendablelist_layout);
//		expendablelist_layout.setGroupIndicator(null);
//		expendablelist_layout.setPinnedHeaderView(exlist_pinn);

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

		tab_1 = (TextView)findViewById(R.id.tab_1);
		tab_2 = (TextView)findViewById(R.id.tab_2);
		tab_3 = (TextView)findViewById(R.id.tab_3);
		OnClickListener tab_click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.tab_1){
					getVideo();
				}else if(v.getId() == R.id.tab_2){
					getUploader();
				}else if(v.getId() == R.id.tab_3){
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

	@Override
	protected void onStop() {
		if(u_adap != null){
			ArrayList<String> idList =  u_adap.getExpendIdList();
			LogUtil.D("is expended size = " + idList.size());
			DbQueryUtil.setIsViewUser(getApplicationContext(), idList);
		}
		super.onStop();
	}
	
}
