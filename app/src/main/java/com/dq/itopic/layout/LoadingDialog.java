package com.dq.itopic.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dq.itopic.R;


public class LoadingDialog extends AlertDialog {

	private TextView mMessageView;

	private boolean mCancelAble = true;

	public LoadingDialog(Context context, boolean cancelAble) {
		super(context, R.style.LoadingDialog);

		mCancelAble = cancelAble;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog);

		mMessageView = (TextView) findViewById(R.id.progress_message);

		setCancelable(mCancelAble);
		setCanceledOnTouchOutside(false);

		
	}

	/**
	 * 一定要先progress.show();再调用这个方法
	 * @param message
     */
	public void setMessage(String message) {
		if (message != null) {
			mMessageView.setVisibility(View.VISIBLE);
			mMessageView.setText(message);
		}
	}
}
