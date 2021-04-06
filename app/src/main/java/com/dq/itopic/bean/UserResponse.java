package com.dq.itopic.bean;


public class UserResponse extends BaseResponse {

	private UserBean data;

	public UserBean getData() {
		return data;
	}

	public void setData(UserBean data) {
		this.data = data;
	}

}
