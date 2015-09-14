package mango.condor.domain.msg.lbs;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013-7-16 上午10:12:30
 * @Description 
 */
public class GetFollowMsgReqMsg extends LBSRequestMessage {
	private int pageNum;	// 获取第几页，从1开始
	private int pageSize;	// 每页的条数
	
	public GetFollowMsgReqMsg() {
		super(LBSMessageDefine.LBS_GET_FOLLOW_MSG_LIST_REQUEST);
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
