package com.returndays.ralara;

import com.winsontan520.WScratchView;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class testScratchActivity extends Activity {
	private WScratchView scratchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scratch_test);
		
		scratchView = (WScratchView) findViewById(R.id.scratch_view);
	}

	

	public void onClickHandler(View view){
		switch(view.getId()){
		case R.id.reset_button:
			scratchView.resetView();
			break;
		}
	}
}
