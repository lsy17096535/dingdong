package com.intexh.bidong.location;

import android.content.Context;
import com.intexh.bidong.utils.StringUtil;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;

public class LocationHelper implements OnGetGeoCoderResultListener {

	public class LocationInfo{
		public LatLng location;
		public String province;
		public String city;
		public String district;
		public String street;
		public String streetNo;
	}
	
	public interface OnLocationListener{
		public void onLocationChange(LocationInfo info);
	}
	
	private static LocationHelper helper = null;
	private LocationInfo locationInfo = new LocationInfo();
	// 定位相关
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private GeoCoder mSearch = null;
	private OnLocationListener mLocationListener = null;
	
	public void startLocation(OnLocationListener listener){
		if(null != mLocClient){
//			mLocClient.registerLocationListener(myListener);
//			LocationClientOption option = new LocationClientOption();
//			option.setOpenGps(true);// 打开gps
//			option.setCoorType("bd09ll"); // 设置坐标类型
//			option.setScanSpan(3000);
//			mLocClient.setLocOption(option);
			mLocClient.start();
		}
		mLocationListener = listener;
	}
	
	public void destroy(){
		if(null != mSearch){
			mSearch.destroy();
			mSearch = null;
		}
		if(null != mLocClient){
			mLocClient.stop();
			mLocClient = null;
		}
		mLocationListener = null;
		helper = null;
	}
	
	public void stopLocation(){
		if(null != mLocClient){
			mLocClient.stop();
		}
		mLocationListener = null;
	}
	
	private void reverseLocation(BDLocation location){
		if(null == mSearch){
			mSearch = GeoCoder.newInstance();
			mSearch.setOnGetGeoCodeResultListener(this);
		}
		ReverseGeoCodeOption opt = new ReverseGeoCodeOption();
		opt.location(new LatLng(location.getLatitude(), location.getLongitude()));
		mSearch.reverseGeoCode(opt);
	}
	
	private LocationHelper(Context context) {
		super();
		// 定位初始化
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(3000);
		mLocClient.setLocOption(option);
	}

	public static LocationHelper getInstance(Context context) {
		if (null == helper) {
			helper = new LocationHelper(context);
		}
		return helper;
	}
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
            if(StringUtil.isEmptyString(location.getProvince())){
            	reverseLocation(location);
            }else{
            	if(null != mLocationListener){
            		locationInfo.location = new LatLng(location.getLatitude(), location.getLongitude());
        			locationInfo.province = location.getProvince();
        			locationInfo.city = location.getCity();
        			locationInfo.district = location.getDistrict();
        			locationInfo.street = location.getStreet();
        			locationInfo.streetNo = location.getStreetNumber();
        			mLocationListener.onLocationChange(locationInfo);
        		}else{
        			stopLocation();
        		}
            }
        }

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		AddressComponent component = result.getAddressDetail();
		if(null != mLocationListener){
			locationInfo.location = result.getLocation();
			locationInfo.province = component.province;
			locationInfo.city = component.city;
			locationInfo.district = component.district;
			locationInfo.street = component.street;
			locationInfo.streetNo = component.streetNumber;
			mLocationListener.onLocationChange(locationInfo);
		}else{
			stopLocation();
		}
	}
}
