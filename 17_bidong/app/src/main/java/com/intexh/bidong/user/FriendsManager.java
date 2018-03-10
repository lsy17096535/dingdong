package com.intexh.bidong.user;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.intexh.bidong.message.GetFriendListRequest;
import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.utils.UserUtils;

import com.lidroid.xutils.exception.HttpException;
import com.manteng.common.CommonAbstractDataManager.CommonNetworkCallback;

/*
 * 该模块涉及的userid均为hxid
 * */
public class FriendsManager {

	public interface OnFriendsListener{
		public void onLoadFrinedsInfo(String hxid,FriendItemEntity userInfo);
		public void onLoadFrinedsFailed(String hxid,int code,String reason);
	}
	public interface OnFriendsInitListener{
		public void onFrinedsInitSuccess();
		public void onFrinedsInitFailed();
	}
	
	private static final String LAST_TIMESTAMP = "LAST_TIMESTAMP";
	
	private static Context context = null;
	public static FriendsManager instance = null;
	private FriendsUserInfoDao mDao = null;
	private Map<String, FriendItemEntity> friendsMap = new HashMap<String, FriendItemEntity>();
	private FriendItemEntity selfUserInfo = null;
	
	public static void init(Context context){
		FriendsManager.context = context;
	}
	
	private FriendsManager(){
		super();
		mDao = new FriendsUserInfoDao(context);
	}
	
	public void updateFriend(FriendItemEntity entity){
		saveUserInfo(entity);
	}
	
	public static FriendsManager getInstance(){
		if(null == instance){
			instance = new FriendsManager();
		}
		return instance;
	}
	
	private long getLastUpdateTime(){
		long timestamp = 0;
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(LAST_TIMESTAMP, Context.MODE_PRIVATE);
		timestamp = sp.getLong(LAST_TIMESTAMP, 0);
		return timestamp;
	}
	
	private void saveLastUpdateTime(long timestamp){
		SharedPreferences sp = context.getApplicationContext().getSharedPreferences(LAST_TIMESTAMP, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putLong(LAST_TIMESTAMP, timestamp);
		edit.commit();
	}
	
	public void loadAllFriends(final OnFriendsInitListener listener){
		GetFriendListRequest request = new GetFriendListRequest();
		request.setUserid(UserUtils.getUserid());
		request.setTimestamp(getLastUpdateTime());
		request.setNetworkListener(new CommonNetworkCallback<FriendItemEntity[]>() {

			@Override
			public void onSuccess(FriendItemEntity[] data) {
				if(null != data){
					long maxTimestatmp = 0;
					for(FriendItemEntity entity : data){
						saveUserInfo(entity);
						long timestamp = entity.getUpdateTimestamp();
						if(timestamp > maxTimestatmp){
							maxTimestatmp = timestamp;
						}
					}
					saveLastUpdateTime(maxTimestatmp);
				}
				if(null != listener){
					listener.onFrinedsInitSuccess();
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				if(null != listener){
					listener.onFrinedsInitFailed();
				}
			}
		});
		request.getDataFromServer();
	}

	public boolean isValidFriend(String userId){
		boolean isValid = false;
		String hxid = UserUtils.getHXUserid(userId);
		FriendItemEntity ret = friendsMap.get(hxid);
		if(null == ret){
			ret = getFriendFromDB(hxid);
		}
		if(null != ret && ret.getStatus() == 0){
			isValid = true;
		}
		return isValid;
	}
	
	public void getFrinedInfo(String hxid,OnFriendsListener listener){
		FriendItemEntity ret = null;
		ret = friendsMap.get(hxid);
		if(null == ret){
			ret = getFriendFromDB(hxid);
			if(null == ret){
				tryToLoadFriendFromServer(hxid, listener);
			}else{
				if(null != listener){
					listener.onLoadFrinedsInfo(hxid, ret);
				}
			}
		}else{
			if(null != listener){
				listener.onLoadFrinedsInfo(hxid, ret);
			}
		}
	}
	
	public FriendItemEntity getFrinedInfo(String hxid){
		FriendItemEntity ret = null;
		ret = friendsMap.get(hxid);
		if(null == ret){
			if("ppstar".equals(hxid)){
				return null;
			}
			ret = getFriendFromDB(hxid);
			if(null == ret){
				tryToLoadFriendFromServer(hxid, null);
			}
		}
		return ret;
	}
	
	private FriendItemEntity getFriendFromDB(String hxid){
		FriendItemEntity ret = null;
		if(null != mDao){
			ret = mDao.getFriendUserInfo(hxid);
		}
		return ret;
	}
	
	private void tryToLoadFriendFromServer(final String hxid,final OnFriendsListener listener){
		GetFriendListRequest request = new GetFriendListRequest();
		request.setUserid(UserUtils.getUserid());
		request.setNetworkListener(new CommonNetworkCallback<FriendItemEntity[]>() {

			@Override
			public void onSuccess(FriendItemEntity[] data) {
				if(null != data){
					for(FriendItemEntity entity : data){
						saveUserInfo(entity);
					}
					if(null != listener){
						if(null != hxid){
							FriendItemEntity ret = friendsMap.get(hxid);
							if(null != ret){
								listener.onLoadFrinedsInfo(hxid, ret);
							}else{
								listener.onLoadFrinedsFailed(hxid, 0, "用户信息获取失败");
							}
						}
					}
				}
			}

			@Override
			public void onFailed(int code, HttpException error, String reason) {
				if(null != listener){
					listener.onLoadFrinedsFailed(hxid, -1, reason);
				}
			}
		});
		request.getDataFromServer();
	}
	
	private void saveUserInfo(FriendItemEntity userinfo){
		if(null != userinfo && null != userinfo.getFans()){
			friendsMap.put(userinfo.getFans().getHxId(), userinfo);
			if(null != mDao){
				mDao.saveHXUserInfo(userinfo);
			}
		}
	}
	
}
