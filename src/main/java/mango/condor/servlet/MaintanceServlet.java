package mango.condor.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.toolkit.CommonToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-30 上午11:29:45
 * @Description 
 */
public class MaintanceServlet extends HttpServlet {

	private static final long serialVersionUID = -8097793632367120040L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int isMaintance = Integer.parseInt(req.getParameter("is_m"));
		CommonToolkit.getIsMaintance().set(isMaintance == 1 ? true : false);
		
		resp.getWriter().print(isMaintance == 1 ? "maintance" : "run");

	}

}
