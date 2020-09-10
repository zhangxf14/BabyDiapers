/**
 * 
 */
package com.example.babydiapers;

import java.sql.Blob;

/**
 * @author zhangxf14
 * 创建成员对象
 */
public  class BabyInfo {
	private String Usercode;
	private byte[]  UserPictrue;
	private String Username;
	private String Time;
	private String Numbers;

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
	
	public String getTime() {
		return Time;
	}
	public void setTime(String Time) {
		this.Time = Time;
	}
	
	public String getNumbers() {
		return Numbers;
	}
	public void setNumbers(String Numbers) {
		this.Numbers = Numbers;
	}

}
