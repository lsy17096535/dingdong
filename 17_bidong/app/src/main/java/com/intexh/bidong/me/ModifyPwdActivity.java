package com.intexh.bidong.me;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

public class ModifyPwdActivity extends BaseTitleActivity {

	@ViewInject(R.id.txt_title_confirm)
	private TextView confirmView;
	@ViewInject(R.id.edit_modifypwd_oldpwd)
	private EditText oldPwdInput;
	@ViewInject(R.id.edit_modifypwd_pwd)
	private EditText pwdInput;
	@ViewInject(R.id.edit_modifypwd_confirmpwd)
	private EditText confirmPwdInput;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_modifypwd);
		setTitleText("修改密码");
		confirmView.setVisibility(View.VISIBLE);
		confirmView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doModifyPwd();				
			}
		});
	}

	private void doModifyPwd(){
		String oldPwd = oldPwdInput.getText().toString();
		String pwd = pwdInput.getText().toString();
		String confirmPwd = confirmPwdInput.getText().toString();
		if(StringUtil.isEmptyString(oldPwd)){
			showToast("请输入旧密码");
			return ;
		}
		if(!RegexUtil.checkPasswork(oldPwd)){
			showToast(R.string.error_pwd);
			return ;
		}
		if(StringUtil.isEmptyString(pwd)){
			showToast("请输入密码");
			return ;
		}
		if(!RegexUtil.checkPasswork(pwd)){
			showToast(R.string.error_pwd);
			return ;
		}
		if(StringUtil.isEmptyString(confirmPwd)){
			showToast("请输入确认密码");
			return ;
		}
		if(!pwd.equals(confirmPwd)){
			showToast("两次密码输入不一致，请重新输入");
			return ;
		}
		ModifyPwdRequest request = new ModifyPwdRequest();
		request.setUserid(UserUtils.getUserid());
		request.setPwd(pwd);
		request.setOldPwd(oldPwd);
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("密码修改成功");
				finish();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				showToast(reason);
				hideLoading();
			}
		});
		showLoading();
		request.getDataFromServer();
	}
	
}
