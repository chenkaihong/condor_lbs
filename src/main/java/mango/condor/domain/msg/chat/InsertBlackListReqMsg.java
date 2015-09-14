package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-15 下午12:01:29
 * @Description 
 */
public class InsertBlackListReqMsg extends LBSRequestMessage {
	
	private int hisServerId;	// 对方服务器id
	private int hisId;			// 对方玩家id
	
	public InsertBlackListReqMsg() {
		super(LBSMessageDefine.CHAT_INSERT_BLACKLIST_REQUEST);
	}

	public int getHisServerId() {
		return hisServerId;
	}

	public void setHisServerId(int hisServerId) {
		this.hisServerId = hisServerId;
	}

	public int getHisId() {
		return hisId;
	}

	public void setHisId(int hisId) {
		this.hisId = hisId;
	}
	
}
