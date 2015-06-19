package com.mcproject.net.customview;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcproject.net.adapter.AutoCompleteAdapter;
import com.mcproject.net.conf.UrlDef;
import com.mcproject.net.dto.MySearchDto;
import com.mcproject.net.util.DbQueryUtil;
import com.mcproject.net.util.LogUtil;
import com.mcproject.net.util.McUtil;
import com.mcproject.net.util.StringUtil;
import com.mcproject.ytfavorite_t.R;
import com.nineoldandroids.animation.ValueAnimator;

public class CustomEditText extends AutoCompleteTextView{

	private EditTextImeBackListener mOnImeBack;
	private int SEARCH_QUERY_MARGIN_RIGHT;
	Runnable runn;
	Context ctx;
	
	AutoCompleteAdapter mAdap;
	String latest_text = "";
	
	RequestQueue reqQ;
	Listener<String> listener;
	ErrorListener error;
	String Q_TAG = "customEditText";
	
	boolean isFirst = true;

	public interface EditTextImeBackListener
	{
		public void onBackEvent();
	}
	
	private void init(){		
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
					
					if(mAdap == null){
						mAdap = new AutoCompleteAdapter(ctx, searchList);
					}else{
						mAdap.setData(searchList);
						mAdap.notifyDataSetChanged();
					}
					
					showDropDown();
					
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
		
		addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				if(CustomEditText.this.getThreshold() < s.length()){
					return;
				}
				
				if(!CustomEditText.this.getText().toString().equals("")){
					String encoded_query = "";
					try {
						encoded_query = URLEncoder.encode(CustomEditText.this.getText().toString(), "UTF-8");
					} catch (UnsupportedEncodingException e) {
						LogUtil.E(e.toString());
					}
					
					String url = String.format(UrlDef.GET_AUTO_SEARCH, encoded_query);
					
					LogUtil.I("auto complete Text = " + url);
					
					StringRequest req = new StringRequest(url, listener, error);			
					req.setTag(Q_TAG);
					
					reqQ.cancelAll(Q_TAG);
					reqQ.add(req);	
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
			}
		});
	}
	
	// return true로 하면 항상 리스트 팝업 보이게함 
	@Override
	public boolean enoughToFilter() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public CustomEditText(Context context) {
		super(context);
		ctx = context;
		SEARCH_QUERY_MARGIN_RIGHT = (int) McUtil.getDpToPixel(context, 55);
		init();
	}

	public CustomEditText(Context context, AttributeSet attr) {
		super(context, attr);
		// TODO Auto-generated constructor stub
		ctx = context;
		SEARCH_QUERY_MARGIN_RIGHT = (int) McUtil.getDpToPixel(context, 55);
		init();
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
//		LogUtil.D("keyboard down..");
		// 애니메이션 
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) 
            	mOnImeBack.onBackEvent();
            srch_query_slide_left();
            clearFocus();
        }else{
        }
        return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		srch_query_slide_right();
		if(isFirst){
			isFirst = false;
			setAutoText();
		}
		return super.onTouchEvent(event);
	}
	
	private void setAutoText(){		
		ArrayList<MySearchDto> mysearchList = DbQueryUtil.getSearchListNormal(ctx, "");
		ArrayList<String> searchList = new ArrayList<String>();
		if(mysearchList.size() > 0){
			for(int i = 0 ; i < mysearchList.size() ; i++){
				searchList.add(mysearchList.get(i).search_text);
			}			
		}else{
			LogUtil.D("srch_query list empty ");
		}
		
		mAdap = new AutoCompleteAdapter(ctx, searchList);
		setAdapter(mAdap);
		
		if(!isPopupShowing()){
			showDropDown();
		}
	}
	
	@Override
	public void onEditorAction(int actionCode) {
		if(actionCode == EditorInfo.IME_ACTION_SEARCH){
			if(!this.getText().toString().equals("")){
				srch_query_slide_left();
				clearFocus();
				runn.run();
			}else{
				Toast.makeText(ctx, ctx.getString(R.string.srch_alert), Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void setImeAction(Runnable run){
		runn = run;
	}
	
	public void srch_query_slide_left(){
		final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
		
		LogUtil.D("slide left = rM = " + params.rightMargin);
		
		ValueAnimator animation = ValueAnimator.ofInt(params.rightMargin, SEARCH_QUERY_MARGIN_RIGHT);
		animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		    @Override
		    public void onAnimationUpdate(ValueAnimator valueAnimator)
		    {
	    		params.rightMargin = (Integer) valueAnimator.getAnimatedValue();
		        setLayoutParams(params);
		      
		    }
		});
		animation.setDuration(300);
		animation.start();
	}
	
	public void srch_query_slide_right(){
		final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
		
		LogUtil.D("slide right = rM = " + params.rightMargin);
		
		ValueAnimator animation = ValueAnimator.ofInt(params.rightMargin, 0);
		animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
		    @Override
		    public void onAnimationUpdate(ValueAnimator valueAnimator)
		    {
	    		params.rightMargin = (Integer) valueAnimator.getAnimatedValue();
		        setLayoutParams(params);
		    }
		});
		animation.setDuration(300);
		animation.start();
	}
	
	public void setOnEditTextImeBackListener(EditTextImeBackListener listener) {
        mOnImeBack = listener;
    }
	
	
}
