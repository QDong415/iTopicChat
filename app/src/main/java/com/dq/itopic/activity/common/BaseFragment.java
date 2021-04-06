package com.dq.itopic.activity.common;

import com.dq.itopic.layout.LoadingDialog;
import com.dq.itopic.manager.ITopicApplication;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;

public class BaseFragment extends Fragment {

	public LoadingDialog progress;

	/**
	 * 如果登录了，返回true，否则返回false并去登录
	 * @return
	 */
	public boolean checkLogined(){
		return getBaseActivity().checkLogined();
	}
	
	public void initProgressDialog() {
		initProgressDialog(true, null);
	}

	public void initProgressDialog(boolean cancel, String message) {
		initProgressDialog(getActivity(), cancel, message);
	}

	public void initProgressDialog(Context mContext, boolean cancel,
			String message) {
		progress = new LoadingDialog(mContext, cancel);
	}

	public ITopicApplication getITopicApplication() {
		return (ITopicApplication) getActivity().getApplication();
	}

	public void hideKeyboard(View v) {
		((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(v.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public void hideKeyboard() {
		hideKeyboard(getActivity().getWindow().getDecorView());
	}
	
	public void showKeyboard(EditText et) {
		((InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE))
				.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
	}
	
	public String getUserID() {
		return getBaseActivity().getUserID();
	}
	
	public BaseActivity getBaseActivity(){
		return (BaseActivity)getActivity();
	}

	public int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object object = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = (Integer) field.get(object);
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			return 0;
		}
	}
}
