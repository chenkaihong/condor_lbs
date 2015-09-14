package mango.condor.domain.lbs;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-8-15 下午12:56:28
 * @Description
 */
public class BMapIPGeoResp {
	private int status;
	private IPGeoResult content;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Coor getCoor() {
		return content.point;
	}
}

class IPGeoResult {
	Coor point;
}
