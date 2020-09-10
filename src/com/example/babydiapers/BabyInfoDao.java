package com.example.babydiapers;

import java.util.ArrayList;

import com.example.babydiapers.db.DBManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
/**
 * 用户记录Dao
 * @author zhangxf
 *
 */
public class BabyInfoDao {
	private static BabyInfoDao instance;
	private Context context;
	private static String table = "TB_BABYINFO";

	public BabyInfoDao(Context context) {
		this.context = context;
	}

	public static BabyInfoDao getInstance(Context context) {
		if (instance == null) {
			instance = new BabyInfoDao(context);
		}
		return instance;
	}

	public ContentValues objectToValue(BabyInfo data){
		ContentValues values=new ContentValues();
		values.put("USERCODE", data.getUsercode());
//		values.put("USERPICTRUE", data.getUserPictrue());
		values.put("USERNAME", data.getUsername());
		values.put("TIME", data.getTime());
		values.put("NUMBERS", data.getNumbers());
		
		return values;
	}
	public Long insert(BabyInfo data) {
		return DBManager.getInstance(context).insert(table, objectToValue(data));
	}
	public ArrayList<BabyInfo> queryAll(){
		ArrayList<BabyInfo> mDatas=new ArrayList<BabyInfo>();
		Cursor mCursor = DBManager.getInstance(context).query(table, null, null,
				null, null, null, "TIME"+" desc");
		while (mCursor.moveToNext()) {
			BabyInfo data= new BabyInfo();
			String usercode=mCursor.getString(mCursor.getColumnIndex("USERCODE"));
//			byte[] userPictrue=mCursor.getBlob(mCursor.getColumnIndex("USERPICTRUE"));
			String username=mCursor.getString(mCursor.getColumnIndex("USERNAME"));
			String time=mCursor.getString(mCursor.getColumnIndex("TIME"));
			String numbsers=mCursor.getString(mCursor.getColumnIndex("NUMBERS"));			
			
			data.setUsercode(usercode);
//			data.setUserPictrue(userPictrue);
			data.setUsername(username);
			data.setTime(time);
			data.setNumbers(numbsers);
			
			mDatas.add(data);
		}
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}
	
	public ArrayList<BabyInfo> query(String iUsercode) {
		ArrayList<BabyInfo> mDatas=new ArrayList<BabyInfo>();
		String selection = "USERCODE" + "=? ";
				
		String[] selectionArgs = { iUsercode };		
		Cursor mCursor = DBManager.getInstance(context).query(table, null,
				selection, selectionArgs, null, null, null);
		
		while (mCursor.moveToNext()) {
			BabyInfo data= new BabyInfo();
			String usercode=mCursor.getString(mCursor.getColumnIndex("USERCODE"));
//			byte[] userPictrue=mCursor.getBlob(mCursor.getColumnIndex("USERPICTRUE"));
			String username=mCursor.getString(mCursor.getColumnIndex("USERNAME"));
			String time=mCursor.getString(mCursor.getColumnIndex("TIME"));
			String numbsers=mCursor.getString(mCursor.getColumnIndex("NUMBERS"));			
			
			data.setUsercode(usercode);
//			data.setUserPictrue(userPictrue);
			data.setUsername(username);
			data.setTime(time);
			data.setNumbers(numbsers);
			
			mDatas.add(data);	
		}		
		
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}
}
