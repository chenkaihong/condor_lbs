package mango.condor.domain.bottle;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-9-27 下午4:25:57
 * @Description
 */
public class BottleListItem {
	private long bottleId;			// 瓶子id
	private int serverId;			// 对方的服务器id
	private int playerId;			// 对方的playerId
	private String imageId;			// 对方的头像id
	private String name;			// 对方的名字
	private int level;				// 对方的等级
	private long sendTime;			// 发送时间
	private int rewardId;			// 奖励的id
	private boolean isReaded;		// 是否已读
	private boolean isRewarded;	    // 此字段只针对发送者有效，接收者只会出现<查看>按钮
									// 代表是否已领取回复奖励，如果为true则代表已经领取过回复奖励，出现<查看>按钮，否则出现<领奖>按钮

	private int distance;			// 与对方的距离，如果被捞到的话，单位：米

	public long getBottleId() {
		return bottleId;
	}

	public void setBottleId(long bottleId) {
		this.bottleId = bottleId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
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

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public boolean isRewarded() {
		return isRewarded;
	}

	public void setRewarded(boolean isRewarded) {
		this.isRewarded = isRewarded;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
