package com.intexh.bidong.main.square;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;
import com.intexh.bidong.callback.ModeChangeCallback;
import com.intexh.bidong.callback.RecyclerItemClickListener;
import com.intexh.bidong.callback.RecyclerItemClickListener.OnItemClickListener;
import com.intexh.bidong.common.CommonAdapter;
import com.intexh.bidong.common.ViewHolder;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.me.GetOffServiceItemRequest;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.userentity.OffServiceItemEntity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.PageInfo;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.SpacesItemDecoration;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout.Mode;

public class MainSquareOffServiceFragment extends BaseFragment implements ModeChangeCallback<Object> {
	public static MainSquareOffServiceFragment instance = null;
	private static final String SERVICE_DATA ="SERVICE_DATA";
	public static final int MODE_SERVICE = 1;
	private static final int REQUEST_OFFSERVICE = 400;
	private boolean isLoading = false;
	private boolean ifFirst = true;

	private String filterItemId = "0";
	private CommonAdapter osAdapter;
	private Indicator.IndicatorAdapter indicatorAdapter;
	public static List<OffServiceItemEntity> osItems = new ArrayList<OffServiceItemEntity>();
	private static List<String> itemNames = new ArrayList<String>();
	private PopupWindow offServItemsWindow;

	private View mView = null;
	@ViewInject(R.id.square_indicator_layout)
	private FrameLayout indicatorLayout;
	@ViewInject(R.id.square_indicator_scrollbar)
	private ScrollIndicatorView indicatorView;
	@ViewInject(R.id.square_indicator_filter)
	private CheckBox filterCheckBox;
	@ViewInject(R.id.recycler_mainsquare_main)
	private RecyclerView gridView;
	@ViewInject(R.id.rotate_header_grid_view_frame)
	private PtrClassicFrameLayout ptrLayout;
	private RecyclerItemClickListener serviceItemClickListener = null;
	private SpacesItemDecoration spaceManager = null;
	private MainSquareGridManager<MainSquareOffServiceAdapter> serviceManageInfo = null;

	private class MainSquareGridManager<T extends RecyclerView.Adapter> {
		GridLayoutManager gridManager;
		T gridAdapter;
		PageInfo pageInfo;
	}

	private class PageServiceInfo extends PageInfo<OffServiceEntity>{}

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
			int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing);
			spaceManager = new SpacesItemDecoration(spacingInPixels, 1);
			spaceManager.setNumOfColumn(1);
