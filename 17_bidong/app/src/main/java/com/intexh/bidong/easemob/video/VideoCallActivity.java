/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intexh.bidong.easemob.video;

import java.util.UUID;

import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.intexh.bidong.R;
import com.intexh.bidong.easemob.help.CameraHelper;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.easemob.video.VideoGiftDialog.VideoGiftDialogMode;
import com.intexh.bidong.easemob.video.VideoGiftDialog.ViewGiftDialogListener;
import com.intexh.bidong.gift.GiftConfirmActivity;
import com.intexh.bidong.main.TransMsgHelper;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GiftActivity;
import com.intexh.bidong.main.square.GiftGridAdapter;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.user.FriendsManager.OnFriendsListener;
import com.intexh.bidong.user.PromptActivity;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.exceptions.EMServiceNotReadyException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

public class VideoCallActivity extends CallActivity implements OnClickListener,
		EMEventListener {

	private SurfaceView localSurface;
	private SurfaceHolder localSurfaceHolder;
	private static SurfaceView oppositeSurface;
	private SurfaceHolder oppositeSurfaceHolder;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	private boolean isAnswered;
	private int streamID;
	private boolean endCallTriggerByMe = false;
	private boolean monitor = true;

	EMVideoCallHelper callHelper;
	private TextView callStateTextView;

	private Handler handler = new Handler();
	private LinearLayout comingBtnContainer;
	private Button refuseBtn;
	private Button answerBtn;
	private ImageView hangupBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;
	private Chronometer chronometer;
	private LinearLayout voiceContronlLayout;
	private RelativeLayout btnsContainer;
	private CameraHelper cameraHelper;
	private LinearLayout topContainer;
	private LinearLayout bottomContainer;
	@ViewInject(R.id.btn_video_beg)
	private View begView;
	@ViewInject(R.id.btn_video_send)
	private View sendView;
	@ViewInject(R.id.layout_videocall_userinfo)
	private View userInfoView;
	@ViewInject(R.id.txt_videocall_name)
	private TextView nameView;
	@ViewInject(R.id.image_videocall_avatar)
	private ImageView avatarView;
	@ViewInject(R.id.iv_switch)
	private ImageView switchView;
	@ViewInject(R.id.root_layout)
	private View rootLayout;
	private CallState state = CallState.RINGING;
	private boolean isShow = true;
	private static final int REQUEST_GIFT = 1100;
	private static final int REQUEST_SENDGIFT = 1101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			finish();
			return;
		}
		setContentView(R.layout.em_activity_video_call);

		ViewUtils.inject(this);
		 sendView.setVisibility(View.GONE);
         begView.setVisibility(View.GONE);
		DemoHelper.getInstance().isVideoCalling = true;
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		rootLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch(state){
				case ACCEPTED:{
					isShow = !isShow;
					if(isShow){
						hangupBtn.setVisibility(View.VISIBLE);
						muteImage.setVisibility(View.VISIBLE);
						handsFreeImage.setVisibility(View.VISIBLE);
						switchView.setVisibility(View.VISIBLE);
					}else{
						hangupBtn.setVisibility(View.INVISIBLE);
						muteImage.setVisibility(View.INVISIBLE);
						handsFreeImage.setVisibility(View.INVISIBLE);
						switchView.setVisibility(View.INVISIBLE);
					}
					break;
				}
				default:{
					isShow = !isShow;
					if(!isInComingCall){
						if(isShow){
							hangupBtn.setVisibility(View.VISIBLE);
						}else{
							hangupBtn.setVisibility(View.INVISIBLE);
						}	
					}else{
						if(isShow){
							refuseBtn.setVisibility(View.VISIBLE);
							answerBtn.setVisibility(View.VISIBLE);
						}else{
							refuseBtn.setVisibility(View.INVISIBLE);
							answerBtn.setVisibility(View.INVISIBLE);
						}	
					}
					break;
				}
				}
			}
		});
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
		comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
		answerBtn = (Button) findViewById(R.id.btn_answer_call);
		hangupBtn = (ImageView) findViewById(R.id.iv_close);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
		btnsContainer = (RelativeLayout) findViewById(R.id.ll_btns);
		topContainer = (LinearLayout) findViewById(R.id.ll_top_container);
		bottomContainer = (LinearLayout) findViewById(R.id.ll_bottom_container);
		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);
		begView.setOnClickListener(this);
		sendView.setOnClickListener(this);
		switchView.setOnClickListener(this);
		muteImage.setVisibility(View.INVISIBLE);
		handsFreeImage.setVisibility(View.INVISIBLE);
		switchView.setVisibility(View.INVISIBLE);

		msgid = UUID.randomUUID().toString();
		// 获取通话是否为接收方向的
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
		username = getIntent().getStringExtra("username");
		FriendItemEntity entity = FriendsManager.getInstance().getFrinedInfo(username);
		if(null != entity){
    		nameView.setText(entity.getFans().getAlias());
    		ImageUtils.loadAvatarDefaultImage(entity.getFans().getAvatar(), avatarView);
    	}else{
    		if("ppstar".equals(username)){
    			nameView.setText(getString(R.string.app_kefu));
    		}else{
    			nameView.setText(username);
    		}
    	}
		// 显示本地图像的surfaceview
		localSurface = (SurfaceView) findViewById(R.id.local_surface);
		localSurface.setZOrderMediaOverlay(true);
		localSurface.setZOrderOnTop(true);
		localSurfaceHolder = localSurface.getHolder();

		// 获取callHelper,cameraHelper
		callHelper = EMVideoCallHelper.getInstance();
		cameraHelper = new CameraHelper(callHelper, localSurfaceHolder);
		// 显示对方图像的surfaceview
		oppositeSurface = (SurfaceView) findViewById(R.id.opposite_surface);
		oppositeSurfaceHolder = oppositeSurface.getHolder();
		// 设置显示对方图像的surfaceview
		callHelper.setSurfaceView(oppositeSurface);
		oppositeSurface.setVisibility(View.INVISIBLE);
		localSurfaceHolder.addCallback(new LocalCallback());
		oppositeSurfaceHolder.addCallback(new OppositeCallback());

		// 设置通话监听
		addCallStateListener();
		if (!isInComingCall) {// 拨打电话
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			String st = getResources().getString(
					R.string.Are_connected_to_each_other);
			callStateTextView.setText(st);

			handler.postDelayed(new Runnable() {
				public void run() {
					streamID = playMakeCallSounds();
				}
			}, 300);
		} else { // 有电话进来
			voiceContronlLayout.setVisibility(View.INVISIBLE);
//			localSurface.setVisibility(View.INVISIBLE);
			Uri ringUri = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}
		EMChatManager.getInstance().registerEventListener(
				this,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventOfflineMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck,EMNotifierEvent.Event.EventNewCMDMessage});
		
		checkPrompt();
		if (!cameraHelper.isStarted()) {
			if (!isInComingCall) {
				try {
					// 拨打视频通话
					EMChatManager.getInstance().makeVideoCall(username);
					// 通知cameraHelper可以写入数据
					cameraHelper.setStartFlag(true);
				} catch (EMServiceNotReadyException e) {
					Toast.makeText(VideoCallActivity.this,
							R.string.Is_not_yet_connected_to_the_server, Toast.LENGTH_SHORT)
							.show();
				}
			}

		} else {
			cameraHelper.setStartFlag(true);
		}
	}

	private void checkPrompt(){
//		if(UserUtils.getUserInfo().getUser().getGender() == 1){
//			SharedPreferences sp = getSharedPreferences("FEMALE", MODE_PRIVATE);
//			String ss = sp.getString(UserUtils.getUserid(), "false");
//			if(ss.equals("false")){
//				Editor edit = sp.edit();
//				edit.putString(UserUtils.getUserid(), "true");
//				edit.commit();
				Intent intent = new Intent(this,PromptActivity.class);
				startActivity(intent);
//			}
//		}
	}
	
	/**
	 * 本地SurfaceHolder callback
	 * 
	 */
	class LocalCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			cameraHelper.startFrontCapture();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}
	
	/**
	 * 对方SurfaceHolder callback
	 */
	class OppositeCallback implements SurfaceHolder.Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
			callHelper.setRenderFlag(true);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			callHelper.onWindowResize(width, height, format);
