package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.TrendVideoEntity;
import com.intexh.bidong.utils.Page;

public class GetTrendVideosRequest extends CommonAbstractDataManager<TrendVideoEntity[]> {

	private Page page = null;
	private int gender;
	private String occup;
	private String province;
	private String city;
	
	public void setPage(Page page) {
		this.page = page;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setOccup(String occup) {
		this.occup = occup;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) +  "," + String.valueOf(page.getCount()));
		if(1 == gender){
			params.addQueryStringParameter("gender", String.valueOf(2));
		}else{
			params.addQueryStringParameter("gender", String.valueOf(1));
		}
		if(null != province){
			params.addQueryStringParameter("province", province);
		}
		if(null != city){
			params.addQueryStringParameter("city", city);
		}
		if(null != occup){
			params.addQueryStringParameter("occup", occup);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/new/trends";
	}

	@Override
	protected Class<TrendVideoEntity[]> getClassName() {
		return TrendVideoEntity[].class;
	}

	
	
}
