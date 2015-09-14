package mango.condor.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mango.condor.cache.CacheFactory;
import mango.condor.dao.DaoFactory;
import mango.condor.domain.chat.ChatContentMy;
import mango.condor.domain.chat.ChatContentMyVo;
import mango.condor.domain.chat.ChatMy;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.FollowMsg;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSResponseMessage;
import mango.condor.domain.msg.chat.ChatDeleteMyReqMsg;
import mango.condor.domain.msg.chat.ChatDeleteMyRspMsg;
import mango.condor.domain.msg.chat.ChatHistoryReqMsg;
import mango.condor.domain.msg.chat.ChatHistoryRspMsg;
import mango.condor.domain.msg.chat.ChatListMyContentReqMsg;
import mango.condor.domain.msg.chat.ChatListMyContentRspMsg;
import mango.condor.domain.msg.chat.ChatListMyReqMsg;
import mango.condor.domain.msg.chat.ChatListMyRspMsg;
import mango.condor.domain.msg.chat.ChatSendMyReqMsg;
import mango.condor.domain.msg.chat.ChatSendMyRspMsg;
import mango.condor.domain.msg.chat.DeleteChatReqMsg;
import mango.condor.domain.msg.chat.DeleteChatRspMsg;
import mango.condor.domain.msg.chat.GetBlackListReqMsg;
import mango.condor.domain.msg.chat.GetBlackListRspMsg;
import mango.condor.domain.msg.chat.InitChatCacheOnLoginReqMsg;
import mango.condor.domain.msg.chat.InitChatCacheOnLoginRspMsg;
import mango.condor.domain.msg.chat.InsertBlackListReqMsg;
import mango.condor.domain.msg.chat.InsertBlackListRspMsg;
import mango.condor.domain.msg.chat.RemoveBlackListReqMsg;
import mango.condor.domain.msg.chat.RemoveBlackListRspMsg;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.MultiLanguageManager;
import mango.condor.toolkit.RedisToolkit;

import com.gzyouai.hummingbird.common.component.DataPager;
import com.gzyouai.hummingbird.common.utils.CollectionUtil;
import com.gzyouai.hummingbird.common.utils.UIUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-10 下午4:33:06
 * @Description 
 */
public class ChatService {	
	/**
	 * 获取某玩家跨服私聊会话列表
	 * @param req
	 * @return
	 */
	public static final ChatListMyRspMsg processChatListMy(ChatListMyReqMsg req) {
		ChatListMyRspMsg resp = new ChatListMyRspMsg(req);
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		int pageSize = req.getPageSize();
		int pageNum = req.getPageNum();
		boolean fromTimer = req.isFromTimer();
		String unreadList = req.getUnreadList();
		
		if (pageNum < 1) {
			pageNum = 1;
		}
		if (pageSize <= 0) {
			pageSize = 20;
		}
		
		PlayerInfo playerInfo = StorageService.getPlayerInfo(serverId, playerId);
		if (null == playerInfo) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}

