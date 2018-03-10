package com.intexh.bidong.charge;

import com.intexh.bidong.userentity.ChargePackageItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetChargePackagesRequest extends CommonAbstractDataManager<ChargePackageItemEntity[]> {

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/capital/recharge/packages";
	}

	@Override
	protected Class<ChargePackageItemEntity[]> getClassName() {
		return ChargePackageItemEntity[].class;
	}
	
	

}
