package com.intexh.bidong.reg;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CommitCoverRequest extends CommonAbstractDataManager<String> {

	private String userId;
	private String snapshot;
	private String video;
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userId);
		params.addQueryStringParameter("snapshort", snapshot);
		params.addQueryStringParameter("video", video);
		params.addQueryStringParameter("is_cover", "true");
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/add";
	}

}
