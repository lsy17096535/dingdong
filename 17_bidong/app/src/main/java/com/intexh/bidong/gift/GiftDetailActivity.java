package com.intexh.bidong.gift;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
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
import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.me.MeFragment;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;

public class GiftDetailActivity extends BaseTitleActivity implements OnClickListener,IWXAPIEventHandler{
	
	public static final String GIFT_ENTITY = "GIFT_ENTITY";
	public static final String GIFT_RESULT = "GIFT_RESULT";
	public static final String GIFT_STATUS = "GIFT_STATUS";
	public static final int GIFT_RESULT_ACCEPT = 1;
	public static final int GIFT_RESULT_REFUSE = 2;
	@ViewInject(R.id.image_giftdetail_icon)
	private ImageView iconView;
	@ViewInject(R.id.txt_giftdetail_giftname)
	private TextView giftNameTxt;
	@ViewInject(R.id.txt_giftdetail_time)
	private TextView timeTxt;
	@ViewInject(R.id.image_giftdetail_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.txt_giftdetail_userinfo)
	private TextView nameView;
	@ViewInject(R.id.txt_giftdetail_age)
	private TextView ageView;
	@ViewInject(R.id.txt_giftdetail_sign)
	private TextView signView;
	@ViewInject(R.id.btn_giftdetail_accept)
	private Button acceptView;
	@ViewInject(R.id.btn_giftdetail_refuse)
	private Button refuseView;
	@ViewInject(R.id.layout_giftdetail_user)
	private View userView;
	@ViewInject(R.id.txt_giftdetail_height)
	private TextView heightView;
	@ViewInject(R.id.txt_giftdetail_weight)
	private TextView weightView;
	@ViewInject(R.id.txt_giftdetail_addressinfo)
	private TextView addressInfoView;
	@ViewInject(R.id.txt_giftdetail_career)
	private TextView careerView;
	@ViewInject(R.id.image_giftdetail_vip)
	private ImageView vipView;
	@ViewInject(R.id.label_giftdetail_prompt)
	private TextView promptView;
	@ViewInject(R.id.layout_giftdetail_share)
	private View shareView;
	@ViewInject(R.id.image_giftdetail_checkbox)
	private ImageView checkBox;
	private SendGiftItemEntity giftEntity = null;
	private User user = null;
	private IWXAPI api = null;

