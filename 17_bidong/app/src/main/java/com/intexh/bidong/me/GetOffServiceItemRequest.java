package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.OffServiceItemEntity;

public class GetOffServiceItemRequest extends CommonAbstractDataManager<OffServiceItemEntity[]> {

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
		return "/api/v1/activity/item/list";
	}

	@Override
	protected Class<OffServiceItemEntity[]> getClassName() {
		return OffServiceItemEntity[].class;
	}

}
