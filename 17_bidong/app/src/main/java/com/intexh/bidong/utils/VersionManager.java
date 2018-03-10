package com.intexh.bidong.utils;

import com.manteng.common.GsonUtils;

import com.intexh.bidong.userentity.VersionUpdateEntity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionManager {

	private static Context context = null;
	private static VersionManager instance = null;
	private static final String UPDATE_VERSION = "UPDATE_VERSION";

	public VersionUpdateEntity getVersionUpdateEntity(){
		SharedPreferences sp = context.getSharedPreferences(UPDATE_VERSION, Context.MODE_PRIVATE);
		String ss = sp.getString(UPDATE_VERSION, null);
		VersionUpdateEntity entity = GsonUtils.jsonToObj(ss, VersionUpdateEntity.class);
		return entity;
	}
	
	public boolean isForceUpdate(){
		boolean ret = isNewVersion();
		VersionUpdateEntity entity = getVersionUpdateEntity();
		if(null != entity && entity.isForced_update() && isNewVersion()){
			ret = true;
		}else{
			ret = false;
		}
		return ret;
	}
	
	public void saveVersionUpdateEntity(VersionUpdateEntity entity){
		SharedPreferences sp = context.getSharedPreferences(UPDATE_VERSION, Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(UPDATE_VERSION, GsonUtils.objToJson(entity));
		edit.commit();
	}
	
	public boolean isNewVersion(){
		boolean ret = false;
		VersionUpdateEntity entity = getVersionUpdateEntity();
		if(null != entity && entity.getCode() > getVersionCode()){
			ret = true;
		}
		return ret;
	}
	
	public static void init(Context context){
		VersionManager.context = context;
	}
	
	public static VersionManager getInstance(){
		if(null == instance){
			instance = new VersionManager();
		}
		return instance;
	}
	
	public String getVersionName() {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String version = "v1.0.0";
		if(null != packInfo){
			version = packInfo.versionName;
		}
		return version;
	}
	
	public int getVersionCode(){
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int version = 0;
		if(null != packInfo){
			version = packInfo.versionCode;
		}
		return version;
	}
	
}
