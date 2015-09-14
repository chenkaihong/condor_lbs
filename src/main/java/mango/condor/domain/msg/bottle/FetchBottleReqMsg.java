package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午10:44:00
 * @Description 
 */
public class FetchBottleReqMsg extends LBSRequestMessage {
	
	public FetchBottleReqMsg() {
		super(LBSMessageDefine.BOTTLE_THROW_REQUEST);
	}
	
}
