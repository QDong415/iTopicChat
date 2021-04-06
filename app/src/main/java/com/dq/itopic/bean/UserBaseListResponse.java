package com.dq.itopic.bean;

import java.io.Serializable;
import java.util.List;

public class UserBaseListResponse extends BaseResponse implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6373433945675310461L;
	private UserBaseListData data;

	public UserBaseListData getData() {
		return data;
	}

	public void setData(UserBaseListData data) {
		this.data = data;
	}

	public class UserBaseListData extends PagerData implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<UserBaseBean> items;

		public List<UserBaseBean> getItems() {
			return items;
		}

		public void setItems(List<UserBaseBean> items) {
			this.items = items;
		}

	}
}
