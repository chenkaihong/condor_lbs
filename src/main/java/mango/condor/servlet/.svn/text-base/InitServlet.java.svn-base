package mango.condor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.service.StorageService;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-25 上午10:41:59
 * @Description 
 * 开服前调用
 */
public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = -5171315220368062559L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("\n重新加载缓存ing...");
		RedisToolkit.flushDB(Const.REDIS_DB_BLACK_LIST);
		RedisToolkit.flushDB(Const.REDIS_DB_FOLLOW_LIST);
		StorageService.initFollowListAndBlackList();
		StorageService.genFollowRankAndCache();
		RedisToolkit.flushDB(Const.REDIS_DB_INDEX);
		StorageService.initIndexFresh();
		// 加载未读的漂流瓶
		RedisToolkit.flushDB(Const.REDIS_DB_BOTTLE);
		StorageService.initAllUnreadBottle();
		
		resp.getWriter().print("OK");
	}
	
}
