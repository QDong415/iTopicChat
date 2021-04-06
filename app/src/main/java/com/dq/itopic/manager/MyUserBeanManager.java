package com.dq.itopic.manager;

import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.Response;

import com.dq.itopic.tools.LogUtil;
import com.dq.itopic.tools.Rom;
import com.huawei.hms.aaid.HmsInstanceId;
import com.igexin.sdk.PushManager;
import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.bean.ModifyResponse;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.DBReq;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.JsonUtil;
import com.dq.itopic.tools.OkHttpHelper;
import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class MyUserBeanManager {

	private static final String USER_KEY = "current-user";

	private UserBean instanceUser;

	private ITopicApplication mContext;
	private ArrayList<UserStateChangeListener> onUserStateChangeListenerList;
	private EditInfoListener onEditListener;
	private String hwPushToken;
	private android.os.Handler mHandler = new android.os.Handler();

	public MyUserBeanManager(ITopicApplication mContext) {
		this.mContext = mContext;
		onUserStateChangeListenerList = new ArrayList<UserStateChangeListener>();
	}

	public UserBean getInstance() {
		return instanceUser;
	}

	public boolean isLogin() {
		return instanceUser != null && !TextUtils.isEmpty(instanceUser.getUserid());
	}

	public String getUserId() {
		return instanceUser == null ? "" : instanceUser.getUserid();
	}

	public String getMobile() {
		return instanceUser == null ? "" : instanceUser.getMobile();
	}

	/**
	 * 只运行初始化app调用
	 */
	public void checkUserInfo() {
		SharedPreferences pref = mContext.getSharedPreferences(USER_KEY,
				Activity.MODE_PRIVATE);
		instanceUser = JsonUtil.getObject(pref.getString("USERJSON", ""),
				UserBean.class);
	}

	/**
	 * 保存新的用户json并发出观察者通知
	 * 仅限于 刚登录or刚注册 之后才允许调用
	 */
	public void storeUserInfoAndNotity(UserBean ub) {
		//保存用户json到SharedPreferences，和全局变量
		storeUserInfo(ub);
		//发出登录广播
		notityUserLogin(ub);

		//打开推送SDK服务
		if (Rom.isEmui()){
			//华为手机，用华为push
			checkHWidAndUpdate(true);
		} else {
			//非华为，用个推
			//检查个推cid
			checkCidAndUpdate(true);

			//打开个推推送开关
			PushManager.getInstance().turnOnPush(mContext);
		}
	}

	/**
	 * 仅仅保存新的用户json
	 */
	public void storeUserInfo(UserBean ub) {
		instanceUser = ub;
		SharedPreferences pref = mContext.getSharedPreferences(USER_KEY,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("USERJSON", JsonUtil.getJson(ub));
		editor.commit();
	}

	//检查Cid是否跟本地缓存的一致
	//isLoginOrReg，刚刚是因为注册或登录成功，才调用的该接口
	public void checkCidAndUpdate(boolean isLoginOrReg) {
		String cid = PushManager.getInstance().getClientid(mContext);
		if(!TextUtils.isEmpty(cid) && instanceUser!=null && !cid.equals(instanceUser.getCid())){
			//有新的cid。通知服务器
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("cid",cid);
			startEditInfoRun(params, isLoginOrReg ? new EditInfoListener() {
				@Override
				public void onEditSuccess(HashMap<String,String> params) {
					loginOrRegSoon();
				}

				@Override
				public void onEditFail(String message) {}
			} : null);
		}
	}

	//检查HWid是否跟本地缓存的一致
	public void checkHWidAndUpdate(boolean isLoginOrReg) {
		if(!TextUtils.isEmpty(hwPushToken) && instanceUser!=null && !hwPushToken.equals(instanceUser.getHwid())){
			//有新的HWid。通知服务器
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("hwid", hwPushToken);
			startEditInfoRun(params, isLoginOrReg ? new EditInfoListener() {
				@Override
				public void onEditSuccess(HashMap<String,String> params) {
					loginOrRegSoon();
				}

				@Override
				public void onEditFail(String message) {}
			} : null);
		}
	}

	//告诉php服务器我刚才注册成功了，php服务器收到这个接口后，会推送小秘书默认消息
	private void loginOrRegSoon(){
		OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/doregaction", new HashMap<String,String>(),
				new CompleteCallback<BaseResponse>(BaseResponse.class,mContext) {
					@Override
					public void onComplete(Response okhttpResponse, BaseResponse response) {
						// TODO Auto-generated method stub
					}
				});
	}

	public boolean requireHWToken(final Activity activity){
		if (!Rom.isEmui()){
			return false;
		}
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				new Thread() {
					@Override
					public void run() {
						try {
							final String hwPushToken = HmsInstanceId.getInstance(activity).getToken(ServiceConstants.HUAWEI_APPID, "HCM");
							if (!TextUtils.isEmpty(hwPushToken)) {
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										MyUserBeanManager.this.hwPushToken = hwPushToken;
										checkHWidAndUpdate(false);
									}
								});
							}
						} catch (Exception e) {
							LogUtil.e("hwPushToken直接失败 Exception "+e.getMessage());
						}
					}
				}.start();
			}
		},350);

		return true;
	}

	//作用1：让服务器统计最后登录时间，2让服务器统计最后登录设备
	public void openAppToService() {
		if(instanceUser != null){
			HashMap<String, String> params = new HashMap<String, String>();
			OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/open", params,
					new CompleteCallback<BaseResponse>(BaseResponse.class,mContext) {
				@Override
				public void onComplete(Response okhttpResponse, BaseResponse response) {
				}
			});
		}
	}

	public void setHwPushToken(String hwPushToken){
		this.hwPushToken = hwPushToken;
		checkHWidAndUpdate(false);
	}

	/**
	 * 当前用户信息改变（性别，年龄，昵称 等）包括用户发的动态数，粉丝数，关注数
	 * @param ub
	 */
	public void notityUserInfoChanged(UserBean ub) {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserInfoChanged(ub);
		}
	}

	/**
	 * 当前用户登录 发出通知
	 * @param ub
	 */
	public void notityUserLogin(UserBean ub) {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserLogin(ub);
		}
	}

	/**
	 * 当前用户退出 发出通知
	 * 发出这个通知的时候，userBean == null
	 */
	public void notityUserLogout() {
		for (UserStateChangeListener onUserStateChangeListener : onUserStateChangeListenerList) {
			onUserStateChangeListener.onUserLogout();
		}
	}

	public void clean() {
		instanceUser = null;
		SharedPreferences pref = mContext.getSharedPreferences(USER_KEY,Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();

		mContext.getUnreadNoticeManager().resetUnreadCache();

		DBReq.getInstence(mContext).close();

		notityUserLogout();
	}

	/**
	 * 修改个人资料
	 */
	public void startEditInfoRun(final HashMap<String, String> params, final EditInfoListener onEditListener) {

		OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/modifyarray", params
				, new CompleteCallback<ModifyResponse>(ModifyResponse.class,mContext) {

			@Override
			public void onComplete(Response okhttpResponse, ModifyResponse response) {
				// TODO Auto-generated method stub
				if (response.isSuccess()) {
					//修改资料成功，重新修改userbean
					UserBean newUserBean = response.getData();
					instanceUser.setAge(newUserBean.getAge());
					instanceUser.setAvatar(newUserBean.getAvatar());
					instanceUser.setGender(newUserBean.getGender());
					instanceUser.setIntro(newUserBean.getIntro());
					instanceUser.setName(newUserBean.getName());
					instanceUser.setCityid(newUserBean.getCityid());
					instanceUser.setCityname(newUserBean.getCityname());
					instanceUser.setSlience(newUserBean.getSlience());
					instanceUser.setCid(newUserBean.getCid());
					instanceUser.setHwid(newUserBean.getHwid());
					//重新保存用户信息
					storeUserInfo(instanceUser);
					//用观察者模式通知全部监听用户资料的界面，告诉他们用户信息更改了
					notityUserInfoChanged(instanceUser);

					//回掉网络访问修改成功
					if (onEditListener != null) {
						onEditListener.onEditSuccess(params);
					}
				} else if (onEditListener != null) {
					onEditListener.onEditFail(response.getMessage());
				}
			}
		});
	}

	public void addOnUserStateChangeListener(
			UserStateChangeListener onUserStateChangeListener) {
		if (onUserStateChangeListener != null
				&& !onUserStateChangeListenerList
				.contains(onUserStateChangeListener)) {
			onUserStateChangeListenerList.add(onUserStateChangeListener);
		}
	}

	public void removeUserStateChangeListener(
			UserStateChangeListener onUserStateChangeListener) {
		if (onUserStateChangeListener != null
				&& onUserStateChangeListenerList
				.contains(onUserStateChangeListener)) {
			onUserStateChangeListenerList.remove(onUserStateChangeListener);
		}
	}

	public EditInfoListener getOnEditListener() {
		return onEditListener;
	}

	public void setOnEditListener(EditInfoListener onEditListener) {
		this.onEditListener = onEditListener;
	}

	//用户状态改变，观察者
	public interface UserStateChangeListener {
		//当前用户信息改变（性别，年龄，昵称 等）包括用户发的动态数，粉丝数，关注数
		public void onUserInfoChanged(UserBean ub);
		//用户输入密码登录（或者注册后自动登录）
		public void onUserLogin(UserBean ub);
		//用户登出
		public void onUserLogout();
	}

	//用户修改自己的信息，网络监听
	public interface EditInfoListener {
		public void onEditFail(String message);
		public void onEditSuccess(HashMap<String, String> params);
	}

}

