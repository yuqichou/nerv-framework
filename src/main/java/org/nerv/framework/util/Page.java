package org.nerv.framework.util;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Page {

	private int pageSize = 20;
	private int firstPage = 1;
	private int gotoPage = 1;

	private int countPage;
	private int countData;

	private int lastPage;
	private int nextPage;
	private int prePage;

	private boolean hasNextPage = true;
	private boolean hasPrePage = true;

	@JsonIgnore
	private Object data;
	
	public Page() {
		super();
	}
	
	public Page(int gotoPage, int countData, int pageSize, Object data) {
		this(gotoPage, countData, pageSize);
		this.data = data;
	}

	private Page(int gotoPage, int countData, int pageSize) {
		if (gotoPage < 1) {
			gotoPage = 1;
		}
		if (pageSize < 1) {
			pageSize = 20;
		}

		this.gotoPage = gotoPage;
		this.countData = countData;
		this.pageSize = pageSize;

		if (countData == 0) {
			this.countPage = 1;
		}
		if (countData % pageSize == 0) {
			this.countPage = countData / pageSize;
		} else {
			this.countPage = countData / pageSize + 1;
		}
		this.lastPage = this.countPage;

		this.nextPage = this.gotoPage + 1;
		if (this.nextPage > this.countPage) {
			hasNextPage = false;
			this.nextPage = this.countPage;
		}

		if (this.gotoPage > this.lastPage) {
			this.prePage = this.lastPage;
		} else {
			this.prePage = this.gotoPage - 1;
			if (this.prePage < 1) {
				hasPrePage = false;
				this.prePage = 1;
			}
		}

	}

	public int getPageSize() {
		return pageSize;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public int getGotoPage() {
		return gotoPage;
	}

	public int getCountPage() {
		return countPage;
	}

	public int getCountData() {
		return countData;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public int getPrePage() {
		return prePage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public boolean isHasPrePage() {
		return hasPrePage;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public static void main(String[] args) {
		// int gotopage=2;
		// int countData=18;
		// int pagesize=15;
		// Page page=new Page(gotopage,countData,pagesize);
		//
		// ObjectMapper mapper = new ObjectMapper();
		// System.out.println(mapper.writeValueAsString(page));

		int showPerSize = 5;
		int gotopage = 3;
		int countpage = 8;

		int start = 0;
		int end = 0;

		if (countpage <= showPerSize) {
			start = 1;
			end = countpage;
		} else {
			if (showPerSize % 2 == 0) {
				start = gotopage - (showPerSize / 2 - 1);
				end = gotopage + (showPerSize / 2 + 1);
			} else {
				start = gotopage - showPerSize / 2;
				end = gotopage + showPerSize / 2;
			}

			System.out.println(start + "  " + end);

			if (start <= 0) {
				end = end + (1 - start);
				start = 1;
			} else if (end > countpage) {
				start = start - (end - countpage);
				end = countpage;
			}
		}

		for (int i = start; i <= end; i++) {
			System.out.println(i);
		}

	}

}
