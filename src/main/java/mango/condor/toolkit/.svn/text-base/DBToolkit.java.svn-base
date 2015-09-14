package mango.condor.toolkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import mango.condor.domain.chat.BlackList;
import mango.condor.domain.chat.ChatContentMyVo;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.BasePlayerInfo;
import mango.condor.domain.lbs.FollowList;
import mango.condor.domain.lbs.FollowRank;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.ServerURL;

import com.google.gson.Gson;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-12 上午9:47:47
 * @Description
 */

public class DBToolkit {

	private static final String INSERT_CHAT_SESSION_SQL = "INSERT INTO game_chat (chatId, chater1, chater2, chater1ServerId, chater2ServerId"
			+ ", lastMessage, lastTime, unReadNumChater1, unReadNumChater2, readedChater1, readedChater2 "
			+ ", chater1State, chater2State, delChater1, delChater2, chater1Id, chater2Id, lastReadedChater1, lastReadedChater2) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String UPDATE_CHAT_SESSION_SQL = "UPDATE game_chat SET lastMessage = ?, lastTime = ?, unReadNumChater1 = ?, "
			+ "unReadNumChater2 = ?, readedChater1 = ?, readedChater2 = ?, chater1State = ?, chater2State = ?, "
			+ "delChater1 = ?, delChater2 = ?, lastReadedChater1 = ?, lastReadedChater2 = ? WHERE chatId = ?";

	private static final String SELECT_CHAT_SESSION_SQL = "SELECT * FROM game_chat WHERE chater1Id = ? "
			+ "UNION ALL SELECT * FROM game_chat WHERE chater2Id = ?";

	private static final String DELETE_CHAT_SESSION_SQL = "DELETE FROM game_chat WHERE chatId = ?";

	private static final String INSERT_CHAT_CONTENT_SQL = "INSERT INTO game_chat_content(sender, senderServerId, receiver, receiverServerId, "
			+ "chatId, senderStatus, receiverStatus, content, sendTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String SELECT_CHAT_CONTENT_SQL = "SELECT * FROM game_chat_content WHERE chatId = ?";

	private static final String CALL_SELECT_HISTORY_CHAT_CONTENT_SQL = "CALL select_history_chat_content(?, ?, ?)";

	private static final String INSERT_OR_UPDATE_PLAYER_INFO_SQL = "INSERT INTO player_info (uid, playerId, serverId, name, serverName, "
			+ "jsonData, updateTS) VALUES (?, ?, ?, ?, ?, ?, ?) "
			+ "ON DUPLICATE KEY UPDATE uid = ?, playerId = ?, serverId = ?, serverName = ?, jsonData = ?, updateTS = ?, name = ?";

	private static final String DELETE_PLAYER_INFO_SQL = "DELETE FROM player_info WHERE uid = ?";

	private static final String SELECT_PLAYER_INFO_SQL = "SELECT playerId, serverId, serverName, jsonData, updateTS "
			+ "FROM player_info WHERE uid = ?";

	private static final String SELECT_PLAYER_INFO_BY_NAME_SQL = "SELECT playerId, serverId, serverName, jsonData, updateTS "
			+ "FROM player_info WHERE name = ?";

	private static final String SELECT_PLAYER_INFO_BY_LIMIT = "SELECT playerId, serverId, serverName, jsonData, updateTS "
			+ "FROM player_info LIMIT 0, " + Const.FOLLOW_RANK_LIMIT;

	private static final String SELECT_ALL_BLACK_LIST_SQL = "SELECT * FROM black_list WHERE id >= ? AND id < ?";

	private static final String INSERT_BLACK_LIST_SQL = "INSERT INTO black_list(pid, tid) VALUES(?, ?)";

	private static final String DELETE_BLACK_LIST_SQL = "DELETE FROM black_list WHERE pid = ? AND tid = ?";

	private static final String SELECT_ALL_FOLLOW_LIST_SQL = "SELECT * FROM follow_list WHERE id >= ? AND id < ?";

	private static final String INSERT_FOLLOW_LIST_SQL = "INSERT INTO follow_list(follower, followed) VALUES(?, ?)";

	private static final String DELETE_FOLLOW_LIST_SQL = "DELETE FROM follow_list WHERE follower = ? AND followed = ?";

