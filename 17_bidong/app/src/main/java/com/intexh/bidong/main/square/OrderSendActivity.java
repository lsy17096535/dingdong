package com.intexh.bidong.main.square;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.DatePickerDialog;
import com.intexh.bidong.widgets.DatePickerDialog.OnDatePickerListener;

public class OrderSendActivity extends BaseTitleActivity implements OnClickListener{
	private static final int REQUEST_GIFT = 1000;
	public static final String SERVICE_ENTITY = "SERVICE_ENTITY";

	@ViewInject(R.id.image_ordersend_itemicon)
	private ImageView itemIconView;
	@ViewInject(R.id.txt_ordersend_itemname)
	private TextView itemNameTxt;
	@ViewInject(R.id.txt_ordersend_itemprice)
	private TextView itemPriceTxt;
	@ViewInject(R.id.txt_ordersend_weeks)
	private TextView weeksTxt;
	@ViewInject(R.id.txt_ordersend_cdescr)
	private TextView cdescrTxt;

	@ViewInject(R.id.image_ordersend_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.txt_ordersend_username)
	private TextView usernameView;
	@ViewInject(R.id.txt_ordersend_age)
	private TextView ageView;
	@ViewInject(R.id.txt_ordersend_occup)
	private TextView occupView;
	@ViewInject(R.id.txt_ordersend_addr)
	private TextView addrView;
	@ViewInject(R.id.txt_ordersend_userdis)
	private TextView distanceView;

	@ViewInject(R.id.layout_ordersend_user)
	private View userLayout;
	@ViewInject(R.id.layout_ordersend_atime)
	private View atimeLayout;
	@ViewInject(R.id.layout_ordersend_gift)
	private View giftLayout;

	@ViewInject(R.id.image_ordersend_gifticon)
	private ImageView giftIconView;
	@ViewInject(R.id.txt_ordersend_giftprice)
	private TextView giftPriceTxt;
	@ViewInject(R.id.txt_ordersend_atime)
	private TextView atimeTxt;
	@ViewInject(R.id.edit_ordersend_adescr)
	private EditText adescrTxt;

	@ViewInject(R.id.btn_ordersend_save)
	private Button saveButton;

	private OffServiceEntity serviceEntity = null;
	private User user = null;
	private GiftItemEntity giftItemEntity = null;

	private String toId = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_ordersend);
		ViewUtils.inject(this);
		setTitleText(getString(R.string.title_off_service)); //activity title

		String ss = getIntent().getStringExtra(SERVICE_ENTITY);
		if(!StringUtil.isEmptyString(ss)){
			serviceEntity = GsonUtils.jsonToObj(ss, OffServiceEntity.class);
		}
		ImageUtils.loadOffServiceItemImage(serviceEntity.getItem().getUri(), itemIconView);
		itemNameTxt.setText(serviceEntity.getItem().getName());
		String priceStr = serviceEntity.getPrice();
		if(priceStr.equals("0")){
			priceStr = "免费";
		}
		else {
			priceStr = priceStr + "金币/" + serviceEntity.getUnit();
		}
		itemPriceTxt.setText(priceStr);
		weeksTxt.setText(StringUtil.join("、", serviceEntity.getIdel_times()));
		cdescrTxt.setText(serviceEntity.getDescr());

		atimeLayout.setOnClickListener(this);
		giftLayout.setOnClickListener(this);
		saveButton.setOnClickListener(this);

		user = serviceEntity.getUser();
		if(null == user){
			userLayout.setVisibility(View.GONE);
			toId = serviceEntity.getUser_id();
			return;
		}else{
			initUserLayout(user);
			toId = serviceEntity.getUser().getId();
		}
	}

	private void initUserLayout(User user){
		ImageUtils.loadAvatarDefaultImage(user.getAvatar(), avatarView);
		usernameView.setText(user.getAlias());
		int genderColorId = R.color.text_bg_color_red; //女色
		int genderFlag = R.string.femal_flag;
		if(2 == user.getGender()){  //男
			genderColorId = R.color.text_bg_color_blue;
			genderFlag = R.string.male_flag;

			saveButton.setText(getString(R.string.common_order_male));
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
			ss = StringUtil.getDistanceStr(upoint);
		}
		//特殊处理
		if(user.is_top() && !user.getId().equals(UserUtils.getUserInfo().getUser().getId())){
			ss = "同城";
		}
		distanceView.setText(ss);

		userLayout.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent arg2) {
		super.onActivityResult(request, result, arg2);
		if (result == RESULT_OK) {
			switch (request) {
				case REQUEST_GIFT: {
					String ss = arg2.getStringExtra(GiftActivity.GIFT_ENTITY);
					giftItemEntity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
					giftIconView.setVisibility(View.VISIBLE);
					ImageUtils.loadGiftImage(giftItemEntity.getUri(), giftIconView);
					giftPriceTxt.setText(giftItemEntity.getName() + " " + giftItemEntity.getPrice() + "金币");
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
			case R.id.layout_ordersend_user:{
				openUserDetail();
				break;
			}
			case R.id.layout_ordersend_atime:{
				DatePickerDialog dlg = new DatePickerDialog(this, DateUtil.getCurrYYMMDD());
				dlg.setCompare(1);//返回结果大于等于当前时间
				dlg.setListener(new OnDatePickerListener() {
					@Override
					public void onDatePickerConfirm(String date) {
						atimeTxt.setText(date);
					}
				});
				dlg.show();
				break;
			}
			case R.id.layout_ordersend_gift:{
				openGiftPanel();
				break;
			}
			case R.id.btn_ordersend_save:{
				commit();
				break;
			}
		}
	}


	private void openUserDetail(){
		GetUserDetailRequest request = new GetUserDetailRequest();
		request.setUserid(user.getId());
		request.setGender(user.getGender());
		request.setNetworkListener(new CommonNetworkCallback<User>() {

			@Override
			public void onSuccess(User data) {
				hideLoading();
				if (null != data) {
					data.setVideo(user.getVideo());
					Intent intent = new Intent(OrderSendActivity.this, UserDetailActivity.class);
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

	private void openGiftPanel() {
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				Intent intent = new Intent(OrderSendActivity.this, GiftActivity.class);
				intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
				intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
				intent.putExtra(GiftActivity.GIFT_TIPS, R.string.title_gift_select);
				startActivityForResult(intent, REQUEST_GIFT);
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
	
	private void commit(){
		CommitOrderSendRequest request = new CommitOrderSendRequest();
		String str = atimeTxt.getText().toString();
		if(StringUtil.isEmptyString(str) || str.equals("请选择")){
			showToast("请输入约定时间");
			return ;
		}
		request.setApp_time(str);

		if(null == giftItemEntity){
			showToast("请选择诚意礼物");
			return ;
		}
		if(giftItemEntity.getPrice() < 10){
			showToast("请选择大于10金币的礼物，以表示您的诚意");
			return ;
		}
		request.setGift_id(giftItemEntity.getId());
		request.setGift_price(String.valueOf(giftItemEntity.getPrice()));

		str = adescrTxt.getText().toString();
		if(StringUtil.isEmptyString(str)){
			showToast("请输入约会说明");
			return ;
		}
		request.setApp_descr(str);

		request.setItem_id(serviceEntity.getItem().getId());
		request.setItem_price(serviceEntity.getPrice());
		request.setItem_unit(serviceEntity.getUnit());

		request.setFrom_id(UserUtils.getUserid());
		request.setTo_id(toId);

		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("发送成功，请等待对方接单");
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
}
