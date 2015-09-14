package mango.condor.domain.msg.lbs;

import java.util.List;

import mango.condor.domain.lbs.FollowRank;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-25 下午2:43:55
 * @Description 
 */
public class GetFollowRankRspMsg extends LBSResponseMessage {
	
	private List<FollowRank> list;	// 排行列表
	
	// 自己的排名，如果没有为null，则代表在1000名之外
	private String rank;
	
	// 自己的粉丝数量
	private int fanNum;
	
	public GetFollowRankRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public List<FollowRank> getList() {
		return list;
	}

	public void setList(List<FollowRank> list) {
		this.list = list;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public int getFanNum() {
		return fanNum;
	}

	public void setFanNum(int fanNum) {
		this.fanNum = fanNum;
	}
	
}
