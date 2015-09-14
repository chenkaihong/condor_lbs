package mango.condor.cache.bottle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mango.condor.cache.BaseRedisCache;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.domain.bottle.Bottle;
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
public class BottleRedisCache extends BaseRedisCache {

	/**
	 * @param pool
	 * @param db
	 * @param keyProfix
	 */
	public BottleRedisCache(JedisPool pool) {
		super(pool, Const.REDIS_DB_BOTTLE, RedisCacheKeyConst.BOTTLE.value);
	}

	/**
	 * 获取一个漂流瓶
	 * @param bottleId
	 * @return
	 */
	public Bottle getBottle(long bottleId) {		
		String key = createJedisKey(bottleId);
		
		String value = get(key);
		if (value == null) {
			return null;
		}
		
		return JsonManager.getGson().fromJson(value, Bottle.class);
	}
	
	/**
	 * 批量获取漂流瓶
	 * @param bottleIdList
	 * @return
	 */
	public Map<Long, Bottle> batchBottle (List<Long> bottleIdList) {
		if (CollectionUtil.isEmptyCollection(bottleIdList)) {
			return new HashMap<Long, Bottle> ();
		}
		
		String[] keys = new String[bottleIdList.size()];
		for (int i = 0; i < bottleIdList.size(); i++) {
			keys[i] = createJedisKey(bottleIdList.get(i));
		}
		
		Map<String, String> map = mget(keys);
		if (map == null) {
			return new HashMap<Long, Bottle> ();
		}
		
		Bottle bottle = null;
		Map<Long, Bottle> bottleMap = new HashMap<Long, Bottle>();
		for (String value : map.values()) {
			if (value != null) {
				bottle = JsonManager.getGson().fromJson(value, Bottle.class);
				bottleMap.put(bottle.getId(), bottle);
			}
		}
		
		return bottleMap;
	}

	/**
	 * 删除一个漂流瓶
	 * @param bottleId
	 * @return
	 */
	public boolean deleteBottle(long bottleId) {
		String key = createJedisKey(bottleId);
		
		return del(key) >= 0;
	}

	/**
	 * 设置一个漂流瓶
	 * @param bottle
	 * @return
	 */
	public boolean setBottle(Bottle bottle) {
		if (bottle == null) {
			return false;
		}
		
		String key = createJedisKey(bottle.getId());
		String value = JsonManager.getGson().toJson(bottle);
		return set(key, value, 1800);	// 1800秒
	}
	
	/**
	 * 设置一堆漂流瓶
	 * @param bottleList
	 * @return
	 */
	public boolean setBottleList(List<Bottle> bottleList) {
		if (bottleList == null) {
			return false;
		}
		if (bottleList.isEmpty()) {
			return true;
		}
		
		Map<String, String> kv = new HashMap<String, String>();
		for (Bottle bottle : bottleList) {
			String key = createJedisKey(bottle.getId());
			String value = JsonManager.getGson().toJson(bottle);
			kv.put(key, value);
		}
		
		return mset(kv, 1800); // 1800秒
	}
}
