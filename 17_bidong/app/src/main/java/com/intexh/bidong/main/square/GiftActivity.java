package com.intexh.bidong.main.square;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intexh.bidong.utils.DialogUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.charge.ChargeActivity;
import com.intexh.bidong.main.square.GiftFragment.OnGiftListener;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.StringUtil;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class GiftActivity extends BaseActivity {

	public static final String USER_ENTITY = "USER_ENTITY";
	public static final String CAPITAL_ENTITY = "CAPITAL_ENTITY";
	public static final String GIFT_ENTITY = "GIFT_ENTITY";
	public static final String GIFT_TIPS = "GIFT_TIPS";
	public static final String SHOW_MODE = "SHOW_MODE";
	
	@ViewInject(R.id.pager_gift_pager)
	private ViewPager pager;
	@ViewInject(R.id.layout_gift_indicator)
	private LinearLayout indicatorContainer;
	@ViewInject(R.id.txt_gift_value)
	private TextView valueTxt;
	@ViewInject(R.id.image_gift_close)
	private ImageView closeView;
	@ViewInject(R.id.btn_gift_charge)
	private Button chargeBtn;
	@ViewInject(R.id.txt_gift_tips)
	private TextView giftTipsTxt;
	private List<ImageView> indicators = new ArrayList<ImageView>();
	private List<GiftItemEntity> giftLists = new ArrayList<GiftItemEntity>();
	private User userEntity = null;
	private Capital accountInfo = null;
	private int showMode = GiftGridAdapter.SHOWMODE_NAME;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_gift);
		ViewUtils.inject(this);
		int tipsResource = getIntent().getIntExtra(GIFT_TIPS, -1);
		showMode = getIntent().getIntExtra(SHOW_MODE, GiftGridAdapter.SHOWMODE_NAME);
		if(tipsResource > 0){
			giftTipsTxt.setText(tipsResource);
		}
		pager.addOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				updateIndicator(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		String ss = getIntent().getStringExtra(USER_ENTITY);
		if(!StringUtil.isEmptyString(ss)){
			userEntity = GsonUtils.jsonToObj(ss, User.class);
		}
		ss = getIntent().getStringExtra(CAPITAL_ENTITY);
		if(!StringUtil.isEmptyString(ss)){
			accountInfo = GsonUtils.jsonToObj(ss, Capital.class);
		}
		closeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		chargeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GiftActivity.this,ChargeActivity.class);
				intent.putExtra(ChargeActivity.RICH_INFO, GsonUtils.objToJson(accountInfo));
				startActivity(intent);
			}
		});
		valueTxt.setText(String.valueOf(accountInfo.getBalance()));
		getGifts();
	}
	
	private void initIndicators(){
		indicatorContainer.removeAllViews();
		int count = (giftLists.size()+7)/8;
		for(int i=0;i<count;i++){
			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, PtrLocalDisplay.dp2px(4), 0);
			imageView.setPadding(0, 0, PtrLocalDisplay.dp2px(4), 0);
			imageView.setImageResource(R.drawable.selector_giftindicator);
			indicatorContainer.addView(imageView, params);
			indicators.add(imageView);
		}
	}
	
	private void updateIndicator(int index){
		for(int i=0;i<indicators.size();i++){
			ImageView imageView = indicators.get(i);
			imageView.setSelected(index == i);
		}
	}
	
	private void getGifts(){
		GetGiftsRequest request = new GetGiftsRequest();
		request.setNetworkListener(new CommonNetworkCallback<GiftItemEntity[]>() {

			@Override
			public void onSuccess(GiftItemEntity[] data) {
				hideLoading();
				giftLists.clear();
				if(null != data){
					for(GiftItemEntity entity : data){
						giftLists.add(entity);
					}
				}
				GiftPageAdapter adapter = new GiftPageAdapter(getSupportFragmentManager());
				pager.setAdapter(adapter);
				pager.setCurrentItem(0);
				initIndicators();
				updateIndicator(0);
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
	
	private OnGiftListener mListener = new OnGiftListener() {
		
		@Override
		public void onClickGift(final GiftItemEntity gift) {
			if(accountInfo.getBalance() < gift.getPrice()){
				showToast("余额不足，请充值");
				return ;
			}
			if(gift.getPrice()>=50){
				DialogUtils.showDialog(GiftActivity.this,"提醒","您当前赠送的礼品已过50金币以上，请确认对方好友关系，谨防受骗，一旦送出，平台不予以处理！",
						"确定","取消",new DialogUtils.DialogImpl(){
					@Override
					public void onOk() {
						Intent intent = new Intent();
						intent.putExtra(GiftActivity.GIFT_ENTITY, GsonUtils.objToJson(gift));
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(GiftActivity.GIFT_ENTITY, GsonUtils.objToJson(gift));
			setResult(RESULT_OK, intent);
			finish();

		}
	};
	
	private GiftFragment getGiftFragment(int index){
		GiftFragment fragment = new GiftFragment();
		ArrayList<GiftItemEntity> datas = new ArrayList<GiftItemEntity>();
		for(int i=index*8;i<8+index*8;i++){
			if(i >= giftLists.size()){
				break;
			}
			datas.add(giftLists.get(i));
		}
		fragment.setShowMode(showMode);
		fragment.setDatas(datas);
		fragment.setOnGiftListener(mListener);
		return fragment;
	}
	
	private class GiftPageAdapter extends FragmentPagerAdapter{
		
		public GiftPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return getGiftFragment(arg0);
		}

		@Override
		public int getCount() {
			return (giftLists.size()+7)/8;
		}
		
	}
	
	
}
