package com.dq.itopic.activity.login;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Response;

import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.R;
import com.dq.itopic.bean.StringResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 用户效验手机号界面（验证码界面）
 * 本案采用的验证码是基于mob的短信smssdk，免费，且不需要服务器端接入。但是会带来一堆libssmssdk.so
 */
public class SMSBaseActivity extends BaseActivity {

	public final static int NETWORK_OTHER = 0x19;
	public final static int NETWORK_FAIL = 0x05;

	protected EditText phone_et, check_pw_et;
	private TextView check_pw_get_btn;
	private Timer timer;
	private TimerTask task;
	private int CURRENTDELAYTIME;
	private final int DELAYTIME = 60;
	
	private MyHandler baseHandler = new MyHandler(this);
	private static class MyHandler extends Handler {
		
		private WeakReference<Context> reference;
		
	    public MyHandler(Context context) {
	    	reference = new WeakReference<Context>(context);
	    }
	    
	    @Override
		public void handleMessage(Message msg) {
	    	final SMSBaseActivity activity = (SMSBaseActivity) reference.get();
			if(activity == null){
				return;
			}
			switch (msg.what) {
				case NETWORK_OTHER: // 验证码倒计时
					if (activity.CURRENTDELAYTIME <= 0) {
						activity.cancelTime();
					} else {
						activity.CURRENTDELAYTIME--;
						activity.check_pw_get_btn.setText(activity.CURRENTDELAYTIME + "秒后重获");
					}
					break;
				case NETWORK_FAIL:
					activity.progress.dismiss();
					break;
			}
		}
	};

	protected void initView() {
		// TODO Auto-generated method stub
		backButtonListener();
		timer = new Timer();
		phone_et = (EditText) findViewById(R.id.phone_et);
		check_pw_et = (EditText) findViewById(R.id.check_pw_et);

		initProgressDialog(false, null);
		baseHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showKeyboard(phone_et);
			}
		}, 150);
		
	}

	private void startTime() {
		CURRENTDELAYTIME = DELAYTIME;
		task = new TimerTask() {

			@Override
			public void run() {
				baseHandler.sendEmptyMessage(NETWORK_OTHER);
			}
		};
		timer.schedule(task, 0, 1000);
	}

	private void cancelTime() {
		if (task!=null) {
			task.cancel();
		}
		check_pw_get_btn.setClickable(true);
		check_pw_get_btn.setText("获取验证码");
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		check_pw_get_btn = (TextView) findViewById(R.id.check_pw_get_btn);
		check_pw_get_btn.setText("获取验证码");
		check_pw_get_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (phone_et.getText().toString().trim().length() != 11){
					showToast("请输入手机号码");
					return;
				}
				if (check_pw_get_btn.isClickable()) {
					check_pw_get_btn.setClickable(false);
					check_pw_get_btn.setText(DELAYTIME+"秒后重获");
				}
				progress.show();

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("mobile", phone_et.getText().toString().trim());
				
				OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/coderequire", params, new CompleteCallback<StringResponse>(StringResponse.class,getITopicApplication()) {

					@Override
					public void onComplete(Response okhttpResponse, StringResponse response) {
						// TODO Auto-generated method stub
						// 删除某条动态 接口返回
						progress.dismiss();
						if (response.isSuccess()) {
							check_pw_get_btn.setClickable(false);
							check_pw_get_btn.setText(DELAYTIME+"秒后重获");
							startTime();
						} else {
							showToast(response.getMessage());
						}
					}
				});
			}
		});
	}

	//验证码效验正确，由子类重写该方法，
	protected void checkVerificationSuccess(){

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null){
			timer.cancel();
			timer = null;
		}
		super.onDestroy();
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
