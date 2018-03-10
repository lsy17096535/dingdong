package com.intexh.bidong.main.square;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;
import com.intexh.bidong.callback.ModeChangeCallback;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.PageInfo;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.SpacesItemDecoration;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout.Mode;

public class MainSquareNearFragment extends BaseFragment implements ModeChangeCallback<Object> {
	public static MainSquareNearFragment instance = null;
	private static final String NEARBY_DATA = "NEARBY_DATA";
	private boolean isLoading = false;
	private boolean ifFirst = true;

	private View mView = null;
	@ViewInject(R.id.recycler_mainsquare_main)
	private RecyclerView gridView;
	@ViewInject(R.id.rotate_header_grid_view_frame)
	private PtrClassicFrameLayout ptrLayout;
	private RecyclerItemClickListener nearItemClickListener = null;
	private SpacesItemDecoration spaceManager = null;
	private MainSquareGridManager<MainSquareNearAdapter> nearbyManageInfo = null;

	private class MainSquareGridManager<T extends RecyclerView.Adapter> {
		GridLayoutManager gridManager;
		T gridAdapter;
		PageInfo pageInfo;
	}

	private class PageUserInfo extends PageInfo<User>{}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		instance = this;
		LogUtils.d("MainSquareFragment onCreateView , instance = " + this);
		if (null == mView) {
			mView = inflater.inflate(R.layout.fragment_mainsquare, null);
			ViewUtils.inject(this, mView);
			GridLayoutManager mgr = new GridLayoutManager(getActivity(), 2);
			MainSquareNearAdapter adapter = new MainSquareNearAdapter(getActivity());
			gridView.setLayoutManager(mgr);
			gridView.setAdapter(adapter);
			int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
			spaceManager = new SpacesItemDecoration(spacingInPixels, 2);
			spaceManager.setNumOfColumn(2);
			gridView.addItemDecoration(spaceManager);
			ptrLayout.setPullToRefresh(true);
			ptrLayout.setMode(Mode.BOTH);
			ptrLayout.setPtrHandler(new PtrDefaultHandler2() {

				@Override
				public void onLoadMoreBegin(PtrFrameLayout frame) {
					if(nearbyManageInfo.pageInfo.isNoMoreData()){
						ptrLayout.refreshComplete();
					}else{
						getNearbyData();
					}
				}

				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					nearbyManageInfo.pageInfo.setNeedClear(true);
					getNearbyData();
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
		initNearbyIfNeeded();
		String ss = null;
		if(null != savedInstanceState){
			ss = savedInstanceState.getString(NEARBY_DATA);
		}
		if(null != ss){
			PageUserInfo entity = GsonUtils.jsonToObj(ss, PageUserInfo.class);
			if(null != entity){
				nearbyManageInfo.pageInfo = entity;
				nearbyManageInfo.gridAdapter.setDatas(entity.getDatas());
			}
		}
		if (0 == nearbyManageInfo.pageInfo.getDatas().size()) {
			nearbyManageInfo.pageInfo.setNeedClear(true);
			getNearbyData();
		}
		switchToNearby();
		return mView;
	}

	private void initNearbyIfNeeded() {
		if (null == nearbyManageInfo) {
			nearbyManageInfo = new MainSquareGridManager<MainSquareNearAdapter>();
			nearbyManageInfo.gridManager = new GridLayoutManager(getActivity(), 2);
			nearbyManageInfo.gridAdapter = new MainSquareNearAdapter(getActivity());
			nearbyManageInfo.pageInfo = new PageUserInfo();
			nearbyManageInfo.gridAdapter.setDatas(nearbyManageInfo.pageInfo.getDatas());
			nearItemClickListener = new RecyclerItemClickListener(getActivity(), new OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(getActivity(),UserDetailActivity.class);
					intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(nearbyManageInfo.pageInfo.getDatas().get(position)));
					startActivity(intent);
				}
			});
		}
	}

	private void switchToNearby() {
		if(null != nearbyManageInfo && nearbyManageInfo.pageInfo.getDatas().size() > 0){
			return;
		}
		initNearbyIfNeeded();
		spaceManager.setNumOfColumn(2);
		gridView.setLayoutManager(nearbyManageInfo.gridManager);
		gridView.removeOnItemTouchListener(nearItemClickListener);
		gridView.addOnItemTouchListener(nearItemClickListener);
		gridView.setAdapter(nearbyManageInfo.gridAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (0 == nearbyManageInfo.pageInfo.getDatas().size()) {
			nearbyManageInfo.pageInfo.setNeedClear(true);
			getNearbyData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(NEARBY_DATA, GsonUtils.objToJson(nearbyManageInfo.pageInfo));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		instance = null;
	}


	@Override
	public void onModeChange(int mode, Object data) {
	}

	@Override
	protected void onBaseResume() {
		getDataIfNeeded();
	}

	@Override
	protected void onBasePause() {

	}

	private void getNearbyData() {
		LocationInfo locationInfo = LocationLocalManager.getInstance().getLastLocation();
		if (null != locationInfo) {
			if(isLoading){
				return ;
			}

			showLoading();
			GetNearbyUsersRequest request = new GetNearbyUsersRequest();
			RegDataEntity userInfo = UserUtils.getUserInfo();
			request.setGender(userInfo.getUser().getGender());
			request.setLocation(locationInfo.location);
			if (nearbyManageInfo.pageInfo.isNeedClear()) {
				request.setPage(new Page());
			} else {
				request.setPage(nearbyManageInfo.pageInfo.getPage());
			}
			request.setNetworkListener(new CommonNetworkCallback<User[]>() {

				@Override
				public void onSuccess(User[] data) {
					hideLoading();
					ptrLayout.refreshComplete();
					if (null != data) {
						if (nearbyManageInfo.pageInfo.isNeedClear()) {
							nearbyManageInfo.pageInfo.getDatas().clear();
							nearbyManageInfo.pageInfo.getPage().setStart(0);
						}
						for (User entity : data) {
							nearbyManageInfo.pageInfo.getDatas().add(entity);
						}
						if (data.length < nearbyManageInfo.pageInfo.getPage().getCount()) {
							nearbyManageInfo.pageInfo.setNoMoreData(true);
						} else {
							nearbyManageInfo.pageInfo.setNoMoreData(false);
						}
						nearbyManageInfo.pageInfo.getPage().setStart(nearbyManageInfo.pageInfo.getPage().getStart() + data.length);
					} else {
						nearbyManageInfo.pageInfo.setNoMoreData(false);
					}
					nearbyManageInfo.pageInfo.setNeedClear(false);
					nearbyManageInfo.gridAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailed(int code, HttpException error, String reason) {
					hideLoading();
					ptrLayout.refreshComplete();
					nearbyManageInfo.pageInfo.setNeedClear(false);
					showToast(reason);
				}
			});
			request.getDataFromServer();
		}else{
			ptrLayout.refreshComplete();
			showToast("正在定位，请稍候");
		}
	}

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

	private void getDataIfNeeded() {
		if(!ifFirst){
			return;
		}
		ifFirst = false;
		initNearbyIfNeeded();
		if (0 == nearbyManageInfo.pageInfo.getDatas().size()) {
			nearbyManageInfo.pageInfo.setNeedClear(true);
			getNearbyData();
		}
	}

	@Override
	public void onLocationChange(LocationInfo locationInfo) {
		if (isLoading) {
			return;
		}
		getDataIfNeeded();
	}

}
