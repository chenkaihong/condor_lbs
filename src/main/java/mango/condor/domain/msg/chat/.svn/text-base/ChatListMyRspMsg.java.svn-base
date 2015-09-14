package mango.condor.domain.msg.chat;

import java.util.List;

import mango.condor.domain.chat.ChatMy;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 胡连江
 * @Date 2012-10-11 下午4:42:20
 * @Description
 */
public class ChatListMyRspMsg extends LBSResponseMessage {
	
	public ChatListMyRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private List<ChatMy> myChatList;	// 聊天会话列表	
	private int chatTotal;				// 聊天会话总数
	private boolean refresh;			// 是否需要刷新 - 针对客户端定时请求

	public List<ChatMy> getMyChatList() {
		return myChatList;
	}

	public void setMyChatList(List<ChatMy> myChatList) {
		this.myChatList = myChatList;
	}

	public int getChatTotal() {
		return chatTotal;
	}

	public void setChatTotal(int chatTotal) {
		this.chatTotal = chatTotal;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

}