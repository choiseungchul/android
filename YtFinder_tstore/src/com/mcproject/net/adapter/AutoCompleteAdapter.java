package com.mcproject.net.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.StringUtil;
import com.mcproject.net.util.ViewUtil;
import com.mcproject.ytfavorite_t.R;

// 자동완성기능
public class AutoCompleteAdapter extends BaseAdapter
implements Filterable
{
	Context ctx;
	private ArrayList<String> search_query = new ArrayList<String>();
	
	RequestQueue reqQ;
	Listener<String> listener;
	ErrorListener error;
	AutoCompleteAdapter srch_query_list;
	String Q_TAG = "customEditText";

	public AutoCompleteAdapter(Context ctx, ArrayList<String> list){		
		this.ctx = ctx;
		search_query = list;		
		
		reqQ = Volley.newRequestQueue(ctx);
		
		listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				try{
					int f = response.indexOf(",");
					String replaced = response.substring(f + 1, response.length()-1);
					replaced = replaced.replace("[", "").replace("]", "").replace("\"", "").trim();
					
					String[] auto_text = replaced.split(",");
					
					ArrayList<String> searchList = new ArrayList<String>();
					
					int cnt = 0;
					
					for(int i = 0; i < auto_text.length; i++){					
						cnt++;
						if(cnt < 6){
							String decoded = StringUtil.unicodeDecode(auto_text[i]);
							LogUtil.I("decoded = " + decoded);							
							searchList.add(decoded);
						}else{ break; }
					}
					
					search_query = searchList;
					notifyDataSetChanged();
		
				}catch(Exception e){
					LogUtil.E(e.toString());
				}
			}
		};
		
		error = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtil.E(error.toString());
			}
		};
	}		
	public void setData( ArrayList<String> geoInfos ){
		search_query = geoInfos;		
	}		
	public void clear() {
		search_query.clear();
	}

	@Override
	public int getCount() {			
		return search_query.size();
	}

	@Override
	public String getItem(int position) {
		return search_query.get( position );
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( convertView == null )				
			convertView = ViewUtil.getInflatedView( parent.getContext(), R.layout.search_dropdown_text );
		String query = search_query.get( position );
		TextView tv = (TextView)convertView.findViewById( R.id.search_dropdown_textview);
		tv.setText( query );
		
		int height = (int) McUtil.getDpToPixel(ctx, 40);
		
		tv.setHeight(height);
		
//		LogUtil.I("getview = " + query);
		
		return convertView;
	}

	@Override
	public Filter getFilter() { 
		return new Filter(){		
			// 텍스트 변할때마다 호출됨
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				filterResults.count = search_query.size();
				filterResults.values = search_query;
				
				LogUtil.D("performFiltering.... ");
	
				return filterResults;
			}
	
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {				
				if ( results == null || results.count == 0 ){
//					notifyDataSetInvalidated();
					LogUtil.D("notifyDataSetInvalidated");
				}else {
					notifyDataSetChanged();
					LogUtil.D("notifyDataSetChanged");
				}					
			}
		};
	}
}