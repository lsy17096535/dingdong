package com.intexh.bidong.me;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.utils.UserUtils;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

public class ReceiveGiftsActivity extends BaseTitleActivity {
	
	public static final String MODE = "MODE";
	public static final int MODE_RECEIVED = 1;
	public static final int MODE_SENT = 2;
	
	@ViewInject(R.id.txt_receivegifts_value)
	private TextView valueTxt;
	@ViewInject(R.id.recyleview_receivegifts_grid)
	private RecyclerView gridView;
	private ReceivedGiftGridAdapter mAdapter = null;
	private List<GiftItemEntity> datas = new ArrayList<GiftItemEntity>();
	private int mode = MODE_RECEIVED;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_receivegifts);
		ViewUtils.inject(this);
		mode = getIntent().getIntExtra(MODE, MODE_RECEIVED);
		getData();
	}
	
	private void updateTotalValue(){
		int total = 0;
		for(GiftItemEntity entity : datas){
			total += entity.getValue()*entity.getCount();
		}
		valueTxt.setText(String.valueOf(total));
	}
	
	private void getData(){
		switch(mode){
		case MODE_RECEIVED:{
			getReceiveGifts();
			setTitleText(R.string.receivegifts);
			break;
		}
		case MODE_SENT:{
			getSendGifts();
			setTitleText(R.string.sendgifts);
			break;
		}
		}
	}
	
	private void getSendGifts(){
		GetSentGiftsRequest request = new GetSentGiftsRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<GiftItemEntity[]>() {

			@Override
			public void onSuccess(GiftItemEntity[] data) {
				hideLoading();
				if(null != data){
					datas.clear();
					for(GiftItemEntity entity : data){
						datas.add(entity);
					}
					gridView.setLayoutManager(new GridLayoutManager(ReceiveGiftsActivity.this, 4));
					mAdapter = new ReceivedGiftGridAdapter(ReceiveGiftsActivity.this);
					mAdapter.setDatas(datas);
					gridView.setAdapter(mAdapter);
					updateTotalValue();
				}
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

	private void getReceiveGifts(){
		GetReceivedGiftsRequest request = new GetReceivedGiftsRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<GiftItemEntity[]>() {

			@Override
			public void onSuccess(GiftItemEntity[] data) {
				hideLoading();
				if(null != data){
					datas.clear();
					for(GiftItemEntity entity : data){
						datas.add(entity);
					}
					gridView.setLayoutManager(new GridLayoutManager(ReceiveGiftsActivity.this, 4));
					mAdapter = new ReceivedGiftGridAdapter(ReceiveGiftsActivity.this);
					mAdapter.setDatas(datas);
					gridView.setAdapter(mAdapter);
//					mAdapter.notifyDataSetChanged();
					updateTotalValue();
				}
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
