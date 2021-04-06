package com.dq.itopic.activity.chat;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dq.itopic.R;
import com.dq.itopic.activity.chat.search.SearchTagActivity;
import com.dq.itopic.bean.ChatBean;
import com.dq.itopic.bean.UserBean;
import com.dq.itopic.layout.SureOrCancelDialog;
import com.dq.itopic.manager.ChatManager;
import com.dq.itopic.manager.ITopicApplication;
import com.dq.itopic.manager.MyUserBeanManager;
import com.dq.itopic.tools.DBReq;
import com.dq.itopic.tools.ValueUtil;
import com.dq.itopic.tools.imageloader.GlideLoaderUtil;

public class ConversationFragment extends BaseMessageFragment implements ChatManager.NewMessageGetListener, MyUserBeanManager.UserStateChangeListener{

	public final static int CELL_TYPE_REMIND = 10000;

	private boolean fragmentIsHidden;
	private ListView listView;
	private ChatManager chatManager;
	private List<ChatBean> list;
	private ChatBean praiseRemindBean,commentRemindBean,fansRemindBean,systemRemindBean;
	private MemberAdapter adapter;
	private ITopicApplication mApp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_conversion,container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initListener();
	}

	private void initView() {
		// TODO Auto-generated method stub
		final View title_layout = getView().findViewById(R.id.title_layout);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) title_layout.getLayoutParams(); //取控件textView当前的布局参数
		linearParams.height = (int)getResources().getDimension(R.dimen.navigation_height) + getStatusBarHeight();

		mApp = getITopicApplication();
		list = new ArrayList<ChatBean>();
		listView = (ListView) getView().findViewById(R.id.listview);
		adapter = new MemberAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		praiseRemindBean = new ChatBean();
		praiseRemindBean.setType(CELL_TYPE_REMIND);
		praiseRemindBean.setSubtype(ChatManager.REMIND_SUBTYPE_PRAISE);

		commentRemindBean = new ChatBean();
		commentRemindBean.setType(CELL_TYPE_REMIND);
		commentRemindBean.setSubtype(ChatManager.REMIND_SUBTYPE_COMMENT);

		fansRemindBean = new ChatBean();
		fansRemindBean.setType(CELL_TYPE_REMIND);
		fansRemindBean.setSubtype(ChatManager.REMIND_SUBTYPE_FANS);

		systemRemindBean = new ChatBean();
		systemRemindBean.setType(CELL_TYPE_REMIND);
		systemRemindBean.setSubtype(ChatManager.REMIND_SUBTYPE_AT);

		chatManager = mApp.getChatManager();
		//监听新消息已拉（pull/msg接口）
		chatManager.addOnNewMessageGetListener(this);

		mApp.getMyUserBeanManager().addOnUserStateChangeListener(this);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		chatManager.removeOnNewMessageGetListener(this);
		mApp.getMyUserBeanManager().removeUserStateChangeListener(this);
		super.onDestroy();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		getView().findViewById(R.id.search_layout).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent 	i = new Intent(getActivity(),SearchTagActivity.class);
				i.putExtra("newActivity",getActivity().getApplication().getPackageName()+".activity.chat.search.UserListActivity");
				startActivity(i);
			}
		});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ChatBean bean = list.get(arg2 - listView.getHeaderViewsCount());
				if (bean.getType() == ChatBean.TYPE_CHAT_SINGLE || bean.getType() == ChatBean.TYPE_CHAT_GROUP) {
					Intent i = new Intent(getActivity(), ChatActivity.class);
					i.putExtra("targetid", bean.getTargetid());
					i.putExtra("userid", bean.getOther_userid());
					i.putExtra("avatar", bean.getOther_photo());
					i.putExtra("name", bean.getOther_name());
					i.putExtra("type", bean.getType());
					startActivity(i);
				} else if (bean.getType() == CELL_TYPE_REMIND ) {
					if (bean.getSubtype() == ChatManager.REMIND_SUBTYPE_FANS ){
//						Intent i = new Intent(getActivity(), FriendShipActivity.class);
//						i.putExtra("isFansList", true);
//						startActivity(i);
					} else {
//						Intent i = new Intent(getActivity(), RemindActivity.class);
//						i.putExtra("pushType", bean.getSubtype());
//						startActivity(i);
					}
				}
			}
		});

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				// TODO Auto-generated method stub
				SureOrCancelDialog followDialog = new SureOrCancelDialog(
						getActivity(), "删除此对话", "确定",
						new SureOrCancelDialog.SureButtonClick() {

							@Override
							public void onSureButtonClick() {
								// TODO Auto-generated method stub
								ChatBean bean = list.get(arg2 - listView.getHeaderViewsCount());
								DBReq.getInstence(mApp).clearChatWithTargetid(bean.getTargetid());
								chatManager.thisMessageHadRead(ChatBean.TYPE_CHAT_SINGLE);

								list.remove(arg2 - listView.getHeaderViewsCount());
								adapter.notifyDataSetChanged();
							}
						});
				followDialog.show();
				return true;
			}
		});
	}

	@Override
	public void onNewMessageGet(List<Integer> messageTypes) {
		onUserLogin(null);
		return;
	}

	@Override
	public void onUserInfoChanged(UserBean ub) {

	}

	@Override
	public void onUserLogin(UserBean ub) {
		list.clear();
		praiseRemindBean.setHisTotalUnReadedChatCount(mApp.getUnreadNoticeManager().unreadPraiseRemindCount());
		commentRemindBean.setHisTotalUnReadedChatCount(mApp.getUnreadNoticeManager().unreadCommentRemindCount());
		fansRemindBean.setHisTotalUnReadedChatCount(mApp.getUnreadNoticeManager().unreadFansRemindCount());
		systemRemindBean.setHisTotalUnReadedChatCount(mApp.getUnreadNoticeManager().unreadAtRemindCount());

		list.add(praiseRemindBean);
		list.add(commentRemindBean);
		list.add(fansRemindBean);
		list.add(systemRemindBean);
		list.addAll(DBReq.getInstence(mApp).getConversationList());
		adapter.notifyDataSetChanged();
		if (list.isEmpty()){
			showEmptyView(listView,getEmptyDataLayout());
		} else {
			showEmptyView(listView,null);
		}
	}

	@Override
	public void onUserLogout() {
		list.clear();
		adapter.notifyDataSetChanged();
		showEmptyView(listView,getUnLoginLayout());
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.fragmentIsHidden = hidden;
		if (!hidden) {
			if (mApp.getMyUserBeanManager().isLogin()){
				onUserLogin(null);
			} else {
				onUserLogout();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!fragmentIsHidden) {
			if (mApp.getMyUserBeanManager().isLogin()){
				onUserLogin(null);
			} else {
				onUserLogout();
			}
		}
	}

	private class MemberAdapter extends BaseAdapter {
		private List<ChatBean> list;
		private LayoutInflater mInflater;
		public MemberAdapter(Context mContext, List<ChatBean> list) {
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

		@Override
		public int getItemViewType(int position) {
			ChatBean message = list.get(position);
			return message.getType() == CELL_TYPE_REMIND ? 1 : 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		public View getView(final int position, View convertView,ViewGroup parent) {
			ViewHolder viewHolder;
			int type = getItemViewType(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				if (type == 0){
					convertView = mInflater.inflate(R.layout.listitem_conversation, parent, false);
				} else {
					convertView = mInflater.inflate(R.layout.item_conversation_header, parent, false);
				}
				viewHolder.name = (TextView) convertView.findViewById(R.id.name);
				viewHolder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
				viewHolder.message = (TextView) convertView.findViewById(R.id.message);
				viewHolder.time = (TextView) convertView.findViewById(R.id.time);
				viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final ChatBean bean = list.get(position);
			if (bean.getHisTotalUnReadedChatCount() > 0) {
				// 显示与此用户的消息未读数
				viewHolder.unreadLabel.setText(String.valueOf(bean.getHisTotalUnReadedChatCount()));
				viewHolder.unreadLabel.setVisibility(View.VISIBLE);
			} else {
				viewHolder.unreadLabel.setVisibility(View.INVISIBLE);
			}
			if (type == 1){
				switch (bean.getSubtype()) {
					case ChatManager.REMIND_SUBTYPE_FANS :
						viewHolder.name.setText("新粉丝");
						viewHolder.avatar.setImageResource(R.drawable.messagescenter_fans);
						break;
					case ChatManager.REMIND_SUBTYPE_PRAISE :
						viewHolder.name.setText("赞");
						viewHolder.avatar.setImageResource(R.drawable.messagescenter_praise);
						break;
					case ChatManager.REMIND_SUBTYPE_COMMENT :
						viewHolder.name.setText("评论");
						viewHolder.avatar.setImageResource(R.drawable.messagescenter_comments);
						break;
					case ChatManager.REMIND_SUBTYPE_AT:
						viewHolder.name.setText("@我");
						viewHolder.avatar.setImageResource(R.drawable.messagescenter_at);
						break;
				}
			} else {
				viewHolder.time.setText(ValueUtil.getTimeStringFromNow(bean.getCreate_time(),false));
				viewHolder.name.setText(bean.getOther_name());
				GlideLoaderUtil.loadImage(ConversationFragment.this,ValueUtil.getQiniuUrlByFileName(bean.getOther_photo(),true),R.drawable.user_photo,viewHolder.avatar);
				viewHolder.message.setText(bean.getContent());
			}
			return convertView;
		}
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		private TextView name;
		/** 消息未读数 */
		private TextView unreadLabel;
		/** 最后一条消息的内容 */
		private TextView message;
		/** 最后一条消息的时间 */
		private TextView time;
		/** 用户头像 */
		private ImageView avatar;
	}

}
