package com.dq.itopic.bean;

public class StringResponse extends BaseResponse {
	private String data;
	private String tag;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
