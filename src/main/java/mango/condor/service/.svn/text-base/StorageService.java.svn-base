package mango.condor.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import mango.condor.dao.DaoFactory;
import mango.condor.domain.bottle.Bottle;
import mango.condor.domain.bottle.BottleListItem;
import mango.condor.domain.chat.BlackList;
import mango.condor.domain.chat.ChatContentMy;
import mango.condor.domain.chat.ChatContentMyVo;
import mango.condor.domain.chat.ChatMy;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.FollowList;
import mango.condor.domain.lbs.FollowMsg;
import mango.condor.domain.lbs.FollowRank;
import mango.condor.domain.lbs.FollowRecord;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.TmpFollowRankItem;
import mango.condor.domain.msg.lbs.IndexReqMsg;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.DBBottleToolkit;
import mango.condor.toolkit.DBToolkit;
import mango.condor.toolkit.GeoToolkit;
import mango.condor.toolkit.MultiLanguageManager;
import mango.condor.toolkit.RedisLock;
import mango.condor.toolkit.RedisToolkit;

import com.gzyouai.hummingbird.common.component.Pair;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-10 下午5:50:50
 * @Description 
 */
public class StorageService {
	
	/**
	 * 启动时加载所有未读瓶子到redis
	 */
	public static void initAllUnreadBottle() {
		int bottleNum = DBToolkit.selectMaxID(Const.TBL_BOTTLE);
		for (int st = 0; st <= bottleNum; st += Const.INIT_BOTTLE_SCAN_LIMIT) {
			List<Bottle> bottleList = DBBottleToolkit.selectAllUnreadBottle(st);
			if (null == bottleList) {
				break;
			}
			for (Bottle bottle : bottleList) {
				// 加载到redis
				RedisToolkit.putUnreadBottle(bottle);
			}
		}
	}
	