//			if (!cameraHelper.isStarted()) {
//				if (!isInComingCall) {
//						// 拨打视频通话
//						// 通知cameraHelper可以写入数据
//						cameraHelper.setStartFlag(true);
//				}
//
//			} else {
//			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			callHelper.setRenderFlag(false);
		}

	}

	/**
	 * 设置通话状态监听
	 */
	void addCallStateListener() {
		callStateListener = new EMCallStateChangeListener() {

			@Override
			public void onCallStateChanged(CallState callState, CallError error) {
				// Message msg = handler.obtainMessage();
				state = callState;
				switch (callState) {

				case CONNECTING: // 正在连接对方
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							callStateTextView
									.setText(R.string.Are_connected_to_each_other);
						}

					});
					break;
				case CONNECTED: // 双方已经建立连接
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							oppositeSurface.setVisibility(View.VISIBLE);
							callStateTextView
									.setText(R.string.have_connected_with);
						}

					});
					break;

				case ACCEPTED: // 电话接通成功
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							try {
								if (soundPool != null)
									soundPool.stop(streamID);
							} catch (Exception e) {
							}
							muteImage.setVisibility(View.VISIBLE);
							handsFreeImage.setVisibility(View.VISIBLE);
							switchView.setVisibility(View.VISIBLE);
							userInfoView.setVisibility(View.GONE);
							 sendView.setVisibility(View.VISIBLE);
	                         begView.setVisibility(View.VISIBLE);
							oppositeSurface.setVisibility(View.VISIBLE);
							openSpeakerOn();
							((TextView) findViewById(R.id.tv_is_p2p))
									.setText(EMChatManager.getInstance()
											.isDirectCall() ? R.string.direct_call
											: R.string.relay_call);
							handsFreeImage
									.setImageResource(R.drawable.video_sound_hl);
							isHandsfreeState = true;
							chronometer.setVisibility(View.VISIBLE);
							chronometer.setBase(SystemClock.elapsedRealtime());
							// 开始记时
							chronometer.start();
							callStateTextView.setText(R.string.In_the_call);
							callingState = CallingState.NORMAL;
							startMonitor();
							cameraHelper.startFrontCapture();
							cameraHelper.setStartFlag(true);
							isShow = true;
							hangupBtn.setVisibility(View.VISIBLE);
							muteImage.setVisibility(View.VISIBLE);
							handsFreeImage.setVisibility(View.VISIBLE);
							switchView.setVisibility(View.VISIBLE);
						}

					});
					break;
				case DISCONNNECTED: // 电话断了
					final CallError fError = error;
					runOnUiThread(new Runnable() {
						private void postDelayedCloseMsg() {
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									saveCallRecord(1);
									finish();
								}

							}, 200);
						}

						@Override
						public void run() {
							chronometer.stop();
							callDruationText = chronometer.getText().toString();
							String s1 = getResources().getString(
									R.string.The_other_party_refused_to_accept);
							String s2 = getResources().getString(
									R.string.Connection_failure);
							String s3 = getResources().getString(
									R.string.The_other_party_is_not_online);
							String s4 = getResources().getString(
									R.string.The_other_is_on_the_phone_please);
							String s5 = getResources().getString(
									R.string.The_other_party_did_not_answer);

							String s6 = getResources().getString(
									R.string.hang_up);
							String s7 = getResources().getString(
									R.string.The_other_is_hang_up);
							String s8 = getResources().getString(
									R.string.did_not_answer);
							String s9 = getResources().getString(
									R.string.Has_been_cancelled);

							if (fError == CallError.REJECTED) {
								callingState = CallingState.BEREFUESD;
								callStateTextView.setText(s1);
							} else if (fError == CallError.ERROR_TRANSPORT) {
								callStateTextView.setText(s2);
							} else if (fError == CallError.ERROR_INAVAILABLE) {
								callingState = CallingState.OFFLINE;
								callStateTextView.setText(s3);
							} else if (fError == CallError.ERROR_BUSY) {
								callingState = CallingState.BUSY;
								callStateTextView.setText(s4);
							} else if (fError == CallError.ERROR_NORESPONSE) {
								callingState = CallingState.NORESPONSE;
								callStateTextView.setText(s5);
							} else {
								if (isAnswered) {
									callingState = CallingState.NORMAL;
									if (endCallTriggerByMe) {
										// callStateTextView.setText(s6);
									} else {
										callStateTextView.setText(s7);
									}
								} else {
									if (isInComingCall) {
										callingState = CallingState.UNANSWERED;
										callStateTextView.setText(s8);
									} else {
										if (callingState != CallingState.NORMAL) {
											callingState = CallingState.CANCED;
											callStateTextView.setText(s9);
										} else {
											callStateTextView.setText(s6);
										}
									}
								}
							}
							postDelayedCloseMsg();
						}

					});

					break;

				default:
					break;
				}

			}
		};
		EMChatManager.getInstance().addCallStateChangeListener(
				callStateListener);
	}

	private void jumpToGiftPage(final boolean isSend) {
		GetCapitalRequest request = new GetCapitalRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<Capital>() {

			@Override
			public void onSuccess(Capital data) {
				hideLoading();
				if (isSend) {
					Intent intent = new Intent(VideoCallActivity.this,
							GiftActivity.class);
					intent.putExtra(GiftActivity.CAPITAL_ENTITY,
							GsonUtils.objToJson(data));
					intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.common_sendgift);
					startActivityForResult(intent, REQUEST_SENDGIFT);
				} else {
					Intent intent = new Intent(VideoCallActivity.this,
							GiftActivity.class);
					intent.putExtra(GiftActivity.CAPITAL_ENTITY,
							GsonUtils.objToJson(data));
					intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
					intent.putExtra(GiftActivity.GIFT_TIPS, R.string.common_beggift);
					startActivityForResult(intent, REQUEST_GIFT);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_video_beg: {
			jumpToGiftPage(false);
			break;
		}
		case R.id.btn_video_send: {
			jumpToGiftPage(true);
			break;
		}
		case R.id.btn_refuse_call: // 拒绝接听
			refuseBtn.setEnabled(false);
			if (ringtone != null)
				ringtone.stop();
			try {
				EMChatManager.getInstance().rejectCall();
			} catch (Exception e1) {
				e1.printStackTrace();
				saveCallRecord(1);
				finish();
			}
			callingState = CallingState.REFUESD;
			break;

		case R.id.btn_answer_call: // 接听电话
			answerBtn.setEnabled(false);
			if (ringtone != null)
				ringtone.stop();
			if (isInComingCall) {
				try {
					callStateTextView.setText("正在接听...");
					EMChatManager.getInstance().answerCall();
					cameraHelper.setStartFlag(true);

					openSpeakerOn();
					handsFreeImage
							.setImageResource(R.drawable.video_sound_hl);
					isAnswered = true;
					isHandsfreeState = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					saveCallRecord(1);
					finish();
					return;
				}
			}
			comingBtnContainer.setVisibility(View.INVISIBLE);
			hangupBtn.setVisibility(View.VISIBLE);
			voiceContronlLayout.setVisibility(View.VISIBLE);
			localSurface.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_switch:{
			cameraHelper.switchCamera();
			break;
		}
		case R.id.iv_close: // 挂断电话
			hangupBtn.setEnabled(false);
			if (soundPool != null)
				soundPool.stop(streamID);
			chronometer.stop();
			endCallTriggerByMe = true;
			callStateTextView.setText(getResources().getString(
					R.string.hanging_up));
			try {
				EMChatManager.getInstance().endCall();
			} catch (Exception e) {
				e.printStackTrace();
				saveCallRecord(1);
				finish();
			}
			break;

		case R.id.iv_mute: // 静音开关
			if (isMuteState) {
				// 关闭静音
				muteImage.setImageResource(R.drawable.video_mute);
				audioManager.setMicrophoneMute(false);
				isMuteState = false;
			} else {
				// 打开静音
				muteImage.setImageResource(R.drawable.video_mute_hl);
				audioManager.setMicrophoneMute(true);
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree: // 免提开关
			if (isHandsfreeState) {
				// 关闭免提
				handsFreeImage
						.setImageResource(R.drawable.video_sound);
				closeSpeakerOn();
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.video_sound_hl);
				openSpeakerOn();
				isHandsfreeState = true;
			}
			break;
		case R.id.root_layout:
			if (callingState == CallingState.NORMAL) {
				if (bottomContainer.getVisibility() == View.VISIBLE) {
					bottomContainer.setVisibility(View.GONE);
					topContainer.setVisibility(View.GONE);

				} else {
					bottomContainer.setVisibility(View.VISIBLE);
					topContainer.setVisibility(View.VISIBLE);

				}
			}

			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		DemoHelper.getInstance().isVideoCalling = false;
		EMChatManager.getInstance().unregisterEventListener(this);
		stopMonitor();
		try {
			callHelper.setSurfaceView(null);
			cameraHelper.stopCapture();
			oppositeSurface = null;
			cameraHelper = null;
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		EMChatManager.getInstance().endCall();
		callDruationText = chronometer.getText().toString();
		saveCallRecord(1);
		finish();
	}

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(request, result, data);
		if (result == RESULT_OK) {
			switch (request) {
			case REQUEST_SENDGIFT:{
				String ss = data.getStringExtra(GiftActivity.GIFT_ENTITY);
				final GiftItemEntity entity = GsonUtils.jsonToObj(ss,
						GiftItemEntity.class);
				showLoading();
				FriendsManager.getInstance().getFrinedInfo(username, new OnFriendsListener() {
					
					@Override
					public void onLoadFrinedsInfo(String hxid, FriendItemEntity userInfo) {
						hideLoading();
						Intent intent = new Intent(VideoCallActivity.this,GiftConfirmActivity.class);
						intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, GsonUtils.objToJson(entity));
						intent.putExtra(GiftConfirmActivity.USER_ID, userInfo.getFans().getId());
						intent.putExtra(GiftConfirmActivity.GIFT_FROM, 2);
						startActivity(intent);
					}
					
					@Override
					public void onLoadFrinedsFailed(String hxid, int code, String reason) {
						hideLoading();
						showToast(reason);
					}
				});
				break;
			}
			case REQUEST_GIFT: {
				String ss = data.getStringExtra(GiftActivity.GIFT_ENTITY);
				final GiftItemEntity entity = GsonUtils.jsonToObj(ss,
						GiftItemEntity.class);
				showLoading();
				FriendsManager.getInstance().getFrinedInfo(username,
						new OnFriendsListener() {

							@Override
							public void onLoadFrinedsInfo(String hxid,
									FriendItemEntity userInfo) {
								hideLoading();
								SendGiftItemEntity sendEntity = new SendGiftItemEntity();
								sendEntity.setTo(UserUtils.getUserInfo()
										.getUser());
								sendEntity.setFrom(userInfo.getFans());
								sendEntity.setGift(entity);
								VideoGiftDialog dlg = new VideoGiftDialog(
										VideoCallActivity.this);
								dlg.setMode(VideoGiftDialogMode.VideoGiftDialogModeBeg);
								dlg.setGiftEntity(sendEntity);
								dlg.setGiftDialogListener(new ViewGiftDialogListener() {

									@Override
									public void onClickSend(
											SendGiftItemEntity entity) {

									}

									@Override
									public void onClickCancel() {
									}

									@Override
									public void onClickBeg(
											SendGiftItemEntity entity) {
										doBeg(entity);
									}
								});
								dlg.show();
							}

							@Override
							public void onLoadFrinedsFailed(String hxid,
									int code, String reason) {
								hideLoading();
								showToast(reason);
							}
						});
				break;
			}
			}
		}
	}

	private void doBeg(SendGiftItemEntity giftEntity) {
		BegGiftRequest request = new BegGiftRequest();
		request.setFrom_id(giftEntity.getTo().getId());
		request.setTo_id(giftEntity.getFrom().getId());
		request.setGift_id(giftEntity.getGift().getId());
		GiftItemEntity gift = giftEntity.getGift();
		request.setName(gift.getName());
		request.setUri(gift.getUrl());
		request.setValue(gift.getPrice());
		request.setNetworkListener(new CommonNetworkCallback<String>() {

			@Override
			public void onSuccess(String data) {
				hideLoading();
				showToast("已成功向对方索要礼物，请等待对方回应");
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

	/**
	 * 方便开发测试，实际app中去掉显示即可
	 */
	void startMonitor() {
		 new Thread(new Runnable() {
		 public void run() {
		 while(monitor){
		 runOnUiThread(new Runnable() {
		 public void run() {
		 Log.e("wuhao","宽x高："+callHelper.getVideoWidth()+"x"+callHelper.getVideoHeight()
		 + "\n延迟：" + callHelper.getVideoTimedelay()
		 + "\n帧率：" + callHelper.getVideoFramerate()
		 + "\n丢包数：" + callHelper.getVideoLostcnt()
		 + "\n本地比特率：" + callHelper.getLocalBitrate()
		 + "\n对方比特率：" + callHelper.getRemoteBitrate());
		 }
		 });
		 try {
		 Thread.sleep(1500);
		 } catch (InterruptedException e) {
		 }
		 }
		 }
		 }).start();
	}

	void stopMonitor() {
		monitor = false;
	}

	@Override
	public void onEvent(EMNotifierEvent event) {
		switch (event.getEvent()) {
		case EventNewCMDMessage: {
			// 获取到message
			EMMessage message = (EMMessage) event.getData();
			TransMsgHelper.handleTransMessage(this, message);
			break;
		}
		case EventNewMessage:
			// 获取到message
			EMMessage message = (EMMessage) event.getData();

			String username = null;
			// 群组消息
			if (message.getChatType() == ChatType.GroupChat
					|| message.getChatType() == ChatType.ChatRoom) {
				username = message.getTo();
			} else {
				// 单聊消息
				username = message.getFrom();
			}

			// 如果是当前会话的消息，刷新聊天页面
			// if (username.equals(toChatUsername)) {
			// messageList.refreshSelectLast();
			// // 声音和震动提示有新消息
			EaseUI.getInstance().getNotifier().viberateAndPlayTone(message);
			// } else {
			// // 如果消息不是和当前聊天ID的消息
			// EaseUI.getInstance().getNotifier().onNewMsg(message);
			// }

			break;
		case EventDeliveryAck:
		case EventReadAck:
			// 获取到message
			// messageList.refresh();
			break;
		case EventOfflineMessage:
			// a list of offline messages
			// List<EMMessage> offlineMessages = (List<EMMessage>)
			// event.getData();
			// messageList.refresh();
			break;
		default:
			break;
		}
	}

}
