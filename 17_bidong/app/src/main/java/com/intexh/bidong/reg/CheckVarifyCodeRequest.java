package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CheckVarifyCodeRequest extends CommonAbstractDataManager<String> {
	private String mobile;
	private int type;
	private String code;
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/*
	 * type 0 注册  1找回密码
	 * */
	public void setType(int type) {
		this.type = type;
	}



	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("mobile", mobile);
		params.addQueryStringParameter("type", String.valueOf(type));
		params.addQueryStringParameter("code", code);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/captcha/verify";
	}

}
