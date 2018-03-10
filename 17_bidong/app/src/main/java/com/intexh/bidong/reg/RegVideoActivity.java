package com.intexh.bidong.reg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadOptions;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.example.aliyunvideo.RecordVideoActivity;
import com.github.rtoshiro.view.video.FullscreenVideoLayout;
import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.QupaiHelper;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.constants.VideoInfo;
import com.intexh.bidong.easemob.common.ImageGridActivity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.RequestCode;
import com.intexh.bidong.utils.UserUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.io.File;
import java.io.IOException;

public class RegVideoActivity extends BaseTitleActivity implements OnClickListener {

	private static final int REQUEST_LOCAL_VIDEO = 100;

	@ViewInject(R.id.image_regvideo_snap)
	private ImageView snapView;
	@ViewInject(R.id.btn_regvideo_complete)
	private View completeBtn;
	@ViewInject(R.id.layout_regvideo_center_tip)
	private LinearLayout centerTipLayout;
	@ViewInject(R.id.layout_reg_videocontainer)
	private FrameLayout videocontainer;
	@ViewInject(R.id.video_reg_video)
	private FullscreenVideoLayout videoView;

	private int screenWidth;
	private VideoInfo uploadInfo = null;
	private VideoInfo localInfo = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_regvideo);
		ViewUtils.inject(this);
		setTitleText(R.string.title_authvideo);

		//取得窗口属性
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		//窗口的宽度
		screenWidth = dm.widthPixels;
		videocontainer.setLayoutParams(new FrameLayout.LayoutParams(screenWidth - 20, screenWidth - 20));
		snapView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth - 20, screenWidth - 20));
		snapView.setOnClickListener(this);
		completeBtn.setOnClickListener(this);
		centerTipLayout.setOnClickListener(this);
		videoView.setActivity(this);
		videoView.hideBackView();
		videoView.hideMoreView();
	}

	private void recordVideo() {
		QupaiHelper.showRecordActivity(this, RequestCode.RECORDE_SHOW);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.image_regvideo_snap: {
				ActionSheet
						.createBuilder(this, getSupportFragmentManager())
						.setCancelButtonTitle(R.string.common_cancel)
						.setOtherButtonTitles("拍摄认证视频")
						.setListener(new ActionSheetListener() {
							@Override
							public void onOtherButtonClick(ActionSheet actionSheet,
														   int index) {
								switch (index) {
									case 0: {
										recordVideo();
										break;
									}
									case 1: {
										Intent intent = new Intent(RegVideoActivity.this,ImageGridActivity.class);
										startActivityForResult(intent, REQUEST_LOCAL_VIDEO);
										break;
									}
								}
							}

							@Override
							public void onDismiss(ActionSheet actionSheet,
												  boolean isCancel) {

							}
						}).show();
				break;
			}
			case R.id.layout_regvideo_center_tip: {
				recordVideo();
				break;
			}
			case R.id.btn_regvideo_complete: {
				if (null == localInfo) {
					showToast(R.string.error_regvideo);
				} else {
					uploadPic(localInfo.thumbFile, localInfo.thumbName);
				}
				break;
			}
		}
	}

	private void doComplete() {
		RegDataEntity userInfo = UserUtils.getUserInfo();
		CommitCoverRequest request = new CommitCoverRequest();
		request.setUserId(userInfo.getUser().getId());
		request.setSnapshot(uploadInfo.thumbName);
		request.setVideo(uploadInfo.videoName);
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				RegDataEntity userInfo = UserUtils.getUserInfo();
				User user = userInfo.getUser();
				user.setSnapshot(uploadInfo.thumbName);
				user.setVideo(uploadInfo.videoName);
				UserUtils.saveUserInfo(userInfo);
				setResult(RESULT_OK);
				showToast(R.string.common_uploadvideo_auth);
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

	private void uploadVideo(File file, String name) {
		showLoading();
		UploadOptions opt = new UploadOptions.Builder().aliases(name).build();
		PPStarApplication.wantuService.upload(file, opt, new UploadListener() {
			@Override
			public void onUploading(UploadTask arg0) {}

			@Override
			public void onUploadFailed(UploadTask arg0, FailReason arg1) {
				hideLoading();
				showToast(R.string.error_uploadfile);
			}

			@Override
			public void onUploadComplete(UploadTask arg0) {
				hideLoading();
				uploadInfo.videoFile = localInfo.videoFile;
				uploadInfo.videoName = localInfo.videoName;
				doComplete();
			}

			@Override
			public void onUploadCancelled(UploadTask arg0) {
				hideLoading();
				showToast(R.string.error_uploadfile);
			}
		}, BucketHelper.getInstance().getVideoBucketToken());
	}

	private void uploadPic(File file, String name) {
		showLoading();
		UploadOptions opt = new UploadOptions.Builder().aliases(name).build();
		PPStarApplication.wantuService.upload(file, opt, new UploadListener() {
			@Override
			public void onUploading(UploadTask arg0) {}

			@Override
			public void onUploadFailed(UploadTask arg0, FailReason arg1) {
				hideLoading();
				showToast(R.string.error_uploadfile);
			}

			@Override
			public void onUploadComplete(UploadTask arg0) {
				hideLoading();
				uploadInfo = new VideoInfo();
				uploadInfo.thumbFile = localInfo.thumbFile;
				uploadInfo.thumbName = localInfo.thumbName;
				uploadVideo(localInfo.videoFile, localInfo.videoName);
			}

			@Override
			public void onUploadCancelled(UploadTask arg0) {
				hideLoading();
				showToast(R.string.error_uploadfile);
			}
		}, BucketHelper.getInstance().getVideoBucketToken());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode ==  RequestCode.RECORDE_SHOW){
			if(resultCode == Activity.RESULT_OK && data!= null){
				videocontainer.setVisibility(View.VISIBLE);
				String path = data.getStringExtra(RecordVideoActivity.OUTPUT_PATH);
				localInfo = QupaiHelper.processVideoData(RegVideoActivity.this,path);
				ImageUtils.loadSnapshotImage("file:///" + localInfo.thumbPath, snapView, 0.6f);
				try{
					videoView.setVideoPath(path);
					videoView.setShouldAutoplay(true);
				}catch (IOException e){}
				centerTipLayout.setVisibility(View.GONE);
			}else if(resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(this,"用户取消录制",Toast.LENGTH_SHORT).show();
			}
		}
	}

}
