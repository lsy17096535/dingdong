package com.intexh.bidong.order;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager;
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
import com.intexh.bidong.charge.ChargeActivity;
import com.intexh.bidong.constants.Constants;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.OrderEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShareThemeImpl;
import cn.sharesdk.wechat.moments.WechatMoments;

public class OrderDetailActivity extends BaseTitleActivity implements OnClickListener,IWXAPIEventHandler, EMEventListener{
	
	public static final String ORDER_ENTITY = "ORDER_ENTITY";
	public static final String ORDER_RESULT = "ORDER_RESULT";
	public static final String ORDER_STATUS = "ORDER_STATUS";
	public static final String ORDER_PAY_STATUS = "PAY_STATUS";
	public static final String ORDER_PAY_AMOUNT = "PAY_AMOUNT";
	public static final int ORDER_RESULT_ACCEPT = 1;
	public static final int ORDER_RESULT_REFUSE = 2;
	@ViewInject(R.id.image_orderdetail_itemicon)
	private ImageView itemIconView;
	@ViewInject(R.id.txt_orderdetail_itemname)
	private TextView itemNameTxt;
	@ViewInject(R.id.txt_orderdetail_itemprice)
	private TextView itemPriceTxt;
	@ViewInject(R.id.image_orderdetail_gifticon)
	private ImageView giftIconView;
	@ViewInject(R.id.txt_orderdetail_giftprice)
	private TextView giftPriceTxt;
	@ViewInject(R.id.txt_orderdetail_ctime)
	private TextView ctimeTxt;
	@ViewInject(R.id.txt_orderdetail_otime)
	private TextView otimeTxt;
	@ViewInject(R.id.txt_orderdetail_descr)
	private TextView descrTxt;

	@ViewInject(R.id.image_orderdetail_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.txt_orderdetail_username)
	private TextView usernameView;
	@ViewInject(R.id.txt_orderdetail_age)
	private TextView ageView;
	@ViewInject(R.id.txt_orderdetail_occup)
	private TextView occupView;
	@ViewInject(R.id.txt_orderdetail_addr)
	private TextView addrView;
	@ViewInject(R.id.txt_orderdetail_userdis)
	private TextView distanceView;
	@ViewInject(R.id.btn_orderdetail_accept)
	private Button acceptView;
	@ViewInject(R.id.btn_orderdetail_refuse)
	private Button refuseView;
	@ViewInject(R.id.layout_orderdetail_user)
	private View userView;
	@ViewInject(R.id.label_orderdetail_prompt)
	private TextView promptView;
	@ViewInject(R.id.layout_orderdetail_share)
	private View shareView;
	@ViewInject(R.id.image_orderdetail_checkbox)
	private ImageView checkBox;

	@ViewInject(R.id.layout_orderdetail_cost)
	private RelativeLayout costLayout;
	@ViewInject(R.id.txt_orderdetail_cost)
	private TextView costView;
	@ViewInject(R.id.edit_orderdetail_cost)
	private EditText costEdit;

	private OrderEntity orderEntity = null;
	private User user = null;
	private IWXAPI api = null;

	private void wechatShare() {
		String title =String.format("我收到了好友的线下约单[%s %s金币／%s,%s", orderEntity.getItem().getName(), orderEntity.getItem().getPrice(), orderEntity.getItem().getUnit(), this.getString(R.string.weixin_share));
		WechatMoments.ShareParams spWeChatMoments = new WechatMoments.ShareParams();
		spWeChatMoments.setShareType(Platform.SHARE_WEBPAGE);
		spWeChatMoments.setText(title);
		spWeChatMoments.setUrl("http://bidong.intexh.com/download/");
		spWeChatMoments.setImageUrl("http://106.75.118.28:8080/img/system/logo.png");
		spWeChatMoments.setTitle( title);
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
//		msg.description = String.format("我收到了好友的线下约单[%s %s金币／%s,%s", orderEntity.getItem().getName(),
//				orderEntity.getItem().getPrice(), orderEntity.getItem().getUnit(), this.getString(R.string.weixin_share));
//		// 这里替换一张自己工程里的图片资源
//		BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
//		Bitmap thumb = bmpDraw.getBitmap();
//		msg.setThumbImage(thumb);
//
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = String.valueOf(System.currentTimeMillis());
//		req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		api.sendReq(req);
	}

