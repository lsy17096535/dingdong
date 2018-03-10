package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CommitOrderSendRequest extends CommonAbstractDataManager<String> {

	private String from_id;
	private String to_id;
	private String item_id;
	private String item_price;
	private String item_unit;
	private String gift_id;
	private String gift_price;
	private String app_time;
	private String app_descr;

	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}

	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public void setItem_price(String item_price) {
		this.item_price = item_price;
	}

	public void setItem_unit(String item_unit) {
		this.item_unit = item_unit;
	}

	public void setGift_id(String gift_id) {
		this.gift_id = gift_id;
	}

	public void setGift_price(String gift_price) {
		this.gift_price = gift_price;
	}

	public void setApp_time(String app_time) {
		this.app_time = app_time;
	}

	public void setApp_descr(String app_descr) {
		this.app_descr = app_descr;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(null != from_id){
			params.addQueryStringParameter("from_id", from_id);
		}
		if(null != to_id){
			params.addQueryStringParameter("to_id", to_id);
		}
		if(null != item_id){
			params.addQueryStringParameter("item_id", item_id);
		}
		if(null != item_price){
			params.addQueryStringParameter("item_price", item_price);
		}
		if(null != item_unit){
			params.addQueryStringParameter("item_unit", item_unit);
		}
		if(null != gift_id){
			params.addQueryStringParameter("gift_id", gift_id);
		}
		if(null != gift_price){
			params.addQueryStringParameter("gift_price", gift_price);
		}
		if(null != app_time){
			params.addQueryStringParameter("app_time",app_time);
		}
		if(null != app_descr){
			params.addQueryStringParameter("app_descr", app_descr);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/order/new";
	}

}
