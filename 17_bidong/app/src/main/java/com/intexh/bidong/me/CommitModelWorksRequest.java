package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

/**
 * Created by shenxin on 15/12/28.
 */
public class CommitModelWorksRequest extends CommonAbstractDataManager<String[]> {

    private String userid;
    private String title;
    private String uri;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.POST;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("title",title);
        params.addQueryStringParameter("uri",uri);
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" +  userid + "/works";
    }

    @Override
    protected Class<String[]> getClassName() {
        return String[].class;
    }
}
