package mango.condor.component;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013年11月20日 下午7:06:12
 * @Description 
 */
public class Pager {
	/**
	 * 总数
	 */
	private int total;
	
	/**
	 * 分页显示数量
	 */
	private int pageSize;

	public Pager () {
		
	}
	
	public Pager (int total, int pageSize) {
		this.total = total;
		this.pageSize = pageSize;
	}
	
	/**
	 * 计算分页数量
	 * @param total
	 * @param pageSize
	 * @return
	 */
	public static int calculatePageCount (int total, int pageSize) {
		if (total <= 0) {
			return 0;
		}
		
		return (total + pageSize - 1) / pageSize;
	}
	
	/**
	 * 获取分页数量
	 * @return
	 */
	public int getPageCount () {
		return calculatePageCount(total, pageSize);
	}
	
	/**
	 * 计算并获取正确的 pageNum
	 * @return
	 */
	public int getAndValidPageNum (int pageNum) {
		if (pageNum < 1) {
			pageNum = 1;
		}
		else {
			int pageCount = getPageCount();
			if (pageNum > pageCount) {
				pageNum = pageCount;
			}
			
			if (pageNum < 1) {
				pageNum = 1;
			}
		}
		
		return pageNum;
	}
	
	/**
	 * 获取 fromIndex & toIndex
	 * @param pageNum
	 * @return
	 */
	public Pair<Integer, Integer> getFromAndTo (int pageNum) {
		int from = (getAndValidPageNum(pageNum) - 1) * pageSize;
		int to = Math.min(from + pageSize, total);
		return Pair.makePair(from, to);
	}
	
	/**
	 * 获取 offset & limit
	 * @param pageNum
	 * @return
	 */
	public Pair<Integer, Integer> getOffsetAndLimit (int pageNum) {
		int offset = (getAndValidPageNum(pageNum) - 1) * pageSize;
		int limit = pageSize;
		return Pair.makePair(offset, limit);
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "Pager [total=" + total + ", pageSize=" + pageSize + "]";
	}
}
