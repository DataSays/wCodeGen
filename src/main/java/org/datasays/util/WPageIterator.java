package org.datasays.util;

import java.util.Iterator;
import java.util.List;

public abstract class WPageIterator<T> implements Iterator<T> {
	private WPage page;
	private List<T> data = null;
	private int dataIndex = 0;
	private Object params = null;

	public WPageIterator() {
		super();
	}

	public WPageIterator(Object params, WPage page) {
		super();
		this.params = params;
		reset(page);
	}

	public WPageIterator(WPage page) {
		super();
		reset(page);
	}

	public WPage getPage() {
		return page;
	}

	public void reset(WPage page) {
		this.page = page;
		_search();
	}

	@Override
	public boolean hasNext() {
		return page != null && page.getTotal() != null && page.getTotal() > 0 && page.getFrom() + 1 <= page.getTotal();
	}

	@Override
	public T next() {
		if (dataIndex + 1 > data.size()) {
			_search();
		}
		T d = data.get(dataIndex++);
		page.nextItem();
		return d;
	}

	public void update(List<T> data, int total) {
		this.data = data;
		this.page.setTotal(total);
	}

	private void _search() {
		doSearch();
		dataIndex = 0;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public abstract void doSearch();
}
