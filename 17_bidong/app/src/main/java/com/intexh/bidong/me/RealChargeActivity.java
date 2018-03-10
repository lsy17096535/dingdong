package com.intexh.bidong.me;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.ChargePackageItemEntity;
import com.intexh.bidong.userentity.PayResult;
import com.intexh.bidong.utils.UserUtils;

public class RealChargeActivity extends BaseTitleActivity {

	public static final String PACKAGE_ENTITY = "PACKAGE_ENTITY";
	@ViewInject(R.id.txt_realcharge_value)
	private TextView valueTxt;
	@ViewInject(R.id.btn_realcharge_charge)
	private Button chargeBtn;
	@ViewInject(R.id.btn_realcharge_pay)
	private Button payBtn;
	private ChargePackageItemEntity entity = null;
	private static final int SDK_PAY_FLAG = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_realcharge);
		setTitleText(R.string.common_charge);
		String ss = getIntent().getStringExtra(PACKAGE_ENTITY);
		entity = GsonUtils.jsonToObj(ss, ChargePackageItemEntity.class);
		valueTxt.setText(String.valueOf(entity.getValue()));
		chargeBtn.setText("￥" + entity.getAmount());
		payBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				jumpToPay();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					showToast("支付成功");
					setResult(RESULT_OK);
					finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						showToast("支付结果确认中");
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						showToast("支付失败");
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	private void jumpToPay(){
		WeChatPayRequest request = new WeChatPayRequest();
		request.setAmount(entity.getAmount());
		request.setPackageid(entity.getId());
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(final String data) {
				hideLoading();
				// 必须异步调用
				// 构造PayTask 对象
				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						// 构造PayTask 对象
						PayTask alipay = new PayTask(RealChargeActivity.this);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(data);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				Thread payThread = new Thread(payRunnable);
				payThread.start();
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
