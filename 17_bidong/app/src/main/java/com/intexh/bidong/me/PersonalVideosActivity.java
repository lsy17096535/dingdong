package com.intexh.bidong.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadOptions;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.easemob.util.PathUtil;
import com.example.aliyunvideo.RecordVideoActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.QupaiHelper;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.constants.VideoInfo;
import com.intexh.bidong.easemob.common.ImageGridActivity;
import com.intexh.bidong.location.LocationHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.main.square.VideoDetailActivity;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.FileUtils;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.RequestCode;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.SpacesItemDecoration;

public class PersonalVideosActivity extends BaseTitleActivity {

	public static final String IS_SHOULDRECORD = "IS_SHOULDRECORD";
	public static final String IS_SELECTMODE = "IS_SELECTMODE";
	public static final String VIDEO_ENTITY = "VIDEO_ENTITY";
	public static final String USER_ENTITY = "USER_ENTITY";
	
	public static final int REQUEST_LOCAL_VIDEO = 20000;
	public static final int REQUEST_LOCAL_REMARK = 20001;
	public static final int REQUEST_HOTVIDEO = 20002;

	@ViewInject(R.id.recycler_personalvideos_main)
	private RecyclerView mainView;
	@ViewInject(R.id.image_title_right)
	private ImageView rightView;
	private MyTrendVideoAdapter mAdapter = null;
	private List<TrendVideoEntity> datas = new ArrayList<TrendVideoEntity>();
	private VideoInfo uploadInfo = null;
	private VideoInfo localInfo = null;
	private String remark = null;
	private RecyclerItemClickListener mItemClickListener = null;
	private boolean isShouldRecord = false;
	private boolean isSelectMode = false;
	private TrendVideoEntity hotEntity = null;
	private User targetUserid = null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_personalvideos);
		setTitleText(R.string.title_peronalvideos);
		ViewUtils.inject(this);
		rightView.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showActionSheet();
			}
		});
		mItemClickListener = new RecyclerItemClickListener(this, new OnItemClickListener() {
			
			@Override
			public void onItemClick(View view, int position) {
				TrendVideoEntity entity = datas.get(position);
				if(isSelectMode){
					Intent intent = new Intent();
					intent.putExtra(VIDEO_ENTITY, GsonUtils.objToJson(entity));
					setResult(RESULT_OK, intent);
					finish();
				}else{
					hotEntity = entity;
					Intent intent = new Intent(PersonalVideosActivity.this,VideoDetailActivity.class);
					intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
					startActivityForResult(intent,REQUEST_HOTVIDEO);
				}
			}
		});
		int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
		SpacesItemDecoration spaceManager = new SpacesItemDecoration(spacingInPixels, 2);
		mainView.addOnItemTouchListener(mItemClickListener);
		mainView.addItemDecoration(spaceManager);
		mAdapter = new MyTrendVideoAdapter(this, getResources().getDisplayMetrics().widthPixels);
		mAdapter.setDatas(datas);
		mainView.setLayoutManager(new GridLayoutManager(this, 2));
		mainView.setAdapter(mAdapter);
		isShouldRecord = getIntent().getBooleanExtra(IS_SHOULDRECORD, false);
		isSelectMode = getIntent().getBooleanExtra(IS_SELECTMODE, false);
		String ss = getIntent().getStringExtra(USER_ENTITY);
		targetUserid = GsonUtils.jsonToObj(ss, User.class);
		if(null != targetUserid){
			rightView.setVisibility(View.GONE);
			setTitleText("全部视频");
		}
		if(isShouldRecord){
			showActionSheet();
		}
		getDatas();
	}
	
	private void showActionSheet(){
		ActionSheet
		.createBuilder(this, getSupportFragmentManager())
		.setCancelButtonTitle(R.string.common_cancel)
		.setOtherButtonTitles(getResources().getString(R.string.common_recordvideo))
		.setListener(new ActionSheetListener() {

			@Override
			public void onOtherButtonClick(ActionSheet actionSheet, int index) {
				switch (index) {
				case 0: {
					recordVideo();
					break;
				}
				case 1: {
					Intent intent = new Intent(PersonalVideosActivity.this,ImageGridActivity.class);
					startActivityForResult(intent, REQUEST_LOCAL_VIDEO);
//					MediaOptions.Builder builder = new MediaOptions.Builder();
//					builder.setMaxVideoDuration(1000*60);
//					MediaOptions options = builder.selectVideo()
//							.canSelectMultiVideo(false).build();
//					MediaPickerActivity.open(PersonalVideosActivity.this,
//							REQUEST_LOCAL_VIDEO, options);
					break;
				}
				}
			}

			@Override
			public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

			}
		}).show();
	}
	
	private void recordVideo() {
		QupaiHelper.showRecordActivity(this, RequestCode.RECORDE_SHOW);
	}

	private void getDatas(){
		GetPersonalVideosRequest request = new GetPersonalVideosRequest();
		if(null != targetUserid){
			request.setUserid(targetUserid.getId());
		}else{
			request.setUserid(UserUtils.getUserid());
		}
		request.setNetworkListener(new CommonNetworkCallback<TrendVideoEntity[]>() {

			@Override
			public void onSuccess(TrendVideoEntity[] data) {
				hideLoading();
				datas.clear();
				if(null != data){
					User user = UserUtils.getUserInfo().getUser();
					if(null != targetUserid){
						user = targetUserid;
					}
					for(TrendVideoEntity entity : data){
						entity.setUser(user);
						datas.add(entity);
					}
				}
				mAdapter.notifyDataSetChanged();
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
	
	private void doCommitVideo(){
		CommitVideoRequest request = new CommitVideoRequest();
		request.setUserid(UserUtils.getUserid());
		request.setRemark(remark);
		request.setSnapshot(uploadInfo.thumbName);
		request.setVideo(uploadInfo.videoName);

		LocationHelper.LocationInfo locObj = LocationLocalManager.getInstance().getLastLocation();
		if(locObj != null && locObj.location != null){
			request.setLoc(locObj.location.longitude + "," + locObj.location.latitude);
			request.setCity(locObj.city);
			request.setDistrict(locObj.district);
			request.setStreet(locObj.street);
		}


		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				User user = UserUtils.getUserInfo().getUser();
				TrendVideoEntity entity = new TrendVideoEntity();
				entity.setUser(user);
				entity.setRemark(remark);
				entity.setId(data);
				entity.setVideo(uploadInfo.videoName);
				entity.setSnapshort(uploadInfo.thumbName);
				entity.setCreated_at(DateUtil.getUTCTimeString());
				datas.add(entity);
				mAdapter.notifyDataSetChanged();
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
			public void onUploading(UploadTask arg0) {

			}

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
				doCommitVideo();
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
			public void onUploading(UploadTask arg0) {

			}

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
	
	private void jumpToRemark(){
		Intent intent = new Intent(this,VideoRemarkActivity.class);
		startActivityForResult(intent, REQUEST_LOCAL_REMARK);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case RequestCode.RECORDE_SHOW: {
				String path = data.getStringExtra(RecordVideoActivity.OUTPUT_PATH);
				localInfo = QupaiHelper.processVideoData(PersonalVideosActivity.this,path);
				jumpToRemark();
				break;
			}
			case REQUEST_LOCAL_VIDEO: {
				int duration = data.getIntExtra("dur", 0);
                String videoPath = data.getStringExtra("path");
                File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                    ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                    fos.close();
					String basePath = FileUtils.newOutgoingFilePath();
					String thumbPath = basePath + ".jpg";
					ImageUtils.saveBitmapToDisk(ThumbBitmap, thumbPath, this);
					localInfo = new VideoInfo();
					localInfo.thumbFile = new File(thumbPath);
					localInfo.thumbName = localInfo.thumbFile.getName();// FileUtils.parseNameByPath(thumbPath);
					localInfo.videoFile = new File(videoPath);
					localInfo.videoName = basePath + ".mp4";
					jumpToRemark();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//				List<MediaItem> mMediaSelectedList = MediaPickerActivity
//						.getMediaItemSelected(data);
//				if (mMediaSelectedList.size() > 0) {
//					String thumbPath = Contant.THUMBPATH;
//					MediaItem mediaItem = mMediaSelectedList.get(0);
//					String videoPath = MediaUtils.getRealImagePathFromURI(
//							this.getContentResolver(), mediaItem.getUriOrigin());
//					showToast(videoPath);
//					Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
//							videoPath, Thumbnails.MINI_KIND);
//					ImageUtils.saveBitmapToDisk(bitmap, thumbPath, this);
//					localInfo = new VideoInfo();
//					localInfo.thumbFile = new File(thumbPath);
//					localInfo.thumbName = FileUtils.genPicFileName();// FileUtils.parseNameByPath(thumbPath);
//					localInfo.videoFile = new File(videoPath);
//					localInfo.videoName = FileUtils.genVideoFileName();
//					jumpToRemark();
//				}
				break;
			}
			case REQUEST_LOCAL_REMARK:{
				String remark = data.getStringExtra(VideoRemarkActivity.REMARK);
				this.remark = remark;
				uploadPic(localInfo.thumbFile, localInfo.thumbName);
				break;
			}
			case REQUEST_HOTVIDEO:{
				if(null == data){
					datas.remove(hotEntity);
				}else{
					String ss = data.getStringExtra(VideoDetailActivity.VIDEOENTITY);
					TrendVideoEntity entity = GsonUtils.jsonToObj(ss, TrendVideoEntity.class);
					if(null == entity){
						datas.remove(hotEntity);
					}else{
						hotEntity.setComm_count(entity.getComm_count());
					}
				}
				mAdapter.notifyDataSetChanged();
				break;
			}
			}
		}
	}
	
	
}
