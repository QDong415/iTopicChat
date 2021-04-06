package com.dq.itopic.activity.chat.call;

import com.dq.itopic.manager.CallManager;

public class CallMessageUpdataEvent {

    private String channelId;

    private CallManager.CallStatus callState;
    private String content;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CallManager.CallStatus getCallState() {
        return callState;
    }

    public void setCallState(CallManager.CallStatus callState) {
        this.callState = callState;
    }
}
