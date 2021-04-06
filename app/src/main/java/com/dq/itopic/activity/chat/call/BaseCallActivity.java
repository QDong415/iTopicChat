package com.dq.itopic.activity.chat.call;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
//import android.media.MediaPlayer;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dq.itopic.R;
import com.dq.itopic.activity.chat.call.util.PermissionCheckUtil;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.manager.CallManager;
import com.dq.itopic.tools.statusbar.StatusBarUtils;

import java.io.IOException;


public class BaseCallActivity extends BaseActivity  {

    private final String MEDIAPLAYERTAG = "MEDIAPLAYERTAG";
    private final static long DELAY_TIME = 1000;

    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;

    private long time = 0;
    private Runnable updateTimeRunnable;

    protected Handler handler = new Handler();

    //权限请求
    protected final int REQUIRE_PERMISSIONS_CODE_VIDEO = 0x400;
    protected final int REQUIRE_PERMISSIONS_CODE_AUDIO = 0x401;
    protected final String[] VIDEO_CALL_PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    protected final String[] AUDIO_CALL_PERMISSIONS = {Manifest.permission.RECORD_AUDIO};

    protected CallManager callManager;

    public void postRunnableDelay(Runnable runnable) {
        handler.postDelayed(runnable, DELAY_TIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        callManager = getITopicApplication().getCallManager();
        initMp();
    }

    private void initMp() {
        if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        if (mp != null) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        Log.i(MEDIAPLAYERTAG,"setOnPreparedListener Error!");
                    }
                }
            });
        }
    }

    @Override
    protected void initStatusBar() {
        StatusBarUtils.from(this)
                .setTransparentStatusbar(true)
                .setStatusBarColor(getResources().getColor(android.R.color.black))
                .setLightStatusBar(false)
                .process(this);
    }

    protected void outOrIncomeCallRinging(boolean outgoing) {
        //测试后发现，这种的只能是进入频道后才能播放
//        callManager.mRtcEngine.startAudioMixing(outgoing?"/assets/voip_outgoing_ring.mp3":"/assets/voip_income_call.mp3",true,true,-1);
        try {
            initMp();
            AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(outgoing?R.raw.voip_outgoing_ring:R.raw.voip_income_call);
            mMediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            assetFileDescriptor.close();
            // 设置 MediaPlayer 播放的声音用途
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            }
            mMediaPlayer.prepareAsync();
//            final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            if (am != null) {
//                am.setSpeakerphoneOn(false);
//                // 设置此值可在拨打时控制响铃音量
//                am.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                // 设置拨打时响铃音量默认值
//                am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5, AudioManager.STREAM_VOICE_CALL);
//            }
        } catch (Exception e1){
            Log.i(MEDIAPLAYERTAG,"---onOutgoingCallRinging Error---"+e1.getMessage());
        }
    }

    public void setupTime(final TextView timeView) {
        try {
            if (updateTimeRunnable != null) {
                handler.removeCallbacks(updateTimeRunnable);
            }
            timeView.setVisibility(View.VISIBLE);
            updateTimeRunnable = new UpdateTimeRunnable(timeView);
            handler.post(updateTimeRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getTime() {
        return time;
    }

    @SuppressLint("MissingPermission")
    protected void stopRing() {
//        callManager.mRtcEngine.stopAudioMixing();
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            }
            if (mVibrator != null) {
                mVibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(MEDIAPLAYERTAG,"mMediaPlayer stopRing error="+((e==null)?"null":e.getMessage()));
        }
    }

    protected void onCallDisconnected() {
        stopRing();
    }

    @Override
    protected void onDestroy() {
        try {
            handler.removeCallbacks(updateTimeRunnable);
            if(mMediaPlayer != null){
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer=null;
            }
            // 退出此页面后应设置成正常模式，否则按下音量键无法更改其他音频类型的音量
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.setMode(AudioManager.MODE_NORMAL);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.i(MEDIAPLAYERTAG,"--- onDestroy IllegalStateException---");
        }
        super.onDestroy();
    }

    private class UpdateTimeRunnable implements Runnable {
        private TextView timeView;

        public UpdateTimeRunnable(TextView timeView) {
            this.timeView = timeView;
        }

        @Override
        public void run() {
            time++;
            if (time >= 3600) {
                timeView.setText(String.format("%d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
            } else {
                timeView.setText(String.format("%02d:%02d", (time % 3600) / 60, (time % 60)));
            }
            handler.postDelayed(this, 1000);
        }
    }

    //悬浮框，未完成
    void onMinimizeClick(View view) {
        Toast.makeText(this, getString(R.string.rc_voip_float_window_not_allowed), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionCheckUtil.checkPermissions(this, permissions)) {
            PermissionCheckUtil.showRequestPermissionFailedAlter(this, PermissionCheckUtil.getNotGrantedPermissionMsg(this, permissions, grantResults));
        }
    }

    /**
     * 判断系统是否设置了 响铃时振动
     */
    private boolean isVibrateWhenRinging() {
        ContentResolver resolver = getApplicationContext().getContentResolver();
        if (Build.MANUFACTURER.equals("Xiaomi")) {
            return Settings.System.getInt(resolver, "vibrate_in_normal", 0) == 1;
        } else if (Build.MANUFACTURER.equals("smartisan")) {
            return Settings.Global.getInt(resolver, "telephony_vibration_enabled", 0) == 1;
        } else {
            return Settings.System.getInt(resolver, "vibrate_when_ringing", 0) == 1;
        }
    }
}
