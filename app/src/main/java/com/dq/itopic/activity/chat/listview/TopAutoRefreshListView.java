package com.dq.itopic.activity.chat.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * 顶部自动刷新的ListView
 * 目前只有ChatActivity在使用
 *
 * @author luocan
 */
public class TopAutoRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private static final String TAG = TopAutoRefreshListView.class.getSimpleName();
    private TopAutoRefreshHeader mHeaderLayout;
    private OnTopRefreshListener mOnTopRefreshListener;
    private boolean isEnable = true;
    private boolean isRefreshing = false;
    private AbsListView.OnScrollListener onTopAutoRefreshScrollListener;

    public TopAutoRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public TopAutoRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopAutoRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnTopRefreshListener(OnTopRefreshListener onTopRefreshListener) {
        this.mOnTopRefreshListener = onTopRefreshListener;
    }

    /**
     * 是否启用自动刷新
     *
     * @param isEnable
     */
    public void setTopRefreshEnable(boolean isEnable) {
        this.isEnable = isEnable;

        if (isEnable) {
            mHeaderLayout.show();
        } else {
            mHeaderLayout.hide();
        }
    }

    public void onTopRefreshFinished(){
        isRefreshing = false;
    }

    private void init(Context context) {
        mHeaderLayout = new TopAutoRefreshHeader(context);
        addHeaderView(mHeaderLayout);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onTopAutoRefreshScrollListener != null){
            onTopAutoRefreshScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (isEnable == false) {
            return;
        }
        if (scrollState == SCROLL_STATE_IDLE && view.getFirstVisiblePosition() == 0) {
            mHeaderLayout.show();
            if (mOnTopRefreshListener != null && !isRefreshing) {
                isRefreshing = true;
                mOnTopRefreshListener.onTopRefresh();
            }
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (onTopAutoRefreshScrollListener != null){
            onTopAutoRefreshScrollListener.onScroll(view, firstVisibleItem , visibleItemCount, totalItemCount);
        }
    }

    public interface OnTopRefreshListener {
        void onTopRefresh();
    }

    public void setOnTopAutoRefreshScrollListener(AbsListView.OnScrollListener onTopAutoRefreshScrollListener){
        this.onTopAutoRefreshScrollListener = onTopAutoRefreshScrollListener;
    }
}
