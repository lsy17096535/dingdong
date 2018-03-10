package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.me.MyOffServiceAdapter.OnItemListener;
import com.intexh.bidong.me.MyOffServiceAdapter.OnMenuListener;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.UserUtils;

public class MyOffServiceListActivity extends BaseTitleActivity {
	@ViewInject(R.id.list_myoffservice_main)
	private ListView mainView;
	@ViewInject(R.id.image_title_right)
	private ImageView rightView;

	@ViewInject(R.id.layout_mainorder_empty)
	private RelativeLayout emptylayout;
	@ViewInject(R.id.txt_mainorder_empty_tip)
	private TextView emptyTipView;
	@ViewInject(R.id.txt_mainorder_empty_action)
	private TextView emptyActionView;

	private List<OffServiceEntity> datas = new ArrayList<OffServiceEntity>();
	private List<String> itemids = new ArrayList<String>();
	private MyOffServiceAdapter mAdapter = null;

	private View.OnClickListener publishListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			addOffServiceItem();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_myoffservicelist);
		setTitleText(R.string.me_offlineservice);

		//右侧加号按钮
		rightView.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				addOffServiceItem();
			}
		});

		mAdapter = new MyOffServiceAdapter();
		mAdapter.setItemListener(new OnItemListener() {
			@Override
			public void onClickItem(final OffServiceEntity entity) {
				Intent intent = new Intent(MyOffServiceListActivity.this, MyOffServiceAddActivity.class);
				intent.putExtra("mode", MyOffServiceAddActivity.MODE_EDIT);
				intent.putExtra("data", entity);
				startActivity(intent);
			}
		});
		mAdapter.setMenuListener(new OnMenuListener() {
			@Override
			public void didClickDelete(int index) {
				deleteOffService(index);
			}
		});
		mAdapter.setDatas(datas);
		mainView.setAdapter(mAdapter);
	}

	//发布线下约会项目
	private void addOffServiceItem(){
		if(!UserUtils.isVideoAudit()){
			UserDetailActivity.showRecordVideoAlert(MyOffServiceListActivity.this);
			return;
		}
		Intent intent = new Intent(MyOffServiceListActivity.this, OffServiceItemsActivity.class);
		intent.putExtra("itemids", itemids.toArray(new String[itemids.size()]));
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	private void updateEmptyView(){
		if(!datas.isEmpty()){
			mainView.setVisibility(View.VISIBLE);
			emptylayout.setVisibility(View.GONE);
		}else{
			mainView.setVisibility(View.GONE);
			emptylayout.setVisibility(View.VISIBLE);
			emptylayout.setOnClickListener(publishListener);
			emptyActionView.setVisibility(View.VISIBLE);
		}
	}
	
	private void getData(){
		GetOffServiceRequest request = new GetOffServiceRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<OffServiceEntity[]>() {

			@Override
			public void onSuccess(OffServiceEntity[] data) {
				hideLoading();
				datas.clear();
				itemids.clear();
				for(OffServiceEntity entity : data){
					datas.add(entity);
					itemids.add(entity.getItem().getId());
				}
				mAdapter.notifyDataSetChanged();
				updateEmptyView();
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

	private void deleteOffService(final int index){
		DeleteMyOffServiceRequest request = new DeleteMyOffServiceRequest();
		request.setId(datas.get(index).getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("线下服务删除成功");
				datas.remove(index);
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
	
}
