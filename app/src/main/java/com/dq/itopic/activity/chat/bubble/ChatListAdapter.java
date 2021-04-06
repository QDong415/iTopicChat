package com.dq.itopic.activity.chat.bubble;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dq.itopic.R;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.audio2.AudioRecordManager;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.views.emojitextview.EmojiconTextView;
import com.dq.itopic.views.linktextview.QMUILinkTextView;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_FISH_TIPS = 2;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 4;
    private static final int MESSAGE_TYPE_RECV_CALL = 5;
    private static final int MESSAGE_TYPE_SENT_CALL = 6;
    private static final int MESSAGE_TYPE_RECV_AUDIO = 7;
    private static final int MESSAGE_TYPE_SENT_AUDIO = 8;
    private static final int MESSAGE_TYPE_RECV_GIFT = 9;
    private static final int MESSAGE_TYPE_SENT_GIFT = 10;

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<ChatBean> mData;

    private String myUserPhoto;//我的头像，这里抽成全局变量

    private QMUILinkTextView.OnLinkClickListener mOnLinkClickListener;
    private ChatActionListener onChatActionListener;//点击事件回调，比如点击头像，长按气泡

    private int screenWidthPx;

    public ChatListAdapter(Activity activity, List<ChatBean> data) {
        this.mActivity = activity;
        this.mData = data;
        this.mInflater = LayoutInflater.from(activity);
        this.screenWidthPx = activity.getApplicationContext().getResources().getDisplayMetrics().widthPixels;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        ChatBean message = mData.get(position);
        if (message.isChatTips()){
            return MESSAGE_TYPE_FISH_TIPS;
        }
        switch (message.getSubtype()){
            case ChatBean.SUBTYPE_IMAGE:
                return message.getIssender() == 0 ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
            case ChatBean.SUBTYPE_AUDIO:
                return message.getIssender() == 0 ? MESSAGE_TYPE_RECV_AUDIO : MESSAGE_TYPE_SENT_AUDIO;
            case ChatBean.SUBTYPE_CALL_AUDIO:
            case ChatBean.SUBTYPE_CALL_VIDEO:
                return message.getIssender() == 0 ? MESSAGE_TYPE_RECV_CALL : MESSAGE_TYPE_SENT_CALL;
            case ChatBean.SUBTYPE_GIFT:
                return message.getIssender() == 0 ? MESSAGE_TYPE_RECV_GIFT : MESSAGE_TYPE_SENT_GIFT;
            default:
                return message.getIssender() == 0 ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 11;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatBean bean = mData.get(position);
        int type = getItemViewType(position);
        switch (type) {
            case MESSAGE_TYPE_FISH_TIPS:
                TipsViewHolder tipsHolder;
                if (convertView == null) {
                    tipsHolder = new TipsViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_tips, null);
                    tipsHolder.time_tv = (TextView) convertView.findViewById(R.id.timestamp_tv);
                    convertView.setTag(tipsHolder);
                } else {
                    tipsHolder = (TipsViewHolder) convertView.getTag();
                }
//                tipsHolder.time_tv.setText(ValueUtil.getTimeStringFromNow(bean.getCreate_time(),false));
                tipsHolder.time_tv.setText(bean.getContent());
                break;
            case MESSAGE_TYPE_RECV_TXT:{
                ViewHolderRevText holder;
                if (convertView == null) {
                    holder = new ViewHolderRevText();
                    convertView = mInflater.inflate(R.layout.chatitem_received_message, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    holder.content_tv = (EmojiconTextView) convertView.findViewById(R.id.content_tv);
                    holder.content_tv.setOnLinkClickListener(mOnLinkClickListener);
                    holder.content_tv.setNeedForceEventToParent(true);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolderRevText) convertView.getTag();
                }
                displayBaseTextBubbleView(position ,holder, bean);
            }
                break;
            case MESSAGE_TYPE_RECV_CALL:{
                BaseCallViewHolder holder;
                if (convertView == null) {
                    holder = new BaseCallViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_received_call, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.bubble_logo_iv = (ImageView) convertView.findViewById(R.id.bubble_logo_iv);
                    holder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    holder.content_tv = (EmojiconTextView) convertView.findViewById(R.id.content_tv);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (BaseCallViewHolder) convertView.getTag();
                }
                displayBaseTextBubbleView(position ,holder, bean);
                displayCallBubbleView(position ,holder, bean);
            }
            break;
            case MESSAGE_TYPE_RECV_IMAGE:{//接收的图片
                BaseImageViewHolder holder;
                if (convertView == null) {
                    holder = new BaseImageViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_received_image, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.bubble_iv = (BubbleImageView) convertView.findViewById(R.id.bubble_iv);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (BaseImageViewHolder) convertView.getTag();
                }
                displayBaseImageBubbleView(position,holder, bean);
                holder.bubble_iv.setProgressVisible(false);
                holder.bubble_iv.showShadow(false);
                GlideLoaderUtil.loadImage(mActivity,ValueUtil.getQiniuUrlByFileName(
                        bean.getFilename(),ValueUtil.dip2px(mActivity,ChatBean.IMAGE_MAX_SIZE_DP),false)
                        ,R.drawable.picture_placeholder, holder.bubble_iv);
            }
                break;
            case MESSAGE_TYPE_SENT_IMAGE:{//发送图片
                BaseImageViewHolder holder;
                if (convertView == null) {
                    holder = new BaseImageViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_send_image, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.bubble_iv = (BubbleImageView) convertView.findViewById(R.id.bubble_iv);
                    holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    holder.status_iv = (ImageView) convertView.findViewById(R.id.msg_status_iv);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (BaseImageViewHolder) convertView.getTag();
                }
                switch (bean.getState()) {
                    case ChatBean.SUCCESS: // 发送成功
                        holder.bubble_iv.setProgressVisible(false);
                        holder.bubble_iv.showShadow(false);
                        break;
                    case ChatBean.FAIL: // 发送失败
                        holder.bubble_iv.setProgressVisible(false);
                        holder.bubble_iv.showShadow(false);
                        break;
                    case ChatBean.INPROGRESS: // 发送中
                        holder.bubble_iv.setProgressVisible(true);
                        holder.bubble_iv.setPercent(bean.getFileUploadProgress());
                        holder.bubble_iv.showShadow(true);
                        break;
                    default:
                        break;
                }

                displayBaseImageBubbleView(position,holder, bean);

                GlideLoaderUtil.loadImage(mActivity,ValueUtil.findVoiceLocalPathWithFileName(mActivity,bean.getFilename())
                        ,R.drawable.gray_rect, holder.bubble_iv);

                displayStatusView(holder, bean);
            }
                break;
            case MESSAGE_TYPE_SENT_TXT://发送文本
                ViewHolderSendText imageHolder;
                if (convertView == null) {
                    imageHolder = new ViewHolderSendText();
                    convertView = mInflater.inflate(R.layout.chatitem_sent_message, null);
                    imageHolder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    imageHolder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    imageHolder.content_tv = (EmojiconTextView) convertView.findViewById(R.id.content_tv);
                    imageHolder.content_tv.setOnLinkClickListener(mOnLinkClickListener);
                    imageHolder.content_tv.setNeedForceEventToParent(true);
                    imageHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    imageHolder.status_iv = (ImageView) convertView.findViewById(R.id.msg_status_iv);
                    baseConvertViewListener(imageHolder);
                    convertView.setTag(imageHolder);
                } else {
                    imageHolder = (ViewHolderSendText) convertView.getTag();
                }
                displayStatusView(imageHolder, bean);
                displayBaseTextBubbleView(position,imageHolder, bean);
                break;
            case MESSAGE_TYPE_SENT_CALL://发送通话
                BaseCallViewHolder callHolder;
                if (convertView == null) {
                    callHolder = new BaseCallViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_sent_call, null);
                    callHolder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    callHolder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    callHolder.content_tv = (EmojiconTextView) convertView.findViewById(R.id.content_tv);
                    callHolder.bubble_logo_iv = (ImageView) convertView.findViewById(R.id.bubble_logo_iv);
                    callHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    callHolder.status_iv = (ImageView) convertView.findViewById(R.id.msg_status_iv);
                    baseConvertViewListener(callHolder);
                    convertView.setTag(callHolder);
                } else {
                    callHolder = (BaseCallViewHolder) convertView.getTag();
                }
                displayStatusView(callHolder, bean);
                displayBaseTextBubbleView(position, callHolder, bean);
                displayCallBubbleView(position ,callHolder, bean);
                break;
            case MESSAGE_TYPE_SENT_AUDIO:{//发语音条
                BaseAudioViewHolder holder;
                if (convertView == null) {
                    holder = new BaseAudioViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_sent_voice, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.audio_iv = (ImageView) convertView.findViewById(R.id.audio_iv);
                    holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    holder.status_iv = (ImageView) convertView.findViewById(R.id.msg_status_iv);
                    holder.duration_tv = (TextView) convertView.findViewById(R.id.duration_tv);
                    holder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (BaseAudioViewHolder) convertView.getTag();
                }
                switch (bean.getState()) {
                    case ChatBean.SUCCESS: // 发送成功
                        break;
                    case ChatBean.FAIL: // 发送失败
                        break;
                    case ChatBean.INPROGRESS: // 发送中
                        break;
                    default:
                        break;
                }
                displayStatusView(holder, bean);
                displayAudioBubbleView(position,holder, bean);
            }
                break;
            case MESSAGE_TYPE_RECV_AUDIO:{
                //收到的语音条
                BaseAudioViewHolder holder;
                if (convertView == null) {
                    holder = new BaseAudioViewHolder();
                    convertView = mInflater.inflate(R.layout.chatitem_received_voice, null);
                    holder.avatar_iv = (ImageView) convertView.findViewById(R.id.avatar_iv);
                    holder.duration_tv = (TextView) convertView.findViewById(R.id.duration_tv);
                    holder.audio_iv = (ImageView) convertView.findViewById(R.id.audio_iv);
                    holder.bubble_rl = (RelativeLayout) convertView.findViewById(R.id.bubble_rl);
                    holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    holder.status_iv = (ImageView) convertView.findViewById(R.id.msg_status_iv);
                    holder.voice_unread_iv = (ImageView) convertView.findViewById(R.id.voice_unread_iv);
                    baseConvertViewListener(holder);
                    convertView.setTag(holder);
                } else {
                    holder = (BaseAudioViewHolder) convertView.getTag();
                }

                displayReadedView(holder, bean);
                displayStatusView(holder, bean);
                displayAudioBubbleView(position,holder, bean);
            }
                break;
            default:
                convertView = new View(mActivity);
                break;
        }
        return convertView;
    }

    public void displayBaseTextBubbleView(int position, BaseTextViewHolder holder, ChatBean bean) {
//        SimpleCommonUtils.spannableEmoticonFilter(holder.content_tv, bean.getContent());

//        long startTime=System.currentTimeMillis();   //获取开始时间
        holder.content_tv.setText(bean.getContent());
//        long endTime=System.currentTimeMillis(); //获取结束时间
//        LogUtil.e(bean.getContent() + " = "+(endTime-startTime)+"ms");

        GlideLoaderUtil.loadImage(mActivity,
                ValueUtil.getQiniuUrlByFileName(bean.getIssender() == 0?bean.getOther_photo():myUserPhoto,true)
                ,R.drawable.user_photo,holder.avatar_iv);
        holder.avatar_iv.setTag(position);
        holder.bubble_rl.setTag(position);
        holder.content_tv.setTag(position);
    }

    public void displayBaseImageBubbleView(int position, BaseImageViewHolder holder, ChatBean bean) {
        if (bean.getThumbnailImageWidth() != 0 && bean.getThumbnailImageHeight() != 0){
            holder.bubble_iv.getLayoutParams().width = (int)bean.getThumbnailImageWidth();
            holder.bubble_iv.getLayoutParams().height = (int)bean.getThumbnailImageHeight();
        } else {
            holder.bubble_iv.getLayoutParams().width = (int)ValueUtil.dip2px(mActivity,ChatBean.IMAGE_MAX_SIZE_DP);
            holder.bubble_iv.getLayoutParams().height = (int)ValueUtil.dip2px(mActivity,ChatBean.IMAGE_MAX_SIZE_DP);
        }
        GlideLoaderUtil.loadImage(mActivity,
                ValueUtil.getQiniuUrlByFileName(bean.getIssender() == 0?bean.getOther_photo():myUserPhoto,true)
                ,R.drawable.user_photo,holder.avatar_iv);
        holder.avatar_iv.setTag(position);
        holder.bubble_iv.setTag(position);
    }

    private void displayBaseGiftBubbleView(int position, BaseGiftViewHolder holder, ChatBean bean) {

        GlideLoaderUtil.loadGif(mActivity,bean.getFilename(),false,0,holder.gift_iv);

        GlideLoaderUtil.loadImage(mActivity,
                ValueUtil.getQiniuUrlByFileName(bean.getIssender() == 0?bean.getOther_photo():myUserPhoto,true)
                ,R.drawable.user_photo,holder.avatar_iv);
        holder.avatar_iv.setTag(position);
    }

    public void displayCallBubbleView(int position, BaseCallViewHolder holder, ChatBean bean) {
        if (bean.getIssender() == 1) {
            holder.bubble_logo_iv.setImageResource(bean.getSubtype() == ChatBean.SUBTYPE_CALL_AUDIO
                    ?R.drawable.audio_receiver_right:R.drawable.video_right);
        } else {
            holder.bubble_logo_iv.setImageResource(bean.getSubtype() == ChatBean.SUBTYPE_CALL_AUDIO
                    ?R.drawable.audio_receiver_left:R.drawable.video_left);
        }
    }

    public void displayAudioBubbleView(int position, BaseAudioViewHolder holder, ChatBean bean) {
        holder.bubble_rl.setTag(position);
        holder.duration_tv.setText(bean.getDuration()+"''");
        int increment = (int) (screenWidthPx / 2 / AudioRecordManager.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND * bean.getDuration());

        ViewGroup.LayoutParams params = holder.bubble_rl.getLayoutParams();
        params.width = ValueUtil.dip2px(mActivity,65) + increment;
        holder.bubble_rl.setLayoutParams(params);

        GlideLoaderUtil.loadImage(mActivity,
                ValueUtil.getQiniuUrlByFileName(bean.getIssender() == 0?bean.getOther_photo():myUserPhoto,true)
                ,R.drawable.user_photo,holder.avatar_iv);
        holder.avatar_iv.setTag(position);
    }

    public void displayStatusView(BaseViewHolder holder, ChatBean bean) {
        switch (bean.getState()) {
            case ChatBean.SUCCESS: // 发送成功
                holder.progressBar.setVisibility(View.GONE);
                holder.status_iv.setVisibility(View.GONE);
                if (bean.getSubtype() == ChatBean.SUBTYPE_IMAGE){
                    BaseImageViewHolder imageHolder = (BaseImageViewHolder)holder;
                    imageHolder.bubble_iv.setProgressVisible(false);
                    imageHolder.bubble_iv.showShadow(false);
                }
                break;
            case ChatBean.FAIL: // 发送失败
                holder.progressBar.setVisibility(View.GONE);
                holder.status_iv.setVisibility(View.VISIBLE);
                break;
            case ChatBean.INPROGRESS: // 发送中
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.status_iv.setVisibility(View.GONE);
                break;
            default:
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.status_iv.setVisibility(View.GONE);
                break;
        }
    }

    public void displayReadedView(BaseAudioViewHolder holder, ChatBean bean) {
        switch (bean.getState()) {
            case ChatBean.SUCCESS: // 发送成功
                holder.voice_unread_iv.setVisibility(bean.getPlayed() == 1?View.GONE:View.VISIBLE);
                break;
            case ChatBean.FAIL: // 发送失败
                holder.voice_unread_iv.setVisibility(View.GONE);
                break;
            case ChatBean.INPROGRESS: // 发送中
                holder.voice_unread_iv.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void setMyUserPhoto(String myUserPhoto) {
        this.myUserPhoto = myUserPhoto;
    }

    public void setOnLinkClickListener(QMUILinkTextView.OnLinkClickListener mOnLinkClickListener) {
        this.mOnLinkClickListener = mOnLinkClickListener;
    }

    public void setOnChatActionListener(ChatActionListener onChatActionListener) {
        this.onChatActionListener = onChatActionListener;
    }

    private void baseConvertViewListener(BaseViewHolder baseViewHolder){
        baseViewHolder.avatar_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChatActionListener.onAvatarClick(view);
            }
        });
        if (baseViewHolder instanceof BaseImageViewHolder){
            BaseImageViewHolder imageViewHolder = (BaseImageViewHolder)baseViewHolder;
            imageViewHolder.bubble_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChatActionListener.onBubbleClick(view);
                }
            });
            imageViewHolder.bubble_iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onChatActionListener != null){
                        return onChatActionListener.onBubbleLongClick(view);
                    }
                    return false;
                }
            });
        }  else if (baseViewHolder instanceof BaseCallViewHolder){
            BaseCallViewHolder callViewHolder = (BaseCallViewHolder)baseViewHolder;
            callViewHolder.bubble_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("dz","BaseCallViewHolder onClick");
                    onChatActionListener.onBubbleClick(view);
                }
            });
            callViewHolder.content_tv.setEnabled(false);
