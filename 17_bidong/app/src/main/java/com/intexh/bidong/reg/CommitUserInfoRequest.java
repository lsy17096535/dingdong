package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CommitUserInfoRequest extends CommonAbstractDataManager<String> {

	private String avatar;
	private String username;
	private String alias;
	private String age;
//	private String birthday;
	private String height;
	private String weight;
	private String gender;
	private String signature;
	private String province;
	private String city;
	private String district;
	private String location;
	private String userId;
	private String occup;
	
	public void setOccup(String occup) {
		this.occup = occup;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setAge(String age) {
		this.age = age;
	}

//	public void setBirthday(String birthday) {
//		this.birthday = birthday;
//	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setSignature(String signature) {
		this.signature = signature;
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

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(null != avatar){
			params.addQueryStringParameter("avatar", avatar);
		}
		if(null != username){
			params.addQueryStringParameter("username", username);
		}
		if(null != alias){
			params.addQueryStringParameter("alias", alias);
		}
		if(null != age){
			params.addQueryStringParameter("age", age);
		}
//		if(null != birthday){
//			params.addQueryStringParameter("birthday", birthday);
//		}
		if(null != height){
			params.addQueryStringParameter("height", height);
		}
		if(null != weight){
			params.addQueryStringParameter("weight", weight);
		}
		if(null != gender){
			params.addQueryStringParameter("gender",gender);
		}
		if(null != signature){
			params.addQueryStringParameter("signature", signature);
		}
		if(null != province){
			params.addQueryStringParameter("province", province);
		}
		if(null != city){
			params.addQueryStringParameter("city", city);
		}
		if(null != district){
			params.addQueryStringParameter("district", district);
		}
		if(null != location){
			params.addQueryStringParameter("location", location);
		}
		if(null != occup){
			params.addQueryStringParameter("occup", occup);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/" + userId + "/update";
	}

}
