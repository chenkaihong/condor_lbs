package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-29 上午9:55:55
 * @Description 
 */
public class GetReplyRewardRspMsg extends LBSResponseMessage {
	
	private int rewardId;
	
	public GetReplyRewardRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}
	
}
