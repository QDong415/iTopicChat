package com.dq.itopic.activity.chat.call;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dq.itopic.R;
import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.StringListResponse;
import com.dq.itopic.manager.CallManager;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.DBReq;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.LogUtil;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.HashMap;
import java.util.List;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_DECODING;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STARTING;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STOPPED;

public class SingleCallActivity extends BaseCallActivity implements Handler.Callback, EasyPermissions.PermissionCallbacks {

    private FrameLayout bodyFrameContainer;
    private FrameLayout rightTopFrameContainer;
    private FrameLayout mButtonContainer;
    private LinearLayout mUserInfoContainer;
    private boolean isInformationShow = false;

    private VideoCanvas mLocalVideo;//VideoCanvas 是声网的类
    private VideoCanvas mRemoteVideo;

    private boolean audiomuted = false;
    private boolean handFree = false;

    private boolean hangUpByMyself;//由我自己挂断的（仅用于leaveChannel判断一下是不是我主动挂断的来决定是否上传服务器）

    private int EVENT_FULL_SCREEN = 1;

    @Override
    final public boolean handleMessage(Message msg) {
        if (msg.what == EVENT_FULL_SCREEN) {
            hideVideoCallInformation();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rc_voip_activity_single_call);
        initView();

        EventBus.getDefault().register(this);
        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO){
            if (EasyPermissions.hasPermissions(this, VIDEO_CALL_PERMISSIONS)) {
                setupEngineAfterHasPermissions();
            } else {
                EasyPermissions.requestPermissions(this, "视频通话需要摄像头和麦克风权限",REQUIRE_PERMISSIONS_CODE_VIDEO, VIDEO_CALL_PERMISSIONS);
            }
        } else {
            if (EasyPermissions.hasPermissions(this, AUDIO_CALL_PERMISSIONS)) {
                setupEngineAfterHasPermissions();
            } else {
                EasyPermissions.requestPermissions(this, "语音通话需要摄像头权限",REQUIRE_PERMISSIONS_CODE_AUDIO, AUDIO_CALL_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == REQUIRE_PERMISSIONS_CODE_VIDEO){
            setupEngineAfterHasPermissions();
        } else if(requestCode == REQUIRE_PERMISSIONS_CODE_AUDIO){
            setupEngineAfterHasPermissions();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(requestCode == REQUIRE_PERMISSIONS_CODE_VIDEO){
            showToastByApplication("视频通话需要相机和麦克风权限");
            finish();
        } else if(requestCode == REQUIRE_PERMISSIONS_CODE_AUDIO){
            showToastByApplication("语音通话需要麦克风权限");
            finish();
        }
    }

    private void setupEngineAfterHasPermissions() {
        callManager.initializeEngine(mRtcEventHandler);
        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO) {
            callManager.setupVideoConfig();
            setupLocalVideo();
        } else {
            callManager.mRtcEngine.enableAudio();
        }
        if (callManager.callStatus == CallManager.CallStatus.CallDialing){
            callManager.mRtcEngine.joinChannel(null, callManager.channelId, "", Integer.parseInt(getUserID()));
        }
    }

    private void setupLocalVideo() {
        SurfaceView mLocalView = RtcEngine.CreateRendererView(this);
        mLocalView.setZOrderMediaOverlay(true);
        rightTopFrameContainer.addView(mLocalView);
        mLocalVideo = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        callManager.mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callManager.callStatus == CallManager.CallStatus.CallIncoming){
                        //说明我本次join是我同意接通来电，而不是一开始的我呼出
                        callManager.callStatus = CallManager.CallStatus.CallActive;
                        onCallConnected();
                        checkOppoStillInChannel();
                    }
                }
            });
        }

        @Override
        public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //看一下Leave之前是什么状态，记住，我自己的代码，调用leaveChannel之前不要修改callStatus

                    //看看之前的状态，如果是我正在呼出，对方未接听，我就挂断，那么需要push给对面
                    CallManager.CallStatus preCallStatus = callManager.callStatus;

