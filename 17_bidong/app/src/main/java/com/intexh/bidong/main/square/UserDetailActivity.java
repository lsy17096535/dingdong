package com.intexh.bidong.main.square;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.easemob.easeui.utils.Settings;
import com.intexh.bidong.gift.GiftCheckUtils;
import com.intexh.bidong.utils.DateUtil;
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
import com.intexh.bidong.common.CommonInputActivity;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.gift.GiftConfirmActivity;
import com.intexh.bidong.me.GetModelInfoRequest;
import com.intexh.bidong.me.MeFragment;
import com.intexh.bidong.me.MyTrendVideoActivity;
import com.intexh.bidong.me.ReportUserRequest;
import com.intexh.bidong.me.model.ModelDetailActivity;
import com.intexh.bidong.reg.RegVideoActivity;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.BadgeItemEntity;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.ModelInfoEntity;
import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.userentity.VideoItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.StringUtil;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class UserDetailActivity extends BaseActivity implements OnClickListener {
    public static final String USER_ENTITY = "USER_ENTITY";
    private static final int REQUEST_GIFT = 1000;

    @ViewInject(R.id.image_userdetail_bigavatar)
    private ImageView bigAvatarView;
    @ViewInject(R.id.image_userdetail_back)
    private ImageView backIcon;
    @ViewInject(R.id.image_userdetail_more)
    private ImageView moreIcon;
    @ViewInject(R.id.image_userdetail_play)
    private ImageView playIcon;
    @ViewInject(R.id.list_userdetail_trends)
    private ListView videoListView;
    @ViewInject(R.id.list_userdetail_offservice)
    private ListView offsListView;
    @ViewInject(R.id.image_userdetail_avatar)
    private ImageView avatarView;
    @ViewInject(R.id.txt_userdetail_username)
    private TextView usernameView;
    @ViewInject(R.id.txt_userdetail_sign)
    private TextView signView;
    @ViewInject(R.id.txt_userdetail_addressinfo)
    private TextView addressInfoTxt;
    @ViewInject(R.id.txt_userdetail_age)
    private TextView ageTextView;
    @ViewInject(R.id.txt_userdetail_height)
    private TextView heightView;
    @ViewInject(R.id.txt_userdetail_weight)
    private TextView weightView;
    @ViewInject(R.id.txt_userdetail_career)
    private TextView careerView;
    @ViewInject(R.id.layout_userdetail_badge_container)
    private LinearLayout badgeContainer;
    @ViewInject(R.id.image_userdetail_vip)
    private ImageView vipView;
    @ViewInject(R.id.layout_userdetail_video)
    private View moreVideoView;
    @ViewInject(R.id.image_userdetail_model)
    private ImageView modelFlagView;
    @ViewInject(R.id.scroll_userdetail_container)
    private ScrollView scrollContainer;
    @ViewInject(R.id.layout_userdetail_offservice)
    private View offsContainer;

    @ViewInject(R.id.layout_userdetail_contact)
    private LinearLayout contactLayout;
    @ViewInject(R.id.txt_userdetail_contact)
    private TextView contactView;
    @ViewInject(R.id.layout_userdetail_like)
    private LinearLayout likeLayout;

    private SimpleVideoListAdapter trendAdapter = null;
    private UserOffServiceAdapter offAdapter = null;
    private List<VideoItemEntity> videos = new ArrayList<VideoItemEntity>();
    private List<OffServiceEntity> offdatas = new ArrayList<OffServiceEntity>();
    private User userEntity = null;
    private int screenWidth = 0;
    private static final int REQUEST_REPORT = 100;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_userdetail);
        ViewUtils.inject(this);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;;

        String ss = getIntent().getStringExtra(USER_ENTITY);
        if (!StringUtil.isEmptyString(ss)) {
            userEntity = GsonUtils.jsonToObj(ss, User.class);
        }

