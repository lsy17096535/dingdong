package com.intexh.bidong.location;

import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class UpdateLocationRequest extends CommonAbstractDataManager<String> {

	private LatLng location = null;
	private String province = null;
	private String city = null;
	private String district = null;
	private String userid;
	
	public void setLocation(LatLng location) {
		this.location = location;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("location",String.format("%.8f", location.longitude) + String.format(",%.8f", location.latitude));
		if(null != province){
			params.addQueryStringParameter("province", province);
		}
		if(null != city){
			params.addQueryStringParameter("city", city);
		}
		if(null != district){
			params.addQueryStringParameter("district", district);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/" + userid + "/loc";
	}

}
