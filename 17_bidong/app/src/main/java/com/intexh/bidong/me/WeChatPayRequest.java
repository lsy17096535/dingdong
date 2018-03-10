package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class WeChatPayRequest extends CommonAbstractDataManager<String> {

	private String packageid;
	private String userid;
	private double amount;
	
	public void setPackageid(String packageid) {
		this.packageid = packageid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		params.addQueryStringParameter("package_id", packageid);
		params.addQueryStringParameter("amount", String.format("%.2f", amount));
		params.addQueryStringParameter("type", "1");
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/recharge/pay";
	}

}
