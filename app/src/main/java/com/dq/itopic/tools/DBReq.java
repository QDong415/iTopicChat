package com.dq.itopic.tools;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.manager.ITopicApplication;

public class DBReq {

	private DBHelper databaseHelper;

	public static int version = 1;

	private static DBReq instence;
	private boolean inited = false;

	private ITopicApplication mApp;

	private void init(ITopicApplication context) {
		getVersion(context);
		databaseHelper = new DBHelper(context, version);
		databaseHelper.getWritableDatabase();
		mApp = context;
		inited = true;
	}


	public boolean isInited() {
		return inited;
	}

	public synchronized static DBReq getInstence(ITopicApplication context) {
		if (instence == null || !instence.isInited()) {
			instence = new DBReq();
			instence.init(context);
		}
		return instence;
	}

	private int getVersion(Context con) {
		try {
			PackageManager manager = con.getPackageManager();
			PackageInfo info = manager.getPackageInfo(con.getPackageName(), 0);
			version = info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;
	}


	/******************************************************************************************
	 * chat start
	 */
	public int getChatTotalCount(){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(1) from "
				+ DBHelper.CHAT_TABLE + " where userid = '"+mApp.getMyUserBeanManager().getUserId() +"'", null);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	public int getChatTotalTodayCount(){
		long currentTimestamps = System.currentTimeMillis();
		long oneDayTimestamps = 86400000L;
		long todayTimeLong0 = currentTimestamps-(currentTimestamps+28800000)%oneDayTimestamps;
		int todayTime0 = (int)(todayTimeLong0/1000);

		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from ");
		sb.append(DBHelper.CHAT_TABLE);
		sb.append(" where userid = '");
		sb.append(mApp.getMyUserBeanManager().getUserId());
		sb.append("' and create_time > ");
		sb.append(todayTime0);

		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(sb.toString(), null);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	public int getChatTotalUnreadCount(){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();

		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from ");
		sb.append(DBHelper.CHAT_TABLE);
		sb.append(" where userid = '");
		sb.append(mApp.getMyUserBeanManager().getUserId());
		sb.append("' and hadread = 0");

		Cursor cursor = db.rawQuery(sb.toString(), null);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}

	public int getChatUnreadCountWithTargetid(String targetid){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		int count = getChatUnreadCountWithTargetid(targetid,db);
		db.close();
		return count;
	}

	public int getChatUnreadCountWithTargetid(String targetid ,SQLiteDatabase db){

		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from ");
		sb.append(DBHelper.CHAT_TABLE);
		sb.append(" where userid = '");
		sb.append(mApp.getMyUserBeanManager().getUserId());
		sb.append("' and targetid = '");
		sb.append(targetid);
		sb.append("' and hadread = 0");

		Cursor cursor = db.rawQuery(sb.toString(), null);
		int count = 0;
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}

	public ArrayList<ChatBean> getConversationList() {
		synchronized (databaseHelper) {
			ArrayList<ChatBean> list = new ArrayList<ChatBean>();
			SQLiteDatabase db = databaseHelper.getReadableDatabase();

			StringBuilder sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(DBHelper.CHAT_TABLE);
			sql.append(" where userid = '");
			sql.append(mApp.getMyUserBeanManager().getUserId());
			sql.append("' group by targetid order by create_time desc");

			Cursor cursor = db.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				ChatBean bean = new ChatBean();
				bean.setOther_userid(cursor.getString(cursor.getColumnIndexOrThrow("other_userid")));
				bean.setOther_name(cursor.getString(cursor.getColumnIndexOrThrow("other_name")));
				bean.setOther_photo(cursor.getString(cursor.getColumnIndexOrThrow("other_photo")));
				bean.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
				bean.setCreate_time(cursor.getInt(cursor.getColumnIndexOrThrow("create_time")));
				bean.setState(cursor.getInt(cursor.getColumnIndexOrThrow("state")));
				bean.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
				bean.setTargetid(cursor.getString(cursor.getColumnIndexOrThrow("targetid")));
				bean.setSubtype(cursor.getInt(cursor.getColumnIndexOrThrow("subtype")));
				bean.setExtend(cursor.getString(cursor.getColumnIndexOrThrow("extend")));
				bean.setIssender(cursor.getInt(cursor.getColumnIndexOrThrow("issender")));
				bean.setHadread(cursor.getInt(cursor.getColumnIndexOrThrow("hadread")));
				bean.setHisTotalUnReadedChatCount(getChatUnreadCountWithTargetid(bean.getTargetid(),db));
				list.add(bean);
			}
			cursor.close();
			db.close();
			return list;
		}
	}

	public ArrayList<ChatBean> getChatListWithTargetid(String targetid, int startTime) {
		synchronized (databaseHelper) {
			ArrayList<ChatBean> list = new ArrayList<ChatBean>();
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			StringBuilder sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(DBHelper.CHAT_TABLE);
			sql.append(" where userid = '");
			sql.append(mApp.getMyUserBeanManager().getUserId());
			sql.append("'");
			sql.append(" and targetid = '");
			sql.append(targetid);
			sql.append("'");
			if (startTime > 0){
				sql.append(" and create_time < ");
				sql.append(startTime);
			}
			sql.append(" order by create_time desc");
			sql.append(" limit 20");
			Cursor cursor = db.rawQuery(sql.toString(), null);
			while (cursor.moveToNext()) {
				ChatBean bean = new ChatBean();
				bean.setDbid(cursor.getInt(cursor.getColumnIndexOrThrow("dbid")));
				bean.setMsgid(cursor.getInt(cursor.getColumnIndexOrThrow("msgid")));
				bean.setClient_messageid(cursor.getString(cursor.getColumnIndexOrThrow("client_messageid")));
				bean.setOther_userid(cursor.getString(cursor.getColumnIndexOrThrow("other_userid")));
				bean.setOther_name(cursor.getString(cursor.getColumnIndexOrThrow("other_name")));
				bean.setOther_photo(cursor.getString(cursor.getColumnIndexOrThrow("other_photo")));
				bean.setContent(cursor.getString(cursor.getColumnIndexOrThrow("content")));
				bean.setCreate_time(cursor.getInt(cursor.getColumnIndexOrThrow("create_time")));
				bean.setState(cursor.getInt(cursor.getColumnIndexOrThrow("state")));
				bean.setType(cursor.getInt(cursor.getColumnIndexOrThrow("type")));
				bean.setSubtype(cursor.getInt(cursor.getColumnIndexOrThrow("subtype")));
				bean.setTargetid(cursor.getString(cursor.getColumnIndexOrThrow("targetid")));
				bean.setFilename(cursor.getString(cursor.getColumnIndexOrThrow("filename")));
				bean.setExtend(cursor.getString(cursor.getColumnIndexOrThrow("extend")));
				bean.setIssender(cursor.getInt(cursor.getColumnIndexOrThrow("issender")));
				bean.setHadread(cursor.getInt(cursor.getColumnIndexOrThrow("hadread")));

				list.add(0,bean);
			}

			int lastRemindTime = 0;//显示时间tips
			for (ChatBean bean : list) {
				if (bean.getCreate_time() - lastRemindTime > 5 * 60) { //和上次显示时间tips间隔超过5分钟
					lastRemindTime = bean.getCreate_time();
					//UI是用create_time是否nil，来判断显示tips的
					bean.setNeedShowTimeTips(true);
				}
			}
			cursor.close();
			db.close();
			return list;
		}
	}


	public void insertNewChatMessage(ChatBean chatBean){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("msgid", chatBean.getMsgid());
		values.put("client_messageid", chatBean.getClient_messageid());
		values.put("userid", mApp.getMyUserBeanManager().getUserId());
		values.put("other_userid", chatBean.getOther_userid());
		values.put("other_name", chatBean.getOther_name());
		values.put("other_photo", chatBean.getOther_photo());
		values.put("content", chatBean.getContent());
		values.put("create_time", chatBean.getCreate_time());
		values.put("state", chatBean.getState());
		values.put("type", chatBean.getType());
		values.put("targetid", chatBean.getTargetid());
		values.put("subtype", chatBean.getSubtype());
		values.put("filename", chatBean.getFilename());
		values.put("extend", chatBean.getExtend());
		values.put("issender", chatBean.getIssender());
		values.put("hadread", chatBean.getHadread());
		db.insert(DBHelper.CHAT_TABLE, null, values);
		db.close();
	}

	public void clearChatWithTargetid(String targetid)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {String.valueOf(mApp.getMyUserBeanManager().getUserId()),String.valueOf(targetid)};
		db.delete(DBHelper.CHAT_TABLE, "userid=? and targetid=?",args);
		db.close();
	}

