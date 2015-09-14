package mango.condor.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.service.StorageService;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.DBToolkit;
import mango.condor.toolkit.RedisToolkit;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-8-21 下午3:00:16
 * @Description 
 */
public class DeletePlayersServlet extends HttpServlet {

	private static final long serialVersionUID = -8025248534405110412L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String defaultFilePath = "f:/delete_players.txt";
		String filePath = req.getParameter("path");
		String mid = req.getParameter("mid");
		
		// 如果没有指定，则使用默认路径
		if (null == filePath) {
			filePath = defaultFilePath;
		}
		
		int motherServerId = 0;
		if (null != mid) {
			motherServerId = Integer.parseInt(mid);
		}
		
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		
		while (true) {
			String line = br.readLine();
			if (null == line) {
				break;
			}

			String[] tmp = line.split(":");
			// 默认服务器id从文件读取
			int serverId = Integer.parseInt(tmp[0]);
			// 如果显示指定，则替换掉
			if (null != mid) {
				serverId = motherServerId;
			}
			
			int playerId = Integer.parseInt(tmp[1]);
			
			// 删除聊天数据
			StorageService.deleteAllChatSession(serverId, playerId);
			
			// 删除玩家数据
			RedisToolkit.removePlayerInfo(serverId, playerId);
			DBToolkit.deletePlayerInfo(CommonToolkit.toUID(serverId, playerId));	
			
			// 解除黑名单
			int i = 1;
			while (true) {
				List<PlayerInfo> list = StorageService.getBlackList(serverId, playerId, i, 100);
				if (null == list || list.isEmpty()) {
					break;
				}
				
				for (PlayerInfo pi : list) {
					StorageService.removeBlackList(serverId, playerId, pi.getServerId(), pi.getPlayerId());
				}
				
				++i;
			}
			
			// 解除关注
			i = 1;
			while (true) {
				List<PlayerInfo> list = StorageService.getFollowList(serverId, playerId, i, 100);
				if (null == list || list.isEmpty()) {
					break;
				}
				
				for (PlayerInfo pi : list) {
					StorageService.removeFollowList(serverId, playerId, pi.getServerId(), pi.getPlayerId());
				}
				
				++i;
			}
		}
		
		br.close();
		fr.close();
		
		System.out.println("重新加载缓存ing...");
		StorageService.genFollowRankAndCache();
		RedisToolkit.flushDB(Const.REDIS_DB_INDEX);
		StorageService.initIndexFresh();
	
		System.out.println("OK");
		resp.getWriter().print("OK");
	}

}
