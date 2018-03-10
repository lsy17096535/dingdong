package com.intexh.bidong.userentity;

import com.google.gson.annotations.SerializedName;

public class BucketEntity {

	@SerializedName("bucketName")
	private String buck;
	private String imageUrl;
	private int thumbnail;
	private String token;
	public String getBucketName() {
		return buck;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public int getThumbnail() {
		return thumbnail;
	}
	public String getToken() {
		return token;
	}
}
