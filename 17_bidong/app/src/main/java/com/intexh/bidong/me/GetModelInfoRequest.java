package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.ModelInfoEntity;

/**
 * Created by shenxin on 15/12/28.
 */
public class GetModelInfoRequest extends CommonAbstractDataManager<ModelInfoEntity> {

    private String userid;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.GET;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" + userid + "/acting";
    }
}
