package mango.condor.component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013年11月20日 下午7:16:22
 * @Description
 * 		分页组件
 */
public class DataPager<T> extends Pager {
	private List<T> data;

	public DataPager () {
		
	}
	
	public DataPager (List<T> data, int pageSize) {
		super(data.size(), pageSize);
		this.data = data;
	}
	
	/**
	 * 获取分页数据
	 * @param pageNum
	 * @return
	 */
	public List<T> getPageData(int pageNum) {
		Pair<Integer, Integer> pair = getFromAndTo(pageNum);
		int fromIndex = pair.first;
		int toIndex = pair.second;
		
		return new ArrayList<T>( data.subList(fromIndex, toIndex) );
	}
	
	/**
	 * 列表分页数据
	 * @param pageNum
	 * @return
	 * 		List<Pair<Index, T>>
	 */
	public List<Pair<Integer, T>> enumeratePagerData (int pageNum) {
		Pair<Integer, Integer> pair = getFromAndTo(pageNum);
		int fromIndex = pair.first;
		int toIndex = pair.second;
		List<T> list = data.subList(fromIndex, toIndex);
		
		List<Pair<Integer, T>> result = new ArrayList<Pair<Integer, T>>(list.size());
		
		int size = list.size();
		for (int i = 0; i < size; i++) {
			result.add( Pair.makePair(fromIndex + i, list.get(i)) );
		}
		
		return result;
	}
	
	/**
	 * subListPager
	 * @param data
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public static <T> List<T> subListPager (List<T> data, int pageSize, int pageNum) {
		DataPager<T> pager = new DataPager<T>(data, pageSize);
		return pager.getPageData(pageNum);
	}
	
	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
	public static void main(String[] args) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 7; i++) {
			list.add(i * 10);
		}
		System.out.println( "list=" + list );
		
		DataPager<Integer> pager = new DataPager<Integer>(list, 5);
		for (int j = 0; j < 4; j++) {
			System.out.println( "p=" + j + "\t" + pager.getPageData(j) );
		}
		for (int j = 0; j < 4; j++) {
			System.out.println( "p=" + j + "\t" + pager.enumeratePagerData(j) );
		}
	}
}