	/**
	 * 注意！！如果部署了2个tomcat，不能在这里处理！！
	 * 程序启动时调用
	 */
	public static void initFollowListAndBlackList() {
		int blackListNum = DBToolkit.selectMaxID(Const.TBL_BLACK_LIST);
		for (int st = 0; st <= blackListNum; st += Const.INIT_BLACK_LIST_SCAN_LIMIT) {
			// 加载所有的黑名单数据到缓存，永不过期
			List<BlackList> blackList = DBToolkit.selectAllBlackList(st);
			if (null == blackList) {
				break;
			}
			for (BlackList bl : blackList) {
				RedisToolkit.insertBlackList(bl.getPid(), bl.getTid(), bl.getId());
			}
		}

		// 线程池个数500，加速初始化
		int poolNum = 500;
		ExecutorService pool = Executors.newFixedThreadPool(poolNum);
		
		int followListNum = DBToolkit.selectMaxID(Const.TBL_FOLLOW_LIST);
		for (int st = 0; st <= followListNum; st += Const.INIT_FOLLOW_LIST_SCAN_LIMIT) {
			// 加载所有的关注，粉丝数据到缓存，永不过期
			List<FollowList> followList = DBToolkit.selectAllFollowList(st);
			if (null == followList) {
				break;
			}
			InitWorker worker = new InitWorker();
			worker.list = followList;
			pool.submit(worker);
		}
		
		try {
			// 等到所有的加载结束后再继续执行
			pool.shutdown();
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("pool shutdown");
	}
	
	// 多线程优化，加快初始化速度
	private static class InitWorker implements Runnable {
		List<FollowList> list;

		@Override
		public void run() {
			for (FollowList fl : list) {
				boolean suc = RedisToolkit.insertFollowList(fl.getFollower(), fl.getFollowed(), fl.getId());
				if (!fl.isFollowedClearMsg() && fl.getFollowTime() > 0) {
					suc &= RedisToolkit.insertFollowMsg(fl.getFollower(), fl.getFollowed(), fl.getFollowTime(), fl.isReadMsg());
				}
				if (false == suc) {
					throw new RuntimeException();
				}
			}
		}
	}
	
	/**
	 * 初始化首页新鲜事
	 */
	public static void initIndexFresh() {
		List<String> uidList = DBToolkit.selectInitIndexPlayerInfo(Const.INDEX_INIT_LIMIT);
		long now = System.currentTimeMillis();
		if (null != uidList) {
			for (String uid : uidList) {
				RedisToolkit.appendIndexFresh(uid, now);
			}
		}
	}
	
	/**
	 * 从数据库查询关注排行并缓存
	 */
	public static void genFollowRankAndCache() {
		List<FollowRank> list = DBToolkit.selectFollowRank();
		if (null != list) {
			// 如果小于排行榜最大人数，则抓一些粉丝为0的人来凑齐
			if (list.size() < Const.FOLLOW_RANK_LIMIT) {
				Set<String> alreadySet = new HashSet<String>();
				for (FollowRank fr : list) {
					alreadySet.add(fr.getUid());
				}
				
				int i = list.size() + 1;
				List<PlayerInfo> zeroList = DBToolkit.selectPlayerInfoByLimit();
				if (null != zeroList) {
					for (PlayerInfo pi : zeroList) {
						// 如果该玩家不在集合中，则加入
						if (false == alreadySet.contains(pi.getUid())) {
							FollowRank fr = new FollowRank();
							fr.set(pi);
							fr.setFanNum(0);
							fr.setUid(pi.getUid());
							fr.setRank(i);
							++i;
							list.add(fr);
						}
					}
				}
			}

			RedisToolkit.putFollowRank(list);
		}
	}
	
	/**
	 * 从数据库中加载私聊数据并缓存
	 * @param serverId
	 * @param playerId
	 * @param isLogin
	 * @return
	 * 		Pair<是否load成功, 聊天会话列表>
	 */
	public static Pair<Boolean, List<ChatMyVo>> loadChatData(int serverId, int playerId, boolean isLogin) {
		StringBuilder log = new StringBuilder("TIMES.loadChatData playerId=" + playerId);
		final long now = System.currentTimeMillis();
		long t = now;
		long newT = 0;
		
		List<ChatMyVo> sessionList = null;
		
		try {
			// 从数据库中加载
			sessionList = DBToolkit.selectMyChatSession(serverId, playerId);
			if (sessionList == null) {
				return Pair.makePair(false, null);
			}
			if (sessionList.isEmpty()) {
				return Pair.makePair(true, sessionList);
			}
			
			newT = System.currentTimeMillis();
			log.append(" selectMyChatSession=" + (newT - t));
			t = newT;
			
			// 更新chat的最后阅读时间
			String uid = CommonToolkit.toUID(serverId, playerId);
			if (isLogin) {
				DBToolkit.updateMyChatSessionWhenLogin(uid);
				
				for (ChatMyVo session : sessionList) {
					if (session.getChater1() == playerId && session.getChater1ServerId() == serverId) {
						session.setLastReadedChater1(session.getReadedTimestampChater1());
					} else {
						session.setLastReadedChater2(session.getReadedTimestampChater2());
					}
				}
				
				newT = System.currentTimeMillis();
				log.append(" updateMyChatSessionWhenLogin=" + (newT - t));
				t = newT;
			}
			
			String[] chatIds = new String[sessionList.size()];
			for (int i = 0; i < sessionList.size(); i++) {
				chatIds[i] = sessionList.get(i).getChatId();
			}
			
			// 更新 chat
			if (!RedisToolkit.resetMyChatSession(sessionList, true)) {
				return Pair.makePair(false, null);
			}
			
			newT = System.currentTimeMillis();
			log.append(" batchPutMyChatSession=" + (newT - t));
			t = newT;
			
			// 更新 chat id 集合
			if (!RedisToolkit.resetMyChatSet(serverId, playerId, chatIds, true)) {
				return Pair.makePair(false, null);
			}
			
			newT = System.currentTimeMillis();
			log.append(" resetMyChatSet=" + (newT - t));
			t = newT;
			
//			// 更新 chat content
//			for (ChatMyVo session : sessionList) {
//				String sessionId = session.getChatId();				
//				List<ChatContentMyVo> contentList = DBToolkit.selectMyChatContent(sessionId);
//				if (null != contentList && !contentList.isEmpty()) {
//					RedisToolkit.resetMyChatContent(sessionId, contentList, true);
//				}
//			}
			
			newT = System.currentTimeMillis();
			log.append(" batchInsertMyChatContent=" + (newT - t));
			t = newT;
		}
		finally {
			newT = System.currentTimeMillis();
			long timeDiff = newT - now;
			if (timeDiff > 1000L) {
				log.append(" timeDiff=" + timeDiff);
				System.out.println( log.toString() );
			}	
		}
		
		return Pair.makePair(true, sessionList);
	}
	
	/**
	 * 获得历史聊天记录
	 * @param playerServerId
	 * @param playerId
	 * @param targetServerId
	 * @param targetId
	 * @return
	 */
	public static List<ChatContentMyVo> getHistoryChatContent(int playerServerId, int playerId, int targetServerId, int targetId) {
		// 遍历会话列表
		Set<String> sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		
		// 先重新尝试加载
		if (null == sessionSet || sessionSet.isEmpty()) {
			StorageService.loadChatData(playerServerId, playerId, false);
			sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		}
		
		if (null == sessionSet) {
			return null;
		}

		String foundSessionId = null;
		long delTimestamp = -1;
		long readedTimestamp = -1;
		
		for (String sessionId : sessionSet) {
			ChatMyVo session = RedisToolkit.getMyChatSession(sessionId);
			if (session != null) {
				if (session.getChater1() == playerId && session.getChater1ServerId() == playerServerId
						&& session.getChater2() == targetId && session.getChater2ServerId() == targetServerId) {
					
					foundSessionId = sessionId;
					delTimestamp = session.getDelTimestampChater1();
					readedTimestamp = Math.min(session.getReadedTimestampChater1(), session.getLastReadedChater1());
					break;
				} else if (session.getChater2() == playerId && session.getChater2ServerId() == playerServerId
						&& session.getChater1() == targetId && session.getChater1ServerId() == targetServerId) {
					
					foundSessionId = sessionId;
					delTimestamp = session.getDelTimestampChater2();
					readedTimestamp = Math.min(session.getReadedTimestampChater2(), session.getLastReadedChater2());
					break;
				}
			}
		}
		
		// 没找到
		if (null == foundSessionId) {
			return null;
		}
		
		return DBToolkit.getHistoryChatContent(foundSessionId, readedTimestamp, delTimestamp);
	}
	
	/**
	 * 删除某玩家的所有会话
	 * @param senderServerId
	 * @param sender
	 * @return
	 */
	public static boolean deleteAllChatSession(int playerServerId, int playerId) {
		// 遍历会话列表
		Set<String> sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		
		// 先重新尝试加载
		if (null == sessionSet || sessionSet.isEmpty()) {
			StorageService.loadChatData(playerServerId, playerId, false);
			sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		}
		
		if (null == sessionSet) {
			return false;
		}
		
		String lockKey = null;
		boolean isLock = false;
		boolean result = false;
		
		try {
			for (String sessionId : sessionSet) {
				ChatMyVo session = RedisToolkit.getMyChatSession(sessionId);
				if (session != null) {
					if (session.getChater1() == playerId && session.getChater1ServerId() == playerServerId) {
						
						lockKey = RedisToolkit.getLockKey(sessionId);
						// 对session加锁
						isLock = RedisLock.lock(lockKey);
						
						// 加锁失败，这种情况极少发生
						if (false == isLock) {
							return false;
						}
						
						// 彻底删除
						result = DBToolkit.deleteMyChatSession(sessionId);
						if (result) {
							result = RedisToolkit.removeChatSession(playerServerId, playerId, 
									session.getChater2ServerId(), session.getChater2());
						}
					} else if (session.getChater2() == playerId && session.getChater2ServerId() == playerServerId) {
						lockKey = RedisToolkit.getLockKey(sessionId);
						// 对session加锁
						isLock = RedisLock.lock(lockKey);
						
						// 彻底删除
						result = DBToolkit.deleteMyChatSession(sessionId);
						if (result) {
							result = RedisToolkit.removeChatSession(playerServerId, playerId, 
									session.getChater1ServerId(), session.getChater1());
						}
					}
				}
			}
		} finally {
			// 释放锁
			if (isLock) {
				RedisLock.release(lockKey);
			}
		}
	
		return result;
	}
	
	/**
	 * 删除某玩家会话
	 * @param senderServerId
	 * @param sender
	 * @return
	 */
	public static boolean deleteChatSession(int playerServerId, int playerId, int hisServerId, int hisPlayerId) {
		// 遍历会话列表
		Set<String> sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		
		// 先重新尝试加载
		if (null == sessionSet || sessionSet.isEmpty()) {
			StorageService.loadChatData(playerServerId, playerId, false);
			sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		}
		
		if (null == sessionSet) {
			return false;
		}
		
		String lockKey = null;
		boolean isLock = false;
		boolean result = false;
		
		try {
			for (String sessionId : sessionSet) {
				ChatMyVo session = RedisToolkit.getMyChatSession(sessionId);
				if (session != null) {
					if (session.getChater1() == playerId && session.getChater1ServerId() == playerServerId
							&& session.getChater2() == hisPlayerId && session.getChater2ServerId() == hisServerId) {
						
						lockKey = RedisToolkit.getLockKey(sessionId);
						// 对session加锁
						isLock = RedisLock.lock(lockKey);
						
						// 加锁失败，这种情况极少发生
						if (false == isLock) {
							return false;
						}
						
						if (session.getChater2ServerId() == Const.ROBOT_SERVER_ID || 
								Const.CHAT_SESSION_STATUS_DELETED == session.getChater2State() ||
								Const.CHAT_SESSION_STATUS_INVISIBLE == session.getChater2State()) {
							// 如果对方是机器人或者已经删除或者是隐藏的，则彻底删除
							result = DBToolkit.deleteMyChatSession(sessionId);
							if (result) {
								result = RedisToolkit.removeChatSession(playerServerId, playerId, hisServerId, hisPlayerId);
							}
						} else {
							session.setChater1State(Const.CHAT_SESSION_STATUS_DELETED);
							session.setDelTimestampChater1(System.currentTimeMillis());
							result = DBToolkit.updateMyChatSession(session);
							if (result) {
								result = RedisToolkit.putMyChatSession(session, false);
							}
						}
						
						break;
					} else if (session.getChater2() == playerId && session.getChater2ServerId() == playerServerId
							&& session.getChater1() == hisPlayerId && session.getChater1ServerId() == hisServerId) {
						
						lockKey = RedisToolkit.getLockKey(sessionId);
						// 对session加锁
						isLock = RedisLock.lock(lockKey);
						
						if (session.getChater1ServerId() == Const.ROBOT_SERVER_ID ||
								Const.CHAT_SESSION_STATUS_DELETED == session.getChater1State() ||
								Const.CHAT_SESSION_STATUS_INVISIBLE == session.getChater1State()) {
							// 如果对方是机器人或者已经删除或者是隐藏的，则彻底删除
							result = DBToolkit.deleteMyChatSession(sessionId);
							if (result) {
								result = RedisToolkit.removeChatSession(playerServerId, playerId, hisServerId, hisPlayerId);
							}
						} else {
							session.setChater2State(Const.CHAT_SESSION_STATUS_DELETED);
							session.setDelTimestampChater2(System.currentTimeMillis());
							result = DBToolkit.updateMyChatSession(session);
							if (result) {
								result = RedisToolkit.putMyChatSession(session, false);
							}
						}
						
						break;
					}
				}
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			// 释放锁
			if (isLock) {
				RedisLock.release(lockKey);
			}
		}
	}
	
	/**
	 * 获取当前有几个人在和你说话
	 * @param senderServerId
	 * @param sender
	 * @param needPlusUnreadFollowMsgCount
	 * @return
	 */
	public static int getUnreadSecretChatNum(int senderServerId, int sender, boolean needPlusUnreadFollowMsgCount) {
		List<ChatMy> myChatList = StorageService.getMyChatList(senderServerId, sender);
		if (myChatList == null) {
			return 0;
		}
		
		int unReadNum = 0;
		for (ChatMy chat : myChatList) {
			if (chat.getUnReadNum() > 0) {
				unReadNum++;
			}
		}
		
		if (needPlusUnreadFollowMsgCount) {
			// 是否有未读关注消息
			boolean hasUnreadFollowMsg = false;
			int lang = Const.LANG_ZH_CN;		// 使用 lang 只取关注消息的数量
			List<FollowMsg> msgList = StorageService.readFollowMsgList(senderServerId, sender, lang, false);
			if (msgList.size() > 0) {
				for (FollowMsg msg : msgList) {
					if (!msg.isRead()) {
						hasUnreadFollowMsg = true;
						break;
					}
				}
			}
			if (hasUnreadFollowMsg) {
				unReadNum += 1;
			}
		}
		
		return unReadNum;
	}

	/**
	 * 获取某个会话的聊天内容
	 * @param senderServerId
	 * @param sender
	 * @param receiverServerId
	 * @param receiver
	 * @return
	 */
	public static List<ChatContentMy> getMyChatContent(int playerServerId, int playerId, int receiverServerId, int receiver) {
		// 遍历会话列表
		Set<String> sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		
		// 先重新尝试加载
		if (null == sessionSet || sessionSet.isEmpty()) {
			StorageService.loadChatData(playerServerId, playerId, false);
			sessionSet = RedisToolkit.getMyChatSet(playerServerId, playerId);
		}
		
		if (null == sessionSet) {
			return null;
		}
		
		// 试图找到发送者的会话
		ChatMyVo foundSession = null;
		
		boolean result = false;
				
		// 暂不考虑跟某个人有重复会话的情况
		for (String sessionId : sessionSet) {
			// 取得会话缓存
			ChatMyVo session = RedisToolkit.getMyChatSession(sessionId);
			if (null == session) {
				continue;
			}
			
			// 找到与对方的会话对象
			if (session.getChater1() == receiver && session.getChater1ServerId() == receiverServerId
					&& session.getChater2() == playerId && session.getChater2ServerId() == playerServerId) {
				
				foundSession = session;
				break;
			} else if (session.getChater1() == playerId && session.getChater1ServerId() == playerServerId 
					&& session.getChater2() == receiver && session.getChater2ServerId() == receiverServerId) {
				
				foundSession = session;
				break;
			}
		}
		
		if (null == foundSession) {
			return null;
		}
		
		String lockKey = RedisToolkit.getLockKey(foundSession.getChatId());
		boolean isLock = false; 
		
		// 返回值
		List<ChatContentMy> messagesList = new ArrayList<ChatContentMy>();
		
		try {
			// 加锁
			isLock = RedisLock.lock(lockKey);
			
			// 加锁失败，这种情况极少发生
			if (false == isLock) {
				return null;
			}
			
			// 拿到上次读取的时间戳
			long lastMessageTimestamp = 0L;
			lastMessageTimestamp = (foundSession.getChater1() == playerId ? foundSession.getReadedTimestampChater1()
							: (foundSession.getChater2() == playerId ? foundSession.getReadedTimestampChater2() : 0L));
			
			List<ChatContentMyVo> chatContentList = DBToolkit.selectMyChatContent(foundSession.getChatId());
			if (chatContentList == null) {
				return null;
			}
			
			if (chatContentList.isEmpty()) {
				return messagesList;
			}
			
			for (ChatContentMyVo content : chatContentList) {
				if (content.getSendTime() > lastMessageTimestamp) {
					if (content.getSender() == playerId && content.getSenderServerId() == playerServerId 
							&& content.getSenderStatus() == Const.CHAT_CONTENT_INVISIBLE_STATUS) {
						continue;
					}
					
					if (content.getReceiver() == playerId && content.getReceiverServerId() == playerServerId 
							&& content.getReceiverStatus() == Const.CHAT_CONTENT_INVISIBLE_STATUS) {
						continue;	
					}

					ChatContentMy chatContentMy = new ChatContentMy();
					chatContentMy.setContent(content.getContent());
					chatContentMy.setReceiver(content.getReceiver());
					chatContentMy.setSender(content.getSender());
					chatContentMy.setStrTime(content.getStrTime());
					messagesList.add(chatContentMy);
				}
			}
			
			// 更新最后一次我已读的时间戳，未读设置为0
			if (foundSession.getChater1() == playerId) {
				foundSession.setReadedTimestampChater1(chatContentList.get(chatContentList.size() - 1).getSendTime());
				foundSession.setUnReadNumChater1(0);
			} else if (foundSession.getChater2() == playerId) {
				foundSession.setReadedTimestampChater2(chatContentList.get(chatContentList.size() - 1).getSendTime());
				foundSession.setUnReadNumChater2(0);
			}
			
			// 更新foundSession到缓存和DB
			result = DBToolkit.updateMyChatSession(foundSession);
			if (false == result) {
				return null;
			}
			result = RedisToolkit.putMyChatSession(foundSession, false);
			if (false == result) {
				return null;
			}
		} finally {
			if (isLock) {
				RedisLock.release(lockKey);
			}
		}
			
		return messagesList;
	}
	
	/**
	 * 获得某玩家的所有私聊会话
	 * 只从缓存中读取
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static List<ChatMy> getMyChatList(int serverId, int playerId) {
		List<ChatMy> retList = new ArrayList<ChatMy>();
		
		// 遍历会话列表
		Set<String> sessionSet = RedisToolkit.getMyChatSet(serverId, playerId);
		
		// 先重新尝试加载
		if (null == sessionSet || sessionSet.isEmpty()) {
			StorageService.loadChatData(serverId, playerId, false);
			sessionSet = RedisToolkit.getMyChatSet(serverId, playerId);
		}
		
		if (null == sessionSet) {
			return null;
		}
		
		// 暂不考虑跟某个人有重复会话的情况
		for (String sessionId : sessionSet) {
			// 取得会话缓存
			ChatMyVo chatMyVo = RedisToolkit.getMyChatSession(sessionId);
			if (null == chatMyVo) {
				continue;
			}
			
			// 过滤已删除或已隐藏的会话
			if (playerId == chatMyVo.getChater1() && serverId == chatMyVo.getChater1ServerId()) {
				if (chatMyVo.getChater1State() == Const.CHAT_SESSION_STATUS_DELETED ||
						chatMyVo.getChater1State() == Const.CHAT_SESSION_STATUS_INVISIBLE) {
					continue;
				}
			} else if (playerId == chatMyVo.getChater2() && serverId == chatMyVo.getChater2ServerId()) {
				if (chatMyVo.getChater2State() == Const.CHAT_SESSION_STATUS_DELETED ||
						chatMyVo.getChater2State() == Const.CHAT_SESSION_STATUS_INVISIBLE) {
					continue;
				}
			}
			
			ChatMy chatMy = new ChatMy();
			int targetPlayerId = chatMyVo.getChater1();
			int targetServerId = chatMyVo.getChater1ServerId();
			int selfUnReadNum = chatMyVo.getUnReadNumChater1();
					
			if (targetPlayerId == playerId) {
				targetPlayerId = chatMyVo.getChater2();
				targetServerId = chatMyVo.getChater2ServerId();
			} else {
				selfUnReadNum = chatMyVo.getUnReadNumChater2();
			}
			
			// 找到对方的个人信息
			PlayerInfo playerInfo = getPlayerInfo(targetServerId, targetPlayerId);
			if (null == playerInfo) {
				continue;
			}

			chatMy.setHisServerId(targetServerId);
			chatMy.setHisId(targetPlayerId);
			chatMy.setChaterImageId(playerInfo.getImageId());
			chatMy.setChaterLevel(playerInfo.getLevel());
			chatMy.setChaterName(playerInfo.getName());
			chatMy.setLastMessage(chatMyVo.getLastMessage());
			chatMy.setLastTime(chatMyVo.getLastTimeReadable());
			chatMy.setLongLastTime(chatMyVo.getLastTime());
			chatMy.setGender(playerInfo.isGender());
			chatMy.setMyId(playerId);
			chatMy.setMyServerId(serverId);
			chatMy.setUnReadNum(selfUnReadNum);
			
			retList.add(chatMy);
		}

		return retList;
	}
	
	/**
	 * 发送私聊
	 * @param senderServerId
	 * @param sender
	 * @param receiverServerId
	 * @param receiver
	 * @param content
	 * @param senderUid
	 * @return
	 */
	public static boolean insertMyChatContent(int senderServerId, int sender, byte senderStatus,
			int receiverServerId, int receiver, byte receiverStatus, String content) {
		
		// 可能会出现这样的情况，插入关注的时候，可能会以接收者的角度给自己发私聊
		// 而对方又不一定在线，所以私聊相关数据不会加载到缓存
		// 而之前可能两人之间又可能发生过聊天，所以就是说isFound可能会false，
		// 但数据库中却会有相应的字段，导致重复主键，2013-08-06修复之。
		// 解决方法是，如果拿到的senderSessionSet是null，则重新加载下私聊数据
		
		// 遍历发送方会话列表
		Set<String> senderSessionSet = RedisToolkit.getMyChatSet(senderServerId, sender);
		
		// 重新加载
		if (null == senderSessionSet || senderSessionSet.isEmpty()) {
			StorageService.loadChatData(senderServerId, sender, false);
			senderSessionSet = RedisToolkit.getMyChatSet(senderServerId, sender);
		}
		
		boolean isFound = false;
		
		// 试图找到发送者的会话
		ChatMyVo foundSession = null;
		
		long now = System.currentTimeMillis() / 1000 * 1000;
		
		boolean result = false;
		
		if (null != senderSessionSet) {
			for (String sessionId : senderSessionSet) {
				ChatMyVo session = RedisToolkit.getMyChatSession(sessionId);
				if (null == session) {
					continue;
				}
				
				// 找到与对方的会话对象
				if (session.getChater1() == receiver && session.getChater1ServerId() == receiverServerId
						&& session.getChater2() == sender && session.getChater2ServerId() == senderServerId) {
					
					isFound = true;
					foundSession = session;
					break;
				} else if (session.getChater1() == sender && session.getChater1ServerId() == senderServerId 
						&& session.getChater2() == receiver && session.getChater2ServerId() == receiverServerId) {
					
					isFound = true;
					foundSession = session;
					break;
				}
			}
		}
		
		String sessionId = null;
		
		if (false == isFound) {
			// 没找到会话
			foundSession = new ChatMyVo();
			sessionId = CommonToolkit.getSessionId(senderServerId, sender, receiverServerId, receiver);
			foundSession.setChatId(sessionId);
			foundSession.setReadedTimestampChater1(now - 1000);
			foundSession.setReadedTimestampChater2(now - 1000);
			foundSession.setDelTimestampChater1(now - 1000);
			foundSession.setDelTimestampChater2(now - 1000);
			foundSession.setLastReadedChater1(now - 1000);
			foundSession.setLastReadedChater2(now - 1000);
			
			result = RedisToolkit.appendMyChatSet(senderServerId, sender, sessionId, false, false);
			if (false == result) {
				return false;
			}
		} else {
			sessionId = foundSession.getChatId();
		}
		
		String lockKey = RedisToolkit.getLockKey(sessionId);
		boolean isLock = false;
		
		try {
			// 加锁
			isLock = RedisLock.lock(lockKey);
			
			// 加锁失败，这种情况极少发生
			if (false == isLock) {
				return false;
			}
			
			// 更新对方会话集合
			RedisToolkit.appendMyChatSet(receiverServerId, receiver, sessionId, true, true);
			
			// 私聊内容表插入
			ChatContentMyVo chatContentMyVo = new ChatContentMyVo();
			chatContentMyVo.setChatId(sessionId);
			chatContentMyVo.setContent(content);
			chatContentMyVo.setSendTime(now);
			chatContentMyVo.setSender(sender);
			chatContentMyVo.setSenderStatus(senderStatus);
			chatContentMyVo.setReceiverStatus(receiverStatus);
			chatContentMyVo.setSenderServerId(senderServerId);
			chatContentMyVo.setReceiver(receiver);
			chatContentMyVo.setReceiverServerId(receiverServerId);
			
			// 更新聊天列表对象。把接收者的未读数目加一,如果还没有列表对象，则创建.
			String s = senderServerId + "-" + sender;
			String r = receiverServerId + "-" + receiver;
			if (s.compareTo(r) > 0) {
				// s > r
				foundSession.setChater1(receiver);
				foundSession.setChater1ServerId(receiverServerId);
				foundSession.setChater2(sender);
				foundSession.setChater2ServerId(senderServerId);
				foundSession.setUnReadNumChater1(foundSession.getUnReadNumChater1() + 1);
				
				// 对方状态设为正常
				foundSession.setChater1State(Const.CHAT_SESSION_STATUS_NORMAL);
				
				// 设置自己的状态
				if (senderStatus == Const.CHAT_CONTENT_INVISIBLE_STATUS &&
						(false == isFound || (isFound && foundSession.getChater2State() == Const.CHAT_SESSION_STATUS_INVISIBLE))) {
					foundSession.setChater2State(Const.CHAT_SESSION_STATUS_INVISIBLE);
				} else {
					foundSession.setChater2State(Const.CHAT_SESSION_STATUS_NORMAL);
				}
			} else {
				foundSession.setChater1(sender);
				foundSession.setChater1ServerId(senderServerId);
				foundSession.setChater2(receiver);
				foundSession.setChater2ServerId(receiverServerId);
				foundSession.setUnReadNumChater2(foundSession.getUnReadNumChater2() + 1);
				
				// 对方状态设为正常
				foundSession.setChater2State(Const.CHAT_SESSION_STATUS_NORMAL);
				
				// 设置自己的状态
				if (senderStatus == Const.CHAT_CONTENT_INVISIBLE_STATUS &&
						(false == isFound || (isFound && foundSession.getChater1State() == Const.CHAT_SESSION_STATUS_INVISIBLE))) {
					foundSession.setChater1State(Const.CHAT_SESSION_STATUS_INVISIBLE);
				} else {
					foundSession.setChater1State(Const.CHAT_SESSION_STATUS_NORMAL);
				}
			}

			foundSession.setChatId(sessionId);
			foundSession.setLastMessage(content);
			foundSession.setLastTime(now);
			
			// 缓存和持久化foundSession，chatContentMyVo
			RedisToolkit.putMyChatSession(foundSession, false);
			if (false == isFound) {
				result = DBToolkit.insertMyChatSession(foundSession);
				if (false == result) {
					return false;
				}
			} else {
				result = DBToolkit.updateMyChatSession(foundSession);
				if (false == result) {
					return false;
				}
			}
			
//			result = RedisToolkit.insertMyChatContent(chatContentMyVo, false, true);
//			if (false == result) {
//				return false;
//			}
			result = DBToolkit.insertMyChatContent(chatContentMyVo);
			if (false == result) {
				return false;
			}
		} finally {
			if (isLock) {
				RedisLock.release(lockKey);
			}
		}

		return true;
	}
	
	/**
	 * 持久化玩家信息
	 * @param playerInfo
	 * @return
	 */
	public static boolean insertOrUpdatePlayerInfo(PlayerInfo playerInfo) {
		StringBuilder log = new StringBuilder("TIMES.insertOrUpdatePlayerInfo uid=" + playerInfo.getUid());
		final long now = System.currentTimeMillis();
		long t = now;
		long newT = 0;
		
		try {
			
			// 更新时间戳
			playerInfo.setUpdateTS(now);
			
			// 如果玩家有签名或者有自定义头像或者有语音签名，则加入新鲜事集合
			if (null != playerInfo.getSign() || 
					playerInfo.getImageId().startsWith(Const.DIY_AVATAR_PREFIX) || 
					null != playerInfo.getVoiceId()) {
				RedisToolkit.appendIndexFresh(playerInfo.getUid(), now);
				
				newT = System.currentTimeMillis();
				log.append(" appendIndexFresh=" + (newT - t));
				t = newT;
			}
			
			// 获取旧的玩家数据
			PlayerInfo oldPlayerInfo = StorageService.getPlayerInfo(playerInfo.getServerId(), playerInfo.getPlayerId(), false);
			
			newT = System.currentTimeMillis();
			log.append(" get.oldPlayerInfo=" + (newT - t));
			t = newT;
			
			// 先持久化到数据库
			boolean result = DBToolkit.insertOrUpdatePlayerInfo(playerInfo);
			
			newT = System.currentTimeMillis();
			log.append(" insertOrUpdatePlayerInfo=" + (newT - t));
			t = newT;
			
			if (false == result) {
				return false;
			}
			
			// 不管是否过期，都插入缓存
			result = RedisToolkit.putPlayerInfoAndGeoHash(playerInfo, oldPlayerInfo, true);
			
			newT = System.currentTimeMillis();
			log.append(" putPlayerInfoAndGeoHash=" + (newT - t));
			t = newT;
			
			return result;
		} 
		finally {
			newT = System.currentTimeMillis();
			long timeDiff = newT - now;
			if (timeDiff > 100L) {
				log.append(" timeDiff=" + timeDiff);
				System.out.println( log.toString() );
			}
		}
	}
	
	/**
	 * 得到玩家信息
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo getPlayerInfo(String uid, boolean isCache) {
		// 先尝试从内存中查找
		PlayerInfo pi = RedisToolkit.getPlayerInfoById(uid);
		// 如果找不到则从数据库查找，再缓存
		if (null == pi) {
			pi = DBToolkit.selectPlayerInfo(uid);
			if (null == pi) {
				return null;
			}
			
			if (isCache) {
				RedisToolkit.putPlayerInfoAndGeoHash(pi, null, false);
			}
		}
		return pi;
	}
	
	/**
	 * 获取一个玩家信息
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo getPlayerInfo(int serverId, int playerId) {
		return getPlayerInfo(serverId, playerId, true);
	}
	
	/**
	 * 获取一个玩家信息
	 * @param serverId
	 * @param playerId
	 * @param cacheWhenNotInCache
	 * 		如果 playerInfo不在缓存内，就更新到缓存内
	 * @return
	 */
	public static PlayerInfo getPlayerInfo(int serverId, int playerId, boolean cacheWhenNotInCache) {
		PlayerInfo player = RedisToolkit.getPlayerInfo(serverId, playerId);
		
		// 如果找不到则从数据库查找，再缓存
		if (null == player) {
			player = DBToolkit.selectPlayerInfo( CommonToolkit.toUID(serverId, playerId) );
			if (null == player) {
				return null;
			}
			
			if (cacheWhenNotInCache) {
				RedisToolkit.putPlayerInfoAndGeoHash(player, null, false);
			}
		}
		
		return player;
	}

	/**
	 * 分页获取黑名单列表
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static List<PlayerInfo> getBlackList(int playerServerId, int playerId, int pageNum, int pageSize) {
		Set<String> idSet = RedisToolkit.getBlackList(playerServerId, playerId, pageNum, pageSize);
		List<PlayerInfo> list = new ArrayList<PlayerInfo>();
		PlayerInfo selfPi = StorageService.getPlayerInfo(playerServerId, playerId);
		if (null != idSet) {
			for (String id : idSet) {
				PlayerInfo pi = StorageService.getPlayerInfo(id, true);
				if (null != pi) {
					pi.calDiffTS();
					if (null != selfPi) {
						pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
								pi.getLat(), pi.getLng()));
					}
					list.add(pi);
				}
			}
		}
		return list;
	}

	/**
	 * 删除黑名单
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean removeBlackList(int playerServerId, int playerId, int hisServerId, int hisId) {
		boolean result = DBToolkit.deleteBlackList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			return result = RedisToolkit.removeBlackList(playerServerId, playerId, hisServerId, hisId);
		}
		return result;
	}
	
	/**
	 * 判断双方是否有黑名单的关系
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static int isBlackList(int playerServerId, int playerId, int hisServerId, int hisId) {
		boolean result = RedisToolkit.isBlackList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			return Const.BLACK_LIST_STATUS_ADD;
		}
		
		result = RedisToolkit.isBlackList(hisServerId, hisId, playerServerId, playerId);
		if (result) {
			return Const.BLACK_LIST_STATUS_ADDED;
		}
		
		return Const.BLACK_LIST_STATUS_OK;
	}
	
	/**
	 * 添加黑名单
	 * @param pServerId
	 * @param pId
	 * @param tServerId
	 * @param tId
	 * @return
	 */
	public static boolean insertBlackList(int pServerId, int pId, int tServerId, int tId) {
		// 如果已经添加对方到黑名单了，返回
		if (RedisToolkit.isBlackList(pServerId, pId, tServerId, tId)) {
			return false;
		}
		
		int genId = DBToolkit.insertBlackList(pServerId, pId, tServerId, tId);
		boolean result = genId > 0 ? true : false;
		if (result) {
			return RedisToolkit.insertBlackList(pServerId, pId, tServerId, tId, genId);
		}
		return result;
	}

	/**
	 * 获取关注列表
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static List<PlayerInfo> getFollowList(int playerServerId, int playerId, int pageNum, int pageSize) {
		Set<String> idSet = RedisToolkit.getFollowList(playerServerId, playerId, pageNum, pageSize);
		List<PlayerInfo> list = new ArrayList<PlayerInfo>();
		PlayerInfo selfPi = StorageService.getPlayerInfo(playerServerId, playerId);

		if (null != idSet) {
			for (String id : idSet) {
				PlayerInfo pi = StorageService.getPlayerInfo(id, true);
				if (null != pi) {
					pi.calDiffTS();
					if (null != selfPi) {
						pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
								pi.getLat(), pi.getLng()));
					}
					list.add(pi);
				}
			}
		}
		return list;
	}

	/**
	 * 插入关注列表
	 * 		-- playerId 关注 hisId
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean insertFollowList(int playerServerId, int playerId, int hisServerId, int hisId) {
		int genId = DBToolkit.insertFollowList(playerServerId, playerId, hisServerId, hisId);
		boolean result = genId > 0 ? true : false;
		if (result) {
			result = RedisToolkit.insertFollowList(playerServerId, playerId, hisServerId, hisId, genId);
			// 给hisId插入一条被关注信息
			RedisToolkit.insertFollowMsg(playerServerId, playerId, hisServerId, hisId, System.currentTimeMillis(), false);
		}
		return result;
	}

	/**
	 * 删除关注人
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean removeFollowList(int playerServerId, int playerId, int hisServerId, int hisId) {
		boolean result = DBToolkit.deleteFollowList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			return result = RedisToolkit.removeFollowList(playerServerId, playerId, hisServerId, hisId);
		}
		return result;
	}
	
	/**
	 * 判断 hisId 是否在 playerId 的关注列表内
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean isInFollowList(int playerServerId, int playerId, int hisServerId, int hisId) {
		return RedisToolkit.isInFollowList(playerServerId, playerId, hisServerId, hisId);
	}
	
	public static boolean isInFollowList(String uid1, String uid2) {
		return RedisToolkit.isInFollowList(uid1, uid2);
	}

	/**
	 * 根据一定的策略获取玩家
	 * @param selfServerId
	 * @param selfPlayerId
	 * @return
	 */
	public static List<PlayerInfo> getRandomPlayerInfo(IndexReqMsg req, int selfServerId, int selfPlayerId, long ts) {
		StringBuilder log = new StringBuilder("TIMES.getRandomPlayerInfo serverId=" + selfServerId + ", playerId=" + selfPlayerId);
		final long now = System.currentTimeMillis();
		long t = now;
		long newT = 0;
		
		List<String> uidList = RedisToolkit.getLastFresh(20); 
		
		newT = System.currentTimeMillis();
		log.append(" getLastFresh=" + (newT - t));
		t = newT;
		
		List<PlayerInfo> retList = new ArrayList<PlayerInfo>();
		if (null != uidList && !uidList.isEmpty()) {
			Collections.shuffle(uidList);
			
			String selfUID = CommonToolkit.toUID(selfServerId, selfPlayerId);
			for (String uid : uidList) {
				// 如果是自己的新鲜事，跳过
				if (uid.equals(selfUID)) {
					continue;
				}
				
				PlayerInfo pi = StorageService.getPlayerInfo(uid, true);
				if (null != pi) {
					ImageWrapper.wrapImage(req, pi);
					pi.setFollow(StorageService.isInFollowList(selfServerId, selfPlayerId, pi.getServerId(), pi.getPlayerId()));
					pi.calDiffTS();
					retList.add(pi);
					
					if (retList.size() >= Const.INDEX_GET_LIMIT) {
						break;
					}
				}
			}
			
			newT = System.currentTimeMillis();
			log.append(" proccessUidList2PlayerInfoList=" + (newT - t));
			t = newT;
		}
		
		long newNow = System.currentTimeMillis();
		long timeDiff = newNow - now;
		if (timeDiff > 50L) {
			log.append(" timeDiff=" + timeDiff);
			System.out.println( log.toString() );
		}
		
		return retList;
	}

	/**
	 * 获取粉丝列表
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static List<PlayerInfo> getFanList(int playerServerId, int playerId, int pageNum, int pageSize) {
		Set<String> idSet = RedisToolkit.getFanList(playerServerId, playerId, pageNum, pageSize);
		List<PlayerInfo> list = new ArrayList<PlayerInfo>();
		PlayerInfo selfPi = StorageService.getPlayerInfo(playerServerId, playerId);

		if (null != idSet) {
			for (String id : idSet) {
				PlayerInfo pi = StorageService.getPlayerInfo(id, true);
				if (null != pi) {
					pi.setFollow(StorageService.isInFollowList(playerServerId, playerId, pi.getServerId(), pi.getPlayerId()));
					pi.calDiffTS();
					if (null != selfPi) {
						pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
								pi.getLat(), pi.getLng()));
					}
					list.add(pi);
				}
			}
		}
		return list;
	}
	
	/**
	 * 分页获取关注排行榜
	 * @param serverId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static TmpFollowRankItem getFollowRank(int serverId, int playerId, int pageNum, int pageSize) {
		return RedisToolkit.getFollowRank(serverId, playerId, pageNum, pageSize);
	}

	/**
	 * 搜索某玩家
	 * @param name
	 * @return
	 */
	public static List<PlayerInfo> searchPlayer(String name, int serverId, int playerId) {
		return DBToolkit.searchPlayer(name, serverId, playerId);
	}
	
	/**
	 * 存储未读的漂流瓶
	 * @param sender
	 * @param text
	 * @param vid
	 * @return
	 */
	public static boolean insertUnreadBottle(String sender, String text, String vid) {
		long now = System.currentTimeMillis();
		long id = DBBottleToolkit.insertBottle(sender, text, vid);
		if (id > 0) {
			Bottle bottle = new Bottle();
			bottle.setId(id);
			bottle.setSender(sender);
			bottle.setText(text);
			bottle.setVid(vid);
			bottle.setSendTime(now);
			return RedisToolkit.putUnreadBottle(bottle);
		}
		return false;
	}

	/**
	 * 随机捞取未读瓶子
	 * @param uid
	 */
	public static Bottle fetchUnreadBottle(String uid) {
		Bottle bottle = RedisToolkit.fetchUnreadBottle(uid);
		if (null != bottle) {
			// 设置捞取者
			bottle.setReceiver(uid);
		}
		
		return bottle;
	}
	
	/**
	 * 初始化我的瓶子ID列表
	 * @param uid
	 */
	public static void initMyBottleIdList(String uid) {
		List<Long> bottleIdList = DBBottleToolkit.selectMyBottleId(uid, 0);
		if (null != bottleIdList && false == bottleIdList.isEmpty()) {
			RedisToolkit.initMyBottleIdList(0, uid, bottleIdList);
		}
		
		bottleIdList = DBBottleToolkit.selectMyBottleId(uid, 1);
		if (null != bottleIdList && false == bottleIdList.isEmpty()) {
			RedisToolkit.initMyBottleIdList(1, uid, bottleIdList);
		}
		
	}

	/**
	 * 获取与自己相关的瓶子
	 * @return
	 */
	public static List<BottleListItem> getMyBottle(String uid, int type, int pageNum) {
		List<Long> bottleIdList = RedisToolkit.getMyBottleIdList(type, uid, pageNum);	
		List<BottleListItem> retList = new ArrayList<BottleListItem>();
		long now = System.currentTimeMillis();
		for (Long bottleId : bottleIdList) {
			Bottle bottle = StorageService.getBottleById(bottleId);
			if (null == bottle) {
				continue;
			}
			
			BottleListItem item = new BottleListItem();
			PlayerInfo pi = null;
			PlayerInfo selfPi = null;
			
			if (0 == type) {
				pi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
				selfPi = StorageService.getPlayerInfo(bottle.getSender(), false);
				item.setReaded(bottle.isSenderReaded());
				item.setRewarded(bottle.isSenderRewarded());
			} else {
				pi = StorageService.getPlayerInfo(bottle.getSender(), false);
				selfPi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
				item.setReaded(bottle.isReceiverReaded());
				item.setRewarded(bottle.isReceiverRewarded());
			}
			
			if (null == pi || selfPi == null) {
				continue;
			}
			
			item.setBottleId(bottle.getId());
			item.setImageId(pi.getImageId());
			item.setLevel(pi.getLevel());
			item.setName(pi.getName());
			item.setPlayerId(pi.getPlayerId());
			item.setServerId(pi.getServerId());
			item.setSendTime(now - bottle.getSendTime());
			item.setRewardId(bottle.getRewardId());
			item.setDistance((int) GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), pi.getLat(), pi.getLng()));
			
			retList.add(item);
		}
		
		return retList;
	}
	
