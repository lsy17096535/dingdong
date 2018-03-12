package com.intexh.bidong.easemob.chat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.widget.ChatRowVoiceCall;
import com.easemob.easeui.ui.EaseChatFragment;
import com.easemob.easeui.ui.EaseChatFragment.EaseChatFragmentListener;
import com.easemob.easeui.widget.chatrow.EaseChatRow;
import com.easemob.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.easemob.util.PathUtil;
import com.intexh.bidong.gift.GiftSendDialog;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import com.intexh.bidong.R;
import com.intexh.bidong.easemob.common.ImageGridActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.easemob.domain.RobotUser;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.easemob.video.VideoCallActivity;
import com.intexh.bidong.easemob.video.VoiceCallActivity;
import com.intexh.bidong.gift.GiftConfirmActivity;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GetUserBaseRequest;
import com.intexh.bidong.main.square.GiftActivity;
import com.intexh.bidong.main.square.GiftGridAdapter;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.UserUtils;

public class ChatFragment extends EaseChatFragment implements EaseChatFragmentListener{

    //避免和基类定义的常量可能发生的冲突，常量从11开始定义
    private static final int ITEM_VIDEO = 11;
//    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 12;
    private static final int ITEM_VIDEO_CALL = 13;
    private static final int ITEM_SENDGIFT = 14;
    
    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
//    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 12;
    private static final int REQUEST_CODE_CONTEXT_MENU = 13;
    
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3; 
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private EaseChatFragmentListener chatCallback = null;   
    
    private static final int REQUESTCODE_GIFT = 100;
    private static final int REQUESTCODE_CONFIRM = 101;
    
    public void setEaseChatFragmentProxyListener(EaseChatFragmentListener listener){
    	chatCallback = listener;
    }
    
    /**
     * 是否为环信小助手
     */
    private boolean isRobot;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
    	setChatFragmentListener(this);
        if (chatType == Constant.CHATTYPE_SINGLE) { 
            Map<String,RobotUser> robotMap = DemoHelper.getInstance().getRobotList();
            if(robotMap!=null && robotMap.containsKey(toChatUsername)){
                isRobot = true;
            }
        }
        super.setUpView();
    }
    
    @Override
    protected void registerExtendMenuItem() {
        //demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
        super.registerExtendMenuItem();
        //增加扩展item
        inputMenu.registerExtendMenuItem(R.string.attach_video, R.drawable.em_chat_video_selector, ITEM_VIDEO, extendMenuItemClickListener);
//        inputMenu.registerExtendMenuItem(R.string.attach_file, R.drawable.em_chat_file_selector, ITEM_FILE, extendMenuItemClickListener);
        if(chatType == Constant.CHATTYPE_SINGLE){
            inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, ITEM_VOICE_CALL, extendMenuItemClickListener);
            inputMenu.registerExtendMenuItem(R.string.attach_video_call, R.drawable.em_chat_video_call_selector, ITEM_VIDEO_CALL, extendMenuItemClickListener);
            if(!toChatUsername.equals("ppstar")){
            	inputMenu.registerExtendMenuItem("送礼物", R.drawable.em_chat_gift_selector, ITEM_SENDGIFT, extendMenuItemClickListener);
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
            switch (resultCode) {
            case ContextMenuActivity.RESULT_CODE_COPY: // 复制消息
                clipboard.setText(((TextMessageBody) contextMenuMessage.getBody()).getMessage());
                break;
            case ContextMenuActivity.RESULT_CODE_DELETE: // 删除消息
                conversation.removeMessage(contextMenuMessage.getMsgId());
                messageList.refresh();
                break;

//            case ContextMenuActivity.RESULT_CODE_FORWARD: // 转发消息
//                Intent intent = new Intent(getActivity(), ForwardMessageActivity.class);
//                intent.putExtra("forward_msg_id", contextMenuMessage.getMsgId());
//                startActivity(intent);
//                
//                break;
            default:
                break;
            }
        }
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
            case REQUEST_CODE_SELECT_VIDEO: //发送选中的视频
                if (data != null) {
                    int duration = data.getIntExtra("dur", 0);
                    String videoPath = data.getStringExtra("path");
                    File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                        ThumbBitmap.compress(CompressFormat.JPEG, 100, fos);
                        fos.close();
                        sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUESTCODE_GIFT:{
                // TODO: 2018/3/12  跳转到礼物页面
                String ss = data.getStringExtra(GiftActivity.GIFT_ENTITY);
            	FriendItemEntity friendEntity = FriendsManager.getInstance().getFrinedInfo(toChatUsername);
            	Intent intent = new Intent(getActivity(),GiftConfirmActivity.class);
            	intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, ss);
            	intent.putExtra(GiftConfirmActivity.USER_ID, friendEntity.getFans().getId());
            	startActivityForResult(intent, REQUESTCODE_CONFIRM);
            	break;
            }
            case REQUESTCODE_CONFIRM:{
                //todo  点击礼物回来之后
            	String ss = data.getStringExtra(GiftConfirmActivity.GIFT_ENTITY);
            	final GiftItemEntity entity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
            	GiftSendDialog dialog = new GiftSendDialog(getActivity(),entity);
            	dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        sendTextMessage("送给你礼物[" + entity.getName() + entity.getPrice() +  "金币]");
                    }
                });
            	dialog.show();

