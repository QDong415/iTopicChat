package com.dq.itopic.manager;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class UnreadNoticeManager {

	private ITopicApplication mContext;

	//未读的 赞我集合，数据源取自UserDefault，里面存的是<String> remindId
	private Set<String> unreadPraiseRemindCacheArray;
	private Set<String> unreadCommentRemindCacheArray;
	private Set<String> unreadFansRemindCacheArray;
	private Set<String> unreadAtRemindCacheArray;

	public UnreadNoticeManager(ITopicApplication mContext) {
		this.mContext = mContext;
	}

	public void resetUnreadCache(){
		unreadAtRemindCacheArray = null;
		unreadPraiseRemindCacheArray = null;
		unreadCommentRemindCacheArray = null;
		unreadFansRemindCacheArray = null;
	}

	public int unreadPraiseRemindCount(){
		//未读的 被点赞数
		return getUnreadPraiseRemindCacheArray().size();
	}

	//有新的被点赞推送
	public boolean insertNewPraiseRemind(String remindId) {
		if (getUnreadPraiseRemindCacheArray().contains(remindId)) {
			return false;
		}
		unreadPraiseRemindCacheArray.add(remindId);
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_PRAISE",Activity.MODE_PRIVATE);
		String unreadPraiseKey = "UNREAD_PRAISE_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadPraiseKey, unreadPraiseRemindCacheArray);
		editor.commit();
		return true;
	}

	//读完了所有未读的点赞
	public void readAllPraise() {
		getUnreadPraiseRemindCacheArray().clear();
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_PRAISE",Activity.MODE_PRIVATE);
		String unreadPraiseKey = "UNREAD_PRAISE_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadPraiseKey, unreadPraiseRemindCacheArray);
		editor.commit();
	}

	private Set<String> getUnreadPraiseRemindCacheArray() {
		if(unreadPraiseRemindCacheArray == null) {
			if (mContext.getMyUserBeanManager().isLogin()) {
				SharedPreferences pref = mContext.getSharedPreferences("REMIND_PRAISE" ,Activity.MODE_PRIVATE);
				unreadPraiseRemindCacheArray = pref.getStringSet("UNREAD_PRAISE_REMIND_" + mContext.getMyUserBeanManager().getUserId(), new HashSet<String>());
			} else {
				unreadPraiseRemindCacheArray = new HashSet<String>();
			}
		}
		return unreadPraiseRemindCacheArray;
	}


	public int unreadCommentRemindCount(){
		//未读的 被评论数
		return getUnreadCommentRemindCacheArray().size();
	}

	//有新的被评论推送
	public boolean insertNewCommentRemind(String remindId) {
		if (getUnreadCommentRemindCacheArray().contains(remindId)) {
			return false;
		}
		unreadCommentRemindCacheArray.add(remindId);
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_COMMENT", Activity.MODE_PRIVATE);
		String unreadCommentKey = "UNREAD_COMMENT_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadCommentKey, unreadCommentRemindCacheArray);
		editor.commit();
		return true;
	}

	//读完了所有未读的评论
	public void readAllComment() {
		getUnreadCommentRemindCacheArray().clear();
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_COMMENT",Activity.MODE_PRIVATE);
		String unreadCommentKey = "UNREAD_COMMENT_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadCommentKey, unreadCommentRemindCacheArray);
		editor.commit();
	}

	private Set<String> getUnreadCommentRemindCacheArray() {
		if(unreadCommentRemindCacheArray == null) {
			if (mContext.getMyUserBeanManager().isLogin()) {
				SharedPreferences pref = mContext.getSharedPreferences("REMIND_COMMENT" ,Activity.MODE_PRIVATE);
				unreadCommentRemindCacheArray = pref.getStringSet("UNREAD_COMMENT_REMIND_" + mContext.getMyUserBeanManager().getUserId(), new HashSet<String>());
			} else {
				unreadCommentRemindCacheArray = new HashSet<String>();
			}
		}
		return unreadCommentRemindCacheArray;
	}


	public int unreadFansRemindCount(){
		//未读的 被点赞数
		return getUnreadFansRemindCacheArray().size();
	}

	//有新的被点赞推送
	public boolean insertNewFansRemind(String remindId) {
		if (getUnreadFansRemindCacheArray().contains(remindId)) {
			return false;
		}
		unreadFansRemindCacheArray.add(remindId);
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_FANS",Activity.MODE_PRIVATE);
		String unreadFansKey = "UNREAD_FANS_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadFansKey, unreadFansRemindCacheArray);
		editor.commit();
		return true;
	}

	//读完了所有未读的点赞
	public void readAllFans() {
		getUnreadFansRemindCacheArray().clear();
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_FANS",Activity.MODE_PRIVATE);
		String unreadFansKey = "UNREAD_FANS_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadFansKey, unreadFansRemindCacheArray);
		editor.commit();
	}

	private Set<String> getUnreadFansRemindCacheArray() {
		if(unreadFansRemindCacheArray == null) {
			if (mContext.getMyUserBeanManager().isLogin()) {
				SharedPreferences pref = mContext.getSharedPreferences("REMIND_FANS" ,Activity.MODE_PRIVATE);
				unreadFansRemindCacheArray = pref.getStringSet("UNREAD_FANS_REMIND_" + mContext.getMyUserBeanManager().getUserId(), new HashSet<String>());
			} else {
				unreadFansRemindCacheArray = new HashSet<String>();
			}
		}
		return unreadFansRemindCacheArray;
	}


	public int unreadAtRemindCount(){
		//未读的 被点赞数
		return getUnreadAtRemindCacheArray().size();
	}

	//有新的被点赞推送
	public boolean insertAtRemind(String remindId) {
		if (getUnreadAtRemindCacheArray().contains(remindId)) {
			return false;
		}
		unreadAtRemindCacheArray.add(remindId);
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_AT",Activity.MODE_PRIVATE);
		String unreadAtKey = "UNREAD_AT_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadAtKey, unreadAtRemindCacheArray);
		editor.commit();
		return true;
	}

	//读完了所有未读的点赞
	public void readAllAt() {
		getUnreadAtRemindCacheArray().clear();
		SharedPreferences pref = mContext.getSharedPreferences("REMIND_AT",Activity.MODE_PRIVATE);
		String unreadAtKey = "UNREAD_AT_REMIND_"+mContext.getMyUserBeanManager().getUserId();
		SharedPreferences.Editor editor =  pref.edit().clear();
		editor.putStringSet(unreadAtKey, unreadAtRemindCacheArray);
		editor.commit();
	}

	private Set<String> getUnreadAtRemindCacheArray() {
		if(unreadAtRemindCacheArray == null) {
			if (mContext.getMyUserBeanManager().isLogin()) {
				SharedPreferences pref = mContext.getSharedPreferences("REMIND_AT" ,Activity.MODE_PRIVATE);
				unreadAtRemindCacheArray = pref.getStringSet("UNREAD_AT_REMIND_" + mContext.getMyUserBeanManager().getUserId(), new HashSet<String>());
			} else {
				unreadAtRemindCacheArray = new HashSet<String>();
			}
		}
		return unreadAtRemindCacheArray;
	}
}
