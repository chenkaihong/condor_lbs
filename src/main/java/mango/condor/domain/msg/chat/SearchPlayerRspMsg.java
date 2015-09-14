package mango.condor.domain.msg.chat;

import java.util.List;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程 
 * @Date 2013-8-2 上午10:34:48
 * @Description 
 */
public class SearchPlayerRspMsg extends LBSResponseMessage {
	
	private List<PlayerInfo> list;	// 搜索到的玩家列表
	
	public SearchPlayerRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public List<PlayerInfo> getList() {
		return list;
	}

	public void setList(List<PlayerInfo> list) {
		this.list = list;
	}
	
}
