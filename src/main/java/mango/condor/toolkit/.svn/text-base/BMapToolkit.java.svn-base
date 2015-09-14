package mango.condor.toolkit;

import mango.condor.domain.lbs.BMapIPGeoResp;
import mango.condor.domain.lbs.BMapIPGeoRespStatus;
import mango.condor.domain.lbs.BMapReverseGeoResp;
import mango.condor.domain.lbs.BMapReverseGeoRespStatus;
import mango.condor.domain.lbs.Coor;
import mango.condor.domain.lbs.PlayerInfo;

import com.google.gson.Gson;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * Copyright (c) 2011-2012 by 广州游爱 Inc.
 * 
 * @Author Create by 邢陈程
 * @Date 2013-7-3 上午10:45:47
 * @Description
 */
public class BMapToolkit {
	
	private static LookupService cl;
	
	public static void init() {
		try {
			String path = BMapToolkit.class.getClassLoader().getResource("GeoLiteCity.dat").getPath();

			// You should only call LookupService once, especially if you use
			// GEOIP_MEMORY_CACHE mode, since the LookupService constructor
			// takes up
			// resources to load the GeoIP.dat file into memory
			// LookupService cl = new
			// LookupService(dbfile,LookupService.GEOIP_STANDARD);
			cl = new LookupService(path, LookupService.GEOIP_MEMORY_CACHE);
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_INIT_GEOIP, e, null, null);
			e.printStackTrace();
		}
	}
	
	public static void destroy() {
		if (null != cl) {
			cl.close();
			cl = null;
		}
	}

	/**
	 * 根据经纬度逆解析地址
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static String getReverseGeo(double lat, double lng) {
		try {
			String url = String.format(Const.BMAP_REV_GEO_URL, lat, lng);
			String jsonData = CommonToolkit.httpGet(url);
			if (null == jsonData) {
				return "";
			}

			Gson gson = CommonToolkit.getGson();
			BMapReverseGeoResp bmapRevGeoResp = gson.fromJson(jsonData,
					BMapReverseGeoResp.class);
			if (null == bmapRevGeoResp
					|| bmapRevGeoResp.getStatus() != BMapReverseGeoRespStatus.OK) {
				
				//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_BAIDU_GEO_REVERSE, new RuntimeException(), null, new Double[] { lat, lng });
				
				return "";
			}

			return bmapRevGeoResp.toAddress();
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_BAIDU_GEO_REVERSE, new RuntimeException(), null, new Double[] { lat, lng });
			e.printStackTrace();
			
			return "";
		}
	}

	/**
	 * 根据IP地址设置经纬度
	 * 
	 * @param playerInfo
	 * @param ip
	 */
	public static void setCoorByIP(PlayerInfo playerInfo, String ip) {
		try {
			String url = String.format(Const.BMAP_IP_GEO_URL, ip);
			String jsonData = CommonToolkit.httpGet(url);
			if (null == jsonData) {
				return;
			}

			Gson gson = CommonToolkit.getGson();
			BMapIPGeoResp bmapIPGeoResp = gson.fromJson(jsonData,
					BMapIPGeoResp.class);
			if (null == bmapIPGeoResp
					|| bmapIPGeoResp.getStatus() != BMapIPGeoRespStatus.OK) {
				
				//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_BAIDU_GEO_IP, new RuntimeException(), new String[] { ip }, null);
				

				return;
			}

			Coor coor = bmapIPGeoResp.getCoor();
			if (null == coor) {
				//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_BAIDU_GEO_IP, new RuntimeException(), new String[] { ip }, null);
				return;
			}

			playerInfo.setLat(coor.getY());
			playerInfo.setLng(coor.getX());
		} catch (Exception e) {
			//LogstashExceptionLogger.handleException(Const.LBS_SERVER_NAME, 0, Const.LBS_EXP_BAIDU_GEO_IP, new RuntimeException(), new String[] { ip }, null);
			e.printStackTrace();
		}
	}

	/**
	 * 使用免费的geoip数据库根据IP地址获取地理位置
	 * @param playerInfo
	 * @param ip
	 * 
	 * https://github.com/maxmind/geoip-api-java/blob/master/examples/CountryLookup.java
	 * http://dev.maxmind.com/geoip/
	 */
	public synchronized static void setCoorByIPVersion2(PlayerInfo playerInfo, String ip) {
		if (playerInfo != null) {
			final long t = System.currentTimeMillis();
			try {
				Location loc = cl.getLocation(ip);
				playerInfo.setLat(loc.latitude);
				playerInfo.setLng(loc.longitude);
			}
			catch (Exception e) {
				System.out.println( "reset [lat & lng] error. playerId=" + playerInfo.getPlayerId() + ", ip=" + ip);
			}
			finally {
				long now = System.currentTimeMillis();
				long timeDiff = now - t;
				if (timeDiff > 20L) {
					System.out.println( "TIMES.setCoorByIPVersion2. uid=" + playerInfo.getUid() + ", ip=" + ip + ", timeDiff=" + timeDiff);
				}
			}
		}
	}
	
	/**
	 * 缩放经纬度
	 * @param value
	 * @param rule
	 * @return
	 */
	public static double scaleLatOrLng (double value, int rule) {
		if (rule == 1) {
			return value;
		}
		
		/**
		 * 把1度分成 rule 份，每份大概是 多少米
		 * 		==================
		 * 		rule -> 每份对应多少米
		 * 		==================
		 * 		1110 -> 100
		 *		1000 -> 111
		 *		500  -> 222
		 *		200	 -> 556
		 *		100  -> 1113
		 */
		return ((int) (value * rule)) *1D / rule;
	}
	
	/**
	 * 缩放经纬度 最小经纬区域点
	 * @param value
	 * @return
	 */
	public static double scaleLatOrLngForGeohashMinPoint (double value) {
		// 556米的区域为最小存储单位
		return scaleLatOrLng(value, 200);
	}
}
