package mango.condor.domain.msg;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-10 下午3:26:46
 * @Description
 */
public abstract class LBSMessage {

	protected int messageId;

	protected int playerId;

	protected int serverId;
	
	public LBSMessage(int messageId) {
		this.messageId = messageId;
	}
	
	public LBSMessage() {
		this.serverId = this.playerId = this.messageId = 0;
	}

	public int getMessageId() {
		return messageId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	
}