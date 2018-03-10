package com.manteng.common;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;


public abstract class CommonAbstractDataManager<T> {
	private static HttpUtils httpUtils = new HttpUtils();
//	public static final String NETWORK_BASE_URL = "https://106.75.118.28:8889";
	public static final String NETWORK_BASE_URL = "http://106.75.171.181:8888";
	public static final int NETWORK_FAILED = 1000;
	
	private CommonNetworkCallback<T> networkListener = null;
	public interface CommonNetworkCallback<T>{
		public void onSuccess(T data); 
		public void onFailed(int code,HttpException error,String reason);
	}
	
	public void setNetworkListener(CommonNetworkCallback<T> networkListener) {
		this.networkListener = networkListener;
	}
	protected abstract HttpMethod getHttpMethod();
	protected abstract RequestParams getHttpParams();
	protected abstract String getBusinessUrl();
	protected Class<T> getClassName(){
//		Type[] obj = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
//		Class<T> entityClass = null;
//		entityClass = (Class<T>) (obj[0]);
//		return entityClass;

		return GenericsUtils.getSuperClassGenricType(getClass());
	}
	
	public void getDataFromServer(){
		Log.e("frank","请求地址:"+NETWORK_BASE_URL + getBusinessUrl());
		httpUtils.send(getHttpMethod(), NETWORK_BASE_URL + getBusinessUrl(),getHttpParams(),new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if(null != responseInfo){
					Log.e("frank","请求数据返回:"+responseInfo.result);
					ResResult result = ResResult.parse(responseInfo.result);//GsonUtils.jsonToObj(responseInfo.result, ResResult.class);//ResResult.parse(responseInfo.result);
					if(null == result){
						if(null != networkListener){
							networkListener.onFailed(NETWORK_FAILED,null, "返回数据异常");
						}
					}else{
						if(0 != result.getCode() && 29 != result.getCode()){
							if(null != networkListener){
								networkListener.onFailed(result.getCode(),null, result.getReason());
							}
						}else{
							T entity = null;
							if(getClassName().getSimpleName().toLowerCase().equals("string")){
								entity = (T)result.getData();
							}else{
								entity = GsonUtils.jsonToObj(result.getData(), getClassName());
							}
							if(null != networkListener){
								networkListener.onSuccess(entity);
							}
						}
					}
				}else{
					Log.e("frank","请求数据返回: 返回数据异常");
					if(null != networkListener){
						networkListener.onFailed(NETWORK_FAILED,null, "返回数据异常");
					}
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				Log.e("frank","onFailure: "+msg);
				if(null != networkListener){
					if(null != error){
						msg = "网络异常，请检查您的网络";
					}
					networkListener.onFailed(NETWORK_FAILED,null, msg);
				}
			}
		});
	}



}
