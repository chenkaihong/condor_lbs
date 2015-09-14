package mango.condor.domain.msg.map;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 熊三山
 * @Date 2013-12-27 下午2:35:47
 * @Description
 */
public class MapRangeListMsgReq extends LBSRequestMessage {

	/**
	 * @param messageId
	 */
	public MapRangeListMsgReq() {
		super(LBSMessageDefine.MAP_RANGE_LIST_REQUEST);
	}

	private double lng; // 经度
	private double lat; // 纬度
	private String gender; // 按性别查询，M表示男性，W代表女性，A代表所有
	private boolean isOnline; // 玩家是否在线
	private boolean isLocal; // 是否搜索本服中的玩家，true代表本服，false代表全服
	private boolean isAvatar; // 搜索的玩家是否有自定义头像
	private boolean isVoice; // 搜索的玩家是否有语音
	
	private double minLat; // 范围搜索最小纬度
	private double maxLat; // 范围搜索最大纬度
	private double minLng; // 范围搜索最小经度
	private double maxLng; // 范围搜索最大经度

	public double getMinLat() {
		return minLat;
	}

	public void setMinLat(double minLat) {
		this.minLat = minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(double maxLat) {
		this.maxLat = maxLat;
	}

	public double getMaxLng() {
		return maxLng;
	}

	public void setMaxLng(double maxLng) {
		this.maxLng = maxLng;
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

	public double getMinLng() {
		return minLng;
	}

	public void setMinLng(double minLng) {
		this.minLng = minLng;
	}

}
