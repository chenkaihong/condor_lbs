package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午10:44:00
 * @Description 
 */
public class MyBottleReqMsg extends LBSRequestMessage {
	
	private int type;		// 0表示扔出去的瓶子，1表示捞到的瓶子
	private int pageNum;	// 查看第几页，从1开始
	
	public MyBottleReqMsg() {
		super(LBSMessageDefine.BOTTLE_MY_BOTTLE_REQUEST);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
}
