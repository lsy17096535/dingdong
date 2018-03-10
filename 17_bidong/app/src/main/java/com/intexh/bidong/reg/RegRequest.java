package com.intexh.bidong.reg;

import com.intexh.bidong.userentity.RegDataEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class RegRequest extends CommonAbstractDataManager<RegDataEntity> {
	private String mobile;
	private String password;
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("mobile", mobile);
		params.addQueryStringParameter("password", password);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/reg";
	}

}