		List<ChatMy> myChatList = StorageService.getMyChatList(serverId, playerId);
		if (null != myChatList) {
			for (ChatMy chat : myChatList) {
				chat.setLastMessage( UIUtil.trim4PhoneUI(chat.getLastMessage()) );
			}
			
			boolean hasUnreadFollowMsg = false;	// 是否有未读的关注消息
			boolean needFollowMsg = req.getVersion() != null && req.getVersion().length() > 0;
			if (needFollowMsg) {
				// 关注消息
				ChatMy msgChat = null;
				int lang = req.getLang() ;		// 只取关注消息的数量
				List<FollowMsg> msgList = StorageService.readFollowMsgList(serverId, playerId, lang, false);
				if (msgList.size() > 0) {
					int unReadNum = 0;	// 未读关注消息数量
					for (FollowMsg msg : msgList) {
						if (!msg.isRead()) {
							unReadNum++;
						}
					}
					
					FollowMsg lastFollowMsg = msgList.get(msgList.size() - 1);
					
					msgChat = new ChatMy ();
					msgChat.setMyId(0);
					msgChat.setHisId(0);
					msgChat.setUnReadNum( unReadNum );
					msgChat.setLastTime( lastFollowMsg.getStrTime() );
					msgChat.setLongLastTime(lastFollowMsg.getLongStrTime());
					msgChat.setLastMessage( lastFollowMsg.getContent() );
					hasUnreadFollowMsg = unReadNum > 0;
				}
				
				if (msgChat != null && msgChat.getUnReadNum() <= 0) {
					// 没有未读的关注消息,就参与私信排序
					myChatList.add(msgChat);
				}
				
				Collections.sort(myChatList, new Comparator<ChatMy>() {
					@Override
					public int compare(ChatMy o1, ChatMy o2) {
						// 未读排前面，然后按照时间倒序
						if ((o1.getUnReadNum() > 0) == (o2.getUnReadNum() > 0)) {
							return (o2.getLongLastTime() > o1.getLongLastTime()) ? 1 : -1;
						}
						else {
							if (o1.getUnReadNum() > 0) {
								return -1;
							}
							
							return 1;
						}
					}
				});
				
				if (msgChat != null && msgChat.getUnReadNum() > 0) {
					// 有未读的关注消息, 直接插到第一位
					myChatList.add(0, msgChat);
				}
			}
			else {
				Collections.sort(myChatList, new Comparator<ChatMy>() {
					@Override
					public int compare(ChatMy o1, ChatMy o2) {
						// 未读排前面，然后按照时间倒序
						if ((o1.getUnReadNum() > 0) == (o2.getUnReadNum() > 0)) {
							return (o2.getLongLastTime() > o1.getLongLastTime()) ? 1 : -1;
						}
						else {
							if (o1.getUnReadNum() > 0) {
								return -1;
							}
							
							return 1;
						}
					}
				});
			}
			
			int chatTotal = myChatList.size();
			if (req.isNotLessThanVersion_2_5()) {
				if (fromTimer) {
					// 请求来自客户端定时请求
					if( isUnreadDataChange4ClientTimerRequest(unreadList, myChatList, hasUnreadFollowMsg) ) {
						resp.setRefresh(true);
						myChatList = DataPager.subListPager(myChatList, pageSize, pageNum);
					}
					else {
						// 未读数据未发生变化
						resp.setRefresh(false);
					}
				}
				else {
					resp.setRefresh(true);
					myChatList = DataPager.subListPager(myChatList, pageSize, pageNum);
				}
			}
			else {
				myChatList = CollectionUtil.subList(myChatList, 0, 30);
			}
			
			for (ChatMy chat : myChatList) {
				ImageWrapper.wrapImage(req, chat);
			}
			
			resp.setSuc(true);
			resp.setMyChatList(myChatList);
			resp.setChatTotal(chatTotal);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}

