package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class ModifyPwdRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private String pwd;
	private String oldPwd;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("password",pwd);
		params.addQueryStringParameter("oldPassword", oldPwd);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/" + userid + "/passwd";
	}

}
