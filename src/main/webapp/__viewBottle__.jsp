<%@ page language="java" contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="mango.condor.domain.lbs.PlayerInfo"%>
<%@page import="mango.condor.domain.bottle.Bottle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="mango.condor.domain.bottle.BottleListItem"%>
<%@page import="java.util.List"%>
<%@page import="mango.condor.service.StorageService"%>
<%@page import="mango.condor.toolkit.RedisToolkit"%>
<%!
static String createBottleString (Bottle bottle, boolean throwOrFecth) {
	if (throwOrFecth) {
		return String.format("id=%s senderReaded=%s receiver=%s text=%s", 
				bottle.getId(), bottle.isSenderReaded(), 
				bottle.getReceiver(), bottle.getText());
	}
	else {
		return String.format("id=%s receiverReaded=%s sender=%s text=%s", 
				bottle.getId(), bottle.isReceiverReaded(), 
				bottle.getSender(), bottle.getText());
	}
}
%>
<%
	/**
	  * curl "http://127.0.0.1:18090/lbs/__viewBottle__.jsp?uid="
	  */

	String uid = request.getParameter("uid");

	out.println( "\n####################################" );
	out.println ("uid=" + uid + ", unreadNum: " + StorageService.getUnreadNum(uid));
	
	out.println( "\n####################################" );	
	int type = 0;
	out.println( "### throw bottles ###" );
	List<Long> bottleIdList = RedisToolkit.getMyBottleIdList(type, uid, 1);	
	out.println( "bottleIdList=" + bottleIdList );
	
	List<BottleListItem> retList = new ArrayList<BottleListItem>();
	long now = System.currentTimeMillis();
	for (Long bottleId : bottleIdList) {
		Bottle bottle = StorageService.getBottleById(bottleId);
		
		BottleListItem item = new BottleListItem();
		PlayerInfo pi = null;
		PlayerInfo selfPi = null;
		
		if (0 == type) {
			pi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
			selfPi = StorageService.getPlayerInfo(bottle.getSender(), false);
			item.setReaded(bottle.isSenderReaded());
			item.setRewarded(bottle.isSenderRewarded());
		} else {
			pi = StorageService.getPlayerInfo(bottle.getSender(), false);
			selfPi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
			item.setReaded(bottle.isReceiverReaded());
			item.setRewarded(bottle.isReceiverRewarded());
		}
		
		if (null == pi || selfPi == null) {
			out.println( "ERROR:   \t" + createBottleString(bottle, true)  + ", pi=" + pi + ", selfPi=" + selfPi);
			continue;
		}
		
		item.setBottleId(bottle.getId());
		item.setImageId(pi.getImageId());
		item.setLevel(pi.getLevel());
		item.setName(pi.getName());
		item.setPlayerId(pi.getPlayerId());
		item.setServerId(pi.getServerId());
		item.setSendTime(now - bottle.getSendTime());
		item.setRewardId(bottle.getRewardId());
		out.println( "SUCCESS: \t" + createBottleString(bottle, true));
		
		retList.add(item);
	}
	out.println ("retList.size=" + retList.size());
	
	out.println( "\n####################################" );	
	type = 1;
	out.println( "### fetch bottles ###" );
	bottleIdList = RedisToolkit.getMyBottleIdList(type, uid, 1);	
	out.println( "bottleIdList=" + bottleIdList );
	
	retList = new ArrayList<BottleListItem>();
	for (Long bottleId : bottleIdList) {
		Bottle bottle = StorageService.getBottleById(bottleId);
		
		BottleListItem item = new BottleListItem();
		PlayerInfo pi = null;
		PlayerInfo selfPi = null;
		
		if (0 == type) {
			pi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
			selfPi = StorageService.getPlayerInfo(bottle.getSender(), false);
			item.setReaded(bottle.isSenderReaded());
			item.setRewarded(bottle.isSenderRewarded());
		} else {
			pi = StorageService.getPlayerInfo(bottle.getSender(), false);
			selfPi = StorageService.getPlayerInfo(bottle.getReceiver(), false);
			item.setReaded(bottle.isReceiverReaded());
			item.setRewarded(bottle.isReceiverRewarded());
		}
		
		if (null == pi || selfPi == null) {
			out.println( "ERROR:   \t" + createBottleString(bottle, false)  + ", pi=" + pi + ", selfPi=" + selfPi);
			continue;
		}
		
		item.setBottleId(bottle.getId());
		item.setImageId(pi.getImageId());
		item.setLevel(pi.getLevel());
		item.setName(pi.getName());
		item.setPlayerId(pi.getPlayerId());
		item.setServerId(pi.getServerId());
		item.setSendTime(now - bottle.getSendTime());
		item.setRewardId(bottle.getRewardId());
		out.println( "SUCCESS: \t" + createBottleString(bottle, false));
		
		retList.add(item);
	}
	out.println ("retList.size=" + retList.size());
%>