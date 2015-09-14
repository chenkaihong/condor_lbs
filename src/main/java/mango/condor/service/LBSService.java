package mango.condor.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mango.condor.domain.lbs.FollowMsg;
import mango.condor.domain.lbs.FollowRank;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.TmpFollowRankItem;
import mango.condor.domain.msg.LBSResponseMessage;
import mango.condor.domain.msg.chat.SearchPlayerReqMsg;
import mango.condor.domain.msg.chat.SearchPlayerRspMsg;
import mango.condor.domain.msg.lbs.CachePlayerInfoReqMsg;
import mango.condor.domain.msg.lbs.CachePlayerInfoRspMsg;
import mango.condor.domain.msg.lbs.ClearFollowMsgReqMsg;
import mango.condor.domain.msg.lbs.ClearFollowMsgRspMsg;
import mango.condor.domain.msg.lbs.DeleteFollowListReqMsg;
import mango.condor.domain.msg.lbs.DeleteFollowListRspMsg;
import mango.condor.domain.msg.lbs.GetFanListReqMsg;
import mango.condor.domain.msg.lbs.GetFanListRspMsg;
import mango.condor.domain.msg.lbs.GetFollowListReqMsg;
import mango.condor.domain.msg.lbs.GetFollowListRspMsg;
import mango.condor.domain.msg.lbs.GetFollowMsgReqMsg;
import mango.condor.domain.msg.lbs.GetFollowMsgRspMsg;
import mango.condor.domain.msg.lbs.GetFollowRankReqMsg;
import mango.condor.domain.msg.lbs.GetFollowRankRspMsg;
import mango.condor.domain.msg.lbs.GetNeighbourReqMsg;
import mango.condor.domain.msg.lbs.GetNeighbourRspMsg;
import mango.condor.domain.msg.lbs.GetNewChatNumReqMsg;
import mango.condor.domain.msg.lbs.GetNewChatNumRspMsg;
import mango.condor.domain.msg.lbs.GetPlayerInfoReqMsg;
import mango.condor.domain.msg.lbs.GetPlayerInfoRspMsg;
import mango.condor.domain.msg.lbs.IndexReqMsg;
import mango.condor.domain.msg.lbs.IndexRspMsg;
import mango.condor.domain.msg.lbs.InsertFollowListReqMsg;
import mango.condor.domain.msg.lbs.InsertFollowListRspMsg;
import mango.condor.domain.multilang.MultiLangConstant;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.ConfManager;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.GeoToolkit;
import mango.condor.toolkit.MultiLanguageManager;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-13 下午3:19:11
 * @Description 
 */
public class LBSService {
	/**
	 * 缓存用户信息，加载数据
	 * @param req
	 * @return
	 */
	public static CachePlayerInfoRspMsg processCachePlayerInfo(CachePlayerInfoReqMsg req, String ip) {
		CachePlayerInfoRspMsg resp = new CachePlayerInfoRspMsg(req);
		PlayerInfo playerInfo = req.getPi();
		
		if (playerInfo != null) {
			playerInfo.setName( PlayerInfo.trimPlayerName(playerInfo.getName()) );
		}
		
		// 插入或更新玩家信息
		boolean result = StorageService.insertOrUpdatePlayerInfo(playerInfo);
		if (!result) {
			resp.setSuc(false);
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
			return resp;
		}

		resp.setSuc(true);
		return resp;
	}
	
