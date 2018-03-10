package com.intexh.bidong.userentity;

/**
 * Created by shenxin on 15/12/28.
 */
public class ModelInfoEntity {
    private String id;
    private int bust;
    private int waist;
    private int hips;
    private int shoes;
    private String country;
    private String offer;
    private String created_at;
    private String updated_at;
    private String cups;

    public int getShoes() {
        return shoes;
    }

    public String getId() {
        return id;
    }

    public int getBust() {
        return bust;
    }

    public int getWaist() {
        return waist;
    }

    public int getHips() {
        return hips;
    }

    public String getCountry() {
        return country;
    }

    public String getOffer() {
        return offer;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCups() {
        return cups;
    }
}
