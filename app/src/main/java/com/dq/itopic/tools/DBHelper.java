package com.dq.itopic.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String name = "itopic.db";
	private int version;

	public static final String CHAT_TABLE = "chat";

	public DBHelper(Context context, int version) {
		super(context,  name, null, version);
		this.version = version;
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		ver1(db);
		if (version > 1) {
			for (int i = 2; i <= version; i++) {
				a: switch (i) {
				case 2:
					ver2(db);
					break a;
				}
			}
		}
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion >= 1) {
			for (int i = oldVersion + 1; i <= newVersion; i++) {
				a: switch (i) {
				case 2:
					ver2(db);
					break a;
				}
			}
		}
	}

	public final void ver1(SQLiteDatabase db) {

		StringBuilder sql = new StringBuilder();
		sql.append("create table if not exists ");
		sql.append(CHAT_TABLE);
		sql.append("(dbid INTEGER PRIMARY KEY AUTOINCREMENT,");
		sql.append("userid varchar(20),msgid integer,client_messageid varchar(20),targetid varchar(20),other_userid varchar(20),other_name varchar(50),other_photo varchar(100),content varchar(4096),create_time integer,state tinyint(1),type tinyint(1),subtype tinyint(1),filename varchar(60),extend varchar(100),issender tinyint(1),hadread tinyint(1)");
		sql.append(")");
		db.execSQL(sql.toString());
	}
	

	private void ver2(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}
}
