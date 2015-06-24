package com.momsfree.net.customview;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.momsfree.net.R;
import com.momsfree.net.conf.UrlDef;
import com.momsfree.net.http.HttpDocument;
import com.momsfree.net.http.HttpDocument.HttpCallBack;

public class AddrSrchDialog extends Dialog{

	TextViewNanumGothic srch_addr_backbtn, addr_srch_btn, find_addr_result_empty;
	EditText addr_srch_input;
	ImageView srch_addr_close;
	ListView find_addr_result;
	HttpDocument mHttp;
	
	public AddrSrchDialog(final Context context) {
		// TODO Auto-generated constructor stub
		super(context , android.R.style.Theme_Translucent_NoTitleBar);
		
		setContentView(R.layout.srch_addr_dialog);

		srch_addr_backbtn = (TextViewNanumGothic)findViewById(R.id.srch_addr_backbtn);
		srch_addr_close = (ImageView)findViewById(R.id.srch_addr_close);
		find_addr_result_empty = (TextViewNanumGothic)findViewById(R.id.find_addr_result_empty);
		addr_srch_btn = (TextViewNanumGothic)findViewById(R.id.addr_srch_btn);
		addr_srch_input = (EditText)findViewById(R.id.addr_srch_input);
		find_addr_result = (ListView)findViewById(R.id.find_addr_result);
		
		setBackgroundGrayOver();
		
		addr_srch_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String addr = addr_srch_input.getText().toString();
				if(addr.equals("")){
					Toast.makeText(context, "동,읍,면,리 를 입력해주세요.", Toast.LENGTH_LONG).show();
				}else{
					mHttp = new HttpDocument(context);
					Hashtable<String, String> params = new Hashtable<String, String>();
					params.put("dong", addr);
					mHttp.getDocument(UrlDef.SEARCH_ADDR, params, null, new HttpCallBack() {
						@Override
						public void onHttpCallBackListener(Document document,
								Header[] header) {
							String body = document.select("body").text();
							
							try {
								JSONObject json = new JSONObject(body);
								
								String rs = json.getString("result");
								String data = json.getString("data");
								
								if(rs.equals("0000")){
								
									ArrayList<String> datas = new ArrayList<String>();
									
									JSONArray arr = new JSONArray(data);
									for(int i = 0 ; i < arr.length(); i++){
										JSONObject item = new JSONObject(arr.getString(i));
										
										String sido = item.getString("sido");
										String sigungu = item.getString("sigungu");
										String dong = item.getString("dong");
										String bungi = item.getString("bungi");
										String apt = item.getString("apt");
										String ri = item.getString("ri");
										String dose = item.getString("dose");
										
										try {
											sido = URLDecoder.decode(sido, "UTF-8");
											sigungu = URLDecoder.decode(sigungu, "UTF-8");
											dong = URLDecoder.decode(dong, "UTF-8");
											bungi = URLDecoder.decode(bungi, "UTF-8");
											apt = URLDecoder.decode(apt, "UTF-8");
											ri = URLDecoder.decode(ri, "UTF-8");
											dose = URLDecoder.decode(dose, "UTF-8");
										} catch (UnsupportedEncodingException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
												
										
										StringBuffer sb = new StringBuffer();
										sb.append(sido);
										sb.append(" ");
										sb.append(sigungu);
										
										if(!dong.equals("")){
											sb.append(" ");
											sb.append(dong);
										}
										if(!bungi.equals("")){
											sb.append(" ");
											sb.append(bungi);	
										}
										if(!apt.equals("")){
											sb.append(" ");
											sb.append(apt);
										}
										if(!ri.equals("")){
											sb.append(" ");
											sb.append(ri);
										}
										if(!dose.equals("")){
											sb.append(" ");
											sb.append(dose);
										}
									
										datas.add(sb.toString());
									}
									
									if(datas.size() == 0){
										Toast.makeText(context, "주소를 정확히 입력해주세요.", Toast.LENGTH_LONG).show();
										find_addr_result.setEmptyView(find_addr_result_empty);
										find_addr_result_empty.setVisibility(View.VISIBLE);
									}else{
										find_addr_result_empty.setVisibility(View.GONE);
										ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.addr_list_item, datas);
										
										find_addr_result.setAdapter(adapter);
									}
								
									
									
								}else{
									Toast.makeText(context, "주소를 받아오는데 실패했습니다.", Toast.LENGTH_LONG).show();
									find_addr_result.setEmptyView(find_addr_result_empty);
									find_addr_result_empty.setVisibility(View.VISIBLE);
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}, false);
					
				}
			}
		});
	}
	
	public void setItemOnclick(OnItemClickListener itemclick){
		find_addr_result.setOnItemClickListener(itemclick);
	}
	
	public void setBackBtn(View.OnClickListener listener){
		srch_addr_backbtn.setOnClickListener(listener);
	}
	
	public void setCloseBtn(View.OnClickListener listener){
		srch_addr_close.setOnClickListener(listener);
	}
	
	public void setBackgroundTransparent(){
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}
	
	public void setBackgroundGrayOver(){
		getWindow().setBackgroundDrawable(new ColorDrawable(0xAA888888));
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}
	
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	


	
}
