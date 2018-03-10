package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

/**
 * Created by shenxin on 15/12/28.
 */
public class UpdateModelWorksRequest extends CommonAbstractDataManager<String> {
    private String worksId;
    private String title;

    public void setWorksId(String worksId) {
        this.worksId = worksId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.PUT;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("title",title);
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" + worksId + "/works";
    }
}
