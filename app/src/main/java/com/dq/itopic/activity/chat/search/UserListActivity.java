package com.dq.itopic.activity.chat.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.activity.common.BaseFragment;
import com.dq.itopic.bean.HashMapListResponse;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.NetWorkCallback;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.views.PagedListView;

public class UserListActivity extends BaseActivity {

	private int pageNumber = 1;
	private PagedListView pagedListView;
	
	private View mEmptyLayout;
	private List<HashMap<String,String>> list;
	private MemberAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_refresh_paged_title);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();
		setTitleName(""+getIntent().getStringExtra("keyword"));
		findViewById(R.id.title_right).setVisibility(View.GONE);

		mEmptyLayout =  LayoutInflater.from(UserListActivity.this).inflate(R.layout.listview_empty, null);

		pagedListView = (PagedListView) findViewById(R.id.paged_listview);

		requireFirstPageDate();
	}

	private void requireFirstPageDate() {
		HashMap<String,String> data = new HashMap<>();
		data.put("keyword",getIntent().getStringExtra("keyword"));
		data.put("page", "1");
		OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/getlist", data, new NetWorkCallback<HashMapListResponse>(HashMapListResponse.class,getITopicApplication()) {

			@Override
			public void onSuccess(Response okhttpResponse, HashMapListResponse response) {
				// TODO Auto-generated method stub
				if (response.isSuccess()) {
					list = response.getData().getItems();
					pagedListView.onFinishLoading(response.getData().hasMore());
					adapter = new MemberAdapter(UserListActivity.this, list);
					pagedListView.setAdapter(adapter);
					pageNumber = 2;
					pagedListView.setEmptyView(list.isEmpty() ? mEmptyLayout:null);
				} else {
					pagedListView.onFinishLoading(false);
					showToast(response.getMessage());
				}
			}

			@Override
			public void onFailure(Request okhttpRequest, Exception e) {
				// TODO Auto-generated method stub
				pagedListView.onFinishLoading(false);
			}
		});
	}
	
	private void initListener() {
		// TODO Auto-generated method stub
		backButtonListener();
		pagedListView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				HashMap<String, String> data = new HashMap<String, String>();
				data.put("keyword",getIntent().getStringExtra("keyword"));
				data.put("page", ""+pageNumber);
				OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/getlist", data, new NetWorkCallback<HashMapListResponse>(HashMapListResponse.class,getITopicApplication()) {

					@Override
					public void onSuccess(Response okhttpResponse, HashMapListResponse pageResponse) {
						// TODO Auto-generated method stub
						
						if (pageResponse.isSuccess()) {
							list.addAll(pageResponse.getData().getItems());
							adapter.notifyDataSetChanged();
							pagedListView.onFinishLoading(pageResponse.getData().hasMore());
							pageNumber++;
						} else {
							pagedListView.onFinishLoading(false);
							showToast(pageResponse.getMessage());
						}
					}

					@Override
					public void onFailure(Request okhttpRequest, Exception e) {
						// TODO Auto-generated method stub
						pagedListView.onFinishLoading(false);
					}
				});

			}
		});

		pagedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Map<String,String> bean = list.get(arg2 - pagedListView.getHeaderViewsCount());
				jumpToHisInfoActivity(bean.get("userid"), bean.get("name"), bean.get("avatar"));
			}
		});
	}

	private class MemberAdapter extends BaseAdapter {
		private List<HashMap<String,String>> list;
		private LayoutInflater mInflater;
		public MemberAdapter(Context mContext, List<HashMap<String,String>> list) {
			this.list = list;
			mInflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_friendapply, null);
				viewHolder.item_name = (TextView) convertView.findViewById(R.id.item_name);
				viewHolder.item_content = (TextView) convertView.findViewById(R.id.item_content);
				viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
				convertView.findViewById(R.id.fragment_ll).setVisibility(View.GONE);
				viewHolder.item_content.setVisibility(View.GONE);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final Map<String,String> bean = list.get(position);

			viewHolder.item_name.setText(bean.get("name"));

			GlideLoaderUtil.loadImage(UserListActivity.this,ValueUtil.getQiniuUrlByFileName(bean.get("avatar"),true)
					,R.drawable.user_photo,viewHolder.avatar);

			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView item_name,item_content;
		private ImageView avatar;
	}

}
