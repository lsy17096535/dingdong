package com.intexh.bidong.utils;

import java.util.ArrayList;
import java.util.List;

public class PageInfo <T>{
	private boolean isNeedClear = false;
	private boolean isNoMoreData = false;
	private Page page = new Page();
	private List<T> datas = new ArrayList<T>();
	
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	public boolean isNeedClear() {
		return isNeedClear;
	}
	public void setNeedClear(boolean isNeedClear) {
		this.isNeedClear = isNeedClear;
	}
	public boolean isNoMoreData() {
		return isNoMoreData;
	}
	public void setNoMoreData(boolean isNoMoreData) {
		this.isNoMoreData = isNoMoreData;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page mPage) {
		this.page = mPage;
	}
	
	
}
