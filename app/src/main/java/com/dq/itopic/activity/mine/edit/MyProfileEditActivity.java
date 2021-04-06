package com.dq.itopic.activity.mine.edit;

import com.dq.itopic.activity.common.BasePhotoActivity;
import com.dq.itopic.R;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.manager.MyUserBeanManager;
import com.dq.itopic.manager.MyUserBeanManager.EditInfoListener;
import com.dq.itopic.manager.MyUserBeanManager.UserStateChangeListener;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 我的 个人资料 界面
 * UserStateChangeListener，当前账号状态监听
 * EditInfoListener，修改我的某项资料的网络请求监听
 */
public class MyProfileEditActivity extends BasePhotoActivity implements
		OnClickListener, UserStateChangeListener, EditInfoListener {

	private UserBean userBean ;
	private MyUserBeanManager myUserBeanManager;
	private ImageView user_icon;

	private List<String> ageList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mine_info_personal);
		initView();
		initListener();
	}

	private void initView() {
		initProgressDialog();
		EventBus.getDefault().register(this);

		myUserBeanManager =  getITopicApplication().getMyUserBeanManager();
		userBean = myUserBeanManager.getInstance();
		
		user_icon = (ImageView) findViewById(R.id.user_head);

		GlideLoaderUtil.loadImage(this,ValueUtil.getQiniuUrlByFileName(userBean.getAvatar(),true),R.drawable.user_photo,user_icon);

		onUserInfoChanged(userBean);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
		myUserBeanManager.addOnUserStateChangeListener(this);
		
		findViewById(R.id.avatar_ll).setOnClickListener(this);
		findViewById(R.id.realname_ll).setOnClickListener(this);
		findViewById(R.id.age_ll).setOnClickListener(this);
		findViewById(R.id.sex_ll).setOnClickListener(this);
		findViewById(R.id.city_ll).setOnClickListener(this);
		findViewById(R.id.introduce_tv).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i;
		switch (v.getId()) {
		case R.id.avatar_ll:
			hideKeyboard();
			startPictureSelect(user_icon);
			break;
		case R.id.realname_ll:
			jumpToEditTextActivity("昵称", userBean.getName(), "name",
					12,InputType.TYPE_NULL, false ,false );
			break;
		case R.id.introduce_tv:
			jumpToEditTextActivity("自我介绍", userBean.getIntro(), "intro"
					, 150,InputType.TYPE_NULL, true,true);
			break;
		case R.id.sex_ll:
			break;
		default:
			break;
		}
	}
	
	private void jumpToEditTextActivity(String title,String content,String eventName,
										int maxLength,int inputType,boolean canEmpty,boolean canBreak ) {
		Intent i = new Intent(MyProfileEditActivity.this, EditTextActivity.class);
		i.putExtra(EditTextActivity.TITLE_KEY, title);
		i.putExtra(EditTextActivity.CONTENT, content);
		i.putExtra(EditTextActivity.EVENT_NAME, eventName);
		i.putExtra(EditTextActivity.MAX_LENGTH_KEY, maxLength);
		i.putExtra(EditTextActivity.CAN_EMPTY, canEmpty);
		i.putExtra(EditTextActivity.INPUT_BREAK_ABLE, canBreak);
		if (inputType!=InputType.TYPE_NULL) {			
			i.putExtra(EditTextActivity.INPUT_TYPE, inputType);
		}
		startActivity(i);
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
		// 我的资料界面。不登录是不可能进入这个界面的，所以这个ub肯定不是null
		userBean = ub;

		TextView realname_tv = (TextView) findViewById(R.id.realname_tv);
		TextView age_tv = (TextView) findViewById(R.id.age_tv);
		TextView sex_tv = (TextView) findViewById(R.id.sex_tv);
		TextView introduce_tv = (TextView) findViewById(R.id.introduce_tv);
		TextView city_tv = (TextView) findViewById(R.id.city_tv);

		realname_tv.setText(userBean.getName());
		sex_tv.setText(findGenderText(userBean.getGender()));
		age_tv.setText(userBean.getAge() == 0?"":""+userBean.getAge());
		introduce_tv.setText(userBean.getIntro());
		city_tv.setText("" + userBean.getCityname());
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEditFail(String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
		showToast(message);
	}

	@Override
	public void onEditSuccess(HashMap<String,String> params) {
		// TODO Auto-generated method stub
		progress.dismiss();
		if (!TextUtils.isEmpty(params.get("avatar"))){
			//刚才修改成功的是头像，清除掉缓存
			clearCache();
		}
	}

	@Override
	public void onPhotoSelectSuccess(String picturePath,ImageView currentImageView) {
		GlideLoaderUtil.loadImage(MyProfileEditActivity.this,picturePath,R.drawable.gray_rect,currentImageView);
	}

	@Override
	public void onPhotoUploadSuccess(String qiniuFileName, JSONObject qiniuJSONObject,
									 ImageView currentImageView) {
		HashMap<String, String> params = new HashMap<>();
		params.put("avatar",qiniuFileName);
		myUserBeanManager.startEditInfoRun(params ,MyProfileEditActivity.this);
	}

	@Override
	public void onPhotoUploadFail(ImageView currentImageView) {
		currentImageView.setImageResource(R.drawable.user_photo);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		myUserBeanManager.removeUserStateChangeListener(this);
		super.onDestroy();
	}

	private String findGenderText(int genderCode){
		switch (genderCode){
			case 0:
				return "未填";
			case 1:
				return "男";
			case 2:
				return "女";
		}
		return "";
	}
}
