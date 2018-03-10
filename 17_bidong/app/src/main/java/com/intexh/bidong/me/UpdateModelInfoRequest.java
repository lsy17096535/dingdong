package com.intexh.bidong.me;

import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.client.HttpRequest;
import com.manteng.common.CommonAbstractDataManager;

/**
 * Created by shenxin on 15/12/28.
 */
public class UpdateModelInfoRequest extends CommonAbstractDataManager<String> {

    private String userid;
    private int bust;
    private int waist;
    private int hips;
    private int shoeSize;
    private String cups;
    private String country;
    private String offer;

    public void setShoeSize(int shoeSize) {
        this.shoeSize = shoeSize;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setBust(int bust) {
        this.bust = bust;
    }

    public void setWaist(int waist) {
        this.waist = waist;
    }

    public void setHips(int hips) {
        this.hips = hips;
    }

    public void setCups(String cups) {
        this.cups = cups;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    @Override
    protected HttpRequest.HttpMethod getHttpMethod() {
        return HttpRequest.HttpMethod.PUT;
    }

    @Override
    protected RequestParams getHttpParams() {
        RequestParams params = new RequestParams();
        params.addQueryStringParameter("bust",String.valueOf(bust));
        params.addQueryStringParameter("waist",String.valueOf(waist));
        params.addQueryStringParameter("hips",String.valueOf(hips));
        params.addQueryStringParameter("shoes",String.valueOf(shoeSize));
        params.addQueryStringParameter("country",country);
        params.addQueryStringParameter("offer",offer);
        if(null != cups){
            params.addQueryStringParameter("cups",cups);
        }
        return params;
    }

    @Override
    protected String getBusinessUrl() {
        return "/api/v1/user/" + userid + "/acting";
    }
}
