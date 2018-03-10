package com.intexh.bidong.userentity;

public class RegDataEntity {
	private BucketEntity[] buckets;
	private User user;
	public BucketEntity[] getBuckets() {
		return buckets;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
