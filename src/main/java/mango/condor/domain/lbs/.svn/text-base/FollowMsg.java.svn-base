package mango.condor.domain.lbs;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013年11月19日 下午4:49:55
 * @Description 
 */
public class FollowMsg {
	private int sender;
	private int senderServerId;
	private String senderImageId;
	private int receiver;
	private String content;
	private String strTime;
	private volatile long longStrTime;	// 字段strTime的 long 类型，仅仅为了排序 
	private boolean read;		// 是否已读
	
	public long getLongStrTime() {
		return longStrTime;
	}
	public void setLongStrTime(long longStrTime) {
		this.longStrTime = longStrTime;
	}
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}
	public int getSenderServerId() {
		return senderServerId;
	}
	public void setSenderServerId(int senderServerId) {
		this.senderServerId = senderServerId;
	}
	public int getReceiver() {
		return receiver;
	}
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStrTime() {
		return strTime;
	}
	public void setStrTime(String strTime) {
		this.strTime = strTime;
	}
	public String getSenderImageId() {
		return senderImageId;
	}
	public void setSenderImageId(String senderImageId) {
		this.senderImageId = senderImageId;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
}