//        if(null != userEntity.getVideo_id()){
//            playIcon.setVisibility(View.VISIBLE);
//        }
        playIcon.setOnClickListener(this);
        backIcon.setOnClickListener(this);
        moreIcon.setOnClickListener(this);
        avatarView.setOnClickListener(this);
        if (UserUtils.getUserInfo().getUser().getGender() == 1) {// 女
            contactView.setText("与他互动");
        } else {
            contactView.setText("与她互动");
        }
        contactLayout.setOnClickListener(this); //与她互动事件
        likeLayout.setOnClickListener(this); //喜欢事件

        bigAvatarView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, screenWidth));
        ImageUtils.loadBigAvatarImage(userEntity.getAvatar(), bigAvatarView);
        ImageUtils.loadAvatarDefaultImage(userEntity.getAvatar(), avatarView);
        String username = userEntity.getAlias();
        if (null == username) {
            username = "";
        }
        ss = " ";
        if (null != userEntity.getHeight()) {
            ss += userEntity.getHeight();
        }
        ss += "cm ";
        heightView.setText(ss);
        ss = " ";
        if (null != userEntity.getWeight()) {
            ss += userEntity.getWeight();
        }
        ss += "kg ";
        weightView.setText(ss);

        int genderColorId = R.color.text_bg_color_red; //女色
        int genderFlag = R.string.femal_flag;
        if(2 == userEntity.getGender()){  //男
            genderColorId = R.color.text_bg_color_blue;
            genderFlag = R.string.male_flag;
        }
        ageTextView.setBackgroundColor(getResources().getColor(genderColorId));
        ageTextView.setText(getString(genderFlag) + userEntity.getAge() + " ");

        usernameView.setText(username);
        signView.setText(userEntity.getSignature());
        ss = "";
        if (null != userEntity.getCity()) {
            ss += userEntity.getCity();
        }
        if (null != userEntity.getDistrict()) {
            ss += userEntity.getDistrict();
        }
        addressInfoTxt.setText(ss);
        ss = " ";
        if (null != userEntity.getOccup()) {
            ss += userEntity.getOccup() + " ";
            careerView.setText(ss);
        } else {
            careerView.setText("");
        }

        trendAdapter = new SimpleVideoListAdapter(this, screenWidth);
        trendAdapter.setDatas(videos);
        videoListView.setAdapter(trendAdapter);
        trendAdapter.setItemListener(new SimpleVideoListAdapter.OnItemListener() {
            @Override
            public void onClickItem(final VideoItemEntity entity) {
                TrendVideoEntity hotEntity = new TrendVideoEntity();
                hotEntity.setComm_count(entity.getComm_count());
                hotEntity.setId(entity.getId());
                hotEntity.setPlay_count(entity.getPlay_count());
                hotEntity.setRemark(entity.getRemark());
                hotEntity.setSnapshort(entity.getSnapshort());
                hotEntity.setVideo(entity.getVideo());
                hotEntity.setPhotos(entity.getPhotos());
                hotEntity.setCreated_at(entity.getCreated_at());
                hotEntity.setUpdated_at(entity.getUpdated_at());
                hotEntity.setUser(userEntity);
                if(null != entity.getPhotos() && entity.getPhotos().length > 0){
                    Intent intent = new Intent(UserDetailActivity.this, TrendDetailActivity.class);
                    intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(hotEntity));
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(UserDetailActivity.this, VideoDetailActivity.class);
                    intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(hotEntity));
                    startActivity(intent);
                }
            }
        });

        moreVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UserDetailActivity.this, MyTrendVideoActivity.class);
                intent.putExtra(MyTrendVideoActivity.USER_ENTITY, GsonUtils.objToJson(userEntity));
                startActivity(intent);
            }
        });

        offAdapter = new UserOffServiceAdapter();
        offAdapter.setDatas(offdatas);
        offAdapter.setItemListener(new UserOffServiceAdapter.OnItemListener() {
            @Override
            public void onClickItem(final OffServiceEntity entity) {
//                if(!UserUtils.isVideoAudit()){
//                    showRecordVideoAlert(UserDetailActivity.this);
//                    return;
//                }
                Intent intent = new Intent(UserDetailActivity.this, OrderSendActivity.class);
                intent.putExtra(OrderSendActivity.SERVICE_ENTITY, GsonUtils.objToJson(entity));
                startActivity(intent);
            }
        });
        offsListView.setAdapter(offAdapter);

        getUserDetail(); //查用户详情
        commitVisitReq(); //发送拜访
    }

    private void showMore() {
        ActionSheet
                .createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(R.string.common_cancel)
                .setOtherButtonTitles("举报用户")
                .setListener(new ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                Intent intent = new Intent(UserDetailActivity.this, CommonInputActivity.class);
                                intent.putExtra(CommonInputActivity.MAX_LENGTH, 64);
                                intent.putExtra(CommonInputActivity.TIPS, "举报内容");
                                intent.putExtra(CommonInputActivity.TITLE, "举报内容");
                                intent.putExtra(CommonInputActivity.VALUE, "");
                                startActivityForResult(intent, REQUEST_REPORT);
                                break;
                        }
                    }

                }).show();
    }

    private void getUserDetail() {
        GetUserDetailRequest request = new GetUserDetailRequest();
        request.setUserid(userEntity.getId());
        request.setGender(userEntity.getGender());
        request.setIs_video(true);
        request.setIs_newapi(true);
        request.setNetworkListener(new CommonNetworkCallback<User>() {

            @Override
            public void onSuccess(User data) {
                hideLoading();
                if (userEntity.is_acting()) {
                    modelFlagView.setVisibility(View.VISIBLE);
                }
                vipView.setImageResource(MeFragment.getLevelResId(data.getLevel(), data.getGender()));
                BadgeItemEntity[] badges = data.getBadges();
                if (null != badges && badges.length > 0) {
                    badgeContainer.setVisibility(View.VISIBLE);
                    int index = 0;
                    for (BadgeItemEntity badge : badges) {
                        ImageView imageView = new ImageView(UserDetailActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                PtrLocalDisplay.designedDP2px(45),
                                PtrLocalDisplay.designedDP2px(45));
                        if (index != badges.length - 1) {
                            params.rightMargin = PtrLocalDisplay.designedDP2px(10);
                        }
                        index++;
                        imageView.setScaleType(ScaleType.FIT_XY);
                        badgeContainer.addView(imageView, params);
                        ImageUtils.loadSnapshotDefaultImage(badge.getBadge_uri(), imageView);
                    }
                }
                //最新动态
                if (null != data.getVideos()) {
                    videos.clear();
                    VideoItemEntity[] userVideos = data.getVideos();
                    int index = 0;
                    for (VideoItemEntity entity : userVideos) {
                        if(index >= 3){
                            break;
                        }
                        videos.add(entity);
                        index++;
                    }
                }
                trendAdapter.notifyDataSetChanged();

                //线下约会
                if (null != data.getActs() && data.getActs().length > 0) {
                    offsContainer.setVisibility(View.VISIBLE); //有服务才显示
                    offdatas.clear();
                    OffServiceEntity[] userOffs = data.getActs();
                    Log.d("USER_DETAIL", String.valueOf(userOffs.length));
                    for (OffServiceEntity entity : userOffs) {
                        offdatas.add(entity);
                    }
                    offAdapter.notifyDataSetChanged();
                }

                scrollContainer.scrollTo(10, 10);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getAccountInfo() {
        GetCapitalRequest request = new GetCapitalRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<Capital>() {

            @Override
            public void onSuccess(Capital data) {
                hideLoading();
                Intent intent = new Intent(UserDetailActivity.this, GiftActivity.class);
                intent.putExtra(GiftActivity.CAPITAL_ENTITY, GsonUtils.objToJson(data));
                intent.putExtra(GiftActivity.SHOW_MODE, GiftGridAdapter.SHOWMODE_VALUE);
                if (1 == UserUtils.getUserInfo().getUser().getGender()) {
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_male_tips);
                } else {
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
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

    private void getCoverVideoInfo() {
        GetVideoRequest request = new GetVideoRequest();
        request.setId(userEntity.getVideo_id());
        request.setNetworkListener(new CommonNetworkCallback<TrendVideoEntity>() {

            @Override
            public void onSuccess(TrendVideoEntity data) {
                if(null != data){
                    data.setUser(userEntity);
                }
                Intent intent = new Intent(UserDetailActivity.this, VideoDetailActivity.class);
                intent.putExtra(VideoDetailActivity.VIDEOENTITY, GsonUtils.objToJson(data));
                startActivity(intent);
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                showToast(reason);
            }
        });
        request.getDataFromServer();
    }

    private void commitVisitReq(){
        CommitVisitRequest request = new CommitVisitRequest();
        request.setUserId(userEntity.getId());
        request.setVisitorId(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<String>() {
            @Override
            public void onSuccess(String data) {}

            @Override
            public void onFailed(int code, HttpException error, String reason) {}
        });
        request.getDataFromServer();
    }

    private void commitLikeReq(){
        CommitLikeRequest request = new CommitLikeRequest();
        request.setUserId(userEntity.getId());
        request.setLikerId(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<String>() {
            @Override
            public void onSuccess(String data) {
                showToast("已发送喜欢");
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                showToast(reason);
            }
        });
        request.getDataFromServer();
    }

    private void openChat(){
        Intent intent = new Intent(UserDetailActivity.this, ChatActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, UserUtils.getHXUserid(userEntity.getId()));
        startActivity(intent);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.layout_userdetail_contact: {
//                if(!UserUtils.isVideoAudit()){
//                    showRecordVideoAlert(this);
//                    return;
//                }
                if (userEntity.getId().equals(UserUtils.getUserid())) {
                    showToast("不能与自己互动");
                    return;
                }
                if(FriendsManager.getInstance().isValidFriend(userEntity.getId())){
                    if(!GiftCheckUtils.checkChatidHex(UserUtils.getHXUserid(userEntity.getId()))){    //没有赠送过礼物 需要赠送
                        getAccountInfo();
                        return;
                    }
                    openChat();
                }
                else{
                    DialogUtils.showDialog(UserDetailActivity.this, "提醒", "严禁传播涉政、黄赌毒等信息，禁止互留其它软件方式，以免上当受骗，一经发现永久封号。仅受理平台内的用户权益！", "知道了", "", new DialogUtils.DialogImpl() {
                        @Override
                        public void onOk() {
                            getAccountInfo();
                        }
                    });

                }
                break;
            }
            case R.id.layout_userdetail_like: {
                if (userEntity.getId().equals(UserUtils.getUserid())) {
                    showToast("不能喜欢自己");
                    return;
                }
                commitLikeReq();
                break;
            }
            case R.id.image_userdetail_play:{
//                getCoverVideoInfo();
                break;
            }
            case R.id.image_userdetail_back:{
                finish();
                break;
            }
            case R.id.image_userdetail_more:{
                showMore();
                break;
            }
            case R.id.image_userdetail_avatar: {
                if (userEntity.is_acting()) {
                    GetModelInfoRequest request = new GetModelInfoRequest();
                    request.setUserid(userEntity.getId());
                    request.setNetworkListener(new CommonNetworkCallback<ModelInfoEntity>() {
                        @Override
                        public void onSuccess(ModelInfoEntity data) {
                            hideLoading();
                            Intent intent = new Intent(UserDetailActivity.this, ModelDetailActivity.class);
                            intent.putExtra(ModelDetailActivity.USER_ENTITY, GsonUtils.objToJson(userEntity));
                            intent.putExtra(ModelDetailActivity.MODEL_ENTITY, GsonUtils.objToJson(data));
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
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent arg2) {
        super.onActivityResult(request, result, arg2);
        if (result == RESULT_OK) {
            switch (request) {
                case REQUEST_GIFT: {
                    String ss = arg2.getStringExtra(GiftActivity.GIFT_ENTITY);
                    GiftItemEntity entity = GsonUtils.jsonToObj(ss, GiftItemEntity.class);
                    Intent intent = new Intent(this, GiftConfirmActivity.class);
                    intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, GsonUtils.objToJson(entity));
                    intent.putExtra(GiftConfirmActivity.SHOULDSHOW_TIPS, true);
                    intent.putExtra(GiftConfirmActivity.USER_ID, userEntity.getId());
                    startActivity(intent);
                    break;
                }
                case REQUEST_REPORT: {
                    String content = arg2.getStringExtra(CommonInputActivity.VALUE);
                    ReportUserRequest requestEntity = new ReportUserRequest();
                    requestEntity.setUserid(userEntity.getId());
                    requestEntity.setReportid(UserUtils.getUserid());
                    requestEntity.setContent(content);
                    requestEntity.setNetworkListener(new CommonNetworkCallback<String>() {

                        @Override
                        public void onSuccess(String data) {
                            hideLoading();
                            showToast("举报成功！");
                        }

                        @Override
                        public void onFailed(int code, HttpException error,
                                             String reason) {
                            hideLoading();
                            showToast(reason);
                        }
                    });
                    showLoading();
                    requestEntity.getDataFromServer();
                    break;
                }
            }
        }
    }



    //显示拍摄认证视频提示
    public static void showRecordVideoAlert(final Activity activity){
        new AlertDialog.Builder(activity).setTitle("拍摄认证视频")
                .setMessage(activity.getResources().getString(R.string.common_video_record))
                .setPositiveButton("拍摄", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, RegVideoActivity.class);
                        activity.startActivity(intent);
                    }
                })
                .setNeutralButton("等待", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .create()
                .show();
    }

}
