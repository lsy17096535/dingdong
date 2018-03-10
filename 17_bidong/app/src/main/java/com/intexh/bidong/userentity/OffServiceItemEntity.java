package com.intexh.bidong.userentity;

import java.io.Serializable;

public class OffServiceItemEntity implements Serializable, Comparable {
	private static final long serialVersionUID = 100002L;

	private String id;
	private String name;
	private String price;
	private String unit;
	private String type_id;
	private String type_name;
	private String uri;
	private String created_at;
	private String updated_at;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String getUnit() {
		return unit;
	}

	public String getType_id() {
		return type_id;
	}

	public String getType_name() {
		return type_name;
	}

	public String getUri() {
		return uri;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	@Override
	public int compareTo(Object another) {
		OffServiceItemEntity ano = (OffServiceItemEntity)another;
		int thisId = Integer.parseInt(id);
		int anoId = Integer.parseInt(ano.id);
		if(thisId > anoId) {
			return 1;
		}
		else if(thisId < anoId){
			return -1;
		}
		else {
			return 0;
		}
	}
}
