package com.intexh.bidong.userentity;

public class TrendVideoEntity {
	private User user;
	private String id;
	private String snapshort;
	private String video;
	private String[] photos;
	private String remark;
//	private boolean status;
	private int play_count;
	private int comm_count;
	private String city;
	private String district;
	private String street;
	private String loc;
	private String created_at;
	private String updated_at;

	public void setUser(User user) {
		this.user = user;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setSnapshort(String snapshort) {
		this.snapshort = snapshort;
	}
	public void setVideo(String video) {
		this.video = video;
	}

	public String[] getPhotos() {
		return photos;
	}

	public void setPhotos(String[] photos) {
		this.photos = photos;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
//	public void setStatus(boolean status) {
//		this.status = status;
//	}
	public void setPlay_count(int play_count) {
		this.play_count = play_count;
	}
	public void setComm_count(int comm_count) {
		this.comm_count = comm_count;
	}
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public User getUser() {
		return user;
	}
	public String getId() {
		return id;
	}
	public String getSnapshort() {
		return snapshort;
	}
	public String getVideo() {
		return video;
	}
	public String getRemark() {
		return remark;
	}
//	public boolean isStatus() {
//		return status;
//	}
	public int getPlay_count() {
		return play_count;
	}
	public int getComm_count() {
		return comm_count;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	
	
}
