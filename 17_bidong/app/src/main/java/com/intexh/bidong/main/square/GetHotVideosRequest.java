package com.intexh.bidong.main.square;

import com.intexh.bidong.userentity.HotVideoEntity;
import com.intexh.bidong.utils.Page;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetHotVideosRequest extends CommonAbstractDataManager<HotVideoEntity[]> {

	private Page page = null;
	private int gender;
	private String province;
	private String city;
	
	public void setPage(Page page) {
		this.page = page;
	}

	public void setGender(int gender) {
		this.gender = gender;
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
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/hot/stat";
	}

	@Override
	protected Class<HotVideoEntity[]> getClassName() {
		return HotVideoEntity[].class;
	}

	
	
}
