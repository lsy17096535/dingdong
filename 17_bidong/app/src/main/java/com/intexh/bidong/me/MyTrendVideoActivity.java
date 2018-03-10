package com.intexh.bidong.me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.example.aliyunvideo.RecordVideoActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.QupaiHelper;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.main.square.TrendDetailActivity;
import com.intexh.bidong.main.square.VideoDetailActivity;
import com.intexh.bidong.trend.TrendPublishActivity;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.RequestCode;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MyTrendVideoActivity extends BaseTitleActivity {

	public static final String IS_SHOULDRECORD = "IS_SHOULDRECORD";
	public static final String IS_SELECTMODE = "IS_SELECTMODE";
	public static final String VIDEO_ENTITY = "VIDEO_ENTITY";
	public static final String USER_ENTITY = "USER_ENTITY";

	@ViewInject(R.id.pull_mytrend_pull)
	private PtrClassicFrameLayout ptrLayout;
	@ViewInject(R.id.recycler_personalvideos_main)
	private RecyclerView recyclerView;
	@ViewInject(R.id.image_title_right)
	private ImageView rightView;
	@ViewInject(R.id.layout_mytrend_empty)
	private LinearLayout emptylayout;

	private MyTrendVideoAdapter mAdapter = null;
	private List<TrendVideoEntity> datas = new ArrayList<TrendVideoEntity>();
	private RecyclerItemClickListener mItemClickListener = null;
	private boolean isShouldRecord = false;
	private boolean isSelectMode = false;
	private User targetUserid = null;
	private Page page = new Page();
	private boolean isNeedClear = false;
	private boolean isNoMoreData = false;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_personalvideos);
		setTitleText(R.string.me_personalvideo);
		ViewUtils.inject(this);
		rightView.setImageResource(R.drawable.icon_ban_camera);
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
				if(null != entity.getPhotos() && entity.getPhotos().length > 0){
					Intent intent = new Intent(MyTrendVideoActivity.this, TrendDetailActivity.class);
					intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
					startActivityForResult(intent, RequestCode.TREND_DELETE);
				}
				else{
					Intent intent = new Intent(MyTrendVideoActivity.this, VideoDetailActivity.class);
					intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
					startActivityForResult(intent, RequestCode.TREND_DELETE);
				}
			}
		});
		ptrLayout.setPullToRefresh(true);
		ptrLayout.setMode(PtrFrameLayout.Mode.BOTH);
		ptrLayout.setPtrHandler(new PtrDefaultHandler2() {
			@Override
			public void onLoadMoreBegin(PtrFrameLayout frame) {
				if (isNoMoreData) {
					ptrLayout.refreshComplete();
				} else {
					getDatas();
				}
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				isNeedClear = true;
				getDatas();
			}
		});
		recyclerView.addOnItemTouchListener(mItemClickListener);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		mAdapter = new MyTrendVideoAdapter(this, getResources().getDisplayMetrics().widthPixels);
		mAdapter.setDatas(datas);
		recyclerView.setAdapter(mAdapter);
		mAdapter.setVideoListener(new MyTrendVideoAdapter.OnVideoListener() {
			@Override
			public void onClick(TrendVideoEntity entity) {
				if(null != entity.getPhotos() && entity.getPhotos().length > 0){
					Intent intent = new Intent(MyTrendVideoActivity.this, TrendDetailActivity.class);
					intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
					startActivity(intent);
				}
				else{
					Intent intent = new Intent(MyTrendVideoActivity.this, VideoDetailActivity.class);
					intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
					startActivity(intent);
				}
			}
		});

		isShouldRecord = getIntent().getBooleanExtra(IS_SHOULDRECORD, false);
		isSelectMode = getIntent().getBooleanExtra(IS_SELECTMODE, false);
		String ss = getIntent().getStringExtra(USER_ENTITY);
		targetUserid = GsonUtils.jsonToObj(ss, User.class);
		if(null != targetUserid){
			rightView.setVisibility(View.GONE);
			setTitleText("全部动态");
		}
		if(isShouldRecord){
			showActionSheet();
		}

		getDatas();
	}

	//弹出 动态菜单
	private void showActionSheet(){
		ActionSheet
		.createBuilder(this, getSupportFragmentManager())
		.setCancelButtonTitle(R.string.common_cancel)
		.setOtherButtonTitles(getResources().getString(R.string.common_recordvideo), getResources().getString(R.string.common_localpic))
		.setListener(new ActionSheetListener() {
			@Override
			public void onOtherButtonClick(ActionSheet actionSheet, int index) {
				switch (index) {
					case 0: {
						recordVideo();
						break;
					}
					case 1: {
						Intent intent = new Intent(MyTrendVideoActivity.this, MultiImageSelectorActivity.class);
						// whether show camera
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
						// max select image amount
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
						intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
						// default select images (support array list)
						intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
						startActivityForResult(intent, TrendPublishActivity.REQUEST_SEL_IMAGE);
						break;
					}
				}
			}

			@Override
			public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}
		}).show();
	}

	//拍摄视频
	private void recordVideo() {
		QupaiHelper.showRecordActivity(this, RequestCode.RECORDE_SHOW);
	}


    //获取个人动态数据
	private void getDatas(){
		GetPersonalVideosRequest request = new GetPersonalVideosRequest();
		if(null != targetUserid){
			request.setUserid(targetUserid.getId());
		}else{
			request.setUserid(UserUtils.getUserid());
		}
		if(isNeedClear){
			request.setPage(new Page());
		}else{
			request.setPage(page);
		}
		request.setNetworkListener(new CommonNetworkCallback<TrendVideoEntity[]>() {

			@Override
			public void onSuccess(TrendVideoEntity[] data) {
				hideLoading();
				ptrLayout.refreshComplete();
				if (isNeedClear) {
					isNeedClear = false;
					datas.clear();
					page.setStart(0);
				}
				if (null != data) {
					User user = UserUtils.getUserInfo().getUser();
					if(null != targetUserid){
						user = targetUserid;
					}
					for(TrendVideoEntity entity : data){
						entity.setUser(user);
						datas.add(entity);
					}
					if (data.length < page.getCount()) {
						isNoMoreData = true;
					}
					page.setStart(data.length);
				} else {
					isNoMoreData = true;
				}
				mAdapter.notifyDataSetChanged();
				updateEmptyView();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				isNeedClear = true;
				ptrLayout.refreshComplete();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	private void updateEmptyView(){
		if(!datas.isEmpty()){
			ptrLayout.setVisibility(View.VISIBLE);
			emptylayout.setVisibility(View.GONE);
		}else{
			ptrLayout.setVisibility(View.GONE);
			emptylayout.setVisibility(View.VISIBLE);
			emptylayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showActionSheet();
				}
			});
		}
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
				case TrendPublishActivity.REQUEST_SEL_IMAGE: {
					TrendPublishActivity.clearData();
					ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					TrendPublishActivity.addPaths(paths,1);
					Intent intent = new Intent(MyTrendVideoActivity.this, TrendPublishActivity.class);
					startActivityForResult(intent, RequestCode.TREND_PUBLISH);
					break;
				}
				case RequestCode.RECORDE_SHOW: {
					String path = data.getStringExtra(RecordVideoActivity.OUTPUT_PATH);
					TrendPublishActivity.videoInfo = QupaiHelper.processVideoData(MyTrendVideoActivity.this,path);
					Intent intent = new Intent(MyTrendVideoActivity.this, TrendPublishActivity.class);
					intent.putExtra(TrendPublishActivity.IS_VIDEO, true);
					startActivityForResult(intent, RequestCode.TREND_PUBLISH);
					break;
				}
				case RequestCode.TREND_PUBLISH:
				case RequestCode.TREND_DELETE: {
					isNeedClear = true;
					getDatas();
					break;
				}
			}
		}
	}
	
	
}
