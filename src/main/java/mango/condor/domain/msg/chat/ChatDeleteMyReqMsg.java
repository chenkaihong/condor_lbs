package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 胡连江
 * @Date 2012-10-11 下午4:42:20
 * @Description
 */
public class ChatDeleteMyReqMsg extends LBSRequestMessage {
	private int hisId;			// 对方玩家id
	private int hisServerId;	// 对方服务器id

	public ChatDeleteMyReqMsg() {
		super(LBSMessageDefine.CHAT_DELETE_REQUEST);
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
