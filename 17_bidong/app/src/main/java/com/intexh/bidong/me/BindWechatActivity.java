package com.intexh.bidong.me;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.PayResult;
import com.intexh.bidong.utils.UserUtils;

public class BindWechatActivity extends BaseTitleActivity {

	@ViewInject(R.id.btn_bindwechat_pay)
	private Button payBtn;
	private static final int SDK_PAY_FLAG = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_bindwechat);
		setTitleText(R.string.title_bindwechat);
		payBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				jumpToPay();
			}
		});
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private void jumpToPay(){
		WeChatPayRequest request = new WeChatPayRequest();
		request.setAmount(0.01);
		request.setPackageid("0");
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(final String data) {
				hideLoading();
				// 构造PayTask 对象
				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						PayTask alipay = new PayTask(BindWechatActivity.this);
						// 调用支付接口，获取支付结果
						String result = alipay.pay(data);

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				// 必须异步调用
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
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
		
	};
	
}
