package com.intexh.bidong.message;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class DeleteFriendRequest extends CommonAbstractDataManager<String> {

	private String id;
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.DELETE;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/fans/del/" + id;
	}

}
