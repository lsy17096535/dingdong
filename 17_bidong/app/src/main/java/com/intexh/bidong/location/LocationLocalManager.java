package com.intexh.bidong.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.baidu.mapapi.utils.DistanceUtil;
import com.intexh.bidong.location.LocationHelper.LocationInfo;
import com.manteng.common.GsonUtils;

public class LocationLocalManager {

	private static Context context;
	private static LocationLocalManager mInstance = null;
	private static final String LASTLOCATION = "LASTLOCATION";
	private static final String LASTDISTANCELOCATION = "LASTDISTANCELOCATION";
	
	public static void init(Context context){
		LocationLocalManager.context = context;
	}
	
	private static String getLocationCacheFileName(){
//		return context.getCacheDir() + File.separator + "location" + UserUtils.getUserid() +".paldb";
//		return "location" + UserUtils.getUserid() +".paldb";
		return "location.paldb";
	}
	
	public static LocationLocalManager getInstance(){
		if(null == mInstance){
			mInstance = new LocationLocalManager();
		}
		return mInstance;
	}
	
	private LocationInfo getLastDistance(){
		String ss = null;
		SharedPreferences sp = context.getSharedPreferences(getLocationCacheFileName(), Context.MODE_PRIVATE);
		ss = sp.getString(LASTDISTANCELOCATION,null);
		LocationInfo info = GsonUtils.jsonToObj(ss, LocationInfo.class);
		return info;
	}
	
	public LocationInfo getLastLocation(){
		String ss = null;
		SharedPreferences sp = context.getSharedPreferences(getLocationCacheFileName(), Context.MODE_PRIVATE);
		ss = sp.getString(LASTLOCATION,null);
		LocationInfo info = GsonUtils.jsonToObj(ss, LocationInfo.class);
		return info;
	}
	
	
	public boolean saveLocation(LocationInfo locationInfo){
		if(null == locationInfo){
			return false;
		}
		LocationInfo lastInfo = getLastDistance();
		boolean ret = false;
		SharedPreferences sp = context.getSharedPreferences(getLocationCacheFileName(), Context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString(LASTLOCATION, GsonUtils.objToJson(locationInfo));
		if(null == lastInfo){
			edit.putString(LASTDISTANCELOCATION, GsonUtils.objToJson(locationInfo));
			ret = true;
		}else{
			double distance = DistanceUtil.getDistance(lastInfo.location, locationInfo.location);
			if(distance > 50){
				ret = true;
				edit.putString(LASTDISTANCELOCATION, GsonUtils.objToJson(locationInfo));
			}
		}
		edit.commit();
		return ret;
	}
	
	
	private LocationLocalManager(){
		super();
	}
	
}
