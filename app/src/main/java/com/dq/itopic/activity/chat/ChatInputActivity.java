package com.dq.itopic.activity.chat;

import android.Manifest;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.chat.keyboard.AppGridLayout;
import com.dq.itopic.activity.chat.keyboard.ChatKeyBoard;
import com.dq.itopic.activity.chat.keyboard.EmojiConstants;
import com.dq.itopic.activity.chat.keyboard.SimpleCommonUtils;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.tools.GlideEngine;
import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.LogUtil;
import com.dq.itopic.tools.audio2.AudioRecordManager;
import com.dq.itopic.tools.audio2.IAudioRecordListener;
import com.dq.itopic.views.keyboard.data.EmoticonEntity;
import com.dq.itopic.views.keyboard.interfaces.EmoticonClickListener;
import com.dq.itopic.views.keyboard.utils.EmoticonsKeyboardUtils;
import com.dq.itopic.views.keyboard.widget.EmoticonsEditText;
import com.dq.itopic.views.keyboard.widget.FuncLayout;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.sj.emoji.EmojiBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import pub.devrel.easypermissions.EasyPermissions;

//由于聊天界面代码量太大，分流出来一部分 输入相关的代码抽离到这个类里
public abstract class ChatInputActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener
        , EasyPermissions.PermissionCallbacks{

    private final static int REQUIRE_PERMISSIONS_CODE_AUDIO = 0x92;

    protected ChatKeyBoard ekBar;

    private AudioRecordManager audioRecordManager;
    
    protected void initInputView() {
        ekBar = findViewById(R.id.ek_bar);
        initEmoticonsKeyBoardBar();
        initAudioRecordManager();
    }


    private void initEmoticonsKeyBoardBar() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);

        AppGridLayout appGridLayout = new AppGridLayout(this);
        appGridLayout.initView(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((int)view.getTag() == 1){
                    //点击了+👌进入相册选图
                    PictureSelector.create(ChatInputActivity.this)
                            .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .isWeChatStyle(true)//开启R.style.picture_WeChat_style样式
                            .imageEngine(GlideEngine.createGlideEngine())
                            .maxSelectNum(9)// 最大图片选择数量
                            .minSelectNum(1)// 最小选择数量
                            .imageSpanCount(3)// 每行显示个数
                            .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE : PictureConfig.SINGLE
                            .isPreviewImage(true)// 是否可预览图片
                            .isPreviewVideo(false)// 是否可预览视频
                            .isCamera(true)// 是否显示拍照按钮
                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                            .isEnableCrop(false)// 是否裁剪
                            .isCompress(true)// 是否压缩
                            .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                            .isGif(true)// 是否显示gif图片
                            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                            .circleDimmedLayer(false)// 是否圆形裁剪
                            .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                            .isOpenClickSound(true)// 是否开启点击声音
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code

                } else if ((int)view.getTag() == 2 || (int)view.getTag() == 3){
                    sendCallMessage((int)view.getTag() == 2? ChatBean.SUBTYPE_CALL_AUDIO:ChatBean.SUBTYPE_CALL_VIDEO);
                }
            }
        });
        ekBar.addFuncView(appGridLayout);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ekBar.getEtChat().getText().toString().trim().equals("")){
                    onSendBtnClick(ekBar.getEtChat().getText().toString());
                    ekBar.getEtChat().setText("");
                }
            }
        });

        ekBar.getBtnVoice().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    String[] perms = {Manifest.permission.RECORD_AUDIO};
                    if (EasyPermissions.hasPermissions(this, perms)) {
                        audioRecordManager.startRecord();
                    } else {
                        EasyPermissions.requestPermissions(this, "发语音需要获取录音权限",REQUIRE_PERMISSIONS_CODE_AUDIO, perms);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isCancelled(v, event)) {
                        audioRecordManager.willCancelRecord();
                    } else {
                        audioRecordManager.continueRecord();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    audioRecordManager.stopRecord();
                    audioRecordManager.destroyRecord();
                    break;
            }
            return false;
        });
    }


    /**
     * 复制单个文件
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     *         <code>false</code> otherwise
     */
    public boolean copyFile(String oldPath$Name, File newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists()) {
                return false;
            } else if (!oldFile.isFile()) {
                return false;
            } else if (!oldFile.canRead()) {
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if(o == null){
                    return;
                }
                if(actionType == EmojiConstants.EMOTICON_CLICK_BIGIMAGE){
                    if(o instanceof EmoticonEntity){
                        onSendImage(((EmoticonEntity)o).getIconUri());
                    }
                } else {
                    String content = null;
                    if(o instanceof EmojiBean){
                        content = ((EmojiBean)o).emoji;
                    } else if(o instanceof EmoticonEntity){
                        content = ((EmoticonEntity)o).getContent();
                    }

                    if(TextUtils.isEmpty(content)){
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(EmoticonsKeyboardUtils.isFullScreen(this)){
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ekBar.reset();
    }

    private boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth()
                || event.getRawY() < location[1] - 40) {
            return true;
        }

        return false;
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() { }

    protected abstract void scrollToBottom();

    protected abstract void onSendImage(String image);

    protected abstract void sendAudioMessage(String filename, String extend ,String sandboxFilePath);

    protected abstract void sendCallMessage(int callType);

    protected abstract void onSendBtnClick(String content);

    protected void initAudioRecordManager() {
        audioRecordManager = new AudioRecordManager(this);
        audioRecordManager.setMaxVoiceDuration(AudioRecordManager.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND);
        audioRecordManager.setAudioSavePath(null);
        audioRecordManager.setAudioRecordListener(new IAudioRecordListener() {

            private TextView mTimerTV;
            private TextView mStateTV;
            private ImageView mStateIV;
            private PopupWindow mRecordWindow;

            @Override
            public void initTipView() {
                LogUtil.e("initTipView");
                View view = View.inflate(ChatInputActivity.this, R.layout.popup_audio_wi_vo, null);
                mStateIV = (ImageView) view.findViewById(R.id.rc_audio_state_image);
                mStateTV = (TextView) view.findViewById(R.id.rc_audio_state_text);
                mTimerTV = (TextView) view.findViewById(R.id.rc_audio_timer);
                mRecordWindow = new PopupWindow(view, -1, -1);
                mRecordWindow.showAtLocation(findViewById(R.id.root_rl), 17, 0, 0);
                mRecordWindow.setFocusable(true);
                mRecordWindow.setOutsideTouchable(false);
                mRecordWindow.setTouchable(false);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.GONE);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setText(String.format("%s", new Object[]{Integer.valueOf(counter)}));
                    this.mTimerTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (this.mRecordWindow != null) {
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.drawable.ic_volume_1);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_rec);
                    this.mStateTV.setBackgroundResource(R.drawable.bg_voice_popup);
                    this.mTimerTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (this.mRecordWindow != null) {
                    mStateIV.setImageResource(R.drawable.ic_volume_wraning);
                    mStateTV.setText(R.string.voice_short);
                }
            }

            @Override
            public void setCancelTipView() {
                if (this.mRecordWindow != null) {
                    this.mTimerTV.setVisibility(View.GONE);
                    this.mStateIV.setVisibility(View.VISIBLE);
                    this.mStateIV.setImageResource(R.drawable.ic_volume_cancel);
                    this.mStateTV.setVisibility(View.VISIBLE);
                    this.mStateTV.setText(R.string.voice_cancel);
                    this.mStateTV.setBackgroundResource(R.drawable.corner_voice_style);
                }
            }

            @Override
            public void destroyTipView() {
                if (this.mRecordWindow != null) {
                    this.mRecordWindow.dismiss();
                    this.mRecordWindow = null;
                    this.mStateIV = null;
                    this.mStateTV = null;
                    this.mTimerTV = null;
                }
            }

            @Override
            public void onStartRecord() {
            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                //发送文件
                File file = new File(audioPath.getPath());
                if (file.exists()) {
                    HashMap<String,String> extendmap = new HashMap<>();
                    extendmap.put("duration",String.valueOf(duration));
                    extendmap.put("hadplay","1");
                    sendAudioMessage(file.getName(), JsonUtil.getJson(extendmap),audioPath.getPath());
                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                switch (db / 5) {
                    case 0:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_1);
                        break;
                    case 1:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_2);
                        break;
                    case 2:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_3);
                        break;
                    case 3:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_4);
                        break;
                    case 4:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_5);
                        break;
                    case 5:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_6);
                        break;
                    case 6:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_7);
                        break;
                    default:
                        this.mStateIV.setImageResource(R.drawable.ic_volume_8);
                }
            }
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == REQUIRE_PERMISSIONS_CODE_AUDIO){
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(requestCode == REQUIRE_PERMISSIONS_CODE_AUDIO){
            showToast("发语音消息需要获取录音权限");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
