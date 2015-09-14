package mango.condor.domain.msg.lbs;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-13 下午2:50:30
 * @Description 
 */
public class CachePlayerInfoReqMsg extends LBSRequestMessage {
	
	private PlayerInfo pi;
	
	public CachePlayerInfoReqMsg() {
		super(LBSMessageDefine.LBS_CACHE_PLAYER_INFO);
	}

	public PlayerInfo getPi() {
		return pi;
	}

	public void setPi(PlayerInfo pi) {
		this.pi = pi;
	}
	
}
