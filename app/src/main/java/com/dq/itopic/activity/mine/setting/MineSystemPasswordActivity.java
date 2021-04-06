package com.dq.itopic.activity.mine.setting;

import java.util.HashMap;

import okhttp3.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;

public class MineSystemPasswordActivity extends BaseActivity {

	private EditText old_password_et,new_password_et1,new_password_et2;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_password);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
		 Button update_btn = (Button) findViewById(R.id.release_btn);
		 old_password_et= (EditText) findViewById(R.id.old_password_et);
		 new_password_et1= (EditText) findViewById(R.id.new_password_et1);
		 new_password_et2= (EditText) findViewById(R.id.new_password_et2);
		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (old_password_et.getText().toString().trim().equals("")
						||new_password_et1.getText().toString().trim().equals("")
						||new_password_et2.getText().toString().trim().equals("")) {
					showToast("密码不能为空");
					return;
				}
				if (!new_password_et1.getText().toString().trim().equals(new_password_et2.getText().toString().trim())) {
					showToast("两次密码不一致");
					return;
				}
				hideKeyboard();
				progress.show();

				final String password = new_password_et1.getText().toString().trim();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("oldpassword", old_password_et.getText().toString());
				params.put("password", password);
				
				OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/changepw", params, new CompleteCallback<BaseResponse>(BaseResponse.class,getITopicApplication()) {

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
		});

	}


}