	/**
	 * 得到附近的人的列表
	 * @param req
	 * @return
	 */
	public static GetNeighbourRspMsg processGetNeighbourList(GetNeighbourReqMsg req) {
		GetNeighbourRspMsg resp = new GetNeighbourRspMsg(req);
		
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		
		boolean is_more = req.isMore();  	// 此次请求是否是点击"更多"按钮产生的
		String gender = getValidGender(req.getGender());    // 按性别
		boolean isOnline = req.isOnline();  // 玩家是否在线
		boolean isLocal = req.isLocal();    // 是否搜索本服玩家
		boolean isAvatar = req.isAvatar();	// 是否搜索有自定义头像的玩家
		boolean isVoice = req.isVoice();	// 是否搜索有语音的玩家
		
		double lng = req.getLng();		 	// 经度
		double lat = req.getLat();		 	// 纬度
		// 种种原因取不到，则从缓存读取，缓存中的经纬度肯定靠谱
		if ((int) lat == 0 && (int) lng == 0 && ConfManager.isUseBaiduAPI) {
			PlayerInfo self = StorageService.getPlayerInfo(CommonToolkit.toUID(serverId, playerId), true);
			if (null != self) {
				lng = self.getLng();
				lat = self.getLat();
			}
		}
		
		if (lng <= 0 && lat <= 0) {
			// 暂未获取到您当前的位置
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_ERROR_POSITION));
			return resp;
		}
		
		// 获取附近的人的列表
		List<PlayerInfo> retList = GeoToolkit.getNeighbourPlayerInfoList(serverId, playerId, lat, lng, 
				gender, isOnline, isLocal, isAvatar, isVoice, 
				is_more);
		if (retList == null) {
			resp.setpList(null);
			resp.setSuc(false);
		}
		else {			
			for (PlayerInfo player : retList) {
				ImageWrapper.wrapImage(req, player);
			}
			resp.setpList(retList);
			resp.setSuc(true);
		}
		
		return resp;
	}
	
	
	
	
	/**
	 * 获取一个有效的性别
	 * @param gender
	 * @return
	 */
	private static String getValidGender (String gender) {
		if (gender == null) {
			return Const.LBS_GEO_HASH_ALL; 
		}
		
		Set<String> set = new HashSet<String>();
		set.add( Const.LBS_GEO_HASH_ALL );
		set.add( Const.LBS_GEO_HASH_MEN );
		set.add( Const.LBS_GEO_HASH_WOMEN );
		
		if (set.contains(gender)) {
			return gender;
		}
		
		return Const.LBS_GEO_HASH_ALL;
	}

	/**
	 * 获取关注列表
	 * @param req
	 * @return
	 */
	public static GetFollowListRspMsg processGetFollowList(GetFollowListReqMsg req) {
		GetFollowListRspMsg resp = new GetFollowListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int pageNum = req.getPageNum();
		int pageSize = req.getPageSize();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
				
		List<PlayerInfo> list = StorageService.getFollowList(playerServerId, playerId, pageNum, pageSize);
		if (null != list) {
			for (PlayerInfo player : list) {
				ImageWrapper.wrapImage(req, player);
			}
			
			resp.setSuc(true);
			resp.setList(list);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}

		return resp;
	}

	/**
	 * 插入关注
	 * @param req
	 * @return
	 */
	public static InsertFollowListRspMsg processInsertFollowList(InsertFollowListReqMsg req) {
		InsertFollowListRspMsg resp = new InsertFollowListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int hisId = req.getHisId();
		int hisServerId = req.getHisServerId();
		
		// 不能自己添加自己
		if (playerId == hisId && playerServerId == hisServerId) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.FOLLOW_SELF));
			return resp;
		}
		
		PlayerInfo self = StorageService.getPlayerInfo(playerServerId, playerId);
		if (null == self) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		PlayerInfo idol = StorageService.getPlayerInfo(hisServerId, hisId);
		if (null == idol) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		// 黑名单判断
		int blackListStatus = StorageService.isBlackList(playerServerId, playerId, hisServerId, hisId);
		if (blackListStatus == Const.BLACK_LIST_STATUS_ADD) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_IS_PLAYER_BLACK_LIST));
			return resp;
		}
		else if (blackListStatus == Const.BLACK_LIST_STATUS_ADDED) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.CHAT_IS_BLACK_LIST));
			return resp;
		}
		
		// 如果已经关注了对方，返回
		if (RedisToolkit.isInFollowList(playerServerId, playerId, hisServerId, hisId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.FOLLOW_ALREADY));
			return resp;
		}
		
		boolean result = StorageService.insertFollowList(playerServerId, playerId, hisServerId, hisId);
		if (result) {			
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		
		return resp;
	}

	/**
	 * 删除关注
	 * @param req
	 * @return
	 */
	public static DeleteFollowListRspMsg processDeleteFollowList(DeleteFollowListReqMsg req) {
		DeleteFollowListRspMsg resp = new DeleteFollowListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int hisId = req.getHisId();
		int hisServerId = req.getHisServerId();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		boolean result = StorageService.removeFollowList(playerServerId, playerId, hisServerId, hisId);
		if (result) {
			resp.setSuc(true);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		return resp;
	}

	/**
	 * 首页新鲜事
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processIndex(IndexReqMsg req) {
		IndexRspMsg resp = new IndexRspMsg(req);
		
		boolean needPlusUnreadFollowMsgCount = req.getVersion() != null && req.getVersion().length() > 0;
		
		resp.setUnReadChatNum(StorageService.getUnreadSecretChatNum(req.getServerId(), req.getPlayerId(), needPlusUnreadFollowMsgCount));
		resp.setList(StorageService.getRandomPlayerInfo(req, req.getServerId(), req.getPlayerId(), req.getTs()));
		resp.setSuc(true);
		return resp;
	}
	
	/**
	 * 获取未读私信数量
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage getNewChatNum(GetNewChatNumReqMsg req) {
		boolean needPlusUnreadFollowMsgCount = req.getVersion() != null && req.getVersion().length() > 0;
				
		GetNewChatNumRspMsg resp = new GetNewChatNumRspMsg(req);
		resp.setUnReadChatNum(StorageService.getUnreadSecretChatNum(req.getServerId(), req.getPlayerId(), needPlusUnreadFollowMsgCount));
		resp.setSuc(true);
		return resp;
	}


	/**
	 * 获取粉丝列表
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processGetFanList(GetFanListReqMsg req) {
		GetFanListRspMsg resp = new GetFanListRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		int pageNum = req.getPageNum();
		int pageSize = req.getPageSize();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		List<PlayerInfo> list = StorageService.getFanList(playerServerId, playerId, pageNum, pageSize);
		if (null != list) {
			for (PlayerInfo player : list) {
				ImageWrapper.wrapImage(req, player);
			}
			resp.setSuc(true);
			resp.setList(list);
		} else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		return resp;
	}

	/**
	 * 查看玩家数据
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processGetPlayerInfo(GetPlayerInfoReqMsg req) {
		GetPlayerInfoRspMsg resp = new GetPlayerInfoRspMsg(req);
		PlayerInfo pi = StorageService.getPlayerInfo(req.getHisServerId(), req.getHisId());
		PlayerInfo selfPi = StorageService.getPlayerInfo(req.getServerId(), req.getPlayerId());
		if (null != pi) {
			ImageWrapper.wrapImage(req, pi);
			if (null != selfPi) {
				pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
						pi.getLat(), pi.getLng()));
			}
			pi.setFollow(StorageService.isInFollowList(req.getServerId(), req.getPlayerId(), req.getHisServerId(), req.getHisId()));
			pi.setBlack(Const.BLACK_LIST_STATUS_ADD == StorageService.isBlackList(req.getServerId(), req.getPlayerId(), req.getHisServerId(), req.getHisId()));
			pi.calDiffTS();
			
			resp.setSuc(true);
			resp.setPi(pi);
		}
		
		return resp;
	}

	/**
	 * 分页获取关注排行榜
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processFollowRank(GetFollowRankReqMsg req) {
		GetFollowRankRspMsg resp = new GetFollowRankRspMsg(req);
		TmpFollowRankItem item = StorageService.getFollowRank(req.getServerId(), req.getPlayerId(), req.getPageNum(), req.getPageSize());
		if (null != item && null != item.getRankList()) {
			for (FollowRank fr : item.getRankList()) {
				PlayerInfo pi = StorageService.getPlayerInfo(fr.getUid(), true);
				if (null != pi) {
					ImageWrapper.wrapImage(req, pi);
					
					pi.setFollow(StorageService.isInFollowList(req.getServerId(), req.getPlayerId(), pi.getServerId(), pi.getPlayerId()));
					pi.calDiffTS();
					fr.set(pi);
				}
			}
		}

		resp.setList(item.getRankList());
		resp.setRank(item.getRank());
		resp.setFanNum(item.getFanNum());
		resp.setSuc(true);
		return resp;
	}

	/**
	 * 根据玩家名搜索玩家
	 * @param msg
	 * @return
	 */
	public static LBSResponseMessage processSearchPlayer(SearchPlayerReqMsg req) {
		SearchPlayerRspMsg resp = new SearchPlayerRspMsg(req);
		String name = req.getName();
		
		if (null == StorageService.getPlayerInfo(req.getServerId(), req.getPlayerId())) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
		
		// 是否有非法字符
		if (false == CommonToolkit.isUTF8StringValid(name)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_STR_NOT_VALID));
			return resp;
		}
		
		List<PlayerInfo> list = StorageService.searchPlayer(name, req.getServerId(), req.getPlayerId());
		PlayerInfo selfPi = StorageService.getPlayerInfo(req.getServerId(), req.getPlayerId());

		resp.setSuc(true);
		if (null != list) {
			for (PlayerInfo pi : list) {
				ImageWrapper.wrapImage(req, pi);
				if (null != selfPi) {
					pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
							pi.getLat(), pi.getLng()));
					pi.setFollow(StorageService.isInFollowList(req.getServerId(), req.getPlayerId(), pi.getServerId(), pi.getPlayerId()));
				}
				pi.calDiffTS();
			}

			resp.setList(list);
		}
		
		return resp;
	}
	
	/**
	 * 获取 被关注的消息
	 * @param req
	 * @return
	 */
	public static GetFollowMsgRspMsg processGetFollowMsgList(GetFollowMsgReqMsg req) {
		GetFollowMsgRspMsg resp = new GetFollowMsgRspMsg(req);
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		
		if (null == StorageService.getPlayerInfo(playerServerId, playerId)) {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_LOCATION_MSG));
			return resp;
		}
				
		List<FollowMsg> list = StorageService.readFollowMsgList(playerServerId, playerId, req.getLang(), true);
		if (null != list) {
			for (FollowMsg follow : list) {
				ImageWrapper.wrapImage(req, follow);
			}
			
			resp.setSuc(true);
			resp.setList(list);
		}
		else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}

		return resp;
	}
	
	/**
	 * 清空 被关注的消息
	 * @param req
	 * @return
	 */
	public static ClearFollowMsgRspMsg processClearFollowMsg(ClearFollowMsgReqMsg req) {
		ClearFollowMsgRspMsg resp = new ClearFollowMsgRspMsg(req);
		
		int playerId = req.getPlayerId();
		int playerServerId = req.getServerId();
		
		boolean result = StorageService.clearFollowMsg(playerServerId, playerId);
		if (result) {
			resp.setSuc(true);
		} 
		else {
			resp.setErrMsg(MultiLanguageManager.getString(req.getLang(), MultiLangConstant.SYS_SERVER_BUSY));
		}
		
		return resp;
	}
}
