package com.dq.itopic.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class HashMapListResponse extends BaseResponse{

	private HashMapListData data;

	public HashMapListData getData() {
		return data;
	}

	public void setData(HashMapListData data) {
		this.data = data;
	}

	public class HashMapListData extends PagerData implements Serializable{

		private List<HashMap<String,String>> items;

		public List<HashMap<String,String>> getItems() {
			return items;
		}

		public void setItems(List<HashMap<String,String>> items) {
			this.items = items;
		}

	}
}
