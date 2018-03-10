package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

import com.intexh.bidong.utils.StringUtil;

public class UpdateMyOffServiceRequest extends CommonAbstractDataManager<String> {

    private String id;
    private String user_id;
    private String item_id;
    private int price;
    private String unit;
    private String[] idel_times;
    private String descr;

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setIdel_times(String[] idel_times) {
        this.idel_times = idel_times;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        if(null == id){
            return HttpRequest.HttpMethod.POST;
        }
        return HttpRequest.HttpMethod.PUT;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        if(null != user_id){
            params.addQueryStringParameter("user_id", user_id);
        }
        if(null != item_id){
            params.addQueryStringParameter("item_id", item_id);
        }
        if(0 <= price){
            params.addQueryStringParameter("price", String.valueOf(price));
        }
        if(null != unit){
            params.addQueryStringParameter("unit", unit);
        }
        if(null != idel_times && idel_times.length > 0){
            params.addQueryStringParameter("idel_times", StringUtil.join(",", idel_times));
        }
        if(null != descr){
            params.addQueryStringParameter("descr", descr);
        }
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        if(null == id){ //发布
            return "/api/v1/activity/add";
        }
        return "/api/v1/activity/update/" + id; //修改
    }
}
