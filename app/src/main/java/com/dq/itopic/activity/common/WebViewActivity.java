package com.dq.itopic.activity.common;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dq.itopic.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewActivity extends BaseActivity {

	private static final int UNSUPPORT_PROTOCOL_ERROR = -10;
	private ProgressBar progressBar;
	private WebView mWebView = null;
	private String  url;
	private String title = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_web);
	
	
		url = this.getIntent().getStringExtra("url");

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		initWebView();
		initView();

		initProgressDialog();
		mWebView.loadUrl(url);
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mWebView.canGoBack()){
					mWebView.goBack();
				}else{
					finish();
				}
			}
		});
		findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mWebView.canGoBack()){
			mWebView.goBack();
		}else{
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 初始化WebView
	 * 
	 * @return
	 */
	private WebView initWebView() {
		mWebView = (WebView) this.findViewById(R.id.webview);
		mWebView.requestFocus();
		// mWebView.setInitialScale(1);
		// mWebView.getSettings().setLoadWithOverviewMode(true);
		// mWebView.getSettings().setUseWideViewPort(false);

		mWebView.setWebViewClient(mWebViewClient);
		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);

		// 设置可以支持缩放
		mWebView.getSettings().setSupportZoom(false);
		// 设置出现缩放工具
		mWebView.getSettings().setBuiltInZoomControls(false);
		//设置默认缩放方式尺寸是far   
		mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);  
		
		mWebView.getSettings().setLoadWithOverviewMode(true);

		mWebView.getSettings().setUseWideViewPort(false);
		return mWebView;
	}

	private WebChromeClient mWebChromeClient = new WebChromeClient() {
		
		@Override
		public void onProgressChanged(WebView view, int progress) {
			// Activity和Webview根据加载程度决定进度条的进度大小
			progressBar.setProgress(progress);
			super.onProgressChanged(view, progress);
		}
		
		 @Override  
         public void onReceivedTitle(WebView view, String title) {  
             super.onReceivedTitle(view, title);  
             WebViewActivity.this.title = title;
             setTitleName("" +title);  
         }
	};
	
	private WebViewClient mWebViewClient = new WebViewClient() {

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			// 加载完成时，打开ProgresDialog
			progressBar.setVisibility(View.INVISIBLE);
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			progressBar.setVisibility(View.VISIBLE);
			WebViewActivity.this.url = url;
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			// 拨打电话error不处理
			if (errorCode != UNSUPPORT_PROTOCOL_ERROR) {
				showToast(description);
				finish();
			}
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("tel:") || url.startsWith("mailto:")
					|| url.startsWith("geo:")) {
				// 调用拨号、邮件、地图程序
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			} else if (url.indexOf(".3gp") != -1 || url.indexOf(".mp4") != -1
					|| url.indexOf(".flv") != -1) {
				Intent intent = new Intent("android.intent.action.VIEW",
						Uri.parse(url));
				view.getContext().startActivity(intent);

			} else {
				// 当有新连接时，使用当前的 WebView
				view.loadUrl(url);
			}
			return true;
		}
	};

	private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWebView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWebView.onResume();
	}

	/**
	 * 获得apk版本号
	 */
	public static String getVersionName(Context context) {
		try {
			final String PackageName = context.getPackageName();
			return context.getPackageManager().getPackageInfo(PackageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "未知";
	}

	
	
}
