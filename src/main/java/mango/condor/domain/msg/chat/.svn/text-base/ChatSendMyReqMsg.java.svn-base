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
public class ChatSendMyReqMsg extends LBSRequestMessage {
	private String content;			// 聊天内容
	private int receiverServerId;	// 对方服务器id
	private int receiver;			// 对方玩家id

	public ChatSendMyReqMsg() {
		super(LBSMessageDefine.CHAT_SEND_MY_REQUEST);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public int getReceiverServerId() {
		return receiverServerId;
	}

	public void setReceiverServerId(int receiverServerId) {
		this.receiverServerId = receiverServerId;
	}

}
