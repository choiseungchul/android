package com.mcproject.net.dialog;

import java.util.Calendar;

import com.mcproject.net.util.LogUtil;
import com.mcproject.ytfavorite_t.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class DatePickerDialog extends Dialog{

	private TextView dialog_title;
	private WheelView year, month, day;
	private Activity mActivity;
	private Runnable runn;
	private DateArrayAdapter date_month_adap ;
	private DateNumericAdapter date_year_adap, date_day_adap;
	private TextView dialog_setDate;
	OnWheelChangedListener listener;
	boolean isCurrent = false;

	public DatePickerDialog(Activity activity, boolean isCurrent) {
		super(activity , android.R.style.Theme_Translucent_NoTitleBar);
		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
		mActivity = activity;
		this.isCurrent = isCurrent;
		
		setContentView(R.layout.dialog_datepicker);
		
		dialog_title = (TextView)findViewById(R.id.dialog_title);
		year = (WheelView)findViewById(R.id.year);
		month = (WheelView)findViewById(R.id.month);
		day = (WheelView)findViewById(R.id.day);
		dialog_setDate = (TextView)findViewById(R.id.dialog_close);
		
		dialog_title.setText(mActivity.getString(R.string.srch_select_datepicker_title));
		
        setData();
	}
	
	protected DatePickerDialog(Context context) {
		super(context);
	}
	
	protected DatePickerDialog(Context context, int id) {
		super(context, id);
	}
	
	protected DatePickerDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	public void setDateInputListener(View.OnClickListener listen){
		dialog_setDate.setOnClickListener(listen);
	}
	
	private void setData(){
		Calendar calendar = Calendar.getInstance();
		
		listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };
		
		 // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = mActivity.getResources().getStringArray(R.array.datepicker_month);
        date_month_adap = new DateArrayAdapter(mActivity, months, curMonth);
        month.setViewAdapter(date_month_adap);
        if(!isCurrent){
        	year.setCurrentItem(0);
        }else{
        	year.setCurrentItem(curMonth );
        }
        month.addChangingListener(listener);
    
        // year
        int curYear = calendar.get(Calendar.YEAR);
        // 20≥‚¿¸≤® ∫Œ≈Õ
        date_year_adap = new DateNumericAdapter(mActivity, curYear - 20, curYear, 0);
        year.setViewAdapter(date_year_adap);
        LogUtil.D("is_current = " + isCurrent);
        LogUtil.D("current_year = " + curYear);
        LogUtil.D("year index = " + year.getCurrentItem());
        if(!isCurrent){
        	year.setCurrentItem(0);
        }else{
        	year.setCurrentItem(date_year_adap.getItemsCount() - 1);
        }
        year.addChangingListener(listener);
        
        //day        
        int curDay = calendar.get(Calendar.DAY_OF_MONTH);
        date_day_adap = new DateNumericAdapter(mActivity, 1, 31, 0);
        day.setViewAdapter(date_day_adap);
        if(!isCurrent){
        	day.setCurrentItem(0);
        }else{
        	day.setCurrentItem(curDay - 1);
        }
        day.addChangingListener(listener);
	}
	
	public void setTitle(String title){
		dialog_title.setText(title);
	}
	
	public void setUpdateDate(Runnable run){
		this.runn = run;
	}
	
	/**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
//        calendar.set(Calendar.MONTH, month.getCurrentItem());
//        
//        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        day.setViewAdapter(new DateNumericAdapter(mActivity, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
//        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
//        day.setCurrentItem(curDay - 1, true);
        
        runn.run();
    }
	
    public String getYear(){
    	return date_year_adap.getItemText(year.getCurrentItem()).toString();
    }
    
    public String getMonth(){
    	return date_month_adap.getItemText(month.getCurrentItem()).toString();
    }
    
    public String getDay(){
    	return date_day_adap.getItemText(day.getCurrentItem()).toString();
    }
    
	/**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    
    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;
        
        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(16);
        }
        
        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }
        
        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
	

}
