package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 胡连江  
 * @Date 2012-10-11 下午4:42:20
 * @Description 
 */
public class ChatListMyReqMsg extends LBSRequestMessage {
	private int pageSize;			// 分页的数据量
	private int pageNum;			// 第几页
	
	private boolean fromTimer;		// 请求是否来自客户端定时器
	private String unreadList; 		// 上次获取的未读私信列表 (0:标志关注, >0 标志私信中对方的Id)
	
	public ChatListMyReqMsg() {
		super(LBSMessageDefine.CHAT_LIST_MY_REQUEST);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public String getUnreadList() {
		return unreadList;
	}

	public void setUnreadList(String unreadList) {
		this.unreadList = unreadList;
	}

	public boolean isFromTimer() {
		return fromTimer;
	}

	public void setFromTimer(boolean fromTimer) {
		this.fromTimer = fromTimer;
	}
	
}
