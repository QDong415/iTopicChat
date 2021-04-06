package com.dq.itopic.activity.user;

import java.util.HashMap;

import okhttp3.Response;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class WarningReportActivity extends BaseActivity {
	private String hisUserName;
	private EditText editText1;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warning_report);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		hisUserName = getIntent().getStringExtra("hisUserID");
		 Button update_btn = (Button) findViewById(R.id.reg_btn);
		editText1= (EditText) findViewById(R.id.editText1);
		update_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editText1.getText().toString().trim().equals("")) {
					return;
				}
				hideKeyboard();
				progress.show();

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("to_userid", hisUserName==null?"":hisUserName);
				params.put("content", editText1.getText().toString());
				
				OkHttpHelper.getInstance().post(ServiceConstants.IP + "version/opinion_send", params, new CompleteCallback<BaseResponse>(BaseResponse.class,getITopicApplication()) {

					@Override
					public void onComplete(Response okhttpResponse, BaseResponse response) {
						// TODO Auto-generated method stub
						// 删除某条动态 接口返回
						progress.dismiss();
						if (response.isSuccess()) {
							showToast("提交成功");
							finish();				
						} else {
							showToast(response.getMessage());
						}
					}
				});
			}
		});
		backButtonListener();
	}

}
