package com.intexh.bidong.userentity;

public class SendGiftItemEntity {
	private String id;
	private String from_id;
	private String to_id;
	private String gift_id;
	private int value;
	private int origin;
	private String remark;
	private int status;
	private String created_at;
	private String updated_at;
	private User from;
	private User to;
	private GiftItemEntity gift;
	
	
	public void setId(String id) {
		this.id = id;
	}
	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}
	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}
	public void setGift_id(String gift_id) {
		this.gift_id = gift_id;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void setOrigin(int origin) {
		this.origin = origin;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public void setFrom(User from) {
		this.from = from;
	}
	public void setTo(User to) {
		this.to = to;
	}
	public void setGift(GiftItemEntity gift) {
		this.gift = gift;
	}
	public String getId() {
		return id;
	}
	public String getFrom_id() {
		return from_id;
	}
	public String getTo_id() {
		return to_id;
	}
	public String getGift_id() {
		return gift_id;
	}
	public int getValue() {
		return value;
	}
	public int getOrigin() {
		return origin;
	}
	public String getRemark() {
		return remark;
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
	public User getTo() {
		return to;
	}
	public User getFrom() {
		return from;
	}
	public GiftItemEntity getGift() {
		return gift;
	}
	
}
