package com.intexh.bidong.userentity;

public class VersionUpdateEntity {
	private int id;
	private int os_type;
	private int code;
	private String name;
	private String url;
	private String change_log;
	private boolean forced_upgrade;
	private String created_date;
	private String updated_date;
	public int getId() {
		return id;
	}
	public int getOs_type() {
		return os_type;
	}
	public int getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public String getChange_log() {
		return change_log;
	}
	public boolean isForced_update() {
		return forced_upgrade;
	}
	public String getCreated_date() {
		return created_date;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	
}
