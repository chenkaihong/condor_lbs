package mango.condor.domain.msg;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程  
 * @Date 2013-7-16 下午2:20:40
 * @Description 
 */
public class LBSResponseMessage extends LBSMessage {
	protected boolean isSuc;	// 是否成功执行
	protected String errMsg;	// 错误字符串，客户端会弹窗（toast）
	protected boolean isMaintance;	// 当前是否在维护状态，合服时为true
	
	public LBSResponseMessage() {
		super();
	}
	
	public LBSResponseMessage(LBSRequestMessage req) {
		super(req.getMessageId());
		this.serverId = req.getServerId();
		this.playerId = req.getPlayerId();
		this.isSuc = false;
		this.isMaintance = false;
	}
	
	public boolean isSuc() {
		return isSuc;
	}

	public void setSuc(boolean isSuc) {
		this.isSuc = isSuc;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public boolean isMaintance() {
		return isMaintance;
	}

	public void setMaintance(boolean isMaintance) {
		this.isMaintance = isMaintance;
	}

}
