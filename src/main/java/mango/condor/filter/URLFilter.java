package mango.condor.filter;

import it.sauronsoftware.base64.Base64;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.toolkit.CaesarCipher;
import mango.condor.toolkit.Const;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * 解密参数
 * 
 * @Author Create by 邢陈程
 * @Date 2013-6-8 上午11:25:04
 * @Description 
 */
public class URLFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
			
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = ((HttpServletResponse) servletResponse);
		
		/*String mid = request.getParameter("msg_id");
		System.out.println("mid===========================  ==="+mid);*/

        // 对请求先进行BASE64解码，再解密
    	String rawData = request.getParameter("data");
    	if (rawData == null) {
    		StringBuilder log = new StringBuilder("URLFilter ERROR");
    		log.append(" uri=").append(request.getRequestURI());
    		log.append(" ip=").append(request.getRemoteHost());
    		Enumeration<?> e = request.getParameterNames();
    		while (e.hasMoreElements()) {
    			String key = (String) e.nextElement();
    			Object value = request.getParameter(key);
    			if (value.getClass().isArray()) {
    				log.append(" " + key + "=").append( Arrays.toString( (String[])value ) );		
    			}
    			else {
    				log.append(" " + key + "=").append( value );
    			}
    		}
    		
    		System.out.println( log.toString() );
    		return;
    	}
    	
    	// 对中文Base64编码可能会出现空格
    	rawData = rawData.replaceAll(" ", "+");
    	String data = Base64.decode(CaesarCipher.decrypt(rawData), Const.CHARSET);
    	
    	request.setAttribute("data", data);
        
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
