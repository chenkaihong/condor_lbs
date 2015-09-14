package mango.condor.cache.bottle;

import java.util.List;

import mango.condor.cache.BaseRedisCache;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.toolkit.Const;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.gzyouai.hummingbird.common.component.Pair;
import com.gzyouai.hummingbird.common.json.JsonManager;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年1月18日 下午4:43:39
 * @Description 
 */
public class MyBottleIdListRedisCache extends BaseRedisCache {

	/**
	 * @param pool
	 * @param db
	 * @param keyProfix
	 */
	public MyBottleIdListRedisCache(JedisPool pool) {
		super(pool, Const.REDIS_DB_BOTTLE, RedisCacheKeyConst.BOTTLE.value);
	}

	private String createJedisKey (String uid, boolean throwOrFetch) {
		if (throwOrFetch) {
			return Const.BOTTLE_MY_THROW_PREFIX + uid;
		} else {
			return Const.BOTTLE_MY_FETCH_PREFIX + uid;
		}
	}
	
	/**
	 * 获取我的瓶子Id列表
	 * @param uid
	 * @param throwOrFetch
	 * 		true: 扔
	 * 		false: 捞
	 * @param offset
	 * @param limit
	 * @return
	 * 		Pair<数据总量, 分页数据>
	 */
	public Pair<Integer, List<Long>> getMyBottleIds(String uid, boolean throwOrFetch, int offset, int limit) {
		if (uid == null) {
			return null;
		}
		
		String key = createJedisKey(uid, throwOrFetch);
		Long total = null;
		List<String> result = null;
		
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			if (!jedis.exists(key)) {
				return null;
			}
			
			// 获取分页数据
			int start = offset;
			int end = offset + limit - 1;
			result = jedis.lrange(key, start, end);			
			// 获取数据总量
			total = jedis.llen(key);
		}
		catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
			return null;
		}
		finally {
			returnJedis(jedis, broken);
		}
		
		List<Long> bottleIdList = JsonManager.fromJsonList(result, Long.class);
		return Pair.makePair(total.intValue(), bottleIdList);
	}
	
	/**
	 * 删除我的瓶子Id列表
	 * @param bottleId
	 * @return
	 */
	public boolean delMyBottleIds(String uid, boolean throwOrFetch) {
		if (uid == null) {
			return false;
		}
		
		String key = createJedisKey(uid, throwOrFetch);
		
		return del(key) >= 0;
	}

	/**
	 * 设置我的瓶子Id列表
	 * @param bottle
	 * @return
	 */
	public boolean setMyBottleIds(String uid, boolean throwOrFetch, List<Long> bottleIdList) {
		if (uid == null || bottleIdList == null) {
			return false;
		}
		if (bottleIdList.isEmpty()) {
			return true;
		}
		
		String key = createJedisKey(uid, throwOrFetch);
		String[] values = JsonManager.toJsonArray(bottleIdList);
		
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			jedis.rpush(key, values);
			jedis.expire(key, 1800); // 1800秒
			return true;
		}
		catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
			return false;
		}
		finally {
			returnJedis(jedis, broken);
		}
	}
}
