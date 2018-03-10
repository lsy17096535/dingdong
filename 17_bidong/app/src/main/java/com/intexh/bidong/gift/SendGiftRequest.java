package com.intexh.bidong.gift;

import com.intexh.bidong.userentity.SendGiftItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class SendGiftRequest extends CommonAbstractDataManager<SendGiftItemEntity> {

	private String from_id;
	private String to_id;
	private String gift_id;
	private int value;
	private int origin;
	private String remark;
	
	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}

	public void setGift_id(String gift_id) {
		this.gift_id = gift_id;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setOrigin(int origin) {
		this.origin = origin;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("from_id", from_id);
		params.addQueryStringParameter("to_id", to_id);
		params.addQueryStringParameter("gift_id", gift_id);
		params.addQueryStringParameter("value", String.valueOf(value));
		params.addQueryStringParameter("origin", String.valueOf(origin));
		if(null != remark){
			params.addQueryStringParameter("remark", remark);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/offer/new";
	}

}
