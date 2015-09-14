package mango.condor.service.timer;

import java.util.Calendar;
import java.util.Date;

import mango.condor.dao.ConnectionPool;

import com.gzyouai.hummingbird.common.dao.PreparedStatementDao;
import com.gzyouai.hummingbird.common.utils.DateTimeUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月13日 下午5:18:17
 * @Description 
 */
public class BottleTimerCleaner extends PreparedStatementDao implements Runnable {
	
	public BottleTimerCleaner() {
		super(ConnectionPool.getInstance());
	}
	
	@Override
	public void run() {		
		clear30DaysAgoBottle();
		clear15DaysAgoBottle();
	}

	/**
	 * 清除 [30天前] 的 [所有瓶子]
	 */
	private void clear30DaysAgoBottle() {
		final int limit = 20000;
		
		Calendar cal = Calendar.getInstance();
		long start = cal.getTimeInMillis();
		
		DateTimeUtil.plusDaysToCalendar(cal, -30);
		DateTimeUtil.fillTimeToCalendar(cal, 0, 0, 0, 0);
		String anyday = DateTimeUtil.formatDateTime(new Date(cal.getTimeInMillis()), "yyyy-MM-dd HH:mm:ss");
		
		String sql = "DELETE FROM bottle WHERE sendTime<=? LIMIT ?";
		
		int cleanRows = 0;
		try {
			for (int i = 0; i < 100; i++) {
				int updateRows = updatePreparedStatement(sql, anyday, limit);
				cleanRows += updateRows;
				if (updateRows < limit) {
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println( "clear30DaysAgoBottle clear be done. cleanRows=" + cleanRows + ", execute_millis=" + (System.currentTimeMillis() - start));
	}
	
	/**
	 * 清除 [15天前] 的 [双方已领取奖励的瓶子]
	 */
	private void clear15DaysAgoBottle() {
		final int limit = 20000;
		
		Calendar cal = Calendar.getInstance();
		long start = cal.getTimeInMillis();
		
		DateTimeUtil.plusDaysToCalendar(cal, -15);
		DateTimeUtil.fillTimeToCalendar(cal, 0, 0, 0, 0);
		String anyday = DateTimeUtil.formatDateTime(new Date(cal.getTimeInMillis()), "yyyy-MM-dd HH:mm:ss");
		
		String sql = "DELETE FROM bottle WHERE sendTime<=? AND senderRewarded=1 AND receiverRewarded=1 LIMIT ?";
		
		int cleanRows = 0;
		try {
			for (int i = 0; i < 100; i++) {
				int updateRows = updatePreparedStatement(sql, anyday, limit);
				cleanRows += updateRows;
				if (updateRows < limit) {
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println( "clear15DaysAgoBottle clear be done. cleanRows=" + cleanRows + ", execute_millis=" + (System.currentTimeMillis() - start));
	}
}