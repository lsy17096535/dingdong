package com.intexh.bidong.easemob.video;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class BegGiftRequest extends CommonAbstractDataManager<String> {

	private String from_id;
	private String to_id;
	private String gift_id;
	private int value;
	private String uri;
	private String name;
	
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

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setName(String name) {
		this.name = name;
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
		params.addQueryStringParameter("uri", uri);
		params.addQueryStringParameter("name", name);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/offer/ask";
	}

}
