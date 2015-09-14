package mango.condor.domain.msg.lbs;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-25 上午10:18:07
 * @Description 
 */
public class GetPlayerInfoRspMsg extends LBSResponseMessage {
	
	private PlayerInfo pi;	// 对方的资料
	
	public GetPlayerInfoRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public PlayerInfo getPi() {
		return pi;
	}

	public void setPi(PlayerInfo pi) {
		this.pi = pi;
	}
	
}
