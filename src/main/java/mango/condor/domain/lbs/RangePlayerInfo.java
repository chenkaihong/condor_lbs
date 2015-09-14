package mango.condor.domain.lbs;

import java.util.Set;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 熊三山
 * @Date 2014-1-3 上午11:26:39
 * @Description
 */
public class RangePlayerInfo {

	private int size; // 人数
	private Set<String> uidSet; // 附近的人

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Set<String> getUidSet() {
		return uidSet;
	}

	public void setUidSet(Set<String> uidSet) {
		this.uidSet = uidSet;
	}

}
