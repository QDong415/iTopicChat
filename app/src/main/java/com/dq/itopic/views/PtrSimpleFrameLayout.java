package com.dq.itopic.views;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by dongjin on 2017/6/23.
 */

public class PtrSimpleFrameLayout extends PtrFrameLayout {

    private PtrSimpleHeader mPtrSimpleHeader;

    public PtrSimpleFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrSimpleFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrSimpleFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrSimpleHeader = new PtrSimpleHeader(getContext());
        setHeaderView(mPtrSimpleHeader);
        addPtrUIHandler(mPtrSimpleHeader);
    }

    public PtrSimpleHeader getHeader() {
        return mPtrSimpleHeader;
    }


}
