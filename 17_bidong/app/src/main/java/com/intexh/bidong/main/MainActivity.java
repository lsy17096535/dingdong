package com.intexh.bidong.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.easeui.utils.Settings;
import com.intexh.bidong.gift.GiftCheckUtils;
import com.intexh.bidong.utils.DialogUtils;
import com.jauker.widget.BadgeView;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.viewpager.SViewPager;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.constants.Constants;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.chat.ConversationListFragment;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.gift.MainGiftFragment;
import com.intexh.bidong.location.LocationHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.location.UpdateLocationRequest;
import com.intexh.bidong.login.LoginActivity;
import com.intexh.bidong.main.square.GetSheildContactsRequest;
import com.intexh.bidong.main.square.MainSquareFragment;
import com.intexh.bidong.main.square.MainSquareNearFragment;
import com.intexh.bidong.main.square.MainSquareOffServiceFragment;
import com.intexh.bidong.main.square.MainSquareTrendFragment;
import com.intexh.bidong.main.square.SheildContactsRequest;
import com.intexh.bidong.me.MeFragment;
import com.intexh.bidong.me.VersionCheckRequest;
import com.intexh.bidong.order.MainOrderFragment;
import com.intexh.bidong.userentity.VersionUpdateEntity;
import com.intexh.bidong.utils.ConstactUtils;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.utils.VersionManager;

public class MainActivity extends BaseActivity implements EMEventListener {
	private ProgressDialog mProgressDlg = null;
	private IndicatorViewPager indicatorViewPager;
	private SViewPager viewPager;
	private Indicator indicator;
	private MyAdapter myAdapter;
	private BadgeView meBadge;
	private BadgeView messageBadge;

	protected LocationHelper.LocationInfo locationInfo = null;
	private boolean isFirst = true;
	private int meBadgeCount;

	private LocationHelper.OnLocationListener mLocationListener = new LocationHelper.OnLocationListener() {
		@Override
		public void onLocationChange(LocationHelper.LocationInfo locationInfo) {
			MainActivity.this.locationInfo = locationInfo;
			didUpdateLocation(locationInfo);
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_tabmain);
		setSwipeBackEnable(false); //主界面禁止右滑退出
		viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
		indicator = (Indicator) findViewById(R.id.tabmain_indicator);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		myAdapter = new MyAdapter(getSupportFragmentManager());
		indicatorViewPager.setAdapter(myAdapter);
		viewPager.setCanScroll(false); // 禁止viewpager的滑动事件
		viewPager.setOffscreenPageLimit(5); // 设置viewpager保留界面不重新加载的页面数量
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageSelected(int position) {
				if(position==1){
					if(!Settings.getBoolean("isFirstOrder",false)){
						DialogUtils.showDialog(MainActivity.this,"","约单功能可向平台全部异性人员发起线下活动邀约，" +
								"可设定对方约会时间、地点等，赠送礼品以示诚意，发起特定约单活动","知道了","",new DialogUtils.DialogImpl());
					}
					Settings.setBoolean("isFirstOrder",true);
				}
				if(position == 4 && null != meBadge){
					meBadgeCount = 0;
					meBadge.setVisibility(View.GONE);
				}
				if(position == 3 && null != messageBadge){
					messageBadge.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});

		LocationHelper.getInstance(this).startLocation(mLocationListener);
		EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[] {
			EMNotifierEvent.Event.EventNewMessage,
			EMNotifierEvent.Event.EventOfflineMessage,
			EMNotifierEvent.Event.EventConversationListChanged,
			EMNotifierEvent.Event.EventDeliveryAck,
			EMNotifierEvent.Event.EventReadAck,
			EMNotifierEvent.Event.EventNewCMDMessage });

