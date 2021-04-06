package com.dq.itopic.bean;


public class SendMessageResponse extends BaseResponse {

	private String targetid;
	private String client_messageid;

	public String getClient_messageid() {
		return client_messageid;
	}

	public void setClient_messageid(String client_messageid) {
		this.client_messageid = client_messageid;
	}

	public String getTargetid() {
		return targetid;
	}

	public void setTargetid(String targetid) {
		this.targetid = targetid;
	}
}
