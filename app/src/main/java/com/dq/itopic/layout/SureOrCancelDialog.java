package com.dq.itopic.layout;

import com.dq.itopic.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SureOrCancelDialog extends Dialog {

	public TextView message_tv;
	public TextView sure_tv;
	public TextView cancel_tv;
	
	public SureOrCancelDialog(Context context,String message,String sureTextView, SureButtonClick onSureButtonClick) {
		super(context,R.style.LoadingDialog);
		initView(context, message, sureTextView, onSureButtonClick);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context,String message,String sureTextView,final SureButtonClick onSureButtonClick) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.dialog_sure, null);
		
		message_tv = (TextView)v.findViewById(R.id.message_tv);
		message_tv.setText(message);
		sure_tv = (TextView)v.findViewById(R.id.sure_tv);
		sure_tv.setText(sureTextView);
		
		cancel_tv = (TextView)v.findViewById(R.id.cancel_tv);
		cancel_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	
		sure_tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
				if (onSureButtonClick!=null) {
					onSureButtonClick.onSureButtonClick();
				}
			}
		});
	
		setContentView(v, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	
	}
	
	public interface SureButtonClick{
		public  void onSureButtonClick();
	}
}
