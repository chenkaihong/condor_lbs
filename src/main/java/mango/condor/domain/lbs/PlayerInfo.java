package mango.condor.domain.lbs;

import java.sql.ResultSet;
import java.sql.SQLException;

import mango.condor.toolkit.BMapToolkit;
import mango.condor.toolkit.CommonToolkit;

import com.gzyouai.hummingbird.common.dao.DaoRecord;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-2 上午11:14:00
 * @Description
 */
public class PlayerInfo implements Comparable<PlayerInfo>, DaoRecord {
	private int playerId;
	private int serverId;
	private String serverName;	// 服务器名
	private long updateTS;      // 缓存时间
	
	// 以下字段作为jsonData存到数据库，也是BasePlayerInfo的各字段
	private String name;
	private int level;
	private String imageId;
	private String voiceId;
	private boolean gender;
	private String sign;
	private int meili;
	private String gangName;	// 帮会名
	private String partnerName;
	private String teacherName; // 师傅名字
	
	private int power; 	// 战斗力
	private double lng;		   // 经度
	private double lat;		   // 纬度
	private String version;
	
// ================================================================================
	
	// 以下字段不存数据库
	private String addr;	    // 地址
	private double distance;    // 与玩家的距离，单位为米
	private boolean isFollow;   // 是否关注了对方
	private boolean isBlack;    // 对方是否在自己的黑名单中
	private long diffTS;  // 服务器当前与玩家上次刷新的时间差
	
	public static String trimPlayerName (String name) {
		if (name != null) {
			name = name.replace("\n", "").replace("\r", "").replace(" ", "").replace("　", "");
		}
		
		return name;
	}
	
	@Override
	public void parseResultSet(ResultSet rs) throws SQLException {
		this.setPlayerId(rs.getInt("playerId"));
		this.setServerId(rs.getInt("serverId"));
		this.setServerName(rs.getString("serverName"));
		this.setUpdateTS(rs.getTimestamp("updateTS").getTime());
		
		BasePlayerInfo baseInfo = CommonToolkit.getGson().fromJson(rs.getString("jsonData"), BasePlayerInfo.class);
		this.setName(baseInfo.getName());
		this.setLevel(baseInfo.getLevel());
		this.setImageId(baseInfo.getImageId());
		this.setVoiceId(baseInfo.getVoiceId());
		this.setGender(baseInfo.isGender());
		this.setSign(baseInfo.getSign());
		this.setMeili(baseInfo.getMeili());
		this.setGangName(baseInfo.getGangName());
		this.setPartnerName(baseInfo.getPartnerName());
		this.setTeacherName(baseInfo.getTeacherName());
		this.setPower(baseInfo.getPower());
		this.setLat(baseInfo.getLat());
		this.setLng(baseInfo.getLng());
		this.setVersion(baseInfo.getVersion());
	}
	
	/**
	 * 获取缩放过的lng For [附件的人]最小经纬度区域
	 * @return
	 */
	public double getScaleLngForGeohashMinPoint () {
		return BMapToolkit.scaleLatOrLngForGeohashMinPoint(lng);
	}
	
	/**
	 * 获取缩放过的lat For [附件的人]最小经纬度区域
	 * @return
	 */
	public double getScaleLatForGeohashMinPoint () {
		return BMapToolkit.scaleLatOrLngForGeohashMinPoint(lat);
	}
	
	
	/**
	 * 填充玩家地址
	 */
	public void fillAddr() {
		this.addr = BMapToolkit.getReverseGeo(this.lat, this.lng);
	}
	
	/**
	 * 设置是否在线
	 */
	public void calDiffTS() {
		this.diffTS = System.currentTimeMillis() - this.updateTS;
	}

	public String getUid() {
		return CommonToolkit.toUID(serverId, playerId);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getName() {
		return trimPlayerName(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getVoiceId() {
		return voiceId;
	}

	public void setVoiceId(String voiceId) {
		this.voiceId = voiceId;
	}

	public boolean isGender() {
		return gender;
	}

	public void setGender(boolean gender) {
		this.gender = gender;
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

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public long getUpdateTS() {
		return updateTS;
	}

	public void setUpdateTS(long updateTS) {
		this.updateTS = updateTS;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public int getMeili() {
		return meili;
	}

	public void setMeili(int meili) {
		this.meili = meili;
	}

	public String getGangName() {
		return gangName;
	}

	public void setGangName(String gangName) {
		this.gangName = gangName;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public boolean isFollow() {
		return isFollow;
	}

	public void setFollow(boolean isFollow) {
		this.isFollow = isFollow;
	}

	@Override
	public int compareTo(PlayerInfo o) {
		return Double.compare(this.getDistance(), o.getDistance());
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public void setBlack(boolean isBlack) {
		this.isBlack = isBlack;
	}

	public long getDiffTS() {
		return diffTS;
	}

	public void setDiffTS(long diffTS) {
		this.diffTS = diffTS;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
