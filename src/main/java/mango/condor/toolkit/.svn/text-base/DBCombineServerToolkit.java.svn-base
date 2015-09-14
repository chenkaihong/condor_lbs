package mango.condor.toolkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mango.condor.domain.bottle.Bottle;
import mango.condor.domain.chat.BlackList;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.FollowList;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-30 上午11:52:24
 * @Description 
 */
public class DBCombineServerToolkit {
	private static final String SELECT_BLACK_LIST_SQL_V1 = "SELECT * FROM black_list WHERE pid LIKE ? LIMIT 0, 5000";
	private static final String SELECT_BLACK_LIST_SQL_V2 = "SELECT * FROM black_list WHERE tid LIKE ? LIMIT 0, 5000";
	
	private static final String SELECT_FOLLOW_LIST_SQL_V1 = "SELECT * FROM follow_list WHERE follower LIKE ? LIMIT 0, 5000";
	private static final String SELECT_FOLLOW_LIST_SQL_V2 = "SELECT * FROM follow_list WHERE followed LIKE ? LIMIT 0, 5000";
	
	private static final String SELECT_PLAYER_INFO_SQL = "SELECT uid FROM player_info WHERE uid LIKE ? LIMIT 0, 5000";
	private static final String SELECT_PLAYER_INFO_SQL_NO_LIMIT = "SELECT uid FROM player_info WHERE uid LIKE ?";
	
	private static final String SELECT_CHAT_SESSION_SQL = "SELECT * FROM game_chat WHERE chater1Id LIKE ? LIMIT 0, 5000 "
			+ "UNION SELECT * FROM game_chat WHERE chater2Id LIKE ? LIMIT 0, 5000";
	
	private static final String UPDATE_BLACK_LIST_SQL = "UPDATE black_list SET pid = ?, tid = ? WHERE id = ?";
	private static final String UPDATE_FOLLOW_LIST_SQL = "UPDATE follow_list SET follower = ?, followed = ? WHERE id = ?";
	
	private static final String SELECT_BOTTLE_SQL = "SELECT * FROM bottle WHERE sender LIKE ? LIMIT 0, 5000 " +
			"UNION SELECT * FROM bottle WHERE receiver LIKE ? LIMIT 0, 5000";
	
	/**
	 * 查询某玩家所有的聊天会话
	 * 
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static List<Bottle> selectBottles(int serverId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_BOTTLE_SQL);
			ps.setString(1, serverId + "-%");
			ps.setString(2, serverId + "-%");

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
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	public static boolean updateBlackList(BlackList bl) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(UPDATE_BLACK_LIST_SQL);
			ps.setString(1, bl.getPid());
			ps.setString(2, bl.getTid());
			ps.setLong(3, bl.getId());

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return false;
	}
	
	public static boolean updateFollowList(FollowList fl) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(UPDATE_FOLLOW_LIST_SQL);
			ps.setString(1, fl.getFollower());
			ps.setString(2, fl.getFollowed());
			ps.setLong(3, fl.getId());

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return false;
	}
	
	/**
	 * 查询某玩家所有的聊天会话
	 * 
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static List<ChatMyVo> selectMyChatSession(int serverId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_CHAT_SESSION_SQL);
			ps.setString(1, serverId + "-%");
			ps.setString(2, serverId + "-%");

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<ChatMyVo> retList = new ArrayList<ChatMyVo>();
			while (rs.next()) {
				ChatMyVo session = new ChatMyVo();
				session.setChatId(rs.getString("chatId"));
				session.setChater1(rs.getInt("chater1"));
				session.setChater2(rs.getInt("chater2"));
				session.setChater1ServerId(rs.getInt("chater1ServerId"));
				session.setChater2ServerId(rs.getInt("chater2ServerId"));
				session.setLastMessage(rs.getString("lastMessage"));
				session.setLastTime(rs.getTimestamp("lastTime").getTime());
				session.setUnReadNumChater1(rs.getInt("unReadNumChater1"));
				session.setUnReadNumChater2(rs.getInt("unReadNumChater2"));
				session.setReadedTimestampChater1(rs.getTimestamp(
						"readedChater1").getTime());
				session.setReadedTimestampChater2(rs.getTimestamp(
						"readedChater2").getTime());
				session.setChater1State(rs.getByte("chater1State"));
				session.setChater2State(rs.getByte("chater2State"));
				session.setDelTimestampChater1(rs.getTimestamp("delChater1")
						.getTime());
				session.setDelTimestampChater2(rs.getTimestamp("delChater2")
						.getTime());
				session.setLastReadedChater1(rs.getTimestamp(
						"lastReadedChater1").getTime());
				session.setLastReadedChater2(rs.getTimestamp(
						"lastReadedChater2").getTime());

				retList.add(session);
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	/**
	 * 查找玩家信息
	 * @param serverId
	 * @return
	 */
	public static List<String> selectPlayerInfo(int serverId, boolean isLimit) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List<String> retList = new ArrayList<String>();
			if (isLimit) {
				ps = conn.prepareStatement(SELECT_PLAYER_INFO_SQL);
			} else {
				ps = conn.prepareStatement(SELECT_PLAYER_INFO_SQL_NO_LIMIT);
			}
			
			ps.setString(1, serverId + "-%");
			
			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}
			
			while (rs.next()) {
				retList.add(rs.getString("uid"));
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	public static List<BlackList> selectBlackListV1(int serverId) {
		return selectBlackList(SELECT_BLACK_LIST_SQL_V1, serverId);
	}
	
	public static List<BlackList> selectBlackListV2(int serverId) {
		return selectBlackList(SELECT_BLACK_LIST_SQL_V2, serverId);
	}

	private static List<BlackList> selectBlackList(String sql, int serverId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, serverId + "-%");

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<BlackList> retList = new ArrayList<BlackList>();
			while (rs.next()) {
				BlackList bl = new BlackList();
				bl.setId(rs.getInt("id"));
				bl.setPid(rs.getString("pid"));
				bl.setTid(rs.getString("tid"));
				retList.add(bl);
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	
	public static List<FollowList> selectFollowListV1(int serverId) {
		return selectFollowList(SELECT_FOLLOW_LIST_SQL_V1, serverId);
	}
	
	public static List<FollowList> selectFollowListV2(int serverId) {
		return selectFollowList(SELECT_FOLLOW_LIST_SQL_V2, serverId);
	}
	
	private static List<FollowList> selectFollowList(String sql, int serverId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, serverId + "-%");
			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<FollowList> retList = new ArrayList<FollowList>();
			while (rs.next()) {
				FollowList fl = new FollowList();
				fl.setId(rs.getInt("id"));
				fl.setFollower(rs.getString("follower"));
				fl.setFollowed(rs.getString("followed"));
				fl.setFollowTime(rs.getTimestamp("followTime").getTime());
				fl.setFollowedClearMsg(rs.getBoolean("followedClearMsg"));
				retList.add(fl);
			}

			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e, null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
}
