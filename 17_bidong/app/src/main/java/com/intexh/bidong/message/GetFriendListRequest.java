package com.intexh.bidong.message;

import com.intexh.bidong.userentity.FriendItemEntity;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class GetFriendListRequest extends CommonAbstractDataManager<FriendItemEntity[]> {

	private String userid;
	private long timestamp = 0;
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
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
		if(timestamp > 0){
			params.addQueryStringParameter("timestamp", String.valueOf(timestamp));
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/fans/" + userid + "/list";
	}

	@Override
	protected Class<FriendItemEntity[]> getClassName() {
		return FriendItemEntity[].class;
	}
	
	

}
