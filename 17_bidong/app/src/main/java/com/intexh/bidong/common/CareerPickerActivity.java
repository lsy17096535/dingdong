package com.intexh.bidong.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class CareerPickerActivity extends BaseActivity {

	public static final String CAREER = "CAREER";
	public static final String MODE = "MODE";

	@ViewInject(R.id.wheel_careerpicker_wheel)
	private WheelView wheelView;
	@ViewInject(R.id.txt_careerpicker_cancel)
	private TextView cancelView;
	@ViewInject(R.id.txt_careerpicker_confirm)
	private TextView confirmView;
	private String[] careears = {"职员","学生","模特","演员","空乘","教师",
								"医护","歌手","车手","经理人","商人","军人","其他"};
	private String[] cups = {"A","B","C","D","E","F","G"};
	private int mode = 0;		//0选择职业，1选择罩杯
	private int index = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_careerpicker);
		ViewUtils.inject(this);
		mode = getIntent().getIntExtra(MODE,0);
		String ss = getIntent().getStringExtra(CAREER);
		if(null != ss){
			String[] array = careears;
			if(0 == mode){
				array = careears;
			}else{
				array = cups;
			}
			for(int i=0;i<array.length;i++){
				if(ss.equals(array[i])){
					index = i;
					break;
				}
			}
		}
		cancelView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		confirmView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				if(0 == mode){
					intent.putExtra(CAREER, careears[index]);
				}else{
					intent.putExtra(CAREER, cups[index]);
				}
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		wheelView.setVisibleItems(5);
		wheelView.setViewAdapter(new AbstractWheelTextAdapter(this) {

			@Override
			public int getItemsCount() {
				if (0 == mode) {
					return careears.length;
				}else{
					return cups.length;
				}
			}

			@Override
			protected CharSequence getItemText(int index) {
				if(0 == mode){
					return careears[index];
				}else{
					return cups[index];
				}
			}

		});
		wheelView.setCurrentItem(index);
		wheelView.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				index = newValue;
			}
		});
		
	}

	
	
}
