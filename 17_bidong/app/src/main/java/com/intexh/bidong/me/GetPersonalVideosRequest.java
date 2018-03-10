package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.utils.Page;

public class GetPersonalVideosRequest extends CommonAbstractDataManager<TrendVideoEntity[]> {

	private String userid = null;

	private Page page = null;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(null != page){
			params.addQueryStringParameter("page", page.getStart() + "," + page.getCount());
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/" + userid + "/list";
	}

	@Override
	protected Class<TrendVideoEntity[]> getClassName() {
		return TrendVideoEntity[].class;
	}
	
	

}
