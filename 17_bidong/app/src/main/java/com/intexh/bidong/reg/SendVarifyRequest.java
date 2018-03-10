package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class SendVarifyRequest extends CommonAbstractDataManager<String> {
	private String mobile;
	private int type;
	
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
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("mobile", mobile);
		params.addQueryStringParameter("type", String.valueOf(type));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/captcha";
	}

}
