package mango.condor.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.domain.msg.CommonRespMsg;
import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.domain.msg.LBSRequestMessage;
import mango.condor.domain.msg.LBSResponseMessage;
import mango.condor.domain.msg.bottle.DeleteBottleReqMsg;
import mango.condor.domain.msg.bottle.FetchBottleReqMsg;
import mango.condor.domain.msg.bottle.GetBottleContentReqMsg;
import mango.condor.domain.msg.bottle.GetReplyRewardReqMsg;
import mango.condor.domain.msg.bottle.GetUnreadNumReqMsg;
import mango.condor.domain.msg.bottle.MyBottleReqMsg;
import mango.condor.domain.msg.bottle.ReplyBottleReqMsg;
import mango.condor.domain.msg.bottle.SenderHarvestRewardReqMsg;
import mango.condor.domain.msg.bottle.ThrowBottleReqMsg;
import mango.condor.domain.msg.bottle.ViewBottleContentReqMsg;
import mango.condor.domain.msg.chat.ChatDeleteMyReqMsg;
import mango.condor.domain.msg.chat.ChatHistoryReqMsg;
import mango.condor.domain.msg.chat.ChatListMyContentReqMsg;
import mango.condor.domain.msg.chat.ChatListMyReqMsg;
import mango.condor.domain.msg.chat.ChatSendMyReqMsg;
import mango.condor.domain.msg.chat.DeleteChatReqMsg;
import mango.condor.domain.msg.chat.GetBlackListReqMsg;
import mango.condor.domain.msg.chat.InitChatCacheOnLoginReqMsg;
import mango.condor.domain.msg.chat.InsertBlackListReqMsg;
import mango.condor.domain.msg.chat.RemoveBlackListReqMsg;
import mango.condor.domain.msg.chat.SearchPlayerReqMsg;
import mango.condor.domain.msg.lbs.CachePlayerInfoReqMsg;
import mango.condor.domain.msg.lbs.ClearFollowMsgReqMsg;
import mango.condor.domain.msg.lbs.DeleteFollowListReqMsg;
import mango.condor.domain.msg.lbs.GetFanListReqMsg;
import mango.condor.domain.msg.lbs.GetFollowListReqMsg;
import mango.condor.domain.msg.lbs.GetFollowMsgReqMsg;
import mango.condor.domain.msg.lbs.GetFollowRankReqMsg;
import mango.condor.domain.msg.lbs.GetNeighbourReqMsg;
import mango.condor.domain.msg.lbs.GetNewChatNumReqMsg;
import mango.condor.domain.msg.lbs.GetPlayerInfoReqMsg;
import mango.condor.domain.msg.lbs.IndexReqMsg;
import mango.condor.domain.msg.lbs.InsertFollowListReqMsg;
import mango.condor.domain.msg.map.MapRangeListMsgReq;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.service.BottleService;
import mango.condor.service.ChatService;
import mango.condor.service.LBSService;
import mango.condor.service.MapService;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.MultiLanguageManager;

import com.google.gson.Gson;
import com.gzyouai.hummingbird.common.utils.DateTimeUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-13 下午2:48:14
 * @Description 
 */
public class LBSServlet extends HttpServlet {

	private static final long serialVersionUID = 4952585672616829406L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		resp.setCharacterEncoding(Const.CHARSET);
		resp.setContentType("text/html;charset=" + Const.CHARSET);
		
		String jsonData = null;
		Short msgId = null;
		String fromGS = null;
		String ip = null;
		LBSRequestMessage msg = null;
		CommonRespMsg commonResp = null;
		Gson gson = CommonToolkit.getGson();
		
		final long t = System.currentTimeMillis();
		
