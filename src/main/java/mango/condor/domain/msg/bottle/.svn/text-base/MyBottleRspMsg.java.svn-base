package mango.condor.domain.msg.bottle;

import java.util.List;

import mango.condor.domain.bottle.BottleListItem;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午10:44:15
 * @Description 
 */
public class MyBottleRspMsg extends LBSResponseMessage {
	private int pageTotal;
	private List<BottleListItem> list; 	
	private boolean notExistUser;		// 该用户不存在
	
	public MyBottleRspMsg(LBSRequestMessage req) {
		super(req);
	}

	public List<BottleListItem> getList() {
		return list;
	}

	public void setList(List<BottleListItem> list) {
		this.list = list;
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}

	public boolean isNotExistUser() {
		return notExistUser;
	}

	public void setNotExistUser(boolean notExistUser) {
		this.notExistUser = notExistUser;
	}
	
}
