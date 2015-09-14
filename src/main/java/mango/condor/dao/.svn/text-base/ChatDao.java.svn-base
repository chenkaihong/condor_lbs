package mango.condor.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mango.condor.domain.chat.ChatMyVo;

import com.gzyouai.hummingbird.common.component.GameRuntimeException;
import com.gzyouai.hummingbird.common.dao.DaoConnectionPool;
import com.gzyouai.hummingbird.common.dao.PreparedStatementDao;
import com.gzyouai.hummingbird.common.dao.record.CountDaoRecord;
import com.gzyouai.hummingbird.common.utils.CollectionUtil;
import com.gzyouai.hummingbird.common.utils.StringUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月14日 下午3:08:31
 * @Description 
 */
public class ChatDao extends PreparedStatementDao {
	ChatDao(DaoConnectionPool pool) {
		super(pool);
	}
	
	/**
	 * 获取一个私信
	 * @param chatId
	 * @return
	 */
	public ChatMyVo getChat (String chatId) {
		String sql = "SELECT * FROM game_chat WHERE chatId = ?";
		
		try {
			return selectPreparedStatement(ChatMyVo.class, sql, chatId);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 批量获取私信
	 * @param chatIdList
	 * @return
	 */
	public Map<String, ChatMyVo> batchChat (List<String> chatIdList) {
		if (chatIdList == null) {
			return null;
		}
		if (chatIdList.isEmpty()) {
			return new HashMap<String, ChatMyVo>();
		}
		chatIdList = new ArrayList<String>( new HashSet<String>(chatIdList) );
		final int MAX = 200;
		if (chatIdList.size() > MAX) {
			throw new GameRuntimeException("batchChat too long parameter. chatIdList.size=" + chatIdList.size());
		}
		
		String sql = String.format("SELECT * FROM game_chat WHERE chatId in (%s)", 
				StringUtil.join4KeyWordOf_SQL_IN(chatIdList, MAX));
		
		List<ChatMyVo> list = null;
		try {
			list = batchSelectPreparedStatement(ChatMyVo.class, sql);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Map<String, ChatMyVo> map = new HashMap<String, ChatMyVo>();
		for (ChatMyVo chat : list) {
			map.put(chat.getChatId(), chat);
		}
		
		return map;
	}
	
	/**
	 * 获取未读私信数量
	 * @param chatId
	 * @return
	 */
	public int selectUnreadChatCount (String uid) {
		String[] sqls = {
				"SELECT COUNT(1) FROM game_chat WHERE chater1Id=? AND unReadNumChater1>0 AND delChater1 IS NOT NULL AND delChater1<lastTime",
				"SELECT COUNT(1) FROM game_chat WHERE chater2Id=? AND unReadNumChater2>0 AND delChater2 IS NOT NULL AND delChater2<lastTime"
		};
		
		try {
			int total = 0;
			for (String sql : sqls) {
				CountDaoRecord record = selectPreparedStatement(CountDaoRecord.class, sql, uid);
				total += record.getCount();
			}
			return total;
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 批量删除聊天记录
	 * 		-- 最多删除20个;如果超过了20,则排序chatIdList,删除前面20条
	 * @param chatIdList
	 * @return
	 */
	public boolean batchDeleteChat (List<String> chatIdList) {
		if (CollectionUtil.isEmptyCollection(chatIdList)) {
			return true;
		}
		
		String chatIds = StringUtil.join4KeyWordOf_SQL_IN(chatIdList, 20);
		String sql = String.format("DELETE FROM game_chat WHERE chatId in (%s)", chatIds);	// TODO 优化 SQL注入
		
		try {
			int rows = updatePreparedStatement(sql);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected boolean test_batchUpdatePreparedStatement(String sql, List<Object[]> data, boolean autoCommit) throws Exception {
		if (data.isEmpty()) {
			return true;
		}

		Connection conn = getConnection();
		PreparedStatement stmt = null;
		try {
			if (autoCommit) {
				// 使用事物
				conn.setAutoCommit(false);
				stmt = conn.prepareStatement(sql);
	
				for (Object[] vals : data) {
					fillPreparedStatement("batchUpdatePreparedStatement", stmt, vals);
					stmt.addBatch();
				}
				int[] arr = stmt.executeBatch();
	
				conn.commit();
				conn.setAutoCommit(true);
				
				return true;
			}
			else {
				stmt = conn.prepareStatement(sql);
				
				for (Object[] vals : data) {
					fillPreparedStatement("batchUpdatePreparedStatement", stmt, vals);
					stmt.addBatch();
				}
				stmt.executeBatch();
				
				return true;
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			close(stmt, conn);
		}
	}	
	
	/**
	 * 批量更新Chat       
	 * @param chatList
	 * @return
	 */
	public boolean batchUpdate (List<ChatMyVo> chatList) {
		if (CollectionUtil.isEmptyCollection(chatList)) {
			return true;
		}
		
		List<Object[]> data = new ArrayList<Object[]>(chatList.size());
		for (ChatMyVo chat : chatList) {
			Object[] params = new Object[]{
					chat.getLastMessage(), new Timestamp(chat.getLastTime()),
					chat.getUnReadNumChater1(),	chat.getUnReadNumChater2(),
					new Timestamp(chat.getReadedTimestampChater1()), new Timestamp(chat.getReadedTimestampChater2()),
					chat.getChater1State(), chat.getChater2State(),
					new Timestamp(chat.getDelTimestampChater1()), new Timestamp(chat.getDelTimestampChater2()),
					new Timestamp(chat.getLastReadedChater1()), new Timestamp(chat.getLastReadedChater2()),
					chat.getChatId()
			};	
			
			data.add(params);
		}
		
		String sql = "UPDATE game_chat "
				+ " SET lastMessage = ?, lastTime = ?,"
				+ " unReadNumChater1 = ?, unReadNumChater2 = ?,"
				+ " readedChater1 = ?, readedChater2 = ?,"
				+ " chater1State = ?, chater2State = ?, "
				+ " delChater1 = ?, delChater2 = ?,"
				+ " lastReadedChater1 = ?, lastReadedChater2 = ?"
				+ " WHERE chatId = ?";
		
		try {
			return test_batchUpdatePreparedStatement(sql, data, true);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
