package com.intexh.bidong.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

import com.intexh.bidong.R;
import com.intexh.bidong.base.BaseActivity;
import com.intexh.bidong.common.UserLicenseActivity;
import com.intexh.bidong.easemob.constants.Constant;
import com.intexh.bidong.easemob.help.DemoHelper;
import com.intexh.bidong.location.LocationLocalManager;
import com.intexh.bidong.main.MainActivity;
import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.reg.RegActivity;
import com.intexh.bidong.reg.RegInformationActivity;
import com.intexh.bidong.user.FriendsManager;
import com.intexh.bidong.user.FriendsManager.OnFriendsInitListener;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;
import com.intexh.bidong.utils.RegexUtil;
import com.intexh.bidong.utils.UserUtils;

public class LoginActivity extends BaseActivity implements OnClickListener {
    public static final String SP_USER_INFO = "USERINFO";
    @ViewInject(R.id.txt_login_forgetpwd)
    private TextView forgetPwdView;
    @ViewInject(R.id.txt_login_reg)
    private TextView regView;
    @ViewInject(R.id.txt_login_license)
    private TextView licenseView;
    @ViewInject(R.id.btn_login_login)
    private View loginBtn;
    @ViewInject(R.id.edit_login_mobile)
    private EditText mobileInput;
    @ViewInject(R.id.edit_login_pwd)
    private EditText pwdInput;

    private String mobile;
    private String pwd;

    public static void saveLoginInfo(String username, String pwd, Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString("USERNAME", username);
        edit.putString("PWD", pwd);
        edit.commit();
    }

