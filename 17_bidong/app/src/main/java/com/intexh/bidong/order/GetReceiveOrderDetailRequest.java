package com.intexh.bidong.order;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.OrderEntity;

public class GetReceiveOrderDetailRequest extends CommonAbstractDataManager<OrderEntity> {

	private String id;
	
	public void setId(String id) {
		this.id = id;
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
		// TODO Auto-generated method stub
		return "/api/v1/order/in/" + id;
	}

}
