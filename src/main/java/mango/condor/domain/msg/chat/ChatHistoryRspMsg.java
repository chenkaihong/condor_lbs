package mango.condor.domain.msg.chat;

import java.util.List;

import mango.condor.domain.chat.ChatContentMy;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-4-18 下午4:32:01
 * @Description
 */
public class ChatHistoryRspMsg extends LBSResponseMessage {

	public ChatHistoryRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private int hisId;			// 对方玩家id
	private int hisServerId;	// 对方服务器id
	private List<ChatContentMy> myChatContent;	// 聊天内容列表

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

	public List<ChatContentMy> getMyChatContent() {
		return myChatContent;
	}

	public void setMyChatContent(List<ChatContentMy> myChatContent) {
		this.myChatContent = myChatContent;
	}

}