	/**
	 * 根据瓶子id查找瓶子
	 * @param bottleId
	 * @return
	 */
	public static Bottle getBottleById(long bottleId) {
		Bottle bottle = RedisToolkit.getBottleContent(bottleId);
		
		if (null == bottle) {
			bottle = DBBottleToolkit.selectBottleById(bottleId);
			if (bottle == null) {
				return null;
			}
			
			RedisToolkit.cacheBottleContent(bottle, true);
		}
		return bottle;
	}
	
	/**
	 * 更新瓶子
	 * @param bottle
	 * @return
	 */
	public static boolean updateBottle(Bottle bottle) {
		RedisToolkit.cacheBottleContent(bottle, false);
		return DaoFactory.getBottleDao().updateBottle(bottle);
	}
	
	/**
	 * 获取未读的瓶子数量
	 * @param isThrowOrFetch
	 * @param uid
	 * @return
	 */
	public static int getUnreadNum(boolean isThrowOrFetch, String uid) {
		return DBBottleToolkit.selectUnreadNum(isThrowOrFetch, uid);
	}
	
	/**
	 * 获取未读的瓶子数量
	 * @param uid
	 * @return
	 */
	public static int getUnreadNum(String uid) {
		int ret = RedisToolkit.getUnreadBottleNum(uid);
		if (-1 == ret) {
			// 初始化
			ret = StorageService.initUnreadBottleNum(uid);
		}
		
		return ret;
	}
	
