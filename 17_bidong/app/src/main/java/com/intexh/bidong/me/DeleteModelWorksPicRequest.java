package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

/**
 * Created by shenxin on 15/12/28.
 */
public class DeleteModelWorksPicRequest extends CommonAbstractDataManager<String> {
    private String workPicId;

    public void setWorkPicId(String workPicId) {
        this.workPicId = workPicId;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.DELETE;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" + workPicId + "/works";
    }
}
