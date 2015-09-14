package mango.condor.toolkit;


/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-18 下午5:12:54
 * @Description
 * 算法来源:https://github.com/huangz1990/redis_lock_algorithm/blob/master/h_with_release.rb
 */
public class RedisLock {
	
	/**
	 * 加锁
	 * @param key
	 * @return
	 */
	public static boolean lock(String key) {
//		Jedis jedis = null;
//		
//		try {
//			jedis = RedisToolkit.getJedis();
//			if (null == jedis) {
//				return false;
//			}
//			
//			jedis.select(Const.REDIS_DB_LOCK);
//			int spinCnt = 0;
//			while (spinCnt < Const.LOCK_SPIN_MAX_CNT) {
//				jedis.watch(key);
//				
//				boolean isExist = jedis.exists(key);
//				if (isExist) {
//					jedis.unwatch();
//				} else {
//					Transaction t = jedis.multi();
//					t.setex(key, Const.LOCK_TTL, Const.DUMMY);
//					List<Object> result = t.exec();
//					if (null != result && false == result.isEmpty()) {
//						String r = (String) result.get(0);
//						if (null != r && r.equalsIgnoreCase("OK")) {						
//							return true;
//						}
//					}
//				}
//				
//				try {
//					Thread.sleep(Const.LOCK_SPIN_MILLI);
//				} catch (InterruptedException e) {
//					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e,	null, null);
//					e.printStackTrace();
//				}
//				
//				++spinCnt;
//			}
//			
//			return false;
//		} catch (Exception e) {
//			if (null != jedis) {
//				RedisToolkit.returnJedis(jedis, true);
//			}
//			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e,null, null);
//			e.printStackTrace();
//			return false;
//		} finally {
//			if (null != jedis) {
//				RedisToolkit.returnJedis(jedis, false);
//			}
//		}
		return true;
	}
	
	/**
	 * 释放锁
	 * @param key
	 * @return
	 */
	public static boolean release(String key) {
//		Jedis jedis = null;
//		try {
//			jedis = RedisToolkit.getJedis();
//			if (null == jedis) {
//				return false;
//			}
//			
//			jedis.select(Const.REDIS_DB_LOCK);
//			jedis.watch(key);
//			String v = jedis.get(key);
//			if (null != v && v.equals(Const.DUMMY)) {
//				Transaction t = jedis.multi();
//				t.del(key);
//				List<Object> result = t.exec();
//				if (null != result && false == result.isEmpty()) {
//					Long r = (Long) result.get(0);
//					if (null != r && 1L == r) {
//						return true;
//					}
//				}
//			} else {
//				jedis.unwatch();
//			}
//			return false;
//		} catch (Exception e) {
//			if (null != jedis) {
//				RedisToolkit.returnJedis(jedis, true);
//			}
//			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_REDIS, e,	null, null);
//			e.printStackTrace();
//			return false;
//		} finally {
//			if (null != jedis) {
//				RedisToolkit.returnJedis(jedis, false);
//			}
//		}
//		
		return true;
	}
}
