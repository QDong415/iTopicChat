package com.dq.itopic.activity.chat.keyboard;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.dq.itopic.R;
import com.dq.itopic.tools.ValueUtil;

public class AppGridLayout extends GridLayout {

	private int pictureSize;

	public AppGridLayout(Context context){
		super(context);
	}

	public AppGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void initView(Context context,OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		setPadding(0	, ValueUtil.dip2px(context,18),0,0);
		Activity activity = (Activity) context;
		WindowManager wm = activity.getWindowManager();
		int margin = (int)context.getResources().getDimension(R.dimen.topic_gridphoto_leftmargin);
		pictureSize = (wm.getDefaultDisplay().getWidth() - 2 *margin )/3;
		setRowCount(1);
		setColumnCount(3);

		View layout2 =  itemView(context,"图片",R.drawable.message_more_pic);
		addView(layout2);
		layout2.setTag(1);
		layout2.getLayoutParams().width = pictureSize;
		layout2.getLayoutParams().height = pictureSize;
		layout2.setOnClickListener(onClickListener);

		layout2 =  itemView(context,"语音通话",R.drawable.message_more_audio_call);
		addView(layout2);
		layout2.setTag(2);
		layout2.getLayoutParams().width = pictureSize;
		layout2.getLayoutParams().height = pictureSize;
		layout2.setOnClickListener(onClickListener);

		layout2 =  itemView(context,"视频通话",R.drawable.message_more_video_call);
		addView(layout2);
		layout2.setTag(3);
		layout2.getLayoutParams().width = pictureSize;
		layout2.getLayoutParams().height = pictureSize;
		layout2.setOnClickListener(onClickListener);

		layout2 =  itemView(context,"送礼物",R.drawable.message_more_groupluckybag);
		addView(layout2);
		layout2.setTag(4);
		layout2.getLayoutParams().width = pictureSize;
		layout2.getLayoutParams().height = pictureSize;
		layout2.setOnClickListener(onClickListener);
	}

	private View itemView(Context context,String name,int picture){
		View layout2 =  LayoutInflater.from(context).inflate(R.layout.griditem_app,null);
		ImageView imageview2 = (ImageView)layout2.findViewById(R.id.imageview);
		imageview2.setImageResource(picture);
		TextView groupname_tv = (TextView)layout2.findViewById(R.id.groupname_tv);
		groupname_tv.setText(name);
		return layout2;
	}
}

