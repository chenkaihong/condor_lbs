package mango.condor.domain.msg.lbs;

import java.util.List;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-24 下午6:46:45
 * @Description 
 */
public class IndexRspMsg extends LBSResponseMessage {
	// 未读的私聊数
	private int unReadChatNum;
	// 随机刷出来的玩家列表（新鲜事）
	private List<PlayerInfo> list;
	
	public IndexRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public int getUnReadChatNum() {
		return unReadChatNum;
	}

	public void setUnReadChatNum(int unReadChatNum) {
		this.unReadChatNum = unReadChatNum;
	}

	public List<PlayerInfo> getList() {
		return list;
	}

	public void setList(List<PlayerInfo> list) {
		this.list = list;
	}
	
}