	private static final String SELECT_FOLLOW_RANK_SQL = "SELECT followed, COUNT(*) AS fn FROM follow_list GROUP BY followed "
			+ "ORDER BY fn DESC LIMIT 0, ?";

	private static final String SELECT_INDEX_PLAYER_INFO_SQL = "SELECT uid FROM player_info ORDER BY id DESC LIMIT 0, ?";

	private static final String SELECT_NEED_SYNC_PLAYER_INFO_SQL = "SELECT serverId, playerId, updateTS FROM player_info "
			+ "WHERE id >= ? AND id < ?";

	private static final String SELECT_ALL_SERVER_URL_SQL = "SELECT * FROM server_url";

	private static final String SELECT_MAX_ID_SQL = "SELECT MAX(id) FROM %s";
	
	private static final String CLEAR_FOLLOWED_MSG_SQL = "UPDATE follow_list SET followedClearMsg=1, readMsg=1 WHERE followed=? AND followedClearMsg=0";
	
	private static final String READ_FOLLOWED_MSG_SQL = "UPDATE follow_list SET readMsg=1 WHERE followed=? AND follower=?";

	/**
	 * 获取指定表的最大自增id
	 * 
	 * @param tableName
	 * @return
	 */
	public static int selectMaxID(String tableName) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return -1;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(String.format(SELECT_MAX_ID_SQL,
					tableName));

			rs = ps.executeQuery();
			if (null == rs) {
				return -1;
			}

			if (rs.next()) {
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
		return -1;
	}

	/**
	 * 搜索所有的服务器id->游戏服url数据
	 * 
	 * @return
	 */
	public static List<ServerURL> selectAllServerURL() {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_ALL_SERVER_URL_SQL);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<ServerURL> retList = new ArrayList<ServerURL>();
			while (rs.next()) {
				ServerURL su = new ServerURL();
				su.setServerId(rs.getInt("serverId"));
				su.setUrl(rs.getString("url"));
				su.setName(rs.getString("name"));
				retList.add(su);
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
	 * 初始化调用，查询新鲜事数据
	 * 
	 * @param size
	 * @return
	 */
	public static List<String> selectInitIndexPlayerInfo(int size) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_INDEX_PLAYER_INFO_SQL);
			ps.setInt(1, size);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<String> retList = new ArrayList<String>();
			while (rs.next()) {
				retList.add(rs.getString("uid"));
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
	 * 每小时查询数据库生成拥有粉丝最多前1000人
	 * 
	 * @return
	 */
	public static List<FollowRank> selectFollowRank() {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_FOLLOW_RANK_SQL);
			ps.setInt(1, Const.FOLLOW_RANK_LIMIT);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<FollowRank> retList = new ArrayList<FollowRank>();
			int i = 1;
			while (rs.next()) {
				FollowRank fr = new FollowRank();
				fr.setRank(i);
				fr.setUid(rs.getString("followed"));
				fr.setFanNum(rs.getInt("fn"));
				retList.add(fr);
				++i;
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
	 * 获取某玩家的所有关注列表
	 * 
	 * @return
	 */
	public static List<FollowList> selectAllFollowList(int st) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_ALL_FOLLOW_LIST_SQL);
			ps.setInt(1, st);
			ps.setInt(2, st + Const.INIT_FOLLOW_LIST_SCAN_LIMIT);
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
				fl.setReadMsg( rs.getBoolean("readMsg") );
				retList.add(fl);
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
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}

	/**
	 * 插入关注者
	 * 		-- playerId 关注 hisId
	 * 
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static int insertFollowList(int playerServerId, int playerId,
			int hisServerId, int hisId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return -1;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(INSERT_FOLLOW_LIST_SQL,
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, CommonToolkit.toUID(playerServerId, playerId));
			ps.setString(2, CommonToolkit.toUID(hisServerId, hisId));

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
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

	/**
	 * 删除关注
	 * 
	 * @param playerServerId
	 * @param playerId
	 * @param hisServerId
	 * @param hisId
	 * @return
	 */
	public static boolean deleteFollowList(int playerServerId, int playerId,
			int hisServerId, int hisId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_FOLLOW_LIST_SQL);
			ps.setString(1, CommonToolkit.toUID(playerServerId, playerId));
			ps.setString(2, CommonToolkit.toUID(hisServerId, hisId));

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
	 * 删除黑名单
	 * 
	 * @param pServerId
	 * @param pId
	 * @param tServerId
	 * @param tId
	 * @return
	 */
	public static boolean deleteBlackList(int pServerId, int pId,
			int tServerId, int tId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_BLACK_LIST_SQL);
			ps.setString(1, CommonToolkit.toUID(pServerId, pId));
			ps.setString(2, CommonToolkit.toUID(tServerId, tId));

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
	 * 插入黑名单
	 * 
	 * @param pServerId
	 * @param pId
	 * @param tServerId
	 * @param tId
	 * @return
	 */
	public static int insertBlackList(int pServerId, int pId, int tServerId,
			int tId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return -1;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(INSERT_BLACK_LIST_SQL,
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, CommonToolkit.toUID(pServerId, pId));
			ps.setString(2, CommonToolkit.toUID(tServerId, tId));

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs != null && rs.next()) {
				return rs.getInt(1);
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
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return -1;
	}

