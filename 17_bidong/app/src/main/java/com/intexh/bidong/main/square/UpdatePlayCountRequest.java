package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class UpdatePlayCountRequest extends CommonAbstractDataManager<String> {

	private String id;
	private String userid;
	private String viewerid;
	
	public void setId(String id) {
		this.id = id;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setViewerid(String viewerid) {
		this.viewerid = viewerid;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.PUT;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		if(null != id){
			params.addQueryStringParameter("id", id);
		}
		params.addQueryStringParameter("user_id", userid);
		if(null != viewerid){
			params.addQueryStringParameter("viewer_id", viewerid);
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/play/count";
	}

}
