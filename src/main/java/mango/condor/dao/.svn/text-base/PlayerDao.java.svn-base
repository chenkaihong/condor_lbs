package mango.condor.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mango.condor.domain.lbs.PlayerInfo;

import com.gzyouai.hummingbird.common.component.GameRuntimeException;
import com.gzyouai.hummingbird.common.dao.DaoConnectionPool;
import com.gzyouai.hummingbird.common.dao.PreparedStatementDao;
import com.gzyouai.hummingbird.common.utils.StringUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月14日 下午3:08:31
 * @Description 
 */
public class PlayerDao extends PreparedStatementDao {
	PlayerDao(DaoConnectionPool pool) {
		super(pool);
	}
	
	/**
	 * 获取一个PlayerInfo
	 * @param uid
	 * @return
	 */
	public PlayerInfo getPlayerInfo (String uid) {
		if (uid == null) {
			return null;
		}
		
		String sql = "SELECT * FROM player_info WHERE uid=? LIMIT 1";
		
		try {
			return selectPreparedStatement(PlayerInfo.class, sql, uid);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 批量获取 PlayerInfo
	 * @param uidList
	 * @return
	 * 		Map<Uid, PlayerInfo>
	 */
	public Map<String, PlayerInfo> batchPlayerInfo (List<String> uidList) {
		if (uidList == null) {
			return null;
		}
		if (uidList.isEmpty()) {
			return new HashMap<String, PlayerInfo>();
		}
		uidList = new ArrayList<String>( new HashSet<String>(uidList) );
		final int MAX = 100;
		if (uidList.size() > MAX) {
			throw new GameRuntimeException("batchPlayerInfo too long parameter. uidList.size=" + uidList.size());
		}
		
		String sql = String.format("SELECT * FROM player_info WHERE uid in (%s)", 
				StringUtil.join4KeyWordOf_SQL_IN(uidList, MAX));
		
		List<PlayerInfo> list = null;
		try {
			list = batchSelectPreparedStatement(PlayerInfo.class, sql);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Map<String, PlayerInfo> map = new HashMap<String, PlayerInfo>();
		for (PlayerInfo player : list) {
			map.put(player.getUid(), player);
		}
		
		return map;
	}
}
