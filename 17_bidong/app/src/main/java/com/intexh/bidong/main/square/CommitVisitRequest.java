package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CommitVisitRequest extends CommonAbstractDataManager<String> {

	private String userId;
	private String visitorId;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userId);
		params.addQueryStringParameter("visitor_id", visitorId);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/visit";
	}

}
