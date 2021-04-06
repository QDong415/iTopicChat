package com.dq.itopic.tools;

import com.dq.itopic.bean.BaseResponse;
import com.dq.itopic.manager.ITopicApplication;

import okhttp3.Request;
import okhttp3.Response;

public abstract class CompleteCallback<T extends BaseResponse> extends NetWorkCallback<T> {

	public CompleteCallback(Class<T> clazz, ITopicApplication mApplication) {
		super(clazz, mApplication);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onSuccess(Response okhttpResponse, T t) {
		// TODO Auto-generated method stub
		onComplete(okhttpResponse, t);
	}

	@Override
	public void onFailure(Request request, Exception e) {
		// TODO Auto-generated method stub
		try {
			T t = clazz.newInstance();
			t.setMessage("网络访问失败");
			onComplete(null, t);
			return ;
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
     * 请求完毕，无论成功或者失败（失败后new一个Response）
     *
     * @param okhttpResponse  OKhttp返回的的，可能为null
     * @param t
     */
    public abstract void onComplete(Response okhttpResponse, T t);
}
