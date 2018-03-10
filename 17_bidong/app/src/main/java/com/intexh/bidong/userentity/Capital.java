package com.intexh.bidong.userentity;

public class Capital {
	private String id;
	private int balance;
	private String rate;
	private String account;
	private String issuers;
	private String created_at;
	private String updated_at;

	public int getRate() {
		double rate = Double.parseDouble(this.rate);
		return (int)(rate*100);
	}

	public String getId() {
		return id;
	}
	public int getBalance() {
		return balance;
	}
	public String getAccount() {
		return account;
	}
	public String getIssuers() {
		return issuers;
	}
	public String getCreated_at() {
		return created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
}
