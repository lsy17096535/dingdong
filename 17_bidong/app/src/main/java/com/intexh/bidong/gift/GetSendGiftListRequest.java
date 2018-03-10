package com.intexh.bidong.gift;

import com.intexh.bidong.userentity.SendGiftItemEntity;
import com.intexh.bidong.utils.Page;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetSendGiftListRequest extends CommonAbstractDataManager<SendGiftItemEntity[]> {

	private int status = -1;
	private Page page = null;
	private String userid;
	
	public void setStatus(int status) {
		this.status = status;
	}

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
		if(status >= 0){
			params.addQueryStringParameter("status",String.valueOf(status));
		}
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) + "," +  
											String.valueOf(page.getCount()));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/offer/" + userid + "/out/list";
	}

	@Override
	protected Class<SendGiftItemEntity[]> getClassName() {
		return SendGiftItemEntity[].class;
	}
	

}
