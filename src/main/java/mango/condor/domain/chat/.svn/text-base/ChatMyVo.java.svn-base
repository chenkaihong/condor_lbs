package mango.condor.domain.chat;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gzyouai.hummingbird.common.dao.DaoRecord;

import mango.condor.toolkit.CommonToolkit;

/**
 * 
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 邢陈程
* @Date 2013-7-11 下午8:31:09
* @Description
 */

public class ChatMyVo implements DaoRecord {
	private String chatId;
	private int chater1;
	private int chater2;
	private int chater1ServerId;
	private int chater2ServerId;
	private String lastMessage;
	private long lastTime;
	private int unReadNumChater1;
	private int unReadNumChater2;

	private long readedTimestampChater1;
	private long readedTimestampChater2;

	private byte chater1State;// chater1删除状态
	private byte chater2State;// chater2删除状态
	private long delTimestampChater1; // chater1删除时间
	private long delTimestampChater2; // chater2删除时间
	
	private long lastReadedChater1;
	private long lastReadedChater2;
	
	@Override
	public void parseResultSet(ResultSet rs) throws SQLException {
		this.setChatId(rs.getString("chatId"));
		this.setChater1(rs.getInt("chater1"));
		this.setChater2(rs.getInt("chater2"));
		this.setChater1ServerId(rs.getInt("chater1ServerId"));
		this.setChater2ServerId(rs.getInt("chater2ServerId"));
		this.setLastMessage(rs.getString("lastMessage"));
		this.setLastTime(rs.getTimestamp("lastTime").getTime());
		this.setUnReadNumChater1(rs.getInt("unReadNumChater1"));
		this.setUnReadNumChater2(rs.getInt("unReadNumChater2"));
		this.setReadedTimestampChater1(rs.getTimestamp("readedChater1").getTime());
		this.setReadedTimestampChater2(rs.getTimestamp("readedChater2").getTime());
		this.setChater1State(rs.getByte("chater1State"));
		this.setChater2State(rs.getByte("chater2State"));
		this.setDelTimestampChater1(rs.getTimestamp("delChater1").getTime());
		this.setDelTimestampChater2(rs.getTimestamp("delChater2").getTime());
		this.setLastReadedChater1(rs.getTimestamp("lastReadedChater1").getTime());
		this.setLastReadedChater2(rs.getTimestamp("lastReadedChater2").getTime());
	}
	
	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public int getChater1() {
		return chater1;
	}

	public void setChater1(int chater1) {
		this.chater1 = chater1;
	}

	public int getChater2() {
		return chater2;
	}

	public void setChater2(int chater2) {
		this.chater2 = chater2;
	}

	public int getChater1ServerId() {
		return chater1ServerId;
	}

	public void setChater1ServerId(int chater1ServerId) {
		this.chater1ServerId = chater1ServerId;
	}

	public int getChater2ServerId() {
		return chater2ServerId;
	}

	public void setChater2ServerId(int chater2ServerId) {
		this.chater2ServerId = chater2ServerId;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public int getUnReadNumChater1() {
		return unReadNumChater1;
	}

	public void setUnReadNumChater1(int unReadNumChater1) {
		this.unReadNumChater1 = unReadNumChater1;
	}

	public int getUnReadNumChater2() {
		return unReadNumChater2;
	}

	public void setUnReadNumChater2(int unReadNumChater2) {
		this.unReadNumChater2 = unReadNumChater2;
	}

	public long getLastTime() {
		return this.lastTime;
	}
	
	public String getLastTimeReadable() {
		return CommonToolkit.getFormatDateString(this.lastTime);
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public long getReadedTimestampChater1() {
		return readedTimestampChater1;
	}

	public void setReadedTimestampChater1(long readedTimestampChater1) {
		this.readedTimestampChater1 = readedTimestampChater1;
	}

	public long getReadedTimestampChater2() {
		return readedTimestampChater2;
	}

	public void setReadedTimestampChater2(long readedTimestampChater2) {
		this.readedTimestampChater2 = readedTimestampChater2;
	}

	public byte getChater1State() {
		return chater1State;
	}

	public void setChater1State(byte chater1State) {
		this.chater1State = chater1State;
	}

	public byte getChater2State() {
		return chater2State;
	}

	public void setChater2State(byte chater2State) {
		this.chater2State = chater2State;
	}

	public long getDelTimestampChater1() {
		return delTimestampChater1;
	}

	public void setDelTimestampChater1(long delTimestampChater1) {
		this.delTimestampChater1 = delTimestampChater1;
	}

	public long getDelTimestampChater2() {
		return delTimestampChater2;
	}

	public void setDelTimestampChater2(long delTimestampChater2) {
		this.delTimestampChater2 = delTimestampChater2;
	}

	public long getLastReadedChater1() {
		return lastReadedChater1;
	}

	public void setLastReadedChater1(long lastReadedChater1) {
		this.lastReadedChater1 = lastReadedChater1;
	}

	public long getLastReadedChater2() {
		return lastReadedChater2;
	}

	public void setLastReadedChater2(long lastReadedChater2) {
		this.lastReadedChater2 = lastReadedChater2;
	}

}
