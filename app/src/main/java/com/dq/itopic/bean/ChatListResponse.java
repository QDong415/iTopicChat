package com.dq.itopic.bean;

import java.util.List;

public class ChatListResponse extends BaseResponse {

	private List<ChatBean> data;

	public List<ChatBean> getData() {
		return data;
	}

	public void setData(List<ChatBean> data) {
		this.data = data;
	}
}
