package com.intexh.bidong.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.main.MainActivity;
import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.UserUtils;

/**
 * Created by shenxin on 15/12/29.
 */
public class WelcomeActivity extends BaseActivity {
    private String mobile;
    private String pwd;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_welcome);
        UserUtils.init(getApplicationContext());
        LocationLocalManager.init(getApplicationContext());

        mobile = LoginActivity.getLoginUserName(this);
        pwd = LoginActivity.getLoginPwd(this);
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(20);
                    //检测是否为第一登陆
                    checkGuideOrLogin();
                }catch (InterruptedException e){}
            }
        });
    }

    private void checkGuideOrLogin() {
        SharedPreferences sp = getSharedPreferences("GUIDE", MODE_PRIVATE);
        String ret = sp.getString("GUIDE", "false");
        if (ret.equals("false")) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("GUIDE", "true");
            edit.commit();
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            if (!"".equals(mobile) && !"".equals(pwd) && null != UserUtils.getUserid()) {
                getUserDetails();
            }
            else{
                toLoginActivity();
            }
        }
    }

    private void toLoginActivity(){
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void toMainActivity(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void getUserDetails() {
        GetUserDetailRequest request = new GetUserDetailRequest();
        request.setUserid(UserUtils.getUserid());
        request.setGender(UserUtils.getUserInfo().getUser().getGender());
        request.setNetworkListener(new CommonAbstractDataManager.CommonNetworkCallback<User>() {

            @Override
            public void onSuccess(User data) {
                hideLoading();
                if (data != null) {
                    RegDataEntity userInfo = UserUtils.getUserInfo();
                    userInfo.setUser(data);
                    UserUtils.saveUserInfo(userInfo);
                    HttpUtils.USERID = UserUtils.getUserid();
                    doLoginEasemob();
                } else {
                    showToast("用户信息获取失败");
                    toLoginActivity();
                }
            }

            @Override
            public void onFailed(int code, HttpException error, String reason) {
                hideLoading();
                showToast(reason);
                toLoginActivity();
            }
        });
        showLoading();
        request.getDataFromServer();
    }

    private void doLoginEasemob() {
        showLoading();
        // 调用sdk登陆方法登陆聊天服务器
//        EMChatManager.getInstance().login(UserUtils.getHXUserid(UserUtils.getUserid()), UserUtils.getHXPwd(pwd), new EMCallBack() {
        String userId = UserUtils.getHXUserid(UserUtils.getUserid());
        Log.e("frank","account="+userId);
        EMChatManager.getInstance().login(userId,userId, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名
                DemoHelper.getInstance().setCurrentUserName(UserUtils.getUserInfo().getUser().getAlias());
                // 注册群组和联系人监听
                DemoHelper.getInstance().registerGroupAndContactListener();
                EMChat.getInstance().setAppInited();

                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();

                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(UserUtils.getUserInfo().getUser().getAlias());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                FriendsManager.getInstance().loadAllFriends(new FriendsManager.OnFriendsInitListener() {
                    @Override
                    public void onFrinedsInitSuccess() {
                        hideLoading();
                        RegDataEntity data = UserUtils.getUserInfo();
                        if (null == data || 0 == data.getUser().getGender()) {
                            toLoginActivity();
                        } else {
                            toMainActivity();
                        }
                    }

                    @Override
                    public void onFrinedsInitFailed() {
                        hideLoading();
                        showToast(R.string.error_network);
                        toLoginActivity();
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {}

            @Override
            public void onError(final int code, final String message) {
                hideLoading();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if(null != message && message.equals("invalid user or password")){
                            String err = "手机号和密码不匹配，如多次尝试无果，可以通过[忘记密码]功能重置密码";
                            Toast.makeText(getApplicationContext(), "登录失败：" + err,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "登录失败：" + message,
                                    Toast.LENGTH_SHORT).show();
                        }
                        toLoginActivity();
                    }
                });
            }
        });
    }
}
