package com.dq.itopic.activity.login;
import java.util.HashMap;

import okhttp3.Response;

import com.dq.itopic.R;
import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.views.ShakeAnimation;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class UserFindPasswordActivity extends SMSBaseActivity{

	public final static int REG_LOGIN_SUCCESS = 0x99;
	private EditText pw_et_1,new_password_et2;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_findpassword);
		initView();
		initListener();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		super.initView();
		// TODO Auto-generated method stub
		new_password_et2= (EditText) findViewById(R.id.new_password_et2);
		pw_et_1 = (EditText) findViewById(R.id.pw_et_1);
	}


	protected void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		backButtonListener();
		
		findViewById(R.id.release_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (phone_et.getText().toString().trim().equals("")){
							ShakeAnimation.create().with(findViewById(R.id.phone_et)).start();
							showToast("请输入手机号");
							return;
						}
						if (check_pw_et.getText().toString().trim().equals("")){
							ShakeAnimation.create().with(findViewById(R.id.check_pw_et)).start();
							showToast("请输入验证码");
							return;
						}
						if (pw_et_1.getText().toString().trim().equals("")){
							ShakeAnimation.create().with(findViewById(R.id.pw_et_1)).start();
							showToast("请输入密码");
							return;
						}
						if (!pw_et_1.getText().toString().trim().equals(new_password_et2.getText().toString().trim())) {
							ShakeAnimation.create().with(findViewById(R.id.pw_et_1)).start();
							ShakeAnimation.create().with(findViewById(R.id.new_password_et2)).start();
							showToast("两次密码不一致");
							return;
						}
						
						progress.show();
						requireValidate();
					}
				});
		backButtonListener();
	}

	private void requireValidate(){
		final String mobile = phone_et.getText().toString().trim();
		final String password = pw_et_1.getText().toString().trim();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("password", password);
		params.put("code", check_pw_et.getText().toString().trim());
		OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/findpw", params, new CompleteCallback<BaseResponse>(BaseResponse.class,getITopicApplication()) {

			@Override
			public void onComplete(Response okhttpResponse, BaseResponse response) {
				// TODO Auto-generated method stub
				// 删除某条动态 接口返回
				progress.dismiss();
				if (response.isSuccess()) {
					showToast("修改成功");
					finish();
				} else {
					showToast(response.getMessage());
				}
			}
		});
	}
	
	//验证码效验正确，由子类重写该方法，
	protected void checkVerificationSuccess(){

	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return super.onTouchEvent(event);
	}


}
