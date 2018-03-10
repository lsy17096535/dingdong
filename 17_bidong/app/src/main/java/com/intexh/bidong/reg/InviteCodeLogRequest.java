package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class InviteCodeLogRequest extends CommonAbstractDataManager<String> {

	private String mobile;
	private String code;

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(null != mobile){
			params.addQueryStringParameter("mobile", mobile);
		}
		if(null != code){
			params.addQueryStringParameter("code", code);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/invite/log";
	}

}
