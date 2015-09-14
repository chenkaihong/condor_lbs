package mango.condor.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.dao.DaoFactory;
import mango.condor.domain.bottle.Bottle;
import mango.condor.domain.chat.BlackList;
import mango.condor.domain.chat.ChatContentMyVo;
import mango.condor.domain.chat.ChatMyVo;
import mango.condor.domain.lbs.FollowList;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.service.StorageService;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.DBBottleToolkit;
import mango.condor.toolkit.DBCombineServerToolkit;
import mango.condor.toolkit.DBToolkit;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-30 下午2:13:33
 * @Description 
 */
public class CombineServerServlet extends HttpServlet {

	private static final long serialVersionUID = -7753128804120816109L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		int oldServerId = Integer.parseInt(req.getParameter("oldServerId"));
		int newServerId = Integer.parseInt(req.getParameter("newServerId"));
		String newServerName = req.getParameter("newServerName");
		
		System.out.println("CombineServerServlet start..");
		
		// 清空聊天缓存，在线玩家动态加载缓存，不会导致问题		
		RedisToolkit.flushDB(Const.REDIS_DB_LOCK);
		
		while (false == RedisToolkit.flushDB(Const.REDIS_DB_CHAT)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// 查找oldServerId相关的会话
		System.out.println("CombineServerServlet.chat start..");
		while (true) {
			List<ChatMyVo> sessionList = DBCombineServerToolkit.selectMyChatSession(oldServerId);
			if (null == sessionList || sessionList.isEmpty()) {
				break;
			}
			
			System.out.println("\n会话数：" + sessionList.size());
			
			for (ChatMyVo session : sessionList) {				
				// 修改服务器id
				if (session.getChater1ServerId() == oldServerId) {
					session.setChater1ServerId(newServerId);
				}
				
				if (session.getChater2ServerId() == oldServerId) {
					session.setChater2ServerId(newServerId);
				}
				
				// 保存原来的会话id
				String oldSessionId = session.getChatId();
				
				// 生成新的会话id
				String newSessionId = CommonToolkit.getSessionId(session.getChater1ServerId(), session.getChater1(), 
						session.getChater2ServerId(), session.getChater2());
				
				session.setChatId(newSessionId);
				
				// 插入新的会话
				DBToolkit.insertMyChatSession(session);
				
				// 搜出相关的聊天记录
				List<ChatContentMyVo> contentList = DBToolkit.selectMyChatContent(oldSessionId);
				if (null != contentList) {
					for (ChatContentMyVo content : contentList) {
						// 修改服务器id
						if (content.getSenderServerId() == oldServerId) {
							content.setSenderServerId(newServerId);
						}
						
						if (content.getReceiverServerId() == oldServerId) {
							content.setReceiverServerId(newServerId);
						}
						
						content.setChatId(newSessionId);
						// 插入
						DBToolkit.insertMyChatContent(content);
					}
				}
				
				// 删除老的会话，顺带把老的聊天记录删除
				DBToolkit.deleteMyChatSession(oldSessionId);
			}
		}
		System.out.println("CombineServerServlet.chat end..");
		
		System.out.println("\n开始合并玩家数据");
		
		// 修改母服玩家数据
		System.out.println("CombineServerServlet.player[main] start..");
		List<String> uidList = DBCombineServerToolkit.selectPlayerInfo(newServerId, false);
		if (!(null == uidList || uidList.isEmpty())) {
			System.out.println("\n母服玩家数：" + uidList.size());
			
			for (String uid : uidList) {
				PlayerInfo pi = StorageService.getPlayerInfo(uid, false);
				if (null != pi) {
					RedisToolkit.removePlayerInfo(pi.getServerId(), pi.getPlayerId());
					DBToolkit.deletePlayerInfo(uid);
					
					pi.setServerId(newServerId);
					pi.setServerName(newServerName);
					DBToolkit.insertOrUpdatePlayerInfo(pi); 
				}
			}
		}
		System.out.println("CombineServerServlet.player[main] end..");
		
		// 先删玩家信息表，再插
		// 修改子服玩家数据
		System.out.println("CombineServerServlet.player[branch] start..");
		while (true) {
			uidList = DBCombineServerToolkit.selectPlayerInfo(oldServerId, true);
			if (null == uidList || uidList.isEmpty()) {
				break;
			}
			
			System.out.println("\n子服玩家数：" + uidList.size());
			
			for (String uid : uidList) {
				PlayerInfo pi = StorageService.getPlayerInfo(uid, false);
				if (null != pi) {
					RedisToolkit.removePlayerInfo(pi.getServerId(), pi.getPlayerId());
					DBToolkit.deletePlayerInfo(uid);
					
					pi.setServerId(newServerId);
					pi.setServerName(newServerName);
					DBToolkit.insertOrUpdatePlayerInfo(pi);
				}
			}
		}
		System.out.println("CombineServerServlet.player[branch] end..");
		
		System.out.println("\n开始合并黑名单数据");
		// 清空缓存
		RedisToolkit.flushDB(Const.REDIS_DB_BLACK_LIST);
		
		// 更新黑名单
		System.out.println("CombineServerServlet.blacklist.1 start..");
		while (true) {
			List<BlackList> blList = DBCombineServerToolkit.selectBlackListV1(oldServerId);
			if (null == blList || blList.isEmpty()) {
				break;
			}
			
			System.out.println("\n条目数:" + blList.size());
			
			for (BlackList bl : blList) {
				String tmpUID = bl.getPid();
				bl.setPid(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				
				DBCombineServerToolkit.updateBlackList(bl);
			}
		}
		System.out.println("CombineServerServlet.blacklist.1 end..");
		
		System.out.println("CombineServerServlet.blacklist.2 start..");
		while (true) {
			List<BlackList> blList = DBCombineServerToolkit.selectBlackListV2(oldServerId);
			if (null == blList || blList.isEmpty()) {
				break;
			}
			
			System.out.println("\n条目数:" + blList.size());
			
			for (BlackList bl : blList) {
				String tmpUID = bl.getTid();
				bl.setTid(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				
				DBCombineServerToolkit.updateBlackList(bl);
			}
		}
		System.out.println("CombineServerServlet.blacklist.2 end..");

		System.out.println("\n开始合并关注数据");
		// 清空缓存
		while (false == RedisToolkit.flushDB(Const.REDIS_DB_FOLLOW_LIST)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// 更新关注表
		System.out.println("CombineServerServlet.follow.1 start..");
		while (true) {
			List<FollowList> flList = DBCombineServerToolkit.selectFollowListV1(oldServerId);
			if (null == flList || flList.isEmpty()) {
				break;
			}
			
			System.out.println("\n条目数:" + flList.size());
			
			for (FollowList fl : flList) {
				String tmpUID = fl.getFollower();
				fl.setFollower(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				
				DBCombineServerToolkit.updateFollowList(fl);
			}
		}
		System.out.println("CombineServerServlet.follow.1 end..");

		System.out.println("CombineServerServlet.follow.2 start..");
		while (true) {
			List<FollowList> flList = DBCombineServerToolkit.selectFollowListV2(oldServerId);
			if (null == flList || flList.isEmpty()) {
				break;
			}
			
			System.out.println("\n条目数:" + flList.size());
			
			for (FollowList fl : flList) {
				String tmpUID = fl.getFollowed();
				fl.setFollowed(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				
				DBCombineServerToolkit.updateFollowList(fl);
			}
		}
		System.out.println("CombineServerServlet.follow.2 end..");
		
		System.out.println("\n开始合并漂流瓶数据");		
		// 清空缓存
		while (false == RedisToolkit.flushDB(Const.REDIS_DB_BOTTLE)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// 更新瓶子表
		System.out.println("CombineServerServlet.bottle start..");
		while (true) {
			List<Bottle> bottleList = DBCombineServerToolkit.selectBottles(oldServerId);
			if (null == bottleList || bottleList.isEmpty()) {
				break;
			}
			
			System.out.println("\n条目数:" + bottleList.size());
			
			for (Bottle bottle : bottleList) {
				String tmpUID = bottle.getSender();
				if (null != tmpUID) {
					bottle.setSender(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				}
				
				tmpUID = bottle.getReceiver();
				if (null != tmpUID) {
					bottle.setReceiver(tmpUID.replace(oldServerId + "-", newServerId + "-"));
				}
				
				DaoFactory.getBottleDao().updateBottle(bottle);
			}
		}
		System.out.println("CombineServerServlet.bottle end..");
		
		System.out.println("CombineServerServlet end.. OK");
		
		resp.getWriter().print("CombineServerServlet be OK");
	}
	
}
