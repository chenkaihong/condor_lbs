package mango.condor.util;

import mango.condor.domain.msg.CommonRespMsg;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.toolkit.MultiLanguageManager;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月12日 上午11:49:29
 * @Description 
 */
public class RspMsgUtil {
	/**
	 * 生成一个 CommonRespMsg
	 * @param req
	 * @param errKey
	 * @return
	 */
	public static CommonRespMsg createCommonErrorRespMsg (LBSRequestMessage req, String errKey) {
		String errMsg = MultiLanguageManager.getString(req.getLang(), errKey);
		
		CommonRespMsg rsp = new CommonRespMsg(req);
		rsp.setSuc(false);
		rsp.setErrMsg(errMsg);
		return rsp;
	}
	
	/**
	 * 填充默认的返回结果消息
	 * @param rsp
	 * @param success
	 */
	public static void fillDefaultMsg (LBSResponseMessage rsp, boolean success) {
		rsp.setSuc(success);
		
		if (!success) {
			rsp.setErrMsg(MultiLangConstant.SYS_SERVER_ERROR);
		}
	}
}
