package mango.condor.domain.msg.bottle;

import mango.condor.domain.bottle.BottleContent;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-29 上午9:54:32
 * @Description 
 */
public class GetBottleContentRspMsg extends LBSResponseMessage {
	
	private BottleContent bottleContent;	// 瓶子内容
	
	public GetBottleContentRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public BottleContent getBottleContent() {
		return bottleContent;
	}

	public void setBottleContent(BottleContent bottleContent) {
		this.bottleContent = bottleContent;
	}
	
}
