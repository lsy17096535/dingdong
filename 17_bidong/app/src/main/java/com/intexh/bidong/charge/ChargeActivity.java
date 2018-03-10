package com.intexh.bidong.charge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.me.RealChargeActivity;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.ChargePackageItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class ChargeActivity extends BaseTitleActivity{

	public static final String RICH_INFO = "RICH_INFO";
	
	@ViewInject(com.intexh.bidong.R.id.list_charge_list)
	private ListView chargeListView;
	private TextView currentValueTxt;
	private LinearLayout containerView;
	private List<ChargePackageItemEntity> datas = new ArrayList<ChargePackageItemEntity>();
	private com.intexh.bidong.charge.ChargePackagesAdapter mAdapter = new com.intexh.bidong.charge.ChargePackagesAdapter();
	private Capital richInfo = null;
	private static final int REQUEST_BIND = 100;
	private static final int REQUEST_CHARGE = 101;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(com.intexh.bidong.R.layout.activity_charge);
		setTitleText(com.intexh.bidong.R.string.common_charge);
		mAdapter.setDatas(datas);
		View headView = LayoutInflater.from(this).inflate(R.layout.header_charge,null);
		currentValueTxt = (TextView)headView.findViewById(R.id.txt_charge_value);
		View footerView = LayoutInflater.from(this).inflate(R.layout.footer_charge,null);
		containerView = (LinearLayout)footerView.findViewById(R.id.layout_userdetail_container);
		chargeListView.addHeaderView(headView);
		chargeListView.addFooterView(footerView);
		chargeListView.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ChargePackageItemEntity entity = datas.get(arg2);
				Intent intent = new Intent(ChargeActivity.this,RealChargeActivity.class);
				intent.putExtra(RealChargeActivity.PACKAGE_ENTITY, GsonUtils.objToJson(entity));
				startActivityForResult(intent,REQUEST_CHARGE);
			}
		});
//		chargeBtn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				Intent intent = new Intent(ChargeActivity.this,BindWechatActivity.class);
//				startActivityForResult(intent,REQUEST_BIND);
//			}
//		});
//		licenseView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//
//			}
//		});
		String ss = getIntent().getStringExtra(RICH_INFO);
		richInfo = GsonUtils.jsonToObj(ss, Capital.class);
		currentValueTxt.setText(String.valueOf(richInfo.getBalance()));
		getChargePackages();
	}


	private void getChargePackages(){
		com.intexh.bidong.charge.GetChargePackagesRequest request = new com.intexh.bidong.charge.GetChargePackagesRequest();
		request.setNetworkListener(new CommonNetworkCallback<ChargePackageItemEntity[]>() {

			@Override
			public void onSuccess(ChargePackageItemEntity[] data) {
				hideLoading();
				datas.clear();
				List<String> badges = new ArrayList<String>();
				if(null != data){
					for(ChargePackageItemEntity entity : data){
						if(null != entity.getBadge_uri()){
							badges.add(entity.getBadge_uri());
						}
						datas.add(entity);
					}
				}
				if(0 == badges.size()){
					containerView.setVisibility(View.GONE);
				}else{
					containerView.setVisibility(View.VISIBLE);
					int index = 0;
					for (String badge : badges) {
						ImageView imageView = new ImageView(
								ChargeActivity.this);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								PtrLocalDisplay.designedDP2px(45),
								PtrLocalDisplay.designedDP2px(45));
						if (index != badges.size() - 1) {
							params.rightMargin = PtrLocalDisplay
									.designedDP2px(10);
						}
						index++;
						imageView.setScaleType(ImageView.ScaleType.FIT_XY);
						containerView.addView(imageView, params);
						ImageUtils.loadSnapshotDefaultImage(
								badge, imageView);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK == resultCode){
			switch(requestCode){
			case REQUEST_BIND:{
				setResult(RESULT_OK);
				break;
			}
			case REQUEST_CHARGE:{
				setResult(RESULT_OK);
				finish();
				break;
			}
			}
		}
	}
	
}
