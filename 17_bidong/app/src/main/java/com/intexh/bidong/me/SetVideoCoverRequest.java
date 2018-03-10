package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class SetVideoCoverRequest extends CommonAbstractDataManager<String> {

	private String videoId;
	private String userid;
	
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/cover/" + videoId;
	}

}
