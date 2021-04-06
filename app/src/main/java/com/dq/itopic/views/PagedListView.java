package com.dq.itopic.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.dq.itopic.R;

/**
 * 使用PagedListView 要保证PAGE_SIZE个item 一定可以充满屏幕 
 */
public class PagedListView extends ListView {
	public interface OnLoadMoreListener {
		void onLoadMoreItems();
	}

	private boolean isLoading;
	private boolean hasMoreItems;
	private OnLoadMoreListener onLoadMoreListener;
	public View loadingView;

	private View mEmptyView;// 内容为空显示的emptyview。当无headerview时候使用，占满全屏
	private View mEmptyFooterView; // 有headerview时候使用，只是在底部
	private OnScrollListener onScrollListener;

	private boolean loadingFootViewHadRemoved; //底部的loadingView被移除了，只有setEmptyFooter时候才触发

	public PagedListView(Context context) {
		super(context);
		initView(context);
	}

	public PagedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public PagedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public boolean isLoading() {
		return this.isLoading;
	}

	public void setIsLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public OnLoadMoreListener getOnLoadMoreListener(){
		return onLoadMoreListener;
	}


	private void setHasMoreItems(boolean hasMoreItems) {
		this.hasMoreItems = hasMoreItems;
		if (hasMoreItems && loadingFootViewHadRemoved) {
			//还有更多，但是footview被移除了
			addFooterView(loadingView);
		}
		loadingView.findViewById(R.id.footer_container_ll).setVisibility(hasMoreItems ? View.VISIBLE:View.GONE);
	}

	public boolean hasMoreItems() {
		return this.hasMoreItems;
	}


	/**
	 * false表示没有下一页了
	 */
	public void onFinishLoading(boolean hasMoreItems) {
		setHasMoreItems(hasMoreItems);
		setIsLoading(false);
	}


	private void initView(Context context) {
		setFooterDividersEnabled(false);
		isLoading = false;
		loadingView =  LayoutInflater.from(context).inflate( R.layout.footer_loading_view,null);
		addFooterView(loadingView);
		super.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//Dispatch to child OnScrollListener
				if (onScrollListener != null) {
					onScrollListener.onScrollStateChanged(view, scrollState);
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

				//Dispatch to child OnScrollListener
				if (onScrollListener != null) {
					onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
				if (totalItemCount > 0) {
					int lastVisibleItem = firstVisibleItem + visibleItemCount;
					if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
						if (onLoadMoreListener != null) {
							isLoading = true;
							onLoadMoreListener.onLoadMoreItems();
						}
					}
				}
			}
		});
	}

	@Override
	public void setOnScrollListener(OnScrollListener listener) {
		onScrollListener = listener;
	}

	//直接触发一次翻页
	public void startToGetMore(){
		if (!isLoading) {
			if(!hasMoreItems){
				hasMoreItems = true;
				loadingView.findViewById(R.id.footer_container_ll).setVisibility(hasMoreItems ? View.VISIBLE:View.GONE);
			}
			if (onLoadMoreListener != null) {
				isLoading = true;
				onLoadMoreListener.onLoadMoreItems();
			}
		}
	}

	public void showEmptyFooter(Context context,String message){
		if (mEmptyFooterView != null) {
			//已经有empty footer了
			return ;
		}
		mEmptyFooterView =  LayoutInflater.from(context).inflate(R.layout.listitem_empty_padding, null);
		TextView tv = (TextView)mEmptyFooterView.findViewById(R.id.textViewMessage);
		tv.setText(message == null ? "":message);
//		removeLoadingFooterView();
		addFooterView(mEmptyFooterView);
	}

	public void resetFooterMessage(String message){
		resetFooterMessageAndIcon(message,0 ,0);
	}

	public void resetFooterMessageAndIcon(String message,int drawableIcon){
		resetFooterMessageAndIcon(message, drawableIcon,0);
	}

	public void resetFooterMessageAndIcon(String message,int drawableIcon,int paddingBottomPx){
		if (mEmptyFooterView == null) {
			return ;
		}
		TextView tv = (TextView)mEmptyFooterView.findViewById(R.id.textViewMessage);
		tv.setText(message == null ? "":message);
		if (drawableIcon != 0){
			Drawable drawable = getResources().getDrawable(drawableIcon);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv.setCompoundDrawables(null,drawable, null, null);
		}
		if (paddingBottomPx != 0){
			tv.setPadding(0,tv.getPaddingTop(),0,paddingBottomPx);
		}
	}

	public void removeEmptyFooter(){
		if (mEmptyFooterView!=null) {
			removeFooterView(mEmptyFooterView);
			mEmptyFooterView = null;
		}
	}

	public void removeLoadingFooterView(){
		removeFooterView(loadingView);
		loadingFootViewHadRemoved = true;
	}

	public void hideFooterTopLine(){
		mEmptyFooterView.findViewById(R.id.empty_topline).setVisibility(View.GONE);
	}

	public void setFooterViewBackgroundColor(int color){
		mEmptyFooterView.setBackgroundColor(color);
	}

	public final void setEmptyView(View newEmptyView) {
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

			((ViewGroup) getParent()).addView(newEmptyView,
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.FILL_PARENT);

			super.setEmptyView(newEmptyView);
		}
		this.mEmptyView = newEmptyView;
	}
}
