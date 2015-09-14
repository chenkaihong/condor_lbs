package mango.condor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.service.StorageService;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-25 下午1:57:08
 * @Description 
 * 每小时调用一次，重新生成关注排行榜
 * 
 * 		curl "http://183.61.86.75:8090/lbs/rank"		// 封测
 */
public class GenRankServlet extends HttpServlet {

	private static final long serialVersionUID = 1540515392966146058L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StorageService.genFollowRankAndCache();
		resp.getWriter().println("OK");
	}

}
