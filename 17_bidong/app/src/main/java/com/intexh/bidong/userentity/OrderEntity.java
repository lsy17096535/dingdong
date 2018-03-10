package com.intexh.bidong.userentity;

public class OrderEntity {
	private String id;
	private String from_id;
	private String to_id;
	private String app_time;
	private String app_descr;
	private int status;
	private String remark;
	private int amount;
	private int pay_status;
	private String created_at;
	private String updated_at;
	private User from;
	private User to;
	private OffServiceItemEntity item;
	private GiftItemEntity gift;


	public String getId() {
		return id;
	}

	public String getFrom_id() {
		return from_id;
	}

	public String getTo_id() {
		return to_id;
	}

	public String getApp_time() {
		return app_time;
	}

	public String getApp_descr() {
		return app_descr;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPay_status() {
		return pay_status;
	}

	public void setPay_status(int pay_status) {
		this.pay_status = pay_status;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public User getFrom() {
		return from;
	}

	public User getTo() {
		return to;
	}

	public OffServiceItemEntity getItem() {
		return item;
	}

	public GiftItemEntity getGift() {
		return gift;
	}
}
