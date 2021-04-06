package com.dq.itopic.activity.chat;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseFragment;

public class BaseMessageFragment extends BaseFragment {

	private View mEmptyLayout;// 内容为空显示的emptyview
	private View unLoginLayout;// 没登录的emptyview
	private View mEmptyView;

	protected View getEmptyDataLayout(){
		if (mEmptyLayout == null){
			mEmptyLayout = LayoutInflater.from(getActivity()).inflate(
					R.layout.listview_empty_image_text, null);
			ImageView emptyImageView = (ImageView) mEmptyLayout
					.findViewById(R.id.emptyImageView);
		}
		return mEmptyLayout;
	}

	protected View getUnLoginLayout(){
		if (unLoginLayout == null){
			unLoginLayout = LayoutInflater.from(getActivity()).inflate(
					R.layout.listview_empty_image_text, null);
			ImageView emptyImageView = (ImageView) unLoginLayout
					.findViewById(R.id.emptyImageView);
			emptyImageView.setImageResource(R.drawable.tips_empty_ban);
			TextView textViewMessage = (TextView) unLoginLayout
					.findViewById(R.id.textViewMessage);
			textViewMessage.setText("需要登录后才能查看");
		}
		return unLoginLayout;
	}

	protected void showEmptyView(ListView listView, View newEmptyView) {
		// If we already have an Empty View, remove it
		if (null != mEmptyView) {

			if (mEmptyView == newEmptyView){
				return;
			}

			ViewParent currentEmptyViewParent = mEmptyView.getParent();
			if (null != currentEmptyViewParent
					&& currentEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) currentEmptyViewParent).removeView(mEmptyView);
			}
		}

		if (null != newEmptyView) {
			// New view needs to be clickable so that Android recognizes it as a
			// target for Touch Events
			newEmptyView.setClickable(true);

			ViewParent newEmptyViewParent = newEmptyView.getParent();
			if (null != newEmptyViewParent
					&& newEmptyViewParent instanceof ViewGroup) {
				((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
			}

			((ViewGroup) listView.getParent()).addView(newEmptyView,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT);

			listView.setEmptyView(newEmptyView);
		}
		this.mEmptyView = newEmptyView;
	}

}
