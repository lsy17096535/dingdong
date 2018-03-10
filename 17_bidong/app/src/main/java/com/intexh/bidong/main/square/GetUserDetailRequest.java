package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.User;

public class GetUserDetailRequest extends CommonAbstractDataManager<User> {

	private String userid;
	private int gender;
	private boolean is_video;
	private boolean is_newapi;

	public void setIs_newapi(boolean is_newapi) {
		this.is_newapi = is_newapi;
	}

	public void setIs_video(boolean is_video) {
		this.is_video = is_video;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
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
		params.addQueryStringParameter("gender", String.valueOf(gender));
		if(is_video){
			params.addQueryStringParameter("is_video", String.valueOf(is_video));
		}
		if(is_newapi){
			params.addQueryStringParameter("is_newapi", String.valueOf(is_newapi));
		}
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/" + userid + "/detail";
	}

}
