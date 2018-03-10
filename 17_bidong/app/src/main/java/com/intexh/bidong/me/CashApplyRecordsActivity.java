package com.intexh.bidong.me;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.me.CashApplyAdapter.OnCashApplyListener;
import com.intexh.bidong.userentity.CashApplyItemEntity;
import com.intexh.bidong.userentity.CashApplyItemEntityEx;
import com.intexh.bidong.userentity.CashApplyTotalEntity;
import com.intexh.bidong.utils.Page;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout.Mode;

public class CashApplyRecordsActivity extends BaseTitleActivity {
	public static final String MODE = "MODE";
	public static final int MODE_CASHAPPLY = 100;
	public static final int MODE_CHARGERECORDS = 101;
	
	@ViewInject(R.id.txt_cashapply_total)
	private TextView totalTxt;
	@ViewInject(R.id.txt_cashapply_realtotal)
	private TextView realTotalTxt;
	@ViewInject(R.id.list_cashapply_main)
	private ListView mainView;
	@ViewInject(R.id.frame_cashapply_refresh)
	private PtrClassicFrameLayout ptrLayout;
	private List<CashApplyItemEntity> datas = new ArrayList<CashApplyItemEntity>();
	private CashApplyAdapter mAdapter = null;
	private Page page = new Page();
	private boolean isNeedClear = false;
	private boolean isNoMoreData = false;
	private int mode = MODE_CASHAPPLY;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_cashapplyrecords);
		mode = getIntent().getIntExtra(MODE, MODE_CASHAPPLY);
		if(mode == MODE_CHARGERECORDS){
//			totalTxt.setVisibility(View.GONE);
			setTitleText(R.string.title_chargerecords);
			getChargeTotal();
		}else{
			setTitleText(R.string.title_cashrecords);
			getRecordTotal();
		}
		mAdapter = new CashApplyAdapter();
		mAdapter.setMode(mode);
		mAdapter.setmListener(new OnCashApplyListener() {
			
			@Override
			public void onClickItem(final CashApplyItemEntity entity) {
				CancelCashApplyRequest request = new CancelCashApplyRequest();
				request.setId(entity.getId());
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						showToast("取消成功");
						setResult(RESULT_OK);
						((CashApplyItemEntityEx)entity).setStatus(2);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onFailed(int code, HttpException error,
							String reason) {
						hideLoading();
						showToast(reason);
					}
				});
				showLoading();
				request.getDataFromServer();
			}
		});
		mAdapter.setDatas(datas);
		mainView.setAdapter(mAdapter);
		ptrLayout.setPullToRefresh(true);
		ptrLayout.setMode(Mode.BOTH);
		ptrLayout.setPtrHandler(new PtrDefaultHandler2() {

			@Override
			public void onLoadMoreBegin(PtrFrameLayout frame) {
				if(isNoMoreData){
					ptrLayout.refreshComplete();
				}else{
					getData();
				}
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				isNeedClear = true;
				getData();
			}

		});
		getData();
	}
	
	private void getData(){
		if(mode == MODE_CASHAPPLY){
			getRecordLists();
		}else{
			getChargeLists();
		}
	}

	private void getChargeTotal(){
		GetChargeTotalRequest request = new GetChargeTotalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<CashApplyTotalEntity>() {

			@Override
			public void onSuccess(CashApplyTotalEntity data) {
				hideLoading();
				totalTxt.setText("充值总额：￥" + String.valueOf(data.getAmount()));
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

	private void getChargeLists(){
		GetChargeListRequest request = new GetChargeListRequest();
		if(isNeedClear){
			request.setPage(new Page());
		}else{
			request.setPage(page);
		}
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<CashApplyItemEntity[]>() {

			@Override
			public void onSuccess(CashApplyItemEntity[] data) {
				hideLoading();
				ptrLayout.refreshComplete();
				if(isNeedClear){
					datas.clear();
					isNeedClear = false;
					page.setStart(0);
				}
				if(null != data){
					for(CashApplyItemEntity entity : data){
						datas.add(entity);
					}
					if(data.length < page.getCount()){
						isNoMoreData = true;
					}else{
						isNoMoreData = false;
					}
					page.setStart(page.getStart() + data.length);
				}else{
					isNoMoreData = true;
				}
//				int totalCount = 0;
//				for(CashApplyItemEntity entity : datas){
//					totalCount += entity.getAmount();
//				}
//				totalTxt.setText("充值金额：￥" + String.valueOf(totalCount));
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				ptrLayout.refreshComplete();
				isNeedClear = false;
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}
	
	private void getRecordTotal(){
		GetCashApplyTotalRequest request = new GetCashApplyTotalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<CashApplyTotalEntity>() {

			@Override
			public void onSuccess(CashApplyTotalEntity data) {
				hideLoading();
				totalTxt.setText("提现金额：￥" + String.valueOf(data.getAmount()));
				realTotalTxt.setText("到账金额：￥" + data.getActual_amount());
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
	
	private void getRecordLists(){
		CashApplyListRequest request = new CashApplyListRequest();
		if(isNeedClear){
			request.setPage(new Page());
		}else{
			request.setPage(page);
		}
		request.setUser_id(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<CashApplyItemEntityEx[]>() {

			@Override
			public void onSuccess(CashApplyItemEntityEx[] data) {
				hideLoading();
				ptrLayout.refreshComplete();
				if(isNeedClear){
					datas.clear();
					isNeedClear = false;
					page.setStart(0);
				}
				if(null != data){
					for(CashApplyItemEntityEx entity : data){
						datas.add(entity);
					}
					if(data.length < page.getCount()){
						isNoMoreData = true;
					}else{
						isNoMoreData = false;
					}
					page.setStart(page.getStart() + data.length);
				}else{
					isNoMoreData = true;
				}
//				updateTotal();
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				ptrLayout.refreshComplete();
				isNeedClear = false;
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}
	
	private void updateTotal(){
		int total = 0;
		for(CashApplyItemEntity entity : datas){
			total += entity.getActual_amount();
		}
		totalTxt.setText("提现金额：￥" + String.valueOf(total));
	}
	
}