		//提示用户屏蔽通讯录联系人
		checkSheildContacts();
//		showWindow();
	}

	//显示悬浮窗
	private void showWindow() {
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setBackgroundColor(Color.BLACK);
		textView.setText("随行科技");
		textView.setTextSize(10);
		textView.setTextColor(Color.RED);
		//类型是TYPE_TOAST，像一个普通的Android Toast一样。这样就不需要申请悬浮窗权限了。
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_TOAST);
		//初始化后不首先获得窗口焦点。不妨碍设备上其他部件的点击、触摸事件。
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = 500;
		params.height = 500;
		params.gravity=Gravity.CENTER;
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplication(), "不需要权限的悬浮窗实现", Toast.LENGTH_LONG).show();
			}
		});
		WindowManager windowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
		windowManager.addView(textView, params);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EMChatManager.getInstance().unregisterEventListener(this);
	}

	protected void didUpdateLocation(LocationHelper.LocationInfo info) {
		boolean isNeedUpload = LocationLocalManager.getInstance().saveLocation(info);
		if (isNeedUpload || isFirst) {
			isFirst = false;
			UpdateLocationRequest request = new UpdateLocationRequest();
			request.setCity(info.city);
			request.setProvince(info.province);
			request.setDistrict(info.district);
			request.setLocation(info.location);
			request.setUserid(UserUtils.getUserid());
			request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<String>() {
				@Override
				public void onSuccess(String data) {}

				@Override
				public void onFailed(int code, HttpException error, String reason) {}
			});
			request.getDataFromServer();
		}
		if (null != MainSquareNearFragment.instance) {
			MainSquareNearFragment.instance.onLocationChange(info);
		}
		if (null != MainSquareOffServiceFragment.instance) {
			MainSquareOffServiceFragment.instance.onLocationChange(info);
		}
		if (null != MainSquareTrendFragment.instance) {
			MainSquareTrendFragment.instance.onLocationChange(info);
		}
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
			case EventNewCMDMessage: {
				EMMessage message = (EMMessage) event.getData();
				if (null == ChatActivity.activityInstance && (!DemoHelper.getInstance().isVideoCalling && !DemoHelper.getInstance().isVoiceCalling)) {
					TransMsgHelper.handleTransMessage(this, message);
				}
				break;
			}
			case EventNewMessage: {// 普通消息
				EMMessage message = (EMMessage) event.getData();
				if(!message.getFrom().equals("ppstar")){
					GiftCheckUtils.saveChatid(message.getFrom());
				}
				// 提示新消息
				DemoHelper.getInstance().getNotifier().onNewMsg(message);
				refreshUIWithMessage();
				break;
			}
			case EventOfflineMessage: {
				refreshUIWithMessage();
				break;
			}
			case EventConversationListChanged: {
				refreshUIWithMessage();
				break;
			}
			default: {
				break;
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		myAdapter.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {
//		private String[] tabNames = { "广场", "礼尚", "约单", "消息", "我的" };
		private String[] tabNames = { "广场", "约单", "消息", "我的" };
//		private int[] tabIcons = { R.drawable.maintab_1_selector, R.drawable.maintab_2_selector, R.drawable.maintab_3_selector,
//				R.drawable.maintab_4_selector, R.drawable.maintab_5_selector };
		private int[] tabIcons = { R.drawable.maintab_1_selector, R.drawable.maintab_3_selector,
				R.drawable.maintab_4_selector, R.drawable.maintab_5_selector };
		private LayoutInflater inflater;

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			inflater = LayoutInflater.from(getApplicationContext());
		}

		@Override
		public int getCount() {
			return tabNames.length;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = (TextView) inflater.inflate(R.layout.tab_main_text, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(tabNames[position]);
			textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
			return textView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			Fragment fragment = null;
//			switch(position){
//				case 0:
//					fragment = new MainSquareFragment();
//					break;
//				case 1:
//					fragment = new MainGiftFragment();
//					break;
//				case 2:
//					fragment = new MainOrderFragment();
//					break;
//				case 3:
//					fragment = new ConversationListFragment();
//					break;
//				case 4:
//					fragment = new MeFragment();
//					break;
//			}
			switch(position){
				case 0:
					fragment = new MainSquareFragment();
					break;
				case 1:
					fragment = new MainOrderFragment();
					break;
				case 2:
					fragment = new ConversationListFragment();
					break;
				case 3:
					fragment = new MeFragment();
					break;
			}
			return fragment;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(isShouldCheckVersion()){
			checkRemoteVersion();
		}
	}

	private boolean isShouldCheckVersion(){
		String lastTime = getSharedPreferences("VERSION",MODE_PRIVATE).getString("VERSIONTIME", "");
		if(lastTime.equals(DateUtil.timestampToYMDay(System.currentTimeMillis()))){
			return false;
		}else{
			return true;
		}
	}

	private void saveCheckVersion(){
		SharedPreferences sp = getSharedPreferences("VERSION", MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("VERSIONTIME",DateUtil.timestampToYMDay(System.currentTimeMillis()));
		editor.commit();
	}

	private void showForceUpdate(){
		VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
		new AlertDialog.Builder(this).setTitle("有新版本").setCancelable(false).setMessage(entity.getChange_log()).setPositiveButton("更新", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
				Uri uri = Uri.parse(entity.getUrl());
				startActivity(new Intent(Intent.ACTION_VIEW,uri));
			}

		}).show();

	}

	private void showUpdate(){
		VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
		new AlertDialog.Builder(this).setTitle("有新版本").setCancelable(false).setMessage(entity.getChange_log()).setPositiveButton("更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				VersionUpdateEntity entity = VersionManager.getInstance().getVersionUpdateEntity();
				Uri uri = Uri.parse(entity.getUrl());
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			}

		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		}).show();

	}

	private void checkVersion(){
		if(VersionManager.getInstance().isForceUpdate()){
			showForceUpdate();
		}else if(VersionManager.getInstance().isNewVersion()){
			showUpdate();
		}else{}
	}

	private void checkRemoteVersion(){
		VersionCheckRequest request = new VersionCheckRequest();
		request.setCode(VersionManager.getInstance().getVersionCode());
		request.setNetworkListener(new CommonNetworkCallback<VersionUpdateEntity>() {

			@Override
			public void onSuccess(VersionUpdateEntity data) {
				hideLoading();
				VersionManager.getInstance().saveVersionUpdateEntity(data);
				saveCheckVersion();
				checkVersion();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				hideLoading();
			}
		});
		showLoading();
		request.getDataFromServer();
	}

	/**
	 * 提示更新头像
	 */
	public void refreshMeAvatar(final int type, final String data) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(type == Constants.MessageType.MessageType_AvatarNo){
					MeFragment.instance.setRegAvatarPrompt(data);
				}
				if(type == Constants.MessageType.MessageType_AvatarOk){
					MeFragment.instance.setNewAvatar(data);
				}
			}
		});
	}

	/**
	 * 提示被访问或被喜欢
	 */
	public void refreshMeUIWithBadge(final int type) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(null == meBadge){
					meBadge = new BadgeView(MainActivity.this);
					meBadge.setTargetView(indicator.getItemView(4));
					meBadge.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
					meBadge.setBadgeMargin(0, 0, 8, 0);
				}
				if(indicator.getCurrentItem() != 4) {
					meBadgeCount++;
					meBadge.setVisibility(View.VISIBLE);
					meBadge.setBadgeCount(meBadgeCount);
				}
				if(type == Constants.MessageType.MessageType_USER_VISIT){
					MeFragment.instance.addVisitBadge();
				}
				if(type == Constants.MessageType.MessageType_USER_LIKE){
					MeFragment.instance.addLikeBadge();
				}
			}
		});
	}

	//刷新消息列表界面
	private void refreshUIWithMessage() {
		runOnUiThread(new Runnable() {
			public void run() {
				// 刷新bottom bar消息未读数
				updateUnreadLabel();
				if (viewPager.getCurrentItem() == 2) {
					// 当前页面如果为聊天历史页面，刷新此页面
					((ConversationListFragment)(myAdapter.getCurrentFragment())).refresh();
				}
			}
		});
	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		if(indicator.getCurrentItem() == 2){
			return;
		}
		int count = getUnreadMsgCountTotal();
		if(null == messageBadge){
			messageBadge = new BadgeView(this);
			messageBadge.setTargetView(indicator.getItemView(2));
			messageBadge.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
			messageBadge.setBadgeMargin(0, 0, 15, 0);
		}
		if (count > 0) {
			messageBadge.setBadgeCount(count);
			messageBadge.setVisibility(View.VISIBLE);
		} else {
			messageBadge.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取未读消息数
	 *
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		int chatroomUnreadMsgCount = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		for(EMConversation conversation:EMChatManager.getInstance().getAllConversations().values()){
			if(conversation.getType() == EMConversation.EMConversationType.ChatRoom)
				chatroomUnreadMsgCount=chatroomUnreadMsgCount+conversation.getUnreadMsgCount();
		}
		return unreadMsgCountTotal-chatroomUnreadMsgCount;
	}

	public void showLoading(){
		if(null == mProgressDlg){
			mProgressDlg = new ProgressDialog(this,R.style.MyDialogStyle);
			mProgressDlg.setCancelable(false);
		}else{
			mProgressDlg.dismiss();
		}
		mProgressDlg.show();
		mProgressDlg.setContentView(R.layout.dlalog_loading);
	}

	public void hideLoading(){
		if(null != mProgressDlg){
			mProgressDlg.dismiss();
		}
	}

	//保存用户对屏蔽提示的操作
	private void saveSheildIgnoreFlag(int count) {
		SharedPreferences sp = this.getSharedPreferences(LoginActivity.SP_USER_INFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt("SHILE_CONTACTS_IGNORE", count);
		edit.commit();
	}

	private void saveSheildSetFlag() {
		SharedPreferences sp = this.getSharedPreferences(LoginActivity.SP_USER_INFO, MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putBoolean("SHILE_CONTACTS_SET", true);
		edit.commit();
	}

	//读取用户对屏蔽提示的操作
	private int getSheildIgnoreFlag() {
		SharedPreferences sp = this.getSharedPreferences(LoginActivity.SP_USER_INFO, MODE_PRIVATE);
		return sp.getInt("SHILE_CONTACTS_IGNORE", 0);
	}

	private boolean getSheildSetFlag() {
		SharedPreferences sp = this.getSharedPreferences(LoginActivity.SP_USER_INFO, MODE_PRIVATE);
		return sp.getBoolean("SHILE_CONTACTS_SET", false);
	}

	//检查屏蔽通讯录联系人
	private void checkSheildContacts(){
		final int sheildIgnore = getSheildIgnoreFlag();
		final boolean sheildSet = getSheildSetFlag();
		if(sheildSet || sheildIgnore >= 2){
			return;
		}
		GetSheildContactsRequest request = new GetSheildContactsRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<Integer>() {
			@Override
			public void onSuccess(Integer data) {
				 if(1 != data.intValue()){
					new AlertDialog.Builder(MainActivity.this).setTitle("屏蔽通讯录联系人")
							.setMessage(getResources().getString(R.string.shield_contacts))
							.setPositiveButton("屏蔽", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									sheildContacts();
								}
							})
							.setNeutralButton("忽略", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									saveSheildIgnoreFlag(sheildIgnore + 1);
								}
							})
							.create()
							.show();
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {}
		});
		request.getDataFromServer();
	}

	//屏蔽通讯录联系人
	private void sheildContacts(){
		List<String> contacts = ConstactUtils.getContact(this);
		if(contacts.size() < 1){
			return;
		}
		SheildContactsRequest request = new SheildContactsRequest();
		request.setUserid(UserUtils.getUserid());
		request.setContacts(contacts);
		request.setNetworkListener(new CommonNetworkCallback<String>() {
			@Override
			public void onSuccess(String data) {
				saveSheildSetFlag();
				Toast.makeText(MainActivity.this, "屏蔽通讯录联系人成功", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {}
		});
		request.getDataFromServer();
	}
}
