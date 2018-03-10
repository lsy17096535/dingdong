package com.intexh.bidong.main.square;

import com.intexh.bidong.userentity.CommentItemEntity;
import com.intexh.bidong.utils.Page;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetCommentsRequest extends CommonAbstractDataManager<CommentItemEntity[]> {

	private String videoid;
	private Page page = null;
	
	public void setPage(Page page) {
		this.page = page;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) + "," + String.valueOf(page.getCount()));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/" + videoid + "/comment/list";
	}

	@Override
	protected Class<CommentItemEntity[]> getClassName() {
		return CommentItemEntity[].class;
	}
	
	

}
