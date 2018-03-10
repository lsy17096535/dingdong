package com.intexh.bidong.contact;

import com.intexh.bidong.userentity.FriendItemEntity;

import java.util.ArrayList;
import java.util.List;


public class ContactUsers {
	private String sort;
	private List<FriendItemEntity> users = new ArrayList<FriendItemEntity>();
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public List<FriendItemEntity> getUsers() {
		return users;
	}
	public void setUsers(List<FriendItemEntity> users) {
		this.users = users;
	}
	
	
}
