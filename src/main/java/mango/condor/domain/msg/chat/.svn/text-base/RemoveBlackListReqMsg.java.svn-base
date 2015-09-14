package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-15 下午2:24:28
 * @Description 
 */
public class RemoveBlackListReqMsg extends LBSRequestMessage {
	
	private int hisServerId;	// 对方服务器id
	private int hisId;			// 对方玩家id
	
	public RemoveBlackListReqMsg() {
		super(LBSMessageDefine.CHAT_REMOVE_BLACKLIST_REQUEST);
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
