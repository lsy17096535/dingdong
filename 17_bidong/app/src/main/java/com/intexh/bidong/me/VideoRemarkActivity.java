package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.utils.StringUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class VideoRemarkActivity extends BaseActivity {

	public static final String REMARK = "REMARK";
	
	@ViewInject(R.id.edit_videoremark_remark)
	private EditText remarkInput;
	@ViewInject(R.id.btn_videoremark_complete)
	private Button completeBtn;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_videoremark);
		ViewUtils.inject(this);
		completeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String ss = remarkInput.getText().toString();
				if(StringUtil.isEmptyString(ss)){
					showToast("请输入");
					return ;
				}
				Intent intent = new Intent();
				intent.putExtra(REMARK, ss);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	

}
