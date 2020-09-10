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
public class UserIdDao {
	private static UserIdDao instance;
	private Context context;
	private static String table = "TB_USERID";

	public UserIdDao(Context context) {
		this.context = context;
	}

	public static UserIdDao getInstance(Context context) {
		if (instance == null) {
			instance = new UserIdDao(context);
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
	public ContentValues objectToValue(UserId data){
		ContentValues values=new ContentValues();
		values.put("ID", data.getId());		
		return values;
	}
	public Long insert(UserId data) {
		return DBManager.getInstance(context).insert(table, objectToValue(data));
	}
	public ArrayList<UserId> queryAll(){
		ArrayList<UserId> mDatas=new ArrayList<UserId>();
		Cursor mCursor = DBManager.getInstance(context).query(table, null, null,
				null, null, null, null);
		while (mCursor.moveToNext()) {
			UserId data= new UserId();
			int id=mCursor.getInt(mCursor.getColumnIndex("ID"));
						
			data.setId(id);
			mDatas.add(data);
		}
		mCursor.close();
		DBManager.getInstance(context).closeDatabase();
		return mDatas;
	}
	
//	public ArrayList<UserInfo> query(String iUsercode) {
//		ArrayList<UserInfo> mDatas=new ArrayList<UserInfo>();
//		String selection = "USERCODE" + "=? ";
//				
//		String[] selectionArgs = { iUsercode };		
//		Cursor mCursor = DBManager.getInstance(context).query(table, null,
//				selection, selectionArgs, null, null, null);
//		
//		while (mCursor.moveToNext()) {
//			UserInfo data= new UserInfo();
//			String usercode=mCursor.getString(mCursor.getColumnIndex("USERCODE"));
//			byte[] userPictrue=mCursor.getBlob(mCursor.getColumnIndex("USERPICTRUE"));
//			String username=mCursor.getString(mCursor.getColumnIndex("USERNAME"));
//			String sex=mCursor.getString(mCursor.getColumnIndex("SEX"));
//			String birthdate=mCursor.getString(mCursor.getColumnIndex("BIRTHDATE"));			
//			
//			data.setUsercode(usercode);
//			data.setUserPictrue(userPictrue);
//			data.setUsername(username);
//			data.setSex(sex);
//			data.setBirthdate(birthdate);
//			
//			mDatas.add(data);	
//		}		
//		
//		mCursor.close();
//		DBManager.getInstance(context).closeDatabase();
//		return mDatas;
//	}
}
