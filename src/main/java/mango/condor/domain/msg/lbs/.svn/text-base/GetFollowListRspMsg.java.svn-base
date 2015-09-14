package mango.condor.domain.msg.lbs;

import java.util.List;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程 
 * @Date 2013-7-16 上午10:12:39
 * @Description 
 */
public class GetFollowListRspMsg extends LBSResponseMessage {

	public GetFollowListRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private List<PlayerInfo> list;		// 关注的玩家列表

	public List<PlayerInfo> getList() {
		return list;
	}

	public void setList(List<PlayerInfo> list) {
		this.list = list;
	}
	
}
