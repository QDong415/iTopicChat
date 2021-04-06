package com.dq.itopic.manager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Response;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.dq.itopic.bean.StringResponse;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.OkHttpHelper;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeManager {
	
	private ITopicApplication mApp;

	public HomeManager(ITopicApplication mApp) {
		this.mApp = mApp;
	}

	//上传图片文件
	public void uploadFile(final String sandboxFileName,final LocalMedia localMedia
			,final UpCompletionHandler upCompletionHandler ,final UploadOptions opinion) {

		//用户选择的是图片
		QiniuManager.getInstance().requireQiniuToken(mApp,"qiniu/uploadtoken",new HashMap<String, String>(),this, new QiniuManager.RequireQiniuTokenListener() {

			@Override
			public void success(String qiniuToken) {

				Configuration qiniuConfig = new Configuration.Builder().build();
				final UploadManager uploadManager = new UploadManager(qiniuConfig);

				//gif图，上传原始图片
				uploadManager.put(PictureMimeType.isGif(localMedia.getMimeType())?(TextUtils.isEmpty(localMedia.getAndroidQToPath())?localMedia.getPath():localMedia.getAndroidQToPath()):localMedia.getCompressPath()
						, sandboxFileName, qiniuToken, upCompletionHandler, opinion);
			}

			@Override
			public void fail(int code, String message) {
				//七牛token获取失败
				upCompletionHandler.complete(sandboxFileName,null,null);
			}
		});
	}

	public void uploadFile(final String sandboxFileName,final String sandboxFilePath
			,final UpCompletionHandler upCompletionHandler ,final UploadOptions opinion) {

		//用户选择的是图片
		QiniuManager.getInstance().requireQiniuToken(mApp,"qiniu/uploadtoken",new HashMap<String, String>(),this, new QiniuManager.RequireQiniuTokenListener() {

			@Override
			public void success(String qiniuToken) {

				Configuration qiniuConfig = new Configuration.Builder().build();
				final UploadManager uploadManager = new UploadManager(qiniuConfig);

				uploadManager.put(sandboxFilePath , sandboxFileName, qiniuToken, upCompletionHandler, opinion);
			}

			@Override
			public void fail(int code, String message) {
				//七牛token获取失败
				upCompletionHandler.complete(sandboxFileName,null,null);
			}
		});
	}

}