		try {
			jsonData = (String) req.getAttribute("data");
			msgId = Short.parseShort(req.getParameter("msg_id"));
			fromGS = req.getParameter("fromGS");			
			
			Class<? extends LBSRequestMessage> msgClass = LBSMessageDefine.getMsgClass(msgId);
			msg = gson.fromJson(jsonData, msgClass);
			
			// 如果服务端正在维护
			if (CommonToolkit.getIsMaintance().get()) {
				if (msg != null) {
					commonResp = new CommonRespMsg(msg);
				} else {
					commonResp = new CommonRespMsg();
				}

				commonResp.setSuc(true);
				commonResp.setMaintance(true);
				commonResp.setErrMsg(MultiLanguageManager.getString(msg.getLang(), MultiLangConstant.SYS_MAINTANCE_MSG));
				resp.getWriter().print(gson.toJson(commonResp));
				return;
			}
			
//			final int playerId = msg.getPlayerId();
			final long nowMillis = System.currentTimeMillis();
//			if (!UserActionMonitorService.safePass(playerId, msgId, nowMillis)) {
//				commonResp = new CommonRespMsg(msg);
//
//				commonResp.setSuc(false);
//				commonResp.setErrMsg(MultiLanguageManager.getString(msg.getLang(), MultiLangConstant.SERVER_BUSY));
//				resp.getWriter().print(gson.toJson(commonResp));
//				return;
//			}
			
			LBSResponseMessage response = null;
			switch (msgId) {
			case LBSMessageDefine.CHAT_INIT_CHAT_CACHE_REQUEST:
				response = ChatService.processInitChatCache((InitChatCacheOnLoginReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_SEND_MY_REQUEST:
				response = ChatService.processChatSendMy((ChatSendMyReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_LIST_MY_REQUEST:
				response = ChatService.processChatListMy((ChatListMyReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_LIST_CONTENT_REQUEST:
				response = ChatService.processChatListContentMy((ChatListMyContentReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_DELETE_REQUEST:
				response = ChatService.processChatDeleteMy((ChatDeleteMyReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_GET_HISTORY_CONTENT_REQUEST:
				response = ChatService.processGetMyChatHistory((ChatHistoryReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_INSERT_BLACKLIST_REQUEST:
				response = ChatService.processInsertBlackList((InsertBlackListReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_GET_BLACKLIST_REQUEST:
				response = ChatService.processGetBlackList((GetBlackListReqMsg) msg);
				break;
			case LBSMessageDefine.CHAT_REMOVE_BLACKLIST_REQUEST:
				response = ChatService.processRemoveBlackList((RemoveBlackListReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_CACHE_PLAYER_INFO:
				ip = req.getParameter("ip");
				response = LBSService.processCachePlayerInfo((CachePlayerInfoReqMsg) msg, ip);
				break;
			case LBSMessageDefine.LBS_GET_NEIGHBOUR:
				response = LBSService.processGetNeighbourList((GetNeighbourReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_GET_FOLLOWLIST_REQUEST:
				response = LBSService.processGetFollowList((GetFollowListReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_INSERT_FOLLOWLIST_REQUEST:
				response = LBSService.processInsertFollowList((InsertFollowListReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_DELETE_FOLLOWLIST_REQUEST:
				response = LBSService.processDeleteFollowList((DeleteFollowListReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_INDEX_REQUEST:
				response = LBSService.processIndex((IndexReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_GET_FAN_LIST_REQUEST:
				response = LBSService.processGetFanList((GetFanListReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_GET_PLAYER_INFO_REQUEST:
				response = LBSService.processGetPlayerInfo((GetPlayerInfoReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_GET_FOLLOW_RANK_REQUEST:
				response = LBSService.processFollowRank((GetFollowRankReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_SEARCH_PLAYER_REQUEST:
				response = LBSService.processSearchPlayer((SearchPlayerReqMsg) msg);
				break;
			// 以下为漂流瓶相关请求
			case LBSMessageDefine.BOTTLE_GET_UNREAD_NUM_REQUEST:
				response = BottleService.getUnreadBottleNum((GetUnreadNumReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_THROW_REQUEST:
				response = BottleService.processThrowBottle((ThrowBottleReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_FETCH_REQUEST:
				response = BottleService.processFetchBottle((FetchBottleReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_MY_BOTTLE_REQUEST:
				response = BottleService.getMyBottle((MyBottleReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_GET_BOTTLE_CONTENT_REQUEST:
				response = BottleService.processGetBottleContent((GetBottleContentReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_GET_REPLY_REWARD_REQUEST:
				response = BottleService.processGetReplyReward((GetReplyRewardReqMsg) msg);
				break;
			case LBSMessageDefine.BOTTLE_DELETE_BOTTLE_REQUEST:
				response = BottleService.processDeleteBottle((DeleteBottleReqMsg) msg);
				break;
			case LBSMessageDefine.GET_NEW_CHAT_NUM_REQUEST:
				response = LBSService.getNewChatNum((GetNewChatNumReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_GET_FOLLOW_MSG_LIST_REQUEST:
				response = LBSService.processGetFollowMsgList((GetFollowMsgReqMsg) msg);
				break;
			case LBSMessageDefine.LBS_CLEAR_FOLLOW_MSG_REQUEST:
				response = LBSService.processClearFollowMsg((ClearFollowMsgReqMsg) msg);
				break;
				
			case LBSMessageDefine.MAP_RANGE_LIST_REQUEST:
				response = MapService.rangeList((MapRangeListMsgReq)msg);
				break;	
				
			case LBSMessageDefine.BOTTLE_REPLY:
				response = BottleService.replyBottle((ReplyBottleReqMsg)msg);
				break;
			case LBSMessageDefine.BOTTLE_SENDER_HARVEST_REWARD:
				response = BottleService.senderHarvestReward((SenderHarvestRewardReqMsg)msg); 
				break;
			case LBSMessageDefine.BOTTLE_VIEW_CONTENT:
				response = BottleService.viewBottleContent((ViewBottleContentReqMsg)msg);
				break;
				
			case LBSMessageDefine.CHAT_DELETE_CHAT:
				response = ChatService.deleteChat((DeleteChatReqMsg)msg);
				break;
			default:
				System.out.println( "@@: not config. msgId=" + msgId );
			}
			
			String jsonResponse = gson.toJson(response);
			
			boolean DEBUG = true;
			if (DEBUG) {
				long millis = System.currentTimeMillis() - nowMillis;
				int bytes = jsonResponse.getBytes("UTF-8").length;
				int kbs = bytes / 1024;
				System.out.println( "stat: " + DateTimeUtil.formatDateTime(new Date(), "yyyy-MM-dd.HH:mm:ss") 
						+ "," + msg.getPlayerId() 
						+ "," + msg.getServerId() 
						+ "," + msg.getMessageId() 
						+ "," + fromGS
						+ "," + msg.isNotLessThanVersion_2_5()
						+ "," + bytes
						+ "," + kbs
						+ ",millis=" + millis);
			}

			// 如果不是游戏服发过来的请求，响应使用gzip压缩
			if (null == fromGS || false == fromGS.equals("1")) {
				outputGZip(resp, jsonResponse);
			} else {
				resp.getWriter().print(jsonResponse);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			if (null != jsonData) {
				sb.append("jsonData:" + jsonData);
			}
			
			if (null != msgId) {
				sb.append("msgId:" + msgId);
			}
			
			// 处理异常
			e.printStackTrace();
			
			if (msg != null) {
				commonResp = new CommonRespMsg(msg);
			} else {
				commonResp = new CommonRespMsg();
			}

			commonResp.setSuc(false);
			commonResp.setErrMsg(MultiLanguageManager.getString(msg.getLang(), MultiLangConstant.SYS_SERVER_ERROR));
			resp.getWriter().print(gson.toJson(commonResp));
		}
		finally {
			long now = System.currentTimeMillis();
			long timeDiff = now - t;
			if (timeDiff > 500L) {
				System.out.println( "TIMES.LBSServlet long time request. msg=" + msgId + ", fromGS=" + fromGS + ", ip=" + ip + ", timeDiff=" + timeDiff);
			}
		}
	}
	
	private static void outputGZip(HttpServletResponse resp, String content) {
		OutputStream o = null;
		GZIPOutputStream gz = null;
		
		try {
			resp.setHeader("Content-Encoding", "gzip");
			o = resp.getOutputStream();
			gz = new GZIPOutputStream(o);
			gz.write(content.getBytes(Const.CHARSET));
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_GZIP, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != gz) {
				try {
					gz.close();
				} catch (IOException e) {
					String msg = e.getMessage();
					if (msg == null || msg.indexOf("Broken pipe") < 0) {
						e.printStackTrace();
					}
				}
			}
			
			if (null != o) {
				try {
					o.close();
				} catch (IOException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_GZIP, e, null, null);
					e.printStackTrace();
				}
			}
		}
	}
	
}
