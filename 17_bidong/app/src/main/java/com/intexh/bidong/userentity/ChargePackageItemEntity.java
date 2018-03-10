package com.intexh.bidong.userentity;

public class ChargePackageItemEntity {

	private String id;
	private String name;
	private int amount;			//金额
	private int value;			//价值多少金币
	private boolean status;
	private String remark;
	private String created_at;
	private String updated_at;
	private String badge_name;
	private String badge_uri;

	public String getBadge_name() {
		return badge_name;
	}

	public String getBadge_uri() {
		return badge_uri;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public int getAmount() {
		return amount;
	}
	public int getValue() {
		return value;
	}
	public boolean isStatus() {
		return status;
	}
	public String getRemark() {
		return remark;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	
}
