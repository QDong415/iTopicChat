package com.dq.itopic.activity.mine.setting;

import com.dq.itopic.R;
import com.dq.itopic.activity.user.WarningReportActivity;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.layout.SureOrCancelDialog;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.views.popup.QMUIListPopup;
import com.dq.itopic.views.popup.QMUIPopup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatDelegate;
import okhttp3.Response;

public class MineSystemActivity extends BaseActivity implements OnClickListener {

	private TextView slience_tv,dark_tv;

	private QMUIListPopup releasePopup,darkPopup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		slience_tv = findViewById(R.id.slience_tv);
		dark_tv = findViewById(R.id.dark_tv);

		UserBean userBean = getITopicApplication().getMyUserBeanManager().getInstance();
		if (userBean != null){
			slience_tv.setText(userBean.getSlience()==0?"响铃":"静音");
		}

		int nightModel = AppCompatDelegate.getDefaultNightMode();
		switch (nightModel){
			case AppCompatDelegate.MODE_NIGHT_NO:
				dark_tv.setText("白天模式");
				break;
			case AppCompatDelegate.MODE_NIGHT_YES:
				dark_tv.setText("黑夜模式");
				break;
			default:
				dark_tv.setText("跟随系统");
				break;
		}

		backButtonListener();
		findViewById(R.id.exit_btn).setOnClickListener(this);
		findViewById(R.id.backlist_ll).setOnClickListener(this);
		findViewById(R.id.setting_ll3).setOnClickListener(this);
		findViewById(R.id.setting_opinion).setOnClickListener(this);
		findViewById(R.id.setting_ll5).setOnClickListener(this);
		findViewById(R.id.change_pw_ll).setOnClickListener(this);
		findViewById(R.id.slience_ll).setOnClickListener(this);
		findViewById(R.id.dark_ll).setOnClickListener(this);
	}

	private void initView() {
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = null;
		switch (v.getId()) {
	
		case R.id.exit_btn:
			getITopicApplication().getMyUserBeanManager().clean();
			finish();
			break;
			case R.id.dark_ll:
				if (checkLogined()){
					initDarkPopupIfNeed();
					darkPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
					darkPopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
					darkPopup.show(dark_tv);
				}
				break;

		case R.id.backlist_ll:
			if (checkLogined()) {
				 i = new Intent(MineSystemActivity.this, BlacklistActivity.class);
				 startActivity(i);
			}
			break;
			case R.id.change_pw_ll:
				if (checkLogined()) {
					i = new Intent(MineSystemActivity.this, MineSystemPasswordActivity.class);
					startActivity(i);
				}
				break;
		case R.id.setting_ll3:
			break;
		case R.id.setting_ll5:
			 i = new Intent(MineSystemActivity.this, AboutUsActivity.class);
			 startActivity(i);
			
			break;
		case R.id.setting_opinion:
			 i = new Intent(MineSystemActivity.this, WarningReportActivity.class);
			 startActivity(i);
			break;
			case R.id.slience_ll:
				if (checkLogined()){
					initReleasePopupIfNeed();
					releasePopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
					releasePopup.setPreferredDirection(QMUIPopup.DIRECTION_TOP);
					releasePopup.show(slience_tv);
				}
				break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	private void initReleasePopupIfNeed() {
		if (releasePopup == null) {
			final List<String> danceList = new ArrayList<>();
			danceList.add("响铃");
			danceList.add("静音");
			ArrayAdapter adapter = new ArrayAdapter<>(MineSystemActivity.this, R.layout.popup_list_item, danceList);

			releasePopup = new QMUIListPopup(MineSystemActivity.this, QMUIPopup.DIRECTION_TOP, adapter);
			releasePopup.create(ValueUtil.dip2px(MineSystemActivity.this, 120), ValueUtil.dip2px(MineSystemActivity.this, 280), new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					releasePopup.dismiss();

					slience_tv.setText(i==0?"响铃":"静音");

					HashMap<String, String> params = new HashMap<>();
					params.put("slience",""+i);
					getITopicApplication().getMyUserBeanManager().startEditInfoRun(params ,null);
				}
			});
		}
	}


	private void initDarkPopupIfNeed() {
		if (darkPopup == null) {
			final List<String> danceList = new ArrayList<>();
			danceList.add("跟随系统");
			danceList.add("白天模式");
			danceList.add("黑夜模式");
			ArrayAdapter adapter = new ArrayAdapter<>(MineSystemActivity.this, R.layout.popup_list_item, danceList);

			darkPopup = new QMUIListPopup(MineSystemActivity.this, QMUIPopup.DIRECTION_TOP, adapter);
			darkPopup.create(ValueUtil.dip2px(MineSystemActivity.this, 120),
					ValueUtil.dip2px(MineSystemActivity.this, 280), new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
							darkPopup.dismiss();
							dark_tv.setText(danceList.get(i));
							switch (i){
								case 0:
									AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
									break;
								case 1:
									AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
									break;
								case 2:
									AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
									break;
							}

							startActivity(new Intent(MineSystemActivity.this,MineSystemActivity.class));
//							overridePendingTransition(R.anim.animo_alph_close, R.anim.animo_alph_close);
							finish();

							SharedPreferences pref = getSharedPreferences("darkmodel",
									Activity.MODE_PRIVATE);
							SharedPreferences.Editor editor = pref.edit();
							editor.putInt("darkmodel",i);
							editor.commit();

						}
					});
		}
	}


	private void isLastVersion() {
		// TODO Auto-generated method stub
		showToast("已经是最新版本了");
	}

	private void versionNetworkFail(String message) {
		// TODO Auto-generated method stub
		showToast();
	}

	private String getVersionName() {
		try {
			final String PackageName = getPackageName();
			return getPackageManager().getPackageInfo(PackageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}
	
}