		return resp;
	}
	
	/**
	 * 未读数据是否发生了变化
	 * 		1) 针对客户端定时请求
	 * 		2) 未发生变化，就不要返回列表了
	 * 		
	 * @param unreadList
	 * @param myChatList
	 * @param hasUnreadFollowMsg
	 * @return
	 * 		true: 发生了变化
	 * 		false: 未发生变化
	 */
	private static boolean isUnreadDataChange4ClientTimerRequest(String unreadList, List<ChatMy> myChatList,
			boolean hasUnreadFollowMsg) {
		final int FOLLOW_MSG_PLAYERID = 0;
		
		// 服务端未[未读的数据信息]
		Set<Integer> serverUnreadChatIdSet = new HashSet<Integer>();	// 未读私信中对方的playerId
		// 服务端未[未读的数据信息]--未读关注消息
		if (hasUnreadFollowMsg) {
			serverUnreadChatIdSet.add(FOLLOW_MSG_PLAYERID);
		}
		// 服务端未[未读的数据信息]--未读私信
		for (ChatMy chat : myChatList) {
			if (chat.getUnReadNum() > 0) {
				serverUnreadChatIdSet.add(chat.getHisId());
			}
		}
					
		// 客户端[未读的数据信息]
		List<Integer> clientUnreadChatIdList = new ArrayList<Integer>();
		if (null != unreadList && !unreadList.isEmpty()) {
			String[] arr = unreadList.split(",");
			for (int i = 0, size = arr.length; i < size; i++) {
				clientUnreadChatIdList.add( Integer.parseInt(arr[0]) );
			}	
		}
		
		// 比较服务器端和客户端的[未读的数据信息]
		if (clientUnreadChatIdList.size() != serverUnreadChatIdSet.size()) {
			return true;
		}
		
		Iterator<Integer> it = clientUnreadChatIdList.iterator();
		while (it.hasNext()) {
			int chatId = it.next();
			if (serverUnreadChatIdSet.contains(chatId)) {
				serverUnreadChatIdSet.remove(Integer.valueOf(chatId));
				it.remove();
			}
		}
		
		return !serverUnreadChatIdSet.isEmpty() || clientUnreadChatIdList.isEmpty();
	}
	
	/**
	 * 发送私聊
	 * @param req
	 * @return
	 */
	public static final ChatSendMyRspMsg processChatSendMy(ChatSendMyReqMsg req) {
		
		int receiver = req.getReceiver();
		int receiverServerId = req.getReceiverServerId();
		int sender = req.getPlayerId();
		int senderServerId = req.getServerId();
		
		ChatSendMyRspMsg resp = new ChatSendMyRspMsg(req);
		
		if (null == StorageService.getPlayerInfo(senderServerId, sender)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		// 不能跟自己聊天
		if (senderServerId == receiverServerId && sender == receiver) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
			return resp;
		}

		// 不能给机器人发送私聊
		if (receiverServerId == Const.ROBOT_SYSTEM_ID) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
			return resp;
		}
		
		// 数据是否为空
		String content = req.getContent();
		if (content == null) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_INPUT));
			return resp;
		}
		
		// 黑名单判断
		int blackListStatus = StorageService.isBlackList(senderServerId, sender, receiverServerId, receiver);
		if (blackListStatus == Const.BLACK_LIST_STATUS_ADD) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_IS_PLAYER_BLACK_LIST));
			return resp;
		} else if (blackListStatus == Const.BLACK_LIST_STATUS_ADDED) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_IS_BLACK_LIST));
			return resp;
		}

		// 限制为1到100个字符
		int contentLen = content.length();
		if (contentLen < 1 || contentLen > 100) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_LIMIT));
			return resp;
		}

		// 是否有非法字符
		if (false == CommonToolkit.isUTF8StringValid(content)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_STR_NOT_VALID));
			return resp;
		}
		
		boolean result = StorageService.insertMyChatContent(senderServerId, sender, Const.CHAT_CONTENT_NORMAL_STATUS,
				receiverServerId, receiver, Const.CHAT_CONTENT_NORMAL_STATUS,
				content);
		if (result) {			
			List<ChatContentMy> myChatContentList = StorageService.getMyChatContent(senderServerId, sender, receiverServerId, receiver);
			resp.setMyChatContent(myChatContentList);
			resp.setHisServerId(receiverServerId);
			resp.setHisId(receiver);
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		return resp;
	}
	
	/**
	 * 获取某个会话的聊天数据
	 * @param req
	 * @return
	 */
	public static ChatListMyContentRspMsg processChatListContentMy(ChatListMyContentReqMsg req) {
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int receiver = req.getHisId();
		int receiverServerId = req.getHisServerId();
		
		ChatListMyContentRspMsg resp = new ChatListMyContentRspMsg(req);
		
		PlayerInfo hisPlayerInfo = StorageService.getPlayerInfo(receiverServerId, receiver);
		if (null == hisPlayerInfo) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		// 是否可以向对方发语音私聊
		resp.setCanSendVoice(CommonToolkit.isCanSendVoice(hisPlayerInfo.getVersion()));
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}

		List<ChatContentMy> myChatContentList = StorageService.getMyChatContent(playerServerId, playerId, receiverServerId, receiver);
		if (null != myChatContentList) {
			for (ChatContentMy content : myChatContentList) {
				content.setContent( UIUtil.trim4PhoneUI(content.getContent()) );
			}
			
			final int maxSize = 100;
			if (myChatContentList.size() > maxSize) {
				myChatContentList = myChatContentList.subList(myChatContentList.size() - maxSize, myChatContentList.size());
			}
			
			resp.setSuc(true);
			resp.setMyChatContent(myChatContentList);
			resp.setHisId(receiver);
			resp.setHisServerId(receiverServerId);
		}
		
		boolean needPlusUnreadFollowMsgCount = req.getVersion() != null && req.getVersion().length() > 0;
		resp.setChatUnreadNum(StorageService.getUnreadSecretChatNum(playerServerId, playerId, needPlusUnreadFollowMsgCount));

		return resp;
	}
	
	/**
	 * 删除某用户的某个会话
	 * @param req
	 * @return
	 */
	public static final ChatDeleteMyRspMsg processChatDeleteMy(ChatDeleteMyReqMsg req) {
		int sender = req.getPlayerId();
		int senderServerId = req.getServerId();
		int receiver = req.getHisId();
		int receiverServerId = req.getHisServerId();
		
		ChatDeleteMyRspMsg resp = new ChatDeleteMyRspMsg(req);
		
		if (null == StorageService.getPlayerInfo(senderServerId, sender)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		resp.setHisId(receiver);
		resp.setHisServerId(receiverServerId);
		
		boolean result = StorageService.deleteChatSession(senderServerId, sender, receiverServerId, receiver);
		if (result) {
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		
		return resp; 
	}
	
	/**
	 * 获取某会话的私聊历史记录 
	 * 有多少天之前和条数的限制
	 *
	 * @param req
	 * @return
	 */
	public static ChatHistoryRspMsg processGetMyChatHistory(ChatHistoryReqMsg req) {
		ChatHistoryRspMsg resp = new ChatHistoryRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int hisId = req.getHisId();
		int hisServerId = req.getHisServerId();
		
		PlayerInfo pi = StorageService.getPlayerInfo(playerServerId, playerId);
		if (null == pi) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		List<ChatContentMy> retList = new ArrayList<ChatContentMy>();
	
		List<ChatContentMyVo> hisList = StorageService.getHistoryChatContent(playerServerId, playerId, hisServerId, hisId);
		if (null != hisList) {
			for (ChatContentMyVo chatContentMyVo : hisList) {
				if (chatContentMyVo.getSender() == playerId && chatContentMyVo.getSenderServerId() == playerServerId 
						&& chatContentMyVo.getSenderStatus() == Const.CHAT_CONTENT_INVISIBLE_STATUS) {
					continue;
				}
				
				if (chatContentMyVo.getReceiver() == playerId && chatContentMyVo.getReceiverServerId() == playerServerId 
						&& chatContentMyVo.getReceiverStatus() == Const.CHAT_CONTENT_INVISIBLE_STATUS) {
					continue;	
				}
				
				ChatContentMy chatContentMy = new ChatContentMy();
				chatContentMy.setContent( UIUtil.trim4PhoneUI(chatContentMyVo.getContent()) );
				chatContentMy.setReceiver(chatContentMyVo.getReceiver());
				chatContentMy.setSender(chatContentMyVo.getSender());
				chatContentMy.setStrTime(chatContentMyVo.getStrTime());
				retList.add(chatContentMy);
			}
			
			final int maxSize = 30;
			if (hisList.size() > maxSize) {
				hisList = hisList.subList(hisList.size() - maxSize, hisList.size());
			}
		}

		resp.setSuc(true);
		resp.setMyChatContent(retList);
		resp.setHisId(hisId);
		resp.setHisServerId(hisServerId);
		return resp;
	}

	/**
	 * 加入黑名单
	 * @param req
	 * @return
	 */
	public static InsertBlackListRspMsg processInsertBlackList(InsertBlackListReqMsg req) {
		InsertBlackListRspMsg resp = new InsertBlackListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int hisId = req.getHisId();
		int hisServerId = req.getHisServerId();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		// 不能自己添加自己
		if (playerId == hisId && playerServerId == hisServerId) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.BLACK_SELF));
			return resp;
		}
		
		boolean result = StorageService.insertBlackList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			// 如果自己关注了对方，或者对方关注了自己，都需要删除
			StorageService.removeFollowList(playerServerId, playerId, hisServerId, hisId);
			StorageService.removeFollowList(hisServerId, hisId, playerServerId, playerId);
			
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.BLACK_ALREADY));
		}
		return resp;
	}

	/**
	 * 获取黑名单列表
	 * @param req
	 * @return
	 */
	public static GetBlackListRspMsg processGetBlackList(GetBlackListReqMsg req) {
		GetBlackListRspMsg resp = new GetBlackListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int pageNum = req.getPageNum();
		int pageSize = req.getPageSize();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		List<PlayerInfo> list = StorageService.getBlackList(playerServerId, playerId, pageNum, pageSize);
		if (null != list) {			
			if (list.size() > 0) {
				for (PlayerInfo player : list) {
					ImageWrapper.wrapImage(req, player);
				}
			}
			resp.setSuc(true);
			resp.setList(list);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}

		return resp;
	}

	/**
	 * 删除黑名单
	 * @param req
	 * @return
	 */
	public static RemoveBlackListRspMsg processRemoveBlackList(RemoveBlackListReqMsg req) {
		RemoveBlackListRspMsg resp = new RemoveBlackListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int hisId = req.getHisId();
		int hisServerId = req.getHisServerId();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		boolean result = StorageService.removeBlackList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		return resp;
	}

	/**
	 * 登陆时初始化私聊
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processInitChatCache(InitChatCacheOnLoginReqMsg req) {
		InitChatCacheOnLoginRspMsg resp = new InitChatCacheOnLoginRspMsg(req);
		
		if (null == StorageService.getPlayerInfo(req.getServerId(), req.getPlayerId())) {
			resp.setSuc(true);			
			return resp;
		}	
		
		// 加载玩家私聊数据到缓存
		boolean result = StorageService.loadChatData(req.getServerId(), req.getPlayerId(), true).first;
		if (result) {
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		
		return resp;
	}

	/**
	 * 删除私信
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage deleteChat (DeleteChatReqMsg req) {		
		DeleteChatRspMsg rsp = new DeleteChatRspMsg (req);
		rsp.setSuc(true);
		
		String chatIds = req.getChatIds();
		if (chatIds == null || chatIds.isEmpty()) {
			// 非法操作，无视
			return rsp;
		}
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		
		boolean needDeleteFollowMsg = false;	// 是否需要删除关注消息
		
		// 生成待删除的 chatId 列表
		List<String> chatIdList = new ArrayList<String>();
		String[] items = chatIds.split(";");
		for (String item : items) {
			if (item.isEmpty()) {
				continue;
			}
			
			String[] arr = item.split(",");
			try {
				int oppositeServerId = Integer.parseInt(arr[0]);
				int oppositePlayerId = Integer.parseInt(arr[1]);
				
				if (oppositeServerId == 0 && oppositePlayerId == 0) {
					needDeleteFollowMsg = true;
					continue;
				}
				
				String chatId = CommonToolkit.getSessionId(oppositeServerId, oppositePlayerId, serverId, playerId);
				chatIdList.add(chatId);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 获取所有的 chat
		Map<String, ChatMyVo> chatMap = CacheFactory.getChatRedisCache().batchChatMyVo(chatIdList);
		if (chatMap == null) {
			chatMap = new HashMap<String, ChatMyVo>();
		}
		if (chatMap.size() < chatIdList.size()) {
			// 未缓存的 chatId
			List<String> unCachedChatIds = new ArrayList<String>();
			for (String chatId : chatIdList) {
				if (!chatMap.containsKey(chatId)) {
					unCachedChatIds.add(chatId);
				}
			}
			Map<String, ChatMyVo> uncachedChatMap = DaoFactory.getChatDao().batchChat(unCachedChatIds);
			if (uncachedChatMap != null) {
				chatMap.putAll(uncachedChatMap);
			}
		}
		
		final long nowMillis = System.currentTimeMillis();
		
		// 分开[可以直接删除的]和[打删除标记的]
		List<String> directDeleteList = new ArrayList<String>();	// 可以直接删除的
		List<ChatMyVo> markDeleteList = new ArrayList<ChatMyVo>();		// 打删除标记的
		List<String> list = new ArrayList<String>();	// TODO For Test
		for (ChatMyVo chat : chatMap.values()) {
			if (chat.getChater2ServerId() == Const.ROBOT_SERVER_ID || 
					Const.CHAT_SESSION_STATUS_DELETED == chat.getChater2State() ||
					Const.CHAT_SESSION_STATUS_INVISIBLE == chat.getChater2State()) {
				// 如果对方是机器人或者已经删除或者是隐藏的，则彻底删除
				directDeleteList.add(chat.getChatId());
			}
			else {
				if (serverId == chat.getChater1ServerId() && playerId == chat.getChater1()) {
					chat.setChater1State(Const.CHAT_SESSION_STATUS_DELETED);
					chat.setDelTimestampChater1(nowMillis);
				}
				else {
					chat.setChater2State(Const.CHAT_SESSION_STATUS_DELETED);
					chat.setDelTimestampChater2(nowMillis);
				}
				
				markDeleteList.add(chat);
				list.add(chat.getChatId());
			}
		}
		
		// 删除/更新数据
		DaoFactory.getChatDao().batchUpdate(markDeleteList); // [打删除标记的]
		CacheFactory.getChatRedisCache().batchSetChat(markDeleteList);
		
		DaoFactory.getChatDao().batchDeleteChat(directDeleteList); // [可以直接删除的]
		CacheFactory.getChatRedisCache().batchDeleteChat(directDeleteList);
		
		if (needDeleteFollowMsg) {
			// 删除关注消息
			StorageService.clearFollowMsg(serverId, playerId);
		}
		
		RedisToolkit.removeMyAllChatSession(serverId, playerId);
		
		return rsp;
	}
}
