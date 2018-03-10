package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class DeleteVideoRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private String videoid;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.DELETE;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/del/" + videoid;
	}

}
