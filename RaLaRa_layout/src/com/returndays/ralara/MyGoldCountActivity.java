package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;

public class MyGoldCountActivity extends BaseActivity{

	TextView my_m_count, mMoneyCount_FIX, my_r_cnt, m2r_result_gold, goto_collect_m;
	EditText mInput_R;
	LinearLayout back_btn;
	RecyclingImageView m2_cnt_plus_btn, m2_cnt_minus_btn;
	HttpDocument mHttp;
	Button save_btn;
	SeekBar m2r_exchange;
	RecyclingImageView exg_count_minus, exg_count_plus;
	int curr_m = 0;
	int curr_r = 0;
	int user_ex_m = 0;
	int user_ex_r = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.money2r);

		mHttp = new HttpDocument(this);

		initUI();
		initData();

	}

	private void initData() {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttp.getDocument(UrlDef.GET_COUNTS, params, null, new HttpCallBack() {

			@Override
			public void onHttpCallBackListener(Document document) {
				// TODO Auto-generated method stub
				if(document.select("ResultTable").select("Result").text().equals("true")){

					String EGG = document.select("ReturnTable").select("EGG").text();
					String GOLD = document.select("ReturnTable").select("GOLD").text();
					//테스트 코드
//										EGG = "2";
//										GOLD = "22000";

					mMoneyCount_FIX.setText(GOLD);

					curr_m = Integer.parseInt(GOLD);
					curr_r = Integer.parseInt(EGG);

					user_ex_m = curr_m;
					user_ex_r = curr_m / Define.EXCHANGE_EGG_COUNT;

					my_m_count.setText(String.valueOf(user_ex_m));
					my_r_cnt.setText(String.valueOf(user_ex_m / Define.EXCHANGE_EGG_COUNT));

					m2r_exchange.setMax(Integer.parseInt(GOLD) / Define.EXCHANGE_EGG_COUNT);
					m2r_exchange.setProgress(Integer.parseInt(GOLD) / Define.EXCHANGE_EGG_COUNT);
				}
			}
		}, false);

		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("변경하기");
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(user_ex_m >= Define.EXCHANGE_EGG_COUNT && user_ex_m >=Define.EXCHANGE_EGG_COUNT  ){
					DialogUtil.confirm(MyGoldCountActivity.this, "골드를 알로 변경", user_ex_m + "골드를 알" +user_ex_r + "개로 변경하시겠습니까?" , new OnClickListener() {
						@Override
						public void onClick(View v) {
							DialogUtil.alert.dismiss();

							if(user_ex_m % Define.EXCHANGE_EGG_COUNT == 0){

								Hashtable<String, String> params = new Hashtable<String, String>();
								params.put( "user_seq", Setting.getUserSeq(getApplicationContext()));
								params.put("amount", String.valueOf(user_ex_m));

								mHttp.getDocument(UrlDef.EXCHANGE_EGG, params, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										// TODO Auto-generated method stub
										if(document.select("ResultTable").select("Result").text().equals("true")){
											String Code = document.select("ReturnTable").select("Code").text();

											if(Code.equals("0")){
												curr_m = Integer.parseInt(document.select("GOLD").text());
												curr_r = Integer.parseInt(document.select("EGG").text());

												user_ex_m = curr_m;
												user_ex_r = curr_m / Define.EXCHANGE_EGG_COUNT;

												mMoneyCount_FIX.setText(String.valueOf(curr_m));
												my_m_count.setText("0");
												my_r_cnt.setText("0");
												m2r_exchange.setMax(curr_m / Define.EXCHANGE_EGG_COUNT);
												m2r_exchange.setProgress(0); 
												Toast.makeText(getApplicationContext(), "변경이 완료되었습니다.", Toast.LENGTH_LONG).show();
												
											}else{
												if(Code.equals("-2001")){
													Toast.makeText(getApplicationContext(), "현재 가지고있는 골드를 초과하였습니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2002")){
													Toast.makeText(getApplicationContext(), "골드가 모자랍니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2003")){
													Toast.makeText(getApplicationContext(), "골드가 맞지않습니다.", Toast.LENGTH_LONG).show();
												}else{
													Toast.makeText(getApplicationContext(), "알로 변경하지 못했습니다.", Toast.LENGTH_LONG).show();
												}
											}

										}
									}
								}, true);


							}else{
								user_ex_m -= user_ex_m % Define.EXCHANGE_EGG_COUNT;

								Hashtable<String, String> params = new Hashtable<String, String>();
								params.put( "user_seq", Setting.getUserSeq(getApplicationContext()));
								params.put("amount", String.valueOf( user_ex_m));

								mHttp.getDocument(UrlDef.EXCHANGE_EGG, params, null, new HttpCallBack() {
									@Override
									public void onHttpCallBackListener(Document document) {
										// TODO Auto-generated method stub
										if(document.select("ResultTable").select("Result").text().equals("true")){
											String Code = document.select("ReturnTable").select("Code").text();

											if(Code.equals("0")){
												curr_m = Integer.parseInt(document.select("GOLD").text());
												curr_r = Integer.parseInt(document.select("EGG").text());

												user_ex_m = curr_m;
												user_ex_r = curr_m / Define.EXCHANGE_EGG_COUNT;

												mMoneyCount_FIX.setText(String.valueOf(curr_m));
												my_m_count.setText("0");
												my_r_cnt.setText("0");
												m2r_exchange.setMax(curr_m / Define.EXCHANGE_EGG_COUNT);
												m2r_exchange.setProgress(0); 
												Toast.makeText(getApplicationContext(), "변경이 완료되었습니다.", Toast.LENGTH_LONG).show();
												m2r_result_gold.setText(String.valueOf(curr_m - user_ex_m));
												
											}else{
												if(Code.equals("-2001")){
													Toast.makeText(getApplicationContext(), "현재 가지고있는 골드를 초과하였습니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2002")){
													Toast.makeText(getApplicationContext(), "골드가 모자랍니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2003")){
													Toast.makeText(getApplicationContext(), "골드가 맞지않습니다.", Toast.LENGTH_LONG).show();
												}else{
													Toast.makeText(getApplicationContext(), "알로 변경하지 못했습니다.", Toast.LENGTH_LONG).show();
												}
											}
										}
									}
								}, true);
							}
						}
					});	
				}else{
					Toast.makeText(getApplicationContext(), "골드가 모자랍니다.", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void initUI() {
		
		goto_collect_m = (TextView)findViewById(R.id.goto_collect_m);
		goto_collect_m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), SettingWebActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				ii.putExtra("url", UrlDef.HELP_COLLECT_G);
				ii.putExtra("title", "골드 모으기");
				startActivity(ii);	
			}
		});

		m2r_result_gold = (TextView)findViewById(R.id.m2r_result_gold);
		mMoneyCount_FIX = (TextView)findViewById(R.id.my_moneycount);
		my_m_count = (TextView)findViewById(R.id.my_gold_count);
		my_r_cnt = (TextView)findViewById(R.id.my_r_cnt);

		m2r_exchange = (SeekBar)findViewById(R.id.m2r_exchange);
		m2r_exchange.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				user_ex_m = progress * Define.EXCHANGE_EGG_COUNT;
				user_ex_r = user_ex_m / Define.EXCHANGE_EGG_COUNT;
				my_m_count.setText(Integer.toString(user_ex_m));
				my_r_cnt.setText(Integer.toString(user_ex_r));
				m2r_result_gold.setText(String.valueOf(curr_m - user_ex_m));
			}
		});

		exg_count_minus = (RecyclingImageView)findViewById(R.id.exg_count_minus);
		exg_count_minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cur_prog = m2r_exchange.getProgress();
				if(cur_prog - 1 >=0 ){
					m2r_exchange.setProgress(cur_prog - 1);					
				}
			}
		});
		exg_count_plus = (RecyclingImageView)findViewById(R.id.exg_count_plus);
		exg_count_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cur_prog = m2r_exchange.getProgress();
				if((cur_prog + 1) * Define.EXCHANGE_EGG_COUNT <= curr_m){
					m2r_exchange.setProgress(cur_prog + 1);
				}
			}
		});

		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);

		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mHttp.threadStop();
		
		if(m2_cnt_plus_btn != null){
			m2_cnt_plus_btn.setImageBitmap(null);
		}
		if(m2_cnt_minus_btn != null){
			m2_cnt_minus_btn.setImageBitmap(null);
		}
		if(exg_count_minus != null){
			exg_count_minus.setImageBitmap(null);
		}
		if(exg_count_plus != null){
			exg_count_plus.setImageBitmap(null);
		}
		
		super.onDestroy();
	}

}
