package com.dq.itopic.activity.common;

import com.alibaba.fastjson.JSON;
import com.dq.itopic.R;
import com.dq.itopic.activity.login.UserRegInfoPersonalActivity;
import com.dq.itopic.activity.login.UserRegMobileActivity;
import com.dq.itopic.activity.user.PersonalHomeActivity;
import com.dq.itopic.bean.HashMapResponse;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.layout.LoadingDialog;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.manager.OtherManager;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.LogUtil;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.statusbar.StatusBarUtils;
import com.umeng.umverify.UMResultCode;
import com.umeng.umverify.UMVerifyHelper;
import com.umeng.umverify.listener.UMAuthUIControlClickListener;
import com.umeng.umverify.listener.UMTokenResultListener;
import com.umeng.umverify.model.UMTokenRet;
import com.umeng.umverify.view.UMAuthRegisterViewConfig;
import com.umeng.umverify.view.UMAuthUIConfig;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Response;

/**
 * 除了每次打开的欢迎页面之外 的所有activity都需要继承本类。
 * 功能：
 * 1、强行在onCreate里先验证是否初始化成功：调用checkInit()。若没有，先在本类里初始化，才能往下走子类的onCreate
 * 2、强行在onResume 和 onPause里插入友盟的统计API
 * 3、提供公共方法checkLogin，检查用户是否登录
 * 4、提供公共方法initProgressDialog，需要弹出加载中弹框的activity子类，需要先在oncreate里调用initProgressDialog
 * 5、提供获取当前登录用户的id方法getUserID，若没登录，返回""
 * 6、提供其他方法，比如跳转都聊天界面，跳转到个人主页，隐藏弹出键盘
 */
public class BaseActivity extends AppCompatActivity {

