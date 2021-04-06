package com.dq.itopic.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.mine.edit.MyProfileEditActivity;
import com.dq.itopic.activity.mine.setting.MineSystemActivity;
import com.dq.itopic.activity.common.BaseFragment;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.manager.MyUserBeanManager;
import com.dq.itopic.manager.MyUserBeanManager.UserStateChangeListener;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.views.MyScrollView;


public class MineFragment extends BaseFragment implements OnClickListener,
		UserStateChangeListener {
	
	private ImageView avatar_iv;
	private TextView name_tv;
	private MyUserBeanManager myUserBeanManager;

	private float hearderMaxHeight;
	private float titleMaxScrollHeight;
	private float maxScrollHeight;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_mine_tab_personal,container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	protected void initView() {
		// TODO Auto-generated method stub
		myUserBeanManager = getITopicApplication().getMyUserBeanManager();
		myUserBeanManager.addOnUserStateChangeListener(this);

		avatar_iv = (ImageView) getView().findViewById(R.id.avatar_iv);
		name_tv = (TextView)  getView().findViewById(R.id.name_tv);

		final View title_tv =  getView().findViewById(R.id.title);
		final View title_layout = getView().findViewById(R.id.title_layout);
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) title_layout.getLayoutParams(); //取控件textView当前的布局参数
		linearParams.height = (int)getResources().getDimension(R.dimen.navigation_height) + getStatusBarHeight();
		MyScrollView scroll_view = (MyScrollView) getView().findViewById(R.id.scroll_view);
		scroll_view.setOnScrollListener(new MyScrollView.OnScrollListener() {
			@Override
			public void onScroll(int scrollY) {
				title_layout.setAlpha((float)scrollY/title_layout.getMeasuredHeight());
				scrollY = -scrollY;
				if (titleMaxScrollHeight == 0) {
					titleMaxScrollHeight = ((View) title_tv.getParent()).getBottom() - title_tv.getTop();
					maxScrollHeight = hearderMaxHeight + titleMaxScrollHeight;
				}
				if (hearderMaxHeight == 0) {
					hearderMaxHeight = title_tv.getTop();
					maxScrollHeight = hearderMaxHeight + titleMaxScrollHeight;
				}
				title_tv.setTranslationY(Math.max(0, maxScrollHeight + scrollY));
			}
		});

		onUserInfoChanged(myUserBeanManager.getInstance());
	}

	protected void initListener() {
		// TODO Auto-generated method stub
		 getView().findViewById(R.id.name_tv).setOnClickListener(this);
		 getView().findViewById(R.id.avatar_iv).setOnClickListener(this);

		 getView().findViewById(R.id.setting_ll).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent i = new Intent(getActivity(),MineSystemActivity.class);
						startActivity(i);
					}
				});

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (!checkLogined()) {
			return;
		}
		Intent i = null;
		switch (arg0.getId()) {
		case R.id.avatar_iv:
		case R.id.name_tv:
			i = new Intent(getActivity(),MyProfileEditActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	@Override
	public void onUserInfoChanged(final UserBean ub) {
		if (ub == null) {
			avatar_iv.setImageResource(R.drawable.user_photo);
			name_tv.setText("未登录");
		} else {
			name_tv.setText("" + ub.getName());
			GlideLoaderUtil.loadImage(this,ValueUtil.getQiniuUrlByFileName(ub.getAvatar(),true),R.drawable.user_photo,avatar_iv);
		}
	}

	@Override
	public void onUserLogin(UserBean ub) {
		// TODO Auto-generated method stub
		onUserInfoChanged(ub);
	}

	@Override
	public void onUserLogout() {
		// TODO Auto-generated method stub
		onUserInfoChanged(null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		myUserBeanManager.removeUserStateChangeListener(this);
		super.onDestroy();
	}

}
