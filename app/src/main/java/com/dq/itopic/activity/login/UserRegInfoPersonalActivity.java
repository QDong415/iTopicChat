package com.dq.itopic.activity.login;

import java.util.HashMap;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dq.itopic.activity.common.BasePhotoActivity;
import com.dq.itopic.activity.common.MainActivity;
import com.dq.itopic.activity.common.WebViewActivity;
import com.dq.itopic.R;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.bean.UserResponse;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.manager.MyUserBeanManager;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.MD5Tool;
import com.dq.itopic.tools.NetWorkCallback;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.tools.statusbar.StatusBarUtils;
import com.dq.itopic.views.ShakeAnimation;
import com.dq.itopic.views.popup.QMUIListPopup;
import com.dq.itopic.views.popup.QMUIPopup;

import org.json.JSONObject;

public class UserRegInfoPersonalActivity extends BasePhotoActivity{

	private ImageView user_icon;
	private String currentPhotoUrl,openid,qqid;
	private EditText user_name_et;
	private String mobile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_reg_info_personal);
		initView();
		initListener();
		checkIfAutoReg();
	}

	private void checkIfAutoReg() {
		// TODO Auto-generated method stub
		if(getIntent().getBooleanExtra("auto", false)){
			String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
			Random random = new Random();
			StringBuffer sb = new StringBuffer();

			for(int i = 0 ; i < 5; ++i){
				int number = random.nextInt(62);//[0,62)
				sb.append(str.charAt(number));
			}
			user_name_et.setText(sb.toString());
			progress.show();
			requireToRegister();
		}
	}

	@Override
	protected void initStatusBar() {
		StatusBarUtils.from(this)
				.setTransparentStatusbar(true)
				.setStatusBarColor(getResources().getColor(R.color.white))
				.setLightStatusBar(true)
				.process(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		mobile = getIntent().getStringExtra("mobile");
		user_name_et = (EditText) findViewById(R.id.name_et);
		user_icon = (ImageView) findViewById(R.id.user_head);

		if (getIntent().getStringExtra("avatar") != null){
			currentPhotoUrl = getIntent().getStringExtra("avatar");
			GlideLoaderUtil.loadImage(this,currentPhotoUrl,R.drawable.user_photo_circle,user_icon);

			OkHttpHelper.getInstance().downLoadImage(UserRegInfoPersonalActivity.this, currentPhotoUrl,
					new NetWorkCallback<Bitmap>(Bitmap.class,getITopicApplication()) {
						@Override
						public void onSuccess(Response okhttpResponse, Bitmap bitmap) {
							String fileName = "";  //fileName 是最终存到sd卡里的文件名
							try {
								fileName = MD5Tool.getMD5(currentPhotoUrl);
								upLoadPicture(getFilesDir().getAbsolutePath()+"/"+fileName);
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}

						@Override
						public void onFailure(Request okhttpRequest, Exception e) {}
					});
		}

		if (getIntent().getStringExtra("name") != null){
			user_name_et.setText(getIntent().getStringExtra("name"));
		}

		if (getIntent().getStringExtra("openid") != null) {
			openid = getIntent().getStringExtra("openid");
		}
		if (getIntent().getStringExtra("qqid") != null) {
			qqid = getIntent().getStringExtra("qqid");
		}
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();

		Button reg_btn = (Button) findViewById(R.id.reg_btn);
		reg_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String name = user_name_et.getText().toString().trim();
				if (name.equals("")) {
					ShakeAnimation.create().with(user_name_et).start();
					return;
				}

				if (name.contains("@")||name.contains(" ")||name.contains("#")) {
					ShakeAnimation.create().with(user_name_et).start();
					showToast("昵称不能包含特殊字符");
					return;
				}

				hideKeyboard(user_name_et);

				progress.show();
				requireToRegister();
			}
		});

		user_icon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				hideKeyboard();
				startPictureSelect(user_icon);
			}
		});

		findViewById(R.id.agreement_tv).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(UserRegInfoPersonalActivity.this, WebViewActivity.class);
						i.putExtra("url", ServiceConstants.IP_NOAPI+"web/agreement");
						startActivity(i);
					}
				});

		findViewById(R.id.scrollview).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideKeyboard(getWindow().getDecorView());
				return false;
			}
		});

	}

	private void requireToRegister(){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", mobile);
		params.put("code", getIntent().getStringExtra("code"));
		params.put("name", user_name_et.getText().toString().trim());
		params.put("avatar", currentPhotoUrl);
		params.put("password", getIntent().getStringExtra("password"));
		if (openid != null){
			params.put("unionid", openid);
		}
		if (qqid != null){
			params.put("qqid", qqid);
		}
		OkHttpHelper.getInstance().post(ServiceConstants.IP + "account/register", params, new CompleteCallback<UserResponse>(UserResponse.class,getITopicApplication()) {

			@Override
			public void onComplete(Response okhttpResponse,final UserResponse response) {
				// TODO Auto-generated method stub
				if (response.isSuccess()) {

					UserBean userBean = response.getData();
					ITopicApplication mApp = getITopicApplication();
					// 保存用户信息并开启推送
					mApp.getMyUserBeanManager().storeUserInfoAndNotity(userBean);

					progress.dismiss();
					// 进入主页面
					Intent i = new Intent(UserRegInfoPersonalActivity.this,MainActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					i.putExtra("regsoon",true);
					startActivity(i);
					finish();

				} else {
					progress.dismiss();
					showToast(response.getMessage());
				}
			}
		});
	}

	@Override
	public void onPhotoSelectSuccess(String picturePath, ImageView currentImageView) {
		GlideLoaderUtil.loadImage(this,picturePath,R.drawable.user_photo_circle,user_icon);
	}

	@Override
	public void onPhotoUploadSuccess(String qiniuFileName, JSONObject qiniuJSONObject, ImageView currentImageView) {
		currentPhotoUrl = qiniuFileName;
		clearCache();
	}

	@Override
	public void onPhotoUploadFail(ImageView currentImageView) {
		currentImageView.setImageResource(R.drawable.user_photo_circle);
		currentPhotoUrl = null;
	}

}
