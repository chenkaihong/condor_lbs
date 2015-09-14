package mango.condor.domain.lbs;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-3 上午11:02:48
 * @Description
 */

public class BMapReverseGeoResp {
	private int status;
	private Result result;
	
	public String toAddress() {
		return result.addressComponent.province + result.addressComponent.city;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}

class Result {
	AddressComponent addressComponent;
}

class AddressComponent {
	String city;
	String province;
}
