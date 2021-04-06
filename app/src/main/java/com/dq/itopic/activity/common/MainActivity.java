package com.dq.itopic.activity.common;

import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;
import com.dq.itopic.R;
import com.dq.itopic.activity.chat.ConversationFragment;
import com.dq.itopic.activity.mine.MineFragment;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.manager.ChatManager;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.manager.MyUserBeanManager.UserStateChangeListener;
import com.dq.itopic.tools.DBReq;
import com.dq.itopic.tools.statusbar.QMUIStatusBarHelper;
import com.igexin.sdk.PushManager;

public class MainActivity extends BaseActivity implements UserStateChangeListener, ChatManager.MessageHadRead
		, ChatManager.NewMessageGetListener , EasyPermissions.PermissionCallbacks {

	private final static int REQUIRE_PERMISSIONS_CODE_GETUI = 0x111;

	private TextView unReadMessageCountTV;
	private ITopicApplication app;

	private int currentTabIndex;//当前选中的tab
	private ConversationFragment conversationListFragment;
	private MineFragment mineFragment;

	private android.os.Handler mHandler = new android.os.Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView(savedInstanceState);
		initListener();
		ITopicApplication mApp = getITopicApplication();
		boolean emui = mApp.getMyUserBeanManager().requireHWToken(this);
		if (!emui)
			startGetui();
		QMUIStatusBarHelper.translucent(this);
		if (!isDarkMode()){
			QMUIStatusBarHelper.setStatusBarLightMode(this);
		}

		if(mApp.getMyUserBeanManager().isLogin()){
			mApp.getChatManager().pullMesssages();
		}

		checkLink(getIntent());
	}

	private void initView(Bundle savedInstanceState) {
		app = (ITopicApplication) getApplication();
		unReadMessageCountTV = (TextView) findViewById(R.id.unReadMessageCountTV);

		if (app.getMyUserBeanManager().isLogin()){
			resetConversationTabBadge();
		}

		RadioGroup rgOpterator = (RadioGroup) findViewById(R.id.rgOperator);
		
		currentTabIndex = rgOpterator.getCheckedRadioButtonId();
		rgOpterator.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				hideCurrentFragment(transaction);
				switch (checkedId) {
				case R.id.chat_radio:
					showFragment(getConversationFragment(),transaction);
					break;
				case R.id.mine_radio:
					showFragment(getMineFragment(),transaction);
					break;
				}
				currentTabIndex = checkedId;
			}
		});

		// 这里一定要在save为null时才加载Fragment，Fragment中onCreateView等生命周里加载根子Fragment同理
		// 因为在页面重启时，Fragment会被保存恢复，而此时再加载Fragment会重复加载，导致重叠
		if (savedInstanceState == null) {
			// 正常时候
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			showFragment(getConversationFragment(),transaction);
		} else {
			// “内存重启”时调用 解决重叠问题
			int currentCheckId = savedInstanceState.getInt("LAST_INDEX",R.id.chat_radio);
			List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
			for (Fragment fragment : fragmentList) {
				if (fragment instanceof ConversationFragment) {
					conversationListFragment = (ConversationFragment) fragment;
				} else if (fragment instanceof MineFragment) {
					mineFragment = (MineFragment) fragment;
				}
			}

			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			hideAllFragment(transaction);

			switch (currentCheckId) {
				case R.id.chat_radio:
					showFragment(getConversationFragment(),transaction);
					break;
				case R.id.mine_radio:
					showFragment(getMineFragment(),transaction);
					break;
			}
		}
	}

	//隐藏当前的Fragment，切换tab时候使用
	private void hideCurrentFragment(FragmentTransaction transaction) {
		switch (currentTabIndex) {
		case R.id.chat_radio:
			transaction.hide(conversationListFragment);
			break;
		case R.id.mine_radio:
			transaction.hide(mineFragment);
			break;
		default:
			break;
		}
	}

	//隐藏所有Fragment
	private void hideAllFragment(FragmentTransaction transaction) {
		if (mineFragment != null)
			transaction.hide(mineFragment);
		if (conversationListFragment != null)
			transaction.hide(conversationListFragment);
	}

	//显示Fragment
	private void showFragment(Fragment willShowFragment, FragmentTransaction transaction) {
		if (!willShowFragment.isAdded()){
			transaction.add(R.id.container,willShowFragment);
		} else {
			transaction.show(willShowFragment);
		}
		transaction.commit();
	}

	private ConversationFragment getConversationFragment(){
		if (conversationListFragment == null){
			conversationListFragment = new ConversationFragment();
		}
		return conversationListFragment;
	}

	private MineFragment getMineFragment(){
		if (mineFragment == null){
			mineFragment = new MineFragment();
		}
		return mineFragment;
	}

	private void initListener() {
		// TODO Auto-generated method stub
		app.getMyUserBeanManager().addOnUserStateChangeListener(this);
		app.getChatManager().addOnNewMessageGetListener(this);
		app.getChatManager().addOnMessageHadRead(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		app.getMyUserBeanManager().removeUserStateChangeListener(this);
		app.getChatManager().removeOnNewMessageGetListener(this);
		app.getChatManager().removeOnMessageHadRead(this);
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// 保存当前Fragment的下标
		RadioGroup rgOpterator = (RadioGroup) findViewById(R.id.rgOperator);
		outState.putInt("LAST_INDEX", rgOpterator.getCheckedRadioButtonId());
	}

	private void resetConversationTabBadge() {
        int unreadNotityCount =  app.getUnreadNoticeManager().unreadPraiseRemindCount()
                + app.getUnreadNoticeManager().unreadCommentRemindCount()
                + app.getUnreadNoticeManager().unreadFansRemindCount()
				+ app.getUnreadNoticeManager().unreadAtRemindCount();

		int unreadChatCount =  DBReq.getInstence(getITopicApplication()).getChatTotalUnreadCount();
		int unreadCount = unreadNotityCount + unreadChatCount;
		unReadMessageCountTV.setText(String.valueOf(unreadCount));
		unReadMessageCountTV.setVisibility(unreadCount == 0 ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		resetConversationTabBadge();
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		unReadMessageCountTV.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onMessageHadRead(int messageType) {
		resetConversationTabBadge();
	}

	@Override
	public void onNewMessageGet(List<Integer> messageTypes) {
		resetConversationTabBadge();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 点击notification bar进入聊天页面，保证只有一个聊天页面
		super.onNewIntent(intent);
		checkLink(intent);
	}

	private void startGetui(){
		// 个推官方要求请求两个权限
		// 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
		// read phone state用于获取 imei 设备信息
		String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
		if (EasyPermissions.hasPermissions(this, perms)) {
			PushManager.getInstance().initialize(this);
		} else {
			EasyPermissions.requestPermissions(this, "接收新消息需要读取sd卡权限",REQUIRE_PERMISSIONS_CODE_GETUI, perms);
		}
	}


	@Override
	public void onPermissionsGranted(int requestCode, List<String> perms) {
		if(requestCode == REQUIRE_PERMISSIONS_CODE_GETUI){
			PushManager.getInstance().initialize(this.getApplicationContext());
		}
	}

	@Override
	public void onPermissionsDenied(int requestCode, List<String> perms) {
		if(requestCode == REQUIRE_PERMISSIONS_CODE_GETUI){
			//根据个推官方注释，没权限部分功能会受到影响
			PushManager.getInstance().initialize(this.getApplicationContext());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
	}

	private void checkLink(Intent intent){
		final String swip = intent.getStringExtra("swip");
		if (!TextUtils.isEmpty(swip)){
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					int index = Integer.parseInt(swip);
					switch (index){
						case 1:
							RadioButton chat_radio = (RadioButton)findViewById(R.id.chat_radio);
							chat_radio.setChecked(true);
							break;
					}
				}
			},250);
		}
	}

	private long exitTime = 0;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
			return;
		}
		super.onBackPressed();
	}
}
