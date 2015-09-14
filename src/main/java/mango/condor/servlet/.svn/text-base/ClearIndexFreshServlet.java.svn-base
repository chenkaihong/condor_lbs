package mango.condor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-26 上午10:58:04
 * @Description 
 * 定期清除新鲜事数量
 */
public class ClearIndexFreshServlet extends HttpServlet {

	private static final long serialVersionUID = 6054669270095629102L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		RedisToolkit.clearIndexFresh();
		resp.getWriter().print("OK");

	}

}
