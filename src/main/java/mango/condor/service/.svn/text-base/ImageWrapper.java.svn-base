package mango.condor.service;

import java.util.HashMap;
import java.util.Map;

import mango.condor.domain.bottle.BottleContent;
import mango.condor.domain.bottle.BottleInfo;
import mango.condor.domain.bottle.BottleListItem;
import mango.condor.domain.chat.ChatMy;
import mango.condor.domain.lbs.FollowMsg;
import mango.condor.domain.lbs.PlayerInfo;
import mango.condor.domain.msg.LBSRequestMessage;

/**
 * 图片资源封装类
 * 		-- 主要是兼容老版本不存在的图片
* Copyright (c) 2011-2012 by 广州游爱 Inc.
* @Author Create by 李兴 
* @Date 2014年3月3日 下午5:43:40
* @Description
 */
public class ImageWrapper {
	private static Map<String, String> replacementMap = new HashMap<String, String>();
	static {
//		replacementMap.put("hero_h_10315", "hero_h_10130");
//		replacementMap.put("hero_h_10314", "hero_h_10130");
	}
	
	private static boolean needWrap (LBSRequestMessage req) {
		return false;
		//return !replacementMap.isEmpty() && !req.isNotLessThanVersion_2_5();
	}
	
	public static void wrapImage (LBSRequestMessage req, ChatMy chat) {
		if (chat != null && needWrap(req)) {
			String replacement = replacementMap.get( chat.getChaterImageId() );
			if (null != replacement) {
				chat.setChaterImageId( replacement );
			}
		}
	}
	
	public static void wrapImage (LBSRequestMessage req, BottleListItem bottle) {
		if (bottle != null && needWrap(req)) {
			String replacement = replacementMap.get( bottle.getImageId() );
			if (null != replacement) {
				bottle.setImageId( replacement );
			}
		}
	}
	
	public static void wrapImage (LBSRequestMessage req, BottleContent bottle) {
		if (bottle != null && needWrap(req)) {
			String replacement = replacementMap.get( bottle.getImageId() );
			bottle.setImageId( replacement );
		}
	}
	
	public static void wrapImage (LBSRequestMessage req, FollowMsg msg) {
		if (msg != null && needWrap(req)) {
			String replacement = replacementMap.get( msg.getSenderImageId() );
			msg.setSenderImageId( replacement );
		}
	}
	
	public static void wrapImage (LBSRequestMessage req, BottleInfo bottle) {
		if (bottle != null && needWrap(req)) {
			String replacement = replacementMap.get( bottle.getImageId() );
			bottle.setImageId( replacement );
		}
	}
	
	public static void wrapImage (LBSRequestMessage req, PlayerInfo player) {
		if (player != null && needWrap(req)) {
			String replacement = replacementMap.get( player.getImageId() );
			player.setImageId( replacement );
		}
	}
	
}
