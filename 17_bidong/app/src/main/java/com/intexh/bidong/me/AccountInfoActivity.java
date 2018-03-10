package com.intexh.bidong.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.charge.ChargeActivity;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

public class AccountInfoActivity extends BaseTitleActivity implements OnClickListener{

	@ViewInject(R.id.txt_accountinfo_value)
	private TextView valueTxt;
	@ViewInject(R.id.btn_accountinfo_charge)
	private Button chargeBtn;
	@ViewInject(R.id.btn_accountinfo_applycash)
	private Button applycashBtn;
	@ViewInject(R.id.layout_accountinfo_wechat)
	private View wechatView;
	@ViewInject(R.id.txt_accountinfo_bindstatus)
	private TextView bindStatusTxt;
	@ViewInject(R.id.layout_accountinfo_chargerecords)
	private View chargerecordsView;
	@ViewInject(R.id.layout_accountinfo_applyrecords)
	private View applyrecordsView;
	@ViewInject(R.id.layout_accountinfo_receivegifts)
	private View receivegiftsView;
	@ViewInject(R.id.layout_accountinfo_sendgifts)
	private View sendgiftsView;
	private Capital capital = null;
	private static final int REQUEST_BIND = 100;
	private static final int REQUEST_RECHARGE = REQUEST_BIND + 1;
	private static final int REQUEST_APPLY = REQUEST_RECHARGE + 1;
	private static final int REQUEST_APPLYCASH = REQUEST_APPLY + 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_accountinfo);
		setTitleText("我的钱包");
		ViewUtils.inject(this);
		getAccountInfo();
		chargeBtn.setOnClickListener(this);
		applycashBtn.setOnClickListener(this);
		wechatView.setOnClickListener(this);
		chargerecordsView.setOnClickListener(this);
		applyrecordsView.setOnClickListener(this);
		receivegiftsView.setOnClickListener(this);
		sendgiftsView.setOnClickListener(this);
		bindStatusTxt.setVisibility(View.GONE);
	}
	
	private void getAccountInfo(){
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				AccountInfoActivity.this.capital = data;
				valueTxt.setText(String.valueOf(data.getBalance()));
				bindStatusTxt.setVisibility(View.VISIBLE);
				if(null == data.getAccount()){
					bindStatusTxt.setText("未绑定");
				}else{
					bindStatusTxt.setText(data.getAccount());
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

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_accountinfo_charge:{
			Intent intent = new Intent(this,ChargeActivity.class);
			intent.putExtra(ChargeActivity.RICH_INFO, GsonUtils.objToJson(capital));
			startActivityForResult(intent,REQUEST_RECHARGE);
			break;
		}
		case R.id.btn_accountinfo_applycash:{
			if(null == capital){
				showToast("正在同步账号信息");
				return ;
			}
//			if(null == capital.getAccount()){
//				showToast("绑定支付宝账号才能提现哦~~");
//				Intent intent = new Intent(this,BindWechatActivity.class);
//				startActivityForResult(intent,REQUEST_BIND);
//			}else{
//				if(StringUtil.isEmptyString(UserUtils.getUserInfo().getUser().getUsername())){
//					showToast("请在[基本信息]里填写真实姓名");
//					return ;
//				}
				Intent intent = new Intent(this,ApplyCashActivity.class);
				intent.putExtra(ApplyCashActivity.CAPITAL, GsonUtils.objToJson(capital));
				startActivityForResult(intent,REQUEST_APPLYCASH);
//			}
			break;
		}
		case R.id.layout_accountinfo_wechat:{
			if(null == capital){
				showToast("正在同步账号信息");
				return ;
			}
			Intent intent = new Intent(this,BindWechatActivity.class);
			startActivityForResult(intent,REQUEST_BIND);
			break;
		}
		case R.id.layout_accountinfo_chargerecords:{
			Intent intent = new Intent(this,CashApplyRecordsActivity.class);
			intent.putExtra(CashApplyRecordsActivity.MODE, CashApplyRecordsActivity.MODE_CHARGERECORDS);
			startActivity(intent);
			break;
		}
		case R.id.layout_accountinfo_applyrecords:{
			Intent intent = new Intent(this,CashApplyRecordsActivity.class);
			startActivityForResult(intent, REQUEST_APPLY);
			break;
		}
		case R.id.layout_accountinfo_receivegifts:{
			Intent intent = new Intent(this,ReceiveGiftsActivity.class);
			intent.putExtra(ReceiveGiftsActivity.MODE, ReceiveGiftsActivity.MODE_RECEIVED);
			startActivity(intent);
			break;
		}
		case R.id.layout_accountinfo_sendgifts:{
			Intent intent = new Intent(this,ReceiveGiftsActivity.class);
			intent.putExtra(ReceiveGiftsActivity.MODE, ReceiveGiftsActivity.MODE_SENT);
			startActivity(intent);
			break;
		}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK == resultCode){
			switch(requestCode){
				case REQUEST_BIND:{
					getAccountInfo();
					break;
				}
				case REQUEST_RECHARGE:{
					getAccountInfo();
					break;
				}
				case REQUEST_APPLYCASH:
				case REQUEST_APPLY:{
					getAccountInfo();
					break;
				}
			}
		}
	}

}
