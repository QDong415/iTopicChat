package com.dq.itopic.activity.chat.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
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
import com.dq.itopic.activity.common.BaseFragment;
import com.dq.itopic.bean.HashMapListResponse;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.NetWorkCallback;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.views.PagedListView;
import com.dq.itopic.views.PtrSimpleFrameLayout;

public class UserListFragment extends BaseFragment {

	private int pageNumber = 1;
	private PtrSimpleFrameLayout mPtrFrame;
	private PagedListView pagedListView;
	
	private View mEmptyLayout;
	private List<HashMap<String,String>> list;
	private MemberAdapter adapter;

	private HashMap<String,String> params;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_listview_refresh_paged,container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		initProgressDialog();

		Bundle bundle = getArguments();
		if(bundle != null){
			this.params = (HashMap<String, String>) bundle.getSerializable("params");
		} else {
			this.params = new HashMap<>();
		}

		mEmptyLayout =  LayoutInflater.from(getActivity()).inflate(R.layout.listview_empty, null);

		pagedListView = (PagedListView) getView().findViewById(R.id.paged_listview);

		mPtrFrame = (PtrSimpleFrameLayout) getView().findViewById(R.id.rotate_header_list_view_frame);
		mPtrFrame.setEnabledNextPtrAtOnce(true);
		mPtrFrame.setLoadingMinTime(600);
		mPtrFrame.setPtrHandler(new PtrHandler() {

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				requireFirstPageDate();
			}

			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return PtrDefaultHandler.checkContentCanBePulledDown(frame, pagedListView, header);
			}
		});
		mPtrFrame.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPtrFrame.autoRefresh();
			}
		}, 150);
	}

	private void requireFirstPageDate() {
		HashMap<String, String> data = new HashMap<String, String>();
		data.putAll(params);
		data.put("page", "1");
		OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/getlist", data, new NetWorkCallback<HashMapListResponse>(HashMapListResponse.class,getITopicApplication()) {

			@Override
			public void onSuccess(Response okhttpResponse, HashMapListResponse response) {
				// TODO Auto-generated method stub
				mPtrFrame.refreshComplete();
				if (response.isSuccess()) {
					list = response.getData().getItems();
					pagedListView.onFinishLoading(response.getData().hasMore());
					adapter = new MemberAdapter(getBaseActivity(), list);
					pagedListView.setAdapter(adapter);
					pageNumber = 2;
					pagedListView.setEmptyView(list.isEmpty() ? mEmptyLayout:null);
				} else {
					pagedListView.onFinishLoading(false);
					showErrorToast(response.getMessage());
				}
			}

			@Override
			public void onFailure(Request okhttpRequest, Exception e) {
				// TODO Auto-generated method stub
				mPtrFrame.refreshComplete();
				pagedListView.onFinishLoading(false);
			}
		});
	}
	
	private void initListener() {
		// TODO Auto-generated method stub
		BackButtonListener();
		pagedListView.setOnLoadMoreListener(new PagedListView.OnLoadMoreListener() {

			@Override
			public void onLoadMoreItems() {
				// TODO Auto-generated method stub
				HashMap<String, String> data = new HashMap<String, String>();
				data.putAll(params);
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
							showErrorToast(pageResponse.getMessage());
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

			GlideLoaderUtil.loadImage(UserListFragment.this,ValueUtil.getQiniuUrlByFileName(bean.get("avatar"),true)
					,R.drawable.user_photo,viewHolder.avatar);

			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView item_name,item_content;
		private ImageView avatar;
	}

}
