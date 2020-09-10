package com.example.babydiapers.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 *@author Sai
 *Created on 2014年9月10日 下午21:53:39
 *类说明：数据库
 */
public class DBHelper extends SQLiteOpenHelper{

	public DBHelper(Context context) {
		this(context, "DB_BABYDIAPERS", null, 1);
	}
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+"TB_USERINFO"+"(" +
				"USERCODE"+" INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT ," +
				"USERPICTRUE"+" BLOB," +
				"USERNAME"+" varchar(20)," +
				"SEX"+" varchar(20)," +
				"BIRTHDATE"+" varchar(20)" +
				");");	
		
		db.execSQL("CREATE TABLE "+"TB_USERID"+"(" +
				"ID"+" INTEGER " +				
				");");
		
		db.execSQL("CREATE TABLE "+"TB_BABYINFO"+"(" +
//				"USERCODE"+" INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT ," +
				"USERCODE"+" varchar(20),"+
//				"USERPICTRUE"+" BLOB," +
				"USERNAME"+" varchar(20)," +
				"TIME"+" varchar(20)," +
				"NUMBERS"+" varchar(20)" +
				");");	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
