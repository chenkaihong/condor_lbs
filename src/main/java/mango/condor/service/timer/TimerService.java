package mango.condor.service.timer;

import java.net.InetAddress;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gzyouai.hummingbird.common.utils.DateTimeUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月13日 下午4:34:08
 * @Description 
 */
public class TimerService {
	private static ScheduledThreadPoolExecutor executor;
	
	public synchronized static void init () {
		if (null == executor) {
			try {
				String localIP = InetAddress.getLocalHost().getHostAddress();
				if ("192.168.100.53".equals(localIP)) {
					/**
					 * 大陆LBS有两台服务器，一台执行定时清理就可以了
					 */
					System.out.println( "localIP=" + localIP + " be ignore timer clean actions." );
					return;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
			
			// 每天凌晨5点45定时清理 聊天数据
			long initialDelayOfChatTimerCleaner = DateTimeUtil.getMillisToNextClock(5, 45, 0);
			long periodOfChatTimerCleaner = DateTimeUtil.MILLISECOND_IN_DAY;
			ChatTimerCleaner chatTimerCleaner = new ChatTimerCleaner();
			executor.scheduleAtFixedRate(chatTimerCleaner, 
					initialDelayOfChatTimerCleaner, periodOfChatTimerCleaner, 
					TimeUnit.MILLISECONDS);
			System.out.println( "TimerService start. initialDelayOfChatTimerCleaner=" + initialDelayOfChatTimerCleaner );
			
			// 每天凌晨5点30定时清理 漂流瓶数据
			long initialDelayOfBottleTimerCleaner = DateTimeUtil.getMillisToNextClock(5, 30, 0);
			long periodOfBottleTimerCleaner = DateTimeUtil.MILLISECOND_IN_DAY;
			BottleTimerCleaner bottleTimerCleaner = new BottleTimerCleaner();
			executor.scheduleAtFixedRate(bottleTimerCleaner, 
					initialDelayOfBottleTimerCleaner, periodOfBottleTimerCleaner, 
					TimeUnit.MILLISECONDS);
			System.out.println( "TimerService start. initialDelayOfBottleTimerCleaner=" + initialDelayOfBottleTimerCleaner );
			
			// 每天凌晨5点清理不合法的用户数据
			long initialDelayOfUserDataTimerCleaner = DateTimeUtil.getMillisToNextClock(5, 0, 0);
			long periodOfUserDataTimerCleaner = DateTimeUtil.MILLISECOND_IN_DAY;
			Runnable userDataTimerCleaner = new UserDataTimerCleaner();
			executor.scheduleAtFixedRate(userDataTimerCleaner, 
					initialDelayOfUserDataTimerCleaner, periodOfUserDataTimerCleaner, 
					TimeUnit.MILLISECONDS);
			System.out.println( "TimerService start. initialDelayOfUserDataTimerCleaner=" + initialDelayOfUserDataTimerCleaner );
		}
	}
	
	public synchronized static void destroy () {
		if (null != executor) {
			executor.shutdown();
			executor = null;
			System.out.println( "TimerService destroy" );
		}
	}
}