	public static int initUnreadBottleNum(String uid) {
		StringBuilder log = new StringBuilder("TIMES.initUnreadBottleNum uid=" + uid);
		final long now = System.currentTimeMillis();
		long t = now;
		long newT = 0;
		
		try {
			int ret = DBBottleToolkit.selectUnreadNum(true, uid);
			newT = System.currentTimeMillis();
			log.append(" selectUnreadNum=" + (newT - t));
			t = newT;
			
			RedisToolkit.initUnreadBottleNum(uid, ret);
			newT = System.currentTimeMillis();
			log.append(" initUnreadBottleNum=" + (newT - t));
			t = newT;
			
			return ret;
		}
		finally {
			newT = System.currentTimeMillis();
			long timeDiff = newT - now;
			if (timeDiff > 50L) {
				log.append(" timeDiff=" + timeDiff);
				System.out.println( log.toString() );
			}
		}
	}
	
	/**
	 * 批量删除瓶子
	 * @param bottleIdList
	 * @return
	 */
	public static boolean deleteBottle(long bottleId) {
		return DBBottleToolkit.deleteBottle(bottleId);
	}
	
	/**
	 * 获取 playerId被关注的消息
	 * @param playerServerId
	 * @param playerId
	 * @param lang
	 * @param updateWhenRead
	 * 		true: 读的时候，把状态给更新成已读
	 * @return
	 */
	public static List<FollowMsg> readFollowMsgList(int playerServerId, int playerId, int lang, boolean updateWhenRead) {
		List<FollowMsg> list = new ArrayList<FollowMsg>();
		
		Map<String, FollowRecord> map = RedisToolkit.readFollowMsg(playerServerId, playerId, updateWhenRead);
		
		if (updateWhenRead) {
			String followed = CommonToolkit.toUID(playerServerId, playerId);
			
			List<String> followerList = new ArrayList<String>();
			for (Map.Entry<String, FollowRecord> entry : map.entrySet()) {
				if (!entry.getValue().isRead()) {
					followerList.add(entry.getKey());
				}
			}
			
			DBToolkit.readFollowMsg(followed, followerList);
		}
		
		for (Map.Entry<String, FollowRecord> entry : map.entrySet()) {
			PlayerInfo fans = StorageService.getPlayerInfo(entry.getKey(), true);
			if (null != fans) {
				FollowMsg msg = new FollowMsg ();
				msg.setReceiver(playerId);
				msg.setSender(fans.getPlayerId());
				msg.setSenderServerId(fans.getServerId());
				msg.setSenderImageId(fans.getImageId());
				msg.setStrTime(CommonToolkit.getFormatDateString(Long.valueOf(entry.getValue().getFollowTime())));
				msg.setLongStrTime(entry.getValue().getFollowTime());
				msg.setRead( entry.getValue().isRead() );
				
				String taStr = MultiLanguageManager.getString(lang, fans.isGender() ? MultiLangConstant.HIM : MultiLangConstant.HER);
				if (StorageService.isInFollowList(playerServerId, playerId, fans.getServerId(), fans.getPlayerId())) {
					// playerId 也是 fans.playerId 的粉丝
					msg.setContent( String.format(MultiLanguageManager.getString(lang, MultiLangConstant.FOLLOW_MSG1), fans.getName(), taStr) );
				}
				else {
					msg.setContent( String.format(MultiLanguageManager.getString(lang, MultiLangConstant.FOLLOW_MSG2), fans.getName(), taStr) );
				}
				
				list.add(msg);
			}
		}
		
		return list;
	}
	
	/**
	 * 清空 playerServerId被关注的消息
	 * @param playerServerId
	 * @param playerId
	 * @return
	 */
	public static boolean clearFollowMsg (int playerServerId, int playerId) {
		boolean result = DBToolkit.clearFollowMsg(playerServerId, playerId);
		if (result) {
			result = RedisToolkit.clearFollowMsg(playerServerId, playerId);
		}
		
		return result;
	}
}
