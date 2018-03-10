package com.intexh.bidong.me;

import com.intexh.bidong.userentity.CashApplyTotalEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetChargeTotalRequest extends CommonAbstractDataManager<CashApplyTotalEntity> {

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
		params.addQueryStringParameter("user_id", userid);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/recharge/total";
	}

}
