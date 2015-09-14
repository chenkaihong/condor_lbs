package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程  
 * @Date 2013-9-29 上午9:54:10
 * @Description 
 */
public class ViewBottleContentReqMsg extends LBSRequestMessage {
	
	private long bottleId;	// 瓶子id
	
	public ViewBottleContentReqMsg() {
		super(LBSMessageDefine.BOTTLE_VIEW_CONTENT);
	}

	public long getBottleId() {
		return bottleId;
	}

	public void setBottleId(long bottleId) {
		this.bottleId = bottleId;
	}
	
}
