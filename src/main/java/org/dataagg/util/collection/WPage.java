package org.dataagg.util.collection;

public class WPage {
	private int from = 0; //开始索引
	private int size = 20; //每页数量
	private Integer total = 0; //总数

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPages() {
		if (total <= 0) { return 0; }
		if (total <= size) { return 1; }
		if (total % size == 0) {
			return total / size;
		} else {
			return (total - total % size) / size + 1;
		}
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public void nextItem() {
		from++;
	}

	public void preItem() {
		from--;
	}

	public int getPageNo() {
		if (0 >= from) { return 1; }
		if (from < total) {
			return divide(from, size) + 1;
		} else {
			return getPages();
		}
	}

	private int divide(int fz, int fm) {
		if (fz == 0) { return 0; }
		if (fz % fm != 0) {
			return (fz - fz % fm) / fm;
		} else {
			return fz / fm;
		}
	}

	@Override
	public String toString() {
		return String.format("size: %d, total: %d, page: %d/%d", size, total, getPageNo(), getPages());
	}
}
