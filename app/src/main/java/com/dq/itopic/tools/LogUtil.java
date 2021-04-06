package com.dq.itopic.tools;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dq.itopic.manager.ITopicApplication;

public class LogUtil {

	public static void e(String log) {
		Log.e("dz", log);
	}

	public static void i(String log) {
//		Log.i("dz", log);
	}

	public static void d(String log) {
//		Log.d("dz", log);
	}
}

