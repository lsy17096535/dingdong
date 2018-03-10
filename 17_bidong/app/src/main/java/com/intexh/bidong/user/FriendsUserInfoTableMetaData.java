/**
 * MessageHistoryTableMetaData.java
 * com.tc.client.blacktea.db
 * Copyright (c) 2012, TNT All Rights Reserved.
*/

package com.intexh.bidong.user;

import android.provider.BaseColumns;

/**
 * ClassName: OrderHistoryTableMetaData
 * @version  1.0
 * @since    v1.0
 * @Date	 2012-12-25 上午11:20:31 
 */
public class FriendsUserInfoTableMetaData implements BaseColumns{
	//Table Name
	public static final String HXUSERINFO_TABLE_NAME = "hxuserinfo";
	/*
	 * private String id;
	private String mobile;
	private String username;
	private String avatar;
	private String hxId;
	 * */
	public static final String ID = "ID";	
	public static final String HXID = "HXID";
	public static final String USER_ID = "USER_ID";				
	public static final String STATUS = "STATUS";				
	public static final String OPPO_STATUS = "OPPO_STATUS";
	public static final String CREATED_AT = "CRATED_AT";
	public static final String UPDATED_AT = "UPDATED_AT";
	public static final String FANS = "FANS";
	public static final String TIMESTAMP = "TIMESTAMP";
	/**
	 * Create SQL
	 */
	public static final String CREATE_ORDER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ HXUSERINFO_TABLE_NAME
			+ "("
			+ ID
			+ " TEXT,"
			+ HXID
			+ " TEXT,"
			+ USER_ID
			+ " TEXT,"
			+ STATUS
			+ " INTEGER,"
			+ OPPO_STATUS
			+ " INTEGER,"
			+ CREATED_AT
			+ " TEXT,"
			+ UPDATED_AT
			+ " TEXT," 
			+ FANS 
			+ " TEXT," 
			+ TIMESTAMP 
			+ " INTEGER " + 
			");";

	/**
	 * Drop SQL
	 */
	public static final String DROP_ORDER_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ HXUSERINFO_TABLE_NAME;
}

