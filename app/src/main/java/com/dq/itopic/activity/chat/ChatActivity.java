package com.dq.itopic.activity.chat;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import com.dq.itopic.R;
import com.dq.itopic.activity.chat.bubble.BubbleImageView;
import com.dq.itopic.activity.chat.bubble.ChatActionListener;
import com.dq.itopic.activity.chat.bubble.ChatListAdapter;
import com.dq.itopic.activity.chat.call.CallMessageUpdataEvent;
import com.dq.itopic.activity.chat.call.SingleCallActivity;
import com.dq.itopic.activity.chat.listview.TopAutoRefreshListView;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.SendMessageResponse;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.layout.TextDialog;
import com.dq.itopic.manager.CallManager;
import com.dq.itopic.manager.ChatManager;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.tools.DBReq;
import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.audio2.AudioPlayManager;
import com.dq.itopic.tools.audio2.IAudioPlayListener;
import com.dq.itopic.views.linktextview.QMUILinkTextView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.shinichi.library.ImagePreview;

public class ChatActivity extends ChatInputActivity implements ChatManager.SendMessageListener, ChatManager.NewChatBeanGetListener
        , TopAutoRefreshListView.OnTopRefreshListener {

    // ??????????????????
    private String targetid;//???????????????targetid???????????????userid????????????????????????id

    private String hisName;//?????????????????????????????????????????????????????????
    private String hisPhoto;
    private int type;

    private ChatManager chatManager;
    private List<ChatBean> messageList;

    private TopAutoRefreshListView chatListView;

    private ChatListAdapter chatListAdapter;

    private ITopicApplication mApp;
    private AudioPlayManager audioPlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        initListener();
    }

    private void initView() {
        mApp = getITopicApplication();
        setTitleName(getIntent().getStringExtra("name"));
        targetid = getIntent().getStringExtra("targetid");
        hisName = getIntent().getStringExtra("name");
        hisPhoto = getIntent().getStringExtra("avatar");
        type = getIntent().getIntExtra("type",ChatBean.TYPE_CHAT_SINGLE);
        initInputView();
        chatListView = findViewById(R.id.lv_chat);
        chatManager = getITopicApplication().getChatManager();
        //????????????
        DBReq.getInstence(getITopicApplication()).readChatWithTargetid(targetid);
        chatManager.thisMessageHadRead(type);

        initListView();
        chatManager.cancelAllNotificaton();
        EventBus.getDefault().register(this);

        audioPlayManager = new AudioPlayManager();
    }

    private void initListener() {
        // TODO Auto-generated method stub
        backButtonListener();

        chatManager.addOnSendMessageListener(this);
        chatManager.setOnNewChatGetListener(this);
    }

    @Override
    public void onDestroy() {
        chatManager.removeOnSendMessageListener(this);
        chatManager.setOnNewChatGetListener(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // ??????notification bar???????????????????????????????????????????????????
        String userid = intent.getStringExtra("targetid");
        if (targetid.equals(userid)){
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onSendMessageInserted(ChatBean messageBean) {

    }

    @Override
    public void onSendMessageComplete(SendMessageResponse postResponse) {
        if (targetid.equals(postResponse.getTargetid())){
            for (int i = messageList.size() - 1; i >= 0; i-- ) {
                ChatBean messageBean = messageList.get(i);
                if (postResponse.getClient_messageid().equals(messageBean.getClient_messageid())){
                    if (postResponse.isSuccess()){
                        messageBean.setState(ChatBean.SUCCESS);
                    } else {
                        messageBean.setState(ChatBean.FAIL);
                    }
//                    chattingListAdapter.notifyDataSetChanged();
                    View itemView = getViewByPosition(i, chatListView);
                    if (itemView != null){
                        ChatListAdapter.BaseViewHolder holder = (ChatListAdapter.BaseViewHolder) itemView.getTag();
                        if (holder != null){
                            chatListAdapter.displayStatusView(holder ,messageBean);
                        }
                    }
                    break;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallMessageUpdataEvent mMessageEvent) {
        for (int i = messageList.size() - 1; i >= 0; i-- ) {
            ChatBean messageBean = messageList.get(i);
            if (mMessageEvent.getChannelId().equals(messageBean.getFilename())){
                messageBean.setExtend(String.valueOf(mMessageEvent.getCallState().ordinal()));
                messageBean.setContent(mMessageEvent.getContent());
                messageBean.processModelForItem(this);
                chatListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * ??????listView???item?????????
     */
    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return null;
//            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex + chatListView.getHeaderViewsCount());
        }
    }

    @Override
    public void onNewChatBeanGet(List<ChatBean> newChatBeanList) {
        //????????????,?????????newChatBeanList???????????????
        boolean contains = false;
        ArrayList<ChatBean> tempChatBeanList = new ArrayList<ChatBean>();
        for (ChatBean chatBean : newChatBeanList) {
            if (targetid.equals(chatBean.getTargetid())){
                //????????????????????????????????????
                tempChatBeanList.add(chatBean);
                contains = true;
            }
        }

        if (contains){
            int lastRemindTime = 0;//????????????tips
            if (messageList.size() > 0) {
                lastRemindTime = messageList.get(messageList.size() - 1).getCreate_time();
            }
            for (ChatBean bean : tempChatBeanList) {
                if (bean.getCreate_time() - lastRemindTime > 5 * 60) { //?????????????????????tips????????????5??????
                    lastRemindTime = bean.getCreate_time();
                    //UI??????create_time??????nil??????????????????tips???
                    bean.setNeedShowTimeTips(true);
                }
            }
            ArrayList<ChatBean> timerHandledMessageList = handleTimeMessage(tempChatBeanList);
            messageList.addAll(timerHandledMessageList);

            chatListAdapter.notifyDataSetChanged();
            scrollToBottom();
            DBReq.getInstence(getITopicApplication()).readChatWithTargetid(targetid);
            chatManager.thisMessageHadRead(type);
            chatManager.cancelAllNotificaton();
        }
    }

    @Override
    public void onChatMessageFileDownloadComplete(int msgid, int state){
        ArrayList<ChatBean> tempChatBeanList = new ArrayList<ChatBean>();
        for (int i = messageList.size() - 1; i >= 0 ; i--) {
            ChatBean chatBean = messageList.get(i);
            if (chatBean.getMsgid() == msgid){
                chatBean.setState(state);
                View itemView = getViewByPosition(i, chatListView);
                if (itemView != null){
                    ChatListAdapter.BaseViewHolder holder = (ChatListAdapter.BaseViewHolder) itemView.getTag();
                    if (holder != null){
                        chatListAdapter.displayStatusView(holder ,chatBean);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // ????????????????????????
                    //1 ????????????????????????????????????
                    List<String> sandboxFileNameArray = new ArrayList<String>();

                    long currentTime = System.currentTimeMillis()/1000;

                    List<LocalMedia> selectLocalMedia = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia localMedia : selectLocalMedia) {

                        //??????????????? + ??????
                        String path = TextUtils.isEmpty(localMedia.getAndroidQToPath())?localMedia.getPath():localMedia.getAndroidQToPath();

                        int lastIndex = path.lastIndexOf(".");
                        String pictureType = lastIndex<0?"":path.substring(lastIndex);
                        StringBuilder sb = new StringBuilder();
                        String key = sb.append(targetid).append("-").append(currentTime).append("-").append(Math.random() * 10000).append(pictureType).toString();
                        String boxfilepath =  ValueUtil.findVoiceLocalPathWithFileName(ChatActivity.this,key);
                        File sandFile = new File(boxfilepath);
                        copyFile(PictureMimeType.isGif(localMedia.getMimeType())?path:localMedia.getCompressPath(),sandFile);
                        sandboxFileNameArray.add(key);
                    }
                    for (int i = 0 ; i < selectLocalMedia.size() ; i++){
                        LocalMedia localMedia = selectLocalMedia.get(i);
                        HashMap<String,Integer> map = new HashMap<>();
                        map.put("width",localMedia.getWidth());
                        map.put("height",localMedia.getHeight());
                        sendImageMessage(sandboxFileNameArray.get(i),JsonUtil.getJson(map),localMedia);
                    }
                    break;
            }
        }
    }


    private void initListView() {
        UserBean myUserBean = getITopicApplication().getMyUserBeanManager().getInstance();

        ArrayList<ChatBean> tempMessageList = DBReq.getInstence(getITopicApplication()).getChatListWithTargetid(targetid,0);
        messageList = handleTimeMessage(tempMessageList);

        chatListAdapter = new ChatListAdapter(this,messageList);
        chatListAdapter.setMyUserPhoto(myUserBean.getAvatar());
        chatListAdapter.setOnChatActionListener(new OnChatActionListener());
        chatListAdapter.setOnLinkClickListener(mOnLinkClickListener);
        chatListView.setAdapter(chatListAdapter);
        scrollToBottom();

        //????????????????????????????????????setTopRefreshEnable???true???
        if (tempMessageList.size() < 20){
            chatListView.setTopRefreshEnable(false);
        } else {
            chatListView.setOnTopRefreshListener(this);
        }

        chatListView.setOnTopAutoRefreshScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ekBar.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public void onTopRefresh() {
        ArrayList<ChatBean> tempMessageList = DBReq.getInstence(getITopicApplication()).getChatListWithTargetid(targetid
                ,messageList.get(0).getCreate_time());
        for (ChatBean tempChatBean : tempMessageList) {
            tempChatBean.processModelForItem(this);
        }
        ArrayList<ChatBean> timerHandledMessageList = handleTimeMessage(tempMessageList);
        messageList.addAll(0, timerHandledMessageList);

        chatListAdapter.notifyDataSetChanged();
        chatListView.setSelection(timerHandledMessageList.size());

        chatListView.onTopRefreshFinished();

        if (tempMessageList.size() < 20){
            chatListView.setTopRefreshEnable(false);
        }
    }

    private ArrayList<ChatBean> handleTimeMessage(ArrayList<ChatBean> tempMessageList){
        ArrayList<ChatBean> timerHandledMessageList = new ArrayList<ChatBean>();
        for (ChatBean model : tempMessageList) {
            model.processModelForItem(this);
            if (model.isNeedShowTimeTips()) {
                //???????????????tipscell????????????array
                ChatBean timemodel = new ChatBean();
                timemodel.setType(ChatBean.SUBTYPE_TIPS);
                timemodel.setCreate_time(model.getCreate_time());
                timemodel.setContent(ValueUtil.getTimeStringFromNow(model.getCreate_time(),false));
                timerHandledMessageList.add(timemodel);
            }
            timerHandledMessageList.add(model);
        }
        return timerHandledMessageList;
    }

    //??????????????? ??? ??????????????????????????????
    private boolean addMessageToDataSource(ChatBean willSendModel)
    {
        willSendModel.processModelForItem(this);
        ChatBean lastMessage = null;
        if (messageList.size() > 0) {
            lastMessage = messageList.get(messageList.size() - 1);
        }
        if (lastMessage == null || willSendModel.getCreate_time() - lastMessage.getCreate_time() > 5 * 60) { //?????????????????????tips????????????5??????
            willSendModel.setNeedShowTimeTips(true);
            //???????????????tipscell????????????array
            ChatBean timemodel = new ChatBean();
            timemodel.setType(ChatBean.SUBTYPE_TIPS);
            timemodel.setCreate_time(willSendModel.getCreate_time());
            timemodel.setContent(ValueUtil.getTimeStringFromNow(willSendModel.getCreate_time(),false));
            messageList.add(timemodel);
        }
        messageList.add(willSendModel);

        boolean needSuccess = true;
        //??????????????????????????????????????????????????????????????????????????????needSuccess?????????false

        chatListAdapter.notifyDataSetChanged();
        //if ([self shouldScrollToBottomForNewMessage]){
        scrollToBottom();
        return  needSuccess;
    }

    private ChatBean createCommonChatModel(){
        ChatBean willSendModel = new ChatBean();

        //??????????????????ID,??????????????????,??????????????????:unix?????????+rand(1,1000)
        willSendModel.setClient_messageid(targetid + (""+System.currentTimeMillis()) + (int) (Math.random() * 1000));
        if (type == ChatBean.TYPE_CHAT_SINGLE){
            willSendModel.setOther_userid(targetid);
            willSendModel.setOther_photo(hisPhoto);
            willSendModel.setOther_name(hisName);
        } else {
            UserBean instanceUser = getITopicApplication().getMyUserBeanManager().getInstance();
            willSendModel.setOther_userid(instanceUser.getUserid());
            willSendModel.setOther_photo(instanceUser.getName());
            willSendModel.setOther_name(instanceUser.getAvatar());
        }
        willSendModel.setTargetid(targetid);
        willSendModel.setCreate_time((int)(System.currentTimeMillis() / 1000));
        willSendModel.setState(ChatBean.INPROGRESS);
        willSendModel.setType(type);
        willSendModel.setHadread(1);
        willSendModel.setIssender(1);
        return willSendModel;
    }

    @Override
    protected void onSendBtnClick(String content) {
        ChatBean willSendModel = createCommonChatModel();
        willSendModel.setExtend("");
        willSendModel.setContent(content);
        willSendModel.setSubtype(ChatBean.SUBTYPE_TEXT);
        willSendModel.setFilename("");

        //??????????????????
        DBReq.getInstence(getITopicApplication()).insertNewChatMessage(willSendModel);

        //???????????????????????????
        boolean needSuccess = addMessageToDataSource(willSendModel);

        if (needSuccess) {
            //??????????????????
            chatManager.sendChatMessage(willSendModel);
        } else {
            SendMessageResponse postResponse = new SendMessageResponse();
            chatManager.sendNotification(postResponse, willSendModel);
        }
    }

    @Override
    protected void sendCallMessage(int callType) {
        ChatBean willSendModel = createCommonChatModel();
        willSendModel.setExtend(String.valueOf(CallManager.CallStatus.CallDialing.ordinal()));
        willSendModel.setContent(callType == ChatBean.SUBTYPE_CALL_AUDIO?"????????????":"????????????");
        willSendModel.setSubtype(callType);
        StringBuilder sb = new StringBuilder();
        sb.append(getUserID());
        sb.append(":");
        sb.append(targetid);
        sb.append(":");
        sb.append((int)(System.currentTimeMillis()/1000));
        willSendModel.setFilename(sb.toString());

        //??????????????????
        DBReq.getInstence(getITopicApplication()).insertNewChatMessage(willSendModel);
        //??????????????????
        chatManager.sendChatMessage(willSendModel);
        //???????????????????????????
        addMessageToDataSource(willSendModel);
        //call???????????????Manager
        CallManager callManager = getITopicApplication().getCallManager();
        callManager.channelId = willSendModel.getFilename();
        callManager.targetId = targetid;
        callManager.other_name = willSendModel.getOther_name();
        callManager.other_photo = willSendModel.getOther_photo();
        callManager.callStatus = CallManager.CallStatus.CallDialing;
        callManager.mediaType = callType;
        callManager.startTime = (int)(System.currentTimeMillis()/1000);

        //??????????????????
        Intent i = new Intent(ChatActivity.this,SingleCallActivity.class);
        startActivity(i);
    }

    private void sendImageMessage(String filename, String extend ,LocalMedia localMedia)
    {
        final ChatBean willSendModel = createCommonChatModel();
        willSendModel.setExtend(extend);
        willSendModel.setContent("[????????????]");
        willSendModel.setSubtype(ChatBean.SUBTYPE_IMAGE);
        willSendModel.setFilename(filename);
        //??????????????????
        DBReq.getInstence(getITopicApplication()).insertNewChatMessage(willSendModel);
        //???????????????????????????
        addMessageToDataSource(willSendModel);
        //????????????
        UploadOptions opinion = new UploadOptions(null, null, false, new UpProgressHandler() {

            @Override
            public void progress(String key,final double percent) {
                //????????????????????????
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //?????????????????????????????????????????????post???????????????????????????????????????????????????????????????????????????????????????95%
                        //????????????????????????
                        willSendModel.setFileUploadProgress((int)(percent * 95));
                        // ??????????????????????????????????????????item?????????
                        int firstVisiblePosition = chatListView.getFirstVisiblePosition() - chatListView.getHeaderViewsCount();
                        int lastVisiblePosition = chatListView.getLastVisiblePosition();
                        for (int i = messageList.size() - 1; i >= 0 ; i--) {
                            ChatBean chatBean = messageList.get(i);
                            if (chatBean.getSubtype() == ChatBean.SUBTYPE_IMAGE && chatBean.equals(willSendModel)){
                                if (i >= firstVisiblePosition && i <= lastVisiblePosition) {
                                    View view = chatListView.getChildAt(i - firstVisiblePosition);
                                    if (view != null) {
                                        BubbleImageView bubble_iv = view.findViewById(R.id.bubble_iv);
                                        bubble_iv.setPercent(willSendModel.getFileUploadProgress());
                                    }
                                }
                            }
                        }
                    }
                });
            }
        },null);

        getITopicApplication().getHomeManager().uploadFile(filename, localMedia, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (response != null) {
                    //??????????????????????????????
                    chatManager.sendChatMessage(willSendModel);
                } else {
                    //?????????????????????????????????????????????
                    SendMessageResponse postResponse = new SendMessageResponse();
                    chatManager.sendNotification(postResponse,willSendModel);
                }
            }
        },opinion);
    }

    @Override
    protected void sendAudioMessage(String filename, String extend ,String sandboxFilePath)
    {
        final ChatBean willSendModel = createCommonChatModel();
        willSendModel.setExtend(extend);
        willSendModel.setContent("[????????????]");
        willSendModel.setSubtype(ChatBean.SUBTYPE_AUDIO);
        willSendModel.setFilename(filename);
        //??????????????????
        DBReq.getInstence(getITopicApplication()).insertNewChatMessage(willSendModel);
        //???????????????????????????
        final boolean needSuccess = addMessageToDataSource(willSendModel);
        //????????????
        UploadOptions opinion = new UploadOptions(null, null, false, new UpProgressHandler() {

            @Override
            public void progress(String key,final double percent) {
                //????????????????????????
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //?????????????????????????????????????????????post???????????????????????????????????????????????????????????????????????????????????????95%
                        //????????????????????????
                        willSendModel.setFileUploadProgress((int)(percent * 95));
                    }
                });
            }
        },null);

        getITopicApplication().getHomeManager().uploadFile(filename, sandboxFilePath, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (response != null) {
                    //??????????????????????????????
                    if (needSuccess) {
                        //??????????????????
                        chatManager.sendChatMessage(willSendModel);
                    } else {
                        SendMessageResponse postResponse = new SendMessageResponse();
                        chatManager.sendNotification(postResponse, willSendModel);
                    }
                } else {
                    //?????????????????????????????????????????????
                    SendMessageResponse postResponse = new SendMessageResponse();
                    chatManager.sendNotification(postResponse,willSendModel);
                }
            }
        },opinion);
    }

    @Override
    protected void onSendImage(String image) {
        if (!TextUtils.isEmpty(image)) {
            onSendBtnClick("[img]" + image);
        }
    }

    @Override
    protected void scrollToBottom() {
//        lvChat.requestLayout();
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                chatListView.setSelection(chatListView.getBottom());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioPlayManager.stopPlay();
    }

    public class OnChatActionListener implements ChatActionListener {

        @Override
        public void onAvatarClick(View view) {
            ChatBean chatBean = messageList.get((int)view.getTag());
            if (chatBean.getIssender() == 0){
                jumpToHisInfoActivity(chatBean.getOther_userid(),chatBean.getOther_name(),chatBean.getOther_photo());
            } else {
                UserBean myUserBean = getITopicApplication().getMyUserBeanManager().getInstance();
                jumpToHisInfoActivity(myUserBean.getUserid(),myUserBean.getName(),myUserBean.getAvatar());
            }
        }

        @Override
        public boolean onBubbleLongClick(View view) {
            ChatBean chatBean = messageList.get((int)view.getTag());
            if (chatBean.getSubtype() == ChatBean.SUBTYPE_TEXT){
                TextDialog dialog = new TextDialog(ChatActivity.this);
                dialog.initTextView(ChatActivity.this,chatBean.getContent());
                dialog.show();
            }
            return false;
        }

        @Override
        public void onBubbleClick(View view) {
            final ChatBean chatBean = messageList.get((int)view.getTag());
            if (chatBean.getSubtype() == ChatBean.SUBTYPE_IMAGE){
//                ArrayList<String> sourceImageList = new ArrayList<String>();
//                ArrayList<String> thumbImageList = new ArrayList<String>();
                String resultUrl = "";
                if (chatBean.getIssender() == 1){
                    resultUrl = ValueUtil.findVoiceLocalPathWithFileName(ChatActivity.this,chatBean.getFilename());
                } else {
//                    sourceImageList.add(ValueUtil.getQiniuUrlByFileName(chatBean.getFilename(), false));
//                    thumbImageList.add(ValueUtil.getQiniuUrlByFileName(
//                            chatBean.getFilename(),ValueUtil.dip2px(ChatActivity.this,ChatBean.IMAGE_MAX_SIZE_DP),false));
                    resultUrl = ValueUtil.getQiniuUrlByFileName(chatBean.getFilename(), false);
                }

                ArrayList<ImageView> imageViewList = new ArrayList<ImageView>();
                imageViewList.add((ImageView) view);

                ImagePreview.getInstance()
                        .setContext(ChatActivity.this)
                        .setIndex(0)
                        .setZoomTransitionDuration(400)
                        .setImage(resultUrl)
                        .setEnableDragClose(true)
                        .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                        .start();
            } else if (chatBean.getSubtype() == ChatBean.SUBTYPE_CALL_AUDIO){
                sendCallMessage(ChatBean.SUBTYPE_CALL_AUDIO);
            } else if (chatBean.getSubtype() == ChatBean.SUBTYPE_CALL_VIDEO){
                sendCallMessage(ChatBean.SUBTYPE_CALL_VIDEO);
            } else if (chatBean.getSubtype() == ChatBean.SUBTYPE_AUDIO){
                //???????????????
                if (chatBean.getState() == ChatBean.SUCCESS){//????????????????????????
                    final ImageView ivAudio = view.findViewById(R.id.audio_iv);
                    audioPlayManager.startPlay(ChatActivity.this,
                            Uri.parse(ValueUtil.findVoiceLocalPathWithFileName(ChatActivity.this, chatBean.getFilename()))
                            , new IAudioPlayListener() {

                        @Override
                        public void onStart(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.start();
                            }
                        }

                        @Override
                        public void onStop(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.stop();
                                animation.selectDrawable(0);
                            }
                        }

                        @Override
                        public void onComplete(Uri var1) {
                            if (ivAudio != null && ivAudio.getBackground() instanceof AnimationDrawable) {
                                AnimationDrawable animation = (AnimationDrawable) ivAudio.getBackground();
                                animation.stop();
                                animation.selectDrawable(0);
                            }
                        }
                    });

                    if (chatBean.getPlayed() == 0){
                        //???????????????
                        HashMap<String,String> extendMap = new HashMap<>();
                        extendMap.put("duration",String.valueOf(chatBean.getDuration()));
                        extendMap.put("hadplay","1");

                        DBReq.getInstence(mApp).updateMessageExtendByMsgid(chatBean.getMsgid(),JsonUtil.getJson(extendMap));
                        chatBean.setPlayed(1);

                        View itemView = getViewByPosition((int)view.getTag(), chatListView);
                        if (itemView != null){
                            ChatListAdapter.BaseAudioViewHolder holder = (ChatListAdapter.BaseAudioViewHolder) itemView.getTag();
                            if (holder != null){
                                chatListAdapter.displayReadedView(holder ,chatBean);
                            }
                        }
                    }
                }
            }
        }
    }

    private QMUILinkTextView.OnLinkClickListener mOnLinkClickListener = new QMUILinkTextView.OnLinkClickListener() {
        @Override
        public void onTelLinkClick(String phoneNumber) {
            TextDialog dialog = new TextDialog(ChatActivity.this);
            dialog.initPhoneView(ChatActivity.this,phoneNumber);
            dialog.show();
        }

        @Override
        public void onMailLinkClick(String mailAddress) {
            TextDialog dialog = new TextDialog(ChatActivity.this);
            dialog.initEmailView(ChatActivity.this,mailAddress);
            dialog.show();
        }

        @Override
        public void onWebUrlLinkClick(String url) {
            TextDialog dialog = new TextDialog(ChatActivity.this);
            dialog.initWebView(ChatActivity.this,url);
            dialog.show();
        }
    };

}
