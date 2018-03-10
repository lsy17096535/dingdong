package com.intexh.bidong.easemob.help;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import com.intexh.bidong.easemob.help.DemoHelper.DataSyncListener;

import com.easemob.EMValueCallBack;
import com.easemob.easeui.domain.EaseUser;

public class UserProfileManager {

	/**
	 * application context
	 */
	protected Context appContext = null;

	/**
	 * init flag: test if the sdk has been inited before, we don't need to init
	 * again
	 */
	private boolean sdkInited = false;

	/**
	 * HuanXin sync contact nick and avatar listener
	 */
	private List<DataSyncListener> syncContactInfosListeners;

	private boolean isSyncingContactInfosWithServer = false;

	private EaseUser currentUser;

	public UserProfileManager() {
	}

	public synchronized boolean init(Context context) {
		if (sdkInited) {
			return true;
		}
//		ParseManager.getInstance().onInit(context);
		syncContactInfosListeners = new ArrayList<DataSyncListener>();
		sdkInited = true;
		return true;
	}

	public void asyncFetchContactInfosFromServer(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {
		if (isSyncingContactInfosWithServer) {
			return;
		}
		isSyncingContactInfosWithServer = true;
//		ParseManager.getInstance().getContactInfos(usernames, new EMValueCallBack<List<EaseUser>>() {
//
//			@Override
//			public void onSuccess(List<EaseUser> value) {
//				isSyncingContactInfosWithServer = false;
//				// in case that logout already before server returns,we should
//				// return immediately
//				if (!EMChat.getInstance().isLoggedIn()) {
//					return;
//				}
//				if (callback != null) {
//					callback.onSuccess(value);
//				}
//			}
//
//			@Override
//			public void onError(int error, String errorMsg) {
//				isSyncingContactInfosWithServer = false;
//				if (callback != null) {
//					callback.onError(error, errorMsg);
//				}
//			}
//
//		});

	}

}