//            callViewHolder.content_tv.setFocusable(false);
        } else if (baseViewHolder instanceof BaseTextViewHolder){
            BaseTextViewHolder textViewHolder = (BaseTextViewHolder)baseViewHolder;
            textViewHolder.bubble_rl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onChatActionListener != null){
                        return onChatActionListener.onBubbleLongClick(view);
                    }
                    return false;
                }
            });
        } else if (baseViewHolder instanceof BaseAudioViewHolder) {
            BaseAudioViewHolder audioViewHolder = (BaseAudioViewHolder) baseViewHolder;
            audioViewHolder.bubble_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChatActionListener.onBubbleClick(view);
                }
            });
        }
    }

    public static class TipsViewHolder {
        public TextView time_tv;
    }

    public static class BaseViewHolder {
        public ImageView avatar_iv;
        public ProgressBar progressBar;
        public ImageView status_iv;
    }

    public static class BaseTextViewHolder extends BaseViewHolder{
        public RelativeLayout bubble_rl;
        public EmojiconTextView content_tv;
    }

    public static class ViewHolderRevText extends BaseTextViewHolder{}

    public static class ViewHolderSendText extends BaseTextViewHolder{
        public TextView percentageView;
    }

    public static class BaseImageViewHolder extends BaseViewHolder{
        public BubbleImageView bubble_iv;
    }

    public static class BaseCallViewHolder extends BaseTextViewHolder{
        public ImageView bubble_logo_iv;
    }

    public static class BaseAudioViewHolder extends BaseViewHolder{
        public ImageView audio_iv;
        public TextView duration_tv;
        public RelativeLayout bubble_rl;

        public ImageView voice_unread_iv;
    }

    public static class BaseGiftViewHolder extends BaseViewHolder{
        public ImageView gift_iv;
    }

//    public static class ViewHolderRevImage extends BaseImageViewHolder{}
//
//    public static class ViewHolderSendImage extends BaseImageViewHolder{}

}