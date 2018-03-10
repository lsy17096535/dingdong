package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CommitLikeRequest extends CommonAbstractDataManager<String> {

	private String userId;
	private String likerId;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setLikerId(String likerId) {
		this.likerId = likerId;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userId);
		params.addQueryStringParameter("liker_id", likerId);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/like";
	}

}