//            	final FriendItemEntity friendEntity = FriendsManager.getInstance().getFrinedInfo(toChatUsername);
//            	if(null != entity){
//    				SendGiftRequest netRequest = new SendGiftRequest();
//    				netRequest.setFrom_id(UserUtils.getUserid());
//    				netRequest.setGift_id(entity.getId());
//    				netRequest.setTo_id(friendEntity.getFans().getId());
//    				netRequest.setOrigin(1);
//    				netRequest.setValue(entity.getPrice());
//    				netRequest
//    						.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {
//
//    							@Override
//    							public void onSuccess(SendGiftItemEntity data) {
//    								hideLoading();
//    								friendEntity.setOppo_status(-1);
//    								FriendsManager.getInstance().updateFriend(
//    										friendEntity);
//    								showToast("礼物发送成功，等待对方确认");
//    								sendTextMessage("送给你礼物[" + entity.getName() + entity.getPrice() +  "金币]");
//    							}
//
//    							@Override
//    							public void onFailed(int code, HttpException error,
//    									String reason) {
//    								hideLoading();
//    								showToast(reason);
//    							}
//    						});
//    				showLoading();
//    				netRequest.getDataFromServer();
//            	}
            	break;
            }
//            case REQUEST_CODE_SELECT_FILE: //发送选中的文件
//                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        sendFileByUri(uri);
//                    }
//                }
//                break;

            default:
                break;
            }
        }
        
    }
    
    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if(isRobot){
            //设置消息扩展属性
            message.setAttribute("em_robot_message", isRobot);
        }
        if(null != chatCallback){
        	chatCallback.onSetMessageAttributes(message);
        }
    }
    
    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        //设置自定义listview item提供者
        return new CustomChatRowProvider();
    }
  

    @Override
    public void onEnterToChatDetails() {
//        if (chatType == Constant.CHATTYPE_GROUP) {
//            EMGroup group = EMGroupManager.getInstance().getGroup(toChatUsername);
//            if (group == null) {
//                Toast.makeText(getActivity(), R.string.gorup_not_found, 0).show();
//                return;
//            }
//            startActivityForResult(
//                    (new Intent(getActivity(), GroupDetailsActivity.class).putExtra("groupId", toChatUsername)),
//                    REQUEST_CODE_GROUP_DETAIL);
//        }
    }

    private void getUserBase(String userid){
        GetUserBaseRequest request = new GetUserBaseRequest();
        request.setUserid(userid);
        request.setNetworkListener(new CommonNetworkCallback<User>() {
            @Override
            public void onSuccess(User data) {
                hideLoading();
                if(getActivity()==null)return;
                Intent intent = new Intent(getActivity(), UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ENTITY, GsonUtils.objToJson(data));
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

    @Override
    public void onAvatarClick(String username) {
        //头像点击事件
        getUserBase(username);
    }
    
    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
    	if(null != chatCallback){
    		return chatCallback.onMessageBubbleClick(message);
    	}
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        //消息框长按
        startActivityForResult((new Intent(getActivity(), ContextMenuActivity.class)).putExtra("message",message),
                REQUEST_CODE_CONTEXT_MENU);
    }



    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
        case ITEM_VIDEO: {//视频
            Intent intent = new Intent(getActivity(), ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
            break;
        }
//        case ITEM_FILE: //一般文件
//            //demo这里是通过系统api选择文件，实际app中最好是做成qq那种选择发送文件
//            selectFileFromLocal();
//            break;
        case ITEM_VOICE_CALL: //音频通话
            startVoiceCall();
            break;
        case ITEM_VIDEO_CALL: //视频通话
            startVideoCall();
            break;
        case ITEM_SENDGIFT:{  //发送礼物
        	GetCapitalRequest request = new GetCapitalRequest();
    		request.setUserid(UserUtils.getUserid());
    		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

    			@Override
    			public void onSuccess(Capital data) {
    				hideLoading();
    				FriendItemEntity friendEntity = FriendsManager.getInstance().getFrinedInfo(toChatUsername);
    				Intent intent = new Intent(getActivity(), GiftActivity.class);
    				intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
    				intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
    				if(1 == friendEntity.getFans().getGender()){
    					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
    				}else{
    					intent.putExtra(GiftActivity.GIFT_TIPS,  R.string.addfriend_male_tips);
    				}
    				startActivityForResult(intent, REQUESTCODE_GIFT);
    			}

    			@Override
    			public void onFailed(int code, HttpException error, String reason) {
    				hideLoading();
    				showToast(reason);
    			}
    		});
    		showLoading();
    		request.getDataFromServer();
        	break;
        }
        default:
            break;
        }
        //不覆盖已有的点击事件
        return false;
    }
    
//    /**
//     * 选择文件
//     */
//    protected void selectFileFromLocal() {
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT < 19) { //19以后这个api不可用，demo这里简单处理成图库选择图片
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
//    }
    
    /**
     * 拨打语音电话
     */
    protected void startVoiceCall() {
        if (!EMChatManager.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }
    
    /**
     * 拨打视频电话
     */
    protected void startVideoCall() {
        if (!EMChatManager.getInstance().isConnected())
            Toast.makeText(getActivity(), R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(getActivity(), VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
            inputMenu.hideExtendMenuContainer();
        }
    }
    
    /**
     * chat row provider 
     *
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种
            return 4;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if(message.getType() == EMMessage.Type.TXT){
                //语音通话类型
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //视频通话
                    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {
            if(message.getType() == EMMessage.Type.TXT){
                // 语音通话,  视频通话
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                    message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }
            }
            return null;
        }
    }
}
