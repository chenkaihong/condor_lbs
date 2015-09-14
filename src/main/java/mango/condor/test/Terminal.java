package mango.condor.test;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-11 下午4:39:49
 * @Description
 */
public class Terminal {

//	private static final String HELP_MSG = "请输入操作\n1:存储你的个人信息\n2:获取私聊会话列表\n3:聊天\n4:获取某会话聊天消息\n"
//			+ "5:删除会话\n6:获取历史聊天记录\n7:插入黑名单\n8:删除黑名单\n9:查看黑名单\n10:插入关注\n"
//			+ "11:删除关注\n12:查看关注列表\n13:获取附近的人\n14:获取关注排行榜\n15:LBS首页\n16:获取粉丝列表\n17:查看玩家详细数据\n18:查找玩家" + "\n19:扔漂流瓶\n20:捞瓶子\n21:查看瓶子列表\n22:查看地图玩家列表";
//
//	private static final String URL_PREFIX = "http://127.0.0.1:18091/lbs/";
//
////	private static final String URL_PREFIX = "http://10.20.202.66:18091/lbs/";
//	// private static final String URL_PREFIX = "http://lbs.bwzq.fytxonline.com:8090/lbs/";
//
//	private static final String CHAT = "lbs";
//
//	private static Scanner scanner = new Scanner(System.in, Const.CHARSET);
//	private static Gson gson = new Gson();
//
//	public static void main(String[] args) {
//
//		while (true) {
//			System.out.println(HELP_MSG);
//
//			int option = scanner.nextInt();
//			switch (option) {
//			case 1:
//				processPlayerInfo();
//				break;
//			case 2:
//				processGetChatSessionList();
//				break;
//			case 3:
//				processSendChat();
//				break;
//			case 4:
//				processGetChatContent();
//				break;
//			case 5:
//				processDeleteChatSession();
//				break;
//			case 6:
//				processGetHistoryChatContent();
//				break;
//			case 7:
//				processInsertBlackList();
//				break;
//			case 8:
//				processRemoveBlackList();
//				break;
//			case 9:
//				processGetBlackList();
//				break;
//			case 10:
//				processInsertFollowList();
//				break;
//			case 11:
//				processRemoveFollowList();
//				break;
//			case 12:
//				processGetFollowList();
//				break;
//			case 13:
//				processGetNeighbour();
//				break;
//			case 14:
//				processGetFollowRank();
//				break;
//			case 15:
//				processLBSIndex();
//				break;
//			case 16:
//				processGetFanList();
//				break;
//			case 17:
//				processGetPlayerInfo();
//				break;
//			case 18:
//				processSearchPlayer();
//				break;
//			case 19:
//				processThrowBottle();
//				break;
//			case 20:
//				processFetchBottle();
//				break;
//			case 21:
//				processMyBottle();
//				break;
//			case 22:
//				processRangList();
//				break;
//			default:
//				System.out.println("不合法");
//				break;
//			}
//		}
//	}
//
//	/**
//	 * 
//	 */
//	private static void processRangList() {
//		MapRangeListMsgReq req = new MapRangeListMsgReq();
//		req.setMessageId(LBSMessageDefine.MAP_RANGE_LIST_REQUEST);
//		//103.3471374511719|123.3471374511719|13.14400482177734|33.144004821777344
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入范围，|分隔");
//		String[] range = scanner.next().split("\\|");
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setMinLng(Double.parseDouble(range[0]));
//		req.setMaxLng(Double.parseDouble(range[1]));
//		req.setMinLat(Double.parseDouble(range[2]));
//		req.setMaxLat(Double.parseDouble(range[3]));
//		
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processMyBottle() {
//		MyBottleReqMsg req = new MyBottleReqMsg();
//		req.setMessageId(LBSMessageDefine.BOTTLE_MY_BOTTLE_REQUEST);
//
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//
//		System.out.println("请输入类型，0为扔出去的瓶子，1为捞到的瓶子");
//		int type = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setType(type);
//
//		req.setPageNum(1);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processFetchBottle() {
//		FetchBottleReqMsg req = new FetchBottleReqMsg();
//		req.setMessageId(LBSMessageDefine.BOTTLE_FETCH_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processThrowBottle() {
//		ThrowBottleReqMsg req = new ThrowBottleReqMsg();
//		req.setMessageId(LBSMessageDefine.BOTTLE_THROW_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入瓶子文字内容");
//		String text = scanner.next();
//		/*
//		 * System.out.println("请输入语音id"); String vid = scanner.next();
//		 */
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setText(text);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processSearchPlayer() {
//		SearchPlayerReqMsg req = new SearchPlayerReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_SEARCH_PLAYER_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家名");
//		String name = scanner.next();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setName(name);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetPlayerInfo() {
//		GetPlayerInfoReqMsg req = new GetPlayerInfoReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_GET_PLAYER_INFO_REQUEST);
//
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetFanList() {
//		GetFanListReqMsg req = new GetFanListReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_GET_FAN_LIST_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入要查看第几页");
//		int pageNum = scanner.nextInt();
//		System.out.println("请输入每页的条目数");
//		int pageSize = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setPageNum(pageNum);
//		req.setPageSize(pageSize);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processLBSIndex() {
//		IndexReqMsg req = new IndexReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_INDEX_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入ts");
//		long ts = scanner.nextLong();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setTs(ts);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		params.put("fromGS", "1");
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetFollowRank() {
//		GetFollowRankReqMsg req = new GetFollowRankReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_GET_FOLLOW_RANK_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入要查看第几页");
//		int pageNum = scanner.nextInt();
//		System.out.println("请输入每页的条目数");
//		int pageSize = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setPageNum(pageNum);
//		req.setPageSize(pageSize);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetNeighbour() {
//		GetNeighbourReqMsg req = new GetNeighbourReqMsg();
//		req.setServerId(5);
//		req.setPlayerId(5244248);
//		req.setLat(23.0);
//		req.setLng(113.0);
//		req.setLang(0);
//		req.setGender(Const.LBS_GEO_HASH_ALL);
//		req.setMore(false);
//		req.setMessageId(LBSMessageDefine.LBS_GET_NEIGHBOUR);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetFollowList() {
//		GetFollowListReqMsg req = new GetFollowListReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_GET_FOLLOWLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入要查看第几页");
//		int pageNum = scanner.nextInt();
//		System.out.println("请输入每页的条目数");
//		int pageSize = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setPageNum(pageNum);
//		req.setPageSize(pageSize);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processRemoveFollowList() {
//		DeleteFollowListReqMsg req = new DeleteFollowListReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_DELETE_FOLLOWLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processInsertFollowList() {
//		InsertFollowListReqMsg req = new InsertFollowListReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_INSERT_FOLLOWLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetBlackList() {
//		GetBlackListReqMsg req = new GetBlackListReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_GET_BLACKLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入要查看第几页");
//		int pageNum = scanner.nextInt();
//		System.out.println("请输入每页的条目数");
//		int pageSize = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setPageNum(pageNum);
//		req.setPageSize(pageSize);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processRemoveBlackList() {
//		RemoveBlackListReqMsg req = new RemoveBlackListReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_REMOVE_BLACKLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processInsertBlackList() {
//		InsertBlackListReqMsg req = new InsertBlackListReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_INSERT_BLACKLIST_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetHistoryChatContent() {
//		ChatDeleteMyReqMsg req = new ChatDeleteMyReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_GET_HISTORY_CONTENT_REQUEST);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processDeleteChatSession() {
//		ChatDeleteMyReqMsg req = new ChatDeleteMyReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_DELETE_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetChatContent() {
//		ChatListMyContentReqMsg req = new ChatListMyContentReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_LIST_CONTENT_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setHisId(targetPlayerId);
//		req.setHisServerId(targetServerId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processSendChat() {
//		ChatSendMyReqMsg req = new ChatSendMyReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_SEND_MY_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入对方玩家id");
//		int targetPlayerId = scanner.nextInt();
//		System.out.println("请输入对方玩家服务器id");
//		int targetServerId = scanner.nextInt();
//		System.out.println("请输入聊天内容");
//		String content = scanner.next();
//
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//		req.setReceiver(targetPlayerId);
//		req.setReceiverServerId(targetServerId);
//		req.setContent(content);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processGetChatSessionList() {
//		ChatListMyReqMsg req = new ChatListMyReqMsg();
//		req.setMessageId(LBSMessageDefine.CHAT_LIST_MY_REQUEST);
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
//
//	private static void processPlayerInfo() {
//		CachePlayerInfoReqMsg req = new CachePlayerInfoReqMsg();
//		req.setMessageId(LBSMessageDefine.LBS_CACHE_PLAYER_INFO);
//
//		System.out.println("请输入玩家id");
//		int playerId = scanner.nextInt();
//		System.out.println("请输入服务器id");
//		int serverId = scanner.nextInt();
//		System.out.println("请输入名字");
//		String name = scanner.next();
//		System.out.println("请输入等级");
//		int level = scanner.nextInt();
//		System.out.println("请输入imageId");
//		String imageId = scanner.next();
//		System.out.println("请输入voiceId");
//		String voiceId = scanner.next();
//		System.out.println("请输入性别");
//		boolean gender = scanner.nextBoolean();
//		System.out.println("请输入服务器名称");
//		String serverName = scanner.next();
//
//		// 经纬度先略过
//
//		req.setLang(0);
//		req.setPlayerId(playerId);
//		req.setServerId(serverId);
//
//		PlayerInfo pi = new PlayerInfo();
//		pi.setPlayerId(playerId);
//		pi.setServerId(serverId);
//		pi.setName(name);
//		pi.setLevel(level);
//		pi.setImageId(imageId);
//		pi.setVoiceId(voiceId);
//		pi.setGender(gender);
//		pi.setServerName(serverName);
//		pi.setLat(39);
//		pi.setLng(112);
//		pi.setTeacherName("haha");
//		pi.setGangName("haha");
//		pi.setMeili(0);
//		pi.setPower(33);
//		pi.setSign("shit");
//
//		req.setPi(pi);
//		String url = URL_PREFIX + CHAT;
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("data", gson.toJson(req));
//		params.put("msg_id", String.valueOf(req.getMessageId()));
//		String ret = CommonToolkit.getPostResult(url, params, true);
//		System.out.println(ret);
//	}
}
