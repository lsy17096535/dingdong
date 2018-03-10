package com.intexh.bidong.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;

import com.intexh.bidong.utils.UserUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class PromptActivity extends BaseActivity {

	@ViewInject(R.id.image_prompt_main)
	private ImageView promptView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_prompt);
		ViewUtils.inject(this);
		if(UserUtils.getUserInfo().getUser().getGender() == 1){	//女
			promptView.setImageResource(R.drawable.prompt);
		}else{	//男
			promptView.setImageResource(R.drawable.prompt_mail);
		}
		promptView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	
}
