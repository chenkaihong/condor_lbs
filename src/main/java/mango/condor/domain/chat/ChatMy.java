package mango.condor.domain.chat;

/**
 * 
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 邢陈程
* @Date 2013-7-11 上午11:57:21
* @Description
 */

public class ChatMy {
	private int myId;
	private int hisId;
	private String lastMessage;
	private String lastTime;
	private volatile long longLastTime;		// 字段lastTime的 long 类型，仅仅为了排序 
	private int unReadNum;
	private String chaterName;
	private int chaterLevel;
	private String chaterImageId;
	private boolean gender;
	private int myServerId;
	private int hisServerId;

	public int getMyId() {
		return myId;
	}

	public void setMyId(int myId) {
		this.myId = myId;
	}

	public int getHisServerId() {
		return hisServerId;
	}

	public void setHisServerId(int hisServerId) {
		this.hisServerId = hisServerId;
	}

	public int getHisId() {
		return hisId;
	}

	public void setHisId(int hisId) {
		this.hisId = hisId;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public int getUnReadNum() {
		return unReadNum;
	}

	public void setUnReadNum(int unReadNum) {
		this.unReadNum = unReadNum;
	}

	public String getChaterName() {
		return chaterName;
	}

	public void setChaterName(String chaterName) {
		this.chaterName = chaterName;
	}

	public int getChaterLevel() {
		return chaterLevel;
	}

	public void setChaterLevel(int chaterLevel) {
		this.chaterLevel = chaterLevel;
	}

	public String getChaterImageId() {
		return chaterImageId;
	}

	public void setChaterImageId(String chaterImageId) {
		this.chaterImageId = chaterImageId;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

	public int getMyServerId() {
		return myServerId;
	}

	public void setMyServerId(int myServerId) {
		this.myServerId = myServerId;
	}

	public long getLongLastTime() {
		return longLastTime;
	}

	public void setLongLastTime(long longLastTime) {
		this.longLastTime = longLastTime;
	}

}
