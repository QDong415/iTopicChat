package com.dq.itopic.bean;

import android.content.Context;
import android.text.TextUtils;

import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.ValueUtil;

import java.util.HashMap;

public class ChatBean {

    /**
     * 每一条字段后面的注释表明它的数据来源 ：
     *
     * sqlite表示该变量来自手机本地sqlite数据库，服务器上无此字段，比如 是否已读，是否发送成功
     * service表示该变量来自服务器接口返回
     * service+sqlite 表示服务器也会返回，手机端也会去存
     * temp表示不入库也不是网络请求，只是Activity为了临时处理
     */

    public final static int INPROGRESS = 0;//客户端自己定义的。发送中
    public final static int SUCCESS = 1;//客户端自己定义的。发送成功
    public final static int FAIL = 2;//客户端自己定义的。发送失败

    public final static int TYPE_CHAT_SINGLE = 1;//单聊
    public final static int TYPE_CHAT_GROUP = 2;//群聊

    public final static int SUBTYPE_TIPS = 99;//聊天时候的tips，不入库

    public final static int SUBTYPE_TEXT = 1;//文本消息
    public final static int SUBTYPE_IMAGE = 2;//图片消息
    public final static int SUBTYPE_AUDIO = 3;//语音条消息
    public final static int SUBTYPE_GIFT = 4;//礼物消息

    public final static int SUBTYPE_CALL_AUDIO = 10;//电话消息
    public final static int SUBTYPE_CALL_VIDEO = 11;//视频消息

    private int dbid;//手机端本地数据库主键（sqlite）
    private int msgid;//服务器返回的主键id（service + sqlite）

    //如果是群聊（type==2），这里是发布人id; 如果是单聊，这里是对方id
    //如果是服务器返回的ChatBean，这里是发布人id，也就是对方id；
    //如果是本地发消息时候创建的单聊ChatBean，就用代码设为对方id； 如果是本地发消息时候创建的群聊ChatBean，就用代码设为自己
    private String other_userid; //（service + sqlite）
    private String other_name; //（service + sqlite）
    private String other_photo; //（service + sqlite）

    //如果是群聊（type==2），这里是群id；如果是单聊，这里是对方的userid（同other_userid）
    private String targetid; //（service + sqlite）

    private String content;//消息文本内容 //（service + sqlite）
    private int create_time;//1491545686 //（service + sqlite）
    private int type;//1为单聊 2为群聊 3为聊天时候的tips 4系统通知 //（service + sqlite）
    private int subtype;//0为文本 1为图片 2为语音 目前只可能为0 //（service + sqlite）
    private int issender;//当前登录账号是这个消息的发送者 //（sqlite）
    private int hadread;//0默认，未读 //（sqlite）

    //extend = 服务器返回的json，如果subtype是1图片，那么extend是{"height":2340,"width":1080}
    //如果subtype是2语音条，那么extend是{"duration":"6"}
    private String extend; //（service + sqlite）

    //filename = 附件本地沙盒文件名，同时也是七牛文件名
    //如果subtype是1图片，或者2语音条，那么就是文件名
    //如果subtype是11或者10电话消息，那么就是1611:800018:1586864352 （发送人id:接受人id:时间戳）
    private String filename; //（service + sqlite）

    // 图片
    private float thumbnailImageWidth;//客户端自己定义的，从extend解析出来的图片宽 //（temp)
    private float thumbnailImageHeight;//客户端自己定义的，从extend解析出来的图片高 //（temp)
    private int fileUploadProgress;//附件上传进度，范围为0--100 //（temp)

    //语音条
    private int duration;//秒,从extend解析出来的 //（temp)
    private int played;//0==没播放过，1==播放过，从extend解析出来的 //（temp)

    private int state;//客户端自己定义的。发送状态 //（sqlite)
    private int hisTotalUnReadedChatCount;//客户端自己定义的。与这个人的所有的未读消息数量 // (temp)
//    private String timeLag;//client定义的。直接显示的时间
    private String client_messageid;//客户端自己定义的临时消息id。// (sqlite)
    private boolean needShowTimeTips;//客户端自己定义的。是否要显示时间tips //（temp)

