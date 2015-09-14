package mango.condor.domain.msg.map;

import java.util.List;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 熊三山  
 * @Date 2013-12-30 下午2:08:23
 * @Description 
 */
public class MapRangeListMsgRsp  extends LBSResponseMessage{

	public MapRangeListMsgRsp(LBSRequestMessage req) {
		super(req);
	}

	private List<PlayerInfo> pList;

	public List<PlayerInfo> getpList() {
		return pList;
	}

	public void setpList(List<PlayerInfo> pList) {
		this.pList = pList;
	}
}
