/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intexh.bidong.R;
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
import com.easemob.chat.EMCallStateChangeListener.CallState;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.exceptions.EMServiceNotReadyException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

/**
 * 语音通话页面
 *
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener, EMEventListener {
    private LinearLayout comingBtnContainer;
    @ViewInject(R.id.btn_answer_hangup)
    private Button hangupBtn;
    private ImageView refuseBtn;
    private Button answerBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;

    private boolean isMuteState;
    private boolean isHandsfreeState;

    private TextView callStateTextView;
    private int streamID;
    private boolean endCallTriggerByMe = false;
    private Handler handler = new Handler();
    private TextView nickTextView;
    private TextView durationTextView;
    private Chronometer chronometer;
    String st1;
    private boolean isAnswered;
    private LinearLayout voiceContronlLayout;
    @ViewInject(R.id.btn_voice_beg)
    private View begView;
    @ViewInject(R.id.btn_voice_send)
    private View sendView;
    @ViewInject(R.id.swing_card)
    private ImageView avatarView;
    @ViewInject(R.id.root_layout)
    private View rootView;
    private boolean isShow = true;
    private static final int REQUEST_GIFT = 1100;
    private static final int REQUEST_SENDGIFT = 1101;
    private CallState state = CallState.RINGING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.em_activity_voice_call);
        ViewUtils.inject(this);
        rootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                switch (state) {
                    case ACCEPTED: {
                        isShow = !isShow;
                        if (isShow) {
                            refuseBtn.setVisibility(View.VISIBLE);
                            muteImage.setVisibility(View.VISIBLE);
                            handsFreeImage.setVisibility(View.VISIBLE);
                        } else {
                            refuseBtn.setVisibility(View.INVISIBLE);
                            muteImage.setVisibility(View.INVISIBLE);
                            handsFreeImage.setVisibility(View.INVISIBLE);
                        }
                        break;
                    }
                    default: {
                        isShow = !isShow;
                        if (!isInComingCall) {
                            if (isShow) {
                                refuseBtn.setVisibility(View.VISIBLE);
                            } else {
                                refuseBtn.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (isShow) {
                                answerBtn.setVisibility(View.VISIBLE);
                                hangupBtn.setVisibility(View.VISIBLE);
                            } else {
                                answerBtn.setVisibility(View.INVISIBLE);
                                hangupBtn.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    }
                }
            }
        });
        DemoHelper.getInstance().isVoiceCalling = true;
        sendView.setVisibility(View.GONE);
        begView.setVisibility(View.GONE);
        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        refuseBtn = (ImageView) findViewById(R.id.iv_close);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
//		hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);

        muteImage.setVisibility(View.INVISIBLE);
        handsFreeImage.setVisibility(View.INVISIBLE);
        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        begView.setOnClickListener(this);
        sendView.setOnClickListener(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        // 注册语音电话的状态的监听
        addCallStateListener();
        msgid = UUID.randomUUID().toString();

        username = getIntent().getStringExtra("username");
        // 语音电话是否为接收的
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        // 设置通话人
        FriendItemEntity entity = FriendsManager.getInstance().getFrinedInfo(username);
        if (null != entity) {
            nickTextView.setText(entity.getFans().getAlias());
            ImageUtils.loadAvatarDefaultImage(entity.getFans().getAvatar(), avatarView);
        } else {
            if ("ppstar".equals(username)) {
                nickTextView.setText(getString(R.string.app_kefu));
            } else {
                nickTextView.setText(username);
            }
        }
        if (!isInComingCall) {// 拨打电话
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
//			hangupBtn.setVisibility(View.VISIBLE);
            st1 = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st1);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
            try {
                // 拨打语音电话
                EMChatManager.getInstance().makeVoiceCall(username);
            } catch (EMServiceNotReadyException e) {
                e.printStackTrace();
                final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(VoiceCallActivity.this, st2, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else { // 有电话进来
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
        }
        EMChatManager.getInstance().registerEventListener(
                this,
                new EMNotifierEvent.Event[]{
                        EMNotifierEvent.Event.EventNewMessage,
                        EMNotifierEvent.Event.EventOfflineMessage,
                        EMNotifierEvent.Event.EventDeliveryAck,
                        EMNotifierEvent.Event.EventReadAck, EMNotifierEvent.Event.EventNewCMDMessage});
        checkPrompt();
    }

    private void checkPrompt() {
        if (UserUtils.getUserInfo().getUser().getGender() == 1) {
//			SharedPreferences sp = getSharedPreferences("FEMALE", MODE_PRIVATE);
//			String ss = sp.getString(UserUtils.getUserid(), "false");
//			if(ss.equals("false")){
//				Editor edit = sp.edit();
//				edit.putString(UserUtils.getUserid(), "true");
//				edit.commit();
            Intent intent = new Intent(this, PromptActivity.class);
            startActivity(intent);
//			}
        }
    }

    /**
     * 设置电话监听
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
                                // TODO Auto-generated method stub
                                callStateTextView.setText(st1);
                            }

                        });
                        break;
                    case CONNECTED: // 双方已经建立连接
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String st3 = getResources().getString(R.string.have_connected_with);
                                callStateTextView.setText(st3);
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
                                sendView.setVisibility(View.VISIBLE);
                                begView.setVisibility(View.VISIBLE);
                                if (!isHandsfreeState)
                                    closeSpeakerOn();
                                //显示是否为直连，方便测试
                                ((TextView) findViewById(R.id.tv_is_p2p)).setText(EMChatManager.getInstance().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                // 开始记时
                                chronometer.start();
                                String str4 = getResources().getString(R.string.In_the_call);
                                callStateTextView.setText(str4);
                                callingState = CallingState.NORMAL;
                                refuseBtn.setVisibility(View.VISIBLE);
                                muteImage.setVisibility(View.VISIBLE);
                                handsFreeImage.setVisibility(View.VISIBLE);
                                isShow = true;
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
                                        saveCallRecord(0);
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(800);
                                        findViewById(R.id.root_layout).startAnimation(animation);
                                        finish();
                                    }

                                }, 200);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String st3 = getResources().getString(R.string.Connection_failure);
                                String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                                String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);

                                String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                                String st7 = getResources().getString(R.string.hang_up);
                                String st8 = getResources().getString(R.string.The_other_is_hang_up);

                                String st9 = getResources().getString(R.string.did_not_answer);
                                String st10 = getResources().getString(R.string.Has_been_cancelled);
                                String st11 = getResources().getString(R.string.hang_up);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUESD;
                                    callStateTextView.setText(st2);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(st3);
                                } else if (fError == CallError.ERROR_INAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(st4);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(st5);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NORESPONSE;
                                    callStateTextView.setText(st6);
                                } else {
                                    if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                        } else {
                                            callStateTextView.setText(st8);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(st9);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCED;
                                                callStateTextView.setText(st10);
                                            } else {
                                                callStateTextView.setText(st11);
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
        EMChatManager.getInstance().addCallStateChangeListener(callStateListener);
    }

    private void jumpToGiftPage(final boolean isSend) {
        GetCapitalRequest request = new GetCapitalRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<Capital>() {

            @Override
            public void onSuccess(Capital data) {
                hideLoading();
                if (isSend) {
                    Intent intent = new Intent(VoiceCallActivity.this,
                            GiftActivity.class);
                    intent.putExtra(GiftActivity.CAPITAL_ENTITY,
                            GsonUtils.objToJson(data));
                    intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.common_sendgift);
                    startActivityForResult(intent, REQUEST_SENDGIFT);
                } else {
                    Intent intent = new Intent(VoiceCallActivity.this,
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
            case R.id.btn_voice_beg: {
                jumpToGiftPage(false);
                break;
            }
            case R.id.btn_answer_hangup: {
                refuseBtn.setEnabled(false);
                if (ringtone != null)
                    ringtone.stop();
                try {
                    EMChatManager.getInstance().rejectCall();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    saveCallRecord(0);
                    finish();
                }
                callingState = CallingState.REFUESD;
                break;
            }
            case R.id.btn_voice_send: {
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
                    saveCallRecord(0);
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
                        isAnswered = true;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        saveCallRecord(0);
                        finish();
                        return;
                    }
                }
                comingBtnContainer.setVisibility(View.INVISIBLE);
//            hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
                closeSpeakerOn();
                break;

            case R.id.iv_close: // 挂断电话
//		    hangupBtn.setEnabled(false);
                if (soundPool != null)
                    soundPool.stop(streamID);
                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                try {
                    EMChatManager.getInstance().endCall();
                } catch (Exception e) {
                    e.printStackTrace();
                    saveCallRecord(0);
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
                    handsFreeImage.setImageResource(R.drawable.video_sound);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    handsFreeImage.setImageResource(R.drawable.video_sound_hl);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoHelper.getInstance().isVoiceCalling = false;
        EMChatManager.getInstance().unregisterEventListener(this);
    }

    @Override
    public void onBackPressed() {
        EMChatManager.getInstance().endCall();
        callDruationText = chronometer.getText().toString();
        saveCallRecord(0);
        finish();
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


    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            switch (request) {
                case REQUEST_SENDGIFT: {
                    String ss = data.getStringExtra(GiftActivity.GIFT_ENTITY);
                    final GiftItemEntity entity = GsonUtils.jsonToObj(ss,
                            GiftItemEntity.class);
                    showLoading();
                    FriendsManager.getInstance().getFrinedInfo(username, new OnFriendsListener() {

                        @Override
                        public void onLoadFrinedsInfo(String hxid, FriendItemEntity userInfo) {
                            hideLoading();
                            Intent intent = new Intent(VoiceCallActivity.this, GiftConfirmActivity.class);
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
                                            VoiceCallActivity.this);
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

}
