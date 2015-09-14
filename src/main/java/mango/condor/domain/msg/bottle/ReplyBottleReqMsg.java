package mango.condor.domain.msg.bottle;

import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * 李兴
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 李兴
* @Date 2014年2月12日 上午11:33:47
* @Description
 */
public class ReplyBottleReqMsg extends LBSRequestMessage {
	private int rewardId;		// 奖励Id (扔和捞的奖励一样)
	private long bottleId;		// 回复的瓶子Id
	private String replyText;	// 回复-文字内容或语音
	
	public ReplyBottleReqMsg() {
		super(LBSMessageDefine.BOTTLE_REPLY);
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public long getBottleId() {
		return bottleId;
	}

	public void setBottleId(long bottleId) {
		this.bottleId = bottleId;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}
	
}
