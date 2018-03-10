package com.intexh.bidong.easemob.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.NormalFileMessageBody;
import com.easemob.easeui.ui.EaseChatFragment.EaseChatFragmentListener;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.easemob.util.FileUtils;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.io.File;

import com.intexh.bidong.R;
import com.intexh.bidong.common.OpporCommendActivity;
import com.intexh.bidong.constants.Constants;
import com.intexh.bidong.easemob.base.BaseActivity;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.gift.GetReceiveGiftDetailRequest;
import com.intexh.bidong.gift.GetSendGiftDetailRequest;
import com.intexh.bidong.gift.GiftDetailActivity;
import com.intexh.bidong.main.TransMsgHelper;
import com.intexh.bidong.main.square.VideoDetailActivity;
import com.intexh.bidong.order.GetReceiveOrderDetailRequest;
import com.intexh.bidong.order.GetSendOrderDetailRequest;
import com.intexh.bidong.order.OrderDetailActivity;
import com.intexh.bidong.userentity.HotVideoEntity;
import com.intexh.bidong.userentity.OrderEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.UserUtils;

/**
 * 聊天页面，需要fragment的使用link EaseChatFragment
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private ChatFragment chatFragment;
    private String toChatUsername;
    public String playMsgId;
    public static final String IMAGE_DIR = "chat/image/";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //聊天人或群id
        toChatUsername = getIntent().getExtras().getString("userId");
        //可以直接new EaseChatFratFragment使用
        chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        chatFragment.setEaseChatFragmentProxyListener(new EaseChatFragmentListener() {

            @Override
            public void onSetMessageAttributes(EMMessage message) {
                message.setAttribute("nickName", UserUtils.getUserInfo().getUser().getAlias());
                if(UserUtils.getUserInfo().getUser().getAvatar()!=null){
                    message.setAttribute("avalis", UserUtils.getUserInfo().getUser().getAvatar());
                }
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {
            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                if (DemoHelper.getInstance().isVideoCalling || DemoHelper.getInstance().isVoiceCalling) {
                    return false;
                }
                int type = message.getIntAttribute("type", -1);
                if (type > 0) {
                    switch (type) {
                        case Constants.MessageType.MessageType_VideoAudio: {//视频审核通知
                            break;
                        }
                        case Constants.MessageType.MessageType_CashApply: {//体现处理
                            break;
                        }
                        case Constants.MessageType.MessageType_GiftAsk: {//要礼物
                            if (!DemoHelper.getInstance().isVideoCalling && !DemoHelper.getInstance().isVoiceCalling) {
                                TransMsgHelper.handleTransMessage(ChatActivity.this, message);
                            }
                            break;
                        }
                        case Constants.MessageType.MessageType_OfferReject:
                        case Constants.MessageType.MessageType_OfferAccept: {//邀约被接受
                            String data = message.getStringAttribute("data", null);
                            if (null != data) {
                                jumpToSendGift(data);
                            }
                            break;
                        }
                        case Constants.MessageType.MessageType_GiftReceive:
                        case Constants.MessageType.MessageType_OfferNew:
                        case Constants.MessageType.MessageType_OffsetCancel:{//新邀约
                            String data = message.getStringAttribute("data", null);
                            if (null != data) {
                                jumpToReceiveGift(data);
                            }
                            break;
                        }
                        case Constants.MessageType.MessageType_COMMENT: {
                            HotVideoEntity entity = GsonUtils.jsonToObj(message.getStringAttribute("data", null), HotVideoEntity.class);
                            entity.setUser(UserUtils.getUserInfo().getUser());
                            Intent intent = new Intent(ChatActivity.this, VideoDetailActivity.class);
                            intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(entity));
                            startActivity(intent);
                            break;
                        }
                        case Constants.MessageType.MessageType_OrderReject:
                        case Constants.MessageType.MessageType_OrderAccept:
                        case Constants.MessageType.MessageType_OrderAsk: {//约单被接受、拒绝、收款
                            String data = message.getStringAttribute("data", null);
                            if (null != data) {
                                jumpToSendOrder(data);
                            }
                            break;
                        }
                        case Constants.MessageType.MessageType_OrderNew:
                        case Constants.MessageType.MessageType_OrderCancel:
                        case Constants.MessageType.MessageType_OrderPay:{//新约单、取消约单、已付款
                            String data = message.getStringAttribute("data", null);
                            if (null != data) {
                                jumpToReceiveOrder(data);
                            }
                            break;
                        }
                        case Constants.MessageType.MessageType_OpporCommend:{//新推荐
                            String data = message.getStringAttribute("data", null);
                            if (null != data && data.indexOf("ppstar.cn") > 0) {
                                jumpToOpporCommend(data);
                            }
                            break;
                        }
                    }
                }
                switch (message.getType()) {
                    case FILE: {
                        // 文件存在，直接打开
                        NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
                        String filePath = fileMessageBody.getLocalUrl();
                        File file = new File(filePath);
                        if (file.exists()) {
                            FileUtils.openFile(file, ChatActivity.this);
                        }
                        break;
                    }
                    case IMAGE: {
//					ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
//					String filePath = imgBody.getLocalUrl();
//					if (filePath != null && new File(filePath).exists()) {
//						showImageView(ImageUtils.getThumbnailImagePath(filePath), null, filePath, null, message);
//					} else {
//						showImageView(ImageUtils.getThumbnailImagePath(filePath), null, filePath, IMAGE_DIR, message);
//					}
//					ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
//					String remotePath = imgBody.getRemoteUrl();
//					String filePath1 = ImageUtils.getImagePath(remotePath);
//					Intent intent = new Intent(ChatActivity.this, ShowBigImage.class);
//					File file = new File(filePath1);
//					if (file.exists()) {
//						Uri uri = Uri.fromFile(file);
//						intent.putExtra("uri", uri);
//					} else {
//						// The local full size pic does not exist yet.
//						// ShowBigImage needs to download it from the server
//						// first
//						// intent.putExtra("", message.get);
//						ImageMessageBody body = (ImageMessageBody) message.getBody();
//						intent.putExtra("secret", body.getSecret());
//						intent.putExtra("remotepath", remotePath);
//					}
//					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
//							&& message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
//						try {
//							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//							message.isAcked = true;
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					startActivity(intent);
                        break;
                    }
                    case LOCATION: {
//					LocationMessageBody locBody = (LocationMessageBody) message.getBody();
//					Intent intent;
//					intent = new Intent(ChatActivity.this, BaiduMapActivity.class);
//					intent.putExtra("latitude", locBody.getLatitude());
//					intent.putExtra("longitude", locBody.getLongitude());
//					intent.putExtra("address", locBody.getAddress());
//					startActivity(intent);
                        break;
                    }
                    case VIDEO: {
//					VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
//					Intent intent = new Intent(ChatActivity.this, ShowVideoActivity.class);
//					intent.putExtra("localpath", videoBody.getLocalUrl());
//					intent.putExtra("secret", videoBody.getSecret());
//					intent.putExtra("remotepath", videoBody.getRemoteUrl());
//					if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
//							&& message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
//						message.isAcked = true;
//						try {
//							EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//					startActivity(intent);
                        break;
                    }
                    default: {
                        return false;
                    }
//				case VOICE:{
//					playMsgId = message.getMsgId();
//					break;
//				}
                }
                return false;
            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onEnterToChatDetails() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAvatarClick(String username) {
                // TODO Auto-generated method stub

            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
                                  final EMMessage message) {
        final String remote = remoteDir;
        Intent intent = new Intent(ChatActivity.this, ShowBigImage.class);
        File file = new File(localFullSizePath);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            intent.putExtra("uri", uri);
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            // intent.putExtra("", message.get);
            ImageMessageBody body = (ImageMessageBody) message.getBody();
            intent.putExtra("secret", body.getSecret());
            intent.putExtra("remotepath", remote);
        }
        if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                && message.getChatType() != ChatType.GroupChat && message.getChatType() != ChatType.ChatRoom) {
            try {
                EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                message.isAcked = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startActivity(intent);
        return true;
//		// String imagename =
//		// localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
//		// localFullSizePath.length());
//		// final String remote = remoteDir != null ? remoteDir+imagename :
//		// imagename;
//		final String remote = remoteDir;
//		EMLog.d("###", "local = " + localFullSizePath + " remote: " + remote);
//		// first check if the thumbnail image already loaded into cache
//		Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
//		if (bitmap != null) {
//			// thumbnail image is already loaded, reuse the drawable
//			iv.setImageBitmap(bitmap);
//			iv.setClickable(true); 
//			iv.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					
//				}
//			});
//			return true;
//		} else {
//
//			new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, this, message);
//			return true;
//		}

    }

    private void jumpToSendGift(String data) {
        GetSendGiftDetailRequest request = new GetSendGiftDetailRequest();
        request.setId(data);
        request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

            @Override
            public void onSuccess(SendGiftItemEntity result) {
                hideLoading();
                Intent intent = new Intent(ChatActivity.this, GiftDetailActivity.class);
                intent.putExtra(GiftDetailActivity.GIFT_ENTITY, GsonUtils.objToJson(result));
                startActivity(intent);
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

    private void jumpToReceiveGift(String data) {
        GetReceiveGiftDetailRequest request = new GetReceiveGiftDetailRequest();
        request.setId(data);
        request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

            @Override
            public void onSuccess(SendGiftItemEntity result) {
                hideLoading();
                Intent intent = new Intent(ChatActivity.this, GiftDetailActivity.class);
                intent.putExtra(GiftDetailActivity.GIFT_ENTITY, GsonUtils.objToJson(result));
                startActivity(intent);
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

    private void jumpToSendOrder(String data) {
        GetSendOrderDetailRequest request = new GetSendOrderDetailRequest();
        request.setId(data);
        request.setNetworkListener(new CommonNetworkCallback<OrderEntity>() {

            @Override
            public void onSuccess(OrderEntity result) {
                hideLoading();
                Intent intent = new Intent(ChatActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_ENTITY, GsonUtils.objToJson(result));
                startActivity(intent);
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

    private void jumpToReceiveOrder(String data) {
        GetReceiveOrderDetailRequest request = new GetReceiveOrderDetailRequest();
        request.setId(data);
        request.setNetworkListener(new CommonNetworkCallback<OrderEntity>() {

            @Override
            public void onSuccess(OrderEntity result) {
                hideLoading();
                Intent intent = new Intent(ChatActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.ORDER_ENTITY, GsonUtils.objToJson(result));
                startActivity(intent);
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

    private void jumpToOpporCommend(String data) {
        Intent intent = new Intent(ChatActivity.this, OpporCommendActivity.class);
        intent.putExtra(OpporCommendActivity.OPPOR_URL, data);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername() {
        return toChatUsername;
    }
}
