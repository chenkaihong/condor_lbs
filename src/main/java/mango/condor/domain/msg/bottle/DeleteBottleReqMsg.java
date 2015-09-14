package mango.condor.domain.msg.bottle;

import java.util.List;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-29 上午9:56:20
 * @Description 
 */
public class DeleteBottleReqMsg extends LBSRequestMessage {
	
	private List<Long> bottleIdList;	// 瓶子id列表
	
	public DeleteBottleReqMsg() {
		super(LBSMessageDefine.BOTTLE_DELETE_BOTTLE_REQUEST);
	}

	public List<Long> getBottleIdList() {
		return bottleIdList;
	}

	public void setBottleIdList(List<Long> bottleIdLists) {
		this.bottleIdList = bottleIdLists;
	}
	
}
