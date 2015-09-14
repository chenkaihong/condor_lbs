package mango.condor.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mango.condor.domain.bottle.Bottle;

import com.gzyouai.hummingbird.common.component.GameRuntimeException;
import com.gzyouai.hummingbird.common.dao.DaoConnectionPool;
import com.gzyouai.hummingbird.common.dao.PreparedStatementDao;
import com.gzyouai.hummingbird.common.dao.record.CountDaoRecord;
import com.gzyouai.hummingbird.common.dao.record.IntegerDaoRecord;
import com.gzyouai.hummingbird.common.utils.StringUtil;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月14日 下午3:08:31
 * @Description 
 */
public class BottleDao extends PreparedStatementDao {
	BottleDao(DaoConnectionPool pool) {
		super(pool);
	}
	
	/**
	 * 增加一个漂流瓶，并填充漂流瓶Id
	 * @param bottle
	 * @return
	 */
	public boolean addBottleAndSetBottleId (Bottle bottle) {
		if (bottle == null) {
			return false;
		}
		
		String sql = "INSERT INTO bottle(sender, text, vid) VALUES (?, ?, ?)";
		
		long autoId = 0;
		try {
			autoId = insertAndFecthAutoIdPreparedStatement(sql, bottle.getSender(), bottle.getText(), bottle.getVid());
			
			if (autoId > 0) {
				bottle.setId(autoId);
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 更新瓶子
	 * @param bottle
	 * @return
	 */
	public boolean updateBottle (Bottle bottle) {
		String sql = "UPDATE bottle SET sender =? , receiver = ?, rewardId = ?,"
				+ " senderRewarded = ?, receiverRewarded = ?,"
				+ " senderReaded = ?, receiverReaded = ?,"
				+ " senderDeleted = ?, receiverDeleted = ?, sendTime = ?,"
				+ " replyText=?, replyTime=?"
				+ " WHERE id = ?";
		
		try {
			return updatePreparedStatement(sql, 
					bottle.getSender(), bottle.getReceiver(), bottle.getRewardId(),
					bottle.isSenderRewarded(), bottle.isReceiverRewarded(),
					bottle.isSenderReaded(), bottle.isReceiverReaded(),
					bottle.isSenderDeleted(), bottle.isReceiverDeleted(), new Timestamp(bottle.getSendTime()),
					bottle.getReplyText(), bottle.getReplyTime() == 0 ? null : new Timestamp(bottle.getReplyTime()),
					bottle.getId()
			) > 0;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 删除一个瓶子
	 * @param bottleId
	 * @return
	 */
	public boolean deleteBottle (long bottleId) {
		String sql = "DELETE FROM bottle WHERE id = ?";
		
		try {
			updatePreparedStatement(sql, bottleId);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 获取一个瓶子
	 * @param bottleId
	 * @return
	 */
	public Bottle getBottle (long bottleId) {
		String sql = "SELECT * FROM bottle WHERE id = ?";
		
		try {
			return selectPreparedStatement(Bottle.class, sql, bottleId);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取扔出去的瓶子中未读的数量
	 * 		1) 我扔的
	 * 		2) 我未删除
	 * 		3) 已被回复
	 * 		4) 我未读
	 * @param uid
	 * @return
	 */
	public int getUnreadBottleNum (String uid) {
		if (uid == null) {
			return -1;
		}
		
		String sql = "SELECT COUNT(1) FROM bottle WHERE sender=? AND senderDeleted=0 AND receiver IS NOT NULL AND senderReaded=0";
		
		try {
			CountDaoRecord record = selectPreparedStatement(CountDaoRecord.class, sql, uid);
			return record.getCount();
		}
		catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 我的瓶子列表
	 * @param uid
	 * @param throwOrFetch
	 * 		true: 扔的
	 * 			1) 我扔的
	 * 			2) 我未删除
	 * 			3) 已被回复
	 * 		false: 捞的
	 * 			1) 我捞的
	 * 			2) 我未删除
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Bottle> batchMyBottles (String uid, boolean throwOrFetch, int offset, int limit) {
		if (uid == null) {
			return null;
		}
		
		String sql = throwOrFetch
				? "SELECT * FROM bottle WHERE sender=? AND senderDeleted=0 AND receiver IS NOT NULL ORDER BY sendTime DESC LIMIT ?,?"
						: "SELECT * FROM bottle WHERE receiver=? AND receiverDeleted=0 ORDER BY sendTime DESC LIMIT ?,?";
		try {
			return batchSelectPreparedStatement(Bottle.class, sql, uid, offset, limit);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 我的瓶子Id列表
	 * @param uid
	 * @param throwOrFetch
	 * 		true: 扔的
	 * 			1) 我扔的
	 * 			2) 我未删除
	 * 			3) 已被回复
	 * 		false: 捞的
	 * 			1) 我捞的
	 * 			2) 我未删除
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Integer> batchMyBottleIds (String uid, boolean throwOrFetch, int offset, int limit) {
		if (uid == null) {
			return null;
		}
		
		String sql = throwOrFetch
				? "SELECT id FROM bottle WHERE sender=? AND senderDeleted=0 AND receiver IS NOT NULL ORDER BY sendTime DESC LIMIT ?,?"
						: "SELECT id FROM bottle WHERE receiver=? AND receiverDeleted=0 ORDER BY sendTime DESC LIMIT ?,?";
		
		List<IntegerDaoRecord> records = null;
		try {
			records = batchSelectPreparedStatement(IntegerDaoRecord.class, sql, uid, offset, limit);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return IntegerDaoRecord.toIntegerList(records);
	}
	
	/**
	 * 批量获取瓶子
	 * @param bottleIdList
	 * @return
	 */
	public Map<Long, Bottle> batchBottle (List<Long> bottleIdList) {
		if (bottleIdList == null) {
			return null;
		}
		if (bottleIdList.isEmpty()) {
			return new HashMap<Long, Bottle>();
		}
		bottleIdList = new ArrayList<Long>( new HashSet<Long>(bottleIdList) );
		final int MAX = 300;
		if (bottleIdList.size() > MAX) {
			throw new GameRuntimeException("batchBottle too long parameter. bottleIdList.size=" + bottleIdList.size());
		}
		
		String sql = String.format("SELECT * FROM bottle WHERE id in (%s)", 
				StringUtil.join4KeyWordOf_SQL_IN(bottleIdList, MAX));
		
		List<Bottle> list = null;
		try {
			list = batchSelectPreparedStatement(Bottle.class, sql);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Map<Long, Bottle> map = new HashMap<Long, Bottle>();
		for (Bottle bottle : list) {
			map.put(bottle.getId(), bottle);
		}
		
		return map;
	}
}
