package com.intexh.bidong.me;

import com.intexh.bidong.userentity.LevelResultEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class MyLevelRequest extends CommonAbstractDataManager<LevelResultEntity> {

	private int gender;
	private String userid;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("gender", String.valueOf(gender));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/" + userid + "/level";
	}

}
