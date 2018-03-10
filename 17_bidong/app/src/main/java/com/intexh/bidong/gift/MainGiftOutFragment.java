package com.intexh.bidong.gift;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.intexh.bidong.me.MyTrendVideoActivity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.PageInfo;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout.Mode;

public class MainGiftOutFragment extends BaseFragment implements ModeChangeCallback<Object> {
	public static MainGiftOutFragment instance = null;
	
	public static final int MODE_SEND = 0;
	private static final int REQUEST_SEND = 100;
	
	public static final String MODE = "MODE";
	private static final String SEND_DATA = "SEND_DATA";
	private SendGiftItemEntity selectEntity = null;
	private OnClickListener recordListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(getActivity(),MyTrendVideoActivity.class);
			intent.putExtra(MyTrendVideoActivity.IS_SHOULDRECORD, true);
			startActivity(intent);
		}
	};

	private class MainGiftGridManager<T extends RecyclerView.Adapter> {
		T gridAdapter;
		PageGiftInfo pageInfo;
	}

	private class PageGiftInfo extends PageInfo<SendGiftItemEntity>{}
	
	private int mode = MODE_SEND;

	private View mView = null;
	@ViewInject(R.id.recycler_maingift_main)
	private RecyclerView gridView;
	@ViewInject(R.id.pull_maingift_pull)
	private PtrClassicFrameLayout ptrLayout;
	@ViewInject(R.id.layout_maingift_empty)
	private LinearLayout emptylayout;
	@ViewInject(R.id.txt_maingift_empty_tip)
	private TextView emptyTipView;
	@ViewInject(R.id.txt_maingift_empty_action)
	private TextView emptyActionView;
	private MainGiftGridManager<MainGiftSendAdapter> sendManager;
	private MainGiftGridManager<MainGiftSendAdapter> receiveManager;
	private RecyclerItemClickListener sendItemClickListener = null;
	private RecyclerItemClickListener receiveItemClickListener = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		instance = this;
		LogUtils.d("MainGiftFragment onCreateView , instance = " + this);
		if (null == mView) {
			mView = inflater.inflate(R.layout.fragment_maingift, null);
			ViewUtils.inject(this, mView);
			GridLayoutManager mgr = new GridLayoutManager(getActivity(), 1);
			gridView.setLayoutManager(mgr);
			ptrLayout.setPullToRefresh(true);
			ptrLayout.setMode(Mode.BOTH);
			ptrLayout.setPtrHandler(new PtrDefaultHandler2() {

				@Override
				public void onLoadMoreBegin(PtrFrameLayout frame) {
					if(sendManager.pageInfo.isNoMoreData()){
						ptrLayout.refreshComplete();
					}else{
						getSendGiftData();
					}
				}

				@Override
				public void onRefreshBegin(PtrFrameLayout frame) {
					sendManager.pageInfo.setNeedClear(true);
					getSendGiftData();
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
//		int defaultMode = UserUtils.getUserInfo().getUser().getGender() == 1 ? MODE_RECEIVE : MODE_SEND;
//		mode = savedInstanceState.getInt(MODE, defaultMode);
		initSendGiftIfNeed();
		String ss = null;
		if(null != savedInstanceState){
			ss = savedInstanceState.getString(SEND_DATA);
		}
		if(null != ss){
			PageGiftInfo entity = GsonUtils.jsonToObj(ss, PageGiftInfo.class);
			if(null != entity){
				sendManager.pageInfo = entity;
				sendManager.gridAdapter.setDatas(entity.getDatas());
			}
		}
		switchToSend();
		return mView;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(MODE, mode);
		outState.putString(SEND_DATA, GsonUtils.objToJson(sendManager.pageInfo));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		instance = null;
	}
	
	private void getDataIfNeed(){
		if(0 == sendManager.pageInfo.getDatas().size()) {
			sendManager.pageInfo.setNeedClear(true);
			getSendGiftData();
		}
	}
	
	private void getSendGiftData(){
		GetSendGiftListRequest request = new GetSendGiftListRequest();
		if(sendManager.pageInfo.isNeedClear()){
			request.setPage(new Page());
		}else{
			request.setPage(sendManager.pageInfo.getPage());
		}
		request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity[]>() {

			@Override
			public void onSuccess(SendGiftItemEntity[] data) {
				hideLoading();
				ptrLayout.refreshComplete();
				if(sendManager.pageInfo.isNeedClear()){
					sendManager.pageInfo.getDatas().clear();
					sendManager.pageInfo.setNeedClear(false);
					sendManager.pageInfo.getPage().setStart(0);
				}
				if(null != data){
					for(SendGiftItemEntity entity : data){
						sendManager.pageInfo.getDatas().add(entity);
					}
					if(data.length < sendManager.pageInfo.getPage().getCount()){
						sendManager.pageInfo.setNoMoreData(true);
					}else{
						sendManager.pageInfo.setNoMoreData(false);
					}
				}else{
					sendManager.pageInfo.setNoMoreData(true);
				}
				sendManager.gridAdapter.notifyDataSetChanged();
				updateSendEmptyView();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				ptrLayout.refreshComplete();
				sendManager.pageInfo.setNeedClear(false);
				showToast(reason);
			}
		});
		request.setUserid(UserUtils.getUserid());
		showLoading();
		request.getDataFromServer();
	}
	
	private void initSendGiftIfNeed(){
		if(null == sendManager){
			sendManager = new MainGiftGridManager<MainGiftSendAdapter>();
			PageGiftInfo pageInfo = new PageGiftInfo();
			sendManager.pageInfo = pageInfo;
			sendManager.gridAdapter = new MainGiftSendAdapter(MainGiftSendAdapter.GIFT_SEND);
			sendManager.gridAdapter.setDatas(pageInfo.getDatas());
			sendItemClickListener = new RecyclerItemClickListener(getActivity(), new OnItemClickListener() {
				
				@Override
				public void onItemClick(View view, int position) {
					SendGiftItemEntity entity = sendManager.pageInfo.getDatas().get(position);
					selectEntity = entity;
					Intent intent = new Intent(getActivity(),GiftDetailActivity.class);
					intent.putExtra(GiftDetailActivity.GIFT_ENTITY, GsonUtils.objToJson(entity));
					startActivityForResult(intent, REQUEST_SEND);
				}
			});
		}
	}

	private void switchToSend() {
		initSendGiftIfNeed();
		gridView.setAdapter(sendManager.gridAdapter);
		gridView.removeOnItemTouchListener(receiveItemClickListener);
		gridView.removeOnItemTouchListener(sendItemClickListener);
		gridView.addOnItemTouchListener(sendItemClickListener);
		updateSendEmptyView();
		getDataIfNeed();
	}
	
	private void updateSendEmptyView(){
		if(!sendManager.pageInfo.getDatas().isEmpty()){
			ptrLayout.setVisibility(View.VISIBLE);
			emptylayout.setVisibility(View.GONE);
		}else{
			ptrLayout.setVisibility(View.GONE);
			emptylayout.setVisibility(View.VISIBLE);
			emptylayout.setOnClickListener(null);
			emptyActionView.setVisibility(View.GONE);
			emptyTipView.setText("给心动的人送个小礼物，开启激情互动！");
		}
	}

	@Override
	public void onModeChange(int mode, Object data) {
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch(requestCode){
				case REQUEST_SEND:{
					int status = data.getIntExtra(GiftDetailActivity.GIFT_STATUS, 0);
					selectEntity.setStatus(status);
					sendManager.gridAdapter.notifyDataSetChanged();
					break;
				}
			}
		}
	}

	@Override
	protected void onBaseResume() {
		getDataIfNeed();
	}

	@Override
	protected void onBasePause() {}

	@Override
	public void onLocationChange(LocationInfo locationInfo) {
	}

}
