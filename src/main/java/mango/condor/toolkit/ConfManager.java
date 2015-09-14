package mango.condor.toolkit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-8-24 上午10:38:59
 * @Description 
 */
public class ConfManager {
	public static boolean isUseBaiduAPI;
	
	public static String redisHost;
	public static int redisPort;
	public static int redisMaxActive;
	public static int redisMaxIdle;
	public static int redisMinIdle;
	
	public static void main(String[] args) {
		init();
	}

	public static void init() {
		Properties p = new Properties();
		InputStream is = ConfManager.class.getClassLoader().getResourceAsStream("core-foundation.properties");

		try {
			p.load(is);
		} catch (IOException e) {
			throw new RuntimeException("core-foundation properties load failed.", e);
		}
		
		String confId = p.getProperty("conf_id");
		String c3p0Path = ConfManager.class.getClassLoader().getResource("c3p0-config-" + confId + ".xml").getPath();
		System.setProperty("com.mchange.v2.c3p0.cfg.xml", c3p0Path);
		isUseBaiduAPI = !confId.contains("korea");
		
		redisHost = p.getProperty("redis.host." + confId);
		redisPort = Integer.parseInt(p.getProperty("redis.port." + confId));
		redisMaxActive = Integer.parseInt(p.getProperty("redis.maxActive." + confId));
		redisMaxIdle = Integer.parseInt(p.getProperty("redis.maxIdle." + confId));
		redisMinIdle = Integer.parseInt(p.getProperty("redis.minIdle." + confId));
	}
}
