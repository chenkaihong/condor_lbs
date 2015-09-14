package mango.condor.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mango.condor.cache.CacheFactory;
import mango.condor.dao.DaoFactory;
import mango.condor.domain.bottle.Bottle;
import mango.condor.domain.bottle.BottleContent;
import mango.condor.domain.bottle.BottleInfo;
import mango.condor.domain.bottle.BottleListItem;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSResponseMessage;
import mango.condor.domain.msg.bottle.DeleteBottleReqMsg;
import mango.condor.domain.msg.bottle.DeleteBottleRspMsg;
import mango.condor.domain.msg.bottle.FetchBottleReqMsg;
import mango.condor.domain.msg.bottle.FetchBottleRspMsg;
import mango.condor.domain.msg.bottle.GetBottleContentReqMsg;
import mango.condor.domain.msg.bottle.GetBottleContentRspMsg;
import mango.condor.domain.msg.bottle.GetReplyRewardReqMsg;
import mango.condor.domain.msg.bottle.GetReplyRewardRspMsg;
import mango.condor.domain.msg.bottle.GetUnreadNumReqMsg;
import mango.condor.domain.msg.bottle.GetUnreadNumRspMsg;
import mango.condor.domain.msg.bottle.MyBottleReqMsg;
import mango.condor.domain.msg.bottle.MyBottleRspMsg;
import mango.condor.domain.msg.bottle.ReplyBottleReqMsg;
import mango.condor.domain.msg.bottle.ReplyBottleRspMsg;
import mango.condor.domain.msg.bottle.SenderHarvestRewardReqMsg;
import mango.condor.domain.msg.bottle.SenderHarvestRewardRspMsg;
import mango.condor.domain.msg.bottle.ThrowBottleReqMsg;
import mango.condor.domain.msg.bottle.ThrowBottleRspMsg;
import mango.condor.domain.msg.bottle.ViewBottleContentReqMsg;
import mango.condor.domain.msg.bottle.ViewBottleContentRspMsg;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.GeoToolkit;
import mango.condor.toolkit.MultiLanguageManager;
import mango.condor.toolkit.RedisToolkit;
import mango.condor.util.RspMsgUtil;

import com.gzyouai.hummingbird.common.component.Pair;
import com.gzyouai.hummingbird.common.utils.CollectionUtil;
import com.gzyouai.hummingbird.common.utils.DateTimeUtil;
import com.gzyouai.hummingbird.common.utils.UIUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午11:07:10
 * @Description 
 */
public class BottleService {

