package mango.condor.cache;

import java.util.Map;

import mango.condor.cache.bottle.BottleRedisCache;
import mango.condor.cache.bottle.MyBottleIdListRedisCache;
import mango.condor.cache.bottle.UnreadBottleNumRedisCache;
import mango.condor.cache.chat.ChatRedisCache;
import mango.condor.cache.player.PlayerRedisCache;
import mango.condor.dao.DaoFactory;
import mango.condor.toolkit.RedisToolkit;
import redis.clients.jedis.JedisPool;

import com.gzyouai.hummingbird.common.component.ComponentMap;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年1月18日 下午4:37:05
 * @Description 
 */
public class CacheFactory {
	private static JedisPool pool = RedisToolkit.getPool();

	private static Map<String, BaseRedisCache> cacheMap = new ComponentMap<String, BaseRedisCache>(DaoFactory.class.getName(), false);

	static {
		cacheMap.put(PlayerRedisCache.class.getName(), new PlayerRedisCache(pool));
		
		cacheMap.put(UnreadBottleNumRedisCache.class.getName(), new UnreadBottleNumRedisCache(pool));
		cacheMap.put(BottleRedisCache.class.getName(), new BottleRedisCache(pool));
		cacheMap.put(MyBottleIdListRedisCache.class.getName(), new MyBottleIdListRedisCache(pool));
		
		cacheMap.put(ChatRedisCache.class.getName(), new ChatRedisCache(pool));
		
		System.out.println( "CacheFactory init.." + "\t" + cacheMap.keySet() );
	}
	
	public static void init () {
		
	}
	
	public static PlayerRedisCache getPlayerRedisCache() {
		return (PlayerRedisCache) cacheMap.get(PlayerRedisCache.class.getName());
	}
	
	public static UnreadBottleNumRedisCache getUnreadBottleNumRedisCache() {
		return (UnreadBottleNumRedisCache) cacheMap.get(UnreadBottleNumRedisCache.class.getName());
	}
	
	public static BottleRedisCache getBottleRedisCache() {
		return (BottleRedisCache) cacheMap.get(BottleRedisCache.class.getName());
	}
	
	public static MyBottleIdListRedisCache getMyBottleIdListRedisCache() {
		return (MyBottleIdListRedisCache) cacheMap.get(MyBottleIdListRedisCache.class.getName());
	}
	
	public static ChatRedisCache getChatRedisCache() {
		return (ChatRedisCache) cacheMap.get(ChatRedisCache.class.getName());
	}
}
