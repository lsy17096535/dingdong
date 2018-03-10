package com.intexh.bidong.me;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.common.UserFangpianActivity;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.userentity.VersionUpdateEntity;
import com.intexh.bidong.utils.VersionManager;

public class AboutActivity extends BaseTitleActivity {

	@ViewInject(R.id.txt_about_update)
	private TextView updateView;
	@ViewInject(R.id.txt_about_version)
	private TextView versionView;
	@ViewInject(R.id.txt_about_helper)
	private TextView helperView;
	@ViewInject(R.id.txt_about_fangpian)
	private TextView fangpianView;
	@ViewInject(R.id.layout_about_update)
	private View updateLayoutView;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_about);
		setTitleText(R.string.title_about);
		helperView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AboutActivity.this, ChatActivity.class);
				intent.putExtra(Constant.EXTRA_USER_ID, "ppstar");
				startActivity(intent);
			}
		});
		fangpianView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(AboutActivity.this, UserFangpianActivity.class);
				intent.putExtra(Constant.EXTRA_USER_ID, "ppstar");
				startActivity(intent);
			}
		});
		versionView.setText(VersionManager.getInstance().getVersionName());
		updateLayoutView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				checkVersion();
			}
		});
		VersionCheckRequest request = new VersionCheckRequest();
		request.setCode(VersionManager.getInstance().getVersionCode());
		request.setNetworkListener(new CommonNetworkCallback<VersionUpdateEntity>() {

			@Override
			public void onSuccess(VersionUpdateEntity data) {
				hideLoading();
				VersionManager.getInstance().saveVersionUpdateEntity(data);
				checkVersion();
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

	private void checkVersion(){
		if(VersionManager.getInstance().isForceUpdate()){
			showForceUpdate();
			updateView.setText("有新版本，请点击更新");
		}else if(VersionManager.getInstance().isNewVersion()){
			showUpdate();
			updateView.setText("有新版本，请点击更新");
		}else{
			updateView.setText("已是最新版本");
		}
	}
	
	private void showForceUpdate(){
		VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
		new AlertDialog.Builder(this).setTitle("有新版本").setCancelable(false).setMessage(entity.getChange_log()).setPositiveButton("更新", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
				Uri uri = Uri.parse(entity.getUrl());  
			    startActivity(new Intent(Intent.ACTION_VIEW,uri));  
			}
			
		}).show();
		
	}
	
	
	private void showUpdate(){
		VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
		new AlertDialog.Builder(this).setTitle("有新版本").setCancelable(false).setMessage(entity.getChange_log()).setPositiveButton("更新", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
				Uri uri = Uri.parse(entity.getUrl());  
			    startActivity(new Intent(Intent.ACTION_VIEW,uri));  
			}
			
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		}).show();
		
	}
	
}
