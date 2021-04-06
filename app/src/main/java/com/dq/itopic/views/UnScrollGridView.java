package com.dq.itopic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class UnScrollGridView extends GridView {

	public UnScrollGridView(Context context) {
		super(context);
	}

	public UnScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