	/**
	 * 查询所有的黑名单，程序启动时调用
	 * 
	 * @return
	 */
	public static List<BlackList> selectAllBlackList(int st) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_ALL_BLACK_LIST_SQL);
			ps.setInt(1, st);
			ps.setInt(2, st + Const.INIT_BLACK_LIST_SCAN_LIMIT);

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
	 * 取得某会话的历史聊天记录
	 * 
	 * @param sessionId
	 * @param readedTimestamp
	 * @param delTimestamp
	 * @return
	 */
	public static List<ChatContentMyVo> getHistoryChatContent(String sessionId,
			long readedTimestamp, long delTimestamp) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareCall(CALL_SELECT_HISTORY_CHAT_CONTENT_SQL);
			ps.setString(1, sessionId);
			ps.setTimestamp(2, new Timestamp(readedTimestamp));
			ps.setTimestamp(3, new Timestamp(delTimestamp));

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<ChatContentMyVo> retList = new ArrayList<ChatContentMyVo>();
			while (rs.next()) {
				ChatContentMyVo chatContent = new ChatContentMyVo();
				chatContent.setChatId(sessionId);
				chatContent.setSender(rs.getInt("sender"));
				chatContent.setSenderServerId(rs.getInt("senderServerId"));
				chatContent.setReceiver(rs.getInt("receiver"));
				chatContent.setReceiverServerId(rs.getInt("receiverServerId"));
				chatContent.setContent(rs.getString("content"));
				chatContent.setSendTime(rs.getTimestamp("sendTime").getTime());
				chatContent.setSenderStatus(rs.getByte("senderStatus"));
				chatContent.setReceiverStatus(rs.getByte("receiverStatus"));

				retList.add(chatContent);
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
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}
	
	public static boolean updateMyChatSessionWhenLogin(String uid) {
		String[] SQLs = new String[]{
				"UPDATE game_chat SET lastReadedChater1=readedChater1 WHERE chater1Id=?",
				"UPDATE game_chat SET lastReadedChater2=readedChater2 WHERE chater2Id=?"
		};
		
		for (String sql : SQLs) {
			Connection conn = DBPoolToolkit.getConnection();
			if (null == conn) {
				return false;
			}
			
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, uid);
				pstmt.executeUpdate();
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			finally {
				closePreparedStatement(pstmt);
				closeConnection(conn);
			}
		}
		
		return true;
	}

	/**
	 * 查询某玩家所有的聊天会话
	 * 
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static List<ChatMyVo> selectMyChatSession(int serverId, int playerId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_CHAT_SESSION_SQL);

			String uid = CommonToolkit.toUID(serverId, playerId);
			ps.setString(1, uid);
			ps.setString(2, uid);

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
	 * 更新聊天会话
	 * 
	 * @param chatMyVo
	 * @return
	 */
	public static boolean updateMyChatSession(ChatMyVo chatMyVo) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(UPDATE_CHAT_SESSION_SQL);
			ps.setString(1, chatMyVo.getLastMessage());
			ps.setTimestamp(2, new Timestamp(chatMyVo.getLastTime()));
			ps.setInt(3, chatMyVo.getUnReadNumChater1());
			ps.setInt(4, chatMyVo.getUnReadNumChater2());
			ps.setTimestamp(5,
					new Timestamp(chatMyVo.getReadedTimestampChater1()));
			ps.setTimestamp(6,
					new Timestamp(chatMyVo.getReadedTimestampChater2()));
			ps.setByte(7, chatMyVo.getChater1State());
			ps.setByte(8, chatMyVo.getChater2State());
			ps.setTimestamp(9, new Timestamp(chatMyVo.getDelTimestampChater1()));
			ps.setTimestamp(10,
					new Timestamp(chatMyVo.getDelTimestampChater2()));
			ps.setTimestamp(11, new Timestamp(chatMyVo.getLastReadedChater1()));
			ps.setTimestamp(12, new Timestamp(chatMyVo.getLastReadedChater2()));
			ps.setString(13, chatMyVo.getChatId());

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
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
	 * 插入聊天会话
	 * 
	 * @param chatMyVo
	 * @return
	 */
	public static boolean insertMyChatSession(ChatMyVo chatMyVo) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(INSERT_CHAT_SESSION_SQL);
			ps.setString(1, chatMyVo.getChatId());
			ps.setInt(2, chatMyVo.getChater1());
			ps.setInt(3, chatMyVo.getChater2());
			ps.setInt(4, chatMyVo.getChater1ServerId());
			ps.setInt(5, chatMyVo.getChater2ServerId());
			ps.setString(6, chatMyVo.getLastMessage());
			ps.setTimestamp(7, new Timestamp(chatMyVo.getLastTime()));
			ps.setInt(8, chatMyVo.getUnReadNumChater1());
			ps.setInt(9, chatMyVo.getUnReadNumChater2());
			ps.setTimestamp(10,
					new Timestamp(chatMyVo.getReadedTimestampChater1()));
			ps.setTimestamp(11,
					new Timestamp(chatMyVo.getReadedTimestampChater2()));
			ps.setByte(12, chatMyVo.getChater1State());
			ps.setByte(13, chatMyVo.getChater2State());
			ps.setTimestamp(14,
					new Timestamp(chatMyVo.getDelTimestampChater1()));
			ps.setTimestamp(15,
					new Timestamp(chatMyVo.getDelTimestampChater2()));

			ps.setString(
					16,
					CommonToolkit.toUID(chatMyVo.getChater1ServerId(),
							chatMyVo.getChater1()));
			ps.setString(
					17,
					CommonToolkit.toUID(chatMyVo.getChater2ServerId(),
							chatMyVo.getChater2()));

			ps.setTimestamp(18, new Timestamp(chatMyVo.getLastReadedChater1()));
			ps.setTimestamp(19, new Timestamp(chatMyVo.getLastReadedChater2()));

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
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
	 * 删除会话数据，聊天消息关联了，会自动删除
	 * 
	 * @param sessionId
	 * @return
	 */
	public static boolean deleteMyChatSession(String sessionId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_CHAT_SESSION_SQL);
			ps.setString(1, sessionId);

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
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,		null, null);
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

		return false;
	}

	/**
	 * 查找某个会话的所有聊天记录
	 * 
	 * @param chatId
	 * @return
	 */
	public static List<ChatContentMyVo> selectMyChatContent(String chatId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_CHAT_CONTENT_SQL);
			ps.setString(1, chatId);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<ChatContentMyVo> retList = new ArrayList<ChatContentMyVo>();
			while (rs.next()) {
				ChatContentMyVo chatContent = new ChatContentMyVo();
				chatContent.setChatId(chatId);
				chatContent.setSender(rs.getInt("sender"));
				chatContent.setSenderServerId(rs.getInt("senderServerId"));
				chatContent.setReceiver(rs.getInt("receiver"));
				chatContent.setReceiverServerId(rs.getInt("receiverServerId"));
				chatContent.setContent(rs.getString("content"));
				chatContent.setSendTime(rs.getTimestamp("sendTime").getTime());
				chatContent.setSenderStatus(rs.getByte("senderStatus"));
				chatContent.setReceiverStatus(rs.getByte("receiverStatus"));

				retList.add(chatContent);
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
	 * 插入聊天记录
	 * 
	 * @param chatContentMyVo
	 * @return
	 */
	public static boolean insertMyChatContent(ChatContentMyVo chatContentMyVo) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(INSERT_CHAT_CONTENT_SQL);
			ps.setInt(1, chatContentMyVo.getSender());
			ps.setInt(2, chatContentMyVo.getSenderServerId());
			ps.setInt(3, chatContentMyVo.getReceiver());
			ps.setInt(4, chatContentMyVo.getReceiverServerId());
			ps.setString(5, chatContentMyVo.getChatId());
			ps.setShort(6, chatContentMyVo.getSenderStatus());
			ps.setShort(7, chatContentMyVo.getReceiverStatus());
			ps.setString(8, chatContentMyVo.getContent());
			ps.setTimestamp(9, new Timestamp(chatContentMyVo.getSendTime()));

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		} finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				} catch (SQLException e) {
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,			null, null);
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

		return false;
	}

	/**
	 * 找出需要进行同步的玩家
	 * 
	 * @param st
	 * @param size
	 * @return
	 */
	public static List<String> selectNeedSyncPlayerInfo(int st, int size) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> retList = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(SELECT_NEED_SYNC_PLAYER_INFO_SQL);
			ps.setInt(1, st);
			ps.setInt(2, st + size);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			long now = System.currentTimeMillis();

			while (rs.next()) {
				// 现在与上次玩家上次刷新个人资料的时间差
				long diff = now - rs.getTimestamp("updateTS").getTime();

				// 符合策略的则向游戏服进行同步
				if (diff > Const.SYNC_MIN_TIME && diff < Const.SYNC_MAX_TIME) {
					retList.add(rs.getInt("serverId") + ","
							+ rs.getInt("playerId"));
				}
			}
			return retList;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
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
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,	null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}
		return null;
	}

	/**
	 * 删除某玩家
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean deletePlayerInfo(String uid) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(DELETE_PLAYER_INFO_SQL);
			ps.setString(1, uid);

			int result = ps.executeUpdate();
			return result > 0 ? true : false;
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
	 * 查找玩家信息
	 * 
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static PlayerInfo selectPlayerInfo(String uid) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_PLAYER_INFO_SQL);
			ps.setString(1, uid);

			rs = ps.executeQuery();
			if (null == rs || false == rs.next()) {
				return null;
			}

			PlayerInfo pi = new PlayerInfo();
			pi.setPlayerId(rs.getInt("playerId"));
			pi.setServerId(rs.getInt("serverId"));
			pi.setServerName(rs.getString("serverName"));
			pi.setUpdateTS(rs.getTimestamp("updateTS").getTime());

			Gson gson = CommonToolkit.getGson();
			BasePlayerInfo bpi = gson.fromJson(rs.getString("jsonData"),
					BasePlayerInfo.class);
			bpi.setPlayerInfo(pi);

			return pi;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
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
	 * 插入玩家信息，如果已经存在的话则更新
	 * 
	 * @param pi
	 * @return
	 */
	public static boolean insertOrUpdatePlayerInfo(PlayerInfo pi) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(INSERT_OR_UPDATE_PLAYER_INFO_SQL);
			ps.setString(1, pi.getUid());
			ps.setInt(2, pi.getPlayerId());
			ps.setInt(3, pi.getServerId());
			ps.setString(4, pi.getName());
			ps.setString(5, pi.getServerName());

			BasePlayerInfo bpi = new BasePlayerInfo(pi);
			Gson gson = CommonToolkit.getGson();
			ps.setString(6, gson.toJson(bpi));
			ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

			ps.setString(8, pi.getUid());
			ps.setInt(9, pi.getPlayerId());
			ps.setInt(10, pi.getServerId());
			ps.setString(11, pi.getServerName());
			ps.setString(12, gson.toJson(bpi));
			ps.setTimestamp(13, new Timestamp(System.currentTimeMillis()));
			ps.setString(14, pi.getName());

			int ret = ps.executeUpdate();
			return ret > 0 ? true : false;
		} catch (Exception e) {
			System.out.println( "insertOrUpdatePlayerInfo: playerId=" + pi.getPlayerId() 
					+ ",name=" + pi.getName() + ",name.length=" + pi.getName().length()
					+ ",serverName=" + pi.getServerName() + ",serverName.length=" + pi.getServerName().length());
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
	 * 搜索某玩家
	 * 
	 * @param name
	 * @return
	 */
	public static List<PlayerInfo> searchPlayer(String name, int serverId,
			int playerId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_PLAYER_INFO_BY_NAME_SQL);
			ps.setString(1, name);

			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<PlayerInfo> retList = new ArrayList<PlayerInfo>();

			while (rs.next()) {
				PlayerInfo pi = new PlayerInfo();
				pi.setPlayerId(rs.getInt("playerId"));
				pi.setServerId(rs.getInt("serverId"));

				// 把自己过滤掉
				if (pi.getServerId() == serverId
						&& pi.getPlayerId() == playerId) {
					continue;
				}

				pi.setServerName(rs.getString("serverName"));
				pi.setUpdateTS(rs.getTimestamp("updateTS").getTime());

				Gson gson = CommonToolkit.getGson();
				BasePlayerInfo bpi = gson.fromJson(rs.getString("jsonData"),
						BasePlayerInfo.class);
				bpi.setPlayerInfo(pi);

				retList.add(pi);
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
	 * 粉丝排行相关
	 * 
	 * @param name
	 * @return
	 */
	public static List<PlayerInfo> selectPlayerInfoByLimit() {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT_PLAYER_INFO_BY_LIMIT);
			rs = ps.executeQuery();
			if (null == rs) {
				return null;
			}

			List<PlayerInfo> retList = new ArrayList<PlayerInfo>();
			while (rs.next()) {
				PlayerInfo pi = new PlayerInfo();
				pi.setPlayerId(rs.getInt("playerId"));
				pi.setServerId(rs.getInt("serverId"));
				pi.setServerName(rs.getString("serverName"));
				pi.setUpdateTS(rs.getTimestamp("updateTS").getTime());

				Gson gson = CommonToolkit.getGson();
				BasePlayerInfo bpi = gson.fromJson(rs.getString("jsonData"),
						BasePlayerInfo.class);
				bpi.setPlayerInfo(pi);

				retList.add(pi);
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
					//LogstashExceptionLogger.handleException(	Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,		null, null);
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
	 * 清空 playerServerId被关注的消息
	 * @param playerServerId
	 * @param playerId
	 * @return
	 */
	public static boolean clearFollowMsg(int playerServerId, int playerId) {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return false;
		}

		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(CLEAR_FOLLOWED_MSG_SQL);
			ps.setString(1, CommonToolkit.toUID(playerServerId, playerId));			
			ps.executeUpdate();
			return true;
		}
		catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		}
		finally {
			if (null != ps) {
				try {
					ps.close();
					ps = null;
				}
				catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
			}

			if (null != conn) {
				try {
					conn.close();
				}
				catch (SQLException e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_DB, e,null, null);
					e.printStackTrace();
				}
				conn = null;
			}
		}

		return false;
	}
	
	/**
	 * followed 阅读了被关注消息
	 * @param followed
	 * @param followerList
	 * @return
	 */
	public static boolean readFollowMsg(String followed, List<String> followerList) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBPoolToolkit.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(READ_FOLLOWED_MSG_SQL);
			
			for (String follower : followerList) {
				pstmt.setString(1, followed);
				pstmt.setString(2, follower);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			
			return true;
		}
		catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,Const.LBS_EXP_DB, e, null, null);
			e.printStackTrace();
		}
		finally {
			closePreparedStatement(pstmt);
			closeConnection(conn);
		}

		return false;
	}
	
	
	/**
	 * 查找玩家信息
	 * 
	 * @param serverId
	 * @param playerId
	 * @return
	 */
	public static List<PlayerInfo> loadPlayerInfo() {
		Connection conn = DBPoolToolkit.getConnection();
		if (null == conn) {
			return null;
		}
		List<PlayerInfo>  list = new ArrayList<PlayerInfo>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select * from player_info");

			rs = ps.executeQuery();
			if (null == rs || false == rs.next()) {
				return null;
			}
			while(rs.next()){
				
				PlayerInfo pi = new PlayerInfo();
				pi.setPlayerId(rs.getInt("playerId"));
				pi.setServerId(rs.getInt("serverId"));
				pi.setServerName(rs.getString("serverName"));
				pi.setUpdateTS(rs.getTimestamp("updateTS").getTime());
				
				Gson gson = CommonToolkit.getGson();
				BasePlayerInfo bpi = gson.fromJson(rs.getString("jsonData"),
						BasePlayerInfo.class);
				bpi.setPlayerInfo(pi);
				list.add(pi);
			}

			return list;
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0,	Const.LBS_EXP_DB, e, null, null);
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
	
	public static void closeResultSet (ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closePreparedStatement(PreparedStatement pstmt) {
		if (pstmt != null) {
			try {
				pstmt.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.getAutoCommit()) {
					conn.setAutoCommit(true);
				}

				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
