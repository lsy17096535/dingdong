package com.intexh.bidong.main.square;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.manteng.common.CommonAbstractDataManager;

public class ReportVideoRequest extends CommonAbstractDataManager<String> {

	private String userid;
	private String videoid;
	private String reportid;
	private String content;
	
	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
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
		params.addQueryStringParameter("video_id", videoid);
		params.addQueryStringParameter("report_id", reportid);
		params.addQueryStringParameter("content", content);
		return params;
	}

	@Override
	protected String getBusinessUrl() {
		return "/api/v1/video/report";
	}

}
