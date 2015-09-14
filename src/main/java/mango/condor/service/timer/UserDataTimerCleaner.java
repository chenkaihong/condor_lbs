package mango.condor.service.timer;

import mango.condor.dao.ConnectionPool;

import com.gzyouai.hummingbird.common.dao.PreparedStatementDao;

/**
 * 清理不合法的用户数据
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月13日 下午5:18:17
 * @Description 
 */
public class UserDataTimerCleaner extends PreparedStatementDao implements Runnable {
	
	public UserDataTimerCleaner() {
		super(ConnectionPool.getInstance());
	}
	
	@Override
	public void run() {
		clearInvalidUserData();
	}

	/**
	 * 清理不合法的用户数据
	 */
	private void clearInvalidUserData() {
		String[] sqls = {
				// 清理漂流瓶
				"DELETE bottle FROM bottle LEFT JOIN player_info ON bottle.sender=player_info.uid WHERE player_info.name IS NULL",
				"DELETE bottle FROM bottle LEFT JOIN player_info ON bottle.receiver=player_info.uid WHERE bottle.receiver IS NOT NULL AND player_info.name IS NULL",
				// 清理黑名单 
				"DELETE black_list FROM black_list LEFT JOIN player_info ON black_list.pid=player_info.uid WHERE player_info.name IS NULL",
				"DELETE black_list FROM black_list LEFT JOIN player_info ON black_list.tid=player_info.uid WHERE player_info.name IS NULL",
				// 清理关注
				"DELETE follow_list FROM follow_list LEFT JOIN player_info ON follow_list.follower=player_info.uid WHERE player_info.name IS NULL",
				"DELETE follow_list FROM follow_list LEFT JOIN player_info ON follow_list.followed=player_info.uid WHERE player_info.name IS NULL",
				// 清理私信
				"DELETE game_chat FROM game_chat LEFT JOIN player_info ON game_chat.chater1Id=player_info.uid WHERE player_info.name IS NULL",
				"DELETE game_chat FROM game_chat LEFT JOIN player_info ON game_chat.chater2Id=player_info.uid WHERE player_info.name IS NULL"
		};
		
		for (String sql : sqls) {
			try {
				int rows = updatePreparedStatement(sql);
				System.out.println( "UserDataTimerCleaner.clearInvalidUserData: rows=" + rows + ", sql=" + sql);
			}
			catch (Exception e) {
				System.out.println( "UserDataTimerCleaner.clearInvalidUserData: Exception=" + e.getMessage() + ", sql=" + sql);
				e.printStackTrace();
			}
		}
	}
}