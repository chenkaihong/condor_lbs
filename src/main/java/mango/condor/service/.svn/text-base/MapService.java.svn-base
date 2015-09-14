package mango.condor.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.map.MapExploreType;
import mango.condor.domain.msg.map.MapExploreMsgReq;
import mango.condor.domain.msg.map.MapExploreMsgRsp;
import mango.condor.domain.msg.map.MapRangeListMsgReq;
import mango.condor.domain.msg.map.MapRangeListMsgRsp;
import mango.condor.toolkit.BMapToolkit;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.ConfManager;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.GeoToolkit;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 熊三山  
 * @Date 2013-12-31 下午10:11:52
 * @Description 
 */
public class MapService {

	
	
	/**
	 * 得到范围内的人的列表
	 * @param req
	 * @return
	 */
	public static MapRangeListMsgRsp rangeList(MapRangeListMsgReq req) {
		MapRangeListMsgRsp resp = new MapRangeListMsgRsp(req);
		
		int playerId = req.getPlayerId();
		int serverId = req.getServerId();
		
		String gender = getValidGender(req.getGender());    // 按性别
		boolean isOnline = req.isOnline();  // 玩家是否在线
		boolean isLocal = req.isLocal();    // 是否搜索本服玩家
		boolean isAvatar = req.isAvatar();	// 是否搜索有自定义头像的玩家
		boolean isVoice = req.isVoice();	// 是否搜索有语音的玩家
		
		double lng = req.getLng();		 	// 经度
		double lat = req.getLat();		 	// 纬度
		double minLng = req.getMinLng();
		double maxLng = req.getMaxLng();
		double minLat = req.getMinLat();
		double maxLat = req.getMaxLat();
		
		// 
		if ((int) lat == 0 && (int) lng == 0 && ConfManager.isUseBaiduAPI) {
			PlayerInfo self = StorageService.getPlayerInfo(CommonToolkit.toUID(serverId, playerId), true);
			if (null != self) {
				lng = self.getLng();
				lat = self.getLat();
			}
		}
		
		// 获取范围内的玩家的列表
		List<PlayerInfo> retList = GeoToolkit.getRangePlayerInfoList(serverId, playerId, lat, lng, gender, isOnline, isLocal, isAvatar, isVoice,  minLat, minLng, maxLat, maxLng);
		if (retList == null) {
			resp.setpList(null);
			resp.setSuc(false);
		}
		else {			
			
			double searchMinLat =  BMapToolkit.scaleLatOrLngForGeohashMinPoint(minLat);
			double searchMinLng =  BMapToolkit.scaleLatOrLngForGeohashMinPoint(minLng);
			double searchMaxLat =  BMapToolkit.scaleLatOrLngForGeohashMinPoint(maxLat);
			double searchMaxLng =  BMapToolkit.scaleLatOrLngForGeohashMinPoint(maxLng);
			
			System.out.println(String.format("范围内经纬度[%s,%s]-[%s,%s],玩家列表：%s", searchMinLng, searchMinLat, searchMaxLng, searchMaxLat,
					retList.size()));
			
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
	
	public static MapExploreMsgRsp explore(MapExploreMsgReq req){
		MapExploreMsgRsp rsp = new MapExploreMsgRsp(req);
		rsp.setType(req.getType());
		if(req.getType() == MapExploreType.Follow){
			String uid = CommonToolkit.toUID(req.getServerId(), req.getPlayerId());
			PlayerInfo playerInfo = RedisToolkit.getRandomPlayerInfo(uid);
			ImageWrapper.wrapImage(req, playerInfo);
			
			rsp.setInfo(playerInfo);
		}else if(req.getType() == MapExploreType.Business){
			//TODO 填充跑商玩家哦
			
		}
		return rsp;
	}
	
	
}
