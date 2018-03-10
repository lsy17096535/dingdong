package com.intexh.bidong.user;

import com.manteng.common.GsonUtils;

import com.intexh.bidong.userentity.FriendItemEntity;
import com.intexh.bidong.userentity.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class FriendsUserInfoDao {
	FriendsUserInfoHelper bHelp = null;

	public FriendsUserInfoDao(Context context) {
		bHelp = FriendsUserInfoHelper.getInstance(context);
	}
	
	/**
	 * 插入单条签到
	 * @param msg
	 * @return
	 */
	public boolean insertHXUserInfo(FriendItemEntity userInfo) {
		boolean ret = true;
		SQLiteDatabase db = null;
		try {
			db = bHelp.getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put(FriendsUserInfoTableMetaData.ID, userInfo.getId());
			cv.put(FriendsUserInfoTableMetaData.HXID, userInfo.getFans().getHxId());
			cv.put(FriendsUserInfoTableMetaData.USER_ID, userInfo.getUser_id());
			cv.put(FriendsUserInfoTableMetaData.STATUS, userInfo.getStatus());
			cv.put(FriendsUserInfoTableMetaData.OPPO_STATUS, userInfo.getOppo_status());
			cv.put(FriendsUserInfoTableMetaData.CREATED_AT, userInfo.getCreated_at());
			cv.put(FriendsUserInfoTableMetaData.UPDATED_AT,userInfo.getUpdated_at());
			cv.put(FriendsUserInfoTableMetaData.FANS, GsonUtils.objToJson(userInfo.getFans()));
			cv.put(FriendsUserInfoTableMetaData.TIMESTAMP, System.currentTimeMillis());
			long result = db
					.insert(FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME, null, cv);
			if (result == -1) {
				ret = false;
			}
		} catch (Exception e) {
			ret = false;
		}finally{
			if(null != db && db.isOpen()){
				db.close();
			}
		}
		return ret;
	}
	
	private boolean updateHXUserInfo(FriendItemEntity userInfo) {
		boolean ret = true;
		SQLiteDatabase db = null;
		try {
			db = bHelp.getWritableDatabase();
			ContentValues cv = new ContentValues();

			long now = System.currentTimeMillis();
			cv.put(FriendsUserInfoTableMetaData.USER_ID, userInfo.getUser_id());
			cv.put(FriendsUserInfoTableMetaData.HXID, userInfo.getFans().getHxId());
			cv.put(FriendsUserInfoTableMetaData.STATUS, userInfo.getStatus());
			cv.put(FriendsUserInfoTableMetaData.OPPO_STATUS, userInfo.getOppo_status());
			cv.put(FriendsUserInfoTableMetaData.CREATED_AT, userInfo.getCreated_at());
			cv.put(FriendsUserInfoTableMetaData.UPDATED_AT,userInfo.getUpdated_at());
			cv.put(FriendsUserInfoTableMetaData.FANS, GsonUtils.objToJson(userInfo.getFans()));
			cv.put(FriendsUserInfoTableMetaData.TIMESTAMP, now);
			long result = db.update(FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME, cv, " "
					+ FriendsUserInfoTableMetaData.ID + " = ?",
					new String[] { userInfo.getId() });
			if (result == -1) {
				ret = false;
			}
		} catch (Exception e) {
			ret = false;
		}finally{
			if(db.isOpen()){
				db.close();
			}
		}
		return ret;
	}
	
	/*
	 * 图片信息是否存在
	 * */
	public boolean isHXUserInfoExists(FriendItemEntity userInfo){
		boolean ret = false;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = bHelp.getWritableDatabase();
			cursor = db.rawQuery("select * from "
					+ FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME + " where "
					+ FriendsUserInfoTableMetaData.ID + " = ?  "
					, new String[] {
					userInfo.getId() });
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						String id = cursor
								.getString(cursor
										.getColumnIndex(FriendsUserInfoTableMetaData.ID));
						if (id != null) {
							ret = true;
						}
					}
				}
			}
		} catch (Exception e) {
			ret = false;
		}finally{
			if(null != cursor && !cursor.isClosed()){
				cursor.close();
			}
			if(null != db && db.isOpen()){
				db.close();
			}
		}
		return ret;
	}
	
	/*
	 */
	public void saveHXUserInfo(FriendItemEntity info) {
		if (!isHXUserInfoExists(info)) {
			insertHXUserInfo(info);
		}else{
			updateHXUserInfo(info);
		}
	}
	
	/*
	 */
	public void deleteHXUserInfo(FriendItemEntity info) {
		SQLiteDatabase db = null;
		try {
			db = bHelp.getWritableDatabase();
			int result = db.delete(FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME,
					FriendsUserInfoTableMetaData.ID + " = ? ", new String[] { info.getId() });
			if (result < 0) {
			}
		} catch (Exception e) {
		} finally {
			if (null != db && db.isOpen()) {
				db.close();
			}
		}
	}

	public FriendItemEntity getFriendUserInfoByHXID(String hxid){
		FriendItemEntity ret = null;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = bHelp.getWritableDatabase();
			cursor = db.query(FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME, null, FriendsUserInfoTableMetaData.HXID  + " ? ",
					new String[] {hxid}, null, null, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						ret = new FriendItemEntity();
						ret.setId(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.ID)));
						ret.setUser_id(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.USER_ID)));
						ret.setStatus(cursor.getInt(cursor.getColumnIndex(FriendsUserInfoTableMetaData.STATUS)));
						ret.setOppo_status(cursor.getInt(cursor.getColumnIndex(FriendsUserInfoTableMetaData.OPPO_STATUS)));
						ret.setCreated_at(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.CREATED_AT)));
						ret.setUpdated_at(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.UPDATED_AT)));
						String userStr = cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.FANS));
						ret.setFans(GsonUtils.jsonToObj(userStr, User.class));
					}
				}
			}
		} catch (Exception e) {
			ret = null;
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
			if(null != db && db.isOpen()){
				db.close();
			}
		}
		return ret;
	}
	
	public FriendItemEntity getFriendUserInfo(String hxid){
		FriendItemEntity ret = null;
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = bHelp.getWritableDatabase();
			cursor = db.query(FriendsUserInfoTableMetaData.HXUSERINFO_TABLE_NAME, null, FriendsUserInfoTableMetaData.HXID  + " = ? ",
					new String[] {hxid}, null, null, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						ret = new FriendItemEntity();
						ret.setId(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.ID)));
						ret.setUser_id(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.USER_ID)));
						ret.setStatus(cursor.getInt(cursor.getColumnIndex(FriendsUserInfoTableMetaData.STATUS)));
						ret.setOppo_status(cursor.getInt(cursor.getColumnIndex(FriendsUserInfoTableMetaData.OPPO_STATUS)));
						ret.setCreated_at(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.CREATED_AT)));
						ret.setUpdated_at(cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.UPDATED_AT)));
						String userStr = cursor.getString(cursor.getColumnIndex(FriendsUserInfoTableMetaData.FANS));
						ret.setFans(GsonUtils.jsonToObj(userStr, User.class));
					}
				}
			}
		} catch (Exception e) {
			ret = null;
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
			if(null != db && db.isOpen()){
				db.close();
			}
		}
		return ret;
	}
	
}
