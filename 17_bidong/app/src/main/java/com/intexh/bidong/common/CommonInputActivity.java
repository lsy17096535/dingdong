package com.intexh.bidong.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.utils.StringUtil;

import com.lidroid.xutils.view.annotation.ViewInject;

public class CommonInputActivity extends BaseTitleActivity {

	public static final String TITLE = "TITLE";
	public static final String TIPS = "TIPS";
	public static final String VALUE = "VALUE";
	public static final String MAX_LENGTH = "MAX_LENGTH";
	@ViewInject(R.id.txt_commoninput_tips)
	private TextView tipsView;
	@ViewInject(R.id.edit_commoninput_input)
	private EditText input;
	@ViewInject(R.id.txt_title_confirm)
	private TextView confirmView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_commoninput);
		String title = getIntent().getStringExtra(TITLE);
		String tips = getIntent().getStringExtra(TIPS);
		String value = getIntent().getStringExtra(VALUE);
		int maxLength = getIntent().getIntExtra(MAX_LENGTH, 100);
		setTitleText(title);
		confirmView.setVisibility(View.VISIBLE);
		tipsView.setText(tips);
		input.setText(value);
		InputFilter[] fArray = new InputFilter[1];
		fArray[0] = new InputFilter.LengthFilter(maxLength);
		input.setFilters(fArray);
		confirmView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String value = input.getText().toString();
				if(StringUtil.isEmptyString(value)){
					showToast("请输入");
					return ;
				}
				Intent intent = new Intent();
				intent.putExtra(VALUE, value);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	

	
	
}
