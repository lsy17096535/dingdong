package com.intexh.bidong.reg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.io.File;
import java.util.ArrayList;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleLocationActivity;
import com.intexh.bidong.common.CareerPickerActivity;
import com.intexh.bidong.crop.ClipImageActivity;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class RegInformationActivity extends BaseTitleLocationActivity implements OnClickListener{
	
	@ViewInject(R.id.image_reginfo_avatar)
	private CircleImageView avatarView;
	@ViewInject(R.id.btn_reginf_male)
	private View maleView;
	@ViewInject(R.id.btn_reginf_female)
	private View femaleView;
	@ViewInject(R.id.btn_reginfo_next)
	private View nextView;
	@ViewInject(R.id.edit_reginfo_nickname)
	private EditText nickNameInput;
	@ViewInject(R.id.edit_reginfo_age)
	private EditText ageInput;
	@ViewInject(R.id.edit_reginfo_height)
	private EditText heightInput;
	@ViewInject(R.id.edit_reginfo_weight)
	private EditText weightInput;
	@ViewInject(R.id.txt_reginfo_career)
	private TextView careerInput;
	@ViewInject(R.id.edit_reginfo_sign)
	private EditText signInput;
	@ViewInject(R.id.layout_reginfo_career)
	private View careerLayout;
	private String avatarUrl = null;
	private String avatarName = null;
	private static final int REQUEST_CODE_CAREER = 10000;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_reginformation);
		ViewUtils.inject(this);
		View view = findViewById(R.id.image_title_back);
		view.setVisibility(View.INVISIBLE);
		setTitleText("注册-基本信息");
		RegDataEntity userInfo = UserUtils.getUserInfo();
		if(null != userInfo){
			User user = userInfo.getUser();
			if(1 == userInfo.getUser().getGender()){
				maleView.setSelected(false);
				femaleView.setSelected(true);
			}else{
				maleView.setSelected(true);
				femaleView.setSelected(false);
			}
			nickNameInput.setText(user.getAlias());
			if(user.getAge() == 0 || user.getAge() == 18){
				ageInput.setText("");
			}else{
				ageInput.setText(String.valueOf(user.getAge()));
			}
			if("0".equals(user.getHeight())){
				heightInput.setText("");
			}else{
				heightInput.setText(user.getHeight());
			}
			if("0".equals(user.getWeight())){
				weightInput.setText("");
			}else{
				weightInput.setText(user.getWeight());
			}
			careerInput.setText(user.getOccup());
			careerInput.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(RegInformationActivity.this,CareerPickerActivity.class);
					intent.putExtra(CareerPickerActivity.CAREER, careerInput.getText().toString());
					startActivityForResult(intent, REQUEST_CODE_CAREER);
				}
			});
			signInput.setText(user.getSignature());
			ImageUtils.loadAvatarDefaultImage(user.getAvatar(), avatarView);
		}else{
			maleView.setSelected(true);
			femaleView.setSelected(false);
		}
		avatarView.setOnClickListener(this);
		maleView.setOnClickListener(this);
		femaleView.setOnClickListener(this);
		nextView.setOnClickListener(this);
	}
	
	
	private void uploadAvatarPic(File file){
		showLoading();
		PPStarApplication.wantuService.upload(file, null, new UploadListener() {
			
			@Override
			public void onUploading(UploadTask arg0) {}
			
			@Override
			public void onUploadFailed(UploadTask arg0, FailReason arg1) {
				hideLoading();
				showToast(R.string.error_uploadavatar);
			}
			
			@Override
			public void onUploadComplete(UploadTask arg0) {
				avatarUrl = arg0.getResult().url;
				avatarName = arg0.getResult().name;
				hideLoading();
				ImageUtils.loadAvatarDefaultImage(avatarUrl, avatarView);
			}
			
			@Override
			public void onUploadCancelled(UploadTask arg0) {
				hideLoading();
				showToast(R.string.error_uploadavatar);
			}
		}, BucketHelper.getInstance().getAvatarBucketToken());
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
				case ClipImageActivity.CROP_RESULT_CODE: {
					String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
					uploadAvatarPic(new File(path));
					break;
				}
				case ClipImageActivity.START_ALBUM_REQUESTCODE:
					//使用MultiImageSelector库选择图片，避免android不同版本取相册图片的差异
					ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					ClipImageActivity.startCropImageActivity(this, paths.get(0));
					break;
				case ClipImageActivity.CAMERA_WITH_DATA:
					// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
					ClipImageActivity.startCropImageActivity(this, ClipImageActivity.TMP_PATH);
					break;
			}
		}
	}

	private void commitInfo(){
		CommitUserInfoRequest request = new CommitUserInfoRequest();
		if(femaleView.isSelected()){
			request.setGender("1");
		}else{
			request.setGender("2");
		}
		if(null == avatarUrl && null == UserUtils.getUserInfo().getUser().getAvatar()){
			showToast("请设置头像");
			return ;
		}
		if(null != avatarUrl){
			request.setAvatar(avatarName);
		}
		String str = nickNameInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入昵称");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			request.setAlias(str);
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
			if(age >= 18 && age <= 68){
				request.setAge(str);
			}else{
				showToast(R.string.error_age);
				return ;
			}
		}

		str = heightInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入身高");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			if(!RegexUtil.checkDigit(str)){
				showToast(R.string.error_height);
				return ;
			}else{
				int height = Integer.parseInt(str);
				if(height >= 100 && height <= 300){
					request.setHeight(str);
				}else{
					showToast(R.string.error_height);
					return ;
				}
			}
		}
		str = weightInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入体重");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			if(!RegexUtil.checkDigit(str)){
				showToast(R.string.error_weight);
				return ;
			}else{
				int weight = Integer.parseInt(str);
				if(weight >= 30 && weight <= 300){
					request.setWeight(str);
				}else{
					showToast(R.string.error_weight);
					return ;
				}
			}
		}
		str = careerInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入职业");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			request.setOccup(str);
		}
		str = signInput.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入签名");
			return ;
		}
		if(!StringUtil.isEmptyString(str)){
			request.setSignature(str);
		}
		if(null != locationInfo){
			request.setProvince(locationInfo.province);
			request.setCity(locationInfo.city);
			request.setDistrict(locationInfo.district);
			request.setLocation(String.format("%.8f", locationInfo.location.longitude) + "," + String.format("%.8f", locationInfo.location.latitude));
		}
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				RegDataEntity userInfo = UserUtils.getUserInfo();
				if(null != userInfo){
					User user = userInfo.getUser();
					user.setAlias(nickNameInput.getText().toString());
					if(null != avatarName){
						user.setAvatar(avatarName);
					}
					user.setAge(Integer.parseInt(ageInput.getText().toString()));
					user.setHeight(heightInput.getText().toString());
					user.setWeight(weightInput.getText().toString());
					user.setOccup(careerInput.getText().toString());
					user.setSignature(signInput.getText().toString());
					if(maleView.isSelected()){
						user.setGender(2);
					}else{
						user.setGender(1);
					}
					userInfo.setUser(user);
					UserUtils.saveUserInfo(userInfo);
					setResult(RESULT_OK);
					showToast("注册成功");
					finish();
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.setUserId(UserUtils.getUserInfo().getUser().getId());
		showLoading();
		request.getDataFromServer();
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
			case R.id.btn_reginf_male:{
				if(!maleView.isSelected()){
					maleView.setSelected(!maleView.isSelected());
					femaleView.setSelected(!femaleView.isSelected());
				}
				break;
			}
			case R.id.btn_reginf_female:{
				if(!femaleView.isSelected()){
					maleView.setSelected(!maleView.isSelected());
					femaleView.setSelected(!femaleView.isSelected());
				}
				break;
			}
			case R.id.btn_reginfo_next:{
				commitInfo();
				break;
			}
			case R.id.image_reginfo_avatar:{
				ActionSheet.createBuilder(this, getSupportFragmentManager()).
				setCancelButtonTitle(R.string.common_cancel).
				setOtherButtonTitles(getResources().getString(R.string.common_takecamera),getResources().getString(R.string.common_photo)).
				setListener(new ActionSheetListener() {

					@Override
					public void onOtherButtonClick(ActionSheet actionSheet, int index) {
						switch(index){
							case 0:{
								ClipImageActivity.startCapture(RegInformationActivity.this);
								break;
							}
							case 1:{
//								ClipImageActivity.startAlbum(RegInformationActivity.this);
								ClipImageActivity.starImageSelector(RegInformationActivity.this);
								break;
							}
						}
					}

					@Override
					public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}
				}).show();
				break;
			}
		}
	}

	@Override
	protected void didUpdateLocation(LocationInfo info) {
		
	}

}
