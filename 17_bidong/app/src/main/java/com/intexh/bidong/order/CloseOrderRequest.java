package com.intexh.bidong.order;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CloseOrderRequest extends CommonAbstractDataManager<String> {

	private String id;
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/order/close/" + id;
	}

}
