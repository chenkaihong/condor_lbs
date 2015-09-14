package mango.condor.cache;

import java.util.HashSet;
import java.util.Set;

import com.gzyouai.hummingbird.common.component.GameRuntimeException;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * @Author Create by 李兴
 * @Date 2014年2月24日 下午4:03:31
 * @Description 
 */
public enum RedisCacheKeyConst {
	CHAT ("cl:"),
	PLAYER ("pi"),
	BOTTLE ("br"),
	UNREAD_BOTTLE_NUM ("urbn");	
	
	public final String value;
	private RedisCacheKeyConst (String value) {
		this.value = value;
	}
	
	public static void checkUniqueCacheKey () {
		Set<String> set = new HashSet<String>();
		for (RedisCacheKeyConst item : RedisCacheKeyConst.values()) {
			if (set.contains(item.value)) {
				String msg = String.format("RedisCacheKeyConst key[%s] be repeat.", item.value);
				throw new GameRuntimeException(msg);
			}
			else {
				set.add(item.value);
			}
		}
	}
	
	public static void main(String[] args) {
		checkUniqueCacheKey();
		System.out.println( "End .." );
	}
}
