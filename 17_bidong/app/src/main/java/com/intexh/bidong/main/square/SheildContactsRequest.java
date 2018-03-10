package com.intexh.bidong.main.square;

import java.util.List;

import com.intexh.bidong.utils.StringUtil;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class SheildContactsRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private List<String> contacts;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setContacts(List<String> contacts) {
		this.contacts = contacts;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		params.addQueryStringParameter("contacts", StringUtil.join(",", contacts.toArray(new String[contacts.size()])));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/shield";
	}

}
