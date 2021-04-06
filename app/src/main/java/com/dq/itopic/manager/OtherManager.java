package com.dq.itopic.manager;

import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.deviceid.DeviceUuidFactory;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.umverify.UMResultCode;
import com.umeng.umverify.UMVerifyHelper;
import com.umeng.umverify.listener.UMPreLoginResultListener;
import com.umeng.umverify.listener.UMTokenResultListener;
import com.umeng.umverify.model.UMTokenRet;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class OtherManager {

	private ITopicApplication mContext;

	public OtherManager(ITopicApplication mContext) {
		this.mContext = mContext;
	}

	//当前设备id
	private String deviceId;

	//下面两个是友盟一键登录
	private boolean umVerifyAvailable;
	private UMVerifyHelper mPhoneNumberAuthHelper;

	public void initOther() {

		//友盟统计
		UMConfigure.init(mContext, ServiceConstants.UM_APPKEY,"itopic",UMConfigure.DEVICE_TYPE_PHONE,null);

		//获取设备唯一id
		DeviceUuidFactory uuidFactory = new DeviceUuidFactory();
		deviceId = String.valueOf(uuidFactory.getUuid(mContext));

		if (!mContext.getMyUserBeanManager().isLogin()) {
			//我当前没登录，初始化一键登录相关的组件
			UMTokenResultListener mCheckListener = new UMTokenResultListener() {
				@Override
				public void onTokenSuccess(String s) {
					try {
						UMTokenRet pTokenRet = UMTokenRet.fromJson(s);
						if (UMResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(pTokenRet.getCode())) {
							mPhoneNumberAuthHelper.accelerateLoginPage(5000, new UMPreLoginResultListener() {
								@Override
								public void onTokenSuccess(String s) {
									//这是里主线程 ，s == cmcc
									umVerifyAvailable = true;
								}
								@Override
								public void onTokenFailed(String s, String s1) {
								}
							});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onTokenFailed(String s) {
					umVerifyAvailable = false;
					//终端环境检查失败之后 跳转到其他号码校验方式
				}
			};
			mPhoneNumberAuthHelper = UMVerifyHelper.getInstance(mContext, mCheckListener);
			mPhoneNumberAuthHelper.setAuthSDKInfo(ServiceConstants.UM_VERIFY);
			mPhoneNumberAuthHelper.setLoggerEnable(false);
			mPhoneNumberAuthHelper.checkEnvAvailable(UMVerifyHelper.SERVICE_TYPE_LOGIN);
		}

	}

	public void release(){
		if (mPhoneNumberAuthHelper != null){
			mPhoneNumberAuthHelper.setAuthListener(null);
			mPhoneNumberAuthHelper.setUIClickListener(null);
			mPhoneNumberAuthHelper.removeAuthRegisterViewConfig();
			mPhoneNumberAuthHelper.removeAuthRegisterXmlConfig();
		}
	}

	// 获取当前版本号
	public String getVersionName() {
		PackageManager packageManager = mContext.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					mContext.getPackageName(), 0);

			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}

	public String getDeviceId() {
		return deviceId;
	}

	public boolean isUmVerifyAvailable() {
		return umVerifyAvailable;
	}

	public UMVerifyHelper getPhoneNumberAuthHelper(){
		return mPhoneNumberAuthHelper;
	}
}
