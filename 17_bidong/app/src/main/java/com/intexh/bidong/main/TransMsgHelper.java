package com.intexh.bidong.main;

import android.app.Activity;

import com.easemob.chat.EMMessage;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import com.intexh.bidong.base.BaseActivityInterface;
import com.intexh.bidong.constants.Constants;
import com.intexh.bidong.easemob.video.VideoGiftDialog;
import com.intexh.bidong.easemob.video.VideoGiftDialog.VideoGiftDialogMode;
import com.intexh.bidong.easemob.video.VideoGiftDialog.ViewGiftDialogListener;
import com.intexh.bidong.gift.SendGiftRequest;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.user.FriendsManager.OnFriendsListener;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.GiftSummaryEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.UserUtils;

public class TransMsgHelper {

	public static void handleTransMessage(final Activity activity, EMMessage message) {
		final int type = message.getIntAttribute("type", -1);
		final String data = message.getStringAttribute("data", null);
		if (type > 0) {
			switch (type) {
				case Constants.MessageType.MessageType_GiftReceive:
				case Constants.MessageType.MessageType_GiftAsk: {
					showVideoGiftDialog(activity, message);
					break;
				}
				case Constants.MessageType.MessageType_VIDEO_OK: {
					if(null != data){
						UserUtils.getUserInfo().getUser().setVideo_id(data);
					}
					break;
				}
				case Constants.MessageType.MessageType_USER_VISIT:
				case Constants.MessageType.MessageType_USER_LIKE:{
					if(activity instanceof MainActivity){
						((MainActivity) activity).refreshMeUIWithBadge(type);
					}
					break;
				}
				case Constants.MessageType.MessageType_AvatarOk:
				case Constants.MessageType.MessageType_AvatarNo:{
					if(activity instanceof MainActivity){
						((MainActivity) activity).refreshMeAvatar(type, data);
					}
					break;
				}
			}
		}
	}

	private static void showVideoGiftDialog(final Activity activity, EMMessage message){
		final int type = message.getIntAttribute("type", -1);
		String ss = message.getStringAttribute("data", null);
		if(null == ss){
			return;
		}
		final GiftSummaryEntity summaryEntity = GsonUtils.jsonToObj(ss, GiftSummaryEntity.class);
		if (null == summaryEntity) {
			return;
		}
		FriendsManager.getInstance().getFrinedInfo(UserUtils.getHXUserid(summaryEntity.getFrom_id()),
			new OnFriendsListener() {
				@Override
				public void onLoadFrinedsInfo(String hxid, FriendItemEntity userInfo) {
					final SendGiftItemEntity entity = new SendGiftItemEntity();
					entity.setFrom(UserUtils.getUserInfo().getUser());
					entity.setTo(userInfo.getFans());
					GiftItemEntity gift = new GiftItemEntity();
					gift.setId(summaryEntity.getGift_id());
					gift.setName(summaryEntity.getName());
					gift.setUri(summaryEntity.getUri());
					gift.setPrice(Integer.parseInt(summaryEntity.getValue()));
					entity.setGift(gift);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							VideoGiftDialog dlg = new VideoGiftDialog(activity);
							if(type == Constants.MessageType.MessageType_GiftReceive){
								dlg.setMode(VideoGiftDialogMode.VideoGiftDialogModeReceive);
							}else{
								dlg.setMode(VideoGiftDialogMode.VideoGiftDialogModeSend);
							}
							dlg.setGiftEntity(entity);
							dlg.setGiftDialogListener(new ViewGiftDialogListener() {

								@Override
								public void onClickSend(SendGiftItemEntity entity) {
//														Intent intent = new Intent(activity,GiftConfirmActivity.class);
//														intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, GsonUtils.objToJson(entity.getGift()));
//														intent.putExtra(GiftConfirmActivity.GIFT_FROM, 2);
//														intent.putExtra(GiftConfirmActivity.USER_ID, summaryEntity.getFrom_id());
//														activity.startActivity(intent);
									SendGiftRequest request = new SendGiftRequest();
									request.setFrom_id(UserUtils.getUserid());
									request.setTo_id(summaryEntity.getFrom_id());
									request.setGift_id(summaryEntity.getGift_id());
									request.setOrigin(2);
									request.setValue(Integer.parseInt(summaryEntity.getValue()));
									request.setNetworkListener(new CommonNetworkCallback<SendGiftItemEntity>() {

										@Override
										public void onSuccess(SendGiftItemEntity data) {
											((BaseActivityInterface) activity).hideLoading();
											((BaseActivityInterface) activity).showToast("礼物发送成功");
										}

										@Override
										public void onFailed(int code, HttpException error, String reason) {
											((BaseActivityInterface) activity).hideLoading();
											((BaseActivityInterface) activity).showToast(reason);
										}
									});
									((BaseActivityInterface) activity).showLoading();
									request.getDataFromServer();
								}

								@Override
								public void onClickCancel() {}

								@Override
								public void onClickBeg(SendGiftItemEntity entity) {}
							});
							dlg.show();
						}
					});
				}

				@Override
				public void onLoadFrinedsFailed(String hxid, int code, String reason) {}
			});
	}

}
