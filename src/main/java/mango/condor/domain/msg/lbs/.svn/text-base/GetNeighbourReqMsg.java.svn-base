package mango.condor.domain.msg.lbs;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-13 下午3:09:57
 * @Description 
 */
public class GetNeighbourReqMsg extends LBSRequestMessage {
	
	private double lng; 		// 经度
	private double lat;			// 纬度
	private boolean isMore;		// 此次请求是否是点击"更多"按钮产生的
	private String gender;		// 按性别查询，M表示男性，W代表女性，A代表所有
	private boolean isOnline;	// 玩家是否在线
	private boolean isLocal;    // 是否搜索本服中的玩家，true代表本服，false代表全服
	private boolean isAvatar;	// 搜索的玩家是否有自定义头像
	private boolean isVoice;	// 搜索的玩家是否有语音
	
	public GetNeighbourReqMsg() {
		super(LBSMessageDefine.LBS_GET_NEIGHBOUR);
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public boolean isMore() {
		return isMore;
	}

	public void setMore(boolean isMore) {
		this.isMore = isMore;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public boolean isAvatar() {
		return isAvatar;
	}

	public void setAvatar(boolean isAvatar) {
		this.isAvatar = isAvatar;
	}

	public boolean isVoice() {
		return isVoice;
	}

	public void setVoice(boolean isVoice) {
		this.isVoice = isVoice;
	}
}

