package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.common.CareerPickerActivity;
import com.intexh.bidong.reg.CommitUserInfoRequest;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

public class AccountBaseInfoActivity extends BaseTitleActivity {
	@ViewInject(R.id.txt_baseinfo_mobile)
	private TextView mobileView;
	@ViewInject(R.id.txt_baseinfo_pwd)
	private TextView updatePwdBtn;
	@ViewInject(R.id.edit_baseinfo_nickname)
	private EditText nickNameInput;
	@ViewInject(R.id.edit_baseinfo_age)
	private EditText ageInput;
	@ViewInject(R.id.edit_baseinfo_height)
	private EditText heightInput;
	@ViewInject(R.id.edit_baseinfo_weight)
	private EditText weightInput;
	@ViewInject(R.id.edit_baseinfo_career)
	private TextView careerInput;
	@ViewInject(R.id.layout_baseinfo_career)
	private View careerLayout;
	@ViewInject(R.id.edit_baseinfo_username)
	private EditText usernameInput;
	@ViewInject(R.id.edit_baseinfo_sign)
	private EditText signInput;
	@ViewInject(R.id.btn_baseinfo_save)
	private Button saveBtn;
	private static final int REQUEST_CODE_CAREER = 1000;

	private User user;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_baseinfo);
		setTitleText(R.string.me_basicinfo);
		ViewUtils.inject(this);

		user = UserUtils.getUserInfo().getUser();
		mobileView.setText(user.getMobile());
		nickNameInput.setText(user.getAlias());
		ageInput.setText(String.valueOf(user.getAge()));
		heightInput.setText(user.getHeight());
		weightInput.setText(user.getWeight());
		careerInput.setText(user.getOccup());
		signInput.setText(user.getSignature());
		usernameInput.setText(user.getUsername());
		careerLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AccountBaseInfoActivity.this,CareerPickerActivity.class);
				intent.putExtra(CareerPickerActivity.CAREER, careerInput.getText().toString());
				startActivityForResult(intent, REQUEST_CODE_CAREER);
			}
		});
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				saveInfos();
			}
		});
		updatePwdBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountBaseInfoActivity.this,ModifyPwdActivity.class);
				startActivity(intent);
			}
		});
	}

	private void saveInfos(){
		CommitUserInfoRequest request = new CommitUserInfoRequest();
		String str = nickNameInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入昵称");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			str = str.trim();
			if(!user.getAlias().equals(str)) {
				request.setAlias(str);
			}
		}
		str = ageInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入年龄");
			return ;
		}
		if(!RegexUtil.checkDigit(str)){
			showToast(R.string.error_age);
			return ;
		}else{
			int age = Integer.parseInt(str);
			if(user.getAge() != age){
				if(age >= 18 && age <= 68){
					request.setAge(str);
				} else {
					showToast(R.string.error_age);
					return ;
				}
			}
		}

		str = heightInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入身高");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			str = str.trim();
			if(!user.getHeight().equals(str)) {
				if(!RegexUtil.checkDigit(str)){
					showToast(R.string.error_height);
					return ;
				}else{
					int height = Integer.parseInt(str);
					if (height >= 100 && height <= 300) {
						request.setHeight(str);
					} else {
						showToast(R.string.error_height);
						return;
					}
				}
			}
		}
		str = weightInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入体重");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			str = str.trim();
			if(!user.getWeight().equals(str)) {
				if(!RegexUtil.checkDigit(str)){
					showToast(R.string.error_weight);
					return ;
				}else{
					int weight = Integer.parseInt(str);
					if (weight >= 30 && weight <= 300) {
						request.setWeight(str);
					} else {
						showToast(R.string.error_weight);
						return;
					}
				}
			}
		}
		str = careerInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入职业");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			if(!user.getOccup().equals(str)) {
				request.setOccup(str);
			}
		}
		str = signInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入个性签名");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			str = str.trim();
			if(!user.getSignature().equals(str)) {
				request.setSignature(str);
			}
		}
		str = usernameInput.getText().toString();
		if(!StringUtil.isEmptyString(str)){
			str = str.trim();
			if(user.getUsername() == null || !user.getUsername().equals(str)) {
				request.setUsername(str);
			}
		}
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast(R.string.save_success);
				RegDataEntity userInfo = UserUtils.getUserInfo();
				User user = userInfo.getUser();
//				user.setBirthday(DateUtil.getUTCTimeString(DateUtil.getDayTime(birthdayTxt.getText().toString())));
				user.setAge(Integer.parseInt(ageInput.getText().toString()));
				user.setHeight(heightInput.getText().toString());
				user.setWeight(weightInput.getText().toString());
				user.setOccup(careerInput.getText().toString());
				user.setUsername(usernameInput.getText().toString());
				UserUtils.saveUserInfo(userInfo);
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.setUserId(UserUtils.getUserid());
		showLoading();
		request.getDataFromServer();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case REQUEST_CODE_CAREER:{
				String ss = data.getStringExtra(CareerPickerActivity.CAREER);
				careerInput.setText(ss);
				break;
			}
			}
		}
	}
	
}
