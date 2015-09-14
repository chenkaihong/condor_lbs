package mango.condor.domain.lbs;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-25 下午2:15:05
 * @Description
 */
public class FollowRank {
	private String uid;
	private int rank; 	// 排名
	private int fanNum; // 粉丝数
	
	private int playerId;
	private int serverId;
	private String name;
	private int level;
	private String imageId;
	private boolean gender;
	
	public void set(PlayerInfo pi) {
		this.playerId = pi.getPlayerId();
		this.serverId = pi.getServerId();
		this.name = pi.getName();
		this.level = pi.getLevel();
		this.imageId = pi.getImageId();
		this.gender = pi.isGender();
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getFanNum() {
		return fanNum;
	}

	public void setFanNum(int fanNum) {
		this.fanNum = fanNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
	}

}
