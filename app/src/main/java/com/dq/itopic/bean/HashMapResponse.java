package com.dq.itopic.bean;


import java.util.HashMap;

public class HashMapResponse extends BaseResponse {

	private HashMap<String,String> data;

	public HashMap<String,String> getData() {
		return data;
	}

	public void setData(HashMap<String,String> data) {
		this.data = data;
	}

}