    public static String getLoginUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, MODE_PRIVATE);
        return sp.getString("USERNAME", "");
    }

    public static String getLoginPwd(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_USER_INFO, MODE_PRIVATE);
        return sp.getString("PWD", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        UserUtils.init(getApplicationContext());
        LocationLocalManager.init(getApplicationContext());

        mobile = getLoginUserName(this);
        pwd = getLoginPwd(this);

        loginBtn.setOnClickListener(this);
        forgetPwdView.setOnClickListener(this);
        regView.setOnClickListener(this);
        licenseView.setOnClickListener(this);
        //13989826414    123456
        mobileInput.setText(mobile);
        pwdInput.setText(pwd);

        boolean isConflict = getIntent().getBooleanExtra(Constant.ACCOUNT_CONFLICT, false);
        if (isConflict) {
            getIntent().putExtra(Constant.ACCOUNT_CONFLICT, false);
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("检测到您的账号已在其他地方登录，您已经被强制下线，如非本人操作，请尽快修改密码")
                    .setPositiveButton("知道了", null).create().show();
        }
    }

    private void checkLogin(){
        mobile = mobileInput.getText().toString();
        pwd = pwdInput.getText().toString();
        if (!RegexUtil.checkMobile(mobile)) {
            showToast(R.string.error_mobile);
            return;
        }
        if (!RegexUtil.checkPasswork(pwd)) {
            showToast(R.string.error_pwd);
            return;
        }

        doLogin(mobile, pwd);
    }

    private void doLogin(final String mobile, final String pwd) {
        LoginRequest request = new LoginRequest();
        request.setMobile(mobile);
        request.setPassword(pwd);
        request.setNetworkListener(new CommonNetworkCallback<RegDataEntity>() {

            @Override
            public void onSuccess(RegDataEntity data) {
                hideLoading();
                UserUtils.saveUserInfo(data);
                LoginActivity.saveLoginInfo(mobile, pwd, LoginActivity.this);
                getUserDetails();
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

    public void getUserDetails() {
        doLoginEasemob();
//        GetUserDetailRequest request = new GetUserDetailRequest();
//        request.setUserid(UserUtils.getUserid());
//        request.setGender(UserUtils.getUserInfo().getUser().getGender());
//        request.setNetworkListener(new CommonNetworkCallback<User>() {
//
//            @Override
//            public void onSuccess(User data) {
//                hideLoading();
//                if (data != null) {
//                    RegDataEntity userInfo = UserUtils.getUserInfo();
//                    userInfo.setUser(data);
//                    UserUtils.saveUserInfo(userInfo);
//                    HttpUtils.USERID = UserUtils.getUserid();
//                    doLoginEasemob();
//                } else {
//                    showToast("用户信息获取失败");
//                }
//            }
//
//            @Override
//            public void onFailed(int code, HttpException error, String reason) {
//                hideLoading();
//                showToast(reason);
//            }
//        });
//        showLoading();
//        request.getDataFromServer();
    }

    private void doLoginEasemob() {
        showLoading();
        // 调用sdk登陆方法登陆聊天服务器
//        EMChatManager.getInstance().login(UserUtils.getHXUserid(UserUtils.getUserid()), UserUtils.getHXPwd(pwd), new EMCallBack() {
        String userId = UserUtils.getHXUserid(UserUtils.getUserid());
        Log.e("frank","chatid="+userId);
        EMChatManager.getInstance().login(userId, userId, new EMCallBack() {

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
                FriendsManager.getInstance().loadAllFriends(new OnFriendsInitListener() {
                    @Override
                    public void onFrinedsInitSuccess() {
                        hideLoading();
                        dealWithJump(UserUtils.getUserInfo());
                    }

                    @Override
                    public void onFrinedsInitFailed() {
                        hideLoading();
                        showToast(R.string.error_network);
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {}

            @Override
            public void onError(final int code, final String message) {
                Log.e("frank","环信登录失败："+code+"--"+message);
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
                    }
                });
//                // 登陆成功，保存用户名
//                DemoHelper.getInstance().setCurrentUserName(UserUtils.getUserInfo().getUser().getAlias());
//                // 注册群组和联系人监听
//                DemoHelper.getInstance().registerGroupAndContactListener();
//                EMChat.getInstance().setAppInited();
//
//                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
//                // ** manually load all local groups and
//                EMGroupManager.getInstance().loadAllGroups();
//                EMChatManager.getInstance().loadAllConversations();
//
//                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
//                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(UserUtils.getUserInfo().getUser().getAlias());
//                if (!updatenick) {
//                    Log.e("LoginActivity", "update current user nick fail");
//                }
//                FriendsManager.getInstance().loadAllFriends(new OnFriendsInitListener() {
//                    @Override
//                    public void onFrinedsInitSuccess() {
//                        hideLoading();
//                        dealWithJump(UserUtils.getUserInfo());
//                    }
//
//                    @Override
//                    public void onFrinedsInitFailed() {
//                        hideLoading();
//                        showToast(R.string.error_network);
//                    }
//                });
            }
        });
    }

    private static final int MSG_REGINFO = 1;
    private static final int REQUEST_REGINFORMATION = 100;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REGINFO: {
                    Intent intent = new Intent(LoginActivity.this, RegInformationActivity.class);
                    startActivityForResult(intent, REQUEST_REGINFORMATION);
                    break;
                }
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case REQUEST_REGINFORMATION: {
                if (0 != UserUtils.getUserInfo().getUser().getGender()) {
                    dealWithJump(UserUtils.getUserInfo());
                }
                break;
            }
        }
    }

    private void dealWithJump(RegDataEntity data) {
        if (null != data) {
            User user = data.getUser();
            if (0 == user.getGender()) {
                showToast("你还未完成注册过程，信息完整才能登入");
                mHandler.sendEmptyMessage(MSG_REGINFO);
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.txt_login_forgetpwd: {
                Intent intent = new Intent(this, RegActivity.class);
                intent.putExtra(RegActivity.MODE, RegActivity.MODE_FORGETPWD);
                startActivity(intent);
                break;
            }
            case R.id.txt_login_reg: {
                Intent intent = new Intent(this, RegActivity.class);
                intent.putExtra(RegActivity.MODE, RegActivity.MODE_REG);
                startActivity(intent);
                break;
            }
            case R.id.txt_login_license: {
                Intent intent = new Intent(this, UserLicenseActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_login_login: {
                checkLogin();
                break;
            }
        }
    }

}