	/**
	 * 扔瓶子
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage processThrowBottle(ThrowBottleReqMsg req) {
		ThrowBottleRspMsg resp = new ThrowBottleRspMsg(req);
		boolean isSuc = false;
		
		if (req.getVid() == null || req.getText() == null) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_STR_NOT_VALID));
			return resp;
		}
		
		if (req.getVid().isEmpty()) {
			// 文字 - 漂流瓶
			if (req.getText().isEmpty()) {
				resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_STR_NOT_VALID));
				return resp;
			}
			
			if (!CommonToolkit.isUTF8StringValid(req.getText())) {
				resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_STR_NOT_VALID));
				return resp;
			}
			
			req.setText( UIUtil.trim4PhoneUI(req.getText()) );
		}
		
		
		isSuc = StorageService.insertUnreadBottle(CommonToolkit.toUID(req.getServerId(), req.getPlayerId()), req.getText(), req.getVid());
		
		resp.setSuc(isSuc);
		return resp;
	}

	/**
	 * 捞瓶子
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processFetchBottle(FetchBottleReqMsg msg) {
		FetchBottleRspMsg resp = new FetchBottleRspMsg(msg);
		String uid = CommonToolkit.toUID(msg.getServerId(), msg.getPlayerId());
		Bottle foundBottle = StorageService.fetchUnreadBottle(uid);
		if (null != foundBottle) {
			Bottle bottle = StorageService.getBottleById(foundBottle.getId());
			if (null != bottle) {
				BottleContent bottleContent = new BottleContent();
				bottleContent.setBottleId(bottle.getId());
				bottleContent.setFollow(StorageService.isInFollowList(uid, bottle.getSender()));
				bottleContent.setMsg(bottle.getText());
				bottleContent.setVid(bottle.getVid());
				
				PlayerInfo pi = StorageService.getPlayerInfo(bottle.getSender(), false);
				if (null != pi) {
					bottleContent.setImageId(pi.getImageId());
					bottleContent.setName(pi.getName());
					bottleContent.setServerName(pi.getServerName());
					bottleContent.setSign(pi.getSign());
					bottleContent.setPlayerId(pi.getPlayerId());
					bottleContent.setServerId(pi.getServerId());
					ImageWrapper.wrapImage(msg, bottleContent);
				}
				
				// 发送者的未读瓶子数+1
				RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
				
				// 发送者要设置为未读
				bottle.setSenderReaded(false);
				
				// 接收者设置为已读
				if (false == bottle.isReceiverReaded()) {
					bottle.setReceiverReaded(true);
				}
				
				// 设置接收者的uid
				bottle.setReceiver(uid);
				
				// 更新时间
				bottle.setSendTime(System.currentTimeMillis());
				
				// 自己的瓶子id列表的缓存加上这个捞到的瓶子
				// 发送者的也得加上
				RedisToolkit.cacheMyBottleIdList(0, bottle.getSender(), bottle.getId());
				RedisToolkit.cacheMyBottleIdList(1, bottle.getReceiver(), bottle.getId());
				
				StorageService.updateBottle(bottle);
				
				resp.setBottleContent(bottleContent);
			}
		}
		
		resp.setSuc(true);
		return resp;
	}

	/**
	 * 查看跟自己相关的瓶子
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage getMyBottle (MyBottleReqMsg req) {
		int pageNum = req.getPageNum();
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		PlayerInfo player = StorageService.getPlayerInfo(serverId, playerId);
		if (player == null) {
			MyBottleRspMsg rsp = new MyBottleRspMsg(req);
			rsp.setSuc(true);
			rsp.setNotExistUser(true);
			return rsp;
		}
		String uid = player.getUid();
		
		final int limit = 10;
		int offset = (pageNum - 1) * limit;
		boolean throwOrFetch = req.getType() == 0;
		
		int total = 0;	// 瓶子总数
		List<BottleListItem> pagerBottleItems = null; // 当前分页数据
		
		Pair<Integer, List<Long>> pairBottleIdList = CacheFactory.getMyBottleIdListRedisCache().getMyBottleIds(uid, throwOrFetch, offset, limit);
		if (pairBottleIdList == null) {
			// 缓存没有，直接去DB里面取
			List<Bottle> bottleList = DaoFactory.getBottleDao().batchMyBottles(uid, throwOrFetch, 0, 600);
			if (bottleList == null) {
				return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.SYS_SERVER_ERROR);	
			}
			
			List<Long> bottleIdList = new ArrayList<Long>(bottleList.size());
			for (Bottle bottle : bottleList) {
				bottleIdList.add( bottle.getId() );
			}
			
			// cache data
			CacheFactory.getBottleRedisCache().setBottleList(bottleList);
			CacheFactory.getMyBottleIdListRedisCache().setMyBottleIds(uid, throwOrFetch, bottleIdList);
			
			total = bottleIdList.size();
			List<Bottle> pagerBottles = CollectionUtil.subList(bottleList, offset, offset + limit);
			pagerBottleItems = createBottleListItemList(player, pagerBottles);
		}
		else {
			total = pairBottleIdList.first;			
			List<Long> bottleIdList = pairBottleIdList.second;
			
			Map<Long, Bottle> bottleMap = batchBottle( new HashSet<Long>(bottleIdList) );
			List<Bottle> pagerBottles = new ArrayList<Bottle>(bottleMap.size());
			for (long bottleId : bottleIdList) {
				Bottle bottle = bottleMap.get(bottleId);
				if (bottle != null) {
					pagerBottles.add(bottle);
				}
			}
			pagerBottleItems = createBottleListItemList(player, pagerBottles);
		}
		
		for (BottleListItem item : pagerBottleItems) {
			ImageWrapper.wrapImage(req, item);	
		}
		int pageTotal = (total + limit - 1) / limit;
		
		if (pagerBottleItems != null) {
			for (BottleListItem item : pagerBottleItems) {
				String targetName = item.getName();
				if (targetName != null) {
					targetName = PlayerInfo.trimPlayerName(targetName);
					item.setName(targetName);
				}
			}
		}
		
		MyBottleRspMsg rsp = new MyBottleRspMsg(req);
		rsp.setSuc(true);
		rsp.setList(pagerBottleItems);
		rsp.setPageTotal(pageTotal);
		return rsp;
	}
	
	/**
	 * 批量获取瓶子
	 * @param bottleIdSet
	 * @return
	 */
	public static Map<Long, Bottle> batchBottle (Set<Long> bottleIdSet) {
		if (bottleIdSet.isEmpty()) {
			return new HashMap<Long, Bottle>();
		}
		List<Long> bottleIdList = new LinkedList<Long>(bottleIdSet);
		
		// 去cache里面获取数据
		Map<Long, Bottle> bottleMap = CacheFactory.getBottleRedisCache().batchBottle(bottleIdList);
		
		// 删除已cache的,保留未cache的
		Iterator<Long> it = bottleIdList.iterator();
		while (it.hasNext()) {
			Long bottleId = it.next();
			if (bottleMap.containsKey(bottleId)) {
				it.remove();
			}
		}
		
		if (!bottleIdList.isEmpty()) {
			// 去DB里面获取数据
			Map<Long, Bottle> map = DaoFactory.getBottleDao().batchBottle(bottleIdList);
			if (!CollectionUtil.isEmptyMap(map)) {
				bottleMap.putAll(map);
				// 缓存到Cache
				CacheFactory.getBottleRedisCache().setBottleList(new ArrayList<Bottle>(map.values()));
			}
		}
		
		return bottleMap;
	}
	
