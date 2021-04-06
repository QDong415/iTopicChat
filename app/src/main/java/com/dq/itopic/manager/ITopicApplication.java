package com.dq.itopic.manager;

import android.app.Application;
import android.content.Context;

public class ITopicApplication extends Application {
	// 其他一些杂七杂八的Manager管理类，现在是只有友盟相关的
	private OtherManager otherManage;
	// 处理当前登录的用户相关的信息
	private MyUserBeanManager myUserBeanManager;
	// 处理首页上的动态相关的，比如点赞评论
	private HomeManager homeManager;
	// 对话列表上面的一堆未读信息控制
	private UnreadNoticeManager unreadNoticeManager;
	//聊天管理
	private ChatManager chatManager;
	//音视频通话管理
	private CallManager callManager;

	private boolean hadInit; // 如果为false
								// 说明Application以及被系统回收了，需要重新初始化一遍所有的Manager

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	public void checkInit() {
		if (!hadInit) {
			myUserBeanManager = new MyUserBeanManager(this);
			unreadNoticeManager = new UnreadNoticeManager(this);
			otherManage = new OtherManager(this);
			homeManager = new HomeManager(this);
			chatManager = new ChatManager(this);
			callManager = new CallManager(this);
			myUserBeanManager.checkUserInfo();
			otherManage.initOther();
			hadInit = true;
		}
	}

	public OtherManager getOtherManage() {
		return otherManage;
	}

	public MyUserBeanManager getMyUserBeanManager() {
		return myUserBeanManager;
	}

	public HomeManager getHomeManager() {
		return homeManager;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
	}

	public UnreadNoticeManager getUnreadNoticeManager() {
		return unreadNoticeManager;
	}

	public CallManager getCallManager() {
		return callManager;
	}
}