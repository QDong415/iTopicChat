package com.dq.itopic.layout;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.WebViewActivity;
import com.dq.itopic.tools.ValueUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TextDialog extends Dialog {

	public TextView title_tv;

	public TextDialog(Context context ) {
		super(context,R.style.LoadingDialog);
		// TODO Auto-generated constructor stub
	}

	public void initTextView(final Context context ,final String title) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_menu_title, null);
		TextView title_tv = layout.findViewById(R.id.title_tv);
		title_tv.setText(title);

		View titleline = new View(context);
		titleline.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
		layout.addView(titleline,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));

		List<String> menuList = new ArrayList<>();
		menuList.add("复制");

		for (int i = 0 ; i < menuList.size(); i++){
			final int position = i;
			TextView tv = new TextView(context);
			tv.setText(menuList.get(i));
			tv.setTextSize(17);
			tv.setPadding(0, ValueUtil.dip2px(context,14), 0, ValueUtil.dip2px(context,14));
			tv.setGravity(Gravity.CENTER);
			tv.setClickable(true);
			tv.setTextColor(context.getResources().getColor(R.color.text_black_color));
			tv.setBackgroundResource(R.drawable.white_background);
			layout.addView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
					if (position == 0){
						ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(title);
						Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
					}
				}
			});

			if (i != menuList.size()-1){
				//加上横线
				View line = new View(context);
				line.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
				layout.addView(line,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));
			}
		}

		setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

	}

	public void initPhoneView(final Context context ,final String title) {
		// TODO Auto-generated method stub
		
		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_menu_title, null);
		TextView title_tv = layout.findViewById(R.id.title_tv);
		title_tv.setText(title);

		View titleline = new View(context);
		titleline.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
		layout.addView(titleline,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));

		List<String> menuList = new ArrayList<>();
		menuList.add("复制");
		menuList.add("呼叫");

		for (int i = 0 ; i < menuList.size(); i++){
			final int position = i;
			TextView tv = new TextView(context);
			tv.setText(menuList.get(i));
			tv.setTextSize(17);
			tv.setPadding(0, ValueUtil.dip2px(context,14), 0, ValueUtil.dip2px(context,14));
			tv.setGravity(Gravity.CENTER);
			tv.setClickable(true);
			tv.setTextColor(context.getResources().getColor(R.color.text_black_color));
			tv.setBackgroundResource(R.drawable.white_background);
			layout.addView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
					if (position == 0){
						ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(title);
						Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + title));
						context.startActivity(dialIntent);
					}
				}
			});

			if (i != menuList.size()-1){
				//加上横线
				View line = new View(context);
				line.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
				layout.addView(line,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));
			}
		}

		setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
	
	}


	public void initWebView(final Context context ,final String title) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_menu_title, null);
		TextView title_tv = layout.findViewById(R.id.title_tv);
		title_tv.setText(title);

		View titleline = new View(context);
		titleline.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
		layout.addView(titleline,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));

		List<String> menuList = new ArrayList<>();
		menuList.add("复制");
		menuList.add("打开网页");

		for (int i = 0 ; i < menuList.size(); i++){
			final int position = i;
			TextView tv = new TextView(context);
			tv.setText(menuList.get(i));
			tv.setTextSize(17);
			tv.setPadding(0, ValueUtil.dip2px(context,14), 0, ValueUtil.dip2px(context,14));
			tv.setGravity(Gravity.CENTER);
			tv.setClickable(true);
			tv.setTextColor(context.getResources().getColor(R.color.text_black_color));
			tv.setBackgroundResource(R.drawable.white_background);
			layout.addView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
					if (position == 0){
						ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(title);
						Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Intent i = new Intent(context, WebViewActivity.class);
						i.putExtra("url", title);
						context.startActivity(i);
					}
				}
			});

			if (i != menuList.size()-1){
				//加上横线
				View line = new View(context);
				line.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
				layout.addView(line,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));
			}
		}

		setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

	}


	public void initEmailView(final Context context ,final String title) {
		// TODO Auto-generated method stub

		LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.dialog_menu_title, null);
		TextView title_tv = layout.findViewById(R.id.title_tv);
		title_tv.setText(title);

		View titleline = new View(context);
		titleline.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
		layout.addView(titleline,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));

		List<String> menuList = new ArrayList<>();
		menuList.add("复制");
		menuList.add("发邮件");

		for (int i = 0 ; i < menuList.size(); i++){
			final int position = i;
			TextView tv = new TextView(context);
			tv.setText(menuList.get(i));
			tv.setTextSize(17);
			tv.setPadding(0, ValueUtil.dip2px(context,14), 0, ValueUtil.dip2px(context,14));
			tv.setGravity(Gravity.CENTER);
			tv.setClickable(true);
			tv.setTextColor(context.getResources().getColor(R.color.text_black_color));
			tv.setBackgroundResource(R.drawable.white_background);
			layout.addView(tv,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
					if (position == 0){
						ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
						clipboard.setText(title);
						Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show();
					} else {
						Intent dialIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + title));
						context.startActivity(dialIntent);
					}
				}
			});

			if (i != menuList.size()-1){
				//加上横线
				View line = new View(context);
				line.setBackgroundColor(context.getResources().getColor(R.color.divider_color));
				layout.addView(line,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ValueUtil.dip2px(context,0.5f)));
			}
		}

		setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));

	}
}
