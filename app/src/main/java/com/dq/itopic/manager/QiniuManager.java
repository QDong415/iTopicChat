package com.dq.itopic.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.dq.itopic.bean.HashMapResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;

import java.util.HashMap;

import okhttp3.Response;

// 七牛云存储相关
public class QiniuManager {

	/**
	 * 采用单例模式使用QiniuManager
	 */
	private static QiniuManager instanceQiniuManager;

	/**
	 * 获取实例
	 */
	public static QiniuManager getInstance() {
		if (instanceQiniuManager == null) {
			synchronized (QiniuManager.class) {
				if (instanceQiniuManager == null) {
					instanceQiniuManager = new QiniuManager();
				}
			}
		}
		return instanceQiniuManager;
	}

	public void requireQiniuToken(ITopicApplication mContext, final String api, HashMap<String, String> params ,Object tag , final RequireQiniuTokenListener callback){

		final boolean photoQiniuToken = "qiniu/uploadtoken".equals(api);
		final SharedPreferences pref = mContext.getSharedPreferences("QINIU",Activity.MODE_PRIVATE);
		if (photoQiniuToken) {
			//说明是上传图片的七牛token，可以用缓存；否则是视频的七牛token，视频的不用缓存
			int expires = pref.getInt(api+"_expires", 0);
			if (expires > (System.currentTimeMillis()/1000) + 600) {
				String token = pref.getString(api+"_token", null);
				if (!TextUtils.isEmpty(token)){
					callback.success(token);
					return;
				}
			}
		}

		OkHttpHelper.getInstance().get(ServiceConstants.IP + api, params, tag , new CompleteCallback<HashMapResponse>(HashMapResponse.class,mContext) {

			@Override
			public void onComplete(Response okhttpResponse, HashMapResponse t) {
				if (t.isSuccess()){
					if (photoQiniuToken){
						SharedPreferences.Editor editor = pref.edit();
						editor.putInt(api+"_expires", Integer.parseInt(t.getData().get("expires")));
						editor.putString(api+"_token", t.getData().get("token"));
						editor.commit();
					}
					callback.success(t.getData().get("token"));
				}else{
					callback.fail(t.getCode(),t.getMessage());
				}
			}
		});
	}

	public interface RequireQiniuTokenListener{
		public void success(String qiniuToken);
		public void fail(int code,String message);
	}
}
