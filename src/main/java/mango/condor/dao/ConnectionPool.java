package mango.condor.dao;

import java.sql.Connection;
import java.sql.SQLException;

import mango.condor.toolkit.DBPoolToolkit;

import com.gzyouai.hummingbird.common.dao.DaoConnectionPool;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 李兴
 * @Date 2014年1月16日 下午8:10:35
 * @Description
 */
public class ConnectionPool implements DaoConnectionPool {
	private final static ConnectionPool instance = new ConnectionPool ();
	
	private ConnectionPool () {
		
	}
	
	public static ConnectionPool getInstance() {
		return instance;
	}

	@Override
	public Connection getConnection() {
		try {
			Connection conn = DBPoolToolkit.getConnection();
			if (conn != null) {
				if (!conn.getAutoCommit()) {
					// 设置为自动提交
					conn.setAutoCommit(true);
				}
			}
			return conn;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
