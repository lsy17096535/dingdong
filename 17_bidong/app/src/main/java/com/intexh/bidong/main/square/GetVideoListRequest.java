package com.intexh.bidong.main.square;

import com.intexh.bidong.userentity.VideoItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetVideoListRequest extends CommonAbstractDataManager<VideoItemEntity[]> {

	private String userid;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/" + userid + "/list";
	}

	@Override
	protected Class<VideoItemEntity[]> getClassName() {
		return VideoItemEntity[].class;
	}
	
	

}
