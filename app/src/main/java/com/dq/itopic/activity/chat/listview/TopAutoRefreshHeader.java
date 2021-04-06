package com.dq.itopic.activity.chat.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.dq.itopic.R;


/**
 * 顶部自动刷新的Header
 *
 * @author luocan
 */
public class TopAutoRefreshHeader extends RelativeLayout {
    private View mLayout;
    private boolean isShow = false;

    public TopAutoRefreshHeader(Context context) {
        super(context);
        init(context);
    }

    public TopAutoRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopAutoRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mLayout = LayoutInflater.from(context).inflate(R.layout.top_refresh_header, null);
        addView(mLayout);
        mLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void hide() {
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        lp.height = 0;
        mLayout.setLayoutParams(lp);
        isShow = false;
    }


    public void show() {
        if (isShow) {
            return;
        }
        isShow = true;
        LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mLayout.setLayoutParams(lp);
    }
}