//                    LogUtil.e("我onLeaveChannel, preCallStatus: " +preCallStatus);

                    String resultContent = onCallDisconnected(true);

                    if (hangUpByMyself){
                        //告诉服务器，我主动挂断了通话，之前的状态可能是 通话中 or 呼叫中
                        uploadStatusToService(CallManager.CallStatus.CallHangup, resultContent ,
                                preCallStatus == CallManager.CallStatus.CallDialing);
                    }
                }
            });
        }

        @Override
        public void onUserJoined(final int uid, int elapsed) {
            //远程有用户加入
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserJoined(uid);
                    onCallConnected();
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            //远端用户（通信场景）离开当前频道回调。
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.rc_voip_call_hang_up).setEnabled(false);
                    callManager.mRtcEngine.leaveChannel();
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(final int uid,final int state,final int reason, int elapsed) {
            //对方leaveChannel 回调： uid: （对方uid） state=0 reason7
            //具体参考https://docs.agora.io/cn/Voice/APIReference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler.html
//            LogUtil.e("onRemoteVideoStateChanged, uid: " + uid +" state="+state+" reason"+reason);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (state == REMOTE_VIDEO_STATE_STOPPED){
                        if (reason == REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED){
                            //对方关闭了摄像头
                            if (mRemoteVideo.view != null) {
                                mRemoteVideo.view.setVisibility(View.GONE);
                            }
                        } else if (reason == REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED){

                        } else if (reason == REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE){
                            //对方leaveChannel,sdk在回调这里之前的0.6秒会先回调onUserOffline
                        }
                    } else if (state == REMOTE_VIDEO_STATE_STARTING){
                        //我已接收远端视频首包，sdk在回调这里之前的0.6秒会先回调onUserJoined
                        //为了防止出现0.6秒的黑屏，把这里当做远端用户join

                    } else if (state == REMOTE_VIDEO_STATE_DECODING){
                        if (reason == REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED){
                            //对方打开了摄像头
                            if (mRemoteVideo.view != null) {
                                mRemoteVideo.view.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        if (pickupDetector != null && callManager.mediaType == ChatBean.SUBTYPE_CALL_AUDIO) {
//            pickupDetector.register(this);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (pickupDetector != null) {
//            pickupDetector.unRegister();
//        }
    }

    // initView方法只在onCreate时候调用一次
    private void initView() {

        bodyFrameContainer = (FrameLayout) findViewById(R.id.rc_voip_call_large_preview);
        rightTopFrameContainer = (FrameLayout) findViewById(R.id.rc_voip_call_small_preview);
        mButtonContainer = (FrameLayout) findViewById(R.id.rc_voip_btn);
        mUserInfoContainer = (LinearLayout) findViewById(R.id.rc_voip_user_info);

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout buttonLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        RelativeLayout userInfoLayout = null;

        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_AUDIO || callManager.callStatus == CallManager.CallStatus.CallIncoming) {
            //语音聊天 或者是 视频来电，那么就显示对方头像
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_audio_call_user_info_incoming, null);
            userInfoLayout.findViewById(R.id.iv_large_preview_Mask).setVisibility(View.VISIBLE);

            ImageView userPortrait = (ImageView) userInfoLayout.findViewById(R.id.rc_voip_user_portrait);
            GlideLoaderUtil.loadImage(this,ValueUtil.getQiniuUrlByFileName(callManager.other_photo,true),R.drawable.user_photo, userPortrait);
            TextView userName = (TextView) userInfoLayout.findViewById(R.id.rc_voip_user_name);
            userName.setText(callManager.other_name);

        } else {
            //视频聊天 且是 我主动发出的视频 界面
            userInfoLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_audio_call_user_info, null);
            TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
            callInfo.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.callkit_shadowcolor));
        }

        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_AUDIO) {
            //语音通话
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
            bodyFrameContainer.setVisibility(View.GONE);
            rightTopFrameContainer.setVisibility(View.GONE);

            if (callManager.callStatus == CallManager.CallStatus.CallIncoming) {
                //对方给我来电，显示对方头像
                buttonLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                ImageView iv_answerBtn = (ImageView) buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setBackground(ContextCompat.getDrawable( SingleCallActivity.this,R.drawable.rc_voip_audio_answer_selector_new));

                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.callkit_shadowcolor));
                callInfo.setText(R.string.rc_voip_audio_call_inviting);
                outOrIncomeCallRinging(false);
            }
        } else if (callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO){
            //视频通话
            if (callManager.callStatus == CallManager.CallStatus.CallIncoming) {
                //对方给我来电，显示对方头像
                findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(R.color.rc_voip_background_color));
                buttonLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_call_bottom_incoming_button_layout, null);
                ImageView iv_answerBtn = (ImageView) buttonLayout.findViewById(R.id.rc_voip_call_answer_btn);
                iv_answerBtn.setBackground(ContextCompat.getDrawable(SingleCallActivity.this,R.drawable.rc_voip_vedio_answer_selector_new));

                TextView callInfo = (TextView) userInfoLayout.findViewById(R.id.rc_voip_call_remind_info);
                callInfo.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.callkit_shadowcolor));
                callInfo.setText(R.string.rc_voip_video_call_inviting);
                outOrIncomeCallRinging(false);
            }

            rightTopFrameContainer.setVisibility(View.VISIBLE);
        }
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(buttonLayout);
        mUserInfoContainer.removeAllViews();
        mUserInfoContainer.addView(userInfoLayout);

        if (callManager.callStatus == CallManager.CallStatus.CallDialing) {
            //我正在主动呼出
//            userInfoLayout.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
            RelativeLayout layout = buttonLayout.findViewById(R.id.rc_voip_call_mute);
            layout.setVisibility(View.VISIBLE);
            ImageView button = buttonLayout.findViewById(R.id.rc_voip_call_mute_btn);
            button.setEnabled(false);
            buttonLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.VISIBLE);

            outOrIncomeCallRinging(true);
        }

        handFree = callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO;
    }

    //连接走通的回调，可能是对方进入了我的房间，也可能是我进入了别人的房间
    private void onCallConnected() {
        stopRing();
        LayoutInflater inflater = LayoutInflater.from(this);
        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_AUDIO) {
//            findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
            RelativeLayout btnLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
            ImageView button = btnLayout.findViewById(R.id.rc_voip_call_mute_btn);
            button.setEnabled(true);
            mButtonContainer.removeAllViews();
            mButtonContainer.addView(btnLayout);
        } else {
            // 二人视频通话接通后 mUserInfoContainer 中更换为无头像的布局
            mUserInfoContainer.removeAllViews();
            inflater.inflate(R.layout.rc_voip_video_call_user_info, mUserInfoContainer);
            TextView userName = mUserInfoContainer.findViewById(R.id.rc_voip_user_name);
            userName.setText(callManager.other_name);
            userName.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.callkit_shadowcolor));//callkit_shadowcolor
        }
        TextView tv_rc_voip_call_remind_info = (TextView) mUserInfoContainer.findViewById(R.id.rc_voip_call_remind_info);
        tv_rc_voip_call_remind_info.setShadowLayer(16F, 0F, 2F, getResources().getColor(R.color.callkit_shadowcolor));

        tv_rc_voip_call_remind_info.setVisibility(View.GONE);
        TextView remindInfoTextView = null;
        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_AUDIO) {
            remindInfoTextView = mUserInfoContainer.findViewById(R.id.tv_setupTime);
        } else {
            remindInfoTextView = mUserInfoContainer.findViewById(R.id.tv_setupTime_video);
        }
        if(remindInfoTextView == null){
            remindInfoTextView = tv_rc_voip_call_remind_info;
        }
        setupTime(remindInfoTextView);

        callManager.mRtcEngine.muteLocalAudioStream(audiomuted);
        View muteV = mButtonContainer.findViewById(R.id.rc_voip_call_mute);
        if (muteV != null) {
            muteV.setSelected(audiomuted);
        }

        callManager.mRtcEngine.setEnableSpeakerphone(handFree);
        View handFreeV = mButtonContainer.findViewById(R.id.rc_voip_handfree);
        if (handFreeV != null) {
            handFreeV.setSelected(handFree);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallRefuseMyDialingEvent mMessageEvent) {
        if (callManager.callStatus == CallManager.CallStatus.CallDialing){
            //对方拒绝了我的呼叫
            callManager.mRtcEngine.leaveChannel();
        } else if (callManager.callStatus == CallManager.CallStatus.CallIncoming){
            //我还没接呢，对方就取消通话了
            onCallDisconnected(false);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopRing();

        //暂时不做悬浮框功能的话，可以destroy
//        callManager.mRtcEngine.stopPreview();
        endCall();
        callManager.mRtcEngine = null;
        LogUtil.e("视频通话onDestroy");
        super.onDestroy();

        RtcEngine.destroy();
    }

    //远程有用户加入
    private void onRemoteUserJoined(final int remoteUserid) {
        callManager.callStatus = CallManager.CallStatus.CallActive;
        callManager.connectedTime = (int)(System.currentTimeMillis()/1000);

        if (callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO) {
            //视频聊天
            findViewById(R.id.rc_voip_call_information).setBackgroundColor(getResources().getColor(android.R.color.transparent));
            bodyFrameContainer.setVisibility(View.VISIBLE);

            bodyFrameContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInformationShow) {
                        hideVideoCallInformation();
                    } else {
                        showVideoCallInformation();
                        handler.sendEmptyMessageDelayed(EVENT_FULL_SCREEN, 5 * 1000);
                    }
                }
            });

            if (mRemoteVideo != null) {
                return;
            }

            ViewGroup parent = bodyFrameContainer;
            if (parent.indexOfChild(mLocalVideo.view) > -1) {
                parent = rightTopFrameContainer;
            }

