package com.dq.itopic.bean;

public class PagerData {
	private int total;
	private int totalpage;
	private int currentpage;
	private int nextpage;

	public int getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public int getnextpage() {
		return nextpage;
	}
	public void setnextpage(int nextpage) {
		this.nextpage = nextpage;
	}
	public boolean hasMore(){
		return totalpage > currentpage;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getCurrentpage() {
		return currentpage;
	}
	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}
}
