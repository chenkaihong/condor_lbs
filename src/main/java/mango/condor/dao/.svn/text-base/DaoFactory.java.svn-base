package mango.condor.dao;

import java.util.Map;

import com.gzyouai.hummingbird.common.component.ComponentMap;
import com.gzyouai.hummingbird.common.dao.BaseDao;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 李兴
 * @Date 2014年1月16日 下午4:50:44
 * @Description
 */
public class DaoFactory {
	private static ConnectionPool pool = ConnectionPool.getInstance();

	private static Map<String, BaseDao> daoMap = new ComponentMap<String, BaseDao>(DaoFactory.class.getName(), false);

	static {
		daoMap.put(ChatDao.class.getName(), new ChatDao(pool));
		daoMap.put(BottleDao.class.getName(), new BottleDao(pool));
		daoMap.put(PlayerDao.class.getName(), new PlayerDao(pool));
		System.out.println( "DaoFactory init.." + "\t@@:\t" + daoMap.keySet() );
	}

	public static void init () {
		
	}
	
	public static ChatDao getChatDao() {
		return (ChatDao) daoMap.get(ChatDao.class.getName());
	}
	
	public static BottleDao getBottleDao() {
		return (BottleDao) daoMap.get(BottleDao.class.getName());
	}
	
	public static PlayerDao getPlayerDao() {
		return (PlayerDao) daoMap.get(PlayerDao.class.getName());
	}
}
