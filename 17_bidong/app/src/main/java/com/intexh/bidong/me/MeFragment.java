package com.intexh.bidong.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.baoyz.actionsheet.ActionSheet;
import com.baoyz.actionsheet.ActionSheet.ActionSheetListener;
import com.easemob.easeui.utils.Settings;
import com.intexh.bidong.utils.DialogUtils;
import com.jauker.widget.BadgeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.intexh.bidong.PPStarApplication;
import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseFragment;
import com.intexh.bidong.common.UserLicenseActivity;
import com.intexh.bidong.crop.ClipImageActivity;
import com.intexh.bidong.easemob.common.ImageGridActivity;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.login.LoginActivity;
import com.intexh.bidong.main.square.GetVideoRequest;
import com.intexh.bidong.main.square.VideoDetailActivity;
import com.intexh.bidong.reg.CommitUserInfoRequest;
import com.intexh.bidong.reg.RegVideoActivity;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.BucketHelper;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class MeFragment extends BaseFragment implements OnClickListener {
    public static MeFragment instance;
    private View mView = null;

    @ViewInject(R.id.image_me_menu_more)
    private ImageView menuMoreView;
    @ViewInject(R.id.image_me_play)
    private ImageView playIcon;
    @ViewInject(R.id.image_me_bigavatar)
    private ImageView bigAvatar;
    @ViewInject(R.id.txt_me_avatar_audit_prompt)
    private TextView avatarPrompt;
    @ViewInject(R.id.layout_me_basicinfo)
    private View basicLayout;
    @ViewInject(R.id.layout_me_personalvideo)
    private View personalLayout;
    @ViewInject(R.id.layout_me_offlineservice)
    private View offserviceLayout;
    @ViewInject(R.id.layout_me_accountinfo)
    private View accountLayout;
    @ViewInject(R.id.layout_me_visitor)
    private View visitorLayout;
    @ViewInject(R.id.txt_me_visitorlabel)
    private TextView visitorLabel;
    @ViewInject(R.id.layout_me_liker)
    private View likerLayout;
    @ViewInject(R.id.txt_me_likelabel)
    private TextView likearLabel;
    @ViewInject(R.id.layout_me_about)
    private View aboutLayout;
    @ViewInject(R.id.layout_me_license)
    private View licenseLayout;
    @ViewInject(R.id.btn_me_exit)
    private View exitBtn;
    @ViewInject(R.id.image_me_level)
    private ImageView levelIconView;
    @ViewInject(R.id.layout_me_level)
    private View levelView;
    @ViewInject(R.id.layout_me_model)
    private View modelView;
    @ViewInject(R.id.image_me_gender)
    private ImageView genderView;
    @ViewInject(R.id.gift_rlt)
    private RelativeLayout gift_rlt;
    @ViewInject(R.id.share_rlt)
    private RelativeLayout share_rlt;

    private BadgeView visitBadge;
    private BadgeView likeBadge;
    private int visitCount;
    private int likeCount;

    private RegDataEntity userInfo;
    private String avatarUrl = null;
    private String avatarName = null;
    public static final int REQUEST_LOCAL_VIDEO = 20000;
    public static final int REQUEST_LOCAL_PIC = REQUEST_LOCAL_VIDEO + 1;
    public static final int CAPTURE_PHOTO_REQUEST_CODE = REQUEST_LOCAL_PIC + 1;
    public static final int REQUEST_NICKNAME = CAPTURE_PHOTO_REQUEST_CODE + 1;
    public static final int REQUEST_SIGN = REQUEST_NICKNAME + 1;
    public static final int REQUEST_SELECTVIDEO = REQUEST_SIGN + 1;
    public static final int REQUEST_CROPIMAGE = REQUEST_SELECTVIDEO + 1;

    private static final int[] LEVEL_FEMALE_RES = {R.drawable.ico_femalelv1,
            R.drawable.ico_femalelv2, R.drawable.ico_femalelv3,
            R.drawable.ico_femalelv4, R.drawable.ico_femalelv5,
            R.drawable.ico_femalelv6, R.drawable.ico_femalelv7,
            R.drawable.ico_femalelv8, R.drawable.ico_femalelv9,
            R.drawable.ico_femalelv10};
    private static final int[] LEVEL_MALE_RES = {R.drawable.ico_malelv1,
            R.drawable.ico_malelv2, R.drawable.ico_malelv3,
            R.drawable.ico_malelv4, R.drawable.ico_malelv5,
            R.drawable.ico_malelv6, R.drawable.ico_malelv7,
            R.drawable.ico_malelv8, R.drawable.ico_malelv9,
            R.drawable.ico_malelv10};

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        instance = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_me, null);
            ViewUtils.inject(this, mView);
            visitBadge = new BadgeView(this.getActivity());
            visitBadge.setTargetView(visitorLabel);
            visitBadge.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            visitBadge.setBadgeMargin(66, 0, 0, 0);
            visitBadge.setVisibility(View.GONE);
            likeBadge = new BadgeView(this.getActivity());
            likeBadge.setTargetView(likearLabel);
            likeBadge.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
            likeBadge.setBadgeMargin(66, 0, 0, 0);
            likeBadge.setVisibility(View.GONE);
            userInfo = UserUtils.getUserInfo();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if(userInfo!=null && userInfo.getUser()!=null && null != userInfo.getUser().getVideo_id()){
                playIcon.setVisibility(View.VISIBLE);
            }
            bigAvatar.setLayoutParams(new FrameLayout.LayoutParams(dm.widthPixels, dm.widthPixels));
            String avatarUri = userInfo.getUser().getAvatar();
            if(null == avatarUri){
//                avatarUri = userInfo.getUser().getAvatar_new();
                avatarPrompt.setVisibility(View.VISIBLE);
            }
            ImageUtils.loadBigAvatarImage(avatarUri, bigAvatar);
            if (1 == userInfo.getUser().getGender()) {
                genderView.setImageResource(R.drawable.icon_female);
            } else {
                genderView.setImageResource(R.drawable.icon_male);
            }

            bigAvatar.setOnClickListener(this);
            basicLayout.setOnClickListener(this);
            personalLayout.setOnClickListener(this);
            offserviceLayout.setOnClickListener(this);
            accountLayout.setOnClickListener(this);
            visitorLayout.setOnClickListener(this);
            likerLayout.setOnClickListener(this);
            aboutLayout.setOnClickListener(this);
            licenseLayout.setOnClickListener(this);
            levelView.setOnClickListener(this);
            modelView.setOnClickListener(this);
            playIcon.setOnClickListener(this);
            menuMoreView.setOnClickListener(this);
            exitBtn.setOnClickListener(this);
            gift_rlt.setOnClickListener(this);
            share_rlt.setOnClickListener(this);
            updateLevelView();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) {
                parent.removeView(mView);
            }
        }
        return mView;
    }

    private void getCoverVideoInfo() {
        GetVideoRequest request = new GetVideoRequest();
        request.setId(userInfo.getUser().getVideo_id());
        request.setNetworkListener(new CommonNetworkCallback<TrendVideoEntity>() {

            @Override
            public void onSuccess(TrendVideoEntity data) {
                if(null != data){
                    data.setUser(userInfo.getUser());
                }
                Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
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

    private void recordVideo() {
        Intent intent = new Intent(getActivity(), RegVideoActivity.class);
        startActivity(intent);
    }

    public static int getLevelResId(int index, int gender) {
        int level = index;
        if (1 == gender) {
            if (level >= 1 && level <= 10) {
                return LEVEL_FEMALE_RES[level - 1];
            } else {
                return LEVEL_FEMALE_RES[0];
            }
        } else {
            if (level >= 1 && level <= 10) {
                return LEVEL_MALE_RES[level - 1];
            } else {
                return LEVEL_MALE_RES[0];
            }
        }
    }

    public static int getLevelResId(int index) {
        User user = UserUtils.getUserInfo().getUser();
        int level = index;
        if (1 == user.getGender()) {
            if (level >= 1 && level <= 10) {
                return LEVEL_FEMALE_RES[level - 1];
            } else {
                return LEVEL_FEMALE_RES[0];
            }
        } else {
            if (level >= 1 && level <= 10) {
                return LEVEL_MALE_RES[level - 1];
            } else {
                return LEVEL_MALE_RES[0];
            }
        }
    }

    public static int getLevelResId() {
        User user = UserUtils.getUserInfo().getUser();
        int level = user.getLevel();
        if (1 == user.getGender()) {
            if (level >= 1 && level <= 10) {
                return LEVEL_FEMALE_RES[level - 1];
            } else {
                return LEVEL_FEMALE_RES[0];
            }
        } else {
            if (level >= 1 && level <= 10) {
                return LEVEL_MALE_RES[level - 1];
            } else {
                return LEVEL_MALE_RES[0];
            }
        }
    }

    private void updateLevelView() {
        int res = getLevelResId();
        levelIconView.setVisibility(View.VISIBLE);
        levelIconView.setImageResource(res);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onBaseResume() {
        updateLevelView();
    }

    @Override
    protected void onBasePause() {}

    //录制认证视频
    private void showRecordVideo() {
        String authVideoMenu = "重新拍摄认证视频";
        if(null == userInfo.getUser().getVideo_id()){
            authVideoMenu = "拍摄形象认证视频";
        }
        ActionSheet
                .createBuilder(getActivity(), getChildFragmentManager())
                .setCancelButtonTitle(R.string.common_cancel)
                .setOtherButtonTitles("更换头像", authVideoMenu)
                .setListener(new ActionSheetListener() {

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0: {
                                updateAvatar();
                                break;
                            }
                            case 1: {
                                recordVideo();
                                break;
                            }
                            case 2: {
                                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                                startActivityForResult(intent, REQUEST_LOCAL_VIDEO);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}
                }).show();
    }

    private void updateAvatar(){
        ActionSheet
                .createBuilder(getActivity(), getChildFragmentManager())
                .setCancelButtonTitle(R.string.common_cancel)
                .setOtherButtonTitles(getResources().getString(R.string.common_camera_avatar),
                        getResources().getString(R.string.common_photo))
                .setListener(new ActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0: {
                                ClipImageActivity.startCapture(MeFragment.this);
                                break;
                            }
                            case 1: {
                                ClipImageActivity.starImageSelector(getActivity());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {}
                }).show();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.image_me_bigavatar: {
                DialogUtils.showDialog(getActivity(),"","头像图片禁止卡通 风景 汽车明星等 人物应使用高清头像",
                        "确定","",new DialogUtils.DialogImpl(){
                            @Override
                            public void onOk() {
                                updateAvatar();
                            }
                        });

                break;
            }
            case R.id.share_rlt: {
                //分享
                showShare(getActivity(),"一秒帮你快速邂逅靓女、土豪，约出附近单身男女",
                        "一秒帮你快速邂逅靓女、土豪，约出附近单身男女",
                        "http://106.75.118.28:8080/img/system/logo.png",
                        "http://bidong.intexh.com/download/");
                break;
            }
            case R.id.image_me_play: {
                getCoverVideoInfo();
                break;
            }
            case R.id.image_me_menu_more: {
                showRecordVideo();
                break;
            }
            case R.id.layout_me_level: {
                Intent intent = new Intent(getActivity(), MyLevelActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_model: {
//                Intent intent = new Intent(getActivity(), ModelInfoActivity.class);
//                startActivity(intent);
                recordVideo();
                break;
            }
            case R.id.layout_me_basicinfo: {
                Intent intent = new Intent(getActivity(), AccountBaseInfoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_personalvideo: {
                Intent intent = new Intent(getActivity(), MyTrendVideoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_offlineservice: {
                Intent intent = new Intent(getActivity(), MyOffServiceListActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_accountinfo: {
                Intent intent = new Intent(getActivity(), AccountInfoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_visitor: {
                visitCount = 0;
                visitBadge.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), VisitMeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_liker: {
                likeCount = 0;
                likeBadge.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), LikeMeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_about: {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.layout_me_license: {
                Intent intent = new Intent(getActivity(), UserLicenseActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_me_exit: {
                LoginActivity.saveLoginInfo(LoginActivity.getLoginUserName(getActivity()), "", getActivity());
//                Settings.setString("chat_ids", ""); //清除礼物聊天记录
                getActivity().finish();
                UserUtils.saveUserInfo(null);
                DemoHelper.getInstance().logout(false, null);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.gift_rlt: {
                Intent intent = new Intent(getActivity(), MainGiftActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    //显示分享
    public static void showShare(Context context, String title, String text, String imagePath, String linkUrl) {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(linkUrl);
        // text是分享文本，所有平台都需要这个字段
        oks.setText(text);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(imagePath);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(linkUrl);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment(data.getDescription());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(text);
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(linkUrl);
        // 启动分享GUI
        oks.show(context);
    }


    private void commitAvatar() {

        CommitUserInfoRequest request = new CommitUserInfoRequest();
        request.setAvatar(avatarName);
        request.setUserId(UserUtils.getUserid());
        request.setNetworkListener(new CommonNetworkCallback<String>() {

            @Override
            public void onSuccess(String data) {
                hideLoading();
                showToast("头像上传成功，请等待审核！");
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

    private void uploadAvatarPic(File file) {
        showLoading();
        PPStarApplication.wantuService.upload(file, null, new UploadListener() {
            @Override
            public void onUploading(UploadTask arg0) {}

            @Override
            public void onUploadFailed(UploadTask arg0, FailReason arg1) {
                hideLoading();
                showToast(R.string.error_uploadavatar);
            }

            @Override
            public void onUploadComplete(UploadTask arg0) {
                avatarUrl = arg0.getResult().url;
                avatarName = arg0.getResult().name;
                Log.e("frank","avatarUrl="+avatarUrl);
                Log.e("frank","avatarName="+avatarName);
                hideLoading();
                commitAvatar();
            }

            @Override
            public void onUploadCancelled(UploadTask arg0) {
                hideLoading();
                showToast(R.string.error_uploadavatar);
            }
        }, BucketHelper.getInstance().getAvatarBucketToken());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", String.valueOf(requestCode));
            switch (requestCode) {
                case ClipImageActivity.CROP_RESULT_CODE: {
                    String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
                    uploadAvatarPic(new File(path));
                    break;
                }
                case ClipImageActivity.START_ALBUM_REQUESTCODE:
                    //使用MultiImageSelector库选择图片，避免android不同版本取相册图片的差异
                    ArrayList<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    ClipImageActivity.startCropImageActivity(this, paths.get(0));
                    break;
                case ClipImageActivity.CAMERA_WITH_DATA:
                    // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                    ClipImageActivity.startCropImageActivity(this, ClipImageActivity.TMP_PATH);
                    break;
            }
        }
    }

    /**
     * 设置头像
     */
    public void setNewAvatar(String avatar) {
        userInfo.getUser().setAvatar(avatar);
        avatarPrompt.setVisibility(View.GONE);
        ImageUtils.loadBigAvatarImage(avatar, bigAvatar);
    }

    /**
     * 注册头像审核不通过时
     */
    public void setRegAvatarPrompt(String remark) {
        if(null == userInfo.getUser().getAvatar()){
            avatarPrompt.setText(remark);
        }
    }

    /**
     * 加访问提醒
     */
    public void addVisitBadge() {
        visitCount++;
        visitBadge.setVisibility(View.VISIBLE);
        visitBadge.setBadgeCount(visitCount);
    }

    /**
     * 加被访问提醒
     */
    public void addLikeBadge() {
        likeCount++;
        likeBadge.setVisibility(View.VISIBLE);
        likeBadge.setBadgeCount(likeCount);
    }

}
