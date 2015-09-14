package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * 删除私聊记录
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 李兴
* @Date 2014年3月18日 下午5:15:07
* @Description
 */
public class DeleteChatReqMsg extends LBSRequestMessage {
	private String chatIds;	// 待删除的聊天记录 (格式:0,0;serverId,playerId;serverId,playerId;serverId,playerId) 0,0标识删除关注消息

	public DeleteChatReqMsg() {
		super(LBSMessageDefine.CHAT_DELETE_CHAT);
	}

	public String getChatIds() {
		return chatIds;
	}

	public void setChatIds(String chatIds) {
		this.chatIds = chatIds;
	}
}
