package com.intexh.bidong.userentity;

public class CommentItemEntity {
	private String id;
	private String video_id;
	private String content;
	private String created_at;
	private User user;
	
	public void setId(String id) {
		this.id = id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getId() {
		return id;
	}
	public String getVideo_id() {
		return video_id;
	}
	public String getContent() {
		return content;
	}
	public String getCreated_at() {
		return created_at;
	}
	public User getUser() {
		return user;
	}
	
}
