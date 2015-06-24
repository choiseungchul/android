package com.returndays.ralara;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.util.XmlDom;
import com.example.android.bitmapfun.ui.RecyclingImageView;
import com.returndays.ralara.conf.Define;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.dto.HttpResultDto;
import com.returndays.ralara.http.HttpListener;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.DialogUtil;
import com.returndays.ralara.util.HttpUtil;
import com.returndays.ralara.util.LogUtil;

public class R2ScratchActivity extends BaseActivity{

	TextView r_count, mRCount_FIX, mR2Scratch_cnt, r2scratch_result_r, goto_collect_r;
	EditText mInput_R;
	LinearLayout back_btn;
	SeekBar mCount;
	HttpUtil mHttp;
	Button save_btn;
	RecyclingImageView ex_count_minus, ex_count_plus;
	int curr_r = 0;
	int curr_s = 0;
	int user_ex_r = 0;
	int user_ex_s = 0;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r2scratch);

		mHttp = new HttpUtil(this);

		initUI();
		initData();
	}

	private void initData() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user_seq", Setting.getUserSeq(getApplicationContext()));

		mHttp.httpExecute(UrlDef.GET_COUNTS, params, new HttpListener() {
			@Override
			public void onSuccess(XmlDom xml, HttpResultDto result) {

				LogUtil.D(xml.toString());

				if(xml.tag("ResultTable").tag("Result").text().equals("true")){

					String EGG = xml.tag("ReturnTable").tag("EGG").text();
					String SCRATCH = xml.tag("ReturnTable").tag("SCRATCH").text();

					mRCount_FIX.setText(EGG);
					r_count.setText(EGG);
					curr_r = Integer.parseInt(EGG);
					curr_s = Integer.parseInt(SCRATCH);
					int scratch = Integer.parseInt(EGG) / Define.EXCHANGE_SCRATCH_COUNT;
					user_ex_r = curr_r - (curr_r % Define.EXCHANGE_SCRATCH_COUNT);
					user_ex_s = scratch;
					mR2Scratch_cnt.setText(String.valueOf(scratch));
					r2scratch_result_r.setText(String.valueOf(curr_r - user_ex_r));

					mCount.setMax(Integer.parseInt(EGG) / Define.EXCHANGE_SCRATCH_COUNT);
					mCount.setProgress(Integer.parseInt(EGG) / Define.EXCHANGE_SCRATCH_COUNT);
				}
			}
		}, false);
	}

	private void initUI() {

		goto_collect_r = (TextView)findViewById(R.id.goto_collect_r);
		goto_collect_r.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent ii = new Intent(getApplicationContext(), SettingWebActivity.class);
				ii.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				ii.putExtra("url", UrlDef.HELP_COLLECT_R);
				ii.putExtra("title", "알 모으기");
				startActivity(ii);
			}
		});
		
		r2scratch_result_r = (TextView)findViewById(R.id.r2scratch_result_r);
		
		mCount = (SeekBar)findViewById(R.id.r2count_exchange);
		mCount.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				user_ex_r = progress * Define.EXCHANGE_SCRATCH_COUNT;
				user_ex_s = user_ex_r / Define.EXCHANGE_SCRATCH_COUNT;
				r_count.setText(Integer.toString(user_ex_r));
				r2scratch_result_r.setText(String.valueOf(curr_r - user_ex_r));
				mR2Scratch_cnt.setText(Integer.toString(user_ex_s));
			}
		});

		ex_count_minus = (RecyclingImageView)findViewById(R.id.ex_count_minus);
		ex_count_minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cur_prog = mCount.getProgress();
				if(cur_prog >= 0){
					mCount.setProgress(cur_prog - 1);
				}
			}
		});
		ex_count_plus = (RecyclingImageView)findViewById(R.id.ex_count_plus);
		ex_count_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int cur_prog = mCount.getProgress();
				if(cur_prog+Define.EXCHANGE_SCRATCH_COUNT <= curr_r){
					mCount.setProgress(cur_prog + 1);
				}
			}
		});

		save_btn = (Button)findViewById(R.id.save_btn);
		save_btn.setText("변경하기");
		save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(curr_r > Define.EXCHANGE_SCRATCH_COUNT-1 && user_ex_r > Define.EXCHANGE_SCRATCH_COUNT-1){
					DialogUtil.confirm(R2ScratchActivity.this, "알을 스크래치권으로 변경", user_ex_r +  "개의 알을 스크래치권 " + user_ex_s + "장 으로\n으로 변경하시겠습니까?", new OnClickListener(){
						@Override
						public void onClick(View v) {
							DialogUtil.alert.dismiss();
							if(user_ex_r % Define.EXCHANGE_SCRATCH_COUNT == 0){

								Map<String, Object> params = new HashMap<String, Object>();
								params.put( "user_seq", Setting.getUserSeq(getApplicationContext()));
								params.put("amount", user_ex_r);

								mHttp.httpExecute(UrlDef.EXCHANGE_SCRATCH, params, new HttpListener() {

									@Override
									public void onSuccess(XmlDom xml, HttpResultDto result) {
										

										if(xml.tag("ResultTable").tag("Result").text().equals("true")){
											String Code = xml.tag("ReturnTable").tag("Code").text();

											if(Code.equals("0")){
												curr_r = Integer.parseInt(xml.tag("EGG").text());
												curr_s = Integer.parseInt(xml.tag("SCRATCH").text());
												user_ex_r = curr_r - (curr_r % Define.EXCHANGE_SCRATCH_COUNT);
												user_ex_s = curr_r / Define.EXCHANGE_SCRATCH_COUNT;

												mRCount_FIX.setText(String.valueOf(curr_r));
												r_count.setText("0");
												mR2Scratch_cnt.setText("0");
												mCount.setMax(curr_r / Define.EXCHANGE_SCRATCH_COUNT);
												mCount.setProgress(0);
												r2scratch_result_r.setText(String.valueOf(curr_r));

												Toast.makeText(getApplicationContext(), "변경이 완료되었습니다.", Toast.LENGTH_LONG).show();

											}else{
												if(Code.equals("-2001")){
													Toast.makeText(getApplicationContext(), "현재 가지고있는 알 갯수를 초과하였습니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2002")){
													Toast.makeText(getApplicationContext(), "알이 모자랍니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2003")){
													Toast.makeText(getApplicationContext(), "알갯수가 맞지않습니다.", Toast.LENGTH_LONG).show();
												}else{
													Toast.makeText(getApplicationContext(), "스크래치권으로 변경하지 못했습니다.", Toast.LENGTH_LONG).show();
												}
											}
										}
									}
								}, true);


							}else{
								//user_ex_r -= 1;

								Map<String, Object> params = new HashMap<String, Object>();
								params.put( "user_seq", Setting.getUserSeq(getApplicationContext()));
								params.put("amount", user_ex_r);

								mHttp.httpExecute(UrlDef.EXCHANGE_SCRATCH, params, new HttpListener() {

									@Override
									public void onSuccess(XmlDom xml, HttpResultDto result) {
										

										if(xml.tag("ResultTable").tag("Result").text().equals("true")){
											String Code = xml.tag("ReturnTable").tag("Code").text();

											if(Code.equals("0")){
												curr_r = Integer.parseInt(xml.tag("EGG").text());
												curr_s = Integer.parseInt(xml.tag("SCRATCH").text());
												user_ex_r = curr_r - (curr_r % Define.EXCHANGE_SCRATCH_COUNT);
												user_ex_s = curr_r / Define.EXCHANGE_SCRATCH_COUNT;

												mRCount_FIX.setText(String.valueOf(curr_r));
												r_count.setText("0");
												mR2Scratch_cnt.setText("0");
												mCount.setMax(curr_r / Define.EXCHANGE_SCRATCH_COUNT);
												mCount.setProgress(0);
												r2scratch_result_r.setText(String.valueOf(curr_r));
												Toast.makeText(getApplicationContext(), "변경이 완료되었습니다.", Toast.LENGTH_LONG).show();
											}else{
												if(Code.equals("-2001")){
													Toast.makeText(getApplicationContext(), "현재 가지고있는 알 갯수를 초과하였습니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2002")){
													Toast.makeText(getApplicationContext(), "알이 모자랍니다.", Toast.LENGTH_LONG).show();
												}else if(Code.equals("-2003")){
													Toast.makeText(getApplicationContext(), "알갯수가 맞지않습니다.", Toast.LENGTH_LONG).show();
												}else{
													Toast.makeText(getApplicationContext(), "스크래치권으로 변경하지 못했습니다.", Toast.LENGTH_LONG).show();
												}
											}
										}
									}
								}, true);
							}
						}
					});
				}else{
					Toast.makeText(getApplicationContext(), "알이 부족합니다.", Toast.LENGTH_LONG).show();
				}

			}
		});

		r_count = (TextView)findViewById(R.id.my_change_count);

		mRCount_FIX = (TextView)findViewById(R.id.my_r_count_fixed);
		mR2Scratch_cnt = (TextView)findViewById(R.id.r2_scratch_count);
		back_btn = (LinearLayout)findViewById(R.id.sub_back_btn);

		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onDestroy(){
		
		if(ex_count_minus != null){
			ex_count_minus.setImageBitmap(null);
		}
		if(ex_count_plus != null){
			ex_count_plus.setImageBitmap(null);
		}
		
		super.onDestroy();
	}
}
