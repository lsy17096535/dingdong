package com.intexh.bidong.me;

import com.intexh.bidong.userentity.CashApplyItemEntityEx;
import com.intexh.bidong.utils.Page;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CashApplyListRequest extends CommonAbstractDataManager<CashApplyItemEntityEx[]> {

	private String user_id;
	private Page page = null;
	
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", user_id);
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) + "," + 
												String.valueOf(page.getCount()));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/cash/list";
	}

	@Override
	protected Class<CashApplyItemEntityEx[]> getClassName() {
		return CashApplyItemEntityEx[].class;
	}

}
