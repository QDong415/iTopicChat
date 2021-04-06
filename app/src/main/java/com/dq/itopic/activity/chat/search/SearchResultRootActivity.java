package com.dq.itopic.activity.chat.search;

import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.tools.ValueUtil;

import java.util.HashMap;

public class SearchResultRootActivity extends BaseActivity implements IndicatorViewPager.OnIndicatorPageChangeListener {

	private UserListFragment memberFragment;

	private LayoutInflater inflate;
	private IndicatorViewPager indicatorViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_root);
		initView(savedInstanceState);
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
	}

	private void initView(Bundle savedInstanceState) {
		setTitleName(""+getIntent().getStringExtra("keyword"));

		if (savedInstanceState != null && getSupportFragmentManager().getFragments()!=null && getSupportFragmentManager().getFragments().size()>0) {
			memberFragment = (UserListFragment)getSupportFragmentManager().getFragments().get(1);
		} else {
			memberFragment = new UserListFragment();
		}

		Bundle bundle3 = new Bundle();
		HashMap<String,String> params4 = new HashMap<>();
		params4.put("keyword",getIntent().getStringExtra("keyword"));
		bundle3.putSerializable("params",params4);
		memberFragment.setArguments(bundle3);

		Resources res = getResources();
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
		int selectColor = res.getColor(R.color.blue_color);
		indicator.setScrollBar(new TextWidthColorBar(this,indicator, selectColor, ValueUtil.dip2px(this,3)));
		float unSelectSize = 16;
		float selectSize = 16;
		int unSelectColor = res.getColor(R.color.text_black_color);
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

		viewPager.setOffscreenPageLimit(1);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		indicatorViewPager.setOnIndicatorPageChangeListener(this);
		inflate = LayoutInflater.from(this);
		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getChildFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
	}

	@Override
	public void onIndicatorPageChange(int preItem, int currentItem) {
		
	}

	private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			TextView textView = (TextView) convertView;
			switch (position){
				case 0:
					textView.setText("用户");
					break;
			}
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			switch (position){
				case 0:
					return memberFragment;
			}
			return null;
		}
	}

}