package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.userentity.WatchItemEntity;
import com.intexh.bidong.utils.Page;

/**
 * Created by shenxin on 15/12/30.
 */
public class GetLikeMeRequest extends CommonAbstractDataManager<WatchItemEntity[]> {
    private String userid = null;
    private Page page = null;

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.GET;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        if(null != page){
            params.addQueryStringParameter("page",page.getStart() + "," + page.getCount());
        }
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" + userid + "/likers";
    }

    @Override
    protected Class<WatchItemEntity[]> getClassName() {
        return WatchItemEntity[].class;
    }
}
