package mango.condor.domain.msg.chat;

import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;

public class DeleteChatRspMsg extends LBSResponseMessage {
	
	public DeleteChatRspMsg(LBSRequestMessage req) {
		super(req);
	}
	
}
