package com.intexh.bidong.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;
import com.manteng.common.GsonUtils;
import com.manteng.common.MD5Util;

import com.intexh.bidong.main.square.GetUserDetailRequest;
import com.intexh.bidong.userentity.RegDataEntity;
import com.intexh.bidong.userentity.User;

public class UserUtils {

	private static Context context = null;
	private static RegDataEntity userInfo = null;

	public static void init(Context context) {
		UserUtils.context = context;
	}

	public static String getHXUserid(String userid) {
//		return userid;
		return MD5Util.getMD5Format(userid);
	}

	private static String getUserCacheFileName() {
//		return context.getCacheDir() + File.separator + "userInfo.paldb";
		return "userInfo.paldb";
	}

	public static String getHXPwd(String pwd) {
		return MD5Util.getMD5Format(MD5Util.getMD5Format(pwd));
	}

	public static void saveUserInfo(RegDataEntity data) {
		userInfo = data;
		if (null != userInfo) {
			HttpUtils.USERID = userInfo.getUser().getId();
		} else {
			HttpUtils.USERID = "0";
		}
		SharedPreferences sp = context.getSharedPreferences(getUserCacheFileName(), Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		if(null == data){
			edit.putString("userInfo", GsonUtils.objToJson(data));
		}else{
			edit.putString("userInfo", GsonUtils.objToJson(data));
		}
		edit.commit();
	}

	public static boolean isVideoAudit() {
		boolean ret = false;
		RegDataEntity userInfo = getUserInfo();
		if (null != userInfo && null != userInfo.getUser() && null != userInfo.getUser().getVideo_id()){
			ret = true;
		}
		return ret;
	}

	public static String getUserid() {
		String ret = null;
		RegDataEntity userInfo = getUserInfo();
		if (null != userInfo) {
			if (null != userInfo.getUser()) {
				ret = userInfo.getUser().getId();
			}
		}
		return ret;
	}

	public static void updateUserInfo(){
		GetUserDetailRequest request = new GetUserDetailRequest();
		request.setUserid(UserUtils.getUserid());
		request.setGender(UserUtils.getUserInfo().getUser().getGender());
		request.setNetworkListener(new CommonNetworkCallback<User>() {

			@Override
			public void onSuccess(User data) {
				if(null != data){
					RegDataEntity userInfo = UserUtils.getUserInfo();
					userInfo.setUser(data);
					saveUserInfo(userInfo);
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				
			}
		});
		request.getDataFromServer();
	}
	
	public static RegDataEntity getUserInfo() {
		try {
			if (null == userInfo) {
				String ss = null;
				SharedPreferences sp = context.getSharedPreferences(getUserCacheFileName(), Context.MODE_PRIVATE);
				ss = (String) sp.getString("userInfo", null);
				RegDataEntity ret = null;

				ret = GsonUtils.jsonToObj(ss, RegDataEntity.class);
				userInfo = ret;
				if (null != userInfo) {
					HttpUtils.USERID = userInfo.getUser().getId();
				} else {
					HttpUtils.USERID = "0";
				}
			}
			return userInfo;
		}catch (Exception e){
			return null;
		}
	}
}