	private void wechatShare() {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.ppstar.cn";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = getString(R.string.app_name) + getString(R.string.app_title);
		msg.description = String.format("我收到了好友的礼物%s,%s", giftEntity.getGift().getName(),
				this.getString(R.string.weixin_share));
		// 这里替换一张自己工程里的图片资源
		BitmapDrawable bmpDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
		Bitmap thumb = bmpDraw.getBitmap();
		msg.setThumbImage(thumb);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
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
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_giftdetail);
		ViewUtils.inject(this);
		String ss = getIntent().getStringExtra(GIFT_ENTITY);
		if(!StringUtil.isEmptyString(ss)){
			giftEntity = GsonUtils.jsonToObj(ss, SendGiftItemEntity.class);
		}
		String url = giftEntity.getGift().getUrl();
		if(null != url && url.contains(".png")){
			url = url.replace(".png", "_b.png");
			ImageUtils.loadGiftImage(url, iconView);
		}else{
			ImageUtils.loadGiftImage(giftEntity.getGift().getUrl(), iconView);
		}
		giftNameTxt.setText(giftEntity.getGift().getName() + " " + giftEntity.getValue());
		timeTxt.setText(DateUtil.timestampToMDHMDate(DateUtil.getTimestamp(giftEntity.getCreated_at())));
		int status = giftEntity.getStatus();
		acceptView.setVisibility(View.GONE);
		refuseView.setVisibility(View.GONE);
		if(null == giftEntity.getFrom()){//送礼物
			promptView.setText(R.string.gift_send_prompt);
			user = giftEntity.getTo();
			if(0 == status){//未处理
				refuseView.setText(R.string.common_cancel);
				refuseView.setVisibility(View.VISIBLE);
				refuseView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						cancelGift();
					}
				});
			}else if(1 == status){
				setStatusPrompt("已接受");
			}else if(2 == status || 3 == status){
				refuseView.setText(R.string.common_delete);
				refuseView.setVisibility(View.VISIBLE);
				refuseView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						deleteGift();
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
		}else{ //收礼物
			user = giftEntity.getFrom();
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
					setStatusPrompt("已接受");
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
		nameView.setText(user.getAlias());
		String username = user.getAlias();
		ss = "";
		if (null == username) {
			username = "";
		}
		ss = " ";
		if (null != user.getHeight()) {
			ss += user.getHeight();
		}
		ss += "CM ";
		heightView.setText(ss);
		ss = " ";
		if (null != user.getWeight()) {
			ss += user.getWeight();
		}
		ss += "KG ";
		weightView.setText(ss);
		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == user.getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;
		}
		ageView.setBackgroundColor(getResources().getColor(genderColorId));
		ageView.setText(getString(genderFlag) + String.valueOf(user.getAge()) + " ");

		signView.setText(user.getSignature());
		ss = "";
		if (null != user.getCity()) {
			ss += user.getCity();
		}
		if (null != user.getDistrict()) {
			ss += user.getDistrict();
		}
		addressInfoView.setText(ss);
		ss = " ";
		if (null != user.getOccup()) {
			ss += user.getOccup() + " ";
			careerView.setText(ss);
		}else{
			careerView.setText("");
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
							Intent intent = new Intent(GiftDetailActivity.this, UserDetailActivity.class);
							intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(data));
							startActivity(intent);
						} else {
							showToast("用户信息获取失败");
						}
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
		setTitleText(R.string.title_giftdetail);
		getAccount();
		api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
		api.registerApp(Constants.APP_ID);
		api.handleIntent(getIntent(), this);
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
	
	private void getAccount() {
		GetUserDetailRequest request = new GetUserDetailRequest();
		request.setUserid(user.getId());
		request.setGender(user.getGender());
		request.setNetworkListener(new CommonNetworkCallback<User>() {

			@Override
			public void onSuccess(User data) {
				hideLoading();
				vipView.setImageResource(MeFragment.getLevelResId(data.getLevel(),data.getGender()));
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
	
	private void cancelGift(){
		CancelGiftRequest request = new CancelGiftRequest();
		request.setId(giftEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(GIFT_STATUS, 3);
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
	
	private void deleteGift(){
		DeleteGiftRequest request = new DeleteGiftRequest();
		request.setId(giftEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(GIFT_STATUS, 4);
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

	private void acceptGift(){
		AcceptGiftRequest request = new AcceptGiftRequest();
		request.setId(giftEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(GIFT_RESULT, GIFT_RESULT_ACCEPT);
				intent.putExtra(GIFT_STATUS, 1);
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
			public void onFailed(int code, HttpException error,
					String reason) {
				hideLoading();
				showToast(reason);
			}
		});
		showLoading();
		request.getDataFromServer();
	}
	
	private void refuseGift(){
		RejectGiftRequest request = new RejectGiftRequest();
		request.setId(giftEntity.getId());
		request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

			@Override
			public void onSuccess(SendGiftItemEntity data) {
				hideLoading();
				Intent intent = new Intent();
				intent.putExtra(GIFT_RESULT, GIFT_RESULT_REFUSE);
				intent.putExtra(GIFT_STATUS, 2);
				setResult(RESULT_OK, intent);
				finish();
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
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_giftdetail_accept:{
			acceptGift();
			break;
		}
		case R.id.btn_giftdetail_refuse:{
			refuseGift();
			break;
		}
			case R.id.image_giftdetail_checkbox:{
				checkBox.setSelected(!checkBox.isSelected());
				break;
			}
		}
	}
	
	
}
