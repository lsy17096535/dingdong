package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.common.CommonInputActivity;
import com.intexh.bidong.reg.CommitUserInfoRequest;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.utils.UserUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

public class UserInfoActivity extends BaseTitleActivity {

	@ViewInject(R.id.txt_userinfo_nickname)
	private TextView nicknameTxt;
	@ViewInject(R.id.txt_userinfo_mobile)
	private TextView mobileView;
	@ViewInject(R.id.layout_userinfo_pwd)
	private View pwdLayout;
	@ViewInject(R.id.layout_userinfo_nickname)
	private View nicknameLayout;
	
	private static final int REQUEST_NICKNAME = 100;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_userinfo);
		setTitleText("账号");
		RegDataEntity entity = UserUtils.getUserInfo();
		mobileView.setText(entity.getUser().getMobile());
		nicknameTxt.setText(entity.getUser().getAlias());
		nicknameLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this, CommonInputActivity.class);
				intent.putExtra(CommonInputActivity.TITLE, "修改昵称");
				intent.putExtra(CommonInputActivity.TIPS, "昵称");
				intent.putExtra(CommonInputActivity.MAX_LENGTH, 5);
				intent.putExtra(CommonInputActivity.VALUE, UserUtils.getUserInfo()
						.getUser().getAlias());
				startActivityForResult(intent, REQUEST_NICKNAME);
			}
		});
		pwdLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(UserInfoActivity.this,ModifyPwdActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK == resultCode){
			switch(requestCode){
			case REQUEST_NICKNAME:{
				final String nickName = data
						.getStringExtra(CommonInputActivity.VALUE);
				CommitUserInfoRequest request = new CommitUserInfoRequest();
				request.setUserId(UserUtils.getUserid());
				request.setAlias(nickName);
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						RegDataEntity userInfo = UserUtils.getUserInfo();
						userInfo.getUser().setAlias(nickName);
						;
						UserUtils.saveUserInfo(userInfo);
						nicknameTxt.setText(nickName);
						showToast("昵称修改成功");
						hideLoading();
						setResult(RESULT_OK);
					}

					@Override
					public void onFailed(int code, HttpException error,
							String reason) {
						hideLoading();
						showToast(reason);
					}
				});
				request.getDataFromServer();
				break;
			}
			}
		}
	}

}
