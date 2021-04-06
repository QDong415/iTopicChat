package com.dq.itopic.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dq.itopic.tools.ValueUtil;


public class UserBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9223008374062009177L;
	private String userid;
	private String mobile;
	private String name;
	private String avatar;
	
	private String intro;//个人介绍
	private int gender;
	private int age;

	private String cityid;//110000 or 411500
	private String cityname;//北京市

	private int slience;//0 ==正常收消息，1==静音

	//只有自己才有
	private String cid;//个推clientid,如果与客户端自己取到的不同,客户端需要调用 /User/Update 更新cid
	private String hwid;//华为推送token,如果与客户端自己取到的不同,客户端需要调用 /User/Update 更新hwid

	private int followcount;
	private int fanscount;
	private int topiccount;
	private int videocount;

	//查看他人接口才有
	private int follow;

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}



	public int getFanscount() {
		return fanscount;
	}


	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public int getFollow() {
		return follow;
	}

	public void setFollow(int follow) {
		this.follow = follow;
	}

	public String getHwid() {
		return hwid;
	}

	public void setHwid(String hwid) {
		this.hwid = hwid;
	}


	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getTopiccount() {
		return topiccount;
	}

	public void setTopiccount(int topiccount) {
		this.topiccount = topiccount;
	}


	public void setFanscount(int fanscount) {
		this.fanscount = fanscount;
	}

	public int getFollowcount() {
		return followcount;
	}

	public void setFollowcount(int followcount) {
		this.followcount = followcount;
	}

	public int getVideocount() {
		return videocount;
	}

	public void setVideocount(int videocount) {
		this.videocount = videocount;
	}

	public int getSlience() {
		return slience;
	}

	public void setSlience(int slience) {
		this.slience = slience;
	}
}
