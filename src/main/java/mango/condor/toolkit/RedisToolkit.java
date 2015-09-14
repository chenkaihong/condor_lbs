package mango.condor.toolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mango.condor.domain.bottle.Bottle;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.FollowRank;
import mango.condor.domain.lbs.FollowRecord;
import mango.condor.domain.lbs.NeighbourInfo;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.RangePlayerInfo;
import mango.condor.domain.lbs.TmpFollowRankItem;
import mango.condor.service.StorageService;

import org.apache.commons.pool.impl.GenericObjectPool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-6-20 下午2:49:43
 * @Description
 */
public class RedisToolkit {
	private static JedisPoolConfig config;
	private static JedisPool pool;

	/**
	 * 重置所有用户GeoHash数据
	 * 		原因: 
	 * 			原来的数据的key 基于用户 lat和lng， 新数据的key 基于缩放过lat和lng，所以所有老数据要全部清理掉
	 * 		执行:
	 * 			1) 服务启动时候执行
	 * 			2) 该方法只执行一次就可以了
	 * 		其它:
	 * 			1) 执行多次，也没关系，只是启动会慢
	 * 			2) 执行出错，也没关系，只是可能会存在无法使用的数据，造成内存浪费
	 */
	public static String resetAllPlayerGeoHash () {
		StringBuilder log = new StringBuilder();
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			
			// 清空 GeoHash 数据
			jedis.select(Const.REDIS_DB_GEO);
			jedis.flushDB();
			log.append( "resetGeoHash flush GeoDB succ.\n" );
			
			// 查出所有的用户keys
			jedis.select(Const.REDIS_DB_PLAYER_INFO);			
			Set<String> playerInfoKeys = jedis.keys(Const.LBS_USER_INFO_PREFIX + "*");
			
			System.out.println( "resetAllPlayerGeoHash.correctCount=" + 0 );
			
			int correctCount = 0;
			for (String k : playerInfoKeys) {
				try {
					// 查出用户的playerInfo
					jedis.select(Const.REDIS_DB_PLAYER_INFO);
					String jsonData = jedis.get(k);
					if (jsonData != null) {
						Gson gson = CommonToolkit.getGson();
						PlayerInfo player = gson.fromJson(jsonData, PlayerInfo.class);
						
						jedis.select(Const.REDIS_DB_GEO);
						
						// 重置新的 geoHash 数据
						String geoHash = GeoToolkit.getGeoHashBase32(player.getScaleLatForGeohashMinPoint(), player.getScaleLngForGeohashMinPoint());
						String geoHashAllKey = createGeoHashKey(geoHash, Const.LBS_GEO_HASH_ALL);
						String geoHashGenderKey = createGeoHashKey(geoHash, player.isGender() ? Const.LBS_GEO_HASH_MEN : Const.LBS_GEO_HASH_WOMEN);
						
						jedis.zadd(geoHashAllKey, (int)(player.getUpdateTS() / 1000), k);
						jedis.zadd(geoHashGenderKey, (int)(player.getUpdateTS() / 1000), k);
						
						correctCount++;
					}
					if (correctCount % 1000 == 999) {
						System.out.println( "resetAllPlayerGeoHash.correctCount=" + correctCount );
						Thread.sleep(1000L);
					}
				}
				catch (Exception e) {
					log.append( "resetGeoHash: key=" + k + ", Exception.msg=" + e.getMessage() + "\n" );
				}
			}
			System.out.println( "resetAllPlayerGeoHash.correctCount=" + correctCount );
			
			log.append( "resetGeoHash be done. count=" + playerInfoKeys.size() + ", correctCount=" + correctCount + "\n");
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			
			log.append(" redis Exception. msg=" + e.getMessage() + "\n");
			
		}
		finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		
		return log.toString();
	}
	
	/**
	 * 程序启动时初始化
	 */
	public static void init() {
		config = new JedisPoolConfig();
		
		// 能从池中借出的对象的最大数目
		config.setMaxActive(ConfManager.redisMaxActive);
		
		// 最大的空闲对象数  
		config.setMaxIdle(ConfManager.redisMaxIdle);
		
		// 最小的空闲对象数  
		config.setMinIdle(ConfManager.redisMinIdle);
		
		// 获取连接最长等待时间
		config.setMaxWait(1000 * 5);
		
		// 连接空闲时间设为30分钟
		config.setMinEvictableIdleTimeMillis(1000 * 60 * 30);
		
		// 每次检查空闲连接测试几个连接
		config.setNumTestsPerEvictionRun(100);
		
		// 对象被回收前在池中保持空闲状态的最小时间毫秒数 
		config.setSoftMinEvictableIdleTimeMillis(1000 * 60 * 30);
		
		// 取连接时先检查一下
		// config.setTestOnBorrow(true);
		config.setTestOnBorrow(false);
		
		// 还连接不检查
		config.setTestOnReturn(false);
		
		// 设定在进行后台对象清理时，是否还对没有过期的池内对象进行有效性检查，不能通过有效性检查的对象也将被回收。 
		config.setTestWhileIdle(true);
		
		// 每隔多少毫秒进行对象清理，暂设为10分钟
		config.setTimeBetweenEvictionRunsMillis(1000 * 60 * 10);
		
		// 指定当对象池耗尽时的行为（等待、创建新实例、抛异常），设置为抛异常
		config.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
		
		pool = new JedisPool(config, ConfManager.redisHost, ConfManager.redisPort);
		
		System.out.println( "Init Redis: host=" + ConfManager.redisHost + ", port=" + ConfManager.redisPort);
	}

	/**
	 * 关闭时销毁
	 */
	public static void destory() {
		if (pool != null) {
			pool.destroy();
			pool = null;
		}
	}
	
	/**
	 * 获取分布式锁的key
	 * @param key
	 * @return
	 */
	public static String getLockKey(String key) {
		return Const.LOCK_KEY_PREFIX + key;
	}
	
	/**
	 * 获得连接
	 * @return
	 */
	public static Jedis getJedis() {
		return pool.getResource();
	}
	
	/**
	 * 回收连接
	 * @param jedis
	 * @param isError
	 */
	public static void returnJedis(Jedis jedis, boolean isError) {
		if (isError) {
			pool.returnBrokenResource(jedis);
		} else {
			pool.returnResource(jedis);
		}
	}
	
	/**
	 * 清空某个库
	 * @param dbID
	 * @return
	 */
	public static boolean flushDB(int dbID) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(dbID);
			jedis.flushDB();
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 获取最近的 count 条新鲜事记录
	 * @param count
	 * @return
	 */
	public static List<String> getLastFresh(int count) {
		List<String> retList = new ArrayList<String>();
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_INDEX);
			
			Set<String> tmpSet = jedis.zrevrangeByScore(
					Const.LBS_INDEX_FRESH_PREFIX, 
					System.currentTimeMillis(), 0,
					0, count);
			
			if (null != tmpSet) {
				retList.addAll(tmpSet);
			}

		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		
		return retList;
	}
	
	/**
	 * 随机获取首页新鲜事
	 * @return
	 */
	public static List<String> getIndexFresh(long ts) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_INDEX);
			List<String> retList = new ArrayList<String>();
			Set<String> tmpSet = null;
			
			if (ts == 0) {
				// 第一次请求
				tmpSet = jedis.zrevrangeByScore(Const.LBS_INDEX_FRESH_PREFIX, 
						System.currentTimeMillis(), 0,
						0, Const.INDEX_GET_LIMIT);
			} else {
				tmpSet = jedis.zrangeByScore(Const.LBS_INDEX_FRESH_PREFIX, 
						ts, System.currentTimeMillis(), 
						0, Const.INDEX_GET_LIMIT);
			}
			
			if (null != tmpSet) {
				retList.addAll(tmpSet);
			}

			return retList;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 删除旧的新鲜事
	 */
	public static void clearIndexFresh() {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_INDEX);
			jedis.zremrangeByScore(Const.LBS_INDEX_FRESH_PREFIX, 
					0, System.currentTimeMillis() - Const.INDEX_RESERVE_MILLI);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 追加新鲜事
	 * @param uid
	 */
	public static void appendIndexFresh(String uid, long now) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_INDEX);
			jedis.zadd(Const.LBS_INDEX_FRESH_PREFIX, now, uid);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 设置关注排行榜
	 * @param followRank
	 */
	public static void putFollowRank(List<FollowRank> followRank) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_RANK);
			Gson gson = CommonToolkit.getGson();
			
			// 先删除
			jedis.del(Const.LBS_FOLLOW_RANK);
			jedis.del(Const.LBS_FOLLOW_RANK_INDEX);
			
			for (FollowRank fr : followRank) {
				jedis.rpush(Const.LBS_FOLLOW_RANK, gson.toJson(fr));
				jedis.hset(Const.LBS_FOLLOW_RANK_INDEX, fr.getUid(), String.valueOf(fr.getRank()));
			}
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 分页获取关注排行榜
	 * @param serverId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static TmpFollowRankItem getFollowRank(int serverId, int playerId, int pageNum, int pageSize) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_RANK);
			int st = (pageNum - 1) * pageSize;
			int ed = st + pageSize - 1;
			List<String> list = jedis.lrange(Const.LBS_FOLLOW_RANK, st, ed);
			if (null == list) {
				return null;
			}
			
			List<FollowRank> retList = new ArrayList<FollowRank>();
			Gson gson = new Gson();
			for (String jsonData : list) {
				retList.add(gson.fromJson(jsonData, FollowRank.class));
			}
			
			String rank = jedis.hget(Const.LBS_FOLLOW_RANK_INDEX, CommonToolkit.toUID(serverId, playerId));
			
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String fanListKey = getFanListKey(serverId, playerId);
			Long ret = jedis.zcard(fanListKey);
			int fanNum = 0;
			if (null != ret) {
				fanNum = ret.intValue();
			}
			
			TmpFollowRankItem item = new TmpFollowRankItem();
			item.setRankList(retList);
			item.setRank(rank);
			item.setFanNum(fanNum);
			return item;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 在某人的关注列表删除某人
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean removeFollowList(int playerServerId, int playerId, int hisServerId, int hisPlayerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String followListKey = getFollowListKey(playerServerId, playerId);
			String fanListKey = getFanListKey(hisServerId, hisPlayerId);
					
			jedis.zrem(followListKey, CommonToolkit.toUID(hisServerId, hisPlayerId));
			jedis.zrem(fanListKey, CommonToolkit.toUID(playerServerId, playerId));
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 分页获取某人的关注集合
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static Set<String> getFollowList(int playerServerId, int playerId, int pageNum, int pageSize) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String followListKey = getFollowListKey(playerServerId, playerId);
			int st = (pageNum - 1) * pageSize;
			int ed = st + pageSize - 1;
			return jedis.zrevrange(followListKey, st, ed);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 分页获取粉丝列表
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static Set<String> getFanList(int playerServerId, int playerId, int pageNum, int pageSize) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String fanListKey = getFanListKey(playerServerId, playerId);
			int st = (pageNum - 1) * pageSize;
			int ed = st + pageSize - 1;
			return jedis.zrevrange(fanListKey, st, ed);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 判断 hisId 是否在 playerId 的关注列表内
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean isInFollowList(int playerServerId, int playerId, int hisServerId, int hisId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String followListKey = getFollowListKey(playerServerId, playerId);
			// playerId的关注列表
			Double score = jedis.zscore(followListKey, CommonToolkit.toUID(hisServerId, hisId));
			return score != null ? true : false;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	public static boolean isInFollowList(String uid1, String uid2) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String followListKey = getFollowListKey(uid1);
			Double score = jedis.zscore(followListKey, uid2);
			return score != null ? true : false;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 某人把某人加进了关注列表
	 * 		-- playerId 关注 hisId
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @param id
	 * @param isExpire
	 * @return
	 */
	public static boolean insertFollowList(int playerServerId, int playerId, int hisServerId, 
			int hisPlayerId, int id) {
		return insertFollowList(CommonToolkit.toUID(playerServerId, playerId), 
				CommonToolkit.toUID(hisServerId, hisPlayerId), 
				id);
	}

	/**
	 * 某人把某人加进了关注列表
	 * -- pid 关注 tid
	 * @param pid
	 * @param tid
	 * @param id
	 * @param isExpire
	 * @return
	 */
	public static boolean insertFollowList(String pid, String tid, long id) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_FOLLOW_LIST);
			String followListKey = getFollowListKey(pid);
			String fanListKey = getFanListKey(tid);
			
			// pid的关注列表
			jedis.zadd(followListKey, id, tid);
			
			// tid的粉丝列表
			jedis.zadd(fanListKey, id, pid);
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 在某人的黑名单集合删除某人
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean removeBlackList(int playerServerId, int playerId, int hisServerId, int hisPlayerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_BLACK_LIST);
			String blackListKey = getBlackListKey(playerServerId, playerId);
			jedis.zrem(blackListKey, CommonToolkit.toUID(hisServerId, hisPlayerId));
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 分页获取某人的黑名单集合
	 * @param playerServerId
	 * @param playerId
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static Set<String> getBlackList(int playerServerId, int playerId, int pageNum, int pageSize) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_BLACK_LIST);
			String blackListKey = getBlackListKey(playerServerId, playerId);
			int st = (pageNum - 1) * pageSize;
			int ed = st + pageSize - 1;
			return jedis.zrange(blackListKey, st, ed);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 判断对方是否在某人的黑名单中
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean isBlackList(int playerServerId, int playerId, int hisServerId, int hisPlayerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_BLACK_LIST);
			String blackListKey = getBlackListKey(playerServerId, playerId);
			Double score = jedis.zscore(blackListKey, CommonToolkit.toUID(hisServerId, hisPlayerId));
			return score != null ? true : false;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 某人把某人加进了黑名单
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean insertBlackList(int playerServerId, int playerId, int hisServerId, int hisPlayerId, int id) {
		return insertBlackList(CommonToolkit.toUID(playerServerId, playerId), 
				CommonToolkit.toUID(hisServerId, hisPlayerId), 
				id);
	}
	
	/**
	 * 某人把某人加进了黑名单
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean insertBlackList(String pid, String tid, long id) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_BLACK_LIST);
			String blackListKey = getBlackListKey(pid);
			
			jedis.zadd(blackListKey, id, tid);

			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 删除会话相关的缓存数据
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @return
	 */
	public static boolean removeChatSession(int playerServerId, int playerId, int hisServerId, int hisPlayerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_CHAT);
			String myChatSetKey = getMyChatSetKey(playerServerId, playerId);
			String hisChatSetKey = getMyChatSetKey(hisServerId, hisPlayerId);
			String sessionId = CommonToolkit.getSessionId(playerServerId, playerId, hisServerId, hisPlayerId);
			String chatSessionKey = getMyChatSessionKey(sessionId);