//			gridView.addItemDecoration(spaceManager);
			ptrLayout.setPullToRefresh(true);
			ptrLayout.setMode(Mode.BOTH);
			ptrLayout.setPtrHandler(new PtrDefaultHandler2() {

				@Override
				public void onLoadMoreBegin(PtrFrameLayout frame) {
					if(serviceManageInfo.pageInfo.isNoMoreData()){
						ptrLayout.refreshComplete();
					}else{
						getServiceData();
					}
				}

				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					serviceManageInfo.pageInfo.setNeedClear(true);
					getServiceData();
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
		initServiceIfNeeded();
		String ss = null;
		if(null != savedInstanceState){
			ss = savedInstanceState.getString(SERVICE_DATA);
		}
		if(null != ss){
			PageServiceInfo entity = GsonUtils.jsonToObj(ss, PageServiceInfo.class);
			if(null != entity){
				serviceManageInfo.pageInfo = entity;
				serviceManageInfo.gridAdapter.setDatas(entity.getDatas());
			}
		}
		if (0 == serviceManageInfo.pageInfo.getDatas().size()) {
			serviceManageInfo.pageInfo.setNeedClear(true);
			getServiceData();
		}
		switchToService();

		indicatorLayout.setVisibility(View.VISIBLE);
		indicatorView.setOnTransitionListener(new OnTransitionTextListener().setSize(15*1.1f, 15));
		indicatorAdapter = new Indicator.IndicatorAdapter() {
			@Override
			public int getCount() {
				return itemNames.size();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup container) {
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.tab_filter_text, container, false);
				}
				TextView textView = (TextView) convertView;
				textView.setText(itemNames.get(position % itemNames.size()));
				textView.setPadding(20, 0, 20, 0);
				return convertView;
			}
		};
		indicatorView.setAdapter(indicatorAdapter);
		indicatorView.setOnItemSelectListener(new Indicator.OnItemSelectedListener() {
			@Override
			public void onItemSelected(View view, int i, int i1) {
				setFilterItemId(String.valueOf(i));
			}
		});
		offServItemsWindow = buildPopupWindow();
		filterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					offServItemsWindow.showAsDropDown(filterCheckBox);
				}
				else{
					offServItemsWindow.dismiss();
				}
			}
		});
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (0 == serviceManageInfo.pageInfo.getDatas().size()) {
			serviceManageInfo.pageInfo.setNeedClear(true);
			getServiceData();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SERVICE_DATA, GsonUtils.objToJson(serviceManageInfo.pageInfo));
	}

	private void initServiceIfNeeded() {
		if (null == serviceManageInfo) {
			serviceManageInfo = new MainSquareGridManager<MainSquareOffServiceAdapter>();
			serviceManageInfo.gridManager = new GridLayoutManager(getActivity(), 1);
			serviceManageInfo.gridAdapter = new MainSquareOffServiceAdapter(getActivity());
			serviceManageInfo.pageInfo = new PageInfo<OffServiceEntity>();
			serviceManageInfo.gridAdapter.setDatas(serviceManageInfo.pageInfo.getDatas());
			serviceItemClickListener = new RecyclerItemClickListener(getActivity(),new OnItemClickListener() {

				@Override
				public void onItemClick(View view, int position) {
					Intent intent = new Intent(getActivity(), OrderSendActivity.class);
					OffServiceEntity entity = ((List< OffServiceEntity>)serviceManageInfo.pageInfo.getDatas()).get(position);
					intent.putExtra(OrderSendActivity.SERVICE_ENTITY, GsonUtils.objToJson(entity));
					startActivityForResult(intent, REQUEST_OFFSERVICE);
				}
			});
		}
	}

	private void switchToService() {
		initServiceIfNeeded();
		gridView.setLayoutManager(serviceManageInfo.gridManager);
		gridView.removeOnItemTouchListener(serviceItemClickListener);
		gridView.addOnItemTouchListener(serviceItemClickListener);
		gridView.setAdapter(serviceManageInfo.gridAdapter);
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

	private void getServiceData() {
		getServerItems();
		LocationInfo locationInfo = LocationLocalManager.getInstance().getLastLocation();
		if (null != locationInfo) {
			if (isLoading) {
				return;
			}
			showLoading();
			RegDataEntity userInfo = UserUtils.getUserInfo();
			GetNearbyOffServiceRequest request = new GetNearbyOffServiceRequest();
			if(null != filterItemId && Integer.parseInt(filterItemId) > 0){
				request.setItemId(filterItemId);
			}
			request.setGender(userInfo.getUser().getGender());
			request.setLocation(locationInfo.location);
			if (serviceManageInfo.pageInfo.isNeedClear()) {
				request.setPage(new Page());
			} else {
				request.setPage(serviceManageInfo.pageInfo.getPage());
			}
			request.setNetworkListener(new CommonNetworkCallback<OffServiceEntity[]>() {

				@Override
				public void onSuccess(OffServiceEntity[] data) {
					hideLoading();
					ptrLayout.refreshComplete();
					if (null != data) {
						if (serviceManageInfo.pageInfo.isNeedClear()) {
							serviceManageInfo.pageInfo.getDatas().clear();
							serviceManageInfo.pageInfo.getPage().setStart(0);
						}
						for (OffServiceEntity entity : data) {
							serviceManageInfo.pageInfo.getDatas().add(entity);
						}
						if (data.length < serviceManageInfo.pageInfo.getPage().getCount()) {
							serviceManageInfo.pageInfo.setNoMoreData(true);
						} else {
							serviceManageInfo.pageInfo.setNoMoreData(false);
						}
						serviceManageInfo.pageInfo.getPage().setStart(serviceManageInfo.pageInfo.getPage().getStart() + data.length);
					} else {
						serviceManageInfo.pageInfo.setNoMoreData(false);
					}
					serviceManageInfo.pageInfo.setNeedClear(false);
					serviceManageInfo.gridAdapter.notifyDataSetChanged();
				}

				@Override
				public void onFailed(int code, HttpException error, String reason) {
					hideLoading();
					ptrLayout.refreshComplete();
					serviceManageInfo.pageInfo.setNeedClear(false);
					showToast(reason);
				}
			});
			request.getDataFromServer();
		}
	}

	public void getServerItems(){
		// 获取线下约会所有项目
		if(!osItems.isEmpty()) {
			return;
		}
		GetOffServiceItemRequest request = new GetOffServiceItemRequest();
		request.setNetworkListener(new CommonNetworkCallback<OffServiceItemEntity[]>() {
			@Override
			public void onSuccess(OffServiceItemEntity[] data) {
				if(null != data){
					Arrays.sort(data);
					itemNames.clear();
					osItems.clear();
					itemNames.add("全部");
					for(OffServiceItemEntity item : data){
						itemNames.add(item.getName());
						osItems.add(item);
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

	private void setFilterItemId(String itemId){
		if(!filterItemId.equals(itemId)){
			this.filterItemId = itemId;
			serviceManageInfo.pageInfo.setNeedClear(true);
			getServiceData();
		}
	}

	private void getDataIfNeeded() {
		if(!ifFirst){
			return;
		}
		ifFirst = false;
		initServiceIfNeeded();
		if (0 == serviceManageInfo.pageInfo.getDatas().size()) {
			serviceManageInfo.pageInfo.setNeedClear(true);
			getServiceData();
		}
	}

	@Override
	public void onLocationChange(LocationInfo locationInfo) {
		if(isLoading){
			return ;
		}
		getDataIfNeeded();
	}

	private PopupWindow buildPopupWindow(){
		// 一个自定义的布局，作为显示的内容
		final View contentView = LayoutInflater.from(this.getActivity()).inflate(R.layout.activity_offserviceitemfilter, null);
		final PopupWindow offServItemsWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		offServItemsWindow.setFocusable(false);
		offServItemsWindow.setOutsideTouchable(true);
		offServItemsWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				filterCheckBox.setChecked(false);
			}
		});
		GridView gridView = (GridView) contentView.findViewById(R.id.gridview_yuema_items);
		osAdapter = new CommonAdapter<OffServiceItemEntity>(this.getActivity(), osItems, R.layout.listitem_offserviceitem) {

			@Override
			public void convert(ViewHolder holder, final OffServiceItemEntity item) {
				((TextView)holder.getView(R.id.txt_offservice_item_name)).setText(item.getName());
				ImageView iconView = holder.getView(R.id.image_offservice_item_icon);
				ImageUtils.loadOffServiceItemImage(item.getUri(), iconView);
				iconView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						setFilterItemId(item.getId());
						indicatorView.setCurrentItem(Integer.parseInt(item.getId()));
						filterCheckBox.setChecked(false);
					}
				});
			}
		};
		gridView.setAdapter(osAdapter);
		return offServItemsWindow;
	}

}
