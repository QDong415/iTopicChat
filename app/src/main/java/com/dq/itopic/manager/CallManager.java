package com.dq.itopic.manager;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoEncoderConfiguration;
import android.util.Log;

import com.dq.itopic.tools.ServiceConstants;

public class CallManager {

	public enum CallStatus {
		CallHangup,// 初始状态
		CallDialing,//正在呼出
		CallIncoming,//正在呼入
		CallActive,// 正在通话
	}

	public String channelId;//通话房间ID
	public String targetId;//如果是单聊，targetId = 通话的对象userID；群聊 targetId = 群聊id；目前只支持单聊
	public String other_name;//目前只支持单聊，对方的名字
	public String other_photo;//目前只支持单聊，对方的头像
	public CallStatus callStatus;//通话的当前状态, 重要
	public long startTime;// 通话开始的时间,如果是用户呼出的通话，则startTime为通话呼出时间；如果是呼入的通话，则startTime为通话呼入时间。
	public long connectedTime;//通话接通时间
	public int mediaType;//视频通话，还是语音通话

	private ITopicApplication mApp;
	public RtcEngine mRtcEngine;//声网sdk

	public CallManager(ITopicApplication mApp) {
		this.mApp = mApp;
		this.callStatus = CallStatus.CallHangup;
	}

	public void initializeEngine(IRtcEngineEventHandler handler) {
		if (mRtcEngine != null) mRtcEngine.destroy();
		try {
			mRtcEngine = RtcEngine.create(mApp, ServiceConstants.AGORA_APPID, handler);
		} catch (Exception e) {
			throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
		}
	}

	public void setupVideoConfig() {
		// In simple use cases, we only need to enable video capturing
		// and rendering once at the initialization step.
		// Note: audio recording and playing is enabled by default.
		mRtcEngine.enableVideo();
		mRtcEngine.enableAudio();
		// Please go to this page for detailed explanation
		// https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
		mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
				VideoEncoderConfiguration.VD_640x360,
				VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
				VideoEncoderConfiguration.STANDARD_BITRATE,
				VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
	}

}
