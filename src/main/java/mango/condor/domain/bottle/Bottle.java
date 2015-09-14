package mango.condor.domain.bottle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.gzyouai.hummingbird.common.dao.BaseDao;
import com.gzyouai.hummingbird.common.dao.DaoRecord;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午10:09:58
 * @Description
 */
public class Bottle implements DaoRecord {
	private long id;
	private String sender;
	private String receiver;
	private String text;
	private String vid;
	private long sendTime;
	private String replyText;
	private long replyTime;
	private int rewardId;
	private boolean senderRewarded;
	private boolean receiverRewarded;
	private boolean senderReaded;
	private boolean receiverReaded;
	private boolean senderDeleted;
	private boolean receiverDeleted;

	@Override
	public void parseResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
        this.setSender(rs.getString("sender"));
        this.setReceiver(rs.getString("receiver"));
        this.setText(rs.getString("text"));
        this.setVid(rs.getString("vid"));
        this.setSendTime( BaseDao.parseTimestampToLong(rs, "sendTime") );
        this.setReplyText(rs.getString("replyText"));
        this.setReplyTime( BaseDao.parseTimestampToLong(rs, "replyTime") );
        this.setRewardId(rs.getInt("rewardId"));
		this.setSenderRewarded(rs.getBoolean("senderRewarded"));
		this.setReceiverRewarded(rs.getBoolean("receiverRewarded"));
		this.setSenderReaded(rs.getBoolean("senderReaded"));
		this.setReceiverReaded(rs.getBoolean("receiverReaded"));
		this.setSenderDeleted(rs.getBoolean("senderDeleted"));
		this.setReceiverDeleted(rs.getBoolean("receiverDeleted"));
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public boolean isSenderRewarded() {
		return senderRewarded;
	}

	public void setSenderRewarded(boolean senderRewarded) {
		this.senderRewarded = senderRewarded;
	}

	public boolean isReceiverRewarded() {
		return receiverRewarded;
	}

	public void setReceiverRewarded(boolean receiverRewarded) {
		this.receiverRewarded = receiverRewarded;
	}

	public boolean isSenderReaded() {
		return senderReaded;
	}

	public void setSenderReaded(boolean senderReaded) {
		this.senderReaded = senderReaded;
	}

	public boolean isReceiverReaded() {
		return receiverReaded;
	}

	public void setReceiverReaded(boolean receiverReaded) {
		this.receiverReaded = receiverReaded;
	}

	public boolean isSenderDeleted() {
		return senderDeleted;
	}

	public void setSenderDeleted(boolean senderDeleted) {
		this.senderDeleted = senderDeleted;
	}

	public boolean isReceiverDeleted() {
		return receiverDeleted;
	}

	public void setReceiverDeleted(boolean receiverDeleted) {
		this.receiverDeleted = receiverDeleted;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}

	public long getReplyTime() {
		return replyTime;
	}

	public void setReplyTime(long replyTime) {
		this.replyTime = replyTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Bottle [id=");
		builder.append(id);
		builder.append(", sender=");
		builder.append(sender);
		builder.append(", receiver=");
		builder.append(receiver);
		builder.append(", text=");
		builder.append(text);
		builder.append(", vid=");
		builder.append(vid);
		builder.append(", sendTime=");
		builder.append(sendTime);
		builder.append(", replyText=");
		builder.append(replyText);
		builder.append(", replyTime=");
		builder.append(replyTime);
		builder.append(", rewardId=");
		builder.append(rewardId);
		builder.append(", senderRewarded=");
		builder.append(senderRewarded);
		builder.append(", receiverRewarded=");
		builder.append(receiverRewarded);
		builder.append(", senderReaded=");
		builder.append(senderReaded);
		builder.append(", receiverReaded=");
		builder.append(receiverReaded);
		builder.append(", senderDeleted=");
		builder.append(senderDeleted);
		builder.append(", receiverDeleted=");
		builder.append(receiverDeleted);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getSender()=");
		builder.append(getSender());
		builder.append(", getReceiver()=");
		builder.append(getReceiver());
		builder.append(", getText()=");
		builder.append(getText());
		builder.append(", getVid()=");
		builder.append(getVid());
		builder.append(", getSendTime()=");
		builder.append(getSendTime());
		builder.append(", getRewardId()=");
		builder.append(getRewardId());
		builder.append(", isSenderRewarded()=");
		builder.append(isSenderRewarded());
		builder.append(", isReceiverRewarded()=");
		builder.append(isReceiverRewarded());
		builder.append(", isSenderReaded()=");
		builder.append(isSenderReaded());
		builder.append(", isReceiverReaded()=");
		builder.append(isReceiverReaded());
		builder.append(", isSenderDeleted()=");
		builder.append(isSenderDeleted());
		builder.append(", isReceiverDeleted()=");
		builder.append(isReceiverDeleted());
		builder.append(", getReplyText()=");
		builder.append(getReplyText());
		builder.append(", getReplyTime()=");
		builder.append(getReplyTime());
		builder.append(", getClass()=");
		builder.append(getClass());
		builder.append(", hashCode()=");
		builder.append(hashCode());
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}
