package mango.condor.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 熊三山  
 * @Date 2014年1月15日 下午5:57:19
 * @Description 
 */
public class UserActionMonitorService {
	private static ConcurrentMap<String, Long> map = new ConcurrentHashMap<String, Long>();
	
	private static final long SAFE_MILLIS = 200; // 两次动作的时间间隔安全时间 - 单位毫秒

	static {
		new Thread("UserActionMonitorService") {
			public void run () {
//				int i = 0;
				while (true) {
//					System.out.println("UserActionMonitorService start.");
					try {
						Thread.sleep(300 * 1000L);
						
						ConcurrentMap<String, Long> oldMap = map;
						map = new ConcurrentHashMap<String, Long>();
//						System.out.println("UserActionMonitorService reset i=" + (++i) + ", size=" + oldMap.size());
						oldMap.clear();
						oldMap = null;
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();	
	}
	
	/**
	 * 是否安全通过动作频率验证
	 * @param playerId
	 * @param nowMillis
	 * @return
	 */
	public static boolean safePass (int playerId, int msgId, long nowMillis) {
		String key = String.format("%s,%s", playerId, msgId);
		
		Long lastMark = map.get(key);
		if (lastMark == null) {
			map.put(key, nowMillis);
			return true;
		}
		
		long diff = nowMillis - lastMark;
		if (diff >= SAFE_MILLIS) {
			map.put(key, nowMillis);
			return true;
		}
		
//		System.out.println( "safePass refuse=" + key + ", diff=" + diff);
		return false; 
	}
}
