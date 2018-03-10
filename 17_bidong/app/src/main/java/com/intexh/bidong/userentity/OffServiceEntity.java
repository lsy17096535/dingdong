package com.intexh.bidong.userentity;

import java.io.Serializable;

public class OffServiceEntity implements Serializable{
	private static final long serialVersionUID = 100001L;

	private String id;
	private String user_id;
	private String item_id;
	private String price;
	private String unit;
	private String descr;
	private String[] idel_times;
	private String created_at;
	private int status;
	private String remark;
	private double distance;
	private OffServiceItemEntity item;
	private User user;

	public String getId() {
		return id;
	}

	public String getUser_id() {
		return user_id;
	}

	public String getItem_id() {
		return item_id;
	}

	public String getPrice() {
		return price;
	}

	public String getUnit() {
		return unit;
	}

	public String getDescr() {
		return descr;
	}

	public String[] getIdel_times() {
		return idel_times;
	}

	public String getCreated_at() {
		return created_at;
	}

	public int getStatus() {
		return status;
	}

	public String getRemark() {
		return remark;
	}

	public double getDistance() {
		return distance;
	}

	public OffServiceItemEntity getItem() {
		return item;
	}

	public User getUser() {
		return user;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setIdel_times(String[] idel_times) {
		this.idel_times = idel_times;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public void setItem(OffServiceItemEntity item) {
		this.item = item;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
