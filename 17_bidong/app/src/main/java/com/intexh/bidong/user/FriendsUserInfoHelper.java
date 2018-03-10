package com.intexh.bidong.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class FriendsUserInfoHelper extends SQLiteOpenHelper {

	// database name
	public static final String DATABASE_NAME = "hxuserinfo.db";

	public static final int DATABASE_VERSION = 1;

	private static FriendsUserInfoHelper mHelper;

	/**
	 * 单例<br>
	 * 
	 * @method getInstance
	 * @param ctx
	 * @return
	 * @throws
	 * @since v1.0
	 */
	public static synchronized FriendsUserInfoHelper getInstance(Context ctx) {
		if (mHelper == null) {
			return new FriendsUserInfoHelper(ctx);
		} else {
			return mHelper;
		}
	}

	private FriendsUserInfoHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	FriendsUserInfoHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(FriendsUserInfoTableMetaData.CREATE_ORDER_TABLE_SQL);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// 创建上传图片数据表
		db.execSQL(FriendsUserInfoTableMetaData.CREATE_ORDER_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
