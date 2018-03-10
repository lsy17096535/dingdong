package com.intexh.bidong.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.intexh.bidong.net.Apis;
import com.intexh.bidong.net.NetworkManager;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.constants.Constants;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ApplyCashActivity extends BaseTitleActivity implements IWXAPIEventHandler {

	public static final String CAPITAL = "CAPITAL";
	private Capital capital = null;
	@ViewInject(R.id.txt_applycash_value)
	private TextView valueTxt;
	@ViewInject(R.id.edit_applycash_count)
	private EditText countInput;
	@ViewInject(R.id.btn_applycash_commit)
	private Button commitBtn;
	@ViewInject(R.id.txt_title_confirm)
	private TextView confirmView;
	@ViewInject(R.id.txt_applycash_tips)
	private TextView tipsView;
	@ViewInject(R.id.image_applycash_checkbox)
	private ImageView checkbox;
	@ViewInject(R.id.edit_card_number)
	private EditText edit_card_number;
	@ViewInject(R.id.edit_card_user)
	private EditText edit_card_user;
	@ViewInject(R.id.edit_bank_name)
	private EditText edit_bank_name;

	private IWXAPI api = null;

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				//分享成功
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				//分享取消
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//分享拒绝
				break;
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
	 *
	 */
	private void wechatShare() {
		String title =String.format("我从%s提现%s元,%s", this.getString(R.string.app_name), countInput.getText(), this.getString(R.string.weixin_share));
		WechatMoments.ShareParams spWeChatMoments = new WechatMoments.ShareParams();
		spWeChatMoments.setShareType(Platform.SHARE_WEBPAGE);
		spWeChatMoments.setText(title);
		spWeChatMoments.setUrl("http://bidong.intexh.com/download/");
		spWeChatMoments.setImageUrl("http://106.75.118.28:8080/img/system/logo.png");
		spWeChatMoments.setTitle(title);
		spWeChatMoments.setComment(title);
		Platform weChatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
		weChatMoments.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				showToast("分享成功！");
			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {
				showToast("分享失败");
			}

			@Override
			public void onCancel(Platform platform, int i) {
				showToast("分享取消");
			}
		}); // 设置分享事件回调
		// 执行图文分享
		weChatMoments.share(spWeChatMoments);

//		WXWebpageObject webpage = new WXWebpageObject();
//		webpage.webpageUrl = "http://www.ppstar.cn";
//		WXMediaMessage msg = new WXMediaMessage(webpage);
//		msg.title = getString(R.string.app_name) + getString(R.string.app_title);
//		msg.description = String.format("我从%s提现%s元,%s", this.getString(R.string.app_name),
//				countInput.getText(), this.getString(R.string.weixin_share));
//		// 这里替换一张自己工程里的图片资源
//		BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(
//				R.drawable.ic_launcher);
//		Bitmap thumb = bmpDraw.getBitmap();
//		msg.setThumbImage(thumb);
//
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = String.valueOf(System.currentTimeMillis());
//		req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		boolean ret = api.sendReq(req);
//		if(ret){
//			showToast("调用成功");
//		}else{
//			showToast("调用失败");
//		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_applycash);
		setTitleText(R.string.common_applycash);
		checkbox.setSelected(true);
		checkbox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				checkbox.setSelected(!checkbox.isSelected());
			}
		});
		commitBtn.setEnabled(false);
		countInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String ss = countInput.getText().toString();
				if(!RegexUtil.checkDigit(ss)){
					commitBtn.setEnabled(false);
				}else{
					int realCount = Integer.parseInt(ss);
					if(realCount < 100){
						commitBtn.setEnabled(false);
					}else{
						if(realCount <= capital.getBalance()){
							commitBtn.setEnabled(true);
						}else{
							commitBtn.setEnabled(false);
						}
					}
//					int realValue = (int)(realCount*(1-(capital.getRate()/100.0))+0.5);
//					tipsView.setText("提现需收取" + capital.getRate()+"%手续费，实际到账金额"+ realValue+ "元");
				}
			}
		});
		String ss = getIntent().getStringExtra(CAPITAL);
		capital = GsonUtils.jsonToObj(ss, Capital.class);
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				commitApplyCash();
			}
		});
		tipsView.setText("提现手续费将从余额扣除，实际到账金额为30%（含税）");
		valueTxt.setText(String.valueOf(capital.getBalance()));
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID,true);
		api.registerApp(Constants.APP_ID);
	}

	private void commitApplyCash(){
		String ss = countInput.getText().toString();
		if(!RegexUtil.checkDigit(ss)){
			showToast(R.string.error_digit);
			return ;
		}
		int realCount = Integer.parseInt(ss);
		if(realCount <= 0){
			showToast(R.string.error_digit);
			return ;
		}
		if(realCount > capital.getBalance()){
			showToast(R.string.error_overmax);
			return ;
		}
		String card_number = edit_card_number.getText().toString();
		if(TextUtils.isEmpty(card_number)){
			showToast("请输入支付宝账号");
			return;
		}
		String card_user = edit_card_user.getText().toString();
		if(TextUtils.isEmpty(card_user)){
			showToast("请输入支付宝账户姓名");
			return;
		}
//		String bank_name = edit_bank_name.getText().toString();
//		if(TextUtils.isEmpty(bank_name)){
//			showToast("请输入收款银行");
//			return;
//		}
		Map<String,String> params =new HashMap<>();
		params.put("user_id", UserUtils.getUserid());
		params.put("amount", String.valueOf(realCount));
		params.put("card_no", card_number);
		params.put("card_name", card_user);
//		params.put("card_band", bank_name);
		showLoading();
		NetworkManager.INSTANCE.post(Apis.cash, params, new NetworkManager.OnRequestCallBack() {
			@Override
			public void onOk(String response) {
				hideLoading();
				setResult(RESULT_OK);
				showToast("提现申请提交成功");
				if (checkbox.isSelected()) {
					if (api.isWXAppSupportAPI() && api.isWXAppInstalled()) {
						wechatShare();
						finish();
					} else {
						finish();
					}
				} else {
					finish();
				}
			}

			@Override
			public void onError(String errorMessage) {
				hideLoading();
				showToast(errorMessage);
			}
		});
//		CashApplyRequest request = new CashApplyRequest();
//		request.setAmount(realCount);
//		request.setUser_id();
//		request.setCard_no();
//		request.setCard_name();
//		request.setCard_band();
//		request.setNetworkListener(new CommonNetworkCallback<String>() {
//
//            @Override
//            public void onSuccess(String data) {
//                hideLoading();
//                setResult(RESULT_OK);
//                showToast("提现申请提交成功");
//                if (checkbox.isSelected()) {
//                    if (api.isWXAppSupportAPI() && api.isWXAppInstalled()) {
//                        wechatShare();
//                        finish();
//                    } else {
//                        finish();
//                    }
//                } else {
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailed(int code, HttpException error, String reason) {
//                hideLoading();
//                showToast(reason);
//            }
//        });
//		showLoading();
//		request.getDataFromServer();
	}

}
