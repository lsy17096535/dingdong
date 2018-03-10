package com.intexh.bidong.reg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.UserUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

public class RegPasswordActivity extends BaseTitleActivity implements OnClickListener {

	public static final String MOBILE = "MOBILE";
	
	@ViewInject(R.id.edit_regpwd_pwd)
	private EditText pwdInput;
	@ViewInject(R.id.edit_regpwd_confirmpwd)
	private EditText confirmInput;
	@ViewInject(R.id.btn_regpwd_next)
	private View nextView;
	private String mobile;
	private int mode = RegActivity.MODE_REG;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_regpwd);
		ViewUtils.inject(this);
		mobile = getIntent().getStringExtra(MOBILE);
		mode = getIntent().getIntExtra(RegActivity.MODE, RegActivity.MODE_REG);
		if(mode == RegActivity.MODE_REG){
			setTitleText(R.string.title_password);
		}else{
			setTitleText(R.string.title_resetpwd);
		}
		nextView.setOnClickListener(this);
	}

	private void doReg(){
		String pwd = pwdInput.getText().toString();
		String confirmPwd = confirmInput.getText().toString();
		if(!RegexUtil.checkPasswork(pwd)){
			showToast(R.string.error_pwd);
			return ;
		}
		if(!RegexUtil.checkPasswork(confirmPwd)){
			showToast(R.string.error_confirmpwd);
			return ;
		}
		if(!pwd.equals(confirmPwd)){
			showToast(R.string.error_difpwd);
			return ;
		}
		RegRequest request = new RegRequest();
		request.setNetworkListener(new CommonNetworkCallback<RegDataEntity>() {

			@Override
			public void onSuccess(RegDataEntity data) {
				UserUtils.saveUserInfo(data);
				Intent intent = new Intent(RegPasswordActivity.this,RegInformationActivity.class);
				startActivity(intent);
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				showToast(reason);
			}
		});
		request.setMobile(mobile);
		request.setPassword(pwd);
		showLoading();
		request.getDataFromServer();
	}
	
	private void doModifyPwd(){
		String pwd = pwdInput.getText().toString();
		String confirmPwd = confirmInput.getText().toString();
		if(!RegexUtil.checkPasswork(pwd)){
			showToast(R.string.error_pwd);
			return ;
		}
		if(!RegexUtil.checkPasswork(confirmPwd)){
			showToast(R.string.error_confirmpwd);
			return ;
		}
		if(!pwd.equals(confirmPwd)){
			showToast(R.string.error_difpwd);
			return ;
		}
		ForgetpwdRequest request = new ForgetpwdRequest();
		request.setMobile(mobile);
		request.setPwd(pwdInput.getText().toString());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("密码修改成功");
				setResult(RESULT_OK);
				finish();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_regpwd_next:{
			if(mode == RegActivity.MODE_REG){
				doReg();
			}else{
				doModifyPwd();
			}
			break;
		}
		}
	}

	
	
}
