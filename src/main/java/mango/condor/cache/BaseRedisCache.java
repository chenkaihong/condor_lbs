package mango.condor.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 李兴
 * @Date 2013年12月19日 下午6:38:18
 * @Description
 */
public class BaseRedisCache {

	private final JedisPool pool;
	private final boolean ignoreSelectDB; 	// true: 只使用 DB=0, 不需要每次 select {{db}}
	private final int db; 					// ignoreSelectDB=true时, 无效
	private final String keyPrefix;			// key 的前缀

	protected BaseRedisCache(JedisPool pool, int db, String keyPrefix) {
		this.pool = pool;
		this.ignoreSelectDB = false;
		this.db = db;
		this.keyPrefix = keyPrefix;
	}

	protected BaseRedisCache(JedisPool pool, String keyPrefix) {
		this.pool = pool;
		this.ignoreSelectDB = true;
		this.db = 0;
		this.keyPrefix = keyPrefix;
	}

	/**
	 * 获取一个 Jedis
	 * @return
	 */
	protected Jedis getResource() {
		Jedis jedis = pool.getResource();
		if (!ignoreSelectDB) {
			if (jedis.getDB() != db) {
				jedis.select(db);
			}
		}

		return jedis;
	}

	/**
	 * 返回 Jedis 到 Jedis Pool
	 * @param jedis
	 * @param broken
	 */
	protected void returnJedis(Jedis jedis, boolean broken) {
		if (jedis != null) {
			if (broken) {
				pool.returnBrokenResource(jedis);
			}
			else {
				pool.returnResource(jedis);
			}
		}
	}
	
	/**
	 * 生成 Jedis key
	 * @param key
	 * @return
	 */
	protected String createJedisKey (String key) {
		return keyPrefix + ":" + key;
	}
	
	/**
	 * 生成 Jedis key
	 * @param key
	 * @return
	 */
	protected String createJedisKey (long key) {
		return createJedisKey(Long.toString(key));
	}
	
	/**
	 * 生成 Jedis key
	 * @param key
	 * @return
	 */
	protected String createJedisKey (int key) {
		return createJedisKey(Integer.toString(key));
	}
	
	/**
	 * 是否更新成功
	 * @param result
	 * @return
	 */
	protected boolean isUpdateSucc (String result) {
		return "OK".equalsIgnoreCase(result);
	}

	/**
	 * 获取key的前缀
	 * @return
	 */
	public String getKeyPrefix() {
		return keyPrefix;
	}
	
	/**
	 * 批量删除 keys
	 * @param keys
	 * @return
	 */
	protected long del (String... keys) {
		if (keys == null || keys.length == 0) {
			return 0;
		}
		
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			return jedis.del(keys);
		}
		catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
			return -1;
		}
		finally {
			returnJedis(jedis, broken);
		}
	}
	
	/**
	 * 返回 key 所关联的字符串值
	 * @param key
	 * @return
	 */
	protected String get (String key) {
		if (key == null) {
			return null;
		}
		
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			return jedis.get(key);
		}
		catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
			return null;
		}
		finally {
			returnJedis(jedis, broken);
		}
	}
	
	/**
	 * 返回所有(一个或多个)给定 key 的值
	 * @param keys
	 * @return
	 * 		Map<Key, 给定 key 的值>
	 */
	protected Map<String, String> mget (String... keys) {
		if (keys == null || keys.length == 0) {
			return null;
		}
		
		List<String> result = null;		
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			result = jedis.mget(keys);
		}
		catch (JedisConnectionException e) {
			broken = true;
			e.printStackTrace();
			return null;
		}
		finally {
			returnJedis(jedis, broken);
		}
		
		Map<String, String> map = new HashMap<String, String> ();
		for (int i = 0; i < keys.length; i++) {
			String item = result.get(i);
			if (item != null) {
				map.put(keys[i], item);
			}
		}
		
		return map;
	}
	
	/**
	 * 将字符串值 value 关联到 key, 并设置缓存时间为 seconds
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	protected boolean set (String key, String value, int seconds) {
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			String result = jedis.setex(key, seconds, value);
			return isUpdateSucc(result);
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
	
	/**
	 * 批量设置 key-value, 并设置缓存时间为 seconds
	 * @param kv
	 * @param seconds
	 * @return
	 */
	protected boolean mset (Map<String, String> kv, int seconds) {
		boolean broken = false;		
		Jedis jedis = null;
		try {
			jedis = getResource();
			
			for (Map.Entry<String, String> entry : kv.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				jedis.setex(key, seconds, value);
			}
			
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
