package mango.condor.domain.msg.lbs;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-25 下午2:43:40
 * @Description 
 */
public class GetFollowRankReqMsg extends LBSRequestMessage {
	private int pageNum;	// 获取第几页，从1开始
	private int pageSize;	// 每页的条数
	
	public GetFollowRankReqMsg() {
		super(LBSMessageDefine.LBS_GET_FOLLOW_RANK_REQUEST);
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}
