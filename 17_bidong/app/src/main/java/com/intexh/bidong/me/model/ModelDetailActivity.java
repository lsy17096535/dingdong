package com.intexh.bidong.me.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager;
import com.manteng.common.GsonUtils;

import java.util.ArrayList;
import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.easemob.chat.ChatActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.gift.GiftConfirmActivity;
import com.intexh.bidong.main.square.GetCapitalRequest;
import com.intexh.bidong.main.square.GiftActivity;
import com.intexh.bidong.main.square.GiftGridAdapter;
import com.intexh.bidong.main.square.UserDetailActivity;
import com.intexh.bidong.me.GetModelWorksRequest;
import com.intexh.bidong.me.MeFragment;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.Capital;
import com.intexh.bidong.userentity.GiftItemEntity;
import com.intexh.bidong.userentity.ModelInfoEntity;
import com.intexh.bidong.userentity.ModelWorkItemEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * Created by shenxin on 15/12/30.
 */
public class ModelDetailActivity extends BaseActivity{
    public static final String USER_ENTITY = "USER_ENTITY";
    public static final String MODEL_ENTITY = "MODEL_ENTITY";

    @ViewInject(R.id.txt_modeldetail_username)
    private TextView usernameTxt;
    @ViewInject(R.id.txt_modeldetail_gender)
    private TextView genderTxt;
    @ViewInject(R.id.txt_modeldetail_country)
    private TextView countryTxt;
    @ViewInject(R.id.image_modeldetail_avatar)
    private ImageView avatarImageView;
    @ViewInject(R.id.image_modeldetail_level)
    private ImageView levelImageView;
    @ViewInject(R.id.txt_modeldetail_address)
    private TextView addressView;
    @ViewInject(R.id.txt_modeldetail_height)
    private TextView heightView;
    @ViewInject(R.id.txt_modeldetail_weight)
    private TextView weightView;
    @ViewInject(R.id.txt_modeldetail_shoesize)
    private TextView shoesizeView;
    @ViewInject(R.id.txt_modeldetail_bodyinfo)
    private TextView bodyInfoView;
    @ViewInject(R.id.layout_modeldetail_5)
    private LinearLayout cupLayout;
    @ViewInject(R.id.txt_modeldetail_cupsize)
    private TextView cupsizeView;
    @ViewInject(R.id.txt_modeldetail_service)
    private TextView serviceView;
    @ViewInject(R.id.layout_modeldetail_container)
    private LinearLayout containerView;
    @ViewInject(R.id.btn_modeldetail_contact)
    private Button contactView;
    @ViewInject(R.id.image_modeldetail_back)
    private ImageView backView;
    @ViewInject(R.id.image_modeldetail_bk)
    private ImageView bkView;
    private User user;
    private ModelInfoEntity modelEntity;
    private List<ModelWorkItemEntity> works = new ArrayList<ModelWorkItemEntity>();
    private static final int REQUEST_GIFT = 100;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_modeldetail);
        ViewUtils.inject(this);
        String ss = getIntent().getStringExtra(USER_ENTITY);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!UserUtils.isVideoAudit()){
                    UserDetailActivity.showRecordVideoAlert(ModelDetailActivity.this);
                    return;
                }
                if (user.getId().equals(UserUtils.getUserid())) {
                    showToast("不能与自己互动");
                    return;
                }
                if(FriendsManager.getInstance().isValidFriend(user.getId())){
                    openChat();
                }
                else{
                    jumpToAccount();
                }
            }
        });
        user = GsonUtils.jsonToObj(ss, User.class);
        ss = getIntent().getStringExtra(MODEL_ENTITY);
        modelEntity = GsonUtils.jsonToObj(ss, ModelInfoEntity.class);
        ImageUtils.loadAvatarDefaultImage(user.getAvatar(), avatarImageView);
        usernameTxt.setText(user.getAlias());

        int genderFlag = R.string.femal_flag;
        if(2 == user.getGender()){  //男
            genderFlag = R.string.male_flag;
        }
        genderTxt.setText(getString(genderFlag) + user.getAge() + " ");

        countryTxt.setText(modelEntity.getCountry());
        levelImageView.setImageResource(MeFragment.getLevelResId(user.getLevel(), user.getGender()));
        addressView.setText(user.getCity() + user.getDistrict());
        heightView.setText(String.valueOf(user.getHeight()));
        weightView.setText(String.valueOf(user.getWeight()));
        shoesizeView.setText(String.valueOf(modelEntity.getShoes()));
        bodyInfoView.setText(modelEntity.getBust() + "-" + modelEntity.getWaist() + "-" + modelEntity.getHips());
        cupsizeView.setText(modelEntity.getCups() + " CUP");
        serviceView.setText(modelEntity.getOffer());
        if (user.getGender() == 1) {
            cupLayout.setVisibility(View.VISIBLE);
            contactView.setText("与她互动");
        } else {
            contactView.setText("与他互动");
        }
        getModelWorks();
    }

    private void openChat(){
        Intent intent = new Intent(ModelDetailActivity.this, ChatActivity.class);
        intent.putExtra(Constant.EXTRA_USER_ID, UserUtils.getHXUserid(user.getId()));
        startActivity(intent);
    }

    private void jumpToAccount(){
        GetCapitalRequest request = new GetCapitalRequest();
        request.setUserid(UserUtils.getUserid());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<Capital>() {

            @Override
            public void onSuccess(Capital data) {
                hideLoading();
                Intent intent = new Intent(ModelDetailActivity.this,
                        GiftActivity.class);
                intent.putExtra(GiftActivity.CAPITAL_ENTITY,
                        GsonUtils.objToJson(data));
                intent.putExtra(GiftActivity.SHOW_MODE,
                        GiftGridAdapter.SHOWMODE_VALUE);
                if(1 == user.getGender()){
                    intent.putExtra(GiftActivity.GIFT_TIPS, R.string.addfriend_female_tips);
                }else{
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

    private void getModelWorks(){
        GetModelWorksRequest request = new GetModelWorksRequest();
        request.setUserid(user.getId());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<ModelWorkItemEntity[]>() {
            @Override
            public void onSuccess(ModelWorkItemEntity[] data) {
                hideLoading();
                int index = 0;
                if (null != data) {
                    for (ModelWorkItemEntity badge : data) {
                        ImageView imageView = new ImageView(ModelDetailActivity.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                PtrLocalDisplay.designedDP2px(260),
                                PtrLocalDisplay.designedDP2px(390));
                        if (index != data.length - 1) {
                            params.rightMargin = PtrLocalDisplay.designedDP2px(10);
                        }
                        index++;
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        containerView.addView(imageView, params);
                        ImageUtils.loadWorksDefaultImage(badge.getUri(), imageView);
                    }
                    if(data.length > 0){
                        ModelWorkItemEntity badge = data[0];
                        ImageUtils.loadWorksDefaultImage(badge.getUri(),bkView);
                    }
                }
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                showLoading();
                showToast(reason);
            }
        });
        showLoading();
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
                    Intent intent = new Intent(this, GiftConfirmActivity.class);
                    intent.putExtra(GiftConfirmActivity.GIFT_ENTITY, GsonUtils.objToJson(entity));
                    intent.putExtra(GiftConfirmActivity.SHOULDSHOW_TIPS, true);
                    intent.putExtra(GiftConfirmActivity.USER_ID, user.getId());
                    startActivity(intent);
                    break;
                }
            }
        }
    }

}
