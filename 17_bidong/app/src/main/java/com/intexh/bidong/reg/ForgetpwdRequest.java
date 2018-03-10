package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class ForgetpwdRequest extends CommonAbstractDataManager<String> {

	private String mobile;
	private String pwd;
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("mobile", mobile);
		params.addQueryStringParameter("password", pwd);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/passwd/reset";
	}

}
