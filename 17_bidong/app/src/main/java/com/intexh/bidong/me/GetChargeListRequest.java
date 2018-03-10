package com.intexh.bidong.me;

import com.intexh.bidong.userentity.CashApplyItemEntity;
import com.intexh.bidong.utils.Page;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetChargeListRequest extends CommonAbstractDataManager<CashApplyItemEntity[]> {

	private Page page = null;
	private String userid = null;
	
	public void setPage(Page page) {
		this.page = page;
	}

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
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) + "," + String.valueOf(page.getCount()));
		params.addQueryStringParameter("user_id", userid);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/recharge/list";
	}

	@Override
	protected Class<CashApplyItemEntity[]> getClassName() {
		return CashApplyItemEntity[].class;
	}
	
	

}
