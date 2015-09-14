package mango.condor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.toolkit.RedisToolkit;

/**
 * 重置所有用户GeoHash数据
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 李兴
* @Date 2013年12月4日 下午2:12:22
* @Description
 */
public class ResetGeoHashDataServlet extends HttpServlet {

	private static final long serialVersionUID = 6054669270095629102L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// curl "http://10.20.202.66:18091/lbs/ResetGeoHashDataServlet"
		// curl "http://183.61.86.75:8090/lbs/ResetGeoHashDataServlet"		// 封测
		// curl "http://14.17.123.181:18090/lbs/ResetGeoHashDataServlet"
		resp.getWriter().println("========================");
		resp.getWriter().println(RedisToolkit.resetAllPlayerGeoHash());
		resp.getWriter().println("reset over.");
		resp.getWriter().println();
	}

}
