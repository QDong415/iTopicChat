package com.dq.itopic.bean;

import java.io.Serializable;
import java.util.List;

public class StringListResponse extends BaseResponse {


	/**
	 *
	 */
	private static final long serialVersionUID = -3100299924567238696L;
	/**
	 *
	 */
	private StringListData data;

	public StringListData getData() {
		return data;
	}

	public void setData(StringListData data) {
		this.data = data;
	}

	public class StringListData extends PagerData implements Serializable{

		/**
		 *
		 */
		private static final long serialVersionUID = 2992714119670787789L;
		private List<String> items;

		public List<String> getItems() {
			return items;
		}

		public void setItems(List<String> items) {
			this.items = items;
		}

	}

}
