package com.intexh.bidong.userentity;

public class GiftItemEntity {
	private String id;
	private String name;
	private String spec;
	private String brand;
	private String unit;
	private int category_id;
	private String category_name;
	private int price;
	private String uri;
	private int gender;
	private int value;
	private int count;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getValue() {
		return value;
	}
	public int getCount() {
		return count;
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getSpec() {
		return spec;
	}
	public String getBrand() {
		return brand;
	}
	public String getUnit() {
		return unit;
	}
	public int getCategory_id() {
		return category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public int getPrice() {
		return price;
	}
	public String getUrl() {
		return uri;
	}
	public int getGender() {
		return gender;
	}
	
}
