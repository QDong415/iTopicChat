package com.dq.itopic.activity.mine.setting;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutUsActivity extends BaseActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		TextView version = (TextView) findViewById(R.id.version_name);
		version.setText("版本号：" + getVersionName(this));
	
	}

	/**
	 * 获得apk版本号
	 */
	public static String getVersionName(Context context) {
		try {
			final String PackageName = context.getPackageName();
			return context.getPackageManager().getPackageInfo(PackageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "未知";
	}


}
