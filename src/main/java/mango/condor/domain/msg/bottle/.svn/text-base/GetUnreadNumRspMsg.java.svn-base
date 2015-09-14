package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-29 上午9:57:16
 * @Description 
 */
public class GetUnreadNumRspMsg extends LBSResponseMessage {
	
	private int throwUnreadNum;	// 扔出去的未读数量
	private int fetchUnreadNum;	// 捞的未读数量
	
	public GetUnreadNumRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public int getThrowUnreadNum() {
		return throwUnreadNum;
	}

	public void setThrowUnreadNum(int throwUnreadNum) {
		this.throwUnreadNum = throwUnreadNum;
	}

	public int getFetchUnreadNum() {
		return fetchUnreadNum;
	}

	public void setFetchUnreadNum(int fetchUnreadNum) {
		this.fetchUnreadNum = fetchUnreadNum;
	}
	
}