	@Override
	public void onResp(BaseResp resp) {
		String ss = "";
		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				//分享成功
				ss = "成功";
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				//分享取消
				ss = "取消";
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//分享拒绝
				ss = "授权失败";
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:{
				ss = "不支持";
				break;
			}
			case BaseResp.ErrCode.ERR_COMM:{
				ss = "未知错误";
				break;
			}
			case BaseResp.ErrCode.ERR_SENT_FAILED:{
				ss = "发送失败";
				break;
			}
		}
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_orderdetail);
		ViewUtils.inject(this);
		if(null == orderEntity){
			String entityStr = getIntent().getStringExtra(ORDER_ENTITY);
			if(!StringUtil.isEmptyString(entityStr)){
				orderEntity = GsonUtils.jsonToObj(entityStr, OrderEntity.class);
			}
		}

		//初始化界面
		initView();

		//微信分享
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.registerApp(Constants.APP_ID);
		api.handleIntent(getIntent(), this);

		//注册环信透传消息监听
		EMChatManager.getInstance().registerEventListener(this,
				new EMNotifierEvent.Event[] {EMNotifierEvent.Event.EventNewCMDMessage });
	}

	private void initView(){
		ImageUtils.loadOffServiceItemImage(orderEntity.getItem().getUri(), itemIconView);
		itemNameTxt.setText(orderEntity.getItem().getName());
		String priceStr = orderEntity.getItem().getPrice();
		if(priceStr.equals("0")){
			priceStr = "免费";
		}
		else {
			priceStr = priceStr + "金币/" + orderEntity.getItem().getUnit();
		}
		itemPriceTxt.setText(priceStr);
		ctimeTxt.setText(DateUtil.getTimeDiffDesc(orderEntity.getCreated_at()));

		otimeTxt.setText(DateUtil.getMonthDay(orderEntity.getApp_time()));
		descrTxt.setText(orderEntity.getApp_descr());

		String url = orderEntity.getGift().getUrl();
		if(null != url && url.contains(".png")){
			url = url.replace(".png", "_b.png");
			ImageUtils.loadGiftImage(url, giftIconView);
		}else{
			ImageUtils.loadGiftImage(orderEntity.getGift().getUrl(), giftIconView);
		}
		giftPriceTxt.setText(orderEntity.getGift().getName() + " " + orderEntity.getGift().getPrice() + "金币");

		int payStatus = orderEntity.getPay_status();
		int status = orderEntity.getStatus();
		String amount = String.valueOf(orderEntity.getAmount());
		costLayout.setVisibility(View.GONE);
		refuseView.setVisibility(View.GONE);
		acceptView.setVisibility(View.GONE);
		if(null == orderEntity.getFrom()){//我约
			setTitleText(R.string.title_order_out); //activity title
			promptView.setText(R.string.order_send_prompt);
			user = orderEntity.getTo();
			if(0 == status){//未处理
				refuseView.setText(R.string.common_cancel);
				refuseView.setVisibility(View.VISIBLE);
				refuseView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						cancelOrder();
					}
				});
			}else if(1 == status){
				if(1 == payStatus){
					setStatusPrompt("待付款");
					costLayout.setVisibility(View.VISIBLE);
					costView.setText(amount);
					acceptView.setVisibility(View.VISIBLE);
					acceptView.setText("付款");
					acceptView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							checkBalanceAndPay();
						}
					});
				}
				else if(2 == payStatus){
					costLayout.setVisibility(View.VISIBLE);
					costView.setText(amount);
					setStatusPrompt("已付款");
				}
				else if(3 == payStatus){
					setStatusPrompt("线下已付款");
				}
				else{
					setStatusPrompt("已接单");
				}
			}else if(2 == status || 3 == status){
				refuseView.setText(R.string.common_delete);
				refuseView.setVisibility(View.VISIBLE);
				refuseView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						deleteOrder();
					}
				});
				if(2 == status){
					setStatusPrompt("被拒绝");
				}
				else {
					setStatusPrompt("已取消");
				}
			}else if(4 == status){
				setStatusPrompt("已删除");
			}
		}else{ //约我
			setTitleText(R.string.title_order_in); //activity title
			user = orderEntity.getFrom();
			if(0 == status){
				checkBox.setSelected(true);
				checkBox.setOnClickListener(this);
				shareView.setVisibility(View.VISIBLE);
				refuseView.setVisibility(View.VISIBLE);
				acceptView.setVisibility(View.VISIBLE);
				acceptView.setOnClickListener(this);
				refuseView.setOnClickListener(this);
			}else{
				if(1 == status){
					if(0 == payStatus || 1 == payStatus){
						refuseView.setVisibility(View.VISIBLE);
						acceptView.setVisibility(View.VISIBLE);
						acceptView.setText("发起收款");
						refuseView.setText("线下已收款");
						costLayout.setVisibility(View.VISIBLE);
						costEdit.setVisibility(View.VISIBLE);
						costView.setVisibility(View.GONE);
						acceptView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								askOrder();
							}
						});
						refuseView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								closeAlert();
							}
						});
						if(0 == payStatus){
							setStatusPrompt("待收款");
						}
						else{
							costEdit.setText(amount);
							setStatusPrompt("待付款");
						}
					}
					else if(2 == payStatus){
						costLayout.setVisibility(View.VISIBLE);
						costEdit.setVisibility(View.GONE);
						costView.setVisibility(View.VISIBLE);
						costView.setText(amount);
						setStatusPrompt("已收款");
					}
					else if(3 == payStatus){
						setStatusPrompt("线下已收款");
					}
				}else if(2 == status){
					setStatusPrompt("已拒绝");
				}else if(3 == status){
					setStatusPrompt("被取消");
				}else if(4 == status){
					setStatusPrompt("已删除");
				}
			}
		}

		ImageUtils.loadAvatarDefaultImage(user.getAvatar(), avatarView);
		usernameView.setText(user.getAlias());
		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == user.getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;
		}
		ageView.setBackgroundColor(getResources().getColor(genderColorId));
		ageView.setText(getString(genderFlag) + String.valueOf(user.getAge()) + " ");
		occupView.setText(user.getOccup());
		String ss = "";
		if (null != user.getCity()) {
			ss += user.getCity();
		}
		if (null != user.getDistrict()) {
			ss += user.getDistrict();
		}
		addrView.setText(ss);
		double[] upoint = StringUtil.getLocPoint(user.getLoc());
		if(null != upoint){
			distanceView.setText(StringUtil.getDistanceStr(upoint));
		}

		userView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GetUserDetailRequest request = new GetUserDetailRequest();
				request.setUserid(user.getId());
				request.setGender(user.getGender());
				request.setNetworkListener(new CommonNetworkCallback<User>() {

					@Override
					public void onSuccess(User data) {
						hideLoading();
						if (null != data) {
							data.setVideo(user.getVideo());
							Intent intent = new Intent(OrderDetailActivity.this, UserDetailActivity.class);
							intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(data));
							startActivity(intent);
						} else {
							showToast("用户信息获取失败");
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
		});
	}

	private void setStatusPrompt(String prompt){
		promptView.setText(prompt);
		promptView.setTextSize(20);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}
	
	private void cancelOrder(){
		CancelOrderRequest request = new CancelOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(ORDER_STATUS, 3);
				setResult(RESULT_OK, intent);
				finish();
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
	
	private void deleteOrder(){
		DeleteOrderRequest request = new DeleteOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(ORDER_STATUS, 4);
				setResult(RESULT_OK, intent);
				finish();
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

	private void acceptOrder(){
		AcceptOrderRequest request = new AcceptOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(ORDER_RESULT, ORDER_RESULT_ACCEPT);
				intent.putExtra(ORDER_STATUS, 1);
				setResult(RESULT_OK, intent);
				if (checkBox.isSelected()) {
					if (api.isWXAppSupportAPI() && api.isWXAppInstalled()) {
						wechatShare();
						finish();
					}else{
						finish();
					}
				} else {
					finish();
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
	
	private void refuseOrder(){
		RejectOrderRequest request = new RejectOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(ORDER_RESULT, ORDER_RESULT_REFUSE);
				intent.putExtra(ORDER_STATUS, 2);
				setResult(RESULT_OK, intent);
				finish();
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

	//检查账户信息并支付
	private void checkBalanceAndPay() {
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				if(data.getBalance() < orderEntity.getAmount()){
					new AlertDialog.Builder(OrderDetailActivity.this).setTitle("余额不足")
							.setMessage("请先充值，再支付")
							.setPositiveButton("去充值", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(OrderDetailActivity.this, ChargeActivity.class);
									startActivityForResult(intent, 1);
								}
							})
							.setNeutralButton("取消", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {}
							})
							.create()
							.show();
				}
				else{
					payAlert();
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

	private void payAlert(){
		String alertStr = "你确定支付" + orderEntity.getAmount() + "金币给" + orderEntity.getTo().getAlias() + "吗？";
				new AlertDialog.Builder(OrderDetailActivity.this).setTitle("支付确认")
				.setMessage(alertStr)
				.setPositiveButton("支付", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						payOrder();
					}
				})
				.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				})
				.create()
				.show();
	}

	//支付
	private void payOrder(){
		PayOrderRequest request = new PayOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(ORDER_PAY_STATUS, 2); //已付款
				intent.putExtra(ORDER_STATUS, 1);
				intent.putExtra(ORDER_PAY_AMOUNT, orderEntity.getAmount());
				setResult(RESULT_OK, intent);
				finish();
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
	protected void handleLeft(){
		Intent intent = new Intent();
		if(orderEntity.getPay_status() == 1 || orderEntity.getPay_status() == 2){
			intent.putExtra(ORDER_PAY_STATUS, orderEntity.getPay_status()); //待付款
			intent.putExtra(ORDER_STATUS, 1);
			intent.putExtra(ORDER_PAY_AMOUNT, orderEntity.getAmount());
		}
		setResult(RESULT_OK, intent);
		super.handleLeft();
	}

	@Override
	public void scrollToFinishActivity() {
		Intent intent = new Intent();
		if(orderEntity.getPay_status() == 1 || orderEntity.getPay_status() == 2){
			intent.putExtra(ORDER_PAY_STATUS, orderEntity.getPay_status()); //待付款
			intent.putExtra(ORDER_STATUS, 1);
			intent.putExtra(ORDER_PAY_AMOUNT, orderEntity.getAmount());
		}
		setResult(RESULT_OK, intent);
		super.scrollToFinishActivity();
	}

	//收款
	private void askOrder(){
		final String amount = costEdit.getText().toString();
		if(StringUtil.isEmptyString(amount) || amount.equals("0")){
			showToast("请输入收款金额");
			return ;
		}
		AskOrderRequest request = new AskOrderRequest();
		request.setId(orderEntity.getId());
		request.setAmount(amount);
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				acceptView.setEnabled(false);
				orderEntity.setPay_status(1);
				orderEntity.setAmount(Integer.parseInt(amount));
				setStatusPrompt("待付款");
				showToast("收款请求已发出，请等待对方付款");
//				Intent intent = new Intent();
//				intent.putExtra(ORDER_PAY_STATUS, 1); //待付款
//				intent.putExtra(ORDER_STATUS, 1);
//				setResult(RESULT_OK, intent);
//				finish();
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

	private void closeAlert(){
		String alertStr = "你确认关闭约单并已经线下收款了吗？";
		new AlertDialog.Builder(OrderDetailActivity.this).setTitle("线下收款确认")
				.setMessage(alertStr)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						closeOrder();
					}
				})
				.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				})
				.create()
				.show();
	}

	//关闭约单，线下收款
	private void closeOrder(){
		CloseOrderRequest request = new CloseOrderRequest();
		request.setId(orderEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				acceptView.setEnabled(false);
				showToast("约单已关闭，请线下收款");
				Intent intent = new Intent();
				intent.putExtra(ORDER_PAY_STATUS, 3); //已线下收款
				intent.putExtra(ORDER_STATUS, 1);
				setResult(RESULT_OK, intent);
				finish();
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
			case R.id.btn_orderdetail_accept:{
				acceptOrder();
				break;
			}
			case R.id.btn_orderdetail_refuse:{
				refuseOrder();
				break;
			}
			case R.id.image_orderdetail_checkbox:{
				checkBox.setSelected(!checkBox.isSelected());
				break;
			}
		}
	}

	//取约单详细信息
	private void getOrderDetail(){
		CommonAbstractDataManager request = new GetSendOrderDetailRequest();
		if(null == orderEntity.getTo()){ //约我
			request = new GetReceiveOrderDetailRequest();
			((GetReceiveOrderDetailRequest)request).setId(orderEntity.getId());
		}else { //我约
			((GetSendOrderDetailRequest)request).setId(orderEntity.getId());
		}
		request.setNetworkListener(new CommonNetworkCallback<OrderEntity>() {

			@Override
			public void onSuccess(OrderEntity result) {
				orderEntity = result;
				initView();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		request.getDataFromServer();
	}


	@Override
	public void onEvent(EMNotifierEvent emNotifierEvent) {
		switch (emNotifierEvent.getEvent()) {
			case EventNewCMDMessage: {
				EMMessage message = (EMMessage) emNotifierEvent.getData();
				final int type = message.getIntAttribute("type", -1);
				final String data = message.getStringAttribute("data", null);
				if (null == data || !orderEntity.getId().equals(data)) {
					return;
				}
				if (type == Constants.MessageType.MessageType_OrderAsk
						|| type == Constants.MessageType.MessageType_OrderPay) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getOrderDetail();
						}
					});
				}
				break;
			}
		}
	}
}
