package mango.condor.cache.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mango.condor.cache.BaseRedisCache;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.toolkit.Const;
import redis.clients.jedis.JedisPool;

import com.gzyouai.hummingbird.common.json.JsonManager;
import com.gzyouai.hummingbird.common.utils.CollectionUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年1月18日 下午4:43:39
 * @Description 
 */
public class PlayerRedisCache extends BaseRedisCache {

	/**
	 * @param pool
	 * @param db
	 * @param keyProfix
	 */
	public PlayerRedisCache(JedisPool pool) {
		super(pool, Const.REDIS_DB_BOTTLE, RedisCacheKeyConst.PLAYER.value);
	}

	/**
	 * 获取一个PlayerInfo
	 * @param playerId
	 * @return
	 */
	public PlayerInfo getPlayer(String uid) {		
		String key = createJedisKey(uid);
		
		String value = get(key);
		if (value == null) {
			return null;
		}
		
		return JsonManager.getGson().fromJson(value, PlayerInfo.class);
	}
	
	/**
	 * 批量获取PlayerInfo
	 * @param uidList
	 * @return
	 * 		Map<Uid, PlayerInfo>
	 */
	public Map<String, PlayerInfo> batchPlayerInfo (List<String> uidList) {
		if (CollectionUtil.isEmptyCollection(uidList)) {
			return new HashMap<String, PlayerInfo> ();
		}
		
		uidList = new ArrayList<String>( new HashSet<String>(uidList) );
		
		String[] keys = new String[uidList.size()];
		for (int i = 0; i < uidList.size(); i++) {
			keys[i] = createJedisKey(uidList.get(i));
		}
		
		Map<String, String> map = mget(keys);
		if (map == null) {
			return null;
		}
		
		PlayerInfo player = null;
		Map<String, PlayerInfo> playerMap = new HashMap<String, PlayerInfo>();
		for (String value : map.values()) {
			if (value != null) {
				player = JsonManager.getGson().fromJson(value, PlayerInfo.class);
				playerMap.put(player.getUid(), player);
			}
		}
		
		return playerMap;
	}

	/**
	 * 删除一个PlayerInfo
	 * @param uid
	 * @return
	 */
	public boolean deletePlayer(String uid) {
		String key = createJedisKey(uid);
		
		return del(key) >= 0;
	}

	/**
	 * 设置一个PlayerInfo
	 * @param player
	 * @return
	 */
	public boolean setPlayer(PlayerInfo player) {
		if (player == null) {
			return false;
		}
		
		String key = createJedisKey(player.getUid());
		String value = JsonManager.getGson().toJson(player);
		return set(key, value, 3600);	// 3600秒
	}
	
	/**
	 * 设置一堆PlayerInfo
	 * @param playerList
	 * @return
	 */
	public boolean setPlayerList(List<PlayerInfo> playerList) {
		if (playerList == null) {
			return false;
		}
		if (playerList.isEmpty()) {
			return true;
		}
		
		Map<String, String> kv = new HashMap<String, String>();
		for (PlayerInfo player : playerList) {
			String key = createJedisKey(player.getUid());
			String value = JsonManager.getGson().toJson(player);
			kv.put(key, value);
		}
		
		return mset(kv, 3600); // 3600秒
	}
}
