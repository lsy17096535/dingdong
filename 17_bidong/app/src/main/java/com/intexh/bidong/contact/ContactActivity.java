package com.intexh.bidong.contact;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.easeui.utils.Settings;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseTitleActivity;
import com.intexh.bidong.contact.ContactAdapter.OnContactMenuClickListener;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.gift.GiftCheckUtils;
import com.intexh.bidong.gift.SendGiftRequest;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GiftActivity;
import com.intexh.bidong.main.square.GiftGridAdapter;
import com.intexh.bidong.message.AddToBlacklistRequest;
import com.intexh.bidong.message.DeleteFriendRequest;
import com.intexh.bidong.message.GetFriendListRequest;
import com.intexh.bidong.message.UnblockFriendRequest;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.PingYinHelpUtil;
import com.intexh.bidong.utils.PinyinComparator;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import com.intexh.bidong.widgets.SideBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactActivity extends BaseTitleActivity {

	@ViewInject(R.id.list_contact_main)
	private ListView mListView;
	@ViewInject(R.id.sideBar_contact_sider)
	private SideBar sideBar;
	@ViewInject(R.id.edit_contact_search)
	private EditText searchInput;
	private List<ContactUsers> datas = new ArrayList<ContactUsers>();
	private List<FriendItemEntity> friends = new ArrayList<FriendItemEntity>();
	private List<FriendItemEntity> searchFriends = new ArrayList<FriendItemEntity>();
	private String[] LETTERS = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"X", "X", "Y", "Z", "#" };
	private ContactAdapter mAdapter = null;
	private TextView mDialogText = null;
	private static final int REQUEST_GIFT = 100;
	private FriendItemEntity friendEntity = null;
	private boolean isSearchMode = false;
	private int jumpToGiftType=1;   //赠送礼物类型

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_contact);
		ViewUtils.inject(this);
		setTitleText("好友");
		
		searchInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				String ss = searchInput.getText().toString();
				if(!StringUtil.isEmptyString(ss)){
					ss = ss.toLowerCase();
					isSearchMode = true;
					searchFriends.clear();
					mAdapter.setDatas(searchFriends);
					String key = ss;
					ss = PingYinHelpUtil.getPingYin(ss);
					for(FriendItemEntity entity : friends){
						String sort_key = entity.getSort_key();
						if(null != sort_key && (sort_key.contains(ss) || sort_key.contains(key))){
							searchFriends.add(entity);
						}else if(entity.getFans().getAlias().toLowerCase().contains(ss)){
							searchFriends.add(entity);
						}
					}
				}else{
					isSearchMode = false;
					mAdapter.setDatas(friends);
				}
				mAdapter.notifyDataSetChanged();
			}
		});
		LayoutInflater inflater = LayoutInflater.from(this);
		// View headerView = inflater.inflate(R.layout.header_contact, null);
		// mListView.addHeaderView(headerView);
		mAdapter = new ContactAdapter();
		mAdapter.setDatas(friends);
		mListView.setAdapter(mAdapter);
		mAdapter.setItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				FriendItemEntity entity = null;
				if(isSearchMode){
					entity = searchFriends.get(arg2);
				}else{
					entity = friends.get(arg2);
				}
				friendEntity = entity;
				if (0 == entity.getOppo_status()) {
					if(!GiftCheckUtils.checkChatidHex(UserUtils.getHXUserid(friendEntity.getFans().getId()))){    //没有赠送过礼物 需要赠送
						jumpToGift();
						jumpToGiftType=2;
						return;
					}

					Intent intent = new Intent(ContactActivity.this, ChatActivity.class);
					intent.putExtra(Constant.EXTRA_USER_ID, UserUtils.getHXUserid(entity.getFans().getId()));
					startActivity(intent);
				} else if (1 == entity.getOppo_status()) {
					new AlertDialog.Builder(ContactActivity.this)
							.setTitle("你已被对方屏蔽")
							.setMessage("送个礼物，对方接受则自动解除屏蔽")
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {

										}
									})
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											jumpToGiftType=1;
											jumpToGift();
										}
									}).show();
				} else if (2 == entity.getOppo_status()) {
					new AlertDialog.Builder(ContactActivity.this)
							.setTitle("你已被对方删除")
							.setMessage("送个礼物，对方接受则恢复好友关系")
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {

										}
									})
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											jumpToGiftType=1;
											jumpToGift();
										}
									}).show();
				} else if (-1 == entity.getOppo_status()) {
					showToast("你还不是对方好友，请送礼物或等待接受");
				}
			}
		});
		mAdapter.setMenuListener(new OnContactMenuClickListener() {

			@Override
			public void didClickUnBlock(final int index) {
				FriendItemEntity entity = mAdapter.getItem(index);
				UnblockFriendRequest request = new UnblockFriendRequest();
				request.setId(entity.getId());
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						FriendItemEntity entity = mAdapter.getItem(index);
						entity.setStatus(0);
						FriendsManager.getInstance().updateFriend(entity);
						mAdapter.notifyDataSetChanged();
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
			public void didClickDelete(final int index) {
				FriendItemEntity entity = mAdapter.getItem(index);
				DeleteFriendRequest request = new DeleteFriendRequest();
				request.setId(entity.getId());
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						FriendItemEntity entity = mAdapter.getItem(index);
						entity.setStatus(2);
						FriendsManager.getInstance().updateFriend(entity);
						friends.remove(index);
						mAdapter.notifyDataSetChanged();
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
			public void didClickBlock(final int index) {
				FriendItemEntity entity = mAdapter.getItem(index);
				AddToBlacklistRequest request = new AddToBlacklistRequest();
				request.setId(entity.getId());
				request.setNetworkListener(new CommonNetworkCallback<String>() {

					@Override
					public void onSuccess(String data) {
						hideLoading();
						FriendItemEntity entity = mAdapter.getItem(index);
						entity.setStatus(1);
						FriendsManager.getInstance().updateFriend(entity);
						mAdapter.notifyDataSetChanged();
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
		mDialogText = (TextView) LayoutInflater.from(this).inflate(R.layout.list_position, null);
		WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		mDialogText.setVisibility(View.INVISIBLE);
		getAllFriends();
	}


	private void jumpToGift() {
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				Intent intent = new Intent(ContactActivity.this, GiftActivity.class);
				intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
				intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
				if(1 == friendEntity.getFans().getGender()){
					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
				}else{
					intent.putExtra(GiftActivity.GIFT_TIPS,  R.string.addfriend_male_tips);
				}
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

	private void getAllFriends() {
		GetFriendListRequest request = new GetFriendListRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<FriendItemEntity[]>() {

			@Override
			public void onSuccess(FriendItemEntity[] data) {
				hideLoading();
				datas.clear();
				friends.clear();
				if (null != data) {
					Map<String, Integer> maps = new HashMap<String, Integer>();
					for (int i = 0; i < LETTERS.length; i++) {
						maps.put(LETTERS[i], i);
						ContactUsers contacts = new ContactUsers();
						contacts.setSort(LETTERS[i]);
						contacts.getUsers().clear();
						datas.add(contacts);
					}
					for (FriendItemEntity entity : data) {
						String sortKey = entity.getSort_key();
						if (null == sortKey) {
							sortKey = PingYinHelpUtil.getPingYin(entity.getFans().getAlias());
							if (null == sortKey) {
								sortKey = "#";
							}
							entity.setSort_key(sortKey);
							entity.setSort_letter(sortKey.substring(0, 1));
						}
						Integer index = maps.get(sortKey.substring(0, 1).toUpperCase());
						if (null == index) {
							index = LETTERS.length - 1;
						}
						ContactUsers contacts = datas.get(index);
						contacts.getUsers().add(entity);
					}
					for (ContactUsers contacts : datas) {
						List<FriendItemEntity> users = contacts.getUsers();
						FriendItemEntity[] usersArray = users.toArray(new FriendItemEntity[users.size()]);
						Arrays.sort(usersArray, new PinyinComparator());
						users.clear();
						for (FriendItemEntity entity : usersArray) {
							users.add(entity);
							friends.add(entity);
						}
					}
					mAdapter.notifyDataSetChanged();
					sideBar.setListView(mListView, mAdapter);
					sideBar.setTextView(mDialogText);
				}
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
	protected void onActivityResult(int request, int result, Intent arg2) {
		super.onActivityResult(request, result, arg2);
		if (result == RESULT_OK) {
			switch (request) {
			case REQUEST_GIFT: {
				String ss = arg2.getStringExtra(GiftActivity.GIFT_ENTITY);
				GiftItemEntity entity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
				SendGiftRequest netRequest = new SendGiftRequest();
				netRequest.setFrom_id(UserUtils.getUserid());
				netRequest.setGift_id(entity.getId());
				netRequest.setTo_id(friendEntity.getFans().getId());
				netRequest.setOrigin(1);
				netRequest.setValue(entity.getPrice());
				netRequest.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

							@Override
							public void onSuccess(SendGiftItemEntity data) {
								hideLoading();
								if(jumpToGiftType==2){
									//记录礼物赠送好友的日期 用于判断每天与好友聊天需要赠送一次礼物
									GiftCheckUtils.saveChatid(UserUtils.getHXUserid(friendEntity.getFans().getId()));
								}else{
									friendEntity.setOppo_status(-1);
									FriendsManager.getInstance().updateFriend(friendEntity);
									mAdapter.notifyDataSetChanged();
									showToast("礼物发送成功，等待对方确认");
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
				netRequest.getDataFromServer();
				break;
			}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mDialogText) {
			WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			mWindowManager.removeView(mDialogText);
		}
	}

}