//			String chatContentKey = getMyChatContentKey(sessionId);
			
			jedis.srem(myChatSetKey, sessionId);
			jedis.srem(hisChatSetKey, sessionId);
			jedis.del(chatSessionKey);
//			jedis.del(chatContentKey);
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 删除某人所有chat
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static boolean removeMyAllChatSession(int serverId, int playerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSetKey(serverId, playerId);
			System.out.println( "@@: removeMyAllChatSession key=" + key );
			
			jedis.del(key);
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 得到某一批私聊会话
	 * @param sessionId
	 * @return
	 */
	public static List<ChatMyVo> batchMyChatSession(List<String> sessionIdList) {
		String[] keys = new String[sessionIdList.size()];
		for (int i = 0; i < sessionIdList.size(); i++) {
			keys[i] = getMyChatSessionKey( sessionIdList.get(i) );
		}
		
		List<String> data = null;
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_CHAT);
			
			data = jedis.mget(keys);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		
		if (data == null) {
			return null;
		}
		
		Gson gson = CommonToolkit.getGson();
		
		List<ChatMyVo> list = new ArrayList<ChatMyVo>();
		for (String item : data) {
			list.add( gson.fromJson(item, ChatMyVo.class) );	
		}
		
		return list;
	}
	
	/**
	 * 得到某一个私聊会话
	 * @param sessionId
	 * @return
	 */
	public static ChatMyVo getMyChatSession(String sessionId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSessionKey(sessionId);
			String jsonData = jedis.get(key);
			if (jsonData == null) {
				return null;
			}
			Gson gson = CommonToolkit.getGson();
			ChatMyVo chatMyVo = gson.fromJson(jsonData, ChatMyVo.class);
			return chatMyVo;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 重置私聊记录
	 * @param chatList
	 * @param isExpire
	 * @return
	 */
	public static boolean resetMyChatSession(List<ChatMyVo> chatList, boolean isExpire) {
		if (chatList == null || chatList.isEmpty()) {
			return true;
		}
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_CHAT);
			
			Gson gson = CommonToolkit.getGson();
			Set<String> keys = new HashSet<String> ();
			
			int size = chatList.size();
			String[] kv = new String[size * 2];
			for (int i = 0; i < size; i++) {
				ChatMyVo chat = chatList.get(i);
				String key = getMyChatSessionKey(chat.getChatId());
				kv[i * 2] = key;
				kv[i * 2 + 1] = gson.toJson(chat);
				
				keys.add(key);
			}
			
			jedis.del(kv);
			jedis.mset(kv);
			
			for (String key : keys) {
				jedis.expire(key, Const.PRIVATE_CACHE_EXPIRE_SECONDS);
			}
			
			return true;
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		
		return false;
	}
	
	/**
	 * 缓存会话
	 * @param session
	 * @return
	 */
	public static boolean putMyChatSession(ChatMyVo session, boolean isExpire) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSessionKey(session.getChatId());
			Gson gson = CommonToolkit.getGson();
			
			jedis.set(key, gson.toJson(session));
			jedis.expire(key, Const.PRIVATE_CACHE_EXPIRE_SECONDS);
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 返回玩家的所有私聊会话id
	 * @param playerId
	 * @return
	 */
	public static Set<String> getMyChatSet(int serverId, int playerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();		
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSetKey(serverId, playerId);
			
			Set<String> set = jedis.smembers(key);
			return set;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 重置 某玩家聊天会话ID集合
	 * @param serverId
	 * @param playerId
	 * @param sessionIds
	 * @param isExpire
	 * @return
	 */
	public static boolean resetMyChatSet(int serverId, int playerId, String[] sessionIds, boolean isExpire) {
		if (sessionIds == null || sessionIds.length == 0) {
			return true;
		}
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSetKey(serverId, playerId);
			
			jedis.del(key);
			jedis.sadd(key, sessionIds);
			
			if (isExpire) {
				jedis.expire(key, Const.PRIVATE_CACHE_EXPIRE_SECONDS);
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 缓存某玩家聊天会话ID集合
	 * @param serverId
	 * @param playerId
	 * @param sessionId
	 * @param isExpire
	 * @param isExist
	 * @return
	 */
	public static boolean appendMyChatSet(int serverId, int playerId, String sessionId, boolean isExpire, boolean isExist) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_CHAT);
			String key = getMyChatSetKey(serverId, playerId);
			
			// key存在才追加
			if (isExist) {
				// 如果不存在，则不追加
				if (false == jedis.exists(key)) {
					return false;
				}
			}
			
			jedis.sadd(key, sessionId);
			if (isExpire) {
				jedis.expire(key, Const.PRIVATE_CACHE_EXPIRE_SECONDS);
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 得到私聊缓存玩家id的key
	 * @param playerId
	 * @return
	 */
	private static String getMyChatSetKey(int serverId, int playerId) {
		return Const.CHAT_SET_PREFIX + CommonToolkit.toUID(serverId, playerId);
	}
	
	/**
	 * 得到私聊缓存的某条会话的key
	 * @param sessionId
	 * @return
	 */
	private static String getMyChatSessionKey(String sessionId) {
		return Const.CHAT_SESSION_PREFIX + sessionId;
	}
	
	/**
	 * 得到某人的黑名单的key
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	private static String getBlackListKey(int serverId, int playerId) {
		return Const.CHAT_BLACK_LIST_PREFIX + CommonToolkit.toUID(serverId, playerId);
	}
	
	/**
	 * 得到某人的黑名单的key
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	private static String getBlackListKey(String id) {
		return Const.CHAT_BLACK_LIST_PREFIX + id;
	}
	
	/**
	 * 得到某人的关注列表的key
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	private static String getFollowListKey(int serverId, int playerId) {
		return Const.CHAT_FOLLOW_LIST_PREFIX + CommonToolkit.toUID(serverId, playerId);
	}
	
	/**
	 * 得到某人的关注列表的key
	 * @param id
	 * @return
	 */
	private static String getFollowListKey(String id) {
		return Const.CHAT_FOLLOW_LIST_PREFIX + id;
	}
	
	/**
	 * 得到某人的粉丝列表的key
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	private static String getFanListKey(int serverId, int playerId) {
		return Const.CHAT_FAN_LIST_PREFIX + CommonToolkit.toUID(serverId, playerId);
	}
	
	private static String getFanListKey(String id) {
		return Const.CHAT_FAN_LIST_PREFIX + id;
	}
	
// ==========================================================================================================================================
	
	/**
	 * 缓存用户数据
	 * @param playerInfo
	 * @param isCacheGeo
	 * @return
	 */
	
	
	/**
	 * 得到用户缓存的资料
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo getPlayerInfo(int serverId, int playerId) {
		String playerInfoKey = getPlayerInfoKey(CommonToolkit.toUID(serverId, playerId));
		return getPlayerInfo(playerInfoKey);
	}
	
	/**
	 * 得到用户缓存的资料
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo getPlayerInfoById(String id) {
		String playerInfoKey = getPlayerInfoKey(id);
		return getPlayerInfo(playerInfoKey);
	}
	
	public static PlayerInfo getPlayerInfo(String playerInfoKey) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_PLAYER_INFO);
			String jsonData = jedis.get(playerInfoKey);
			if (jsonData == null) {
				return null;
			}
			Gson gson = CommonToolkit.getGson();
			PlayerInfo player = gson.fromJson(jsonData, PlayerInfo.class);
			return player;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}

	/**
	 * 获取非关注的随机玩家信息
	 * @param selfUid
	 * @return
	 */
	public static PlayerInfo getRandomPlayerInfo(String selfUid) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_PLAYER_INFO);
			String jsonData = getRandomPlayerUid(jedis, selfUid);
			if (jsonData == null) {
				return null;
			}
			Gson gson = CommonToolkit.getGson();
			PlayerInfo player = gson.fromJson(jsonData, PlayerInfo.class);
			return player;
		} catch (JedisConnectionException e) {
			// LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}

	/**
	 * 查找不在关注列表的玩家
	 * 
	 * @param jedis
	 * @param selfUid
	 * @return
	 */
	private static String getRandomPlayerUid(Jedis jedis, String selfUid) {
		int times = 0;
		do {
			String uId = jedis.randomKey();
			// 没有玩家，则退出
			if (uId == null) {
				return null;
			}
			if (!isInFollowList(selfUid, uId)) {
				// 不是关注的玩家，则获取玩家数据，
				return jedis.get(uId);
			}
			times += 1;
			// 随机次数打到一万次以上，还没有随机到玩家，则自动退出，避免一直随机，造成死循环
			if (times >= 10000) {
				return null;
			}
		} while (true);
	}

	public static void removePlayerInfo(int serverId, int playerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_PLAYER_INFO);
			jedis.del(getPlayerInfoKey(CommonToolkit.toUID(serverId, playerId)));
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 得到缓存玩家id的key
	 * @param playerInfo
	 * @return
	 */
	private static String getPlayerInfoKey(String uid) {
		return Const.LBS_USER_INFO_PREFIX + uid;
	}

// 漂流瓶相关=============================================================================================
	/**
	 * 缓存漂流瓶
	 * @param bottle
	 * @return
	 */
	public static boolean putUnreadBottle(Bottle bottle) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_UNREAD_BOTTLE);
			Gson gson = CommonToolkit.getGson();
			
			String bottleKey = getUnreadBottleKey(bottle.getId());

			// 可使用pineline优化, to do...
			String ret = jedis.set(bottleKey, gson.toJson(bottle));
			boolean isSuc = ret.equalsIgnoreCase("ok") ? true : false;
			if (isSuc) {
				jedis.expire(bottleKey, Const.UNREAD_BOTTLE_EXPIRE_SECONDS);
				return true;
			}
			return false;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 随机捞瓶子
	 * @param uid
	 * @return
	 */
	public static Bottle fetchUnreadBottle(String uid) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_UNREAD_BOTTLE);
			Gson gson = CommonToolkit.getGson();
			
			for (int i = 0; i < 3; ++i) {
				String key = jedis.randomKey();
				
				if (null == key) {
					return null;
				}
				// 不能捞自己发的
				String json = jedis.get(key);
				if (json == null) {
					continue;
				}
				
				Bottle bottle = null;
				try {
					bottle = gson.fromJson(json, Bottle.class);
				}
				catch (Exception e) {
					System.out.println( "parse bottle error. json=" + json );
				}
				if (bottle == null || bottle.getSender().equals(uid)) {
					continue;
				}
				
				// 删除瓶子
				jedis.del(key);
				
				return bottle;
			}
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return null;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 删除我的瓶子ID列表中的一些瓶子ID
	 * @param type
	 * @param uid
	 * @param deleteId
	 * @return
	 */
	public static boolean deleteMyBottleId(int type, String uid, Long deleteId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getMyBottleKey(uid, type);
			if (false == jedis.exists(key)) {
				// 如果不存在先加载
				StorageService.initMyBottleIdList(uid);
			}
			
			if (true == jedis.exists(key)) {
				// 存在才操作
				jedis.lrem(key, 1, String.valueOf(deleteId));
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 得到瓶子
	 * @param bottleId
	 * @return
	 */
	public static Bottle getBottleContent(long bottleId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getBottleContentKey(bottleId);
			String json = jedis.get(key);
			if (null == json) {
				return null;
			}
			
			Gson gson = CommonToolkit.getGson();
			return gson.fromJson(json, Bottle.class);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return null;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 缓存瓶子
	 * @param bottle
	 * @param isExpire
	 * @return
	 */
	public static boolean cacheBottleContent(Bottle bottle, boolean isExpire) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			Gson gson = CommonToolkit.getGson();
			String key = getBottleContentKey(bottle.getId());
			jedis.set(key, gson.toJson(bottle));
			if (isExpire) {
				jedis.expire(key, Const.BOTTLE_CONTENT_EXPIRE_SECONDS);
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	public static int getMyBottlePageSize(int type, String uid) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getMyBottleKey(uid, type);
			if (false == jedis.exists(key)) {
				// 如果不存在先加载
				StorageService.initMyBottleIdList(uid);
			}
			
			long len = jedis.llen(key);
			return (int) Math.ceil((double) len / 10);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return 0;
	}
	
	/**
	 * 分页获取我的瓶子ID列表
	 * @param type
	 * @param uid
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public static List<Long> getMyBottleIdList(int type, String uid, int pageNum) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getMyBottleKey(uid, type);
			if (false == jedis.exists(key)) {
				// 如果不存在先加载
				StorageService.initMyBottleIdList(uid);
			}
			
			int st = (pageNum - 1) * 10;
			int ed = st + 10 - 1;
			List<String> list = jedis.lrange(key, st, ed);
			
			if (null != list) {
				List<Long> retList = new ArrayList<Long>();
				for (String id : list) {
					retList.add(Long.parseLong(id));
				}
				return retList;
			} else {
				return null;
			}
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return null;
	}
	
	/**
	 * 缓存我的瓶子ID
	 * 捞到瓶子时调用
	 * @param type
	 * @param uid
	 * @param bottleId
	 * @return
	 */
	public static boolean cacheMyBottleIdList(int type, String uid, Long bottleId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getMyBottleKey(uid, type);	
			if (false == jedis.exists(key)) {
				// 如果不存在先加载
				StorageService.initMyBottleIdList(uid);
			}
			
			// 不存在就不需要操作了
			jedis.lpushx(key, String.valueOf(bottleId));
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 初始化我的瓶子ID列表
	 * @param type
	 * @param uid
	 * @param bottleIdList
	 * @return
	 */
	public static boolean initMyBottleIdList(int type, String uid, List<Long> bottleIdList) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getMyBottleKey(uid, type);
			
			if (false == jedis.exists(key)) {
				// key不存在才进行初始化，不要重复初始化
				for (Long bottleId : bottleIdList) {
					jedis.rpush(key, String.valueOf(bottleId));
				}
				jedis.expire(key, Const.MY_BOTTLE_EXPIRE_SECONDS);
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	public static int getUnreadBottleNum(String uid) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getUnreadBottleNumKey(uid);
			if (false == jedis.exists(key)) {
				// 如果不存在先加载
				StorageService.initUnreadBottleNum(uid);
			}
			
			String ret = jedis.get(key);
			if (null == ret) {
				return -1;
			}
			return Integer.parseInt(ret);
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return -1;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 改变未读瓶子数量
	 * @param uid
	 * @return
	 */
	public static boolean deleteUnreadBottleNum(String uid) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getUnreadBottleNumKey(uid);
			jedis.del(key);
			
			return true;
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	/**
	 * 初始化未读瓶子数量
	 * @param uid
	 * @param unreadBottleNum
	 * @return
	 */
	public static boolean initUnreadBottleNum(String uid, int unreadBottleNum) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_BOTTLE);
			
			String key = getUnreadBottleNumKey(uid);
			
			if (false == jedis.exists(key)) {
				// 不要重复初始化
				jedis.set(key, String.valueOf(unreadBottleNum));
				jedis.expire(key, Const.UNREAD_BOTTLE_EXPIRE_SECONDS);
			}
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
			return false;
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
	}
	
	private static String getUnreadBottleKey(long id) {
		return Const.BOTTLE_UNREAD_PREFIX + id;
	}
	
	private static String getMyBottleKey(String uid, int type) {
		if (0 == type) {
			return Const.BOTTLE_MY_THROW_PREFIX + uid;
		} else {
			return Const.BOTTLE_MY_FETCH_PREFIX + uid;
		}
	}
	
	private static String getUnreadBottleNumKey(String uid) {
		return Const.BOTTLE_UNREAD_BOTTLE_NUM_PREFIX + uid;
	}
	
	private static String getBottleContentKey(long bottleId) {
		return Const.BOTTLE_CONTENT_PREFIX + bottleId;
	}

	/**
	 * playerId 关注 hisPlayerId, 给 hisPlayerId 增加一条关注消息
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisPlayerId
	 * @param followTime
	 * @return
	 */
	public static boolean insertFollowMsg(int playerServerId, int playerId, int hisServerId, int hisPlayerId, long followTime, boolean read) {
		String fans = CommonToolkit.toUID(playerServerId, playerId);
		String idol = CommonToolkit.toUID(hisServerId, hisPlayerId);
		return insertFollowMsg(fans, idol, followTime, read);
	}
	
	/**
	 * fans 关注 idol, 给 idol 增加一条关注消息
	 * @param fans
	 * @param idol
	 * @param followTime
	 * @param read
	 * 		是否已读该消息
	 * @return
	 */
	public static boolean insertFollowMsg(String fans, String idol, long followTime, boolean read) {
		FollowRecord followRecord = new FollowRecord ();
		followRecord.setFollowTime(followTime);
		followRecord.setRead(read);
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_FOLLOW_MSG);
			jedis.hset(getFollowMsgKey(idol), fans, CommonToolkit.getGson().toJson(followRecord));			
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}
	
	/**
	 * 获取 playerId被关注的消息
	 * @param playerServerId
	 * @param playerId
	 * @param updateWhenRead
	 * 		true: 读的时候，把状态给更新成已读
	 * @return
	 * 		Map<serverId + "-" + playerId, FollowRecord>
	 */
	public static Map<String, FollowRecord> readFollowMsg(int playerServerId, int playerId, boolean updateWhenRead) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();			
			jedis.select(Const.REDIS_DB_FOLLOW_MSG);
			
			String key = getFollowMsgKey(playerServerId, playerId);
			
			// 获取数据
			Map<String, String> map = jedis.hgetAll(key);
			Map<String, FollowRecord> followMap = new HashMap<String, FollowRecord>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				followMap.put(entry.getKey(), CommonToolkit.getGson().fromJson(entry.getValue(), FollowRecord.class));
			}
			
			if (updateWhenRead) {
				// 更新 read 标志，并更新到redis
				Map<String, String> newMap = new HashMap<String, String>();
				for (Map.Entry<String, FollowRecord> entry : followMap.entrySet()) {
					FollowRecord record = entry.getValue();
					if (!record.isRead()) {
						FollowRecord readRecord = new FollowRecord(record.getFollowTime(), true);
						newMap.put(entry.getKey(), CommonToolkit.getGson().toJson(readRecord));
					}
				}
				if (!newMap.isEmpty()) {
					jedis.hmset(key, newMap);
				}
			}
				
			return followMap;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return new HashMap<String, FollowRecord>();
	}
	
	/**
	 * 清空 playerServerId被关注的消息
	 * @param playerServerId
	 * @param playerId
	 */
	public static boolean clearFollowMsg(int playerServerId, int playerId) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();	
			jedis.select(Const.REDIS_DB_FOLLOW_MSG);
			jedis.del(getFollowMsgKey(playerServerId, playerId));
			return true;
		} catch (JedisConnectionException e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e, null, null);
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null)
				pool.returnBrokenResource(jedis);
		} finally {
			if (borrowOrOprSuccess)
				pool.returnResource(jedis);
		}
		return false;
	}

	/**
	 * 我被关注的消息 的 redis table key
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	private static String getFollowMsgKey(int serverId, int playerId) {
		return getFollowMsgKey( CommonToolkit.toUID(serverId, playerId) );
	}
	
	/**
	 * 我被关注的消息 的 redis table key
	 * @param uid
	 * @return
	 */
	private static String getFollowMsgKey(String uid) {
		return Const.CHAT_FOLLOW_MSG_PREFIX + uid;
	}
	
	//========== 获取附近的人 ==============================================================
	/**
	 * 生成[附近的人]的key
	 * @param uid
	 * @param gender
	 * @return
	 */
	private static String createNeighbourKey (String uid, String gender, boolean isOnline) {
		return Const.LBS_NEIGHBOUR_LIST_PREFIX + uid + ":" + gender + ":" + (isOnline ? 1 : 0);
	}
	
	/**
	 * 获取[附近的人]
	 * @param uid
	 * @param gender
	 * @return
	 * 		NeighbourInfo
	 */
	public static NeighbourInfo getNeighbourInfo (String uid, String gender, boolean isOnline) {
		String key = createNeighbourKey(uid, gender, isOnline);
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO);
			
			String jsonData = jedis.get(key);
			if (jsonData != null) {
				NeighbourInfo data = CommonToolkit.getGson().fromJson(jsonData, NeighbourInfo.class);
				return data;
			}
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		}
		finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}
		
		return null;	
	}
	
	/**
	 * 设置[附近的人]
	 * @param geoHash
	 * @param data
	 * @return
	 */
	public static boolean putNeighbourInfo(String uid, String gender, boolean isOnline, NeighbourInfo data) {
		String key = createNeighbourKey(uid, gender, isOnline);
		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO);
			
			String jsonData = CommonToolkit.getGson().toJson(data);

			String ret = jedis.set(key, jsonData);
			boolean isSuc = ret.equalsIgnoreCase("ok");
			if (isSuc) {
				jedis.expire(key, Const.NEIGHBOUR_LIST_EXPIRE_SECONDS);
				return true;
			}
			
			return false;
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		}
		finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}
		
		return false;
	}
	
	/**
	 * 删除[附近的人]
	 * @param geoHash
	 * @return
	 */
	public static boolean removeNeighbourInfo(String uid) {		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO);
			
			jedis.del(createNeighbourKey(uid, Const.LBS_GEO_HASH_ALL, true), createNeighbourKey(uid, Const.LBS_GEO_HASH_ALL, false),
					createNeighbourKey(uid, Const.LBS_GEO_HASH_MEN, true), createNeighbourKey(uid, Const.LBS_GEO_HASH_MEN, false),
					createNeighbourKey(uid, Const.LBS_GEO_HASH_WOMEN, true), createNeighbourKey(uid, Const.LBS_GEO_HASH_WOMEN, false));
			return true;
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		}
		finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}
		
		return false;
	}
	
	/**
	 * 生成 GeoHash 的key
	 * @param geoHash
	 * @param gender
	 * @return
	 */
	private static String createGeoHashKey(String geoHash, String gender) {
		return Const.LBS_GEO_HASH_PREFIX + gender + geoHash;
	}

	/**
	 * 缓存某用户的 PlayerInfo & GeoHash信息
	 * @param playerInfo
	 * @param oldPlayerInfo
	 * @param isCacheGeo
	 * @return
	 */
	public static boolean putPlayerInfoAndGeoHash(PlayerInfo playerInfo, PlayerInfo oldPlayerInfo, boolean isCacheGeo) {		
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_PLAYER_INFO);
			Gson gson = CommonToolkit.getGson();
			
			String playerInfoKey = getPlayerInfoKey(playerInfo.getUid());
			String ret = jedis.set(playerInfoKey, gson.toJson(playerInfo));
			
			if (isCacheGeo) {
				final int nowSeconds = (int) (System.currentTimeMillis() / 1000);
				
				jedis.select(Const.REDIS_DB_GEO);
				
				// 如果用户的经纬度改变，或者性别改变，需要把缓存中上一次的地理数据清除
				if (null != oldPlayerInfo) {
					if (Double.compare(oldPlayerInfo.getLat(), playerInfo.getLat()) != 0 
							|| Double.compare(oldPlayerInfo.getLng(), playerInfo.getLng()) != 0 
							|| oldPlayerInfo.isGender() != playerInfo.isGender()) {
						String oldGeoHash = GeoToolkit.getGeoHashBase32(oldPlayerInfo.getScaleLatForGeohashMinPoint(), oldPlayerInfo.getScaleLngForGeohashMinPoint());
						String geoHashAllKey = createGeoHashKey(oldGeoHash, Const.LBS_GEO_HASH_ALL);
						String geoHashGenderKey = createGeoHashKey(oldGeoHash, oldPlayerInfo.isGender() ? Const.LBS_GEO_HASH_MEN : Const.LBS_GEO_HASH_WOMEN);
						
						jedis.zrem(geoHashAllKey, playerInfoKey);
						jedis.zrem(geoHashGenderKey, playerInfoKey);
					}
				}
				
				String geoHash = GeoToolkit.getGeoHashBase32(playerInfo.getScaleLatForGeohashMinPoint(), playerInfo.getScaleLngForGeohashMinPoint());
				String geoHashAllKey = createGeoHashKey(geoHash, Const.LBS_GEO_HASH_ALL);
				String geoHashGenderKey = createGeoHashKey(geoHash, playerInfo.isGender() ? Const.LBS_GEO_HASH_MEN : Const.LBS_GEO_HASH_WOMEN);
				
				jedis.zadd(geoHashAllKey, nowSeconds, playerInfoKey);
				jedis.zadd(geoHashGenderKey, nowSeconds, playerInfoKey);
				putGeoHashLngAndLat(playerInfo.getLat(), playerInfo.getLng(), playerInfoKey);
			}
			
			boolean isSuc = ret.equalsIgnoreCase("ok");
			if (isSuc) {
				jedis.expire(playerInfoKey, Const.PLAYER_INFO_CACHE_EXPIRE_SECONDS);
				return true;
			}
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		}
		finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}
		
		return false;
	}

	/**
	 * 删除geoHash的对应性别的用户
	 * @param geoHash
	 * @param playerInfoKey
	 * @return
	 */
	public static boolean removeGeoHash(String geoHash, String playerInfoKey) {
		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO);
			String geoHashAllKey = createGeoHashKey(geoHash, Const.LBS_GEO_HASH_ALL);
			String geoHashMenKey = createGeoHashKey(geoHash, Const.LBS_GEO_HASH_MEN);
			String geoHashWoMenKey = createGeoHashKey(geoHash, Const.LBS_GEO_HASH_WOMEN);
			
			jedis.zrem(geoHashAllKey, playerInfoKey);
			jedis.zrem(geoHashMenKey, playerInfoKey);
			jedis.zrem(geoHashWoMenKey, playerInfoKey);
			return true;
		}
		catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		}
		finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}
		
		return false;
	}

	/**
	 * 获取geoHash的对应性别的用户集合
	 * 
	 * @param geoHash
	 * @param gender
	 * @oaram isOnline
	 * @return
	 */
	public static Set<String> getGeoHashSet(String geoHash, String gender, boolean isOnline) {
		String geoHashKey = createGeoHashKey(geoHash, gender);
		
		final int nowSeconds = (int) (System.currentTimeMillis() / 1000);
		final int max = Integer.MAX_VALUE;
		final int min = isOnline ? (nowSeconds - Const.ONLINE_TIME_FLAG / 1000) : 0;

		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO);

			return jedis.zrangeByScore(geoHashKey, min, max);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}

		return null;
	}

	/**
	 * 缓存geoHash经纬块的玩家列表
	 * 
	 * @param areaGeoHash
	 * @param info
	 * @return
	 */
	public static boolean putGeoHashAreaPlayer(String areaGeoHash, RangePlayerInfo info) {

		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO_DISTRIBUTE);
			String jsonData = CommonToolkit.getGson().toJson(info);

			String ret = jedis.set(areaGeoHash, jsonData);
			boolean isSuc = ret.equalsIgnoreCase("ok");
			if (isSuc) {
				jedis.expire(areaGeoHash, Const.NEIGHBOUR_LIST_EXPIRE_SECONDS);
				return true;
			}

			return false;
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}

		return false;
	}

	/**
	 * 获取geoHash经纬块的玩家列表
	 * 
	 * @param areaGeoHash
	 * @return
	 */
	public static RangePlayerInfo getGeoHashAreaPlayer(String areaGeoHash) {

		boolean borrowOrOprSuccess = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.REDIS_DB_GEO_DISTRIBUTE);
			String jsonData = jedis.get(areaGeoHash);
			if (jsonData != null) {
				RangePlayerInfo data = CommonToolkit.getGson().fromJson(jsonData, RangePlayerInfo.class);
				return data;
			}
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			borrowOrOprSuccess = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (borrowOrOprSuccess) {
				pool.returnResource(jedis);
			}
		}

		return null;
	}
	/**
	 * 缓存经纬度线玩家
	 * 
	 * @param areaGeoHash
	 * @param pointGeoHash
	 * @return
	 */
	public static boolean putGeoHashLngAndLat(double lat, double lng, String pid) {
		boolean success = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.GEO_DISTRIBUTE_LNG_LAT);
			jedis.zrem(Const.GEO_DISTRIBUTE_LAT, pid);
			jedis.zadd(Const.GEO_DISTRIBUTE_LAT, lat, pid);

			jedis.zrem(Const.GEO_DISTRIBUTE_LNG, pid);
			jedis.zadd(Const.GEO_DISTRIBUTE_LNG, lng, pid);

			return true;
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			success = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (success) {
				pool.returnResource(jedis);
			}
		}

		return false;
	}

	/**
	 * 去掉经纬度线玩家
	 * 
	 * @param areaGeoHash
	 * @param pointGeoHash
	 * @return
	 */
	public static boolean removeGeoHashLngAndLat(double lat, double lng, String pid) {
		boolean success = true;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.select(Const.GEO_DISTRIBUTE_LNG_LAT);
			jedis.zrem(Const.GEO_DISTRIBUTE_LAT, pid);
			jedis.zrem(Const.GEO_DISTRIBUTE_LNG, pid);
			return true;
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			success = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (success) {
				pool.returnResource(jedis);
			}
		}

		return false;
	}

	/**
	 * 根据经纬度读取范围内玩家
	 * 
	 * @param minLat
	 * @param maxLat
	 * @param minLng
	 * @param maxLng
	 * @return
	 */
	public static Set<String> getGeoHashByLngAndLat(double minLat, double maxLat, double minLng, double maxLng) {
		boolean success = true;
		Jedis jedis = null;
		Set<String> set = new HashSet<String>();
		try {
			jedis = pool.getResource();
			jedis.select(Const.GEO_DISTRIBUTE_LNG_LAT);
			Set<String> set1 = jedis.zrangeByScore(Const.GEO_DISTRIBUTE_LAT, minLat, maxLat);
			Set<String> set2 = jedis.zrangeByScore(Const.GEO_DISTRIBUTE_LNG, minLng, maxLng);

			if (set1.size() > set2.size()) {
				for (String s : set2) {
					if (set1.contains(s)) {
						set.add(s);
					}
				}
			} else {
				for (String s : set1) {
					if (set2.contains(s)) {
						set.add(s);
					}
				}
			}

		} catch (JedisConnectionException e) {
			e.printStackTrace();
			success = false;
			if (jedis != null) {
				pool.returnBrokenResource(jedis);
			}
		} finally {
			if (success) {
				pool.returnResource(jedis);
			}
		}

		return set;
	}

	public static JedisPool getPool() {
		return pool;
	}

	// ========== 获取附近的人 End ==============================================================
	
}
