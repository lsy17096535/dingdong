package com.intexh.bidong.order;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class AskOrderRequest extends CommonAbstractDataManager<String> {

	private String id;
	private String amount;
	
	public void setId(String id) {
		this.id = id;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("amount", amount);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/order/ask/" + id;
	}

}
