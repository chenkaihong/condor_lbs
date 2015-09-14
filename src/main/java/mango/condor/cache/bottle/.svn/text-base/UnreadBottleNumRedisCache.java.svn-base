package mango.condor.cache.bottle;

import mango.condor.cache.BaseRedisCache;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.toolkit.Const;
import redis.clients.jedis.JedisPool;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年1月18日 下午4:43:39
 * @Description 
 */
public class UnreadBottleNumRedisCache extends BaseRedisCache {

	/**
	 * @param pool
	 * @param db
	 * @param keyProfix
	 */
	public UnreadBottleNumRedisCache(JedisPool pool) {
		super(pool, Const.REDIS_DB_BOTTLE, RedisCacheKeyConst.UNREAD_BOTTLE_NUM.value);
	}

	/**
	 * 获取 未读的瓶子数量
	 * @param uid
	 * @return
	 */
	public int getUnreadBottleNum(String uid) {
		if (uid == null) {
			return -1;
		}
		
		String key = createJedisKey(uid);
		
		String value = get(key);
		if (value == null) {
			return -1;
		}
		
		return Integer.parseInt(value);
	}

	/**
	 * 删除 未读的瓶子数量
	 * @param uid
	 * @return
	 */
	public boolean deleteUnreadBottleNum(String uid) {
		if (uid == null) {
			return false;
		}
		
		String key = createJedisKey(uid);
		
		return del(key) >= 0;
	}

	/**
	 * 设置 未读的瓶子数量
	 * @param uid
	 * @param num
	 * @return
	 */
	public boolean setUnreadBottleNum(String uid, int num) {
		if (uid == null || num < 0) {
			return false;
		}
		
		String key = createJedisKey(uid);
		
		return set(key, Integer.toString(num), 3600); // 1小时
	}
}
