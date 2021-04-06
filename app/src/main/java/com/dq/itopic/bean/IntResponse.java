package com.dq.itopic.bean;

public class IntResponse extends BaseResponse {
	private int data;
	private String tag;
	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
