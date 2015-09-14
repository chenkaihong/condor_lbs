package mango.condor.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import mango.condor.cache.CacheFactory;
import mango.condor.cache.RedisCacheKeyConst;
import mango.condor.dao.DaoFactory;
import mango.condor.domain.msg.LBSMessageDefine;
import mango.condor.service.timer.TimerService;
import mango.condor.toolkit.BMapToolkit;
import mango.condor.toolkit.ConfManager;
import mango.condor.toolkit.DBPoolToolkit;
import mango.condor.toolkit.MultiLanguageManager;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-6-20 下午2:58:58
 * @Description 
 */
public class ServerListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ConfManager.init();
		LBSMessageDefine.init();
		MultiLanguageManager.init();
		RedisToolkit.init();
		DBPoolToolkit.init();
		BMapToolkit.init();
		
		RedisCacheKeyConst.checkUniqueCacheKey();
		CacheFactory.init();
		DaoFactory.init();
		TimerService.init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		RedisToolkit.destory();
		DBPoolToolkit.destroy();
		BMapToolkit.destroy();
		
		TimerService.destroy();
	}

}