//            int count = mLPreviewContainer.getChildCount();
//            View view = null;
//            for (int i = 0; i < count; i++) {
//                View v = mLPreviewContainer.getChildAt(i);
//                if (v.getTag() instanceof Integer && ((int) v.getTag()) == remoteUserid) {
//                    view = v;
//                }
//            }
//            if (view == null) {
//                mRemoteView = RtcEngine.CreateRendererView(this);
//                mLPreviewContainer.addView(mRemoteView);
//                callManager.mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUserid));
//                mRemoteView.setTag(remoteUserid);


            SurfaceView mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
            mRemoteView.setZOrderMediaOverlay(parent == rightTopFrameContainer);
            parent.addView(mRemoteView);
            mRemoteVideo = new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUserid);
            callManager.mRtcEngine.setupRemoteVideo(mRemoteVideo);
//            }

            /** 小窗口点击事件,切换我和对方的视频区域 **/
            rightTopFrameContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchVideoCanvasView(mLocalVideo);
                    switchVideoCanvasView(mRemoteVideo);
                }
            });
            mButtonContainer.setVisibility(View.GONE);
            mUserInfoContainer.setVisibility(View.GONE);
        }
    }

    //挂断单人视频，有两个地方会进入这里
    public void onHangupBtnClick(View view) {
//        unRegisterHeadsetplugReceiver();

        if (callManager.callStatus == CallManager.CallStatus.CallIncoming) {
            //来电话我没接，选择了直接挂断

            String resultContent = onCallDisconnected(true);

            //告诉服务器，我主动挂断了
            uploadStatusToService(CallManager.CallStatus.CallHangup, resultContent ,true);
        } else {
            //我主动结束了语音通话，通话中 or 呼叫中
            stopRing();
            hangUpByMyself = true;
            callManager.mRtcEngine.leaveChannel();
        }
    }

    private void uploadStatusToService(CallManager.CallStatus callStatus, String resultContent, boolean needPushToOp){
        HashMap<String,String> params = new HashMap<>();
        params.put("channelid",callManager.channelId);
        params.put("subtype",String.valueOf(callManager.mediaType));
        params.put("status",String.valueOf(callStatus.ordinal()));
        params.put("content",resultContent);
        params.put("to_userid",callManager.targetId);
        params.put("push",needPushToOp?"1":"0");
        OkHttpHelper.getInstance().post(ServiceConstants.IP + "chat/hangup", params
                , new CompleteCallback<BaseResponse>(BaseResponse.class, getITopicApplication()) {

                    @Override
                    public void onComplete(Response response, BaseResponse intResponse) {
                        // TODO Auto-generated method stub
                    }
                });
    }

    //访问服务器，检查对方是否还在频道里，返回值是当前频道里的所有用户们的userid
    private void checkOppoStillInChannel(){
        HashMap<String,String> params = new HashMap<>();
        params.put("channelid",callManager.channelId);

        OkHttpHelper.getInstance().post(ServiceConstants.IP + "chat/check_channel", params
                , new CompleteCallback<StringListResponse>(StringListResponse.class, getITopicApplication()) {

                    @Override
                    public void onComplete(Response response, StringListResponse listResponse) {
                        // TODO Auto-generated method stub
                        if (listResponse.isSuccess()){
                            boolean exist = false;
                            for (String item : listResponse.getData().getItems()) {
                                if (callManager.targetId.equals(item)){
                                    exist = true;
                                    break;
                                }
                            }
                            if(!exist){
                                LogUtil.e("对方不在了，我也退出");
                                callManager.mRtcEngine.leaveChannel();
                            }
                        }
                    }
                });
    }

    public void onReceiveBtnClick(View view) {
        //_接听单人视频
        callManager.mRtcEngine.joinChannel(null, callManager.channelId, "", Integer.parseInt(getUserID()));
    }

    private void hideVideoCallInformation() {
        isInformationShow = false;
        mUserInfoContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);
        findViewById(R.id.rc_voip_video_mute_cb).setVisibility(View.GONE);
    }

    private void showVideoCallInformation() {
        isInformationShow = true;
        mUserInfoContainer.setVisibility(View.VISIBLE);

//        mUserInfoContainer.findViewById(R.id.rc_voip_call_minimize).setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout btnLayout = (RelativeLayout) inflater.inflate(R.layout.rc_voip_call_bottom_connected_button_layout, null);
        btnLayout.findViewById(R.id.rc_voip_call_mute).setSelected(audiomuted);
        btnLayout.findViewById(R.id.rc_voip_handfree).setVisibility(View.GONE);
        btnLayout.findViewById(R.id.rc_voip_camera).setVisibility(View.VISIBLE);
        mButtonContainer.removeAllViews();
        mButtonContainer.addView(btnLayout);

        CheckBox rc_voip_video_mute_cb = findViewById(R.id.rc_voip_video_mute_cb);
        rc_voip_video_mute_cb.setVisibility(View.VISIBLE);
        rc_voip_video_mute_cb.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                callManager.mRtcEngine.muteLocalVideoStream(b);
                mLocalVideo.view.setVisibility(b?View.GONE:View.VISIBLE);
                compoundButton.setText(b?R.string.rc_voip_enable_camera:R.string.rc_voip_disable_camera);
            }
        });
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //我关闭了自己的摄像头 todo
//                callManager.mRtcEngine.muteLocalVideoStream(mVideoMuted);
//                view.setSelected(!view.isSelected());
//                mSPreviewContainer.removeAllViews();
//                mSPreviewContainer.setVisibility(View.GONE);
//            }
//        });
    }

    public void onHandFreeButtonClick(View view) {
        callManager.mRtcEngine.setEnableSpeakerphone(!view.isSelected());//true:打开免提 false:关闭免提
        view.setSelected(!view.isSelected());
        handFree = view.isSelected();
    }

    public void onMuteAudioButtonClick(View view) {
        view.setSelected(!view.isSelected());
        callManager.mRtcEngine.muteLocalAudioStream(view.isSelected());
        audiomuted = view.isSelected();
    }

    //融云是override ，这里我public
    protected String onCallDisconnected(boolean hangUpByMyself) {

        //重要，全局变量设置为挂断
        callManager.callStatus = CallManager.CallStatus.CallHangup;

        super.onCallDisconnected();

        showToastByApplication(hangUpByMyself?"通话已经结束":"对方已挂断");

        String content = "";
        if (callManager.connectedTime == 0) {
            content = hangUpByMyself?"对方未接听":"对方已挂断";
            DBReq.getInstence(getITopicApplication()).updateCallMessageState(callManager.channelId, CallManager.CallStatus.CallHangup.ordinal(),content);
        } else {
//            long sec = (int)(System.currentTimeMillis()/1000) - callManager.connectedTime;
            StringBuilder sb = new StringBuilder();
            sb.append(callManager.mediaType == ChatBean.SUBTYPE_CALL_VIDEO?"视频时长":"通话时长");
            sb.append(" ");
            long time = getTime();
            if (time >= 3600) {
                sb.append(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
            } else {
                sb.append(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
            }
            content = sb.toString();
            DBReq.getInstence(getITopicApplication()).updateCallMessageState(callManager.channelId, CallManager.CallStatus.CallHangup.ordinal(),content);
        }

        CallMessageUpdataEvent articleEvent = new CallMessageUpdataEvent();
        articleEvent.setChannelId(callManager.channelId);
        articleEvent.setCallState(CallManager.CallStatus.CallHangup);
        articleEvent.setContent(content);
        EventBus.getDefault().post(articleEvent);

        callManager.connectedTime = 0;
        callManager.startTime = 0;

        //再震动一次
//        startVibrator(new long[] { 0, 180, 80, 120 },-1);

//        endCall();

        postRunnableDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });

        return content;
    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;

        //官方demo里没有这两句，但是我发现不加第一句会内存泄漏，然后我同理推出了第2句
        callManager.mRtcEngine.setupLocalVideo(null);
        callManager.mRtcEngine.setupRemoteVideo(null);
    }

    public void onMinimizeClick(View view) {
        super.onMinimizeClick(view);
    }

    public void onSwitchCameraClick(View view) {
        callManager.mRtcEngine.switchCamera();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchVideoCanvasView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == rightTopFrameContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            bodyFrameContainer.addView(canvas.view);
        } else if (parent == bodyFrameContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            rightTopFrameContainer.addView(canvas.view);
        }
    }

//    @Override
//    public void showOnGoingNotification() {
//        Intent intent = new Intent(getIntent().getAction());
//        Bundle bundle = new Bundle();
//        onSaveFloatBoxState(bundle);
//        intent.putExtra("floatbox", bundle);
//        intent.putExtra("callAction", RongCallAction.ACTION_RESUME_CALL.getName());
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationUtil.showNotification(this, "todo", "coontent", pendingIntent, CALL_NOTIFICATION_ID);
//    }
}
