package com.intexh.bidong.userentity;

import com.manteng.common.MD5Util;

import java.io.Serializable;

import com.intexh.bidong.utils.UserUtils;

public class User implements Serializable {
	private static final long serialVersionUID = 10000L;
	private String id;
	private String mobile;
	private String username;
	private String alias;
	private String avatar;
	private String avatar_new;
	private int gender;	//0未设置，1女 2男
	private int age;
	private String height;
	private String weight;
	private String signature;
	private String occup;
	private String province;
	private String city;
	private String district;
	private double[] location;
	private String loc;
	private String udid;
	private int status;	//0正常，1锁定，2封号
	private String created_at;
	private String updated_at;
	private String video;
	private String snapshort;
	private String video_id;
	private String sort_key;
	private String hxId;
	private long lastUpdateTime;
	private int level;
	private boolean is_audit;
	private boolean is_acting;
	private boolean is_top;
	private BadgeItemEntity[] badges;
	private VideoItemEntity[] videos;
	private OffServiceEntity[] acts;

	public OffServiceEntity[] getActs() {
		return acts;
	}

	public void setActs(OffServiceEntity[] acts) {
		this.acts = acts;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public boolean is_acting() {
		return is_acting;
	}

	public boolean is_audit() {
		return is_audit;
	}

	public boolean is_top(){
		return is_top;
	}

	public void setIs_audit(boolean is_audit) {
		this.is_audit = is_audit;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public BadgeItemEntity[] getBadges() {
		return badges;
	}

	public void setBadges(BadgeItemEntity[] badges) {
		this.badges = badges;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public void setHxId(String id){
		hxId = id;
	}
	
	public String getHxId(){
		if(null == hxId){
			hxId = MD5Util.getMD5Format(id);
		}
		return hxId;
	}
	
	public String getSort_key() {
		return sort_key;
	}
	public void setSort_key(String sort_key) {
		this.sort_key = sort_key;
	}
	public VideoItemEntity[] getVideos() {
		return videos;
	}
	public double[] getLocation() {
		return location;
	}
	public String getLoc() {
		return loc;
	}
	public String getSnapshort() {
		return snapshort;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getSnapshot() {
		return snapshort;
	}
	public void setSnapshot(String snapshot) {
		this.snapshort = snapshot;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAlias() {
		if(null == alias){
			return username;
		}
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getAvatar_new() {
		return avatar_new;
	}
	public void setAvatar_new(String avatar_new) {
		this.avatar_new = avatar_new;
	}

	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
//	public String getBirthday() {
//		return birthday;
//	}
//	public void setBirthday(String birthday) {
//		this.birthday = birthday;
//	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getOccup() {
		return occup;
	}
	public void setOccup(String occup) {
		this.occup = occup;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		if(is_top() && !getId().equals(UserUtils.getUserInfo().getUser().getId())){
			return UserUtils.getUserInfo().getUser().getCity();
		}
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		if(is_top() && !getId().equals(UserUtils.getUserInfo().getUser().getId())){
			return "";
		}
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	private String password;
	public String getId() {
		return id;
	}
	public String getPassword() {
		return password;
	}
	public String getMobile() {
		return mobile;
	}
	
}
