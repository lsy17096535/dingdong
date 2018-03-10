package com.intexh.bidong.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import com.intexh.bidong.R;

public class BaseTitleActivity extends BaseActivity {

	@ViewInject(R.id.image_title_back)
	private View backView;
	@ViewInject(R.id.txt_title_title)
	private TextView titleView;
	
	protected void handleLeft(){
		hideSoftKeyboard();
		finish();
	}
	
	protected void setTitleText(int resId){
		if(null != titleView){
			titleView.setText(resId);
		}
	}
	
	protected void setTitleText(String text){
		if(null != titleView){
			titleView.setText(text);
		}
	}
	
	@Override
	public void setContentView(int layoutResID) {
		RelativeLayout parentView = new RelativeLayout(this);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View titleView = mInflater.inflate(R.layout.title_layout, null);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		parentView.addView(titleView, titleParams);
		View contentView = mInflater.inflate(layoutResID, null);
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentParams.addRule(RelativeLayout.BELOW, titleView.getId());
		parentView.addView(contentView,contentParams);
		setContentView(parentView);
		ViewUtils.inject(this);
		backView = findViewById(R.id.image_title_back);
		this.titleView = (TextView)findViewById(R.id.txt_title_title);
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				handleLeft();
			}
		});
	}

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
	}
	
	
	
	
}
