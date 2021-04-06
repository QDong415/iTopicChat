package com.dq.itopic.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 这个类用来辅助OKHttp
 */
public class OkHttpHelper {

    /**
     * 采用单例模式使用OkHttpClient
     */
    private static OkHttpHelper mOkHttpHelperInstance;
    private static OkHttpClient mClientInstance;
    private Handler mHandler;

    /**
     * 单例模式，私有构造函数，构造函数里面进行一些初始化
     */
    private OkHttpHelper() {
    	OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

    	okHttpClientBuilder.connectTimeout(50, TimeUnit.SECONDS);
    	okHttpClientBuilder.readTimeout(50, TimeUnit.SECONDS);
    	okHttpClientBuilder.writeTimeout(60, TimeUnit.SECONDS);

    	mClientInstance = okHttpClientBuilder.build();
    	
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 获取实例
     *
     * @return
     */
    public static OkHttpHelper getInstance() {

        if (mOkHttpHelperInstance == null) {

            synchronized (OkHttpHelper.class) {
                if (mOkHttpHelperInstance == null) {
                    mOkHttpHelperInstance = new OkHttpHelper();
                }
            }
        }
        return mOkHttpHelperInstance;
    }

    /**
     * 封装一个request方法，不管post或者get方法中都会用到
     */
    private <T> void request(final Request request, final NetWorkCallback<T> callback) {
    	
        mClientInstance.newCall(request).enqueue(new Callback() {

			@Override
			public void onFailure(Call call, IOException e) {
				// TODO Auto-generated method stub
                // 网络访问失败，但是超时并不会进入这里
				if (!call.isCanceled()) {
					//如果是主动调用了cancel，就不执行回调
	                callbackFailure(request, callback, e);
				}
			}

			@Override
			public void onResponse(Call call, Response response){
				// TODO Auto-generated method stub
				//返回成功(或者网络访问超时)回调
				T t = null;
				try {
					String resString = response.body().string();
					t = callback.parseNetworkResponse(resString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
               
				if (t != null) {
					//网络访问成功 + 解析json成功
					callbackSuccess(response, t, callback);
				} else {
					//一般为服务器响应成功，但是数据解析错误，这里当做网络访问失败一样的处理
					callbackFailure(request, callback,new IOException("数据解析失败"));
				}
			}
        });
    }

    /**
     * 在主线程中执行的回调
     *
     * @param response
     * @param callback
     */
    private <T> void callbackSuccess(final Response response, final T t, final NetWorkCallback<T> callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, t);
            }
        });
    }

    /**
     * 在主线程中执行的回调
     * @param request
     * @param callback
     * @param e
     */
    private <T> void callbackFailure(final Request request, final NetWorkCallback<T> callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    /**
     * 对外公开的get方法
     */
    public <T> void get(String url,Map<String, String> params, NetWorkCallback<T> callback) {
        get(url, params, null, callback);
    }
    
    /**
     * 对外公开的get方法
     * @param url 如：http://xxxx/api/topic/getlist
     * @param params 如：page:1  userid:1
     * @param tag 用来取消用的tag
     * @param callback 回调，可以是NetWorkCallback 也可以是 CompleteCallback
     */
    public <T> void get(String url,Map<String, String> params,Object tag, NetWorkCallback<T> callback) {
        Request request = buildRequest(url + "?" + callback.dictionaryToString(callback.configParams(params),true) , null , tag , HttpMethodType.GET);
        request(request, callback);
    }

    /**
     * 对外公开的post方法
     */
    public <T> void post(String url, Map<String, String> params, NetWorkCallback<T> callback) {
    	post(url, params, null, callback);
    }
    
    /**
     * 对外公开的post方法
     * @param url 如：http://xxxx/api/topic/getlist
     * @param params 如：page:1  userid:1
     * @param tag 用来取消用的tag
     * @param callback 回调，可以是NetWorkCallback 也可以是 CompleteCallback
     */
    public <T> void post(String url, Map<String, String> params,Object tag, NetWorkCallback<T> callback) {
        Request request = buildRequest(url, callback.configParams(params) , tag , HttpMethodType.POST);
        request(request, callback);
    }
    /**
     * 构建请求对象
     *
     * @param url
     * @param params
     * @param tag
     * @param type
     * @return
     */
    private Request buildRequest(String url, Map<String, String> params,Object tag, HttpMethodType type) {
//    	LogUtil.e(type+"  "+url);
//        LogUtil.e("params  = "+params);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (type == HttpMethodType.GET) {
            builder.get();
        } else if (type == HttpMethodType.POST) {
            builder.post(buildRequestBody(params));
        }
        builder.tag(tag);
        return builder.build();
    }

    /**
     * 通过Map的键值对构建请求对象的body
     *
     * @param params
     * @return
     */
    private RequestBody buildRequestBody(Map<String, String> params) {
    	FormBody.Builder bodyBuilder = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, String> entity : params.entrySet()) {
            	bodyBuilder.add(entity.getKey(), entity.getValue() == null?"":entity.getValue());
            }
        }
        return bodyBuilder.build();
    }

    /** 根据Tag取消请求；调用后，有可能并不会立刻返回失败，而是任然访问网络之后回调Failure */
    public void cancelTag(Object tag) {
        for (Call call : mClientInstance.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mClientInstance.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 下载文件
     * @param fileUrl 文件下载地址，http开头
     */
    public <T> void downLoadImage(final Context context, final String fileUrl, final NetWorkCallback<T> callBack) {
        String fileName = "";  //fileName 是最终存到sd卡里的文件名
        try {
            fileName = MD5Tool.getMD5(fileUrl);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }
        try {
            FileInputStream fis = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            if (callBack!=null)
                callbackSuccess(null,(T) bitmap, callBack);
            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = mClientInstance.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack!=null)
                    callbackFailure(request, callBack, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = context.openFileOutput( MD5Tool.getMD5(fileUrl),context.MODE_PRIVATE);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    if (callBack!=null){
                        callbackSuccess(response, (T) null, callBack);
                    }

                } catch (Exception e) {
                    if (callBack!=null)
                        callbackFailure(request, callBack, e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }


    /**
     * 下载文件
     */
    public <T> void downLoadFile(final Context context, final String fileName, final NetWorkCallback<T> callBack) {

        final Request request = new Request.Builder().url(ValueUtil.getQiniuUrlByFileName(fileName,false)).build();
        final Call call = mClientInstance.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack!=null)
                    callbackFailure(request, callBack, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = context.openFileOutput(fileName,context.MODE_PRIVATE);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    if (callBack!=null){
                        callbackSuccess(response, (T) null, callBack);
                    }

                } catch (Exception e) {
                    if (callBack!=null)
                        callbackFailure(request, callBack, e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * 这个枚举用于指明是哪一种提交方式
     */
    enum HttpMethodType {
        GET,
        POST
    }

}
