package com.intexh.bidong.me;

import com.intexh.bidong.userentity.VersionUpdateEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class VersionCheckRequest extends CommonAbstractDataManager<VersionUpdateEntity> {

	private int code = 0;
	
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("code", String.valueOf(code));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/setting/version/check";
	}

}
