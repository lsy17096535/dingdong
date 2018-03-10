package com.intexh.bidong.userentity;

public class WeChatPayParamsEntity {
	private String appid;
	private String noncestr;
	private String packageStr;
	private String partnerid;
	private String prepayid;
	private String timestamp;
	private String sign;
	public String getAppid() {
		return appid;
	}
	public String getNoncestr() {
		return noncestr;
	}
	public String getPackageStr() {
		return packageStr;
	}
	public String getPartnerid() {
		return partnerid;
	}
	public String getPrepayid() {
		return prepayid;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public String getSign() {
		return sign;
	}
	
}
