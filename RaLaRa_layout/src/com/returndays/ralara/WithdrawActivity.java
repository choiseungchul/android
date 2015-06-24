package com.returndays.ralara;

import java.util.Hashtable;

import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.returndays.customview.DefaultDialog;
import com.returndays.http.HttpDocument;
import com.returndays.http.HttpDocument.HttpCallBack;
import com.returndays.ralara.conf.UrlDef;
import com.returndays.ralara.preference.Setting;
import com.returndays.ralara.util.LogUtil;
import com.returndays.ralara.util.MadUtil;

public class WithdrawActivity extends BaseActivity{

	TextView btn_title;
	LinearLayout back_btn;
	EditText pass, reason;
	String passStr = "", reasonStr = "";
	HttpDocument mhttp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdraw);

		mhttp = new HttpDocument(getApplicationContext());

		initUI();
		initData();

	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void initUI() {

		pass = (EditText)findViewById(R.id.withdraw_pass);
		reason = (EditText)findViewById(R.id.withdraw_reason);

		pass.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {	
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				passStr = s.toString();
			}
		});
		reason.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				reasonStr = s.toString();
			}
		});

		btn_title = (TextView)findViewById(R.id.save_btn);
		btn_title.setText("탈퇴하기");

		btn_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final DefaultDialog alert = new DefaultDialog(WithdrawActivity.this);
				alert.setTitle("회원탈퇴");
				alert.setMessage("탈퇴하시면 보유중인 알,골드,\n스크래치권이 모두 삭제됩니다.\n 정말 탈퇴하시겠습니까?");
				alert.setPositiveButton("확인", new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(passStr.equals("")){
							Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
						}else{
							Hashtable<String, String> params = new Hashtable<String, String>();
							params.put("user_seq", Setting.getUserSeq(getApplicationContext()));
							params.put("user_password", passStr);
							params.put("token_key", Setting.getToken(getApplicationContext()));
							params.put("withdraw_cause", reasonStr);

							mhttp.getDocument(UrlDef.USER_WITHDRAW, params, null, new HttpCallBack() {
								@Override
								public void onHttpCallBackListener(Document document) {
									// TODO Auto-generated method stub
									if(document.select("ResultTable").select("Result").text().equals("true")){
										LogUtil.W(document.toString());

										String Code = document.select("ReturnTable").select("Code").text();
										String Result = document.select("ReturnTable").select("Result").text();

										if(Code.equals("0")){
											MadUtil.clearAllPrefferencesWithraw(getApplicationContext());
											Toast.makeText(getApplicationContext(), "탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
											Intent ii = new Intent(getApplicationContext(), SplashActivity.class);
											ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											alert.dismiss();
											startActivity(ii);											
										}else if(Code.equals("-2002")){
											if(Result.equals("already_withdraw")){
												Setting.setToken(getApplicationContext(), "");
												Setting.setUserSeq(getApplicationContext(), "");
												Setting.setEmail(getApplicationContext(), "");
												Setting.setPassword(getApplicationContext(), "");

												Toast.makeText(getApplicationContext(), "이미 탈퇴하셨습니다.", Toast.LENGTH_LONG).show();
												alert.dismiss();
												Intent ii = new Intent(getApplicationContext(), LoginActivity.class);
												ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(ii);											
											}else if(Result.equals("user_not_match")){
												Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_LONG).show();
												alert.dismiss();
												pass.requestFocus();
											}else{
												Toast.makeText(getApplicationContext(), "Code : " + Code + " / Result : " + Result , Toast.LENGTH_LONG).show();
											}
										}
									}else{
										LogUtil.D("http error!!!");
									}
								}
							}, false);
						}
					}
				});
				alert.setNegativeButton("취소", new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alert.dismiss();
						//DestroyUtil.unbindReferences(v);
					}
				});

				alert.show();
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
		mhttp.threadStop();
		super.onDestroy();
	}
}
