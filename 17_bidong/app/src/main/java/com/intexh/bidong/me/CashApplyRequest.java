package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class CashApplyRequest extends CommonAbstractDataManager<String> {

    private String user_id;
    private int amount;
    private String card_no;
    private String card_name;
    private String card_band;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public void setCard_band(String card_band) {
        this.card_band = card_band;
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();

        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "";
    }
/*
    private String user_id;
	private int amount;

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", user_id);
		params.addQueryStringParameter("amount", String.valueOf(amount));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/cash/apply";
	}
*/

}