    public boolean isNeedShowTimeTips() {
        return needShowTimeTips;
    }

    public void setNeedShowTimeTips(boolean needShowTimeTips) {
        this.needShowTimeTips = needShowTimeTips;
    }

    public String getClient_messageid() {
        return client_messageid;
    }

    public void setClient_messageid(String client_messageid) {
        this.client_messageid = client_messageid;
    }

    public int getHisTotalUnReadedChatCount() {
        return hisTotalUnReadedChatCount;
    }

    public void setHisTotalUnReadedChatCount(int hisTotalUnReadedChatCount) {
        this.hisTotalUnReadedChatCount = hisTotalUnReadedChatCount;
    }


    public int getHadread() {
        return hadread;
    }

    public void setHadread(int hadread) {
        this.hadread = hadread;
    }

    public int getIssender() {
        return issender;
    }

    public void setIssender(int issender) {
        this.issender = issender;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public String getOther_photo() {
        return TextUtils.isEmpty(other_photo)?"":other_photo;
    }

    public void setOther_photo(String other_photo) {
        this.other_photo = other_photo;
    }

    public String getOther_name() {
        return TextUtils.isEmpty(other_name)?("用户"+other_userid):other_name;
    }

    public void setOther_name(String other_name) {
        this.other_name = other_name;
    }

    public String getOther_userid() {
        return TextUtils.isEmpty(other_userid)?"":other_userid;
    }

    public void setOther_userid(String other_userid) {
        this.other_userid = other_userid;
    }

    public boolean isChatTips(){
        return type == SUBTYPE_TIPS;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }

    public float getThumbnailImageWidth() {
        return thumbnailImageWidth;
    }

    public float getThumbnailImageHeight() {
        return thumbnailImageHeight;
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFileUploadProgress() {
        return fileUploadProgress;
    }

    public void setFileUploadProgress(int fileUploadProgress) {
        this.fileUploadProgress = fileUploadProgress;
    }

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public int getMsgid() {
        return msgid;
    }

    public void setMsgid(int msgid) {
        this.msgid = msgid;
    }

    public void processModelForItem(Context context){
        if (subtype == SUBTYPE_IMAGE){
            HashMap<String,Integer> map = JsonUtil.getObject(extend,HashMap.class);
            calculaThumhSize(context,Integer.parseInt(String.valueOf(map.get("width"))),Integer.parseInt(String.valueOf(map.get("height"))));
        } else if (subtype == SUBTYPE_AUDIO){
            HashMap<String,String> map = JsonUtil.getObject(extend,HashMap.class);
            duration = Integer.parseInt(map.get("duration"));
            played = map.get("hadplay") == null?0:1;
        }
    }

    public final static int IMAGE_MAX_SIZE_DP = 150;

    public void calculaThumhSize(Context context, int largeWidth, int largeHeight){

        if (this.thumbnailImageHeight > 0){
            return;
        }

        int IMAGE_MAX_SIZE = (int)ValueUtil.dip2px(context,IMAGE_MAX_SIZE_DP);

        float thumhWidth;
        float thumhHeight;

        if (largeHeight < largeWidth) {
            // 图片很扁
            if (largeWidth < IMAGE_MAX_SIZE){
                // 图片很扁，但是宽度依然小于150dp
                thumhWidth = largeWidth;
                thumhHeight = largeHeight;
            } else {
                thumhWidth = IMAGE_MAX_SIZE;
                thumhHeight = largeHeight * IMAGE_MAX_SIZE / largeWidth;
            }
        } else {
            // 图片很长
            if (largeHeight < IMAGE_MAX_SIZE){
                // 图片很扁，但是宽度依然小于150dp
                thumhWidth = largeWidth;
                thumhHeight = largeHeight;
            } else {
                thumhHeight = IMAGE_MAX_SIZE;
                thumhWidth = largeWidth * IMAGE_MAX_SIZE / largeHeight;
            }
        }
        this.thumbnailImageHeight = thumhHeight;
        this.thumbnailImageWidth = thumhWidth;
    }

    public int getDuration() {
        return duration;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }
}
