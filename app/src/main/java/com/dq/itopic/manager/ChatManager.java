package com.dq.itopic.manager;

import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dq.itopic.activity.chat.EaseNotifier;
import com.dq.itopic.activity.chat.call.SingleCallActivity;
import com.dq.itopic.activity.chat.call.CallRefuseMyDialingEvent;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.ChatListResponse;
import com.dq.itopic.bean.SendMessageResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.LogUtil;
import com.dq.itopic.tools.NetWorkCallback;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.DBReq;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Request;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class ChatManager {

	//下面4个要和服务器一致，123已经被聊天push占用
	public final static int PUSH_TYPE_REMIND_COMMENT = 4;// 被评论提醒通知 不入库，只是推送时候作为type
	public final static int PUSH_TYPE_REMIND_PRAISE = 5;// 被赞提醒通知 不入库，只是推送时候作为type
	public final static int PUSH_TYPE_REMIND_FANS = 6;// 被粉丝提醒通知 不入库，只是推送时候作为type
	public final static int PUSH_TYPE_REMIND_SYSYEM = 7;// 系统消息提醒通知 不入库，只是推送时候作为type
	public final static int PUSH_TYPE_REMIND_AT = 8;// 被@提醒通知 不入库，只是推送时候作为type
	public final static int PUSH_TYPE_CALL_REFUSE = 10;// 对方拒绝了我的通话申请(注意是未接通的通话申请)

	//下面的4个仅限安卓内部使用
	public final static int REMIND_SUBTYPE_COMMENT = 0x100;// 被评论
	public final static int REMIND_SUBTYPE_PRAISE = 0x200;// 被点赞
	public final static int REMIND_SUBTYPE_FANS = 0x300;// 被粉
	public final static int REMIND_SUBTYPE_AT = 0x400;// 被@

	private ITopicApplication mApp;
	private EaseNotifier notifierManager;//系统通知栏管理
	private ArrayList<SendMessageListener> onSendMessageListenerList;//发消息回调
	private ArrayList<NewMessageGetListener> onNewMessageGetListenerList;//收到新消息（包含聊天和其他通知）管理
	private ArrayList<MessageHadRead> onMessageHadReadList;//消息被我读（包含聊天和其他通知）
	private NewChatBeanGetListener onNewChatGetListener;//收到新聊天消息，单独回调给chat界面

	private android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper());

	public ChatManager(ITopicApplication mApp) {
		this.mApp = mApp;
		onSendMessageListenerList = new ArrayList<SendMessageListener>();
		onNewMessageGetListenerList = new ArrayList<NewMessageGetListener>();
		onMessageHadReadList = new ArrayList<MessageHadRead>();
		notifierManager = new EaseNotifier(mApp);
	}

	public void closeNewMessageGetListener() {
		onNewMessageGetListenerList.clear();
	}

	public void addOnSendMessageListener(
			SendMessageListener onSendMessageListener) {
		if (onSendMessageListener!=null && !onSendMessageListenerList.contains(onSendMessageListener)) {			
			this.onSendMessageListenerList.add(onSendMessageListener);
		}
	}

	public void removeOnSendMessageListener(
			SendMessageListener onSendMessageListener) {
		if (onSendMessageListener!=null && onSendMessageListenerList.contains(onSendMessageListener)) {			
			this.onSendMessageListenerList.remove(onSendMessageListener);
		}
	}

	/**
	 * 发消息
	 * @param messageBean
	 */
	public void sendChatMessage(final ChatBean messageBean) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("targetid", messageBean.getTargetid());
		params.put("content", messageBean.getContent());
		params.put("type", ""+messageBean.getType());
		params.put("subtype", ""+messageBean.getSubtype());
		params.put("filename", messageBean.getFilename());
		params.put("extend", messageBean.getExtend());
		params.put("username", mApp.getMyUserBeanManager().getInstance().getName());
		OkHttpHelper.getInstance().post(ServiceConstants.IP+"chat/send", params, new CompleteCallback<SendMessageResponse>(SendMessageResponse.class,mApp) {

			@Override
			public void onComplete(Response okhttpResponse, SendMessageResponse postResponse) {
				// TODO Auto-generated method stub
				sendNotification(postResponse,messageBean);
			}
		});
	}

	public void sendNotification(SendMessageResponse response , ChatBean chatModel){
		response.setClient_messageid(chatModel.getClient_messageid());
		response.setTargetid(chatModel.getTargetid());

		//根据刚才是否网络成功 来更新本地数据库
		if (response.isSuccess()) {
			DBReq.getInstence(mApp).updateChatMessageState(response.getClient_messageid(),ChatBean.SUCCESS);
		} else {
			DBReq.getInstence(mApp).updateChatMessageState(response.getClient_messageid(),ChatBean.FAIL);
		}

		for (SendMessageListener onSendMessageListener : onSendMessageListenerList) {
			onSendMessageListener.onSendMessageComplete(response);
		}
	}

	public EaseNotifier getNotifierManager() {
		return notifierManager;
	}


	/**
	 * 我主动发消息 发送过程的回调
	 */
	public interface SendMessageListener {

		public void onSendMessageInserted(ChatBean messageBean);

		public void onSendMessageComplete(SendMessageResponse postResponse);

	}

	//收到第3方的穿透消息（华为push，个推push）
	public void handleThirdPartyPush(String payloaddata){

		final HashMap<String,String> payloadMessage = JsonUtil.getObject(payloaddata, HashMap.class);
		if (payloadMessage != null && "1".equals(payloadMessage.get("needpull"))){
			//说明是聊天消息，聊天消息不处理，由needpull处理
			pullMesssages();
		} else if (payloadMessage != null && !TextUtils.isEmpty(payloadMessage.get("type"))){
			switch (Integer.parseInt(payloadMessage.get("type"))) {
				//下面这3种类型的，needpull都是0
				case ChatManager.PUSH_TYPE_REMIND_COMMENT:
					mApp.getUnreadNoticeManager().insertNewCommentRemind(payloadMessage.get("dataid"));
					break;
				case ChatManager.PUSH_TYPE_REMIND_PRAISE:
					mApp.getUnreadNoticeManager().insertNewPraiseRemind(payloadMessage.get("dataid"));
					break;
				case ChatManager.PUSH_TYPE_REMIND_FANS:
					mApp.getUnreadNoticeManager().insertNewFansRemind(payloadMessage.get("dataid"));
					break;
				case ChatManager.PUSH_TYPE_REMIND_AT:
					mApp.getUnreadNoticeManager().insertAtRemind(payloadMessage.get("dataid"));
					break;
				case PUSH_TYPE_CALL_REFUSE:{
					CallManager callManager = mApp.getCallManager();
					if(payloadMessage.get("channelid").equals(callManager.channelId)){
						//推送来的被拒绝的channelid 就是当期那的channelid
						CallRefuseMyDialingEvent articleEvent = new CallRefuseMyDialingEvent();
						EventBus.getDefault().post(articleEvent);
					} else {
						//不出bug情况下不会进入这里
					}
				}
				break;
				default:
					//说明是聊天消息，聊天消息不处理，由needpull处理
					break;
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					//通知各大root界面，收到了新的消息类型
					List<Integer> newMessagesTypes = new ArrayList<Integer>();
					newMessagesTypes.add(Integer.valueOf(Integer.parseInt(payloadMessage.get("type"))));
					ChatManager.this.sendNewMessageNotity(newMessagesTypes);
				}
			});
		}
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// 判断app是否在后台,如果是，弹出本地通知
				ChatManager.this.getNotifierManager().ringViberate(payloadMessage);
			}
		});
	}


	/**
	 * 拉消息
	 * 有接口返回了msg不为0。或许推送来了新消息，就拉一遍
	 */
	public void pullMesssages(){
		HashMap<String, String> params = new HashMap<String, String>();

		OkHttpHelper.getInstance().get(ServiceConstants.IP+"message/pull", params, new CompleteCallback<ChatListResponse>(ChatListResponse.class,mApp) {

			@Override
			public void onComplete(Response okhttpResponse, ChatListResponse postResponse) {
				// TODO Auto-generated method stub
				if (!postResponse.isSuccess()) {
					return;
				}

				if (!mApplication.getMyUserBeanManager().isLogin()) {
					return;
				}

				List<Integer> newMessagesTypes = new ArrayList<Integer>();
				List<ChatBean> newChatBeanList = new ArrayList<ChatBean>();

				for (ChatBean chatBean : postResponse.getData()) {
					switch (chatBean.getType()) {
						case ChatBean.TYPE_CHAT_SINGLE:
						case ChatBean.TYPE_CHAT_GROUP:
							chatBean.setState(ChatBean.SUCCESS);
							chatBean.setIssender(0);//当前登录账号是这个消息的发送者
							chatBean.setHadread(0);//0未读

							if(chatBean.getType() == ChatBean.TYPE_CHAT_SINGLE){
								//单聊的话，服务器返回的targetid肯定是我自己。为了手机本地group查询处理方便，将targetid入库时候设置为对方uid
								chatBean.setTargetid(chatBean.getOther_userid());
							}

							if(chatBean.getSubtype() == ChatBean.SUBTYPE_CALL_AUDIO){
								//声音需要下载
								chatBean.setState(ChatBean.INPROGRESS);
							}

							//只有聊天消息才插入聊天表
							DBReq.getInstence(mApp).insertNewChatMessage(chatBean);

							if(chatBean.getSubtype() == ChatBean.SUBTYPE_AUDIO){
								//声音需要下载
								final int msgid = chatBean.getMsgid();
								OkHttpHelper.getInstance().downLoadFile(mApp, chatBean.getFilename(),
										new NetWorkCallback<String>(String.class,mApp) {
											@Override
											public void onSuccess(Response okhttpResponse, String bitmap) {
												DBReq.getInstence(mApp).updateChatMessageStateByMsgid(msgid, ChatBean.SUCCESS);
												//单独回调给chat界面
												if (onNewChatGetListener != null)
													onNewChatGetListener.onChatMessageFileDownloadComplete(msgid,ChatBean.SUCCESS);
											}

											@Override
											public void onFailure(Request okhttpRequest, Exception e) {
												DBReq.getInstence(mApp).updateChatMessageStateByMsgid(msgid, ChatBean.FAIL);
												//单独回调给chat界面
												if (onNewChatGetListener != null)
													onNewChatGetListener.onChatMessageFileDownloadComplete(msgid,ChatBean.FAIL);
											}
										});
							}

							newChatBeanList.add(chatBean);

							switch (chatBean.getSubtype()) {
								case ChatBean.SUBTYPE_CALL_AUDIO:
								case ChatBean.SUBTYPE_CALL_VIDEO: {

									if(Integer.parseInt(chatBean.getExtend()) == CallManager.CallStatus.CallHangup.ordinal()){
										//对方已经挂断了
										LogUtil.e("对方已经挂断了,我没接听到");
										break;
									}
									CallManager callManager = mApp.getCallManager();
									//这里要判断我当前是不是在通话中
									if (callManager.callStatus != CallManager.CallStatus.CallHangup){
										//当前我正在通话，没空接听
										LogUtil.e("当前我正在通话，没空接听 = "+callManager.callStatus);
										break;
									}
									callManager.channelId = chatBean.getFilename();
									callManager.targetId = chatBean.getTargetid();
									callManager.callStatus = CallManager.CallStatus.CallIncoming;
									callManager.mediaType = chatBean.getSubtype();
									callManager.startTime = (int)(System.currentTimeMillis()/1000);
									callManager.other_name = chatBean.getOther_name();
									callManager.other_photo = chatBean.getOther_photo();

									Intent intent = new Intent(mApp, SingleCallActivity.class);
									intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
									mApp.startActivity(intent);
								}
								break;
								default:
									break;
							}

							break;
					}
					newMessagesTypes.add(Integer.valueOf(chatBean.getType()));
				}

				//通知各大root界面，收到了新的消息类型
				sendNewMessageNotity(newMessagesTypes);

				//单独回调给chat界面
				if (onNewChatGetListener != null){
					onNewChatGetListener.onNewChatBeanGet(newChatBeanList);
				}
			}
		});
	}

	//通知各大root界面，收到了新的消息类型
	public void sendNewMessageNotity(List<Integer> newMessagesTypes){
		for (NewMessageGetListener onSendMessageListener : onNewMessageGetListenerList) {
			onSendMessageListener.onNewMessageGet(newMessagesTypes);
		}
	}

	public void addOnNewMessageGetListener(
			NewMessageGetListener onNewMessageGetListener) {
		if (onNewMessageGetListener != null
				&& !onNewMessageGetListenerList.contains(onNewMessageGetListener)) {
			this.onNewMessageGetListenerList.add(onNewMessageGetListener);
		}
	}

	public void removeOnNewMessageGetListener(
			NewMessageGetListener onNewMessageGetListener) {
		if (onNewMessageGetListener != null
				&& onNewMessageGetListenerList.contains(onNewMessageGetListener)) {
			this.onNewMessageGetListenerList.remove(onNewMessageGetListener);
		}
	}

	/**
	 * 收到的消息集合 返回消息集合类型
	 * 观察者
	 */
	public interface NewMessageGetListener {
		public void onNewMessageGet(List<Integer> messageTypes);
	}

	/**
	 * 收到的聊天消息
	 */
	public interface NewChatBeanGetListener {
		public void onNewChatBeanGet(List<ChatBean> newChatBeanList);
		public void onChatMessageFileDownloadComplete(int msgid, int state);
	}


	public void setOnNewChatGetListener(NewChatBeanGetListener onNewChatGetListener) {
		this.onNewChatGetListener = onNewChatGetListener;
	}


	/**
	 * 消息已读，目前是单点 由chatActivty调用thisMessageHadRead方法， thisMessageHadRead
	 * 做数据库刷新通知 再 给对话activity 和mainactivity
	 * 
	 * @author acer
	 * 
	 */
	public interface MessageHadRead {
		public void onMessageHadRead(int messageType);
	}

	public void addOnMessageHadRead(MessageHadRead onMessageHadRead) {
		if (onMessageHadRead != null
				&& !onMessageHadReadList.contains(onMessageHadRead)) {
			onMessageHadReadList.add(onMessageHadRead);
		}
	}

	public void removeOnMessageHadRead(MessageHadRead onMessageHadRead) {
		if (onMessageHadRead != null
				&& onMessageHadReadList.contains(onMessageHadRead)) {
			onMessageHadReadList.remove(onMessageHadRead);
		}
	}

	/**
	 * 由chatActivty调用：通知给对话activity和数据库刷新为已读
	 * 由ConversationActivity调用：删除了改对话列表，仅仅借用该方法刷新界面（mainAc上的红圈）
	 */
	public void thisMessageHadRead(int messageType) {
		for (MessageHadRead onMessageHadRead : onMessageHadReadList) {
			onMessageHadRead.onMessageHadRead(messageType);
		}
	}

	public void cancelAllNotificaton() {
		notifierManager.cancelAllNotificaton();
	}
}
