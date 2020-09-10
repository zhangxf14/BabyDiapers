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
public class UserInfoDao {
	private static UserInfoDao instance;
	private Context context;
	private static String table = "TB_USERINFO";

	public UserInfoDao(Context context) {
		this.context = context;
	}

	public static UserInfoDao getInstance(Context context) {
		if (instance == null) {
			instance = new UserInfoDao(context);
		}
		return instance;
	}
//	private void cursorToValue(Cursor cursor){
//		String userCode=cursor.getString(cursor.getColumnIndex(Constants.USERCODE));
////		String userName=cursor.getString(cursor.getColumnIndex(Constants.USERNAME));
//		UserInfo.getInstance().setUserAccount(userCode);
//		return;
//	}
//	public TagInfo query(String userCode){
//		String selection = Constants.USERCODE + "=?";
//		String[] selectionArgs = { String.valueOf(id) };
//		Cursor mCursor = DBManager.getInstance(context).query(table, null, selection,
//				selectionArgs, null, null, null);
//		TagInfo data = new TagInfo();
//		while (mCursor.moveToNext()) {
//			mCursor.getString(arg0)
//		}
//		mCursor.close();
//		DBManager.getInstance(context).closeDatabase();
//		return data;
//	}
	public ContentValues objectToValue(UserInfo data){
		ContentValues values=new ContentValues();
		values.put("USERCODE", data.getUsercode());
		values.put("USERPICTRUE", data.getUserPictrue());
		values.put("USERNAME", data.getUsername());
		values.put("SEX", data.getSex());
		values.put("BIRTHDATE", data.getBirthdate());
		
		return values;
	}
	public Long insert(UserInfo data) {
		return DBManager.getInstance(context).insert(table, objectToValue(data));
	}
	public ArrayList<UserInfo> queryAll(){
		ArrayList<UserInfo> mDatas=new ArrayList<UserInfo>();
		Cursor mCursor = DBManager.getInstance(context).query(table, null, null,
				null, null, null, null);
		while (mCursor.moveToNext()) {
			UserInfo data= new UserInfo();
			String usercode=mCursor.getString(mCursor.getColumnIndex("USERCODE"));
			byte[] userPictrue=mCursor.getBlob(mCursor.getColumnIndex("USERPICTRUE"));
			String username=mCursor.getString(mCursor.getColumnIndex("USERNAME"));
			String sex=mCursor.getString(mCursor.getColumnIndex("SEX"));
			String birthdate=mCursor.getString(mCursor.getColumnIndex("BIRTHDATE"));			
			
			data.setUsercode(usercode);
			data.setUserPictrue(userPictrue);
			data.setUsername(username);
			data.setSex(sex);
			data.setBirthdate(birthdate);
			
			mDatas.add(data);
		}
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}
	
	public ArrayList<UserInfo> query(String iUsercode) {
		ArrayList<UserInfo> mDatas=new ArrayList<UserInfo>();
		String selection = "USERCODE" + "=? ";
				
		String[] selectionArgs = { iUsercode };		
		Cursor mCursor = DBManager.getInstance(context).query(table, null,
				selection, selectionArgs, null, null, null);
		
		while (mCursor.moveToNext()) {
			UserInfo data= new UserInfo();
			String usercode=mCursor.getString(mCursor.getColumnIndex("USERCODE"));
			byte[] userPictrue=mCursor.getBlob(mCursor.getColumnIndex("USERPICTRUE"));
			String username=mCursor.getString(mCursor.getColumnIndex("USERNAME"));
			String sex=mCursor.getString(mCursor.getColumnIndex("SEX"));
			String birthdate=mCursor.getString(mCursor.getColumnIndex("BIRTHDATE"));			
			
			data.setUsercode(usercode);
			data.setUserPictrue(userPictrue);
			data.setUsername(username);
			data.setSex(sex);
			data.setBirthdate(birthdate);
			
			mDatas.add(data);	
		}		
		
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}
}
