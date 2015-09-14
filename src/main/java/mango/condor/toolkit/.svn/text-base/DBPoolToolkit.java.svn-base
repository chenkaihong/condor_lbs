package mango.condor.toolkit;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-9 下午7:37:19
 * @Description
 */
public class DBPoolToolkit {
	private static ComboPooledDataSource cpds;
	
	/**
	 * 初始化数据库连接池，容器启动时调用
	 * @throws SQLException 
	 */
	public static void init() {
		destroy();
		cpds = new ComboPooledDataSource("DataCenterDB");

		try {
			cpds.setDriverClass("org.gjt.mm.mysql.Driver");
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 释放数据库连接池，容器关闭时调用
	 */
	public static void destroy() {
		if (cpds != null) {
			// cpds.close();
			try {
				DataSources.destroy(cpds);
				cpds = null;
			} catch (SQLException e) {
				//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = cpds.getConnection();
			
			if (conn != null) {
				if (!conn.getAutoCommit()) {
					// 设置为自动提交
					conn.setAutoCommit(true);
				}
			}
			
		} catch (SQLException e) {
			// 获取数据库连接池失败
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		}
		
		return conn;
	}
	
}
