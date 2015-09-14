package mango.condor.domain.msg;

import java.util.HashMap;
import java.util.Map;

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
import mango.condor.domain.msg.map.MapExploreMsgReq;
import mango.condor.domain.msg.map.MapRangeListMsgReq;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-10 下午3:28:02
 * @Description
 */
public class LBSMessageDefine {
	
	// 发送用户数据，如经纬度，进入LBS页面时调用
	public static final short LBS_CACHE_PLAYER_INFO = 10000;
	
	// 请求附近的人的列表
	public static final short LBS_GET_NEIGHBOUR = 10001;

	// 发送私聊
	// 对应游戏服1902
	public static final short CHAT_SEND_MY_REQUEST = 10002;

	// 获取私聊会话列表
	// 对应游戏服1906
	public static final short CHAT_LIST_MY_REQUEST = 10003;
	
	// 与某朋友私聊列表内容
	// 对应游戏服1907
	public static final short CHAT_LIST_CONTENT_REQUEST = 10004;
	
	// 删除私聊
	// 对应游戏服1910
	public static final short CHAT_DELETE_REQUEST = 10005;

	// 获得某会话在登录之前的私聊记录
	// 对应游戏服1912
	public static final short CHAT_GET_HISTORY_CONTENT_REQUEST = 10006;
	
	// 插入黑名单
	public static final short CHAT_INSERT_BLACKLIST_REQUEST = 10007;
	
	// 删除黑名单
	public static final short CHAT_REMOVE_BLACKLIST_REQUEST = 10008;
	
	// 获取黑名单列表，有分页
	public static final short CHAT_GET_BLACKLIST_REQUEST = 10009;
	
	// 加入关注
	public static final short LBS_INSERT_FOLLOWLIST_REQUEST = 10010;
	
	// 删除关注
	public static final short LBS_DELETE_FOLLOWLIST_REQUEST = 10011;
	
	// 获取关注列表，有分页
	public static final short LBS_GET_FOLLOWLIST_REQUEST = 10012;
	
	// LBS首页
	public static final short LBS_INDEX_REQUEST = 10013;					// TODO 优化
	
	// 获取粉丝列表
	public static final short LBS_GET_FAN_LIST_REQUEST = 10014;
	
	// 查看玩家详细信息
	public static final short LBS_GET_PLAYER_INFO_REQUEST = 10015;
	
	// 查看关注排行榜
	public static final short LBS_GET_FOLLOW_RANK_REQUEST = 10016;
	
	// 根据玩家名搜索玩家
	public static final short LBS_SEARCH_PLAYER_REQUEST = 10017;
	
	// 登陆时加载聊天缓存
	public static final short CHAT_INIT_CHAT_CACHE_REQUEST = 10018;
	
	// 扔瓶子
	public static final short BOTTLE_THROW_REQUEST = 10019;
	
	// 捞瓶子
	public static final short BOTTLE_FETCH_REQUEST = 10020;
	
	// 我的瓶子列表
	public static final short BOTTLE_MY_BOTTLE_REQUEST = 10021;
	
	// 查看瓶子内容
	public static final short BOTTLE_GET_BOTTLE_CONTENT_REQUEST = 10022;
	
	// 领取回复瓶子的奖励
	public static final short BOTTLE_GET_REPLY_REWARD_REQUEST = 10023;
	
	// 删除瓶子
	public static final short BOTTLE_DELETE_BOTTLE_REQUEST = 10024;
	
	// 获取瓶子未读条数
	public static final short BOTTLE_GET_UNREAD_NUM_REQUEST = 10025;
	
	// 获取未读私信数量
	public static final short GET_NEW_CHAT_NUM_REQUEST = 10026;				// TODO 优化
	
	// 获取被关注的消息列表
	public static final short LBS_GET_FOLLOW_MSG_LIST_REQUEST = 10027;
	
	// 清空被关注的消息
	public static final short LBS_CLEAR_FOLLOW_MSG_REQUEST = 10028;
	
	/** 世界地图范围内随机玩家列表    */
	public static final short MAP_RANGE_LIST_REQUEST = 10029;
	
	/** 世界地图探索    */
	public static final short MAP_EXPLOER_REQUEST = 10030;
	
	public static final short BOTTLE_REPLY = 10100;						// 漂流瓶 - 回复瓶子并获取奖励
	public static final short BOTTLE_SENDER_HARVEST_REWARD = 10101; 	// 漂流瓶 - 扔瓶子的人 获取 回复奖励
	public static final short BOTTLE_VIEW_CONTENT = 10102;				// 漂流瓶 - 查看瓶子内容
	
	public static final short CHAT_DELETE_CHAT = 10200;			// 私聊 - 删除私聊记录
	
