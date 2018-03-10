package com.intexh.bidong.main.square;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.example.aliyunvideo.RecordVideoActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.zaaach.citypicker.CityPickerActivity;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.QupaiHelper;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;
import com.intexh.bidong.callback.ModeChangeCallback;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.trend.TrendPublishActivity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.PageInfo;
import com.intexh.bidong.utils.RequestCode;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.SpacesItemDecoration;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout.Mode;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MainSquareTrendFragment extends BaseFragment implements ModeChangeCallback<Object> {
	public static MainSquareTrendFragment instance = null;

	private static final String TREND_DATA ="TREND_DATA";
	private static final int REQUEST_TRENDVIDEO = 300;
	private static final int REQUEST_CITY = 301;
	private boolean isLoading = false;

	private String filterCity = "全国";
	private Indicator.IndicatorAdapter indicatorAdapter;
	private static ArrayList<String> cityNames = new ArrayList<String>();
	private static ArrayList<String> hotCityNames = new ArrayList<String>();

	private View mView = null;
	@ViewInject(R.id.recycler_mainsquare_main)
	private RecyclerView gridView;
	@ViewInject(R.id.rotate_header_grid_view_frame)
	private PtrClassicFrameLayout ptrLayout;

	@ViewInject(R.id.square_indicator_layout)
	private FrameLayout indicatorLayout;
	@ViewInject(R.id.square_indicator_scrollbar)
	private ScrollIndicatorView indicatorView;
	@ViewInject(R.id.square_indicator_filter)
	private CheckBox filterCheckBox;

	private RecyclerItemClickListener trendItemClickListener = null;
	private MainSquareGridManager<MainSquareTrendVideoAdapter> trendManageInfo = null;
	private SpacesItemDecoration spaceManager = null;
	private TrendVideoEntity trendEntity = null;

	private class MainSquareGridManager<T extends RecyclerView.Adapter> {
		GridLayoutManager gridManager;
		T gridAdapter;
		PageInfo pageInfo;
	}

	private class PageVideoInfo extends PageInfo<TrendVideoEntity>{}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		LogUtils.d("MainSquareFragment onCreateView , instance = " + this);
		if (null == mView) {
			mView = inflater.inflate(R.layout.fragment_mainsquare, null);
			ViewUtils.inject(this, mView);
			GridLayoutManager mgr = new GridLayoutManager(getActivity(), 1);
			gridView.setLayoutManager(mgr);
			MainSquareNearAdapter adapter = new MainSquareNearAdapter(getActivity());
			gridView.setAdapter(adapter);
			int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.divider_height_1dp);
			spaceManager = new SpacesItemDecoration(spacingInPixels, 1);
			spaceManager.setNumOfColumn(1);
			gridView.addItemDecoration(spaceManager);
			ptrLayout.setPullToRefresh(true);
			ptrLayout.setMode(Mode.BOTH);
			ptrLayout.setPtrHandler(new PtrDefaultHandler2() {

				@Override
				public void onLoadMoreBegin(PtrFrameLayout frame) {
					if(trendManageInfo.pageInfo.isNoMoreData()){
						ptrLayout.refreshComplete();
					}else{
						getTrendData();
					}
				}

				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					trendManageInfo.pageInfo.setNeedClear(true);
					getTrendData();
				}

			});
		} else {
			ViewGroup parentView = (ViewGroup) (mView.getParent());
			if (null != parentView) {
				parentView.removeView(mView);
			}
		}
		if(null == savedInstanceState){
			savedInstanceState = getArguments();
		}
		initTrendIfNeeded();
		String ss = null;
		if(null != savedInstanceState){
			ss = savedInstanceState.getString(TREND_DATA);
		}
		if(null != ss){
			PageVideoInfo entity = GsonUtils.jsonToObj(ss, PageVideoInfo.class);
			if(null != entity){
				trendManageInfo.pageInfo = entity;
				trendManageInfo.gridAdapter.setDatas(entity.getDatas());
			}
		}
		if (0 == trendManageInfo.pageInfo.getDatas().size()) {
			trendManageInfo.pageInfo.setNeedClear(true);
			getTrendData();
		}
		switchToTrend();