	/**
	 * 根据 Bottle 生成一个 BottleListItem
	 * @param selfPlayer
	 * @param bottleList
	 * @return
	 */
	private static List<BottleListItem> createBottleListItemList (PlayerInfo selfPlayer, List<Bottle> bottleList) {
		Set<String> uidSet = new HashSet<String>();
		for (Bottle bottle : bottleList) {
			uidSet.add( bottle.getSender() );
			if (bottle.getReceiver() != null) {
				uidSet.add(bottle.getReceiver());
			}
		}
		uidSet.remove(selfPlayer.getUid());
		
		Map<String, PlayerInfo> playerMap = PlayerService.batchPlayerInfo(uidSet);
		
		final long nowMillis = System.currentTimeMillis();
		List<BottleListItem> list = new ArrayList<BottleListItem>();
		for (Bottle bottle : bottleList) {			
			// 对方
			String oppositeUid = bottle.getSender().equals(selfPlayer.getUid()) ? bottle.getReceiver() : bottle.getSender();
			if (oppositeUid == null) {
				continue;
			}
			PlayerInfo oppositePlayer = playerMap.get(oppositeUid);
			if (oppositePlayer == null) {
				PlayerService.logLostUid(oppositeUid);
				continue;
			}

			BottleListItem item = createBottleListItem(selfPlayer, oppositePlayer, nowMillis, bottle);
			
			list.add(item);
		}
		
		return list;
	}

