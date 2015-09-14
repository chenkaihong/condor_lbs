<%@ page language="java" contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<%@page import="mango.condor.toolkit.DBToolkit"%>
<%@page import="mango.condor.toolkit.CommonToolkit"%>
<%@page import="mango.condor.domain.msg.LBSRequestMessage"%>
<%@page import="mango.condor.domain.msg.LBSResponseMessage"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.GsonBuilder"%>
<%@page import="mango.condor.toolkit.Const"%>
<%@page import="mango.condor.toolkit.GeoToolkit"%>
<%@page import="mango.condor.service.ImageWrapper"%>
<%@page import="mango.condor.service.StorageService"%>
<%@page import="mango.condor.domain.lbs.PlayerInfo"%>
<%@page import="mango.condor.domain.msg.lbs.GetPlayerInfoRspMsg"%>
<%
int serverId = 497, playerId = 497001776;
int hisServerId = 17, hisPlayerId = 32021614;

GetPlayerInfoRspMsg resp = new GetPlayerInfoRspMsg(new LBSRequestMessage(10015));
PlayerInfo pi = StorageService.getPlayerInfo(hisServerId, hisPlayerId);
PlayerInfo selfPi = StorageService.getPlayerInfo(serverId, playerId);
if (null != pi) {
	if (null != selfPi) {
		pi.setDistance(GeoToolkit.distanceInMeters(selfPi.getLat(), selfPi.getLng(), 
				pi.getLat(), pi.getLng()));
	}
	pi.setFollow(StorageService.isInFollowList(serverId, playerId, hisServerId, hisPlayerId));
	pi.setBlack(Const.BLACK_LIST_STATUS_ADD == StorageService.isBlackList(serverId, playerId, hisServerId, hisPlayerId));
	pi.calDiffTS();
	
	resp.setSuc(true);
	resp.setPi(pi);
}

out.println (new Gson().toJson(resp));
out.println ();
%>