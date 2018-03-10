package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.OffServiceEntity;

public class GetOffServiceRequest extends CommonAbstractDataManager<OffServiceEntity[]> {

	private String userid = null;
	
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
		return "/api/v1/activity/user/" + userid + "/list";
	}

	@Override
	protected Class<OffServiceEntity[]> getClassName() {
		return OffServiceEntity[].class;
	}
	
	

}
