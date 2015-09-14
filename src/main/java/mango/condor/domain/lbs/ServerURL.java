package mango.condor.domain.lbs;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-26 上午11:30:58
 * @Description
 */
public class ServerURL {
	private int serverId;
	private String url;
	private String name;

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
