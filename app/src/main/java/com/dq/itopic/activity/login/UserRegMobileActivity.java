package com.dq.itopic.activity.login;


import com.dq.itopic.R;
import com.dq.itopic.views.ShakeAnimation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class UserRegMobileActivity extends SMSBaseActivity  {

	private String name,avatar,openid;
	private EditText password_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_reg_mobile);
		initView();
		initListener();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		password_et = (EditText)findViewById(R.id.password_et);
		backButtonListener();
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		findViewById(R.id.next_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (phone_et.getText().toString().trim().length() < 8){
							ShakeAnimation.create().with(findViewById(R.id.phone_et)).start();
							showToast("请输入正确手机号");
							return;
						}
						if (check_pw_et.getText().toString().trim().equals("")){
							ShakeAnimation.create().with(findViewById(R.id.check_pw_et)).start();
							showToast("请输入验证码");
							return;
						}
						if (password_et.getText().toString().trim().equals("")){
							ShakeAnimation.create().with(findViewById(R.id.password_et)).start();
							showToast("请输入密码");
							return;
						}

						Intent i = new Intent(UserRegMobileActivity.this,UserRegInfoPersonalActivity.class);
						i.putExtra("mobile",  phone_et.getText().toString().trim());
						i.putExtra("password",  password_et.getText().toString().trim());
						i.putExtra("name", name);
						i.putExtra("code", check_pw_et.getText().toString().trim());
						i.putExtra("avatar",  avatar);
						i.putExtra("openid", openid);
						startActivity(i);

//						checkVerificationCode();
					}
				});
		
		findViewById(R.id.go_login_tv).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(UserRegMobileActivity.this, UserLoginActivity.class);
				startActivity(i);
			}
		});
		
//		findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent i = new Intent(UserRegMobileActivity.this, UserRegInfoPersonalActivity.class);
//				String mobileend =  ""+System.currentTimeMillis();
//				String mobile = "1" + mobileend.substring(mobileend.length() - 10,mobileend.length());
//				i.putExtra("mobile", mobile);
//				i.putExtra("auto", true);
//				i.putExtra("name", name);
//				i.putExtra("avatar",  avatar);
//				i.putExtra("openid", openid);
//				startActivity(i);
//			}
//		});
	}

	//验证码效验正确，由子类重写该方法，
	protected void checkVerificationSuccess(){
		Intent i = new Intent(this,UserRegInfoPersonalActivity.class);
		i.putExtra("mobile",  phone_et.getText().toString().trim());
		i.putExtra("password",  password_et.getText().toString().trim());
		i.putExtra("name", name);
		i.putExtra("avatar",  avatar);
		i.putExtra("openid", openid);
		startActivity(i);
	}
//
//	@Override
//	public void onCancel(Platform arg0, int arg1) {
//		// TODO Auto-generated method stub
//		dismissProgress();
//	}
//
//	@Override
//	public void onComplete(final Platform platform, int action, final HashMap<String, Object> authresult) {
//		// TODO Auto-generated method stub
//		dismissProgress();
//		if (action == Platform.ACTION_USER_INFOR) {
//
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					if(QQ.NAME.equals(platform.getName())){
//						//QQ登录
//						name = authresult.get("nickname").toString();
//						avatar = authresult.get("figureurl_qq_2").toString();
//						openid = platform.getDb().getUserId();
//					}else if(Wechat.NAME.equals(platform.getName())){
//						//微信登录
//						name = authresult.get("nickname").toString();
//						avatar = authresult.get("headimgurl").toString();
//						openid = platform.getDb().getUserId();
//					}
//
//					showErrorToast("获取头像昵称成功，请认证手机号");
//				}
//			});
//		}
//	}
//
//	@Override
//	public void onError(Platform arg0, int arg1, Throwable arg2) {
//		// TODO Auto-generated method stub
//		dismissProgress();
//	}

	private void dismissProgress(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progress.dismiss();
			}
		});
	}
}

