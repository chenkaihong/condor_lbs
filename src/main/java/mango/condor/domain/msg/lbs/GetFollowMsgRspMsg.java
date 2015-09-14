package mango.condor.domain.msg.lbs;

import java.util.List;

import mango.condor.domain.lbs.FollowMsg;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2013-7-16 上午10:12:39
 * @Description 
 */
public class GetFollowMsgRspMsg extends LBSResponseMessage {

	public GetFollowMsgRspMsg(LBSRequestMessage req) {
		super(req);
	}

	private List<FollowMsg> list;

	public List<FollowMsg> getList() {
		return list;
	}

	public void setList(List<FollowMsg> list) {
		this.list = list;
	}
	
}
