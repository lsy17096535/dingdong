package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class ReportUserRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private String reportid;
	private String content;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setReportid(String reportid) {
		this.reportid = reportid;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected RequestParams getHttpParams() {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("user_id", userid);
		params.addQueryStringParameter("report_id", reportid);
		params.addQueryStringParameter("content", content);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/user/report";
	}

}