	/**
	 * 根据 Bottle 生成一个 BottleListItem
	 * @param selfPlayer
	 * @param oppositePlayer
	 * @param nowMillis
	 * @param bottle
	 * @return
	 */
	private static BottleListItem createBottleListItem(PlayerInfo selfPlayer, PlayerInfo oppositePlayer, final long nowMillis, Bottle bottle) {
		BottleListItem item = new BottleListItem();
		
		if (bottle.getSender().equals(selfPlayer.getUid())) {
			item.setReaded(bottle.isSenderReaded());
			item.setRewarded(bottle.isSenderRewarded());
		}
		else {
			item.setReaded(bottle.isReceiverReaded());
			item.setRewarded(bottle.isReceiverRewarded());				
		}	
		
		item.setBottleId(bottle.getId());
		item.setImageId(oppositePlayer.getImageId());
		item.setLevel(oppositePlayer.getLevel());
		item.setName(oppositePlayer.getName());
		item.setPlayerId(oppositePlayer.getPlayerId());
		item.setServerId(oppositePlayer.getServerId());
		item.setSendTime(nowMillis - bottle.getSendTime());
		item.setRewardId(bottle.getRewardId());
		item.setDistance((int) GeoToolkit.distanceInMeters(selfPlayer.getLat(), selfPlayer.getLng(), oppositePlayer.getLat(), oppositePlayer.getLng()));
		
		return item;
	}
	
	/**
	 * 查看某个瓶子的内容
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processGetBottleContent(GetBottleContentReqMsg msg) {
		GetBottleContentRspMsg resp = new GetBottleContentRspMsg(msg);
/*		如果发送者，直接进入私聊
	          如果是捞的人第一次点击，服务端判断有没有领奖即可，则出现瓶子详细内容，否则直接进入私聊
	          瓶子则设为已读*/
		long bottleId = msg.getBottleId();
		Bottle bottle = StorageService.getBottleById(bottleId);
		String uid = CommonToolkit.toUID(msg.getServerId(), msg.getPlayerId());
		
		if (null != bottle) {
			if (bottle.getSender().equals(uid)) {
				// 如果发送者，则直接进入私聊，不返回瓶子内容
				// 设置为已读
				if (false == bottle.isSenderReaded()) {
					bottle.setSenderReaded(true);
					StorageService.updateBottle(bottle);
					
					// 发送者的未读瓶子数-1
					RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
				}
			} else if (false == bottle.isReceiverRewarded()) {
				// 如果捞取的人已经领过奖，则直接进入私聊，不返回瓶子内容
				// 否则返回瓶子内容
				BottleContent bottleContent = new BottleContent();
				bottleContent.setBottleId(bottleId);
				bottleContent.setFollow(StorageService.isInFollowList(uid, bottle.getSender()));
				bottleContent.setMsg(bottle.getText());
				bottleContent.setVid(bottle.getVid());
				
				String senderUid = bottle.getSender();
				String[] tmp = senderUid.split("-");
				bottleContent.setServerId(Integer.parseInt(tmp[0]));
				bottleContent.setPlayerId(Integer.parseInt(tmp[1]));
				
				PlayerInfo pi = StorageService.getPlayerInfo(bottle.getSender(), false);
				if (null != pi) {
					bottleContent.setImageId(pi.getImageId());
					bottleContent.setName(pi.getName());
					bottleContent.setServerName(pi.getServerName());
					bottleContent.setSign(pi.getSign());
					ImageWrapper.wrapImage(msg, bottleContent);
				}

				resp.setBottleContent(bottleContent);
				
				// 设置已读
				if (false == bottle.isReceiverReaded()) {
					bottle.setReceiverReaded(true);
					StorageService.updateBottle(bottle);
				}
			}
		}
		
