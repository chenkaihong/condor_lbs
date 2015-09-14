package mango.condor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import mango.condor.cache.CacheFactory;
import mango.condor.dao.DaoFactory;
import mango.condor.domain.lbs.PlayerInfo;

import com.gzyouai.hummingbird.common.utils.CollectionUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月25日 下午9:05:56
 * @Description 
 */
public class PlayerService {
	private static Queue<String> lostUidList = new ArrayBlockingQueue<String>(1000);
	
	/**
	 * 记录一个丢失的Uid (合服等原因导致的未删除干净)
	 * @param uid
	 * @return
	 */
	public static boolean logLostUid (String uid) {
		if (uid != null && !uid.isEmpty()) {
			return lostUidList.offer(uid);
		}
		
		return false;
	}
	
	/**
	 * 批量获取 PlayerInfo
	 * @param uidSet
	 * @return
	 * 		Map<Uid, PlayerInfo>
	 */
	public static Map<String, PlayerInfo> batchPlayerInfo (Set<String> uidSet) {
		if (uidSet.isEmpty()) {
			return new HashMap<String, PlayerInfo> ();
		}
		List<String> uidList = new LinkedList<String>(uidSet);
		
		// 去cache里面获取数据
		Map<String, PlayerInfo> playerMap = CacheFactory.getPlayerRedisCache().batchPlayerInfo(uidList);
		
		// 删除已cache的,保留未cache的
		Iterator<String> it = uidList.iterator();
		while (it.hasNext()) {
			String uid = it.next();
			if (playerMap.containsKey(uid)) {
				it.remove();
			}
		}
		
		if (!uidList.isEmpty()) {
			// 去DB里面获取数据
			Map<String, PlayerInfo> map = DaoFactory.getPlayerDao().batchPlayerInfo(uidList);
			if (!CollectionUtil.isEmptyMap(map)) {
				playerMap.putAll(map);
				// 缓存到Cache
				CacheFactory.getPlayerRedisCache().setPlayerList(new ArrayList<PlayerInfo>(map.values()));
			}
		}
		
		return playerMap;
	}
}
