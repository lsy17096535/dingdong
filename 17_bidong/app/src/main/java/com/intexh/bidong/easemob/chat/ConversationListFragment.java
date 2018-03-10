package com.intexh.bidong.easemob.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.easeui.ui.EaseConversationListFragment;
import com.easemob.easeui.utils.Settings;
import com.easemob.util.NetUtils;
import com.intexh.bidong.R;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.easemob.dao.InviteMessgeDao;
import com.intexh.bidong.gift.GiftCheckUtils;
import com.intexh.bidong.gift.SendGiftRequest;
import com.intexh.bidong.main.MainActivity;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GiftActivity;
import com.intexh.bidong.main.square.GiftGridAdapter;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.user.FriendsManager.OnFriendsListener;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.DateUtil;
import com.intexh.bidong.utils.UserUtils;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import static android.app.Activity.RESULT_OK;

public class ConversationListFragment extends EaseConversationListFragment implements EMEventListener {

    private TextView errorText;
    private static final int REQUEST_GIFT = 100;
    private FriendItemEntity friendEntity = null;
    private int jumpToGiftType=1;   //赠送礼物类型

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // 注册上下文菜单
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final EMConversation conversation = conversationListView.getItem(position);
                if (conversation.getUserName().equals("ppstar")) {
//                    // 进入聊天页面
//                    Log.e("zjw1","friendEntity.getFans().getId()="+friendEntity.getFans().getId());
//                    String userDate = Settings.getString(friendEntity.getFans().getId(), "");
//                    if (TextUtils.isEmpty(userDate)) {    //没有赠送过礼物 需要赠送
//                        jumpToGift();
//                        jumpToGiftType=2;
//                        return;
//                    }
//
//                    if (!userDate.equals(DateUtil.timestampToYMDay(System.currentTimeMillis()))) {
//                        //对比今天没有送过礼物 需要赠送
//                        jumpToGift();
//                        jumpToGiftType=2;
//                        return;
//                    }
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//					if (conversation.isGroup()) {
//						if (conversation.getType() == EMConversationType.ChatRoom) {
//							// it's group chat
//							intent.putExtra(Constant.EXTRA_CHAT_TYPE,Constant.CHATTYPE_CHATROOM);
//						} else {
//							intent.putExtra(Constant.EXTRA_CHAT_TYPE,Constant.CHATTYPE_GROUP);
//						}
//
//					}
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, conversation.getUserName());
                    startActivity(intent);
                    return;
                }
                FriendsManager.getInstance().getFrinedInfo(conversation.getUserName(), new OnFriendsListener() {

                    @Override
                    public void onLoadFrinedsInfo(String hxid, FriendItemEntity entity) {
                        friendEntity = entity;
                        if (0 == entity.getOppo_status()) {
                            String username = conversation.getUserName();
                            if (username.equals(EMChatManager.getInstance().getCurrentUser()))
                                Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                            else {
                                // 进入聊天页面
                                if (!GiftCheckUtils.checkChatidHex(UserUtils.getHXUserid(friendEntity.getFans().getId()))) {    //没有赠送过礼物 需要赠送
                                    jumpToGift();
                                    jumpToGiftType=2;
                                    return;
                                }

                                Intent intent = new Intent(getActivity(), ChatActivity.class);
//										if (conversation.isGroup()) {
//											if (conversation.getType() == EMConversationType.ChatRoom) {
//												// it's group chat
//												intent.putExtra(
//														Constant.EXTRA_CHAT_TYPE,
//														Constant.CHATTYPE_CHATROOM);
//											} else {
//												intent.putExtra(
//														Constant.EXTRA_CHAT_TYPE,
//														Constant.CHATTYPE_GROUP);
//											}
//
//										}
                                // it's single chat
                                intent.putExtra(Constant.EXTRA_USER_ID, username);
                                startActivity(intent);
                            }
                        } else if (1 == entity.getOppo_status()) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("你已被对方屏蔽")
                                    .setMessage("送个礼物，对方接受则自动解除屏蔽")
                                    .setNegativeButton(
                                            "取消",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface arg0,
                                                                    int arg1) {
                                                }
                                            })
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface arg0,
                                                        int arg1) {
                                                    jumpToGiftType=1;
                                                    jumpToGift();
                                                }
                                            }).show();
                        } else if (2 == entity.getOppo_status()) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("你已被对方删除")
                                    .setMessage("送个礼物，对方接受则恢复好友关系")
                                    .setNegativeButton(
                                            "取消",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            })
                                    .setPositiveButton(
                                            "确定",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface arg0,
                                                        int arg1) {
                                                    jumpToGiftType=1;
                                                    jumpToGift();
                                                }
                                            }).show();
                        } else if (-1 == entity.getOppo_status()) {
                            showToast("你还不是对方好友，请送礼物或等待接受");
                        }
                    }

                    @Override
                    public void onLoadFrinedsFailed(String hxid, int code, String reason) {
                        showToast(reason);
                    }
                });
            }
        });
    }

    private void jumpToGift() {
        GetCapitalRequest request = new GetCapitalRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<Capital>() {

            @Override
            public void onSuccess(Capital data) {
                hideLoading();
                Intent intent = new Intent(getActivity(), GiftActivity.class);
                intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
                intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
                if (1 == friendEntity.getFans().getGender()) {
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
                } else {
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_male_tips);
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

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }

    // @Override
    // public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo
    // menuInfo) {
    // getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
    // }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        /*
		 * if (item.getItemId() == R.id.delete_message) { deleteMessage = true;
		 * handled = true; } else
		 */
        // if (item.getItemId() == R.id.delete_conversation) {
        // deleteMessage = true;
        // handled = true;
        // }
        EMConversation tobeDeleteCons = conversationListView
                .getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(
                tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(),
                deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        refresh();

        // 更新消息未读数
        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled ? true : super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // register the event listener when enter the foreground
//		EMChatManager.getInstance().registerEventListener(
//				this,
//				new EMNotifierEvent.Event[] { EMNotifierEvent.Event.EventNewMessage,
//						EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventDeliveryAck,
//						EMNotifierEvent.Event.EventReadAck,EMNotifierEvent.Event.EventNewCMDMessage});
    }

    @Override
    public void onPause() {
        super.onPause();
        // unregister this event listener when this activity enters the
        // background
//		EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public void onEvent(EMNotifierEvent event) {
        // 更新消息未读数
        ((MainActivity) getActivity()).updateUnreadLabel();
        refresh();
    }

    @Override
    public void onActivityResult(int request, int result, Intent arg2) {
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
                                showToast("礼物发送成功，等待对方确认");
                            }

                        }

                        @Override
                        public void onFailed(int code, HttpException error, String reason) {
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

}
