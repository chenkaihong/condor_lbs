package mango.condor.toolkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mango.condor.domain.bottle.Bottle;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-9-27 上午11:15:11
 * @Description 
 */
public class DBBottleToolkit {
	private static final String INSERT_BOTTLE_SQL = "INSERT INTO bottle(sender, text, vid) VALUES (?, ?, ?)";
	
	private static final String UPDATE_BOTTLE_SQL = "UPDATE bottle SET sender =? , receiver = ?, rewardId = ?,"
			+ " senderRewarded = ?, receiverRewarded = ?,"
			+ " senderReaded = ?, receiverReaded = ?,"
			+ " senderDeleted = ?, receiverDeleted = ?, sendTime = ?,"
			+ " replyText=?, replyTime=?"
			+ " WHERE id = ?";
	
	private static final String SELECT_UNREAD_BOTTLE_SQL = "SELECT * FROM bottle WHERE receiver IS NULL AND id >= ? AND id < ?";
	
	private static final String SELECT_MY_THROW_BOTTLE_SQL = "SELECT * FROM bottle WHERE sender = ? AND " +
			"senderDeleted = 0 AND receiver IS NOT NULL ORDER BY sendTime DESC LIMIT ?, ?";
	
	private static final String SELECT_MY_FETCH_BOTTLE_SQL = "SELECT * FROM bottle WHERE receiver = ? AND receiverDeleted = 0 " +
			"ORDER BY sendTime DESC LIMIT ?, ?";
	
	private static final String SELECT_BOTTLE_BY_ID_SQL = "SELECT * FROM bottle WHERE id = ?";
	
	private static final String SELECT_THROW_UNREAD_NUM_SQL = "SELECT COUNT(*) FROM bottle WHERE sender = ? AND senderReaded != 1 AND senderDeleted=0";
	
	private static final String SELECT_FETCH_UNREAD_NUM_SQL = "SELECT COUNT(*) FROM bottle WHERE receiver = ? AND receiverReaded != 1 AND receiverDeleted=0";
	
	private static final String DELETE_BOTTLES_SQL = "DELETE FROM bottle WHERE id = ?";
	
	private static final String SELECT_MY_THROW_BOTTLE_ID_SQL = "SELECT id FROM bottle WHERE sender = ? AND senderDeleted = 0 " +
			"AND receiver IS NOT NULL ORDER BY sendTime DESC";
	
	private static final String SELECT_MY_FETCH_BOTTLE_ID_SQL = "SELECT id FROM bottle WHERE receiver = ? AND " +
			"receiverDeleted = 0 ORDER BY sendTime DESC";
	
	/**
	 * 查询某用户相关的所有瓶子的ID
	 * type=0为查询扔出的瓶子
	 * type=1为查询收到的瓶子
	 * @param uid
	 * @param type
	 * @return
	 */
	public static List<Long> selectMyBottleId(String uid, int type) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (type == 0) {
				ps = conn.prepareStatement(SELECT_MY_THROW_BOTTLE_ID_SQL);
			} else {
				ps = conn.prepareStatement(SELECT_MY_FETCH_BOTTLE_ID_SQL);
			}
			
			ps.setString(1, uid);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<Long> retList = new ArrayList<Long>();
			while (rs.next()) {
				retList.add(rs.getLong("id"));
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	/**
	 * 删除瓶子
	 * @param bottleIdList
	 * @return
	 */
	public static boolean deleteBottle(long bottleId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_BOTTLES_SQL);
			
			ps.setLong(1, bottleId);

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return false;
	}
		
	/**
	 * 获取未读瓶子的数量
	 * @param isThrowOrFetch
	 * @param uid
	 * @return
	 */
	public static int selectUnreadNum(boolean isThrowOrFetch, String uid) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return 0;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (isThrowOrFetch) {
				ps = conn.prepareStatement(SELECT_THROW_UNREAD_NUM_SQL);
			} else {
				ps = conn.prepareStatement(SELECT_FETCH_UNREAD_NUM_SQL);
			}
			
			ps.setString(1, uid);

			rs = ps.executeQuery();
			if (null == rs) {
				return 0;
			}

			while (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return 0;
	}
	
	/**
	 * 根据瓶子id查找瓶子
	 * @param bottleId
	 * @return
	 */
	public static Bottle selectBottleById(long bottleId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_BOTTLE_BY_ID_SQL);
			
			ps.setLong(1, bottleId);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			while (rs.next()) {
				Bottle bottle = new Bottle();
				bottle.parseResultSet(rs);
				return bottle;
			}
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	/**
	 * 查询某用户相关的瓶子
	 * type=0为查询扔出的瓶子
	 * type=1为查询收到的瓶子
	 * @param uid
	 * @param type
	 * @return
	 */
	public static List<Bottle> selectMyBottle(String uid, int type, int pageNum, int pageSize) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (type == 0) {
				ps = conn.prepareStatement(SELECT_MY_THROW_BOTTLE_SQL);
			} else {
				ps = conn.prepareStatement(SELECT_MY_FETCH_BOTTLE_SQL);
			}
			
			ps.setString(1, uid);
			ps.setInt(2, pageNum);
			ps.setInt(3, pageSize);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<Bottle> retList = new ArrayList<Bottle>();
			while (rs.next()) {
				Bottle bottle = new Bottle();
				bottle.parseResultSet(rs);
				
				retList.add(bottle);
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	/**
	 * 查询所有未读的瓶子，初始化时调用
	 * @param st
	 * @return
	 */
	public static List<Bottle> selectAllUnreadBottle(int st) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_UNREAD_BOTTLE_SQL);
			ps.setInt(1, st);
			ps.setInt(2, st + Const.INIT_BOTTLE_SCAN_LIMIT);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<Bottle> retList = new ArrayList<Bottle>();
			while (rs.next()) {
				Bottle bottle = new Bottle();
				bottle.parseResultSet(rs);
				
				retList.add(bottle);
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	/**
	 * 修改瓶子
	 * @param bottle
	 * @return
	 */
	@Deprecated
	public static boolean updateBottle(Bottle bottle) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(UPDATE_BOTTLE_SQL);
			ps.setString(1, bottle.getSender());
			ps.setString(2, bottle.getReceiver());
			ps.setInt(3, bottle.getRewardId());
			ps.setBoolean(4, bottle.isSenderRewarded());
			ps.setBoolean(5, bottle.isReceiverRewarded());
			ps.setBoolean(6, bottle.isSenderReaded());
			ps.setBoolean(7, bottle.isReceiverReaded());
			ps.setBoolean(8, bottle.isSenderDeleted());
			ps.setBoolean(9, bottle.isReceiverDeleted());
			ps.setTimestamp(10, new Timestamp(bottle.getSendTime()));
			ps.setString(11, bottle.getReplyText());
			ps.setTimestamp(12, bottle.getReplyTime() == 0 ? null : new Timestamp(bottle.getReplyTime()));
			ps.setLong(13, bottle.getId());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return false;
	}
	
	/**
	 * 添加瓶子
	 * @param sender
	 * @param text
	 * @param vid
	 * @return
	 */
	public static long insertBottle(String sender, String text, String vid) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return -1;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(INSERT_BOTTLE_SQL, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, sender);
			ps.setString(2, text);
			ps.setString(3, vid);

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getLong(1);
			} else {
				return -1;
			}
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return -1;
	}
}
