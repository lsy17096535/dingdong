package com.intexh.bidong.gift;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.easeui.utils.Settings;
import com.google.gson.reflect.TypeToken;
import com.intexh.bidong.utils.DateUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

public class GiftConfirmActivity extends BaseActivity implements OnClickListener{

	public static final String GIFT_ENTITY = "GIFT_ENTITY";
	public static final String GIFT_FROM = "GIFT_FROM";
	public static final String USER_ID = "USER_ID";
	public static final String SHOULDSHOW_TIPS = "SHOULDSHOW_TIPS";
	
	@ViewInject(R.id.btn_giftconfirm_confirm)
	private Button confirmView;
	@ViewInject(R.id.btn_giftconfirm_cancel)
	private Button cancelView;
	@ViewInject(R.id.image_giftconfirm_icon)
	private ImageView iconView;
	@ViewInject(R.id.txt_giftconfirm_value)
	private TextView valueTxt;
	@ViewInject(R.id.txt_giftconfirm_tips)
	private TextView tipsTxt;
	private GiftItemEntity giftEntity = null;
	private String userid = null;
	private int giftFrom = 1;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_giftconfirm);
		ViewUtils.inject(this);
		confirmView.setOnClickListener(this);
		cancelView.setOnClickListener(this);
		String ss = getIntent().getStringExtra(GIFT_ENTITY);
		if(!StringUtil.isEmptyString(ss)){
			giftEntity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
		}
		giftFrom = getIntent().getIntExtra(GIFT_FROM, 1);
		userid = getIntent().getStringExtra(USER_ID);
		String url = giftEntity.getUri();
		if(null != url && url.contains(".png")){
			url = url.replace(".png", "_b.png");
		}
		boolean shouldShowTips = getIntent().getBooleanExtra(SHOULDSHOW_TIPS, false);
		if(shouldShowTips){
			tipsTxt.setVisibility(View.VISIBLE);
		}
		ImageUtils.loadGiftImage(url, iconView);
		valueTxt.setText(giftEntity.getName() + " " + String.valueOf(giftEntity.getPrice()));
	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.btn_giftconfirm_confirm:{
			SendGiftRequest request = new SendGiftRequest();
			request.setFrom_id(UserUtils.getUserid());
			request.setTo_id(userid);
			request.setOrigin(giftFrom);
			request.setGift_id(giftEntity.getId());
			request.setValue(giftEntity.getPrice());
			request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

				@Override
				public void onSuccess(SendGiftItemEntity data) {
					hideLoading();
					showToast(R.string.gift_send_success);
					setResult(RESULT_OK,getIntent());

					//记录礼物赠送好友的日期 用于判断每天与好友聊天需要赠送一次礼物
					GiftCheckUtils.saveChatid(UserUtils.getHXUserid(userid));
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
			break;
		}
		case R.id.btn_giftconfirm_cancel:{
			finish();
			break;
		}
		}
	}

	
	
}
