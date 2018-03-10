package com.intexh.bidong.main.square;

import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.OffServiceEntity;
import com.intexh.bidong.utils.Page;


public class GetNearbyOffServiceRequest extends CommonAbstractDataManager<OffServiceEntity[]> {

	private LatLng location;
	private int gender;
	private String itemId;
	private Page page = null;
	
	public void setLocation(LatLng location) {
		this.location = location;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
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
		params.addQueryStringParameter("location", String.format("%.8f", location.longitude) + "," + String.format("%.8f", location.latitude));
		if(gender == 1){
			params.addQueryStringParameter("gender", String.valueOf(2));
		}else{
			params.addQueryStringParameter("gender", String.valueOf(1));
		}
		if(null != itemId){
			params.addQueryStringParameter("item_id", itemId);
		}
		params.addQueryStringParameter("page", String.valueOf(page.getStart()) + "," + String.valueOf(page.getCount()));
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/activity/near/list";
	}

	@Override
	protected Class<OffServiceEntity[]> getClassName() {
		return OffServiceEntity[].class;
	}
	
	
	
}
