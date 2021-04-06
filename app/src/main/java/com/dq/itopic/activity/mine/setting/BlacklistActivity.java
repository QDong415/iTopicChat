package com.dq.itopic.activity.mine.setting;

import java.util.HashMap;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.common.BaseActivity;
import com.dq.itopic.bean.StringResponse;
import com.dq.itopic.bean.UserBaseBean;
import com.dq.itopic.bean.UserBaseListResponse;
import com.dq.itopic.layout.SureOrCancelDialog;
import com.dq.itopic.tools.CompleteCallback;
import com.dq.itopic.tools.ServiceConstants;
import com.dq.itopic.tools.NetWorkCallback;
import com.dq.itopic.tools.OkHttpHelper;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;
import com.dq.itopic.views.CircleImageView;
import com.dq.itopic.views.PagedListView;

/**
 * 黑名单列表页面
 */
public class BlacklistActivity extends BaseActivity {

	private PagedListView pagedListView;

	private View mEmptyLayout;
	private List<UserBaseBean> list;

	private int pageNumber = 1;
	private MyListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview_refresh_paged_title);
		initView();
		initListener();
	}

	private void initView() {
		initProgressDialog();
		setTitleName("黑名单");
		findViewById(R.id.title_right).setVisibility(View.GONE);
		mEmptyLayout =  LayoutInflater.from(this).inflate(R.layout.listview_empty, null);

		pagedListView = (PagedListView) findViewById(R.id.paged_listview);

		requireFirstPageDate();
	}

	private void requireFirstPageDate() {
		HashMap<String, String> params = new HashMap<>();
		params.put("page", "1");
		params.put("block", "1");
		OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/getlist", params, new NetWorkCallback<UserBaseListResponse>(UserBaseListResponse.class,getITopicApplication()) {

			@Override
			public void onSuccess(Response okhttpResponse, UserBaseListResponse response) {
				// TODO Auto-generated method stub
				if (response.isSuccess()) {
					list = response.getData().getItems();
					pagedListView.onFinishLoading(response.getData().hasMore());
					adapter = new MyListAdapter(BlacklistActivity.this, list);
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
				HashMap<String, String> params = new HashMap<>();
				params.put("page", ""+pageNumber);
				params.put("block", "1");
				OkHttpHelper.getInstance().get(ServiceConstants.IP + "user/getlist", params, new NetWorkCallback<UserBaseListResponse>(UserBaseListResponse.class,getITopicApplication()) {

					@Override
					public void onSuccess(Response okhttpResponse, UserBaseListResponse pageResponse) {
						// TODO Auto-generated method stub
						if (pageResponse.isSuccess()) {
							List<UserBaseBean> tempList = pageResponse.getData().getItems();
							list.addAll(tempList);
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				UserBaseBean bean =  list.get(arg2-pagedListView.getHeaderViewsCount());
//				jumpToHisInfoActivity(bean.getUserid(),bean.getName(),bean.getAvatar());
                SureOrCancelDialog followDialog = new SureOrCancelDialog(
                        BlacklistActivity.this, "移除黑名单", "好",
                        new SureOrCancelDialog.SureButtonClick() {

                            @Override
                            public void onSureButtonClick() {
                                // TODO Auto-generated method stub
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("to_userid", bean.getUserid());
                                OkHttpHelper.getInstance().post(ServiceConstants.IP + "user/unblock", params,this, new CompleteCallback<StringResponse>(StringResponse.class,getITopicApplication()) {

                                    @Override
                                    public void onComplete(Response okhttpResponse, StringResponse response) {
                                        // TODO Auto-generated method stub
                                        list.remove(bean);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                followDialog.show();
			}
		});

		pagedListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				final UserBaseBean bean = list.get(arg2 - pagedListView.getHeaderViewsCount());
				SureOrCancelDialog followDialog = new SureOrCancelDialog(
						BlacklistActivity.this, "移除黑名单", "好",
						new SureOrCancelDialog.SureButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub
								HashMap<String, String> params = new HashMap<String, String>();
								params.put("to_userid", bean.getUserid());
								OkHttpHelper.getInstance().post(ServiceConstants.IP + "user/unblock", params,this, new CompleteCallback<StringResponse>(StringResponse.class,getITopicApplication()) {

									@Override
									public void onComplete(Response okhttpResponse, StringResponse response) {
										// TODO Auto-generated method stub
										list.remove(bean);
										adapter.notifyDataSetChanged();
									}
								});
							}
						});
				followDialog.show();
				return true;
			}
		});

	}

	private class MyListAdapter extends BaseAdapter {
		private List<UserBaseBean> list;
		private LayoutInflater mInflater;

		public MyListAdapter(Context mContext, List<UserBaseBean> list) {
			this.list = list;
			mInflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			return list == null?0:list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.listitem_follower,
						null);
				viewHolder.name_tv = (TextView) convertView
						.findViewById(R.id.name_tv);
				viewHolder.avatar_iv = (CircleImageView) convertView
						.findViewById(R.id.avatar_iv);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final UserBaseBean tb = list.get(position);
			viewHolder.name_tv.setText(tb.getName());
			GlideLoaderUtil.loadImage(BlacklistActivity.this,ValueUtil.getQiniuUrlByFileName(tb.getAvatar(),true)
					,R.drawable.user_photo,viewHolder.avatar_iv);

			return convertView;
		}
	}

	private static class ViewHolder {
		private TextView name_tv;
		private CircleImageView avatar_iv;
	}

}
