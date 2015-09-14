package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程 
 * @Date 2013-4-18 下午4:31:46
 * @Description 
 */
public class ChatHistoryReqMsg extends LBSRequestMessage {
	
	private int hisId;			// 对方玩家id
	private int hisServerId;	// 对方服务器id
	
	public ChatHistoryReqMsg() {
		super(LBSMessageDefine.CHAT_GET_HISTORY_CONTENT_REQUEST);
	}

	public int getHisId() {
		return hisId;
	}

	public void setHisId(int hisId) {
		this.hisId = hisId;
	}

	public int getHisServerId() {
		return hisServerId;
	}

	public void setHisServerId(int hisServerId) {
		this.hisServerId = hisServerId;
	}
	
}
