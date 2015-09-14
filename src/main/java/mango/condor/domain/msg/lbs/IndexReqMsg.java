package mango.condor.domain.msg.lbs;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-24 下午6:46:30
 * @Description 
 */
public class IndexReqMsg extends LBSRequestMessage {
	
	// 当前客户度新鲜事更新时间最新的一个玩家的更新时间戳，如果第一次请求则设为0
	private long ts;
	
	public IndexReqMsg() {
		super(LBSMessageDefine.LBS_INDEX_REQUEST);
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}
	
}
