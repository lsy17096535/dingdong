package com.intexh.bidong.me;

import com.intexh.bidong.userentity.GiftItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetSentGiftsRequest extends CommonAbstractDataManager<GiftItemEntity[]> {

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
		return "/api/v1/offer/" + userid + "/out/stat";
	}

	@Override
	protected Class<GiftItemEntity[]> getClassName() {
		return GiftItemEntity[].class;
	}

	
	
}
