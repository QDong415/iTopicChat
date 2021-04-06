package com.dq.itopic.tools;


import com.dq.itopic.manager.ITopicApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络访问的回调，外加了发出请求时候的sig签名 + url拼接 + json解析
 * @param <T>
 */
public abstract class NetWorkCallback<T> {

	protected Class<T> clazz;

	protected ITopicApplication mApplication;

	public NetWorkCallback(Class<T> clazz,ITopicApplication mApplication) {
		this.clazz = clazz;
		this.mApplication = mApplication;
	}

    /**
     * 请求成功 且 解析出bean
     */
    public abstract void onSuccess(Response okhttpResponse, T t);
	
    /**
     * 请求失败调用（网络问题）
     */
    public abstract void onFailure(Request okhttpRequest, Exception e);
    
    /**
     * 解析json，可以在这里更换别的解析方式（比如Gson）
     * @param json 服务器返回的json
     * @return
     */
    protected T parseNetworkResponse(String json){
    	T data = (T) JsonUtil.getObject(json, clazz);
    	return data;
    }

	//get+post都会调用
	protected Map<String, String> configParams(Map<String, String> orgdict){
		//拼接上uid etag ts。但是没sign
		Map<String, String> parametersMutableDictionary = new HashMap<String, String>();
		parametersMutableDictionary.putAll(orgdict);

		//登录后调用的接口该参数为必选参数
		parametersMutableDictionary.put("userid",mApplication.getMyUserBeanManager().getUserId());
		parametersMutableDictionary.put("ts",""+(int)(System.currentTimeMillis()/1000));
		parametersMutableDictionary.put("os",Rom.getName());
		parametersMutableDictionary.put("clientver", mApplication.getOtherManage().getVersionName());
		parametersMutableDictionary.put("deviceid",mApplication.getOtherManage().getDeviceId());
		parametersMutableDictionary.put("sig", ServiceConstants.SIG_KEY);

		//参数添加完毕，加入签名
		String signString = dictionaryToString(parametersMutableDictionary, false);
		try {
			parametersMutableDictionary.put("sig",MD5Tool.getMD5(signString));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return parametersMutableDictionary;
	}

	/**
	 * 对dic进行排序并拼接成string
	 * @param map 原始map
	 * @param needEncoder 是否需要对value进行UrlEncoder
	 * @return
	 */
	protected String dictionaryToString(Map<String, String> map, boolean needEncoder) {

		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
				map.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
							   Map.Entry<String, String> o2) {
				// return (o2.getValue() - o1.getValue());
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < infoIds.size(); i++) {
			String key = infoIds.get(i).getKey();
			if (i != 0) {
				buffer.append("&");
			}
			buffer.append(key);
			buffer.append("=");
			String value = infoIds.get(i).getValue();
			value = value == null ? "" : value;
			try {
				buffer.append(needEncoder ? URLEncoder.encode(value, "utf-8"):value);
			} catch (UnsupportedEncodingException e) {
				buffer.append(value);
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}
}