	public void readChatWithTargetid(String targetid)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {String.valueOf(mApp.getMyUserBeanManager().getUserId()),String.valueOf(targetid)};
		ContentValues values = new ContentValues();
		values.put("hadread", "1");
		db.update(DBHelper.CHAT_TABLE, values,"userid=? and targetid=?",args);
		db.close();
	}

//	public void readChatMessageWithMsgid(int msgid)
//	{
//		SQLiteDatabase db = databaseHelper.getWritableDatabase();
//		String[] args = {String.valueOf(msgid)};
//		ContentValues values = new ContentValues();
//		values.put("hadread", "1");
//		db.update(DBHelper.CHAT_TABLE, values,"msgid=?",args);
//		db.close();
//	}

	public void updateChatMessageState(String client_messageid,int state)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {client_messageid};
		ContentValues values = new ContentValues();
		values.put("state", String.valueOf(state));
		db.update(DBHelper.CHAT_TABLE, values,"client_messageid=?",args);
		db.close();
	}

	public void updateChatMessageStateByMsgid(int msgid,int state)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {String.valueOf(msgid)};
		ContentValues values = new ContentValues();
		values.put("state", String.valueOf(state));
		db.update(DBHelper.CHAT_TABLE, values,"msgid=?",args);
		db.close();
	}


	public void updateCallMessageState(String channelid, int newCallState, String content)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {channelid};
		ContentValues values = new ContentValues();
		values.put("extend",newCallState);
		values.put("content", content);
		db.update(DBHelper.CHAT_TABLE, values,"filename=?",args);
		db.close();
	}

	public void updateMessageExtendByMsgid(int msgid, String extend)
	{
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] args = {String.valueOf(msgid)};
		ContentValues values = new ContentValues();
		values.put("extend",extend);
		db.update(DBHelper.CHAT_TABLE, values,"msgid=?",args);
		db.close();
	}

	/**
	 * chat end
	 ******************************************************************************************/

	public synchronized void close() {
		if (databaseHelper != null) {
			databaseHelper.close();
			databaseHelper = null;
		}
		instence.inited = false;
		instence = null;
	}


	public boolean ExecuteSQL(String sql) {
		synchronized (databaseHelper) {
			try {
				SQLiteDatabase db = databaseHelper.getWritableDatabase();
				boolean ret = SafeDBExecute(db, sql);
				db.close();
				return ret;
			} catch (Exception e) {
				return false;
			}
		}
	}


	public synchronized ArrayList<String[]> QuerySQL(String sql) {
		synchronized (databaseHelper) {
			ArrayList<String[]> list = new ArrayList<String[]>();

			try {

				SQLiteDatabase db = databaseHelper.getReadableDatabase();

				Cursor cursor = db.rawQuery(sql, null);
				while (cursor.moveToNext()) {
					int col = cursor.getColumnCount();
					String[] row = new String[col];
					for (int i = 0; i < col; i++) {
						row[i] = cursor.getString(i);
					}
					list.add(row);
				}
				cursor.close();
				db.close();
				return list;
			} catch (Exception e) {
				
			}
			return list;
		}

	}


	private boolean SafeDBExecute(SQLiteDatabase db, String sql) {
		try {
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