	public LoadingDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkInit();
		initStatusBar();
	}

	protected void initStatusBar() {
		StatusBarUtils.from(this)
				.setTransparentStatusbar(true)
				.setStatusBarColor(getResources().getColor(R.color.navigation_color))
				.setLightStatusBar(!isDarkMode())
				.process(this);
	}

	public boolean isDarkMode() {
		int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		return mode == Configuration.UI_MODE_NIGHT_YES;
	}

	/**
	 * 如果登录了，返回true，否则返回false并去登录
	 * @return
	 */
	public boolean checkLogined(){
		if (TextUtils.isEmpty(getUserID())) {
			OtherManager otherManager = getITopicApplication().getOtherManage();
			if (otherManager.isUmVerifyAvailable()){
				UMVerifyHelper mPhoneNumberAuthHelper = otherManager.getPhoneNumberAuthHelper();
				mPhoneNumberAuthHelper.addAuthRegistViewConfig("switch_acc_tv", new UMAuthRegisterViewConfig.Builder()
						.setRootViewId(UMAuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
						.build());

				mPhoneNumberAuthHelper.setUIClickListener(new UMAuthUIControlClickListener() {
					@Override
					public void onClick(String code, Context context, String jsonString) {
						switch (code) {
							//点击授权页默认样式的返回按钮
							case UMResultCode.CODE_ERROR_USER_CANCEL:
								mPhoneNumberAuthHelper.quitLoginPage();
								break;
							//点击授权页默认样式的切换其他登录方式 会关闭授权页
							//如果不希望关闭授权页那就setSwitchAccHidden(true)隐藏默认的  通过自定义view添加自己的
							case UMResultCode.CODE_ERROR_USER_SWITCH:
								LogUtil.e( "点击了授权页默认切换其他登录方式");
								break;
							//点击一键登录按钮会发出此回调
							//当协议栏没有勾选时 点击按钮会有默认toast 如果不需要或者希望自定义内容 setLogBtnToastHidden(true)隐藏默认Toast
							//通过此回调自己设置toast
							case UMResultCode.CODE_ERROR_USER_LOGIN_BTN:
//								try {
//									JSONObject jsonObj = new JSONObject(jsonString);
//									if (!jsonObj.getBoolean("isChecked")) {
//									}
//								} catch (JSONException e) {}
								break;
							//checkbox状态改变触发此回调
							case UMResultCode.CODE_ERROR_USER_CHECKBOX:
								break;
							//点击协议栏触发此回调
							case UMResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL:
//								LogUtil.e("点击协议，" + "name: " + jsonObj.getString("name") + ", url: " + jsonObj.getString("url"));
								break;
							default:
								break;
						}
					}
				});
				mPhoneNumberAuthHelper.removeAuthRegisterXmlConfig();
				mPhoneNumberAuthHelper.removeAuthRegisterViewConfig();
				int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
				if (Build.VERSION.SDK_INT == 26) {
					authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
				}
				mPhoneNumberAuthHelper.setAuthUIConfig(new UMAuthUIConfig.Builder()
						.setAppPrivacyOne("《用户服务协议》", ServiceConstants.IP_NOAPI+"home/article/agreement?agreementid=1")
						.setAppPrivacyTwo("《用户隐私协议》", ServiceConstants.IP_NOAPI+"home/article/agreement?agreementid=2")
						.setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
						//隐藏默认切换其他登录方式
						.setSwitchAccHidden(false)
						.setSwitchAccText("使用手机验证码登录")
						.setCheckboxHidden(true)
						//隐藏默认Toast
						.setLogBtnToastHidden(false)
						//沉浸式状态栏
						.setStatusBarColor(Color.TRANSPARENT)
//						.setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
						.setLightColor(true)
						.setWebNavTextSize(20)
						.setNavHidden(true)
						//图片或者xml的传参方式为不包含后缀名的全称 需要文件需要放在drawable或drawable-xxx目录下 in_activity.xml, mytel_app_launcher.png
//						.setAuthPageActIn("slide_in_from_bottom", "slide_out_to_bottom")
//						.setAuthPageActOut("slide_in_from_bottom", "slide_out_to_bottom")
						.setVendorPrivacyPrefix("《")
						.setVendorPrivacySuffix("》")
						.setPageBackgroundPath("white_background")
						.setLogoImgPath("welcome_logo")
						//一键登录按钮三种状态背景示例login_btn_bg.xml
						.setLogBtnBackgroundPath("blue_round_background")
						.setScreenOrientation(authPageOrientation)
						.create());

				UMTokenResultListener mTokenResultListener = new UMTokenResultListener() {
					@Override
					public void onTokenSuccess(String s) {
//						hideLoadingDialog();
						UMTokenRet tokenRet = null;
						try {
							tokenRet = UMTokenRet.fromJson(s);
							if (UMResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
							} else if (UMResultCode.CODE_GET_TOKEN_SUCCESS.equals(tokenRet.getCode())) {
								otherManager.release();

								initProgressDialog();
								progress.show();
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("token", tokenRet.getToken());
								params.put("verifyId", mPhoneNumberAuthHelper.getVerifyId(BaseActivity.this));

								OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/ummobile", params
										, new CompleteCallback<HashMapResponse>(HashMapResponse.class,getITopicApplication()) {

									@Override
									public void onComplete(Response okhttpResponse, HashMapResponse response) {
										progress.dismiss();
										if (response.isSuccess()) {
											UserBean instanceUser = JsonUtil.getObject(JSON.toJSONString(response.getData()),
													UserBean.class);

											// 保存用户信息并开启推送
											getITopicApplication().getMyUserBeanManager().storeUserInfoAndNotity(instanceUser);

											// 进入主页面
											Intent i = new Intent(BaseActivity.this,MainActivity.class);
											i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
											startActivity(i);
											mPhoneNumberAuthHelper.quitLoginPage();

										} else if (response.getCode() == 2) {

											HashMap<String,String> data = response.getData();
											Intent i = new Intent(BaseActivity.this, UserRegInfoPersonalActivity.class);
											i.putExtra("mobile",  data.get("mobile"));
											i.putExtra("code", data.get("code"));
											startActivity(i);
											mPhoneNumberAuthHelper.quitLoginPage();

										} else {
											//服务器异常
											toRegMobileActivity();
											mPhoneNumberAuthHelper.quitLoginPage();
										}
									}
								});
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onTokenFailed(String s) {
//						Log.e(TAG, "获取token失败：" + s);
//						hideLoadingDialog();
						//如果环境检查失败 使用其他登录方式
						try {
							UMTokenRet tokenRet = UMTokenRet.fromJson(s);
							if (!UMResultCode.CODE_ERROR_USER_CANCEL.equals(tokenRet.getCode())) {
								//经过测试，点"切换到其他方式" 也会进入这里
								toRegMobileActivity();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						mPhoneNumberAuthHelper.quitLoginPage();
						otherManager.release();
					}
				};
				mPhoneNumberAuthHelper.setAuthListener(mTokenResultListener);
				mPhoneNumberAuthHelper.getLoginToken(this, 3000);
//				showLoadingDialog("正在唤起授权页");

			} else {
				toRegMobileActivity();
			}
			return false;
		} else {
			return true;
		}
	}

	private void toRegMobileActivity(){
		LogUtil.e("toRegMobileActivity");
		Intent i = new Intent(this, UserRegMobileActivity.class);
		startActivity(i);
	}

	private void checkInit() {
		ITopicApplication app = getITopicApplication();
		app.checkInit();
	}

	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}

	public void initProgressDialog() {
		initProgressDialog(true, null);
	}

	public void initProgressDialog(boolean cancel, String message) {
		initProgressDialog(this, cancel, message);
	}

	public void initProgressDialog(Context mContext, boolean cancel,
			String message) {
		progress = new LoadingDialog(mContext, cancel);
	}

	public void showToast() {
		showToast("无法连接到网络\n请稍后再试");
	}

	public void showToast(String err) {
		if (!isDestroyed() && !isFinishing())
			Toast.makeText(BaseActivity.this, err, Toast.LENGTH_SHORT).show();
	}

	public void showToastByApplication(String err) {
		Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
	}


	public void backButtonListener() {
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideKeyboard();
				finish();
			}
		});
	}

	public ITopicApplication getITopicApplication() {
		return (ITopicApplication) getApplication();
	}

	public void jumpToHisInfoActivity( String UserID,
			String realName,String headImageBean) {
		 Intent i = new Intent(this, PersonalHomeActivity.class);
		i.putExtra(PersonalHomeActivity.HIS_AVATAR_KEY, headImageBean);
		i.putExtra(PersonalHomeActivity.HIS_ID_KEY, UserID);
		if (realName != null && realName.startsWith("@")){
			realName = realName.substring(1);
		}
		i.putExtra(PersonalHomeActivity.HIS_NAME_KEY, realName);
		startActivity(i);
	}

	public void jumpToChatActivity(final String hisUserID,
			final String hisRealName,final String headImageBean,final int chatType) {
		if(!checkLogined()){
			return;
		}
	}
	
	private void intentToChatActivity(String hisUserID,
			String hisRealName,String headImageBean,int chatType){
	}
	
	public void setTitleName(String titleName) {
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(titleName);
	}


	public void hideKeyboard(View v) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(v.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void hideKeyboard() {
		hideKeyboard(getWindow().getDecorView());
	}
	
	public void showKeyboard(EditText et) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public String getUserID() {
		UserBean instanceUser = getITopicApplication()
				.getMyUserBeanManager().getInstance();
		return instanceUser == null ? "" : instanceUser.getUserid();
	}
	
}