//		indicatorLayout.setVisibility(View.VISIBLE);
		indicatorLayout.setVisibility(View.GONE);
		indicatorView.setOnTransitionListener(new OnTransitionTextListener().setSize(15*1.1f, 15));
		indicatorAdapter = new Indicator.IndicatorAdapter() {
			@Override
			public int getCount() {
				return cityNames.size();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup container) {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.tab_filter_text, container, false);
				}
				TextView textView = (TextView) convertView;
				textView.setText(cityNames.get(position % cityNames.size()));
				textView.setPadding(20, 0, 20, 0);
				return convertView;
			}
		};
		indicatorView.setAdapter(indicatorAdapter);
		indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
			@Override
			public void onItemSelected(View view, int i, int i1) {
				setFilterCity(cityNames.get(i));
			}
		});
		filterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					Intent intent = new Intent(getActivity(), CityPickerActivity.class);
					intent.putStringArrayListExtra(CityPickerActivity.KEY_HOT_CITY, hotCityNames);
					getActivity().startActivityForResult(intent, REQUEST_CITY);
					filterCheckBox.setChecked(false);
				}
			}
		});

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (0 == trendManageInfo.pageInfo.getDatas().size()) {
			trendManageInfo.pageInfo.setNeedClear(true);
			getTrendData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TREND_DATA, GsonUtils.objToJson(trendManageInfo.pageInfo));
	}

	private void initTrendIfNeeded() {
		if (null == trendManageInfo) {
			trendManageInfo = new MainSquareGridManager<MainSquareTrendVideoAdapter>();
			trendManageInfo.gridManager = new GridLayoutManager(getActivity(), 1);
			trendManageInfo.gridAdapter = new MainSquareTrendVideoAdapter(getActivity(), getResources().getDisplayMetrics().widthPixels);
			trendManageInfo.pageInfo = new PageInfo<TrendVideoEntity>();
			trendManageInfo.gridAdapter.setDatas(trendManageInfo.pageInfo.getDatas());
			trendManageInfo.gridAdapter.setCommentListener(new MainSquareTrendVideoAdapter.OnCommentListener() {
				@Override
				public void onCommit(View v, String comment, String videoId) {
					commitComment(v, comment, videoId);
				}
			});
			trendManageInfo.gridAdapter.setVideoListener(new MainSquareTrendVideoAdapter.OnVideoListener() {
				@Override
				public void onClick(TrendVideoEntity entity) {
					if(null != entity.getPhotos() && entity.getPhotos().length > 0){
						Intent intent = new Intent(getActivity(), TrendDetailActivity.class);
						intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
						startActivity(intent);
					}
					else{
						Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
						intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
						startActivity(intent);
					}
				}
			});
			trendManageInfo.gridAdapter.setUserListener(new MainSquareTrendVideoAdapter.OnUserListener() {
				@Override
				public void onClick(User user) {
					Intent intent = new Intent(getActivity(),UserDetailActivity.class);
					intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(user));
					startActivity(intent);
				}
			});

			trendItemClickListener = new RecyclerItemClickListener(getActivity(),new OnItemClickListener() {
				@Override
				public void onItemClick(View view, int position) {
					TrendVideoEntity entity = ((List<TrendVideoEntity>)trendManageInfo.pageInfo.getDatas()).get(position);
					trendEntity = entity;
				}
			});
		}
	}

	private void switchToTrend() {
		initTrendIfNeeded();
		gridView.setLayoutManager(trendManageInfo.gridManager);
		gridView.removeOnItemTouchListener(trendItemClickListener);
		gridView.addOnItemTouchListener(trendItemClickListener);
		gridView.setAdapter(trendManageInfo.gridAdapter);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		instance = null;
	}

	@Override
	public void onModeChange(int mode, Object data) {}

	@Override
	protected void onBaseResume() {
		getDataIfNeeded();
	}

	@Override
	protected void onBasePause() {}

	@Override
	protected void showLoading() {
		super.showLoading();
		isLoading = true;
	}

	@Override
	protected void hideLoading() {
		super.hideLoading();
		isLoading = false;
	}

	private void setFilterCity(String selCity){
		if(null != selCity && !filterCity.equals(selCity)){
			this.filterCity = selCity;
			trendManageInfo.pageInfo.setNeedClear(true);
			getTrendData();
		}
	}

	private void getTrendData() {
		getHotCities();
		if(isLoading){
			return ;
		}
		showLoading();
		RegDataEntity userInfo = UserUtils.getUserInfo();
		GetTrendVideosRequest request = new GetTrendVideosRequest();
		request.setGender(userInfo.getUser().getGender());
		if(!filterCity.equals("全国")){
			request.setCity(filterCity);
		}
		if (trendManageInfo.pageInfo.isNeedClear()) {
			request.setPage(new Page());
		} else {
			request.setPage(trendManageInfo.pageInfo.getPage());
		}
		request.setNetworkListener(new CommonNetworkCallback<TrendVideoEntity[]>() {

			@Override
			public void onSuccess(TrendVideoEntity[] data) {
				hideLoading();
				ptrLayout.refreshComplete();
				if (null != data) {
					if (trendManageInfo.pageInfo.isNeedClear()) {
						trendManageInfo.pageInfo.getDatas().clear();
						trendManageInfo.pageInfo.getPage().setStart(0);
					}
					for (TrendVideoEntity entity : data) {
						trendManageInfo.pageInfo.getDatas().add(entity);
					}
					if (data.length < trendManageInfo.pageInfo.getPage().getCount()) {
						trendManageInfo.pageInfo.setNoMoreData(true);
					} else {
						trendManageInfo.pageInfo.setNoMoreData(false);
					}
					trendManageInfo.pageInfo.getPage().setStart(trendManageInfo.pageInfo.getPage().getStart() + data.length);
				} else {
					trendManageInfo.pageInfo.setNoMoreData(false);
				}
				trendManageInfo.pageInfo.setNeedClear(false);
				trendManageInfo.gridAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				ptrLayout.refreshComplete();
				trendManageInfo.pageInfo.setNeedClear(false);
				showToast(reason);
			}
		});
		request.getDataFromServer();
	}

	public void getHotCities(){
		// 获取热门城市
		if(!cityNames.isEmpty()) {
			return;
		}
		GetHotCityRequest request = new GetHotCityRequest();
		request.setNetworkListener(new CommonNetworkCallback<ArrayList<String>>() {
			@Override
			public void onSuccess(ArrayList<String> data) {
				if(null != data){
					String currCity = UserUtils.getUserInfo().getUser().getCity();
					boolean isContain = false;
					hotCityNames.clear();
					cityNames.clear();
					cityNames.add("全国");
					if(!data.contains(currCity)){
						cityNames.add(currCity);
					}
					for(String item : data){
						cityNames.add(item);
						hotCityNames.add(item);
					}
					indicatorAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				showToast(reason);
			}
		});
		request.getDataFromServer();
	}

	private void getDataIfNeeded() {
		initTrendIfNeeded();
		if (0 == trendManageInfo.pageInfo.getDatas().size()) {
			trendManageInfo.pageInfo.setNeedClear(true);
			getTrendData();
		}
	}

	private void commitComment(final View v, String comment, String videoId){
		if(StringUtil.isEmptyString(comment)){
			showToast("请输入评论");
			return;
		}
		SendCommentRequest request = new SendCommentRequest();
		request.setUser_id(UserUtils.getUserid());
		request.setVideo_id(videoId);
		request.setContent(comment);
		request.setNetworkListener(new CommonNetworkCallback<String>() {
			@Override
			public void onSuccess(String data) {
				hideLoading();
				((View)(v.getParent())).setVisibility(View.GONE);
				showToast("评论提交成功");
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
	public void onLocationChange(LocationInfo locationInfo) {
		if(isLoading){
			return ;
		}
//		getDataIfNeeded();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){
				case REQUEST_CITY:{
					String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
					setFilterCity(city);
					if(cityNames.contains(city)) {
						indicatorView.setCurrentItem(cityNames.indexOf(city));
					}
					break;
				}
				case REQUEST_TRENDVIDEO:{
					String ss = data.getStringExtra(VideoDetailActivity.VIDEOENTITY);
					TrendVideoEntity entity = GsonUtils.jsonToObj(ss,TrendVideoEntity.class);
					if(null == entity){
						trendManageInfo.pageInfo.getDatas().remove(trendEntity);
					}else{
						trendEntity.setComm_count(entity.getComm_count());
					}
					trendManageInfo.gridAdapter.notifyDataSetChanged();
					break;
				}
				case TrendPublishActivity.REQUEST_SEL_IMAGE: {
					TrendPublishActivity.clearData();
					ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
					TrendPublishActivity.addPaths(paths,0);
					Intent intent = new Intent(getActivity(), TrendPublishActivity.class);
					startActivity(intent);
					break;
				}
				case RequestCode.RECORDE_SHOW: {
					String path = data.getStringExtra(RecordVideoActivity.OUTPUT_PATH);
					TrendPublishActivity.videoInfo = QupaiHelper.processVideoData(getActivity(),path);
					Intent intent = new Intent(getActivity(), TrendPublishActivity.class);
					intent.putExtra(TrendPublishActivity.IS_VIDEO, true);
					startActivity(intent);
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	//弹出 动态菜单
	public void showActionSheet(){
		ActionSheet
				.createBuilder(getActivity(), getChildFragmentManager())
				.setCancelButtonTitle(R.string.common_cancel)
				.setOtherButtonTitles(getResources().getString(R.string.common_recordvideo), getResources().getString(R.string.common_localpic))
				.setListener(new ActionSheet.ActionSheetListener() {
					@Override
					public void onOtherButtonClick(ActionSheet actionSheet, int index) {
						switch (index) {
							case 0: {
								recordVideo();
								break;
							}
							case 1: {
								Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
								// whether show camera
								intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
								// max select image amount
								intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
								intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
								// default select images (support array list)
								intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, new ArrayList<String>());
								getActivity().startActivityForResult(intent, TrendPublishActivity.REQUEST_SEL_IMAGE);
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
		QupaiHelper.showRecordActivity(getActivity(), RequestCode.RECORDE_SHOW);
	}

}
