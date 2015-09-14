package mango.condor.domain.msg.chat;

import java.util.List;

import mango.condor.domain.chat.ChatContentMy;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 胡连江
 * @Date 2012-10-11 下午4:42:20
 * @Description
 */
public class ChatSendMyRspMsg extends LBSResponseMessage {

	public ChatSendMyRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private int hisId;							// 对方玩家id
	private int hisServerId;					// 对方服务器id
	private List<ChatContentMy> myChatContent;	// 最新的聊天内容

	public List<ChatContentMy> getMyChatContent() {
		return myChatContent;
	}

	public void setMyChatContent(List<ChatContentMy> myChatContent) {
		this.myChatContent = myChatContent;
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
