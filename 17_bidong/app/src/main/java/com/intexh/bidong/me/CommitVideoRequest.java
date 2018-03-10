package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.utils.StringUtil;

public class CommitVideoRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private String snapshot;
	private String video;
	private String[] photos;
	private String remark;
	private String loc;
	private String city;
	private String district;
	private String street;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public void setPhotos(String[] photos) {
		this.photos = photos;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		if(null != snapshot) {
			params.addQueryStringParameter("snapshort", snapshot);
		}
		if(null != video){
			params.addQueryStringParameter("video", video);
		}
		if(null != photos){
			params.addQueryStringParameter("photos", StringUtil.join(",", photos));
		}
		params.addQueryStringParameter("loc", loc);
		params.addQueryStringParameter("city", city);
		params.addQueryStringParameter("district", district);
		params.addQueryStringParameter("street", street);
		params.addQueryStringParameter("remark", remark);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/add";
	}

}
