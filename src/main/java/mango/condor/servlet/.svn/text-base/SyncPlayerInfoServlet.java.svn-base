package mango.condor.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.lbs.ServerURL;
import mango.condor.toolkit.CommonToolkit;
import mango.condor.toolkit.Const;
import mango.condor.toolkit.DBToolkit;
import mango.condor.toolkit.RedisToolkit;

import com.google.gson.Gson;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 邢陈程
 * @Date 2013-7-26 下午1:48:17
 * @Description 
 * 每天凌晨5点调用，根据一定的策略同步玩家数据
 */
public class SyncPlayerInfoServlet extends HttpServlet {

	private static final long serialVersionUID = -597513796138103864L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Map<Integer, String> suMap = new HashMap<Integer, String>();
		List<ServerURL> suList = DBToolkit.selectAllServerURL();
		for (ServerURL su : suList) {
			suMap.put(su.getServerId(), su.getUrl());
		}
		
		Gson gson = CommonToolkit.getGson();
		Map<String, String> paraMap = new HashMap<String, String>();
		int maxID = DBToolkit.selectMaxID(Const.TBL_NAME_PLAYER_INFO);
		for (int stID = 0; stID <= maxID; stID += Const.SYNC_SCAN_LIMIT) {
			List<String> uidList = DBToolkit.selectNeedSyncPlayerInfo(stID, Const.SYNC_SCAN_LIMIT);
			if (null == uidList) {
				break;
			}
			
			for (String uid : uidList) {
				try {
					String[] tmp = uid.split(",");
					paraMap.put("sid", tmp[0]);
					paraMap.put("pid", tmp[1]);
					String jsonData = CommonToolkit.getPostResult(String.format(Const.SYNC_URL, suMap.get(Integer.parseInt(tmp[0]))), 
							paraMap, false);
					
					PlayerInfo pi = gson.fromJson(jsonData, PlayerInfo.class);
					if (null != pi) {
						// 更新LBS服玩家数据
						DBToolkit.insertOrUpdatePlayerInfo(pi);
						RedisToolkit.putPlayerInfoAndGeoHash(pi, null, false);
					}
				} catch (Exception e) {
					//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_SYNC, e, null, null);
					e.printStackTrace();
				}

			}
		}
		
		resp.getWriter().print("OK");
	}

}
