package com.intexh.bidong.userentity;

public class VideoItemEntity {
	private String id;
	private String user_id;
	private String snapshort;
	private String video;
	private String[] photos;
	private String remark;
//	private boolean status;
	private int play_count;
	private int comm_count;
	private String loc;
	private String created_at;
	private String updated_at;
	public String getId() {
		return id;
	}
	public String getUser_id() {
		return user_id;
	}
	public String getSnapshort() {
		return snapshort;
	}
	public String getVideo() {
		return video;
	}
	public String[] getPhotos() {
		return photos;
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
	public String getLoc() {
		return loc;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	
}
