package com.dq.itopic.activity.mine.edit;

import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.views.ShakeAnimation;
import com.dq.itopic.R;
import com.dq.itopic.manager.MyUserBeanManager.EditInfoListener;
import com.dq.itopic.views.InputControlEditText;
import com.dq.itopic.views.InputControlEditText.GetInputLengthListener;
import com.dq.itopic.views.InputControlEditText.InputLengthHintListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.HashMap;

/**
 * 编辑，输入库activity
 * 
 * @author Administrator
 * 
 */
public class EditTextActivity extends BaseActivity implements
		InputLengthHintListener, GetInputLengthListener, EditInfoListener {

	private TextView showlength_tv, title_tv;
	private InputControlEditText inputContent;
	private int maxlength = 140;
	public static final String MAX_LENGTH_KEY = "MAX_LENGTH_KEY";
	public static final String TITLE_KEY = "TITLE_KEY";
	public static final String CONTENT = "CONTENT";
	public static final String EVENT_NAME = "EVENT_NAME";
	public static final String CAN_EMPTY = "CAN_EMPTY";
	public static final String INPUT_TYPE = "InputType";
	public static final String INPUT_BREAK_ABLE = "INPUT_BREAK_ABLE";//是否允许换行

	private boolean canBeBreak;
	private boolean canBeEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edittext);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		title_tv = (TextView) findViewById(R.id.title_tv);
		showlength_tv = (TextView) findViewById(R.id.showLength);
		inputContent = (InputControlEditText) findViewById(R.id.inputContent);
		inputContent.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (!canBeBreak && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					doneClick();
					return true;
				}
				return false;
			}
		});
		Intent i = getIntent();
		int inputType = i.getIntExtra(INPUT_TYPE,InputType.TYPE_NULL);
		if (inputType!=InputType.TYPE_NULL) {
			inputContent.setInputType(inputType);
		}

		maxlength = i.getIntExtra(MAX_LENGTH_KEY, maxlength);
		inputContent.setOnMaxInputListener(maxlength, this);

		inputContent.setOnGetInputLengthListener(this);

		title_tv.setText(i.getStringExtra(TITLE_KEY));
		inputContent.setText("" + i.getStringExtra(CONTENT));
		setShowLength(maxlength);
		canBeEmpty = i.getBooleanExtra(CAN_EMPTY, false);
		canBeBreak = i.getBooleanExtra(INPUT_BREAK_ABLE, false);

//		inputContent.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//					return true;
//				}
//				return false;
//			}
//		});
		
		Handler hander = new Handler();
		hander.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				inputContent.setSelection(inputContent.length());
				showKeyboard(inputContent);
			}
		}, 150);
	}

	private void initListener() {
		// TODO Auto-generated method stub
		findViewById(R.id.reg_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						doneClick();
					}
				});
		backButtonListener();
	}

	
	private void doneClick() {
		if (!canBeEmpty
				&& inputContent.getText().toString().trim()
						.equals("")) {
			ShakeAnimation.create().with(inputContent).start();
			showToast(title_tv.getText().toString()
					+ "不能为空");
			return;
		}
		if (inputContent.getText().toString().trim()
				.equals(getIntent().getStringExtra(CONTENT))) {
			finish();
			return;
		}
		hideKeyboard();
		progress.show();

		HashMap<String,String> params = new HashMap<>();
		params.put(getIntent().getStringExtra(EVENT_NAME),inputContent.getText().toString().trim());
		getITopicApplication().getMyUserBeanManager()
				.startEditInfoRun(params, EditTextActivity.this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return super.onTouchEvent(event);
	}

	private void setShowLength(int requestCode) {
		int currentLength = inputContent.getText().length();
		setShowTextLength(currentLength, maxlength);
	}

	@Override
	public void getInputLength(int length) {
		setShowTextLength(length, maxlength);
	}

	private void setShowTextLength(int currentLength, int maxLength) {
		showlength_tv.setText(currentLength + "/" + maxLength + " 字");
	}

	@Override
	public void onOverFlowHint() {
		// TODO Auto-generated method stub
		ShakeAnimation.create().with(showlength_tv).start();
	}

	@Override
	public void onEditFail(String message) {
		// TODO Auto-generated method stub
		progress.dismiss();
	}

	@Override
	public void onEditSuccess(HashMap<String,String> params) {
		// TODO Auto-generated method stub
		progress.dismiss();
		finish();
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
