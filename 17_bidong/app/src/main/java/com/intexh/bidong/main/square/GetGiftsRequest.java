package com.intexh.bidong.main.square;

import com.intexh.bidong.userentity.GiftItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetGiftsRequest extends CommonAbstractDataManager<GiftItemEntity[]> {

	private int category = -1;
	private String name = null;
	
	public void setCategory(int category) {
		this.category = category;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(category > 0){
			params.addQueryStringParameter("category", String.valueOf(category));
		}
		if(null != name){
			params.addQueryStringParameter("name", name);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/gift/list";
	}

	@Override
	protected Class<GiftItemEntity[]> getClassName() {
		return GiftItemEntity[].class;
	}

	
	
}
