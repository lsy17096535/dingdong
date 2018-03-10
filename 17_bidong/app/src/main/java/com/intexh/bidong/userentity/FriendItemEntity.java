package com.intexh.bidong.userentity;

import com.intexh.bidong.utils.DateUtil;

public class FriendItemEntity {
	private String id;
	private String user_id;
	private int status;			//0正常	1屏蔽	2删除
	private int oppo_status;	//-1不是对方好友	0是对方好友	1被对方屏蔽	2被对方删除
	private String created_at;
	private String updated_at;
	private User fans;
	private String sort_key;
	private String sort_letter;
	
	public long getUpdateTimestamp(){
		long ret = 0;
		long updateTime = DateUtil.getTimestamp(updated_at);
		long createTime = DateUtil.getTimestamp(created_at);
		if(updateTime > createTime){
			ret = updateTime;
		}else{
			ret = createTime;
		}
		return ret;
	}
	
	public int getOppo_status() {
		return oppo_status;
	}
	public void setOppo_status(int oppo_status) {
		this.oppo_status = oppo_status;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public void setFans(User fans) {
		this.fans = fans;
	}
	public String getSort_letter() {
		return sort_letter;
	}
	public void setSort_letter(String sort_letter) {
		this.sort_letter = sort_letter;
	}
	public String getSort_key() {
		return sort_key;
	}
	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}
	public String getId() {
		return id;
	}
	public String getUser_id() {
		return user_id;
	}
	public int getStatus() {
		return status;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public User getFans() {
		return fans;
	}
}
