package com.intexh.bidong.reg;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.bidong.utils.DialogUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.common.UserLicenseActivity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.UserUtils;

public class RegActivity extends BaseTitleActivity implements OnClickListener{

	public static final String MODE = "MODE";
	public static final int MODE_REG = 1;
	public static final int MODE_FORGETPWD = 2;
	public static final int REQUEST_RESETPWD = 1000;
	
	@ViewInject(R.id.edit_reg_mobile)
	private EditText mobileInput;
	@ViewInject(R.id.edit_reg_varify)
	private EditText varifyInput;
	@ViewInject(R.id.edit_reg_password)
	private EditText passwordInput;
	@ViewInject(R.id.btn_reg_varify)
	private TextView varifyBtn;
	@ViewInject(R.id.image_reg_eye_ckeck)
	private ImageView eyeCheckView;
	@ViewInject(R.id.layout_invite_code)
	private View inviteCodeLayoutView;
	@ViewInject(R.id.edit_invite_code)
	private EditText inviteCodeInput;
	@ViewInject(R.id.btn_reg_next)
	private View nextBtn;
	@ViewInject(R.id.image_reg_license_check)
	private ImageView licenseCheckView;
	@ViewInject(R.id.txt_reg_license)
	private TextView licenseView;
	@ViewInject(R.id.layout_reg_license)
	private View licenseLayoutView;

	private static final int MAX_TIME = 90;
	private int VARIFY_TYPE = 1;
	private int mode = MODE_REG;
	private int timeCount = 0;

	private Handler mTimeHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(timeCount > 0){
				varifyBtn.setEnabled(false);
				varifyBtn.setText("重发（" + timeCount + "）");
				mTimeHandler.sendEmptyMessageDelayed(0, 1000);
				timeCount--;
			}else{
				timeCount = 0;
				varifyBtn.setEnabled(true);
				varifyBtn.setText("获取验证码");
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		ViewUtils.inject(this);
		if(null != getIntent()){
			mode = getIntent().getIntExtra(MODE, MODE_REG);
		}
		if(mode == MODE_REG){
			setTitleText(R.string.title_reg);
			VARIFY_TYPE = 0;
			inviteCodeLayoutView.setVisibility(View.VISIBLE);
		}else{
			setTitleText(R.string.title_forgetpwd);
			VARIFY_TYPE = 1;
			passwordInput.setHint(R.string.password_new_hint);
			licenseLayoutView.setVisibility(View.GONE);
		}
		DialogUtils.showDialog(RegActivity.this,"","尊敬的用户，欢迎使用本产品，根据国家相关规定抵制色情，低俗，暴力，政治等一切违规内容，一经发现将封停账号。\n" +
				"为了确保您的正常使用及账号安全。请尽快完成相关实名认证。\n" +
				"您在使用过程中遇到任何问题请搜索官方微信公众号：哔咚APP，通过公众号联系客服人员进行处理，还可通过公众号参与官方有奖送现金等活动。","知道了","",new DialogUtils.DialogImpl());
		eyeCheckView.setSelected(false);
		eyeCheckView.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		varifyBtn.setOnClickListener(this);
		licenseCheckView.setSelected(false);
		licenseCheckView.setOnClickListener(this);
		licenseView.setOnClickListener(this);
	}

	private void doSendVarify(){
		String mobile = mobileInput.getText().toString();
		if(!RegexUtil.checkMobile(mobile)){
			showToast(R.string.error_mobile);
			return ;
		}
		SendVarifyRequest request = new SendVarifyRequest();
		request.setMobile(mobile);
		request.setType(VARIFY_TYPE);
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast(R.string.error_sendvarify);
				timeCount = MAX_TIME;
				mTimeHandler.sendEmptyMessage(0);
//				if(LogUtils.allowD){
//					showToast(data);
//				}
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
	
	private void doReg(){
		String code = varifyInput.getText().toString();
		final String mobile = mobileInput.getText().toString();
		final String password = passwordInput.getText().toString();
		final String inviteCode = inviteCodeInput.getText().toString().trim();
		if(!RegexUtil.checkMobile(mobile)){
			showToast(R.string.error_mobile);
			return ;
		}
		if("".equals(code)){
			showToast(R.string.error_varify);
			return ;
		}
		if(!RegexUtil.checkPasswork(password)){
			showToast(R.string.error_pwd);
			return ;
		}
		if(licenseCheckView.isSelected()){
			showToast(R.string.error_license);
			return ;
		}

		if(!"".equals(inviteCode) && inviteCode.length() != 4){
			showToast(R.string.error_invitecode);
			return ;
		}

		CheckVarifyCodeRequest request = new CheckVarifyCodeRequest();
		request.setCode(code);
		request.setMobile(mobile);
		request.setType(VARIFY_TYPE);
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				if(mode == MODE_REG){ //注册
					if(!"".equals(inviteCode)){
						inviteLogReq(mobile, password, inviteCode);
					}
					else{
						commitRegReq(mobile, password);
					}
				}
				else{ //重置密码
					commitForgetPwdReq(mobile, password);
				}
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

	//提交注册请求
	private void commitRegReq(String mobile, String pwd){
		RegRequest request = new RegRequest();
		request.setNetworkListener(new CommonNetworkCallback<RegDataEntity>() {

			@Override
			public void onSuccess(RegDataEntity data) {
				hideLoading();
				UserUtils.saveUserInfo(data);
				Intent intent = new Intent(RegActivity.this,RegInformationActivity.class);
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
		request.getDataFromServer();
	}

	//提交邀请码记录请求
	private void inviteLogReq(final String mobile, final String pwd, String code){
		InviteCodeLogRequest request = new InviteCodeLogRequest();
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				commitRegReq(mobile, pwd);
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.setMobile(mobile);
		request.setCode(code);
		request.getDataFromServer();
	}

	//提交重置密码请求
	private void commitForgetPwdReq(String mobile, String pwd){
		ForgetpwdRequest request = new ForgetpwdRequest();
		request.setMobile(mobile);
		request.setPwd(pwd);
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
		request.getDataFromServer();
	}

	//跳到密码和确认密码 界面，合并之后此方法不需要
//	private void jumpToNextPage(){
//		Intent intent = new Intent(this,RegPasswordActivity.class);
//		intent.putExtra(MODE, mode);
//		intent.putExtra(RegPasswordActivity.MOBILE, mobileInput.getText().toString());
//		startActivityForResult(intent, REQUEST_RESETPWD);
//	}

	
	@Override
	protected void onActivityResult(int requestCode, int result, Intent data) {
		super.onActivityResult(requestCode, result, data);
		if(result == RESULT_OK){
			finish();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_reg_varify:{
			doSendVarify();
			break;
		}
		case R.id.btn_reg_next:{
			doReg();
			break;
		}
		case R.id.image_reg_eye_ckeck:{
			eyeCheckView.setSelected(!eyeCheckView.isSelected());
			if(eyeCheckView.isSelected()){
				passwordInput.setInputType(0x00000091); //明文
			}
			else{
				passwordInput.setInputType(0x00000081); //密文
			}
			break;
		}
		case R.id.image_reg_license_check:{
			licenseCheckView.setSelected(!licenseCheckView.isSelected());
			break;
		}
		case R.id.txt_reg_license:{
			Intent intent = new Intent(this,UserLicenseActivity.class);
			startActivity(intent);
			break;
		}
		}
	}
	
	
}
