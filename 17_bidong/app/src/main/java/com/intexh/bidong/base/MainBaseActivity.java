package com.intexh.bidong.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;

import com.intexh.bidong.R;
import com.intexh.bidong.location.LocationHelper;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.intexh.bidong.location.LocationHelper.OnLocationListener;

public abstract class MainBaseActivity extends BaseActivity {

	protected static final int STATE_SQUARE = 0;
	protected static final int STATE_GIFT = 1;
	protected static final int STATE_ORDER = 2;
	protected static final int STATE_MESSAGE = 3;
	protected static final int STATE_ME = 4;

	protected LocationInfo locationInfo = null;
	protected abstract void didUpdateLocation(LocationInfo info);
	
	private OnLocationListener mLocationListener = new OnLocationListener() {
		
		@Override
		public void onLocationChange(LocationInfo locationInfo) {
			MainBaseActivity.this.locationInfo = locationInfo;
			didUpdateLocation(locationInfo);
		}
	};
	
	private View leftView = null;
	private ImageView rightView = null;

	private View switcherOne = null;
	private View switcherTwo = null;
	private View switcherThree = null;

	private TextView switcherTxtOne = null;
	private TextView switcherTxtTwo = null;
	private TextView switcherTxtThree = null;
	private View switcherDividerOne = null;
	private View switcherDividerTwo = null;
	private View switcherDividerThree = null;
	private View switchView = null;
	private TextView titleView;
	private View allTitleView;
	protected int status = STATE_SQUARE;

	protected abstract void handleLeft();

	protected abstract void handleRight();

	protected abstract void handleSwitch(int index);

	protected void setState(int status, int switchIndex) {
		switch (status) {
			case STATE_SQUARE: {
				allTitleView.setVisibility(View.VISIBLE);
				titleView.setVisibility(View.GONE);
				leftView.setVisibility(View.GONE);
				rightView.setVisibility(View.GONE);
				switchView.setVisibility(View.VISIBLE);
				switcherThree.setVisibility(View.VISIBLE);
				if (0 == switchIndex) {
					switcherDividerOne.setSelected(true);
					switcherDividerTwo.setSelected(false);
					switcherDividerThree.setSelected(false);
					switcherTxtOne.setSelected(true);
					switcherTxtTwo.setSelected(false);
					switcherTxtThree.setSelected(false);
				}
				else if (1 == switchIndex) {
					switcherDividerOne.setSelected(false);
					switcherDividerTwo.setSelected(true);
					switcherDividerThree.setSelected(false);
					switcherTxtOne.setSelected(false);
					switcherTxtTwo.setSelected(true);
					switcherTxtThree.setSelected(false);

					rightView.setVisibility(View.VISIBLE);
					rightView.setImageResource(R.drawable.icon_ban_screen);
				}
				else {
					switcherDividerOne.setSelected(false);
					switcherDividerTwo.setSelected(false);
					switcherDividerThree.setSelected(true);
					switcherTxtOne.setSelected(false);
					switcherTxtTwo.setSelected(false);
					switcherTxtThree.setSelected(true);
				}
				switcherTxtOne.setText(R.string.switcher_nearby);
				switcherTxtTwo.setText(R.string.switcher_service);
				switcherTxtThree.setText(R.string.switcher_trend);
				break;
			}
			case STATE_GIFT: {
				allTitleView.setVisibility(View.VISIBLE);
				titleView.setVisibility(View.GONE);
				leftView.setVisibility(View.GONE);
				rightView.setVisibility(View.GONE);
				rightView.setImageResource(R.drawable.icon_ban_see);
				switcherThree.setVisibility(View.GONE);
				switchView.setVisibility(View.VISIBLE);
				if (0 == switchIndex) {
					switcherDividerOne.setSelected(true);
					switcherDividerTwo.setSelected(false);
					switcherTxtOne.setSelected(true);
					switcherTxtTwo.setSelected(false);
				} else {
					switcherDividerOne.setSelected(false);
					switcherDividerTwo.setSelected(true);
					switcherTxtOne.setSelected(false);
					switcherTxtTwo.setSelected(true);
				}
				switcherTxtOne.setText(R.string.switcher_come);
				switcherTxtTwo.setText(R.string.switcher_go);
				break;
			}
			case STATE_ORDER: {
				allTitleView.setVisibility(View.VISIBLE);
				titleView.setVisibility(View.GONE);
				leftView.setVisibility(View.GONE);
				rightView.setVisibility(View.VISIBLE);
				rightView.setImageResource(R.drawable.icon_ban_hotadd);
				switcherThree.setVisibility(View.GONE);
				switchView.setVisibility(View.VISIBLE);
				if (0 == switchIndex) {
					switcherDividerOne.setSelected(true);
					switcherDividerTwo.setSelected(false);
					switcherTxtOne.setSelected(true);
					switcherTxtTwo.setSelected(false);
				} else {
					switcherDividerOne.setSelected(false);
					switcherDividerTwo.setSelected(true);
					switcherTxtOne.setSelected(false);
					switcherTxtTwo.setSelected(true);
				}
				switcherTxtOne.setText(R.string.title_order_out);
				switcherTxtTwo.setText(R.string.title_order_in);
				break;
			}
			case STATE_MESSAGE: {
				allTitleView.setVisibility(View.VISIBLE);
				titleView.setVisibility(View.VISIBLE);
				switchView.setVisibility(View.INVISIBLE);
				rightView.setVisibility(View.VISIBLE);
				rightView.setImageResource(R.drawable.icon_ban_friend);
				break;
			}
			case STATE_ME: {
				allTitleView.setVisibility(View.GONE);
				break;
			}
		}
	}

	@Override
	public void setContentView(int layoutResID) {
		RelativeLayout parentView = new RelativeLayout(this);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View titleView = mInflater.inflate(R.layout.title_main, null);
		allTitleView = titleView;
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		titleParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		parentView.addView(titleView, titleParams);
		View contentView = mInflater.inflate(layoutResID, null);
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentParams.addRule(RelativeLayout.BELOW, titleView.getId());
		parentView.addView(contentView, contentParams);
		setContentView(parentView);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		leftView = findViewById(R.id.image_main_left);
		rightView = (ImageView)findViewById(R.id.image_main_right);
		switchView = findViewById(R.id.layout_main_switcher);
		switcherOne = findViewById(R.id.layout_main_switcherone);
		switcherTwo = findViewById(R.id.layout_main_switchertwo);
		switcherThree = findViewById(R.id.layout_main_switcherthree);
		switcherTxtOne = (TextView) findViewById(R.id.txt_main_one);
		switcherTxtTwo = (TextView) findViewById(R.id.txt_main_two);
		switcherTxtThree = (TextView) findViewById(R.id.txt_main_three);
		switcherDividerOne = findViewById(R.id.layout_main_one);
		switcherDividerTwo = findViewById(R.id.layout_main_two);
		switcherDividerThree = findViewById(R.id.layout_main_three);
		titleView = (TextView) findViewById(R.id.txt_main_title);
		leftView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleLeft();
			}
		});
		rightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleRight();
			}
		});
		switcherOne.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleSwitch(0);
			}
		});
		switcherTwo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleSwitch(1);
			}
		});
		switcherThree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handleSwitch(2);
			}
		});
	}

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		LocationHelper.getInstance(this).startLocation(mLocationListener);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocationHelper.getInstance(this).destroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocationHelper.getInstance(this).stopLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocationHelper.getInstance(this).startLocation(mLocationListener);
	}

}