	public static volatile Map<Short, Class<? extends LBSRequestMessage>> msgClassMap;
	
	/**
	 * 初始化
	 */
	public static void init() {
		msgClassMap = new HashMap<Short, Class<? extends LBSRequestMessage>>();
		msgClassMap.put(CHAT_SEND_MY_REQUEST, ChatSendMyReqMsg.class);
		msgClassMap.put(CHAT_LIST_MY_REQUEST, ChatListMyReqMsg.class);
		msgClassMap.put(CHAT_LIST_CONTENT_REQUEST, ChatListMyContentReqMsg.class);
		msgClassMap.put(CHAT_DELETE_REQUEST, ChatDeleteMyReqMsg.class);
		msgClassMap.put(CHAT_GET_HISTORY_CONTENT_REQUEST, ChatHistoryReqMsg.class);
		msgClassMap.put(LBS_CACHE_PLAYER_INFO, CachePlayerInfoReqMsg.class);
		msgClassMap.put(LBS_GET_NEIGHBOUR, GetNeighbourReqMsg.class);
		msgClassMap.put(CHAT_INSERT_BLACKLIST_REQUEST, InsertBlackListReqMsg.class);
		msgClassMap.put(CHAT_GET_BLACKLIST_REQUEST, GetBlackListReqMsg.class);
		msgClassMap.put(CHAT_REMOVE_BLACKLIST_REQUEST, RemoveBlackListReqMsg.class);
		msgClassMap.put(LBS_GET_FOLLOWLIST_REQUEST, GetFollowListReqMsg.class);
		msgClassMap.put(LBS_INSERT_FOLLOWLIST_REQUEST, InsertFollowListReqMsg.class);
		msgClassMap.put(LBS_DELETE_FOLLOWLIST_REQUEST, DeleteFollowListReqMsg.class);
		msgClassMap.put(LBS_INDEX_REQUEST, IndexReqMsg.class);
		msgClassMap.put(LBS_GET_FAN_LIST_REQUEST, GetFanListReqMsg.class);
		msgClassMap.put(LBS_GET_PLAYER_INFO_REQUEST, GetPlayerInfoReqMsg.class);
		msgClassMap.put(LBS_GET_FOLLOW_RANK_REQUEST, GetFollowRankReqMsg.class);
		msgClassMap.put(LBS_SEARCH_PLAYER_REQUEST, SearchPlayerReqMsg.class);
		msgClassMap.put(CHAT_INIT_CHAT_CACHE_REQUEST, InitChatCacheOnLoginReqMsg.class);
		msgClassMap.put(BOTTLE_THROW_REQUEST, ThrowBottleReqMsg.class);
		msgClassMap.put(BOTTLE_FETCH_REQUEST, FetchBottleReqMsg.class);
		msgClassMap.put(BOTTLE_MY_BOTTLE_REQUEST, MyBottleReqMsg.class);
		msgClassMap.put(BOTTLE_GET_BOTTLE_CONTENT_REQUEST, GetBottleContentReqMsg.class);
		msgClassMap.put(BOTTLE_GET_REPLY_REWARD_REQUEST, GetReplyRewardReqMsg.class);
		msgClassMap.put(BOTTLE_DELETE_BOTTLE_REQUEST, DeleteBottleReqMsg.class);
		msgClassMap.put(BOTTLE_GET_UNREAD_NUM_REQUEST, GetUnreadNumReqMsg.class);
		msgClassMap.put(GET_NEW_CHAT_NUM_REQUEST, GetNewChatNumReqMsg.class);
		msgClassMap.put(LBS_GET_FOLLOW_MSG_LIST_REQUEST, GetFollowMsgReqMsg.class);
		msgClassMap.put(LBS_CLEAR_FOLLOW_MSG_REQUEST, ClearFollowMsgReqMsg.class);
		msgClassMap.put(MAP_RANGE_LIST_REQUEST, MapRangeListMsgReq.class);
		msgClassMap.put(MAP_EXPLOER_REQUEST, MapExploreMsgReq.class);
		
		msgClassMap.put(BOTTLE_REPLY, ReplyBottleReqMsg.class);
		msgClassMap.put(BOTTLE_SENDER_HARVEST_REWARD, SenderHarvestRewardReqMsg.class);
		msgClassMap.put(BOTTLE_VIEW_CONTENT, ViewBottleContentReqMsg.class);
		
		msgClassMap.put(CHAT_DELETE_CHAT, DeleteChatReqMsg.class);
		
	}
	
	/**
	 * 获取对应消息的Class对象
	 * @param msgId
	 * @return
	 */
	public static Class<? extends LBSRequestMessage> getMsgClass(short msgId) {
		return msgClassMap.get(msgId);
	}
}
