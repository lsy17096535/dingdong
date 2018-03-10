package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class SendCommentRequest extends CommonAbstractDataManager<String> {

	private String user_id;
	private String video_id;
	private String content;
	
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", user_id);
		params.addQueryStringParameter("video_id", video_id);
		params.addQueryStringParameter("content", content);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/comment/add";
	}

}
