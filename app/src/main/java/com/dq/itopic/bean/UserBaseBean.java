package com.dq.itopic.bean;


import android.text.TextUtils;

import com.dq.itopic.tools.ValueUtil;

import java.io.Serializable;
import java.text.DecimalFormat;

public class UserBaseBean  implements Serializable {

	private String userid;
	private String name;
	private String avatar;
	private int gender;
	private int age;
	private int create_time;
	private double latitude;
	private double longitude;
	private int follow;
	private int open_time;

	private String cityname;//110000 or 411500

	// client 自己定义的
	private String distance;
	private String timeString;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getCreate_time() {
		return create_time;
	}

	public void setCreate_time(int create_time) {
		this.create_time = create_time;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getFollow() {
		return follow;
	}

	public void setFollow(int follow) {
		this.follow = follow;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public int getOpen_time() {
		return open_time;
	}

	public void setOpen_time(int open_time) {
		this.open_time = open_time;
		try {
			timeString = ValueUtil.getTimeStringFromNow(open_time);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			timeString = "";
			e.printStackTrace();
		}
	}

	public String getTimeString(){
		return timeString;
	}

}
