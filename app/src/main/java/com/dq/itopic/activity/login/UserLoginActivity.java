package com.dq.itopic.activity.login;

import java.util.HashMap;

import okhttp3.Response;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.activity.common.MainActivity;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.bean.UserResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserLoginActivity extends BaseActivity  {

	private EditText user_name_et, user_pw_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		initListener();		
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog(false, null);
		user_name_et = (EditText) findViewById(R.id.username_et);
		user_pw_et = (EditText) findViewById(R.id.check_pw_et);
		user_name_et.setText(getITopicApplication().getMyUserBeanManager().getMobile());
		if (!user_name_et.getText().toString().trim().equals("")) {
			user_pw_et.requestFocus();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					showKeyboard(user_pw_et);
				}
			}, 150);
		} else{
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					showKeyboard(user_name_et);
				}
			}, 150);
		}
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
		Button login_login = (Button) findViewById(R.id.login_btn);
		login_login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hideKeyboard();
				progress.show();

				String loginUserName = user_name_et.getText().toString().trim();
				String password = user_pw_et.getText().toString().trim();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("mobile", loginUserName);
				params.put("password", password);
				
				OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/login", params, new CompleteCallback<UserResponse>(UserResponse.class,getITopicApplication()) {

					@Override
					public void onComplete(Response okhttpResponse,final UserResponse response) {
						// TODO Auto-generated method stub
						if (response.isSuccess()) {

							UserBean userBean = response.getData();

							// 保存用户信息并开启推送
							getITopicApplication().getMyUserBeanManager().storeUserInfoAndNotity(userBean);

							progress.dismiss();
							// 进入主页面
							Intent i = new Intent(UserLoginActivity.this,MainActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivity(i);
							finish();

						} else {
							progress.dismiss();
							showToast(response.getMessage());
						}
					}
				});
			}
		});
		
		findViewById(R.id.lost_password_tv).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserLoginActivity.this,
						UserFindPasswordActivity.class);
				startActivity(i);
			}
		});
		
		TextView go_reg_tv = (TextView) findViewById(R.id.go_reg_tv);
		go_reg_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserLoginActivity.this,
						UserRegMobileActivity.class);
				startActivity(i);
			}
		});
	}

}
