package com.intexh.bidong.gift;

import com.intexh.bidong.userentity.SendGiftItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetReceiveGiftDetailRequest extends CommonAbstractDataManager<SendGiftItemEntity> {

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
		return "/api/v1/offer/in/" + id;
	}

}