		resp.setSuc(true);
		return resp;
	}

	/**
	 * 处理领取回复瓶子奖励逻辑
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processGetReplyReward(GetReplyRewardReqMsg msg) {
		GetReplyRewardRspMsg resp = new GetReplyRewardRspMsg(msg);
		
		Bottle bottle = StorageService.getBottleById(msg.getBottleId());
		if (null == bottle) {
			resp.setRewardId(-1);
			resp.setSuc(false);
			return resp;
		}
		
		String uid = CommonToolkit.toUID(msg.getServerId(), msg.getPlayerId());
		
		if (msg.isThrowOrFetch()) {
			if (!uid.equals(bottle.getSender())) {
				// 不是发送者
				resp.setRewardId(-1);
				resp.setSuc(false);
				return resp;
			}
			if (bottle.isSenderRewarded()) {
				// 已领过奖
				resp.setRewardId(-1);
				resp.setSuc(false);
				return resp;
			}
			
			if (false == bottle.isReceiverRewarded()) {
				// 回复者还没有领奖
				resp.setRewardId(-1);
				resp.setSuc(false);
				return resp;
			}
			
			// 把发送者设为已领奖状态
			bottle.setSenderRewarded(true);
			
			// 发送者设为已读
			if (false == bottle.isSenderReaded()) {
				// 发送者的未读瓶子数-1
				RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
				bottle.setSenderReaded(true);
			}
			
			// 告诉发送者领奖内容
			resp.setRewardId(bottle.getRewardId());
		} else {
			if (bottle.getReceiver() == null || !uid.equals(bottle.getReceiver())) {
				// 不是发送者
				resp.setRewardId(-1);
				resp.setSuc(false);
				return resp;
			}
			if (bottle.isReceiverRewarded()) {
				// 已领过奖
				resp.setRewardId(-1);
				resp.setSuc(false);
				return resp;
			}
			
			// 记录随机状态，并把接受者设为已领奖状态
			bottle.setRewardId(msg.getRewardId());
			bottle.setReceiverRewarded(true);
		}
		
		// 修改数据库
		boolean result = StorageService.updateBottle(bottle);
		resp.setSuc(result);
		
		if (result) {
			System.out.println( "stat_reply: " + DateTimeUtil.formatDateTime(new Date(), "yyyy-MM-dd.HH:mm:ss") 
					+ "," + msg.getPlayerId() 
					+ "," + msg.getServerId() 
					+ "," + msg.getMessageId() 
					+ "," + bottle.getId()
					+ "," + resp.getRewardId() 
					+ "," + msg.isThrowOrFetch());
		}
		return resp;
	}
	
	/**
	 * 获取未读瓶子数量
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage getUnreadBottleNum (GetUnreadNumReqMsg req) {
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		PlayerInfo player = StorageService.getPlayerInfo(serverId, playerId);
		if (player == null) {
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.SYS_SERVER_ERROR);
		}
		String uid = player.getUid();
		
		int unreadNum = 0;
		
		Pair<Integer, List<Long>> pairBottleIdList = CacheFactory.getMyBottleIdListRedisCache().getMyBottleIds(uid, true, 0, 300);
		if (pairBottleIdList == null) {
			// 缓存没有，直接去DB里面取
			List<Bottle> bottleList = DaoFactory.getBottleDao().batchMyBottles(uid, true, 0, 300);
			if (bottleList == null) {
				return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.SYS_SERVER_ERROR);	
			}
			
			List<Long> bottleIdList = new ArrayList<Long>(bottleList.size());
			for (Bottle bottle : bottleList) {
				bottleIdList.add( bottle.getId() );
				
				if (!bottle.isSenderReaded()) {
					unreadNum++;
				}
			}
			
			// cache data
			CacheFactory.getBottleRedisCache().setBottleList(bottleList);
			CacheFactory.getMyBottleIdListRedisCache().setMyBottleIds(uid, true, bottleIdList);
		}
		else {			
			List<Long> bottleIdList = pairBottleIdList.second;
			Map<Long, Bottle> bottleMap = batchBottle( new HashSet<Long>(bottleIdList) );
			for (Bottle bottle : bottleMap.values()) {
				if (bottle != null && !bottle.isSenderReaded()) {
					unreadNum++;
				}
			}
		}
		
		
		GetUnreadNumRspMsg rsp = new GetUnreadNumRspMsg(req);
		rsp.setSuc(true);
		rsp.setThrowUnreadNum(unreadNum);
		rsp.setFetchUnreadNum(0);
		return rsp;
	}

	/**
	 * 删除瓶子
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processDeleteBottle(DeleteBottleReqMsg msg) {
		DeleteBottleRspMsg resp = new DeleteBottleRspMsg(msg);
		String uid = CommonToolkit.toUID(msg.getServerId(), msg.getPlayerId());
		
		if (msg.getBottleIdList() != null) {
			boolean throwOrFetch = true;
			for (Long bottleId : msg.getBottleIdList()) {
				// 如果对方删除了，则从数据库中删除，否则只是置一下状态
				Bottle bottle = StorageService.getBottleById(bottleId);
				if (null != bottle) {
					if (bottle.getSender().equals(uid)) {
						throwOrFetch = true;
						
						if (false == bottle.isSenderReaded()) {
							// 如果发送者还没读，则未读瓶子-1
							RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
						}
			
						if (bottle.isReceiverDeleted()) {
							StorageService.deleteBottle(bottleId);
						} else {
							bottle.setSenderDeleted(true);
							StorageService.updateBottle(bottle);
						}
						
						// 缓存的瓶子ID列表删除
						RedisToolkit.deleteMyBottleId(0, bottle.getSender(), bottleId);
					} else if (bottle.getReceiver().equals(uid)) {
						throwOrFetch = false;
						
						if (bottle.isSenderDeleted()) {
							StorageService.deleteBottle(bottleId);
						} else {
							bottle.setReceiverDeleted(true);
							StorageService.updateBottle(bottle);
						}
						
						// 缓存的瓶子ID列表删除
						RedisToolkit.deleteMyBottleId(1, bottle.getReceiver(), bottleId);
					}
				}
			}
		
			if (msg.getBottleIdList().size() > 0) {
				CacheFactory.getMyBottleIdListRedisCache().delMyBottleIds(uid, throwOrFetch);
				CacheFactory.getUnreadBottleNumRedisCache().deleteUnreadBottleNum(uid);
			}
		}
		
		resp.setSuc(true);
		return resp;
	}

	/**
	 * 扔瓶子的人 获取 回复奖励
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage senderHarvestReward (SenderHarvestRewardReqMsg req) {
		long bottleId = req.getBottleId();
		int serverId = req.getServerId();
		int playerId = req.getPlayerId();
		
		Bottle bottle = StorageService.getBottleById(bottleId);
		if (bottle == null || bottle.isSenderDeleted()) {
			// 不存在的瓶子或者已被删除
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_EXIST);
		}
		
		String uid = CommonToolkit.toUID(serverId, playerId);
		if (!bottle.getSender().equals(uid)) {
			// 不是自己的瓶子
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_MINE);
		}
		if (bottle.isSenderRewarded()) {
			// 重复领奖
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_REPEAT_REWARD);
		}
		if (!bottle.isReceiverRewarded()) {
			// 没人回复
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_REWARD_NOT_REPLY);
		}
		int rewardId = bottle.getRewardId();
		
		bottle.setSenderRewarded(true);
		if (!bottle.isSenderReaded()) {
			bottle.setSenderReaded(true);
			RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
		}
		boolean success = StorageService.updateBottle(bottle);
		
		SenderHarvestRewardRspMsg rsp = new SenderHarvestRewardRspMsg(req);
		rsp.setRewardId(rewardId);
		RspMsgUtil.fillDefaultMsg(rsp, success);
		
		return rsp;
	}
	
	/**
	 * 捞瓶子的人 - 回复瓶子并获取奖励
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage replyBottle (ReplyBottleReqMsg req) {
		int serverId = req.getServerId();
		int playerId = req.getPlayerId();
		long bottleId = req.getBottleId();
		int rewardId = req.getRewardId();
		String replyText = req.getReplyText();
		
		// 验证输入
		if (replyText == null || replyText.isEmpty() || !CommonToolkit.isUTF8StringValid(replyText)) {
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.SYS_STR_NOT_VALID);
		}
		if (!replyText.startsWith("/#$voice")) {
			// 不是语音格式
			replyText = UIUtil.trim4PhoneUI(replyText);
		}
		
		if (replyText.trim().isEmpty()) {
			replyText = " ";
		}
		
		Bottle bottle = StorageService.getBottleById(bottleId);
		if (bottle == null) {
			// 不存在的瓶子或者已被删除
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_EXIST);
		}
		
		String uid = CommonToolkit.toUID(serverId, playerId);
		if (bottle.getReceiver() == null || !bottle.getReceiver().equals(uid)) {
			// 该瓶子没有回复过 或者 不是自己捞到的
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.SYS_ILLEGAL_ACTION);
		}
		if (bottle.isReceiverRewarded()) {
			// 已经领取了奖励
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_REPEAT_REWARD);
		}
		
		final long nowMillis = System.currentTimeMillis();
		bottle.setReplyText(replyText);
		bottle.setReplyTime(nowMillis);
		bottle.setRewardId(rewardId);
		bottle.setReceiverRewarded(true);
		boolean success = StorageService.updateBottle(bottle);
		
		ReplyBottleRspMsg rsp = new ReplyBottleRspMsg(req);
		RspMsgUtil.fillDefaultMsg(rsp, success);
		
		return rsp;
	}
	
	/**
	 * 查看瓶子内容
	 * @param req
	 * @return
	 */
	public static LBSResponseMessage viewBottleContent(ViewBottleContentReqMsg req) {
		long bottleId = req.getBottleId();
		Bottle bottle = StorageService.getBottleById(bottleId);
		if (bottle == null) {
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_EXIST);
		}
		
		String uid = CommonToolkit.toUID(req.getServerId(), req.getPlayerId());
		boolean beSender = false;		// 是否瓶子的发送者
		String oppositeUid = null;		// 对方Uid
		if (uid.equals(bottle.getSender())) {
			if (bottle.isSenderDeleted()) {
				// 扔瓶子的人, 把瓶子扔掉了
				return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_EXIST);	
			}
			beSender = true;
			oppositeUid = bottle.getReceiver();
		}
		else if (uid.equals(bottle.getReceiver())) {
			if (bottle.isReceiverDeleted()) {
				// 捞瓶子的人, 把瓶子扔掉了
				return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_EXIST);	
			}
			beSender = false;
			oppositeUid = bottle.getSender();
		}
		else {
			// 不是自己的瓶子
			return RspMsgUtil.createCommonErrorRespMsg(req, MultiLangConstant.BOTTLE_BE_NOT_MINE);
		}
		
		// 更新 读瓶子内容 标志
		if (beSender) {
			if (!bottle.isSenderReaded()) {
				bottle.setSenderReaded(true);
				StorageService.updateBottle(bottle);
				RedisToolkit.deleteUnreadBottleNum(bottle.getSender());
			}
		}
		else {
			if (!bottle.isReceiverReaded()) {
				bottle.setReceiverReaded(true);
				StorageService.updateBottle(bottle);
			}
		}
		
		BottleInfo bottleInfo = new BottleInfo ();
		bottleInfo.setBottleId(bottleId);		
		
		String[] arr = oppositeUid.split("-");
		bottleInfo.setServerId(Integer.parseInt(arr[0]));
		bottleInfo.setPlayerId(Integer.parseInt(arr[1]));
		
		PlayerInfo oppositePlayerInfo = StorageService.getPlayerInfo(oppositeUid, false);
		if (oppositePlayerInfo != null) {
			bottleInfo.setImageId(oppositePlayerInfo.getImageId());
			bottleInfo.setName(oppositePlayerInfo.getName());
			bottleInfo.setServerName(oppositePlayerInfo.getServerName());
			bottleInfo.setSign(oppositePlayerInfo.getSign());
			
			ImageWrapper.wrapImage(req, bottleInfo);
		}
		
		bottleInfo.setFollow(StorageService.isInFollowList(uid, oppositeUid));
		
		bottleInfo.setBeSender(beSender);
		bottleInfo.setMsg(bottle.getText());
		bottleInfo.setVid(bottle.getVid());
		bottleInfo.setReplyText(bottle.getReplyText());

		ViewBottleContentRspMsg resp = new ViewBottleContentRspMsg(req);
		resp.setBottleInfo(bottleInfo);		
		resp.setSuc(true);
		return resp;
	}
}
