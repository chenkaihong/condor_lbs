package mango.condor.domain.msg.chat;

import java.util.List;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-15 下午2:26:18
 * @Description 
 */
public class GetBlackListRspMsg extends LBSResponseMessage {

	public GetBlackListRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private List<PlayerInfo> list;	// 黑名单列表

	public List<PlayerInfo> getList() {
		return list;
	}

	public void setList(List<PlayerInfo> list) {
		this.list = list;
	}

}
