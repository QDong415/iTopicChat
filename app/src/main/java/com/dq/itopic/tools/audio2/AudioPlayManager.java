//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.dq.itopic.tools.audio2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Build.VERSION;
import android.os.PowerManager.WakeLock;

import com.dq.itopic.tools.LogUtil;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayManager implements SensorEventListener {
    private static final String TAG = "AudioPlayManager";
    private MediaPlayer mMediaPlayer;
    private IAudioPlayListener _playListener;
    private Uri mUriPlaying;
    private Sensor _sensor;
    private SensorManager _sensorManager;
    private AudioManager mAudioManager;
    private PowerManager _powerManager;
    private WakeLock _wakeLock;
    private OnAudioFocusChangeListener afChangeListener;
//    private Context mContext;
    private Handler handler;
    private final Object mLock;
    private boolean isVOIPMode;

    public AudioPlayManager() {
        this.mLock = new Object();
        this.isVOIPMode = false;
        this.handler = new Handler(Looper.getMainLooper());
    }

    @TargetApi(11)
    public void onSensorChanged(SensorEvent event) {
        synchronized(this.mLock) {
            float range = event.values[0];
            double rangeJudgeValue = 0.0D;
            if (this._sensor != null && this.mMediaPlayer != null && this.mAudioManager != null) {
                boolean judge = this.judgeCondition(event, range, rangeJudgeValue);
                if (this.mMediaPlayer.isPlaying()) {
                    FileInputStream fis = null;
                    if (judge) {
                        if (this.mAudioManager.getMode() == 0) {
                            return;
                        }

                        this.mAudioManager.setMode(0);
                        this.mAudioManager.setSpeakerphoneOn(true);
                        final int positions = this.mMediaPlayer.getCurrentPosition();

                        try {
                            this.mMediaPlayer.reset();
                            if (VERSION.SDK_INT >= 21) {
                                AudioAttributes attributes = (new Builder()).setUsage(1).build();
                                this.mMediaPlayer.setAudioAttributes(attributes);
                            } else {
                                this.mMediaPlayer.setAudioStreamType(3);
                            }

                            this.mMediaPlayer.setVolume(1.0F, 1.0F);
                            fis = new FileInputStream(this.mUriPlaying.getPath());
                            this.mMediaPlayer.setDataSource(fis.getFD());
                            this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                                public void onPrepared(MediaPlayer mp) {
                                    mp.seekTo(positions);
                                }
                            });
                            this.mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                                public void onSeekComplete(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            this.mMediaPlayer.prepareAsync();
                        } catch (IOException var20) {
                        } finally {
                            if (fis != null) {
                                try {
                                    fis.close();
                                } catch (IOException var19) {
                                }
                            }

                        }

                        this.setScreenOn();
                    } else {
                        if (!Build.BRAND.equals("samsung") || !Build.MODEL.equals("SM-N9200")) {
                            this.setScreenOff();
                        }

                        if (VERSION.SDK_INT >= 11) {
                            if (this.mAudioManager.getMode() == 3) {
                                return;
                            }

                            this.mAudioManager.setMode(3);
                        } else {
                            if (this.mAudioManager.getMode() == 2) {
                                return;
                            }

                            this.mAudioManager.setMode(2);
                        }

                        this.mAudioManager.setSpeakerphoneOn(false);
                        this.replay();
                    }
                } else if ((double)range > 0.0D) {
                    if (this.mAudioManager.getMode() == 0) {
                        return;
                    }

                    this.mAudioManager.setMode(0);
                    this.mAudioManager.setSpeakerphoneOn(true);
                    this.setScreenOn();
                }

            }
        }
    }

    private boolean judgeCondition(SensorEvent event, float range, double rangeJudgeValue) {
        synchronized(this.mLock) {
            boolean judge;
            if (Build.BRAND.equalsIgnoreCase("HUAWEI")) {
                judge = range >= event.sensor.getMaximumRange();
            } else {
                if (Build.BRAND.equalsIgnoreCase("ZTE")) {
                    rangeJudgeValue = 1.0D;
                } else if (Build.BRAND.equalsIgnoreCase("nubia")) {
                    rangeJudgeValue = 3.0D;
                }

                judge = (double)range > rangeJudgeValue;
            }

            return judge;
        }
    }

    @TargetApi(21)
    private void setScreenOff() {
        synchronized(this.mLock) {
            if (this._wakeLock == null && this._powerManager != null) {
                this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager:wakelockTag");
            }

            if (this._wakeLock != null && !this._wakeLock.isHeld()) {
                this._wakeLock.acquire(600000L);
            }

        }
    }

    private void setScreenOn() {
        synchronized(this.mLock) {
            if (this._wakeLock != null && this._wakeLock.isHeld()) {
                this._wakeLock.setReferenceCounted(false);
                this._wakeLock.release();
                this._wakeLock = null;
            }

        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void replay() {
        synchronized(this.mLock) {
            if (this.mMediaPlayer != null) {
                FileInputStream fis = null;

                try {
                    this.mMediaPlayer.reset();
                    AudioAttributes attributes;
                    if (Build.BRAND.equals("samsung") && Build.MODEL.equals("SM-N9200")) {
                        if (VERSION.SDK_INT >= 21) {
                            attributes = (new Builder()).setUsage(2).build();
                            this.mMediaPlayer.setAudioAttributes(attributes);
                        } else {
                            this.mMediaPlayer.setAudioStreamType(0);
                        }
                    } else if (VERSION.SDK_INT >= 21) {
                        attributes = (new Builder()).setUsage(1).build();
                        this.mMediaPlayer.setAudioAttributes(attributes);
                    } else {
                        this.mMediaPlayer.setAudioStreamType(3);
                    }

                    this.mMediaPlayer.setVolume(1.0F, 1.0F);
                    fis = new FileInputStream(this.mUriPlaying.getPath());
                    this.mMediaPlayer.setDataSource(fis.getFD());
                    this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException var3) {
                                Thread.currentThread().interrupt();
                            }

                            mp.start();
                        }
                    });
                    this.mMediaPlayer.prepareAsync();
                } catch (IOException var14) {
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException var13) {
                        }
                    }

                }
            }
        }
    }

    public void startPlay(final Context context, Uri audioUri, IAudioPlayListener playListener) {
        synchronized(this.mLock) {
            if (context != null && audioUri != null) {
//                this.mContext = context;
                if (this._playListener != null && this.mUriPlaying != null) {
                    this._playListener.onStop(this.mUriPlaying);
                    if (this.mUriPlaying.equals(audioUri)){
                        this.resetMediaPlayer();
                        this.mUriPlaying = null;
                        return;
                    }
                }

                this.resetMediaPlayer();
                this.afChangeListener = new OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        synchronized(AudioPlayManager.this.mLock) {
                            LogUtil.e("OnAudioFocusChangeListener " + focusChange);
                            if (AudioPlayManager.this.mAudioManager != null && focusChange == -1) {
                                AudioPlayManager.this.mAudioManager.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
                                AudioPlayManager.this.afChangeListener = null;
                                AudioPlayManager.this.handler.post(new Runnable() {
                                    public void run() {
                                        synchronized(AudioPlayManager.this.mLock) {
                                            if (AudioPlayManager.this._playListener != null) {
                                                AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
                                                AudioPlayManager.this._playListener = null;
                                            }

                                        }
                                    }
                                });
                                AudioPlayManager.this.reset();
                            }
                        }
                    }
                };
                FileInputStream fis = null;
                ((Activity)context).getWindow().addFlags(128);

                try {
                    this._powerManager = (PowerManager)context.getApplicationContext().getSystemService("power");
                    this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService("audio");
                    if (!this.isHeadphonesPlugged(this.mAudioManager)) {
                        this._sensorManager = (SensorManager)context.getApplicationContext().getSystemService("sensor");
                        if (this._sensorManager != null) {
                            this._sensor = this._sensorManager.getDefaultSensor(8);
                            this._sensorManager.registerListener(this, this._sensor, 3);
                        }
                    }

                    this.muteAudioFocus(this.mAudioManager, true);
                    this._playListener = playListener;
                    this.mUriPlaying = audioUri;
                    this.mMediaPlayer = new MediaPlayer();
                    this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            synchronized(AudioPlayManager.this.mLock) {
                                if (AudioPlayManager.this._playListener != null) {
                                    AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
                                    AudioPlayManager.this._playListener = null;
                                }

                                AudioPlayManager.this.reset();
                                ((Activity)context).getWindow().clearFlags(128);
                            }
                        }
                    });
                    this.mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            synchronized(AudioPlayManager.this.mLock) {
                                AudioPlayManager.this.reset();
                                return true;
                            }
                        }
                    });
                    fis = new FileInputStream(audioUri.getPath());
                    this.mMediaPlayer.setDataSource(fis.getFD());
                    if (VERSION.SDK_INT >= 21) {
                        AudioAttributes attributes = (new Builder()).setUsage(1).build();
                        this.mMediaPlayer.setAudioAttributes(attributes);
                    } else {
                        this.mMediaPlayer.setAudioStreamType(3);
                    }

                    this.mMediaPlayer.prepare();
                    this.mMediaPlayer.start();
                    if (this._playListener != null) {
                        this._playListener.onStart(this.mUriPlaying);
                    }
                } catch (Exception var17) {
                    if (this._playListener != null) {
                        this._playListener.onStop(audioUri);
                        this._playListener = null;
                    }

                    this.reset();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException var16) {
                        }
                    }

                }

            } else {
            }
        }
    }

    private boolean isHeadphonesPlugged(AudioManager audioManager) {
        synchronized(this.mLock) {
            if (audioManager == null) {
                return false;
            } else if (VERSION.SDK_INT < 23) {
                return audioManager.isWiredHeadsetOn();
            } else {
                AudioDeviceInfo[] audioDevices = audioManager.getDevices(3);
                AudioDeviceInfo[] var4 = audioDevices;
                int var5 = audioDevices.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    AudioDeviceInfo deviceInfo = var4[var6];
                    if (deviceInfo.getType() == 4 || deviceInfo.getType() == 3) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public void setPlayListener(IAudioPlayListener listener) {
        synchronized(this.mLock) {
            this._playListener = listener;
        }
    }

    public void stopPlay() {
        synchronized(this.mLock) {
//            if (this.mContext != null) {每次onDestory都报内存泄露，后来把mContext全局变量删了就好了
//                ((Activity)this.mContext).getWindow().clearFlags(128);
//            }

            if (this._playListener != null && this.mUriPlaying != null) {
                this._playListener.onStop(this.mUriPlaying);
            }

            this.reset();
        }
    }

    private void reset() {
        this.resetMediaPlayer();
        this.resetAudioPlayManager();
    }

    private void resetAudioPlayManager() {
        if (this.mAudioManager != null) {
            this.mAudioManager.setMode(0);
            this.muteAudioFocus(this.mAudioManager, false);
        }

        if (this._sensorManager != null) {
            this.setScreenOn();
            this._sensorManager.unregisterListener(this);
        }

        this._sensorManager = null;
        this._sensor = null;
        this._powerManager = null;
        this.mAudioManager = null;
        this._wakeLock = null;
        this.mUriPlaying = null;
        this._playListener = null;
        this.afChangeListener = null;
    }

    private void resetMediaPlayer() {
        synchronized(this.mLock) {
            if (this.mMediaPlayer != null) {
                try {
                    this.mMediaPlayer.stop();
                    this.mMediaPlayer.reset();
                    this.mMediaPlayer.release();
                    this.mMediaPlayer = null;
                } catch (IllegalStateException var4) {
                }
            }

        }
    }

    public Uri getPlayingUri() {
        synchronized(this.mLock) {
            return this.mUriPlaying != null ? this.mUriPlaying : Uri.EMPTY;
        }
    }

    @TargetApi(8)
    private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
        synchronized(this.mLock) {
            if (audioManager != null) {
                if (bMute) {
                    audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
                } else {
                    audioManager.abandonAudioFocus(this.afChangeListener);
                    this.afChangeListener = null;
                }

            }
        }
    }

    public boolean isInNormalMode(Context context) {
        synchronized(this.mLock) {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService("audio");
            }

            return this.mAudioManager != null && this.mAudioManager.getMode() == 0;
        }
    }

    public boolean isInVOIPMode(Context context) {
        return this.isVOIPMode;
    }

    public void setInVoipMode(boolean isVOIPMode) {
        this.isVOIPMode = isVOIPMode;
    }

    public boolean isPlaying() {
        synchronized(this.mLock) {
            return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
        }
    }

}
