package mango.condor.domain.lbs;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-24 下午4:47:51
 * @Description
 */
public class BasePlayerInfo {
	private String name;
	private int level;
	private String imageId;
	private String voiceId;
	private boolean gender;
	private String sign;
	private int meili;
	private String gangName;	// 帮会名
	// private int maxAttack;
	// private int maxDefence;
	// private int minAttack;
	// private int minDefence;
	private String partnerName;	// 侠侣名
	// private int coupleLevel;
	private String teacherName; // 师傅名字
	// private int studentNum;  // 徒弟数量
	// private int studentLimit; // 最大徒弟数量
	
	private int power;  // 战斗力
	private double lat;
	private double lng;
	private String version;

	public BasePlayerInfo(PlayerInfo pi) {
		this.name = pi.getName();
		this.level = pi.getLevel();
		this.imageId = pi.getImageId();
		this.voiceId = pi.getVoiceId();
		this.gender = pi.isGender();
		this.sign = pi.getSign();
		this.meili = pi.getMeili();
		this.gangName = pi.getGangName();
		this.partnerName = pi.getPartnerName();
		this.teacherName = pi.getTeacherName();
		this.power = pi.getPower();
		this.lat = pi.getLat();
		this.lng = pi.getLng();
		this.version = pi.getVersion();
	}

	public void setPlayerInfo(PlayerInfo pi) {
		pi.setName(this.name);
		pi.setLevel(this.level);
		pi.setImageId(this.imageId);
		pi.setVoiceId(this.voiceId);
		pi.setGender(this.gender);
		pi.setSign(this.sign);
		pi.setMeili(this.meili);
		pi.setGangName(this.gangName);
		pi.setPartnerName(this.partnerName);
		pi.setTeacherName(this.teacherName);
		pi.setPower(this.power);
		pi.setLat(this.lat);
		pi.setLng(this.lng);
		pi.setVersion(this.version);
	}

	public String getName() {
		return name;
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

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
