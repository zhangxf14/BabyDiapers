/**
 * 
 */
package com.example.babydiapers;

import java.sql.Blob;

/**
 * @author zhangxf14
 * 创建成员对象
 */
public  class UserInfo {
	private String Usercode;
	private byte[]  UserPictrue;
	private String Username;
	private String Sex;
	private String Birthdate;

	public String getUsercode() {
		return Usercode;
	}
	public void setUsercode(String Usercode) {
		this.Usercode = Usercode;
	}

	public byte[] getUserPictrue() {
		return UserPictrue;
	}
	public void setUserPictrue(byte[] UserPictrue) {
		this.UserPictrue = UserPictrue;
	}
	
	public String getUsername() {
		return Username;
	}
	public void setUsername(String Username) {
		this.Username = Username;
	}
	
	public String getSex() {
		return Sex;
	}
	public void setSex(String Sex) {
		this.Sex = Sex;
	}
	
	public String getBirthdate() {
		return Birthdate;
	}
	public void setBirthdate(String Birthdate) {
		this.Birthdate = Birthdate;
	}

}
